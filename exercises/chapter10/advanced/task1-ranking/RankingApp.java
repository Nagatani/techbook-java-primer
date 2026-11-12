import java.util.List;

/**
 * 第10章 発展課題1: ランキング表（解答例）
 *
 * 同じRankingクラスを、テストの点数（Integer）とゲームの得点（GameScore）に使います。
 *
 * 学習ポイント:
 * - 境界のある型パラメーター <T extends Comparable<T>>
 * - 境界を付けたことで、T型の値に対してcompareToを呼べる
 * - ジェネリックメソッドと、引数の? extends T
 */
public class RankingApp {
    public static void main(String[] args) {
        IO.println("=== テストの点数（上位3件） ===");
        Ranking<Integer> scores = new Ranking<>(3);
        int[] points = {72, 88, 65, 88, 95, 70};
        for (int p : points) {
            int rank = scores.add(p);
            if (rank > 0) {
                IO.println(p + "点 → " + rank + "位に入りました");
            } else {
                IO.println(p + "点 → ランキング外");
            }
        }
        IO.println("ランキング: " + scores.toList());
        IO.println("3位の点数: " + scores.lowest());

        IO.println();
        IO.println("=== ゲームの得点（上位3件） ===");
        Ranking<GameScore> game = new Ranking<>(3);
        game.add(new GameScore("さくら", 1200));
        game.add(new GameScore("たろう", 1500));
        game.add(new GameScore("はなこ", 900));
        game.add(new GameScore("じろう", 1500));  // たろうと同点。先に入ったたろうが上
        for (int rank = 1; rank <= game.size(); rank++) {
            IO.println(rank + "位: " + game.get(rank));
        }

        IO.println();
        IO.println("=== リストからまとめて作る ===");
        List<Integer> heights = List.of(158, 172, 165, 180, 169);
        Ranking<Integer> tallest = Ranking.of(2, heights);
        IO.println("身長の上位2件: " + tallest.toList());

        // Objectには大小の比べ方がない（Comparableを実装していない）ので、次の行はコンパイルエラーになる
        // Ranking<Object> wrong = new Ranking<>(3);
    }
}
