// 第6章コラム「タイムゾーンと「2038年問題」」
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/** 32ビットの秒数があふれる時刻と、タイムゾーンで時刻が変わる例 */
public class Year2038 {
    public static void main(String[] args) {
        // 1970-01-01T00:00:00Z からの秒数を、符号付き32ビット（int）で表せる最後の時刻
        IO.println("intの最大の秒数: " + Instant.ofEpochSecond(Integer.MAX_VALUE));
        // 1秒進めてintに入れると、負の値になって1901年に戻る
        int overflow = (int) (Integer.MAX_VALUE + 1L);
        IO.println("1秒後をintに入れると: " + Instant.ofEpochSecond(overflow));
        // Instantは64ビットの秒数で持つので、はるか先まで表せる
        IO.println("Instantの最大: " + Instant.MAX);

        // 同じ瞬間でも、タイムゾーンで表示が変わる
        Instant moment = Instant.parse("2026-01-01T00:00:00Z");
        IO.println("UTC: " + moment);
        IO.println("東京: " + moment.atZone(ZoneId.of("Asia/Tokyo")).toLocalDateTime());
        IO.println("ニューヨーク: " + moment.atZone(ZoneId.of("America/New_York")).toLocalDateTime());

        // 夏時間の始まりには、存在しない時刻がある（ニューヨークでは2026-03-08の2時台がない）
        LocalDateTime missing = LocalDateTime.of(2026, 3, 8, 2, 30);
        ZonedDateTime adjusted = missing.atZone(ZoneId.of("America/New_York"));
        IO.println("2026-03-08T02:30 をニューヨークの時刻にすると: " + adjusted.toLocalDateTime());
    }
}
