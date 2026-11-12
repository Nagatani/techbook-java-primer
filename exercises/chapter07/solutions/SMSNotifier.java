package chapter07.solutions;

/**
 * SMSで通知し、送ったことをログに残す（課題4の解答例）
 */
public class SMSNotifier implements Notifiable, Loggable {
    @Override
    public String getChannel() {
        return "SMS";
    }

    @Override
    public String getSourceName() {
        return "SMS送信";
    }

    @Override
    public void notify(String message) {
        // インターフェイス名.super.メソッド名()で、そのインターフェイスのデフォルトメソッドを呼ぶ
        Notifiable.super.notify(message);
        log(message);
    }
}
