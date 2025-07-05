import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ThreadSafeCounterクラスのテストクラス
 */
public class ThreadSafeCounterTest {
    
    private static final int THREAD_COUNT = 10;
    private static final int OPERATIONS_PER_THREAD = 1000;
    
    @Test
    public void testSynchronizedCounterBasicOperations() {
        ThreadSafeCounter.SynchronizedCounter counter = new ThreadSafeCounter.SynchronizedCounter();
        
        assertEquals(0, counter.get());
        
        counter.increment();
        assertEquals(1, counter.get());
        
        counter.decrement();
        assertEquals(0, counter.get());
        
        counter.add(5);
        assertEquals(5, counter.get());
        
        assertTrue(counter.compareAndSet(5, 10));
        assertEquals(10, counter.get());
        
        assertFalse(counter.compareAndSet(5, 15));
        assertEquals(10, counter.get());
        
        counter.reset();
        assertEquals(0, counter.get());
    }
    
    @Test
    public void testAtomicCounterBasicOperations() {
        ThreadSafeCounter.AtomicCounter counter = new ThreadSafeCounter.AtomicCounter();
        
        assertEquals(0, counter.get());
        
        counter.increment();
        assertEquals(1, counter.get());
        
        counter.decrement();
        assertEquals(0, counter.get());
        
        counter.add(5);
        assertEquals(5, counter.get());
        
        assertTrue(counter.compareAndSet(5, 10));
        assertEquals(10, counter.get());
        
        assertFalse(counter.compareAndSet(5, 15));
        assertEquals(10, counter.get());
        
        assertEquals(10, counter.getAndIncrement());
        assertEquals(11, counter.get());
        
        assertEquals(12, counter.incrementAndGet());
        assertEquals(12, counter.get());
        
        counter.reset();
        assertEquals(0, counter.get());
    }
    
    @Test
    public void testReadWriteLockCounterBasicOperations() {
        ThreadSafeCounter.ReadWriteLockCounter counter = new ThreadSafeCounter.ReadWriteLockCounter();
        
        assertEquals(0, counter.get());
        
        counter.increment();
        assertEquals(1, counter.get());
        
        counter.decrement();
        assertEquals(0, counter.get());
        
        counter.add(5);
        assertEquals(5, counter.get());
        
        assertTrue(counter.compareAndSet(5, 10));
        assertEquals(10, counter.get());
        
        assertFalse(counter.compareAndSet(5, 15));
        assertEquals(10, counter.get());
        
        assertFalse(counter.isWriteLocked());
        assertEquals(0, counter.getReadLockCount());
        
        counter.reset();
        assertEquals(0, counter.get());
    }
    
    @Test
    public void testStampedLockCounterBasicOperations() {
        ThreadSafeCounter.StampedLockCounter counter = new ThreadSafeCounter.StampedLockCounter();
        
        assertEquals(0, counter.get());
        
        counter.increment();
        assertEquals(1, counter.get());
        
        counter.decrement();
        assertEquals(0, counter.get());
        
        counter.add(5);
        assertEquals(5, counter.get());
        
        assertTrue(counter.compareAndSet(5, 10));
        assertEquals(10, counter.get());
        
        assertFalse(counter.compareAndSet(5, 15));
        assertEquals(10, counter.get());
        
        counter.reset();
        assertEquals(0, counter.get());
    }
    
    @RepeatedTest(5)
    public void testSynchronizedCounterConcurrency() throws InterruptedException {
        ThreadSafeCounter.SynchronizedCounter counter = new ThreadSafeCounter.SynchronizedCounter();
        testCounterConcurrency(new ThreadSafeCounter.CounterBenchmark.SynchronizedCounterWrapper());
    }
    
    @RepeatedTest(5)
    public void testAtomicCounterConcurrency() throws InterruptedException {
        testCounterConcurrency(new ThreadSafeCounter.CounterBenchmark.AtomicCounterWrapper());
    }
    
    @RepeatedTest(5)
    public void testReadWriteLockCounterConcurrency() throws InterruptedException {
        testCounterConcurrency(new ThreadSafeCounter.CounterBenchmark.ReadWriteLockCounterWrapper());
    }
    
    @RepeatedTest(5)
    public void testStampedLockCounterConcurrency() throws InterruptedException {
        testCounterConcurrency(new ThreadSafeCounter.CounterBenchmark.StampedLockCounterWrapper());
    }
    
    private void testCounterConcurrency(ThreadSafeCounter.CounterBenchmark.Counter counter) throws InterruptedException {
        counter.reset();
        
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        
        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        counter.increment();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertTrue(latch.await(30, TimeUnit.SECONDS));
        
        // すべてのスレッドがincrementを実行したので、期待値はTHREAD_COUNT * OPERATIONS_PER_THREAD
        assertEquals(THREAD_COUNT * OPERATIONS_PER_THREAD, counter.get());
        
        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
    }
    
