package functionality;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import domainModel.polynomial.Polynomial;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.DoubleUnaryOperator;

import utils.MyUtils;

/**
 * Utility class to calculate the distance between two polynomials using sampling methods.
 */
public final class PolynomialDistance {

    private PolynomialDistance() {}

    public static void withPlot(String title, Polynomial poly1, Polynomial poly2, double lowerBound, double upperBound, int numberOfSamples) {
        // Input checks.
        MyUtils.requireNonNull(title, "title");
        MyUtils.requireNonNull(poly1, "poly1");
        MyUtils.requireNonNull(poly2, "poly2");
        if (lowerBound >= upperBound)
            throw new IllegalArgumentException(String.format(
        "The support is invalid: lower bound (%f) must be lower than the upper (%f)", lowerBound, upperBound));
        if (numberOfSamples <= 0)
            throw new IllegalArgumentException("numberOfSamples must be greater than 0");
        // Compute and visualyze distance.
        double normL1 = normL1(poly1, poly2, lowerBound, upperBound, numberOfSamples);
        double normL2 = normL2(poly1, poly2, lowerBound, upperBound, numberOfSamples);
        double normLinf = normLinf(poly1, poly2, lowerBound, upperBound, numberOfSamples);
        visualize(title, poly1, poly2, lowerBound, upperBound, numberOfSamples, normL1, normL2, normLinf);
    }

    // Methods.
    /**
     * Calculates the L1 distance (integral of absolute difference) on [a, b] using the simpson rule.
     */
    public static double normL1(Polynomial poly1, Polynomial poly2, double lowerBound, double upperBound, int numberOfSamples) {
        double integral = integrateSimpson(
            lowerBound, upperBound,
            numberOfSamples,
            x -> Math.abs(poly1.eval(x) - poly2.eval(x)));
        return integral;
    }

    /**
     * Calculates the L2 distance (root of the integral of squared difference) on [a, b] using the simpson rule.
     */
    public static double normL2(Polynomial poly1, Polynomial poly2, double lowerBound, double upperBound, int numberOfSamples) {
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
        double stepSize = (upperBound-lowerBound) / numberOfSamples;
        double max = 0.0;
        for (int i=0; i <= numberOfSamples; i++) {
            double x = lowerBound + i * stepSize;
            max = Math.max(max, Math.abs(poly1.eval(x) - poly2.eval(x)));
        }
        return max;
    }

    // Helper.
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

    private static void visualize(String title,
            Polynomial poly1, Polynomial poly2,
            double lowerBound, double upperBound,
            int numberOfPoints,
            double normL1, double normL2, double normLinf) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode data = mapper.createObjectNode();
        data.put("title", title);
        data.put("norm_l1", normL1);
        data.put("norm_l2", normL2);
        data.put("norm_linf", normLinf);
        ArrayNode xNode = data.putArray("x");
        ArrayNode y1Node = data.putArray("y1");
        ArrayNode y2Node = data.putArray("y2");
        double stepSize = (upperBound - lowerBound) / numberOfPoints ;
        for (int i=0; i <= numberOfPoints; i++) {
            double x = lowerBound + i * stepSize;
            xNode.add(x);
            y1Node.add(poly1.eval(x));
            y2Node.add(poly2.eval(x));
        }
        try {
            ProcessBuilder pb = new ProcessBuilder("uv", "run", "src/main/java/visualizer/polynomial_distance.py");
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            Process process = pb.start();
            try (OutputStream os = process.getOutputStream()) {
                mapper.writeValue(os, data);
            }
            int exitCode = process.waitFor();
            if (exitCode != 0)
                System.err.println("Distance visualizer script failed with exit code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            System.err.println("Error calling distance visualizer: " + e.getMessage());
        }
    }

}
