package com.example.datetime;

import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class LegacyConversionExample {
    public static void main(String[] args) {
        // 1. Date ←→ Instant
        Date legacyDate = new Date();
        Instant instant = legacyDate.toInstant();
        Date convertedDate = Date.from(instant);
        
        IO.println("Legacy Date: " + legacyDate);
        IO.println("Instant: " + instant);
        IO.println("Converted Date: " + convertedDate);
        
        // 2. Date → LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.ofInstant(
            legacyDate.toInstant(), ZoneId.systemDefault());
        IO.println("\nLocalDateTime: " + localDateTime);
        
        // 3. LocalDateTime → Date
        ZonedDateTime zonedDateTime = localDateTime.atZone(
            ZoneId.systemDefault());
        Date fromLocalDateTime = Date.from(zonedDateTime.toInstant());
        IO.println("Date from LocalDateTime: " + fromLocalDateTime);
        
        // 4. Calendar ←→ ZonedDateTime
        Calendar calendar = Calendar.getInstance();
        calendar.clear();  // 現在時刻のミリ秒などが残らないように、先に全項目を消す
        calendar.set(2024, Calendar.DECEMBER, 25, 14, 30, 0);
        
        ZonedDateTime fromCalendar = ((GregorianCalendar) calendar)
            .toZonedDateTime();
        IO.println("\nCalendar → ZonedDateTime: " + fromCalendar);
        
        GregorianCalendar newCalendar = GregorianCalendar.from(
            ZonedDateTime.now());
        IO.println("ZonedDateTime → Calendar: " + 
            newCalendar.getTime());
        
        // 5. java.sql.Date との変換
        java.sql.Date sqlDate = new java.sql.Date(
            System.currentTimeMillis());
        LocalDate localDate = sqlDate.toLocalDate();
        java.sql.Date convertedSqlDate = java.sql.Date.valueOf(localDate);
        
        IO.println("\nSQL Date: " + sqlDate);
        IO.println("LocalDate: " + localDate);
        IO.println("Converted SQL Date: " + convertedSqlDate);
        
        // 6. タイムゾーンの変換
        TimeZone legacyTimeZone = TimeZone.getTimeZone("Asia/Tokyo");
        ZoneId zoneId = legacyTimeZone.toZoneId();
        TimeZone convertedTimeZone = TimeZone.getTimeZone(zoneId);
        
        IO.println("\nLegacy TimeZone: " + legacyTimeZone.getID());
        IO.println("ZoneId: " + zoneId);
        IO.println("Converted TimeZone: " + 
            convertedTimeZone.getID());
    }
}