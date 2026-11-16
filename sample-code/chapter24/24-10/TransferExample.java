// TransferExample.java - 送金をトランザクションで行い、失敗したら取り消す
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TransferExample {
    public static void main(String[] args) throws SQLException {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "")) {
            try (Statement stmt = conn.createStatement()) {
                // 金額は誤差の出ないDECIMAL型にし、JavaではBigDecimalで扱う
                stmt.execute("""
                    CREATE TABLE accounts (
                        id INT PRIMARY KEY,
                        balance DECIMAL(12, 2) NOT NULL CHECK (balance >= 0)
                    )""");
                stmt.execute("INSERT INTO accounts VALUES (1, 5000.00), (2, 1000.00)");
            }
            transfer(conn, 1, 2, new BigDecimal("1500")); // 成功する
            printBalances(conn);
            try {
                transfer(conn, 1, 3, new BigDecimal("1000")); // 口座3はない
            } catch (SQLException e) {
                IO.println("送金を取り消しました: " + e.getMessage());
            }
            printBalances(conn); // 口座1の減額も取り消されている
        }
    }

    static void transfer(Connection conn, int from, int to, BigDecimal amount) throws SQLException {
        conn.setAutoCommit(false); // commitを呼ぶまで変更を確定しない
        try {
            update(conn, "UPDATE accounts SET balance = balance - ? WHERE id = ?", amount, from);
            update(conn, "UPDATE accounts SET balance = balance + ? WHERE id = ?", amount, to);
            conn.commit();         // 2つの更新をまとめて確定する
        } catch (SQLException e) {
            conn.rollback();       // どちらかが失敗したら、両方を取り消す
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    static void update(Connection conn, String sql, BigDecimal amount, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, amount);
            ps.setInt(2, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("口座" + id + "がありません");
            }
        }
    }

    static void printBalances(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM accounts ORDER BY id")) {
            StringBuilder sb = new StringBuilder("残高:");
            while (rs.next()) {
                sb.append(" 口座").append(rs.getInt("id")).append("=").append(rs.getBigDecimal("balance"));
            }
            IO.println(sb);
        }
    }
}
