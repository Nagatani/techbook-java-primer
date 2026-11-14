package com.example.typeerasure;

/**
 * 型消去のプロセスを示すジェネリッククラス
 * コンパイル時とランタイムでの型の違いを理解するためのサンプル
 */
public class GenericBox<T> {
    private T value;
    
    public void set(T value) {
        this.value = value;
    }
    
    public T get() {
        return value;
    }
    
    /**
     * 型消去により、実行時には型パラメーター情報が失われることを示すメソッド
     */
    public void printTypeInfo() {
        IO.println("Runtime class: " + this.getClass().getName());
        if (value != null) {
            IO.println("Value class: " + value.getClass().getName());
            IO.println("Value: " + value);
        }
    }
    
    /**
     * デモンストレーション用メソッド
     */
    public static void demonstrateTypeErasure() {
        GenericBox<String> stringBox = new GenericBox<>();
        GenericBox<Integer> intBox = new GenericBox<>();
        
        stringBox.set("Hello");
        intBox.set(42);
        
        // 型消去により、実行時には同じクラス
        IO.println("Same class at runtime: " + 
            (stringBox.getClass() == intBox.getClass())); // true
        
        IO.println("\nString box:");
        stringBox.printTypeInfo();
        
        IO.println("\nInteger box:");
        intBox.printTypeInfo();
    }
}

/**
 * 境界付き型パラメーターの型消去を示すクラス
 */
class NumberBox<T extends Number> {
    private T value;
    
    public void set(T value) {
        this.value = value;
    }
    
    public T get() {
        return value;
    }
    
    /**
     * 境界によりNumber型のメソッドが使用可能
     * 型消去後はNumber型として扱われる
     */
    public double getDoubleValue() {
        return value.doubleValue();
    }
    
    public static void demonstrateBoundedTypeErasure() {
        NumberBox<Integer> intBox = new NumberBox<>();
        NumberBox<Double> doubleBox = new NumberBox<>();
        
        intBox.set(100);
        doubleBox.set(3.14);
        
        IO.println("Integer as double: " + intBox.getDoubleValue());
        IO.println("Double as double: " + doubleBox.getDoubleValue());
    }
}