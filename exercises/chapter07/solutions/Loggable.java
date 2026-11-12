package chapter07.solutions;

/**
 * ログを記録できるもの（課題4の解答例）
 *
 * ログの書式はどのクラスでも同じにしたいので、デフォルトメソッドにまとめる。
 */
public interface Loggable {
    // インターフェイスのフィールドは、すべて定数（public static final）になり、値を変えられない。
    // そのため、記録した件数は別のクラス（LogCounter）のstaticフィールドで数える

    String getSourceName();

    default void log(String message) {
        LogCounter.increment();
        IO.println("[LOG] " + getSourceName() + ": " + message);
    }

    // インターフェイスの静的メソッド。Loggable.totalLogs()のように、インターフェイスの名前で呼ぶ
    static int totalLogs() {
        return LogCounter.count();
    }
}
