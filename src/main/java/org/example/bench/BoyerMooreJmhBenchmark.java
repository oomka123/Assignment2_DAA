package org.example.bench;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.RunnerException;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadLocalRandom;

import org.example.algorithms.BoyerMooreMajorityVote;
import org.example.metrics.Metrics;

/**
 * JMH benchmarks for Boyer-Moore Majority Vote.
 *
 * Provides:
 *  - parameterized input sizes (n)
 *  - parameterized input distributions (random, sorted, reverse, nearly_sorted)
 *  - two modes: with internal metrics (cost of instrumentation) and without (null metrics)
 *
 * Usage (from IDE): run main()
 * Usage (Maven): mvn clean install && java -jar target/benchmarks.jar
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 1, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 2, time = 200, timeUnit = TimeUnit.MILLISECONDS)
@Fork(value = 1, jvmArgsAppend = {"-Xms256m", "-Xmx1g"})
public class BoyerMooreJmhBenchmark {

    /**
     * Size parameter (change/add sizes as needed). Common choices: 100, 1_000, 10_000, 100_000.
     */
    @Param({"100", "1000", "10000", "100000"})
    public int n;

    /**
     * Input distribution:
     * - random: uniform [0..9]
     * - sorted: ascending 0..n-1
     * - reverse: descending n-1..0
     * - nearly_sorted: sorted with 5% random swaps
     */
    @Param({"random", "sorted", "reverse", "nearly_sorted"})
    public String distribution;

    /**
     * Whether to pass a Metrics object to the algorithm (true -> measure instrumentation overhead)
     * "true" will create a fresh Metrics for each invocation; "false" will pass null (no metrics).
     */
    @Param({"false", "true"})
    public boolean withMetrics;

    // The array used for the benchmark run. Recreated for each trial/iteration based on @Setup level.
    private int[] arr;

    // Setup once per trial to avoid skew caused by array creation in measured method.
    @Setup(Level.Trial)
    public void setup() {
        arr = generateArray(n, distribution);
    }

    // Provide a per-invocation setup if you want to change array each invocation (not used by default).
    // @Setup(Level.Invocation)
    // public void setupInvocation() { arr = generateArray(n, distribution); }

    // Benchmark: measure findMajority with metrics == null (no instrumentation overhead)
    @Benchmark
    public Integer bench_findMajority_noMetrics() {
        // Use null metrics to measure raw algorithm performance (assuming findMajority handles null)
        return BoyerMooreMajorityVote.findMajority(arr, null);
    }

    // Benchmark: measure findMajority with a Metrics instance to include instrumentation overhead
    @Benchmark
    public Integer bench_findMajority_withMetrics() {
        Metrics metrics = new Metrics();
        return BoyerMooreMajorityVote.findMajority(arr, metrics);
    }

    // Optional: separate benchmark that calls the convenience majorityElement (uses internal metrics)
    @Benchmark
    public Integer bench_majorityElement_convenience() {
        return BoyerMooreMajorityVote.majorityElement(arr);
    }

    // Helper to create arrays for different distributions
    private static int[] generateArray(int n, String distribution) {
        int[] a = new int[n];
        switch (distribution) {
            case "random":
                ThreadLocalRandom rnd = ThreadLocalRandom.current();
                for (int i = 0; i < n; i++) a[i] = rnd.nextInt(0, 10); // small domain to allow possible majorities
                break;
            case "sorted":
                for (int i = 0; i < n; i++) a[i] = i;
                // already sorted
                break;
            case "reverse":
                for (int i = 0; i < n; i++) a[i] = n - i - 1;
                break;
            case "nearly_sorted":
                for (int i = 0; i < n; i++) a[i] = i;
                // swap about 5% of elements randomly
                int swaps = Math.max(1, n / 20);
                ThreadLocalRandom r = ThreadLocalRandom.current();
                for (int i = 0; i < swaps; i++) {
                    int x = r.nextInt(n);
                    int y = r.nextInt(n);
                    int tmp = a[x];
                    a[x] = a[y];
                    a[y] = tmp;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown distribution: " + distribution);
        }
        return a;
    }

    /**
     * Optional main to run JMH from IDE
     */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BoyerMooreJmhBenchmark.class.getSimpleName())
                .jvmArgsAppend("-Xms512m", "-Xmx2g")
                .shouldFailOnError(true)
                .build();

        new Runner(opt).run();
    }
}