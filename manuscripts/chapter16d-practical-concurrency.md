# 第16章 マルチスレッドプログラミング - Part D: 実践的な並行処理

## 実践的なパフォーマンス測定とベンチマーク

```java
import java.util.concurrent.*;
import java.util.Arrays;
import java.util.Random;

public class ParallelPerformanceExample {
    
    // 大規模配列の並列ソート
    public static void parallelSortBenchmark() {
        int size = 10_000_000;
        int[] array1 = new int[size];
        int[] array2 = new int[size];
        Random random = new Random();
        
        // ランダムデータの生成
        for (int i = 0; i < size; i++) {
            int value = random.nextInt();
            array1[i] = value;
            array2[i] = value;
        }
        
        // シングルスレッドソート
        long startTime = System.currentTimeMillis();
        Arrays.sort(array1);
        long singleThreadTime = System.currentTimeMillis() - startTime;
        
        // 並列ソート
        startTime = System.currentTimeMillis();
        Arrays.parallelSort(array2);
        long parallelTime = System.currentTimeMillis() - startTime;
        
        System.out.println("=== ソートベンチマーク ===");
        System.out.println("配列サイズ: " + size);
        System.out.println("シングルスレッド: " + singleThreadTime + "ms");
        System.out.println("並列処理: " + parallelTime + "ms");
        System.out.println("性能向上: " + 
            String.format("%.2fx", (double)singleThreadTime / parallelTime));
    }
    
    // 並列ストリーム処理
    public static void parallelStreamExample() {
        int size = 50_000_000;
        long[] numbers = new long[size];
        Arrays.fill(numbers, 1);
        
        // シーケンシャル処理
        long startTime = System.currentTimeMillis();
        long sum1 = Arrays.stream(numbers).sum();
        long sequentialTime = System.currentTimeMillis() - startTime;
        
        // 並列処理
        startTime = System.currentTimeMillis();
        long sum2 = Arrays.stream(numbers).parallel().sum();
        long parallelTime = System.currentTimeMillis() - startTime;
        
        System.out.println("\n=== ストリーム処理ベンチマーク ===");
        System.out.println("要素数: " + size);
        System.out.println("シーケンシャル: " + sequentialTime + "ms");
        System.out.println("並列: " + parallelTime + "ms");
        System.out.println("性能向上: " + 
            String.format("%.2fx", (double)sequentialTime / parallelTime));
    }
    
    // ForkJoinPoolを使った再帰的並列処理
    static class RecursiveSum extends RecursiveTask<Long> {
        private static final int THRESHOLD = 10_000;
        private final long[] array;
        private final int start, end;
        
        public RecursiveSum(long[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end = end;
        }
        
        @Override
        protected Long compute() {
            if (end - start <= THRESHOLD) {
                // 小さなタスクは直接計算
                long sum = 0;
                for (int i = start; i < end; i++) {
                    sum += array[i];
                }
                return sum;
            } else {
                // 大きなタスクは分割
                int mid = (start + end) / 2;
                RecursiveSum left = new RecursiveSum(array, start, mid);
                RecursiveSum right = new RecursiveSum(array, mid, end);
                
                left.fork(); // 左側を非同期実行
                long rightResult = right.compute(); // 右側を現在のスレッドで実行
                long leftResult = left.join(); // 左側の結果を待つ
                
                return leftResult + rightResult;
            }
        }
    }
    
    public static void forkJoinExample() {
        int size = 100_000_000;
        long[] array = new long[size];
        Arrays.fill(array, 1);
        
        ForkJoinPool pool = new ForkJoinPool();
        
        long startTime = System.currentTimeMillis();
        Long result = pool.invoke(new RecursiveSum(array, 0, size));
        long time = System.currentTimeMillis() - startTime;
        
        System.out.println("\n=== ForkJoinPool ベンチマーク ===");
        System.out.println("要素数: " + size);
        System.out.println("計算結果: " + result);
        System.out.println("処理時間: " + time + "ms");
        System.out.println("使用スレッド数: " + pool.getParallelism());
    }
    
    public static void main(String[] args) {
        // CPUコア数の確認
        int processors = Runtime.getRuntime().availableProcessors();
        System.out.println("利用可能なCPUコア数: " + processors);
        
        parallelSortBenchmark();
        parallelStreamExample();
        forkJoinExample();
    }
}
```

