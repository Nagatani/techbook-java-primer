public class Student {
    private String name;  // ①
    private int age;      // ①
    private double gpa;   // ①
    
    public Student(String name, int age, double gpa) {  // ②
        this.name = name;
        this.age = age;
        this.gpa = gpa;
    }
    
    public void printInfo() {  // ③
        System.out.println("名前: " + name + ", 年齢: " + age + ", GPA: " + gpa);
    }
}
