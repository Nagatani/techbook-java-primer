// DateBasics.java - 日付と時刻の作成と計算。計算しても元のオブジェクトは変わらない
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DateBasics {
    public static void main(String[] args) {
        // 作り方: of（値を指定）、parse（ISO 8601の文字列から）、now（今の日時）
        // 月は1〜12で指定する
        LocalDate entrance = LocalDate.of(2026, 4, 1);
        LocalDate deadline = LocalDate.parse("2026-04-14");
        LocalDateTime lecture = LocalDateTime.of(2026, 4, 8, 9, 0);
        LocalTime start = lecture.toLocalTime();

        // 計算は新しいオブジェクトを返す（entranceは変わらない）
        LocalDate oneWeekLater = entrance.plusWeeks(1);
        IO.println("入学日: " + entrance + "、1週間後: " + oneWeekLater);
        IO.println("入学日の曜日: " + entrance.getDayOfWeek());
        IO.println("前期の終わり: " + entrance.plusMonths(4).minusDays(1));
        IO.println("初回の講義: " + lecture + "（開始 " + start + "）");

        // 比べる
        IO.println("締め切りは1週間後より後か: "
                + deadline.isAfter(oneWeekLater));
        IO.println("うるう年か: " + LocalDate.of(2028, 1, 1).isLeapYear());
    }
}