## スレッドセーフなシングルトンパターン

```java
public class ThreadSafeSingletonExamples {
    
    // 1. 同期化メソッドを使った実装（シンプルだが遅い）
    static class SynchronizedSingleton {
        private static SynchronizedSingleton instance;
        
        private SynchronizedSingleton() {}
        
        public static synchronized SynchronizedSingleton getInstance() {
            if (instance == null) {
                instance = new SynchronizedSingleton();
            }
            return instance;
        }
    }
    
    // 2. ダブルチェックロッキング（高速だが複雑）
    static class DoubleCheckedSingleton {
        private static volatile DoubleCheckedSingleton instance;
        
        private DoubleCheckedSingleton() {}
        
        public static DoubleCheckedSingleton getInstance() {
            if (instance == null) {
                synchronized (DoubleCheckedSingleton.class) {
                    if (instance == null) {
                        instance = new DoubleCheckedSingleton();
                    }
                }
            }
            return instance;
        }
    }
    
    // 3. 静的内部クラスホルダー（推奨・遅延初期化）
    static class HolderSingleton {
        private HolderSingleton() {}
        
        private static class Holder {
            private static final HolderSingleton INSTANCE = new HolderSingleton();
        }
        
        public static HolderSingleton getInstance() {
            return Holder.INSTANCE;
        }
    }
    
    // 4. Enumシングルトン（最も安全）
    enum EnumSingleton {
        INSTANCE;
        
        public void doSomething() {
            System.out.println("Enum singleton method");
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        // マルチスレッドでのシングルトンアクセステスト
        ExecutorService executor = Executors.newFixedThreadPool(100);
        CountDownLatch latch = new CountDownLatch(100);
        
        for (int i = 0; i < 100; i++) {
            executor.execute(() -> {
                // 各実装のインスタンスを取得
                HolderSingleton singleton = HolderSingleton.getInstance();
                System.out.println(Thread.currentThread().getName() + 
                    ": " + singleton.hashCode());
                latch.countDown();
            });
        }
        
        latch.await();
        executor.shutdown();
        
        // すべて同じハッシュコードが出力されることを確認
    }
}
```

## 一般的な並行処理の落とし穴と解決策

```java
import java.util.*;
import java.util.concurrent.*;

public class ConcurrencyPitfallsAndSolutions {
    
    // 落とし穴1: 不適切な同期によるデッドロック
    static class DeadlockExample {
        private final Object lock1 = new Object();
        private final Object lock2 = new Object();
        
        // デッドロックが発生する可能性のあるコード
        public void method1() {
            synchronized (lock1) {
                System.out.println("Method1: lock1を取得");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                synchronized (lock2) {
                    System.out.println("Method1: lock2を取得");
                }
            }
        }
        
        public void method2() {
            synchronized (lock2) {
                System.out.println("Method2: lock2を取得");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                synchronized (lock1) {
                    System.out.println("Method2: lock1を取得");
                }
            }
        }
        
        // 解決策: ロック順序の統一
        public void safeMethod1() {
            synchronized (lock1) {
                synchronized (lock2) {
                    System.out.println("Safe method1 実行");
                }
            }
        }
        
        public void safeMethod2() {
            synchronized (lock1) {  // 同じ順序でロック
                synchronized (lock2) {
                    System.out.println("Safe method2 実行");
                }
            }
        }
    }
    
    // 落とし穴2: 不適切なvolatileの使用
    static class VolatileMisuse {
        // 誤り: volatileは複合操作をアトミックにしない
        private volatile int counter = 0;
        
        public void incorrectIncrement() {
            counter++; // これはアトミックではない！
        }
        
        // 解決策: AtomicIntegerを使用
        private final AtomicInteger atomicCounter = new AtomicInteger(0);
        
        public void correctIncrement() {
            atomicCounter.incrementAndGet();
        }
    }
    
    // 落とし穴3: スレッドセーフでないコレクションの誤用
    static class CollectionMisuse {
        // 誤り: 通常のHashMapは並行アクセスに対して安全でない
        private final Map<String, Integer> unsafeMap = new HashMap<>();
        
        public void unsafeOperation() {
            // 複数スレッドから同時に呼ばれると問題発生
            unsafeMap.put("key", unsafeMap.getOrDefault("key", 0) + 1);
        }
        
        // 解決策1: ConcurrentHashMapを使用
        private final ConcurrentHashMap<String, Integer> safeMap = 
            new ConcurrentHashMap<>();
        
        public void safeOperation1() {
            safeMap.merge("key", 1, Integer::sum);
        }
        
        // 解決策2: 同期化
        private final Map<String, Integer> syncMap = new HashMap<>();
        
        public synchronized void safeOperation2() {
            syncMap.put("key", syncMap.getOrDefault("key", 0) + 1);
        }
    }
    
    // 落とし穴4: スレッドリーク
    static class ThreadLeakExample {
        // 誤り: ExecutorServiceを適切にシャットダウンしない
        public void causeThreadLeak() {
            ExecutorService executor = Executors.newFixedThreadPool(10);
            executor.execute(() -> System.out.println("Task"));
            // executorをシャットダウンしないとスレッドがリーク！
        }
        
        // 解決策: try-finallyまたはtry-with-resourcesパターン
        public void preventThreadLeak() {
            ExecutorService executor = Executors.newFixedThreadPool(10);
            try {
                executor.execute(() -> System.out.println("Task"));
            } finally {
                executor.shutdown();
                try {
                    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                        executor.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executor.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    
    public static void main(String[] args) {
        System.out.println("並行処理の一般的な落とし穴の例");
        
        // デッドロックの回避例
        DeadlockExample example = new DeadlockExample();
        Thread t1 = new Thread(example::safeMethod1);
        Thread t2 = new Thread(example::safeMethod2);
        t1.start();
        t2.start();
        
        // アトミック操作の例
        VolatileMisuse volatileExample = new VolatileMisuse();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        
        for (int i = 0; i < 1000; i++) {
            executor.execute(volatileExample::correctIncrement);
        }
        
        executor.shutdown();
    }
}
```

