package com.example.oss.plugins;

import com.example.oss.core.Version;

/**
 * プラグインシステムの基本インターフェース
 * 
 * <p>すべてのプラグインはこのインターフェースを実装する必要があります。
 * プラグインのライフサイクル管理とメタデータを提供します。
 * 
 * @since 1.0.0
 */
public interface Plugin {
    
    /**
     * プラグインの名前を取得します
     * 
     * @return プラグイン名
     */
    String getName();
    
    /**
     * プラグインのバージョンを取得します
     * 
     * @return プラグインバージョン
     */
    Version getVersion();
    
    /**
     * プラグインを初期化します
     * 
     * <p>このメソッドは、プラグインがロードされた後に呼ばれます。
     * 必要なリソースの初期化や設定の読み込みを行います。
     * 
     * @param context プラグインコンテキスト
     * @throws PluginException 初期化に失敗した場合
     */
    void initialize(PluginContext context) throws PluginException;
    
    /**
     * プラグインをシャットダウンします
     * 
     * <p>このメソッドは、プラグインがアンロードされる前に呼ばれます。
     * リソースの解放やクリーンアップ処理を行います。
     */
    void shutdown();
    
    /**
     * プラグインの説明を取得します
     * 
     * @return プラグインの説明（オプション）
     */
    default String getDescription() {
        return "";
    }
    
    /**
     * プラグインの作者を取得します
     * 
     * @return プラグインの作者（オプション）
     */
    default String getAuthor() {
        return "Unknown";
    }
}