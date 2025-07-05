import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.Lock;

/**
 * スレッドセーフなカウンターの実装例
 * 
 * 【学習ポイント】
 * 1. finalフィールドによる不変性の確保
 * 2. スレッドセーフティの実現方法
 * 3. Atomicクラスの使用
 * 4. 同期化（synchronized）の使用
 * 5. ReadWriteLockの活用
 * 
 * 【よくある間違い】
 * - 適切な同期化を行わない
 * - volatileキーワードの誤用
 * - パフォーマンスを考慮しない過度な同期化
 * - 競合状態（race condition）の見落とし
 */

/**
 * Atomicクラスを使用したスレッドセーフカウンター
 * 最もシンプルで効率的な実装
 */
public final class ThreadSafeCounter {
    
    // AtomicIntegerを使用してスレッドセーフな操作を実現
    private final AtomicInteger count = new AtomicInteger(0);
    private final AtomicLong totalIncrements = new AtomicLong(0);
    private final AtomicLong totalDecrements = new AtomicLong(0);
    
    /**
     * カウンターをインクリメント
     * @return インクリメント後の値
     */
    public int increment() {
        totalIncrements.incrementAndGet();
        return count.incrementAndGet();
    }
    
    /**
     * カウンターをデクリメント
     * @return デクリメント後の値
     */
    public int decrement() {
        totalDecrements.incrementAndGet();
        return count.decrementAndGet();
    }
    
    /**
     * 指定した値だけカウンターを増加
     * @param delta 増加値
     * @return 増加後の値
     */
    public int addAndGet(int delta) {
        if (delta > 0) {
            totalIncrements.addAndGet(delta);
        } else if (delta < 0) {
            totalDecrements.addAndGet(-delta);
        }
        return count.addAndGet(delta);
    }
    
    /**
     * 現在の値を取得
     * @return 現在の値
     */
    public int get() {
        return count.get();
    }
    
    /**
     * カウンターをリセット
     */
    public void reset() {
        count.set(0);
        totalIncrements.set(0);
        totalDecrements.set(0);
    }
    
    /**
     * 期待値と比較して、一致する場合のみ新しい値を設定
     * @param expect 期待値
     * @param update 新しい値
     * @return 設定に成功した場合true
     */
    public boolean compareAndSet(int expect, int update) {
        return count.compareAndSet(expect, update);
    }
    
    /**
     * 総インクリメント回数を取得
     */
    public long getTotalIncrements() {
        return totalIncrements.get();
    }
    
    /**
     * 総デクリメント回数を取得
     */
    public long getTotalDecrements() {
        return totalDecrements.get();
    }
    
    /**
     * 統計情報を取得
     */
    public CounterStats getStats() {
        return new CounterStats(
            count.get(),
            totalIncrements.get(),
            totalDecrements.get()
        );
    }
    
    @Override
    public String toString() {
        return String.format("ThreadSafeCounter{count=%d, increments=%d, decrements=%d}", 
                           count.get(), totalIncrements.get(), totalDecrements.get());
    }
    
    /**
     * カウンターの統計情報を表すイミュータブルクラス
     */
    public static final class CounterStats {
        private final int currentValue;
        private final long totalIncrements;
        private final long totalDecrements;
        
        public CounterStats(int currentValue, long totalIncrements, long totalDecrements) {
            this.currentValue = currentValue;
            this.totalIncrements = totalIncrements;
            this.totalDecrements = totalDecrements;
        }
        
        public int getCurrentValue() { return currentValue; }
        public long getTotalIncrements() { return totalIncrements; }
        public long getTotalDecrements() { return totalDecrements; }
        public long getTotalOperations() { return totalIncrements + totalDecrements; }
        
        @Override
        public String toString() {
            return String.format("CounterStats{value=%d, increments=%d, decrements=%d, total=%d}", 
                               currentValue, totalIncrements, totalDecrements, getTotalOperations());
        }
    }
}

/**
 * synchronized を使用したスレッドセーフカウンター
 * より詳細な制御が可能だが、パフォーマンスは劣る
 */
final class SynchronizedCounter {
    
    private int count = 0;
    private long totalIncrements = 0;
    private long totalDecrements = 0;
    private final Object lock = new Object();  // 専用のロックオブジェクト
    
    /**
     * カウンターをインクリメント
     */
    public synchronized int increment() {
        totalIncrements++;
        return ++count;
    }
    
    /**
     * カウンターをデクリメント
     */
    public synchronized int decrement() {
        totalDecrements++;
        return --count;
    }
    
    /**
     * 指定した値だけカウンターを増加
     */
    public synchronized int addAndGet(int delta) {
        if (delta > 0) {
            totalIncrements += delta;
        } else if (delta < 0) {
            totalDecrements += (-delta);
        }
        count += delta;
        return count;
    }
    
    /**
     * 現在の値を取得
     */
    public synchronized int get() {
        return count;
    }
    
    /**
     * カウンターをリセット
     */
    public synchronized void reset() {
        count = 0;
        totalIncrements = 0;
        totalDecrements = 0;
    }
    
    /**
     * 複数の操作を原子的に実行
     */
    public synchronized void multipleOperations(int increment, int decrement) {
        // 複数の操作を一つの同期ブロックで実行
        for (int i = 0; i < increment; i++) {
            count++;
            totalIncrements++;
        }
        for (int i = 0; i < decrement; i++) {
            count--;
            totalDecrements++;
        }
    }
    
