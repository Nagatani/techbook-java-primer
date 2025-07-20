// InvalidGradeException.java
public class InvalidGradeException extends Exception {
    public InvalidGradeException(double grade) {
        super("無効な成績です: " + grade + " (0.0-4.0の範囲で入力してください)");
    }
}