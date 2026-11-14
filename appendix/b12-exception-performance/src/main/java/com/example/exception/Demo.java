package com.example.exception;

import com.example.exception.monitoring.ExceptionHandler;
import com.example.exception.monitoring.ExceptionMetrics;
import com.example.exception.optimization.ConditionalStackTraceException;
import com.example.exception.optimization.ExceptionPool;
import com.example.exception.optimization.ValidationException;
import com.example.exception.patterns.NullObject;
import com.example.exception.patterns.Result;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Demonstration of exception performance optimization techniques.
 */
public class Demo {
    
    public static void main(String[] args) {
        IO.println("=== Exception Performance Optimization Demo ===\n");
        
        demonstrateBasicPerformance();
        IO.println();
        
        demonstrateExceptionPooling();
        IO.println();
        
        demonstrateResultPattern();
        IO.println();
        
        demonstrateNullObjectPattern();
        IO.println();
        
        demonstrateConditionalStackTrace();
        IO.println();
        
        demonstrateExceptionMonitoring();
    }
    
    /**
     * Demonstrate basic exception performance comparison.
     */
    private static void demonstrateBasicPerformance() {
        IO.println("1. Basic Exception Performance Comparison");
        IO.println("-----------------------------------------");
        
        final int iterations = 100_000;
        
        // Normal flow
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            int result = divide(10, 2);
        }
        long normalTime = System.nanoTime() - start;
        
