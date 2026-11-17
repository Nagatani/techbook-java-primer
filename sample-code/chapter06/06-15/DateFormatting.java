// DateFormatting.java - 書式を指定して日付を表示し、文字列から読み取る
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

public class DateFormatting {
    // DateTimeFormatterも不変なので、定数にして使い回せる
    private static final DateTimeFormatter JAPANESE =
        DateTimeFormatter.ofPattern("yyyy年M月d日(E)", Locale.JAPANESE);
    // 既定の読み取り方（SMART）は、2月30日のような日を月末に丸めてしまう
    private static final DateTimeFormatter SMART =
            DateTimeFormatter.ofPattern("yyyy/MM/dd");
    // 入力の検査には、存在しない日を拒むSTRICTを使う（年はuuuuで書く）
    private static final DateTimeFormatter STRICT =
        DateTimeFormatter.ofPattern("uuuu/MM/dd")
        .withResolverStyle(ResolverStyle.STRICT);

    public static void main(String[] args) {
        // 表示
        IO.println(LocalDate.of(2026, 4, 1).format(JAPANESE));
        IO.println("SMARTで2026/02/30 → "
                + LocalDate.parse("2026/02/30", SMART));

        // 読み取れない文字列はDateTimeParseException（第13章で学ぶ例外）
        for (String input : new String[] {"2026/02/28", "2026/02/30",
                "2026-02-28"}) {
            try {
                IO.println(input + " → " + LocalDate.parse(input,
                        STRICT).format(JAPANESE));
            } catch (DateTimeParseException e) {
                IO.println(input + " → 日付として読み取れません");
            }
        }
    }
}
