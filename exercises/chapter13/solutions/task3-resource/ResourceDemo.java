/**
 * try-with-resourcesを試すプログラム（課題3の解答例）
 */
public class ResourceDemo {
    public static void main(String[] args) {
        IO.println("--- 1つのリソース ---");
        try (TrackedResource r = new TrackedResource("接続A")) {
            r.use("データを読む");
        }
        IO.println("\n--- 2つのリソース（閉じる順序） ---");
        // 複数のリソースは、開いたのと逆の順に閉じられる
        try (TrackedResource in = new TrackedResource("入力");
                TrackedResource out = new TrackedResource("出力")) {
            in.use("1行読む");
            out.use("1行書く");
        }
        IO.println("\n--- 処理中の例外 ---");
        // 例外が起きると、catchより先にcloseが呼ばれる
        try (TrackedResource r = new TrackedResource("接続B")) {
            r.use("");
            IO.println("ここは実行されません");
        } catch (IllegalStateException e) {
            IO.println("catch: " + e.getMessage());
        } finally {
            IO.println("finally: 後始末が終わりました");
        }
        IO.println("\n--- finallyで閉じる書き方（比較用） ---");
        // try-with-resourcesがなかったころの書き方。閉じ忘れやnullの確かめが要る
        TrackedResource r = null;
        try {
            r = new TrackedResource("接続C");
            r.use("データを書く");
        } finally {
            if (r != null) {
                r.close();
            }
        }
    }
}
