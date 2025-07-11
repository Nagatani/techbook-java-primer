/**
 * リストAC-28
 * RaceConditionExampleクラス
 * 
 * 元ファイル: appendix-c-theoretical-foundations.md (925行目)
 */

public class RaceConditionExample {
    private int counter = 0; // 共有状態
    
    // データ競合が発生する危険なメソッド
    public void unsafeIncrement() {
        // この操作は原子的でない:
        // 1. counter の読み取り
        // 2. 値の増加
        // 3. counter への書き込み
        counter++; // 複数のスレッドで同時実行すると競合状態
    }
    
    // 同期化による競合状態の解決
    public synchronized void safeIncrement() {
        counter++; // synchronized により原子性を保証
    }
    
    // Atomicクラスによる解決
    private AtomicInteger atomicCounter = new AtomicInteger(0);
    
    public void atomicIncrement() {
        atomicCounter.incrementAndGet(); // ハードウェアレベルでの原子性
    }
}