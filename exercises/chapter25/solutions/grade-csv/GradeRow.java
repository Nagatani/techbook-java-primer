// GradeRow.java - 成績のCSVの1行から作った値（どの学生の成績か）
public record GradeRow(String studentId, Grade grade) {
}
