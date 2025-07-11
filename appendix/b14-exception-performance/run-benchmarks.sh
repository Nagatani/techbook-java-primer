#!/bin/bash

# Script to run various exception performance benchmarks

echo "Building project..."
mvn clean package

if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

echo ""
echo "=== Running Exception Performance Benchmarks ==="
echo ""

# Function to run a benchmark and save results
run_benchmark() {
    local benchmark=$1
    local output_file=$2
    local params=$3
    
    echo "Running $benchmark..."
    if [ -z "$params" ]; then
        java -jar target/benchmarks.jar "$benchmark" -rf json -rff "results/${output_file}.json"
    else
        java -jar target/benchmarks.jar "$benchmark" -rf json -rff "results/${output_file}.json" $params
    fi
    echo "Results saved to results/${output_file}.json"
    echo ""
}

# Create results directory
mkdir -p results

# Run benchmarks
run_benchmark "BasicExceptionBenchmark" "basic-exception"
run_benchmark "StackTraceDepthBenchmark" "stack-depth" "-p stackDepth=1,10,50,100,200"
run_benchmark "ExceptionTypeBenchmark" "exception-types"

echo "All benchmarks completed!"
echo ""
echo "To view results:"
echo "  cat results/*.json"
echo ""
echo "To run the demo:"
echo "  mvn exec:java -Dexec.mainClass=\"com.example.exception.Demo\""