package com.example.oss.plugins;

/**
 * プラグインシステムで発生する例外
 * 
 * <p>プラグインのロード、初期化、実行時に発生するエラーを表現します。
 * 
 * @since 1.0.0
 */
public class PluginException extends Exception {
    
    /**
     * 例外を構築します
     * 
     * @param message エラーメッセージ
     */
    public PluginException(String message) {
        super(message);
    }
    
    /**
     * 原因付きの例外を構築します
     * 
     * @param message エラーメッセージ
     * @param cause 原因となった例外
     */
    public PluginException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * 原因となった例外から構築します
     * 
     * @param cause 原因となった例外
     */
    public PluginException(Throwable cause) {
        super(cause);
    }
}