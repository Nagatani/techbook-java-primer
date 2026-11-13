import java.util.List;
import java.util.Objects;

/**
 * WordSearchの動作確認
 */
public class WordSearchCheck {
    private static int total = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        IO.println("=== WordSearchの動作確認 ===");
        WordSearch search = new WordSearch(WordSearch.sampleWords());
        check("単語の数", 40, search.size());
        check("大文字と小文字を区別しない", List.of("HashMap", "TreeMap", "LinkedHashMap"), search.search("map"));
        check("前後の空白は無視する", List.of("SwingWorker"), search.search("  worker "));
        check("一覧の順で返す", List.of("LocalDate", "LocalDateTime"), search.search("localdate"));
        check("空の入力では何も返さない", List.of(), search.search("   "));
        check("見つからなければ空", List.of(), search.search("Python"));
        IO.println();
        IO.println(total + "件中" + (total - failed) + "件OK");
    }

    private static void check(String label, Object expected, Object actual) {
        total++;
        if (Objects.equals(expected, actual)) {
            IO.println("OK  " + label);
        } else {
            failed++;
            IO.println("NG  " + label + "（期待: " + expected + "、実際: " + actual + "）");
        }
    }
}
