import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 第12章 チャレンジ課題: 成績の集計レポート（解答例）
 *
 * 1行に複数科目の点数が並ぶデータをflatMapで1科目ずつのScoreに分け、
 * Collectorsで科目別・クラス別・学生別に集計します。
 * 見つからないかもしれない値はOptionalで受け取ります。
 */
public class GradeReportApp {
    public static void main(String[] args) {
        List<String> lines = GradeData.lines();
        List<Score> scores = ScoreParser.parse(lines);
        GradeReport report = new GradeReport(scores);

        IO.println("学生 " + lines.size() + "人、点数 " + scores.size() + "件");

        IO.println();
        IO.println("=== 科目ごとの平均点と最高点 ===");
        Map<String, Optional<Score>> best = report.bestBySubject();
        report.averageBySubject().forEach((subject, avg) -> {
            String top = best.get(subject)
                    .map(s -> s.name() + " " + s.point() + "点")
                    .orElse("なし");
            IO.println("  " + subject + ": 平均 " + "%.1f".formatted(avg) + "点、最高 " + top);
        });

        IO.println();
        IO.println("=== クラスごとの平均点 ===");
        report.averageByClass().forEach((cls, avg) -> IO.println("  " + cls + ": " + "%.1f".formatted(avg) + "点"));

        IO.println();
        IO.println("=== 合計点の順位（上位3人） ===");
        List<String> ranking = report.ranking(3);
        for (int i = 0; i < ranking.size(); i++) {
            IO.println("  " + (i + 1) + "位 " + ranking.get(i));
        }

        IO.println();
        IO.println("=== 科目ごとの合格者数（" + GradeReport.PASS_LINE + "点以上） ===");
        report.passCountBySubject().forEach((subject, counts) ->
                IO.println("  " + subject + ": 合格 " + counts.get(true) + "人、不合格 " + counts.get(false) + "人"));

        IO.println();
        IO.println("=== 不合格の科目がある学生 ===");
        report.failedSubjects().forEach((student, subjects) -> IO.println("  " + student + ": " + subjects));

        IO.println();
        IO.println("=== 条件の確認 ===");
        IO.println("  全科目合格の学生がいる: " + report.anyoneAllPassed());
        IO.println("  全員が全科目40点以上: " + report.everyoneAtLeast(40));
        IO.println("  全員が全科目50点以上: " + report.everyoneAtLeast(50));

        IO.println();
        IO.println("=== 学籍番号で合計点を探す ===");
        for (String id : List.of("S003", "S010")) {
            String result = report.totalOf(id)
                    .map(total -> total + "点")
                    .orElse("該当する学生はいません");
            IO.println("  " + id + ": " + result);
        }

        IO.println();
        IO.println("=== 全点数の合計（順次処理と並列処理） ===");
        IO.println("  順次処理: " + report.sumSequential());
        IO.println("  並列処理: " + report.sumParallel());
    }
}
