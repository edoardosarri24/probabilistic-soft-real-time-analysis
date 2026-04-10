package polynomial.bernsteinBasis;
import utils.MyMath;
import utils.MyUtils;

public final class LinearBernsteinbasis implements BernsteinBasis {

    private final double lowerSupport;
    private final double upperSupport;

    // Constructor.
    public LinearBernsteinbasis(double lowerSupport, double upperSupport) {
        if (lowerSupport >= upperSupport)
            throw new IllegalArgumentException(String.format(
        "The support is invalid: lower bound (%f) must be lower than the supper (%f)", lowerSupport, upperSupport));
        this.lowerSupport = lowerSupport;
        this.upperSupport = upperSupport;
    }

    // Methods.
    @Override
    public double eval(double x, int i, int degree) {
        MyUtils.validateRange(x, this.lowerSupport, this.upperSupport);
        double t = (x-lowerSupport) / (upperSupport-lowerSupport);
        return MyMath.binomialCoefficient(degree, i)
            * MyMath.intPow(t, i)
            * MyMath.intPow(1-t, degree-i);
    }

}
