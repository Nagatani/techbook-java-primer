# 第9章 ジェネリクス

## 章末演習

本章で学んだジェネリクスの概念を活用して、実践的な練習課題に取り組みましょう。

### 演習の目標
- ジェネリクスの基本概念と型安全性の理解
- 型パラメータ（T、E、K、V）の使い方
- 境界付きワイルドカード（extends、super）の活用
- ジェネリックメソッドとジェネリッククラスの実装
- 型消去（Type Erasure）の理解と制限事項
- 型安全なビルダパターンの設計と実装

### 📁 課題の場所
演習課題は `exercises/chapter09/` ディレクトリに用意されています：

```
exercises/chapter09/
├── basic/          # 基本課題（必須）
│   ├── README.md   # 課題の詳細説明
│   ├── Pair.java   # 課題1: ジェネリックペアクラス
│   ├── PairTest.java
│   ├── GenericBox.java # 課題2: ジェネリックボックス
│   ├── GenericBoxTest.java
│   ├── GenericStack.java # 課題3: ジェネリックスタック
│   ├── GenericStackTest.java
│   ├── TypeSafeBuilder.java # 課題4: 型安全ビルダーパターン
│   └── TypeSafeBuilderTest.java
├── advanced/       # 発展課題（推奨）
└── challenge/      # 挑戦課題（上級者向け）
```

### 推奨する学習の進め方

1. **基本課題**から順番に取り組む
2. 各課題のREADME.mdで詳細を確認
3. ToDoコメントを参考に実装
4. 型安全性を意識した設計を心がける
5. 境界ワイルドカードの適切な使用を理解する

基本課題が完了したら、`advanced/`の発展課題でより複雑なジェネリクス設計に挑戦してみましょう！

## 本章の学習目標

### 前提知識
**必須前提**：
- 第8章のコレクションフレームワークの理解と実践経験
- 型システムの基本的な理解
- オブジェクト指向の継承とポリモーフィズムの理解

**実践経験前提**：
- コレクションを使った実用的なプログラム開発経験
- 型安全性の問題（ClassCastException等）の経験

### 学習目標
**知識理解目標**：
- ジェネリクスの設計目的と利点の深い理解
- 型パラメータと型引数の概念
- 境界ワイルドカード（? extends、? super）の理解
- 型消去（Type Erasure）のしくみと制限事項

**技能習得目標**：
- ジェネリッククラスとインターフェイスの設計・実装
- ジェネリックメソッドの効果的な使用
- 境界ワイルドカードを使った柔軟な型設計
- 既存のジェネリックAPIの効果的な活用

**設計能力目標**：
- 型安全で再利用可能なライブラリの設計
- 複雑な型関係を持つシステムの設計
- ジェネリクスを活用したAPIの設計

**到達レベルの指標**：
- 型安全で再利用可能なジェネリッククラスが独力で設計・実装できる
- 複雑なジェネリック型を含むAPIが理解・活用できる
- 型関連のコンパイルエラーを適切に解決できる
- ジェネリクスを使った柔軟で保守性の高いライブラリが作成できる

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