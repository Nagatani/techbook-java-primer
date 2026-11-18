import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * テストでよく使うH2のインメモリデータベースが、いつ作られ、いつ消えるかを観察する。
 * また、テストごとにトランザクションをロールバックして、テストどうしを独立させる方法を確かめる。
 */
public class H2Lifecycle {

    static int count(Connection c) throws SQLException {
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM items")) {
            rs.next();
            return rs.getInt(1);
        }
    }

    static void create(Connection c) throws SQLException {
        try (Statement st = c.createStatement()) {
            st.execute("CREATE TABLE items(id INT PRIMARY KEY, name VARCHAR(20))");
            st.execute("INSERT INTO items VALUES (1, 'pen')");
        }
    }

    static void tryCount(String label, String url) {
        try (Connection c = DriverManager.getConnection(url)) {
            IO.println(label + ": rows=" + count(c));
        } catch (SQLException e) {
            IO.println(label + ": " + e.getMessage().lines().findFirst().orElse(""));
        }
    }

    public static void main(String[] args) throws SQLException {
        IO.println("--- named in-memory database (jdbc:h2:mem:shop)");
        try (Connection first = DriverManager.getConnection("jdbc:h2:mem:shop")) {
            create(first);
            tryCount("second connection while the first is open", "jdbc:h2:mem:shop");
        }
        tryCount("new connection after all connections closed", "jdbc:h2:mem:shop");

        IO.println("--- DB_CLOSE_DELAY=-1 (kept until the JVM exits)");
        try (Connection first = DriverManager.getConnection("jdbc:h2:mem:kept;DB_CLOSE_DELAY=-1")) {
            create(first);
        }
        tryCount("new connection after all connections closed", "jdbc:h2:mem:kept");

        IO.println("--- unnamed in-memory database (jdbc:h2:mem:)");
        try (Connection first = DriverManager.getConnection("jdbc:h2:mem:")) {
            create(first);
            tryCount("second connection while the first is open", "jdbc:h2:mem:");
        }

        IO.println("--- roll back after each test");
        try (Connection c = DriverManager.getConnection("jdbc:h2:mem:tx")) {
            create(c);
            c.setAutoCommit(false);
            for (int test = 1; test <= 2; test++) {
                try (Statement st = c.createStatement()) {
                    st.execute("INSERT INTO items VALUES (2, 'note')");
                    IO.println("test " + test + ": rows during the test=" + count(c));
                }
                c.rollback();
                IO.println("test " + test + ": rows after rollback=" + count(c));
            }
        }
    }
}
