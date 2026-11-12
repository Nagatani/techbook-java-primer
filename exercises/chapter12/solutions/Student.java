package chapter12.solutions;

/**
 * 学生のデータ（課題1の解答例）
 */
public class Student {
    private String name;
    private int age;
    private double gpa;
    private String department;

    public Student(String name, int age, double gpa, String department) {
        this.name = name;
        this.age = age;
        this.gpa = gpa;
        this.department = department;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public double getGpa() { return gpa; }
    public String getDepartment() { return department; }

    @Override
    public String toString() {
        return name + "（" + age + "歳、GPA " + gpa + "、" + department + "）";
    }
}
