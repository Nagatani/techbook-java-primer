package com.example.datetime;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class JavaTimeBasicExample {
    public static void main(String[] args) {
        // 1. LocalDate - 日付のみ（タイムゾーンなし）
        LocalDate today = LocalDate.now();
        LocalDate birthday = LocalDate.of(2000, 1, 15);
        IO.println("今日: " + today);
        IO.println("誕生日: " + birthday);
        
        // 2. LocalTime - 時刻のみ（タイムゾーンなし）
        LocalTime now = LocalTime.now();
        LocalTime meetingTime = LocalTime.of(14, 30);
        IO.println("現在時刻: " + now);
        IO.println("会議時刻: " + meetingTime);
        
        // 3. LocalDateTime - 日付と時刻（タイムゾーンなし）
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime appointment = LocalDateTime.of(2024, 12, 25, 18, 0);
        IO.println("現在の日時: " + currentDateTime);
        IO.println("予定: " + appointment);
        
        // 4. ZonedDateTime - タイムゾーン付き日時
        ZonedDateTime tokyoTime = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));
        ZonedDateTime newYorkTime = ZonedDateTime.now(ZoneId.of("America/New_York"));
        IO.println("東京: " + tokyoTime);
        IO.println("ニューヨーク: " + newYorkTime);
        
        // 5. Instant - エポック秒（UTC基準のタイムスタンプ）
        Instant timestamp = Instant.now();
        IO.println("タイムスタンプ: " + timestamp);
    }
}