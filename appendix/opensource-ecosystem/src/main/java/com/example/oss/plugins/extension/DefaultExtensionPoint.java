package com.example.oss.plugins.extension;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Objects;
import java.util.Collections;

/**
 * ExtensionPointのデフォルト実装
 * 
 * <p>スレッドセーフな拡張ポイントの実装を提供します。
 * 
 * @param <T> 拡張の型
 * @since 1.0.0
 */
public class DefaultExtensionPoint<T> implements ExtensionPoint<T> {
    private final String name;
    private final Class<T> type;
    private final List<T> extensions = new CopyOnWriteArrayList<>();
    
    /**
     * 拡張ポイントを構築します
     * 
     * @param name 拡張ポイント名
     * @param type 拡張の型クラス
     * @throws NullPointerException nameまたはtypeがnullの場合
     */
    public DefaultExtensionPoint(String name, Class<T> type) {
        this.name = Objects.requireNonNull(name, "Extension point name cannot be null");
        this.type = Objects.requireNonNull(type, "Extension point type cannot be null");
    }
    
    @Override
    public Class<T> getType() {
        return type;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public List<T> getExtensions() {
        return Collections.unmodifiableList(extensions);
    }
    
    @Override
    public void registerExtension(T extension) {
        Objects.requireNonNull(extension, "Extension cannot be null");
        
        if (!type.isInstance(extension)) {
            throw new IllegalArgumentException(
                "Extension must be instance of " + type.getName() + 
                " but was " + extension.getClass().getName()
            );
        }
        
        extensions.add(extension);
    }
    
    @Override
    public void unregisterExtension(T extension) {
        extensions.remove(extension);
    }
    
    @Override
    public void clear() {
        extensions.clear();
    }
    
    @Override
    public String toString() {
        return String.format("ExtensionPoint{name='%s', type=%s, extensions=%d}",
            name, type.getSimpleName(), extensions.size());
    }
}