package com.example.theory.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 競合状態（Race Condition）の形式的分析
 * リストAC-28
 */
public class RaceConditionExample {
    private int counter = 0; // 共有状態
    
    /**
     * データ競合が発生する危険なメソッド
     */
    public void unsafeIncrement() {
        // この操作は原子的でない:
        // 1. counter の読み取り
        // 2. 値の増加
        // 3. counter への書き込み
        counter++; // 複数のスレッドで同時実行すると競合状態
    }
    
    /**
     * 同期化による競合状態の解決
     */
    public synchronized void safeIncrement() {
        counter++; // synchronized により原子性を保証
    }
    
    /**
     * Atomicクラスによる解決
     */
    private AtomicInteger atomicCounter = new AtomicInteger(0);
    
    public void atomicIncrement() {
        atomicCounter.incrementAndGet(); // ハードウェアレベルでの原子性
    }
    
    // ゲッターメソッド
    public int getCounter() {
        return counter;
    }
    
    public int getAtomicCounter() {
        return atomicCounter.get();
    }
    
    /**
     * 競合状態のデモンストレーション
     */
    public static void demonstrateRaceCondition() throws InterruptedException {
        final int THREAD_COUNT = 10;
        final int INCREMENT_COUNT = 1000;
        
        RaceConditionExample example = new RaceConditionExample();
        
        // unsafeIncrementのテスト
        Thread[] unsafeThreads = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; i++) {
            unsafeThreads[i] = new Thread(() -> {
                for (int j = 0; j < INCREMENT_COUNT; j++) {
                    example.unsafeIncrement();
                }
            });
            unsafeThreads[i].start();
        }
        
        for (Thread t : unsafeThreads) {
            t.join();
        }
        
        System.out.println("Unsafe counter (expected " + (THREAD_COUNT * INCREMENT_COUNT) + "): " + 
                         example.getCounter());
        
        // atomicIncrementのテスト
        Thread[] atomicThreads = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; i++) {
            atomicThreads[i] = new Thread(() -> {
                for (int j = 0; j < INCREMENT_COUNT; j++) {
                    example.atomicIncrement();
                }
            });
            atomicThreads[i].start();
        }
        
        for (Thread t : atomicThreads) {
            t.join();
        }
        
        System.out.println("Atomic counter (expected " + (THREAD_COUNT * INCREMENT_COUNT) + "): " + 
                         example.getAtomicCounter());
    }
}