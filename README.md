# Smart Home Energy Simulation

This repository contains a Java-based simulation framework for a household energy management (HEM) system. It models a smart home where appliances, generators, batteries, solar panels, and a central energy manager interact in a coordinated simulation environment.

The code is organized in successive project stages:

- `hem-2025-base`: shared interfaces and base abstractions for the home-energy management ecosystem.
- `hem-2025-e1`: component-based implementation of the first generation home energy manager and equipment components.
- `hem-2025-e2`: model-in-the-loop (MIL) simulation architecture and functional simulation scenarios.
- `hem-2025-e3`: cyber-physical and software-in-the-loop (CyPhy/SIL) extensions with real-time coordination and integration tests.
- `hem-adapter`: XML device descriptors and configuration files for equipment adapters.
- `doc`: generated Javadoc documentation for the project.
- `libs`: external Java libraries used by the simulator.

## Project goals

The project aims to simulate a smart-home electricity ecosystem with:

- energy-consuming appliances (heater, fan, hair dryer, refrigerator, washing machine, toaster)
- energy-producing or storing devices (solar panel, generator, batteries)
- an energy meter and central home energy manager
- model-based and real-time orchestration of equipment behavior
- integration testing of the whole system through simulation scenarios

## Repository structure

```text
smart-home-energy-simulation/
├── doc/                           # Generated Javadoc documentation
├── hem-2025-base/                 # Base interfaces and shared definitions
├── hem-2025-e1/                   # Initial component-based implementation
├── hem-2025-e2/                   # MIL simulation architecture and tests
├── hem-2025-e3/                   # CyPhy/SIL simulation and integration layer
├── hem-adapter/                   # XML device descriptors and configuration
├── images/                        # Architecture and simulation diagrams
├── libs/                          # Third-party Java libraries
├── .github/                       # Repository automation and instructions
├── .gitignore
├── README.md
└── ...
```

## Main areas of the codebase

### Base and HEM interfaces
The base package defines reusable interfaces and registration patterns for the home energy management domain, including the central registration mechanism used by appliances and the home energy manager.

### Equipment models
The e1 and e3 modules define equipment components such as:

- `Heater`
- `Fan`
- `HairDryer`
- `Refrigerator`
- `SolarPanel`
- `Generator`
- `Batteries`
- `ElectricMeter`
- `HEM`

These are implemented as component-based Java classes with connector and inbound/outbound port patterns used in the BCM component model.

### Simulation layers
The e2 and e3 modules add simulation and orchestration layers:

- MIL simulation for appliance behaviors and control flows
- SIL simulation with state and power models
- real-time component simulation architecture
- integration test scenarios exercising the entire HEM stack

## Key Java entry points

Notable classes in the repository include:

- `fr.sorbonne_u.components.hem2025e1.CVMIntegrationTest`
- `fr.sorbonne_u.components.hem2025e2.RunFunctionalTestGlobalSimulation`
- `fr.sorbonne_u.components.hem2025e2.RunFunctionalTestGlobal_RT_Simulation`
- `fr.sorbonne_u.components.hem2025e3.CVMIntegrationTest`
- `fr.sorbonne_u.components.hem2025e3.ComponentSimulationArchitectures`
- `fr.sorbonne_u.components.hem2025e3.GlobalCoupledModel`
- `fr.sorbonne_u.components.hem2025e3.GlobalSupervisor`

These classes are representative of the project’s main simulation and validation workflows.

## Build and run

This repository does not currently include a Maven or Gradle build file at the root, so it is primarily a source-based Java project using the libraries in `libs/` and an IDE or manual `javac` compilation workflow.

### Prerequisites

- JDK 8 or newer
- Java project configured in an IDE such as VS Code, Eclipse, or IntelliJ, or a manual `javac` workflow
- classpath including the JAR files in `libs/`

### Example manual compilation

From the repository root:

```bash
mkdir -p out
find hem-2025-base hem-2025-e1 hem-2025-e2 hem-2025-e3 -name "*.java" > sources.txt
javac -cp "libs/*" -d out @sources.txt
```

### Example execution

Run the e3 integration test:

```bash
java -cp "out:libs/*" fr.sorbonne_u.components.hem2025e3.CVMIntegrationTest
```

Run the e2 global functional simulation:

```bash
java -cp "out:libs/*" fr.sorbonne_u.components.hem2025e2.RunFunctionalTestGlobalSimulation
```

> The exact run command may vary depending on the Java IDE or runtime configuration, but the classes above are the main execution entry points.

## Simulation and documentation assets

- `doc/` contains the generated API reference in HTML form; open `doc/index.html` in a browser.
- `images/` contains diagrams for the architecture and simulation model.
- `hem-adapter/*.xml` contains device configuration descriptors for example appliances.

## License

The Java source files state that the project is distributed under the CeCILL-C license. For the exact legal terms, refer to the source headers and any project-level license information available in the repository.

## Notes

This project is not a typical application with a single main program; it is a research or academic simulation codebase built around home-energy component simulation, model orchestration, and integration testing. The best way to explore it is to start with the Javadoc in `doc/` and the `CVMIntegrationTest` and simulation classes under the e1/e2/e3 modules.
