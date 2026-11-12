import java.util.Arrays;

/**
 * よくない例: 外側の配列だけをコピーした時間割
 *
 * 行（内側の配列）は呼び出し側と共有されたままなので、
 * 呼び出し側が元の配列の要素を書き換えると、この時間割も変わってしまう。
 */
public final class ShallowTimetable {
    private final Lesson[][] lessons;

    public ShallowTimetable(Lesson[][] source) {
        this.lessons = Arrays.copyOf(source, source.length);  // 外側の配列だけのコピー（浅いコピー）
    }

    public Lesson getLesson(int day, int period) {
        return lessons[day][period - 1];
    }
}
