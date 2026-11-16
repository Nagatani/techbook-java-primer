import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class ResiliencePatterns {
    
    // リトライパターン
    static class RetryExecutor {
        private final int maxRetries;
        private final long retryDelay;
        
        public RetryExecutor(int maxRetries, long retryDelay) {
            this.maxRetries = maxRetries;
            this.retryDelay = retryDelay;
        }
        
        public <T> CompletableFuture<T> executeWithRetry(Callable<T> task) {
            return executeWithRetry(task, 0);
        }
        
        private <T> CompletableFuture<T> executeWithRetry(Callable<T> task, int attempt) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return task.call();
                } catch (Exception e) {
                    if (attempt < maxRetries) {
                        IO.println("Retry attempt " + (attempt + 1) + 
                            " after " + retryDelay + "ms");
                        try {
                            Thread.sleep(retryDelay);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                        return executeWithRetry(task, attempt + 1).join();
                    }
                    throw new CompletionException(e);
                }
            });
        }
    }
    
    // サーキットブレーカーパターン
    static class CircuitBreaker {
        enum State { CLOSED, OPEN, HALF_OPEN }
        
        private final AtomicReference<State> state = 
            new AtomicReference<>(State.CLOSED);
        private final AtomicInteger failureCount = new AtomicInteger(0);
        private final AtomicLong lastFailureTime = new AtomicLong(0);
        
        private final int failureThreshold;
        private final long timeout;
        
        public CircuitBreaker(int failureThreshold, long timeout) {
            this.failureThreshold = failureThreshold;
            this.timeout = timeout;
        }
        
        public <T> T execute(Callable<T> task) throws Exception {
            State currentState = state.get();
            
            if (currentState == State.OPEN) {
                if (System.currentTimeMillis() - lastFailureTime.get() > timeout) {
                    state.set(State.HALF_OPEN);
                    currentState = State.HALF_OPEN;
                    IO.println("Circuit breaker: HALF_OPEN");
                } else {
                    throw new RuntimeException("Circuit breaker is OPEN");
                }
            }
            
            try {
                T result = task.call();
                if (currentState == State.HALF_OPEN) {
                    state.set(State.CLOSED);
                    IO.println("Circuit breaker: CLOSED");
                }
                failureCount.set(0); // 成功したら、連続した失敗の回数を0に戻す
                return result;
            } catch (Exception e) {
                failureCount.incrementAndGet();
                lastFailureTime.set(System.currentTimeMillis());
                
                if (failureCount.get() >= failureThreshold) {
                    state.set(State.OPEN);
                    IO.println("Circuit breaker: OPEN");
                }
                throw e;
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
        // リトライパターンの例
        RetryExecutor retryExecutor = new RetryExecutor(3, 1000);
        
        CompletableFuture<String> retryFuture = retryExecutor.executeWithRetry(() -> {
            if (Math.random() < 0.7) {
                throw new RuntimeException("Random failure");
            }
            return "Success!";
        });
        
        try {
            IO.println("Retry result: " + retryFuture.get());
        } catch (Exception e) {
            IO.println("Failed after retries: " + e.getMessage());
        }
        
        // サーキットブレーカーの例
        CircuitBreaker circuitBreaker = new CircuitBreaker(3, 5000);
        
        for (int i = 0; i < 10; i++) {
            try {
                String result = circuitBreaker.execute(() -> {
                    if (Math.random() < 0.6) {
                        throw new RuntimeException("Service failure");
                    }
                    return "Service response";
                });
                IO.println("Request " + i + ": " + result);
            } catch (Exception e) {
                IO.println("Request " + i + " failed: " + e.getMessage());
            }
            
            Thread.sleep(500);
        }
    }
}
