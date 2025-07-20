import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class DateTimeFormatterExample {
    public static void main(String[] args) {
        LocalDateTime dateTime = LocalDateTime.of(2024, 12, 25, 14, 30, 45);
        
        // 1. 定義済みフォーマッタ
        System.out.println("ISO_LOCAL_DATE_TIME: " + 
            dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        // 2. ロケール対応フォーマッタ
        DateTimeFormatter japaneseFormatter = DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.FULL)
            .withLocale(Locale.JAPANESE);
        System.out.println("日本語形式: " + dateTime.format(japaneseFormatter));
        
        // 3. カスタムパターン
        DateTimeFormatter customFormatter = 
            DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH時mm分ss秒");
        System.out.println("カスタム形式: " + dateTime.format(customFormatter));
        
        // 4. よく使われるパターン
        DateTimeFormatter[] formatters = {
            DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
            DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm", Locale.ENGLISH),
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss"),
            DateTimeFormatter.ofPattern("E, d MMM yyyy HH:mm:ss", Locale.ENGLISH)
        };
        
        System.out.println("\nさまざまな形式:");
        for (DateTimeFormatter formatter : formatters) {
            System.out.println(dateTime.format(formatter));
        }
        
        // 5. 文字列からのパース
        String dateStr = "2024-12-25 14:30:45";
        DateTimeFormatter parseFormatter = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime parsed = LocalDateTime.parse(dateStr, parseFormatter);
        System.out.println("\nパース結果: " + parsed);
    }
}