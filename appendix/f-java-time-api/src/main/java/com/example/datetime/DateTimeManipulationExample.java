package com.example.datetime;

import java.time.*;
import java.time.temporal.ChronoUnit;

public class DateTimeManipulationExample {
    public static void main(String[] args) {
        // 日付の操作
        LocalDate date = LocalDate.of(2024, 1, 1);
        LocalDate nextWeek = date.plusWeeks(1);
        LocalDate lastMonth = date.minusMonths(1);
        LocalDate nextYear = date.plusYears(1);
        
        IO.println("元の日付: " + date);
        IO.println("1週間後: " + nextWeek);
        IO.println("1ヶ月前: " + lastMonth);
        IO.println("1年後: " + nextYear);
        
        // 時刻の操作
        LocalTime time = LocalTime.of(10, 30);
        LocalTime after2Hours = time.plusHours(2);
        LocalTime before30Min = time.minusMinutes(30);
        
        IO.println("\n元の時刻: " + time);
        IO.println("2時間後: " + after2Hours);
        IO.println("30分前: " + before30Min);
        
        // より柔軟な操作
        LocalDateTime dateTime = LocalDateTime.now();
        LocalDateTime modified = dateTime
            .withYear(2025)
            .withMonth(6)
            .withDayOfMonth(15)
            .withHour(14)
            .withMinute(0)
            .withSecond(0);
        
        IO.println("\n現在: " + dateTime);
        IO.println("変更後: " + modified);
        
        // 日付の比較
        LocalDate date1 = LocalDate.of(2024, 1, 1);
        LocalDate date2 = LocalDate.of(2024, 6, 1);
        
        IO.println("\n日付の比較:");
        IO.println("date1 < date2: " + date1.isBefore(date2));
        IO.println("date1 > date2: " + date1.isAfter(date2));
        IO.println("date1 == date2: " + date1.isEqual(date2));
    }
}