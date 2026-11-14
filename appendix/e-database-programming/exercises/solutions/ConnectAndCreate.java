import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 接続とテーブルの作成（課題1の解答例）
 */
public class ConnectAndCreate {
    static final String URL = "jdbc:h2:mem:timetable";
    static final String USER = "sa";
    static final String PASSWORD = "";

    private static final String CREATE_TABLE_SQL = """
        CREATE TABLE courses (
            id INT PRIMARY KEY,
            name VARCHAR(50) NOT NULL,
            day_of_week VARCHAR(3) NOT NULL,
            period INT NOT NULL,
            room VARCHAR(20)
        )
        """;

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            IO.println("接続しました");
            DatabaseMetaData metaData = conn.getMetaData();
            IO.println("データベース製品: " + metaData.getDatabaseProductName());

            createTable(conn);
            IO.println("coursesテーブルを作成しました");

            int inserted = insertSampleCourses(conn);
            IO.println(inserted + "件の授業を登録しました");
            IO.println("登録されている授業: " + countCourses(conn) + "件");

            // 同じ名前のテーブルをもう一度作ると、SQLExceptionになる
            try {
                createTable(conn);
            } catch (SQLException e) {
                IO.println("もう一度作成すると失敗しました（SQLState: " + e.getSQLState() + "）");
            }
        } catch (SQLException e) {
            System.err.println("データベースのエラー: " + e.getMessage());
        }

        // メモリ上のデータベースは、最後の接続を閉じると消える
        IO.println();
        IO.println("接続を閉じて、つなぎ直します");
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            countCourses(conn);
            IO.println("coursesテーブルが残っています");
        } catch (SQLException e) {
            IO.println("coursesテーブルはありません（SQLState: " + e.getSQLState() + "）");
        }
    }

    public static void createTable(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL);
        }
    }

    /** 見本の授業を4件登録し、登録した件数を返す */
    public static int insertSampleCourses(Connection conn) throws SQLException {
        String[] sqls = {
            "INSERT INTO courses VALUES (1, 'プログラミング演習', '月', 1, '情報演習室A')",
            "INSERT INTO courses VALUES (2, '線形代数', '月', 3, '201教室')",
            "INSERT INTO courses VALUES (3, 'データベース論', '水', 2, '305教室')",
            "INSERT INTO courses VALUES (4, '英語コミュニケーション', '金', 1, NULL)",
        };
        int count = 0;
        try (Statement stmt = conn.createStatement()) {
            for (String sql : sqls) {
                count += stmt.executeUpdate(sql);   // 変更した行数（ここでは1）を返す
            }
        }
        return count;
    }

    public static int countCourses(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM courses")) {
            rs.next();
            return rs.getInt(1);
        }
    }
}
