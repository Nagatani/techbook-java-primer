package com.example.typeerasure;

import java.util.List;

/**
 * 型消去により実行時型情報が失われることを示すサンプルクラス
 */
public class GenericService<T> {
    
    /**
     * 型消去により、実行時にはList<String>とList<Integer>を区別できない
     * このメソッドはコンパイルエラーとなる例を示している
     */
    public void process(List<T> items) {
        // 以下のコードはコンパイルエラー
        // if (items instanceof List<String>) {
        //     // String固有の処理
        // }
        
        // 型消去後は以下のような処理しかできない
        if (!items.isEmpty()) {
            System.out.println("Processing " + items.size() + " items");
            // T型の詳細な型情報は実行時には利用できない
        }
    }
    
    /**
     * 型消去の影響で、同じraw型になるメソッドオーバーロードは不可能
     */
    public void processStrings(List<String> strings) {
        strings.forEach(str -> System.out.println("String: " + str.toUpperCase()));
    }
    
    // 以下はコンパイルエラー（型消去後に同じシグネチャになる）
    // public void processIntegers(List<Integer> integers) {
    //     integers.forEach(num -> System.out.println("Integer: " + (num * 2)));
    // }
    
    /**
     * 型消去を回避する設計パターン：メソッド名での区別
     */
    public void processIntegerList(List<Integer> integers) {
        integers.forEach(num -> System.out.println("Integer: " + (num * 2)));
    }
}