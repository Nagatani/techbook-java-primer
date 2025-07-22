import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;
import java.util.logging.Logger;

/**
 * スレッドセーフなカウンター実装
 * 複数の同期メカニズムを比較・学習
 */
public class ThreadSafeCounter {
    
    private static final Logger logger = Logger.getLogger(ThreadSafeCounter.class.getName());
    
    /**
     * synchronized キーワードを使用したカウンター
     */
    public static class SynchronizedCounter {
        private long count = 0;
        private final Object lock = new Object();
        
        public synchronized void increment() {
            count++;
        }
        
        public synchronized void decrement() {
            count--;
        }
        
        public synchronized long get() {
            return count;
        }
        
        public void incrementWithExplicitLock() {
            synchronized (lock) {
                count++;
            }
        }
        
        public synchronized void add(long value) {
            count += value;
        }
        
        public synchronized boolean compareAndSet(long expected, long update) {
            if (count == expected) {
                count = update;
                return true;
            }
            return false;
        }
        
        public synchronized void reset() {
            count = 0;
        }
        
        @Override
        public synchronized String toString() {
            return "SynchronizedCounter[count=" + count + "]";
        }
    }
    
    /**
     * AtomicInteger を使用したカウンター
     */
    public static class AtomicCounter {
        private final AtomicLong count = new AtomicLong(0);
        
        public void increment() {
            count.incrementAndGet();
        }
        
        public void decrement() {
            count.decrementAndGet();
        }
        
        public long get() {
            return count.get();
        }
        
        public void add(long value) {
            count.addAndGet(value);
        }
        
        public boolean compareAndSet(long expected, long update) {
            return count.compareAndSet(expected, update);
        }
        
        public long getAndIncrement() {
            return count.getAndIncrement();
        }
        
        public long incrementAndGet() {
            return count.incrementAndGet();
        }
        
        public void reset() {
            count.set(0);
        }
        
        @Override
        public String toString() {
            return "AtomicCounter[count=" + count.get() + "]";
        }
    }
    
    /**
     * ReentrantReadWriteLock を使用したカウンター
     */
    public static class ReadWriteLockCounter {
        private long count = 0;
        private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        
        public void increment() {
            writeLock.lock();
            try {
                count++;
            } finally {
                writeLock.unlock();
            }
        }
        
        public void decrement() {
            writeLock.lock();
            try {
                count--;
            } finally {
                writeLock.unlock();
            }
        }
        
        public long get() {
            readLock.lock();
            try {
                return count;
            } finally {
                readLock.unlock();
            }
        }
        
        public void add(long value) {
            writeLock.lock();
            try {
                count += value;
            } finally {
                writeLock.unlock();
            }
        }
        
        public boolean compareAndSet(long expected, long update) {
            writeLock.lock();
            try {
                if (count == expected) {
                    count = update;
                    return true;
                }
                return false;
            } finally {
                writeLock.unlock();
            }
        }
        
        public void reset() {
            writeLock.lock();
            try {
                count = 0;
            } finally {
                writeLock.unlock();
            }
        }
        
        public boolean isWriteLocked() {
            return lock.isWriteLocked();
        }
        
        public int getReadLockCount() {
            return lock.getReadLockCount();
        }
        
        @Override
        public String toString() {
            readLock.lock();
            try {
                return "ReadWriteLockCounter[count=" + count + "]";
            } finally {
                readLock.unlock();
            }
        }
    }
    
    /**
     * StampedLock を使用したカウンター（Java 8+）
     */
    public static class StampedLockCounter {
        private long count = 0;
        private final StampedLock lock = new StampedLock();
        
        public void increment() {
            long stamp = lock.writeLock();
            try {
                count++;
            } finally {
                lock.unlockWrite(stamp);
            }
        }
        
        public void decrement() {
            long stamp = lock.writeLock();
            try {
                count--;
            } finally {
                lock.unlockWrite(stamp);
            }
        }
        
        public long get() {
            long stamp = lock.tryOptimisticRead();
            long currentCount = count;
            if (!lock.validate(stamp)) {
                stamp = lock.readLock();
                try {
                    currentCount = count;
                } finally {
                    lock.unlockRead(stamp);
                }
            }
            return currentCount;
        }
        
        public void add(long value) {
            long stamp = lock.writeLock();
            try {
                count += value;
            } finally {
                lock.unlockWrite(stamp);
            }
        }
        
        public boolean compareAndSet(long expected, long update) {
            long stamp = lock.writeLock();
            try {
                if (count == expected) {
                    count = update;
                    return true;
                }
                return false;
            } finally {
                lock.unlockWrite(stamp);
            }
        }
        
        public void reset() {
            long stamp = lock.writeLock();
            try {
                count = 0;
            } finally {
                lock.unlockWrite(stamp);
            }
        }
        
        @Override
        public String toString() {
            long stamp = lock.tryOptimisticRead();
            long currentCount = count;
            if (!lock.validate(stamp)) {
                stamp = lock.readLock();
                try {
                    currentCount = count;
                } finally {
                    lock.unlockRead(stamp);
                }
            }
            return "StampedLockCounter[count=" + currentCount + "]";
        }
    }
    
    /**
     * パフォーマンス比較用のベンチマーククラス
     */
    public static class CounterBenchmark {
        
        public interface Counter {
            void increment();
            void decrement();
            long get();
            void reset();
        }
        
