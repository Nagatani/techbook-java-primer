package com.example.memorymodel;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * False Sharingの問題とパフォーマンス最適化のデモンストレーション
 * CPUキャッシュラインの影響とその対策を学習
 */
public class FalseSharingDemo {
    
    /**
     * False Sharingが発生する問題のあるクラス
     */
    static class FalseSharingCounters {
        // これらの変数は連続したメモリ領域に配置される可能性が高い
        // 同じキャッシュライン（通常64バイト）に収まってしまう
        volatile long counter1 = 0;
        volatile long counter2 = 0;
        volatile long counter3 = 0;
        volatile long counter4 = 0;
        volatile long counter5 = 0;
        volatile long counter6 = 0;
        volatile long counter7 = 0;
        volatile long counter8 = 0;
    }
    
    /**
     * パディングによってFalse Sharingを回避するクラス
     */
    static class PaddedCounters {
        // CPUキャッシュライン（通常64バイト）を考慮したパディング
        volatile long p1, p2, p3, p4, p5, p6, p7;     // 前パディング（56バイト）
        volatile long counter1 = 0;                    // 実際のカウンター（8バイト）
        volatile long q1, q2, q3, q4, q5, q6, q7;     // 後パディング（56バイト）
        
        volatile long r1, r2, r3, r4, r5, r6, r7;     // 前パディング
        volatile long counter2 = 0;
        volatile long s1, s2, s3, s4, s5, s6, s7;     // 後パディング
        
        volatile long t1, t2, t3, t4, t5, t6, t7;     // 前パディング
        volatile long counter3 = 0;
        volatile long u1, u2, u3, u4, u5, u6, u7;     // 後パディング
        
        volatile long v1, v2, v3, v4, v5, v6, v7;     // 前パディング
        volatile long counter4 = 0;
        volatile long w1, w2, w3, w4, w5, w6, w7;     // 後パディング
    }
    
    /**
     * @Contendedアノテーションを使用したクラス（Java 8以降）
     * JVMが自動的にパディングを追加する
     */
    static class ContendedCounters {
        @jdk.internal.vm.annotation.Contended
        volatile long counter1 = 0;
        
        @jdk.internal.vm.annotation.Contended
        volatile long counter2 = 0;
        
        @jdk.internal.vm.annotation.Contended
        volatile long counter3 = 0;
        
        @jdk.internal.vm.annotation.Contended
        volatile long counter4 = 0;
    }
    
    /**
     * 配列を使ったFalse Sharingのデモ
     */
    static class ArrayFalseSharingDemo {
        private final long[] counters = new long[8];
        
        public void incrementCounter(int index) {
            counters[index]++;
        }
        
        public long getCounter(int index) {
            return counters[index];
        }
        
