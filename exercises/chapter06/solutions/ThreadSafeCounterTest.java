import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ThreadSafeCounterクラスのテストクラス
 * 
 * 【テストの観点】
 * 1. 基本的な操作の正確性
 * 2. スレッドセーフティ
 * 3. 境界値・異常値の処理
 * 4. 統計情報の正確性
 * 5. パフォーマンス特性
 */
public class ThreadSafeCounterTest {
    
    private ThreadSafeCounter counter;
    
    @BeforeEach
    void setUp() {
        counter = new ThreadSafeCounter();
    }
    
    @Nested
    @DisplayName("基本操作のテスト")
    class BasicOperationTest {
        
        @Test
        @DisplayName("初期値は0")
        void testInitialValue() {
            assertEquals(0, counter.get());
            assertEquals(0, counter.getTotalIncrements());
            assertEquals(0, counter.getTotalDecrements());
        }
        
        @Test
        @DisplayName("インクリメント操作")
        void testIncrement() {
            assertEquals(1, counter.increment());
            assertEquals(1, counter.get());
            assertEquals(1, counter.getTotalIncrements());
            assertEquals(0, counter.getTotalDecrements());
            
            assertEquals(2, counter.increment());
            assertEquals(2, counter.get());
            assertEquals(2, counter.getTotalIncrements());
        }
        
        @Test
        @DisplayName("デクリメント操作")
        void testDecrement() {
            assertEquals(-1, counter.decrement());
            assertEquals(-1, counter.get());
            assertEquals(0, counter.getTotalIncrements());
            assertEquals(1, counter.getTotalDecrements());
            
            assertEquals(-2, counter.decrement());
            assertEquals(-2, counter.get());
            assertEquals(2, counter.getTotalDecrements());
        }
        
        @Test
        @DisplayName("addAndGet操作")
        void testAddAndGet() {
            assertEquals(5, counter.addAndGet(5));
            assertEquals(5, counter.get());
            assertEquals(5, counter.getTotalIncrements());
            assertEquals(0, counter.getTotalDecrements());
            
            assertEquals(2, counter.addAndGet(-3));
            assertEquals(2, counter.get());
            assertEquals(5, counter.getTotalIncrements());
            assertEquals(3, counter.getTotalDecrements());
            
            assertEquals(2, counter.addAndGet(0));
            assertEquals(2, counter.get());
            assertEquals(5, counter.getTotalIncrements());
            assertEquals(3, counter.getTotalDecrements());
        }
        
        @Test
        @DisplayName("リセット操作")
        void testReset() {
            counter.increment();
            counter.increment();
            counter.decrement();
            
            assertEquals(1, counter.get());
            assertEquals(2, counter.getTotalIncrements());
            assertEquals(1, counter.getTotalDecrements());
            
            counter.reset();
            
            assertEquals(0, counter.get());
            assertEquals(0, counter.getTotalIncrements());
            assertEquals(0, counter.getTotalDecrements());
        }
        
        @Test
        @DisplayName("compareAndSet操作")
        void testCompareAndSet() {
            // 期待値が一致する場合
            assertTrue(counter.compareAndSet(0, 10));
            assertEquals(10, counter.get());
            
            // 期待値が一致しない場合
            assertFalse(counter.compareAndSet(5, 15));
            assertEquals(10, counter.get());
            
            // 期待値が一致する場合
            assertTrue(counter.compareAndSet(10, 20));
            assertEquals(20, counter.get());
        }
    }
    
    @Nested
    @DisplayName("統計情報のテスト")
    class StatisticsTest {
        
        @Test
        @DisplayName("統計情報の取得")
        void testGetStats() {
            ThreadSafeCounter.CounterStats stats = counter.getStats();
            assertEquals(0, stats.getCurrentValue());
            assertEquals(0, stats.getTotalIncrements());
            assertEquals(0, stats.getTotalDecrements());
            assertEquals(0, stats.getTotalOperations());
            
            counter.increment();
            counter.increment();
            counter.decrement();
            
            stats = counter.getStats();
            assertEquals(1, stats.getCurrentValue());
            assertEquals(2, stats.getTotalIncrements());
            assertEquals(1, stats.getTotalDecrements());
            assertEquals(3, stats.getTotalOperations());
        }
        
