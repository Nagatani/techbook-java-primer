# 第11章 ラムダ式と関数型インターフェイス

## 章末演習

本章で学んだラムダ式と関数型インターフェイスの概念を活用して、実践的な練習課題に取り組みましょう。

### 🎯 演習の目標
- ラムダ式の基本構文と概念の理解
- 関数型インターフェイス（Function、Predicate、Consumer等）の活用
- メソッド参照の使い方（::演算子）
- 従来の匿名クラスからラムダ式への移行
- 関数型プログラミングの基本的な思考法
- 関数合成とパイプライン処理による宣言的プログラミングの実践

### 演習課題の難易度レベル

#### 🟢 基礎レベル（Basic）
- **目的**: ラムダ式と関数型インターフェイスの基本理解
- **所要時間**: 30-45分/課題
- **前提**: 本章の内容を理解していること
- **評価基準**: 
  - ラムダ式の正しい構文使用
  - 関数型インターフェイスの適切な活用
  - メソッド参照の理解と使用
  - 匿名クラスからの移行

#### 🟡 応用レベル（Applied）  
- **目的**: 実践的な関数型プログラミング技法
- **所要時間**: 45-60分/課題
- **前提**: 基礎レベルを完了していること
- **評価基準**:
  - カスタム関数型インターフェイス設計
  - 高階関数とカリー化
  - 関数合成とパイプライン処理
  - 実用的なアプリケーション設計

#### 🔴 発展レベル（Advanced）
- **目的**: 関数型プログラミングの高度な応用
- **所要時間**: 60-90分/課題
- **前提**: 応用レベルを完了していること
- **評価基準**:
  - モナドパターンの理解
  - リアクティブプログラミング
  - 並列処理と関数型アプローチ
  - フレームワークレベルの設計

---

## 🟢 基礎レベル課題（必須）

### 課題1: 基本的なラムダ式の活用

**学習目標：** ラムダ式の基本構文、標準関数型インターフェイスの活用

**問題説明：**
基本的なラムダ式を活用した処理を実装し、関数型インターフェイスを理解してください。

**要求仕様：**
- Predicate<T> を使った条件判定
- Function<T, R> を使ったデータ変換
- Consumer<T> を使った処理実行
- Supplier<T> を使った値生成
- Comparator<T> を使ったソート

**実行例：**
```
=== 基本的なラムダ式の活用 ===
数値リスト: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

Predicate（偶数判定）:
偶数: [2, 4, 6, 8, 10]

Function（２乗変換）:
元の値: [1, 2, 3, 4, 5]
２乗後: [1, 4, 9, 16, 25]

Consumer（出力処理）:
値の出力: 1, 2, 3, 4, 5

Supplier（ランダム値生成）:
ランダム値: 42, 17, 89, 33, 56

Comparator（ソート）:
文字列長さソート: ["a", "bb", "ccc", "dddd"]
逆順ソート: [10, 9, 8, 7, 6, 5, 4, 3, 2, 1]
```

**実装ヒント：**
```java
public class BasicLambda {
    public void demonstrateFunctionalInterfaces() {
        // Predicate例
        Predicate<Integer> isEven = n -> n % 2 == 0;
        
        // Function例
        Function<Integer, Integer> square = x -> x * x;
        
        // Consumer例
        Consumer<String> printer = System.out::println;
        
        // Supplier例
        Supplier<Integer> randomInt = () -> new Random().nextInt(100);
        
        // Comparator例
        Comparator<String> byLength = (s1, s2) -> s1.length() - s2.length();
    }
}
```

### 課題2: カスタム関数型インターフェイス設計

**学習目標：** 独自の関数型インターフェイス設計、@FunctionalInterface、デフォルトメソッド

**問題説明：**
独自の関数型インターフェイスを設計し、ラムダ式で実装してください。

**要求仕様：**
- @FunctionalInterface アノテーション使用
- 数学演算用の関数型インターフェイス
- 文字列処理用の関数型インターフェイス
- デフォルトメソッドの活用
- 複数の実装パターン

**実行例：**
```
=== カスタム関数型インターフェイス設計 ===
数学演算テスト:
加算: 10 + 5 = 15
減算: 10 - 5 = 5
乗算: 10 * 5 = 50
除算: 10 / 5 = 2.0

複合演算:
(10 + 5) * 2 = 30
(10 - 5) / 2 = 2.5

文字列処理テスト:
大文字変換: "hello" → "HELLO"
小文字変換: "WORLD" → "world"
逆順変換: "Java" → "avaJ"

複合処理:
"hello world" → 大文字化 → 逆順 → "DLROW OLLEH"

条件付き処理:
長さ5以上の文字列のみ大文字化:
"hi" → "hi" (変換なし)
"hello" → "HELLO" (変換実行)
```

