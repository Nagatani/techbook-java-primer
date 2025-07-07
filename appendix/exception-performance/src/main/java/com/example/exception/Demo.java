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
        System.out.println("=== Exception Performance Optimization Demo ===\n");
        
        demonstrateBasicPerformance();
        System.out.println();
        
        demonstrateExceptionPooling();
        System.out.println();
        
        demonstrateResultPattern();
        System.out.println();
        
        demonstrateNullObjectPattern();
        System.out.println();
        
        demonstrateConditionalStackTrace();
        System.out.println();
        
        demonstrateExceptionMonitoring();
    }
    
    /**
     * Demonstrate basic exception performance comparison.
     */
    private static void demonstrateBasicPerformance() {
        System.out.println("1. Basic Exception Performance Comparison");
        System.out.println("-----------------------------------------");
        
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
        
        System.out.printf("Normal flow: %d ms%n", TimeUnit.NANOSECONDS.toMillis(normalTime));
        System.out.printf("Exception flow: %d ms%n", TimeUnit.NANOSECONDS.toMillis(exceptionTime));
        System.out.printf("Exception overhead: %.2fx slower%n", 
            (double) exceptionTime / normalTime);
    }
    
    /**
     * Demonstrate exception pooling for high-frequency scenarios.
     */
    private static void demonstrateExceptionPooling() {
        System.out.println("2. Exception Pooling Demonstration");
        System.out.println("----------------------------------");
        
        ExceptionPool<ValidationException> pool = new ExceptionPool<>(
            ValidationException::create, 10
        );
        
        final int iterations = 1000;
        
        // Without pooling
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            try {
                throw new ValidationException();
            } catch (ValidationException e) {
                // Handle
            }
        }
        long withoutPooling = System.nanoTime() - start;
        
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
        
        System.out.printf("Without pooling: %d ms%n", TimeUnit.NANOSECONDS.toMillis(withoutPooling));
        System.out.printf("With pooling: %d ms%n", TimeUnit.NANOSECONDS.toMillis(withPooling));
        System.out.printf("Improvement: %.2fx faster%n", 
            (double) withoutPooling / withPooling);
        
        System.out.println("Pool statistics: " + pool.getStatistics());
    }
    
    /**
     * Demonstrate Result pattern as exception alternative.
     */
    private static void demonstrateResultPattern() {
        System.out.println("3. Result Pattern Demonstration");
        System.out.println("-------------------------------");
        
        // Traditional exception-based approach
        try {
            int result = parseIntWithException("not-a-number");
            System.out.println("Parsed: " + result);
        } catch (NumberFormatException e) {
            System.out.println("Error: " + e.getMessage());
        }
        
        // Result-based approach
        Result<Integer, String> parseResult = parseIntWithResult("not-a-number");
        
        parseResult
            .map(n -> n * 2)
            .map(n -> "Double value: " + n)
            .ifSuccess(System.out::println)
            .ifFailure(error -> System.out.println("Parse error: " + error));
        
        // Chaining multiple operations
        Result<Integer, String> calculation = 
            parseIntWithResult("42")
                .flatMap(n -> divideWithResult(100, n))
                .map(n -> n + 10)
                .filter(n -> n > 0, "Result must be positive");
        
        System.out.println("Calculation result: " + 
            calculation.fold(
                success -> "Success: " + success,
                error -> "Error: " + error
            ));
    }
    
    /**
     * Demonstrate Null Object pattern.
     */
    private static void demonstrateNullObjectPattern() {
        System.out.println("4. Null Object Pattern Demonstration");
        System.out.println("-----------------------------------");
        
        NullObject.UserService userService = new NullObject.UserService();
        
        // Valid user
        NullObject.User user1 = userService.findById("user1");
        System.out.println("User1 display: " + userService.getUserDisplayName("user1"));
        System.out.println("User1 has write permission: " + 
            userService.hasPermission("user1", "write"));
        
        // Invalid user - no null checks or exceptions needed
        NullObject.User unknownUser = userService.findById("unknown");
        System.out.println("Unknown user display: " + userService.getUserDisplayName("unknown"));
        System.out.println("Unknown user has write permission: " + 
            userService.hasPermission("unknown", "write"));
        System.out.println("Unknown user is valid: " + unknownUser.isValid());
    }
    
    /**
     * Demonstrate conditional stack trace generation.
     */
    private static void demonstrateConditionalStackTrace() {
        System.out.println("5. Conditional Stack Trace Demonstration");
        System.out.println("---------------------------------------");
        
        // Rate-limited exceptions
        for (int i = 0; i < 5; i++) {
            try {
                throw new ConditionalStackTraceException.RateLimitedException(
                    "Rate limited exception #" + i);
            } catch (Exception e) {
                System.out.println("Exception " + i + " has stack trace: " + 
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
        System.out.println("Sampling: " + withStackTrace + " out of 200 had stack traces");
        
        // Debug mode exceptions
        ConditionalStackTraceException.DebugModeException.setDebugMode(false);
        try {
            throw new ConditionalStackTraceException.DebugModeException("Production error");
        } catch (Exception e) {
            System.out.println("Production mode stack trace elements: " + e.getStackTrace().length);
        }
        
        ConditionalStackTraceException.DebugModeException.setDebugMode(true);
        try {
            throw new ConditionalStackTraceException.DebugModeException("Debug error");
        } catch (Exception e) {
            System.out.println("Debug mode stack trace elements: " + e.getStackTrace().length);
        }
    }
    
    /**
     * Demonstrate exception monitoring and metrics.
     */
    private static void demonstrateExceptionMonitoring() {
        System.out.println("6. Exception Monitoring Demonstration");
        System.out.println("------------------------------------");
        
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
        System.out.println(metrics.generateReport());
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