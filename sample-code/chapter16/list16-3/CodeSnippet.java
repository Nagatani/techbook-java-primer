/**
 * リスト16-3
 * コードスニペット
 * 
 * 元ファイル: chapter16-multithreading.md (218行目)
 */

// ① Thread継承方式（非推奨）
class MyThread extends Thread {
    @Override
    public void run() {
        System.out.println("Thread継承方式で実行");
    }
}

// ② Runnable実装方式（推奨）  
class MyTask implements Runnable {
    @Override
    public void run() {
        System.out.println("Runnable実装方式で実行");
    }
}

// ③ ラムダ式方式（Java 8以降推奨）
new Thread(() -> {
    System.out.println("ラムダ式方式で実行");
}).start();