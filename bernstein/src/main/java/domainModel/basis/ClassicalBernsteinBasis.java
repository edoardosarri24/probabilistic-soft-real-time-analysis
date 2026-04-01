package domainModel.basis;

public final class ClassicalBernsteinBasis extends BernsteinBasis {

    // Methods
    @Override
    protected double map(double x) {
        if (x < 0.0 || x > 1.0)
            throw new IllegalArgumentException("x must be within [0,1]");
        return x;
    }

}
