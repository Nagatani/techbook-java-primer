package com.example.memorymodel;

import java.util.concurrent.TimeUnit;

/**
 * メモリ可視性問題のデモンストレーション
 * volatile キーワードの重要性と happens-before 関係を学習
 */
public class VisibilityProblems {
    
    /**
     * 可視性問題が発生する可能性があるクラス
     */
    static class BrokenStopFlag {
        private boolean stopRequested = false; // volatileがない
        private int counter = 0;
        
        public void backgroundWork() {
            System.out.println("Background work started...");
            while (!stopRequested) {
                counter++;
                // 軽い処理をシミュレート
                if (counter % 100_000_000 == 0) {
                    System.out.println("Working... counter: " + counter);
                }
            }
            System.out.println("Background work stopped. Final counter: " + counter);
        }
        
        public void requestStop() {
            System.out.println("Stop requested!");
            stopRequested = true;
        }
        
        public boolean isStopRequested() {
            return stopRequested;
        }
        
        public int getCounter() {
            return counter;
        }
    }
    
    /**
     * 正しく修正されたクラス
     */
    static class CorrectStopFlag {
        private volatile boolean stopRequested = false; // volatileを追加
        private volatile int counter = 0; // counterもvolatileに
        
        public void backgroundWork() {
            System.out.println("Background work started (with volatile)...");
            while (!stopRequested) {
                counter++;
                // 軽い処理をシミュレート
                if (counter % 100_000_000 == 0) {
                    System.out.println("Working... counter: " + counter);
                }
            }
            System.out.println("Background work stopped. Final counter: " + counter);
        }
        
        public void requestStop() {
            System.out.println("Stop requested (volatile version)!");
            stopRequested = true;
        }
        
        public boolean isStopRequested() {
            return stopRequested;
        }
        
        public int getCounter() {
            return counter;
        }
    }
    
    /**
     * 初期化の見切り発車問題
     */
    static class UnsafeInitialization {
        private ExpensiveObject instance;
        
        public ExpensiveObject getInstance() {
            if (instance == null) {
                // 危険：複数スレッドが同時に初期化する可能性
                instance = new ExpensiveObject();
            }
            return instance;
        }
    }
    
    /**
     * 安全な初期化
     */
    static class SafeInitialization {
        private volatile ExpensiveObject instance;
        
        public ExpensiveObject getInstance() {
            ExpensiveObject result = instance;
            if (result == null) {
                synchronized (this) {
                    result = instance;
                    if (result == null) {
                        instance = result = new ExpensiveObject();
                    }
                }
            }
            return result;
        }
    }
    
    /**
     * 高コストなオブジェクトのシミュレーション
     */
    static class ExpensiveObject {
        private final int[] data;
        private final String name;
        
        public ExpensiveObject() {
            System.out.println("Creating expensive object...");
            // 重い初期化をシミュレート
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            this.data = new int[1000];
            for (int i = 0; i < data.length; i++) {
                data[i] = i * i;
            }
            this.name = "ExpensiveObject-" + System.nanoTime();
            System.out.println("Expensive object created: " + name);
        }
        
        public int[] getData() {
            return data.clone();
        }
        
        public String getName() {
            return name;
        }
    }
    