        @Test
        @DisplayName("CounterStatsのtoString")
        void testCounterStatsToString() {
            counter.increment();
            counter.decrement();
            
            ThreadSafeCounter.CounterStats stats = counter.getStats();
            String str = stats.toString();
            
            assertTrue(str.contains("value=0"));
            assertTrue(str.contains("increments=1"));
            assertTrue(str.contains("decrements=1"));
            assertTrue(str.contains("total=2"));
        }
    }
    
    @Nested
    @DisplayName("境界値・異常値のテスト")
    class BoundaryValueTest {
        
        @Test
        @DisplayName("大きな値の加算")
        void testLargeValue() {
            int largeValue = Integer.MAX_VALUE - 1;
            counter.addAndGet(largeValue);
            assertEquals(largeValue, counter.get());
            
            // オーバーフローのテスト
            counter.addAndGet(2);
            assertEquals(Integer.MIN_VALUE + 1, counter.get());
        }
        
        @Test
        @DisplayName("大きな負の値の加算")
        void testLargeNegativeValue() {
            int largeNegativeValue = Integer.MIN_VALUE + 1;
            counter.addAndGet(largeNegativeValue);
            assertEquals(largeNegativeValue, counter.get());
            
            // アンダーフローのテスト
            counter.addAndGet(-2);
            assertEquals(Integer.MAX_VALUE - 1, counter.get());
        }
        
        @Test
        @DisplayName("統計情報のオーバーフロー")
        void testStatisticsOverflow() {
            // 大量のインクリメント（統計情報のオーバーフローは起こりにくいがテスト）
            for (int i = 0; i < 1000; i++) {
                counter.increment();
            }
            
            assertEquals(1000, counter.get());
            assertEquals(1000, counter.getTotalIncrements());
            assertEquals(0, counter.getTotalDecrements());
        }
    }
    
    @Nested
    @DisplayName("スレッドセーフティのテスト")
    class ThreadSafetyTest {
        
        @Test
        @DisplayName("複数スレッドでのインクリメント")
        void testConcurrentIncrement() throws InterruptedException {
            final int THREAD_COUNT = 10;
            final int OPERATIONS_PER_THREAD = 100;
            final int EXPECTED_TOTAL = THREAD_COUNT * OPERATIONS_PER_THREAD;
            
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
            
            assertTrue(latch.await(10, TimeUnit.SECONDS));
            executor.shutdown();
            
            assertEquals(EXPECTED_TOTAL, counter.get());
            assertEquals(EXPECTED_TOTAL, counter.getTotalIncrements());
            assertEquals(0, counter.getTotalDecrements());
        }
        
        @Test
        @DisplayName("複数スレッドでのデクリメント")
        void testConcurrentDecrement() throws InterruptedException {
            final int THREAD_COUNT = 10;
            final int OPERATIONS_PER_THREAD = 100;
            final int EXPECTED_TOTAL = -(THREAD_COUNT * OPERATIONS_PER_THREAD);
            
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
            CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
            
            for (int i = 0; i < THREAD_COUNT; i++) {
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                            counter.decrement();
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }
            
            assertTrue(latch.await(10, TimeUnit.SECONDS));
            executor.shutdown();
            
            assertEquals(EXPECTED_TOTAL, counter.get());
            assertEquals(0, counter.getTotalIncrements());
            assertEquals(THREAD_COUNT * OPERATIONS_PER_THREAD, counter.getTotalDecrements());
        }
        
