package domainModel.polynomial;
import domainModel.polynomial.bernsteinBasis.BernsteinBasis;
import utils.MyMath;
import utils.MyUtils;

public final class BernsteinPolynomial extends Polynomial {

    private BernsteinBasis basis;

    // Constructor.
    /**
     * @param coefficients The coefficient that define the polynomial must be represented as: [c[0],c[1],c[2],...,c[n]].
     * @param basis The {@link BernsteinBasis} type.
     */
    public BernsteinPolynomial(double[] coefficients, BernsteinBasis basis) {
        super(coefficients);
        this.basis = MyUtils.requireNonNull(basis, "basis");
    }

    // Hooks.
    @Override
    protected double evalBasis(double x, int i, int degree) {
        double t = this.basis.map(x);
        return MyMath.binomialCoefficient(degree, i)
            * MyMath.intPow(t, i)
            * MyMath.intPow(1-t, degree-i);
    }

    @Override
    public Polynomial convolve(Polynomial poly) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convolve'");
    }

}
