package org.example.metrics;

public class Metrics {
    private long comparisons = 0;
    private long assignments = 0;
    private long iterations = 0;
    private long startTime = 0;
    private long elapsedNs = 0;
    private long memoryUsed = 0;

    public void incrementComparisons() {
        comparisons++;
    }

    public void incrementAssignments() {
        assignments++;
    }

    public void incrementIterations() {
        iterations++;
    }

    public void startTimer() {
        System.gc();
        startTime = System.nanoTime();
        memoryUsed = usedMemory();
    }

    public void stopTimer() {
        elapsedNs = System.nanoTime() - startTime;
        memoryUsed = usedMemory() - memoryUsed;
    }

    private long usedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    public long getComparisons() {
        return comparisons;
    }

    public long getAssignments() {
        return assignments;
    }

    public long getIterations() {
        return iterations;
    }

    public long getElapsedNs() {
        return elapsedNs;
    }

    public long getMemoryUsed() {
        return memoryUsed;
    }

    public void reset() {
        comparisons = 0;
        assignments = 0;
        iterations = 0;
        startTime = 0;
        elapsedNs = 0;
        memoryUsed = 0;
    }

    @Override
    public String toString() {
        return String.format(
                "Comparisons=%d, Assignments=%d, Iterations=%d, TimeNs=%d, MemoryBytes=%d",
                comparisons, assignments, iterations, elapsedNs, memoryUsed
        );
    }

}
