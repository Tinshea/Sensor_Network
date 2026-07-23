#!/usr/bin/env bash
# Build and run the sensor network on Linux/macOS -- no Eclipse required.
#
#   ./run.sh                            # mono-JVM scenario (app.CVM)
#   ./run.sh --tests                    # compile and run the JUnit 5 suite
#   ./run.sh --main app.DistributedCVM  # multi-JVM node (see README)
#   ./run.sh --skip-build               # reuse the classes already in bin/
#   ./run.sh --no-assertions            # run without -ea

set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BIN="$ROOT/bin"
TEST_BIN="$ROOT/bin-test"
JUNIT_VERSION='1.10.2'
JUNIT_JAR="$ROOT/.junit/junit-platform-console-standalone-$JUNIT_VERSION.jar"

MAIN='app.CVM'
RUN_TESTS=0
SKIP_BUILD=0
ASSERTIONS='-ea'

while [ $# -gt 0 ]; do
    case "$1" in
        --main)           MAIN="$2"; shift 2 ;;
        --tests)          RUN_TESTS=1; shift ;;
        --skip-build)     SKIP_BUILD=1; shift ;;
        --no-assertions)  ASSERTIONS=''; shift ;;
        -h|--help)        sed -n '2,9p' "$0"; exit 0 ;;
        *) echo "Unknown option: $1" >&2; exit 2 ;;
    esac
done

for tool in javac java; do
    command -v "$tool" >/dev/null 2>&1 || {
        echo "$tool not found on PATH. Install a JDK (8 or later) and try again." >&2
        exit 1
    }
done

# CPS.jar is a prebuilt snapshot of this very project; leaving it off the
# classpath keeps it from shadowing what we just compiled into bin/.
CP="$(find "$ROOT/deployment/jars" -name '*.jar' ! -name 'CPS.jar' | tr '\n' ':')"

# src/tests needs JUnit on the classpath, so the main build leaves it out.
if [ "$SKIP_BUILD" -eq 0 ]; then
    mkdir -p "$BIN"
    ARGFILE="$(mktemp)"
    find "$ROOT/src" -path "$ROOT/src/tests" -prune -o -name '*.java' -print > "$ARGFILE"
    javac -nowarn -encoding UTF-8 -d "$BIN" -cp "$CP" "@$ARGFILE"
    echo "Compiled $(wc -l < "$ARGFILE") source files into $BIN"
    rm -f "$ARGFILE"
fi

if [ "$RUN_TESTS" -eq 1 ]; then
    if [ ! -f "$JUNIT_JAR" ]; then
        URL="https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/$JUNIT_VERSION/junit-platform-console-standalone-$JUNIT_VERSION.jar"
        echo "JUnit console launcher not found; downloading it from Maven Central:"
        echo "  $URL"
        mkdir -p "$(dirname "$JUNIT_JAR")"
        if command -v curl >/dev/null 2>&1; then curl -fsSL "$URL" -o "$JUNIT_JAR"
        else wget -q "$URL" -O "$JUNIT_JAR"; fi
    fi

    mkdir -p "$TEST_BIN"
    TESTFILE="$(mktemp)"
    find "$ROOT/src/tests" -name '*.java' > "$TESTFILE"
    javac -nowarn -encoding UTF-8 -d "$TEST_BIN" -cp "$CP$BIN:$JUNIT_JAR" "@$TESTFILE"
    echo "Compiled $(wc -l < "$TESTFILE") test files into $TEST_BIN"
    rm -f "$TESTFILE"

    exec java -jar "$JUNIT_JAR" execute \
        --class-path "$CP$BIN:$TEST_BIN" \
        --scan-class-path "$TEST_BIN" --details=summary --disable-ansi-colors
fi

echo "Running $MAIN -- about 30s, opens ~55 trace windows. Ctrl+C to stop."
cd "$ROOT"
exec java $ASSERTIONS -cp "$CP$BIN" "$MAIN"
