import java.time.LocalDate;

/**
 * 1か月分のカレンダーのマス目を計算するクラス（画面に依存しないロジック）
 *
 * カレンダーは日曜始まりの7列で、6行（42マス）あればどの月も収まります。
 */
public class MonthCalendar {
    public static final int COLUMNS = 7;
    public static final int CELLS = 42;

    private final int year;
    private final int month;

    public MonthCalendar(int year, int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("月は1〜12で指定してください: " + month);
        }
        this.year = year;
        this.month = month;
    }

    public String getTitle() {
        return year + "年" + month + "月";
    }

    /**
     * 1日が何列目（日曜を0とする）に来るかを返す
     * getDayOfWeek().getValue()は月曜が1、日曜が7なので、7で割った余りにする
     */
    public int getFirstColumn() {
        return LocalDate.of(year, month, 1).getDayOfWeek().getValue() % COLUMNS;
    }

    public int getDaysInMonth() {
        return LocalDate.of(year, month, 1).lengthOfMonth();
    }

    /**
     * 42マスのそれぞれに入る日付を返す。日付のないマスは0
     */
    public int[] getCells() {
        int[] cells = new int[CELLS];
        int first = getFirstColumn();
        for (int day = 1; day <= getDaysInMonth(); day++) {
            cells[first + day - 1] = day;
        }
        return cells;
    }
}
