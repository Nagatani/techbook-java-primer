import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * コレクターの特性（Characteristics）と、並列のcollectで途中の入れ物（supplierの結果）がいくつ作られるかを観察する。
 *   javac CollectorTraits.java && java -XX:ActiveProcessorCount=4 CollectorTraits
 */
public class CollectorTraits {
    public static void main(String[] args) {
        IO.println("--- 標準のコレクターの特性 ---");
        IO.println("toList():               " + Collectors.toList().characteristics());
        IO.println("toSet():                " + Collectors.toSet().characteristics());
        IO.println("joining():              " + Collectors.joining().characteristics());
        IO.println("toUnmodifiableList():   " + Collectors.toUnmodifiableList().characteristics());
        IO.println("groupingBy():           " + Collectors.groupingBy(Object::toString).characteristics());
        IO.println("groupingByConcurrent(): "
                + Collectors.groupingByConcurrent(Object::toString).characteristics());

        IO.println("--- 並列のcollectでsupplierが呼ばれた回数 ---");
        AtomicInteger plain = new AtomicInteger();
        Collector<Integer, List<Integer>, List<Integer>> toArrayList = Collector.of(
                () -> {
                    plain.incrementAndGet();
                    return new ArrayList<>();
                },
                List::add,
                (left, right) -> {
                    left.addAll(right);
                    return left;
                });
        List<Integer> list = IntStream.range(0, 1000).parallel().boxed().collect(toArrayList);
        IO.println("ふつうのコレクター: 要素数=" + list.size()
                + " supplierは2回以上呼ばれたか: " + (plain.get() > 1));

        AtomicInteger concurrent = new AtomicInteger();
        Collector<Integer, ConcurrentHashMap<Integer, Boolean>, ConcurrentHashMap<Integer, Boolean>> toMap =
                Collector.of(
                        () -> {
                            concurrent.incrementAndGet();
                            return new ConcurrentHashMap<>();
                        },
                        (map, n) -> map.put(n, Boolean.TRUE),
                        (left, right) -> {
                            left.putAll(right);
                            return left;
                        },
                        Collector.Characteristics.CONCURRENT,
                        Collector.Characteristics.UNORDERED,
                        Collector.Characteristics.IDENTITY_FINISH);
        var map = IntStream.range(0, 1000).parallel().boxed().collect(toMap);
        IO.println("CONCURRENTかつUNORDEREDのコレクター: 要素数=" + map.size()
                + " supplierが呼ばれた回数=" + concurrent.get());
    }
}
