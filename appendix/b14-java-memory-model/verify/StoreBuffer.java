/**
 * 2つのスレッドが、それぞれ自分の変数に1を書いてから相手の変数を読む（ストアバッファーのリトマス試験）。
 * 書いた順序どおりに実行されるなら、少なくとも一方は1を読むので、両方が0を読むことはない。
 * JMMは、データ競合のあるこのプログラムで両方が0になることを許している。
 * 何回試して、両方が0になった回数を数える（回数は実行のたびに変わる。0回のこともある）。
 *
 *   java StoreBuffer          volatileでない変数
 *   java StoreBuffer volatile volatileの変数（両方が0にはならない）
 */
public class StoreBuffer {

    static final int ROUNDS = 200_000;

    static int x;
    static int y;
    static volatile int vx;
    static volatile int vy;

    static int r1;
    static int r2;

    /** 各回の開始と終了をそろえるための番号 */
    static volatile int start;
    static volatile int done1;
    static volatile int done2;

    public static void main(String[] args) throws InterruptedException {
        boolean useVolatile = args.length > 0 && args[0].equals("volatile");
        Thread t1 = new Thread(() -> {
            for (int round = 1; round <= ROUNDS; round++) {
                while (start < round) {
                    Thread.onSpinWait();
                }
                if (useVolatile) {
                    vx = 1;
                    r1 = vy;
                } else {
                    x = 1;
                    r1 = y;
                }
                done1 = round;
            }
        });
        Thread t2 = new Thread(() -> {
            for (int round = 1; round <= ROUNDS; round++) {
                while (start < round) {
                    Thread.onSpinWait();
                }
                if (useVolatile) {
                    vy = 1;
                    r2 = vx;
                } else {
                    y = 1;
                    r2 = x;
                }
                done2 = round;
            }
        });
        t1.start();
        t2.start();

        int bothZero = 0;
        for (int round = 1; round <= ROUNDS; round++) {
            x = 0;
            y = 0;
            vx = 0;
            vy = 0;
            start = round;
            while (done1 < round || done2 < round) {
                Thread.onSpinWait();
            }
            if (r1 == 0 && r2 == 0) {
                bothZero++;
            }
        }
        t1.join();
        t2.join();
        String kind = useVolatile ? "volatile" : "plain";
        IO.println(kind + ": " + ROUNDS + "回のうち、両方が0を読んだ回数は"
                + (bothZero > 0 ? "1回以上" : "0回"));
        System.err.println("（両方が0を読んだ回数: " + bothZero + "）");
    }
}
