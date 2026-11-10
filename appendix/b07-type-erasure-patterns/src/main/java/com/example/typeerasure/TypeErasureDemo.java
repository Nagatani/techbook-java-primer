package com.example.typeerasure;

/**
 * 型消去パターンのデモンストレーション
 * 各クラスの使用例を統合的に実行
 */
public class TypeErasureDemo {
    
    public static void main(String[] args) {
        IO.println("====================================");
        IO.println("Java Type Erasure Patterns Demo");
        IO.println("====================================\n");
        
        // 1. 基本的な型消去の動作
        IO.println("1. Basic Type Erasure Behavior");
        IO.println("------------------------------");
        GenericBox.demonstrateTypeErasure();
        IO.println();
        NumberBox.demonstrateBoundedTypeErasure();
        IO.println("\n");
        
        // 2. ブリッジメソッドの確認
        IO.println("2. Bridge Methods");
        IO.println("-----------------");
        BridgeMethodExample.demonstrateBridgeMethods();
        IO.println("\n");
        
        // 3. 型トークンパターン
        IO.println("3. Type Token Pattern");
        IO.println("---------------------");
        TypeToken.demonstrateTypeToken();
        IO.println();
        TypeReference.demonstrateTypeReference();
        IO.println("\n");
        
        // 4. リフレクションによる型情報取得
        IO.println("4. Reflection Type Analysis");
        IO.println("---------------------------");
        try {
            ReflectionTypeInfo.analyzeFieldTypes();
            ReflectionTypeInfo.analyzeMethodTypes();
        } catch (Exception e) {
            System.err.println("Error in reflection analysis: " + e.getMessage());
        }
        IO.println("\n");
        
        // 5. ジェネリック配列の回避策
        IO.println("5. Generic Array Workarounds");
        IO.println("----------------------------");
        GenericArrayWorkarounds.demonstrateGenericArrayWorkarounds();
        IO.println("\n");
        
        // 6. 型消去による制限の実例
        IO.println("6. Type Erasure Limitations");
        IO.println("---------------------------");
        demonstrateTypErasureLimitations();
        
        IO.println("\n====================================");
        IO.println("Demo completed successfully!");
        IO.println("====================================");
    }
    
    /**
     * 型消去による制限を実例で示す
     */
    private static void demonstrateTypErasureLimitations() {
        // インスタンス生成時の型情報
        GenericService<String> stringService = new GenericService<>();
        GenericService<Integer> intService = new GenericService<>();
        
        IO.println("Service classes are same at runtime: " + 
            (stringService.getClass() == intService.getClass()));
        
        // 実行時型チェックの不可能性
        java.util.List<String> stringList = java.util.Arrays.asList("a", "b", "c");
        java.util.List<Integer> intList = java.util.Arrays.asList(1, 2, 3);
        
        IO.println("List classes are same at runtime: " + 
            (stringList.getClass() == intList.getClass()));
        
        // Raw型での警告（unchecked warning）を回避するための設計
        IO.println("Raw type compatibility preserved for legacy code");
        
        @SuppressWarnings("rawtypes")
        java.util.List rawList = stringList; // 後方互換性
        
        @SuppressWarnings("unchecked")
        java.util.List<Object> objectList = rawList; // 型安全性が失われる
        
        IO.println("Raw list size: " + rawList.size());
    }
}