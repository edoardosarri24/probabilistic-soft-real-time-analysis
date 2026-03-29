package bernstein;

/**
 * Standard Bernstein basis: B_{i,n}(x) = (n choose i) * x^i * (1-x)^{n-i}
 */
public final class StandardBernsteinBasis implements BernsteinBasis {

    @Override
    public double eval(int i, int n, double x) {
        // TODO: Implementation with BinomialCache
        return 0.0;
    }

}
