package domainModel;

import domainModel.basis.BernsteinBasis;

/**
 * Represents a Bernstein polynomial B_n(x) = sum_{i=0}^n c_i * b_{i,n}(x)
 */
public class BernsteinPolynomial {

    private final double[] coefficients;
    private final BernsteinBasis basis;

    public BernsteinPolynomial(double[] coefficients, BernsteinBasis basis) {
        this.coefficients = coefficients.clone();
        this.basis = basis;
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

}
