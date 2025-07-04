# 第11章 ラムダ式と関数型インターフェイス

## 📝 章末演習

本章で学んだラムダ式と関数型インターフェイスの概念を活用して、実践的な練習課題に取り組みましょう。

### 🎯 演習の目標
- ラムダ式の基本構文と概念の理解
- 関数型インターフェイス（Function、Predicate、Consumer等）の活用
- メソッド参照の使い方（::演算子）
- 従来の匿名クラスからラムダ式への移行
- 関数型プログラミングの基本的な思考法
- 関数合成とパイプライン処理による宣言的プログラミングの実践

### 📁 課題の場所
演習課題は `exercises/chapter11/` ディレクトリに用意されています：

```
exercises/chapter11/
├── basic/          # 基本課題（必須）
│   ├── README.md   # 課題の詳細説明
│   ├── BasicLambda.java # 課題1: 基本的なラムダ式の活用
│   ├── BasicLambdaTest.java
│   ├── FunctionalCalculator.java # 課題2: 関数型電卓
│   ├── FunctionalCalculatorTest.java
│   ├── EventProcessor.java # 課題3: イベント処理システム
│   ├── EventProcessorTest.java
│   ├── ValidationSystem.java # 課題4: バリデーションシステム
│   ├── ValidationSystemTest.java
│   ├── PipelineProcessor.java # 課題5: データパイプライン処理
│   └── PipelineProcessorTest.java
├── advanced/       # 発展課題（推奨）
└── challenge/      # 挑戦課題（上級者向け）
```

### 🚀 推奨する学習の進め方

1. **基本課題**から順番に取り組む
2. 各課題のREADME.mdで詳細を確認
3. TODOコメントを参考に実装
4. ラムダ式と従来の匿名クラスの違いを理解する
5. 関数型インターフェイスの使い分けを習得する

基本課題が完了したら、`advanced/`の発展課題でより複雑な関数型プログラミングに挑戦してみましょう！

## 📋 本章の学習目標

### 前提知識
**必須前提**：
- 第9章までのオブジェクト指向とジェネリクスの習得
- インターフェイスと匿名クラスの実装経験
- コレクションフレームワークの実践的な使用経験

**概念的前提**：
- 関数型プログラミングの基本概念への関心
- コードの簡潔性と可読性への意識

### 学習目標
**知識理解目標**：
- 関数型プログラミングパラダイムの基本概念
- ラムダ式の文法と意味論
- 関数型インターフェイス（Function、Predicate、Consumer、Supplier等）の理解
- メソッド参照の概念と使用法

**技能習得目標**：
- ラムダ式を使った簡潔なコード実装
- 標準関数型インターフェイスの効果的な活用
- メソッド参照による可読性の向上
- 従来の匿名クラスからラムダ式への移行

**プログラミングスタイル目標**：
- 宣言的なプログラミングスタイルの習得
- 関数の合成を使った柔軟なプログラム設計
- 副作用の少ない関数型スタイルの実践

**到達レベルの指標**：
- ラムダ式と関数型インターフェイスを適切に使い分けできる
- 複雑な処理をラムダ式の組み合わせで表現できる
- 関数型スタイルで可読性の高いコードが書ける
- カスタム関数型インターフェイスが設計・実装できる

---

## 11.1 匿名クラスからラムダ式へ

Java 8でラムダ式が導入される前、その場限りのインターフェイス実装を提供するためには**匿名クラス（Anonymous Class）**が使われていました。これは名前を持たないクラスで、特にGUIのイベントリスナなどで多用されていました。

```java
// 匿名クラスを使ったボタンのクリック処理
button.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("ボタンがクリックされました！");
    }
});
```
このコードは、`actionPerformed`という1つのメソッドを実装するためだけに、`new ActionListener() { ... }`という定型的な記述が多く、冗長でした。

**ラムダ式**は、この匿名クラスの記述を、本質的な処理だけを抜き出して劇的に簡潔にするために導入されました。

```java
// ラムダ式を使った場合
button.addActionListener(e -> System.out.println("ボタンがクリックされました！"));
```

## 11.2 関数型インターフェイス

ラムダ式は、どのような場所でも書けるわけではありません。ラムダ式は、**関数型インターフェイス（Functional Interface）** として扱われます。

**関数型インターフェイス**とは、**実装すべき抽象メソッドが1つだけ**定義されているインターフェイスのことです。`@FunctionalInterface` アノテーションを付けると、コンパイラが抽象メソッドが1つだけかどうかをチェックしてくれるため、付けることが推奨されます。

`ActionListener`や前章の`Comparator`も、実装すべき抽象メソッドが実質的に1つだけですので、関数型インターフェイスです。そのため、ラムダ式で置き換えることができたのです。

```java
@FunctionalInterface
interface MyFunction {
    int calculate(int x, int y);
}

public class Main {
    public static void main(String[] args) {
        // ラムダ式を関数型インターフェイス型の変数に代入
        MyFunction addition = (a, b) -> a + b;
        MyFunction subtraction = (a, b) -> a - b;

        System.out.println("足し算: " + addition.calculate(10, 5)); // 15
        System.out.println("引き算: " + subtraction.calculate(10, 5)); // 5
    }
}
```

### ラムダ式の構文バリエーション

