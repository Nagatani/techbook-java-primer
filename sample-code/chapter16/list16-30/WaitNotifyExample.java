/**
 * リスト16-30
 * WaitNotifyExampleクラス
 * 
 * 元ファイル: chapter16-multithreading.md (2455行目)
 */

public class WaitNotifyExample {
    private static final Object lock = new Object();
    private static boolean dataReady = false;
    private static String sharedData = null;
    
    public static void main(String[] args) {
        // プロデューサー
        Thread producer = new Thread(() -> {
            synchronized (lock) {
                System.out.println("Producer: データ生成中...");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                sharedData = "重要なデータ";
                dataReady = true;
                System.out.println("Producer: データ生成完了、通知送信");
                lock.notify(); // 待機中のスレッドに通知
            }
        });
        
        // コンシューマー
        Thread consumer = new Thread(() -> {
            synchronized (lock) {
                while (!dataReady) {
                    System.out.println("Consumer: データ待機中...");
                    try {
                        lock.wait(); // データが準備されるまで待機
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                System.out.println("Consumer: データ受信: " + sharedData);
            }
        });
        
        consumer.start();
        producer.start();
    }
}