// Grade.java - 1科目の成績（科目名・単位数・評価点）
public record Grade(String course, int credits, double point) {
    public Grade {
        if (course == null || course.isBlank()) {
            throw new IllegalArgumentException("科目名が空です");
        }
        if (credits <= 0) {
            throw new IllegalArgumentException("単位数は1以上にします: "
                    + credits);
        }
        if (point < 0.0 || point > 4.0) {
            throw new IllegalArgumentException(
                    "評価点は0.0〜4.0にします: " + point);
        }
    }
}