        public void printCounters() {
            System.out.print("Counters: [");
            for (int i = 0; i < counters.length; i++) {
                System.out.print(counters[i]);
                if (i < counters.length - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("]");
        }
    }
    
    /**
     * パディングされた配列要素
     */
    static class PaddedLong {
        volatile long p1, p2, p3, p4, p5, p6, p7;     // パディング
        volatile long value = 0;                       // 実際の値
        volatile long q1, q2, q3, q4, q5, q6, q7;     // パディング
    }
    
    /**
     * False Sharingを回避した配列クラス
     */
    static class PaddedArrayDemo {
        private final PaddedLong[] counters = new PaddedLong[8];
        
        public PaddedArrayDemo() {
            for (int i = 0; i < counters.length; i++) {
                counters[i] = new PaddedLong();
            }
        }
        
        public void incrementCounter(int index) {
            counters[index].value++;
        }
        
        public long getCounter(int index) {
            return counters[index].value;
        }
        
        public void printCounters() {
            System.out.print("Padded Counters: [");
            for (int i = 0; i < counters.length; i++) {
                System.out.print(counters[i].value);
                if (i < counters.length - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("]");
        }
    }
    
    /**
     * スケーラブルなカウンター実装（LongAdderの簡易版）
     */
    static class StripedCounter {
        private static final int STRIPE_COUNT = 
            Runtime.getRuntime().availableProcessors() * 4;
        
        @jdk.internal.vm.annotation.Contended
        static class Cell {
            volatile long value;
            
            Cell(long value) {
                this.value = value;
            }
        }
        
        private final Cell[] cells = new Cell[STRIPE_COUNT];
        
        public StripedCounter() {
            for (int i = 0; i < cells.length; i++) {
                cells[i] = new Cell(0);
            }
        }
        
        public void increment() {
            int index = getStripeIndex();
            cells[index].value++;
        }
        
        public void add(long delta) {
            int index = getStripeIndex();
            cells[index].value += delta;
        }
        
        public long sum() {
            long sum = 0;
            for (Cell cell : cells) {
                sum += cell.value;
            }
            return sum;
        }
        
        private int getStripeIndex() {
            // スレッドIDベースの分散（簡易版）
            return (int) (Thread.currentThread().getId() % STRIPE_COUNT);
        }
        
        public void reset() {
            for (Cell cell : cells) {
                cell.value = 0;
            }
        }
        
        public int getStripeCount() {
            return STRIPE_COUNT;
        }
    }
    
    /**
     * False Sharingのパフォーマンステスト
     */
    public static void testFalseSharingPerformance() {
        System.out.println("=== False Sharing Performance Test ===");
        
        final int threadCount = 4;
        final int iterations = 10_000_000;
        
        // False Sharingが発生するケース
        System.out.println("\n--- Test 1: False Sharing (Bad) ---");
        FalseSharingCounters falseSharingCounters = new FalseSharingCounters();
        
        long startTime = System.nanoTime();
        Thread[] threads1 = new Thread[threadCount];
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            threads1[i] = new Thread(() -> {
                for (int j = 0; j < iterations; j++) {
                    switch (threadId) {
                        case 0: falseSharingCounters.counter1++; break;
                        case 1: falseSharingCounters.counter2++; break;
                        case 2: falseSharingCounters.counter3++; break;
                        case 3: falseSharingCounters.counter4++; break;
                    }
                }
            });
        }
        
        for (Thread thread : threads1) {
            thread.start();
        }
        
        for (Thread thread : threads1) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        
        long falseSharingTime = System.nanoTime() - startTime;
        
        System.out.println("Results: " + 
                         falseSharingCounters.counter1 + ", " +
                         falseSharingCounters.counter2 + ", " +
                         falseSharingCounters.counter3 + ", " +
                         falseSharingCounters.counter4);
        System.out.println("Time: " + TimeUnit.NANOSECONDS.toMillis(falseSharingTime) + "ms");
        
        // パディングでFalse Sharingを回避するケース
        System.out.println("\n--- Test 2: Padded (Good) ---");
        PaddedCounters paddedCounters = new PaddedCounters();
        
        startTime = System.nanoTime();
        Thread[] threads2 = new Thread[threadCount];
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            threads2[i] = new Thread(() -> {
                for (int j = 0; j < iterations; j++) {
                    switch (threadId) {
                        case 0: paddedCounters.counter1++; break;
                        case 1: paddedCounters.counter2++; break;
                        case 2: paddedCounters.counter3++; break;
                        case 3: paddedCounters.counter4++; break;
                    }
                }
            });
        }
        
        for (Thread thread : threads2) {
            thread.start();
        }
        
        for (Thread thread : threads2) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        
        long paddedTime = System.nanoTime() - startTime;
        
        System.out.println("Results: " + 
                         paddedCounters.counter1 + ", " +
                         paddedCounters.counter2 + ", " +
                         paddedCounters.counter3 + ", " +
                         paddedCounters.counter4);
        System.out.println("Time: " + TimeUnit.NANOSECONDS.toMillis(paddedTime) + "ms");
        
        double improvement = (double) falseSharingTime / paddedTime;
        System.out.println("\nPerformance improvement: " + String.format("%.2fx", improvement));
    }
    
    /**
     * 配列でのFalse Sharingテスト
     */
    public static void testArrayFalseSharing() {
        System.out.println("\n=== Array False Sharing Test ===");
        
        final int threadCount = 4;
        final int iterations = 5_000_000;
        
        // 通常の配列（False Sharingあり）
        System.out.println("\n--- Normal Array (False Sharing) ---");
        ArrayFalseSharingDemo normalArray = new ArrayFalseSharingDemo();
        
        long startTime = System.nanoTime();
        Thread[] threads1 = new Thread[threadCount];
        
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads1[i] = new Thread(() -> {
                for (int j = 0; j < iterations; j++) {
                    normalArray.incrementCounter(index);
                }
            });
        }
        
        for (Thread thread : threads1) {
            thread.start();
        }
        
        for (Thread thread : threads1) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        
        long normalTime = System.nanoTime() - startTime;
        normalArray.printCounters();
        System.out.println("Time: " + TimeUnit.NANOSECONDS.toMillis(normalTime) + "ms");
        
        // パディングされた配列（False Sharingなし）
        System.out.println("\n--- Padded Array (No False Sharing) ---");
        PaddedArrayDemo paddedArray = new PaddedArrayDemo();
        
        startTime = System.nanoTime();
        Thread[] threads2 = new Thread[threadCount];
        
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads2[i] = new Thread(() -> {
                for (int j = 0; j < iterations; j++) {
                    paddedArray.incrementCounter(index);
                }
            });
        }
        
        for (Thread thread : threads2) {
            thread.start();
        }
        
        for (Thread thread : threads2) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        
        long paddedTime = System.nanoTime() - startTime;
        paddedArray.printCounters();
        System.out.println("Time: " + TimeUnit.NANOSECONDS.toMillis(paddedTime) + "ms");
        
