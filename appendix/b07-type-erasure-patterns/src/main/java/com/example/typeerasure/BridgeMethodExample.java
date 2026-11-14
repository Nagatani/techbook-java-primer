package com.example.typeerasure;

/**
 * ブリッジメソッドの仕組みを示すサンプルクラス
 * ジェネリックインターフェイスの実装でコンパイラーが自動生成するブリッジメソッドを学習
 */
public class BridgeMethodExample<T> implements Comparable<BridgeMethodExample<T>> {
    private T value;
    private String identifier;
    
    public BridgeMethodExample(T value, String identifier) {
        this.value = value;
        this.identifier = identifier;
    }
    
    /**
     * ジェネリック型での比較メソッド
     * コンパイラーはこのメソッドに加えて、compareTo(Object)のブリッジメソッドを生成する
     */
    @Override
    public int compareTo(BridgeMethodExample<T> other) {
        return this.identifier.compareTo(other.identifier);
    }
    
    // 注意: 以下のブリッジメソッドは実際にはコンパイラーが自動生成する
    // 手動で書く必要はないが、理解のために示している
    /*
    // ブリッジメソッド（コンパイラーが自動生成）
    public int compareTo(Object o) {
        return compareTo((BridgeMethodExample<T>) o);
    }
    */
    
    public T getValue() {
        return value;
    }
    
    public String getIdentifier() {
        return identifier;
    }
    
    @Override
    public String toString() {
        return "BridgeMethodExample{value=" + value + ", identifier='" + identifier + "'}";
    }
    
    /**
     * ブリッジメソッドの存在を確認するデモンストレーション
     */
    public static void demonstrateBridgeMethods() {
        BridgeMethodExample<String> example1 = new BridgeMethodExample<>("Hello", "A");
        BridgeMethodExample<String> example2 = new BridgeMethodExample<>("World", "B");
        
        IO.println("Comparison result: " + example1.compareTo(example2));
        
        // リフレクションでブリッジメソッドの存在を確認
        printDeclaredMethods(BridgeMethodExample.class);

        // 戻り値の型が変わる場合のブリッジメソッド
        new StringProcessor().execute("hello");
        printDeclaredMethods(StringProcessor.class);
    }

    /**
     * クラスで宣言されたメソッドを、ブリッジかどうかとともに表示する
     * getDeclaredMethods()の順序は決まっていないので、表示用に並べ替える
     */
    private static void printDeclaredMethods(Class<?> clazz) {
        var methods = clazz.getDeclaredMethods();
        java.util.Arrays.sort(methods, java.util.Comparator
            .comparing(java.lang.reflect.Method::getName)
            .thenComparing(m -> m.getReturnType().getName())
            .thenComparing(m -> java.util.Arrays.toString(m.getParameterTypes())));
        IO.println("\nMethods in " + clazz.getSimpleName() + ":");
        for (var method : methods) {
            if (method.isSynthetic() && !method.isBridge()) {
                continue; // ラムダ式のためにコンパイラーが作るメソッドは表示しない
            }
            IO.println("- " + method.getReturnType().getSimpleName() + " " + method.getName() +
                " (parameters: " + java.util.Arrays.toString(method.getParameterTypes()) +
                ", bridge: " + method.isBridge() + ")");
        }
    }

    public static void main(String[] args) {
        demonstrateBridgeMethods();
    }
}

/**
 * より複雑なブリッジメソッドの例：共変戻り値型
 */
abstract class AbstractProcessor<T> {
    public abstract T process(String input);
    
    public void execute(String input) {
        T result = process(input);
        IO.println("Processed: " + result);
    }
}

class StringProcessor extends AbstractProcessor<String> {
    @Override
    public String process(String input) {
        return input.toUpperCase();
    }
    
    // コンパイラーが生成するブリッジメソッド:
    // public Object process(String input) {
    //     return process(input); // String版を呼び出し
    // }
}

class IntegerProcessor extends AbstractProcessor<Integer> {
    @Override
    public Integer process(String input) {
        return input.length();
    }
    
    // コンパイラーが生成するブリッジメソッド:
    // public Object process(String input) {
    //     return process(input); // Integer版を呼び出し
    // }
}