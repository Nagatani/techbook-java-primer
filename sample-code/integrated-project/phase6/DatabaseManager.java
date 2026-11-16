// DatabaseManager.java
import java.sql.*;
import java.util.*;

public class DatabaseManager {
    // 既定の接続先（実行したディレクトリにstudent_management.dbを作る）
    public static final String DEFAULT_DB_URL = "jdbc:sqlite:student_management.db";
    private Connection connection;
    
    public DatabaseManager() throws SQLException {
        this(DEFAULT_DB_URL);
    }
    
    // 接続先のURLを指定する（テストでは別のファイルやメモリ上のDBを使う）
    public DatabaseManager(String dbUrl) throws SQLException {
        connection = DriverManager.getConnection(dbUrl);
        // SQLiteは既定では外部キー制約を検査しないので、接続ごとに有効にする
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
        createTables();
    }
    
    private void createTables() throws SQLException {
        String createStudentTable = """
            CREATE TABLE IF NOT EXISTS students (
                student_id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                age INTEGER NOT NULL,
                department TEXT NOT NULL,
                gpa REAL DEFAULT 0.0
            )
        """;
        
        String createGradeTable = """
            CREATE TABLE IF NOT EXISTS grades (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id TEXT NOT NULL,
                course_name TEXT NOT NULL,
                grade REAL NOT NULL,
                FOREIGN KEY (student_id) REFERENCES students(student_id),
                UNIQUE(student_id, course_name)
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createStudentTable);
            stmt.execute(createGradeTable);
        }
    }
    
    // 学生と成績を保存する。途中で失敗したら全体を取り消す（トランザクション）
    public void saveStudent(GradeableStudent student) throws SQLException {
        // 同じIDの学生がいれば、行を消さずに列の値を更新する（UPSERT）
        String sql = """
            INSERT INTO students (student_id, name, age, department, gpa)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT(student_id) DO UPDATE SET
                name = excluded.name,
                age = excluded.age,
                department = excluded.department,
                gpa = excluded.gpa
        """;
        
        connection.setAutoCommit(false);
        try {
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, student.getId());
                pstmt.setString(2, student.getName());
                pstmt.setInt(3, student.getAge());
                pstmt.setString(4, student.getDepartment());
                pstmt.setDouble(5, student.getGpa());
                pstmt.executeUpdate();
            }
            
            saveGrades(student);
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    private void saveGrades(GradeableStudent student) throws SQLException {
        // 既存の成績を削除
        String deleteSql = "DELETE FROM grades WHERE student_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteSql)) {
            pstmt.setString(1, student.getId());
            pstmt.executeUpdate();
        }
        
        // 新しい成績を挿入
        String insertSql = """
            INSERT INTO grades (student_id, course_name, grade)
            VALUES (?, ?, ?)
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(insertSql)) {
            Map<String, Double> grades = student.getGrades();
            for (Map.Entry<String, Double> entry : grades.entrySet()) {
                pstmt.setString(1, student.getId());
                pstmt.setString(2, entry.getKey());
                pstmt.setDouble(3, entry.getValue());
                pstmt.executeUpdate();
            }
        }
    }
    
    public List<GradeableStudent> loadAllStudents() throws SQLException {
        List<GradeableStudent> students = new ArrayList<>();
        
        // g.idの順に並べると、成績を登録した順になる
        String sql = """
            SELECT s.student_id, s.name, s.age, s.department, g.course_name, g.grade
            FROM students s
            LEFT JOIN grades g ON s.student_id = g.student_id
            ORDER BY s.student_id, g.id
        """;
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            GradeableStudent currentStudent = null;
            String currentId = null;
            
            while (rs.next()) {
                String studentId = rs.getString("student_id");
                
                // 新しい学生の場合
                if (!studentId.equals(currentId)) {
                    if (currentStudent != null) {
                        students.add(currentStudent);
                    }
                    
                    currentStudent = new GradeableStudent(
                        studentId,
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("department")
                    );
                    currentId = studentId;
                }
                
                // 成績情報を追加
                String courseName = rs.getString("course_name");
                if (courseName != null) {
                    double grade = rs.getDouble("grade");
                    currentStudent.addGrade(courseName, grade);
                }
            }
            
            // 最後の学生を追加
            if (currentStudent != null) {
                students.add(currentStudent);
            }
        }
        
        return students;
    }
    
    public void deleteStudent(String studentId) throws SQLException {
        // トランザクション開始
        connection.setAutoCommit(false);
        
        try {
            // 成績情報を先に削除（外部キー制約があるため、学生より先に消す）
            String deleteGrades = "DELETE FROM grades WHERE student_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteGrades)) {
                pstmt.setString(1, studentId);
                pstmt.executeUpdate();
            }
            
            // 学生情報を削除
            String deleteStudent = "DELETE FROM students WHERE student_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteStudent)) {
                pstmt.setString(1, studentId);
                pstmt.executeUpdate();
            }
            
            connection.commit();
            
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