        @Test
        @DisplayName("複数スレッドでのインクリメント・デクリメント混在")
        void testConcurrentMixedOperations() throws InterruptedException {
            final int THREAD_COUNT = 20;
            final int OPERATIONS_PER_THREAD = 100;
            
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
            CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
            
            // 半分のスレッドでインクリメント、半分でデクリメント
            for (int i = 0; i < THREAD_COUNT; i++) {
                final boolean isIncrement = (i % 2 == 0);
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                            if (isIncrement) {
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
            
            assertTrue(latch.await(10, TimeUnit.SECONDS));
            executor.shutdown();
            
            // インクリメントとデクリメントの数が同じなので、最終値は0
            assertEquals(0, counter.get());
            assertEquals(THREAD_COUNT / 2 * OPERATIONS_PER_THREAD, counter.getTotalIncrements());
            assertEquals(THREAD_COUNT / 2 * OPERATIONS_PER_THREAD, counter.getTotalDecrements());
        }
        
        @Test
        @DisplayName("複数スレッドでのaddAndGet")
        void testConcurrentAddAndGet() throws InterruptedException {
            final int THREAD_COUNT = 10;
            final int OPERATIONS_PER_THREAD = 100;
            
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
            CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
            
            for (int i = 0; i < THREAD_COUNT; i++) {
                final int threadId = i;
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                            if (threadId % 2 == 0) {
                                counter.addAndGet(1);
                            } else {
                                counter.addAndGet(-1);
                            }
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }
            
            assertTrue(latch.await(10, TimeUnit.SECONDS));
            executor.shutdown();
            
            assertEquals(0, counter.get());
        }
        
        @Test
        @DisplayName("複数スレッドでのcompareAndSet")
        void testConcurrentCompareAndSet() throws InterruptedException {
            final int THREAD_COUNT = 10;
            
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
            CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
            AtomicInteger successCount = new AtomicInteger(0);
            
            for (int i = 0; i < THREAD_COUNT; i++) {
                final int threadId = i;
                executor.submit(() -> {
                    try {
                        // 0から各スレッドIDに変更を試みる
                        if (counter.compareAndSet(0, threadId)) {
                            successCount.incrementAndGet();
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }
            
            assertTrue(latch.await(10, TimeUnit.SECONDS));
            executor.shutdown();
            
            // 1つのスレッドだけが成功するはず
            assertEquals(1, successCount.get());
            // 最終値は成功したスレッドのIDのいずれか
            int finalValue = counter.get();
            assertTrue(finalValue >= 0 && finalValue < THREAD_COUNT);
        }
    }
    
    @RepeatedTest(5)
    @DisplayName("ランダムな操作での整合性テスト")
    void testRandomOperationsConsistency() throws InterruptedException {
        final int THREAD_COUNT = 5;
        final int OPERATIONS_PER_THREAD = 200;
        
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        
        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        int operation = j % 4;
                        switch (operation) {
                            case 0 -> counter.increment();
                            case 1 -> counter.decrement();
                            case 2 -> counter.addAndGet(5);
                            case 3 -> counter.addAndGet(-5);
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        executor.shutdown();
        
        // 各スレッドで同じ操作を実行するので、最終値は0になるはず
        assertEquals(0, counter.get());
        
        // 統計情報の整合性確認
        ThreadSafeCounter.CounterStats stats = counter.getStats();
        assertEquals(0, stats.getCurrentValue());
        
        // 各操作の回数を確認
        long expectedIncrements = THREAD_COUNT * OPERATIONS_PER_THREAD / 4 * 2;  // increment + addAndGet(5)
        long expectedDecrements = THREAD_COUNT * OPERATIONS_PER_THREAD / 4 * 2;  // decrement + addAndGet(-5)
        
        assertEquals(expectedIncrements, stats.getTotalIncrements());
        assertEquals(expectedDecrements, stats.getTotalDecrements());
    }
    
    @Test
    @DisplayName("読み取り専用操作の並行性")
    void testConcurrentReadOperations() throws InterruptedException {
        // 初期値を設定
        counter.addAndGet(100);
        
        final int THREAD_COUNT = 20;
        final int READS_PER_THREAD = 1000;
        
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger errorCount = new AtomicInteger(0);
        
        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < READS_PER_THREAD; j++) {
                        int value = counter.get();
                        ThreadSafeCounter.CounterStats stats = counter.getStats();
                        
                        // 読み取り値に一貫性があることを確認
                        if (value != stats.getCurrentValue()) {
                            errorCount.incrementAndGet();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        executor.shutdown();
        
        // 読み取り操作では不整合が発生しないはず
        assertEquals(0, errorCount.get());
        assertEquals(100, counter.get());
    }
    
    @Test
    @DisplayName("toString メソッドのテスト")
    void testToString() {
        counter.increment();
        counter.decrement();
        counter.addAndGet(5);
        
        String str = counter.toString();
        assertTrue(str.contains("ThreadSafeCounter"));
        assertTrue(str.contains("count=5"));
        assertTrue(str.contains("increments=6"));
        assertTrue(str.contains("decrements=1"));
    }
    
    @Test
    @DisplayName("統計情報の独立性")
    void testStatsIndependence() {
        ThreadSafeCounter.CounterStats stats1 = counter.getStats();
        counter.increment();
        ThreadSafeCounter.CounterStats stats2 = counter.getStats();
        
        // 最初の統計情報は変更されない
        assertEquals(0, stats1.getCurrentValue());
        assertEquals(0, stats1.getTotalIncrements());
        
        // 新しい統計情報には変更が反映される
        assertEquals(1, stats2.getCurrentValue());
        assertEquals(1, stats2.getTotalIncrements());
    }
}