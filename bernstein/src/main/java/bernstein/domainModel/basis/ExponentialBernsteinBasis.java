package bernstein;

/**
 * Exponential Bernstein basis for distributions with infinite support.
 */
public final class ExponentialBernsteinBasis implements BernsteinBasis {

    private final double lambda;

    public ExponentialBernsteinBasis(double lambda) {
        this.lambda = lambda;
    }

    @Override
    public double eval(int i, int n, double x) {
        // TODO: Implementation for exponential basis
        return 0.0;
    }

    public double getLambda() {
        return lambda;
    }

}
