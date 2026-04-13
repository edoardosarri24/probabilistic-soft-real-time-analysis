package polynomial;

import java.util.function.DoubleUnaryOperator;

import utils.MyUtils;

/**
 * Utility class to calculate the distance between two polynomials using sampling methods.
 */
public final class PolynomialDistance {

    private PolynomialDistance() {}

    // Methods.
    /**
     * Calculates the L1 distance (integral of absolute difference) on [a, b] using the trapezoidal rule.
     */
    public static double normL1(Polynomial poly1, Polynomial poly2, double lowerBound, double upperBound, int numberOfSamples) {
        checkInputs(poly1, poly2, lowerBound, upperBound, numberOfSamples);
        double integral = integrateSimpson(
            lowerBound, upperBound,
            numberOfSamples,
            x -> Math.abs(poly1.eval(x) - poly2.eval(x)));
        return integral;
    }

    /**
     * Calculates the L2 distance (root of the integral of squared difference) on [a, b] using the trapezoidal rule.
     */
    public static double normL2(Polynomial poly1, Polynomial poly2, double lowerBound, double upperBound, int numberOfSamples) {
        checkInputs(poly1, poly2, lowerBound, upperBound, numberOfSamples);
        double integral = integrateSimpson(
            lowerBound, upperBound,
            numberOfSamples,
            x -> {
                double diff = poly1.eval(x) - poly2.eval(x);
                return diff * diff;
            });
        return Math.sqrt(integral);
    }

    /**
     * Calculates the L-infinity distance (maximum absolute difference) on [a,b].
     */
    public static double normLinf(Polynomial poly1, Polynomial poly2, double lowerBound, double upperBound, int numberOfSamples) {
        checkInputs(poly1, poly2, lowerBound, upperBound, numberOfSamples);
        double stepSize = (upperBound-lowerBound) / numberOfSamples;
        double max = 0.0;
        for (int i=0; i <= numberOfSamples; i++) {
            double x = lowerBound + i * stepSize;
            max = Math.max(max, Math.abs(poly1.eval(x) - poly2.eval(x)));
        }
        return max;
    }

    // Helper.
    private static void checkInputs(Polynomial poly1, Polynomial poly2, double lowerBound, double upperBound, int numberOfSamples) {
        MyUtils.requireNonNull(poly1, "poly1");
        MyUtils.requireNonNull(poly2, "poly2");
        if (lowerBound >= upperBound)
            throw new IllegalArgumentException(String.format(
        "The support is invalid: lower bound (%f) must be lower than the upper (%f)", lowerBound, upperBound));
        if (numberOfSamples <= 0)
            throw new IllegalArgumentException("numberOfSamples must be greater than 0");
    }

    /**
     * Generic numerical integration using Simpson's 1/3 rule.
     */
    private static double integrateSimpson(double lowerBound, double upperBound, int numberOfSamples, DoubleUnaryOperator integrand) {
        // Simpson requires that numberOfSamples is even.
        if (numberOfSamples%2 != 0)
            numberOfSamples++;
        double stepSize = (upperBound - lowerBound) / numberOfSamples;
        // First and last iteration with weight 1.
        double integral = integrand.applyAsDouble(lowerBound) + integrand.applyAsDouble(upperBound);
        // Others iteration with others weights (4 and 2).
        for (int i=1; i < numberOfSamples; i++) {
            double x = lowerBound + i * stepSize;
            if (i % 2 == 0) {
                integral += 2.0 * integrand.applyAsDouble(x);
            } else {
                integral += 4.0 * integrand.applyAsDouble(x);
            }
        }
        // Return.
        return integral * (stepSize / 3.0);
    }

}
