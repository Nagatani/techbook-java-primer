// 第12章：Fork/Joinフレームワーク（Java 7で追加）。--release 8で通ることだけを確かめる
import java.util.concurrent.ForkJoinPool;

public class ForkJoin {
    static ForkJoinPool pool = new ForkJoinPool();
}
