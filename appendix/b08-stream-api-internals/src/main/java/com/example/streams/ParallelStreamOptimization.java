package com.example.streams;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 並列ストリームの最適化技術とベストプラクティス
 * 効果的な並列処理とパフォーマンスチューニングの手法を学習
 */
public class ParallelStreamOptimization {
    
    /**
     * データサイズ別の並列化効果を測定
     */
    public static void measureParallelEffectiveness() {
        IO.println("=== Parallel Stream Effectiveness by Data Size ===");
        
        int[] dataSizes = {100, 1_000, 10_000, 100_000, 1_000_000};
        
        for (int size : dataSizes) {
            List<Integer> data = IntStream.range(0, size)
                .boxed()
                .collect(Collectors.toList());
            
            // CPU集約的なタスク：素数判定
            long sequentialTime = measureTime(() -> {
                long count = data.stream()
                    .filter(ParallelStreamOptimization::isPrime)
                    .count();
                return count;
            });
            
            long parallelTime = measureTime(() -> {
                long count = data.parallelStream()
                    .filter(ParallelStreamOptimization::isPrime)
                    .count();
                return count;
            });
            
            double speedup = (double) sequentialTime / parallelTime;
            IO.println("Size: %9d | Sequential: %8.3fms | Parallel: %8.3fms | Speedup: %.2fx".formatted(
                size, toMillis(sequentialTime), toMillis(parallelTime), speedup));
        }
    }
    
    /**
     * CPU集約的なタスクの例：素数判定
     */
    private static boolean isPrime(int n) {
        if (n < 2) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }
    
    /**
     * Fork/Join Pool のカスタマイズ
     */
    public static void demonstrateCustomForkJoinPool() {
        IO.println("\n=== Custom ForkJoinPool Configuration ===");
        
        List<Integer> data = IntStream.range(0, 10000)
            .boxed()
            .collect(Collectors.toList());
        
        // 共通プール（既定の並列度は、通常「CPUコア数 - 1」。呼び出したスレッドも処理に加わる）
        int defaultPoolSize = ForkJoinPool.commonPool().getParallelism();
        IO.println("Default pool parallelism: " + defaultPoolSize);
        
        long defaultTime = measureTime(() -> {
            return data.parallelStream()
                .filter(ParallelStreamOptimization::isPrime)
                .count();
        });
        IO.println("Default pool time: %.3fms".formatted(toMillis(defaultTime)));
        
        // カスタムプール（コア数の2倍）
        // 注意: 独自のForkJoinPoolの中でparallelStreamを動かすと、そのプールで処理されるが、
        // これはAPIの仕様で保証された動作ではなく、実装に依存する
        int customPoolSize = Math.max(2, defaultPoolSize * 2);
        ForkJoinPool customPool = new ForkJoinPool(customPoolSize);
        
        try {
            long customTime = measureTime(() -> {
                return customPool.submit(() ->
                    data.parallelStream()
                        .filter(ParallelStreamOptimization::isPrime)
                        .count()
                ).join();  // join()は検査例外を投げないのでSupplierの中で使える
            });
            IO.println("Custom pool (%d threads) time: %.3fms".formatted(customPoolSize, toMillis(customTime)));
        } finally {
            customPool.shutdown();
        }
    }
    
    /**
     * 並列化に適さないケースの例
     */
    public static void demonstrateInappropriateParallelization() {
        IO.println("\n=== Inappropriate Parallelization Examples ===");
        
        // 1. 小さなデータセット
        List<Integer> smallData = Arrays.asList(1, 2, 3, 4, 5);
        
        long smallSeqTime = measureTime(() -> {
            return smallData.stream()
                .map(x -> x * 2)
                .collect(Collectors.toList());
        });
        
        long smallParTime = measureTime(() -> {
            return smallData.parallelStream()
                .map(x -> x * 2)
                .collect(Collectors.toList());
        });
        
        IO.println("Small dataset (5 elements):");
        IO.println("  Sequential: %.3fms".formatted(toMillis(smallSeqTime)));
        IO.println("  Parallel: %.3fms".formatted(toMillis(smallParTime)));
        IO.println("  Parallel overhead: " + (smallParTime > smallSeqTime ? "Yes" : "No"));
        
        // 2. 順序依存の処理
        // 順序のあるストリームでは、並列でもlimitは「先頭の100件」を返す（結果は逐次と同じ）。
        // そのために各スレッドの結果を順序どおりにそろえる必要があり、並列化の利点が小さくなる。
        // 順序が要らなければunordered()で制約を外せる（どの100件になるかは決まらない）
        List<Integer> numbers = IntStream.range(0, 1_000_000).boxed().collect(Collectors.toList());
        
        List<Integer> sequentialResult = numbers.stream()
            .limit(100)
            .collect(Collectors.toList());
        
        long orderedTime = measureTime(() -> numbers.parallelStream()
            .filter(ParallelStreamOptimization::isPrime)
            .limit(100)
            .collect(Collectors.toList()));
        
        long unorderedTime = measureTime(() -> numbers.parallelStream()
            .unordered()
            .filter(ParallelStreamOptimization::isPrime)
            .limit(100)
            .collect(Collectors.toList()));
        
        List<Integer> parallelResult = numbers.parallelStream()
            .limit(100)
            .collect(Collectors.toList());
        
        IO.println("\nOrder-dependent operations:");
        IO.println("  Sequential and parallel results equal: " + 
            sequentialResult.equals(parallelResult) + " (the order must be preserved, which costs time)");
        IO.println("  parallel filter+limit (ordered): %.3fms".formatted(toMillis(orderedTime)));
        IO.println("  parallel filter+limit (unordered): %.3fms".formatted(toMillis(unorderedTime)));
        
        // 3. I/O集約的なタスク（シミュレーション）
        demonstrateIOBoundTask();
    }
    
