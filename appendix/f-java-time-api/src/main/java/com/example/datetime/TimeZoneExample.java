import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Set;

public class TimeZoneExample {
    public static void main(String[] args) {
        // 利用可能なタイムゾーン一覧（一部）
        Set<String> zones = ZoneId.getAvailableZoneIds();
        System.out.println("利用可能なタイムゾーン数: " + zones.size());
        zones.stream()
             .filter(z -> z.startsWith("Asia/"))
             .sorted()
             .limit(5)
             .forEach(System.out::println);
        
        // タイムゾーン付き日時の作成
        ZonedDateTime tokyoTime = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));
        ZonedDateTime londonTime = tokyoTime.withZoneSameInstant(
            ZoneId.of("Europe/London"));
        ZonedDateTime newYorkTime = tokyoTime.withZoneSameInstant(
            ZoneId.of("America/New_York"));
        
        DateTimeFormatter formatter = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        
        System.out.println("\n同一時刻の各地表示:");
        System.out.println("東京: " + tokyoTime.format(formatter));
        System.out.println("ロンドン: " + londonTime.format(formatter));
        System.out.println("ニューヨーク: " + newYorkTime.format(formatter));
        
        // オフセット付き日時
        OffsetDateTime offsetDateTime = OffsetDateTime.now();
        System.out.println("\nオフセット付き日時: " + offsetDateTime);
        
        // 夏時間（DST）の考慮
        ZoneId nyZone = ZoneId.of("America/New_York");
        ZonedDateTime winterTime = ZonedDateTime.of(
            2024, 1, 15, 12, 0, 0, 0, nyZone);
        ZonedDateTime summerTime = ZonedDateTime.of(
            2024, 7, 15, 12, 0, 0, 0, nyZone);
        
        System.out.println("\n夏時間の影響:");
        System.out.println("冬時間: " + winterTime);
        System.out.println("夏時間: " + summerTime);
        System.out.println("オフセット差: " + 
            (winterTime.getOffset().getTotalSeconds() - 
             summerTime.getOffset().getTotalSeconds()) / 3600 + "時間");
    }
}