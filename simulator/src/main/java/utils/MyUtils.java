package utils;

import java.io.OutputStream;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import taskSet.Task;

public final class MyUtils {

    // Constructor.
    private MyUtils() {
        // Private constructor to prevent instantiation
    }

    // Methods.
    /**
     * Sends simulation data to the Python extractor using Jackson Streaming API.
     * @param data The collected execution times data.
     */
    public static void callPythonExtractor(TaskExecutionTimeCollector data) {
        try {
            // Create a Python process using 'uv run' to handle dependencies automatically.
            ProcessBuilder pb = new ProcessBuilder("uv", "run", "distribution_extractor.py");
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            Process process = pb.start();
            // Use Jackson to stream data directly to Python's stdin.
            try (OutputStream os = process.getOutputStream();
                JsonGenerator generator = new JsonFactory().createGenerator(os)) {
                generator.writeStartObject();
                Map<Task, List<Duration>> taskMap = data.getTaskExecutionTime();
                for (Map.Entry<Task, List<Duration>> entry : taskMap.entrySet()) {
                    generator.writeArrayFieldStart(String.valueOf(entry.getKey().getId()));
                    for (Duration duration : entry.getValue())
                        generator.writeNumber(duration.toNanos() / 1_000_000.0);
                    generator.writeEndArray();
                }
                generator.writeEndObject();
                generator.flush();
            }
            // Java waits for Python to finish all the work.
            process.waitFor();
        } catch (Exception e) {
            System.err.println("Error calling Python extractor (Jackson): " + e.getMessage());
            e.printStackTrace();
        }
    }
}
