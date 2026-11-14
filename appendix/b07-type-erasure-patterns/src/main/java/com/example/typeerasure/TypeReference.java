package com.example.typeerasure;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * TypeReferenceパターン：Jacksonライブラリで使用される類似のパターン
 */
public abstract class TypeReference<T> {
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
        
        IO.println("TypeReference result: " + type);
        IO.println("Type name: " + type.getTypeName());
    }
}
