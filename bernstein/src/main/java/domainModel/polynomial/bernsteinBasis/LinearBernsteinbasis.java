package domainModel.polynomial.bernsteinBasis;
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
    public double map(double x) {
        MyUtils.validateRange(x, this.lowerSupport, this.upperSupport);
        double t = (x-lowerSupport) / (upperSupport-lowerSupport);
        return t;

    }

}
