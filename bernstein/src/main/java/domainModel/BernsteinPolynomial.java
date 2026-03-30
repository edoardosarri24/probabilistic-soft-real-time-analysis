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
        int degree = coefficients.length - 1;
        if (degree < 0)
            return 0.0;
        double result = 0.0;
        for (int i=0; i <= degree; i++)
            result += coefficients[i] * basis.eval(i, degree, x);
        return result;
    }

}
