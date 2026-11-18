import java.util.HashSet;
import java.util.Set;

/**
 * 同じ箇所でJVMが暗黙に投げる例外（NullPointerExceptionなど）を繰り返すと、
 * C2がスタックトレースもメッセージもない例外に置き換えることを見る。
 *
 *   javac FastThrow.java
 *   java FastThrow                                  （既定: -XX:+OmitStackTraceInFastThrow）
 *   java -XX:-OmitStackTraceInFastThrow FastThrow
 */
public class FastThrow {

    static String text = null;

    static int length() {
        return text.length();
    }

    static int divide(int a, int b) {
        return a / b;
    }

    public static void main(String[] args) {
        int npeEmpty = 0;
        Set<Integer> emptyIds = new HashSet<>();
        NullPointerException last = null;
        int aeEmpty = 0;
        for (int i = 0; i < 200_000; i++) {
            try {
                length();
            } catch (NullPointerException e) {
                if (e.getStackTrace().length == 0) {
                    npeEmpty++;
                    emptyIds.add(System.identityHashCode(e));
                }
                last = e;
            }
            try {
                divide(i, 0);
            } catch (ArithmeticException e) {
                if (e.getStackTrace().length == 0) {
                    aeEmpty++;
                }
            }
        }
        IO.println("別の箇所で1回だけ起こしたNullPointerExceptionのメッセージ: " + firstMessage());
        IO.println("スタックトレースのないNullPointerException: "
                + (npeEmpty > 0 ? "あり" : "なし"));
        if (npeEmpty > 0) {
            IO.println("スタックトレースのないNullPointerExceptionのオブジェクトの数: "
                    + emptyIds.size());
        }
        IO.println("スタックトレースのないArithmeticException: "
                + (aeEmpty > 0 ? "あり" : "なし"));
        IO.println("最後のNullPointerExceptionのメッセージ: " + last.getMessage());
        IO.println("最後のNullPointerExceptionの要素数: " + last.getStackTrace().length);
    }

    static String firstMessage() {
        try {
            return String.valueOf(text.length());
        } catch (NullPointerException e) {
            return e.getMessage();
        }
    }
}
