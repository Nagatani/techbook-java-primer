import java.util.*;
import java.util.concurrent.*;

public class ExecutorLambdaExample {
    public static void main(String[] args) {
        // ExecutorServiceとラムダ式の組み合わせ
        ExecutorService executor = Executors.newFixedThreadPool(4);
        
        // 複数のタスクを並行実行
        List<Callable<String>> tasks = Arrays.asList(
            () -> {
                Thread.sleep(1000);  // 1秒待機
                return "Task 1 completed";
            },
            () -> {
                Thread.sleep(500);   // 0.5秒待機
                return "Task 2 completed";
            }, 
            () -> {
                Thread.sleep(1500);  // 1.5秒待機
                return "Task 3 completed";
            }
        );
        
        IO.println("タスク実行開始...");
        long startTime = System.currentTimeMillis();
        
        try {
            List<Future<String>> results = executor.invokeAll(tasks);
            IO.println("実行結果:");
            results.forEach(future -> {
                try {
                    IO.println(future.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
        
        long endTime = System.currentTimeMillis();
        // 順に実行すると合計3秒かかるタスクが、それより短い時間で終わったか
        IO.println("3秒未満で終わったか: " + (endTime - startTime < 3000));
    }
}
