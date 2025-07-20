package com.example.memorymodel;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Happens-Before関係のデモンストレーション
 * Java Memory ModelのHappens-Before規則を実践的に学習
 */
public class HappensBeforeDemo {
    
    /**
     * プログラム順序規則のデモ
     */
    static class ProgramOrderRule {
        private int a = 0;
        private int b = 0;
        private int result = 0;
        
        public void writer() {
            a = 1;              // 1
            b = 2;              // 2
            result = a + b;     // 3
            // 同一スレッド内: 1 HB 2, 2 HB 3, 1 HB 3
        }
        
        public void reader() {
            int r = result;     // result が設定されていれば
            System.out.println("Result: " + r + ", a: " + a + ", b: " + b);
            // プログラム順序により、aとbも設定済みのはず
        }
    }
    
    /**
     * モニターロック規則のデモ
     */
    static class MonitorLockRule {
        private final Object lock = new Object();
        private int sharedData = 0;
        private boolean dataReady = false;
        
        public void writer() {
            synchronized (lock) {
                sharedData = 42;        // 1
                dataReady = true;       // 2
                System.out.println("Data written: " + sharedData);
            } // unlock HB 次のlock
        }
        
        public void reader() {
            synchronized (lock) {     // 前のunlock HB このlock
                if (dataReady) {      // 3
                    int data = sharedData; // 4
                    System.out.println("Data read: " + data);
                    // 1,2 HB 3,4 が保証される
                }
            }
        }
    }
    
    /**
     * volatile変数規則のデモ
     */
    static class VolatileRule {
        private volatile long timestamp = 0;
        private String message = "";
        private int counter = 0;
        
        public void publish() {
            message = "Hello World";      // 1
            counter = 100;                // 2
            timestamp = System.nanoTime(); // 3: volatile write
            // 1,2 HB 3
            System.out.println("Published: " + message + ", counter: " + counter);
        }
        
        public void consume() {
            long t = timestamp;           // 4: volatile read
            if (t != 0) {
                String m = message;       // 5
                int c = counter;          // 6
                // 3 HB 4, therefore 1,2 HB 5,6
                System.out.println("Consumed: " + m + ", counter: " + c + ", time: " + t);
            }
        }
    }
    
    /**
     * スレッド開始規則のデモ
     */
    static class ThreadStartRule {
        private int data = 0;
        private String message = "";
        
        public void demonstrate() {
            data = 42;                    // 1
            message = "Thread started";   // 2
            
            Thread worker = new Thread(() -> {
                int d = data;             // 4
                String m = message;       // 5
                System.out.println("Worker sees: data=" + d + ", message=" + m);
                // 1,2 HB 3 HB 4,5 が保証される
            });
            
            worker.start();               // 3
            // 1,2 HB 3
        }
    }
    
    /**
     * スレッド終了規則のデモ
     */
    static class ThreadJoinRule {
        static class Worker extends Thread {
            private int result = 0;
            private String status = "";
            
            @Override
            public void run() {
                // 何らかの計算を実行
                for (int i = 0; i < 1000; i++) {
                    result += i;
                }
                status = "Completed";     // 1
                System.out.println("Worker completed with result: " + result);
            }
            
            public int getResult() {
                return result;
            }
            
            public String getStatus() {
                return status;
            }
        }
        
        public void demonstrate() throws InterruptedException {
            Worker worker = new Worker();
            worker.start();
            
            worker.join();                // 2
            
            int r = worker.getResult();   // 3
            String s = worker.getStatus(); // 4
            // 1 HB 2 HB 3,4
            System.out.println("Main thread sees: result=" + r + ", status=" + s);
        }
    }
    
    /**
     * Happens-Beforeの推移性デモ
     */
    static class TransitivityDemo {
        private volatile int control = 0;
        private int data1 = 0;
        private int data2 = 0;
        private int data3 = 0;
        
        public void thread1() {
            data1 = 1;              // A
            data2 = 2;              // B
            control = 1;            // C (volatile write)
            System.out.println("Thread1: set control to 1");
        }
        
