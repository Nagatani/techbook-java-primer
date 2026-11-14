package com.example.exception.optimization;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Object pool for exception instances to reduce allocation overhead.
 * Useful for high-frequency exception scenarios where stack traces aren't needed.
 *
 * 注意: 例外オブジェクトの使い回しは、スレッド間でメッセージや状態が混ざる危険があり、
 * 通常は勧めない。スタックトレースを作らない例外（writableStackTrace=false）で
 * 足りることが多い。
 */
public class ExceptionPool<T extends PoolableException> {
    private final Queue<T> pool = new ConcurrentLinkedQueue<>();
    private final ExceptionFactory<T> factory;
    private final int maxPoolSize;
    private final AtomicInteger currentSize = new AtomicInteger(0);
    
    // Statistics
    private final AtomicInteger acquireCount = new AtomicInteger(0);
    private final AtomicInteger releaseCount = new AtomicInteger(0);
    private final AtomicInteger createCount = new AtomicInteger(0);

    public ExceptionPool(ExceptionFactory<T> factory, int maxPoolSize) {
        this.factory = factory;
        this.maxPoolSize = maxPoolSize;
    }

    /**
     * Acquire an exception from the pool or create a new one if pool is empty.
     */
    public T acquire(String message) {
        acquireCount.incrementAndGet();
        
        T exception = pool.poll();
        if (exception == null) {
            createCount.incrementAndGet();
            exception = factory.create();
        } else {
            // プールから取り出したので、プール内の数を減らす
            currentSize.decrementAndGet();
        }
        
        exception.setMessage(message);
        exception.setInUse(true);
        return exception;
    }

    /**
     * Release an exception back to the pool for reuse.
     */
    public void release(T exception) {
        if (exception == null || !exception.isInUse()) {
            return;
        }
        
        releaseCount.incrementAndGet();
        exception.reset();
        exception.setInUse(false);
        
        if (currentSize.get() < maxPoolSize) {
            pool.offer(exception);
            currentSize.incrementAndGet();
        }
    }

    /**
     * Get pool statistics for monitoring.
     */
    public PoolStatistics getStatistics() {
        return new PoolStatistics(
            pool.size(),
            acquireCount.get(),
            releaseCount.get(),
            createCount.get()
        );
    }

    /**
     * Clear the pool and reset statistics.
     */
    public void clear() {
        pool.clear();
        currentSize.set(0);
        acquireCount.set(0);
        releaseCount.set(0);
        createCount.set(0);
    }

    /**
     * Factory interface for creating poolable exceptions.
     */
    @FunctionalInterface
    public interface ExceptionFactory<T extends PoolableException> {
        T create();
    }

    /**
     * Statistics for monitoring pool performance.
     */
    public static class PoolStatistics {
        public final int poolSize;
        public final int totalAcquired;
        public final int totalReleased;
        public final int totalCreated;

        public PoolStatistics(int poolSize, int totalAcquired, int totalReleased, int totalCreated) {
            this.poolSize = poolSize;
            this.totalAcquired = totalAcquired;
            this.totalReleased = totalReleased;
            this.totalCreated = totalCreated;
        }

        public double getReuseRatio() {
            if (totalAcquired == 0) return 0.0;
            return 1.0 - ((double) totalCreated / totalAcquired);
        }

        @Override
        public String toString() {
            return String.format(
                "PoolStatistics{poolSize=%d, acquired=%d, released=%d, created=%d, reuseRatio=%.2f%%}",
                poolSize, totalAcquired, totalReleased, totalCreated, getReuseRatio() * 100
            );
        }
    }
}
