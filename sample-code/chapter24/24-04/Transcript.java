// Transcript.java - 成績の一覧。GPAは保存せず、成績から計算する
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Transcript {
    // 科目名から成績を引く。登録した順に並べる
    private final Map<String, Grade> grades = new LinkedHashMap<>();

    // 同じ科目の成績があれば置き換える
    public void record(Grade grade) {
        grades.put(grade.course(), grade);
    }

    public boolean remove(String course) {
        return grades.remove(course) != null;
    }

    public List<Grade> grades() {
        return List.copyOf(grades.values());
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
