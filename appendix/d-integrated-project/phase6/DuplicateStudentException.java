// DuplicateStudentException.java
public class DuplicateStudentException extends Exception {
    public DuplicateStudentException(String studentId) {
        super("学生IDが重複しています: ID=" + studentId);
    }
}