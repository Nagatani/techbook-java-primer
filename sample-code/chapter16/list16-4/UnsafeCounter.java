/**
 * リスト16-4
 * コードスニペット
 * 
 * 元ファイル: chapter16-multithreading.md (335行目)
 */

class UnsafeCounter {
    private int count = 0;
    
    public void increment() {
        count++;  // ①
    }
}