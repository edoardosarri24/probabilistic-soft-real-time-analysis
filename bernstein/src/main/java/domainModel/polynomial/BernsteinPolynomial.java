package domainModel.polynomial;
import domainModel.polynomial.bernsteinBasis.BernsteinBasis;

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
        return this.basis.eval(x, i, degree);
    }

}
