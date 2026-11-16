// Gradeable.java - インターフェイス
import java.util.Map;

public interface Gradeable {
    void addGrade(String courseName, double grade);
    double calculateGPA();
    Map<String, Double> getGrades();
}