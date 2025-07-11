/**
 * リスト16-27
 * CountDownLatchExampleクラス
 * 
 * 元ファイル: chapter16-multithreading.md (2315行目)
 */

import java.util.concurrent.CountDownLatch;

public class CountDownLatchExample {
    // 3. CountDownLatch - カウントダウン同期
    static class RaceExample {
        public static void demonstrate() throws InterruptedException {
            int runnerCount = 5;
            CountDownLatch startSignal = new CountDownLatch(1);
            CountDownLatch finishSignal = new CountDownLatch(runnerCount);
            
            for (int i = 0; i < runnerCount; i++) {
                int runnerId = i + 1;
                new Thread(() -> {
                    try {
                        System.out.println("ランナー " + runnerId + " 準備完了");
                        startSignal.await(); // スタート信号を待つ
                        
                        System.out.println("ランナー " + runnerId + " スタート！");
                        Thread.sleep((long)(Math.random() * 3000)); // 走行時間
                        System.out.println("ランナー " + runnerId + " ゴール！");
                        
                        finishSignal.countDown(); // ゴールしたことを通知
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            }
            
            Thread.sleep(1000);
            System.out.println("位置について... よーい...");
            Thread.sleep(1000);
            System.out.println("ドン！");
            startSignal.countDown(); // 全ランナーをスタートさせる
            
            finishSignal.await(); // 全ランナーのゴールを待つ
            System.out.println("レース終了！");
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        // CountDownLatchのデモ
        System.out.println("=== CountDownLatch デモ ===");
        RaceExample.demonstrate();
    }
}