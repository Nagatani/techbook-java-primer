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
        System.out.println("Length of 'Hello': " + stringLength.apply("Hello"));
    }
    
    /**
     * 2. Predicate<T>の使用例
     * 数値が偶数かどうかを判定
     */
    public static void demonstratePredicate() {
        // TODO: Predicate<Integer>をラムダ式で実装
        Predicate<Integer> isEven = null;
        
        // テスト
        System.out.println("Is 4 even? " + isEven.test(4));
        System.out.println("Is 5 even? " + isEven.test(5));
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
     * ランダムなUUIDを生成
     */
    public static void demonstrateSupplier() {
        // TODO: Supplier<String>をラムダ式で実装
        // UUID.randomUUID().toString()を返す
        Supplier<String> uuidGenerator = null;
        
        // テスト
        System.out.println("Generated UUID: " + uuidGenerator.get());
    }
    
    /**
     * 5. BiFunction<T,U,R>の使用例
     * 2つの数値の最大値を返す
     */
    public static void demonstrateBiFunction() {
        // TODO: BiFunction<Integer, Integer, Integer>をラムダ式で実装
        BiFunction<Integer, Integer, Integer> max = null;
        
        // テスト
        System.out.println("Max of 10 and 20: " + max.apply(10, 20));
    }
    
    /**
     * 6. UnaryOperator<T>の使用例
     * 文字列を大文字に変換
     */
    public static void demonstrateUnaryOperator() {
        // TODO: UnaryOperator<String>をラムダ式で実装
        UnaryOperator<String> toUpperCase = null;
        
        // テスト
        System.out.println("Uppercase: " + toUpperCase.apply("hello world"));
    }
    
    public static void main(String[] args) {
        System.out.println("=== 標準関数型インターフェイスのデモ ===");
        
        System.out.println("\n1. Function<T,R>:");
        demonstrateFunction();
        
        System.out.println("\n2. Predicate<T>:");
        demonstratePredicate();
        
        System.out.println("\n3. Consumer<T>:");
        demonstrateConsumer();
        
        System.out.println("\n4. Supplier<T>:");
        demonstrateSupplier();
        
        System.out.println("\n5. BiFunction<T,U,R>:");
        demonstrateBiFunction();
        
        System.out.println("\n6. UnaryOperator<T>:");
        demonstrateUnaryOperator();
    }
}