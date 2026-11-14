import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 1年分の歩数をバッチ処理で登録するプログラム（発展課題2の解答例）
 */
public class StepBatchApp {
    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:steps", "sa", "")) {
            StepBatchImporter importer = new StepBatchImporter(conn, 100);
            importer.createTable();

            IO.println("=== 2026年の記録を登録 ===");
            List<DailySteps> year = DailySteps.sample(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));
            IO.println("用意した記録: " + year.size() + "件");
            StepBatchImporter.ImportResult result = importer.importAll(year);
            IO.println("登録した行: " + result.rows() + "件、executeBatchの呼び出し: " + result.batches() + "回");

            IO.println();
            IO.println("=== 月ごとの集計（目標 " + StepBatchImporter.GOAL + "歩） ===");
            int totalSteps = 0;
            for (StepBatchImporter.MonthlySummary m : importer.monthlySummaries()) {
                IO.println("%2d月: %d日 合計%,d歩 平均%,d歩 目標達成%d日".formatted(
                        m.month(), m.days(), m.total(), m.average(), m.goalDays()));
                totalSteps += m.total();
            }
            IO.println("1年の合計: %,d歩".formatted(totalSteps));

            IO.println();
            IO.println("=== 途中に重複がある記録を登録 ===");
            // 2027年1月の記録の途中に、登録済みの2026年12月31日が紛れ込んでいる
            List<DailySteps> next = new ArrayList<>(DailySteps.sample(LocalDate.of(2027, 1, 1), LocalDate.of(2027, 1, 10)));
            next.add(5, new DailySteps(LocalDate.of(2026, 12, 31), 5000));
            IO.println("用意した記録: " + next.size() + "件");
            try {
                importer.importAll(next);
            } catch (BatchUpdateException e) {
                IO.println("登録に失敗しました（SQLState: " + e.getSQLState() + "）");
                IO.println("すべて取り消したので、行数は変わりません: " + importer.count() + "件");
            }
        } catch (SQLException e) {
            System.err.println("データベースのエラー: " + e.getMessage());
        }
    }
}
