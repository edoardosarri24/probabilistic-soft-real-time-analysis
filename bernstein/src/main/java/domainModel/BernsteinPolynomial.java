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
    /**
     * Implementation of the De Casteljau's algorithm.
     */
    public double eval(double x) {
        double t = this.basis.clamp(x);
        // Degree chekcs.
        int degree = coefficients.length - 1;
        if (degree < 0)
            return 0.0;
        if (degree == 0)
            return coefficients[0];
        // Calculus.
        double[] coefficients = this.coefficients.clone();
        for (int j = 1; j <= degree; j++)
            for (int i = 0; i <= degree - j; i++)
                coefficients[i] = (1.0 - t) * coefficients[i] + t * coefficients[i+1];
        return coefficients[0];
    }

}