    @Test
    public void testCounterConcurrencyWithMixedOperations() throws InterruptedException {
        ThreadSafeCounter.AtomicCounter counter = new ThreadSafeCounter.AtomicCounter();
        
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadIndex = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        if (threadIndex % 2 == 0) {
                            counter.increment();
                        } else {
                            counter.decrement();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertTrue(latch.await(30, TimeUnit.SECONDS));
        
        // 半分のスレッドがincrement、半分がdecrementするので、結果は0に近くなるはず
        // （正確には、THREAD_COUNTが偶数の場合は0、奇数の場合はOPERATIONS_PER_THREAD）
        long expectedResult = (THREAD_COUNT % 2 == 0) ? 0 : OPERATIONS_PER_THREAD;
        assertEquals(expectedResult, counter.get());
        
        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
    }
    
    @Test
    public void testBenchmarkExecution() {
        ThreadSafeCounter.CounterBenchmark.Counter counter = 
            new ThreadSafeCounter.CounterBenchmark.AtomicCounterWrapper();
        
        ThreadSafeCounter.CounterBenchmark.BenchmarkResult result = 
            ThreadSafeCounter.CounterBenchmark.runBenchmark(counter, 5, 1000);
        
        assertNotNull(result);
        assertEquals(5, result.getThreadCount());
        assertEquals(1000, result.getOperationsPerThread());
        assertEquals(5000, result.getTotalOperations());
        assertTrue(result.getDurationNanos() > 0);
        assertTrue(result.getDurationMillis() > 0);
        assertTrue(result.getOperationsPerSecond() > 0);
        
        // 混合操作（increment/decrement）の結果、最終カウントは0になるはず
        assertEquals(0, result.getFinalCount());
        
        String resultString = result.toString();
        assertNotNull(resultString);
        assertTrue(resultString.contains("AtomicCounterWrapper"));
        assertTrue(resultString.contains("5 threads"));
        assertTrue(resultString.contains("1000 ops/thread"));
    }
    
    @Test
    public void testToStringMethods() {
        ThreadSafeCounter.SynchronizedCounter syncCounter = new ThreadSafeCounter.SynchronizedCounter();
        ThreadSafeCounter.AtomicCounter atomicCounter = new ThreadSafeCounter.AtomicCounter();
        ThreadSafeCounter.ReadWriteLockCounter rwCounter = new ThreadSafeCounter.ReadWriteLockCounter();
        ThreadSafeCounter.StampedLockCounter stampedCounter = new ThreadSafeCounter.StampedLockCounter();
        
        syncCounter.add(42);
        atomicCounter.add(42);
        rwCounter.add(42);
        stampedCounter.add(42);
        
        assertTrue(syncCounter.toString().contains("SynchronizedCounter[count=42]"));
        assertTrue(atomicCounter.toString().contains("AtomicCounter[count=42]"));
        assertTrue(rwCounter.toString().contains("ReadWriteLockCounter[count=42]"));
        assertTrue(stampedCounter.toString().contains("StampedLockCounter[count=42]"));
    }
    
    @Test
    public void testReadWriteLockMultipleReaders() throws InterruptedException {
        ThreadSafeCounter.ReadWriteLockCounter counter = new ThreadSafeCounter.ReadWriteLockCounter();
        counter.add(100);
        
        int readerCount = 5;
        ExecutorService executor = Executors.newFixedThreadPool(readerCount);
        CountDownLatch latch = new CountDownLatch(readerCount);
        AtomicInteger readValues = new AtomicInteger(0);
        
        for (int i = 0; i < readerCount; i++) {
            executor.submit(() -> {
                try {
                    // 複数の読み取り操作
                    for (int j = 0; j < 100; j++) {
                        long value = counter.get();
                        if (value == 100) {
                            readValues.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        assertTrue(readValues.get() > 0);
        
        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
    }
    
    @Test
    public void testCompareAndSetConcurrency() throws InterruptedException {
        ThreadSafeCounter.AtomicCounter counter = new ThreadSafeCounter.AtomicCounter();
        
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        
        for (int i = 0; i < threadCount; i++) {
            final int expectedValue = i;
            executor.submit(() -> {
                try {
                    boolean success = counter.compareAndSet(expectedValue, expectedValue + 1);
                    if (success) {
                        successCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        
        // compareAndSetは原子的操作なので、成功するのは1つのスレッドのみ
        assertEquals(1, successCount.get());
        assertEquals(1, counter.get());
        
        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
    }
    
    @Test
    public void testStressTest() throws InterruptedException {
        ThreadSafeCounter.AtomicCounter counter = new ThreadSafeCounter.AtomicCounter();
        
        int stressThreadCount = 20;
        int stressOperations = 10000;
        ExecutorService executor = Executors.newFixedThreadPool(stressThreadCount);
        CountDownLatch latch = new CountDownLatch(stressThreadCount);
        
        for (int i = 0; i < stressThreadCount; i++) {
            final int threadIndex = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < stressOperations; j++) {
                        switch (j % 4) {
                            case 0: counter.increment(); break;
                            case 1: counter.decrement(); break;
                            case 2: counter.add(2); break;
                            case 3: counter.add(-2); break;
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertTrue(latch.await(60, TimeUnit.SECONDS));
        
        // increment/decrement と add(2)/add(-2) が同じ回数実行されるので、結果は0になるはず
        assertEquals(0, counter.get());
        
        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));
    }
}