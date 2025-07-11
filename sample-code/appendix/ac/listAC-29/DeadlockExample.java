/**
 * リストAC-29
 * DeadlockExampleクラス
 * 
 * 元ファイル: appendix-c-theoretical-foundations.md (963行目)
 */

public class DeadlockExample {
    private final Object lock1 = new Object();
    private final Object lock2 = new Object();
    
    // デッドロックが発生する可能性のあるコード
    public void method1() {
        synchronized (lock1) {
            System.out.println("Thread " + Thread.currentThread().getId() + " acquired lock1");
            
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            
            synchronized (lock2) { // 循環待機の原因
                System.out.println("Thread " + Thread.currentThread().getId() + " acquired lock2");
            }
        }
    }
    
    public void method2() {
        synchronized (lock2) {
            System.out.println("Thread " + Thread.currentThread().getId() + " acquired lock2");
            
            try { Thread.sleep(100); } catch (InterruptedException e) {}
            
            synchronized (lock1) { // 循環待機の原因
                System.out.println("Thread " + Thread.currentThread().getId() + " acquired lock1");
            }
        }
    }
    
    // デッドロック回避策: ロック順序の統一
    public void safeMethod1() {
        synchronized (lock1) { // 常に lock1 を先に取得
            synchronized (lock2) {
                // 処理
            }
        }
    }
    
    public void safeMethod2() {
        synchronized (lock1) { // 常に lock1 を先に取得
            synchronized (lock2) {
                // 処理
            }
        }
    }
}