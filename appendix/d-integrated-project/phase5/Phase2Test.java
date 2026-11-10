// Phase2Test.java - フェーズ2のテスト
public class Phase2Test {
    public static void main(String[] args) {
        IO.println("=== フェーズ2: 継承とポリモーフィズムのテスト ===\n");
        
        // 通常の学生
        Student student = new Student("S001", "田中太郎", 20, "情報工学科");
        student.setGpa(3.5);
        IO.println("通常の学生: " + student);
        IO.println("役割: " + student.getRole());
        
        // 大学院生
        GraduateStudent gradStudent = new GraduateStudent("G001", "山田花子", 24, 
            "情報工学科", "人工知能");
        gradStudent.setAdvisor("鈴木教授");
        gradStudent.setGpa(3.8);
        IO.println("\n大学院生: " + gradStudent);
        IO.println("役割: " + gradStudent.getRole());
        
        // 成績管理可能な学生
        GradeableStudent gradeableStudent = new GradeableStudent("S002", "佐藤次郎", 21, "理学部");
        gradeableStudent.addGrade("数学", 3.5);
        gradeableStudent.addGrade("物理", 4.0);
        gradeableStudent.addGrade("化学", 3.0);
        
        IO.println("\n成績管理可能な学生: " + gradeableStudent);
        IO.println("成績一覧: " + gradeableStudent.getGrades());
        IO.println("計算されたGPA: " + gradeableStudent.getGpa());
        
        // ポリモーフィズムのデモ
        IO.println("\n=== ポリモーフィズムのデモ ===");
        Person[] people = {student, gradStudent, gradeableStudent};
        
        for (Person person : people) {
            IO.println(person.getName() + "の役割: " + person.getRole());
        }
    }
}