    /**
     * I/O集約的なタスクでの並列化効果
     */
    private static void demonstrateIOBoundTask() {
        List<Integer> data = IntStream.range(0, 100).boxed().collect(Collectors.toList());
        
        // I/Oシミュレーション（スリープ）
        Function<Integer, Integer> ioSimulation = x -> {
            try {
                Thread.sleep(1); // 1ms のI/O待機をシミュレート
                return x * 2;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return x;
            }
        };
        
        long ioSeqTime = measureTime(() -> {
            return data.stream()
                .map(ioSimulation)
                .collect(Collectors.toList());
        });
        
        long ioParTime = measureTime(() -> {
            return data.parallelStream()
                .map(ioSimulation)
                .collect(Collectors.toList());
        });
        
        IO.println("I/O-bound task simulation:");
        IO.println("  Sequential: %.3fms".formatted(toMillis(ioSeqTime)));
        IO.println("  Parallel: %.3fms".formatted(toMillis(ioParTime)));
        IO.println("  Speedup: " + String.format("%.2f", (double) ioSeqTime / ioParTime) + "x");
    }
    
    /**
     * ストリームの分割特性による性能への影響
     */
    public static void demonstrateSpliteratorCharacteristics() {
        IO.println("\n=== Spliterator Characteristics Impact ===");
        
        // ArrayList（良い分割特性）
        List<Integer> arrayList = IntStream.range(0, 100000)
            .boxed()
            .collect(Collectors.toList());
        
        // LinkedList（悪い分割特性）
        LinkedList<Integer> linkedList = IntStream.range(0, 100000)
            .boxed()
            .collect(Collectors.toCollection(LinkedList::new));
        
        // HashSet（中程度の分割特性）
        Set<Integer> hashSet = IntStream.range(0, 100000)
            .boxed()
            .collect(Collectors.toSet());
        
        // ストリームは1回しか使えないので、測るたびに作り直す
        measureCollectionPerformance("ArrayList", arrayList::parallelStream);
        measureCollectionPerformance("LinkedList", linkedList::parallelStream);
        measureCollectionPerformance("HashSet", hashSet::parallelStream);
    }
    
    private static void measureCollectionPerformance(String collectionType, Supplier<Stream<Integer>> streamSupplier) {
        long time = measureTime(() -> {
            return streamSupplier.get().filter(x -> x % 2 == 0)
                .mapToLong(Integer::longValue)
                .sum();
        });
        IO.println(collectionType + " parallel processing: %.3fms".formatted(toMillis(time)));
    }
    
    /**
     * 実行時間測定ユーティリティ
     * 数回空回ししてから（ウォームアップ）、最後の1回の時間をナノ秒で返す。
     * JITコンパイルの影響を減らすための簡易的な方法で、正確な測定にはJMHを使う
     */
    private static long measureTime(Supplier<Object> task) {
        for (int i = 0; i < 5; i++) {
            task.get();
        }
        long startTime = System.nanoTime();
        task.get();
        return System.nanoTime() - startTime;
    }
    
    private static double toMillis(long nanos) {
        return nanos / 1_000_000.0;
    }
    
    /**
     * 並列ストリームのベストプラクティス例
     */
    public static void demonstrateBestPractices() {
        IO.println("\n=== Parallel Stream Best Practices ===");
        
        List<Integer> data = IntStream.range(0, 1_000_000)
            .boxed()
            .collect(Collectors.toList());
        
        // 1. 状態を共有しない処理
        IO.println("1. Stateless operations:");
        long sum = data.parallelStream()
            .filter(x -> x % 2 == 0)
            .mapToLong(Integer::longValue)
            .sum();
        IO.println("   Sum of even numbers: " + sum);
        
        // 2. 適切なコレクター使用
        IO.println("2. Appropriate collectors:");
        Map<Boolean, Long> partitioned = data.parallelStream()
            .collect(Collectors.partitioningBy(
                x -> x % 2 == 0,
                Collectors.counting()
            ));
        IO.println("   Even: " + partitioned.get(true) + 
                         ", Odd: " + partitioned.get(false));
        
        // 3. 副作用を避ける
        IO.println("3. Avoiding side effects:");
        // 悪い例（コメントアウト）
        // List<Integer> badResults = new ArrayList<>(); // スレッドセーフでない
        // data.parallelStream().forEach(badResults::add); // 危険
        
        // 良い例
        List<Integer> goodResults = data.parallelStream()
            .filter(x -> x < 100)
            .collect(Collectors.toList()); // スレッドセーフ
        IO.println("   Collected " + goodResults.size() + " elements safely");
    }
    
    /**
     * メイン実行メソッド
     */
    public static void main(String[] args) {
        measureParallelEffectiveness();
        demonstrateCustomForkJoinPool();
        demonstrateInappropriateParallelization();
        demonstrateSpliteratorCharacteristics();
        demonstrateBestPractices();
    }

}