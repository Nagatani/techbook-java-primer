package com.example.typeerasure;

import java.lang.reflect.*;
import java.util.*;

/**
 * リフレクションを使用した型情報の取得とジェネリクス型の解析
 * フィールド、メソッド、クラスの型情報を実行時に取得する方法を示す
 */
public class ReflectionTypeInfo {
    // 分析対象のフィールド（様々なジェネリクス型）
    private List<String> stringList;
    private Map<String, Integer> scoreMap;
    private Set<Map<String, List<Integer>>> complexStructure;
    private Optional<List<String>> optionalList;
    
    /**
     * フィールドの型情報を分析
     */
    public static void analyzeFieldTypes() throws Exception {
        System.out.println("=== Field Type Analysis ===");
        
        Field[] fields = ReflectionTypeInfo.class.getDeclaredFields();
        for (Field field : fields) {
            System.out.println("\nField: " + field.getName());
            System.out.println("  Declared type: " + field.getType());
            
            Type genericType = field.getGenericType();
            System.out.println("  Generic type: " + genericType);
            
            if (genericType instanceof ParameterizedType) {
                analyzeParameterizedType((ParameterizedType) genericType, 1);
            }
        }
    }
    
    /**
     * ParameterizedTypeの詳細分析
     */
    private static void analyzeParameterizedType(ParameterizedType type, int depth) {
        String indent = "  ".repeat(depth);
        System.out.println(indent + "ParameterizedType analysis:");
        System.out.println(indent + "  Raw type: " + type.getRawType());
        System.out.println(indent + "  Owner type: " + type.getOwnerType());
        
        Type[] typeArgs = type.getActualTypeArguments();
        System.out.println(indent + "  Type arguments (" + typeArgs.length + "):");
        
        for (int i = 0; i < typeArgs.length; i++) {
            Type arg = typeArgs[i];
            System.out.println(indent + "    [" + i + "] " + arg + " (" + arg.getClass().getSimpleName() + ")");
            
            if (arg instanceof ParameterizedType) {
                analyzeParameterizedType((ParameterizedType) arg, depth + 2);
            } else if (arg instanceof WildcardType) {
                analyzeWildcardType((WildcardType) arg, depth + 2);
            } else if (arg instanceof TypeVariable) {
                analyzeTypeVariable((TypeVariable<?>) arg, depth + 2);
            }
        }
    }
    
    /**
     * ワイルドカード型の分析
     */
    private static void analyzeWildcardType(WildcardType wildcardType, int depth) {
        String indent = "  ".repeat(depth);
        System.out.println(indent + "WildcardType analysis:");
        
        Type[] upperBounds = wildcardType.getUpperBounds();
        Type[] lowerBounds = wildcardType.getLowerBounds();
        
        System.out.println(indent + "  Upper bounds: " + Arrays.toString(upperBounds));
        System.out.println(indent + "  Lower bounds: " + Arrays.toString(lowerBounds));
    }
    
    /**
     * 型変数の分析
     */
    private static void analyzeTypeVariable(TypeVariable<?> typeVar, int depth) {
        String indent = "  ".repeat(depth);
        System.out.println(indent + "TypeVariable analysis:");
        System.out.println(indent + "  Name: " + typeVar.getName());
        System.out.println(indent + "  Bounds: " + Arrays.toString(typeVar.getBounds()));
        System.out.println(indent + "  Generic declaration: " + typeVar.getGenericDeclaration());
    }
    
    // 分析対象のメソッド（様々なジェネリクス型）
    public List<String> getStrings() { return null; }
    public void processMap(Map<String, List<Integer>> map) { }
    public <T extends Number> T processNumber(T input, Class<T> type) { return null; }
    public Optional<List<String>> getOptionalList() { return null; }
    
    /**
     * メソッドの型情報を分析
     */
    public static void analyzeMethodTypes() throws Exception {
        System.out.println("\n=== Method Type Analysis ===");
        
        Method[] methods = ReflectionTypeInfo.class.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("get") || method.getName().startsWith("process")) {
                System.out.println("\nMethod: " + method.getName());
                
                // 戻り値の型
                Type returnType = method.getGenericReturnType();
                System.out.println("  Return type: " + returnType);
                if (returnType instanceof ParameterizedType) {
                    analyzeParameterizedType((ParameterizedType) returnType, 1);
                }
                
                // パラメータの型
                Type[] paramTypes = method.getGenericParameterTypes();
                System.out.println("  Parameter types (" + paramTypes.length + "):");
                for (int i = 0; i < paramTypes.length; i++) {
                    System.out.println("    [" + i + "] " + paramTypes[i]);
                    if (paramTypes[i] instanceof ParameterizedType) {
                        analyzeParameterizedType((ParameterizedType) paramTypes[i], 2);
                    }
                }
                
                // 型パラメータ
                TypeVariable<Method>[] typeParams = method.getTypeParameters();
                if (typeParams.length > 0) {
                    System.out.println("  Type parameters:");
                    for (TypeVariable<Method> typeParam : typeParams) {
                        analyzeTypeVariable(typeParam, 1);
                    }
                }
            }
        }
    }
    
    /**
     * デモンストレーション実行
     */
    public static void main(String[] args) {
        try {
            analyzeFieldTypes();
            analyzeMethodTypes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}