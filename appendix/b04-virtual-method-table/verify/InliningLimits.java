/**
 * インライン化の大きさと深さの上限を観察する。
 *
 *   java -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining InliningLimits
 */
public class InliningLimits {
    // バイトコードが35バイト以下の小さいメソッド
    static int small(int x) { return x * 2 + 1; }

    // 35バイトを超え、325バイト以下のメソッド
    static int medium(int x) {
        int a = x * 3 + 7;
        int b = a ^ (a >>> 3);
        int c = b * 31 + a;
        int d = c - (b << 2);
        return a + b + c + d;
    }

    // 325バイトを超える大きいメソッド
    static int large(int x) {
        int s = x;
        s = s * 31 + 1; s = s * 31 + 2; s = s * 31 + 3; s = s * 31 + 4; s = s * 31 + 5;
        s = s * 31 + 6; s = s * 31 + 7; s = s * 31 + 8; s = s * 31 + 9; s = s * 31 + 10;
        s = s * 37 + 1; s = s * 37 + 2; s = s * 37 + 3; s = s * 37 + 4; s = s * 37 + 5;
        s = s * 37 + 6; s = s * 37 + 7; s = s * 37 + 8; s = s * 37 + 9; s = s * 37 + 10;
        s = s * 41 + 1; s = s * 41 + 2; s = s * 41 + 3; s = s * 41 + 4; s = s * 41 + 5;
        s = s * 41 + 6; s = s * 41 + 7; s = s * 41 + 8; s = s * 41 + 9; s = s * 41 + 10;
        s = s * 43 + 1; s = s * 43 + 2; s = s * 43 + 3; s = s * 43 + 4; s = s * 43 + 5;
        s = s * 43 + 6; s = s * 43 + 7; s = s * 43 + 8; s = s * 43 + 9; s = s * 43 + 10;
        s = s * 47 + 1; s = s * 47 + 2; s = s * 47 + 3; s = s * 47 + 4; s = s * 47 + 5;
        s = s * 47 + 6; s = s * 47 + 7; s = s * 47 + 8; s = s * 47 + 9; s = s * 47 + 10;
        return s;
    }

    // 自分自身を呼ぶ再帰
    static int depth(int n) { return n <= 0 ? 0 : 1 + depth(n - 1); }

    public static void main(String[] args) {
        long r = 0;
        for (int i = 0; i < 200_000; i++) {
            r += small(i) + medium(i) + large(i) + depth(i % 8);
        }
        IO.println("result=" + r);
    }
}
