# 付録F: java.time API完全ガイド<small>日付時刻処理の最新手法</small>

この付録では、Java 8で導入されたjava.time APIについて、基本的な使い方から実践的な応用まで詳しく解説します。

## 本付録の目的

従来のjava.util.DateやCalendarクラスの問題点を解決する、現代的な日付時刻処理APIを習得することを目的とします。

## レガシーDate/Calendarの問題点

Java 8以前は、日付時刻の処理に`java.util.Date`と`java.util.Calendar`を使用していましたが、これらのAPIには多くの問題がありました：

1. **可変性（ミュータブル）**: Dateオブジェクトは変更可能であるため、バグの温床となりやすい
2. **設計の不整合**: 月が0から始まる（1月が0）など、直感的でない仕様
3. **スレッドセーフではない**: SimpleDateFormatなどがスレッドセーフでないため、並行処理で問題が発生
4. **タイムゾーン処理の複雑さ**: タイムゾーンの扱いが難しく、エラーが起きやすい
5. **APIの使いにくさ**: 日付の計算や比較が直感的でない

## java.time APIの主要クラス

Java 8で導入された`java.time`パッケージは、これらの問題を解決する新しい日付時刻APIです：

<span class="listing-number">**サンプルコードF-1**</span>

```java
import java.time.*;
import java.time.format.DateTimeFormatter;

public class JavaTimeBasicExample {
    public static void main(String[] args) {
        // 1. LocalDate - 日付のみ（タイムゾーンなし）
        LocalDate today = LocalDate.now();
        LocalDate birthday = LocalDate.of(2000, 1, 15);
        System.out.println("今日: " + today);
        System.out.println("誕生日: " + birthday);
        
        // 2. LocalTime - 時刻のみ（タイムゾーンなし）
        LocalTime now = LocalTime.now();
        LocalTime meetingTime = LocalTime.of(14, 30);
        System.out.println("現在時刻: " + now);
        System.out.println("会議時刻: " + meetingTime);
        
        // 3. LocalDateTime - 日付と時刻（タイムゾーンなし）
        LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime appointment = LocalDateTime.of(2024, 12, 25, 18, 0);
        System.out.println("現在の日時: " + currentDateTime);
        System.out.println("予定: " + appointment);
        
        // 4. ZonedDateTime - タイムゾーン付き日時
        ZonedDateTime tokyoTime = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));
        ZonedDateTime newYorkTime = ZonedDateTime.now(ZoneId.of("America/New_York"));
        System.out.println("東京: " + tokyoTime);
        System.out.println("ニューヨーク: " + newYorkTime);
        
        // 5. Instant - エポック秒（UTC基準のタイムスタンプ）
        Instant timestamp = Instant.now();
        System.out.println("タイムスタンプ: " + timestamp);
    }
}
```

## 日付時刻の作成と操作

java.time APIは不変（イミュータブル）であり、操作は常に新しいインスタンスを返します：

<span class="listing-number">**サンプルコードF-2**</span>

```java
import java.time.*;
import java.time.temporal.ChronoUnit;

public class DateTimeManipulationExample {
    public static void main(String[] args) {
        // 日付の操作
        LocalDate date = LocalDate.of(2024, 1, 1);
        LocalDate nextWeek = date.plusWeeks(1);
        LocalDate lastMonth = date.minusMonths(1);
        LocalDate nextYear = date.plusYears(1);
        
        System.out.println("元の日付: " + date);
        System.out.println("1週間後: " + nextWeek);
        System.out.println("1ヶ月前: " + lastMonth);
        System.out.println("1年後: " + nextYear);
        
        // 時刻の操作
        LocalTime time = LocalTime.of(10, 30);
        LocalTime after2Hours = time.plusHours(2);
        LocalTime before30Min = time.minusMinutes(30);
        
        System.out.println("\n元の時刻: " + time);
        System.out.println("2時間後: " + after2Hours);
        System.out.println("30分前: " + before30Min);
        
        // より柔軟な操作
        LocalDateTime dateTime = LocalDateTime.now();
        LocalDateTime modified = dateTime
            .withYear(2025)
            .withMonth(6)
            .withDayOfMonth(15)
            .withHour(14)
            .withMinute(0)
            .withSecond(0);
        
        System.out.println("\n現在: " + dateTime);
        System.out.println("変更後: " + modified);
        
        // 日付の比較
        LocalDate date1 = LocalDate.of(2024, 1, 1);
        LocalDate date2 = LocalDate.of(2024, 6, 1);
        
        System.out.println("\n日付の比較:");
        System.out.println("date1 < date2: " + date1.isBefore(date2));
        System.out.println("date1 > date2: " + date1.isAfter(date2));
        System.out.println("date1 == date2: " + date1.isEqual(date2));
    }
}
```

## フォーマットとパース（DateTimeFormatter）

日付時刻の文字列表現との相互変換には`DateTimeFormatter`を使用します：

<span class="listing-number">**サンプルコードF-3**</span>

```java
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
```

## 期間と期限の計算（Period、Duration）

日付や時刻の差を扱うために、`Period`（日付ベース）と`Duration`（時間ベース）を使用します：

<span class="listing-number">**サンプルコードF-4**</span>

