// StudentService.java - データベース対応版
// データベースへの保存に失敗したときは、メモリ上の変更を元に戻してから
// IllegalStateExceptionを投げる（メモリとデータベースの内容を食い違わせない）
import java.util.*;
import java.util.stream.Collectors;
import java.util.logging.Logger;
import java.io.*;
import java.sql.SQLException;

public class StudentService {
    private StudentRepository<GradeableStudent> repository;
    private DatabaseManager dbManager;
    private static final Logger logger = Logger.getLogger(StudentService.class.getName());
    
    // 既定のデータベース（student_management.db）を使う
    public StudentService() throws SQLException {
        this(DatabaseManager.DEFAULT_DB_URL);
    }
    
    // 接続先のURLを指定する。接続できなければSQLExceptionを投げる
    public StudentService(String dbUrl) throws SQLException {
        this.repository = new StudentRepository<>();
        this.dbManager = new DatabaseManager(dbUrl);
        loadStudentsFromDatabase();
    }
    
    private void loadStudentsFromDatabase() throws SQLException {
        List<GradeableStudent> students = dbManager.loadAllStudents();
        for (GradeableStudent student : students) {
            repository.addStudent(student);
        }
        logger.info("データベースから " + students.size() + " 件の学生情報を読み込みました");
    }
    
    public void registerStudent(GradeableStudent student) throws DuplicateStudentException {
        try {
            repository.addStudent(student);
        } catch (IllegalArgumentException e) {
            logger.warning("学生登録エラー: " + e.getMessage());
            throw new DuplicateStudentException(student.getId(), e); // 元の例外を原因として渡す
        }
        
        // データベースに保存
        try {
            dbManager.saveStudent(student);
            logger.info("学生を登録しました: " + student.getId());
        } catch (SQLException e) {
            repository.removeStudent(student.getId()); // メモリ上の登録を取り消す
            logger.severe("データベース保存エラー: " + e.getMessage());
            throw new IllegalStateException("データベースへの保存に失敗しました", e);
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
        
        Double oldGrade = student.getGrades().get(courseName);
        student.addGrade(courseName, grade);
        
        // データベースを更新
        try {
            dbManager.saveStudent(student);
            logger.info(String.format("成績を更新: %s - %s: %.1f", 
                studentId, courseName, grade));
        } catch (SQLException e) {
            // メモリ上の成績を元に戻す
            if (oldGrade == null) {
                student.removeGrade(courseName);
            } else {
                student.addGrade(courseName, oldGrade);
            }
            logger.severe("データベース更新エラー: " + e.getMessage());
            throw new IllegalStateException("データベースの更新に失敗しました", e);
        }
    }
    
    // 学生の削除
    public void deleteStudent(String studentId) throws StudentNotFoundException {
        GradeableStudent student = getStudent(studentId);
        repository.removeStudent(studentId);
        try {
            dbManager.deleteStudent(studentId);
            logger.info("学生を削除しました: " + studentId);
        } catch (SQLException e) {
            repository.addStudent(student); // メモリ上に戻す
            logger.severe("データベース削除エラー: " + e.getMessage());
            throw new IllegalStateException("データベースからの削除に失敗しました", e);
        }
    }
    
    // 成績の削除
    public void removeGrade(String studentId, String courseName) throws StudentNotFoundException {
        GradeableStudent student = getStudent(studentId);
        Double oldGrade = student.getGrades().get(courseName);
        if (oldGrade == null) {
            return; // その科目の成績はない
        }
        student.removeGrade(courseName);
        try {
            dbManager.saveStudent(student);
            logger.info("成績を削除: " + studentId + " - " + courseName);
        } catch (SQLException e) {
            student.addGrade(courseName, oldGrade); // メモリ上に戻す
            logger.severe("データベース更新エラー: " + e.getMessage());
            throw new IllegalStateException("データベースの更新に失敗しました", e);
        }
    }
    
    // データベースの接続を閉じる（アプリケーションの終了時に呼ぶ）
    public void shutdown() {
        try {
            dbManager.close();
            logger.info("データベース接続を閉じました");
        } catch (SQLException e) {
            logger.severe("データベースクローズエラー: " + e.getMessage());
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
                report.append(String.format("  %s: %.1f\n", course, grade))
            );
            
            report.append("\nGPA: ").append(String.format("%.2f", student.getGpa()));
            return report.toString();
            
        } catch (StudentNotFoundException e) {
            return "エラー: " + e.getMessage();
        }
    }
    
    // CSVインポートの結果（成功した件数と、エラーになった件数）
    public record ImportResult(int successCount, int errorCount) {}
    
    // CSVファイル（UTF-8）から学生を読み込む。
    // 1行ごとのエラーはログに記録して次の行へ進み、件数を結果として返す。
    // ファイル自体を読めないときは、IOExceptionを呼び出し元に知らせる
    public ImportResult importStudentsFromCSV(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineNumber = 1; // ファイルの行番号（1行目はヘッダー）
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
            return new ImportResult(successCount, errorCount);
        } catch (IOException e) {
            logger.severe("CSVファイル読み込みエラー: " + e.getMessage());
            throw e;
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
}