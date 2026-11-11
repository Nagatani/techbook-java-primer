import java.util.function.*;

public class StandardFunctionalInterfaces {
    public static void main(String[] args) {
        // Predicate: 文字列が空かどうかを判定
        Predicate<String> isEmpty = s -> s.isEmpty();
        IO.println("''は空？: " + isEmpty.test("")); // true
        IO.println("'Java'は空？: " + isEmpty.test("Java")); // false

        // Function: 文字列を長さに変換
        Function<String, Integer> getLength = s -> s.length();
        IO.println("'Java'の長さ: " + getLength.apply("Java")); // 4
        IO.println("'Hello World'の長さ: " + getLength.apply("Hello World")); // 11

        // Consumer: 文字列を大文字で出力
        Consumer<String> printUpper = s -> IO.println(s.toUpperCase());
        IO.print("'hello'を大文字で出力: ");
        printUpper.accept("hello"); // HELLO

        // Supplier: 現在時刻を供給
        Supplier<Long> currentTime = () -> System.currentTimeMillis();
        IO.println("現在時刻: " + currentTime.get());
        
        // UnaryOperator: 同じ型の変換
        UnaryOperator<String> addPrefix = s -> "Hello, " + s;
        IO.println("挨拶付き: " + addPrefix.apply("Java"));
        
        // BinaryOperator: 同じ型の2つの値から1つの値を生成
        BinaryOperator<Integer> multiply = (a, b) -> a * b;
        IO.println("5 × 7 = " + multiply.apply(5, 7));
    }
}
