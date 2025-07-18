# <b>11章</b> <span>ジェネリクス</span> <small>型安全なプログラミングの実現</small>

## 本章の学習目標

### 前提知識

必須
- 第10章のコレクションフレームワークの理解と実践経験
- 型システムの基本的な理解
- オブジェクト指向の継承とポリモーフィズムの理解

推奨
- コレクションを使った実用的なプログラム開発経験
- 型安全性の問題（ClassCastException等）の経験

### 学習目標
#### 知識理解目標
- ジェネリクスの設計目的と利点の深い理解
- 型パラメータと型引数の概念
- 境界ワイルドカード（? extends、? super）の理解
- 型消去（Type Erasure）のしくみと制限事項
- var型推論のメカニズムと適用範囲

#### 技能習得目標
- ジェネリッククラスとインターフェイスの設計・実装
- ジェネリックメソッドの効果的な使用
- 境界ワイルドカードを使った柔軟な型設計
- 既存のジェネリックAPIの効果的な活用
- var型推論を使った可読性の高いコードの作成

設計能力目標。
- 型安全で再利用可能なライブラリの設計
- 複雑な型関係を持つシステムの設計
- ジェネリクスを活用したAPIの設計
- var型推論を適切に判断・活用したコード設計

#### 到達レベルの指標
- 型安全で再利用可能なジェネリッククラスが独力で設計・実装できる
- 複雑なジェネリック型を含むAPIが理解・活用できる
- 型関連のコンパイルエラーを適切に解決できる
- ジェネリクスを使った柔軟で保守性の高いライブラリが作成できる



## なぜジェネリクスが必要なのか？

### 第10章で見た不思議な記法

第10章でコレクションフレームワークを学習したとき、以下のような記法を見たはずです。

<span class="listing-number">**サンプルコード11-1**</span>

```java
List<String> students = new ArrayList<String>();
Set<Integer> numbers = new HashSet<Integer>();
Map<String, Integer> scores = new HashMap<String, Integer>();
```

この`<>`で囲まれた部分がジェネリクスです。なぜこのような記法が必要なのでしょうか？実は、これはJavaの進化の過程で生まれた大切な機能なのです。

### まずは問題を体験してみよう

ジェネリクスがない場合にどんな問題が起きるか、実際に体験してみましょう。以下のコードは、あえてジェネリクスを使わずに書いた例です。

<span class="listing-number">**サンプルコード11-2**</span>

```java
import java.util.ArrayList;
import java.util.List;

public class CollectionProblem {
    public static void main(String[] args) {
        // ジェネリクスを使わないリスト（raw type）
        List list = new ArrayList();
        
        // 文字列を追加
        list.add("Java");
        list.add("Python");
        
        // うっかり数値も追加できてしまう！
        list.add(42);
        
        // 取り出すとき...
        for (int i = 0; i < list.size(); i++) {
            // Object型として取り出される
            Object item = list.get(i);
            
            // 文字列として使いたい場合は型変換が必要
            String language = (String) item;  // 3番目の要素で実行時エラー！
            System.out.println(language.toUpperCase());
        }
    }
}
```

このプログラムを実行すると、3番目の要素（数値の42）を文字列に変換しようとした時点で`ClassCastException`が発生します。これは実行時まで発見できないエラーです。

### ジェネリクスによる解決

同じプログラムをジェネリクスを使って書き直してみましょう。

<span class="listing-number">**サンプルコード11-3**</span>

```java
import java.util.ArrayList;
import java.util.List;

public class CollectionSolution {
    public static void main(String[] args) {
        // String型だけを格納できるリスト
        List<String> list = new ArrayList<String>();
        
        // 文字列は問題なく追加できる
        list.add("Java");
        list.add("Python");
        
        // 数値を追加しようとすると...
        // list.add(42);  // コンパイルエラー！実行前に問題を発見
        
        // 取り出すときも型変換不要
        for (String language : list) {
            System.out.println(language.toUpperCase());
        }
    }
}
```

#### ジェネリクスを使うことで
1. コンパイル時に型の不一致を検出できる
2. 明示的な型変換（キャスト）が不要になる
3. コードの意図が明確になる（「文字列のリスト」であることが一目瞭然）