        // 各カウンター実装をCounter インターフェイスでラップ
        public static class SynchronizedCounterWrapper implements Counter {
            private final SynchronizedCounter counter = new SynchronizedCounter();
            public void increment() { counter.increment(); }
            public void decrement() { counter.decrement(); }
            public long get() { return counter.get(); }
            public void reset() { counter.reset(); }
        }
        
        public static class AtomicCounterWrapper implements Counter {
            private final AtomicCounter counter = new AtomicCounter();
            public void increment() { counter.increment(); }
            public void decrement() { counter.decrement(); }
            public long get() { return counter.get(); }
            public void reset() { counter.reset(); }
        }
        
        public static class ReadWriteLockCounterWrapper implements Counter {
            private final ReadWriteLockCounter counter = new ReadWriteLockCounter();
            public void increment() { counter.increment(); }
            public void decrement() { counter.decrement(); }
            public long get() { return counter.get(); }
            public void reset() { counter.reset(); }
        }
        
        public static class StampedLockCounterWrapper implements Counter {
            private final StampedLockCounter counter = new StampedLockCounter();
            public void increment() { counter.increment(); }
            public void decrement() { counter.decrement(); }
            public long get() { return counter.get(); }
            public void reset() { counter.reset(); }
        }
        
        /**
         * カウンターのパフォーマンステスト
         */
        public static BenchmarkResult runBenchmark(Counter counter, int threadCount, 
                                                 int operationsPerThread) {
            counter.reset();
            
            Thread[] threads = new Thread[threadCount];
            long startTime = System.nanoTime();
            
            // スレッドを作成して実行
            for (int i = 0; i < threadCount; i++) {
                threads[i] = new Thread(() -> {
                    for (int j = 0; j < operationsPerThread; j++) {
                        if (j % 2 == 0) {
                            counter.increment();
                        } else {
                            counter.decrement();
                        }
                    }
                });
                threads[i].start();
            }
            
            // すべてのスレッドの完了を待機
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("ベンチマーク中断", e);
                }
            }
            
            long endTime = System.nanoTime();
            long duration = endTime - startTime;
            long finalCount = counter.get();
            
            return new BenchmarkResult(
                counter.getClass().getSimpleName(),
                threadCount,
                operationsPerThread,
                duration,
                finalCount
            );
        }
        
        /**
         * ベンチマーク結果クラス
         */
        public static class BenchmarkResult {
            private final String counterType;
            private final int threadCount;
            private final int operationsPerThread;
            private final long durationNanos;
            private final long finalCount;
            
            public BenchmarkResult(String counterType, int threadCount, 
                                 int operationsPerThread, long durationNanos, long finalCount) {
                this.counterType = counterType;
                this.threadCount = threadCount;
                this.operationsPerThread = operationsPerThread;
                this.durationNanos = durationNanos;
                this.finalCount = finalCount;
            }
            
            public String getCounterType() { return counterType; }
            public int getThreadCount() { return threadCount; }
            public int getOperationsPerThread() { return operationsPerThread; }
            public long getDurationNanos() { return durationNanos; }
            public double getDurationMillis() { return durationNanos / 1_000_000.0; }
            public long getFinalCount() { return finalCount; }
            public long getTotalOperations() { return (long) threadCount * operationsPerThread; }
            public double getOperationsPerSecond() {
                return getTotalOperations() / (durationNanos / 1_000_000_000.0);
            }
            
            @Override
            public String toString() {
                return String.format("%s: %d threads, %d ops/thread, %.2f ms, %,.0f ops/sec, final count: %d",
                    counterType, threadCount, operationsPerThread, getDurationMillis(),
                    getOperationsPerSecond(), finalCount);
            }
        }
    }
    
    /**
     * すべてのカウンター実装のパフォーマンス比較
     */
    public static void comparePerformance(int threadCount, int operationsPerThread) {
        System.out.println("=== カウンターパフォーマンス比較 ===");
        System.out.println("スレッド数: " + threadCount + ", 操作数/スレッド: " + operationsPerThread);
        System.out.println();
        
        CounterBenchmark.Counter[] counters = {
            new CounterBenchmark.SynchronizedCounterWrapper(),
            new CounterBenchmark.AtomicCounterWrapper(),
            new CounterBenchmark.ReadWriteLockCounterWrapper(),
            new CounterBenchmark.StampedLockCounterWrapper()
        };
        
        for (CounterBenchmark.Counter counter : counters) {
            CounterBenchmark.BenchmarkResult result = 
                CounterBenchmark.runBenchmark(counter, threadCount, operationsPerThread);
            System.out.println(result);
        }
    }
    
    /**
     * デモ用メイン関数
     */
    public static void main(String[] args) {
        // 基本的な動作確認
        System.out.println("=== 基本動作確認 ===");
        
        SynchronizedCounter syncCounter = new SynchronizedCounter();
        AtomicCounter atomicCounter = new AtomicCounter();
        ReadWriteLockCounter rwLockCounter = new ReadWriteLockCounter();
        StampedLockCounter stampedCounter = new StampedLockCounter();
        
        // 各カウンターで基本操作
        for (int i = 0; i < 10; i++) {
            syncCounter.increment();
            atomicCounter.increment();
            rwLockCounter.increment();
            stampedCounter.increment();
        }
        
        System.out.println("Synchronized: " + syncCounter.get());
        System.out.println("Atomic: " + atomicCounter.get());
        System.out.println("ReadWriteLock: " + rwLockCounter.get());
        System.out.println("StampedLock: " + stampedCounter.get());
        System.out.println();
        
        // パフォーマンス比較
        comparePerformance(10, 100000);
        System.out.println();
        comparePerformance(50, 100000);
    }
}