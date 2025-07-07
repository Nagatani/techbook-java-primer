package com.example.oss.plugins;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PluginContextのデフォルト実装
 * 
 * @since 1.0.0
 */
public class DefaultPluginContext implements PluginContext {
    private static final Logger logger = LoggerFactory.getLogger(DefaultPluginContext.class);
    
    private final PluginManager pluginManager;
    private final Map<String, String> config = new ConcurrentHashMap<>();
    private final String dataDirectory;
    
    /**
     * コンテキストを構築します
     * 
     * @param pluginManager プラグインマネージャー
     */
    public DefaultPluginContext(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
        this.dataDirectory = System.getProperty("user.dir") + "/plugins/data";
    }
    
    @Override
    public PluginManager getPluginManager() {
        return pluginManager;
    }
    
    @Override
    public String getConfig(String key) {
        return config.get(key);
    }
    
    @Override
    public String getConfig(String key, String defaultValue) {
        return config.getOrDefault(key, defaultValue);
    }
    
    @Override
    public String getDataDirectory() {
        return dataDirectory;
    }
    
    @Override
    public void log(LogLevel level, String message) {
        switch (level) {
            case DEBUG:
                logger.debug(message);
                break;
            case INFO:
                logger.info(message);
                break;
            case WARN:
                logger.warn(message);
                break;
            case ERROR:
                logger.error(message);
                break;
        }
    }
    
    @Override
    public void log(LogLevel level, String message, Throwable throwable) {
        switch (level) {
            case DEBUG:
                logger.debug(message, throwable);
                break;
            case INFO:
                logger.info(message, throwable);
                break;
            case WARN:
                logger.warn(message, throwable);
                break;
            case ERROR:
                logger.error(message, throwable);
                break;
        }
    }
    
    /**
     * 設定を追加します
     * 
     * @param key 設定キー
     * @param value 設定値
     */
    public void setConfig(String key, String value) {
        config.put(key, value);
    }
    
    /**
     * 設定をマップから一括で追加します
     * 
     * @param configMap 設定マップ
     */
    public void setConfig(Map<String, String> configMap) {
        config.putAll(configMap);
    }
}