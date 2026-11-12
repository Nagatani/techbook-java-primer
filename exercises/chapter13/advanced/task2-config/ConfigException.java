/** 設定の読み込みに失敗したときの例外（検査例外） */
public class ConfigException extends Exception {
    private final int lineNumber;

    /** 原因となる例外がないとき */
    public ConfigException(String message, int lineNumber) {
        super(message);
        this.lineNumber = lineNumber;
    }

    /** 原因となる例外を包むとき（例外の連鎖） */
    public ConfigException(String message, int lineNumber, Throwable cause) {
        super(message, cause);
        this.lineNumber = lineNumber;
    }

    /** 問題のあった行の番号（行に結びつかないときは0） */
    public int getLineNumber() {
        return lineNumber;
    }
}
