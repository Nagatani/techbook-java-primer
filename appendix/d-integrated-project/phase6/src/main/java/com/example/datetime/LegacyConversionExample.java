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
        
        System.out.println("Legacy Date: " + legacyDate);
        System.out.println("Instant: " + instant);
        System.out.println("Converted Date: " + convertedDate);
        
        // 2. Date → LocalDateTime
        LocalDateTime localDateTime = LocalDateTime.ofInstant(
            legacyDate.toInstant(), ZoneId.systemDefault());
        System.out.println("\nLocalDateTime: " + localDateTime);
        
        // 3. LocalDateTime → Date
        ZonedDateTime zonedDateTime = localDateTime.atZone(
            ZoneId.systemDefault());
        Date fromLocalDateTime = Date.from(zonedDateTime.toInstant());
        System.out.println("Date from LocalDateTime: " + fromLocalDateTime);
        
        // 4. Calendar ←→ ZonedDateTime
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.DECEMBER, 25, 14, 30, 0);
        
        ZonedDateTime fromCalendar = ((GregorianCalendar) calendar)
            .toZonedDateTime();
        System.out.println("\nCalendar → ZonedDateTime: " + fromCalendar);
        
        GregorianCalendar newCalendar = GregorianCalendar.from(
            ZonedDateTime.now());
        System.out.println("ZonedDateTime → Calendar: " + 
            newCalendar.getTime());
        
        // 5. java.sql.Date との変換
        java.sql.Date sqlDate = new java.sql.Date(
            System.currentTimeMillis());
        LocalDate localDate = sqlDate.toLocalDate();
        java.sql.Date convertedSqlDate = java.sql.Date.valueOf(localDate);
        
        System.out.println("\nSQL Date: " + sqlDate);
        System.out.println("LocalDate: " + localDate);
        System.out.println("Converted SQL Date: " + convertedSqlDate);
        
        // 6. タイムゾーンの変換
        TimeZone legacyTimeZone = TimeZone.getTimeZone("Asia/Tokyo");
        ZoneId zoneId = legacyTimeZone.toZoneId();
        TimeZone convertedTimeZone = TimeZone.getTimeZone(zoneId);
        
        System.out.println("\nLegacy TimeZone: " + legacyTimeZone.getID());
        System.out.println("ZoneId: " + zoneId);
        System.out.println("Converted TimeZone: " + 
            convertedTimeZone.getID());
    }
}