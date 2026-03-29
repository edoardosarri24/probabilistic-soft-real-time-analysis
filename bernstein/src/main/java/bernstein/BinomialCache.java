package bernstein;

/**
 * Utility to pre-calculate and cache binomial coefficients (n choose k).
 */
public class BinomialCache {

    private final double[][] table;

    public BinomialCache(int maxN) {
        this.table = new double[maxN + 1][maxN + 1];
        // TODO: Initialize table
    }

    public double get(int n, int k) {
        // TODO: Return nCk
        return 0.0;
    }

}
