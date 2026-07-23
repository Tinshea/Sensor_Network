#!/usr/bin/env bash
# Bring up the whole multi-JVM deployment on Linux/macOS: cyclic barrier, global
# registry, then the six JVMs described in deployment/config.xml.
#
#   ./run-multijvm.sh               # build, launch everything, wait, clean up
#   ./run-multijvm.sh --skip-build
#   ./run-multijvm.sh --timeout 150
#
# Per-JVM output lands in logs/multijvm/. See the README for what this
# deployment does and does not currently do.

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BIN="$ROOT/bin"
DEP="$ROOT/deployment"
LOGS="$ROOT/logs/multijvm"
SKIP_BUILD=0
TIMEOUT=100
HEAD_START=10

while [ $# -gt 0 ]; do
    case "$1" in
        --skip-build) SKIP_BUILD=1; shift ;;
        --timeout)    TIMEOUT="$2"; shift 2 ;;
        --head-start) HEAD_START="$2"; shift 2 ;;
        -h|--help)    sed -n '2,10p' "$0"; exit 0 ;;
        *) echo "Unknown option: $1" >&2; exit 2 ;;
    esac
done

# CPS.jar is a prebuilt snapshot of this project; keep it off the classpath so
# the freshly compiled classes in bin/ are what actually run.
CP="$(find "$DEP/jars" -name '*.jar' ! -name 'CPS.jar' | tr '\n' ':')"

if [ "$SKIP_BUILD" -eq 0 ]; then
    mkdir -p "$BIN"
    ARGFILE="$(mktemp)"
    find "$ROOT/src" -path "$ROOT/src/tests" -prune -o -name '*.java' -print > "$ARGFILE"
    javac -nowarn -encoding UTF-8 -d "$BIN" -cp "$CP" "@$ARGFILE"
    echo "Compiled $(wc -l < "$ARGFILE") source files into $BIN"
    rm -f "$ARGFILE"
fi

mkdir -p "$LOGS"
rm -f "$LOGS"/*.log

PIDS=()
NAMES=()

# Everything runs from deployment/, where config.xml and dcvm.policy live.
start_node() {
    local name="$1"; shift
    ( cd "$DEP" && java -ea -cp "$CP$BIN" -Djava.security.policy=dcvm.policy "$@" \
        > "$LOGS/$name.log" 2> "$LOGS/$name.err.log" ) &
    PIDS+=($!)
    NAMES+=("$name")
    printf '  %-9s pid %s\n' "$name" "$!"
}

cleanup() {
    for pid in "${PIDS[@]}"; do kill "$pid" 2>/dev/null || true; done
    sleep 1
    for pid in "${PIDS[@]}"; do kill -9 "$pid" 2>/dev/null || true; done
}
trap cleanup EXIT INT TERM

echo 'Starting support services...'
start_node barrier  fr.sorbonne_u.components.cvm.utils.DCVMCyclicBarrier config.xml
start_node registry fr.sorbonne_u.components.registry.GlobalRegistry config.xml
sleep 5

# jvm0 publishes the clock and the registry into the global registry while it is
# still in instantiateAndPublish. jvm1-5 connect to those ports during their own
# instantiateAndPublish, i.e. in the same barrier phase, so the barrier does not
# order the two. Give jvm0 a head start or the others fail with "not bound!".
echo 'Starting jvm0 (clock + registry)...'
start_node jvm0 app.DistributedCVM jvm0 config.xml
sleep "$HEAD_START"

echo 'Starting jvm1-5 (1 client + 10 nodes each)...'
for j in 1 2 3 4 5; do
    start_node "jvm$j" app.DistributedCVM "jvm$j" config.xml
    sleep 0.4
done

echo "Waiting up to ${TIMEOUT}s for the scenario to finish..."
elapsed=0
while [ "$elapsed" -lt "$TIMEOUT" ]; do
    running=0
    for pid in "${PIDS[@]}"; do kill -0 "$pid" 2>/dev/null && running=1; done
    [ "$running" -eq 0 ] && break
    sleep 2
    elapsed=$((elapsed + 2))
done

echo
echo 'Outcome (stderr bytes > 0 means that JVM reported a problem):'
for name in "${NAMES[@]}"; do
    size=$(wc -c < "$LOGS/$name.err.log" 2>/dev/null || echo 0)
    printf '  %-9s stderr=%s\n' "$name" "$size"
done
echo
echo "Logs in $LOGS; query results in deployment/client.log"
