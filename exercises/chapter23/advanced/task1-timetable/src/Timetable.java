import java.util.ArrayList;
import java.util.List;

/**
 * 1週間の時間割（月〜金 × 1〜5限）
 */
public class Timetable {
    /** 曜日の並び */
    public static final String[] DAYS = {"月", "火", "水", "木", "金"};
    /** 1日の時限の数 */
    public static final int PERIODS = 5;

    // subjects[曜日][時限-1]。授業のないコマはnull
    private final String[][] subjects = new String[DAYS.length][PERIODS];

    /**
     * CSVの行（曜日,時限,科目名,教室）から時間割を作る
     * 空行と#で始まる行は読み飛ばす
     */
    public static Timetable parse(List<String> lines) {
        Timetable table = new Timetable();
        int lineNumber = 0;
        for (String line : lines) {
            lineNumber++;
            if (line.isBlank() || line.startsWith("#")) {
                continue;
            }
            String[] cols = line.split(",");
            if (cols.length != 4) {
                throw new IllegalArgumentException(lineNumber + "行目の列の数が4ではありません: " + line);
            }
            int day = dayIndex(cols[0].trim());
            int period = Integer.parseInt(cols[1].trim());
            if (day < 0 || period < 1 || period > PERIODS) {
                throw new IllegalArgumentException(lineNumber + "行目の曜日か時限が正しくありません: " + line);
            }
            table.subjects[day][period - 1] = cols[2].trim() + "(" + cols[3].trim() + ")";
        }
        return table;
    }

    /** 曜日の文字から添字を返す。見つからなければ-1 */
    public static int dayIndex(String day) {
        for (int i = 0; i < DAYS.length; i++) {
            if (DAYS[i].equals(day)) {
                return i;
            }
        }
        return -1;
    }

    /** 指定した曜日の授業を「1限 科目(教室)」の形で返す */
    public List<String> lessonsOf(String day) {
        int d = dayIndex(day);
        if (d < 0) {
            throw new IllegalArgumentException("曜日は月〜金で指定してください: " + day);
        }
        List<String> result = new ArrayList<>();
        for (int p = 0; p < PERIODS; p++) {
            if (subjects[d][p] != null) {
                result.add((p + 1) + "限 " + subjects[d][p]);
            }
        }
        return result;
    }

    /** 1週間の授業のコマ数 */
    public int countLessons() {
        int count = 0;
        for (String[] day : subjects) {
            for (String s : day) {
                if (s != null) {
                    count++;
                }
            }
        }
        return count;
    }
}
