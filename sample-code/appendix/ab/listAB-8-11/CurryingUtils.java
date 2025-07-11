/**
 * リストAB-8
 * CurryingUtilsクラス
 * 
 * 元ファイル: appendix-b03-programming-paradigms.md (180行目)
 */

// 通常の2引数関数
BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;

// カリー化された関数
Function<Integer, Function<Integer, Integer>> curriedAdd = a -> b -> a + b;

// 部分適用の例
Function<Integer, Integer> add5 = curriedAdd.apply(5);
System.out.println(add5.apply(3)); // 8

// 汎用的なカリー化ユーティリティ
public class CurryingUtils {
    public static <A, B, C> Function<A, Function<B, C>> curry(
            BiFunction<A, B, C> biFunction) {
        return a -> b -> biFunction.apply(a, b);
    }
    
    public static <A, B, C, D> Function<A, Function<B, Function<C, D>>> curry(
            TriFunction<A, B, C, D> triFunction) {
        return a -> b -> c -> triFunction.apply(a, b, c);
    }
}

// 使用例
BiFunction<Double, Double, Double> multiply = (a, b) -> a * b;
Function<Double, Function<Double, Double>> curriedMultiply = 
    CurryingUtils.curry(multiply);

// 税率計算の部分適用
Function<Double, Double> withTax = curriedMultiply.apply(1.1);
System.out.println(withTax.apply(100.0)); // 110.0