        public void thread2() {
            while (control < 1) {   // D (volatile read)
                Thread.yield();
            }
            data3 = data1 + data2;  // E
            control = 2;            // F (volatile write)
            System.out.println("Thread2: computed data3 = " + data3 + ", set control to 2");
        }
        
        public void thread3() {
            while (control < 2) {   // G (volatile read)
                Thread.yield();
            }
            int result = data1 + data2 + data3; // H
            System.out.println("Thread3: final result = " + result);
            // 推移性により A,B HB C HB D HB E HB F HB G HB H
        }
    }
    
    /**
     * 複雑なHappens-Before関係のデモ
     */
    static class ComplexHappensBeforeDemo {
        private volatile boolean phase1Complete = false;
        private volatile boolean phase2Complete = false;
        private final Object phase3Lock = new Object();
        
        private int[] phase1Data = new int[100];
        private int[] phase2Data = new int[100];
        private int[] phase3Data = new int[100];
        
        public void phase1Worker() {
            // Phase 1: データ初期化
            for (int i = 0; i < phase1Data.length; i++) {
                phase1Data[i] = i * i;
            }
            phase1Complete = true; // volatile write
            System.out.println("Phase 1 completed");
        }
        
        public void phase2Worker() {
            // Phase 1の完了を待つ
            while (!phase1Complete) {
                Thread.yield();
            }
            
            // Phase 2: データ変換
            for (int i = 0; i < phase2Data.length; i++) {
                phase2Data[i] = phase1Data[i] * 2; // phase1Data は可視
            }
            phase2Complete = true; // volatile write
            System.out.println("Phase 2 completed");
        }
        
        public void phase3Worker() {
            // Phase 2の完了を待つ
            while (!phase2Complete) {
                Thread.yield();
            }
            
            // Phase 3: 最終計算（ロックを使用）
            synchronized (phase3Lock) {
                for (int i = 0; i < phase3Data.length; i++) {
                    phase3Data[i] = phase1Data[i] + phase2Data[i]; // 両方とも可視
                }
            }
            System.out.println("Phase 3 completed");
            
            // 結果の一部を表示
            System.out.print("Sample results: ");
            for (int i = 0; i < 5; i++) {
                System.out.print(phase3Data[i] + " ");
            }
            System.out.println();
        }
    }
    
    /**
     * CountDownLatchを使ったHappens-Before保証
     */
    static class CountDownLatchDemo {
        private final CountDownLatch startSignal = new CountDownLatch(1);
        private final CountDownLatch doneSignal = new CountDownLatch(3);
        
        private int[] results = new int[3];
        
