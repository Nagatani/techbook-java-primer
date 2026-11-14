// DuplicateStudentException.java
public class DuplicateStudentException extends Exception {
    public DuplicateStudentException(String studentId) {
        super("学生IDが重複しています: ID=" + studentId);
    }
    
    // 原因となった例外（cause）を引き継ぐコンストラクター
    public DuplicateStudentException(String studentId, Throwable cause) {
        super("学生IDが重複しています: ID=" + studentId, cause);
    }
}