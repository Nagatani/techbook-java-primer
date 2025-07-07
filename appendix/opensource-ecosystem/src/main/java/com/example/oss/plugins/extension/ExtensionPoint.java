package com.example.oss.plugins.extension;

import java.util.List;

/**
 * 拡張ポイントインターフェース
 * 
 * <p>プラグインが機能を拡張できるポイントを定義します。
 * 型安全な拡張の登録と取得を提供します。
 * 
 * @param <T> 拡張の型
 * @since 1.0.0
 */
public interface ExtensionPoint<T> {
    
    /**
     * 拡張ポイントの型を取得します
     * 
     * @return 拡張の型クラス
     */
    Class<T> getType();
    
    /**
     * 拡張ポイントの名前を取得します
     * 
     * @return 拡張ポイント名
     */
    String getName();
    
    /**
     * 登録されている拡張のリストを取得します
     * 
     * @return 拡張のリスト（変更不可）
     */
    List<T> getExtensions();
    
    /**
     * 新しい拡張を登録します
     * 
     * @param extension 登録する拡張
     * @throws IllegalArgumentException 拡張が型に一致しない場合
     */
    void registerExtension(T extension);
    
    /**
     * 拡張を削除します
     * 
     * @param extension 削除する拡張
     * @return 削除された場合true
     */
    void unregisterExtension(T extension);
    
    /**
     * すべての拡張をクリアします
     */
    void clear();
    
    /**
     * 拡張が登録されているかチェックします
     * 
     * @return 拡張が1つ以上登録されている場合true
     */
    default boolean hasExtensions() {
        return !getExtensions().isEmpty();
    }
    
    /**
     * 登録されている拡張の数を取得します
     * 
     * @return 拡張の数
     */
    default int size() {
        return getExtensions().size();
    }
}