        // Exception flow
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            try {
                int result = divideWithException(10, 0);
            } catch (ArithmeticException e) {
                // Handle exception
            }
        }
        long exceptionTime = System.nanoTime() - start;
        
        IO.println("Normal flow: %d ms".formatted(TimeUnit.NANOSECONDS.toMillis(normalTime)));
        IO.println("Exception flow: %d ms".formatted(TimeUnit.NANOSECONDS.toMillis(exceptionTime)));
        IO.println("Exception overhead: %.2fx slower".formatted(
            (double) exceptionTime / normalTime));
    }
    
    /**
     * Demonstrate exception pooling for high-frequency scenarios.
     */
    private static void demonstrateExceptionPooling() {
        IO.println("2. Exception Pooling Demonstration");
        IO.println("----------------------------------");
        
        ExceptionPool<ValidationException> pool = new ExceptionPool<>(
            ValidationException::create, 10
        );
        
        final int iterations = 100_000;
        
        // Without pooling: スタックトレースを作る通常の例外を毎回生成する
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            try {
                throw new IllegalArgumentException("Validation error " + i);
            } catch (IllegalArgumentException e) {
                // Handle
            }
        }
        long withoutPooling = System.nanoTime() - start;
        
        // Without pooling: スタックトレースを作らない例外を毎回生成する
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            ValidationException e = ValidationException.create();
            e.setMessage("Validation error " + i);
            try {
                throw e;
            } catch (ValidationException ve) {
                // Handle
            }
        }
        long noStackTrace = System.nanoTime() - start;
        
        // With pooling
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            ValidationException e = pool.acquire("Validation error " + i);
            try {
                throw e;
            } catch (ValidationException ve) {
                // Handle
            } finally {
                pool.release(e);
            }
        }
        long withPooling = System.nanoTime() - start;
        
        IO.println("Without pooling (new exception with stack trace): %d us".formatted(
            TimeUnit.NANOSECONDS.toMicros(withoutPooling)));
        IO.println("Without pooling (new exception, no stack trace): %d us".formatted(
            TimeUnit.NANOSECONDS.toMicros(noStackTrace)));
        IO.println("With pooling (reused exception, no stack trace): %d us".formatted(
            TimeUnit.NANOSECONDS.toMicros(withPooling)));
        IO.println("Pooling vs. stack trace: %.2fx faster".formatted(
            (double) withoutPooling / withPooling));
        IO.println("Note: most of the gain comes from skipping the stack trace, not from reuse.");
        IO.println("Reusing exception objects can mix messages and state between threads,");
        IO.println("so an exception created with writableStackTrace=false is usually enough.");
        
        IO.println("Pool statistics: " + pool.getStatistics());
    }
    
    /**
     * Demonstrate Result pattern as exception alternative.
     */
    private static void demonstrateResultPattern() {
        IO.println("3. Result Pattern Demonstration");
        IO.println("-------------------------------");
        
        // Traditional exception-based approach
        try {
            int result = parseIntWithException("not-a-number");
            IO.println("Parsed: " + result);
        } catch (NumberFormatException e) {
            IO.println("Error: " + e.getMessage());
        }
        
        // Result-based approach
        Result<Integer, String> parseResult = parseIntWithResult("not-a-number");
        
        parseResult
            .map(n -> n * 2)
            .map(n -> "Double value: " + n)
            .ifSuccess(IO::println)
            .ifFailure(error -> IO.println("Parse error: " + error));
        
        // Chaining multiple operations
        Result<Integer, String> calculation = 
            parseIntWithResult("42")
                .flatMap(n -> divideWithResult(100, n))
                .map(n -> n + 10)
                .filter(n -> n > 0, "Result must be positive");
        
        IO.println("Calculation result: " + 
            calculation.fold(
                success -> "Success: " + success,
                error -> "Error: " + error
            ));
    }
    
    /**
     * Demonstrate Null Object pattern.
     */
    private static void demonstrateNullObjectPattern() {
        IO.println("4. Null Object Pattern Demonstration");
        IO.println("-----------------------------------");
        
        NullObject.UserService userService = new NullObject.UserService();
        
        // Valid user
        NullObject.User user1 = userService.findById("user1");
        IO.println("User1 display: " + userService.getUserDisplayName("user1"));
        IO.println("User1 has write permission: " + 
            userService.hasPermission("user1", "write"));
        
        // Invalid user - no null checks or exceptions needed
        NullObject.User unknownUser = userService.findById("unknown");
        IO.println("Unknown user display: " + userService.getUserDisplayName("unknown"));
        IO.println("Unknown user has write permission: " + 
            userService.hasPermission("unknown", "write"));
        IO.println("Unknown user is valid: " + unknownUser.isValid());
    }
    
    /**
     * Demonstrate conditional stack trace generation.
     */
    private static void demonstrateConditionalStackTrace() {
        IO.println("5. Conditional Stack Trace Demonstration");
        IO.println("---------------------------------------");
        
        // Rate-limited exceptions
        for (int i = 0; i < 5; i++) {
            try {
                throw new ConditionalStackTraceException.RateLimitedException(
                    "Rate limited exception #" + i);
            } catch (Exception e) {
                IO.println("Exception " + i + " has stack trace: " + 
                    (e.getStackTrace().length > 0));
            }
        }
        
        // Sampling exceptions
        int withStackTrace = 0;
        for (int i = 0; i < 200; i++) {
            try {
                throw new ConditionalStackTraceException.SamplingException(
                    "Sampled exception");
            } catch (Exception e) {
                if (e.getStackTrace().length > 0) {
                    withStackTrace++;
                }
            }
        }
        IO.println("Sampling: " + withStackTrace + " out of 200 had stack traces");
        
        // Debug mode exceptions
        ConditionalStackTraceException.DebugModeException.setDebugMode(false);
        try {
            throw new ConditionalStackTraceException.DebugModeException("Production error");
        } catch (Exception e) {
            IO.println("Production mode stack trace elements: " + e.getStackTrace().length);
        }
        
        ConditionalStackTraceException.DebugModeException.setDebugMode(true);
        try {
            throw new ConditionalStackTraceException.DebugModeException("Debug error");
        } catch (Exception e) {
            IO.println("Debug mode stack trace elements: " + e.getStackTrace().length);
        }
    }
    
    /**
     * Demonstrate exception monitoring and metrics.
     */
    private static void demonstrateExceptionMonitoring() {
        IO.println("6. Exception Monitoring Demonstration");
        IO.println("------------------------------------");
        
        ExceptionMetrics metrics = new ExceptionMetrics(true);
        ExceptionHandler handler = new ExceptionHandler(metrics, 
            e -> {}, // Suppress error output for demo
            false);  // Don't rethrow
        
        Random random = new Random();
        
        // Simulate various exception scenarios
        for (int i = 0; i < 1000; i++) {
            handler.monitor(() -> {
                switch (random.nextInt(4)) {
                    case 0:
                        throw new IllegalArgumentException("Invalid argument");
                    case 1:
                        throw new IllegalStateException("Invalid state");
                    case 2:
                        if (random.nextBoolean()) {
                            throw new NullPointerException("Null value");
                        }
                        break;
                    default:
                        // Success case
                        break;
                }
            });
        }
        
        // Batch processing with monitoring
        ExceptionHandler.BatchMonitor batchMonitor = handler.createBatchMonitor(
            (index, error) -> {} // Suppress output
        );
        
        batchMonitor.executeBatch(
            () -> { /* Task 1 */ },
            () -> { throw new RuntimeException("Task 2 failed"); },
            () -> { /* Task 3 */ }
        );
        
        // Print metrics report
        IO.println(metrics.generateReport());
    }
    
    // Helper methods
    private static int divide(int a, int b) {
        return b != 0 ? a / b : -1;
    }
    
    private static int divideWithException(int a, int b) {
        if (b == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return a / b;
    }
    
    private static int parseIntWithException(String value) {
        return Integer.parseInt(value);
    }
    
    private static Result<Integer, String> parseIntWithResult(String value) {
        try {
            return Result.success(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return Result.failure("Invalid number: " + value);
        }
    }
    
    private static Result<Integer, String> divideWithResult(int a, int b) {
        if (b == 0) {
            return Result.failure("Division by zero");
        }
        return Result.success(a / b);
    }
}