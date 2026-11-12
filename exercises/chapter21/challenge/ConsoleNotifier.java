/** お知らせを画面に表示する（メールの代わり） */
public class ConsoleNotifier implements Notifier {
    @Override
    public void send(String memberId, String message) {
        IO.println("  [お知らせ → " + memberId + "] " + message);
    }
}
