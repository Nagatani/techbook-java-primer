package com.example.typeerasure;

/**
 * ブリッジメソッドの仕組みを示すサンプルクラス
 * ジェネリックインターフェイスの実装でコンパイラが自動生成するブリッジメソッドを学習
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
     * コンパイラはこのメソッドに加えて、compareTo(Object)のブリッジメソッドを生成する
     */
    @Override
    public int compareTo(BridgeMethodExample<T> other) {
        return this.identifier.compareTo(other.identifier);
    }
    
    // 注意: 以下のブリッジメソッドは実際にはコンパイラが自動生成する
    // 手動で書く必要はないが、理解のために示している
    /*
    // ブリッジメソッド（コンパイラが自動生成）
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
        
        System.out.println("Comparison result: " + example1.compareTo(example2));
        
        // リフレクションでブリッジメソッドの存在を確認
        var methods = BridgeMethodExample.class.getDeclaredMethods();
        System.out.println("\nMethods in BridgeMethodExample:");
        for (var method : methods) {
            System.out.println("- " + method.getName() + 
                " (parameters: " + java.util.Arrays.toString(method.getParameterTypes()) + 
                ", bridge: " + method.isBridge() + ")");
        }
    }
}

/**
 * より複雑なブリッジメソッドの例：共変戻り値型
 */
abstract class AbstractProcessor<T> {
    public abstract T process(String input);
    
    public void execute(String input) {
        T result = process(input);
        System.out.println("Processed: " + result);
    }
}

class StringProcessor extends AbstractProcessor<String> {
    @Override
    public String process(String input) {
        return input.toUpperCase();
    }
    
    // コンパイラが生成するブリッジメソッド:
    // public Object process(String input) {
    //     return process(input); // String版を呼び出し
    // }
}

class IntegerProcessor extends AbstractProcessor<Integer> {
    @Override
    public Integer process(String input) {
        return input.length();
    }
    
    // コンパイラが生成するブリッジメソッド:
    // public Object process(String input) {
    //     return process(input); // Integer版を呼び出し
    // }
}