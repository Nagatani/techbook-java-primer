package com.example.oss.plugins;

import com.example.oss.plugins.extension.ExtensionPoint;
import com.example.oss.plugins.extension.Extension;
import com.example.oss.plugins.extension.DefaultExtensionPoint;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

/**
 * プラグインシステムのマネージャー
 * 
 * <p>プラグインのロード、アンロード、拡張ポイントの管理を行います。
 * スレッドセーフな実装により、並行アクセスにも対応しています。
 * 
 * <h2>使用例</h2>
 * <pre>{@code
 * PluginManager manager = new PluginManager();
 * manager.loadPlugin(Paths.get("plugins/my-plugin.jar"));
 * 
 * ExtensionPoint<DataTransformer> transformers = 
 *     manager.getExtensionPoint("dataTransformers", DataTransformer.class);
 * }</pre>
 * 
 * @since 1.0.0
 */
public class PluginManager {
    private final Map<String, Plugin> plugins = new ConcurrentHashMap<>();
    private final Map<String, ExtensionPoint<?>> extensionPoints = new ConcurrentHashMap<>();
    private final PluginClassLoader classLoader;
    
    /**
     * PluginManagerを構築します
     */
    public PluginManager() {
        this.classLoader = new PluginClassLoader();
        initializeBuiltInExtensionPoints();
    }
    
    /**
     * ビルトインの拡張ポイントを初期化します
     */
    private void initializeBuiltInExtensionPoints() {
        registerExtensionPoint("dataTransformers", DataTransformer.class);
        registerExtensionPoint("validators", Validator.class);
        registerExtensionPoint("serializers", Serializer.class);
    }
    
    /**
     * 新しい拡張ポイントを登録します
     * 
     * @param <T> 拡張ポイントの型
     * @param name 拡張ポイント名
     * @param type 拡張ポイントの型クラス
     * @return 登録された拡張ポイント
     * @throws IllegalArgumentException 同名の拡張ポイントが既に存在する場合
     */
    @SuppressWarnings("unchecked")
    public <T> ExtensionPoint<T> registerExtensionPoint(String name, Class<T> type) {
        Objects.requireNonNull(name, "Extension point name cannot be null");
        Objects.requireNonNull(type, "Extension point type cannot be null");
        
        if (extensionPoints.containsKey(name)) {
            throw new IllegalArgumentException("Extension point already exists: " + name);
        }
        
        ExtensionPoint<T> extensionPoint = new DefaultExtensionPoint<>(name, type);
        extensionPoints.put(name, extensionPoint);
        return extensionPoint;
    }
    
    /**
     * 拡張ポイントを取得します
     * 
     * @param <T> 拡張ポイントの型
     * @param name 拡張ポイント名
     * @param type 拡張ポイントの型クラス
     * @return 拡張ポイント（存在しない場合null）
     */
    @SuppressWarnings("unchecked")
    public <T> ExtensionPoint<T> getExtensionPoint(String name, Class<T> type) {
        ExtensionPoint<?> extensionPoint = extensionPoints.get(name);
        if (extensionPoint != null && extensionPoint.getType().equals(type)) {
            return (ExtensionPoint<T>) extensionPoint;
        }
        return null;
    }
    
    /**
     * プラグインをロードします
     * 
     * @param pluginPath プラグインのパス
     * @throws PluginException プラグインのロードに失敗した場合
     */
    public void loadPlugin(Path pluginPath) throws PluginException {
        Objects.requireNonNull(pluginPath, "Plugin path cannot be null");
        
        try {
            Class<?> pluginClass = classLoader.loadClass(pluginPath);
            if (!Plugin.class.isAssignableFrom(pluginClass)) {
                throw new PluginException("Class does not implement Plugin interface");
            }
            
            Plugin plugin = (Plugin) pluginClass.getDeclaredConstructor().newInstance();
            
            // 既存のプラグインをチェック
            if (plugins.containsKey(plugin.getName())) {
                throw new PluginException("Plugin already loaded: " + plugin.getName());
            }
            
            plugins.put(plugin.getName(), plugin);
            
            // プラグインが提供する拡張を自動登録
            registerPluginExtensions(plugin);
            
            // プラグインを初期化
            PluginContext context = new DefaultPluginContext(this);
            plugin.initialize(context);
            
        } catch (Exception e) {
            throw new PluginException("Failed to load plugin: " + pluginPath, e);
        }
    }
    
