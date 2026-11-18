import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

/**
 * 並列ストリームが、共通のForkJoinPool（commonPool）のスレッドと、呼び出したスレッドで動くことを観察する。
 *   javac CommonPool.java
 *   java -XX:ActiveProcessorCount=4 CommonPool
 *   java -XX:ActiveProcessorCount=4 -Djava.util.concurrent.ForkJoinPool.common.parallelism=2 CommonPool
 */
public class CommonPool {
    public static void main(String[] args) throws Exception {
        IO.println("availableProcessors = " + Runtime.getRuntime().availableProcessors());
        IO.println("getCommonPoolParallelism = " + ForkJoinPool.getCommonPoolParallelism());

        IO.println("--- 並列ストリームを動かしたスレッド ---");
        IO.println("呼び出したスレッド: " + Thread.currentThread().getName());
        IO.println(threadsOf(() -> IntStream.range(0, 2_000).parallel().forEach(i -> work())));
        long workers = RAW.stream().filter(n -> n.startsWith("ForkJoinPool.commonPool-worker-")).count();
        IO.println("commonPoolのワーカーの数は並列度以下か: "
                + (workers <= ForkJoinPool.getCommonPoolParallelism()));

        IO.println("--- 自分で作ったForkJoinPoolの中から呼ぶ ---");
        ForkJoinPool pool = new ForkJoinPool(2);
        try {
            String names = pool.submit(() ->
                    threadsOf(() -> IntStream.range(0, 2_000).parallel().forEach(i -> work())))
                    .get();
            IO.println(names);
        } finally {
            pool.shutdown();
        }
    }

    static final Set<String> SEEN = ConcurrentHashMap.newKeySet();
    static final Set<String> RAW = ConcurrentHashMap.newKeySet();

    static void work() {
        String name = Thread.currentThread().getName();
        RAW.add(name);
        SEEN.add(name.replaceAll("\\d+$", "N"));
        // スレッドが分担する時間を作るための、少し重い計算
        double x = 0;
        for (int i = 0; i < 20_000; i++) {
            x += Math.sqrt(i);
        }
        if (x < 0) {
            IO.println(x);
        }
    }

    /** 処理を動かし、使ったスレッドの名前（末尾の番号をNにしたもの）を返す */
    static String threadsOf(Runnable task) {
        SEEN.clear();
        RAW.clear();
        task.run();
        return "使ったスレッドの種類: " + new TreeSet<>(SEEN);
    }
}
