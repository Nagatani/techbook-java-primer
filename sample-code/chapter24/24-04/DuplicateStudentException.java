// DuplicateStudentException.java - 同じ学生IDで登録しようとした（利用者が直せる誤り）
public class DuplicateStudentException extends Exception {
    public DuplicateStudentException(String studentId) {
        super("学生IDが重複しています: " + studentId);
    }
}
