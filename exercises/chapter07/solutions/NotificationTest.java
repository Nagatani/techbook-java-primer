package chapter07.solutions;

/**
 * 通知とログのインターフェイスを試すプログラム（課題4の解答例）
 */
public class NotificationTest {
    public static void main(String[] args) {
        IO.println("=== 通知システム複合インターフェイステスト ===");

        IO.println("Emailによる通知:");
        new EmailNotifier().notify("システム開始");
        IO.println();

        IO.println("SMSによる通知:");
        new SMSNotifier().notify("エラー発生");
        IO.println();

        IO.println("システムログ:");
        new SystemLogger().log("処理完了");
        IO.println();

        // Loggable型の配列に、通知もできるクラスとログだけのクラスを一緒に入れられる
        Loggable[] loggers = {new EmailNotifier(), new SMSNotifier(), new SystemLogger()};
        IO.println("ログを記録できるもの: " + loggers.length + "種類");
        IO.println("記録したログ: " + Loggable.totalLogs() + "件");
    }
}
