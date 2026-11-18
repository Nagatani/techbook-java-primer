import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 要素がパイプラインを流れる順序（縦の処理）、短絡、全要素を待つ中間操作をpeekで観察する。
 *   javac EvaluationOrder.java && java EvaluationOrder
 */
public class EvaluationOrder {
    public static void main(String[] args) {
        List<String> words = List.of("b", "c", "a");

        IO.println("[1] filter → map → forEach（1要素ずつ縦に流れる）");
        words.stream()
                .peek(w -> IO.println("  filter前: " + w))
                .filter(w -> !w.equals("c"))
                .peek(w -> IO.println("  map前:    " + w))
                .map(String::toUpperCase)
                .forEach(w -> IO.println("  forEach:  " + w));

        IO.println("[2] findFirst（見つかったら残りを読まない）");
        String first = Stream.of("x1", "y2", "y3", "y4")
                .peek(w -> IO.println("  読んだ: " + w))
                .filter(w -> w.startsWith("y"))
                .findFirst()
                .orElseThrow();
        IO.println("  結果: " + first);

        IO.println("[3] 無限のストリームもlimitで止まる");
        List<Integer> squares = Stream.iterate(1, n -> n + 1)
                .peek(n -> IO.println("  生成: " + n))
                .map(n -> n * n)
                .limit(3)
                .toList();
        IO.println("  結果: " + squares);

        IO.println("[4] anyMatch / allMatch（答えが決まった時点で止まる）");
        boolean any = IntStream.of(1, 3, 4, 5, 7)
                .peek(n -> IO.println("  anyMatchが見た: " + n))
                .anyMatch(n -> n % 2 == 0);
        boolean all = IntStream.of(1, 3, 4, 5, 7)
                .peek(n -> IO.println("  allMatchが見た: " + n))
                .allMatch(n -> n % 2 == 1);
        IO.println("  anyMatch=" + any + " allMatch=" + all);

        IO.println("[5] sorted（すべての要素を受け取ってから次へ流す）");
        new ArrayList<>(words).stream()
                .peek(w -> IO.println("  sorted前: " + w))
                .sorted()
                .forEach(w -> IO.println("  sorted後: " + w));

        IO.println("[6] もともと並んでいるソース（TreeSet）では、自然順のsortedは何もしない");
        new TreeSet<>(words).stream()
                .peek(w -> IO.println("  sorted前: " + w))
                .sorted()
                .forEach(w -> IO.println("  sorted後: " + w));

        IO.println("[7] distinctは初めて現れた要素だけを、来た順に流す");
        Stream.of("a", "b", "a", "c", "b")
                .peek(w -> IO.println("  distinct前: " + w))
                .distinct()
                .forEach(w -> IO.println("  distinct後: " + w));

        IO.println("[8] 要素数がわかるソースのcount()は、パイプラインを実行しないことがある");
        long n1 = List.of(1, 2, 3).stream()
                .peek(n -> IO.println("  peek: " + n))
                .map(n -> n * 2)
                .count();
        IO.println("  map後のcount = " + n1 + "（peekは呼ばれない）");
        long n2 = List.of(1, 2, 3).stream()
                .peek(n -> IO.println("  peek: " + n))
                .filter(n -> n > 1)
                .count();
        IO.println("  filter後のcount = " + n2 + "（filterは数を変えうるので実行する）");
    }
}
