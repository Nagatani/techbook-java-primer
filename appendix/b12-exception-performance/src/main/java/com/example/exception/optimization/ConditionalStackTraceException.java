package com.example.exception.optimization;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;

/**
 * Exception implementations with conditional stack trace generation.
 * Useful for production environments where stack traces are only needed occasionally.
 */
public class ConditionalStackTraceException extends RuntimeException {
    private final BooleanSupplier stackTraceCondition;

    public ConditionalStackTraceException(String message, BooleanSupplier stackTraceCondition) {
        super(message);
        this.stackTraceCondition = stackTraceCondition;
    }

    @Override
    public Throwable fillInStackTrace() {
        return stackTraceCondition.getAsBoolean() ? super.fillInStackTrace() : this;
    }

    /**
     * Environment-based stack trace generation.
     */
    public static class EnvironmentAwareException extends ConditionalStackTraceException {
        private static final boolean IS_DEVELOPMENT = 
            "development".equals(System.getProperty("app.environment", "production"));

        public EnvironmentAwareException(String message) {
            super(message, () -> IS_DEVELOPMENT);
        }
    }

    /**
     * Rate-limited stack trace generation.
     */
    public static class RateLimitedException extends ConditionalStackTraceException {
        private static final AtomicLong lastStackTraceTime = new AtomicLong(0);
        private static final long STACK_TRACE_INTERVAL_MS = 60_000; // 1 minute

        public RateLimitedException(String message) {
            super(message, RateLimitedException::shouldGenerateStackTrace);
        }

        private static boolean shouldGenerateStackTrace() {
            long now = System.currentTimeMillis();
            long lastTime = lastStackTraceTime.get();
            
            if (now - lastTime > STACK_TRACE_INTERVAL_MS) {
                return lastStackTraceTime.compareAndSet(lastTime, now);
            }
            return false;
        }
    }

    /**
     * Sampling-based stack trace generation.
     */
    public static class SamplingException extends ConditionalStackTraceException {
        private static final AtomicLong counter = new AtomicLong(0);
        private static final int SAMPLE_RATE = 100; // 1 in 100

        public SamplingException(String message) {
            super(message, () -> counter.incrementAndGet() % SAMPLE_RATE == 0);
        }
    }

    /**
     * Debug mode stack trace generation.
     */
    public static class DebugModeException extends ConditionalStackTraceException {
        private static volatile boolean debugMode = false;

        public DebugModeException(String message) {
            super(message, () -> debugMode);
        }

        public static void setDebugMode(boolean enabled) {
            debugMode = enabled;
        }

        public static boolean isDebugMode() {
            return debugMode;
        }
    }
}