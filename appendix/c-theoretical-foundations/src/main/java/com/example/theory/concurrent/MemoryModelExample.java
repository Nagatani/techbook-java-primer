package com.example.theory.concurrent;

/**
 * Java Memory Model (JMM) とメモリ順序保証
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
        // 1と2の間にhappens-before関係を作るものがないので、別のスレッドからは
        // 2の結果だけが先に見える場合がある（コンパイラーやCPUによる並べ替え）
    }
    
    /**
     * Reader スレッド
     */
    public void reader() {
        while (!ready) { // 3: readyの変化がいつまでも見えない可能性もある
            Thread.yield();
        }
        IO.println(value); // 4: 0 が出力されることをJMMは禁止しない（実際に起きるかは環境による）
    }
    
    /**
     * volatile による順序保証
     */
    private volatile boolean readyVolatile = false;
    private int valueVolatile = 0;
    
    public void writerVolatile() {
        valueVolatile = 42;      // 1
        readyVolatile = true;    // 2: volatile write
        // 同じスレッドの中では、1は2より前に起きる（プログラム順序によるhappens-before）
    }
    
    public void readerVolatile() {
        while (!readyVolatile) { // 3: volatile read
            Thread.yield();
        }
        IO.println(valueVolatile); // 4: 必ず 42 が出力
        // 3がtrueを読んだなら、2の書き込みは3の読み取りより前に起きている（volatileの規則）。
        // 1→2→3→4がhappens-beforeでつながるので、1の書き込みは4から見える
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
            IO.println(valueSync); // 必ず 42 が出力
        }
    }
    
    /**
     * メモリモデルのデモンストレーション
     */
    public static void demonstrateMemoryModel() throws InterruptedException {
        // 問題のある実装のデモ
        IO.println("=== Demonstrating potential memory visibility issue ===");
        MemoryModelExample example1 = new MemoryModelExample();
        
        Thread writer1 = new Thread(example1::writer);
        Thread reader1 = new Thread(example1::reader);
        // readerが値の変化を見られずに回り続けてもJVMが終われるように、デーモンスレッドにする
        reader1.setDaemon(true);
        
        reader1.start();
        Thread.sleep(100); // readerが先に開始されるように
        writer1.start();
        
        writer1.join(1000);
        reader1.join(1000);
        
        // 多くの環境では42と表示されて終わる。問題は「必ずそうなる」という保証がないこと
        if (reader1.isAlive()) {
            // reader()は割り込みを確かめないので、interrupt()では止まらない
            IO.println("Reader thread may be stuck due to visibility issue!");
        }
        
        // volatile を使った安全な実装のデモ
        IO.println("\n=== Demonstrating volatile solution ===");
        MemoryModelExample example2 = new MemoryModelExample();
        
        Thread writer2 = new Thread(example2::writerVolatile);
        Thread reader2 = new Thread(example2::readerVolatile);
        
        reader2.start();
        Thread.sleep(100);
        writer2.start();
        
        writer2.join();
        reader2.join();
        
        // synchronized を使った安全な実装のデモ
        IO.println("\n=== Demonstrating synchronized solution ===");
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