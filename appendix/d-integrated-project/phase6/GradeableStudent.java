// GradeableStudent.java - Gradeableを実装
import java.util.Map;
import java.util.LinkedHashMap;

public class GradeableStudent extends Student implements Gradeable {
    private Map<String, Double> grades;
    
    public GradeableStudent(String studentId, String name, int age, String department) {
        super(studentId, name, age, department);
        this.grades = new LinkedHashMap<>(); // 登録した順に科目を並べる
    }
    
    @Override
    public void addGrade(String courseName, double grade) {
        grades.put(courseName, grade);
        this.gpa = calculateGPA();
    }
    
    // 成績を削除する。削除したらtrueを返す
    public boolean removeGrade(String courseName) {
        if (grades.remove(courseName) == null) {
            return false;
        }
        this.gpa = calculateGPA();
        return true;
    }
    
    @Override
    public double calculateGPA() {
        if (grades.isEmpty()) return 0.0;
        return grades.values().stream()
            .mapToDouble(Double::doubleValue)
            .average()
            .orElse(0.0);
    }
    
    @Override
    public Map<String, Double> getGrades() {
        return new LinkedHashMap<>(grades);
    }
}