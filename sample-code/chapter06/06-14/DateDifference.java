// DateDifference.java - 2つの日付・時刻の差を求める
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;

public class DateDifference {
    public static void main(String[] args) {
        LocalDate borrowed = LocalDate.of(2026, 4, 20);
        // 返却期限は14日後
        LocalDate due = borrowed.plusDays(14);
        LocalDate returned = LocalDate.of(2026, 5, 9);

        // 日数の差はChronoUnit.DAYS.betweenで求める
        long overdue = ChronoUnit.DAYS.between(due, returned);
        IO.println("返却期限: " + due + "、延滞: " + Math.max(overdue, 0)
                + "日");

        // 年・月・日に分けた期間はPeriod（日付どうし）
        Period age = Period.between(LocalDate.of(2006, 7, 15),
                LocalDate.of(2026, 4, 1));
        IO.println("年齢: " + age.getYears() + "歳" + age.getMonths()
                + "か月");

        // 時・分・秒の長さはDuration（時刻どうし）
        Duration exam = Duration.between(
            LocalDateTime.of(2026, 7, 27, 9, 0),
            LocalDateTime.of(2026, 7, 27, 10, 30));
        IO.println("試験時間: " + exam.toMinutes() + "分（" + exam + "）");
    }
}
