import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 歩数の記録をバッチ処理で登録し、集計する
 */
public class StepBatchImporter {
    /** 登録の結果。rowsは登録した行数、batchesはexecuteBatchを呼んだ回数 */
    public record ImportResult(int rows, int batches) {
    }

    /** 月ごとの集計 */
    public record MonthlySummary(int month, int days, int total, int goalDays) {
        public int average() {
            return total / days;
        }
    }

    public static final int GOAL = 8000;

    private final Connection connection;
    private final int batchSize;

    public StepBatchImporter(Connection connection, int batchSize) {
        this.connection = connection;
        this.batchSize = batchSize;
    }

    public void createTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS daily_steps (walked_on DATE PRIMARY KEY, steps INT NOT NULL)");
        }
    }

    /**
     * 記録をbatchSize件ずつまとめて送り、最後にまとめてコミットする。
     * 1件でも失敗したら、この呼び出しで登録したものをすべて取り消し、例外を投げる。
     */
    public ImportResult importAll(List<DailySteps> records) throws SQLException {
        String sql = "INSERT INTO daily_steps (walked_on, steps) VALUES (?, ?)";
        boolean originalAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            int rows = 0;
            int batches = 0;
            int pending = 0;
            for (DailySteps r : records) {
                pstmt.setObject(1, r.date());   // LocalDateをDATEとして渡す
                pstmt.setInt(2, r.steps());
                pstmt.addBatch();
                pending++;
                if (pending == batchSize) {
                    rows += sum(pstmt.executeBatch());
                    batches++;
                    pending = 0;
                }
            }
            if (pending > 0) {   // 残りを送る
                rows += sum(pstmt.executeBatch());
                batches++;
            }
            connection.commit();
            return new ImportResult(rows, batches);
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    private static int sum(int[] counts) {
        int total = 0;
        for (int c : counts) {
            total += c;   // 1件ごとの変更した行数（ここでは1）
        }
        return total;
    }

    public int count() throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM daily_steps")) {
            rs.next();
            return rs.getInt(1);
        }
    }

    /** 月ごとの日数・合計・目標を達成した日数を、月の順に返す */
    public List<MonthlySummary> monthlySummaries() throws SQLException {
        String sql = """
            SELECT EXTRACT(MONTH FROM walked_on) AS month_no, COUNT(*) AS days, SUM(steps) AS total,
                   SUM(CASE WHEN steps >= ? THEN 1 ELSE 0 END) AS goal_days
            FROM daily_steps
            GROUP BY EXTRACT(MONTH FROM walked_on)
            ORDER BY month_no
            """;
        List<MonthlySummary> list = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, GOAL);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new MonthlySummary(rs.getInt("month_no"), rs.getInt("days"),
                            rs.getInt("total"), rs.getInt("goal_days")));
                }
            }
        }
        return list;
    }
}
