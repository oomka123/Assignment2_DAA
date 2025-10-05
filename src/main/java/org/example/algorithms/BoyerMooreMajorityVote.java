package org.example.algorithms;

import org.example.metrics.Metrics;

/**
 * Boyer–Moore Majority Vote Algorithm
 * Defines an element that occurs more than ⌊n/2⌋ times (if it exists).
 * The algorithm runs in O(n) and uses O(1) memory.
 */

public final class BoyerMooreMajorityVote {

    private int candidate;
    private boolean hasMajority;
    private final Metrics metrics = new Metrics();

    public BoyerMooreMajorityVote() {
        this.candidate = -1;
        this.hasMajority = false;
    }

    /**
     * Runs the Boyer–Moore algorithm for an array of integers
     * and collects performance metrics.
     */

    public void run(int[] arr) {
        metrics.reset();
        metrics.startTimer();

        if (arr == null || arr.length == 0) {
            hasMajority = false;
            metrics.stopTimer();
            return;
        }

        int count = 0;
        metrics.incrementAssignments();

// --- Phase 1: Candidate search ---
        for (int num : arr) {
            metrics.incrementIterations();

            metrics.incrementComparisons();
            if (count == 0) {
                candidate = num;
                metrics.incrementAssignments();
            }

            metrics.incrementComparisons();
            if (num == candidate) {
                count++;
            } else {
                count--;
            }
            metrics.incrementAssignments();
        }

// --- Phase 2: Candidate verification ---
        count = 0;
        metrics.incrementAssignments();

        for (int num : arr) {
            metrics.incrementIterations();
            metrics.incrementComparisons();
            if (num == candidate) {
                count++;
                metrics.incrementAssignments();
            }
        }

        metrics.incrementComparisons();
        hasMajority = count > arr.length / 2;
        metrics.incrementAssignments();

        metrics.stopTimer();
    }

    public int getCandidate() {
        return candidate;
    }

    public boolean hasMajority() {
        return hasMajority;
    }

    public Metrics getMetrics() {
        return metrics;
    }

}