### 型安全性の重要性

プログラミング言語における型システムの目的は、プログラムの正しさをコンパイル時に検証し、実行時エラーを減らすことです。しかし、汎用的なデータ構造（コレクションなど）を実装する際には、「どんな型でも扱える」柔軟性と「型の安全性」を両立させる必要がありました。

Java 5より前の時代、コレクションは「あらゆるオブジェクト」を格納できる`Object`型の入れ物でした。これは一見便利に思えますが、大きな問題を抱えていました。

<span class="listing-number">**サンプルコード11-4**</span>

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

この問題を解決するためにJava 5で導入されたのがジェネリクス（Generics）です。

### ジェネリクスの基本概念

ジェネリクスは、クラスやインターフェイスが「どの型のデータ」を扱うのかを、クラスを定義または利用する時点で指定できるようにするしくみです。これは、「パラメータ化された型（Parameterized Types）」とも呼ばれ、以下のような利点を提供します。

1. コンパイル時の型検査：型の不一致をコンパイル時に検出し、実行時エラーを防ぐ
2. キャストの除去：明示的な型キャストが不要になり、コードがより安全で読みやすくなる
3. コードの再利用性：同じロジックを異なる型に対して適用できる

### 型消去（Type Erasure）のしくみ

Javaのジェネリクスの大切な特徴の1つが「型消去」です。これは、コンパイル時にジェネリック型情報が検査された後、実行時にはその情報が削除されるしくみです。これにより。

- 既存のコードとの後方互換性が保たれる
- 実行時のパフォーマンスオーバーヘッドがない
- ただし、実行時に型情報を取得できないという制限もある

`List<String>`のように、クラス名の後に山括弧`< >`で型を指定します。これを型パラメータと呼びます。

<span class="listing-number">**サンプルコード11-5**</span>

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

## ジェネリッククラスの作成

ジェネリクスはコレクションだけでなく、自作のクラスにも適用できます。これにより、特定の型に依存しない、再利用性の高いクラスを作成できます。

### 型パラメータの命名慣習

ジェネリクスで使用される型パラメータには、慣習的な命名規則があります。

- `T` - Type（一般的な型）
- `E` - Element（コレクションの要素）
- `K` - Key（マップのキー）
- `V` - Value（マップの値）
- `N` - Number（数値）
- `S`, `U`, `V` - 2番目、3番目、4番目の型

これらの慣習に従うことで、コードの可読性が向上し、ほかの開発者にとっても理解しやすいコードとなります。

### 基本的なジェネリッククラス

クラス名の後に`<T>`のような型パラメータを宣言します。`T`は"Type"の頭文字で、慣習的に使われるプレースホルダです。

<span class="listing-number">**サンプルコード11-6**</span>

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

Java 7からは、右辺の型パラメータを省略できるダイヤモンド演算子 (`<>`)が導入され、より簡潔に記述できるようになりました。コンパイラが左辺の宣言から型を推論してくれます。

```java
// Java 7以降の推奨される書き方
Map<String, List<Integer>> complexMap = new HashMap<>();
```

## ジェネリックメソッド

クラス全体ではなく、特定のメソッドだけをジェネリックにすることも可能です。メソッドの戻り値の型の前に型パラメータを宣言します。

<span class="listing-number">**サンプルコード11-7**</span>

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

## 型制約（境界のある型パラメータ）

ジェネリクスで使える型を、特定のクラスのサブクラスや、特定のインターフェイスを実装したクラスに限定したい場合があります。これを境界のある型パラメータとよい、`extends`キーワードを使います。

`<T extends Number>`と書くと、「Tは`Number`クラスまたはそのサブクラス」という制約を課すことができます。

<span class="listing-number">**サンプルコード11-8**</span>

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

## ワイルドカード

ジェネリクスを使ったメソッドの引数などで、より柔軟にさまざまな型を受け入れたい場合にワイルドカード (`?`) を使います。

- `List<?>`: 未知の型のリスト。どんな型のリストでも受け取れるが、安全のため`null`以外の要素を追加することはできない
- `List<? extends Type>`: 上限境界ワイルドカード。`Type`またはそのサブクラスのリストを受け取れる。主にデータの読み取り（Producer）に使う
- `List<? super Type>`: 下限境界ワイルドカード。`Type`またはそのスーパークラスのリストを受け取れる。主にデータの書き込み（Consumer）に使う

