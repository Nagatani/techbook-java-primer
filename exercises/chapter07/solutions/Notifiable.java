package chapter07.solutions;

/**
 * 通知を送れるもの（課題4の解答例）
 */
public interface Notifiable {
    // 送り先の種類（EMAIL・SMSなど）
    String getChannel();

    default void notify(String message) {
        IO.println("[" + getChannel() + "] メッセージを送信しました: " + message);
    }
}