```java
import java.time.*;
import java.time.temporal.ChronoUnit;

public class PeriodDurationExample {
    public static void main(String[] args) {
        // Period - 日付ベースの期間
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 3, 15);
        
        Period period = Period.between(startDate, endDate);
        System.out.println("期間: " + period); // P1Y2M14D
        System.out.println("詳細: " + period.getYears() + "年 " + 
                          period.getMonths() + "ヶ月 " + 
                          period.getDays() + "日");
        
        // Periodを使った日付の計算
        LocalDate futureDate = LocalDate.now().plus(Period.ofMonths(6));
        System.out.println("6ヶ月後: " + futureDate);
        
        // Duration - 時間ベースの期間
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(17, 30);
        
        Duration duration = Duration.between(startTime, endTime);
        System.out.println("\n勤務時間: " + duration); // PT8H30M
        System.out.println("詳細: " + duration.toHours() + "時間 " + 
                          duration.toMinutesPart() + "分");
        
        // 日時間の日数計算
        LocalDate birth = LocalDate.of(2000, 1, 1);
        long daysAlive = ChronoUnit.DAYS.between(birth, LocalDate.now());
        System.out.println("\n生まれてから " + daysAlive + " 日経過");
        
        // インスタント間の経過時間
        Instant start = Instant.now();
        // 何か処理
        try { Thread.sleep(1000); } catch (InterruptedException e) {}
        Instant end = Instant.now();
        
        Duration elapsed = Duration.between(start, end);
        System.out.println("処理時間: " + elapsed.toMillis() + "ミリ秒");
    }
}
```

## タイムゾーンの扱い

グローバルなアプリケーションでは、タイムゾーンの適切な処理が重要です：

<span class="listing-number">**サンプルコードF-5**</span>

```java
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
```

## ファイル操作での実践例

java.time APIをファイル操作と組み合わせた実践的な例：

<span class="listing-number">**サンプルコードF-6**</span>

```java
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FileTimeExample {
    public static void main(String[] args) throws IOException, InterruptedException {
        Path logDir = Paths.get("logs");
        Files.createDirectories(logDir);
        
        // 1. タイムスタンプ付きログファイルの作成
        DateTimeFormatter fileNameFormatter = 
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = LocalDateTime.now().format(fileNameFormatter);
        Path logFile = logDir.resolve("app_" + timestamp + ".log");
        
        // ログエントリーの書き込み
        DateTimeFormatter logFormatter = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        List<String> logEntries = new ArrayList<>();
        
        for (int i = 0; i < 5; i++) {
            String entry = String.format("[%s] INFO: Processing item %d",
                LocalDateTime.now().format(logFormatter), i);
            logEntries.add(entry);
            Thread.sleep(100); // 処理のシミュレーション
        }
        
        Files.write(logFile, logEntries);
        System.out.println("ログファイル作成: " + logFile);
        
        // 2. ファイルの更新日時を取得
        BasicFileAttributes attrs = Files.readAttributes(
            logFile, BasicFileAttributes.class);
        
        FileTime creationTime = attrs.creationTime();
        FileTime lastModified = attrs.lastModifiedTime();
        FileTime lastAccess = attrs.lastAccessTime();
        
        System.out.println("\nファイル時刻情報:");
        System.out.println("作成日時: " + 
            LocalDateTime.ofInstant(creationTime.toInstant(), 
                                  ZoneId.systemDefault()));
        System.out.println("最終更新: " + 
            LocalDateTime.ofInstant(lastModified.toInstant(), 
                                  ZoneId.systemDefault()));
        System.out.println("最終アクセス: " + 
            LocalDateTime.ofInstant(lastAccess.toInstant(), 
                                  ZoneId.systemDefault()));
        
        // 3. 古いログファイルの検索と削除
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(7);
        
        System.out.println("\n7日以上前のログファイルを検索...");
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(
                logDir, "*.log")) {
            for (Path file : stream) {
                FileTime fileTime = Files.getLastModifiedTime(file);
                LocalDateTime fileDateTime = LocalDateTime.ofInstant(
                    fileTime.toInstant(), ZoneId.systemDefault());
                
                if (fileDateTime.isBefore(cutoffTime)) {
                    System.out.println("古いファイル発見: " + file);
                    // Files.delete(file); // 実際の削除はコメントアウト
                }
            }
        }
        
        // 4. ファイルの更新日時を変更
        LocalDateTime newDateTime = LocalDateTime.now().minusHours(3);
        FileTime newFileTime = FileTime.from(
            newDateTime.atZone(ZoneId.systemDefault()).toInstant());
        Files.setLastModifiedTime(logFile, newFileTime);
        System.out.println("\n更新日時を3時間前に変更しました");
    }
}
```

## レガシーAPIとの相互変換

既存のコードとの互換性のため、java.time APIとレガシーAPIの間で変換が必要な場合があります：

<span class="listing-number">**サンプルコードF-7**</span>

```java
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
```

## java.time APIのベストプラクティス

1. **適切なクラスの選択**:
   - 日付のみ： `LocalDate`
   - 時刻のみ： `LocalTime`
   - 日付と時刻： `LocalDateTime`
   - タイムゾーン付き： `ZonedDateTime`
   - タイムスタンプ： `Instant`

2. **不変性の活用**: java.timeのクラスはすべて不変なので、スレッドセーフで安全に使用できます

3. **明示的なタイムゾーン指定**: グローバルアプリケーションでは常にタイムゾーンを明示的に扱う

4. **適切なフォーマッタの再利用**: `DateTimeFormatter`はスレッドセーフなので、静的フィールドとして再利用可能

5. **型安全な計算**: PeriodとDurationを適切に使い分ける

6. **レガシーAPIとの混在を避ける**: 可能な限りjava.time APIで統一する

## まとめ

java.time APIは、従来のDate/Calendarクラスの問題を解決し、型安全で直感的な日付時刻処理を提供します。不変性、スレッドセーフティ、豊富な機能により、現代的なJavaアプリケーション開発には必須のAPIです。

この付録で学んだ知識を活用して、堅牢で保守性の高い日付時刻処理を実装してください。