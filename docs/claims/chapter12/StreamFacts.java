// 第12章：遅延評価、使い終わったストリーム、toList、並べ替えの順、並列ストリームの順序
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamFacts {
    public static void main(String[] args) {
        List<String> log = new ArrayList<>();
        Stream<Integer> s = Stream.of(5, 15, 20)
                .filter(n -> { log.add("filter " + n); return n > 10; })
                .map(n -> { log.add("map " + n); return n * 2; });
        System.out.println("before terminal: " + log);
        List<Integer> r = s.collect(Collectors.toList());
        System.out.println("after terminal: " + log + " -> " + r);
        try {
            s.count();
        } catch (IllegalStateException e) {
            System.out.println("reuse -> IllegalStateException");
        }

        List<Integer> fixed = Stream.of(1, 2).toList();
        try {
            fixed.add(3);
        } catch (UnsupportedOperationException e) {
            System.out.println("toList().add -> UnsupportedOperationException");
        }

        System.out.println("sorted: " + Stream.of("banana", "Apple", "cherry", "あ", "漢").sorted().toList());

        List<Integer> nums = IntStream.range(0, 10_000).boxed().toList();
        List<Integer> ordered = new ArrayList<>();
        nums.parallelStream().forEachOrdered(ordered::add);
        List<Integer> collected = nums.parallelStream().map(n -> n).collect(Collectors.toList());
        System.out.println("parallel forEachOrdered keeps order: " + ordered.equals(nums));
        System.out.println("parallel collect keeps order: " + collected.equals(nums));
        boolean forEachChanged = false;
        for (int t = 0; t < 20 && !forEachChanged; t++) {
            List<Integer> seen = java.util.Collections.synchronizedList(new ArrayList<>());
            nums.parallelStream().forEach(seen::add);
            forEachChanged = !seen.equals(nums);
        }
        System.out.println("parallel forEach order differed: " + forEachChanged);

        try {
            Optional.of(null);
        } catch (NullPointerException e) {
            System.out.println("Optional.of(null) -> NullPointerException");
        }
        System.out.println("Optional is Serializable: "
                + java.io.Serializable.class.isAssignableFrom(Optional.class));
    }
}
