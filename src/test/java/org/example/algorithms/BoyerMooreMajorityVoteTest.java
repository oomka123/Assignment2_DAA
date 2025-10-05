package org.example.algorithms;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

class BoyerMooreMajorityVoteTest {

    // --- Helper: Assume BoyerMooreMajorityVote.majorityElement(int[]) is the method under test ---
    // If not, adjust method/class name accordingly.
    // For property-based and cross-validation, we need a brute-force majority finder:

    static Integer bruteForceMajority(int[] nums) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int n : nums) freq.put(n, freq.getOrDefault(n, 0) + 1);
        int threshold = nums.length / 2;
        for (Map.Entry<Integer, Integer> e : freq.entrySet()) {
            if (e.getValue() > threshold) return e.getKey();
        }
        return null;
    }

    // --- 1. Correctness Validation ---
    @Test
    @DisplayName("Edge Case: Empty Array")
    void testEmptyArray() {
        int[] arr = {};
        assertNull(BoyerMooreMajorityVote.majorityElement(arr), "Should return null for empty array");
    }

    @Test
    @DisplayName("Edge Case: Single Element Array")
    void testSingleElement() {
        int[] arr = {42};
        assertEquals(42, BoyerMooreMajorityVote.majorityElement(arr), "Single element should be majority");
    }

    @Test
    @DisplayName("Edge Case: All Duplicates")
    void testAllDuplicates() {
        int[] arr = {7,7,7,7,7};
        assertEquals(7, BoyerMooreMajorityVote.majorityElement(arr), "All duplicates should be majority");
    }

    @Test
    @DisplayName("Edge Case: No Majority")
    void testNoMajority() {
        int[] arr = {1,2,3,4,5,6};
        assertNull(BoyerMooreMajorityVote.majorityElement(arr), "No majority should return null");
    }

    @Test
    @DisplayName("Edge Case: Sorted Array with Majority")
    void testSortedMajority() {
        int[] arr = {2,2,2,3,3};
        assertEquals(2, BoyerMooreMajorityVote.majorityElement(arr), "Majority in sorted array");
    }

    @Test
    @DisplayName("Edge Case: Reverse Sorted Array with Majority")
    void testReverseSortedMajority() {
        int[] arr = {5,5,5,5,4,3,2}; // теперь 5 встречается 4 раза из 7
        assertEquals(5, BoyerMooreMajorityVote.majorityElement(arr), "Majority in reverse sorted array");
    }

    // --- Property-Based Testing ---
    @Test
    @DisplayName("Property-based: Random arrays vs brute-force")
    void testRandomArraysPropertyBased() {
        for (int t = 0; t < 50; ++t) {
            int n = ThreadLocalRandom.current().nextInt(0, 201);
            int[] arr = ThreadLocalRandom.current().ints(n, -50, 51).toArray();
            Integer expected = bruteForceMajority(arr);
            Integer actual = BoyerMooreMajorityVote.majorityElement(arr);
            assertEquals(expected, actual, "Random array property-based test failed");
        }
    }

    // --- Cross-Validation with Java Streams/Map ---
    @Test
    @DisplayName("Cross-validation: Streams frequency check")
    void testCrossValidationWithStreams() {
        int[] arr = {1,1,2,2,2,3,2,4,2};
        // Java Streams frequency check
        Map<Integer, Long> freq = Arrays.stream(arr).boxed().collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        int threshold = arr.length / 2;
        Integer expected = freq.entrySet().stream().filter(e -> e.getValue() > threshold).map(Map.Entry::getKey).findFirst().orElse(null);
        Integer actual = BoyerMooreMajorityVote.majorityElement(arr);
        assertEquals(expected, actual, "Cross-validation with streams failed");
    }

    // --- 2. Performance Testing ---
    static int[] makeArray(int n, String type) {
        int[] arr = new int[n];
        switch (type) {
            case "random":
                for (int i = 0; i < n; ++i) arr[i] = ThreadLocalRandom.current().nextInt(0, 10);
                break;
            case "sorted":
                for (int i = 0; i < n; ++i) arr[i] = i;
                Arrays.sort(arr);
                break;
            case "reverse":
                for (int i = 0; i < n; ++i) arr[i] = n - i - 1;
                break;
            case "nearly_sorted":
                for (int i = 0; i < n; ++i) arr[i] = i;
                for (int i = 0; i < n/10; ++i) { // swap 10% elements
                    int a = ThreadLocalRandom.current().nextInt(n);
                    int b = ThreadLocalRandom.current().nextInt(n);
                    int tmp = arr[a]; arr[a] = arr[b]; arr[b] = tmp;
                }
                break;
        }
        return arr;
    }

    @Test
    @DisplayName("Performance: Scalability test n = 10^2, 10^3, 10^4, 10^5")
    void testScalability() {
        int[] sizes = {100, 1000, 10000, 100000};
        String[] types = {"random", "sorted", "reverse", "nearly_sorted"};
        for (int n : sizes) {
            for (String type : types) {
                int[] arr = makeArray(n, type);
                long start = System.nanoTime();
                BoyerMooreMajorityVote.majorityElement(arr);
                long end = System.nanoTime();
                long ms = (end - start) / 1_000_000;
                // Not asserting, just printing for performance visibility.
                System.out.println("n=" + n + ", type=" + type + ": " + ms + " ms");
            }
        }
    }

    @Test
    @DisplayName("Performance: Memory profiling")
    void testMemoryProfiling() {
        int n = 100_000;
        int[] arr = makeArray(n, "random");
        Runtime rt = Runtime.getRuntime();
        rt.gc();
        long before = rt.totalMemory() - rt.freeMemory();
        BoyerMooreMajorityVote.majorityElement(arr);
        rt.gc();
        long after = rt.totalMemory() - rt.freeMemory();
        long used = after - before;
        System.out.println("Memory used for n=100000: " + used + " bytes");
        // Not asserting, but check that it runs and doesn't use excessive memory.
    }

    // --- 3. Peer Testing ---
    @Test
    @DisplayName("Integration: Algorithm runs without error")
    void testIntegration() {
        int[] arr = {1,2,2,3,2,4,2};
        assertDoesNotThrow(() -> BoyerMooreMajorityVote.majorityElement(arr));
    }

    @RepeatedTest(3)
    @DisplayName("Benchmark Reproduction: Repeated runs for stable performance")
    void testBenchmarkReproduction() {
        int[] arr = makeArray(10_000, "random");
        long start = System.nanoTime();
        Integer res = BoyerMooreMajorityVote.majorityElement(arr);
        long end = System.nanoTime();
        System.out.println("Repeated run: " + ((end - start) / 1_000_000) + " ms, result: " + res);
    }

    @Test
    @DisplayName("Optimization Validation: Run twice, compare time and verify same result")
    void testOptimizationValidation() {
        int[] arr = makeArray(20_000, "random");
        long start1 = System.nanoTime();
        Integer res1 = BoyerMooreMajorityVote.majorityElement(arr);
        long end1 = System.nanoTime();
        long start2 = System.nanoTime();
        Integer res2 = BoyerMooreMajorityVote.majorityElement(arr);
        long end2 = System.nanoTime();
        assertEquals(res1, res2, "Results should be identical on repeated runs");
        System.out.println("First run: " + ((end1 - start1) / 1_000_000) + " ms, Second run: " + ((end2 - start2) / 1_000_000) + " ms");
    }
}