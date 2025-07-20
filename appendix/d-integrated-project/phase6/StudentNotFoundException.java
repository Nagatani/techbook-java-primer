// StudentNotFoundException.java
public class StudentNotFoundException extends Exception {
    public StudentNotFoundException(String studentId) {
        super("学生が見つかりません: ID=" + studentId);
    }
}