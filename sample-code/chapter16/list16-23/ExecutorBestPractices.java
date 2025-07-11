/**
 * リスト16-23
 * ExecutorBestPracticesクラス
 * 
 * 元ファイル: chapter16-multithreading.md (2010行目)
 */

import java.util.concurrent.*;

public class ExecutorBestPractices {
    
    // カスタムThreadFactoryの例
    static class NamedThreadFactory implements ThreadFactory {
        private int counter = 0;
        private final String prefix;
        
        public NamedThreadFactory(String prefix) {
            this.prefix = prefix;
        }
        
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(prefix + "-" + counter++);
            thread.setDaemon(false);
            thread.setPriority(Thread.NORM_PRIORITY);
            return thread;
        }
    }
    
    // RejectedExecutionHandlerの例
    static class CustomRejectionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            System.err.println("Task rejected: " + r.toString());
            // 代替処理を実行するか、ログに記録する
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        // カスタムThreadPoolExecutorの作成
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            2,                      // コアプールサイズ
            4,                      // 最大プールサイズ
            60L,                    // キープアライブ時間
            TimeUnit.SECONDS,       // 時間単位
            new LinkedBlockingQueue<>(10),  // タスクキュー
            new NamedThreadFactory("CustomWorker"),  // ThreadFactory
            new CustomRejectionHandler()  // RejectedExecutionHandler
        );
        
        // ExecutorServiceの監視
        System.out.println("=== Executor監視 ===");
        for (int i = 0; i < 20; i++) {
            final int taskId = i;
            try {
                executor.execute(() -> {
                    System.out.println("Task " + taskId + " executed by " + 
                        Thread.currentThread().getName());
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
                
                // Executorの状態を出力
                System.out.printf("Active: %d, Completed: %d, Queue: %d%n",
                    executor.getActiveCount(),
                    executor.getCompletedTaskCount(),
                    executor.getQueue().size());
                
            } catch (RejectedExecutionException e) {
                System.err.println("Task " + taskId + " was rejected");
            }
            
            Thread.sleep(100);
        }
        
        // 適切なシャットダウン処理
        executor.shutdown();
        if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
            System.err.println("Executor did not terminate in time");
            executor.shutdownNow();
        }
        
        System.out.println("\n最終統計:");
        System.out.println("完了タスク数: " + executor.getCompletedTaskCount());
    }
}