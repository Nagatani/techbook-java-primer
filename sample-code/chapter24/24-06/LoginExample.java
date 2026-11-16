// LoginExample.java - 文字列をつないで作ったSQLと、PreparedStatementの違い
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginExample {
    public static void main(String[] args) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE users (username VARCHAR(50), password_hash VARCHAR(100))");
                stmt.execute("INSERT INTO users VALUES ('tanaka', 'x1y2z3')");
            }
            String attack = "' OR '1'='1"; // パスワードの欄に入力された文字列
            IO.println("文字列をつないだ場合: " + unsafeLogin(conn, "x", attack));
            IO.println("PreparedStatementの場合: " + safeLogin(conn, "x", attack));
        }
    }

    // 危険な例。入力をSQL文に直接つないでいる
    static boolean unsafeLogin(Connection conn, String username, String passwordHash)
            throws SQLException {
        String sql = "SELECT * FROM users WHERE username = '" + username
            + "' AND password_hash = '" + passwordHash + "'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next(); // 1行でも見つかればログイン成功とみなす
        }
    }

    // 安全な例。入力は?の位置に「値」として渡り、SQL文の一部にはならない
    static boolean safeLogin(Connection conn, String username, String passwordHash)
            throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
