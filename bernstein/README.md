# Bernstein

This project provides a library to handle Bernstein polynomials, supporting their construction, evaluation, and approximation from different mathematical representations (i.e., Monomial polynomials or ECDF).

### Features
- **Polynomial Models**: Support for Bernstein and Monomial polynomials with modular architecture.
- **Approximation**: Algorithms to convert Monomial polynomials to Bernstein form (direct conversion or matrix inversion) and to approximate ECDF data.
- **Mathematical Utilities**: Efficient calculation of binomial coefficients and other mathematical helpers.
- **Visualization**: Integration with Python for plotting polynomials and their distances, facilitating analysis of approximation quality.

### Requirements
The project requires the following tools and libraries:
- **Maven**: For dependency management and building the project.
- **SDKMAN!**: Used to manage and switch between different Java SDK versions.
- **Java 24**: The core language used for the library.
- **Python**: For running the visualization and data analysis scripts.
- **uv**: A fast Python package installer and resolver, used to run the visualizer scripts and manage their dependencies.

##### Output
The library produces:
- PDF plots of polynomials and their distances in the *results/* directory.
- Comparative analysis data for approximation experiments.

### How to Use

##### Running Tests and Simulations
To build the project, run all JUnit tests, and execute the main experiment, use the provided script from the project root:
```bash
./exec/bernstein.sh
```

##### Executing Alternative Classes
If you need to execute a specific experiment or a different main class (e.g., `experiments.Main_monomial_approximation_support_0_1_N_equal_M`):
1. Open `bernstein/pom.xml`.
2. Locate the `<mainClass>` tag within the `exec-maven-plugin` configuration.
3. Change the value to the fully qualified name of the desired class.
4. Run the execution script again:
   ```bash
   ./exec/bernstein.sh
   ```
5. **Revert** the `<mainClass>` back to `Main` once the execution is complete to maintain the default entry point.
