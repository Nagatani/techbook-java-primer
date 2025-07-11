/**
 * リスト16-10
 * コードスニペット
 * 
 * 元ファイル: chapter16-multithreading.md (860行目)
 */

class SynchronizedCounter {
    private int count = 0;

    // このメソッドはsynchronizedによりスレッドセーフになる
    public synchronized void increment() {
        count++;
    }

    public int getCount() {
        return count;
    }
}