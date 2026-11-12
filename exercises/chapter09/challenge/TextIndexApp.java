import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * 第9章 チャレンジ課題: 文章の索引と検索（解答例）
 *
 * 英語の短い文章から、本の巻末にあるような索引（単語と、それが出てくる行の番号）を作り、
 * 複数の単語を組み合わせて行を検索します。
 *
 * 学習ポイント:
 * - List・Set・Mapを、目的（順番・重複なし・キーで引く・並び順）に合わせて選ぶ
 * - 値がコレクションのMap（Map<String, TreeSet<Integer>>）を組み立てる
 * - retainAll・addAll・removeAllによる集合演算
 * - Iteratorのremoveで、Mapを走査しながら要素を取り除く
 */
public class TextIndexApp {
    public static void main(String[] args) {
        String[] text = {
            "Java is a programming language.",
            "A list keeps elements in order.",
            "A set does not keep duplicate elements.",
            "A map connects a key to a value.",
            "Java collections include list, set, and map.",
            "Choose a list when the order of elements matters, and choose a set when it does not."
        };
        String[] stopWords = {"a", "an", "the", "is", "in", "to", "and", "of", "it", "when", "does", "not"};

        TextIndex textIndex = new TextIndex(stopWords);
        for (String line : text) {
            textIndex.addLine(line);
        }

        IO.println("=== 文章 ===");
        for (int i = 1; i <= textIndex.getLineCount(); i++) {
            IO.println("  " + i + ": " + textIndex.getLine(i));
        }

        IO.println();
        IO.println("=== 索引（辞書順） ===");
        printIndex(textIndex.getIndex());

        IO.println();
        IO.println("=== 出てきた順の単語 ===");
        IO.println("  " + textIndex.getFirstSeen());

        IO.println();
        IO.println("=== よく出てくる単語 ===");
        TreeMap<Integer, TreeSet<String>> byCount = textIndex.wordsByCount();
        for (Map.Entry<Integer, TreeSet<String>> entry : byCount.descendingMap().entrySet()) {
            if (entry.getKey() < 2) {
                break;  // 回数の多い順に並んでいるので、1回の単語が来たら終わる
            }
            IO.println("  " + entry.getKey() + "回: " + entry.getValue());
        }

        IO.println();
        IO.println("=== 検索 ===");
        printResult(textIndex, "java と list の両方", textIndex.searchAll("java", "list"));
        printResult(textIndex, "list か map", textIndex.searchAny("list", "map"));
        printResult(textIndex, "elements を含み set を含まない", textIndex.searchExcluding("elements", "set"));
        printResult(textIndex, "queue", textIndex.linesOf("queue"));

        IO.println();
        IO.println("=== 1回しか出てこない単語を索引から除く ===");
        int removed = textIndex.removeRareWords(2);
        IO.println("  " + removed + "語を除きました");
        printIndex(textIndex.getIndex());
    }

    private static void printIndex(Map<String, TreeSet<Integer>> index) {
        for (Map.Entry<String, TreeSet<Integer>> entry : index.entrySet()) {
            IO.println("  " + entry.getKey() + " " + entry.getValue());
        }
    }

    private static void printResult(TextIndex textIndex, String label, Set<Integer> lineNumbers) {
        IO.println(label + ": " + lineNumbers.size() + "行");
        for (int number : lineNumbers) {
            IO.println("  " + number + ": " + textIndex.getLine(number));
        }
    }
}
