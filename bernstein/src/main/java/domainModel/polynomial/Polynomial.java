package domainModel.polynomial;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.io.OutputStream;
import utils.MyUtils;

public abstract class Polynomial {

    private final double[] coefficients;

    // Constructor.
    /**
     * @param coefficients The coefficient that define the polynomial must be represented as: [c[0],c[1],c[2],...,c[n]].
     */
    public Polynomial(double[] coefficients) {
        MyUtils.requireNonNull(coefficients, "coefficients");
        if (coefficients.length == 0)
            throw new IllegalArgumentException("Polynomial must have at least one coefficient");
        this.coefficients = coefficients.clone();
    }

    // Getter and setter.
    public double[] getCoefficient() {
        return this.coefficients;
    }

    // Methods.
    public final double eval(double x) {
        int degree = this.coefficients.length - 1;
        double result = 0.0;
        for (int i=0; i <= degree; i++) {
            double basis = this.evalBasis(x, i, degree);
            result += this.coefficients[i] * basis;
        }
        return result;
    }

    public void visualize(String title, double lowerBound, double upperBound, int numbersOfPoints) {
        if (lowerBound >= upperBound)
            throw new IllegalArgumentException(String.format(
        "The support is invalid: lower bound (%f) must be lower than the upper (%f)", lowerBound, upperBound));
        if (numbersOfPoints <= 1)
            throw new IllegalArgumentException("There are few points to visualize the polynomial");
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode data = mapper.createObjectNode();
        data.put("title", title);
        ArrayNode xNode = data.putArray("x");
        ArrayNode yNode = data.putArray("y_poly");
        double stepSize = (upperBound - lowerBound) / (numbersOfPoints - 1);
        for (int i=0; i < numbersOfPoints; i++) {
            double x = lowerBound + i * stepSize;
            xNode.add(x);
            yNode.add(this.eval(x));
        }
        try {
            ProcessBuilder pb = new ProcessBuilder("uv", "run", "src/main/java/visualizer/polynomial.py");
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            Process process = pb.start();
            try (OutputStream os = process.getOutputStream()) {
                mapper.writeValue(os, data);
            }
            int exitCode = process.waitFor();
            if (exitCode != 0)
                System.err.println("Visualizer script failed with exit code: " + exitCode);
        } catch (IOException | InterruptedException e) {
            System.err.println("Error calling visualizer: " + e.getMessage());
        }
    }

    // Hooks.
    protected abstract double evalBasis(double x, int i, int degree);

}

