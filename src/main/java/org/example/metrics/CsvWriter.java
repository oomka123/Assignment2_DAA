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
        public final long timeNs;
        public final long comparisons;
        public final long assignments;
        public final long iterations;
        public final long memoryBytes;

        public Record(String algorithm, int n, Metrics metrics) {
            this.timestamp = Instant.now();
            this.algorithm = algorithm;
            this.n = n;
            this.timeNs = metrics.getElapsedNs();
            this.comparisons = metrics.getComparisons();
            this.assignments = metrics.getAssignments();
            this.iterations = metrics.getIterations();
            this.memoryBytes = metrics.getMemoryUsed();
        }
    }

    public static void write(String path, List<Record> records, boolean append) throws IOException {
        File file = new File(path);
        boolean fileExists = file.exists();

        try (PrintWriter pw = new PrintWriter(new FileWriter(file, append))) {
            if (!fileExists) {
                pw.println("timestamp,algorithm,n,time_ns,comparisons,assignments,iterations,memory_bytes");
            }

            for (Record r : records) {
                pw.printf(Locale.US, "%s,%s,%d,%d,%d,%d,%d,%d%n",
                        r.timestamp.toString(),
                        r.algorithm,
                        r.n,
                        r.timeNs,
                        r.comparisons,
                        r.assignments,
                        r.iterations,
                        r.memoryBytes
                );
            }
        }
    }

}
