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
        // 隣のカウンターとは120バイト離れるので、128バイトのキャッシュラインでもほぼ分かれる。
        // ただし、JVMがフィールドを宣言の順に並べる保証はない（@Contendedなら保証される）
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
     * @Contendedアノテーションを使用したクラス
     * JVMが自動的にパディングを追加する。
     * Java 8ではsun.misc.Contended、Java 9以降はjdk.internal.vm.annotation.Contended。
     * JDKの内部APIなので、コンパイルには --add-exports java.base/jdk.internal.vm.annotation=ALL-UNNAMED、
     * アプリケーションのクラスで効かせるには実行時に -XX:-RestrictContended が必要
     * （指定しないとアノテーションは無視され、パディングされない）
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
        // 普通のlong[]だと、JITが要素をレジスターに置いたままループし、メモリに書かないことがあり、
        // False Sharingが起きない。パディング版（volatileのフィールド）と条件をそろえるため、
        // 要素をvolatileとして読み書きできるAtomicLongArrayを使う
        private final java.util.concurrent.atomic.AtomicLongArray counters =
            new java.util.concurrent.atomic.AtomicLongArray(8);
        
        public void incrementCounter(int index) {
            // パディング版のvalue++と同じく、volatileの読み込みと書き込み（原子的ではない）
            counters.set(index, counters.get(index) + 1);
        }
        
        public long getCounter(int index) {
            return counters.get(index);
        }
        
        public void printCounters() {
            IO.print("Counters: [");
            for (int i = 0; i < counters.length(); i++) {
                IO.print(counters.get(i));
                if (i < counters.length() - 1) {
                    IO.print(", ");
                }
            }
            IO.println("]");
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
            IO.print("Padded Counters: [");
            for (int i = 0; i < counters.length; i++) {
                IO.print(counters[i].value);
                if (i < counters.length - 1) {
                    IO.print(", ");
                }
            }
            IO.println("]");
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
            return (int) (Thread.currentThread().threadId() % STRIPE_COUNT);
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
        IO.println("=== False Sharing Performance Test ===");
        
        final int threadCount = 4;
        final int iterations = 10_000_000;
        
        // False Sharingが発生するケース
        IO.println("\n--- Test 1: False Sharing (Bad) ---");
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
        
        IO.println("Results: " + 
                         falseSharingCounters.counter1 + ", " +
                         falseSharingCounters.counter2 + ", " +
                         falseSharingCounters.counter3 + ", " +
                         falseSharingCounters.counter4);
        IO.println("Time: " + TimeUnit.NANOSECONDS.toMillis(falseSharingTime) + "ms");
        
        // パディングでFalse Sharingを回避するケース
        IO.println("\n--- Test 2: Padded (Good) ---");
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
        
        IO.println("Results: " + 
                         paddedCounters.counter1 + ", " +
                         paddedCounters.counter2 + ", " +
                         paddedCounters.counter3 + ", " +
                         paddedCounters.counter4);
        IO.println("Time: " + TimeUnit.NANOSECONDS.toMillis(paddedTime) + "ms");
        
        double improvement = (double) falseSharingTime / paddedTime;
        IO.println("\nPerformance improvement: " + String.format("%.2fx", improvement));
        
        // @Contendedを使うケース（-XX:-RestrictContended を付けたときだけパディングされる）
        IO.println("\n--- Test 3: @Contended ---");
        ContendedCounters contendedCounters = new ContendedCounters();
        long contendedTime = runCounterThreads(threadCount, iterations, threadId -> {
            switch (threadId) {
                case 0 -> contendedCounters.counter1++;
                case 1 -> contendedCounters.counter2++;
                case 2 -> contendedCounters.counter3++;
                default -> contendedCounters.counter4++;
            }
        });
        IO.println("Results: " + 
                         contendedCounters.counter1 + ", " +
                         contendedCounters.counter2 + ", " +
                         contendedCounters.counter3 + ", " +
                         contendedCounters.counter4);
        IO.println("Time: " + TimeUnit.NANOSECONDS.toMillis(contendedTime) + "ms");
        IO.println("(Without -XX:-RestrictContended the annotation is ignored,");
        IO.println(" so this behaves like Test 1.)");
    }
    
    /**
     * スレッドごとに、threadIdを渡して increment を iterations 回呼び、経過時間（ナノ秒）を返す
     */
    private static long runCounterThreads(int threadCount, int iterations,
                                          java.util.function.IntConsumer increment) {
        long startTime = System.nanoTime();
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < iterations; j++) {
                    increment.accept(threadId);
                }
            });
            threads[i].start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        return System.nanoTime() - startTime;
    }
    
    /**
     * 配列でのFalse Sharingテスト
     */
    public static void testArrayFalseSharing() {
        IO.println("\n=== Array False Sharing Test ===");
        
        final int threadCount = 4;
        final int iterations = 5_000_000;
        
        // 通常の配列（False Sharingあり）
        IO.println("\n--- Normal Array (False Sharing) ---");
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
        IO.println("Time: " + TimeUnit.NANOSECONDS.toMillis(normalTime) + "ms");
        
        // パディングされた配列（False Sharingなし）
        IO.println("\n--- Padded Array (No False Sharing) ---");
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
        IO.println("Time: " + TimeUnit.NANOSECONDS.toMillis(paddedTime) + "ms");
        
        double improvement = (double) normalTime / paddedTime;
        IO.println("\nArray performance improvement: " + String.format("%.2fx", improvement));
    }
    
    /**
     * スケーラブルカウンターのテスト
     */
    public static void testStripedCounter() {
        IO.println("\n=== Striped Counter Test ===");
        
        StripedCounter counter = new StripedCounter();
        final int threadCount = 8;
        final int incrementsPerThread = 1_000_000;
        
        IO.println("Stripe count: " + counter.getStripeCount());
        IO.println("Thread count: " + threadCount);
        IO.println("Increments per thread: " + incrementsPerThread);
        
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
        
        IO.println("Expected sum: " + expectedSum);
        IO.println("Actual sum: " + actualSum);
        IO.println("Time: " + TimeUnit.NANOSECONDS.toMillis(elapsedTime) + "ms");
        IO.println("Operations per second: " + 
                         String.format("%.0f", expectedSum / (elapsedTime / 1_000_000_000.0)));
    }
    
    /**
     * キャッシュライン情報の表示
     */
    public static void displayCacheLineInfo() {
        IO.println("\n=== Cache Line Information ===");
        
        // Javaオブジェクトのメモリレイアウト情報
        IO.println("Typical cache line size: 64 bytes (128 bytes on Apple Silicon)");
        IO.println("long size: 8 bytes");
        IO.println("Object header size: ~12-16 bytes (platform dependent)");
        IO.println("Array header size: ~12-16 bytes + 4 bytes (length)");
        
        // メモリアクセスパターンの説明
        IO.println("\nMemory access patterns:");
        IO.println("- Sequential access: Good cache performance");
        IO.println("- Random access: Poor cache performance");
        IO.println("- False sharing: Multiple threads writing to same cache line");
        IO.println("- Solution: Padding to separate frequently accessed variables");
        
        // パフォーマンスの考慮事項
        IO.println("\nPerformance considerations:");
        IO.println("- A miss to main memory costs far more cycles than a hit in the L1 cache");
        IO.println("- False sharing can make updates several times slower (depends on the CPU)");
        IO.println("- Padding increases memory usage but improves performance");
    }
    
    /**
     * メモリアクセスパターンのデモ
     */
    public static void demonstrateAccessPatterns() {
        IO.println("\n=== Memory Access Pattern Demo ===");
        
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
        
        IO.println("Array size: " + arraySize + " elements");
        IO.println("Iterations: " + iterations);
        IO.println("Sequential access time: " + TimeUnit.NANOSECONDS.toMillis(sequentialTime) + "ms");
        IO.println("Random access time: " + TimeUnit.NANOSECONDS.toMillis(randomTime) + "ms");
        IO.println("Random/Sequential ratio: " + String.format("%.2fx", (double) randomTime / sequentialTime));
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
        
        IO.println("\n=== False Sharing Summary ===");
        IO.println("1. False sharing occurs when multiple threads access different variables in the same cache line");
        IO.println("2. It causes unnecessary cache coherency traffic and performance degradation");
        IO.println("3. Solutions include padding, @Contended annotation, or data structure redesign");
        IO.println("4. Trade-off between memory usage and performance");
        IO.println("5. Modern concurrent libraries like LongAdder use these techniques internally");
        IO.println("6. Understanding cache behavior is crucial for high-performance concurrent programming");
    }
}