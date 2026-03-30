package domainModel.basis;

import utils.MyUtils;

public final class ExponentialBernsteinBasis extends BernsteinBasis {

    private final double lambda;

    // Constructors
    public ExponentialBernsteinBasis(double lambda) {
        this.lambda = MyUtils.requirePositive(lambda, "lambda");
    }

    // Methods
    @Override
    public double clamp(double x) {
        x = MyUtils.requireNonNegative(x, "x");
        return 1.0 - Math.exp(-this.lambda * x);
    }

}