**実装ヒント：**
```java
@FunctionalInterface
public interface MathOperation {
    double calculate(double a, double b);
    
    // デフォルトメソッドで複合演算を実現
    default MathOperation andThen(MathOperation after) {
        return (a, b) -> after.calculate(this.calculate(a, b), 0);
    }
}

@FunctionalInterface
public interface StringProcessor {
    String process(String input);
    
    default StringProcessor andThen(StringProcessor after) {
        return input -> after.process(this.process(input));
    }
    
    default StringProcessor compose(StringProcessor before) {
        return input -> this.process(before.process(input));
    }
}
```

### 課題3: メソッド参照とコンストラクタ参照

**学習目標：** 4種類のメソッド参照、コンストラクタ参照、ラムダ式との比較

**問題説明：**
メソッド参照とコンストラクタ参照を活用した処理を実装してください。

**要求仕様：**
- 静的メソッド参照（Class::staticMethod）
- インスタンスメソッド参照（instance::method）
- 任意オブジェクトのインスタンスメソッド参照（Class::instanceMethod）
- コンストラクタ参照（Class::new）
- メソッド参照からラムダ式への変換比較

**実行例：**
```
=== メソッド参照とコンストラクタ参照 ===
静的メソッド参照テスト:
数値リスト: [1, 4, 9, 16, 25]
平方根: [1.0, 2.0, 3.0, 4.0, 5.0]

インスタンスメソッド参照テスト:
文字列リスト: ["hello", "world", "java"]
大文字変換: ["HELLO", "WORLD", "JAVA"]

任意オブジェクトのメソッド参照:
文字列長さ: [5, 5, 4]

コンストラクタ参照テスト:
Person生成:
田中太郎（25歳）
佐藤花子（30歳）
鈴木一郎（35歳）

配列コンストラクタ参照:
整数配列: [0, 0, 0, 0, 0]
文字列配列: [null, null, null]

ラムダ式 vs メソッド参照比較:
ラムダ式: x -> Math.sqrt(x)
メソッド参照: Math::sqrt
結果は同じ: [1.0, 2.0, 3.0]
```

**実装ヒント：**
```java
public class MethodReference {
    public void demonstrateMethodReferences() {
        List<String> words = Arrays.asList("hello", "world", "java");
        
        // 静的メソッド参照
        List<Double> numbers = Arrays.asList(1.0, 4.0, 9.0);
        numbers.stream().map(Math::sqrt).collect(Collectors.toList());
        
        // インスタンスメソッド参照
        words.forEach(System.out::println);
        
        // 任意オブジェクトのメソッド参照
        words.stream().map(String::toUpperCase).collect(Collectors.toList());
        
        // コンストラクタ参照
        Supplier<List<String>> listFactory = ArrayList::new;
        Function<String, Person> personFactory = Person::new;
    }
}
```

### 課題4: 高階関数とカリー化

**学習目標：** 高階関数、カリー化、関数合成、遅延評価

**問題説明：**
高階関数とカリー化を実装し、関数型プログラミングの高度な概念を理解してください。

**要求仕様：**
- 関数を引数として受け取る高階関数
- 関数を戻り値として返す高階関数
- カリー化（部分適用）の実装
- 関数の合成（compose、andThen）
- 遅延評価の実装

**実行例：**
```
=== 高階関数とカリー化 ===
高階関数テスト（関数を引数に取る）:
リスト: [1, 2, 3, 4, 5]
処理1（2倍）: [2, 4, 6, 8, 10]
処理2（2乗）: [1, 4, 9, 16, 25]

高階関数テスト（関数を返す）:
加算器生成:
add5 = createAdder(5)
add5(10) = 15
add5(20) = 25

カリー化テスト:
三項演算: f(x, y, z) = x + y * z
カリー化: f(2)(3)(4) = 2 + 3 * 4 = 14

部分適用:
multiply(2, 3) = 6
multiplyBy2 = multiply(2, _)
multiplyBy2(3) = 6
multiplyBy2(5) = 10

関数合成テスト:
f(x) = x * 2
g(x) = x + 3
compose: g(f(5)) = g(10) = 13
andThen: f(g(5)) = f(8) = 16

遅延評価テスト:
計算定義時: (処理なし)
実行時: 2 * 3 + 5 = 11
```

**実装ヒント：**
```java
public class HigherOrderFunction {
    // 高階関数（関数を引数に取る）
    public static <T> List<T> mapList(List<T> list, Function<T, T> mapper) {
        return list.stream().map(mapper).collect(Collectors.toList());
    }
    
    // 高階関数（関数を返す）
    public static Function<Integer, Integer> createAdder(int value) {
        return x -> x + value;
    }
    
    // カリー化
    public static Function<Integer, Function<Integer, Function<Integer, Integer>>> 
        curriedAdd() {
        return x -> y -> z -> x + y * z;
    }
    
    // 遅延評価
    public static Supplier<Integer> lazyComputation(int a, int b, int c) {
        return () -> a * b + c;
    }
}
```

---

## 💡 実装のヒント

