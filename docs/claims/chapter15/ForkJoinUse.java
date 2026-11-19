// 第15章：並列ストリームとArrays.parallelSortが、Fork/Joinの共通プールのスレッドで動くこと
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

public class ForkJoinUse {
    static String kind(String name) {
        return name.startsWith("ForkJoinPool.commonPool-worker-") ? "ForkJoinPool.commonPool-worker" : name;
    }

    public static void main(String[] args) {
        IO.println("共通プールの並列度が2以上: " + (ForkJoinPool.getCommonPoolParallelism() >= 2));
        Set<String> stream = ConcurrentHashMap.newKeySet();
        IntStream.range(0, 100_000).parallel().forEach(i -> stream.add(kind(Thread.currentThread().getName())));
        IO.println("並列ストリーム: " + new TreeSet<>(stream));

        Set<String> sort = ConcurrentHashMap.newKeySet();
        Integer[] data = new Integer[200_000];
        for (int i = 0; i < data.length; i++) {
            data[i] = (i * 7919) % data.length;
        }
        Arrays.parallelSort(data, (x, y) -> {
            sort.add(kind(Thread.currentThread().getName()));
            return Integer.compare(x, y);
        });
        IO.println("Arrays.parallelSort: " + new TreeSet<>(sort));
    }
}
