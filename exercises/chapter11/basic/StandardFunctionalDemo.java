import java.util.*;
import java.util.function.*;

/**
 * 第11章 ラムダ式と関数型インターフェイス - 基本演習
 * 標準関数型インターフェイスの活用
 */
public class StandardFunctionalDemo {
    
    /**
     * 1. Function<T,R>の使用例
     * 文字列を受け取り、その長さを返す
     */
    public static void demonstrateFunction() {
        // TODO: Function<String, Integer>をラムダ式で実装
        Function<String, Integer> stringLength = null;
        
        // テスト
        IO.println("Length of 'Hello': " + stringLength.apply("Hello"));
    }
    
    /**
     * 2. Predicate<T>の使用例
     * 数値が偶数かどうかを判定
     */
    public static void demonstratePredicate() {
        // TODO: Predicate<Integer>をラムダ式で実装
        Predicate<Integer> isEven = null;
        
        // テスト
        IO.println("Is 4 even? " + isEven.test(4));
        IO.println("Is 5 even? " + isEven.test(5));
    }
    
    /**
     * 3. Consumer<T>の使用例
     * メッセージを装飾して出力
     */
    public static void demonstrateConsumer() {
        // TODO: Consumer<String>をラムダ式で実装
        // 「*** <メッセージ> ***」の形式で出力
        Consumer<String> printDecorated = null;
        
        // テスト
        printDecorated.accept("Important Message");
    }
    
    /**
     * 4. Supplier<T>の使用例
     * 呼ばれるたびに新しい空のリストを作る
     */
    public static void demonstrateSupplier() {
        // TODO: Supplier<List<String>>をラムダ式で実装
        // new ArrayList<>()を返す
        Supplier<List<String>> listFactory = null;
        
        // テスト
        List<String> first = listFactory.get();
        first.add("A");
        List<String> second = listFactory.get();
        IO.println("first: " + first + ", second: " + second);
    }
    
    /**
     * 5. BiFunction<T,U,R>の使用例
     * 2つの数値の最大値を返す
     */
    public static void demonstrateBiFunction() {
        // TODO: BiFunction<Integer, Integer, Integer>をラムダ式で実装
        BiFunction<Integer, Integer, Integer> max = null;
        
        // テスト
        IO.println("Max of 10 and 20: " + max.apply(10, 20));
    }
    
    /**
     * 6. UnaryOperator<T>の使用例
     * 文字列を大文字に変換
     */
    public static void demonstrateUnaryOperator() {
        // TODO: UnaryOperator<String>をラムダ式で実装
        UnaryOperator<String> toUpperCase = null;
        
        // テスト
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