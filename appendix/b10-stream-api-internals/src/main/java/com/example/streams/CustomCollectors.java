package com.example.streams;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * カスタムコレクターの実装例
 * 効率的なデータ収集とストリームの並列処理最適化を学習
 */
public class CustomCollectors {
    
    /**
     * 効率的な文字列結合コレクター
     * StringBuilder を使用してメモリ効率を向上
     */
    public static class EfficientStringJoiner implements Collector<String, StringBuilder, String> {
        private final String delimiter;
        private final String prefix;
        private final String suffix;
        
        public EfficientStringJoiner(String delimiter, String prefix, String suffix) {
            this.delimiter = delimiter;
            this.prefix = prefix;
            this.suffix = suffix;
        }
        
        public EfficientStringJoiner(String delimiter) {
            this(delimiter, "", "");
        }
        
        @Override
        public Supplier<StringBuilder> supplier() {
            return StringBuilder::new;
        }
        
        @Override
        public BiConsumer<StringBuilder, String> accumulator() {
            return (sb, s) -> {
                if (sb.length() > 0) {
                    sb.append(delimiter);
                }
                sb.append(s);
            };
        }
        
        @Override
        public BinaryOperator<StringBuilder> combiner() {
            return (sb1, sb2) -> {
                if (sb1.length() > 0 && sb2.length() > 0) {
                    sb1.append(delimiter);
                }
                return sb1.append(sb2);
            };
        }
        
        @Override
        public Function<StringBuilder, String> finisher() {
            return sb -> prefix + sb.toString() + suffix;
        }
        
        @Override
        public Set<Characteristics> characteristics() {
            // UNORDEREDを指定して順序を気にしない最適化を許可
            return EnumSet.noneOf(Characteristics.class);
        }
    }
    
    /**
     * 並列処理対応のヒストグラムコレクター
     * スレッドセーフなConcurrentHashMapを使用
     */
    public static class ConcurrentHistogramCollector<T> 
            implements Collector<T, ConcurrentHashMap<T, Long>, Map<T, Long>> {
        
        @Override
        public Supplier<ConcurrentHashMap<T, Long>> supplier() {
            return ConcurrentHashMap::new;
        }
        
        @Override
        public BiConsumer<ConcurrentHashMap<T, Long>, T> accumulator() {
            return (map, element) -> map.merge(element, 1L, Long::sum);
        }
        
        @Override
        public BinaryOperator<ConcurrentHashMap<T, Long>> combiner() {
            return (map1, map2) -> {
                map2.forEach((key, value) -> 
                    map1.merge(key, value, Long::sum));
                return map1;
            };
        }
        
        @Override
        public Function<ConcurrentHashMap<T, Long>, Map<T, Long>> finisher() {
            return Function.identity();
        }
        
        @Override
        public Set<Characteristics> characteristics() {
            return EnumSet.of(
                Characteristics.CONCURRENT,    // 並行アキュムレーション可能
                Characteristics.UNORDERED,     // 順序不問
                Characteristics.IDENTITY_FINISH // finisherが恒等関数
            );
        }
    }
    
    /**
     * 統計情報収集コレクター
     * 一度のパスで複数の統計を計算
     */
    public static class StatisticsCollector implements Collector<Integer, StatisticsCollector.Stats, StatisticsCollector.Stats> {
        
        public static class Stats {
            private long count = 0;
            private long sum = 0;
            private int min = Integer.MAX_VALUE;
            private int max = Integer.MIN_VALUE;
            private double sumOfSquares = 0.0;
            
            public void accept(int value) {
                count++;
                sum += value;
                min = Math.min(min, value);
                max = Math.max(max, value);
                sumOfSquares += (double) value * value;
            }
            
            public void combine(Stats other) {
                count += other.count;
                sum += other.sum;
                min = Math.min(min, other.min);
                max = Math.max(max, other.max);
                sumOfSquares += other.sumOfSquares;
            }
            
            public double getAverage() {
                return count == 0 ? 0.0 : (double) sum / count;
            }
            
            public double getStandardDeviation() {
                if (count == 0) return 0.0;
                double avg = getAverage();
                return Math.sqrt(sumOfSquares / count - avg * avg);
            }
            
            @Override
            public String toString() {
                return String.format(
                    "Stats{count=%d, sum=%d, min=%d, max=%d, avg=%.2f, stddev=%.2f}",
                    count, sum, min, max, getAverage(), getStandardDeviation()
                );
            }
        }
        
        @Override
        public Supplier<Stats> supplier() {
            return Stats::new;
        }
        
