import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.Set;

/**
 * AsynchronousFileChannelの実装クラス、APPENDが使えないこと、
 * 完了ハンドラーを実行するスレッドを観察する。
 */
public class AsyncChannelFacts {
    public static void main(String[] args) throws Exception {
        Path dir = Files.createTempDirectory("async");
        Path file = dir.resolve("a.txt");
        try {
            // 1. APPENDは指定できない
            try {
                AsynchronousFileChannel.open(file, StandardOpenOption.CREATE,
                        StandardOpenOption.WRITE, StandardOpenOption.APPEND).close();
            } catch (IllegalArgumentException | UnsupportedOperationException e) {
                IO.println("APPENDを指定: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }

            try (AsynchronousFileChannel ch = AsynchronousFileChannel.open(file,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.READ)) {
                // 2. 実装クラス
                IO.println("実装クラス: " + ch.getClass().getName());

                // 3. 読み書きには必ず位置を渡す（チャネルは現在位置を持たない）
                Future<Integer> w = ch.write(StandardCharsets.UTF_8.encode("hello"), 0);
                IO.println("Futureで書いたバイト数: " + w.get());

                // 4. 完了ハンドラーはどのスレッドで動くか
                CompletableFuture<String> where = new CompletableFuture<>();
                ch.read(ByteBuffer.allocate(5), 0, null, new CompletionHandler<Integer, Void>() {
                    @Override
                    public void completed(Integer n, Void a) {
                        Thread t = Thread.currentThread();
                        // 呼び出し元をたどり、スレッドプールのタスクから呼ばれたかを調べる
                        boolean fromPoolTask = StackWalker.getInstance().walk(frames -> frames
                                .anyMatch(f -> f.getClassName().equals(
                                        "java.util.concurrent.ThreadPoolExecutor$Worker")));
                        where.complete("読んだバイト数=" + n
                                + " mainスレッドか=" + t.getName().equals("main")
                                + " デーモンか=" + t.isDaemon()
                                + " スレッドプールのタスクの中か=" + fromPoolTask);
                    }

                    @Override
                    public void failed(Throwable exc, Void a) {
                        where.completeExceptionally(exc);
                    }
                });
                IO.println("既定のスレッドプールでの完了ハンドラー: " + where.get());
            }

            // 5. 自分のExecutorServiceを渡すと、そのスレッドで動く
            ExecutorService pool = Executors.newFixedThreadPool(1,
                    r -> new Thread(r, "my-io-thread"));
            try (AsynchronousFileChannel ch = AsynchronousFileChannel.open(file,
                    Set.of(StandardOpenOption.READ), pool)) {
                CompletableFuture<String> where = new CompletableFuture<>();
                ch.read(ByteBuffer.allocate(5), 0, null, new CompletionHandler<Integer, Void>() {
                    @Override
                    public void completed(Integer n, Void a) {
                        where.complete(Thread.currentThread().getName());
                    }

                    @Override
                    public void failed(Throwable exc, Void a) {
                        where.completeExceptionally(exc);
                    }
                });
                IO.println("自分のExecutorServiceを渡したときのスレッド: " + where.get());
            } finally {
                pool.shutdown();
            }
        } finally {
            Files.deleteIfExists(file);
            Files.delete(dir);
        }
    }
}
