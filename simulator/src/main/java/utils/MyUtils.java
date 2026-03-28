package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import taskSet.Task;

public final class MyUtils {

    private MyUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Sends simulation data to the Python extractor using a streaming approach.
     * This avoids loading the entire JSON string into Java memory.
     * 
     * @param data The collected execution times.
     */
    public static void callPythonExtractor(TaskExecutionTimeCollector data) {
        try {
            ProcessBuilder pb = new ProcessBuilder("python3", "distribution_extractor.py");
            Process process = pb.start();

            // Use a BufferedWriter to stream data directly to the Python process stdin
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                writer.write("{");
                Map<Task, List<Duration>> taskMap = data.getTaskExecutionTime();
                boolean firstTask = true;

                for (Map.Entry<Task, List<Duration>> entry : taskMap.entrySet()) {
                    if (!firstTask) {
                        writer.write(", ");
                    }
                    firstTask = false;

                    writer.write("\"" + entry.getKey().getId() + "\": [");
                    List<Duration> durations = entry.getValue();
                    for (int i = 0; i < durations.size(); i++) {
                        if (i > 0) {
                            writer.write(", ");
                        }
                        // Convert Duration to milliseconds (double)
                        writer.write(String.valueOf(durations.get(i).toNanos() / 1_000_000.0));
                    }
                    writer.write("]");
                }
                writer.write("}");
                writer.flush();
            }

            // Read Python script output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Handle potential errors from Python
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String error = errorReader.lines().collect(Collectors.joining("\n"));
            if (!error.isEmpty()) {
                System.err.println("Python Error Output:\n" + error);
            }

            process.waitFor();
        } catch (Exception e) {
            System.err.println("Error calling Python extractor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
