package domainModel.polynomial;
import domainModel.polynomial.bernsteinBasis.BernsteinBasis;
import utils.MyMath;

public final class BernsteinPolynomial extends Polynomial {

    private BernsteinBasis basis;

    // Constructor.
    public BernsteinPolynomial(double[] coefficients, BernsteinBasis basis) {
        super(coefficients);
        this.basis = basis;
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
