/**
 * リストAC-30
 * MemoryModelExampleクラス
 * 
 * 元ファイル: appendix-c-theoretical-foundations.md (1017行目)
 */

public class MemoryModelExample {
    private boolean ready = false;
    private int value = 0;
    
    // Writer スレッド
    public void writer() {
        value = 42;      // 1
        ready = true;    // 2
        // リオーダリングにより 2 が 1 より先に実行される可能性
    }
    
    // Reader スレッド
    public void reader() {
        while (!ready) { // 3
            Thread.yield();
        }
        System.out.println(value); // 4: 0 が出力される可能性
    }
    
    // volatile による順序保証
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
}