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
     * and collects performance metrics (internal metrics object).
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

    // existing getters
    public int getCandidate() { return candidate; }
    public boolean hasMajority() { return hasMajority; }
    public Metrics getMetrics() { return metrics; }

    /**
     * New: run algorithm but record metrics into the provided Metrics object.
     * If metrics == null, delegates to majorityElement(arr).
     */
    public static Integer findMajority(int[] arr, Metrics m) {
        if (m == null) {
            return majorityElement(arr);
        }

        m.reset();
        m.startTimer();

        if (arr == null || arr.length == 0) {
            m.stopTimer();
            return null;
        }

        int candidateLocal = -1;
        int count = 0;
        m.incrementAssignments(); // for count init

        // Phase 1: find candidate
        for (int num : arr) {
            m.incrementIterations();

            m.incrementComparisons();
            if (count == 0) {
                candidateLocal = num;
                m.incrementAssignments();
            }

            m.incrementComparisons();
            if (num == candidateLocal) {
                count++;
            } else {
                count--;
            }
            m.incrementAssignments();
        }

        // Phase 2: verify
        int freq = 0;
        m.incrementAssignments(); // freq
        for (int num : arr) {
            m.incrementIterations();
            m.incrementComparisons();
            if (num == candidateLocal) {
                freq++;
                m.incrementAssignments();
            }
        }

        m.incrementComparisons();
        boolean has = freq > arr.length / 2;
        m.incrementAssignments();

        m.stopTimer();
        return has ? candidateLocal : null;
    }

    /**
     * Existing convenience method used by tests: runs with internal metrics and returns result.
     */
    public static Integer majorityElement(int[] arr) {
        BoyerMooreMajorityVote bm = new BoyerMooreMajorityVote();
        bm.run(arr);
        return bm.hasMajority() ? bm.getCandidate() : null;
    }
}