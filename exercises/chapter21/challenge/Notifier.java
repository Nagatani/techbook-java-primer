/** 会員へのお知らせを送る */
public interface Notifier {
    void send(String memberId, String message);
}
