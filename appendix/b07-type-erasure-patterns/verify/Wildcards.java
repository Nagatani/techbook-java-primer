import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ワイルドカードで、受け取れる型の範囲を広げる。
 *   値を取り出すだけの引数（生産者）は ? extends T
 *   値を入れるだけの引数（消費者）は ? super T
 *
 *   javac Wildcards.java && java Wildcards
 *   javap java.util.Collections | grep ' copy('     標準ライブラリの宣言
 */
public class Wildcards {
    // 取り出すだけなので、List<Integer>もList<Double>も受け取れる
    static double sum(List<? extends Number> numbers) {
        double total = 0;
        for (Number n : numbers) {
            total += n.doubleValue();
        }
        return total;
    }

    // 入れるだけなので、List<Integer>もList<Number>もList<Object>も受け取れる
    static void addSquares(List<? super Integer> out, int count) {
        for (int i = 1; i <= count; i++) {
            out.add(i * i);
        }
    }

    public static void main(String[] args) {
        IO.println("sum(List<Integer>) = " + sum(List.of(1, 2, 3)));
        IO.println("sum(List<Double>) = " + sum(List.of(1.5, 2.5)));

        List<Number> numbers = new ArrayList<>();
        List<Object> objects = new ArrayList<>();
        addSquares(numbers, 3);
        addSquares(objects, 2);
        IO.println("List<Number>: " + numbers + " / List<Object>: " + objects);

        // 標準ライブラリのcopyも同じ形: copy(List<? super T> dest, List<? extends T> src)
        List<Object> dest = new ArrayList<>(List.of("-", "-", "-"));
        Collections.copy(dest, List.of(10, 20));
        IO.println("Collections.copy: " + dest);
    }
}
