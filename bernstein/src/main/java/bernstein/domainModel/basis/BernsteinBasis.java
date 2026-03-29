package bernstein;

/**
 * Interface for the Bernstein basis function B_{i,n}(x).
 */
public interface BernsteinBasis {

    /**
     * Evaluates the i-th basis function of degree n at point x.
     * x is typically in [0, 1] for the basis evaluation.
     */
    double eval(int i, int n, double x);
    
}
