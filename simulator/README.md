
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
The simulation scenario is configured using a **JSON** or **YAML** file. By default, the simulator looks for `config.json` in the `simulator/src` directory.

The configuration file defines:
- **Simulation Duration**: Total simulation time in milliseconds (`simulationDurationMs`).
- **Deadline Miss Strategy**: Strategy to handle jobs that miss their deadline (`continue`, `abortJob` an `abortSimulation`).
- **Tasks**: A list of tasks, where each task defines:
    - **Period**: The distribution of job arrival times. We have the following possibilities:
      - `deterministic`.
      - `discreteChoice`: with `values` and `probabilities`.
    - **Deadline**: The relative deadline in milliseconds (`deadlineMs`).
    - **Execution Time**: The distribution of time required to complete a job.
    - **First Release Time** (optional): The distribution of the first job's release time.

##### Example JSON (`config.json`)
```json
{
  "simulationDurationMs": 5000000,
  "deadlineMissStrategy": { "type": "continue" },
  "tasks": [
    {
      "period": { "type": "deterministic", "value": 35 },
      "deadlineMs": 35,
      "executionTime": { "type": "deterministic", "value": 34 }
    }
  ]
}
```

##### Example YAML (`config.yaml`)
```yaml
simulationDurationMs: 5000000
deadlineMissStrategy:
  type: continue
tasks:
  - period:
      type: deterministic
      value: 35
    deadlineMs: 35
    executionTime:
      type: deterministic
      value: 34
```

##### Running the Simulation
To build and run the simulator, use the provided script from the project root:
```bash
./exec/simulator.sh
```

To use a custom configuration file (e.g., `my_config.yaml`), you can run the simulator directly with:
```bash
cd simulator && mvn exec:java -Dexec.args="my_config.yaml"
```

##### Output
The simulator produces:
- A file logs of the simulation events (if a logger like `TraceLogger` is used) in *results/* directory.
- The data extraction via Python in *results/distributions* directory.

##### Testing
The project uses JUnit and AssertJ for validation. Note that `./exec/simulator.sh` also runs tests by default.
You can run the tests using:
```bash
mvn test
```
