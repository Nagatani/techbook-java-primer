/**
 * アプリの設定。値はprivateにし、検証してから変える
 *
 * 値を変えるメソッドはset(名前, 値)の2つのオーバーロード（int用とString用）だけにし、
 * 変えた内容を履歴に残す。データベースのパスワードにはgetterを作らない。
 */
public class AppSettings {
    public static final String DEVELOPMENT = "開発";
    public static final String PRODUCTION = "本番";
    private static final int MAX_HISTORY = 20;

    private final String environment;
    private final String dbPassword;
    private int port;
    private int maxUsers;
    private String logLevel;
    private String[] history = new String[MAX_HISTORY];
    private int historyCount;

    // すべての値を指定するコンストラクター。検証はここに集める
    public AppSettings(String environment, String dbPassword, int port, int maxUsers, String logLevel) {
        if (!DEVELOPMENT.equals(environment) && !PRODUCTION.equals(environment)) {
            throw new IllegalArgumentException("環境は「開発」か「本番」にしてください");
        }
        if (dbPassword == null || dbPassword.isEmpty()) {
            throw new IllegalArgumentException("データベースのパスワードを指定してください");
        }
        if (!isValidPort(port) || !isValidMaxUsers(maxUsers) || !isValidLogLevel(logLevel)) {
            throw new IllegalArgumentException("設定の値が正しくありません");
        }
        this.environment = environment;
        this.dbPassword = dbPassword;
        this.port = port;
        this.maxUsers = maxUsers;
        this.logLevel = logLevel;
    }

    // 環境だけを指定したときは、環境ごとの標準の値にする
    public AppSettings(String environment, String dbPassword) {
        this(environment, dbPassword,
                DEVELOPMENT.equals(environment) ? 8080 : 80,
                DEVELOPMENT.equals(environment) ? 100 : 10000,
                DEVELOPMENT.equals(environment) ? "DEBUG" : "INFO");
    }

    public String getEnvironment() {
        return environment;
    }

    public int getPort() {
        return port;
    }

    public int getMaxUsers() {
        return maxUsers;
    }

    public String getLogLevel() {
        return logLevel;
    }

    // パスワードは返さず、合っているかどうかだけを答える
    public boolean matchesDbPassword(String password) {
        return dbPassword.equals(password);
    }

    // 数値の設定を変える。名前が違うか、値が範囲の外ならfalse
    public boolean set(String name, int value) {
        if (name.equals("port") && isValidPort(value)) {
            addHistory(name, String.valueOf(port), String.valueOf(value));
            port = value;
            return true;
        }
        if (name.equals("maxUsers") && isValidMaxUsers(value)) {
            addHistory(name, String.valueOf(maxUsers), String.valueOf(value));
            maxUsers = value;
            return true;
        }
        return false;
    }

    // 文字列の設定を変える
    public boolean set(String name, String value) {
        if (name.equals("logLevel") && isValidLogLevel(value)) {
            addHistory(name, logLevel, value);
            logLevel = value;
            return true;
        }
        return false;
    }

    public void show() {
        IO.println("[" + environment + "] port=" + port + ", maxUsers=" + maxUsers
                + ", logLevel=" + logLevel + ", dbPassword=****");
    }

    public void showHistory() {
        if (historyCount == 0) {
            IO.println("  変更はありません");
        }
        for (int i = 0; i < historyCount; i++) {
            IO.println("  " + (i + 1) + ". " + history[i]);
        }
    }

    private void addHistory(String name, String before, String after) {
        if (historyCount < history.length) {
            history[historyCount] = name + ": " + before + " → " + after;
            historyCount++;
        }
    }

    private static boolean isValidPort(int port) {
        return port >= 1 && port <= 65535;
    }

    private static boolean isValidMaxUsers(int maxUsers) {
        return maxUsers >= 1;
    }

    private static boolean isValidLogLevel(String level) {
        return "DEBUG".equals(level) || "INFO".equals(level)
                || "WARN".equals(level) || "ERROR".equals(level);
    }
}