    /**
     * 統計情報を取得（読み取り専用）
     */
    public synchronized ThreadSafeCounter.CounterStats getStats() {
        return new ThreadSafeCounter.CounterStats(count, totalIncrements, totalDecrements);
    }
    
    @Override
    public synchronized String toString() {
        return String.format("SynchronizedCounter{count=%d, increments=%d, decrements=%d}", 
                           count, totalIncrements, totalDecrements);
    }
}

/**
 * ReadWriteLockを使用したスレッドセーフカウンター
 * 読み取り操作が多い場合に有効
 */
final class ReadWriteLockCounter {
    
    private int count = 0;
    private long totalIncrements = 0;
    private long totalDecrements = 0;
    
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();
    
    /**
     * カウンターをインクリメント
     */
    public int increment() {
        writeLock.lock();
        try {
            totalIncrements++;
            return ++count;
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * カウンターをデクリメント
     */
    public int decrement() {
        writeLock.lock();
        try {
            totalDecrements++;
            return --count;
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * 現在の値を取得（読み取り専用）
     */
    public int get() {
        readLock.lock();
        try {
            return count;
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * 統計情報を取得（読み取り専用）
     */
    public ThreadSafeCounter.CounterStats getStats() {
        readLock.lock();
        try {
            return new ThreadSafeCounter.CounterStats(count, totalIncrements, totalDecrements);
        } finally {
            readLock.unlock();
        }
    }
    
    /**
     * カウンターをリセット
     */
    public void reset() {
        writeLock.lock();
        try {
            count = 0;
            totalIncrements = 0;
            totalDecrements = 0;
        } finally {
            writeLock.unlock();
        }
    }
    
    /**
     * 複数の値を読み取り（読み取りロック使用）
     */
    public String getDetailedInfo() {
        readLock.lock();
        try {
            return String.format("Current: %d, Total Operations: %d, Net Change: %d",
                               count, totalIncrements + totalDecrements, totalIncrements - totalDecrements);
        } finally {
            readLock.unlock();
        }
    }
    
    @Override
    public String toString() {
        return getDetailedInfo();
    }
}

/**
 * 使用例とパフォーマンステスト
 */
class CounterDemo {
    
    public static void demonstrateUsage() {
        System.out.println("=== ThreadSafeCounter使用例 ===");
        
        ThreadSafeCounter counter = new ThreadSafeCounter();
        
        // 基本操作
        System.out.println("初期値: " + counter.get());
        System.out.println("インクリメント: " + counter.increment());
        System.out.println("インクリメント: " + counter.increment());
        System.out.println("デクリメント: " + counter.decrement());
        System.out.println("5を加算: " + counter.addAndGet(5));
        System.out.println("現在値: " + counter.get());
        System.out.println("統計: " + counter.getStats());
        
        // Compare and Set
        System.out.println("\n=== Compare and Set ===");
        boolean result1 = counter.compareAndSet(7, 10);
        System.out.println("7を10に変更: " + result1 + ", 現在値: " + counter.get());
        
        boolean result2 = counter.compareAndSet(5, 15);
        System.out.println("5を15に変更: " + result2 + ", 現在値: " + counter.get());
        
        counter.reset();
        System.out.println("リセット後: " + counter.get());
    }
    
    /**
     * マルチスレッドテスト
     */
    public static void multiThreadTest() {
        System.out.println("\n=== マルチスレッドテスト ===");
        
        ThreadSafeCounter counter = new ThreadSafeCounter();
        final int THREAD_COUNT = 10;
        final int OPERATIONS_PER_THREAD = 1000;
        
        Thread[] threads = new Thread[THREAD_COUNT];
        
        // スレッドを作成して開始
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                    if (threadId % 2 == 0) {
                        counter.increment();
                    } else {
                        counter.decrement();
                    }
                }
            });
            threads[i].start();
        }
        
        // すべてのスレッドの完了を待つ
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        System.out.println("最終結果: " + counter.getStats());
        System.out.println("期待値: 0 (インクリメント数とデクリメント数が同じ)");
    }
    
    /**
     * パフォーマンス比較テスト
     */
    public static void performanceTest() {
        System.out.println("\n=== パフォーマンス比較テスト ===");
        
        final int OPERATIONS = 1000000;
        
        // AtomicIntegerを使用したカウンター
        ThreadSafeCounter atomicCounter = new ThreadSafeCounter();
        long startTime = System.nanoTime();
        for (int i = 0; i < OPERATIONS; i++) {
            atomicCounter.increment();
        }
        long atomicTime = System.nanoTime() - startTime;
        
        // synchronizedを使用したカウンター
        SynchronizedCounter syncCounter = new SynchronizedCounter();
        startTime = System.nanoTime();
        for (int i = 0; i < OPERATIONS; i++) {
            syncCounter.increment();
        }
        long syncTime = System.nanoTime() - startTime;
        
        System.out.println("AtomicInteger: " + atomicTime / 1_000_000 + " ms");
        System.out.println("Synchronized: " + syncTime / 1_000_000 + " ms");
        System.out.println("性能比率: " + (double)syncTime / atomicTime + " 倍");
    }
    
    public static void main(String[] args) {
        demonstrateUsage();
        multiThreadTest();
        performanceTest();
    }
}