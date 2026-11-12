import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 不変のアプリケーション設定（課題3の解答例）
 *
 * Map<String, String>（キーと値の組を持つマップ）とLinkedHashMapは第9章で学ぶ。
 * LinkedHashMapは、入れた順に組を並べて持つ。
 */
public final class ApplicationConfig {
    private final String appName;
    private final String version;
    private final Map<String, String> properties;

    // コンストラクターはprivateにし、作るときはビルダーかdefaultConfigを使ってもらう
    private ApplicationConfig(String appName, String version, Map<String, String> properties) {
        this.appName = appName;
        this.version = version;
        this.properties = new LinkedHashMap<>(properties);
    }

    // デフォルトの設定
    public static ApplicationConfig defaultConfig() {
        return new Builder("MyApp", "1.0.0")
                .property("timeout", "30")
                .property("debug", "false")
                .build();
    }

    public String getAppName() {
        return appName;
    }

    public String getVersion() {
        return version;
    }

    public String getProperty(String key) {
        return properties.get(key);
    }

    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    // 設定を1つ変えた（なければ足した）、新しいオブジェクトを返す
    public ApplicationConfig withProperty(String key, String value) {
        Map<String, String> newProperties = new LinkedHashMap<>(properties);
        newProperties.put(key, value);
        return new ApplicationConfig(appName, version, newProperties);
    }

    public void showInfo() {
        IO.println("アプリ名: " + appName);
        IO.println("バージョン: " + version);
        IO.println("設定値: " + properties);
    }

    // 設定を少しずつ組み立てるビルダー（第6章「ビルダーパターン」）
    public static class Builder {
        private final String appName;
        private final String version;
        private final Map<String, String> properties = new LinkedHashMap<>();

        public Builder(String appName, String version) {
            this.appName = appName;
            this.version = version;
        }

        public Builder property(String key, String value) {
            properties.put(key, value);
            return this;
        }

        // 組み立てた内容を検査してから作る。timeoutは正の整数でなければならない
        public ApplicationConfig build() {
            if (appName == null || appName.isBlank()) {
                throw new IllegalStateException("アプリ名がありません");
            }
            String timeout = properties.get("timeout");
            if (timeout != null && !isPositiveNumber(timeout)) {
                throw new IllegalStateException("timeoutは正の整数にします: " + timeout);
            }
            return new ApplicationConfig(appName, version, properties);
        }

        private static boolean isPositiveNumber(String text) {
            if (text.isEmpty()) {
                return false;
            }
            for (int i = 0; i < text.length(); i++) {
                if (!Character.isDigit(text.charAt(i))) {
                    return false;
                }
            }
            return Integer.parseInt(text) > 0;
        }
    }
}
