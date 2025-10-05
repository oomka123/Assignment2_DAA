package org.example.cli;

import java.util.List;
import java.util.Arrays;
import org.example.algorithms.BoyerMooreMajorityVote;
import org.example.metrics.Metrics;
import org.example.metrics.CsvWriter;

/**
 * Simple CLI benchmark runner.
 * Usage example:
 *   java -jar yourapp.jar --sizes 100 1000 10000
 */
public class BenchmarkRunner {

    public static void main(String[] args) {
        run(args);
    }

    public static void run(String[] args) {
        String algorithmName = "BoyerMooreMajorityVote";
        List<Integer> sizes = parseSizes(args);
        String outputFile = parseOutput(args);
        boolean withMajority = parseWithMajority(args);
        if (sizes.isEmpty()) {
            sizes = Arrays.asList(1500, 1000, 10000);
        }
        if (outputFile == null) {
            outputFile = "benchmarks.csv";
        }
        // Print configuration
        System.out.println("Benchmark configuration:");
        System.out.println("  sizes = " + sizes);
        System.out.println("  output = " + outputFile);
        System.out.println("  withMajority = " + withMajority);
        System.out.println("Running " + algorithmName + " benchmark...");

        for (int size : sizes) {
            int[] array = generateArray(size, withMajority);
            Metrics metrics = new Metrics();
            long start = System.currentTimeMillis();
            Integer result = BoyerMooreMajorityVote.findMajority(array, metrics);
            long end = System.currentTimeMillis();
            double timeTaken = metrics.getElapsedMs();

            // Avoid null when printing result or comparisons
            String resultStr = (result != null) ? result.toString() : "null";
            long comparisons = (metrics != null) ? metrics.getComparisons() : 0;

            System.out.printf("Size=%d -> time=%.6f ms, result=%s, comparisons=%d%n",
                    size, timeTakenMs, resultStr, comparisons);

            try {
                CsvWriter.appendRecord(metrics, algorithmName, size, outputFile);
            } catch (Exception e) {
                System.err.println("Failed to append CSV record: " + e.getMessage());
            }
        }
    }

    private static int[] generateArray(int n, boolean withMajority) {
        int[] array = new int[n];
        if (withMajority && n > 0) {
            // 60% of array is filled with the majority element (e.g., 5)
            int majority = 5;
            int majorityCount = (int) Math.ceil(n * 0.6);
            for (int i = 0; i < majorityCount; i++) {
                array[i] = majority;
            }
            for (int i = majorityCount; i < n; i++) {
                int val;
                do {
                    val = (int) (Math.random() * Math.max(1, n + 1));
                } while (val == majority);
                array[i] = val;
            }
            for (int i = n - 1; i > 0; i--) {
                int j = (int) (Math.random() * (i + 1));
                int tmp = array[i];
                array[i] = array[j];
                array[j] = tmp;
            }
        } else {
            for (int i = 0; i < n; i++) array[i] = (int) (Math.random() * Math.max(1, n));
        }
        return array;
    }

    private static List<Integer> parseSizes(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if ("--sizes".equals(args[i]) && i + 1 < args.length) {
                int j = i + 1;
                while (j < args.length && !args[j].startsWith("--")) j++;
                String[] sizeStrings = Arrays.copyOfRange(args, i + 1, j);
                try {
                    Integer[] parsed = new Integer[sizeStrings.length];
                    for (int k = 0; k < sizeStrings.length; k++) parsed[k] = Integer.parseInt(sizeStrings[k]);
                    return Arrays.asList(parsed);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid size argument. Using default sizes.");
                    return Arrays.asList();
                }
            }
        }
        return Arrays.asList();
    }

    private static String parseOutput(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if ("--output".equals(args[i]) && i + 1 < args.length && !args[i + 1].startsWith("--")) {
                return args[i + 1];
            }
        }
        return null;
    }

    private static boolean parseWithMajority(String[] args) {
        for (String arg : args) {
            if ("--with-majority".equals(arg)) {
                return true;
            }
        }
        return false;
    }
}