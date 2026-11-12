import java.util.Arrays;

/**
 * 1週間の時間割（月〜金、1日4コマ）を表す不変クラス
 *
 * 2次元配列は、受け取るときも渡すときも行ごとにコピーする（深いコピー）。
 * Lessonは不変なので、要素そのものはコピーしなくてよい。
 */
public final class Timetable {
    public static final int DAYS = 5;
    public static final int PERIODS = 4;
    // 曜日の番号（配列の添字）
    public static final int MON = 0;
    public static final int TUE = 1;
    public static final int WED = 2;
    public static final int THU = 3;
    public static final int FRI = 4;
    // 配列はfinalでも中身を書き換えられるので、publicにしない
    private static final String[] DAY_NAMES = {"月", "火", "水", "木", "金"};

    private final Lesson[][] lessons;

    public Timetable(Lesson[][] source) {
        if (source == null || source.length != DAYS) {
            throw new IllegalArgumentException("5日分の時間割を指定してください");
        }
        Lesson[][] copy = new Lesson[DAYS][];
        for (int day = 0; day < DAYS; day++) {
            if (source[day] == null || source[day].length != PERIODS) {
                throw new IllegalArgumentException("1日は4コマにしてください");
            }
            copy[day] = Arrays.copyOf(source[day], PERIODS);  // 行ごとにコピーする
        }
        this.lessons = copy;
    }

    // 空の時間割
    public Timetable() {
        this(new Lesson[DAYS][PERIODS]);
    }

    public static String dayName(int day) {
        return DAY_NAMES[day];
    }

    // dayはMON〜FRI、periodは1〜4（時限）。授業のないコマはnull
    public Lesson getLesson(int day, int period) {
        return lessons[day][period - 1];
    }

    // 配列を渡すときもコピーを返す
    public Lesson[][] toArray() {
        Lesson[][] copy = new Lesson[DAYS][];
        for (int day = 0; day < DAYS; day++) {
            copy[day] = Arrays.copyOf(lessons[day], PERIODS);
        }
        return copy;
    }

    // 1コマを変えた新しい時間割を返す。この時間割は変わらない
    public Timetable withLesson(int day, int period, Lesson lesson) {
        Lesson[][] copy = toArray();
        copy[day][period - 1] = lesson;
        return new Timetable(copy);
    }

    public Timetable withoutLesson(int day, int period) {
        return withLesson(day, period, null);
    }

    public int countLessons() {
        int count = 0;
        for (Lesson[] row : lessons) {
            for (Lesson lesson : row) {
                if (lesson != null) {
                    count++;
                }
            }
        }
        return count;
    }

    public void print() {
        for (int day = 0; day < DAYS; day++) {
            StringBuilder line = new StringBuilder("  " + DAY_NAMES[day] + ":");
            for (Lesson lesson : lessons[day]) {
                line.append(" ").append(lesson == null ? "----" : lesson.toString());
            }
            IO.println(line);
        }
        IO.println("  （" + countLessons() + "コマ）");
    }
}
