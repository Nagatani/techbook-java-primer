import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 図書の検索とSQL Injection（課題3の解答例）
 */
public class BookSearch {
    private static final String CREATE_TABLE_SQL = """
        CREATE TABLE books (
            id INT PRIMARY KEY,
            title VARCHAR(100) NOT NULL,
            author VARCHAR(50) NOT NULL,
            published_year INT NOT NULL
        )
        """;

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:books", "sa", "")) {
            setUp(conn);

            IO.println("=== 題名で検索 ===");
            show("Java", searchByTitle(conn, "Java"));
            show("入門", searchByTitle(conn, "入門"));
            show("料理", searchByTitle(conn, "料理"));

            IO.println();
            IO.println("=== 著者と出版年で検索 ===");
            IO.println("山田一郎、2020年以降: " + findByAuthorSince(conn, "山田一郎", 2020));
            IO.println("O'Brien、2000年以降: " + findByAuthorSince(conn, "O'Brien", 2000));

            IO.println();
            IO.println("=== 悪意のある入力 ===");
            String attack = "' OR '1'='1";
            IO.println("入力: " + attack);
            IO.println("文字列をつなぐ書き方: " + searchByTitleUnsafe(conn, attack).size() + "件（全件が見えてしまう）");
            IO.println("PreparedStatement: " + searchByTitle(conn, attack).size() + "件");

            IO.println();
            IO.println("=== 引用符を含む入力 ===");
            try {
                searchByTitleUnsafe(conn, "Let's");
            } catch (SQLException e) {
                IO.println("文字列をつなぐ書き方: SQLの文法エラー（SQLState: " + e.getSQLState() + "）");
            }
            IO.println("PreparedStatement: " + searchByTitle(conn, "Let's").size() + "件");
        } catch (SQLException e) {
            System.err.println("データベースのエラー: " + e.getMessage());
        }
    }

    private static void show(String keyword, List<String> titles) {
        IO.println("「" + keyword + "」: " + titles.size() + "件 " + titles);
    }

    public static void setUp(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL);
        }
        String sql = "INSERT INTO books VALUES (?, ?, ?, ?)";
        Object[][] rows = {
            {1, "Java入門", "山田一郎", 2024},
            {2, "やさしいJavaの練習帳", "佐藤花子", 2019},
            {3, "データベース入門", "山田一郎", 2018},
            {4, "アルゴリズム図鑑", "鈴木次郎", 2021},
            {5, "Let's Learn SQL", "O'Brien", 2016},
        };
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Object[] row : rows) {
                pstmt.setInt(1, (Integer) row[0]);
                pstmt.setString(2, (String) row[1]);
                pstmt.setString(3, (String) row[2]);
                pstmt.setInt(4, (Integer) row[3]);
                pstmt.executeUpdate();
            }
        }
    }

    /** 題名にキーワードを含む本の題名を、ID順に返す（安全な書き方） */
    public static List<String> searchByTitle(Connection conn, String keyword) throws SQLException {
        String sql = "SELECT title FROM books WHERE title LIKE ? ORDER BY id";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");   // %は値の側に付ける
            return titles(pstmt.executeQuery());
        }
    }

    /** 悪い例: 入力をSQLの文字列につなぐ。比べるためだけに使う */
    public static List<String> searchByTitleUnsafe(Connection conn, String keyword) throws SQLException {
        String sql = "SELECT title FROM books WHERE title LIKE '%" + keyword + "%' ORDER BY id";
        try (Statement stmt = conn.createStatement()) {
            return titles(stmt.executeQuery(sql));
        }
    }

    /** 著者が一致し、指定した年以降に出版された本の題名を、出版年の順に返す */
    public static List<String> findByAuthorSince(Connection conn, String author, int year)
            throws SQLException {
        String sql = "SELECT title FROM books WHERE author = ? AND published_year >= ? ORDER BY published_year";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, author);
            pstmt.setInt(2, year);
            return titles(pstmt.executeQuery());
        }
    }

    private static List<String> titles(ResultSet rs) throws SQLException {
        List<String> titles = new ArrayList<>();
        try (rs) {
            while (rs.next()) {
                titles.add(rs.getString("title"));
            }
        }
        return titles;
    }
}
