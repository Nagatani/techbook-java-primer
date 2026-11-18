import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

/**
 * 別のスレッドが立てたフラグを待つループが終わるかを、フラグの読み書きの方法ごとに観察する。
 *
 *   java StopFlag plain        volatileでないフィールド（JITが読み込みをループの外へ出すと終わらない）
 *   java StopFlag volatile     volatileのフィールド
 *   java StopFlag opaque       VarHandleのsetOpaque・getOpaque
 *   java StopFlag acquire      VarHandleのsetRelease・getAcquire
 *   java -Xint StopFlag plain  インタープリターだけで動かす
 *
 * ワーカーが止まらなくても、mainは2秒待ったあとに結果を表示して終了する
 * （ワーカーはデーモンスレッドなので、プログラムの終了を妨げない）。
 */
public class StopFlag {

    static boolean plainStop;
    static volatile boolean volatileStop;
    /** VarHandleで読み書きするフィールド（宣言はvolatileでない） */
    static boolean handleStop;
    static final VarHandle STOP;

    static {
        try {
            STOP = MethodHandles.lookup()
                    .findStaticVarHandle(StopFlag.class, "handleStop", boolean.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        String mode = args.length > 0 ? args[0] : "plain";
        Runnable loop = switch (mode) {
            case "volatile" -> StopFlag::spinVolatile;
            case "opaque" -> StopFlag::spinOpaque;
            case "acquire" -> StopFlag::spinAcquire;
            default -> StopFlag::spinPlain;
        };
        Thread worker = new Thread(loop);
        worker.setDaemon(true);
        worker.start();

        // ループが何度も回り、JITコンパイルされるまで待つ
        Thread.sleep(1000);
        switch (mode) {
            case "volatile" -> volatileStop = true;
            case "opaque" -> STOP.setOpaque(true);
            case "acquire" -> STOP.setRelease(true);
            default -> plainStop = true;
        }
        worker.join(2000);

        IO.println(mode + "のフラグ: ワーカーは"
                + (worker.isAlive() ? "2秒待っても止まらない" : "止まった"));
    }

    static void spinPlain() {
        long count = 0;
        while (!plainStop) {
            count++;
        }
    }

    static void spinVolatile() {
        long count = 0;
        while (!volatileStop) {
            count++;
        }
    }

    static void spinOpaque() {
        long count = 0;
        while (!(boolean) STOP.getOpaque()) {
            count++;
        }
    }

    static void spinAcquire() {
        long count = 0;
        while (!(boolean) STOP.getAcquire()) {
            count++;
        }
    }
}
