# Simulator

This project is a Discrete Event Simulator (DES) designed for Probabilistic Soft Real-Time Analysis. It simulates the execution of a taskset with stochastic parameters to evaluate their timing behavior under a (eventually various) scheduling policies.

### Features
- **Stochastic Modeling**: Tasks are defined with probabilistic execution times and inter-arrival times (i.e., periods) using various samplers (e.g.,Constant, Uniform, Exponential).
- **Scheduling Policies**: Supports fixed-priority scheduling, including Deadline Monotonic (DM).
- **Simulation Trace**: Generates detailed execution traces for analysis.
- **Python Integration**: Includes a bridge to Python for post-simulation data processing and task execution time distribution extraction.

### Requirements
The project requires the following tools and libraries:
- **Maven**: For dependency management and building the project.
- **SDKMAN!**: Used to manage and switch between different Java SDK versions.
- **Java 24**: The core language used for the simulator.
- **Python**: For running the distribution extractor and data analysis scripts.
- **uv**: A fast Python package installer and resolver, used to run the `distribution_extractor.py` script and manage its dependencies.

### How to Use

##### Configuration
The simulation scenario is configured in `simulator/src/main/java/Main.java`. You can define your `TaskSet` by specifying:
- **Period Sampler**: The distribution of job arrival times.
- **Deadline**: The relative deadline for each task.
- **Execution Time Sampler**: The distribution of time required to complete a job.

##### Running the Simulation
To build and run the simulator, use the provided script from the project root:
```bash
./exec/simulator.sh
```
This script ensures the correct environment is set up and executes the `Main` class.

##### Output
The simulator produces:
- A file logs of the simulation events (if a logger like `TraceLogger` is used) in *results/* directory.
- The data extraction via Python, always in *results/* directory.

##### Testing
The project uses JUnit and AssertJ for validation. Note that `./exec/simulator.sh` also runs tests by default.
You can run the tests using:
```bash
mvn test
```
