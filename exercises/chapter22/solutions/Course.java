package chapter22.solutions;

import java.util.Objects;

/**
 * 履修科目を表すデータモデルクラス。
 * <p>
 * 科目ID、科目名、単位数、成績を保持します。
 * </p>
 * 
 * @author Hidehiro Nagatani
 * @version 1.0
 * @since 2024-01-01
 */
public class Course {
    private String courseId;
    private String courseName;
    private int credits;
    private double grade;
    
    /**
     * デフォルトコンストラクタ（Gsonでのデシリアライゼーション用）
     */
    public Course() {
    }
    
    /**
     * 科目情報を作成します。
     * 
     * @param courseId 科目ID
     * @param courseName 科目名
     * @param credits 単位数（1以上）
     * @param grade 成績（0.0～4.0）
     * @throws IllegalArgumentException 単位数または成績が範囲外の場合
     */
    public Course(String courseId, String courseName, int credits, double grade) {
        this.courseId = Objects.requireNonNull(courseId, "科目IDはnullにできません");
        this.courseName = Objects.requireNonNull(courseName, "科目名はnullにできません");
        setCredits(credits);
        setGrade(grade);
    }
    
    // Getters and Setters
    public String getCourseId() {
        return courseId;
    }
    
    public void setCourseId(String courseId) {
        this.courseId = Objects.requireNonNull(courseId, "科目IDはnullにできません");
    }
    
    public String getCourseName() {
        return courseName;
    }
    
    public void setCourseName(String courseName) {
        this.courseName = Objects.requireNonNull(courseName, "科目名はnullにできません");
    }
    
    public int getCredits() {
        return credits;
    }
    
    public void setCredits(int credits) {
        if (credits < 1) {
            throw new IllegalArgumentException("単位数は1以上である必要があります");
        }
        this.credits = credits;
    }
    
    public double getGrade() {
        return grade;
    }
    
    public void setGrade(double grade) {
        if (grade < 0.0 || grade > 4.0) {
            throw new IllegalArgumentException("成績は0.0～4.0の範囲である必要があります");
        }
        this.grade = grade;
    }
    
    /**
     * この科目で取得したグレードポイントを計算します。
     * 
     * @return グレードポイント（成績 × 単位数）
     */
    public double getGradePoints() {
        return grade * credits;
    }
    
    @Override
    public String toString() {
        return String.format("Course[id=%s, name=%s, credits=%d, grade=%.1f]",
                courseId, courseName, credits, grade);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Course course = (Course) obj;
        return Objects.equals(courseId, course.courseId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(courseId);
    }
}