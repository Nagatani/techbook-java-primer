# 第9章 ジェネリクス

## 🎯総合演習プロジェクトへのステップ

前章で学んだコレクションは非常に強力ですが、それを安全に使うためには本章で学ぶ**ジェネリクス（Generics）** が不可欠です。総合演習プロジェクトでは、ジェネリクスを意識することが品質の基盤となります。

- **`ArrayList<Task>` の `<Task>` の意味**: `ArrayList`が、`Task`クラスのインスタンス「だけ」を格納することを保証するのがジェネリクスの役割です。これにより、誤って`String`や`Integer`をリストに追加してしまうといったミスを、コンパイル時点で防ぐことができます。
- **安全な型操作**: リストから要素を取りだす際に、`Task`型であることが保証されているため、面倒で危険なキャスト（型変換）が不要になります。

ジェネリクスを理解することは、コレクションを正しく使いこなし、堅牢で保守性の高いコードを書くための必須条件です。

## 📋 本章の学習目標

### 前提知識
- **第8章の知識**: `List`, `Set`, `Map`などのコレクションの基本的な使い方を理解している。
- **クラスとインターフェイス**: クラスとインターフェイスの基本的な概念を理解している。

### 到達目標

#### 知識理解目標
- ジェネリクスが解決する課題（型安全性、再利用性）を説明できる。
- `List<String>`のようなジェネリクスの基本的な構文を理解する。
- ジェネリッククラスとジェネリックメソッドの概念を理解する。
- 型制約（境界のある型パラメータ）とワイルドカードの基本的な役割を理解する。

#### 技能習得目標
- ジェネリクスを使って、特定の型に限定されたコレクションを作成できる。
- ジェネリクスを利用して、安全にコレクションから要素を取り出し、利用できる。
- 簡単なジェネリッククラスやジェネリックメソッドを定義できる。

---

## 9.1 なぜジェネリクスが必要なのか？

Java 5より前の時代、コレクションは「あらゆるオブジェクト」を格納できる`Object`型の入れ物でした。これは一見便利に思えますが、大きな問題を抱えていました。

```java
// ジェネリクスがなかった時代のコード（現在は非推奨）
import java.util.ArrayList;
import java.util.List;

public class WithoutGenericsExample {
    public static void main(String[] args) {
        List list = new ArrayList();
        list.add("Hello");
        list.add(123); // 文字列も数値も、何でも入ってしまう！

        // 取り出すときに、本来の型を覚えておいて、キャストする必要がある
        String message = (String) list.get(0); // OK

        // もし間違った型で取り出そうとすると...
        // コンパイルは通ってしまうが、実行時に ClassCastException が発生！
        // String wrong = (String) list.get(1); 
    }
}
```
このように、コンパイラは型の誤りをチェックできず、バグが実行時まで発見されない危険な状態でした。

この問題を解決するためにJava 5で導入されたのが**ジェネリクス（Generics）** です。ジェネリクスは、クラスやインターフェイスが「どの型のデータ」を扱うのかを、**クラスを定義または利用する時点**で指定できるようにするしくみです。

`List<String>`のように、クラス名の後に山括弧`< >`で型を指定します。これを**型パラメータ**と呼びます。

```java
import java.util.ArrayList;
import java.util.List;

public class WithGenericsExample {
    public static void main(String[] args) {
        // String型の要素だけを格納できるListを宣言
        List<String> stringList = new ArrayList<>();

        stringList.add("Java");
        // stringList.add(123); // コンパイルエラー！ String型以外は追加できない

        // 取り出すときも、キャストは不要
        String element = stringList.get(0); // 安全！
        System.out.println(element);
    }
}
```

ジェネリクスを使うことで、コンパイラが型の整合性をチェックしてくれるため、意図しない型のデータが混入するのを防ぎ、プログラムの安全性を大幅に向上させることができます。

## 9.2 ジェネリッククラスの作成

ジェネリクスはコレクションだけでなく、自作のクラスにも適用できます。これにより、特定の型に依存しない、再利用性の高いクラスを作成できます。

### 基本的なジェネリッククラス

クラス名の後に`<T>`のような型パラメータを宣言します。`T`は"Type"の頭文字で、慣習的に使われるプレースホルダです。

```java
// Tという型パラメータを持つジェネリッククラス
public class Box<T> {
    private T content;

    public void setContent(T content) {
        this.content = content;
    }

    public T getContent() {
        return this.content;
    }
}

public class BoxExample {
    public static void main(String[] args) {
        // Stringを扱うBox
        Box<String> stringBox = new Box<>();
        stringBox.setContent("Hello Generics");
        String str = stringBox.getContent();
        System.out.println(str);

        // Integerを扱うBox
        Box<Integer> integerBox = new Box<>();
        integerBox.setContent(100);
        int num = integerBox.getContent();
        System.out.println(num);
    }
}
```

