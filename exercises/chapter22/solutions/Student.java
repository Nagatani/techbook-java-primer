package chapter22.solutions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 学生情報を表すデータモデルクラス。
 * <p>
 * JSON形式でのシリアライゼーション/デシリアライゼーションに対応しています。
 * </p>
 * 
 * @author Hidehiro Nagatani
 * @version 1.0
 * @since 2024-01-01
 */
public class Student {
    private String id;
    private String name;
    private int age;
    private List<Course> courses;
    
    /**
     * デフォルトコンストラクタ（Gsonでのデシリアライゼーション用）
     */
    public Student() {
        this.courses = new ArrayList<>();
    }
    
    /**
     * 学生情報を作成します。
     * 
     * @param id 学生ID
     * @param name 学生名
     * @param age 年齢
     */
    public Student(String id, String name, int age) {
        this.id = Objects.requireNonNull(id, "学生IDはnullにできません");
        this.name = Objects.requireNonNull(name, "学生名はnullにできません");
        this.age = age;
        this.courses = new ArrayList<>();
    }
    
    /**
     * 履修科目を追加します。
     * 
     * @param course 追加する科目
     */
    public void addCourse(Course course) {
        Objects.requireNonNull(course, "科目はnullにできません");
        courses.add(course);
    }
    
    /**
     * GPAを計算します。
     * 
     * @return GPA（履修科目がない場合は0.0）
     */
    public double calculateGPA() {
        if (courses.isEmpty()) {
            return 0.0;
        }
        
        double totalGradePoints = 0.0;
        int totalCredits = 0;
        
        for (Course course : courses) {
            totalGradePoints += course.getGrade() * course.getCredits();
            totalCredits += course.getCredits();
        }
        
        return totalCredits > 0 ? totalGradePoints / totalCredits : 0.0;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = Objects.requireNonNull(id, "学生IDはnullにできません");
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "学生名はnullにできません");
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    public List<Course> getCourses() {
        return courses;
    }
    
    public void setCourses(List<Course> courses) {
        this.courses = courses != null ? courses : new ArrayList<>();
    }
    
    @Override
    public String toString() {
        return String.format("Student[id=%s, name=%s, age=%d, courses=%d, GPA=%.2f]",
                id, name, age, courses.size(), calculateGPA());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Student student = (Student) obj;
        return Objects.equals(id, student.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}