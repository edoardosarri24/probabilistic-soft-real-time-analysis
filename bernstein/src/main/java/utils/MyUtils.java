package utils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.OutputStream;
import java.util.Objects;

public final class MyUtils {

    // Constructor.
    private MyUtils() {}

    // Validation Methods.
    public static <T> T requireNonNull(T obj, String paramName) {
        return Objects.requireNonNull(obj, paramName + " cannot be null");
    }

    public static double requirePositive(double value, String paramName) {
        if (value <= 0)
            throw new IllegalArgumentException(paramName + " must be positive");
        return value;
    }

    public static double requireNonNegative(double value, String paramName) {
        if (value < 0)
            throw new IllegalArgumentException(paramName + " cannot be negative");
        return value;
    }

    // Methods.
    /**
     * Sends data to the Python visualizer using Jackson Streaming API.
     * @param xValues The x-coordinates of the data points.
     * @param yPolynomial The y-coordinates of the Bernstein polynomial.
     * @param yOriginal The y-coordinates of the original function (optional).
     */
    public static void callPythonVisualizer(double[] xValues, double[] yPolynomial, double[] yOriginal) {
        try {
            // Create a Python process using 'uv run' to handle dependencies automatically.
            ProcessBuilder pb = new ProcessBuilder("uv", "run", "src/visualizer.py");
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            Process process = pb.start();
            // Use Jackson to stream data directly to Python's stdin.
            try (OutputStream os = process.getOutputStream();
                JsonGenerator generator = new JsonFactory().createGenerator(os)) {
                generator.writeStartObject();
                // Write x values.
                generator.writeArrayFieldStart("x");
                for (double x : xValues)
                    generator.writeNumber(x);
                generator.writeEndArray();
                // Write polynomial y values.
                generator.writeArrayFieldStart("y_poly");
                for (double y : yPolynomial)
                    generator.writeNumber(y);
                generator.writeEndArray();
                // Write original y values if provided.
                if (Objects.nonNull(yOriginal)) {
                    generator.writeArrayFieldStart("y_orig");
                    for (double y : yOriginal)
                        generator.writeNumber(y);
                    generator.writeEndArray();
                }
                generator.writeEndObject();
                generator.flush();
            }
            // Java waits for Python to finish all the work.
            process.waitFor();
        } catch (Exception e) {
            System.err.println("Error calling Python visualizer (Jackson): " + e.getMessage());
            e.printStackTrace();
        }
    }

}
