// H2StudentRepository.java - H2に保存する実装（第26章のJdbcStudentRepositoryをH2に合わせて直した）
// SQLiteとの違い: UPSERTはMERGE、登録した順はrowidではなく自動採番の列、外部キーは既定で検査する
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class H2StudentRepository implements StudentRepository, AutoCloseable {
    private final Connection connection;

    // SQLExceptionを投げてよい処理（トランザクションの中身）
    @FunctionalInterface
    private interface SqlWork {
        void run() throws SQLException;
    }

    public H2StudentRepository(String url) {
        try {
            connection = DriverManager.getConnection(url);
            try (Statement st = connection.createStatement()) {
                st.execute("""
                    CREATE TABLE IF NOT EXISTS students (
                        seq BIGINT AUTO_INCREMENT,   -- 登録した順（SQLiteのrowidの代わり）
                        id VARCHAR(20) PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        department VARCHAR(100) NOT NULL,
                        kind VARCHAR(20) NOT NULL,          -- UNDERGRADUATE または GRADUATE
                        admission_year INTEGER,      -- 学部生だけ
                        research_field VARCHAR(100),         -- 大学院生だけ
                        advisor VARCHAR(100)         -- 大学院生だけ
                    )""");
                st.execute("""
                    CREATE TABLE IF NOT EXISTS grades (
                        student_id VARCHAR(20) NOT NULL REFERENCES students(id) ON DELETE CASCADE,
                        course VARCHAR(100) NOT NULL,
                        credits INTEGER NOT NULL,
                        point DOUBLE PRECISION NOT NULL,
                        position INTEGER NOT NULL,   -- 登録した順
                        PRIMARY KEY (student_id, course)
                    )""");
            }
        } catch (SQLException e) {
            throw new RepositoryException("データベースを開けません: " + url, e);
        }
    }

    @Override
    public void save(Student student) {
        inTransaction(() -> {
            upsertStudent(student);
            replaceGrades(student);
        });
    }

    private void upsertStudent(Student s) throws SQLException {
        // H2のMERGEは、KEYの列が一致する行があれば更新し、なければ追加する（seqは追加のときだけ採番される）
        String sql = """
            MERGE INTO students (id, name, department, kind, admission_year, research_field, advisor)
            KEY (id)
            VALUES (?, ?, ?, ?, ?, ?, ?)""";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, s.getId());
            ps.setString(2, s.getName());
            ps.setString(3, s.getDepartment());
            // 在籍区分ごとに、使う列が違う
            switch (s.getEnrollment()) {
                case Undergraduate u -> {
                    ps.setString(4, "UNDERGRADUATE");
                    ps.setInt(5, u.admissionYear());
                    ps.setNull(6, Types.VARCHAR);
                    ps.setNull(7, Types.VARCHAR);
                }
                case Graduate g -> {
                    ps.setString(4, "GRADUATE");
                    ps.setNull(5, Types.INTEGER);
                    ps.setString(6, g.researchField());
                    ps.setString(7, g.advisor());
                }
            }
            ps.executeUpdate();
        }
    }

    private void replaceGrades(Student s) throws SQLException {
        try (PreparedStatement del = connection.prepareStatement(
                "DELETE FROM grades WHERE student_id = ?")) {
            del.setString(1, s.getId());
            del.executeUpdate();
        }
        try (PreparedStatement ins = connection.prepareStatement(
                "INSERT INTO grades (student_id, course, credits, point, position) VALUES (?, ?, ?, ?, ?)")) {
            int position = 0;
            for (Grade g : s.getGrades()) {
                ins.setString(1, s.getId());
                ins.setString(2, g.course());
                ins.setInt(3, g.credits());
                ins.setDouble(4, g.point());
                ins.setInt(5, position++);
                ins.executeUpdate();
            }
        }
    }

    @Override
    public Optional<Student> findById(String id) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM students WHERE id = ?")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(toStudent(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RepositoryException("学生を読めません: " + id, e);
        }
    }

    @Override
    public List<Student> findAll() {
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM students ORDER BY seq")) {
            List<Student> students = new ArrayList<>();
            while (rs.next()) {
                students.add(toStudent(rs));
            }
            return students;
        } catch (SQLException e) {
            throw new RepositoryException("学生の一覧を読めません", e);
        }
    }

    // 表の1行から学生を組み立てる（成績は別の表から読む）
    private Student toStudent(ResultSet rs) throws SQLException {
        Enrollment enrollment = switch (rs.getString("kind")) {
            case "UNDERGRADUATE" -> new Undergraduate(rs.getInt("admission_year"));
            case "GRADUATE" -> new Graduate(rs.getString("research_field"), rs.getString("advisor"));
            default -> throw new SQLException("未知の在籍区分: " + rs.getString("kind"));
        };
        Student student = new Student(rs.getString("id"), rs.getString("name"),
            rs.getString("department"), enrollment);
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT course, credits, point FROM grades WHERE student_id = ? ORDER BY position")) {
            ps.setString(1, student.getId());
            try (ResultSet g = ps.executeQuery()) {
                while (g.next()) {
                    student.recordGrade(new Grade(g.getString("course"), g.getInt("credits"),
                        g.getDouble("point")));
                }
            }
        }
        return student;
    }

    @Override
    public boolean deleteById(String id) {
        // 成績は外部キーのON DELETE CASCADEで一緒に消える
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM students WHERE id = ?")) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException("学生を削除できません: " + id, e);
        }
    }

    // 途中で失敗したら、それまでの変更をすべて取り消す
    private void inTransaction(SqlWork work) {
        try {
            connection.setAutoCommit(false);
            try {
                work.run();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RepositoryException("データベースの更新に失敗しました", e);
        }
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RepositoryException("データベースを閉じられません", e);
        }
    }
}
