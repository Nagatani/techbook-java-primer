/**
 * 第2章 基本課題6: staticメソッドの定義
 *
 * 課題: 次の3つのstaticメソッドを定義し、mainメソッドから呼び出してください。
 *
 * 要求仕様:
 * - max(int a, int b): 大きいほうの値を返す
 * - isEven(int n): 偶数ならtrue、奇数ならfalseを返す
 * - celsiusToFahrenheit(double celsius): 摂氏を華氏に変換して返す（華氏 = 摂氏 × 9 / 5 + 32）
 *
 * 実行例:
 * === staticメソッドの練習 ===
 * max(12, 30) = 30
 * 7は偶数か: false
 * 10は偶数か: true
 * 摂氏25.0度は華氏77.0度
 *
 * 評価ポイント:
 * - 戻り値の型と引数を正しく宣言できる
 * - returnで値を返せる
 * - 同じ処理をメソッドにまとめて何度も呼び出せる
 */
public class Exercise06_StaticMethods {
    public static void main(String[] args) {
        IO.println("=== staticメソッドの練習 ===");
        // TODO: 下で定義したメソッドを呼び出して、実行例のように表示してください
        // 例: IO.println("max(12, 30) = " + max(12, 30));
    }

    // TODO: maxメソッドを定義してください
    // static int max(int a, int b) { ... }

    // TODO: isEvenメソッドを定義してください

    // TODO: celsiusToFahrenheitメソッドを定義してください
}
