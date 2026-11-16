// GradeHistoryCheck.java - 成績の訂正の履歴を確かめる
public class GradeHistoryCheck {
    public static void main(String[] args) {
        Student taro = new Student("S001", "山田太郎", "工学部", new Undergraduate(2025));
        taro.recordGrade(new Grade("線形代数", 4, 2.0));
        taro.recordGrade(new Grade("プログラミング演習", 2, 4.0));
        IO.println("訂正前のGPA: %.2f".formatted(taro.gpa()));

        taro.recordGrade(new Grade("線形代数", 4, 3.0)); // 採点の誤りを訂正する
        taro.recordGrade(new Grade("線形代数", 4, 3.0)); // 同じ成績をもう一度記録しても履歴は増えない
        IO.println("訂正後のGPA: %.2f".formatted(taro.gpa()));

        for (GradeChange change : taro.getGradeHistory()) {
            IO.println("%s: %.1f → %.1f".formatted(
                change.course(), change.before().point(), change.after().point()));
        }
        check(taro.getGradeHistory().size() == 1, "履歴は1件");
        check(Math.abs(taro.gpa() - 10.0 / 3) < 1e-9, "GPAは最新の成績から計算する");
        IO.println("すべての確認に成功しました");
    }

    private static void check(boolean ok, String label) {
        if (!ok) {
            throw new AssertionError("失敗: " + label);
        }
    }
}
