package bernstein;

import java.util.function.Function;

/**
 * Service to build Bernstein polynomials.
 */
public final class BernsteinOperator {

    private BernsteinOperator() {} // Stateless utility class

    /**
     * Builds a Bernstein polynomial of degree n to approximate function f on [supportMin, supportMax].
     */
    public static BernsteinPolynomial fromFunction(Function<Double, Double> f, int n, double supportMin, double supportMax) {
        // TODO: Sampling f at i/n points and creating polynomial
        return null;
    }

    /**
     * Builds a Bernstein polynomial from pre-sampled values.
     */
    public static BernsteinPolynomial fromSamples(double[] samples, double supportMin, double supportMax) {
        // TODO: Creating polynomial with StandardBernsteinBasis
        return null;
    }

}
