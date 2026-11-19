// 第15章：CompletableFuture（Java 8で導入）
import java.util.concurrent.CompletableFuture;

public class CompletableFutureProbe {
    static CompletableFuture<Integer> make() {
        return CompletableFuture.supplyAsync(() -> 1).thenApply(x -> x + 1);
    }
}
