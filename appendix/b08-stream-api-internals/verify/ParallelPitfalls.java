import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 並列ストリームで、順序・reduceの約束・副作用がどう結果に影響するかを観察する。
 *   javac ParallelPitfalls.java && java ParallelPitfalls
 */
public class ParallelPitfalls {
    public static void main(String[] args) {
        List<Integer> numbers = IntStream.rangeClosed(1, 20).boxed().toList();

        IO.println("--- 順序 ---");
        List<Integer> viaForEach = new CopyOnWriteArrayList<>();
        numbers.parallelStream().forEach(viaForEach::add);
        List<Integer> viaOrdered = new CopyOnWriteArrayList<>();
        numbers.parallelStream().forEachOrdered(viaOrdered::add);
        IO.println("forEachの順序は元の順序と同じか: " + viaForEach.equals(numbers));
        IO.println("forEachOrderedの順序は元の順序と同じか: " + viaOrdered.equals(numbers));
        IO.println("並列のmap→toListの結果: "
                + numbers.parallelStream().map(n -> n * 10).limit(5).toList());
        IO.println("並列のfindFirst: " + numbers.parallelStream().filter(n -> n > 10).findFirst().orElseThrow());

        IO.println("--- reduceの単位元 ---");
        int seq = numbers.stream().reduce(100, Integer::sum);
        int par = numbers.parallelStream().reduce(100, Integer::sum);
        IO.println("reduce(100, sum) 逐次: " + seq + " 並列: " + par + " 一致: " + (seq == par));
        IO.println("reduce(0, sum)   逐次: " + numbers.stream().reduce(0, Integer::sum)
                + " 並列: " + numbers.parallelStream().reduce(0, Integer::sum));

        IO.println("--- reduceの結合性 ---");
        int seqSub = numbers.stream().reduce(0, (a, b) -> a - b);
        int parSub = numbers.parallelStream().reduce(0, (a, b) -> a - b);
        IO.println("引き算 逐次: " + seqSub + " 並列: " + parSub + " 一致: " + (seqSub == parSub));

        IO.println("--- 副作用（スレッドセーフでないリストへの追加） ---");
        int n = 100_000;
        List<Integer> unsafe = new ArrayList<>();
        String outcome;
        try {
            IntStream.range(0, n).parallel().forEach(unsafe::add);
            outcome = "例外なし";
        } catch (RuntimeException e) {
            outcome = e.getClass().getSimpleName();
        }
        IO.println("ArrayListに" + n + "件追加: 要素数が" + n + "件か: "
                + (unsafe.size() == n) + "（" + outcome + "）");
        List<Integer> collected = IntStream.range(0, n).parallel().boxed()
                .collect(Collectors.toList());
        IO.println("collectで集めた要素数: " + collected.size());
    }
}
