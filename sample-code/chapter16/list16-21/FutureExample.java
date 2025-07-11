/**
 * リスト16-21
 * FutureExampleクラス
 * 
 * 元ファイル: chapter16-multithreading.md (1897行目)
 */

import java.util.concurrent.*;

public class FutureExample {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Callable<Integer> task = () -> {
            System.out.println("Calculating sum...");
            Thread.sleep(2000); // 時間のかかる計算を模倣
            int sum = 0;
            for (int i = 1; i <= 100; i++) {
                sum += i;
            }
            return sum;
        };

        Future<Integer> future = executor.submit(task);
        System.out.println("他の処理を実行中です...");

        // future.get()でタスクの完了を待ち、結果を取得
        Integer result = future.get();
        System.out.println("計算結果: " + result);

        executor.shutdown();
    }
}