package domainModel.basis;

public final class StandardBernsteinBasis extends BernsteinBasis {

    private final double supportMin;
    private final double supportMax;

    // Constructor
    public StandardBernsteinBasis() {
        this(0.0, 1.0);
    }

    public StandardBernsteinBasis(double supportMin, double supportMax) {
        if (supportMax <= supportMin)
            throw new IllegalArgumentException("supportMax must be strictly greater than supportMin");
        this.supportMin = supportMin;
        this.supportMax = supportMax;
    }

    // Methods
    @Override
    public double clamp(double x) {
        return (x - this.supportMin) / (this.supportMax - this.supportMin);
    }

}
