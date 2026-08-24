<p align="center">
  <img src="banner.jpg" alt="Sensor Network — a distributed query engine over a self-organising mesh of 50 BCM4Java components" width="100%">
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-8%2B-orange" alt="Java 8+">
  <img src="https://img.shields.io/badge/component%20model-BCM4Java-blue" alt="BCM4Java">
  <img src="https://img.shields.io/badge/tests-79%20passing-brightgreen" alt="79 tests passing">
  <img src="https://img.shields.io/badge/build-no%20IDE%20required-lightgrey" alt="No IDE required">
</p>

# Sensor Network

A distributed sensor-network monitoring system built on **BCM4Java**, the component model taught in the CPS course at Sorbonne Université. Fifty sensor nodes discover each other through a registry, form a geographic mesh, and cooperatively evaluate queries written in a small custom language — each node interpreting the query against its own sensors and forwarding a *continuation* to the neighbours the query still needs to reach.

The interesting part is not the sensors; it is that **no node has a global view**. A query enters the mesh at one node and the result assembles itself out of partial answers flowing back through the topology.

---

## Quick start

You need a **JDK 8 or later** on your `PATH` and nothing else — every dependency is vendored in `deployment/jars/`. No Maven, no Gradle, no Eclipse.

```bash
git clone https://github.com/Tinshea/Sensor_Network.git
cd Sensor_Network
```

**Linux / macOS**
```bash
chmod +x run.sh
./run.sh              # compile + run the mono-JVM scenario
./run.sh --tests      # compile + run the JUnit 5 suite
```

**Windows (PowerShell)**
```powershell
.\run.ps1             # compile + run the mono-JVM scenario
.\run.ps1 -Tests      # compile + run the JUnit 5 suite
```

