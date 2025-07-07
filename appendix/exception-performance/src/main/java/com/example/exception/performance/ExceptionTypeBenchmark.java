package com.example.exception.performance;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks comparing performance characteristics of different exception types.
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class ExceptionTypeBenchmark {

    /**
     * Benchmark checked exception performance.
     */
    @Benchmark
    public int checkedException() {
        try {
            throwCheckedException();
            return 0;
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * Benchmark runtime exception performance.
     */
    @Benchmark
    public int runtimeException() {
        try {
            throwRuntimeException();
            return 0;
        } catch (RuntimeException e) {
            return -1;
        }
    }

    /**
     * Benchmark error performance.
     */
    @Benchmark
    public int error() {
        try {
            throwError();
            return 0;
        } catch (Error e) {
            return -1;
        }
    }

    /**
     * Benchmark custom lightweight exception.
     */
    @Benchmark
    public int lightweightException() {
        try {
            throwLightweightException();
            return 0;
        } catch (LightweightException e) {
            return -1;
        }
    }

    /**
     * Benchmark exception with suppressed exceptions.
     */
    @Benchmark
    public int exceptionWithSuppressed() {
        try {
            throwExceptionWithSuppressed();
            return 0;
        } catch (RuntimeException e) {
            return -1;
        }
    }

    /**
     * Benchmark exception with cause chain.
     */
    @Benchmark
    public int exceptionWithCause() {
        try {
            throwExceptionWithCause();
            return 0;
        } catch (RuntimeException e) {
            return -1;
        }
    }

    private void throwCheckedException() throws IOException {
        throw new IOException("Checked exception");
    }

    private void throwRuntimeException() {
        throw new RuntimeException("Runtime exception");
    }

    private void throwError() {
        throw new AssertionError("Error");
    }

    private void throwLightweightException() {
        throw new LightweightException("Lightweight exception");
    }

    private void throwExceptionWithSuppressed() {
        RuntimeException main = new RuntimeException("Main exception");
        main.addSuppressed(new RuntimeException("Suppressed 1"));
        main.addSuppressed(new RuntimeException("Suppressed 2"));
        throw main;
    }

    private void throwExceptionWithCause() {
        throw new RuntimeException("Outer exception",
            new RuntimeException("Middle exception",
                new RuntimeException("Inner exception")));
    }

    /**
     * Lightweight exception without stack trace and with minimal overhead.
     */
    static class LightweightException extends RuntimeException {
        public LightweightException(String message) {
            super(message, null, false, false); // Disable suppression and stack trace
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ExceptionTypeBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}