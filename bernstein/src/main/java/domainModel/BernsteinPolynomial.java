package domainModel;

import domainModel.basis.BernsteinBasis;
import utils.MyMath;

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
        double t = this.basis.clamp(x);
        double oneMinusT = 1.0 - t;
        double result = 0.0;
        for (int i=0; i <= degree; i++) {
            double binomial = MyMath.binomialCoefficient(degree, i).doubleValue();
            double basisValue = binomial * Math.pow(t, i) * Math.pow(oneMinusT, degree-i);
            result += coefficients[i] * basisValue;
        }
        return result;
    }


}