### ラムダ式の基本原則
1. **簡潔な構文**: (parameters) -> expression
2. **型推論**: コンパイラが型を推論
3. **メソッド参照**: 既存メソッドの簡潔な表現
4. **関数合成**: andThen、composeで連鎖処理

### よくある落とし穴
- ラムダ式内でのfinalまたは実質的にfinalな変数のみ参照可能
- メソッド参照の種類の混同
- カリー化での型の複雑さ
- 関数型インターフェイスの選択

### 設計のベストプラクティス
- 可能な限りメソッド参照を使用
- 適切な関数型インターフェイスを選択
- 副作用の少ない関数を心がける
- 関数合成で柔軟な設計を実現

---

## 🔗 実装環境

演習課題の詳細な実装テンプレート、テストコード、解答例は以下のディレクトリを参照してください：

```
exercises/chapter11/
├── basic/          # 基礎レベル課題
│   ├── README.md   # 詳細な課題説明
│   ├── BasicLambda.java
│   ├── CustomFunctionalInterface.java
│   ├── MethodReference.java
│   └── HigherOrderFunction.java
├── advanced/       # 応用レベル課題
├── challenge/      # 発展レベル課題
└── solutions/      # 解答例（実装完了後に参照）
```

---

## ✅ 完了確認チェックリスト

### 基礎レベル
- [ ] 基本的なラムダ式と関数型インターフェイスが使えている
- [ ] カスタム関数型インターフェイスが設計できている
- [ ] メソッド参照の4パターンが理解できている
- [ ] 高階関数とカリー化が実装できている

### 技術要素
- [ ] ラムダ式の構文を正しく理解している
- [ ] 関数型プログラミングの基本概念を把握している
- [ ] 匿名クラスからラムダ式への移行ができている
- [ ] 関数合成で複雑な処理を表現できている

### 応用レベル
- [ ] モナドパターンの基本を理解している
- [ ] 並列処理での関数型アプローチを活用できている
- [ ] 宣言的なプログラミングスタイルを実践できている
- [ ] 関数型ライブラリやフレームワークが設計できている

## 本章の学習目標

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

## Deep Dive: 関数型プログラミングパラダイムの歴史

> **対象読者**: プログラミングパラダイムの発展に興味がある読者向け  
> **前提知識**: 手続き型・オブジェクト指向プログラミングの基本理解  
> **学習時間**: 約18分

### 数学的基盤：ラムダ計算

関数型プログラミングの理論的基盤は、1930年代にアロンゾ・チャーチが開発した**ラムダ計算（Lambda Calculus）**にあります。これは、関数の定義と適用だけで計算を表現する数学的システムです。

```
// ラムダ計算の記法例
λx.x        // 恒等関数（入力をそのまま返す）
λx.λy.x+y   // 2引数の加算関数
```

このシンプルな記法が、現代のラムダ式の源流となっています。

### 関数型言語の系譜

**LISP（1958年）**
John McCarthyによって開発された最初の関数型言語：
```lisp
(lambda (x) (+ x 1))  ; xに1を加える関数
```

**ML（1973年）・Haskell（1990年）**
より純粋な関数型言語として発展：
```haskell
-- Haskellの例
add1 = \x -> x + 1
map add1 [1,2,3]  -- [2,3,4]
```

### マルチパラダイム言語への統合

**JavaScript（ES6, 2015年）**
```javascript
// アロー関数
const add = (x, y) => x + y;
[1,2,3].map(x => x * 2);
```

**Java 8（2014年）**
```java
// ラムダ式
Function<Integer, Integer> add1 = x -> x + 1;
Arrays.asList(1,2,3).stream().map(x -> x * 2);
```

### なぜ関数型がオブジェクト指向言語に？

1. **並行処理の重要性**: 不変性と副作用の排除により、マルチスレッド処理が安全になる
2. **ビッグデータ処理**: MapReduceパターンなど、大規模データ処理に適している
3. **コードの簡潔性**: 宣言的な記述により、可読性とメンテナンス性が向上
4. **数学的厳密性**: 関数合成により、複雑な処理を理解しやすい形で表現

### モナドとファンクター

関数型プログラミングの高度な概念：

```java
// OptionalはモナドのJava実装
Optional.of("hello")
    .map(String::toUpperCase)      // ファンクター的操作
    .flatMap(s -> getValue(s))     // モナド的操作
    .orElse("default");
```

### 現代的な意義

関数型プログラミングは、以下の現代的課題の解決策として再評価されています：

- **並行・並列処理**: 不変データ構造による安全な並列実行
- **ビッグデータ**: Stream APIによる効率的なデータ処理パイプライン
- **リアクティブプログラミング**: 非同期イベント処理
- **ドメイン駆動設計**: 関数合成による複雑なビジネスロジックの表現

### 参考文献・関連資料
- "Structure and Interpretation of Computer Programs" - Abelson & Sussman
- "Functional Programming in Java" - Venkat Subramaniam
- "Java 8 in Action" - Raoul-Gabriel Urma

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