package com.example.theory.concurrent;

/**
 * デッドロックの理論的分析
 * コフマンの4条件とデッドロック回避策
 */
public class DeadlockExample {
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();
    
    /**
     * デッドロックが発生する可能性のあるコード
     */
    public void method1() {
        synchronized (lock1) {
            IO.println("Thread " + Thread.currentThread().threadId() + " acquired lock1");
            
            try { Thread.sleep(100); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            synchronized (lock2) { // 循環待機の原因
                IO.println("Thread " + Thread.currentThread().threadId() + " acquired lock2");
            }
        }
    }
    
    public void method2() {
        synchronized (lock2) {
            IO.println("Thread " + Thread.currentThread().threadId() + " acquired lock2");
            
            try { Thread.sleep(100); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            synchronized (lock1) { // 循環待機の原因
                IO.println("Thread " + Thread.currentThread().threadId() + " acquired lock1");
            }
        }
    }
    
    /**
     * デッドロック回避策: ロック順序の統一
     */
    public void safeMethod1() {
        synchronized (lock1) { // 常に lock1 を先に取得
            synchronized (lock2) {
                IO.println("Thread " + Thread.currentThread().threadId() + 
                                 " safely acquired both locks (method1)");
            }
        }
    }
    
    public void safeMethod2() {
        synchronized (lock1) { // 常に lock1 を先に取得
            synchronized (lock2) {
                IO.println("Thread " + Thread.currentThread().threadId() + 
                                 " safely acquired both locks (method2)");
            }
        }
    }
    
    /**
     * デッドロックのデモンストレーション
     */
    public static void demonstrateDeadlock() {
        DeadlockExample example = new DeadlockExample();
        
        // デッドロックを起こす可能性のあるスレッド
        // デッドロックしたスレッドが残ってもJVMが終われるように、デーモンスレッドにする
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                example.method1();
            }
        });
        
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                example.method2();
            }
        });
        
        thread1.setDaemon(true);
        thread2.setDaemon(true);
        
        IO.println("=== Demonstrating potential deadlock ===");
        IO.println("Starting threads that may deadlock...");
        thread1.start();
        thread2.start();
        
        // 最大5秒だけ待つ
        // synchronizedのロックを待っているスレッドは、interrupt()では起こせない。
        // デッドロックしたスレッドはロックを持ったまま止まり続ける
        try {
            thread1.join(5000);
            thread2.join(5000);
            
            if (thread1.isAlive() || thread2.isAlive()) {
                IO.println("Threads appear to be deadlocked!");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 安全な実装のデモ
        // 上のスレッドがlock1・lock2を持ったまま止まっている場合があるので、別のインスタンスを使う
        DeadlockExample safeExample = new DeadlockExample();
        Thread safeThread1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                safeExample.safeMethod1();
            }
        });
        
        Thread safeThread2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                safeExample.safeMethod2();
            }
        });
        
        IO.println("\n=== Demonstrating safe implementation ===");
        safeThread1.start();
        safeThread2.start();
        
        try {
            safeThread1.join();
            safeThread2.join();
            IO.println("Safe threads completed successfully!");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}