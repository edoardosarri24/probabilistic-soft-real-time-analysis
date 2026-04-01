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
        // Degree chacks.
        int degree = coefficients.length - 1;
        if (degree < 0)
            return 0.0;
        // Calculation.
        double result = 0.0;
        for (int i=0; i <= degree; i++) {
            double basis = this.basis.eval(degree, i, x);
            result += coefficients[i] * basis;
        }
        return result;
    }


}
