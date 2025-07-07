package com.example.oss.plugins;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * プラグイン用のクラスローダー
 * 
 * <p>プラグインを独立した名前空間でロードし、
 * クラスの競合を防ぎます。
 * 
 * @since 1.0.0
 */
public class PluginClassLoader {
    private final Map<Path, ClassLoader> loaders = new ConcurrentHashMap<>();
    
    /**
     * 指定されたパスからクラスをロードします
     * 
     * @param pluginPath プラグインのパス（JARファイル）
     * @return ロードされたクラス
     * @throws ClassNotFoundException クラスが見つからない場合
     */
    public Class<?> loadClass(Path pluginPath) throws ClassNotFoundException {
        try {
            // パスごとに独立したクラスローダーを作成
            ClassLoader loader = loaders.computeIfAbsent(pluginPath, path -> {
                try {
                    URL url = path.toUri().toURL();
                    return new URLClassLoader(new URL[]{url}, getClass().getClassLoader());
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create class loader", e);
                }
            });
            
            // プラグインのメインクラスを検出（簡易実装）
            // 実際にはMANIFEST.MFやサービスローダーを使用
            String mainClass = detectMainClass(pluginPath);
            return loader.loadClass(mainClass);
            
        } catch (Exception e) {
            throw new ClassNotFoundException("Failed to load plugin class", e);
        }
    }
    
    /**
     * プラグインのメインクラスを検出します
     * 
     * @param pluginPath プラグインのパス
     * @return メインクラス名
     */
    private String detectMainClass(Path pluginPath) {
        // 簡易実装：実際にはJARファイルを読み込んでMANIFEST.MFを解析
        String fileName = pluginPath.getFileName().toString();
        if (fileName.endsWith(".jar")) {
            fileName = fileName.substring(0, fileName.length() - 4);
        }
        // 命名規則に基づく推測
        return "com.example.plugins." + fileName + ".MainPlugin";
    }
    
    /**
     * 指定されたパスのクラスローダーをアンロードします
     * 
     * @param pluginPath プラグインのパス
     */
    public void unloadClassLoader(Path pluginPath) {
        ClassLoader loader = loaders.remove(pluginPath);
        if (loader instanceof URLClassLoader) {
            try {
                ((URLClassLoader) loader).close();
            } catch (Exception e) {
                // ログに記録
            }
        }
    }
    
    /**
     * すべてのクラスローダーをクリアします
     */
    public void clear() {
        loaders.values().forEach(loader -> {
            if (loader instanceof URLClassLoader) {
                try {
                    ((URLClassLoader) loader).close();
                } catch (Exception e) {
                    // ログに記録
                }
            }
        });
        loaders.clear();
    }
}