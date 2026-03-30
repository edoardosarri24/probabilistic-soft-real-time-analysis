package domainModel;

import domainModel.basis.BernsteinBasis;

public class BernsteinPolynomial {

    private final double[] coefficients;
    private final BernsteinBasis basis;

    // Constructor
    public BernsteinPolynomial(double[] coefficients, BernsteinBasis basis) {
        this.coefficients = coefficients.clone();
        this.basis = basis;
    }

    // Methods.
    public double eval(double x) {
        // TODO
        return 0.0;
    }

}
