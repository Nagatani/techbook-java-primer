package chapter09.solutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 英文の単語ごとの出現回数を数える（課題3の解答例）
 */
public class WordCounter {

    /**
     * 文を空白で区切り、ピリオドを除いて小文字にそろえた単語の出現回数を返す
     */
    public static Map<String, Integer> countWords(String text) {
        Map<String, Integer> counts = new HashMap<>();
        for (String raw : text.split(" ")) {
            String word = raw.replace(".", "").toLowerCase();
            if (word.isEmpty()) {
                continue;
            }
            counts.put(word, counts.getOrDefault(word, 0) + 1);
            // 第11章のラムダ式・メソッド参照を使うと、mergeで1行に書ける
            // counts.merge(word, 1, Integer::sum);
        }
        return counts;
    }

    /**
     * 最も多く出た単語を、アルファベット順のリストで返す（同じ回数なら全部）
     */
    public static List<String> mostFrequentWords(Map<String, Integer> counts) {
        int max = 0;
        for (int count : counts.values()) {
            if (count > max) {
                max = count;
            }
        }
        List<String> result = new ArrayList<>();
        // TreeMapに移してから調べると、結果がアルファベット順になる
        for (Map.Entry<String, Integer> entry : new TreeMap<>(counts).entrySet()) {
            if (entry.getValue() == max) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    public static void main(String[] args) {
        String text = "Java is fun. Java is powerful. Programming with Java is exciting.";
        IO.println("入力: " + text);

        Map<String, Integer> counts = countWords(text);

        IO.println("\nアルファベット順（TreeMap）:");
        Map<String, Integer> sorted = new TreeMap<>(counts);
        for (Map.Entry<String, Integer> entry : sorted.entrySet()) {
            IO.println(entry.getKey() + ": " + entry.getValue() + "回");
        }

        IO.println("\n出現回数の多い単語:");
        for (String word : mostFrequentWords(counts)) {
            IO.println(word + "（" + counts.get(word) + "回）");
        }

        int total = 0;
        for (int count : counts.values()) {
            total += count;
        }
        IO.println("\n総単語数: " + total);
        IO.println("異なる単語の数: " + counts.size());
    }
}
