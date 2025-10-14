# Sensor Network in BCM4Java

## About

This project is an implementation of a distributed monitoring system based on the concept of a sensor network. Developed as part of the 2024 "Component-Based Programming" (CPS) course, it leverages the BCM4Java component model to simulate an environmental sensor network capable of executing queries in a decentralized manner. 

The architecture explores advanced concepts such as parallelism, concurrency management, and load distribution across multiple nodes, with a focus on performance and scalability. 

## Core Features

* **Distributed and Decentralized Architecture**: The system is composed of three types of BCM4Java components:
    * **Sensor Nodes**: Simulate physical sensors (e.g., temperature, smoke) and locally execute query fragments.
    * **Global Registry**: Allows nodes to register and dynamically discover their geographical neighbors to form a mesh network. 
    * **Clients (Supervisors)**: Initiate queries on the network to gather data or detect alerts. 

* **Dedicated Query Language**: An interpreter was developed for a project-specific query language, allowing users to:
    * Gather specific sensor data (`GQUERY`). 
    * Evaluate complex boolean expressions for alert detection (`BQUERY`). 

* **Query Propagation (Continuations)**: Queries can be propagated across the network using two distinct strategies:
    * **Directional (`DCONT`)**: Follows a path of nodes in specified geographical directions (e.g., North-East, South-West) for a limited number of hops. 
    * **Flooding (`FCONT`)**: Propagates to all nodes within a defined geographical radius from an origin point. 

* **Dual Execution Models**:
    * **Synchronous (Direct Style)**: A simple model where a node calls its neighbors, waits for their results, aggregates them, and returns the combined result to its caller. 
    * **Asynchronous (Continuation-Passing Style)**: A high-performance model featuring non-blocking calls. The terminal nodes of the call tree send their results directly to the initiating client, enabling greater parallelism and better resource utilization. 

* **Concurrency and Parallelism Management**: Utilizes dedicated thread pools and critical sections to ensure thread-safe access to shared data during the simultaneous execution of multiple queries. 

* **High Modularity with Plugins**: The code is structured into reusable plugins for the "Sensor Node" and "Client" roles, enhancing modularity and reusability. 

## Technology Stack

* **Language**: Java SE 8 
* **Framework**: BCM4Java (Basic Component Model for Java) 

## Setup and Usage

> **Note**: This is a template based on the project specifications. You may need to adapt it to the actual project structure.

1.  **Prerequisites**
    * JDK 8 or higher
    * Apache Maven / Gradle
    * Eclipse IDE for Java Developers 

2.  **Clone the Repository**
    ```bash
    git clone [https://github.com/Tinshea/Sensor_Network.git](https://github.com/Tinshea/Sensor_Network.git)
    cd Sensor_Network
    ```

3.  **Build the Project**
    *(Add your build instructions here, e.g., using Maven)*
    ```bash
    mvn clean install
    ```

4.  **Run the Simulation**
    *(Explain how to launch the deployment here. For example, specify which main class to run to start the BCM4Java virtual machine and deploy the components.)*

## Test Scenarios

The project includes several timed test scenarios that use an `AcceleratedClock` to deterministically simulate events. [cite: 493, 494] These scenarios cover:
* Dynamic arrival and departure of nodes in the network. [cite: 499, 508]
* Simulated updates of sensor values. [cite: 501]
* Sending collection and boolean queries with different continuation strategies.
* Performance tests measuring query throughput under increasing load and with various thread allocations. [cite: 466, 513]
