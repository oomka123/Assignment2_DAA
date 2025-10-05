package org.example.metrics;

/**
 * Metrics for algorithm analysis:
 * Counts comparisons, assignments, iterations, time, and memory.
 */

public class Metrics {
    private long comparisons = 0;
    private long assignments = 0;
    private long iterations = 0;
    private long startTime = 0;
    private long elapsedNs = 0;
    private long startMemory = 0;
    private long endMemory = 0;

    public void incrementComparisons() { comparisons++; }
    public void incrementAssignments() { assignments++; }
    public void incrementIterations() { iterations++; }

    public void startTimer() {
        System.gc();
        try { Thread.sleep(20); } catch (InterruptedException ignored) {}
        startTime = System.nanoTime();
        startMemory = usedMemory();
    }

    public void stopTimer() {
        elapsedNs = System.nanoTime() - startTime;
        System.gc();
        try { Thread.sleep(20); } catch (InterruptedException ignored) {}
        endMemory = usedMemory();
    }

    private long usedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    public long getComparisons() { return comparisons; }
    public long getAssignments() { return assignments; }
    public long getIterations() { return iterations; }
    public long getElapsedNs() { return elapsedNs; }
    public double getElapsedMs() { return elapsedNs / 1_000_000.0; }
    public long getMemoryUsed() { return Math.max(0, endMemory - startMemory); }

    public void reset() {
        comparisons = assignments = iterations = 0;
        startTime = elapsedNs = 0;
        startMemory = endMemory = 0;
    }

    public void merge(Metrics other) {
        this.comparisons += other.comparisons;
        this.assignments += other.assignments;
        this.iterations += other.iterations;
        this.elapsedNs += other.elapsedNs;
    }

    @Override
    public String toString() {
        return String.format(
                "Comparisons=%d, Assignments=%d, Iterations=%d, Time=%.3f ms, Memory=%d bytes",
                comparisons, assignments, iterations, getElapsedMs(), getMemoryUsed()
        );
    }
}
