import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 1日の歩数
 */
public record DailySteps(LocalDate date, int steps) {
    public DailySteps {
        if (steps < 0) {
            throw new IllegalArgumentException("歩数が負です: " + date);
        }
    }

    /**
     * fromからto（toの日を含む）までの見本の記録を作る。
     * 何度実行しても同じ値になるよう、乱数を使わずに日付から計算する。
     */
    public static List<DailySteps> sample(LocalDate from, LocalDate to) {
        List<DailySteps> list = new ArrayList<>();
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            int steps = 4000 + (d.getDayOfYear() * 37 % 50) * 100;
            DayOfWeek w = d.getDayOfWeek();
            if (w == DayOfWeek.SATURDAY || w == DayOfWeek.SUNDAY) {
                steps += 2000;   // 休日はよく歩く
            }
            list.add(new DailySteps(d, steps));
        }
        return list;
    }
}
