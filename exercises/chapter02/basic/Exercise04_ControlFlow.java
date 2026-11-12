/**
 * 第2章 基本課題4: 条件分岐と繰り返し
 *
 * 課題: 点数から成績を判定する処理と、1から15までの数を条件に応じて表示する処理を作成してください。
 *
 * 要求仕様:
 * - 点数（int型）から、90点以上はA、80点以上はB、70点以上はC、60点以上はD、それ未満はFと判定する（if-else if）
 * - 判定した成績から、Aは「優秀」、BとCは「合格」、Dは「再確認」、Fは「不合格」というコメントを選ぶ（switch文またはswitch式）
 * - for文で1から15までの数を順に表示する。3の倍数は「Fizz」、5の倍数は「Buzz」、両方の倍数は「FizzBuzz」と表示する
 *
 * 実行例:
 * === 成績判定 ===
 * 85点: B（合格）
 * 92点: A（優秀）
 * 58点: F（不合格）
 *
 * === FizzBuzz ===
 * 1
 * 2
 * Fizz
 * 4
 * Buzz
 * ...
 * 14
 * FizzBuzz
 *
 * 評価ポイント:
 * - 条件を判定する順序が正しい（大きい点数から調べる）
 * - switchで文字列ごとの分岐が書ける
 * - %演算子で倍数かどうかを調べられる
 */
public class Exercise04_ControlFlow {
    public static void main(String[] args) {
        IO.println("=== 成績判定 ===");
        int[] scores = {85, 92, 58};
        for (int score : scores) {
            String grade = "F";
            // TODO: scoreからgradeを判定してください（if-else if）

            String comment = "";
            // TODO: gradeからcommentを選んでください（switch文またはswitch式）

            IO.println(score + "点: " + grade + "（" + comment + "）");
        }

        IO.println();
        IO.println("=== FizzBuzz ===");
        // TODO: for文で1から15までの数を表示してください
        // ヒント: 両方の倍数（15の倍数）を最初に調べます
    }
}
