# 第8章 ジェネリクス

この章では、Javaのジェネリクス（総称型）について学習します。

## 8.1 ジェネリクスの基礎

### 型安全性の向上

```java
import java.util.*;

public class GenericsBasic {
    public static void main(String[] args) {
        // ジェネリクス使用前（Java 5以前）
        List list = new ArrayList();
        list.add("Hello");
        list.add(123);  // 異なる型も追加可能
        String str = (String) list.get(0);  // キャストが必要
        
        // ジェネリクス使用後
        List<String> stringList = new ArrayList<>();
        stringList.add("Hello");
        // stringList.add(123);  // コンパイルエラー！
        String str2 = stringList.get(0);  // キャスト不要
    }
}
```

## 8.2 ジェネリッククラス

### 型パラメータを持つクラス

```java
public class Box<T> {
    private T content;
    
    public void set(T content) {
        this.content = content;
    }
    
    public T get() {
        return content;
    }
    
    public boolean isEmpty() {
        return content == null;
    }
}

public class BoxExample {
    public static void main(String[] args) {
        Box<String> stringBox = new Box<>();
        stringBox.set("Hello");
        String value = stringBox.get();
        
        Box<Integer> intBox = new Box<>();
        intBox.set(42);
        Integer number = intBox.get();
    }
}
```

### 複数の型パラメータ

```java
public class Pair<T, U> {
    private T first;
    private U second;
    
    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }
    
    public T getFirst() { return first; }
    public U getSecond() { return second; }
    
    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}

public class PairExample {
    public static void main(String[] args) {
        Pair<String, Integer> nameAge = new Pair<>("田中太郎", 25);
        Pair<Double, Double> coordinates = new Pair<>(35.6762, 139.6503);
        
        System.out.println("名前と年齢: " + nameAge);
        System.out.println("座標: " + coordinates);
    }
}
```

## 8.3 ジェネリックメソッド

```java
public class GenericMethods {
    
    // ジェネリックメソッド
    public static <T> void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
    
    public static <T extends Comparable<T>> T max(T a, T b) {
        return a.compareTo(b) > 0 ? a : b;
    }
    
    public static void main(String[] args) {
        String[] words = {"apple", "banana", "cherry"};
        swap(words, 0, 2);
        System.out.println(Arrays.toString(words));
        
        String maxString = max("hello", "world");
        Integer maxNumber = max(10, 20);
        
        System.out.println("最大文字列: " + maxString);
        System.out.println("最大数値: " + maxNumber);
    }
}
```

## 8.4 ワイルドカード

```java
import java.util.*;

public class WildcardExample {
    
    // ? extends T（上限境界）- 読み取り専用
    public static double sumNumbers(List<? extends Number> numbers) {
        double sum = 0;
        for (Number num : numbers) {
            sum += num.doubleValue();
        }
        return sum;
    }
    
    // ? super T（下限境界）- 書き込み専用
    public static void addNumbers(List<? super Integer> numbers) {
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);
    }
    
    public static void main(String[] args) {
        List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
        List<Double> doubles = Arrays.asList(1.1, 2.2, 3.3);
        
        System.out.println("整数の合計: " + sumNumbers(integers));
        System.out.println("小数の合計: " + sumNumbers(doubles));
        
        List<Number> numbers = new ArrayList<>();
        addNumbers(numbers);
        System.out.println("追加後: " + numbers);
    }
}
```

## まとめ

ジェネリクスにより、型安全性を保ちながら再利用可能なコードを作成できます。