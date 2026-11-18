import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Gatherer;
import java.util.stream.Gatherers;
import java.util.stream.Stream;

/**
 * Java 16以降に加わったストリームの操作（toList・mapMulti）と、
 * Java 24で正式になったStream Gatherers（gather）を試す。
 *   javac NewOperations.java && java NewOperations
 */
public class NewOperations {
    public static void main(String[] args) {
        IO.println("--- toList()とCollectors.toList() ---");
        List<Integer> a = Stream.of(1, 2, 3).toList();
        List<Integer> b = Stream.of(1, 2, 3).collect(Collectors.toList());
        IO.println("toList(): " + a.getClass().getName());
        IO.println("Collectors.toList(): " + b.getClass().getName());
        try {
            a.add(4);
        } catch (UnsupportedOperationException e) {
            IO.println("toList()の結果にadd: UnsupportedOperationException");
        }
        b.add(4);
        IO.println("Collectors.toList()の結果にadd: " + b);
        IO.println("toList()はnullを含められる: " + Stream.of("x", null).toList());
        try {
            List.of("x", null);
        } catch (NullPointerException e) {
            IO.println("List.ofはnullを含められない: NullPointerException");
        }

        IO.println("--- mapMulti（1つの要素から0個以上の要素を出す） ---");
        List<Object> nested = List.of(1, List.of(2, 3), 4, List.of());
        List<Integer> flat = nested.stream()
                .<Integer>mapMulti((o, sink) -> {
                    if (o instanceof Integer i) {
                        sink.accept(i);
                    } else if (o instanceof List<?> list) {
                        for (Object x : list) {
                            sink.accept((Integer) x);
                        }
                    }
                })
                .toList();
        IO.println("mapMulti: " + flat);

        IO.println("--- Gatherers（Java 24〜） ---");
        List<Integer> nums = List.of(1, 2, 3, 4, 5, 6, 7);
        IO.println("windowFixed(3):   " + nums.stream().gather(Gatherers.windowFixed(3)).toList());
        IO.println("windowSliding(3): " + nums.stream().gather(Gatherers.windowSliding(3)).toList());
        IO.println("scan(累計):       "
                + nums.stream().gather(Gatherers.scan(() -> 0, Integer::sum)).toList());
        IO.println("fold(合計):       "
                + nums.stream().gather(Gatherers.fold(() -> 0, Integer::sum)).toList());

        // 自作のGatherer: 直前と違う値だけを通す（連続する重複を除く）。前の値を状態として持つ
        Gatherer<Integer, ?, Integer> dedupRuns = Gatherer.ofSequential(
                () -> new ArrayList<Integer>(1),
                (state, element, downstream) -> {
                    if (state.isEmpty() || !state.get(0).equals(element)) {
                        state.clear();
                        state.add(element);
                        return downstream.push(element);
                    }
                    return true;
                });
        IO.println("自作(連続する重複を除く): "
                + Stream.of(1, 1, 2, 2, 2, 3, 1, 1).gather(dedupRuns).toList());

        // gatherも遅延評価で、無限のストリームにも使える
        IO.println("無限のストリーム+windowFixed+limit: "
                + Stream.iterate(1, n -> n + 1).gather(Gatherers.windowFixed(2)).limit(3).toList());
    }
}
