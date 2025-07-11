/**
 * リストAB-18
 * HigherOrderFunctionsクラス
 * 
 * 元ファイル: appendix-b03-programming-paradigms.md (749行目)
 */

public class HigherOrderFunctions {
    
    // 関数を返す関数
    public static Function<Integer, Integer> multiplier(int factor) {
        return x -> x * factor;
    }
    
    // 関数を引数に取る関数
    public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        return list.stream()
            .filter(predicate)
            .collect(Collectors.toList());
    }
    
    // 関数の合成を行う関数
    public static <A, B, C> Function<A, C> compose(
            Function<B, C> f, 
            Function<A, B> g) {
        return x -> f.apply(g.apply(x));
    }
    
    // メモ化（記憶化）
    public static <T, R> Function<T, R> memoize(Function<T, R> f) {
        Map<T, R> cache = new ConcurrentHashMap<>();
        return x -> cache.computeIfAbsent(x, f);
    }
    
    public static void main(String[] args) {
        // 使用例
        Function<Integer, Integer> times3 = multiplier(3);
        System.out.println(times3.apply(5)); // 15
        
        List<Integer> evens = filter(List.of(1, 2, 3, 4, 5), x -> x % 2 == 0);
        System.out.println(evens); // [2, 4]
        
        // 高価な計算のメモ化
        Function<Integer, Integer> expensiveFunction = x -> {
            System.out.println("Computing for " + x);
            return x * x * x;
        };
        
        Function<Integer, Integer> memoized = memoize(expensiveFunction);
        
        System.out.println(memoized.apply(5)); // Computing for 5 → 125
        System.out.println(memoized.apply(5)); // 125 (キャッシュから)
    }
}