<span class="listing-number">**サンプルコード11-9**</span>

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

## var型推論とジェネリクス

Java 10で導入されたvar型推論は、ジェネリクスと組み合わせることで、より簡潔で読みやすいコードを書くことができます。本セクションでは、ジェネリクスの文脈でのvar型推論の活用方法について学習します。

### var型推論の基本概念

var型推論は、ローカル変数型推論（Local Variable Type Inference）とも呼ばれ、コンパイラが変数の初期化式から型を自動的に推論する機能です。これにより、冗長な型宣言を省略できます。

<span class="listing-number">**サンプルコード11-10**</span>

```java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VarBasicExample {
    public static void main(String[] args) {
        // 従来の書き方
        List<String> languages = new ArrayList<String>();
        Map<String, Integer> scores = new HashMap<String, Integer>();

        // ダイヤモンド演算子を使った書き方（Java 7以降）
        List<String> languagesV7 = new ArrayList<>();
        Map<String, Integer> scoresV7 = new HashMap<>();

        // var型推論を使った書き方（Java 10以降）
        var languagesVar = new ArrayList<String>();
        var scoresVar = new HashMap<String, Integer>();

        // いずれも同じ型になる
        System.out.println("languages: " + languages.getClass());
        System.out.println("languagesVar: " + languagesVar.getClass());
    }
}
```

### 使用できる場面と制限事項

var型推論は、以下の場面でのみ使用できます。

1. ローカル変数の初期化（コンストラクタ、メソッド内）
2. 拡張for文のインデックス変数
3. try-with-resources文のリソース変数（Java 14以降）

一方、以下の場面では使用できません。

<span class="listing-number">**サンプルコード11-11**</span>

```java
import java.util.List;
import java.util.ArrayList;

public class VarLimitations {
    // var field = new ArrayList<String>(); // フィールドでは使用不可
    
    // public void method(var param) { } // パラメータでは使用不可
    
    // public var method() { return ""; } // 戻り値型では使用不可
    
    public static void demonstrateLimitations() {
        // OK: ローカル変数の初期化
        var list = new ArrayList<String>();
        
        // NG: 初期化なし
        // var uninitialized; // コンパイルエラー
        
        // NG: null初期化
        // var nullVar = null; // コンパイルエラー
        
        // NG: 配列初期化子
        // var array = {1, 2, 3}; // コンパイルエラー
        
        // OK: 明示的な配列作成
        var validArray = new int[]{1, 2, 3};
        
        // NG: ダイヤモンド演算子単体
        // var diamond = new ArrayList<>(); // コンパイルエラー
        
        // OK: 型が推論可能
        var inferredList = new ArrayList<String>();
    }
}
```

### 可読性の向上とトレードオフ

var型推論は適切に使用すると可読性を向上させますが、使いすぎると逆に可読性を損なう場合があります。

<span class="listing-number">**サンプルコード11-12**</span>

```java
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class VarReadabilityExample {
    public static void demonstrateGoodUsage() {
        // 良い使用例：型が明らかで、冗長性を減らす
        var students = List.of("Alice", "Bob", "Charlie");
        var scoreMap = new HashMap<String, Integer>();
        var builder = new StringBuilder();
        
        // 複雑なジェネリック型で特に有効
        var complexMap = new HashMap<String, List<Map<String, Object>>>();
        
        // Stream APIとの組み合わせ
        var upperCaseNames = students.stream()
            .map(String::toUpperCase)
            .collect(Collectors.toList());
    }
    
    public static void demonstratePoorUsage() {
        // 悪い使用例：型が不明確
        var data = getData();           // 戻り値の型が不明
        var result = process(data);     // メソッドの結果の型が不明
        var flag = checkCondition();    // boolean? String? 不明
        
        // 数値型の場合は明示的な型の方が良い場合も
        var count = 0;      // int? long? 
        var rate = 0.5;     // float? double?
        
        // より明確な書き方
        int itemCount = 0;
        double successRate = 0.5;
    }
    
    private static Object getData() { return null; }
    private static Object process(Object data) { return null; }
    private static boolean checkCondition() { return true; }
}
```

