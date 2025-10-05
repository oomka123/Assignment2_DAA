package org.example.metrics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

public class CsvWriter {

    public static class Record {
        public final Instant timestamp;
        public final String algorithm;
        public final int n;
        public final long timeMs;
        public final long comparisons;
        public final long assignments;
        public final long iterations;
        public final long memoryBytes;

        public Record(String algorithm, int n, Metrics metrics) {
            this.timestamp = Instant.now();
            this.algorithm = algorithm;
            this.n = n;
            this.timeMs = metrics.getElapsedNs() / 1_000_000;
            this.comparisons = metrics.getComparisons();
            this.assignments = metrics.getAssignments();
            this.iterations = metrics.getIterations();
            this.memoryBytes = metrics.getMemoryUsed();
        }
    }

    public static void write(String path, List<Record> records, boolean append) throws IOException {
        File file = new File(path);
        try {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                if (parent.mkdirs()) {
                    System.out.println("Created directories for path: " + parent.getAbsolutePath());
                } else if (!parent.exists()) {
                    throw new IOException("Failed to create directories for path: " + parent.getAbsolutePath());
                }
            }
            boolean fileExists = file.exists();

            try (PrintWriter pw = new PrintWriter(new FileWriter(file, append))) {
                if (!fileExists) {
                    pw.println("timestamp,algorithm,n,time_ms,comparisons,assignments,iterations,memory_bytes");
                }

                for (Record r : records) {
                    pw.printf(Locale.US, "%s,%s,%d,%d,%d,%d,%d,%d%n",
                            r.timestamp.toString(),
                            r.algorithm,
                            r.n,
                            r.timeMs,
                            r.comparisons,
                            r.assignments,
                            r.iterations,
                            r.memoryBytes
                    );
                }
                System.out.println("Successfully wrote " + records.size() + " record(s) to " + path);
            }
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
            throw e;
        }
    }

    public static void appendRecord(Metrics metrics, String algorithm, int n, String path) throws IOException {
        Record record = new Record(algorithm, n, metrics);
        write(path, List.of(record), true);
    }

}
