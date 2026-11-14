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
        private long counter = 0;
        
        public void backgroundWork() {
            // ループの中でメソッドを呼ぶと、JITがフラグの読み込みをループの外へ
            // 出さないことが多く、問題が見えにくくなる。そのため、ループの中は
            // カウンターを増やすだけにしている
            while (!stopRequested) {
                counter++;
            }
            IO.println("Background work stopped. Final counter: " + counter);
        }
        
        public void requestStop() {
            IO.println("Stop requested!");
            stopRequested = true;
        }
        
        public boolean isStopRequested() {
            return stopRequested;
        }
        
        public long getCounter() {
            return counter;
        }
    }
    
    /**
     * 正しく修正されたクラス
     */
    static class CorrectStopFlag {
        private volatile boolean stopRequested = false; // volatileを追加
        private long counter = 0; // 書き込むのはワーカーのスレッドだけ
        
        public void backgroundWork() {
            while (!stopRequested) {
                counter++;
            }
            IO.println("Background work stopped. Final counter: " + counter);
        }
        
        public void requestStop() {
            IO.println("Stop requested (volatile version)!");
            stopRequested = true;
        }
        
        public boolean isStopRequested() {
            return stopRequested;
        }
        
        public long getCounter() {
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
                // 危険1：確認と代入のあいだに別のスレッドが入り、複数回初期化される（デモで見える）
                // 危険2：同期もvolatileもないため、別のスレッドからは代入の順序が入れ替わって見え、
                //        初期化の終わっていないオブジェクトを参照する可能性がある（再現はまれ）
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
            IO.println("Creating expensive object...");
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
            IO.println("Expensive object created: " + name);
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
        IO.println("=== Visibility Problem Demonstration ===");
        
        // 問題のあるバージョン
        IO.println("\n--- Testing Broken Version (may hang) ---");
        BrokenStopFlag broken = new BrokenStopFlag();
        
        Thread workerThread = new Thread(broken::backgroundWork);
        // 止まらなかった場合にもプログラムを終了できるよう、デーモンスレッドにする
        // （ビジーループはinterrupt()では止まらない）
        workerThread.setDaemon(true);
        IO.println("Background work started...");
        workerThread.start();
        
        try {
            // 2秒待ってから停止要求
            Thread.sleep(2000);
            broken.requestStop();
            
            // 最大5秒待つ
            workerThread.join(5000);
            
            if (workerThread.isAlive()) {
                IO.println("Worker thread did not stop - visibility problem occurred!");
                IO.println("(e.g. the JIT compiler may hoist the read of stopRequested out of the loop)");
            } else {
                IO.println("Worker thread stopped properly");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 正しいバージョン
        IO.println("\n--- Testing Correct Version (with volatile) ---");
        CorrectStopFlag correct = new CorrectStopFlag();
        
        Thread correctWorkerThread = new Thread(correct::backgroundWork);
        correctWorkerThread.setDaemon(true);
        IO.println("Background work started (with volatile)...");
        correctWorkerThread.start();
        
        try {
            // 2秒待ってから停止要求
            Thread.sleep(2000);
            correct.requestStop();
            
            // 正常に停止するはず
            correctWorkerThread.join(1000);
            
            if (correctWorkerThread.isAlive()) {
                IO.println("Unexpected: Correct version did not stop");
                correctWorkerThread.interrupt();
            } else {
                IO.println("Correct version stopped properly");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * 初期化問題のデモンストレーション
     */
    public static void demonstrateInitializationProblem() {
        IO.println("\n=== Initialization Problem Demonstration ===");
        
        // 安全でない初期化
        IO.println("\n--- Unsafe Initialization ---");
        UnsafeInitialization unsafe = new UnsafeInitialization();
        
        // 複数スレッドが同時にアクセス
        IO.println("(check-then-act race: the object may be created more than once)");
        runAndJoin(5, unsafe::getInstance);
        
        // 安全な初期化
        IO.println("\n--- Safe Initialization ---");
        SafeInitialization safe = new SafeInitialization();
        
        // 複数スレッドが同時にアクセス
        runAndJoin(5, safe::getInstance);
    }
    
    /**
     * 複数のスレッドで同時にインスタンスを取得し、すべてのスレッドの終了を待つ
     */
    private static void runAndJoin(int threadCount, java.util.function.Supplier<ExpensiveObject> getter) {
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                ExpensiveObject obj = getter.get();
                IO.println("Thread " + threadId + " got: " + obj.getName());
            });
            threads[i].start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
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
        IO.println("\n=== Volatile vs Synchronized Demonstration ===");
        
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
        
        IO.println("Volatile counter:");
        IO.println("  Expected: " + expectedCount);
        IO.println("  Actual: " + actualVolatileCount);
        IO.println("  Lost updates: " + (expectedCount - actualVolatileCount));
        IO.println("  Time: " + TimeUnit.NANOSECONDS.toMillis(volatileTime) + "ms");
        
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
        
        IO.println("\nSynchronized counter:");
        IO.println("  Expected: " + expectedCount);
        IO.println("  Actual: " + actualSyncCount);
        IO.println("  Lost updates: " + (expectedCount - actualSyncCount));
        IO.println("  Time: " + TimeUnit.NANOSECONDS.toMillis(syncTime) + "ms");
        
        IO.println("\nConclusion:");
        IO.println("  Volatile provides visibility but not atomicity");
        IO.println("  Synchronized provides both visibility and atomicity");
        IO.println("  Performance cost: " + (syncTime / (double) volatileTime) + "x slower");
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        demonstrateVisibilityProblem();
        demonstrateInitializationProblem();
        demonstrateVolatileVsSynchronized();
        
        IO.println("\n=== Summary ===");
        IO.println("1. volatile ensures visibility of changes across threads");
        IO.println("2. volatile does NOT ensure atomicity of compound operations");
        IO.println("3. synchronized ensures both visibility and atomicity");
        IO.println("4. Proper initialization requires careful synchronization");
        IO.println("5. Memory model understanding is crucial for concurrent programming");
    }
}