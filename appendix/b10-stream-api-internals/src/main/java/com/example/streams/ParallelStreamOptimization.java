package com.example.streams;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
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
        System.out.println("=== Parallel Stream Effectiveness by Data Size ===");
        
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
            System.out.printf("Size: %7d | Sequential: %4dms | Parallel: %4dms | Speedup: %.2fx%n",
                size, sequentialTime, parallelTime, speedup);
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
        System.out.println("\n=== Custom ForkJoinPool Configuration ===");
        
        List<Integer> data = IntStream.range(0, 10000)
            .boxed()
            .collect(Collectors.toList());
        
        // デフォルトプール（通常はCPUコア数）
        int defaultPoolSize = ForkJoinPool.commonPool().getParallelism();
        System.out.println("Default pool parallelism: " + defaultPoolSize);
        
        long defaultTime = measureTime(() -> {
            return data.parallelStream()
                .filter(ParallelStreamOptimization::isPrime)
                .count();
        });
        System.out.println("Default pool time: " + defaultTime + "ms");
        
        // カスタムプール（コア数の2倍）
        int customPoolSize = Math.max(2, defaultPoolSize * 2);
        ForkJoinPool customPool = new ForkJoinPool(customPoolSize);
        
        try {
            long customTime = measureTime(() -> {
                return customPool.submit(() ->
                    data.parallelStream()
                        .filter(ParallelStreamOptimization::isPrime)
                        .count()
                ).get();
            });
            System.out.println("Custom pool (" + customPoolSize + " threads) time: " + customTime + "ms");
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            customPool.shutdown();
        }
    }
    
    /**
     * 並列化に適さないケースの例
     */
    public static void demonstrateInappropriateParallelization() {
        System.out.println("\n=== Inappropriate Parallelization Examples ===");
        
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
        
        System.out.println("Small dataset (5 elements):");
        System.out.println("  Sequential: " + smallSeqTime + "ms");
        System.out.println("  Parallel: " + smallParTime + "ms");
        System.out.println("  Parallel overhead: " + (smallParTime > smallSeqTime ? "Yes" : "No"));
        
        // 2. 順序依存の処理
        List<Integer> numbers = IntStream.range(0, 1000).boxed().collect(Collectors.toList());
        
        List<Integer> sequentialResult = numbers.stream()
            .limit(100)
            .collect(Collectors.toList());
        
        List<Integer> parallelResult = numbers.parallelStream()
            .limit(100)
            .collect(Collectors.toList());
        
        System.out.println("\nOrder-dependent operations:");
        System.out.println("  Sequential and parallel results equal: " + 
            sequentialResult.equals(parallelResult));
        
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
        
        System.out.println("I/O-bound task simulation:");
        System.out.println("  Sequential: " + ioSeqTime + "ms");
        System.out.println("  Parallel: " + ioParTime + "ms");
        System.out.println("  Speedup: " + String.format("%.2f", (double) ioSeqTime / ioParTime) + "x");
    }
    
    /**
     * ストリームの分割特性による性能への影響
     */
    public static void demonstrateSpliteratorCharacteristics() {
        System.out.println("\n=== Spliterator Characteristics Impact ===");
        
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
        
        measureCollectionPerformance("ArrayList", arrayList.parallelStream());
        measureCollectionPerformance("LinkedList", linkedList.parallelStream());
        measureCollectionPerformance("HashSet", hashSet.parallelStream());
    }
    
    private static void measureCollectionPerformance(String collectionType, Stream<Integer> stream) {
        long time = measureTime(() -> {
            return stream.filter(x -> x % 2 == 0)
                .mapToLong(Integer::longValue)
                .sum();
        });
        System.out.println(collectionType + " parallel processing: " + time + "ms");
    }
    
    /**
     * 実行時間測定ユーティリティ
     */
    private static long measureTime(Supplier<Object> task) {
        long startTime = System.currentTimeMillis();
        task.get();
        return System.currentTimeMillis() - startTime;
    }
    
    /**
     * 並列ストリームのベストプラクティス例
     */
    public static void demonstrateBestPractices() {
        System.out.println("\n=== Parallel Stream Best Practices ===");
        
        List<Integer> data = IntStream.range(0, 1_000_000)
            .boxed()
            .collect(Collectors.toList());
        
        // 1. 状態を共有しない処理
        System.out.println("1. Stateless operations:");
        long sum = data.parallelStream()
            .filter(x -> x % 2 == 0)
            .mapToLong(Integer::longValue)
            .sum();
        System.out.println("   Sum of even numbers: " + sum);
        
        // 2. 適切なコレクター使用
        System.out.println("2. Appropriate collectors:");
        Map<Boolean, Long> partitioned = data.parallelStream()
            .collect(Collectors.partitioningBy(
                x -> x % 2 == 0,
                Collectors.counting()
            ));
        System.out.println("   Even: " + partitioned.get(true) + 
                         ", Odd: " + partitioned.get(false));
        
        // 3. 副作用を避ける
        System.out.println("3. Avoiding side effects:");
        // 悪い例（コメントアウト）
        // List<Integer> badResults = new ArrayList<>(); // スレッドセーフでない
        // data.parallelStream().forEach(badResults::add); // 危険
        
        // 良い例
        List<Integer> goodResults = data.parallelStream()
            .filter(x -> x < 100)
            .collect(Collectors.toList()); // スレッドセーフ
        System.out.println("   Collected " + goodResults.size() + " elements safely");
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
    
    // Supplier のインポート
    private static interface Supplier<T> {
        T get();
    }
    
    // Function のインポート
    private static interface Function<T, R> {
        R apply(T t);
    }
}