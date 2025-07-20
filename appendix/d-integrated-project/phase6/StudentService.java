// StudentService.java - データベース対応版
import java.util.*;
import java.util.stream.Collectors;
import java.util.logging.Logger;
import java.io.*;
import java.sql.SQLException;

public class StudentService {
    private StudentRepository<GradeableStudent> repository;
    private DatabaseManager dbManager;
    private static final Logger logger = Logger.getLogger(StudentService.class.getName());
    
    public StudentService() {
        this.repository = new StudentRepository<>();
        try {
            this.dbManager = new DatabaseManager();
            loadStudentsFromDatabase();
        } catch (SQLException e) {
            logger.severe("データベース接続エラー: " + e.getMessage());
        }
    }
    
    private void loadStudentsFromDatabase() {
        try {
            List<GradeableStudent> students = dbManager.loadAllStudents();
            for (GradeableStudent student : students) {
                repository.addStudent(student);
            }
            logger.info("データベースから " + students.size() + " 件の学生情報を読み込みました");
        } catch (SQLException e) {
            logger.severe("データベース読み込みエラー: " + e.getMessage());
        }
    }
    
    public void registerStudent(GradeableStudent student) throws DuplicateStudentException {
        try {
            repository.addStudent(student);
            logger.info("学生を登録しました: " + student.getId());
            
            // データベースに保存
            try {
                dbManager.saveStudent(student);
                logger.info("学生をデータベースに保存しました: " + student.getId());
            } catch (SQLException e) {
                logger.severe("データベース保存エラー: " + e.getMessage());
            }
        } catch (IllegalArgumentException e) {
            logger.warning("学生登録エラー: " + e.getMessage());
            throw new DuplicateStudentException(student.getId());
        }
    }
    
    public GradeableStudent getStudent(String studentId) throws StudentNotFoundException {
        GradeableStudent student = repository.findById(studentId);
        if (student == null) {
            throw new StudentNotFoundException(studentId);
        }
        return student;
    }
    
    public void updateGrade(String studentId, String courseName, double grade) 
            throws StudentNotFoundException, InvalidGradeException {
        
        if (grade < 0.0 || grade > 4.0) {
            throw new InvalidGradeException(grade);
        }
        
        GradeableStudent student = repository.findById(studentId);
        if (student == null) {
            throw new StudentNotFoundException(studentId);
        }
        
        student.addGrade(courseName, grade);
        logger.info(String.format("成績を更新: %s - %s: %.1f", 
            studentId, courseName, grade));
        
        // データベースを更新
        try {
            dbManager.saveStudent(student);
            logger.info("成績をデータベースに保存しました: " + studentId);
        } catch (SQLException e) {
            logger.severe("データベース更新エラー: " + e.getMessage());
        }
    }
    
    // 全学生の取得
    public List<GradeableStudent> getAllStudents() {
        return repository.findAll();
    }
    
    // 複雑な検索クエリ
    public List<GradeableStudent> findStudentsWithConditions(
            String department, Double minGPA, Integer minAge) {
        
        return repository.findAll().stream()
            .filter(s -> department == null || s.getDepartment().equals(department))
            .filter(s -> minGPA == null || s.getGpa() >= minGPA)
            .filter(s -> minAge == null || s.getAge() >= minAge)
            .collect(Collectors.toList());
    }
    
    // 成績レポート生成
    public String generateGradeReport(String studentId) {
        try {
            GradeableStudent student = getStudent(studentId);
            
            StringBuilder report = new StringBuilder();
            report.append("=== 成績レポート ===\n");
            report.append("学生ID: ").append(student.getId()).append("\n");
            report.append("氏名: ").append(student.getName()).append("\n");
            report.append("学部: ").append(student.getDepartment()).append("\n");
            report.append("\n--- 履修科目 ---\n");
            
            student.getGrades().forEach((course, grade) -> 
                report.append(String.format("%-20s: %.1f\n", course, grade))
            );
            
            report.append("\nGPA: ").append(String.format("%.2f", student.getGpa()));
            return report.toString();
            
        } catch (StudentNotFoundException e) {
            return "エラー: " + e.getMessage();
        }
    }
    
    public void importStudentsFromCSV(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 0;
            int successCount = 0;
            int errorCount = 0;
            
            // ヘッダー行をスキップ
            reader.readLine();
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    String[] data = line.split(",");
                    if (data.length < 4) {
                        logger.warning("行 " + lineNumber + ": データ不足");
                        errorCount++;
                        continue;
                    }
                    
                    GradeableStudent student = new GradeableStudent(
                        data[0].trim(),
                        data[1].trim(),
                        Integer.parseInt(data[2].trim()),
                        data[3].trim()
                    );
                    
                    registerStudent(student);
                    successCount++;
                    
                } catch (NumberFormatException e) {
                    logger.warning("行 " + lineNumber + ": 年齢の形式エラー");
                    errorCount++;
                } catch (DuplicateStudentException e) {
                    logger.warning("行 " + lineNumber + ": " + e.getMessage());
                    errorCount++;
                }
            }
            
            logger.info(String.format("CSVインポート完了: 成功=%d, エラー=%d", 
                successCount, errorCount));
                
        } catch (IOException e) {
            logger.severe("CSVファイル読み込みエラー: " + e.getMessage());
        }
    }
    
    public void exportToCSV(String filePath) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("学生ID,氏名,年齢,学部,GPA");
            
            for (GradeableStudent student : repository.findAll()) {
                writer.printf("%s,%s,%d,%s,%.2f%n",
                    student.getId(),
                    student.getName(),
                    student.getAge(),
                    student.getDepartment(),
                    student.getGpa()
                );
            }
            
            logger.info("CSVエクスポートが完了しました: " + filePath);
        }
    }
    
    // 統計情報の取得
    public String getStatisticsReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== 統計情報 ===\n");
        report.append("登録学生数: ").append(repository.findAll().size()).append("\n");
        report.append("平均GPA: ").append(String.format("%.2f", repository.getAverageGPA())).append("\n");
        report.append("\n学部別学生数:\n");
        
        repository.getStudentCountByDepartment().forEach((dept, count) ->
            report.append("  ").append(dept).append(": ").append(count).append("名\n")
        );
        
        return report.toString();
    }
    
    public void deleteStudent(String studentId) throws StudentNotFoundException {
        GradeableStudent student = repository.findById(studentId);
        if (student == null) {
            throw new StudentNotFoundException(studentId);
        }
        
        boolean removed = repository.removeStudent(studentId);
        if (removed) {
            try {
                dbManager.deleteStudent(studentId);
                logger.info("学生を削除しました: " + studentId);
            } catch (SQLException e) {
                logger.severe("データベース削除エラー: " + e.getMessage());
                // ロールバックのため、メモリ上に再追加
                repository.addStudent(student);
                throw new RuntimeException("データベース削除に失敗しました", e);
            }
        }
    }
    
    public void shutdown() {
        try {
            if (dbManager != null) {
                dbManager.close();
                logger.info("データベース接続を閉じました");
            }
        } catch (SQLException e) {
            logger.severe("データベースクローズエラー: " + e.getMessage());
        }
    }
}