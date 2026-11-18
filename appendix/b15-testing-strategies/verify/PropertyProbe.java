import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.IntBinaryOperator;

/**
 * Property-based Testingの考え方を、小さな自作の仕組みで観察する。
 * 1. 乱数で入力を作り、「どんな入力でも成り立つはずの性質」を確かめる
 * 2. 性質が成り立たない入力（反例）が見つかったら、失敗したままの小さな入力へ縮める
 * 3. 乱数の種（シード）を固定すると、同じ反例が再現する
 * jqwikなどのライブラリは、同じことを入力の型ごとに用意した生成器と縮め方で行う。
 */
public class PropertyProbe {

    /** 2つの値の中間の値（a + b がintの範囲を超えると誤った値になる） */
    static final IntBinaryOperator BUGGY = (a, b) -> (a + b) / 2;

    /** longで計算してから戻す版 */
    static final IntBinaryOperator FIXED = (a, b) -> (int) (((long) a + b) / 2);

    /** 性質：中間の値は、小さいほうの値以上、大きいほうの値以下である */
    static boolean property(IntBinaryOperator mid, int a, int b) {
        int m = mid.applyAsInt(a, b);
        return Math.min(a, b) <= m && m <= Math.max(a, b);
    }

    /** 0に近づける候補（0、x/2、3x/4、……）を、0に近い順に返す */
    static List<Integer> towardZero(int x) {
        List<Integer> list = new ArrayList<>();
        for (int d = x; d != 0; d /= 2) {
            list.add(x - d);
        }
        return list;
    }

    /** 反例を、性質が成り立たないまま、できるだけ0に近い値へ縮める */
    static int[] shrink(IntBinaryOperator mid, int a, int b) {
        int steps = 0;
        boolean progress = true;
        while (progress) {
            progress = false;
            for (int c : towardZero(a)) {
                if (!property(mid, c, b)) {
                    a = c;
                    progress = true;
                    steps++;
                    break;
                }
            }
            for (int c : towardZero(b)) {
                if (!property(mid, a, c)) {
                    b = c;
                    progress = true;
                    steps++;
                    break;
                }
            }
        }
        IO.println("  shrink steps: " + steps);
        return new int[] {a, b};
    }

    /** シードを決めて入力をtries件作り、最初の反例を返す（なければnull） */
    static int[] search(IntBinaryOperator mid, long seed, int tries) {
        Random random = new Random(seed);
        for (int i = 1; i <= tries; i++) {
            int a = random.nextInt();
            int b = random.nextInt();
            if (!property(mid, a, b)) {
                IO.println("  counterexample found at try %d: (%d, %d) -> %d"
                        .formatted(i, a, b, mid.applyAsInt(a, b)));
                return new int[] {a, b};
            }
        }
        IO.println("  property held for %d inputs".formatted(tries));
        return null;
    }

    public static void main(String[] args) {
        IO.println("--- hand-picked examples (buggy version)");
        int[][] examples = {{1, 3}, {-4, 4}, {10, 10}, {0, 100}};
        for (int[] e : examples) {
            IO.println("  mid(%d, %d) = %d  property: %s".formatted(e[0], e[1],
                    BUGGY.applyAsInt(e[0], e[1]), property(BUGGY, e[0], e[1])));
        }

        IO.println("--- random inputs, seed 42 (buggy version)");
        int[] found = search(BUGGY, 42, 1000);
        int[] small = shrink(BUGGY, found[0], found[1]);
        IO.println("  shrunk counterexample: (%d, %d) -> %d".formatted(
                small[0], small[1], BUGGY.applyAsInt(small[0], small[1])));
        IO.println("  a + b = %d (Integer.MAX_VALUE + 1 = %d)".formatted(
                (long) small[0] + small[1], (long) Integer.MAX_VALUE + 1));

        IO.println("--- same seed again (buggy version)");
        int[] again = search(BUGGY, 42, 1000);
        IO.println("  same counterexample: " + (again[0] == found[0] && again[1] == found[1]));

        IO.println("--- random inputs, seed 42 (fixed version)");
        search(FIXED, 42, 1000);
    }
}
