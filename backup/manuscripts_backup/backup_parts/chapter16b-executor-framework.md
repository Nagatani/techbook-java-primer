# 第16章 マルチスレッドプログラミング - Part B: Executorフレームワーク

## 20.4 Executorフレームワークによる高度なスレッド管理

スレッドを直接生成・管理するのは複雑で、エラーも発生しやすいため、現代のJavaプログラミングでは**Executorフレームワーク**を使用するのが一般的です。これは、スレッドの生成・管理を抽象化し、**スレッドプール**を利用して効率的にタスクを実行するためのしくみです。

### `ExecutorService`とスレッドプール

`ExecutorService`は、タスクの投入とスレッドプールの管理を行うためのインターフェイスです。スレッドプールは、あらかじめ作成された再利用可能なスレッドの集まりです。

-   **パフォーマンス向上**: スレッドの生成・破棄コストを削減できます。
-   **リソース管理**: 作成されるスレッド数を制限し、システムの安定性を高めます。

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorExample {
    public static void main(String[] args) {
        // 3つのスレッドを持つスレッドプールを作成
        ExecutorService executor = Executors.newFixedThreadPool(3);

        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            executor.execute(() -> {
                System.out.println("Task " + taskId + " is being executed by " + 
                    Thread.currentThread().getName());
                try {
                    Thread.sleep(1000); // 1秒の処理をシミュレート
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // ExecutorServiceをシャットダウン
        executor.shutdown();
        try {
            if (!executor.awaitTermination(15, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
```

### `Future`による非同期処理の結果取得

計算結果などの戻り値が必要なタスクには、`Callable<V>`インターフェイスを使います。`submit()`メソッドで`Callable`を投入すると、非同期処理の結果を表す`Future<V>`オブジェクトが返されます。

```java
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
```

### Future と Callable を使った非同期処理

```java
import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;

public class FutureCallableExample {
    
    // 計算に時間がかかるタスク
    static class CalculationTask implements Callable<Long> {
        private final int n;
        
        public CalculationTask(int n) {
            this.n = n;
        }
        
        @Override
        public Long call() throws Exception {
            System.out.println("計算開始: フィボナッチ数列の第" + n + "項");
            long start = System.currentTimeMillis();
            long result = fibonacci(n);
            long time = System.currentTimeMillis() - start;
            System.out.printf("計算完了: F(%d) = %d (所要時間: %dms)%n", n, result, time);
            return result;
        }
        
        private long fibonacci(int n) {
            if (n <= 1) return n;
            return fibonacci(n - 1) + fibonacci(n - 2);
        }
    }
    
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Future<Long>> futures = new ArrayList<>();
        
        // 複数の計算タスクを非同期で実行
        int[] numbers = {35, 38, 40, 42};
        for (int n : numbers) {
            Future<Long> future = executor.submit(new CalculationTask(n));
            futures.add(future);
        }
        
        // 他の処理を実行できる
        System.out.println("計算中に他の処理を実行...");
        Thread.sleep(100);
        
        // 結果を取得（必要になったタイミングで）
        System.out.println("\n=== 計算結果 ===");
        for (int i = 0; i < futures.size(); i++) {
            try {
                // get()は結果が得られるまでブロック
                Long result = futures.get(i).get(10, TimeUnit.SECONDS);
                System.out.println("F(" + numbers[i] + ") = " + result);
            } catch (TimeoutException e) {
                System.out.println("F(" + numbers[i] + ") = タイムアウト");
                futures.get(i).cancel(true); // タスクをキャンセル
            }
        }
        
        executor.shutdown();
    }
}
```

### CompleテーブルFuture による高度な非同期処理

```java
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.Random;

public class CompletableFutureExample {
    
    // 外部APIを呼び出すシミュレーション
    static class WeatherService {
        private static final Random random = new Random();
        
        public static CompletableFuture<String> getTemperature(String city) {
            return CompletableFuture.supplyAsync(() -> {
                System.out.println("気温取得中: " + city);
                // APIコールをシミュレート
                try {
                    Thread.sleep(1000 + random.nextInt(1000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                int temp = 20 + random.nextInt(15);
                return city + ": " + temp + "°C";
            });
        }
        
        public static CompletableFuture<String> getHumidity(String city) {
            return CompletableFuture.supplyAsync(() -> {
                System.out.println("湿度取得中: " + city);
                try {
                    Thread.sleep(1000 + random.nextInt(1000));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                int humidity = 40 + random.nextInt(40);
                return city + ": " + humidity + "%";
            });
        }
    }
    
    public static void main(String[] args) throws Exception {
        // 1. 単純な非同期処理
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("非同期タスク開始");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "処理完了";
        });
        
        // 2. チェーン処理
        CompletableFuture<String> chainedFuture = future1
            .thenApply(result -> result + " -> 変換処理")
            .thenApply(result -> result + " -> 最終処理");
        
        System.out.println("チェーン結果: " + chainedFuture.get());
        
        // 3. 複数の非同期処理を組み合わせる
        String[] cities = {"東京", "大阪", "名古屋"};
        
        // 各都市の気温と湿度を並行して取得
        CompletableFuture<?>[] futures = new CompletableFuture[cities.length];
        for (int i = 0; i < cities.length; i++) {
            final String city = cities[i];
            CompletableFuture<String> temp = WeatherService.getTemperature(city);
            CompletableFuture<String> humidity = WeatherService.getHumidity(city);
            
            // 気温と湿度を組み合わせる
            futures[i] = temp.thenCombine(humidity, (t, h) -> t + ", " + h)
                            .thenAccept(System.out::println);
        }
        
        // すべての処理が完了するまで待つ
        CompletableFuture.allOf(futures).join();
        
        // 4. タイムアウト処理
        CompletableFuture<String> timeoutFuture = CompletableFuture
            .supplyAsync(() -> {
                try {
                    Thread.sleep(5000); // 長時間かかる処理
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return "遅い処理の結果";
            })
            .orTimeout(2, TimeUnit.SECONDS)
            .exceptionally(e -> "タイムアウトしました");
        
        System.out.println("\nタイムアウト処理: " + timeoutFuture.get());
    }
}
```

### さまざまなExecutorの種類

```java
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
```

### Executorのベストプラクティス

```java
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
```

## まとめ

このパートでは、Executorフレームワークを使った高度なスレッド管理について学習しました：

- **ExecutorService**：スレッドプールによる効率的なタスク管理
- **Future と Callable**：非同期処理の結果取得
- **CompleテーブルFuture**：チェイン処理や組み合わせ処理が可能な高度な非同期処理
- **さまざまなExecutor**：用途に応じた適切なExecutorの選択
- **ベストプラクティス**：カスタム設定と適切なリソース管理

Executorフレームワークを使うことで、スレッドの直接管理から解放され、より安全で効率的な並行処理プログラムを作成できます。次のパートでは、並行コレクションと高度な同期プリミティブについて学習します。



次のパート：[Part C - 並行コレクションと同期プリミティブ](chapter16c-concurrent-utilities.md)