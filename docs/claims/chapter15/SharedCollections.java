// 第15章：スレッドセーフでないコレクションを複数のスレッドから使うと何が起こるかと、ConcurrentHashMapのアトミックな操作
//   java SharedCollections hashmap        4スレッドが別々のキーをHashMapにputする。要素が失われる回があるか
//   java SharedCollections arraylist      4スレッドがArrayListにaddする。要素が失われるか、例外が起きる回があるか
//   java SharedCollections cme            1つのスレッドがHashMapを順に取り出すあいだに、別のスレッドがputする
//   java SharedCollections compute        8スレッドが同じキーで「なければ作る」を同時に行い、作った回数を数える
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class SharedCollections {
    interface Body {
        void run(int thread) throws Exception;
    }

    /** n個のスレッドを同時に走らせ、どれかが投げた例外の名前を返す（なければnull） */
    static String together(int n, Body body) throws InterruptedException {
        CountDownLatch go = new CountDownLatch(1);
        String[] error = new String[1];
        Thread[] ts = new Thread[n];
        for (int i = 0; i < n; i++) {
            int id = i;
            ts[i] = new Thread(() -> {
                try {
                    go.await();
                    body.run(id);
                } catch (Exception e) {
                    error[0] = e.getClass().getSimpleName();
                }
            });
            ts[i].start();
        }
        go.countDown();
        for (Thread t : ts) {
            t.join();
        }
        return error[0];
    }

    public static void main(String[] args) throws Exception {
        switch (args.length > 0 ? args[0] : "hashmap") {
            case "arraylist" -> arrayList();
            case "cme" -> cme();
            case "compute" -> compute();
            default -> hashMap();
        }
    }

    static void hashMap() throws InterruptedException {
        int lost = 0;
        String hung = null;
        for (int r = 0; r < 50 && hung == null; r++) {
            Map<Integer, Integer> map = new HashMap<>();
            CountDownLatch go = new CountDownLatch(1);
            Thread[] ts = new Thread[4];
            for (int i = 0; i < ts.length; i++) {
                int id = i;
                ts[i] = new Thread(() -> {
                    try {
                        go.await();
                        for (int k = 0; k < 10_000; k++) {
                            map.put(id * 10_000 + k, k);
                        }
                    } catch (Exception e) {
                        // 例外も「壊れた」に数える
                    }
                });
                // 内部の構造が壊れてputが終わらなくなることがあるので、デーモンにしておく
                ts[i].setDaemon(true);
                ts[i].start();
            }
            go.countDown();
            for (Thread t : ts) {
                t.join(5_000);
                if (t.isAlive()) {
                    StackTraceElement top = t.getStackTrace()[0];
                    hung = top.getClassName() + "." + top.getMethodName();
                }
            }
            if (hung == null && map.size() < 40_000) {
                lost++;
            }
        }
        IO.println(lost > 0 ? "HashMap: 要素が失われた回がある" : "HashMap: 要素は失われなかった");
        if (hung != null) {
            IO.println("HashMap: putが終わらなくなった回がある（" + hung + "）");
        }
    }

    static void arrayList() throws InterruptedException {
        int lost = 0;
        String error = null;
        for (int r = 0; r < 50; r++) {
            List<Integer> list = new ArrayList<>();
            String e = together(4, id -> {
                for (int i = 0; i < 10_000; i++) {
                    list.add(i);
                }
            });
            if (e != null) {
                error = e;
            } else if (list.size() < 40_000) {
                lost++;
            }
        }
        IO.println("ArrayList: 要素が失われた回" + (lost > 0 ? "がある" : "はない")
                + (error != null ? "、例外: " + error : ""));
    }

    static void cme() throws InterruptedException {
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < 1_000; i++) {
            map.put(i, i);
        }
        String[] seen = new String[1];
        Thread reader = new Thread(() -> {
            try {
                for (int round = 0; round < 10_000; round++) {
                    long sum = 0;
                    for (int v : map.values()) {
                        sum += v;
                    }
                }
            } catch (ConcurrentModificationException e) {
                seen[0] = e.getClass().getName();
            }
        });
        Thread writer = new Thread(() -> {
            for (int i = 1_000; i < 1_000_000 && reader.isAlive(); i++) {
                map.put(i, i);
            }
        });
        reader.start();
        writer.start();
        reader.join();
        writer.join();
        IO.println(seen[0] != null ? "取り出している最中の更新: " + seen[0] : "例外は起きなかった");
    }

    static void compute() throws InterruptedException {
        int racy = 0;
        int atomicMax = 0;
        for (int r = 0; r < 200; r++) {
            Map<String, String> map = new ConcurrentHashMap<>();
            AtomicInteger created = new AtomicInteger();
            together(8, id -> {
                // containsKeyとputに分けた「なければ作る」
                if (!map.containsKey("key")) {
                    created.incrementAndGet();
                    map.put("key", "value" + id);
                }
            });
            if (created.get() > 1) {
                racy++;
            }
            Map<String, String> map2 = new ConcurrentHashMap<>();
            AtomicInteger created2 = new AtomicInteger();
            together(8, id -> map2.computeIfAbsent("key", k -> {
                created2.incrementAndGet();
                return "value" + id;
            }));
            atomicMax = Math.max(atomicMax, created2.get());
        }
        IO.println("containsKeyとput: 2回以上作った回" + (racy > 0 ? "がある" : "はない"));
        IO.println("computeIfAbsent: 作った回数は最大" + atomicMax + "回");
    }
}
