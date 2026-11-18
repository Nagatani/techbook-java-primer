/**
 * 例外のコストの内訳を観察する。
 * 時間は環境で変わるので、数値ではなく「どちらが大きいか」だけを表示する。
 *
 *   javac StackTraceCost.java && java StackTraceCost
 */
public class StackTraceCost {

    /** スタックトレースを作らない例外（writableStackTrace=false） */
    static class Light extends RuntimeException {
        Light() {
            super(null, null, false, false);
        }
    }

    static volatile Object sink;

    /** depth段の呼び出しの奥で、kind の例外をn個作る */
    static void at(int depth, int kind, int n) {
        if (depth > 0) {
            at(depth - 1, kind, n);
            return;
        }
        for (int i = 0; i < n; i++) {
            switch (kind) {
                case 0 -> sink = new RuntimeException();
                case 1 -> sink = new Light();
                default -> sink = new RuntimeException().getStackTrace();
            }
        }
    }

    static final Light PREMADE = new Light();

    /** depth段の呼び出しの奥から、作っておいた例外を投げる */
    static void throwFrom(int depth) {
        if (depth > 0) {
            throwFrom(depth - 1);
            return;
        }
        throw PREMADE;
    }

    /** 作っておいた例外を、depth段の奥から一番上までn回投げて受け取る */
    static long unwind(int depth, int n) {
        long best = Long.MAX_VALUE;
        for (int round = 0; round < 7; round++) {
            long start = System.nanoTime();
            for (int i = 0; i < n; i++) {
                try {
                    throwFrom(depth);
                } catch (Light e) {
                    sink = e;
                }
            }
            best = Math.min(best, System.nanoTime() - start);
        }
        return best;
    }

    /** 何回か測って、いちばん短い時間（ナノ秒）を返す */
    static long measure(int depth, int kind) {
        long best = Long.MAX_VALUE;
        for (int round = 0; round < 7; round++) {
            long start = System.nanoTime();
            at(depth, kind, 2_000);
            best = Math.min(best, System.nanoTime() - start);
        }
        return best;
    }

    static int depthOfTrace(int depth) {
        if (depth > 0) {
            return depthOfTrace(depth - 1);
        }
        return new RuntimeException().getStackTrace().length;
    }

    public static void main(String[] args) {
        // 1. スタックトレースの長さは、呼び出しの深さとともに増える
        for (int d : new int[] {10, 100, 1000, 2000}) {
            IO.println("深さ%dで作った例外のスタックトレースの要素数: %d".formatted(d, depthOfTrace(d)));
        }

        // 暖機
        for (int i = 0; i < 3; i++) {
            for (int kind = 0; kind < 3; kind++) {
                measure(10, kind);
                measure(500, kind);
            }
            unwind(10, 2_000);
            unwind(500, 2_000);
        }

        long shallow = measure(10, 0);
        long deep = measure(500, 0);
        long lightShallow = measure(10, 1);
        long lightDeep = measure(500, 1);
        long withElements = measure(500, 2);
        long unwindShallow = unwind(10, 2_000);
        long unwindDeep = unwind(500, 2_000);

        // 2. スタックトレースを作る例外は、深いところで作るほど時間がかかる
        IO.println("深さ500で作る例外は深さ10より時間がかかる: " + (deep > shallow * 5));
        // 3. スタックトレースを作らない例外は、深さによらず短い
        IO.println("スタックトレースを作らない例外は作る例外より短い（深さ500）: "
                + (lightDeep * 5 < deep));
        IO.println("スタックトレースを作らない例外は深さで大きく変わらない: "
                + (lightDeep < lightShallow * 3 + 50_000));
        // 4. getStackTrace()でStackTraceElementの配列を作ると、さらに時間がかかる
        IO.println("getStackTrace()を呼ぶとさらに時間がかかる（深さ500）: "
                + (withElements > deep * 2));
        // 5. スタックトレースを作らない例外でも、投げて多くのフレームを抜けるには時間がかかる
        IO.println("作っておいた例外を深さ500から投げると深さ10より時間がかかる: "
                + (unwindDeep > unwindShallow * 5));
    }
}
