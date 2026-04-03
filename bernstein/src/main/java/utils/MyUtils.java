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
     * @param yValues The y-coordinates of the data points.
     */
    public static void callPythonVisualizer(double[] xValues, double[] yValues) {
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
                generator.writeArrayFieldStart("x");
                for (double x : xValues)
                    generator.writeNumber(x);
                generator.writeEndArray();
                generator.writeArrayFieldStart("y");
                for (double y : yValues)
                    generator.writeNumber(y);
                generator.writeEndArray();
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
