public class Student {
    private String name;
    private int age;
    
    private Student(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    public static Student createStudent(String name, int age) {
        if (age < 0 || age > 150) {
            throw new IllegalArgumentException("Invalid age");
        }
        return new Student(name, age);
    }
}
