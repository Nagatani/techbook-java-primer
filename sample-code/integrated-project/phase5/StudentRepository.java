// StudentRepository.java
import java.util.*;
import java.util.stream.Collectors;

public class StudentRepository<T extends Student> {
    private Map<String, T> students;
    private List<T> studentList;
    
    public StudentRepository() {
        this.students = new HashMap<>();
        this.studentList = new ArrayList<>();
    }
    
    // 基本的なCRUD操作
    public void addStudent(T student) {
        if (students.containsKey(student.getId())) {
            throw new IllegalArgumentException("学生ID " + student.getId() + " は既に存在します");
        }
        students.put(student.getId(), student);
        studentList.add(student);
    }
    
    public T findById(String studentId) {
        return students.get(studentId);
    }
    
    public List<T> findAll() {
        return new ArrayList<>(studentList);
    }
    
    public boolean removeStudent(String studentId) {
        T removed = students.remove(studentId);
        if (removed != null) {
            studentList.remove(removed);
            return true;
        }
        return false;
    }
    
    // 検索機能
    public List<T> findByDepartment(String department) {
        return studentList.stream()
            .filter(s -> s.getDepartment().equals(department))
            .collect(Collectors.toList());
    }
    
    public List<T> findByGPARange(double minGPA, double maxGPA) {
        return studentList.stream()
            .filter(s -> s.getGpa() >= minGPA && s.getGpa() <= maxGPA)
            .collect(Collectors.toList());
    }
    
    // Stream APIを使った統計情報
    public double getAverageGPA() {
        return studentList.stream()
            .mapToDouble(Student::getGpa)
            .average()
            .orElse(0.0);
    }
    
    public Map<String, Long> getStudentCountByDepartment() {
        return studentList.stream()
            .collect(Collectors.groupingBy(
                Student::getDepartment,
                Collectors.counting()
            ));
    }
    
    public List<T> getTopStudents(int count) {
        return studentList.stream()
            .sorted((s1, s2) -> Double.compare(s2.getGpa(), s1.getGpa()))
            .limit(count)
            .collect(Collectors.toList());
    }
}