package com.example.exception.performance;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Benchmarks demonstrating how stack depth affects exception performance.
 * Shows the cost of stack trace generation at various call depths.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class StackTraceDepthBenchmark {

    @Param({"1", "10", "50", "100", "200"})
    private int stackDepth;

    /**
     * Benchmark exception performance at various stack depths.
     */
    @Benchmark
    public int exceptionAtDepth() {
        try {
            recursiveCall(stackDepth);
            return 0;
        } catch (RuntimeException e) {
            return -1;
        }
    }

    /**
     * Benchmark exception performance without stack trace at various depths.
     */
    @Benchmark
    public int exceptionAtDepthNoStackTrace() {
        try {
            recursiveCallNoStackTrace(stackDepth);
            return 0;
        } catch (NoStackTraceException e) {
            return -1;
        }
    }

    /**
     * Benchmark normal return at various stack depths (baseline).
     */
    @Benchmark
    public int normalReturnAtDepth() {
        return recursiveCallNormal(stackDepth);
    }

    private void recursiveCall(int depth) {
        if (depth <= 0) {
            throw new RuntimeException("Exception at depth");
        }
        recursiveCall(depth - 1);
    }

    private void recursiveCallNoStackTrace(int depth) {
        if (depth <= 0) {
            throw new NoStackTraceException("Exception at depth");
        }
        recursiveCallNoStackTrace(depth - 1);
    }

    private int recursiveCallNormal(int depth) {
        if (depth <= 0) {
            return -1;
        }
        return recursiveCallNormal(depth - 1);
    }

    /**
     * Exception that doesn't generate stack trace.
     */
    static class NoStackTraceException extends RuntimeException {
        public NoStackTraceException(String message) {
            super(message);
        }

        @Override
        public Throwable fillInStackTrace() {
            return this;
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(StackTraceDepthBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}