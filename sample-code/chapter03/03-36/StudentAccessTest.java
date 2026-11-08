// StudentAccessTest.java
public class StudentAccessTest {
    public static void main(String[] args) {
        Student student = new Student("山田太郎", 22, 3.8);
        
        // publicメソッドは呼び出せる
        student.introduce();
        student.checkStatus();
        
        // privateフィールドには直接アクセスできない
        // System.out.println(student.name); // コンパイルエラー
        
        // privateメソッドも直接呼び出せない
        // student.isAdult(); // コンパイルエラー
    }
}
