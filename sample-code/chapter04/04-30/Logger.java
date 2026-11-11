public class Logger {
    // 文字列メッセージ
    public void log(String message) {
        IO.println("[INFO] " + message);
    }
    
    // 例外情報
    public void log(String message, Exception e) {
        IO.println("[ERROR] " + message + ": " + e.getMessage());
    }
    
    // レベル付きログ
    public void log(String level, String message) {
        IO.println("[" + level + "] " + message);
    }
    
    // フォーマット付きメッセージ
    // log(String, Object...)にすると、log("Hello %s", "World")が
    // レベル付きログのlog(String, String)に解決されるため、名前を分ける
    public void logf(String format, Object... args) {
        IO.println("[INFO] " + String.format(format, args));
    }
    
    // ログレベルを表す定数
    public static final String LOG_DEBUG = "DEBUG";
    public static final String LOG_INFO = "INFO";
    public static final String LOG_WARN = "WARN";
    public static final String LOG_ERROR = "ERROR";
}
