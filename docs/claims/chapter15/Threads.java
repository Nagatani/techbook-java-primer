// 第15章：スレッドの状態の遷移、run()とstart()の違い、起動直後のスレッド、Runnableの形を観察する
//   java Threads states | run | live | runnable
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;

public class Threads {
    public static void main(String[] args) throws Exception {
        switch (args.length > 0 ? args[0] : "states") {
            case "run" -> runVsStart();
            case "live" -> live();
            case "runnable" -> runnable();
            default -> states();
        }
    }

    /** 状態がwantになるまで待つ（最大2秒） */
    static Thread.State waitFor(Thread t, Thread.State want) throws InterruptedException {
        for (int i = 0; i < 200 && t.getState() != want; i++) {
            Thread.sleep(10);
        }
        return t.getState();
    }

    static void states() throws Exception {
        Object lock = new Object();
        CountDownLatch running = new CountDownLatch(1);
        Thread busy = new Thread(() -> {
            running.countDown();
            long end = System.nanoTime() + 300_000_000L;
            while (System.nanoTime() < end) {
                // 計算し続ける
            }
        });
        IO.println("new Thread()の直後: " + busy.getState());
        busy.start();
        running.await();
        IO.println("start()の後、実行中: " + busy.getState());
        busy.join();
        IO.println("run()が終わった後: " + busy.getState());

        Thread blocked = new Thread(() -> {
            synchronized (lock) {
                // ロックを取れたら何もせずに終わる
            }
        });
        synchronized (lock) {
            blocked.start();
            IO.println("ほかのスレッドが持つロックを待つ: " + waitFor(blocked, Thread.State.BLOCKED));
        }
        blocked.join();

        Thread waiting = new Thread(() -> {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        waiting.start();
        IO.println("wait()で待つ: " + waitFor(waiting, Thread.State.WAITING));
        synchronized (lock) {
            lock.notify();
            // 通知を受けても、ロックを取り直すまではBLOCKEDで待つ
            IO.println("notify()の後、ロックが返されるまで: " + waitFor(waiting, Thread.State.BLOCKED));
        }
        waiting.join();

        Thread sleeper = new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // 割り込まれたら終わる
            }
        });
        sleeper.start();
        Thread joiner = new Thread(() -> {
            try {
                sleeper.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        joiner.start();
        IO.println("sleep()で待つ: " + waitFor(sleeper, Thread.State.TIMED_WAITING));
        IO.println("join()で待つ: " + waitFor(joiner, Thread.State.WAITING));
        sleeper.interrupt();
        joiner.join();

        Thread failing = new Thread(() -> {
            throw new IllegalStateException("失敗");
        });
        failing.setUncaughtExceptionHandler((t, e) -> { });
        failing.start();
        failing.join();
        IO.println("例外でrun()を抜けた後: " + failing.getState());
    }

    static void runVsStart() throws InterruptedException {
        Runnable task = () -> IO.println("タスクを実行したスレッド: " + Thread.currentThread().getName());
        Thread t = new Thread(task, "worker");
        IO.println("run()を直接呼ぶ");
        t.run();
        IO.println("start()を呼ぶ");
        t.start();
        t.join();
    }

    static void live() {
        TreeSet<String> names = new TreeSet<>();
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            names.add(t.getName() + (t.isDaemon() ? "（デーモン）" : ""));
        }
        IO.println("起動直後に動いているスレッドの数: " + names.size());
        names.forEach(n -> IO.println("  " + n));
    }

    static void runnable() {
        IO.println("@FunctionalInterface: " + Runnable.class.isAnnotationPresent(FunctionalInterface.class));
        Method[] abs = Arrays.stream(Runnable.class.getDeclaredMethods())
                .filter(m -> Modifier.isAbstract(m.getModifiers())).toArray(Method[]::new);
        for (Method m : abs) {
            IO.println("抽象メソッド: " + m.getReturnType() + " " + m.getName()
                    + "(" + m.getParameterCount() + "個の引数)");
        }
        IO.println("抽象メソッドの数: " + abs.length);
    }
}
