/**
 * 不変のアプリケーション設定（課題3の解答例）
 *
 * 設定の項目をfinalのフィールドで持ち、ビルダー（第6章「ビルダーパターン」）で組み立てる。
 */
public final class ApplicationConfig {
    private final String appName;
    private final String version;
    private final int timeout;
    private final boolean debug;
    private final boolean logging;

    // コンストラクターはprivateにし、作るときはビルダーかdefaultConfigを使ってもらう
    private ApplicationConfig(Builder builder) {
        this.appName = builder.appName;
        this.version = builder.version;
        this.timeout = builder.timeout;
        this.debug = builder.debug;
        this.logging = builder.logging;
    }

    public static ApplicationConfig defaultConfig() {
        return new Builder("MyApp", "1.0.0").build();
    }

    public String getAppName() {
        return appName;
    }

    public String getVersion() {
        return version;
    }

    public int getTimeout() {
        return timeout;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isLogging() {
        return logging;
    }

    // 今の設定をビルダーに写す。一部だけを変えた設定を作るときに使う
    public Builder toBuilder() {
        return new Builder(appName, version).timeout(timeout).debug(debug).logging(logging);
    }

    public ApplicationConfig withTimeout(int newTimeout) {
        return toBuilder().timeout(newTimeout).build();
    }

    public ApplicationConfig withLogging(boolean newLogging) {
        return toBuilder().logging(newLogging).build();
    }

    public void showInfo() {
        IO.println("アプリ名: " + appName);
        IO.println("バージョン: " + version);
        IO.println("タイムアウト: " + timeout + "秒");
        IO.println("デバッグ: " + debug);
        IO.println("ログ出力: " + logging);
    }

    public static class Builder {
        private final String appName;
        private final String version;
        // 指定しなかった項目は、この初期値になる
        private int timeout = 30;
        private boolean debug = false;
        private boolean logging = false;

        public Builder(String appName, String version) {
            this.appName = appName;
            this.version = version;
        }

        public Builder timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder logging(boolean logging) {
            this.logging = logging;
            return this;
        }

        // 組み立てた内容を検査してから作る
        public ApplicationConfig build() {
            if (appName == null || appName.isBlank()) {
                throw new IllegalStateException("アプリ名がありません");
            }
            if (timeout <= 0) {
                throw new IllegalStateException("タイムアウトは1秒以上にします: " + timeout);
            }
            return new ApplicationConfig(this);
        }
    }
}
