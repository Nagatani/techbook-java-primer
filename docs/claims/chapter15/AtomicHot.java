// 第15章：AtomicInteger.incrementAndGetを何度も呼び、JITコンパイルさせる
// -XX:+UnlockDiagnosticVMOptions -XX:+PrintIntrinsics で、Unsafe.getAndAddIntが組み込みの命令（intrinsic）に置き換わるのを見る
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicHot {
    static final AtomicInteger counter = new AtomicInteger();

    static int work() {
        int last = 0;
        for (int i = 0; i < 1_000; i++) {
            last = counter.incrementAndGet();
        }
        return last;
    }

    public static void main(String[] args) {
        long sum = 0;
        for (int i = 0; i < 20_000; i++) {
            sum += work();
        }
        IO.println("counter: " + counter.get() + "（" + (sum > 0) + "）");
    }
}