        public void demonstrate() throws InterruptedException {
            // 3つのワーカースレッドを開始
            for (int i = 0; i < 3; i++) {
                final int workerId = i;
                new Thread(() -> {
                    try {
                        startSignal.await(); // すべてのスレッドが開始シグナルを待つ
                        
                        // 何らかの作業を実行
                        int result = 0;
                        for (int j = 0; j < 1000; j++) {
                            result += j * (workerId + 1);
                        }
                        results[workerId] = result;
                        
                        System.out.println("Worker " + workerId + " completed with result: " + result);
                        doneSignal.countDown(); // 完了を通知
                        
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            }
            
            System.out.println("All workers started, releasing start signal...");
            startSignal.countDown(); // すべてのワーカーを開始
            
            doneSignal.await(); // すべてのワーカーの完了を待つ
            
            // この時点で、すべてのresults[i]への書き込みが可視である
            int totalResult = 0;
            for (int i = 0; i < results.length; i++) {
                totalResult += results[i];
                System.out.println("Result[" + i + "]: " + results[i]);
            }
            System.out.println("Total result: " + totalResult);
        }
    }
    
    /**
     * プログラム順序規則のデモンストレーション
     */
    public static void demonstrateProgramOrder() {
        System.out.println("=== Program Order Rule Demo ===");
        
        ProgramOrderRule demo = new ProgramOrderRule();
        demo.writer();
        demo.reader();
    }
    
    /**
     * モニターロック規則のデモンストレーション
     */
    public static void demonstrateMonitorLock() throws InterruptedException {
        System.out.println("\n=== Monitor Lock Rule Demo ===");
        
        MonitorLockRule demo = new MonitorLockRule();
        
        Thread writer = new Thread(demo::writer);
        Thread reader = new Thread(() -> {
            try {
                Thread.sleep(100); // writerが先に実行されるように少し待つ
                demo.reader();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        writer.start();
        reader.start();
        
        writer.join();
        reader.join();
    }
    
    /**
     * volatile規則のデモンストレーション
     */
    public static void demonstrateVolatileRule() throws InterruptedException {
        System.out.println("\n=== Volatile Rule Demo ===");
        
        VolatileRule demo = new VolatileRule();
        
        Thread publisher = new Thread(demo::publish);
        Thread consumer = new Thread(() -> {
            try {
                Thread.sleep(100); // publisherが先に実行されるように
                demo.consume();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        publisher.start();
        consumer.start();
        
        publisher.join();
        consumer.join();
    }
    
    /**
     * スレッド開始規則のデモンストレーション
     */
    public static void demonstrateThreadStart() throws InterruptedException {
        System.out.println("\n=== Thread Start Rule Demo ===");
        
        ThreadStartRule demo = new ThreadStartRule();
        demo.demonstrate();
        
        Thread.sleep(100); // ワーカースレッドの完了を待つ
    }
    
    /**
     * スレッド終了規則のデモンストレーション
     */
    public static void demonstrateThreadJoin() throws InterruptedException {
        System.out.println("\n=== Thread Join Rule Demo ===");
        
        ThreadJoinRule demo = new ThreadJoinRule();
        demo.demonstrate();
    }
    
    /**
     * 推移性のデモンストレーション
     */
    public static void demonstrateTransitivity() throws InterruptedException {
        System.out.println("\n=== Transitivity Demo ===");
        
        TransitivityDemo demo = new TransitivityDemo();
        
        Thread t1 = new Thread(demo::thread1);
        Thread t2 = new Thread(demo::thread2);
        Thread t3 = new Thread(demo::thread3);
        
        t1.start();
        t2.start();
        t3.start();
        
        t1.join();
        t2.join();
        t3.join();
    }
    
    /**
     * 複雑なHappens-Before関係のデモンストレーション
     */
    public static void demonstrateComplexHappensBefore() throws InterruptedException {
        System.out.println("\n=== Complex Happens-Before Demo ===");
        
        ComplexHappensBeforeDemo demo = new ComplexHappensBeforeDemo();
        
        Thread phase1 = new Thread(demo::phase1Worker);
        Thread phase2 = new Thread(demo::phase2Worker);
        Thread phase3 = new Thread(demo::phase3Worker);
        
        phase1.start();
        phase2.start();
        phase3.start();
        
        phase1.join();
        phase2.join();
        phase3.join();
    }
    
    /**
     * CountDownLatchのデモンストレーション
     */
    public static void demonstrateCountDownLatch() throws InterruptedException {
        System.out.println("\n=== CountDownLatch Demo ===");
        
        CountDownLatchDemo demo = new CountDownLatchDemo();
        demo.demonstrate();
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        try {
            demonstrateProgramOrder();
            demonstrateMonitorLock();
            demonstrateVolatileRule();
            demonstrateThreadStart();
            demonstrateThreadJoin();
            demonstrateTransitivity();
            demonstrateComplexHappensBefore();
            demonstrateCountDownLatch();
            
            System.out.println("\n=== Happens-Before Rules Summary ===");
            System.out.println("1. Program Order: Actions in a thread happen-before later actions in the same thread");
            System.out.println("2. Monitor Lock: Unlock of a monitor happens-before every subsequent lock of that monitor");
            System.out.println("3. Volatile: Write to a volatile field happens-before every subsequent read of that field");
            System.out.println("4. Thread Start: Call to Thread.start() happens-before any action in the started thread");
            System.out.println("5. Thread Join: Any action in a thread happens-before any other thread returns from join()");
            System.out.println("6. Transitivity: If A happens-before B and B happens-before C, then A happens-before C");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Demo interrupted: " + e.getMessage());
        }
    }
}