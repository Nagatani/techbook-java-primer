// 第15章：ExecutorServiceとスレッドプールのふるまい
//   java Pools reuse        3スレッドのプールに10個のタスク。使ったスレッドの数と、同時に動いたタスクの数
//   java Pools exception    executeしたタスクが例外を投げても、プールは次のタスクを実行する
//   java Pools shutdown     shutdown()の後は新しいタスクを受け付けず、投入済みのタスクは終える
//   java Pools shutdownNow  shutdownNow()は実行中のタスクに割り込み、待っていたタスクを返す
//   java Pools nondaemon    タスクが終わっても、プールのスレッドは生き残る（デーモンでない）
//   java Pools queue        newFixedThreadPoolのキューの容量
//   java Pools reject       キューがあふれたときの拒否ポリシー（既定とCallerRunsPolicy）
//   java Pools little       1秒に20個届き、1つ0.5秒かかるタスクを、同時にいくつ処理しているか
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Pools {
    public static void main(String[] args) throws Exception {
        switch (args.length > 0 ? args[0] : "reuse") {
            case "exception" -> exception();
            case "shutdown" -> shutdown();
            case "shutdownNow" -> shutdownNow();
            case "nondaemon" -> nondaemon();
            case "queue" -> queue();
            case "reject" -> reject();
            case "little" -> little();
            default -> reuse();
        }
    }

    static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    static void sleepUntil(long nanoTime) {
        long ms = (nanoTime - System.nanoTime()) / 1_000_000;
        if (ms > 0) {
            sleep(ms);
        }
    }

    static void reuse() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(3);
        Set<String> names = ConcurrentHashMap.newKeySet();
        AtomicInteger running = new AtomicInteger();
        AtomicInteger max = new AtomicInteger();
        for (int i = 0; i < 10; i++) {
            pool.execute(() -> {
                names.add(Thread.currentThread().getName());
                max.accumulateAndGet(running.incrementAndGet(), Math::max);
                sleep(100);
                running.decrementAndGet();
            });
        }
        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);
        IO.println("10個のタスクを実行したスレッドの数: " + names.size());
        IO.println("同時に動いたタスクの最大数: " + max.get());
    }

    static void exception() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        Thread.setDefaultUncaughtExceptionHandler((t, e) ->
                IO.println(t.getName() + "で捕まえられなかった例外: " + e.getClass().getSimpleName()));
        String[] names = new String[2];
        pool.execute(() -> {
            names[0] = Thread.currentThread().getName();
            throw new IllegalStateException("タスクの失敗");
        });
        CountDownLatch next = new CountDownLatch(1);
        pool.execute(() -> {
            names[1] = Thread.currentThread().getName();
            next.countDown();
        });
        IO.println("例外の後のタスクを実行した: " + next.await(5, TimeUnit.SECONDS));
        IO.println("例外を投げたタスクのスレッド: " + names[0] + "、次のタスクのスレッド: " + names[1]);
        pool.shutdown();
    }

    static void shutdown() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        AtomicInteger done = new AtomicInteger();
        for (int i = 0; i < 5; i++) {
            pool.execute(() -> {
                sleep(100);
                done.incrementAndGet();
            });
        }
        pool.shutdown();
        try {
            pool.execute(() -> { });
            IO.println("shutdown()の後のexecute: 受け付けた");
        } catch (RejectedExecutionException e) {
            IO.println("shutdown()の後のexecute: RejectedExecutionException");
        }
        boolean finished = pool.awaitTermination(10, TimeUnit.SECONDS);
        IO.println("awaitTerminationの戻り値: " + finished + "、終えたタスク: " + done.get() + "/5");

        ExecutorService slow = Executors.newFixedThreadPool(1);
        slow.execute(() -> sleep(2000));
        slow.shutdown();
        IO.println("時間内に終わらないときのawaitTerminationの戻り値: " + slow.awaitTermination(100, TimeUnit.MILLISECONDS));
        slow.shutdownNow();
    }

    static void shutdownNow() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        CountDownLatch started = new CountDownLatch(1);
        String[] result = new String[1];
        pool.execute(() -> {
            started.countDown();
            try {
                Thread.sleep(10_000);
                result[0] = "最後まで実行した";
            } catch (InterruptedException e) {
                result[0] = "InterruptedExceptionで中断した";
            }
        });
        for (int i = 0; i < 3; i++) {
            pool.execute(() -> { });
        }
        started.await();
        List<Runnable> waiting = pool.shutdownNow();
        pool.awaitTermination(5, TimeUnit.SECONDS);
        IO.println("実行中のタスク: " + result[0]);
        IO.println("shutdownNow()が返した、待っていたタスクの数: " + waiting.size());
    }

    static void nondaemon() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        Thread[] worker = new Thread[1];
        pool.submit(() -> worker[0] = Thread.currentThread());
        sleep(1000);
        IO.println("タスクが終わって1秒後: スレッドは" + (worker[0].isAlive() ? "生きている" : "終わった")
                + "、デーモン: " + worker[0].isDaemon());
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
        worker[0].join(5000);
        IO.println("shutdown()の後: スレッドは" + (worker[0].isAlive() ? "生きている" : "終わった"));
    }

    static void queue() {
        ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
        IO.println("newFixedThreadPoolのキュー: " + pool.getQueue().getClass().getSimpleName()
                + "、残りの容量: " + pool.getQueue().remainingCapacity()
                + (pool.getQueue().remainingCapacity() == Integer.MAX_VALUE ? "（Integer.MAX_VALUE）" : ""));
        IO.println("既定の拒否ポリシー: " + pool.getRejectedExecutionHandler().getClass().getName());
        pool.shutdown();
    }

    static void reject() throws InterruptedException {
        // スレッド1つ、キューの容量1。3つ目のタスクはあふれる
        ThreadPoolExecutor abort = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1));
        IO.println("コンストラクターで指定しないときの拒否ポリシー: " + abort.getRejectedExecutionHandler().getClass().getSimpleName());
        try {
            for (int i = 0; i < 3; i++) {
                abort.execute(() -> sleep(300));
            }
        } catch (RejectedExecutionException e) {
            IO.println("AbortPolicy: RejectedExecutionException");
        }
        abort.shutdown();

        ThreadPoolExecutor callerRuns = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1),
                new ThreadPoolExecutor.CallerRunsPolicy());
        for (int i = 0; i < 3; i++) {
            int n = i;
            callerRuns.execute(() -> {
                if (n == 2) {
                    IO.println("CallerRunsPolicy: あふれたタスクを実行したスレッド: " + Thread.currentThread().getName());
                }
                sleep(300);
            });
        }
        callerRuns.shutdown();
        callerRuns.awaitTermination(5, TimeUnit.SECONDS);
    }

    static void little() throws InterruptedException {
        ExecutorService pool = Executors.newCachedThreadPool();
        AtomicInteger running = new AtomicInteger();
        int[] freq = new int[64];
        // 50ミリ秒ごと（1秒に20個）に、0.5秒かかるタスクを3秒間投入し、投入と投入のあいだで同時に処理中のタスクを数える
        // 投入の時刻は開始からi×50ミリ秒に合わせる（sleepの誤差が積み重ならないように）
        long t0 = System.nanoTime();
        for (int i = 0; i < 60; i++) {
            pool.execute(() -> {
                long end = System.nanoTime() + 500_000_000L;
                running.incrementAndGet();
                sleepUntil(end);
                running.decrementAndGet();
            });
            sleepUntil(t0 + i * 50_000_000L + 25_000_000L);
            if (i >= 20) {
                freq[running.get()]++;   // 最初の1秒（数が増えていくあいだ）は数えない
            }
            sleepUntil(t0 + (i + 1) * 50_000_000L);
        }
        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
        int mode = 0;
        for (int n = 0; n < freq.length; n++) {
            if (freq[n] > freq[mode]) {
                mode = n;
            }
        }
        IO.println("同時に処理していたタスクの数（もっとも多かった値）: " + mode);
    }
}
