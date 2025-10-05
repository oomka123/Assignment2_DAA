# Assignment2_DAA – Boyer-Moore Majority Vote Benchmark

## Overview

This project implements the Boyer-Moore Majority Vote algorithm, a linear-time algorithm to find the majority element in an array, combined with a benchmarking CLI runner that measures runtime and operation metrics. The system is built to support configurable array sizes and outputs results into a CSV file for further analysis.

---

## Features
- Boyer-Moore Majority Vote Algorithm
- Single-pass, O(n) time complexity
- Constant O(1) auxiliary space
- Metrics Tracking
- Comparisons, assignments, iterations
- Elapsed time in milliseconds (fractional)
- Memory usage estimation
- CLI Benchmark Runner
- Customizable array sizes via command-line arguments
- Generates random integer arrays
- Outputs results to CSV
- CSV Export
- Automatically appends results for each benchmark
- Columns: timestamp, algorithm, n, time_ms, comparisons, assignments, iterations, memory_bytes

---

## Usage

### 1. Build the project

Using Maven:

`mvn clean package`

This will create jars in target/Assingment2_DAA-1.0-SNAPSHOT.jar and target/benchmarks.

### 2. Run the benchmark via CLI
   `java -jar target/Assingment2_DAA-1.0-SNAPSHOT.jar --sizes 1000 5000 10000 --output benchmarks.csv --with-majority`

   Options:
   - --sizes <n1> <n2> ... : Specify array sizes to benchmark. Defaults: 100, 1000, 10000.
   - --output <file> : CSV file path to store benchmark results. Defaults: benchmarks.csv.
   - --with-majority : Fill array with a guaranteed majority element for testing correctness.

### 3. Example Output
  Running BoyerMooreMajorityVote benchmark...  
  Size=1000 -> time=0.003215 ms, result=500, comparisons=1605  
  Size=5000 -> time=0.012842 ms, result=2500, comparisons=8005  
  Size=10000 -> time=0.024120 ms, result=5000, comparisons=16008  
  Benchmark finished. Results appended to: benchmarks.csv  

### 4. Run the benchmark via JMH
   `java -jar target/benchmark.jar`

### Complexity Analysis

**Boyer-Moore Majority Vote**
- Time Complexity: O(n)
- Single pass through the array to select candidate.
- Optional verification pass to ensure candidate is truly the majority.
- Space Complexity: O(1)
- Only a few counters and a candidate variable are stored, independent of input size.

**BenchmarkRunner**
- Time Complexity: O(n) per array size
- Generates array (O(n))
- Runs algorithm (O(n))
- Writes to CSV (O(1) per row)
- Space Complexity: O(n)
- Storage of the generated integer array.
- Minimal overhead for metrics tracking and output.

---

## Design Notes
- Metrics class tracks both algorithmic operations (comparisons, assignments, iterations) and runtime/memory usage.
- CSVWriter separates concerns by handling only output formatting and file operations.
- Main / CLI delegates to BenchmarkRunner.run(args) to allow both programmatic and CLI execution.

---

## Best Practices
- Always specify array sizes appropriate for your system memory.
- Use --with-majority for correctness validation benchmarks.
- Multiple benchmark runs provide more stable measurements due to JVM JIT warmup.

---

## Dependencies
- Java 24+
- Maven
- JUnit 5 (for unit and edge-case testing)

---

## References
- Boyer, R. S., & Moore, J. S. (1981). MJRTY — a fast majority vote algorithm.
- Oracle Java Documentation: Runtime and Memory Management   
