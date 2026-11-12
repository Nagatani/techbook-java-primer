/**
 * 第2章 課題4: 条件分岐と繰り返し - 解答例
 *
 * 学習ポイント:
 * - if-else ifによる範囲の判定（大きい値から順に調べる）
 * - switch式による値ごとの分岐
 * - for文と%演算子による倍数の判定
 */
public class Exercise04_ControlFlow {
    public static void main(String[] args) {
        IO.println("=== 成績判定 ===");
        int[] scores = {85, 92, 58};
        for (int score : scores) {
            String grade;
            if (score >= 90) {
                grade = "A";
            } else if (score >= 80) {
                grade = "B";
            } else if (score >= 70) {
                grade = "C";
            } else if (score >= 60) {
                grade = "D";
            } else {
                grade = "F";
            }

            // switch式（Java 14以降）。switch文で書いてもよい
            String comment = switch (grade) {
                case "A" -> "優秀";
                case "B", "C" -> "合格";
                case "D" -> "再確認";
                default -> "不合格";
            };

            IO.println(score + "点: " + grade + "（" + comment + "）");
        }

        IO.println();
        IO.println("=== FizzBuzz ===");
        for (int i = 1; i <= 15; i++) {
            if (i % 15 == 0) {
                IO.println("FizzBuzz");
            } else if (i % 3 == 0) {
                IO.println("Fizz");
            } else if (i % 5 == 0) {
                IO.println("Buzz");
            } else {
                IO.println(i);
            }
        }
    }
}

/*
 * よくある間違い:
 * - if (score >= 60) から先に調べると、90点も「D」になる
 * - i % 3 == 0 を先に調べると、15は「Fizz」になり「FizzBuzz」が出ない
 */
