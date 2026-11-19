// 第12章：共通のスレッドプールのスレッド数（既定でCPUのコア数より1少ない）
import java.util.concurrent.ForkJoinPool;

public class CommonPoolSize {
    public static void main(String[] args) {
        int cores = Runtime.getRuntime().availableProcessors();
        int parallelism = ForkJoinPool.commonPool().getParallelism();
        System.out.println("cores-1=" + (cores - 1) + " parallelism=" + parallelism
                + " same=" + (parallelism == Math.max(1, cores - 1)));
    }
}
