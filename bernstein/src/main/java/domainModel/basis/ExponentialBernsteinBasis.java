package domainModel.basis;

import utils.MyUtils;

public final class ExponentialBernsteinBasis extends BernsteinBasis {

    // Methods
    @Override
    protected double map(double x) {
        x = MyUtils.requireNonNegative(x, "x");
        return 1.0 - Math.exp(-x);
    }

}
