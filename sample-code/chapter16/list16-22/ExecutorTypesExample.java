/**
 * リスト16-22
 * ExecutorTypesExampleクラス
 * 
 * 元ファイル: chapter16-multithreading.md (1929行目)
 */

import java.util.concurrent.*;

public class ExecutorTypesExample {
    public static void main(String[] args) throws InterruptedException {
        // 1. 固定サイズのスレッドプール
        ExecutorService fixedPool = Executors.newFixedThreadPool(3);
        System.out.println("=== Fixed Thread Pool ===");
        for (int i = 0; i < 6; i++) {
            final int taskId = i;
            fixedPool.execute(() -> {
                System.out.println("Task " + taskId + " executed by " + 
                    Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        fixedPool.shutdown();
        fixedPool.awaitTermination(10, TimeUnit.SECONDS);
        
        // 2. キャッシュ型スレッドプール（必要に応じてスレッドを作成）
        ExecutorService cachedPool = Executors.newCachedThreadPool();
        System.out.println("\n=== Cached Thread Pool ===");
        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            cachedPool.execute(() -> {
                System.out.println("Task " + taskId + " executed by " + 
                    Thread.currentThread().getName());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        cachedPool.shutdown();
        cachedPool.awaitTermination(5, TimeUnit.SECONDS);
        
        // 3. 単一スレッドのExecutor
        ExecutorService singleExecutor = Executors.newSingleThreadExecutor();
        System.out.println("\n=== Single Thread Executor ===");
        for (int i = 0; i < 3; i++) {
            final int taskId = i;
            singleExecutor.execute(() -> {
                System.out.println("Task " + taskId + " executed sequentially");
            });
        }
        singleExecutor.shutdown();
        singleExecutor.awaitTermination(2, TimeUnit.SECONDS);
        
        // 4. スケジュール機能付きExecutor
        ScheduledExecutorService scheduledExecutor = 
            Executors.newScheduledThreadPool(2);
        System.out.println("\n=== Scheduled Executor ===");
        
        // 一定時間後に実行
        scheduledExecutor.schedule(() -> 
            System.out.println("Delayed task executed after 2 seconds"), 
            2, TimeUnit.SECONDS);
        
        // 定期的に実行
        ScheduledFuture<?> periodicTask = scheduledExecutor.scheduleAtFixedRate(() -> 
            System.out.println("Periodic task executed every 1 second"), 
            0, 1, TimeUnit.SECONDS);
        
        // 5秒後に定期タスクをキャンセル
        Thread.sleep(5000);
        periodicTask.cancel(false);
        
        scheduledExecutor.shutdown();
        scheduledExecutor.awaitTermination(2, TimeUnit.SECONDS);
    }
}