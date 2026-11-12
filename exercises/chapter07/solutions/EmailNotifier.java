package chapter07.solutions;

/**
 * メールで通知し、送ったことをログに残す（課題4の解答例）
 */
public class EmailNotifier implements Notifiable, Loggable {
    @Override
    public String getChannel() {
        return "EMAIL";
    }

    @Override
    public String getSourceName() {
        return "Email送信";
    }

    // 通知のあとにログを残すよう、デフォルトメソッドをオーバーライドする
    @Override
    public void notify(String message) {
        Notifiable.super.notify(message);
        log(message);
    }
}
