/**
 * リスト16-28
 * CyclicBarrierExampleクラス
 * 
 * 元ファイル: chapter16-multithreading.md (2366行目)
 */

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.BrokenBarrierException;

public class CyclicBarrierExample {
    
    public static void main(String[] args) {
        int workerCount = 3;
        
        // バリアアクション：全員が到達したときに実行される
        Runnable barrierAction = () -> 
            System.out.println("=== 全員が到達しました！次のフェーズへ ===");
        
        CyclicBarrier barrier = new CyclicBarrier(workerCount, barrierAction);
        
        // ワーカースレッド
        for (int i = 0; i < workerCount; i++) {
            final int workerId = i;
            new Thread(() -> {
                try {
                    for (int phase = 1; phase <= 3; phase++) {
                        // 各フェーズの作業
                        System.out.printf("Worker %d: フェーズ %d 開始%n", 
                            workerId, phase);
                        Thread.sleep((long)(Math.random() * 2000));
                        System.out.printf("Worker %d: フェーズ %d 完了%n", 
                            workerId, phase);
                        
                        // バリアで待機
                        barrier.await();
                    }
                } catch (InterruptedException | BrokenBarrierException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }
}