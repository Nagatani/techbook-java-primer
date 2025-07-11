/**
 * リスト23-2
 * AppConfigクラス
 * 
 * 元ファイル: chapter23-build-and-deploy.md (208行目)
 */

// AppConfig.java
public class AppConfig {
    private static final String VERSION = "1.0.0";
    private static final String APP_NAME = "Todo Manager";
    
    public static String getVersion() {
        return VERSION;
    }
    
    public static String getAppName() {
        return APP_NAME;
    }
}