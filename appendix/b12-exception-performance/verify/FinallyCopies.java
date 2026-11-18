/**
 * finallyの中身が、抜け出す道ごとにコピーされることを見る。
 *
 *   javac FinallyCopies.java && javap -c -p FinallyCopies
 */
public class FinallyCopies {

    static void work(int x) {
        if (x < 0) {
            throw new IllegalStateException("negative");
        }
    }

    static void cleanup() {
        IO.println("cleanup");
    }

    static void run(int x) {
        try {
            work(x);
        } catch (IllegalStateException e) {
            IO.println("caught");
        } finally {
            cleanup();
        }
    }

    public static void main(String[] args) {
        run(1);
        run(-1);
    }
}