    /**
     * 可視性問題のデモンストレーション
     */
    public static void demonstrateVisibilityProblem() {
        System.out.println("=== Visibility Problem Demonstration ===");
        
        // 問題のあるバージョン
        System.out.println("\n--- Testing Broken Version (may hang) ---");
        BrokenStopFlag broken = new BrokenStopFlag();
        
        Thread workerThread = new Thread(broken::backgroundWork);
        workerThread.start();
        
        try {
            // 2秒待ってから停止要求
            Thread.sleep(2000);
            broken.requestStop();
            
            // 最大5秒待つ
            workerThread.join(5000);
            
            if (workerThread.isAlive()) {
                System.out.println("Worker thread did not stop - visibility problem occurred!");
                workerThread.interrupt();
            } else {
                System.out.println("Worker thread stopped properly");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 正しいバージョン
        System.out.println("\n--- Testing Correct Version (with volatile) ---");
        CorrectStopFlag correct = new CorrectStopFlag();
        
        Thread correctWorkerThread = new Thread(correct::backgroundWork);
        correctWorkerThread.start();
        
        try {
            // 2秒待ってから停止要求
            Thread.sleep(2000);
            correct.requestStop();
            
            // 正常に停止するはず
            correctWorkerThread.join(1000);
            
            if (correctWorkerThread.isAlive()) {
                System.out.println("Unexpected: Correct version did not stop");
                correctWorkerThread.interrupt();
            } else {
                System.out.println("Correct version stopped properly");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 初期化問題のデモンストレーション
     */
    public static void demonstrateInitializationProblem() {
        System.out.println("\n=== Initialization Problem Demonstration ===");
        
        // 安全でない初期化
        System.out.println("\n--- Unsafe Initialization ---");
        UnsafeInitialization unsafe = new UnsafeInitialization();
        
        // 複数スレッドが同時にアクセス
        for (int i = 0; i < 5; i++) {
            final int threadId = i;
            new Thread(() -> {
                ExpensiveObject obj = unsafe.getInstance();
                System.out.println("Thread " + threadId + " got: " + obj.getName());
            }).start();
        }
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 安全な初期化
        System.out.println("\n--- Safe Initialization ---");
        SafeInitialization safe = new SafeInitialization();
        
        // 複数スレッドが同時にアクセス
        for (int i = 0; i < 5; i++) {
            final int threadId = i;
            new Thread(() -> {
                ExpensiveObject obj = safe.getInstance();
                System.out.println("Thread " + threadId + " got: " + obj.getName());
            }).start();
        }
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * volatileとsynchronizedの違いを説明
     */
    static class VolatileVsSynchronized {
        private volatile int volatileCounter = 0;
        private int synchronizedCounter = 0;
        
        // volatileは原子性を保証しない
        public void incrementVolatile() {
            volatileCounter++; // この操作は原子的でない（read-modify-write）
        }
        
        // synchronizedは原子性を保証
        public synchronized void incrementSynchronized() {
            synchronizedCounter++;
        }
        
        public int getVolatileCounter() {
            return volatileCounter;
        }
        
        public synchronized int getSynchronizedCounter() {
            return synchronizedCounter;
        }
        
        public void reset() {
            volatileCounter = 0;
            synchronizedCounter = 0;
        }
    }
    
    /**
     * volatileとsynchronizedの違いをデモ
     */
    public static void demonstrateVolatileVsSynchronized() {
        System.out.println("\n=== Volatile vs Synchronized Demonstration ===");
        
        VolatileVsSynchronized demo = new VolatileVsSynchronized();
        final int threadCount = 10;
        final int incrementsPerThread = 1000;
        
        // volatileカウンターのテスト
        Thread[] volatileThreads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            volatileThreads[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    demo.incrementVolatile();
                }
            });
        }
        
        long startTime = System.nanoTime();
        for (Thread thread : volatileThreads) {
            thread.start();
        }
        
        for (Thread thread : volatileThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        long volatileTime = System.nanoTime() - startTime;
        
        int expectedCount = threadCount * incrementsPerThread;
        int actualVolatileCount = demo.getVolatileCounter();
        
        System.out.println("Volatile counter:");
        System.out.println("  Expected: " + expectedCount);
        System.out.println("  Actual: " + actualVolatileCount);
        System.out.println("  Lost updates: " + (expectedCount - actualVolatileCount));
        System.out.println("  Time: " + TimeUnit.NANOSECONDS.toMillis(volatileTime) + "ms");
        
        // カウンターをリセット
        demo.reset();
        
        // synchronizedカウンターのテスト
        Thread[] syncThreads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            syncThreads[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    demo.incrementSynchronized();
                }
            });
        }
        
        startTime = System.nanoTime();
        for (Thread thread : syncThreads) {
            thread.start();
        }
        
        for (Thread thread : syncThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        long syncTime = System.nanoTime() - startTime;
        
        int actualSyncCount = demo.getSynchronizedCounter();
        
        System.out.println("\nSynchronized counter:");
        System.out.println("  Expected: " + expectedCount);
        System.out.println("  Actual: " + actualSyncCount);
        System.out.println("  Lost updates: " + (expectedCount - actualSyncCount));
        System.out.println("  Time: " + TimeUnit.NANOSECONDS.toMillis(syncTime) + "ms");
        
        System.out.println("\nConclusion:");
        System.out.println("  Volatile provides visibility but not atomicity");
        System.out.println("  Synchronized provides both visibility and atomicity");
        System.out.println("  Performance cost: " + (syncTime / (double) volatileTime) + "x slower");
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        demonstrateVisibilityProblem();
        demonstrateInitializationProblem();
        demonstrateVolatileVsSynchronized();
        
        System.out.println("\n=== Summary ===");
        System.out.println("1. volatile ensures visibility of changes across threads");
        System.out.println("2. volatile does NOT ensure atomicity of compound operations");
        System.out.println("3. synchronized ensures both visibility and atomicity");
        System.out.println("4. Proper initialization requires careful synchronization");
        System.out.println("5. Memory model understanding is crucial for concurrent programming");
    }
}