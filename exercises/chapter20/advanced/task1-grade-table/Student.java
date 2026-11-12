/**
 * 学生1人の成績。科目の点数は0〜100点。
 */
public class Student {
    public static final String[] SUBJECTS = {"国語", "数学", "英語"};
    public static final int PASSING_SCORE = 60;

    private final String id;
    private final String name;
    private final int[] scores;

    public Student(String id, String name, int japanese, int math, int english) {
        this.id = id;
        this.name = name;
        this.scores = new int[SUBJECTS.length];
        setScore(0, japanese);
        setScore(1, math);
        setScore(2, english);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getScore(int subject) {
        return scores[subject];
    }

    public void setScore(int subject, int score) {
        if (!isValidScore(score)) {
            throw new IllegalArgumentException("点数は0〜100で指定してください: " + score);
        }
        scores[subject] = score;
    }

    public int getTotal() {
        int total = 0;
        for (int s : scores) {
            total += s;
        }
        return total;
    }

    /** 60点未満の科目があるか */
    public boolean hasFailingSubject() {
        for (int s : scores) {
            if (s < PASSING_SCORE) {
                return true;
            }
        }
        return false;
    }

    public static boolean isValidScore(int score) {
        return 0 <= score && score <= 100;
    }
}
