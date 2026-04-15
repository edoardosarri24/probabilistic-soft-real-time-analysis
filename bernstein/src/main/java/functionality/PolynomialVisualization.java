package functionality;

import java.io.IOException;
import java.io.OutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import domainModel.polynomial.Polynomial;

public final class PolynomialVisualization {

    private PolynomialVisualization() {}

    public static void visualize(String title, Polynomial poly, double lowerBound, double upperBound, int numbersOfPoints) {
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
            yNode.add(poly.eval(x));
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

}
