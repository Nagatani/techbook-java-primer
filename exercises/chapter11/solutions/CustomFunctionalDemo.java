package chapter11.solutions;

/**
 * 独自の関数型インターフェイスのデモ（課題2の解答例）
 */
public class CustomFunctionalDemo {

    public static void demonstrateStringProcessor() {
        StringProcessor toUpper = s -> s.toUpperCase();
        // メソッド参照でも書ける: StringProcessor toUpper = String::toUpperCase;
        StringProcessor reverse = s -> new StringBuilder(s).reverse().toString();
        StringProcessor addPrefix = s -> "[PROCESSED] " + s;

        String input = "  hello world  ";
        IO.println("元の文字列: '" + input + "'");

        StringProcessor trimmedUpper = toUpper.withTrim();
        IO.println("トリム後大文字: '" + trimmedUpper.process(input) + "'");

        // 大文字にする → 逆順にする → 前に付ける、の順に処理する
        StringProcessor combined = toUpper.andThen(reverse).andThen(addPrefix);
        IO.println("複合処理: '" + combined.process("hello") + "'");
    }

    public static void demonstrateCalculator() {
        Calculator add = Calculator.add();
        Calculator subtract = Calculator.subtract();
        Calculator multiply = Calculator.multiply();
        Calculator divide = Calculator.divide();

        double a = 10.0;
        double b = 5.0;

        IO.println("\n=== 計算器のデモ ===");
        IO.println(a + " + " + b + " = " + add.calculate(a, b));
        IO.println(a + " - " + b + " = " + subtract.calculate(a, b));
        IO.println(a + " * " + b + " = " + multiply.calculate(a, b));
        IO.println(a + " / " + b + " = " + divide.calculate(a, b));
        IO.println(a + " / 0 = " + divide.calculate(a, 0));

        Calculator power = (x, y) -> Math.pow(x, y);
        // メソッド参照でも書ける: Calculator power = Math::pow;
        IO.println(a + " ^ " + b + " = " + power.calculate(a, b));
    }

    public static void main(String[] args) {
        IO.println("=== StringProcessorのデモ ===");
        demonstrateStringProcessor();

        IO.println("\n=== Calculatorのデモ ===");
        demonstrateCalculator();
    }
}
