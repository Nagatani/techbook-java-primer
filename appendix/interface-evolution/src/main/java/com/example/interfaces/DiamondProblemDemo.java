package com.example.interfaces;

/**
 * ダイヤモンド問題と多重継承の解決方法を示すデモ
 * インターフェイスの継承優先順位規則を学習
 */
public class DiamondProblemDemo {
    
    /**
     * 基底インターフェイス
     */
    interface A {
        default void method() {
            System.out.println("A.method()");
        }
        
        default void commonMethod() {
            System.out.println("A.commonMethod()");
        }
    }
    
    /**
     * Aを継承するインターフェイス
     */
    interface B extends A {
        @Override
        default void method() {
            System.out.println("B.method()");
        }
        
        default void methodB() {
            System.out.println("B.methodB()");
        }
    }
    
    /**
     * Aを継承するインターフェイス
     */
    interface C extends A {
        @Override
        default void method() {
            System.out.println("C.method()");
        }
        
        default void methodC() {
            System.out.println("C.methodC()");
        }
    }
    
    /**
     * ダイヤモンド問題の解決例
     */
    static class DiamondResolver implements B, C {
        
        // コンパイルエラーを回避するため、明示的にオーバーライド
        @Override
        public void method() {
            System.out.println("DiamondResolver.method() - resolving conflict");
            
            // 特定のインターフェイスのメソッドを呼び出す
            System.out.print("Calling B.super.method(): ");
            B.super.method();
            
            System.out.print("Calling C.super.method(): ");
            C.super.method();
            
            // 独自の処理も追加可能
            System.out.println("Custom logic in DiamondResolver");
        }
    }
    
    /**
     * 継承の優先順位規則のデモ
     */
    
    // 規則1：クラスが常に優先
    static class BaseClass {
        public void priorityMethod() {
            System.out.println("BaseClass.priorityMethod()");
        }
        
        public void classMethod() {
            System.out.println("BaseClass.classMethod()");
        }
    }
    
    interface BaseInterface {
        default void priorityMethod() {
            System.out.println("BaseInterface.priorityMethod()");
        }
        
        default void interfaceMethod() {
            System.out.println("BaseInterface.interfaceMethod()");
        }
    }
    
    static class ClassVsInterface extends BaseClass implements BaseInterface {
        // BaseClassのpriorityMethod()が自動的に使われる
        // オーバーライドも可能
        @Override
        public void priorityMethod() {
            System.out.println("ClassVsInterface.priorityMethod() - overridden");
            super.priorityMethod(); // クラスのメソッドを呼び出し
        }
    }
    
    // 規則2：より具体的なインターフェイスが優先
    interface Parent {
        default void specificityMethod() {
            System.out.println("Parent.specificityMethod()");
        }
    }
    
    interface Child extends Parent {
        @Override
        default void specificityMethod() {
            System.out.println("Child.specificityMethod()");
        }
    }
    
    static class SpecificityDemo implements Parent, Child {
        // ChildのspecificityMethod()が自動的に使われる
        public void demonstrateSpecificity() {
            specificityMethod(); // Child版が呼び出される
            
            System.out.println("Note: Parent method is overridden by Child");
        }
    }
    
    /**
     * 複雑なダイヤモンド構造の例
     */
    interface ReadableResource {
        default String read() {
            return "Reading from " + getClass().getSimpleName();
        }
        
        default boolean isReadable() {
            return true;
        }
    }
    
    interface WritableResource {
        default void write(String content) {
            System.out.println("Writing to " + getClass().getSimpleName() + ": " + content);
        }
        
        default boolean isWritable() {
            return true;
        }
    }
    
    interface ReadWriteResource extends ReadableResource, WritableResource {
        default void copy() {
            if (isReadable() && isWritable()) {
                String content = read();
                write("Copy of: " + content);
            }
        }
    }
    
    interface CacheableResource {
        default String read() {
            return "Cached: " + getClass().getSimpleName();
        }
        
        default void clearCache() {
            System.out.println("Clearing cache for " + getClass().getSimpleName());
        }
    }
    
    /**
     * 複雑な多重継承のケース
     */
    static class ComplexResource implements ReadWriteResource, CacheableResource {
        
        @Override
        public String read() {
            System.out.println("ComplexResource.read() - resolving multiple inheritance");
            
            // どちらのread()を使うかを選択
            String normalRead = "Reading from ComplexResource";
            String cachedRead = "Cached: ComplexResource";
            
            System.out.println("Normal read: " + normalRead);
            System.out.println("Cached read: " + cachedRead);
            
            return "ComplexResource: " + normalRead + " | " + cachedRead;
        }
        
        @Override
        public void write(String content) {
            System.out.println("ComplexResource.write() - with cache invalidation");
            System.out.println("Writing to ComplexResource: " + content);
            clearCache(); // 書き込み時にキャッシュをクリア
        }
    }
    
    /**
     * デモンストレーション用のメインメソッド
     */
    public static void main(String[] args) {
        System.out.println("=== Diamond Problem Resolution Demo ===");
        
        // ダイヤモンド問題の解決
        System.out.println("\n--- Diamond Problem Resolution ---");
        DiamondResolver resolver = new DiamondResolver();
        resolver.method();
        resolver.commonMethod(); // 問題なく継承される
        resolver.methodB();
        resolver.methodC();
        
        // 継承優先順位の規則1：クラス優先
        System.out.println("\n--- Rule 1: Class Takes Priority ---");
        ClassVsInterface classVsInterface = new ClassVsInterface();
        classVsInterface.priorityMethod();
        classVsInterface.classMethod();
        classVsInterface.interfaceMethod();
        
        // 継承優先順位の規則2：より具体的なインターフェイス優先
        System.out.println("\n--- Rule 2: More Specific Interface Takes Priority ---");
        SpecificityDemo specificityDemo = new SpecificityDemo();
        specificityDemo.demonstrateSpecificity();
        
        // 複雑な多重継承
        System.out.println("\n--- Complex Multiple Inheritance ---");
        ComplexResource complexResource = new ComplexResource();
        String content = complexResource.read();
        System.out.println("Final result: " + content);
        
        complexResource.write("New content");
        complexResource.copy();
        
        // 各機能の確認
        System.out.println("\nResource capabilities:");
        System.out.println("Readable: " + complexResource.isReadable());
        System.out.println("Writable: " + complexResource.isWritable());
    }
}