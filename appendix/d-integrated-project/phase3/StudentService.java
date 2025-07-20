// StudentService.java - ビジネスロジック層
import java.util.*;
import java.util.stream.Collectors;

public class StudentService {
    private StudentRepository<GradeableStudent> repository;
    
    public StudentService() {
        this.repository = new StudentRepository<>();
    }
    
    // 学生の登録
    public void registerStudent(GradeableStudent student) {
        repository.addStudent(student);
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
        GradeableStudent student = repository.findById(studentId);
        if (student == null) {
            return "学生が見つかりません: " + studentId;
        }
        
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