package com.example.oss.plugins;

/**
 * プラグインに提供されるコンテキスト情報
 * 
 * <p>プラグインがホストアプリケーションの機能にアクセスするための
 * インターフェースを提供します。
 * 
 * @since 1.0.0
 */
public interface PluginContext {
    
    /**
     * プラグインマネージャーを取得します
     * 
     * @return プラグインマネージャー
     */
    PluginManager getPluginManager();
    
    /**
     * 設定値を取得します
     * 
     * @param key 設定キー
     * @return 設定値（存在しない場合null）
     */
    String getConfig(String key);
    
    /**
     * 設定値を取得します（デフォルト値付き）
     * 
     * @param key 設定キー
     * @param defaultValue デフォルト値
     * @return 設定値（存在しない場合はデフォルト値）
     */
    String getConfig(String key, String defaultValue);
    
    /**
     * プラグインのデータディレクトリを取得します
     * 
     * @return データディレクトリのパス
     */
    String getDataDirectory();
    
    /**
     * ログを出力します
     * 
     * @param level ログレベル
     * @param message ログメッセージ
     */
    void log(LogLevel level, String message);
    
    /**
     * ログを出力します（例外付き）
     * 
     * @param level ログレベル
     * @param message ログメッセージ
     * @param throwable 例外
     */
    void log(LogLevel level, String message, Throwable throwable);
    
    /**
     * ログレベル
     */
    enum LogLevel {
        DEBUG,
        INFO,
        WARN,
        ERROR
    }
}