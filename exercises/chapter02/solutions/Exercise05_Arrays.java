/**
 * 第2章 課題5: 配列の操作 - 解答例
 *
 * 学習ポイント:
 * - 拡張for文による合計
 * - (double)によるキャストで小数の平均を求める
 * - 最初の要素を仮の最大値・最小値にして比べる方法
 */
public class Exercise05_Arrays {
    public static void main(String[] args) {
        int[] scores = {78, 92, 65, 88, 71};

        IO.println("=== 点数の集計 ===");
        IO.print("点数:");
        for (int score : scores) {
            IO.print(" " + score);
        }
        IO.println();

        int sum = 0;
        for (int score : scores) {
            sum += score;
        }
        double average = (double) sum / scores.length;

        int max = scores[0];
        int min = scores[0];
        for (int i = 1; i < scores.length; i++) {
            if (scores[i] > max) {
                max = scores[i];
            }
            if (scores[i] < min) {
                min = scores[i];
            }
        }

        int aboveAverage = 0;
        for (int score : scores) {
            if (score >= average) {
                aboveAverage++;
            }
        }

        IO.println("人数: " + scores.length + "人");
        IO.println("合計: " + sum + "点");
        IO.println("平均: " + "%.1f".formatted(average) + "点");
        IO.println("最高点: " + max + "点");
        IO.println("最低点: " + min + "点");
        IO.println("平均点以上: " + aboveAverage + "人");
    }
}

/*
 * よくある間違い:
 * - sum / scores.length と書くと整数どうしの割り算になり、78になる
 * - for (int i = 0; i <= scores.length; i++) と書くと、最後に範囲外を参照して
 *   ArrayIndexOutOfBoundsExceptionになる
 */
