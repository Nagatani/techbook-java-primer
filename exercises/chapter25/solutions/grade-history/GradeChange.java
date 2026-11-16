// GradeChange.java - 成績の訂正の記録（科目名・訂正前・訂正後）
public record GradeChange(String course, Grade before, Grade after) {
}
