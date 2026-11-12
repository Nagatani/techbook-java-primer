public class SettingsApp {
    public static void main(String[] args) {
        IO.println("=== 標準の値 ===");
        AppSettings dev = new AppSettings(AppSettings.DEVELOPMENT, "dev-secret");
        AppSettings prod = new AppSettings(AppSettings.PRODUCTION, "prod-secret");
        dev.show();
        prod.show();

        IO.println();
        IO.println("=== 開発環境の設定を変える ===");
        change(dev, "port", 9090);
        change(dev, "maxUsers", -1);
        change(dev, "logLevel", "VERBOSE");
        change(dev, "logLevel", "WARN");
        change(dev, "timeout", 30);
        dev.show();

        IO.println();
        IO.println("=== 変更の履歴 ===");
        IO.println("開発:");
        dev.showHistory();
        IO.println("本番:");
        prod.showHistory();

        IO.println();
        IO.println("=== パスワードの確認 ===");
        IO.println("dev-secretか: " + dev.matchesDbPassword("dev-secret"));
        IO.println("prod-secretか: " + dev.matchesDbPassword("prod-secret"));
    }

    // int版とString版のどちらのsetが呼ばれるかは、引数の型で決まる
    private static void change(AppSettings settings, String name, int value) {
        IO.println(name + " → " + value + ": " + (settings.set(name, value) ? "変更しました" : "変更できません"));
    }

    private static void change(AppSettings settings, String name, String value) {
        IO.println(name + " → " + value + ": " + (settings.set(name, value) ? "変更しました" : "変更できません"));
    }
}
