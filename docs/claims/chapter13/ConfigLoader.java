// 第13章コラム「スタックトレースを読める人は強い」：原因つきの例外のスタックトレース
public class ConfigLoader {
    static int readPort(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("設定のポート番号が数字ではありません", e);
        }
    }

    static void start() {
        int port = readPort("80a");
        IO.println("port=" + port);
    }

    public static void main(String[] args) {
        start();
    }
}
