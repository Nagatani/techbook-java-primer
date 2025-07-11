package com.example.exception.monitoring;

import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Enhanced exception handler with automatic metrics collection.
 */
public class ExceptionHandler {
    private final ExceptionMetrics metrics;
    private final Consumer<Throwable> errorLogger;
    private final boolean rethrowExceptions;

    public ExceptionHandler(ExceptionMetrics metrics) {
        this(metrics, Throwable::printStackTrace, true);
    }

    public ExceptionHandler(ExceptionMetrics metrics, Consumer<Throwable> errorLogger, boolean rethrowExceptions) {
        this.metrics = metrics;
        this.errorLogger = errorLogger;
        this.rethrowExceptions = rethrowExceptions;
    }

    /**
     * Execute a runnable with exception monitoring.
     */
    public void monitor(Runnable task) {
        long start = System.nanoTime();
        try {
            task.run();
        } catch (Exception e) {
            handleException(e, System.nanoTime() - start);
        }
    }

    /**
     * Execute a supplier with exception monitoring.
     */
    public <T> T monitor(Supplier<T> task, T defaultValue) {
        long start = System.nanoTime();
        try {
            return task.get();
        } catch (Exception e) {
            handleException(e, System.nanoTime() - start);
            return defaultValue;
        }
    }

    /**
     * Execute a callable with exception monitoring.
     */
    public <T> T monitor(Callable<T> task, T defaultValue) {
        long start = System.nanoTime();
        try {
            return task.call();
        } catch (Exception e) {
            handleException(e, System.nanoTime() - start);
            return defaultValue;
        }
    }

    /**
     * Execute with custom exception handling.
     */
    public <T, E extends Exception> T monitorWithHandler(
            Supplier<T> task,
            Class<E> exceptionType,
            Function<E, T> exceptionHandler) {
        long start = System.nanoTime();
        try {
            return task.get();
        } catch (Exception e) {
            long elapsed = System.nanoTime() - start;
            metrics.recordException(e, elapsed);
            
            if (exceptionType.isInstance(e)) {
                @SuppressWarnings("unchecked")
                E typedException = (E) e;
                return exceptionHandler.apply(typedException);
            }
            
            errorLogger.accept(e);
            if (rethrowExceptions && e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException("Unexpected exception", e);
        }
    }

    /**
     * Batch exception monitoring for multiple operations.
     */
    public static class BatchMonitor {
        private final ExceptionMetrics metrics;
        private final BiConsumer<Integer, Throwable> batchErrorHandler;

        public BatchMonitor(ExceptionMetrics metrics, BiConsumer<Integer, Throwable> batchErrorHandler) {
            this.metrics = metrics;
            this.batchErrorHandler = batchErrorHandler;
        }

        /**
         * Execute a batch of tasks with individual exception monitoring.
         */
        public void executeBatch(Runnable... tasks) {
            for (int i = 0; i < tasks.length; i++) {
                long start = System.nanoTime();
                try {
                    tasks[i].run();
                } catch (Exception e) {
                    metrics.recordException(e, System.nanoTime() - start);
                    batchErrorHandler.accept(i, e);
                }
            }
        }

        /**
         * Execute a batch with stop-on-error behavior.
         */
        public int executeBatchStopOnError(Runnable... tasks) {
            for (int i = 0; i < tasks.length; i++) {
                long start = System.nanoTime();
                try {
                    tasks[i].run();
                } catch (Exception e) {
                    metrics.recordException(e, System.nanoTime() - start);
                    batchErrorHandler.accept(i, e);
                    return i; // Return index of failed task
                }
            }
            return tasks.length; // All tasks completed
        }
    }

    private void handleException(Exception e, long elapsed) {
        metrics.recordException(e, elapsed);
        errorLogger.accept(e);
        
        if (rethrowExceptions) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException("Wrapped exception", e);
        }
    }

    /**
     * Create a batch monitor.
     */
    public BatchMonitor createBatchMonitor(BiConsumer<Integer, Throwable> batchErrorHandler) {
        return new BatchMonitor(metrics, batchErrorHandler);
    }

    /**
     * Get the underlying metrics.
     */
    public ExceptionMetrics getMetrics() {
        return metrics;
    }
}