        double improvement = (double) normalTime / paddedTime;
        System.out.println("\nArray performance improvement: " + String.format("%.2fx", improvement));
    }
    
    /**
     * スケーラブルカウンターのテスト
     */
    public static void testStripedCounter() {
        System.out.println("\n=== Striped Counter Test ===");
        
        StripedCounter counter = new StripedCounter();
        final int threadCount = 8;
        final int incrementsPerThread = 1_000_000;
        
        System.out.println("Stripe count: " + counter.getStripeCount());
        System.out.println("Thread count: " + threadCount);
        System.out.println("Increments per thread: " + incrementsPerThread);
        
        long startTime = System.nanoTime();
        Thread[] threads = new Thread[threadCount];
        
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    counter.increment();
                }
            });
        }
        
        for (Thread thread : threads) {
            thread.start();
        }
        
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        
        long elapsedTime = System.nanoTime() - startTime;
        long expectedSum = (long) threadCount * incrementsPerThread;
        long actualSum = counter.sum();
        
        System.out.println("Expected sum: " + expectedSum);
        System.out.println("Actual sum: " + actualSum);
        System.out.println("Time: " + TimeUnit.NANOSECONDS.toMillis(elapsedTime) + "ms");
        System.out.println("Operations per second: " + 
                         String.format("%.0f", expectedSum / (elapsedTime / 1_000_000_000.0)));
    }
    
    /**
     * キャッシュライン情報の表示
     */
    public static void displayCacheLineInfo() {
        System.out.println("\n=== Cache Line Information ===");
        
        // Javaオブジェクトのメモリレイアウト情報
        System.out.println("Typical cache line size: 64 bytes");
        System.out.println("long size: 8 bytes");
        System.out.println("Object header size: ~12-16 bytes (platform dependent)");
        System.out.println("Array header size: ~12-16 bytes + 4 bytes (length)");
        
        // メモリアクセスパターンの説明
        System.out.println("\nMemory access patterns:");
        System.out.println("- Sequential access: Good cache performance");
        System.out.println("- Random access: Poor cache performance");
        System.out.println("- False sharing: Multiple threads writing to same cache line");
        System.out.println("- Solution: Padding to separate frequently accessed variables");
        
        // パフォーマンスの考慮事項
        System.out.println("\nPerformance considerations:");
        System.out.println("- Cache miss penalty: 100-300 cycles");
        System.out.println("- Cache hit: 1-4 cycles");
        System.out.println("- False sharing can reduce performance by 10-100x");
        System.out.println("- Padding increases memory usage but improves performance");
    }
    
    /**
     * メモリアクセスパターンのデモ
     */
    public static void demonstrateAccessPatterns() {
        System.out.println("\n=== Memory Access Pattern Demo ===");
        
        final int arraySize = 1024 * 1024; // 1M elements
        final int iterations = 10;
        int[] array = new int[arraySize];
        
        // 初期化
        for (int i = 0; i < arraySize; i++) {
            array[i] = i;
        }
        
        // 順次アクセス
        long startTime = System.nanoTime();
        for (int iter = 0; iter < iterations; iter++) {
            long sum = 0;
            for (int i = 0; i < arraySize; i++) {
                sum += array[i];
            }
        }
        long sequentialTime = System.nanoTime() - startTime;
        
        // ランダムアクセス
        int[] randomIndices = new int[arraySize];
        for (int i = 0; i < arraySize; i++) {
            randomIndices[i] = ThreadLocalRandom.current().nextInt(arraySize);
        }
        
        startTime = System.nanoTime();
        for (int iter = 0; iter < iterations; iter++) {
            long sum = 0;
            for (int i = 0; i < arraySize; i++) {
                sum += array[randomIndices[i]];
            }
        }
        long randomTime = System.nanoTime() - startTime;
        
        System.out.println("Array size: " + arraySize + " elements");
        System.out.println("Iterations: " + iterations);
        System.out.println("Sequential access time: " + TimeUnit.NANOSECONDS.toMillis(sequentialTime) + "ms");
        System.out.println("Random access time: " + TimeUnit.NANOSECONDS.toMillis(randomTime) + "ms");
        System.out.println("Random/Sequential ratio: " + String.format("%.2fx", (double) randomTime / sequentialTime));
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        displayCacheLineInfo();
        demonstrateAccessPatterns();
        testFalseSharingPerformance();
        testArrayFalseSharing();
        testStripedCounter();
        
        System.out.println("\n=== False Sharing Summary ===");
        System.out.println("1. False sharing occurs when multiple threads access different variables in the same cache line");
        System.out.println("2. It causes unnecessary cache coherency traffic and performance degradation");
        System.out.println("3. Solutions include padding, @Contended annotation, or data structure redesign");
        System.out.println("4. Trade-off between memory usage and performance");
        System.out.println("5. Modern concurrent libraries like LongAdder use these techniques internally");
        System.out.println("6. Understanding cache behavior is crucial for high-performance concurrent programming");
    }
}