    /**
     * プラグインが提供する拡張を登録します
     */
    private void registerPluginExtensions(Plugin plugin) {
        Class<?> pluginClass = plugin.getClass();
        
        // アノテーションベースの拡張登録
        for (Field field : pluginClass.getDeclaredFields()) {
            Extension annotation = field.getAnnotation(Extension.class);
            if (annotation != null) {
                try {
                    field.setAccessible(true);
                    Object extension = field.get(plugin);
                    registerExtension(annotation.value(), extension);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to register extension", e);
                }
            }
        }
    }
    
    /**
     * 拡張を登録します
     */
    @SuppressWarnings("unchecked")
    private <T> void registerExtension(String extensionPointName, Object extension) {
        ExtensionPoint<Object> extensionPoint = 
            (ExtensionPoint<Object>) extensionPoints.get(extensionPointName);
        if (extensionPoint != null) {
            extensionPoint.registerExtension(extension);
        }
    }
    
    /**
     * プラグインをアンロードします
     * 
     * @param pluginName プラグイン名
     */
    public void unloadPlugin(String pluginName) {
        Plugin plugin = plugins.remove(pluginName);
        if (plugin != null) {
            plugin.shutdown();
            unregisterPluginExtensions(plugin);
        }
    }
    
    /**
     * プラグインが提供していた拡張を削除します
     */
    private void unregisterPluginExtensions(Plugin plugin) {
        Class<?> pluginClass = plugin.getClass();
        for (Field field : pluginClass.getDeclaredFields()) {
            Extension annotation = field.getAnnotation(Extension.class);
            if (annotation != null) {
                try {
                    field.setAccessible(true);
                    Object extension = field.get(plugin);
                    unregisterExtension(annotation.value(), extension);
                } catch (IllegalAccessException e) {
                    // ログに記録して継続
                }
            }
        }
    }
    
    /**
     * 拡張を削除します
     */
    @SuppressWarnings("unchecked")
    private <T> void unregisterExtension(String extensionPointName, Object extension) {
        ExtensionPoint<Object> extensionPoint = 
            (ExtensionPoint<Object>) extensionPoints.get(extensionPointName);
        if (extensionPoint != null) {
            extensionPoint.unregisterExtension(extension);
        }
    }
    
    /**
     * ロードされているプラグインのリストを取得します
     * 
     * @return プラグインのリスト
     */
    public List<Plugin> getPlugins() {
        return new ArrayList<>(plugins.values());
    }
    
    /**
     * 特定のプラグインを取得します
     * 
     * @param name プラグイン名
     * @return プラグイン（存在しない場合null）
     */
    public Plugin getPlugin(String name) {
        return plugins.get(name);
    }
    
    /**
     * すべてのプラグインをシャットダウンします
     */
    public void shutdown() {
        plugins.values().forEach(Plugin::shutdown);
        plugins.clear();
    }
    
    // インターフェース定義
    
    /**
     * データ変換プラグインインターフェース
     */
    public interface DataTransformer {
        String getSupportedFormat();
        boolean canTransform(String fromFormat, String toFormat);
        Object transform(Object input, String fromFormat, String toFormat);
    }
    
    /**
     * バリデータープラグインインターフェース
     */
    public interface Validator {
        boolean canValidate(Class<?> type);
        boolean validate(Object value);
        String getErrorMessage();
    }
    
    /**
     * シリアライザープラグインインターフェース
     */
    public interface Serializer {
        String getFormat();
        byte[] serialize(Object object);
        <T> T deserialize(byte[] data, Class<T> type);
    }
}