ラムダ式は、状況に応じて記述をさらに簡略化できます。

-   **引数の型の省略**: `(int a, int b) -> ...` は `(a, b) -> ...` と書けます。
-   **引数が1つの場合、括弧の省略**: `(a) -> ...` は `a -> ...` と書けます。
-   **処理が1行の場合、中括弧の省略**: `a -> { return a * 2; }` は `a -> a * 2` と書けます。
-   **引数がない場合**: `() -> System.out.println("Hello");` のように括弧だけを書きます。

### `java.util.function` パッケージ

Javaには、`java.util.function`パッケージに、よく使われる汎用的な関数型インターフェイスが多数用意されています。これらを活用することで、自分でインターフェイスを定義する手間を省けます。

| インターフェイス | 抽象メソッド | 説明 |
| :--- | :--- | :--- |
| `Predicate<T>` | `boolean test(T t)` | T型を受け取り、`boolean`値を返す（判定） |
| `Function<T, R>` | `R apply(T t)` | T型を受け取り、R型を返す（変換） |
| `Consumer<T>` | `void accept(T t)` | T型を受け取り、何も返さない（消費） |
| `Supplier<T>` | `T get()` | 何も受け取らず、T型を返す（供給） |
| `UnaryOperator<T>` | `T apply(T t)` | T型を受け取り、同じT型を返す（単項演算） |
| `BinaryOperator<T>` | `T apply(T t1, T t2)` | 同じT型を2つ受け取り、同じT型を返す（二項演算） |

```java
import java.util.function.*;

public class StandardFunctionalInterfaces {
    public static void main(String[] args) {
        // Predicate: 文字列が空かどうかを判定
        Predicate<String> isEmpty = s -> s.isEmpty();
        System.out.println("''は空？: " + isEmpty.test("")); // true

        // Function: 文字列を長さに変換
        Function<String, Integer> getLength = s -> s.length();
        System.out.println("'Java'の長さ: " + getLength.apply("Java")); // 4

        // Consumer: 文字列を大文字で出力
        Consumer<String> printUpper = s -> System.out.println(s.toUpperCase());
        printUpper.accept("hello"); // HELLO

        // Supplier: 現在時刻を供給
        Supplier<Long> currentTime = () -> System.currentTimeMillis();
        System.out.println("現在時刻: " + currentTime.get());
    }
}
```

## 11.3 メソッド参照

ラムダ式が既存のメソッドを呼びだすだけの場合、**メソッド参照（Method Reference）**という、さらに簡潔な記法が使えます。`クラス名::メソッド名`や`インスタンス変数::メソッド名`のように記述します。

| 種類 | 構文 | ラムダ式の例 |
| :--- | :--- | :--- |
| **静的メソッド参照** | `クラス名::静的メソッド名` | `s -> Integer.parseInt(s)` |
| **インスタンスメソッド参照**<br>(特定のインスタンス) | `インスタンス変数::メソッド名` | `s -> System.out.println(s)` |
| **インスタンスメソッド参照**<br>(不特定のインスタンス) | `クラス名::メソッド名` | `s -> s.toUpperCase()` |
| **コンストラクタ参照** | `クラス名::new` | `() -> new ArrayList<>()` |

```java
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class MethodReferenceExample {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("apple", "banana", "cherry");

        // ラムダ: s -> System.out.println(s)
        // メソッド参照: System.out::println
        words.forEach(System.out::println);

        // ラムダ: s -> s.toUpperCase()
        // メソッド参照: String::toUpperCase
        words.stream()
             .map(String::toUpperCase)
             .forEach(System.out::println);

        // ラムダ: () -> new ArrayList<>()
        // メソッド参照: ArrayList::new
        Supplier<List<String>> listFactory = ArrayList::new;
        List<String> newList = listFactory.get();
        System.out.println("新しいリスト: " + newList);
    }
}
```

## 11.4 ラムダ式の応用例

ラムダ式はコレクション操作だけでなく、Javaプログラムのさまざまな場面でコードを簡潔にします。

### スレッドの生成と実行

`Runnable`インターフェイス（`run`メソッドを持つ関数型インターフェイス）もラムダ式で簡単に実装できます。

```java
public class ThreadLambdaExample {
    public static void main(String[] args) {
        // 匿名クラスでのRunnable
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                System.out.println("Thread t1 (Anonymous) is running...");
            }
        });
        t1.start();

        // ラムダ式でのRunnable
        Thread t2 = new Thread(() -> System.out.println("Thread t2 (Lambda) is running..."));
        t2.start();
    }
}
```

## まとめ

本章では、モダンJavaプログラミングの基礎となるラムダ式と関数型インターフェイスについて学びました。

-   **ラムダ式**は、匿名関数を簡潔に記述するための構文で、冗長な匿名クラスを置き換えます。
-   ラムダ式は、**抽象メソッドが1つだけの関数型インターフェイス**として扱われます。
-   `Predicate`, `Function`, `Consumer`, `Supplier`など、汎用的な関数型インターフェイスが標準で用意されています。
-   **メソッド参照**を使うと、既存のメソッドを呼びだすだけのラムダ式をさらに簡潔に書けます。

これらの機能を使いこなすことで、コードの可読性が向上し、より宣言的で簡潔なプログラミングが可能になります。