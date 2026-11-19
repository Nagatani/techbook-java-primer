// 第15章：朝食の準備を順番に行う場合と、3つのスレッドで並行して行う場合の時間を比べる
// 本文の3秒・2秒・5秒を、時間を節約するために10分の1（300・200・500ミリ秒）にして測る
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Breakfast {
    static final List<String> done = Collections.synchronizedList(new ArrayList<>());

    static void task(String name, int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        done.add(name);
    }

    public static void main(String[] args) throws InterruptedException {
        long t0 = System.nanoTime();
        task("コーヒー", 300);
        task("トースト", 200);
        task("卵", 500);
        long seq = (System.nanoTime() - t0) / 1_000_000;

        done.clear();
        long t1 = System.nanoTime();
        Thread coffee = new Thread(() -> task("コーヒー", 300));
        Thread toast = new Thread(() -> task("トースト", 200));
        Thread egg = new Thread(() -> task("卵", 500));
        coffee.start();
        toast.start();
        egg.start();
        coffee.join();
        toast.join();
        egg.join();
        long par = (System.nanoTime() - t1) / 1_000_000;

        // 100ミリ秒単位に丸める（本文の1秒に当たる）
        IO.println("順番に実行: 約" + Math.round(seq / 100.0) + "単位");
        IO.println("3つのスレッド: 約" + Math.round(par / 100.0) + "単位");
        IO.println("終わった順: " + String.join("、", done));
    }
}
