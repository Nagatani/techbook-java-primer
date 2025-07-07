package com.example.typeerasure;

/**
 * 型消去パターンのデモンストレーション
 * 各クラスの使用例を統合的に実行
 */
public class TypeErasureDemo {
    
    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("Java Type Erasure Patterns Demo");
        System.out.println("====================================\n");
        
        // 1. 基本的な型消去の動作
        System.out.println("1. Basic Type Erasure Behavior");
        System.out.println("------------------------------");
        GenericBox.demonstrateTypeErasure();
        System.out.println();
        GenericBox.demonstrateBoundedTypeErasure();
        System.out.println("\n");
        
        // 2. ブリッジメソッドの確認
        System.out.println("2. Bridge Methods");
        System.out.println("-----------------");
        BridgeMethodExample.demonstrateBridgeMethods();
        System.out.println("\n");
        
        // 3. 型トークンパターン
        System.out.println("3. Type Token Pattern");
        System.out.println("---------------------");
        TypeToken.demonstrateTypeToken();
        System.out.println();
        TypeReference.demonstrateTypeReference();
        System.out.println("\n");
        
        // 4. リフレクションによる型情報取得
        System.out.println("4. Reflection Type Analysis");
        System.out.println("---------------------------");
        try {
            ReflectionTypeInfo.analyzeFieldTypes();
            ReflectionTypeInfo.analyzeMethodTypes();
        } catch (Exception e) {
            System.err.println("Error in reflection analysis: " + e.getMessage());
        }
        System.out.println("\n");
        
        // 5. ジェネリック配列の回避策
        System.out.println("5. Generic Array Workarounds");
        System.out.println("----------------------------");
        GenericArrayWorkarounds.demonstrateGenericArrayWorkarounds();
        System.out.println("\n");
        
        // 6. 型消去による制限の実例
        System.out.println("6. Type Erasure Limitations");
        System.out.println("---------------------------");
        demonstrateTypErasureLimitations();
        
        System.out.println("\n====================================");
        System.out.println("Demo completed successfully!");
        System.out.println("====================================");
    }
    
    /**
     * 型消去による制限を実例で示す
     */
    private static void demonstrateTypErasureLimitations() {
        // インスタンス生成時の型情報
        GenericService<String> stringService = new GenericService<>();
        GenericService<Integer> intService = new GenericService<>();
        
        System.out.println("Service classes are same at runtime: " + 
            (stringService.getClass() == intService.getClass()));
        
        // 実行時型チェックの不可能性
        java.util.List<String> stringList = java.util.Arrays.asList("a", "b", "c");
        java.util.List<Integer> intList = java.util.Arrays.asList(1, 2, 3);
        
        System.out.println("List classes are same at runtime: " + 
            (stringList.getClass() == intList.getClass()));
        
        // Raw型での警告（unchecked warning）を回避するための設計
        System.out.println("Raw type compatibility preserved for legacy code");
        
        @SuppressWarnings("rawtypes")
        java.util.List rawList = stringList; // 後方互換性
        
        @SuppressWarnings("unchecked")
        java.util.List<Object> objectList = rawList; // 型安全性が失われる
        
        System.out.println("Raw list size: " + rawList.size());
    }
}