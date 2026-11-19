// 第15章：3つのスレッドを決まった順に start() しても、スレッドが最初の行を出す順序は変わることがある
import java.util.concurrent.ConcurrentLinkedQueue;

public class StartOrder {
    public static void main(String[] args) throws InterruptedException {
        int rounds = 2000;
        int changed = 0;
        for (int r = 0; r < rounds; r++) {
            ConcurrentLinkedQueue<String> order = new ConcurrentLinkedQueue<>();
            Thread a = new Thread(() -> order.add("A"));
            Thread b = new Thread(() -> order.add("B"));
            Thread c = new Thread(() -> order.add("C"));
            a.start();
            b.start();
            c.start();
            a.join();
            b.join();
            c.join();
            if (!String.join("", order).equals("ABC")) {
                changed++;
            }
        }
        IO.println(changed > 0
                ? "start()した順と違う順に動いた回がある"
                : "すべてstart()した順だった");
    }
}
