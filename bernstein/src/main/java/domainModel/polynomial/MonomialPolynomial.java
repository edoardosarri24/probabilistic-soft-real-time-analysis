package domainModel.polynomial;

import utils.MyMath;
import utils.MyUtils;

public final class MonomialPolynomial extends Polynomial {

    // Constructor.
    public MonomialPolynomial(double[] coefficients) {
        super(coefficients);
    }

    // Hooks.
    @Override
    protected double evalBasis(double x, int i, int degree) {
        return MyMath.intPow(x, i);
    }

    /**
     * Convolve this polynomial with another one dealing with the coefficients.
     */
    public Polynomial convolve(Polynomial poly) {
        // Input checks.
        MyUtils.requireNonNull(poly, "poly");
        if (!(poly instanceof MonomialPolynomial))
            throw new IllegalArgumentException("Cannot convolve a MonomialPolynomial with a different polynomial base");
        // Input.
        double[] coefficientsThis = this.getCoefficient();
        double[] coefficientsPoly = poly.getCoefficient();
        int numCoeffsThis = coefficientsThis.length;
        int numCoeffsPoly = coefficientsPoly.length;
        // Calculus.
        double[] result = new double[numCoeffsThis + numCoeffsPoly - 1];
        for (int i=0; i < numCoeffsThis; i++) {
            for (int j=0; j < numCoeffsPoly; j++)
                result[i+j] += coefficientsThis[i] * coefficientsPoly[j];
        }
        return new MonomialPolynomial(result);
    }

}
