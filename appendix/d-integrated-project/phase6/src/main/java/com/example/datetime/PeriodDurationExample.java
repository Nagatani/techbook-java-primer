import java.time.*;
import java.time.temporal.ChronoUnit;

public class PeriodDurationExample {
    public static void main(String[] args) {
        // Period - 日付ベースの期間
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        
        Period period = Period.between(startDate, endDate);
        System.out.println("期間: " + period); // P1Y2M14D
        System.out.println("詳細: " + period.getYears() + "年 " + 
                          period.getMonths() + "ヶ月 " + 
                          period.getDays() + "日");
        
        // Periodを使った日付の計算
        LocalDate futureDate = LocalDate.now().plus(Period.ofMonths(6));
        System.out.println("6ヶ月後: " + futureDate);
        
        // Duration - 時間ベースの期間
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(17, 30);
        
        Duration duration = Duration.between(startTime, endTime);
        System.out.println("\n勤務時間: " + duration); // PT8H30M
        System.out.println("詳細: " + duration.toHours() + "時間 " + 
                          duration.toMinutesPart() + "分");
        
        // 日時間の日数計算
        LocalDate birth = LocalDate.of(2000, 1, 1);
        long daysAlive = ChronoUnit.DAYS.between(birth, LocalDate.now());
        System.out.println("\n生まれてから " + daysAlive + " 日経過");
        
        // インスタント間の経過時間
        Instant start = Instant.now();
        // 何か処理
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        Instant end = Instant.now();
        
        Duration elapsed = Duration.between(start, end);
        System.out.println("処理時間: " + elapsed.toMillis() + "ミリ秒");
    }
}