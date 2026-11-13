/**
 * ApplicationConfigを試すプログラム（課題3の解答例）
 */
public class ApplicationConfigTest {
    public static void main(String[] args) {
        IO.println("=== アプリケーション設定管理テスト ===");

        ApplicationConfig config = ApplicationConfig.defaultConfig();
        IO.println("デフォルト設定:");
        config.showInfo();
        IO.println();

        IO.println("タイムアウトを更新:");
        ApplicationConfig updated = config.withTimeout(60);
        IO.println("元の設定: " + config.getTimeout() + "秒");
        IO.println("新しい設定: " + updated.getTimeout() + "秒");
        IO.println();

        IO.println("ログ出力を有効に:");
        ApplicationConfig withLogging = config.withLogging(true);
        IO.println("元の設定: " + config.isLogging());
        IO.println("新しい設定: " + withLogging.isLogging());
        IO.println();

        IO.println("ビルダーで組み立てた設定:");
        new ApplicationConfig.Builder("Report", "2.1.0").debug(true).timeout(10).build().showInfo();
        IO.println();

        IO.println("設定値の検証:");
        // 例外の受け取り方（try-catch）は第13章で学ぶ（第4章の本文にも例がある）
        try {
            new ApplicationConfig.Builder("MyApp", "1.0.0").timeout(-5).build();
        } catch (IllegalStateException e) {
            IO.println("作れませんでした: " + e.getMessage());
        }
    }
}
