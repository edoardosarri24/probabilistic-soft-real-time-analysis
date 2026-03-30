

import java.util.function.Function;

import domainModel.BernsteinPolynomial;
import domainModel.basis.BernsteinBasis;

/**
 * Service to build Bernstein polynomials.
 */
public final class Bernstein {

    private Bernstein() {} // Stateless utility class

    /**
     * Builds a Bernstein polynomial of degree n to approximate function f on [supportMin, supportMax].
     */
    public static BernsteinPolynomial fromFunction(Function<Double, Double> f, int n, BernsteinBasis basis) {
        // TODO: Sampling f at i/n points and creating polynomial
        return null;
    }

    /**
     * Builds a Bernstein polynomial from pre-sampled values.
     */
    public static BernsteinPolynomial fromSamples(double[] samples, BernsteinBasis basis) {
        // TODO: Creating polynomial with BernsteinBasis
        return null;
    }

}