        @Override
        public BiConsumer<Stats, Integer> accumulator() {
            return Stats::accept;
        }
        
        @Override
        public BinaryOperator<Stats> combiner() {
            return (stats1, stats2) -> {
                stats1.combine(stats2);
                return stats1;
            };
        }
        
        @Override
        public Function<Stats, Stats> finisher() {
            return Function.identity();
        }
        
        @Override
        public Set<Characteristics> characteristics() {
            return EnumSet.of(
                Characteristics.UNORDERED,
                Characteristics.IDENTITY_FINISH
            );
        }
    }
    
    /**
     * Top-K要素を効率的に収集するコレクター
     * 大量のデータから上位K件を効率的に抽出
     */
    public static <T> Collector<T, ?, List<T>> topK(int k, Comparator<T> comparator) {
        return Collector.of(
            () -> new PriorityQueue<>(k, comparator),
            (queue, item) -> {
                if (queue.size() < k) {
                    queue.offer(item);
                } else if (comparator.compare(item, queue.peek()) > 0) {
                    queue.poll();
                    queue.offer(item);
                }
            },
            (queue1, queue2) -> {
                queue1.addAll(queue2);
                return queue1.stream()
                    .sorted(comparator.reversed())
                    .limit(k)
                    .collect(Collectors.toCollection(() -> new PriorityQueue<>(k, comparator)));
            },
            queue -> {
                List<T> result = new ArrayList<>(queue);
                result.sort(comparator.reversed());
                return result;
            }
        );
    }
    
    /**
     * カスタムコレクターのデモンストレーション
     */
    public static void demonstrateCustomCollectors() {
        System.out.println("=== Custom Collectors Demo ===");
        
        // テストデータ
        List<String> words = Arrays.asList(
            "java", "stream", "collector", "parallel", "performance",
            "optimization", "spliterator", "lambda", "functional", "api"
        );
        
        // 1. 効率的な文字列結合
        System.out.println("1. Efficient String Joining:");
        String joined = words.stream()
            .collect(new EfficientStringJoiner(", ", "[", "]"));
        System.out.println("Joined: " + joined);
        
        // 2. ヒストグラム（文字列長の分布）
        System.out.println("\n2. Word Length Histogram:");
        Map<Integer, Long> lengthHistogram = words.stream()
            .map(String::length)
            .collect(new ConcurrentHistogramCollector<>());
        lengthHistogram.forEach((length, count) -> 
            System.out.println("Length " + length + ": " + count + " words"));
        
        // 3. 数値統計
        System.out.println("\n3. Statistics Collection:");
        List<Integer> numbers = IntStream.rangeClosed(1, 1000)
            .boxed()
            .collect(Collectors.toList());
        
        StatisticsCollector.Stats stats = numbers.stream()
            .collect(new StatisticsCollector());
        System.out.println("Sequential: " + stats);
        
        StatisticsCollector.Stats parallelStats = numbers.parallelStream()
            .collect(new StatisticsCollector());
        System.out.println("Parallel: " + parallelStats);
        
        // 4. Top-K 要素
        System.out.println("\n4. Top-K Elements:");
        List<Integer> topNumbers = numbers.stream()
            .collect(topK(5, Integer::compareTo));
        System.out.println("Top 5 numbers: " + topNumbers);
        
        List<String> longestWords = words.stream()
            .collect(topK(3, Comparator.comparing(String::length)));
        System.out.println("Top 3 longest words: " + longestWords);
    }
    
    /**
     * パフォーマンス比較
     */
    public static void performanceComparison() {
        System.out.println("\n=== Performance Comparison ===");
        
        List<String> largeDataset = IntStream.range(0, 100000)
            .mapToObj(i -> "item_" + i)
            .collect(Collectors.toList());
        
        // 標準のjoining vs カスタムコレクター
        long startTime = System.nanoTime();
        String result1 = largeDataset.stream()
            .collect(Collectors.joining(", "));
        long endTime = System.nanoTime();
        System.out.println("Standard joining: " + (endTime - startTime) / 1_000_000 + "ms");
        
        startTime = System.nanoTime();
        String result2 = largeDataset.stream()
            .collect(new EfficientStringJoiner(", "));
        endTime = System.nanoTime();
        System.out.println("Custom joining: " + (endTime - startTime) / 1_000_000 + "ms");
        
        // 結果の一致確認
        System.out.println("Results match: " + result1.equals(result2));
    }
    
    public static void main(String[] args) {
        demonstrateCustomCollectors();
        performanceComparison();
    }
}