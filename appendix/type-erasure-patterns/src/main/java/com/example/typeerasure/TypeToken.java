package com.example.typeerasure;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 型トークンパターン：型消去を回避して実行時に型情報を取得
 * Neal GafterのSuper Type Tokenパターンの実装
 */
public abstract class TypeToken<T> {
    private final Type type;
    
    protected TypeToken() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof ParameterizedType) {
            this.type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
        } else {
            throw new RuntimeException("Missing type parameter.");
        }
    }
    
    public Type getType() {
        return type;
    }
    
    /**
     * 型の文字列表現を取得
     */
    public String getTypeName() {
        return type.getTypeName();
    }
    
    /**
     * Raw型を取得
     */
    @SuppressWarnings("unchecked")
    public Class<T> getRawType() {
        if (type instanceof ParameterizedType) {
            return (Class<T>) ((ParameterizedType) type).getRawType();
        } else if (type instanceof Class) {
            return (Class<T>) type;
        } else {
            throw new IllegalStateException("Cannot determine raw type for " + type);
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof TypeToken)) return false;
        TypeToken<?> that = (TypeToken<?>) obj;
        return type.equals(that.type);
    }
    
    @Override
    public int hashCode() {
        return type.hashCode();
    }
    
    @Override
    public String toString() {
        return "TypeToken{" + type + "}";
    }
    
    /**
     * 型トークンの使用例を示すデモンストレーション
     */
    public static void demonstrateTypeToken() {
        // 匿名クラスを使用して型情報を保持
        TypeToken<String> stringToken = new TypeToken<String>() {};
        TypeToken<java.util.List<String>> listStringToken = new TypeToken<java.util.List<String>>() {};
        TypeToken<java.util.Map<String, Integer>> mapToken = new TypeToken<java.util.Map<String, Integer>>() {};
        
        System.out.println("String token: " + stringToken);
        System.out.println("List<String> token: " + listStringToken);
        System.out.println("Map<String, Integer> token: " + mapToken);
        
        // Raw型の取得
        System.out.println("Raw type of List<String>: " + listStringToken.getRawType());
        
        // 型の比較
        TypeToken<String> anotherStringToken = new TypeToken<String>() {};
        System.out.println("String tokens equal: " + stringToken.equals(anotherStringToken));
    }
}

/**
 * TypeReferenceパターン：Jacksonライブラリで使用される類似のパターン
 */
abstract class TypeReference<T> {
    private final Type type;
    
    protected TypeReference() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof ParameterizedType) {
            this.type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
        } else {
            throw new RuntimeException("Missing type parameter.");
        }
    }
    
    public Type getType() {
        return type;
    }
    
    /**
     * TypeReferenceの使用例
     */
    public static void demonstrateTypeReference() {
        // Jackson風の型参照
        TypeReference<java.util.List<String>> typeRef = new TypeReference<java.util.List<String>>() {};
        Type type = typeRef.getType();
        
        System.out.println("TypeReference result: " + type);
        System.out.println("Type name: " + type.getTypeName());
    }
}