### ジェネリクスとvarの組み合わせ

var型推論は、ジェネリクスと組み合わせることで特に威力を発揮します。

<span class="listing-number">**サンプルコード11-13**</span>

```java
import java.util.*;
import java.util.stream.Collectors;

public class VarWithGenericsExample {
    public static void main(String[] args) {
        // 複雑なジェネリック型の簡略化
        var userPreferences = new HashMap<String, List<String>>();
        var nestedData = new ArrayList<Map<String, Set<Integer>>>();
        
        // メソッドチェーンでの型推論
        var processedData = Arrays.asList("apple", "banana", "cherry")
            .stream()
            .filter(s -> s.length() > 5)
            .map(String::toUpperCase)
            .collect(Collectors.toList());
        
        // ワイルドカードとの組み合わせ
        List<? extends Number> numbers = Arrays.asList(1, 2.5, 3L);
        var sum = calculateSum(numbers);
        
        // ジェネリックメソッドの戻り値
        var pair = createPair("Hello", 42);
        System.out.println("First: " + pair.getFirst());
        System.out.println("Second: " + pair.getSecond());
    }
    
    public static double calculateSum(List<? extends Number> numbers) {
        return numbers.stream()
            .mapToDouble(Number::doubleValue)
            .sum();
    }
    
    public static <T, U> Pair<T, U> createPair(T first, U second) {
        return new Pair<>(first, second);
    }
    
    static class Pair<T, U> {
        private final T first;
        private final U second;
        
        public Pair(T first, U second) {
            this.first = first;
            this.second = second;
        }
        
        public T getFirst() { return first; }
        public U getSecond() { return second; }
    }
}
```

### ベストプラクティスとアンチパターン

var型推論を効果的に使用するためのガイドライン。

<span class="listing-number">**サンプルコード11-14**</span>

```java
import java.util.*;
import java.util.stream.Collectors;

public class VarBestPractices {
    
    // ✅ GOOD: 変数名で型が明確
    public static void goodPractices() {
        var studentList = new ArrayList<String>();
        var scoreMap = new HashMap<String, Integer>();
        var messageBuilder = new StringBuilder();
        
        // ✅ 長いジェネリック型の簡略化
        var complexStructure = new HashMap<String, List<Map<String, Object>>>();
        
        // ✅ ファクトリーメソッドでの使用
        var immutableList = List.of("a", "b", "c");
        var emptySet = Set.of();
        
        // ✅ Stream API処理結果
        var evenNumbers = List.of(1, 2, 3, 4, 5)
            .stream()
            .filter(n -> n % 2 == 0)
            .collect(Collectors.toList());
    }
    
    // ❌ POOR: 型が不明確
    public static void poorPractices() {
        // ❌ 変数名だけでは型が不明
        var data = processData();
        var result = calculate();
        var value = getValue();
        
        // ❌ プリミティブ型での誤用
        var x = 10;          // int? long?
        var y = 3.14;        // float? double?
        
        // ❌ nullリテラル（コンパイルエラー）
        // var nullValue = null;
        
        // ❌ 配列初期化子（コンパイルエラー）
        // var array = {1, 2, 3};
    }
    
    // ✅ GOOD: 型注釈を活用した明確化
    public static void typeAnnotationPattern() {
        // 必要に応じて型注釈で明確化
        List<String> stringList = new ArrayList<>();  // 明示的
        var numberList = new ArrayList<Integer>();     // 推論、型は明確
        
        // メソッド参照で型が明確な場合
        var upperCaseConverter = String::toUpperCase;
        
        // ラムダ式では文脈から型が明確
        var predicate = (String s) -> s.length() > 3;
    }
    
    private static Object processData() { return new Object(); }
    private static int calculate() { return 42; }
    private static String getValue() { return "value"; }
}
```

### リファクタリングでのvar活用

既存のコードをvarを使ってリファクタリングする際の戦略。

<span class="listing-number">**サンプルコード11-15**</span>

