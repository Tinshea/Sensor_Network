# Sensor Network in BCM4Java

> Academic project developed as part of my CPS (Composants) course at STL Master — Sorbonne Université 2024

A distributed sensor network monitoring system built with the BCM4Java component model, inspired by real-world IoT alert systems. Sensor nodes form a geographic mesh, execute a custom query language in a decentralized manner, and propagate queries across neighbors using continuation-passing. The project progressively evolves from synchronous direct-style execution to a fully asynchronous, parallel, and modular architecture.

## 🛠️ Tech Stack

- **Core Technologies**: Java SE 8, BCM4Java (Basic Component Model for Java)
- **Tools & Environment**: Eclipse IDE, Apache Ant / Maven, `AcceleratedClock` + `ClocksServer` (BCM4Java timed test scenarios), Javadoc

## 📦 Installation & Setup

### Prerequisites

- [JDK 8+](https://www.oracle.com/java/technologies/downloads/)
- [Eclipse IDE for Java Developers](https://www.eclipse.org/downloads/)
- BCM4Java library (provided — add to Eclipse Build Path)

### Instructions

```bash
# 1. Clone the repository
git clone https://github.com/Tinshea/Sensor_Network.git
cd Sensor_Network

# 2. Open in Eclipse
#    File → Import → Existing Projects into Workspace

# 3. Add BCM4Java to the Build Path
#    Right-click project → Build Path → Add External JARs

# 4. Run the desired deployment scenario
#    Navigate to deployment/ and launch the target main class
```

## 🏗️ System Architecture

The system is composed of three BCM4Java component types communicating through typed ports and connectors:

```
┌──────────────┐     LookupCI      ┌──────────────────┐     RegistrationCI    ┌──────────────┐
│   Client     │ ────────────────► │  Global Registry │ ◄──────────────────── │ Sensor Node  │
│ (Supervisor) │                   │                  │                       │              │
│              │     RequestingCI  │  - Node registry │     SensorNodeP2PCI   │  - Sensors   │
│              │ ────────────────► │  - Neighbor      │ ◄──────────────────── │  - Neighbors │
└──────────────┘                   │    discovery     │                       │  - Query     │
        ▲                          └──────────────────┘                       │    executor  │
        │                                                                     └──────────────┘
        │  RequestResultCI (async result callback)
        └──────────────────────────────────────────────────────────────────────────────────────
```

## 🔤 Query Language

Queries are constructed as Abstract Syntax Trees and interpreted locally on each node. Two query types are supported:

| Type       | Syntax                          | Returns                                        |
|------------|---------------------------------|------------------------------------------------|
| `GQuery`   | `GQuery gather cont`            | List of `(nodeId, sensorId, value)` triplets   |
| `BQuery`   | `BQuery bexp cont`              | List of node identifiers where `bexp` is `true`|

### Continuation strategies

| Type    | Syntax                   | Behavior                                                              |
|---------|--------------------------|-----------------------------------------------------------------------|
| `ECont` | Empty                    | Execute on the receiving node only                                    |
| `DCont` | `DCont dirs maxHops`     | Propagate in given directions (NE, NW, SE, SW) for up to n hops       |
| `FCont` | `FCont base maxDistance` | Flood to all nodes within a geographic radius from a reference point  |

**Example** — Forest fire detection query propagating north-east:
```java
new BQuery(
    new AndBExp(
        new GeqCExp(new SRand("température"), new CRand(50.0)),
        new GeqCExp(new SRand("fumée"), new CRand(3.0))),
    new DCont(new FDirs(Direction.NE), 2))
```

## ⚙️ Execution Models

### Synchronous — Direct Style
Each node calls its neighbors in sequence, waits for their results, aggregates them with its own local result, and returns the complete result to its caller. Simple but resource-intensive under load.

### Asynchronous — Continuation-Passing Style
Non-blocking calls: after executing locally, a node propagates the request to its neighbors via `executeAsync` without waiting. Leaf nodes send partial results directly to the originating client via `RequestResultCI#acceptRequestResult`. The client uses a timeout to collect and merge all partial results. Enables true parallelism across the call tree.

## 🔑 Key Component Interfaces

| Interface            | Offered by       | Required by      | Purpose                                              |
|----------------------|------------------|------------------|------------------------------------------------------|
| `RegistrationCI`     | Registry         | Sensor Nodes     | Node registration and neighbor discovery             |
| `LookupCI`           | Registry         | Clients          | Retrieve node connection info by ID or geographic zone|
| `RequestingCI`       | Sensor Nodes     | Clients          | Send queries (sync `execute` / async `executeAsync`) |
| `SensorNodeP2PCI`    | Sensor Nodes     | Sensor Nodes     | P2P query propagation and connection management      |
| `RequestResultCI`    | Clients          | Sensor Nodes     | Async result delivery back to the client             |

## 🧩 Plugin Architecture

The final stage refactors component responsibilities into reusable plugins:

- **Node Plugin**: encapsulates `RequestingCI`, `SensorNodeP2PCI`, and `RegistrationCI` behaviors.
- **Client Plugin**: encapsulates `RequestingCI` and `LookupCI` behaviors.

## 🚀 Deployment Models

| Mode         | Description                                                                          |
|--------------|--------------------------------------------------------------------------------------|
| **Mono-JVM** | All components (Registry, Nodes, Clients) run in a single JVM. Minimum requirement. |
| **Multi-JVM**| 5 JVMs × (1 Client + 10 Nodes). Queries execute across JVM boundaries.              |

## 📊 Performance Testing

Tests vary two parameters simultaneously and results are presented as cross-tables:

- **Request rate**: progressively reduced delay between client query emissions to measure throughput under increasing load.
- **Thread pool size**: per-node thread count tested at 1, 2, 5, and 10 threads to identify the saturation point beyond which performance stops improving.

Test scenarios are timed using `AcceleratedClock` with a configurable acceleration factor (e.g., 60×), synchronizing all components on a shared simulated timeline for deterministic execution.

## 🗂️ Project Structure

```
Sensor_Network/
├── src/                    # Java source — nodes, registry, clients, query language, plugins
├── deployment/             # Deployment launchers (mono-JVM and multi-JVM scenarios)
├── test_performance/       # Performance test scenarios and results
├── doc/                    # Generated Javadoc
├── node.log                # Node execution trace
├── client.log              # Client execution trace
├── Register.log            # Registry execution trace
└── cahier-des-charges.pdf  # Original project specification (Prof. J. Malenfant)
```

## 👤 Author

**Malek Bouzarkouna**, **Younes Chetouani** & **Amine Zemali** — Master STL, Sorbonne Université, CPS 2024  
Academic supervisor: Jacques Malenfant

## 📄 License

No license specified.
