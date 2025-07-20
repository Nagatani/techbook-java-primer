// DatabaseManager.java
import java.sql.*;
import java.util.*;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:student_management.db";
    private Connection connection;
    
    public DatabaseManager() throws SQLException {
        connection = DriverManager.getConnection(DB_URL);
        createTables();
    }
    
    private void createTables() throws SQLException {
        String createStudentTable = """
            CREATE TABLE IF NOT EXISTS students (
                student_id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                age INTEGER NOT NULL,
                department TEXT NOT NULL,
                gpa REAL DEFAULT 0.0,
                student_type TEXT NOT NULL
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
        
        String createGraduateStudentTable = """
            CREATE TABLE IF NOT EXISTS graduate_students (
                student_id TEXT PRIMARY KEY,
                research_field TEXT NOT NULL,
                advisor_name TEXT,
                FOREIGN KEY (student_id) REFERENCES students(student_id)
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createStudentTable);
            stmt.execute(createGradeTable);
            stmt.execute(createGraduateStudentTable);
        }
    }
    
    public void saveStudent(Student student) throws SQLException {
        String sql = """
            INSERT OR REPLACE INTO students (student_id, name, age, department, gpa, student_type)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, student.getId());
            pstmt.setString(2, student.getName());
            pstmt.setInt(3, student.getAge());
            pstmt.setString(4, student.getDepartment());
            pstmt.setDouble(5, student.getGpa());
            pstmt.setString(6, student.getClass().getSimpleName());
            
            pstmt.executeUpdate();
            
            // 大学院生の場合は追加情報を保存
            if (student instanceof GraduateStudent) {
                saveGraduateStudentInfo((GraduateStudent) student);
            }
            
            // 成績情報を保存
            if (student instanceof GradeableStudent) {
                saveGrades((GradeableStudent) student);
            }
        }
    }
    
    private void saveGraduateStudentInfo(GraduateStudent student) throws SQLException {
        String sql = """
            INSERT OR REPLACE INTO graduate_students (student_id, research_field, advisor_name)
            VALUES (?, ?, ?)
        """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, student.getId());
            pstmt.setString(2, student.getResearchField());
            pstmt.setString(3, student.getAdvisorName());
            pstmt.executeUpdate();
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
        
        String sql = """
            SELECT s.*, g.course_name, g.grade
            FROM students s
            LEFT JOIN grades g ON s.student_id = g.student_id
            ORDER BY s.student_id, g.course_name
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
            // 成績情報を削除
            String deleteGrades = "DELETE FROM grades WHERE student_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteGrades)) {
                pstmt.setString(1, studentId);
                pstmt.executeUpdate();
            }
            
            // 大学院生情報を削除
            String deleteGradInfo = "DELETE FROM graduate_students WHERE student_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteGradInfo)) {
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