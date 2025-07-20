// Student.java
public class Student {
    private String studentId;
    private String name;
    private int age;
    private String department;
    private double gpa;
    
    // コンストラクタ
    public Student(String studentId, String name, int age, String department) {
        this.studentId = studentId;
        this.name = name;
        this.age = age;
        this.department = department;
        this.gpa = 0.0;
    }
    
    // ゲッター
    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getDepartment() { return department; }
    public double getGpa() { return gpa; }
    
    // セッター
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { 
        if (age > 0) this.age = age; 
    }
    public void setDepartment(String department) { this.department = department; }
    public void setGpa(double gpa) { 
        if (gpa >= 0 && gpa <= 4.0) this.gpa = gpa; 
    }
    
    @Override
    public String toString() {
        return String.format("Student[ID=%s, Name=%s, Age=%d, Dept=%s, GPA=%.2f]",
            studentId, name, age, department, gpa);
    }
}