
# Agent - Bernstein
The project is located in `bernstein` folder. The related documentation is in `report/src/2-bernstein.tex` file.

### Project Overview
This project provides a library to handle the Bernstein Polynomials.

##### Main Technologies
- **Language:** Java 24
- **Build Tool:** Maven
- **Documentation:** LaTeX

##### Architecture
The implementation follows a modular approach for defining polynomials and their underlying bases. The architecture is represented in [class diagram](report/images/2-bernstein/class_diagram.mmd).

- **Data Model (`domainModel` package):**
    - `BernsteinPolynomial`: Represents a Bernstein polynomial and provides evaluation methods.
    - `BernsteinBasis` (`basis` sub-package): Abstract base class for the Bernstein basis functions.
    - `LinearBernsteinBasis`: Implements a linear mapping for finite support.
    - `ExponentialBernsteinBasis`: Implements an exponential mapping for semi-infinite support.
- **Utilities (`utils` package):**
    - `MyMath`: Mathematical helper functions like binomial coefficients.
    - `MyUtils`: Input validation and general utilities.

### Building and Running

##### Mandatory Execution Command
To run the simulation and all tests, you MUST use only the following command:
```bash
./exec/bernstein.sh
```
This script handles the SDKMAN configuration, builds the project, and executes both tests and the simulation. Do not invoke `mvn` directly unless instructed otherwise.

##### Running Alternative Main Classes
If you need to execute a different class (e.g., for specific tests or alternative scenarios):
1. Open `bernstein/pom.xml`.
2. Create a new class in [java](bernstein/src/main/java) folder.
3. Locate the `<mainClass>` tag within the `exec-maven-plugin` configuration.
4. Change `Main` to the fully qualified name of the desired class.
5. Run `./exec/bernstein.sh`.
6. **Revert** the `<mainClass>` back to `Main` once the execution is complete.

### Development Conventions

##### Coding Style
- **Immutability:** Core models like `BernsteinPolynomial` and bases are immutable once constructed.
- **Validation:** Use `MyUtils` for proactive argument checking.

##### Testing Practices
- **JUnit 5 & AssertJ:** The project uses JUnit 5 (Jupiter) and AssertJ for testing.
- **Stochastic Testing:** Includes tests for verifying properties across distributions.

##### Important Files
- `bernstein/src/main/java/Main.java`: The current entry point for experimentation.
- `bernstein/src/main/java/Bernstein.java`: Intended as a central utility (facade) for Bernstein-related operations.
