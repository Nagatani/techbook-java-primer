// ConnectExample.java - H2のメモリ上のデータベースに接続する
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectExample {
    public static void main(String[] args) {
        String url = "jdbc:h2:mem:testdb"; // メモリ上のデータベース（接続を閉じると消える）
        String user = "sa";
        String password = "";

        // ConnectionはAutoCloseableなので、try-with-resourcesで閉じる
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            DatabaseMetaData meta = conn.getMetaData();
            IO.println("接続しました: " + meta.getDatabaseProductName()
                + " " + meta.getDatabaseProductVersion());
        } catch (SQLException e) {
            System.err.println("接続できません: " + e.getMessage());
        }
    }
}