The scenario runs for about 30 seconds and then shuts itself down. It opens a **Network Graph** window plus one trace console per component (~55 windows — this is BCM4Java's normal debugging behaviour). Results also land in `client.log`, `node.log` and `Register.log`.

| Flag | Effect |
|---|---|
| `--tests` / `-Tests` | Build and run the 79 unit tests instead of the simulation |
| `--main <class>` / `-Main <class>` | Run a different entry point (e.g. `app.DistributedCVM`) |
| `--skip-build` / `-SkipBuild` | Reuse the classes already in `bin/` |
| `--no-assertions` / `-NoAssertions` | Drop `-ea` (BCM4Java checks its component contracts with assertions) |

> The `--tests` flag downloads the JUnit Platform console launcher from Maven Central into `.junit/` on first use. Everything else runs fully offline.

---

## Watching it run

Nodes lay themselves out on a staggered grid and connect to whichever neighbours fall within range in each of the four cardinal diagonals. When a query propagates, the edges it travels light up **yellow** and the nodes that evaluated it turn **red** — so you can watch a flood spread and, crucially, watch it *stop* at its distance bound.

<p align="center">
  <img src="assets/network-mesh.png" alt="The live network visualiser: 50 nodes in a staggered mesh, with a query flooding outward from a base position and halting at its radius" width="70%">
</p>

Every component keeps its own trace console. The registry shows nodes registering and receiving their neighbour sets — this is the mesh assembling itself, node by node, with no central topology description anywhere:

<p align="center">
  <img src="assets/trace-register.png" alt="Registry trace showing each node registering and being handed its neighbour set" width="90%">
</p>

And the client shows queries going out and results coming back — first a `BQuery` returning bare node identifiers, then `GQuery` results carrying actual sensor readings gathered from across the mesh:

<p align="center">
  <img src="assets/trace-client.png" alt="Client trace showing query results returning from the mesh" width="90%">
</p>

---

## Architecture

Three component types talk to each other through typed ports and connectors. A component never holds a reference to another component — only to a *port*, which BCM4Java can transparently bind across JVMs.

```mermaid
flowchart LR
    R["<b>Global Registry</b><br/>node directory<br/>neighbour discovery"]
    C["<b>Client</b><br/>issues queries<br/>merges partial results"]
    N1["<b>Sensor Node</b><br/>local sensors<br/>query interpreter"]
    N2["<b>Sensor Node</b>"]
    N3["<b>Sensor Node</b>"]

    C -->|"LookupCI: find node by id / zone"| R
    N1 -->|"RegistrationCI: register, get neighbours"| R
    C -->|"RequestingCI: execute / executeAsync"| N1
    N1 ---|"SensorNodeP2PCI"| N2
    N2 ---|"SensorNodeP2PCI"| N3
    N1 -.->|"RequestResultCI: async partial result"| C
    N2 -.->|"RequestResultCI"| C
    N3 -.->|"RequestResultCI"| C
```

| Interface | Offered by | Required by | Purpose |
|---|---|---|---|
| `RegistrationCI` | Registry | Sensor nodes | Registration and neighbour discovery |
| `LookupCI` | Registry | Clients | Resolve connection info by node id or geographic zone |
| `RequestingCI` | Sensor nodes | Clients | Submit queries (`execute` / `executeAsync`) |
| `SensorNodeP2PCI` | Sensor nodes | Sensor nodes | Peer-to-peer propagation and connection management |
| `RequestResultCI` | Clients | Sensor nodes | Deliver asynchronous partial results back to the client |

All components synchronise on a shared **`AcceleratedClock`** (`ClocksServer`), so scenarios are expressed on a simulated timeline — an acceleration factor of 60 turns a 120-second simulated schedule into 2 seconds of wall clock, and every component agrees on when "now" is.

### Plugin architecture

The final iteration moves component behaviour into reusable BCM4Java **plugins**, leaving the components themselves as thin shells:

- **`SensorPlugin`** — `RequestingCI`, `SensorNodeP2PCI` and `RegistrationCI` behaviour for a node.
- **`ClientPlugin`** — `RequestingCI` and `LookupCI` behaviour for a client.

`app/components/` keeps the earlier non-plugin implementations for comparison; `ComponentFactory` wires up the `withplugin/` versions.

---

## Query language

Queries are **abstract syntax trees**, not strings. They are built in Java, shipped to a node, and interpreted there against local sensor data. Two query forms:

| Form | Shape | Returns |
|---|---|---|
| `GQuery` | `GQuery gather cont` | Triplets of `(nodeId, sensorId, value)` |
| `BQuery` | `BQuery bexp cont` | Identifiers of the nodes where `bexp` holds |

The **continuation** decides how far the query travels — this is what makes evaluation decentralised:

| Continuation | Shape | Behaviour |
|---|---|---|
| `ECont` | *empty* | Evaluate on the receiving node only |
| `DCont` | `DCont dirs maxHops` | Propagate along given directions (NE, NW, SE, SW) for up to *n* hops |
| `FCont` | `FCont base maxDistance` | Flood every node within a geographic radius of a reference point |

A forest-fire detector — "which nodes north-east of here read both hot **and** smoky, within two hops?":

```java
new BQuery(
    new AndBExp(
        new GeqCExp(new SRand("temperature"), new CRand(50.0)),
        new GeqCExp(new SRand("smoke"),       new CRand(3.0))),
    new DCont(new FDirs(Direction.NE), 2))
```

The AST lives in `src/ast/`: `bexp/` (boolean expressions), `cexp/` (comparisons), `rand/` (sensor and constant operands), `cont/` (continuations), `dirs/`, `gather/`, `base/`, `query/`.

---

## Execution models

The project implements the same semantics twice.

**Synchronous — direct style.** A node evaluates locally, calls each neighbour in turn, *blocks* until they answer, merges everything and returns one complete result to its caller. Simple to reason about, but every hop holds a thread hostage for the whole depth of the call tree.

**Asynchronous — continuation-passing style.** A node evaluates locally, fires the continuation at its neighbours via `executeAsync` and returns immediately. Each node sends its own partial result straight back to the originating client through `RequestResultCI`. The client accumulates partials and merges them when its timeout expires.

```mermaid
sequenceDiagram
    participant C as Client
    participant N5 as Node n5
    participant N10 as Node n10
    participant N19 as Node n19

    C->>N5: executeAsync(request)
    Note over N5: evaluate locally
    N5-->>C: acceptRequestResult(partial)
    N5->>N10: executeAsync(continuation)
    N5->>N19: executeAsync(continuation)
    Note over N10,N19: evaluate in parallel
    N10-->>C: acceptRequestResult(partial)
    N19-->>C: acceptRequestResult(partial)
    Note over C: merge partials on timeout
```

Switch between them with `Config.ASYNC`.

---

## Deployment

| Mode | Entry point | Layout |
|---|---|---|
| **Mono-JVM** | `app.CVM` | Clock, registry, 4 clients and 50 nodes in one JVM |
| **Multi-JVM** | `app.DistributedCVM` | `jvm0` hosts the clock and registry; `jvm1`–`jvm5` each host 1 client + 10 nodes, bound over RMI |

Multi-JVM needs BCM4Java's `GlobalRegistry` and `DCVMCyclicBarrier` running alongside the six JVMs. One command brings the whole thing up:

```bash
./run-multijvm.sh          # Linux/macOS
.\run-multijvm.ps1         # Windows
```

Per-JVM output lands in `logs/multijvm/`; query results in `deployment/client.log`. The original author scripts (`deployment/launch`, `start-gregistry`, `start-dcvm`, `start-cyclicbarrier`) do the same thing on Linux, but run everything off `CPS.jar` rather than freshly compiled classes.

### Multi-JVM is timing-sensitive

The distributed deployment works, but getting a clean end-to-end run is a matter of timing rather than configuration. What consistently succeeds: all eight processes start, the RMI registry binds, and all 50 nodes register across JVM boundaries with correct neighbour sets. What is fragile is the window in which queries actually propagate.

Two deadlines are in tension, and both derive from `Config.START_DELAY` (8 s) and the 60× acceleration factor:

- **jvm0 must publish first.** It creates the clock and the registry during `instantiateAndPublish`, while jvm1–5 *connect* to those ports in that same barrier phase — so the cyclic barrier does not order the two. Start them together and jvm1–5 die with `GlobalRegistryResponseException: clock-server-101 not bound!`.
- **But not too early.** The shared clock starts 8 s after jvm0 creates it. Give jvm0 a head start longer than that and the simulated schedule has already elapsed before jvm1–5 finish deploying, so clients fire queries through connectors that are still null.

`--head-start` / `-Jvm0HeadStartSeconds` (default 10) controls this gap. On the machine used here a 10 s head start produced a fully correct run — queries entering at `n5` in jvm1 and returning readings from `n11`, `n14`, `n15` and `n19` in jvm2, which is the whole point of the distributed mode:

```
request result: [Node5: Heat(85.33), Node5: WindDirection(11.65), Node1: Heat(53.77), ...
                 Node11: Heat(23.65), Node15: Heat(55.52), Node19: Heat(59.13), Node14: Heat(39.47), ...]
```

That result was not reliably reproducible: other runs came up clean but with queries never leaving the entry node. Widening the timing budget properly means raising `Config.START_DELAY`, which is deliberately left alone here since it also affects the mono-JVM scenario. **The mono-JVM scenario is the one to run if you just want to see the system work** — it is deterministic and needs no tuning.

---

## Configuration

Everything worth tuning is in [`src/app/config/Config.java`](src/app/config/Config.java):

| Constant | Default | Meaning |
|---|---|---|
| `NBNODE` | `50` | Sensor nodes in the mesh |
| `NBCLIENT` | `4` | Clients issuing queries |
| `COLUM` | `5` | Columns in the staggered grid layout |
| `ASYNC` | `true` | Asynchronous (CPS) vs synchronous execution |
| `ACCELERATION_FACTOR` | `60.0` | Simulated-time speed-up |
| `START_DELAY` | `8000` ms | Grace period before the shared clock starts |

Per-node thread-pool size and neighbour range are set where nodes are created in `CVM.createSensorComponents()` (defaults: 5 threads, range 1.5).

---

## Tests

79 JUnit 5 tests cover the AST evaluators and the data models — every expression node, every continuation, every comparison operator.

```console
$ ./run.sh --tests
Compiled 34 test files into bin-test

Test run finished after 197 ms
[        36 containers successful ]
[        79 tests successful      ]
[         0 tests failed          ]
```

---

## Project structure

```
Sensor_Network/
├── run.sh / run.ps1                    # build + run mono-JVM or tests, no IDE required
├── run-multijvm.sh / run-multijvm.ps1  # bring up the full 6-JVM deployment
├── src/
│   ├── app/
│   │   ├── CVM.java              # mono-JVM deployment
│   │   ├── DistributedCVM.java   # multi-JVM deployment
│   │   ├── components/           # Client, Sensor, Register (pre-plugin)
│   │   ├── ports/ connectors/    # BCM4Java plumbing
│   │   ├── models/ factory/      # data types, request builders
│   │   ├── gui/                  # live network visualiser
│   │   └── config/Config.java    # simulation knobs
│   ├── withplugin/               # plugin-based components (what actually runs)
│   ├── ast/                      # query language AST + interpreters
│   └── tests/                    # 79 JUnit 5 tests
├── deployment/
│   ├── jars/                     # vendored BCM4Java + dependencies
│   ├── config.xml                # multi-JVM topology
│   └── start-* launch            # multi-JVM orchestration (Linux)
├── test_performance/       # benchmark result tables
├── doc/                    # generated Javadoc
└── cahier-des-charges.pdf  # original specification (Prof. J. Malenfant)
```

---

## Notes

- **Assertions matter.** BCM4Java expresses its component contracts as `assert` statements, so the run scripts pass `-ea` by default. Running without it silently skips every pre/postcondition check.
- **`deployment/jars/CPS.jar`** is a prebuilt snapshot of this project. The build scripts deliberately leave it off the classpath so it cannot shadow freshly compiled classes.
- **Cloning on Windows** produces a warning about colliding paths under `doc/`: the generated Javadoc contains directories differing only in case (`doc/AST/` vs `doc/ast/`), which a case-insensitive filesystem cannot represent. It affects only the generated documentation, never the build.

## Credits

**Malek Bouzarkouna**, **Younes Chetouani** and **Amine Zemali** — Master STL, Sorbonne Université, CPS 2024.
Academic supervisor: Jacques Malenfant.

No license specified.
