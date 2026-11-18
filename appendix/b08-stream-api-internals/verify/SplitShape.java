import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.StringJoiner;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Spliterator.trySplitがソースをどう分けるかを観察する。
 * 並列ストリームは、この分割で作った部分を別々のスレッドに渡す。
 *   javac SplitShape.java && java SplitShape
 */
public class SplitShape {
    /** 半分ずつ、深さdepthまで分けたときの各部分の大きさ */
    static String tree(Spliterator<?> s, int depth) {
        if (depth == 0) {
            return String.valueOf(s.estimateSize());
        }
        Spliterator<?> prefix = s.trySplit();
        if (prefix == null) {
            return String.valueOf(s.estimateSize());
        }
        return "(" + tree(prefix, depth - 1) + " | " + tree(s, depth - 1) + ")";
    }

    /**
     * 残りの部分に対してtrySplitをくり返し、切り出された部分の要素数を並べる。
     * 大きさのわからないソースでは見積もり（estimateSize）が当てにならないので、実際に数える
     */
    static String peel(Spliterator<?> s, int times) {
        StringJoiner j = new StringJoiner(", ");
        for (int i = 0; i < times; i++) {
            Spliterator<?> prefix = s.trySplit();
            if (prefix == null) {
                j.add("null");
                break;
            }
            long[] count = {0};
            prefix.forEachRemaining(x -> count[0]++);
            j.add(String.valueOf(count[0]));
        }
        return j + "（残りの見積もり " + s.estimateSize() + "）";
    }

    public static void main(String[] args) {
        int n = 10_000;
        List<Integer> array = new ArrayList<>(IntStream.range(0, n).boxed().toList());
        List<Integer> linked = new LinkedList<>(array);

        IO.println("--- 半分ずつ3段分ける ---");
        IO.println("ArrayList(10000):       " + tree(array.spliterator(), 3));
        IO.println("IntStream.range(10000): " + tree(IntStream.range(0, n).spliterator(), 3));
        IO.println("LinkedList(10000):      " + tree(linked.spliterator(), 3));

        IO.println("--- 残りに対してtrySplitをくり返す ---");
        IO.println("ArrayList(10000):  " + peel(array.spliterator(), 5));
        IO.println("LinkedList(10000): " + peel(linked.spliterator(), 5));
        IO.println("Stream.iterate:    "
                + peel(Stream.iterate(0, x -> x + 1).spliterator(), 5));
        IO.println("HashSet(10000):    " + peel(new HashSet<>(array).spliterator(), 5));

        IO.println("--- 小さなソース ---");
        IO.println("ArrayList(1):  " + peel(new ArrayList<>(List.of(1)).spliterator(), 1));
        IO.println("ArrayList(2):  " + peel(new ArrayList<>(List.of(1, 2)).spliterator(), 2));
    }
}
