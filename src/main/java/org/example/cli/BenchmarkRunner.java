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
        if (sizes.isEmpty()) {
            sizes = Arrays.asList(100, 1000, 10000);
        }

        System.out.println("Running " + algorithmName + " benchmark...");

        for (int size : sizes) {
            int[] array = generateArray(size);
            Metrics metrics = new Metrics();
            long start = System.currentTimeMillis();
            Integer result = BoyerMooreMajorityVote.findMajority(array, metrics);
            long end = System.currentTimeMillis();
            double timeTaken = metrics.getElapsedMs();

            System.out.println("Size=" + size + " -> time=" + timeTaken + " ms, result=" + result + ", comparisons=" + metrics.getComparisons());

            // write to CSV using correct signature: appendRecord(Metrics, algorithm, n, path)
            try {
                CsvWriter.appendRecord(metrics, algorithmName, size, "benchmarks.csv");
            } catch (Exception e) {
                System.err.println("Failed to append CSV record: " + e.getMessage());
            }
        }
    }

    private static int[] generateArray(int n) {
        int[] array = new int[n];
        for (int i = 0; i < n; i++) array[i] = (int) (Math.random() * Math.max(1, n));
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
}