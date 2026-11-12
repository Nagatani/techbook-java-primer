package chapter11.solutions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * メソッド参照と関数の合成（課題3の解答例）
 */
public class MethodRefs {

    /**
     * 「4文字以下で、dで始まらない」条件。andとnegateで2つの条件を組み合わせる
     */
    public static Predicate<String> shortAndNotStartingWithD() {
        Predicate<String> isShort = s -> s.length() <= 4;
        Predicate<String> startsWithD = s -> s.startsWith("d");
        return isShort.and(startsWithD.negate());
    }

    /**
     * 「長さを求めて2倍にする」関数。andThenで、長さを求めた結果を2倍する関数に渡す
     */
    public static Function<String, Integer> doubledLength() {
        Function<String, Integer> length = String::length;
        return length.andThen(n -> n * 2);
    }

    public static void main(String[] args) {
        // List.ofのリストは変更できないので、ArrayListにコピーしてから操作する
        List<String> words = new ArrayList<>(List.of("banana", "Apple", "cherry", "kiwi", "Date"));
        IO.println("元のリスト: " + words);

        // 任意のインスタンスのメソッド参照: 要素ごとに s.toLowerCase() を呼ぶ
        words.replaceAll(String::toLowerCase);
        IO.println("小文字に: " + words);

        words.sort(Comparator.naturalOrder());
        IO.println("辞書順: " + words);

        // sortは安定なので、同じ長さの語は辞書順のまま残る
        words.sort((a, b) -> Integer.compare(a.length(), b.length()));
        // Comparatorのメソッドでも書ける: words.sort(Comparator.comparingInt(String::length));
        IO.println("短い順: " + words);

        IO.println("1つずつ表示:");
        // 静的メソッド参照: 要素ごとに IO.println(s) を呼ぶ
        words.forEach(IO::println);

        words.removeIf(shortAndNotStartingWithD());
        IO.println("4文字以下でdで始まらない語を削除: " + words);

        IO.println("bananaの長さの2倍: " + doubledLength().apply("banana"));

        // コンストラクター参照: s -> new StringBuilder(s) と同じ
        Function<String, StringBuilder> builder = StringBuilder::new;
        IO.println("逆順: " + builder.apply("lambda").reverse());

        // 第12章のStream APIを使うと、元のリストを変えずに、小文字にして短い順に並べたリストを作れる
        // List<String> sorted = List.of("banana", "Apple", "cherry", "kiwi", "Date").stream()
        //         .map(String::toLowerCase)
        //         .sorted(Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder()))
        //         .toList();
    }
}
