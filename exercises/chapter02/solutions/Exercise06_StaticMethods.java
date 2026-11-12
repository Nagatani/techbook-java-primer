/**
 * 第2章 課題6: staticメソッドの定義 - 解答例
 *
 * 学習ポイント:
 * - 戻り値の型・メソッド名・引数の宣言
 * - returnで値を返す
 * - mainメソッドから同じクラスのstaticメソッドを呼び出す
 */
public class Exercise06_StaticMethods {
    public static void main(String[] args) {
        IO.println("=== staticメソッドの練習 ===");
        IO.println("max(12, 30) = " + max(12, 30));
        IO.println("7は偶数か: " + isEven(7));
        IO.println("10は偶数か: " + isEven(10));
        double celsius = 25.0;
        IO.println("摂氏" + celsius + "度は華氏" + celsiusToFahrenheit(celsius) + "度");
    }

    /**
     * 大きいほうの値を返す
     */
    static int max(int a, int b) {
        if (a > b) {
            return a;
        }
        return b;
    }

    /**
     * 偶数ならtrueを返す
     */
    static boolean isEven(int n) {
        return n % 2 == 0;
    }

    /**
     * 摂氏を華氏に変換する
     * 9 / 5 と書くと整数の割り算で1になるため、9.0 / 5 と書く
     */
    static double celsiusToFahrenheit(double celsius) {
        return celsius * 9.0 / 5 + 32;
    }
}
