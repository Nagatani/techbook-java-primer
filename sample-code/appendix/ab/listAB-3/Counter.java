/**
 * リストAB-3
 * Counterクラス
 * 
 * 元ファイル: appendix-b03-programming-paradigms.md (62行目)
 */

// オブジェクト指向：データと振る舞いのカプセル化
public class Counter {
    private int value = 0;
    
    public void increment() {
        value++;
    }
    
    public int getValue() {
        return value;
    }
}