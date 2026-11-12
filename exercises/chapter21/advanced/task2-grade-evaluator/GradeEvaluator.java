/**
 * 科目の点数と出席から、評価と合否を決める
 */
public class GradeEvaluator {
    /** 合格に必要な点数 */
    public static final int PASSING_SCORE = 60;

    /**
     * 点数（0〜100）から評価を返す
     * 90以上はS、80以上はA、70以上はB、60以上はC、60未満はD
     */
    public String grade(int score) {
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("点数は0〜100です: " + score);
        }
        if (score >= 90) {
            return "S";
        } else if (score >= 80) {
            return "A";
        } else if (score >= 70) {
            return "B";
        } else if (score >= PASSING_SCORE) {
            return "C";
        }
        return "D";
    }

    /**
     * 出席が授業回数の3分の2以上かどうか
     * 割り算で比べると小数の誤差や切り捨てが入るので、掛け算で比べる
     */
    public boolean hasEnoughAttendance(int attended, int totalClasses) {
        if (totalClasses <= 0) {
            throw new IllegalArgumentException("授業回数は1以上です: " + totalClasses);
        }
        if (attended < 0 || attended > totalClasses) {
            throw new IllegalArgumentException("出席回数が範囲外です: " + attended);
        }
        return attended * 3 >= totalClasses * 2;
    }

    /** 点数が60以上で、出席が足りていれば合格 */
    public boolean isPassed(int score, int attended, int totalClasses) {
        boolean enoughAttendance = hasEnoughAttendance(attended, totalClasses);
        return !grade(score).equals("D") && enoughAttendance;
    }
}
