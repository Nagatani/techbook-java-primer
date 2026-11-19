// 第15章：待っているスレッドを起こして交代するのにかかる時間を測る（環境で変わる。桁だけを見る）
// 2つのスレッドがSynchronousQueueで1つの値を交互に渡し合う。1回の往復で2回の交代が起こる
import java.util.concurrent.SynchronousQueue;

public class ContextSwitch {
    public static void main(String[] args) throws InterruptedException {
        SynchronousQueue<Integer> ping = new SynchronousQueue<>();
        SynchronousQueue<Integer> pong = new SynchronousQueue<>();
        int n = 20_000;
        Thread echo = new Thread(() -> {
            try {
                for (int i = 0; i < n + 2_000; i++) {
                    pong.put(ping.take());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        echo.setDaemon(true);
        echo.start();
        for (int i = 0; i < 2_000; i++) {   // ウォームアップ
            ping.put(i);
            pong.take();
        }
        long t0 = System.nanoTime();
        for (int i = 0; i < n; i++) {
            ping.put(i);
            pong.take();
        }
        double usPerSwitch = (System.nanoTime() - t0) / 1000.0 / n / 2;
        IO.println("交代1回あたり: %.1fマイクロ秒".formatted(usPerSwitch));
        IO.println(usPerSwitch >= 0.5 && usPerSwitch < 100
                ? "1回あたりの時間はマイクロ秒の単位"
                : "1回あたりの時間がマイクロ秒の単位を外れた");
    }
}
