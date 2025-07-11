/**
 * リスト16-29
 * ExchangerExampleクラス
 * 
 * 元ファイル: chapter16-multithreading.md (2409行目)
 */

import java.util.concurrent.Exchanger;

public class ExchangerExample {
    
    public static void main(String[] args) {
        Exchanger<String> exchanger = new Exchanger<>();
        
        // プロデューサースレッド
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    String data = "データ" + i;
                    System.out.println("Producer: " + data + " を送信");
                    String response = exchanger.exchange(data);
                    System.out.println("Producer: " + response + " を受信");
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        // コンシューマースレッド
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    String received = exchanger.exchange("ACK" + i);
                    System.out.println("Consumer: " + received + " を受信");
                    System.out.println("Consumer: ACK" + i + " を送信");
                    Thread.sleep(1500);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        producer.start();
        consumer.start();
    }
}