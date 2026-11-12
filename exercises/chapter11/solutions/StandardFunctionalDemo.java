package chapter11.solutions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * 標準の関数型インターフェイス（課題1の解答例）
 *
 * 引数の型は代入先の型から推論されるので、ラムダ式では省略しています。
 */
public class StandardFunctionalDemo {

    public static void demonstrateFunction() {
        Function<String, Integer> stringLength = s -> s.length();
        // メソッド参照でも書ける: Function<String, Integer> stringLength = String::length;
        IO.println("Length of 'Hello': " + stringLength.apply("Hello"));
    }

    public static void demonstratePredicate() {
        Predicate<Integer> isEven = n -> n % 2 == 0;
        IO.println("Is 4 even? " + isEven.test(4));
        IO.println("Is 5 even? " + isEven.test(5));
    }

    public static void demonstrateConsumer() {
        Consumer<String> printDecorated = message -> IO.println("*** " + message + " ***");
        printDecorated.accept("Important Message");
    }

    public static void demonstrateSupplier() {
        // getを呼ぶたびにラムダ式の本体が実行されるので、毎回新しいリストができる
        Supplier<List<String>> listFactory = () -> new ArrayList<>();
        // コンストラクター参照でも書ける: Supplier<List<String>> listFactory = ArrayList::new;
        List<String> first = listFactory.get();
        first.add("A");
        List<String> second = listFactory.get();
        IO.println("first: " + first + ", second: " + second);
    }

    public static void demonstrateBiFunction() {
        BiFunction<Integer, Integer, Integer> max = (a, b) -> a >= b ? a : b;
        // メソッド参照でも書ける: BiFunction<Integer, Integer, Integer> max = Math::max;
        IO.println("Max of 10 and 20: " + max.apply(10, 20));
    }

    public static void demonstrateUnaryOperator() {
        // UnaryOperator<String>は、引数と戻り値が同じ型のFunction<String, String>
        UnaryOperator<String> toUpperCase = s -> s.toUpperCase();
        IO.println("Uppercase: " + toUpperCase.apply("hello world"));
    }

    public static void main(String[] args) {
        IO.println("=== 標準関数型インターフェイスのデモ ===");

        IO.println("\n1. Function<T,R>:");
        demonstrateFunction();

        IO.println("\n2. Predicate<T>:");
        demonstratePredicate();

        IO.println("\n3. Consumer<T>:");
        demonstrateConsumer();

        IO.println("\n4. Supplier<T>:");
        demonstrateSupplier();

        IO.println("\n5. BiFunction<T,U,R>:");
        demonstrateBiFunction();

        IO.println("\n6. UnaryOperator<T>:");
        demonstrateUnaryOperator();
    }
}
