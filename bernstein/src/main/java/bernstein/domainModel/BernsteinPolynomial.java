package bernstein;

import java.util.Arrays;

/**
 * Represents a Bernstein polynomial B_n(x) = sum_{i=0}^n c_i * b_{i,n}(x)
 */
public class BernsteinPolynomial {

    private final double[] coefficients;
    private final BernsteinBasis basis;
    private final double supportMin;
    private final double supportMax;

    public BernsteinPolynomial(double[] coefficients, BernsteinBasis basis, double supportMin, double supportMax) {
        this.coefficients = coefficients.clone();
        this.basis = basis;
        this.supportMin = supportMin;
        this.supportMax = supportMax;
    }

    /**
     * Evaluates the polynomial at point x.
     * x must be within [supportMin, supportMax].
     */
    public double evaluate(double x) {
        // TODO: Handle domain transformation and sum weights
        return 0.0;
    }

    /**
     * Computes the derivative of the polynomial.
     * The derivative of a degree-n Bernstein polynomial is a degree-(n-1) Bernstein polynomial.
     */
    public BernsteinPolynomial derivative() {
        // TODO: Derivative implementation
        return null;
    }

    public double[] getCoefficients() {
        return coefficients.clone();
    }

    public BernsteinBasis getBasis() {
        return basis;
    }

    public double getSupportMin() {
        return supportMin;
    }

    public double getSupportMax() {
        return supportMax;
    }

    public int getDegree() {
        return coefficients.length - 1;
    }
    
}
