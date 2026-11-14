package com.example.datetime;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Set;

public class TimeZoneExample {
    public static void main(String[] args) {
        // 利用可能なタイムゾーン一覧（一部）
        Set<String> zones = ZoneId.getAvailableZoneIds();
        IO.println("利用可能なタイムゾーン数: " + zones.size());
        zones.stream()
             .filter(z -> z.startsWith("Asia/"))
             .sorted()
             .limit(5)
             .forEach(IO::println);
        
        // タイムゾーン付き日時の作成
        ZonedDateTime tokyoTime = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));
        ZonedDateTime londonTime = tokyoTime.withZoneSameInstant(
            ZoneId.of("Europe/London"));
        ZonedDateTime newYorkTime = tokyoTime.withZoneSameInstant(
            ZoneId.of("America/New_York"));
        
        // zはタイムゾーンの略称。日本語ロケールでは夏時間でもESTと出るため、英語で表示する
        DateTimeFormatter formatter = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z", Locale.US);
        
        IO.println("\n同一時刻の各地表示:");
        IO.println("東京: " + tokyoTime.format(formatter));
        IO.println("ロンドン: " + londonTime.format(formatter));
        IO.println("ニューヨーク: " + newYorkTime.format(formatter));
        
        // オフセット付き日時
        OffsetDateTime offsetDateTime = OffsetDateTime.now();
        IO.println("\nオフセット付き日時: " + offsetDateTime);
        
        // 夏時間（DST）の考慮
        ZoneId nyZone = ZoneId.of("America/New_York");
        ZonedDateTime winterTime = ZonedDateTime.of(
            2024, 1, 15, 12, 0, 0, 0, nyZone);
        ZonedDateTime summerTime = ZonedDateTime.of(
            2024, 7, 15, 12, 0, 0, 0, nyZone);
        
        IO.println("\n夏時間の影響:");
        IO.println("冬時間: " + winterTime);
        IO.println("夏時間: " + summerTime);
        IO.println("夏時間で進む時間: " + 
            (summerTime.getOffset().getTotalSeconds() - 
             winterTime.getOffset().getTotalSeconds()) / 3600 + "時間");
    }
}