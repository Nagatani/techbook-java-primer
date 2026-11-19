// 第15章：CompletableFutureの連鎖を実行するスレッド、例外の伝わり方、orTimeout
//   java Futures threads | exception | timeout
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Futures {
    static String name() {
        String n = Thread.currentThread().getName();
        return n.startsWith("ForkJoinPool.commonPool-worker-") ? "ForkJoinPool.commonPool-worker" : n;
    }

    static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws Exception {
        switch (args.length > 0 ? args[0] : "threads") {
            case "exception" -> exception();
            case "timeout" -> timeout();
            default -> threads();
        }
    }

    static void threads() throws Exception {
        String[] where = new String[4];
        // 前の処理が終わる前にthenApplyを呼ぶ
        CompletableFuture<Integer> slow = CompletableFuture.supplyAsync(() -> {
            where[0] = name();
            sleep(300);
            return 1;
        });
        CompletableFuture<Integer> a = slow.thenApply(x -> {
            where[1] = name();
            return x + 1;
        });
        a.get();
        IO.println("supplyAsyncのタスク: " + where[0]);
        IO.println("終わる前に呼んだthenApply: " + where[1]);
        // 前の処理がすでに終わってからthenApplyを呼ぶ
        CompletableFuture<Integer> done = CompletableFuture.completedFuture(1);
        done.thenApply(x -> {
            where[2] = name();
            return x;
        }).get();
        IO.println("終わった後に呼んだthenApply: " + where[2]);
        done.thenApplyAsync(x -> {
            where[3] = name();
            return x;
        }).get();
        IO.println("thenApplyAsync: " + where[3]);
    }

    static void exception() throws Exception {
        boolean[] applied = new boolean[1];
        CompletableFuture<Integer> f = CompletableFuture.<Integer>supplyAsync(() -> {
            throw new IllegalStateException("取得に失敗");
        }).thenApply(x -> {
            applied[0] = true;
            return x * 2;
        });
        CompletableFuture<Integer> recovered = f.exceptionally(ex -> {
            IO.println("exceptionallyが受け取った例外: " + ex.getClass().getName());
            IO.println("その原因: " + ex.getCause().getClass().getName());
            IO.println("getMessage(): " + ex.getMessage());
            return 0;
        });
        IO.println("exceptionallyの結果: " + recovered.get());
        IO.println("途中のthenApplyを実行した: " + applied[0]);
        try {
            f.get();
        } catch (ExecutionException e) {
            IO.println("get()が投げた例外: " + e.getClass().getSimpleName() + "（原因: " + e.getCause().getClass().getSimpleName() + "）");
        }
        CompletableFuture<String> handled = f.handle((v, ex) -> ex == null ? "成功: " + v : "失敗を処理した");
        IO.println("handleの結果: " + handled.get());
    }

    static void timeout() throws Exception {
        CompletableFuture<String> f = CompletableFuture.supplyAsync(() -> {
            sleep(2000);
            return "終わった";
        }).orTimeout(200, TimeUnit.MILLISECONDS);
        try {
            f.get();
        } catch (ExecutionException e) {
            IO.println("orTimeoutで時間内に終わらなかったとき: " + e.getCause().getClass().getName());
        }
    }
}