```java
import java.util.*;
import java.util.stream.Collectors;

public class VarRefactoringExample {
    
    // リファクタリング前
    public static void beforeRefactoring() {
        HashMap<String, List<Map<String, Object>>> complexData = 
            new HashMap<String, List<Map<String, Object>>>();
        
        ArrayList<String> filteredNames = new ArrayList<String>();
        
        List<Integer> processedNumbers = Arrays.asList(1, 2, 3, 4, 5)
            .stream()
            .filter(n -> n % 2 == 0)
            .map(n -> n * 2)
            .collect(Collectors.toList());
    }
    
    // リファクタリング後
    public static void afterRefactoring() {
        var complexData = new HashMap<String, List<Map<String, Object>>>();
        var filteredNames = new ArrayList<String>();
        
        var processedNumbers = Arrays.asList(1, 2, 3, 4, 5)
            .stream()
            .filter(n -> n % 2 == 0)
            .map(n -> n * 2)
            .collect(Collectors.toList());
    }
    
    // 段階的リファクタリングの例
    public static void gradualRefactoring() {
        // ステップ1: 最も冗長な部分から開始
        var userCache = new ConcurrentHashMap<String, UserProfile>();
        
        // ステップ2: 型が明確なファクトリーメソッド
        var emptyList = Collections.<String>emptyList();
        
        // ステップ3: Stream処理結果
        var groupedUsers = getAllUsers()
            .stream()
            .collect(Collectors.groupingBy(UserProfile::getDepartment));
    }
    
    private static List<UserProfile> getAllUsers() {
        return List.of();
    }
    
    static class UserProfile {
        public String getDepartment() { return "Engineering"; }
    }
}
```

### var型推論の実践的な判断基準

var型推論を使用するかどうかの判断基準。

1. 使用を推奨する場面。
   - 複雑なジェネリック型で冗長性が高い
   - 変数名から型が明確に推測できる
   - 初期化式から型が明らか（ファクトリーメソッド、new演算子）
   - Stream APIの処理結果

2. 明示的な型宣言を推奨する場面。
   - メソッドの戻り値型が不明確
   - プリミティブ型で精度が重要
   - パブリックAPIの一部
   - 型が重要な意味を持つ場合

var型推論は、Javaの表現力を高める強力な機能ですが、適切に使用することでコードの可読性と保守性を向上させることができます。ジェネリクスと組み合わせて使用することで、複雑な型宣言を簡潔にし、本質的なロジックに集中できるコードを書くことが可能になります。

※ 本章の高度な内容については、付録B09「ジェネリクスの内部実装」を参照してください。

## よくあるエラーと対処法

ジェネリクスの学習で遭遇する典型的なエラーと、その対処法について説明します。

### Raw typeの使用エラー

##### エラー例
```java
// ❌ Raw typeの使用
List list = new ArrayList();
list.add("Hello");
String str = (String) list.get(0); // キャストが必要
```

##### 警告メッセージ
```
Note: GenericsExample.java uses unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for more details.
```

##### 対処法
```java
// ✅ ジェネリクスを使用
List<String> list = new ArrayList<>();
list.add("Hello");
String str = list.get(0); // キャスト不要
```

### 型パラメータの不適切な使用

##### エラー例
```java
// ❌ 不適切な型パラメータの使用
public class Container<T> {
    private T value;
    
    public void setNewValue() {
        this.value = new T(); // コンパイルエラー
    }
}
```

##### エラーメッセージ
```
error: Cannot instantiate the type T
```

##### 対処法
```java
// ✅ ファクトリーメソッドを使用
public class Container<T> {
    private T value;
    private Supplier<T> factory;
    
    public Container(Supplier<T> factory) {
        this.factory = factory;
    }
    
    public void setNewValue() {
        this.value = factory.get();
    }
}
```

### ワイルドカードの誤用

##### エラー例
```java
// ❌ ワイルドカードの誤用
List<? extends Number> numbers = new ArrayList<Integer>();
numbers.add(1); // コンパイルエラー
```

##### エラーメッセージ
```
error: The method add(capture#1-of ? extends Number) is not applicable for the arguments (int)
```

##### 対処法
```java
// ✅ 適切なワイルドカードの使用
List<Integer> integers = new ArrayList<>();
integers.add(1);
List<? extends Number> numbers = integers; // 読み取り専用として使用
```

