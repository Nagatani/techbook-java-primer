import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class BusinessDateCalculator {
    // 日本の祝日（2024年の例）
    private static final Set<LocalDate> JAPANESE_HOLIDAYS_2024 = Set.of(
        LocalDate.of(2024, 1, 1),    // 元日
        LocalDate.of(2024, 1, 8),    // 成人の日
        LocalDate.of(2024, 2, 11),   // 建国記念の日
        LocalDate.of(2024, 2, 12),   // 振替休日
        LocalDate.of(2024, 2, 23),   // 天皇誕生日
        LocalDate.of(2024, 3, 20),   // 春分の日
        LocalDate.of(2024, 4, 29),   // 昭和の日
        LocalDate.of(2024, 5, 3),    // 憲法記念日
        LocalDate.of(2024, 5, 4),    // みどりの日
        LocalDate.of(2024, 5, 5),    // こどもの日
        LocalDate.of(2024, 5, 6),    // 振替休日
        LocalDate.of(2024, 7, 15),   // 海の日
        LocalDate.of(2024, 8, 11),   // 山の日
        LocalDate.of(2024, 8, 12),   // 振替休日
        LocalDate.of(2024, 9, 16),   // 敬老の日
        LocalDate.of(2024, 9, 22),   // 秋分の日
        LocalDate.of(2024, 9, 23),   // 振替休日
        LocalDate.of(2024, 10, 14),  // スポーツの日
        LocalDate.of(2024, 11, 3),   // 文化の日
        LocalDate.of(2024, 11, 4),   // 振替休日
        LocalDate.of(2024, 11, 23)   // 勤労感謝の日
    );
    
    public static void main(String[] args) {
        System.out.println("=== 営業日計算デモ ===\n");
        
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 31);
        
        // 1. 営業日数の計算
        demonstrateBusinessDayCount(startDate, endDate);
        
        // 2. 次の営業日を求める
        demonstrateNextBusinessDay();
        
        // 3. N営業日後を計算
        demonstrateAddBusinessDays();
        
        // 4. 月間営業日カレンダー
        demonstrateMonthlyBusinessCalendar();
        
        // 5. プロジェクト期限計算
        demonstrateProjectDeadline();
    }
    
    private static void demonstrateBusinessDayCount(LocalDate start, LocalDate end) {
        System.out.println("1. 営業日数の計算");
        System.out.println("-".repeat(40));
        
        long totalDays = ChronoUnit.DAYS.between(start, end) + 1;
        long businessDays = countBusinessDays(start, end);
        long weekends = countWeekends(start, end);
        long holidays = countHolidays(start, end);
        
        System.out.println("期間: " + start + " ～ " + end);
        System.out.println("総日数: " + totalDays + "日");
        System.out.println("営業日数: " + businessDays + "日");
        System.out.println("週末日数: " + weekends + "日");
        System.out.println("祝日数: " + holidays + "日");
        System.out.println("営業日率: " + 
            String.format("%.1f%%", (double) businessDays / totalDays * 100));
        System.out.println();
    }
    
    private static void demonstrateNextBusinessDay() {
        System.out.println("2. 次の営業日を求める");
        System.out.println("-".repeat(40));
        
        LocalDate[] testDates = {
            LocalDate.of(2024, 1, 5),   // 金曜日
            LocalDate.of(2024, 1, 6),   // 土曜日
            LocalDate.of(2024, 1, 7),   // 日曜日
            LocalDate.of(2024, 5, 3),   // 祝日（憲法記念日）
            LocalDate.of(2024, 12, 31)  // 大晦日
        };
        
        for (LocalDate date : testDates) {
            LocalDate nextBizDay = getNextBusinessDay(date);
            System.out.println(date + " (" + date.getDayOfWeek() + ") → " +
                             "次の営業日: " + nextBizDay + 
                             " (" + nextBizDay.getDayOfWeek() + ")");
        }
        System.out.println();
    }
    
    private static void demonstrateAddBusinessDays() {
        System.out.println("3. N営業日後を計算");
        System.out.println("-".repeat(40));
        
        LocalDate baseDate = LocalDate.of(2024, 1, 1);
        int[] daysToAdd = {1, 5, 10, 20, 30};
        
        System.out.println("基準日: " + baseDate + " (" + baseDate.getDayOfWeek() + ")");
        System.out.println();
        
        for (int days : daysToAdd) {
            LocalDate resultDate = addBusinessDays(baseDate, days);
            long calendarDays = ChronoUnit.DAYS.between(baseDate, resultDate);
            System.out.println(days + "営業日後: " + resultDate + 
                             " (" + resultDate.getDayOfWeek() + ")" +
                             " [暦日数: " + calendarDays + "日]");
        }
        System.out.println();
    }
    
    private static void demonstrateMonthlyBusinessCalendar() {
        System.out.println("4. 月間営業日カレンダー（2024年5月）");
        System.out.println("-".repeat(40));
        
        YearMonth yearMonth = YearMonth.of(2024, 5);
        printBusinessCalendar(yearMonth);
        
        // 月間統計
        LocalDate monthStart = yearMonth.atDay(1);
        LocalDate monthEnd = yearMonth.atEndOfMonth();
        long businessDays = countBusinessDays(monthStart, monthEnd);
        
        System.out.println("\n月間統計:");
        System.out.println("営業日数: " + businessDays + "日");
        System.out.println("非営業日数: " + (yearMonth.lengthOfMonth() - businessDays) + "日");
        System.out.println();
    }
    
    private static void demonstrateProjectDeadline() {
        System.out.println("5. プロジェクト期限計算");
        System.out.println("-".repeat(40));
        
        LocalDate projectStart = LocalDate.of(2024, 1, 10);
        int[] workDaysRequired = {10, 20, 45, 60};
        
        System.out.println("プロジェクト開始日: " + projectStart);
        System.out.println();
        
        for (int workDays : workDaysRequired) {
            ProjectSchedule schedule = calculateProjectSchedule(projectStart, workDays);
            System.out.println("必要営業日数: " + workDays + "日");
            System.out.println("  期限: " + schedule.deadline);
            System.out.println("  経過暦日数: " + schedule.calendarDays + "日");
            System.out.println("  含まれる週末: " + schedule.weekends + "日");
            System.out.println("  含まれる祝日: " + schedule.holidays + "日");
            System.out.println();
        }
    }
    
    // ユーティリティメソッド
    private static boolean isBusinessDay(LocalDate date) {
        return !isWeekend(date) && !isHoliday(date);
    }
    
    private static boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }
    
    private static boolean isHoliday(LocalDate date) {
        // 年をまたぐ場合の処理が必要な実装では、複数年の祝日を管理
        return JAPANESE_HOLIDAYS_2024.contains(date);
    }
    
    private static long countBusinessDays(LocalDate start, LocalDate end) {
        long count = 0;
        LocalDate current = start;
        
        while (!current.isAfter(end)) {
            if (isBusinessDay(current)) {
                count++;
            }
            current = current.plusDays(1);
        }
        
        return count;
    }
    
    private static long countWeekends(LocalDate start, LocalDate end) {
        long count = 0;
        LocalDate current = start;
        
        while (!current.isAfter(end)) {
            if (isWeekend(current)) {
                count++;
            }
            current = current.plusDays(1);
        }
        
        return count;
    }
    
    private static long countHolidays(LocalDate start, LocalDate end) {
        long count = 0;
        LocalDate current = start;
        
        while (!current.isAfter(end)) {
            if (isHoliday(current) && !isWeekend(current)) {
                count++;
            }
            current = current.plusDays(1);
        }
        
        return count;
    }
    
    private static LocalDate getNextBusinessDay(LocalDate date) {
        LocalDate next = date.plusDays(1);
        while (!isBusinessDay(next)) {
            next = next.plusDays(1);
        }
        return next;
    }
    
    private static LocalDate addBusinessDays(LocalDate date, int businessDaysToAdd) {
        LocalDate result = date;
        int addedDays = 0;
        
        while (addedDays < businessDaysToAdd) {
            result = result.plusDays(1);
            if (isBusinessDay(result)) {
                addedDays++;
            }
        }
        
        return result;
    }
    
    private static void printBusinessCalendar(YearMonth yearMonth) {
        System.out.println("\n日 月 火 水 木 金 土");
        
        LocalDate firstDay = yearMonth.atDay(1);
        int dayOfWeekValue = firstDay.getDayOfWeek().getValue();
        
        // 月初までの空白
        for (int i = 0; i < dayOfWeekValue % 7; i++) {
            System.out.print("   ");
        }
        
        // 各日を出力
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate date = yearMonth.atDay(day);
            
            if (isHoliday(date)) {
                System.out.printf("*%2d", day);  // 祝日
            } else if (isWeekend(date)) {
                System.out.printf("-%2d", day);  // 週末
            } else {
                System.out.printf("%3d", day);   // 営業日
            }
            
            if (date.getDayOfWeek() == DayOfWeek.SATURDAY) {
                System.out.println();
            }
        }
        System.out.println("\n\n凡例: * 祝日, - 週末");
    }
    
    private static ProjectSchedule calculateProjectSchedule(LocalDate start, 
                                                          int workDaysRequired) {
        LocalDate deadline = addBusinessDays(start, workDaysRequired - 1);
        long calendarDays = ChronoUnit.DAYS.between(start, deadline) + 1;
        long weekends = countWeekends(start, deadline);
        long holidays = countHolidays(start, deadline);
        
        return new ProjectSchedule(deadline, calendarDays, weekends, holidays);
    }
    
    static class ProjectSchedule {
        final LocalDate deadline;
        final long calendarDays;
        final long weekends;
        final long holidays;
        
        ProjectSchedule(LocalDate deadline, long calendarDays, 
                       long weekends, long holidays) {
            this.deadline = deadline;
            this.calendarDays = calendarDays;
            this.weekends = weekends;
            this.holidays = holidays;
        }
    }
}