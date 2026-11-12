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

        IO.println("設定を更新:");
        ApplicationConfig updated = config.withProperty("timeout", "60");
        IO.println("元の設定 - タイムアウト: " + config.getProperty("timeout"));
        IO.println("新しい設定 - タイムアウト: " + updated.getProperty("timeout"));
        IO.println();

        IO.println("設定を追加:");
        ApplicationConfig withLogging = config.withProperty("logging", "true");
        IO.println("元の設定: " + config.getProperties());
        IO.println("新しい設定: " + withLogging.getProperties());
        IO.println();

        IO.println("設定値の検証:");
        // 例外の受け取り方（try-catch）は第13章で学ぶ
        try {
            new ApplicationConfig.Builder("MyApp", "1.0.0").property("timeout", "abc").build();
        } catch (IllegalStateException e) {
            IO.println("作れませんでした: " + e.getMessage());
        }
    }
}
