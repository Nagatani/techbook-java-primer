// Transcript.java - 成績の一覧と、訂正の履歴
// GPAは最新の成績から計算し、訂正前の成績は履歴として残す（計算で求められない「事実」なので保存する）
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Transcript {
    private final Map<String, Grade> grades = new LinkedHashMap<>();
    private final List<GradeChange> history = new ArrayList<>();

    // 同じ科目の成績があれば置き換え、置き換えた前後を履歴に残す
    public void record(Grade grade) {
        Grade before = grades.put(grade.course(), grade);
        if (before != null && !before.equals(grade)) {
            history.add(new GradeChange(grade.course(), before, grade));
        }
    }

    public boolean remove(String course) {
        return grades.remove(course) != null;
    }

    public List<Grade> grades() {
        return List.copyOf(grades.values());
    }

    // 訂正した順に返す
    public List<GradeChange> history() {
        return List.copyOf(history);
    }

    // 単位数で重み付けした平均（成績がなければ0.0）
    public double gpa() {
        int totalCredits = 0;
        double weightedSum = 0.0;
        for (Grade grade : grades.values()) {
            totalCredits += grade.credits();
            weightedSum += grade.point() * grade.credits();
        }
        return totalCredits == 0 ? 0.0 : weightedSum / totalCredits;
    }
}
