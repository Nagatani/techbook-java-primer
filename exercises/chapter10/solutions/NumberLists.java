package chapter10.solutions;

import java.util.ArrayList;
import java.util.List;

/**
 * 数値のリストとワイルドカード（課題3の解答例）
 *
 * 値を取り出すだけなら? extends、入れるだけなら? superを使います。
 */
public class NumberLists {

    /**
     * List<Integer>・List<Double>・List<Number>のどれでも合計を返す。
     * 要素はNumberとして取り出せるが、要素を加えることはできない
     */
    public static double sum(List<? extends Number> list) {
        double total = 0.0;
        for (Number n : list) {
            total += n.doubleValue();
        }
        // list.add(1); // 何の型のリストかわからないので、コンパイルエラーになる
        // 第12章のStream APIを使うと、次のように書ける
        // return list.stream().mapToDouble(Number::doubleValue).sum();
        return total;
    }

    /**
     * 1からcountまでの2乗をdestに加える。Integerを入れられるリスト
     * （List<Integer>・List<Number>・List<Object>）なら何でも受け取る
     */
    public static void addSquares(List<? super Integer> dest, int count) {
        for (int i = 1; i <= count; i++) {
            dest.add(i * i);
        }
    }

    public static void main(String[] args) {
        List<Integer> ints = List.of(1, 2, 3, 4, 5);
        List<Double> doubles = List.of(1.5, 2.5, 3.5);
        IO.println("整数 " + ints + " の合計: " + sum(ints));
        IO.println("小数 " + doubles + " の合計: " + sum(doubles));

        List<Number> numbers = new ArrayList<>();
        addSquares(numbers, 3);
        numbers.add(0.5);
        IO.println("List<Number>に追加: " + numbers);

        List<Object> objects = new ArrayList<>();
        objects.add("平方数:");
        addSquares(objects, 4);
        IO.println("List<Object>に追加: " + objects);

        IO.println("List<Number>の合計: " + sum(numbers));
    }
}
