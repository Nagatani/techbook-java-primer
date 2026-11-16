// Student.java - 改良版
public class Student extends Person {
    protected String department;
    protected double gpa;
    
    public Student(String studentId, String name, int age, String department) {
        super(studentId, name, age);
        this.department = department;
        this.gpa = 0.0;
    }
    
    @Override
    public String getRole() {
        return "Student";
    }
    
    // Student固有のメソッド
    public String getDepartment() { return department; }
    public double getGpa() { return gpa; }
    
    public void setDepartment(String department) { this.department = department; }
    public void setGpa(double gpa) {
        if (gpa >= 0 && gpa <= 4.0) this.gpa = gpa;
    }
    
    @Override
    public String toString() {
        return String.format("%s[ID=%s, Name=%s, Age=%d, Dept=%s, GPA=%.2f]",
            getRole(), id, name, age, department, gpa);
    }
}