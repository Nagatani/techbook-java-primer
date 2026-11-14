package com.example.exception.performance;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Basic exception performance benchmarks comparing normal flow with exception flow.
 * This demonstrates the fundamental cost of exception handling in Java.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class BasicExceptionBenchmark {

    private static final int COMPUTATION_VALUE = 42;

    // 定数を直接渡すとJITが計算を畳み込み、何も測れなくなる。
    // @Stateのフィールドから読むことで、毎回の計算を測る
    private int input = COMPUTATION_VALUE;

    /**
     * Benchmark normal control flow without exceptions.
     * This serves as our baseline for comparison.
     */
    @Benchmark
    public int normalFlow() {
        return computeValue(input);
    }

    /**
     * Benchmark exception flow with full stack trace generation.
     * This shows the worst-case performance impact of exceptions.
     */
    @Benchmark
    public int exceptionFlow() {
        try {
            return computeValueWithException(input);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Benchmark exception flow without stack trace generation.
     * This demonstrates optimization potential by avoiding stack traces.
     */
    @Benchmark
    public int exceptionFlowNoStackTrace() {
        try {
            return computeValueWithCustomException(input);
        } catch (CustomException e) {
            return -1;
        }
    }

    /**
     * Benchmark try-catch block overhead when no exception is thrown.
     * This measures the cost of just having exception handling code.
     */
    @Benchmark
    public int tryCatchOverheadNoException() {
        try {
            return computeValue(input);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Benchmark cached exception reuse.
     * This shows the benefit of reusing exception instances.
     */
    private static final RuntimeException CACHED_EXCEPTION = 
        new RuntimeException("Cached exception") {
            @Override
            public Throwable fillInStackTrace() {
                return this;
            }
        };

    @Benchmark
    public int cachedExceptionFlow() {
        try {
            throw CACHED_EXCEPTION;
        } catch (RuntimeException e) {
            return -1;
        }
    }

    // Helper methods
    private int computeValue(int input) {
        return input * 2;
    }

    private int computeValueWithException(int input) throws Exception {
        if (input == COMPUTATION_VALUE) {
            throw new RuntimeException("Example exception with full stack trace");
        }
        return input * 2;
    }

    private int computeValueWithCustomException(int input) throws CustomException {
        if (input == COMPUTATION_VALUE) {
            throw new CustomException("Example exception without stack trace");
        }
        return input * 2;
    }

    /**
     * Custom exception that skips stack trace generation for performance.
     */
    static class CustomException extends Exception {
        public CustomException(String message) {
            super(message);
        }

        @Override
        public Throwable fillInStackTrace() {
            // Skip stack trace generation for performance
            return this;
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(BasicExceptionBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}