## 実践的な並行処理パターン

### 並列データ処理パイプライン

```java
import java.util.concurrent.*;
import java.util.List;
import java.util.ArrayList;

public class ParallelDataPipeline {
    
    // データ処理のステージ
    interface Stage<I, O> {
        O process(I input);
    }
    
    // 並列パイプライン
    static class Pipeline<T> {
        private final ExecutorService executor;
        private final List<Stage<?, ?>> stages;
        
        public Pipeline(int threadPoolSize) {
            this.executor = Executors.newFixedThreadPool(threadPoolSize);
            this.stages = new ArrayList<>();
        }
        
        public <O> Pipeline<O> addStage(Stage<T, O> stage) {
            stages.add(stage);
            return (Pipeline<O>) this;
        }
        
        public CompletableFuture<T> process(T input) {
            CompletableFuture<Object> result = CompletableFuture.completedFuture(input);
            
            for (Stage<?, ?> stage : stages) {
                result = result.thenApplyAsync(data -> 
                    ((Stage<Object, Object>) stage).process(data), executor);
            }
            
            return (CompletableFuture<T>) result;
        }
        
        public void shutdown() {
            executor.shutdown();
        }
    }
    
    public static void main(String[] args) throws Exception {
        // データ処理パイプラインの例
        Pipeline<String> pipeline = new Pipeline<>(4);
        
        // ステージ1: データ取得
        Stage<String, String> fetchStage = url -> {
            System.out.println("Fetching data from: " + url);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Raw data from " + url;
        };
        
        // ステージ2: データ変換
        Stage<String, String> transformStage = data -> {
            System.out.println("Transforming: " + data);
            return data.toUpperCase();
        };
        
        // ステージ3: データ保存
        Stage<String, String> saveStage = data -> {
            System.out.println("Saving: " + data);
            return "Saved: " + data;
        };
        
        // パイプライン構築
        CompletableFuture<String> future = pipeline
            .addStage(fetchStage)
            .addStage(transformStage)
            .addStage(saveStage)
            .process("http://example.com");
        
        // 結果取得
        String result = future.get();
        System.out.println("Pipeline result: " + result);
        
        pipeline.shutdown();
    }
}
```

### リトライとサーキットブレーカーパターン