### ダイヤモンド演算子

Java 7からは、右辺の型パラメータを省略できる**ダイヤモンド演算子** (`<>`)が導入され、より簡潔に記述できるようになりました。コンパイラが左辺の宣言から型を推論してくれます。

```java
// Java 7以降の推奨される書き方
Map<String, List<Integer>> complexMap = new HashMap<>();
```

## 9.3 ジェネリックメソッド

クラス全体ではなく、特定のメソッドだけをジェネリックにすることも可能です。メソッドの戻り値の型の前に型パラメータを宣言します。

```java
public class GenericMethodExample {
    // Tという型パラメータを持つジェネリックメソッド
    public static <T> void printArray(T[] inputArray) {
        for (T element : inputArray) {
            System.out.printf("%s ", element);
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Integer[] intArray = { 1, 2, 3 };
        String[] stringArray = { "A", "B", "C" };

        System.out.print("Integer配列: ");
        printArray(intArray); // TはIntegerと推論される

        System.out.print("String配列: ");
        printArray(stringArray); // TはStringと推論される
    }
}
```

## 9.4 型制約（境界のある型パラメータ）

ジェネリクスで使える型を、特定のクラスのサブクラスや、特定のインターフェイスを実装したクラスに限定したい場合があります。これを**境界のある型パラメータ**とよい、`extends`キーワードを使います。

`<T extends Number>`と書くと、「Tは`Number`クラスまたはそのサブクラス」という制約を課すことができます。

```java
// Numberまたはそのサブクラスしか扱えないNumericBox
public class NumericBox<T extends Number> {
    private T number;

    public void setNumber(T number) {
        this.number = number;
    }

    // TはNumberであることが保証されているので、Numberクラスのメソッドを呼べる
    public double getDoubleValue() {
        return this.number.doubleValue();
    }
}

public class BoundedTypeExample {
    public static void main(String[] args) {
        NumericBox<Integer> intBox = new NumericBox<>();
        intBox.setNumber(10);
        System.out.println(intBox.getDoubleValue()); // 10.0

        NumericBox<Double> doubleBox = new NumericBox<>();
        doubleBox.setNumber(3.14);
        System.out.println(doubleBox.getDoubleValue()); // 3.14

        // NumericBox<String> stringBox = new NumericBox<>(); // コンパイルエラー！
    }
}
```

## 9.5 ワイルドカード

ジェネリクスを使ったメソッドの引数などで、より柔軟にさまざまな型を受け入れたい場合に**ワイルドカード (`?`)** を使います。

-   `List<?>`: **未知の型**のリスト。どんな型のリストでも受け取れるが、安全のため`null`以外の要素を追加することはできない。
-   `List<? extends Type>`: **上限境界ワイルドカード**。`Type`またはそのサブクラスのリストを受け取れる。主にデータの**読み取り（Producer）**に使う。
-   `List<? super Type>`: **下限境界ワイルドカード**。`Type`またはそのスーパークラスのリストを受け取れる。主にデータの**書き込み（Consumer）**に使う。

```java
import java.util.List;
import java.util.ArrayList;

public class WildcardExample {
    // 上限境界ワイルドカード：Numberとそのサブクラスのリストを受け取り、合計値を計算する
    public static double sum(List<? extends Number> numberList) {
        double total = 0.0;
        for (Number num : numberList) {
            total += num.doubleValue();
        }
        return total;
    }

    public static void main(String[] args) {
        List<Integer> intList = List.of(1, 2, 3);
        List<Double> doubleList = List.of(1.1, 2.2, 3.3);

        System.out.println("Integerの合計: " + sum(intList));
        System.out.println("Doubleの合計: " + sum(doubleList));
    }
}
```

## まとめ

本章では、Javaの型安全性を支える重要な機能であるジェネリクスについて学びました。

-   **ジェネリクス**は、クラスやメソッドが扱うデータ型をコンパイル時に指定し、型の安全性を保証するしくみです。
-   これにより、実行時エラーである`ClassCastException`を未然に防ぐことができます。
-   自作のクラスやメソッドもジェネリックにすることで、**再利用性**を高めることができます。
-   **型制約**や**ワイルドカード**を使うことで、より柔軟で安全なメソッド設計が可能になります。

コレクションを扱う際には、必ずジェネリクスを使い、その恩恵を最大限に活用することが、現代的なJavaプログラミングの基本です。