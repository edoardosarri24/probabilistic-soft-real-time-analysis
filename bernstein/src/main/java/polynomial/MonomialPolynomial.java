package polynomial;

import utils.MyMath;

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

}
