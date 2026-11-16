import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * 成績をResultSetからオブジェクトにする（課題4の解答例）
 */
public class ScoreReport {
    /** 1人の1科目の成績。scoreがnullなら欠席 */
    public record Score(String studentId, String name, String subject, Integer score) {
        public String display() {
            return studentId + " " + name + " " + (score == null ? "欠席" : score + "点");
        }
    }

    /** 科目ごとの集計。受験者数には欠席者を含めない */
    public record SubjectSummary(String subject, int examinees, int absentees, double average, int max) {
    }

    private static final String CREATE_TABLE_SQL = """
        CREATE TABLE scores (
            student_id VARCHAR(5) NOT NULL,
            name VARCHAR(20) NOT NULL,
            subject VARCHAR(20) NOT NULL,
            score INT,
            PRIMARY KEY (student_id, subject)
        )
        """;

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:scores", "sa", "")) {
            setUp(conn);

            IO.println("=== プログラミングの成績（点数の高い順） ===");
            for (Score s : findBySubject(conn, "プログラミング")) {
                IO.println(s.display());
            }

            IO.println();
            IO.println("=== getIntとgetObjectの違い（S003のプログラミング） ===");
            compareNullHandling(conn, "S003", "プログラミング");

            IO.println();
            IO.println("=== 科目ごとの集計 ===");
            for (SubjectSummary s : summarize(conn)) {
                IO.println("%s: 受験%d人、欠席%d人、平均%.1f点、最高%d点".formatted(
                        s.subject(), s.examinees(), s.absentees(), s.average(), s.max()));
            }
        } catch (SQLException e) {
            System.err.println("データベースのエラー: " + e.getMessage());
        }
    }

    public static void setUp(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL);
        }
        Object[][] rows = {
            {"S001", "青木健", "プログラミング", 78},
            {"S002", "井上彩", "プログラミング", 92},
            {"S003", "上田翔", "プログラミング", null},
            {"S004", "江藤美咲", "プログラミング", 65},
            {"S001", "青木健", "統計学", 81},
            {"S002", "井上彩", "統計学", 74},
            {"S003", "上田翔", "統計学", 88},
            {"S004", "江藤美咲", "統計学", null},
        };
        String sql = "INSERT INTO scores VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Object[] row : rows) {
                pstmt.setString(1, (String) row[0]);
                pstmt.setString(2, (String) row[1]);
                pstmt.setString(3, (String) row[2]);
                pstmt.setObject(4, row[3]);   // nullならNULLが入る
                pstmt.executeUpdate();
            }
        }
    }

    /** 科目の成績を点数の高い順に返す。欠席者は最後に、学籍番号の順で並べる */
    public static List<Score> findBySubject(Connection conn, String subject) throws SQLException {
        String sql = """
            SELECT student_id, name, subject, score FROM scores
            WHERE subject = ?
            ORDER BY score DESC NULLS LAST, student_id
            """;
        List<Score> scores = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, subject);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    scores.add(toScore(rs));
                }
            }
        }
        return scores;
    }

    /** ResultSetの今の行をScoreにする */
    private static Score toScore(ResultSet rs) throws SQLException {
        return new Score(
                rs.getString("student_id"),
                rs.getString("name"),
                rs.getString("subject"),
                rs.getObject("score", Integer.class));   // NULLはnullのまま
    }

    private static void compareNullHandling(Connection conn, String studentId, String subject)
            throws SQLException {
        String sql = "SELECT score FROM scores WHERE student_id = ? AND subject = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, subject);
            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                int asInt = rs.getInt("score");
                IO.println("getInt: " + asInt + "（wasNull: " + rs.wasNull() + "）");
                IO.println("getObject: " + rs.getObject("score", Integer.class));
            }
        }
    }

    /** 科目ごとの集計を、科目名の順に返す */
    public static List<SubjectSummary> summarize(Connection conn) throws SQLException {
        // COUNT(score)とAVG(score)・MAX(score)は、NULLの行を数えない
        String sql = """
            SELECT subject, COUNT(score) AS examinees, COUNT(*) - COUNT(score) AS absentees,
                   AVG(score) AS average, MAX(score) AS max_score
            FROM scores
            GROUP BY subject
            ORDER BY subject
            """;
        List<SubjectSummary> list = new ArrayList<>();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new SubjectSummary(
                        rs.getString("subject"),
                        rs.getInt("examinees"),
                        rs.getInt("absentees"),
                        rs.getDouble("average"),
                        rs.getInt("max_score")));
            }
        }
        return list;
    }
}
