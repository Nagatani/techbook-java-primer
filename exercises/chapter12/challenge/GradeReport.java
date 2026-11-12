import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 成績の集計。結果のMapはTreeMapにして、キーの順に並ぶようにする
 */
public class GradeReport {
    public static final int PASS_LINE = 60;
    private final List<Score> scores;

    public GradeReport(List<Score> scores) {
        this.scores = scores;
    }

    // 科目ごとの平均点
    public Map<String, Double> averageBySubject() {
        return scores.stream()
                .collect(Collectors.groupingBy(Score::subject, TreeMap::new,
                        Collectors.averagingInt(Score::point)));
    }

    // 科目ごとの最高点のScore
    public Map<String, Optional<Score>> bestBySubject() {
        return scores.stream()
                .collect(Collectors.groupingBy(Score::subject, TreeMap::new,
                        Collectors.maxBy(Comparator.comparingInt(Score::point))));
    }

    // クラスごとの平均点（全科目）
    public Map<String, Double> averageByClass() {
        return scores.stream()
                .collect(Collectors.groupingBy(Score::className, TreeMap::new,
                        Collectors.averagingInt(Score::point)));
    }

    // 学生ごとの合計点（「学籍番号 氏名」→ 合計）
    public Map<String, Integer> totalByStudent() {
        return scores.stream()
                .collect(Collectors.groupingBy(s -> s.studentId() + " " + s.name(), TreeMap::new,
                        Collectors.summingInt(Score::point)));
    }

    // 合計点の高い順の上位n人（同点は学籍番号の順）
    public List<String> ranking(int n) {
        return totalByStudent().entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(n)
                .map(e -> e.getKey() + " " + e.getValue() + "点")
                .toList();
    }

    // 科目ごとに、合格（true）と不合格（false）の人数
    public Map<String, Map<Boolean, Long>> passCountBySubject() {
        return scores.stream()
                .collect(Collectors.groupingBy(Score::subject, TreeMap::new,
                        Collectors.partitioningBy(s -> s.point() >= PASS_LINE, Collectors.counting())));
    }

    // 不合格の科目がある学生と、その科目（学籍番号の順）
    public Map<String, List<String>> failedSubjects() {
        return scores.stream()
                .filter(s -> s.point() < PASS_LINE)
                .collect(Collectors.groupingBy(s -> s.studentId() + " " + s.name(), TreeMap::new,
                        Collectors.mapping(Score::subject, Collectors.toList())));
    }

    // すべての科目で合格した学生がいるか
    public boolean anyoneAllPassed() {
        Map<String, List<String>> failed = failedSubjects();  // 先に1回だけ求めておく
        return totalByStudent().keySet().stream()
                .anyMatch(student -> !failed.containsKey(student));
    }

    // 全員の全科目がpoint点以上か
    public boolean everyoneAtLeast(int point) {
        return scores.stream().allMatch(s -> s.point() >= point);
    }

    // 学籍番号で学生の合計点を探す
    public Optional<Integer> totalOf(String studentId) {
        return totalByStudent().entrySet().stream()
                .filter(e -> e.getKey().startsWith(studentId + " "))
                .map(Map.Entry::getValue)
                .findFirst();
    }

    // 全員の全科目の点数の合計を、順次処理と並列処理で求める
    public int sumSequential() {
        return scores.stream().mapToInt(Score::point).sum();
    }

    public int sumParallel() {
        return scores.parallelStream().mapToInt(Score::point).sum();
    }
}
