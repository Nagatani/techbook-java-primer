// DateRangeExample.java - 日付と時刻の列をjava.timeのクラスで読み書きする
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateRangeExample {
    public static void main(String[] args) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE events (title VARCHAR(50), starts_at TIMESTAMP)");
            }
            insert(conn, "入学式", LocalDateTime.of(2026, 4, 3, 10, 0));
            insert(conn, "履修登録の締め切り", LocalDateTime.of(2026, 4, 14, 17, 0));
            insert(conn, "前期試験", LocalDateTime.of(2026, 7, 27, 9, 0));

            // 4月の予定（4月1日0時以上、5月1日0時未満）
            LocalDateTime from = LocalDate.of(2026, 4, 1).atStartOfDay();
            String sql = "SELECT * FROM events WHERE starts_at >= ? "
                    + "AND starts_at < ? ORDER BY starts_at";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                // LocalDateTimeをTIMESTAMPとして渡す
                ps.setObject(1, from);
                ps.setObject(2, from.plusMonths(1));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        LocalDateTime at = rs.getObject("starts_at",
                                LocalDateTime.class);
                        IO.println(at.toLocalDate()
                                + " " + at.toLocalTime() + " "
                                + rs.getString("title"));
                    }
                }
            }
        }
    }

    static void insert(Connection conn, String title, LocalDateTime startsAt) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO events VALUES (?, ?)")) {
            ps.setString(1, title);
            ps.setObject(2, startsAt);
            ps.executeUpdate();
        }
    }
}
