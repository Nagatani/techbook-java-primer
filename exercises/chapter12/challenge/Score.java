/**
 * 1人の学生の1科目の点数
 */
public record Score(String studentId, String name, String className, String subject, int point) {
}
