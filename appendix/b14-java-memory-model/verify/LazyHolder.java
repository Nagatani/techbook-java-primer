/**
 * 遅延初期化のホルダークラスのイディオムで、インスタンスがいつ作られるかを観察する。
 * ホルダークラスは、そのstaticフィールドを初めて読んだときに初期化される（JLS 12.4.1）。
 * 初期化はJVMがロックをかけて1回だけ行う（JLS 12.4.2）ので、同期を書かなくてよい。
 *
 *   java LazyHolder
 */
public class LazyHolder {

    static final class Config {
        Config() {
            IO.println("Configを作った（" + Thread.currentThread().getName() + "）");
        }
    }

    /** getInstance()から初めて参照されたときに初期化される */
    private static final class Holder {
        static final Config INSTANCE = new Config();
    }

    static Config getInstance() {
        return Holder.INSTANCE;
    }

    public static void main(String[] args) throws InterruptedException {
        IO.println("mainの開始（まだConfigは作られていない）");
        Thread[] threads = new Thread[8];
        Config[] seen = new Config[threads.length];
        for (int i = 0; i < threads.length; i++) {
            int n = i;
            threads[i] = new Thread(() -> seen[n] = getInstance(), "worker-" + i);
        }
        for (Thread t : threads) {
            t.start();
        }
        for (Thread t : threads) {
            t.join();
        }
        boolean same = true;
        for (Config c : seen) {
            same &= c == seen[0];
        }
        IO.println("8つのスレッドが同じインスタンスを得た: " + same);
    }
}
