# 第11章 ラムダ式と関数型インターフェイス

## 🎯総合演習プロジェクトへのステップ

本章で学ぶラムダ式は、**総合演習プロジェクト「ToDoリストアプリケーション」** のGUIイベント処理を、驚くほど簡潔で読みやすく記述するために不可欠な機能です。

- **イベントリスナの実装**:「追加」ボタンがクリックされた時の処理を、従来の匿名クラスではなく、ラムダ式を使ってスマートに記述します。
  ```java
  // 匿名クラスでの記述（冗長）
  addButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
          addNewTask();
      }
  });

  // ラムダ式での記述（簡潔！）
  addButton.addActionListener(e -> addNewTask());
  ```
- **コードの可読性向上**: ラムダ式を用いることで、「何が起きたら」「何をするか」というロジックが直接的に表現され、コードの意図が明確になります。

本章をマスタすることで、モダンJavaにおけるイベント駆動プログラミ��グの標準的なスタイルを身につけることができます。

## 11.1 ラムダ式とは？

**ラムダ式**は、Java 8で導入された、**匿名関数（名前のない関数）** を簡潔に記述するための構文です。これまで匿名クラスを使っていた冗長なコードを、非常にシンプルに書けるようになります。

### 基本的な構文

ラムダ式の基本構文は以下の通りです。

`(引数リスト) -> { 処理本体 }`

-   `->`（アロー演算子）が、引数と処理を区切ります。
-   処理が1行の場合は、`{}`と`return`文を省略できます。
-   引数の型は、多くの場合コンパイラが推論してくれるため省略可能です。

### 従来の匿名クラス vs ラムダ式

GUIのボタンクリック処理を例に、その違いを見てみましょう。

```java
// 従来の匿名クラス
button.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("ボタンがクリックされました！");
    }
});

// ラムダ式
button.addActionListener(e -> System.out.println("ボタンがクリックされました！"));
```

`new ActionListener()` や `actionPerformed` メソッドの宣言といった定型的なコードがなくなり、「引数 `e` を受け取って、メッセージを出力する」という本質的な処理だけが残っているのがわかります。

## 11.2 関数型インターフェイス

ラムダ式は、どのような場所でも書けるわけではありません。ラムダ式は、**関数型インターフェイス（Functional Interface）** として扱われます。

**関数型インターフェイス**とは、**実装すべき抽象メソッドが1つだけ**定義されているインターフェイスのことです。`@FunctionalInterface` アノテーションを付けることが推奨されます（必須ではありませんが、コンパイラがチェックしてくれます）。

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

`ActionListener`や`Comparator`も、実装すべき抽象メソッドが1つだけなので、���数型インターフェイスです。そのため、ラムダ式で置き換えることができたのです。

### 標準で用意されている関数型インターフェイス

Javaには、`java.util.function`パッケージに、よく使われる汎用的な関数型インターフェイスが多数用意されています。

| インターフェイス | 抽象メソッド | 説明 |
| :--- | :--- | :--- |
| `Predicate<T>` | `boolean test(T t)` | T型を受け取り、`boolean`値を返す（判定） |
| `Function<T, R>` | `R apply(T t)` | T型を受け取り、R型を返す（変換） |
| `Consumer<T>` | `void accept(T t)` | T型を受け取り、何も返さない（消費） |
| `Supplier<T>` | `T get()` | 何も受け取らず、T型を返す（供給） |

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

ラムダ式が既存のメソッドを呼び出すだけの場合、**メソッド参照**という、さらに簡潔な記法が使えます。

| 種類 | 構文 | ラムダ式の例 |
| :--- | :--- | :--- |
| 静的メソッド参照 | `クラス名::メソッド名` | `s -> Integer.parseInt(s)` |
| インスタンスメソッド参照 | `インスタンス変数::メソッド名` | `s -> System.out.println(s)` |
| コンストラクタ参照 | `クラス名::new` | `() -> new ArrayList<>()` |

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

        // 静的メソッド参照
        // ラムダ: s -> System.out.println(s)
        Consumer<String> printer = System.out::println;
        words.forEach(printer);

        // インスタンスメソッド参照
        // ラムダ: s -> s.toUpperCase()
        Function<String, String> toUpper = String::toUpperCase;
        words.stream().map(toUpper).forEach(printer);

        // コンストラクタ参照
        // ラムダ: () -> new ArrayList<>()
        Supplier<List<String>> listFactory = ArrayList::new;
        List<String> newList = listFactory.get();
        System.out.println("新しいリスト: " + newList);
    }
}
```

## まとめ

本章では、モダンJavaプログラミングの基礎となるラムダ式と関数型インターフェイスについて学びました。

-   **ラムダ式**は、匿名関数を簡潔に記述するための構文です。
-   ラムダ式は、**抽象メソッドが1つだけの関数型インターフェイス**として扱われます。
-   `Predicate`, `Function`, `Consumer`, `Supplier`など、汎用的な関数型インターフェイスが標準で用意されています。
-   **メソッド参照**を使うと、既存のメソッドを呼び出すだけのラムダ式を��らに簡潔に書けます。

これらの機能を使いこなすことで、コードの可読性が向上し、より宣言的で簡潔なプログラミングが可能になります。
