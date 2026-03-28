# GEMINI.md - Simulator
The project is located in `simulator` folder.

## Project Overview
This project is a **Discrete Event Simulator (DES)** designed for **Probabilistic Soft Real-time Analysis**. It simulates the execution of a set of tasks with stochastic parameters (e.g., execution times and periods) to evaluate their timing behavior under different scheduling policies.

### Main Technologies
- **Language:** Java 24
- **Build Tool:** Maven
- **Core Library:** [Sirio](https://github.com/oris-tool/sirio) (used for samplers/distributions)
- **Documentation:** LaTeX (located in the `report/` directory)

### Architecture
The simulator follows a DES approach, jumping between critical time points (Events) rather than using constant time-stepping. A quite complete architecture is represented in [class diagram](report/images/1-simulator/class_diagram.mmd) file.

- **Data Model:**
    - `Task`: Defines the static properties (period distribution, deadline, execution time distribution).
    - `Job`: Represents a single instance of a task's execution with a specific release time and absolute deadline.
    - `TaskSet`: A collection of tasks to be simulated.
- **Events (`event` package):**
    - `ReleaseEvent`: Signals when a new job is released.
    - `DeadlineEvent`: A sentinel event to verify if a job has met its deadline at the exact instant of expiration.
- **Scheduling (`scheduler` package):**
    - `Scheduler`: Abstract base class managing the event queue (`PriorityQueue`), the clock, and the simulation loop.
    - `FixedPriorityScheduler`: Base for fixed-priority policies.
    - `DMScheduler`: Implementation of the Deadline Monotonic scheduling policy.
- **Utilities:**
    - `MyClock`: Tracks absolute simulation time using `java.time.Duration`.
    - `MyLogger`: Custom logging for simulation traces.

## Building and Running

### Mandatory Execution Command
To run the simulation and all tests, you MUST use only the following command:
```bash
./exec/simulator.sh
```
This script handles the SDKMAN configuration, builds the project, and executes both tests and the simulation. Do not invoke `mvn` directly unless instructed otherwise.

### Running Alternative Main Classes
If you need to execute a different class (e.g., for specific tests or alternative scenarios):
1. Open `simulator/pom.xml`.
2. Create a new class in [java](simulator/src/main/java)folder
3. Locate the `<mainClass>` tag within the `exec-maven-plugin` configuration.
4. Change `Main` to the fully qualified name of the desired class.
5. Run `./exec/simulator.sh`.
6. **Revert** the `<mainClass>` back to `Main` once the execution is complete.

## Development Conventions

### Coding Style
- **Type Safety:** High use of `java.time.Duration` for time representation to avoid precision issues.
- **Immutability:** Core models like `Task` and `Job` use final fields where possible.
- **Exceptions:** Custom exceptions like `DeadlineMissedException` are used to signal simulation-ending timing violations.
- **Readability over Efficiency:** Prioritize code clarity, declarative patterns (e.g., Streams, Pattern Matching), and logical separation over micro-optimizations or redundant checks, unless performance becomes a documented bottleneck.

### Testing Practices
- **JUnit & AssertJ:** The project uses JUnit 4 and AssertJ for testing (see `pom.xml`).
- **Validation:** Every code change should be validated by running `mvn test` to ensure the DES logic remains sound.

### Important Files
- `simulator/src/main/java/Main.java`: The entry point where the `TaskSet` and `Scheduler` are configured.

## Usage
To modify the simulation scenario, update the `Main.java` file by defining new `Task` objects with different samplers and adjusting the `simulationDuration` passed to the scheduler.
