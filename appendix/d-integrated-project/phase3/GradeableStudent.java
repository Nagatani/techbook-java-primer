// GradeableStudent.java - Gradeableを実装
import java.util.Map;
import java.util.HashMap;

public class GradeableStudent extends Student implements Gradeable {
    private Map<String, Double> grades;
    
    public GradeableStudent(String studentId, String name, int age, String department) {
        super(studentId, name, age, department);
        this.grades = new HashMap<>();
    }
    
    @Override
    public void addGrade(String courseName, double grade) {
        grades.put(courseName, grade);
        this.gpa = calculateGPA();
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
        return new HashMap<>(grades);
    }
}