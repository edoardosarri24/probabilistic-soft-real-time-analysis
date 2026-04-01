package domainModel.basis;

public final class LinearBernsteinBasis extends BernsteinBasis {

    private final double supportMin;
    private final double supportMax;

    // Constructor
    public LinearBernsteinBasis(double supportMin, double supportMax) {
        if (supportMax <= supportMin)
            throw new IllegalArgumentException("supportMax must be strictly greater than supportMin");
        this.supportMin = supportMin;
        this.supportMax = supportMax;
    }

    // Methods
    @Override
    protected double map(double x) {
        if (x < this.supportMin || x > this.supportMax)
            throw new IllegalArgumentException("x must be within the support bounds");
        return (x - this.supportMin) / (this.supportMax - this.supportMin);
    }

}
