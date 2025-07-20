package com.example.theory.concurrent;

/**
 * Java Memory Model (JMM) とメモリ順序保証
 * リストAC-30
 */
public class MemoryModelExample {
    private boolean ready = false;
    private int value = 0;
    
    /**
     * Writer スレッド
     */
    public void writer() {
        value = 42;      // 1
        ready = true;    // 2
        // リオーダリングにより 2 が 1 より先に実行される可能性
    }
    
    /**
     * Reader スレッド
     */
    public void reader() {
        while (!ready) { // 3
            Thread.yield();
        }
        System.out.println(value); // 4: 0 が出力される可能性
    }
    
    /**
     * volatile による順序保証
     */
    private volatile boolean readyVolatile = false;
    private int valueVolatile = 0;
    
    public void writerVolatile() {
        valueVolatile = 42;      // 1
        readyVolatile = true;    // 2: volatile write
        // happens-before 関係により 1 は 2 より先に実行
    }
    
    public void readerVolatile() {
        while (!readyVolatile) { // 3: volatile read
            Thread.yield();
        }
        System.out.println(valueVolatile); // 4: 必ず 42 が出力
        // happens-before 関係により 2 は 3 より先、3 は 4 より先
    }
    
    /**
     * synchronized による順序保証
     */
    private boolean readySync = false;
    private int valueSync = 0;
    private final Object lock = new Object();
    
    public void writerSync() {
        synchronized (lock) {
            valueSync = 42;
            readySync = true;
        }
    }
    
    public void readerSync() {
        boolean isReady = false;
        while (!isReady) {
            synchronized (lock) {
                isReady = readySync;
            }
            if (!isReady) {
                Thread.yield();
            }
        }
        synchronized (lock) {
            System.out.println(valueSync); // 必ず 42 が出力
        }
    }
    
    /**
     * メモリモデルのデモンストレーション
     */
    public static void demonstrateMemoryModel() throws InterruptedException {
        // 問題のある実装のデモ
        System.out.println("=== Demonstrating potential memory visibility issue ===");
        MemoryModelExample example1 = new MemoryModelExample();
        
        Thread writer1 = new Thread(example1::writer);
        Thread reader1 = new Thread(example1::reader);
        
        reader1.start();
        Thread.sleep(100); // readerが先に開始されるように
        writer1.start();
        
        writer1.join(1000);
        reader1.join(1000);
        
        if (reader1.isAlive()) {
            System.out.println("Reader thread may be stuck due to visibility issue!");
            reader1.interrupt();
        }
        
        // volatile を使った安全な実装のデモ
        System.out.println("\n=== Demonstrating volatile solution ===");
        MemoryModelExample example2 = new MemoryModelExample();
        
        Thread writer2 = new Thread(example2::writerVolatile);
        Thread reader2 = new Thread(example2::readerVolatile);
        
        reader2.start();
        Thread.sleep(100);
        writer2.start();
        
        writer2.join();
        reader2.join();
        
        // synchronized を使った安全な実装のデモ
        System.out.println("\n=== Demonstrating synchronized solution ===");
        MemoryModelExample example3 = new MemoryModelExample();
        
        Thread writer3 = new Thread(example3::writerSync);
        Thread reader3 = new Thread(example3::readerSync);
        
        reader3.start();
        Thread.sleep(100);
        writer3.start();
        
        writer3.join();
        reader3.join();
    }
}