```java
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class ResiliencePatterns {
    
    // リトライパターン
    static class RetryExecutor {
        private final int maxRetries;
        private final long retryDelay;
        
        public RetryExecutor(int maxRetries, long retryDelay) {
            this.maxRetries = maxRetries;
            this.retryDelay = retryDelay;
        }
        
        public <T> CompletableFuture<T> executeWithRetry(Callable<T> task) {
            return executeWithRetry(task, 0);
        }
        
        private <T> CompletableFuture<T> executeWithRetry(Callable<T> task, int attempt) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return task.call();
                } catch (Exception e) {
                    if (attempt < maxRetries) {
                        System.out.println("Retry attempt " + (attempt + 1) + 
                            " after " + retryDelay + "ms");
                        try {
                            Thread.sleep(retryDelay);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                        }
                        return executeWithRetry(task, attempt + 1).join();
                    }
                    throw new CompletionException(e);
                }
            });
        }
    }
    
    // サーキットブレーカーパターン
    static class CircuitBreaker {
        enum State { CLOSED, OPEN, HALF_OPEN }
        
        private final AtomicReference<State> state = 
            new AtomicReference<>(State.CLOSED);
        private final AtomicInteger failureCount = new AtomicInteger(0);
        private final AtomicLong lastFailureTime = new AtomicLong(0);
        
        private final int failureThreshold;
        private final long timeout;
        
        public CircuitBreaker(int failureThreshold, long timeout) {
            this.failureThreshold = failureThreshold;
            this.timeout = timeout;
        }
        
        public <T> T execute(Callable<T> task) throws Exception {
            State currentState = state.get();
            
            if (currentState == State.OPEN) {
                if (System.currentTimeMillis() - lastFailureTime.get() > timeout) {
                    state.set(State.HALF_OPEN);
                    System.out.println("Circuit breaker: HALF_OPEN");
                } else {
                    throw new RuntimeException("Circuit breaker is OPEN");
                }
            }
            
            try {
                T result = task.call();
                if (currentState == State.HALF_OPEN) {
                    state.set(State.CLOSED);
                    failureCount.set(0);
                    System.out.println("Circuit breaker: CLOSED");
                }
                return result;
            } catch (Exception e) {
                failureCount.incrementAndGet();
                lastFailureTime.set(System.currentTimeMillis());
                
                if (failureCount.get() >= failureThreshold) {
                    state.set(State.OPEN);
                    System.out.println("Circuit breaker: OPEN");
                }
                throw e;
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
        // リトライパターンの例
        RetryExecutor retryExecutor = new RetryExecutor(3, 1000);
        
        CompletableFuture<String> retryFuture = retryExecutor.executeWithRetry(() -> {
            if (Math.random() < 0.7) {
                throw new RuntimeException("Random failure");
            }
            return "Success!";
        });
        
        try {
            System.out.println("Retry result: " + retryFuture.get());
        } catch (Exception e) {
            System.out.println("Failed after retries: " + e.getMessage());
        }
        
        // サーキットブレーカーの例
        CircuitBreaker circuitBreaker = new CircuitBreaker(3, 5000);
        
        for (int i = 0; i < 10; i++) {
            try {
                String result = circuitBreaker.execute(() -> {
                    if (Math.random() < 0.6) {
                        throw new RuntimeException("Service failure");
                    }
                    return "Service response";
                });
                System.out.println("Request " + i + ": " + result);
            } catch (Exception e) {
                System.out.println("Request " + i + " failed: " + e.getMessage());
            }
            
            Thread.sleep(500);
        }
    }
}
```

## まとめ

このパートでは、実践的な並行処理について学習しました：

- **パフォーマンス測定**：並列処理による性能向上の測定方法
- **ForkJoinPool**：分割統治法による並列処理
- **並列ストリーム**：Java 8以降の並列処理API
- **スレッドセーフなパターン**：シングルトンパターンの実装方法
- **一般的な落とし穴**：
  - デッドロック
  - 不適切なvolatileの使用
  - スレッドセーフでないコレクション
  - スレッドリーク
- **実践的なパターン**：
  - 並列データ処理パイプライン
  - リトライパターン
  - サーキットブレーカーパターン

これらの知識を活用することで、安全で効率的な並行処理プログラムを設計・実装できます。

---

次のパート：[Part E - 章末演習](chapter16e-exercises.md)