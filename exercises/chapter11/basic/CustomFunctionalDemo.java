import java.util.*;

/**
 * 第11章 ラムダ式と関数型インターフェイス - 基本演習
 * カスタム関数型インターフェイスのデモ
 */
public class CustomFunctionalDemo {
    
    /**
     * StringProcessorの使用例
     */
    public static void demonstrateStringProcessor() {
        // TODO: ラムダ式を使用して各処理を実装
        
        // 1. 大文字に変換するプロセッサ
        StringProcessor toUpper = null;
        
        // 2. 文字列を逆順にするプロセッサ
        StringProcessor reverse = null;
        
        // 3. プレフィックスを追加するプロセッサ
        StringProcessor addPrefix = s -> "[PROCESSED] " + s;
        
        // テスト
        String input = "  hello world  ";
        System.out.println("元の文字列: '" + input + "'");
        
        // withTrim()を使用
        if (toUpper != null) {
            StringProcessor trimmedUpper = toUpper.withTrim();
            System.out.println("トリム後大文字: '" + trimmedUpper.process(input) + "'");
        }
        
        // andThen()を使用
        if (toUpper != null && reverse != null) {
            StringProcessor combined = toUpper.andThen(reverse).andThen(addPrefix);
            System.out.println("複合処理: '" + combined.process("hello") + "'");
        }
    }
    
    /**
     * Calculatorの使用例
     */
    public static void demonstrateCalculator() {
        // 各種計算器の取得
        Calculator add = Calculator.add();
        Calculator subtract = Calculator.subtract();
        Calculator multiply = Calculator.multiply();
        Calculator divide = Calculator.divide();
        
        double a = 10.0;
        double b = 5.0;
        
        System.out.println("\n=== 計算器のデモ ===");
        if (add != null) {
            System.out.println(a + " + " + b + " = " + add.calculate(a, b));
        }
        if (subtract != null) {
            System.out.println(a + " - " + b + " = " + subtract.calculate(a, b));
        }
        if (multiply != null) {
            System.out.println(a + " * " + b + " = " + multiply.calculate(a, b));
        }
        if (divide != null) {
            System.out.println(a + " / " + b + " = " + divide.calculate(a, b));
            System.out.println(a + " / 0 = " + divide.calculate(a, 0));
        }
        
        // TODO: カスタム計算器の作成（べき乗）
        Calculator power = null; // Math.powを使用
        if (power != null) {
            System.out.println(a + " ^ " + b + " = " + power.calculate(a, b));
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== StringProcessorのデモ ===");
        demonstrateStringProcessor();
        
        System.out.println("\n=== Calculatorのデモ ===");
        demonstrateCalculator();
    }
}