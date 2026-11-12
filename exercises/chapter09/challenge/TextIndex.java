import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 文章の索引。単語ごとに、その単語が出てくる行の番号を記録する
 *
 * 目的ごとに選んだコレクション:
 * - lines: 行を番号で取り出すのでList（ArrayList）
 * - stopWords: 「含まれるか」を速く調べたいのでSet（HashSet）
 * - index: 単語から行番号を引き、索引を辞書順に出すのでTreeMap。行番号は重複なしで小さい順に並べたいのでTreeSet
 * - counts: 単語から回数を引くだけで順序は要らないのでHashMap
 * - firstSeen: 単語を出てきた順に重複なしで覚えるのでLinkedHashSet
 */
public class TextIndex {
    private final List<String> lines = new ArrayList<>();
    private final Set<String> stopWords = new HashSet<>();
    private final Map<String, TreeSet<Integer>> index = new TreeMap<>();
    private final Map<String, Integer> counts = new HashMap<>();
    private final Set<String> firstSeen = new LinkedHashSet<>();

    public TextIndex(String[] stopWords) {
        for (String word : stopWords) {
            this.stopWords.add(word);
        }
    }

    // 1行を加え、その行の単語を索引に登録する
    public void addLine(String line) {
        lines.add(line);
        int lineNumber = lines.size();  // 行番号は1から
        for (String word : splitWords(line)) {
            if (stopWords.contains(word)) {
                continue;
            }
            TreeSet<Integer> numbers = index.get(word);
            if (numbers == null) {
                numbers = new TreeSet<>();
                index.put(word, numbers);
            }
            numbers.add(lineNumber);
            counts.put(word, counts.getOrDefault(word, 0) + 1);
            firstSeen.add(word);
        }
    }

    // 小文字にし、ピリオドとカンマを除いて空白で区切る
    private static List<String> splitWords(String line) {
        List<String> words = new ArrayList<>();
        String cleaned = line.toLowerCase().replace(".", "").replace(",", "");
        for (String word : cleaned.split(" ")) {
            if (!word.isEmpty()) {
                words.add(word);
            }
        }
        return words;
    }

    public String getLine(int lineNumber) {
        return lines.get(lineNumber - 1);
    }

    public int getLineCount() {
        return lines.size();
    }

    public Map<String, TreeSet<Integer>> getIndex() {
        return index;
    }

    public Set<String> getFirstSeen() {
        return firstSeen;
    }

    // 単語が出てくる行。索引にない単語なら空の集合
    // 呼び出し側が集合演算で書き換えても索引が変わらないよう、コピーを返す
    public Set<Integer> linesOf(String word) {
        TreeSet<Integer> numbers = index.get(word);
        if (numbers == null) {
            return new TreeSet<>();
        }
        return new TreeSet<>(numbers);
    }

    // すべての単語を含む行（積集合）
    public Set<Integer> searchAll(String... words) {
        Set<Integer> result = linesOf(words[0]);
        for (int i = 1; i < words.length; i++) {
            result.retainAll(linesOf(words[i]));
        }
        return result;
    }

    // どれかの単語を含む行（和集合）
    public Set<Integer> searchAny(String... words) {
        Set<Integer> result = new TreeSet<>();
        for (String word : words) {
            result.addAll(linesOf(word));
        }
        return result;
    }

    // wordを含み、excludedを含まない行（差集合）
    public Set<Integer> searchExcluding(String word, String excluded) {
        Set<Integer> result = linesOf(word);
        result.removeAll(linesOf(excluded));
        return result;
    }

    // 出てきた回数ごとの単語（回数の多い順に取り出せるよう、回数をキーにしたTreeMap）
    public TreeMap<Integer, TreeSet<String>> wordsByCount() {
        TreeMap<Integer, TreeSet<String>> result = new TreeMap<>();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            TreeSet<String> words = result.get(entry.getValue());
            if (words == null) {
                words = new TreeSet<>();
                result.put(entry.getValue(), words);
            }
            words.add(entry.getKey());
        }
        return result;
    }

    // 出てきた回数がminCount未満の単語を索引から除き、除いた数を返す
    // Mapを走査しながら削除するので、entrySet()のIteratorのremoveを使う
    public int removeRareWords(int minCount) {
        int removed = 0;
        Iterator<Map.Entry<String, TreeSet<Integer>>> it = index.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, TreeSet<Integer>> entry = it.next();
            if (counts.get(entry.getKey()) < minCount) {
                it.remove();
                removed++;
            }
        }
        return removed;
    }
}
