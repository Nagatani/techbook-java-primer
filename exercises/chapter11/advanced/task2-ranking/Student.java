/**
 * 学生の成績（学籍番号・氏名・学年・点数）
 */
public record Student(String id, String name, int grade, int score) {
    @Override
    public String toString() {
        return id + " " + name + "（" + grade + "年）" + score + "点";
    }
}
