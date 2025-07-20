// StudentTest.java - 簡単な動作確認
public class StudentTest {
    public static void main(String[] args) {
        Student student = new Student("S001", "田中太郎", 20, "情報工学科");
        
        System.out.println("学生情報: " + student);
        
        student.setGpa(3.7);
        System.out.println("GPA更新後: " + student.getGpa());
        
        // 異常値のテスト
        System.out.println("\n異常値テスト:");
        student.setGpa(-1.0);
        System.out.println("GPA(-1.0を設定): " + student.getGpa() + " (変更されない)");
        
        student.setAge(-5);
        System.out.println("年齢(-5を設定): " + student.getAge() + " (変更されない)");
        
        // 正常値の再設定
        student.setGpa(3.5);
        student.setAge(21);
        System.out.println("\n正常値設定後: " + student);
    }
}