package chapter10.solutions;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;

/**
 * カスタムCollectorの実装例
 */
public class CustomCollector {
    
    /**
     * 文字列を区切り文字付きで結合するCollector
     */
    public static Collector<String, ?, String> joining(String delimiter, String prefix, String suffix) {
        return Collector.of(
            StringJoiner::new,
            (joiner, str) -> joiner.add(str),
            StringJoiner::merge,
            joiner -> prefix + joiner.toString() + suffix
        );
    }
    
    /**
     * 統計情報を収集するCollector
     */
    public static <T> Collector<T, ?, Map<String, Object>> statistics(
            Function<T, Double> valueExtractor) {
        
        return Collector.of(
            () -> new DoubleSummaryStatistics(),
            (stats, item) -> stats.accept(valueExtractor.apply(item)),
            (stats1, stats2) -> {
                stats1.combine(stats2);
                return stats1;
            },
            stats -> {
                Map<String, Object> result = new HashMap<>();
                result.put("count", stats.getCount());
                result.put("sum", stats.getSum());
                result.put("min", stats.getMin());
                result.put("max", stats.getMax());
                result.put("average", stats.getAverage());
                return result;
            }
        );
    }
    
    /**
     * 上位N件を収集するCollector
     */
    public static <T> Collector<T, ?, List<T>> topN(int n, Comparator<T> comparator) {
        return Collector.of(
            () -> new PriorityQueue<>(comparator.reversed()),
            (queue, item) -> {
                queue.offer(item);
                if (queue.size() > n) {
                    queue.poll();
                }
            },
            (queue1, queue2) -> {
                queue1.addAll(queue2);
                while (queue1.size() > n) {
                    queue1.poll();
                }
                return queue1;
            },
            queue -> {
                List<T> result = new ArrayList<>(queue);
                result.sort(comparator.reversed());
                return result;
            }
        );
    }
    
    /**
     * 条件で分割してグループ化するCollector
     */
    public static <T> Collector<T, ?, Map<Boolean, List<T>>> partitioning(
            Predicate<T> predicate) {
        
        return Collector.of(
            () -> {
                Map<Boolean, List<T>> map = new HashMap<>();
                map.put(true, new ArrayList<>());
                map.put(false, new ArrayList<>());
                return map;
            },
            (map, item) -> map.get(predicate.test(item)).add(item),
            (map1, map2) -> {
                map1.get(true).addAll(map2.get(true));
                map1.get(false).addAll(map2.get(false));
                return map1;
            }
        );
    }
    
    /**
     * 頻度をカウントするCollector
     */
    public static <T> Collector<T, ?, Map<T, Long>> frequency() {
        return Collector.of(
            HashMap::new,
            (map, item) -> map.merge(item, 1L, Long::sum),
            (map1, map2) -> {
                map2.forEach((key, value) -> map1.merge(key, value, Long::sum));
                return map1;
            }
        );
    }
    
    /**
     * ImmutableListを作成するCollector
     */
    public static <T> Collector<T, ?, List<T>> toImmutableList() {
        return Collector.of(
            ArrayList::new,
            List::add,
            (list1, list2) -> {
                list1.addAll(list2);
                return list1;
            },
            Collections::unmodifiableList
        );
    }
    
    /**
     * 範囲別にグループ化するCollector
     */
    public static <T> Collector<T, ?, Map<String, List<T>>> groupingByRange(
            Function<T, Double> valueExtractor, double... ranges) {
        
        return Collector.of(
            HashMap::new,
            (map, item) -> {
                double value = valueExtractor.apply(item);
                String range = findRange(value, ranges);
                map.computeIfAbsent(range, k -> new ArrayList<>()).add(item);
            },
            (map1, map2) -> {
                map2.forEach((key, list) -> 
                    map1.computeIfAbsent(key, k -> new ArrayList<>()).addAll(list));
                return map1;
            }
        );
    }
    
    private static String findRange(double value, double[] ranges) {
        for (int i = 0; i < ranges.length - 1; i++) {
            if (value >= ranges[i] && value < ranges[i + 1]) {
                return String.format("%.1f-%.1f", ranges[i], ranges[i + 1]);
            }
        }
        return "範囲外";
    }
    
    /**
     * 最頻値（モード）を求めるCollector
     */
    public static <T> Collector<T, ?, Optional<T>> mode() {
        return Collector.of(
            HashMap::new,
            (map, item) -> map.merge(item, 1L, Long::sum),
            (map1, map2) -> {
                map2.forEach((key, value) -> map1.merge(key, value, Long::sum));
                return map1;
            },
            map -> map.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
        );
    }
}