import java.util.List;

/**
 * 単語の一覧から、入力した文字を含む単語を探すクラス（画面に依存しないロジック）
 */
public class WordSearch {
    private final List<String> words;

    public WordSearch(List<String> words) {
        this.words = List.copyOf(words);
    }

    /**
     * 大文字と小文字を区別せずに、queryを含む単語を一覧の順で返す
     * 空白だけの入力では何も返さない
     */
    public List<String> search(String query) {
        String key = query.strip().toLowerCase();
        if (key.isEmpty()) {
            return List.of();
        }
        return words.stream()
                .filter(word -> word.toLowerCase().contains(key))
                .toList();
    }

    public int size() {
        return words.size();
    }

    /** 画面で使う単語の一覧（JavaのAPIのクラス名） */
    public static List<String> sampleWords() {
        return List.of(
                "ArrayList", "LinkedList", "HashMap", "TreeMap", "LinkedHashMap", "HashSet", "TreeSet",
                "String", "StringBuilder", "Integer", "Double", "Math", "Objects", "Optional",
                "Stream", "Collectors", "Comparator", "Iterator", "Thread", "ExecutorService",
                "CompletableFuture", "Files", "Path", "BufferedReader", "BufferedWriter", "Scanner",
                "LocalDate", "LocalDateTime", "Duration", "JFrame", "JPanel", "JButton", "JLabel",
                "JTextField", "JTextArea", "JList", "JTable", "SwingWorker", "Timer", "KeyStroke");
    }
}
