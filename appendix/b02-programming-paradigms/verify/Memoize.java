import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/** 高階関数によるメモ化と、computeIfAbsentを再帰で使ったときの落とし穴 */
public class Memoize {

    static <T, R> Function<T, R> memoize(Function<T, R> f) {
        Map<T, R> cache = new ConcurrentHashMap<>();
        return x -> cache.computeIfAbsent(x, f);
    }

    static Function<Integer, Integer> multiplier(int factor) {
        return x -> x * factor;
    }

    static final Map<Integer, Long> CHM = new ConcurrentHashMap<>();
    static final Map<Integer, Long> HASH = new HashMap<>();

    /** マップを引く関数の中で、同じマップにcomputeIfAbsentする（してはいけない例） */
    static long fib(Map<Integer, Long> memo, int n) {
        if (n < 2) {
            return n;
        }
        return memo.computeIfAbsent(n, k -> fib(memo, k - 1) + fib(memo, k - 2));
    }

    /** 再帰ではなく、小さいほうから順に表を埋める */
    static long fibTable(int n) {
        long a = 0;
        long b = 1;
        for (int i = 0; i < n; i++) {
            long next = a + b;
            a = b;
            b = next;
        }
        return a;
    }

    static void tryFib(String label, Map<Integer, Long> memo) {
        try {
            IO.println(label + ": fib(30) = " + fib(memo, 30));
        } catch (RuntimeException e) {
            IO.println(label + ": " + e.getClass().getName()
                + (e.getMessage() == null ? "" : ": " + e.getMessage()));
        }
    }

    public static void main(String[] args) {
        IO.println("multiplier(3).apply(5) = " + multiplier(3).apply(5));

        Function<Integer, Integer> cube = x -> {
            IO.println("Computing for " + x);
            return x * x * x;
        };
        Function<Integer, Integer> memoized = memoize(cube);
        IO.println(memoized.apply(5));
        IO.println(memoized.apply(5));
        IO.println(List.of(1, 2, 3, 4, 5).stream().filter(x -> x % 2 == 0).toList());

        tryFib("ConcurrentHashMap", CHM);
        tryFib("HashMap", HASH);
        IO.println("表を埋める: fib(30) = " + fibTable(30));
    }
}
