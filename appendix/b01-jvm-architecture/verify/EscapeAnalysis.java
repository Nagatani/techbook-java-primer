import java.lang.management.ManagementFactory;

/**
 * メソッドの外へ出ないオブジェクトの割り当てを、JITコンパイラー（C2）が取り除くことを観察する。
 *
 *   javac EscapeAnalysis.java
 *   java EscapeAnalysis                         （エスケープ解析あり）
 *   java -XX:-DoEscapeAnalysis EscapeAnalysis   （エスケープ解析なし）
 *   java -Xint EscapeAnalysis                   （JITコンパイルなし）
 *   java -XX:-DoEscapeAnalysis -XX:+UseCompactObjectHeaders EscapeAnalysis   （オブジェクトヘッダーを小さくする）
 */
public class EscapeAnalysis {
    record Point(int x, int y) { }

    static long sum(int n) {
        long s = 0;
        for (int i = 0; i < n; i++) {
            Point p = new Point(i, i + 1);       // pはこのメソッドの外へ出ない
            s += p.x() + p.y();
        }
        return s;
    }

    public static void main(String[] args) {
        var threads = (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();
        long result = 0;
        for (int i = 0; i < 200; i++) {
            result += sum(100_000);              // 暖機（JITコンパイルを促す）
        }
        long before = threads.getCurrentThreadAllocatedBytes();
        for (int i = 0; i < 100; i++) {
            result += sum(100_000);              // Pointを1000万個作る
        }
        long after = threads.getCurrentThreadAllocatedBytes();
        long bytes = after - before;
        IO.println("Pointを1000万個作る間にヒープに割り当てたバイト数: " + bytes);
        IO.println("Point 1個あたり: " + bytes / 10_000_000 + "バイト");
        IO.println("result=" + result);
    }
}
