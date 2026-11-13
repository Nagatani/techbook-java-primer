package chapter11.solutions;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * 解答例の動作確認（課題1〜3）
 *
 * 期待する値と実際の値を比べ、「OK」か「NG」を表示します。
 * 解答例を書き換えたら実行し、すべてOKになることを確かめます。
 * 第21章で学ぶJUnitを使うと、同じ確認をテストとして書けます。
 */
public class SolutionCheck {
    private static int total = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        IO.println("=== 第11章 解答例の動作確認 ===");

        StringProcessor toUpper = s -> s.toUpperCase();
        StringProcessor addPrefix = s -> "[PROCESSED] " + s;
        check("withTrimは前後の空白を除いてから処理する", "HELLO WORLD",
                toUpper.withTrim().process("  hello world  "));
        check("andThenは自分の結果を次の処理に渡す", "[PROCESSED] HELLO",
                toUpper.andThen(addPrefix).process("hello"));

        check("足し算", 15.0, Calculator.add().calculate(10, 5));
        check("引き算", 5.0, Calculator.subtract().calculate(10, 5));
        check("掛け算", 50.0, Calculator.multiply().calculate(10, 5));
        check("割り算", 2.0, Calculator.divide().calculate(10, 5));
        check("0で割るとNaN", true, Double.isNaN(Calculator.divide().calculate(10, 0)));

        Predicate<String> condition = MethodRefs.shortAndNotStartingWithD();
        check("短くてdで始まらない語は条件に合う", true, condition.test("kiwi"));
        check("dで始まる語は合わない", false, condition.test("date"));
        check("長い語は合わない", false, condition.test("apple"));
        check("長さの2倍", 12, MethodRefs.doubledLength().apply("banana"));

        IO.println();
        IO.println(total + "件中" + (total - failed) + "件OK");
    }

    private static void check(String label, Object expected, Object actual) {
        total++;
        if (Objects.equals(expected, actual)) {
            IO.println("OK  " + label);
        } else {
            failed++;
            IO.println("NG  " + label + "（期待: " + expected + "、実際: " + actual + "）");
        }
    }
}
