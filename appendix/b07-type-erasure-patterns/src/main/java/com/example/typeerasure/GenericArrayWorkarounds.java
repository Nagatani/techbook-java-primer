package com.example.typeerasure;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * ジェネリック配列の制限とその回避策を示すクラス
 * 型消去によりジェネリック配列の作成が制限される問題を解決する方法
 */
public class GenericArrayWorkarounds<T> {
    
    /**
     * 問題のあるアプローチ：コンパイルエラーとなる
     */
    // private T[] array = new T[10]; // コンパイルエラー
    
    /**
     * 回避策1: Object配列を使用（型安全性が失われる）
     */
    @SuppressWarnings("unchecked")
    private T[] unsafeArray = (T[]) new Object[10];
    
    /**
     * 回避策2: Class<T>を使用した型安全な配列作成
     */
    private T[] safeArray;
    private Class<T> elementType;
    
    public GenericArrayWorkarounds(Class<T> elementType, int size) {
        this.elementType = elementType;
        this.safeArray = createArray(size);
    }
    
    /**
     * リフレクションを使用した型安全な配列作成
     */
    @SuppressWarnings("unchecked")
    private T[] createArray(int size) {
        return (T[]) Array.newInstance(elementType, size);
    }
    
    /**
     * 配列への要素設定
     */
    public void setElement(int index, T element) {
        if (index >= 0 && index < safeArray.length) {
            safeArray[index] = element;
        }
    }
    
    /**
     * 配列からの要素取得
     */
    public T getElement(int index) {
        if (index >= 0 && index < safeArray.length) {
            return safeArray[index];
        }
        return null;
    }
    
    /**
     * 配列サイズの取得
     */
    public int size() {
        return safeArray.length;
    }
    
    /**
     * 配列の内容を表示
     */
    public void printArray() {
        System.out.println("Array contents:");
        for (int i = 0; i < safeArray.length; i++) {
            System.out.println("  [" + i + "] = " + safeArray[i]);
        }
    }
    
    /**
     * 回避策3: Listを使用（推奨アプローチ）
     */
    public static class GenericListContainer<T> {
        private List<T> list = new ArrayList<>();
        
        public void add(T element) {
            list.add(element);
        }
        
        public T get(int index) {
            return list.get(index);
        }
        
        public int size() {
            return list.size();
        }
        
        public void printList() {
            System.out.println("List contents:");
            for (int i = 0; i < list.size(); i++) {
                System.out.println("  [" + i + "] = " + list.get(i));
            }
        }
    }
    
    /**
     * 回避策4: ファクトリメソッドを使用した配列作成
     */
    public static <T> T[] createTypedArray(Class<T> type, int size) {
        @SuppressWarnings("unchecked")
        T[] array = (T[]) Array.newInstance(type, size);
        return array;
    }
    
    /**
     * 可変長引数を使用した配列作成
     */
    @SafeVarargs
    public static <T> T[] createArrayFromElements(T... elements) {
        return elements;
    }
    
    /**
     * デモンストレーション
     */
    public static void demonstrateGenericArrayWorkarounds() {
        System.out.println("=== Generic Array Workarounds Demo ===");
        
        // 回避策2: Class<T>を使用
        GenericArrayWorkarounds<String> stringArray = new GenericArrayWorkarounds<>(String.class, 5);
        stringArray.setElement(0, "Hello");
        stringArray.setElement(1, "World");
        stringArray.setElement(2, "Java");
        
        System.out.println("\nString array (using Class<T>):");
        stringArray.printArray();
        
        // 回避策3: List使用
        GenericListContainer<Integer> intList = new GenericListContainer<>();
        intList.add(10);
        intList.add(20);
        intList.add(30);
        
        System.out.println("\nInteger list (recommended approach):");
        intList.printList();
        
        // 回避策4: ファクトリメソッド
        String[] stringArrayFromFactory = createTypedArray(String.class, 3);
        stringArrayFromFactory[0] = "Factory";
        stringArrayFromFactory[1] = "Created";
        stringArrayFromFactory[2] = "Array";
        
        System.out.println("\nFactory created array:");
        for (int i = 0; i < stringArrayFromFactory.length; i++) {
            System.out.println("  [" + i + "] = " + stringArrayFromFactory[i]);
        }
        
        // 可変長引数を使用
        Integer[] intArray = createArrayFromElements(1, 2, 3, 4, 5);
        System.out.println("\nVarargs created array:");
        for (int i = 0; i < intArray.length; i++) {
            System.out.println("  [" + i + "] = " + intArray[i]);
        }
    }
}