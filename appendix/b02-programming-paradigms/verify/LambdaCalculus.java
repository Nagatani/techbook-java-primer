import java.util.function.BiFunction;
import java.util.function.Function;

/** ラムダ計算の式（抽象化・適用・ベータ簡約・カリー化・チャーチ数）をJavaで書いてみる */
public class LambdaCalculus {

    /** 3引数の関数型インターフェイス（標準ライブラリにはない） */
    @FunctionalInterface
    interface TriFunction<A, B, C, R> {
        R apply(A a, B b, C c);
    }

    static <A, B, C> Function<A, Function<B, C>> curry(BiFunction<A, B, C> f) {
        return a -> b -> f.apply(a, b);
    }

    static <A, B, C, D> Function<A, Function<B, Function<C, D>>> curry(
            TriFunction<A, B, C, D> f) {
        return a -> b -> c -> f.apply(a, b, c);
    }

    /**
     * チャーチ数：自然数nを「関数fをn回適用する関数」で表す。
     * 型パラメーターを持つメソッドなので、ラムダ式では実装できない（匿名クラスで書く）
     */
    interface ChurchNumeral {
        <T> Function<T, T> apply(Function<T, T> f);
    }

    // 0 = λf.λx.x
    static final ChurchNumeral ZERO = new ChurchNumeral() {
        @Override
        public <T> Function<T, T> apply(Function<T, T> f) {
            return x -> x;
        }
    };

    // succ = λn.λf.λx.f (n f x)
    static ChurchNumeral succ(ChurchNumeral n) {
        return new ChurchNumeral() {
            @Override
            public <T> Function<T, T> apply(Function<T, T> f) {
                return x -> f.apply(n.apply(f).apply(x));
            }
        };
    }

    // plus = λm.λn.λf.λx.m f (n f x)
    static ChurchNumeral plus(ChurchNumeral m, ChurchNumeral n) {
        return new ChurchNumeral() {
            @Override
            public <T> Function<T, T> apply(Function<T, T> f) {
                return x -> m.apply(f).apply(n.apply(f).apply(x));
            }
        };
    }

    // times = λm.λn.λf.m (n f)
    static ChurchNumeral times(ChurchNumeral m, ChurchNumeral n) {
        return new ChurchNumeral() {
            @Override
            public <T> Function<T, T> apply(Function<T, T> f) {
                return m.apply(n.apply(f));
            }
        };
    }

    /** チャーチ数を、0に「1を足す関数」をn回適用してintに直す */
    static int toInt(ChurchNumeral n) {
        return n.<Integer>apply(x -> x + 1).apply(0);
    }

    public static void main(String[] args) {
        // λx.x と λx.λy.x
        Function<Object, Object> identity = x -> x;
        Function<Object, Function<Object, Object>> constant = x -> y -> x;
        IO.println("(λx.x) 5 = " + identity.apply(5));
        IO.println("(λx.λy.x) 1 2 = " + constant.apply(1).apply(2));

        // (λf.λx.f (f x)) (λy.y + 1) 5 → 7
        Function<Function<Integer, Integer>, Function<Integer, Integer>> twice =
            f -> x -> f.apply(f.apply(x));
        Function<Integer, Integer> increment = y -> y + 1;
        IO.println("twice(increment)(5) = " + twice.apply(increment).apply(5));

        // カリー化と部分適用
        Function<Integer, Function<Integer, Integer>> curriedAdd = a -> b -> a + b;
        Function<Integer, Integer> add5 = curriedAdd.apply(5);
        IO.println("add5(3) = " + add5.apply(3));
        Function<Integer, Integer> triple = curry((Integer a, Integer b) -> a * b).apply(3);
        IO.println("triple(100) = " + triple.apply(100));
        TriFunction<Integer, Integer, Integer, Integer> volume = (w, d, h) -> w * d * h;
        IO.println("curry(volume)(2)(3)(4) = " + curry(volume).apply(2).apply(3).apply(4));

        // チャーチ数
        ChurchNumeral one = succ(ZERO);
        ChurchNumeral two = succ(one);
        ChurchNumeral three = succ(two);
        IO.println("toInt(zero) = " + toInt(ZERO));
        IO.println("toInt(succ(two)) = " + toInt(three));
        IO.println("toInt(plus(two, three)) = " + toInt(plus(two, three)));
        IO.println("toInt(times(two, three)) = " + toInt(times(two, three)));
        // 同じチャーチ数を、文字列に"*"を足す関数にも使える
        IO.println("three(s -> s + \"*\")(\"\") = " + three.<String>apply(s -> s + "*").apply(""));
    }
}