### 型消去に関する問題

##### エラー例
```java
// ❌ 型消去による問題
public class GenericArray<T> {
    private T[] array;
    
    public GenericArray(int size) {
        array = new T[size]; // コンパイルエラー
    }
}
```

##### エラーメッセージ
```
error: Cannot create a generic array of T
```

##### 対処法
```java
// ✅ 型消去を考慮した実装
public class GenericArray<T> {
    private T[] array;
    
    @SuppressWarnings("unchecked")
    public GenericArray(Class<T> clazz, int size) {
        array = (T[]) Array.newInstance(clazz, size);
    }
}
```

### 境界型パラメータの問題

##### エラー例
```java
// ❌ 境界型パラメータの誤用
public class NumberContainer<T extends Number> {
    private T value;
    
    public void multiplyBy(T factor) {
        this.value = this.value * factor; // コンパイルエラー
    }
}
```

##### エラーメッセージ
```
error: The operator * is undefined for the argument type(s) T, T
```

##### 対処法
```java
// ✅ 適切な境界型パラメータの使用
public class NumberContainer<T extends Number> {
    private T value;
    
    public void multiplyBy(double factor) {
        // Number型の共通メソッドを使用
        double result = this.value.doubleValue() * factor;
        // 結果の設定は型に応じて処理
    }
}
```

### var型推論の誤用

##### エラー例
```java
// ❌ var型推論の誤用
var list = new ArrayList(); // Raw typeになる
var value = null; // コンパイルエラー
```

##### エラーメッセージ
```
error: Cannot infer type for local variable value
```

##### 対処法
```java
// ✅ 適切なvar型推論の使用
var list = new ArrayList<String>(); // 型パラメータを明示
String value = null; // 明示的な型宣言
```

### 共通の対処戦略

1. コンパイル時の警告を無視しない： `-Xlint:unchecked`オプションを使用して詳細な警告を確認する
2. 型パラメータの制約を理解する： 何ができて何ができないかを明確に把握する
3. 適切な設計パターンを使用する： Factory Method、Builder、Strategyパターンなどの活用
4. IDEの支援を活用する： Eclipse、IntelliJ IDEAなどの自動補完とエラー検出機能を使用
5. 段階的なリファクタリング： 既存のraw typeコードを少しずつジェネリクスに移行する

## まとめ

本章では、Javaの型安全性を支える大切な機能であるジェネリクスと、それを補完するvar型推論について学びました。

- ジェネリクスは、クラスやメソッドが扱うデータ型をコンパイル時に指定し、型の安全性を保証するしくみである
- これにより、実行時エラーである`ClassCastException`を未然に防ぐことができる
- 自作のクラスやメソッドもジェネリックにすることで、再利用性を高めることができる
- 型制約やワイルドカードを使うことで、より柔軟で安全なメソッド設計が可能になる
- var型推論は、ジェネリクスの複雑な型宣言を簡潔にし、コードの可読性を向上させる強力な機能である
- 適切にvar型推論を活用することで、型安全性を保ちながら、より保守しやすいコードを書くことができる

コレクションを扱う際には、必ずジェネリクスを使い、必要に応じてvar型推論も活用して、その恩恵を最大限に活用することが、現代的なJavaプログラミングの基本です。

## 章末演習

### 演習課題へのアクセス
本章の演習課題は、GitHubリポジトリで提供されています。
`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter11/`

### 課題構成
- 本章の基本概念の理解確認
- 応用的な実装練習
- 実践的な総合問題

詳細な課題内容と実装のヒントは、各課題フォルダ内のREADME.mdを参照してください。

1. 基礎課題： ジェネリックペアクラス、コンテナクラス、ワイルドカード活用、var型推論の適用など、ジェネリクスの基本概念の実装
2. 発展課題： 高度なジェネリックメソッド、型変換ユーティリティの作成、var型推論を活用したリファクタリング
3. チャレンジ課題： 実用的なジェネリックライブラリの設計・実装

詳細な課題内容と実装のヒントは、GitHubリポジトリの各課題フォルダ内のREADME.mdを参照してください。

次のステップ： 基礎課題が完了したら、第12章「高度なコレクション操作とStream API」に進みましょう。