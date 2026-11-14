package com.example.datetime;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class DateTimeFormatterExample {
    public static void main(String[] args) {
        LocalDateTime dateTime = LocalDateTime.of(2024, 12, 25, 14, 30, 45);
        
        // 1. 定義済みフォーマッター
        IO.println("ISO_LOCAL_DATE_TIME: " + 
            dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        // 2. ロケール対応フォーマッター
        // 時刻のFULL形式はタイムゾーン名を含み、LocalDateTimeには使えないため、時刻はMEDIUMにする
        DateTimeFormatter japaneseFormatter = DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.FULL, FormatStyle.MEDIUM)
            .withLocale(Locale.JAPANESE);
        IO.println("日本語形式: " + dateTime.format(japaneseFormatter));
        
        // 3. カスタムパターン
        DateTimeFormatter customFormatter = 
            DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH時mm分ss秒");
        IO.println("カスタム形式: " + dateTime.format(customFormatter));
        
        // 4. よく使われるパターン
        DateTimeFormatter[] formatters = {
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss"),
            DateTimeFormatter.ofPattern("E, d MMM yyyy HH:mm:ss", Locale.ENGLISH)
        };
        
        IO.println("\nさまざまな形式:");
        for (DateTimeFormatter formatter : formatters) {
            IO.println(dateTime.format(formatter));
        }
        
        // 5. 文字列からのパース
        String dateStr = "2024-12-25 14:30:45";
        DateTimeFormatter parseFormatter = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parsed = LocalDateTime.parse(dateStr, parseFormatter);
        IO.println("\nパース結果: " + parsed);
    }
}