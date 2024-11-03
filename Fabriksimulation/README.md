# Factorix - A Factory Simulator

Factorix is a tool for simulating user-defined factory environments.
Users can supply configuration files describing a number of production workplaces, products, and processes.
Factorix creates a simulation of the described factory, allowing the user to create simulated orders and execute their production.

Users can control prioritization of the production tasks on the machines.
The resulting turnaround times and machine occupancy rates are written to a result file.
This allows users to experiment with different prioritization schemes and seeing which scheme optimizes production times.

## Features

- Configuration of workplaces, processes, and products via a configuration file.
  - Workplaces "produce" by running a process on a product for a configurable amount of time.
  - A product is complete once all its required processes are done.
- Users can create orders for a set number of products via a RESTful interface.
- Current statuses of products can be queried via REST.
- Users may modify task prioritization after creating an order (again, via REST).
- Machine occupancy rates and turnaround times of orders are tracked and written to a report file that is output after the simulation ends.

## Technologies

- Programming language: **Java**
- Build environment: **Gradle**
- Testing: **JUnit**
- REST API: **Java Spark**
- Report Generation: **Apache Velocity**
