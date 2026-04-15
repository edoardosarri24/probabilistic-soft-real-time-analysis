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
     * Convolve this polyunomial with another one dealing with the coefficients.
     */
    public Polynomial convolve(Polynomial poly) {
        // Input checks.
        MyUtils.requireNonNull(poly, "poly");
        if (!(poly instanceof MonomialPolynomial))
            throw new IllegalArgumentException("Cannot convolve a MonomialPolynomial with a different polynomial base");
        // Input.
        double[] coefficientsThis = this.getCoefficient();
        double[] coefficientsPoly = poly.getCoefficient();
        int degreeThis = coefficientsThis.length;
        int degreePoly = coefficientsPoly.length;
        // Calculus.
        double[] result = new double[degreeThis + degreePoly - 1];
        for (int i=0; i < degreeThis; i++) {
            for (int j=0; j < degreePoly; j++)
                result[i+j] += coefficientsThis[i] * coefficientsPoly[j];
        }
        return new MonomialPolynomial(result);
    }

}
