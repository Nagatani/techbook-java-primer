// BatchExample.java - 大量の行をバッチでまとめて追加する
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

public class BatchExample {
    private static final int BATCH_SIZE = 1000;

    public static void main(String[] args) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE daily_steps (walk_date DATE PRIMARY KEY, steps INT NOT NULL)");
            }
            LocalDate first = LocalDate.of(2025, 1, 1);
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO daily_steps VALUES (?, ?)")) {
                for (int i = 0; i < 3650; i++) {      // 10年分の歩数
                    ps.setObject(1, first.plusDays(i));
                    ps.setInt(2, 5000 + (i * 37) % 6000);
                    // すぐには送らず、ためておく
                    ps.addBatch();
                    if ((i + 1) % BATCH_SIZE == 0) {
                        // 1000件ずつまとめて送る
                        ps.executeBatch();
                    }
                }
                ps.executeBatch();                    // 残りを送る
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*), MAX(walk_date) FROM daily_steps")) {
                rs.next();
                IO.println(rs.getInt(1) + "件を追加しました（最後の日: " + rs.getObject(2, LocalDate.class) + "）");
            }
        }
    }
}
