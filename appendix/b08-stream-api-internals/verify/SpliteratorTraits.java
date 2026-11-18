import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.StringJoiner;
import java.util.TreeSet;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * ソースごとのSpliteratorの特性（characteristics）と、中間操作で特性がどう変わるかを表示する。
 *   javac SpliteratorTraits.java && java SpliteratorTraits
 */
public class SpliteratorTraits {
    static final int[] FLAGS = {
        Spliterator.ORDERED, Spliterator.DISTINCT, Spliterator.SORTED, Spliterator.SIZED,
        Spliterator.NONNULL, Spliterator.IMMUTABLE, Spliterator.CONCURRENT, Spliterator.SUBSIZED
    };
    static final String[] NAMES = {
        "ORDERED", "DISTINCT", "SORTED", "SIZED", "NONNULL", "IMMUTABLE", "CONCURRENT", "SUBSIZED"
    };

    static String traits(Spliterator<?> s) {
        StringJoiner j = new StringJoiner(" ");
        for (int i = 0; i < FLAGS.length; i++) {
            if (s.hasCharacteristics(FLAGS[i])) {
                j.add(NAMES[i]);
            }
        }
        return j.toString();
    }

    static void show(String label, Spliterator<?> s) {
        IO.println("%-26s %s".formatted(label, traits(s)));
    }

    public static void main(String[] args) {
        List<Integer> data = List.of(3, 1, 2);
        Map<Integer, String> map = new HashMap<>(Map.of(1, "a", 2, "b"));

        IO.println("--- ソースごとの特性 ---");
        show("ArrayList", new ArrayList<>(data).spliterator());
        show("LinkedList", new LinkedList<>(data).spliterator());
        show("Arrays.asList", Arrays.asList(3, 1, 2).spliterator());
        show("List.of", data.spliterator());
        show("HashSet", new HashSet<>(data).spliterator());
        show("TreeSet", new TreeSet<>(data).spliterator());
        show("HashMap.keySet", map.keySet().spliterator());
        show("IntStream.range", IntStream.range(0, 10).spliterator());
        show("Stream.of", Stream.of(3, 1, 2).spliterator());
        show("Stream.iterate", Stream.iterate(1, n -> n + 1).spliterator());
        show("Stream.generate", Stream.generate(() -> 1).spliterator());

        IO.println("--- 中間操作のあとの特性（ソースはTreeSet） ---");
        TreeSet<Integer> tree = new TreeSet<>(data);
        show("stream()", tree.stream().spliterator());
        show("map()", tree.stream().map(n -> n * 2).spliterator());
        show("filter()", tree.stream().filter(n -> n > 1).spliterator());
        show("unordered()", tree.stream().unordered().spliterator());
        IO.println("--- ソースはArrayList ---");
        show("stream()", new ArrayList<>(data).stream().spliterator());
        show("map()", new ArrayList<>(data).stream().map(n -> n * 2).spliterator());
        show("sorted()", new ArrayList<>(data).stream().sorted().spliterator());
        show("distinct()", new ArrayList<>(data).stream().distinct().spliterator());

        IO.println("--- estimateSize ---");
        IO.println("ArrayList(3件): " + new ArrayList<>(data).spliterator().estimateSize());
        IO.println("filter後: " + new ArrayList<>(data).stream().filter(n -> n > 1)
                .spliterator().estimateSize());
        IO.println("Stream.iterate: " + Stream.iterate(1, n -> n + 1).spliterator().estimateSize()
                + "（Long.MAX_VALUE = " + Long.MAX_VALUE + "）");
    }
}
