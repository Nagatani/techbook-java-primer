package chapter12.solutions;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 数のリストのパイプライン（課題3の解答例）
 *
 * map・filterに加えて、partitioningBy・reduce・flatMap・anyMatch・allMatch・findFirstを使います。
 */
public class NumberPipeline {

    /**
     * 偶数だけを2乗して、文字列にする
     */
    public static List<String> evenSquares(List<Integer> numbers) {
        return numbers.stream()
                .filter(n -> n % 2 == 0)
                .map(n -> n * n)
                .map(String::valueOf)
                .collect(Collectors.toList());
    }

    /**
     * 偶数（true）と奇数（false）の2つのグループに分ける。キーはtrueとfalseの2つだけ
     */
    public static Map<Boolean, List<Integer>> partitionByEven(List<Integer> numbers) {
        return numbers.stream()
                .collect(Collectors.partitioningBy(n -> n % 2 == 0));
    }

    /**
     * reduceで合計を求める。0が初期値で、要素を1つずつ足していく
     */
    public static int sum(List<Integer> numbers) {
        return numbers.stream().reduce(0, Integer::sum);
        // IntStreamにして合計してもよい: numbers.stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * 入れ子のリストを、flatMapで1つのリストにならす
     */
    public static List<Integer> flatten(List<List<Integer>> nested) {
        return nested.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * limitより大きい最初の数。ないかもしれないのでOptionalで返す
     */
    public static Optional<Integer> firstGreaterThan(List<Integer> numbers, int limit) {
        return numbers.stream()
                .filter(n -> n > limit)
                .findFirst();
    }

    public static void main(String[] args) {
        // IntStream.rangeClosed(1, 10)は1から10までの整数。boxedでIntegerのストリームにする
        List<Integer> numbers = IntStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());
        IO.println("入力: " + numbers);

        IO.println("偶数の2乗（文字列）: " + evenSquares(numbers));

        Map<Boolean, List<Integer>> groups = partitionByEven(numbers);
        IO.println("奇数: " + groups.get(false) + " 合計 " + sum(groups.get(false)));
        IO.println("偶数: " + groups.get(true) + " 合計 " + sum(groups.get(true)));

        int product = numbers.stream().limit(5).reduce(1, (a, b) -> a * b);
        IO.println("1から5の積: " + product);

        List<List<Integer>> nested = List.of(List.of(1, 2), List.of(3), List.of(), List.of(4, 5, 6));
        IO.println("入れ子のリスト " + nested + " をならす: " + flatten(nested));

        IO.println("7の倍数がある？: " + numbers.stream().anyMatch(n -> n % 7 == 0));
        IO.println("すべて正の数？: " + numbers.stream().allMatch(n -> n > 0));

        IO.println("8より大きい最初の数: " + firstGreaterThan(numbers, 8).orElse(-1));
        IO.println("20より大きい最初の数: "
                + firstGreaterThan(numbers, 20).map(String::valueOf).orElse("なし"));
    }
}
