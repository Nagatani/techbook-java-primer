package com.example.theory.concurrent;

/**
 * デッドロックの理論的分析
 * コフマンの4条件とデッドロック回避策
 * リストAC-29
 */
public class DeadlockExample {
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();
    
    /**
     * デッドロックが発生する可能性のあるコード
     */
    public void method1() {
        synchronized (lock1) {
            System.out.println("Thread " + Thread.currentThread().getId() + " acquired lock1");
            
            try { Thread.sleep(100); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            synchronized (lock2) { // 循環待機の原因
                System.out.println("Thread " + Thread.currentThread().getId() + " acquired lock2");
            }
        }
    }
    
    public void method2() {
        synchronized (lock2) {
            System.out.println("Thread " + Thread.currentThread().getId() + " acquired lock2");
            
            try { Thread.sleep(100); } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            synchronized (lock1) { // 循環待機の原因
                System.out.println("Thread " + Thread.currentThread().getId() + " acquired lock1");
            }
        }
    }
    
    /**
     * デッドロック回避策: ロック順序の統一
     */
    public void safeMethod1() {
        synchronized (lock1) { // 常に lock1 を先に取得
            synchronized (lock2) {
                System.out.println("Thread " + Thread.currentThread().getId() + 
                                 " safely acquired both locks (method1)");
            }
        }
    }
    
    public void safeMethod2() {
        synchronized (lock1) { // 常に lock1 を先に取得
            synchronized (lock2) {
                System.out.println("Thread " + Thread.currentThread().getId() + 
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
        
        System.out.println("=== Demonstrating potential deadlock ===");
        System.out.println("Starting threads that may deadlock...");
        thread1.start();
        thread2.start();
        
        // タイムアウトで強制終了
        try {
            thread1.join(5000);
            thread2.join(5000);
            
            if (thread1.isAlive() || thread2.isAlive()) {
                System.out.println("Threads appear to be deadlocked!");
                thread1.interrupt();
                thread2.interrupt();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 安全な実装のデモ
        Thread safeThread1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                example.safeMethod1();
            }
        });
        
        Thread safeThread2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                example.safeMethod2();
            }
        });
        
        System.out.println("\n=== Demonstrating safe implementation ===");
        safeThread1.start();
        safeThread2.start();
        
        try {
            safeThread1.join();
            safeThread2.join();
            System.out.println("Safe threads completed successfully!");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}