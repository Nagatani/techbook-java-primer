package com.example.datetime;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DateTimeUtilities {
    // よく使うフォーマッタ
    public static final DateTimeFormatter ISO_DATE_TIME_MILLIS = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    public static final DateTimeFormatter JAPANESE_DATE = 
        DateTimeFormatter.ofPattern("yyyy年MM月dd日");
    public static final DateTimeFormatter JAPANESE_DATE_TIME = 
        DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH時mm分ss秒");
    public static final DateTimeFormatter FILE_NAME_TIMESTAMP = 
        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    public static final DateTimeFormatter LOG_TIMESTAMP = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    public static void main(String[] args) {
        IO.println("=== java.time ユーティリティメソッド集 ===\n");
        
        // 1. 日付範囲の生成
        demonstrateDateRanges();
        
        // 2. 時刻の丸め処理
        demonstrateTimeRounding();
        
        // 3. 期間の計算
        demonstratePeriodCalculations();
        
        // 4. タイムゾーン変換
        demonstrateTimeZoneConversions();
        
        // 5. 便利なユーティリティ
        demonstrateUtilityMethods();
    }
    
    private static void demonstrateDateRanges() {
        IO.println("1. 日付範囲の生成");
        IO.println("-".repeat(40));
        
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate end = LocalDate.of(2024, 1, 7);
        
        // 日付のリストを生成
        List<LocalDate> dateRange = getDateRange(start, end);
        IO.println("日付範囲: " + dateRange);
        
        // 月初・月末
        LocalDate today = LocalDate.now();
        IO.println("\n現在: " + today);
        IO.println("月初: " + getFirstDayOfMonth(today));
        IO.println("月末: " + getLastDayOfMonth(today));
        IO.println("四半期初: " + getFirstDayOfQuarter(today));
        IO.println("四半期末: " + getLastDayOfQuarter(today));
        
        // 週の範囲
        LocalDate weekStart = getWeekStart(today);
        LocalDate weekEnd = getWeekEnd(today);
        IO.println("\n週の範囲: " + weekStart + " ～ " + weekEnd);
        IO.println();
    }
    
    private static void demonstrateTimeRounding() {
        IO.println("2. 時刻の丸め処理");
        IO.println("-".repeat(40));
        
        LocalDateTime now = LocalDateTime.now();
        IO.println("現在時刻: " + now);
        IO.println("分単位: " + roundToMinute(now));
        IO.println("5分単位: " + roundToMinutes(now, 5));
        IO.println("15分単位: " + roundToMinutes(now, 15));
        IO.println("時間単位: " + roundToHour(now));
        IO.println("日単位: " + roundToDay(now));
        
        // 切り捨て・切り上げ
        IO.println("\n切り捨て（分）: " + truncateToMinute(now));
        IO.println("切り上げ（分）: " + ceilToMinute(now));
        IO.println();
    }
    
    private static void demonstratePeriodCalculations() {
        IO.println("3. 期間の計算");
        IO.println("-".repeat(40));
        
        LocalDate birthDate = LocalDate.of(2000, 1, 15);
        LocalDate today = LocalDate.now();
        
        // 年齢計算
        AgeInfo age = calculateAge(birthDate, today);
        IO.println("生年月日: " + birthDate);
        IO.println("年齢: " + age);
        
        // 作業時間の計算
        LocalDateTime workStart = LocalDateTime.of(2024, 1, 15, 9, 0);
        LocalDateTime workEnd = LocalDateTime.of(2024, 1, 15, 17, 30);
        
        WorkDuration workDuration = calculateWorkDuration(workStart, workEnd);
        IO.println("\n勤務時間: " + workDuration);
        
        // 日付の差分（わかりやすい形式）
        LocalDate date1 = LocalDate.of(2024, 1, 1);
        LocalDate date2 = LocalDate.of(2025, 3, 15);
        String diff = getReadableDateDifference(date1, date2);
        IO.println("\n" + date1 + " から " + date2 + " まで: " + diff);
        IO.println();
    }
    
    private static void demonstrateTimeZoneConversions() {
        IO.println("4. タイムゾーン変換");
        IO.println("-".repeat(40));
        
        LocalDateTime localTime = LocalDateTime.of(2024, 6, 15, 14, 30);
        
        // 各地の時刻に変換
        Map<String, ZonedDateTime> worldTimes = getWorldTimes(localTime);
        worldTimes.forEach((city, time) -> 
            IO.println(city + ": " + 
                time.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z"))));
        
        // UTC変換
        Instant utcTime = toUTC(localTime, ZoneId.of("Asia/Tokyo"));
        IO.println("\nUTC時刻: " + utcTime);
        
        // エポック秒
        long epochSeconds = toEpochSeconds(localTime, ZoneId.systemDefault());
        IO.println("エポック秒: " + epochSeconds);
        IO.println();
    }
    
    private static void demonstrateUtilityMethods() {
        IO.println("5. 便利なユーティリティ");
        IO.println("-".repeat(40));
        
        // 日付の判定
        LocalDate testDate = LocalDate.of(2024, 2, 29);
        IO.println(testDate + " はうるう年？ " + isLeapYear(testDate));
        IO.println(testDate + " は月末？ " + isLastDayOfMonth(testDate));
        
        // 次の特定曜日
        LocalDate today = LocalDate.now();
        IO.println("\n今日: " + today + " (" + today.getDayOfWeek() + ")");
        IO.println("次の月曜日: " + getNext(today, DayOfWeek.MONDAY));
        IO.println("次の金曜日: " + getNext(today, DayOfWeek.FRIDAY));
        
        // SQL互換フォーマット
        LocalDateTime now = LocalDateTime.now();
        IO.println("\nSQL Timestamp形式: " + toSqlTimestamp(now));
        IO.println("SQL Date形式: " + toSqlDate(now.toLocalDate()));
        
        // ミリ秒精度の処理
        LocalDateTime timeWithNanos = LocalDateTime.now();
        LocalDateTime timeWithoutNanos = removeNanos(timeWithNanos);
        IO.println("\nナノ秒あり: " + timeWithNanos);
        IO.println("ナノ秒なし: " + timeWithoutNanos);
    }
    
    // ===== ユーティリティメソッドの実装 =====
    
    // 日付範囲の生成
    public static List<LocalDate> getDateRange(LocalDate start, LocalDate end) {
        return start.datesUntil(end.plusDays(1))
            .collect(Collectors.toList());
    }
    
    public static LocalDate getFirstDayOfMonth(LocalDate date) {
        return date.with(TemporalAdjusters.firstDayOfMonth());
    }
    
    public static LocalDate getLastDayOfMonth(LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }
    
    public static LocalDate getFirstDayOfQuarter(LocalDate date) {
        int quarter = (date.getMonthValue() - 1) / 3;
        return LocalDate.of(date.getYear(), quarter * 3 + 1, 1);
    }
    
    public static LocalDate getLastDayOfQuarter(LocalDate date) {
        return getFirstDayOfQuarter(date).plusMonths(3).minusDays(1);
    }
    
    public static LocalDate getWeekStart(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }
    
    public static LocalDate getWeekEnd(LocalDate date) {
        return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }
    
    // 時刻の丸め処理
    public static LocalDateTime roundToMinute(LocalDateTime dateTime) {
        int seconds = dateTime.getSecond();
        return dateTime.truncatedTo(ChronoUnit.MINUTES)
            .plusMinutes(seconds >= 30 ? 1 : 0);
    }
    
    public static LocalDateTime roundToMinutes(LocalDateTime dateTime, int minutes) {
        int minute = dateTime.getMinute();
        int roundedMinute = ((minute + minutes / 2) / minutes) * minutes;
        return dateTime.truncatedTo(ChronoUnit.HOURS)
            .plusMinutes(roundedMinute);
    }
    
    public static LocalDateTime roundToHour(LocalDateTime dateTime) {
        int minutes = dateTime.getMinute();
        return dateTime.truncatedTo(ChronoUnit.HOURS)
            .plusHours(minutes >= 30 ? 1 : 0);
    }
    
    public static LocalDateTime roundToDay(LocalDateTime dateTime) {
        int hours = dateTime.getHour();
        return dateTime.truncatedTo(ChronoUnit.DAYS)
            .plusDays(hours >= 12 ? 1 : 0);
    }
    
    public static LocalDateTime truncateToMinute(LocalDateTime dateTime) {
        return dateTime.truncatedTo(ChronoUnit.MINUTES);
    }
    
    public static LocalDateTime ceilToMinute(LocalDateTime dateTime) {
        LocalDateTime truncated = dateTime.truncatedTo(ChronoUnit.MINUTES);
        return dateTime.equals(truncated) ? truncated : truncated.plusMinutes(1);
    }
    
    // 期間計算
    public static AgeInfo calculateAge(LocalDate birthDate, LocalDate currentDate) {
        Period period = Period.between(birthDate, currentDate);
        long totalDays = ChronoUnit.DAYS.between(birthDate, currentDate);
        return new AgeInfo(period.getYears(), period.getMonths(), 
                          period.getDays(), totalDays);
    }
    
    public static WorkDuration calculateWorkDuration(LocalDateTime start, 
                                                    LocalDateTime end) {
        Duration duration = Duration.between(start, end);
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        return new WorkDuration(hours, minutes, duration.toMinutes());
    }
    
    public static String getReadableDateDifference(LocalDate start, LocalDate end) {
        Period period = Period.between(start, end);
        List<String> parts = new ArrayList<>();
        
        if (period.getYears() > 0) {
            parts.add(period.getYears() + "年");
        }
        if (period.getMonths() > 0) {
            parts.add(period.getMonths() + "ヶ月");
        }
        if (period.getDays() > 0) {
            parts.add(period.getDays() + "日");
        }
        
        return parts.isEmpty() ? "0日" : String.join("", parts);
    }
    
    // タイムゾーン変換
    public static Map<String, ZonedDateTime> getWorldTimes(LocalDateTime localTime) {
        Map<String, ZonedDateTime> times = new LinkedHashMap<>();
        ZonedDateTime baseTime = localTime.atZone(ZoneId.systemDefault());
        
        times.put("東京", baseTime.withZoneSameInstant(ZoneId.of("Asia/Tokyo")));
        times.put("ロンドン", baseTime.withZoneSameInstant(ZoneId.of("Europe/London")));
        times.put("ニューヨーク", baseTime.withZoneSameInstant(ZoneId.of("America/New_York")));
        times.put("ロサンゼルス", baseTime.withZoneSameInstant(ZoneId.of("America/Los_Angeles")));
        times.put("シドニー", baseTime.withZoneSameInstant(ZoneId.of("Australia/Sydney")));
        
        return times;
    }
    
    public static Instant toUTC(LocalDateTime localDateTime, ZoneId zoneId) {
        return localDateTime.atZone(zoneId).toInstant();
    }
    
    public static long toEpochSeconds(LocalDateTime localDateTime, ZoneId zoneId) {
        return localDateTime.atZone(zoneId).toEpochSecond();
    }
    
    // その他のユーティリティ
    public static boolean isLeapYear(LocalDate date) {
        return date.isLeapYear();
    }
    
    public static boolean isLastDayOfMonth(LocalDate date) {
        return date.equals(date.with(TemporalAdjusters.lastDayOfMonth()));
    }
    
    public static LocalDate getNext(LocalDate date, DayOfWeek dayOfWeek) {
        return date.with(TemporalAdjusters.next(dayOfWeek));
    }
    
    public static String toSqlTimestamp(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    public static String toSqlDate(LocalDate date) {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
    
    public static LocalDateTime removeNanos(LocalDateTime dateTime) {
        return dateTime.truncatedTo(ChronoUnit.SECONDS);
    }
    
    // ヘルパークラス
    static class AgeInfo {
        final int years;
        final int months;
        final int days;
        final long totalDays;
        
        AgeInfo(int years, int months, int days, long totalDays) {
            this.years = years;
            this.months = months;
            this.days = days;
            this.totalDays = totalDays;
        }
        
        @Override
        public String toString() {
            return String.format("%d歳%dヶ月%d日 (合計%d日)", 
                years, months, days, totalDays);
        }
    }
    
    static class WorkDuration {
        final long hours;
        final long minutes;
        final long totalMinutes;
        
        WorkDuration(long hours, long minutes, long totalMinutes) {
            this.hours = hours;
            this.minutes = minutes;
            this.totalMinutes = totalMinutes;
        }
        
        @Override
        public String toString() {
            return String.format("%d時間%d分 (合計%d分)", 
                hours, minutes, totalMinutes);
        }
    }
}