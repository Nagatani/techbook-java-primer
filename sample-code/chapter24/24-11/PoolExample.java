// PoolExample.java - HikariCPのコネクションプールから接続を借りて使う
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class PoolExample {
    public static void main(String[] args) throws SQLException {
        HikariConfig config = new HikariConfig();
        // DB_CLOSE_DELAY=-1: 接続がすべてプールに戻っても、メモリ上のデータベースを消さない
        config.setJdbcUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        config.setUsername("sa");
        config.setPassword("");
        config.setMaximumPoolSize(5); // 用意しておく接続の数

        // HikariDataSourceはAutoCloseable。閉じるとプールの接続をすべて切る
        try (HikariDataSource pool = new HikariDataSource(config)) {
            try (Connection conn = pool.getConnection();   // プールから借りる
                 Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE visits (page VARCHAR(50))");
            }                                              // closeは切断ではなく、プールへ返す
            for (String page : new String[] {"top", "news", "top"}) {
                try (Connection conn = pool.getConnection();
                     PreparedStatement ps = conn.prepareStatement("INSERT INTO visits VALUES (?)")) {
                    ps.setString(1, page);
                    ps.executeUpdate();
                }
            }
            try (Connection conn = pool.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM visits")) {
                rs.next();
                IO.println("記録した訪問: " + rs.getInt(1) + "件");
            }
        }
    }
}
