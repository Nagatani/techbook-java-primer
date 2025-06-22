# 第9章 ラムダ式と関数型インタフェース

## はじめに：プログラミングパラダイムの収束と関数型プログラミングの復活

前章までで、Javaにおけるオブジェクト指向プログラミングの核心技術について学習してきました。この章では、Java 8（2014年）で導入された革命的な機能である「ラムダ式（Lambda expressions）」と「関数型インターフェイス（Functional interfaces）」について詳細に学習していきます。

これらの機能は、単なる新しい構文の追加ではありません。これは、プログラミング言語の設計思想における重要な転換点を示しており、オブジェクト指向と関数型プログラミングの融合による、より表現力豊かで効率的なプログラミングスタイルの実現を目指したものです。

### プログラミングパラダイムの歴史的発展

プログラミング言語の歴史を振り返ると、複数のプログラミングパラダイム（programming paradigm）が並行して発展してきました。それぞれのパラダイムは、異なる問題領域や思考モデルに適した解決手法を提供してきました。

**手続き型プログラミング（1950年代〜）**：プログラムを一連の手順（procedure）として記述する手法で、FORTRAN、COBOL、Cなどの言語で実装されました。計算処理を段階的に記述することで、複雑な問題を解決可能な形に分解します。

**オブジェクト指向プログラミング（1960年代〜）**：データとそれを操作する手続きを一体化したオブジェクトを中心とする手法で、Simula、Smalltalk、C++、Javaなどで発展しました。現実世界のモデリングと大規模システムの構築に適しています。

**関数型プログラミング（1950年代〜）**：数学的な関数概念を基盤とし、計算を関数の組み合わせとして表現する手法で、LISP、ML、Haskell、Erlangなどで実装されました。宣言的なプログラミングスタイルと、副作用を排除した純粋性を重視します。

**論理型プログラミング（1970年代〜）**：論理的推論に基づいてプログラムを記述する手法で、Prologなどで実装されました。人工知能や知識表現の分野で活用されています。

### 関数型プログラミングの核心思想

関数型プログラミングは、数学における関数概念をプログラミングの基礎とする思想です。その核心的な原則は以下の通りです：

**第一級関数（First-class functions）**：関数を値として扱い、変数に代入したり、他の関数の引数として渡したり、戻り値として返したりできる仕組みです。これにより、高度な抽象化と柔軟なプログラム構造が実現できます。

**不変性（Immutability）**：一度作成されたデータは変更されることがなく、新しいデータは既存のデータから変換によって生成されます。これにより、プログラムの予測可能性と安全性が向上します。

**純粋関数（Pure functions）**：同じ入力に対して常に同じ出力を返し、副作用（外部状態の変更）を持たない関数です。テストが容易で、並行処理における安全性が保証されます。

**高階関数（Higher-order functions）**：関数を引数として受け取ったり、関数を戻り値として返したりする関数です。map、filter、reduceなどの操作により、データ変換を宣言的に記述できます。

### Java言語における関数型機能の必要性

Javaは、長期間にわたってオブジェクト指向パラダイムを中心として発展してきましたが、2010年代に入り、以下のような課題が顕在化しました：

**並行処理の複雑性**：マルチコアプロセッサの普及により、並行処理の重要性が高まりましたが、従来のオブジェクト指向的なアプローチでは、状態の共有と変更に伴う競合状態やデッドロックなどの問題が深刻化しました。

**ビッグデータ処理の需要**：大量のデータを効率的に処理するため、宣言的で高レベルな操作が求められるようになりました。従来のfor文による命令的な処理では、意図が不明確で、最適化も困難でした。

**API設計の制約**：インターフェイスに単一のメソッドしか定義できない場合でも、匿名クラスによる冗長な記述が必要で、コードの可読性と保守性が低下していました。

**関数型言語との競争**：Scala、Clojure、Kotlinなど、JVM上で動作する関数型の特徴を持つ言語が登場し、Javaの表現力不足が指摘されるようになりました。

### Java 8における関数型機能の導入

これらの課題に対応するため、Java 8では大規模な言語拡張が行われました。その中核となるのが、ラムダ式と関数型インターフェイスです：

**ラムダ式の導入**：匿名関数を簡潔に記述できる構文が追加され、関数をファーストクラスの値として扱えるようになりました。これにより、高階関数の使用が自然で読みやすい形で実現されました。

**関数型インターフェイスの標準化**：Function、Predicate、Consumer、Supplierなど、よく使用される関数型のパターンが標準ライブラリとして提供され、一貫した関数型プログラミングスタイルが可能になりました。

**Stream APIの導入**：コレクションに対する関数型操作を支援する強力なAPIが追加され、map、filter、reduceなどの関数型的な操作を効率的に実行できるようになりました。

**メソッド参照の導入**：既存のメソッドを関数として参照する簡潔な記法が追加され、コードの重複を避けながら可読性を向上させることができるようになりました。

### 関数型プログラミングとオブジェクト指向の融合

Java 8の革新性は、関数型プログラミングとオブジェクト指向プログラミングを対立するものとして扱うのではなく、相互補完的な関係として統合したことにあります：

**適材適所の活用**：データの構造化と状態管理にはオブジェクト指向を使用し、データの変換と操作には関数型を使用するという、ハイブリッドなアプローチが可能になりました。

**既存コードとの互換性**：ラムダ式は既存の関数型インターフェイスとシームレスに統合され、既存のAPIやライブラリを変更することなく、新しい機能を活用できます。

**段階的な学習曲線**：オブジェクト指向に慣れ親しんだ開発者が、段階的に関数型の概念を学習し、適用できる環境が整備されました。

### 現代的なソフトウェア開発における関数型の重要性

現代のソフトウェア開発において、関数型プログラミングの重要性はますます高まっています：

**リアクティブプログラミング**：非同期イベントストリームの処理において、関数型の操作が中核的な役割を果たしています。RxJava、Project Reactorなどのライブラリは、ラムダ式を前提とした設計になっています。

**クラウドネイティブ開発**：サーバーレスアーキテクチャやマイクロサービスでは、状態を持たない純粋関数の概念が、スケーラビリティと保守性の向上に貢献しています。

**データ分析と機械学習**：Apache Spark、Apache Beamなどのビッグデータ処理フレームワークでは、関数型の操作が標準的なプログラミングモデルとなっています。

**テスト駆動開発**：純粋関数はテストが容易で、モックやスタブの必要性が減少し、より信頼性の高いテストコードが作成できます。

### この章で学習する内容の意義

この章では、これらの歴史的背景と現代的な課題を踏まえて、Javaにおける関数型プログラミング機能を体系的に学習していきます。単にラムダ式の書き方を覚えるのではなく、以下の点を重視して学習を進めます：

**関数型思考の習得**：問題を関数の組み合わせとして捉え、宣言的にプログラムを記述する思考力を身につけます。

**適切な使い分け**：オブジェクト指向と関数型のそれぞれの長所を理解し、状況に応じて適切なパラダイムを選択する判断力を養います。

**Stream APIの実践的活用**：大量のデータを効率的に処理するためのStream操作を習得し、現代的なデータ処理技術の基礎を身につけます。

**非同期プログラミングの基礎**：CompletableFutureやリアクティブプログラミングライブラリとの連携により、非同期処理の基本概念を理解します。

**関数型設計パターン**：従来のGoFデザインパターンを関数型の観点から再解釈し、より簡潔で表現力豊かな設計手法を習得します。

関数型プログラミングを深く理解することは、Javaプログラマーとしての表現力を大幅に向上させ、現代的なソフトウェア開発手法への適応力を身につけることにつながります。この章を通じて、単なる「新しい構文」を超えて、「思考の新しい次元」を開拓していきましょう。

この章では、Java 8で導入されたラムダ式と関数型プログラミングについて学習します。C言語の関数ポインタとの比較も含めて、モダンなJavaプログラミング手法を習得しましょう。

## 9.1 ラムダ式とは

### C言語の関数ポインタとの比較

```c
// C言語の関数ポインタ
int add(int a, int b) {
    return a + b;
}

int (*operation)(int, int) = add;
int result = operation(5, 3);
```

```java
// Javaのラムダ式
BinaryOperator<Integer> operation = (a, b) -> a + b;
int result = operation.apply(5, 3);
```

### 従来の匿名クラス vs ラムダ式

```java
import java.util.*;

public class LambdaComparison {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("田中", "佐藤", "鈴木", "高橋");
        
        // 従来の匿名クラス
        Collections.sort(names, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareTo(s2);
            }
        });
        
        // ラムダ式
        Collections.sort(names, (s1, s2) -> s1.compareTo(s2));
        
        // メソッド参照（さらに簡潔）
        Collections.sort(names, String::compareTo);
    }
}
```

## 9.2 関数型インタフェース

### 標準の関数型インタフェース

```java
import java.util.function.*;

public class FunctionalInterfaceExample {
    public static void main(String[] args) {
        // Predicate<T>: T -> boolean
        Predicate<String> isEmpty = s -> s.isEmpty();
        Predicate<Integer> isEven = n -> n % 2 == 0;
        
        System.out.println(isEmpty.test(""));      // true
        System.out.println(isEven.test(4));       // true
        
        // Function<T, R>: T -> R
        Function<String, Integer> stringLength = s -> s.length();
        Function<Integer, String> intToString = n -> String.valueOf(n);
        
        System.out.println(stringLength.apply("Hello"));  // 5
        System.out.println(intToString.apply(123));        // "123"
        
        // Consumer<T>: T -> void
        Consumer<String> printer = s -> System.out.println(s);
        Consumer<Integer> doubler = n -> System.out.println(n * 2);
        
        printer.accept("Hello World");
        doubler.accept(5);  // 10
        
        // Supplier<T>: () -> T
        Supplier<Double> randomValue = () -> Math.random();
        Supplier<String> greeting = () -> "こんにちは";
        
        System.out.println(randomValue.get());
        System.out.println(greeting.get());
        
        // BinaryOperator<T>: (T, T) -> T
        BinaryOperator<Integer> add = (a, b) -> a + b;
        BinaryOperator<String> concat = (s1, s2) -> s1 + s2;
        
        System.out.println(add.apply(10, 5));        // 15
        System.out.println(concat.apply("Java", " 8")); // "Java 8"
    }
}
```

### カスタム関数型インタフェース

```java
@FunctionalInterface
public interface MathOperation {
    double calculate(double a, double b);
}

@FunctionalInterface
public interface StringProcessor {
    String process(String input);
}

public class CustomFunctionalInterface {
    public static void main(String[] args) {
        // カスタム関数型インターフェースの使用
        MathOperation addition = (a, b) -> a + b;
        MathOperation multiplication = (a, b) -> a * b;
        MathOperation power = (a, b) -> Math.pow(a, b);
        
        System.out.println(addition.calculate(5, 3));        // 8.0
        System.out.println(multiplication.calculate(4, 7));  // 28.0
        System.out.println(power.calculate(2, 3));           // 8.0
        
        StringProcessor upperCase = s -> s.toUpperCase();
        StringProcessor reverse = s -> new StringBuilder(s).reverse().toString();
        StringProcessor addPrefix = s -> "【重要】" + s;
        
        System.out.println(upperCase.process("hello"));      // "HELLO"
        System.out.println(reverse.process("Java"));         // "avaJ"
        System.out.println(addPrefix.process("お知らせ"));    // "【重要】お知らせ"
    }
}
```

## 9.3 メソッド参照

### 各種メソッド参照

```java
import java.util.*;
import java.util.function.*;

public class MethodReferenceExample {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("apple", "banana", "cherry");
        
        // 静的メソッド参照
        words.forEach(System.out::println);
        
        // インスタンスメソッド参照（特定のオブジェクト）
        String prefix = "Fruit: ";
        Function<String, String> addPrefix = prefix::concat;
        
        // インスタンスメソッド参照（任意のオブジェクト）
        Function<String, String> toUpper = String::toUpperCase;
        Function<String, Integer> getLength = String::length;
        
        words.stream()
             .map(String::toUpperCase)  // メソッド参照
             .forEach(System.out::println);
        
        // コンストラクタ参照
        Supplier<List<String>> listSupplier = ArrayList::new;
        Function<String, StringBuilder> sbBuilder = StringBuilder::new;
        
        List<String> newList = listSupplier.get();
        StringBuilder sb = sbBuilder.apply("Hello");
    }
    
    // 静的メソッドの例
    public static void printWithPrefix(String s) {
        System.out.println("Value: " + s);
    }
}
```

## 9.4 高階関数

### 関数を引数として受け取る

```java
import java.util.*;
import java.util.function.*;

public class HigherOrderFunction {
    
    // 高階関数：関数を引数として受け取る
    public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (T item : list) {
            if (predicate.test(item)) {
                result.add(item);
            }
        }
        return result;
    }
    
    public static <T, R> List<R> map(List<T> list, Function<T, R> mapper) {
        List<R> result = new ArrayList<>();
        for (T item : list) {
            result.add(mapper.apply(item));
        }
        return result;
    }
    
    public static <T> T reduce(List<T> list, T identity, BinaryOperator<T> accumulator) {
        T result = identity;
        for (T item : list) {
            result = accumulator.apply(result, item);
        }
        return result;
    }
    
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 偶数のフィルタリング
        List<Integer> evenNumbers = filter(numbers, n -> n % 2 == 0);
        System.out.println("偶数: " + evenNumbers);
        
        // 2倍にマッピング
        List<Integer> doubled = map(numbers, n -> n * 2);
        System.out.println("2倍: " + doubled);
        
        // 合計の計算
        Integer sum = reduce(numbers, 0, (a, b) -> a + b);
        System.out.println("合計: " + sum);
        
        // チェイニング
        List<String> result = map(
            filter(numbers, n -> n % 2 == 0),
            n -> "Number: " + n
        );
        System.out.println("結果: " + result);
    }
}
```

### 関数を返す関数

```java
import java.util.function.*;

public class FunctionFactory {
    
    // 関数を返す関数
    public static Function<Integer, Integer> multiplier(int factor) {
        return x -> x * factor;
    }
    
    public static Predicate<String> lengthChecker(int minLength) {
        return s -> s.length() >= minLength;
    }
    
    public static Function<String, String> decorator(String prefix, String suffix) {
        return s -> prefix + s + suffix;
    }
    
    public static void main(String[] args) {
        // 乗算器の作成
        Function<Integer, Integer> doubler = multiplier(2);
        Function<Integer, Integer> tripler = multiplier(3);
        
        System.out.println(doubler.apply(5));  // 10
        System.out.println(tripler.apply(4));  // 12
        
        // 長さチェッカーの作成
        Predicate<String> longEnough = lengthChecker(5);
        
        System.out.println(longEnough.test("Hello"));     // true
        System.out.println(longEnough.test("Hi"));        // false
        
        // デコレーターの作成
        Function<String, String> htmlTag = decorator("<h1>", "</h1>");
        Function<String, String> bracket = decorator("[", "]");
        
        System.out.println(htmlTag.apply("タイトル"));      // "<h1>タイトル</h1>"
        System.out.println(bracket.apply("重要"));         // "[重要]"
    }
}
```

## 9.5 関数の合成

```java
import java.util.function.*;

public class FunctionComposition {
    public static void main(String[] args) {
        Function<String, String> addHello = s -> "Hello " + s;
        Function<String, String> addExclamation = s -> s + "!";
        Function<String, String> toUpper = String::toUpperCase;
        
        // andThen: f.andThen(g) = g(f(x))
        Function<String, String> greetingComposed = addHello
            .andThen(addExclamation)
            .andThen(toUpper);
        
        System.out.println(greetingComposed.apply("World"));  // "HELLO WORLD!"
        
        // compose: f.compose(g) = f(g(x))
        Function<String, Integer> getLength = String::length;
        Function<Integer, Integer> doubleValue = x -> x * 2;
        
        Function<String, Integer> lengthDoubler = doubleValue.compose(getLength);
        System.out.println(lengthDoubler.apply("Hello"));  // 10
        
        // Predicateの合成
        Predicate<String> isLong = s -> s.length() > 5;
        Predicate<String> startsWithA = s -> s.startsWith("A");
        
        Predicate<String> longAndStartsWithA = isLong.and(startsWithA);
        Predicate<String> shortOrStartsWithA = isLong.negate().or(startsWithA);
        
        System.out.println(longAndStartsWithA.test("Application"));  // true
        System.out.println(shortOrStartsWithA.test("App"));          // true
    }
}
```

## 9.6 実践的な例

### イベント処理システム

```java
import java.util.*;
import java.util.function.*;

class Event {
    private String type;
    private String message;
    private long timestamp;
    
    public Event(String type, String message) {
        this.type = type;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getType() { return type; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
    
    @Override
    public String toString() {
        return String.format("[%s] %s (%d)", type, message, timestamp);
    }
}

public class EventProcessor {
    private List<Consumer<Event>> handlers = new ArrayList<>();
    
    public void addHandler(Consumer<Event> handler) {
        handlers.add(handler);
    }
    
    public void addConditionalHandler(Predicate<Event> condition, Consumer<Event> handler) {
        handlers.add(event -> {
            if (condition.test(event)) {
                handler.accept(event);
            }
        });
    }
    
    public void processEvent(Event event) {
        handlers.forEach(handler -> handler.accept(event));
    }
    
    public static void main(String[] args) {
        EventProcessor processor = new EventProcessor();
        
        // 全イベントをログに記録
        processor.addHandler(event -> 
            System.out.println("LOG: " + event));
        
        // エラーイベントのみ特別処理
        processor.addConditionalHandler(
            event -> "ERROR".equals(event.getType()),
            event -> System.err.println("ERROR ALERT: " + event.getMessage())
        );
        
        // 情報イベントをファイルに保存（模擬）
        processor.addConditionalHandler(
            event -> "INFO".equals(event.getType()),
            event -> System.out.println("SAVED TO FILE: " + event.getMessage())
        );
        
        // テストイベント
        processor.processEvent(new Event("INFO", "システム開始"));
        processor.processEvent(new Event("ERROR", "データベース接続エラー"));
        processor.processEvent(new Event("DEBUG", "デバッグ情報"));
    }
}
```

### 計算パイプライン

```java
import java.util.function.*;

public class CalculationPipeline {
    
    public static class Pipeline<T> {
        private Function<T, T> function;
        
        public Pipeline(Function<T, T> function) {
            this.function = function;
        }
        
        public Pipeline<T> then(Function<T, T> next) {
            return new Pipeline<>(function.andThen(next));
        }
        
        public T execute(T input) {
            return function.apply(input);
        }
    }
    
    public static void main(String[] args) {
        // 数値処理パイプライン
        Pipeline<Integer> numberPipeline = new Pipeline<Integer>(x -> x)
            .then(x -> x + 10)      // 10を加算
            .then(x -> x * 2)       // 2倍
            .then(x -> x - 5);      // 5を減算
        
        System.out.println(numberPipeline.execute(5));  // ((5+10)*2)-5 = 25
        
        // 文字列処理パイプライン
        Pipeline<String> stringPipeline = new Pipeline<String>(s -> s)
            .then(String::trim)
            .then(String::toLowerCase)
            .then(s -> s.replaceAll("\\s+", "_"))
            .then(s -> "processed_" + s);
        
        String result = stringPipeline.execute("  Hello World  ");
        System.out.println(result);  // "processed_hello_world"
    }
}
```

## 9.7 練習問題

1. 整数のリストを受け取り、偶数のみを抽出して2倍にし、降順でソートする関数を作成してください。

2. 文字列の検証を行う関数を組み合わせて、複合的な検証ルールを作成してください。

3. 簡単な電卓アプリケーションを作成し、演算を関数として渡せるようにしてください。

## 9.8 Recordの活用（Java 16以降）

Java 16から正式に導入されたRecordは、データを格納するためのシンプルなクラスを簡単に定義できる機能です。ラムダ式と組み合わせることで、よりモダンで簡潔なJavaコードを書くことができます。

### Recordの基本

```java
// 従来のクラスでのデータ保持
class Employee {
    private final String name;
    private final int age;
    private final String department;
    
    public Employee(String name, int age, String department) {
        this.name = name;
        this.age = age;
        this.department = department;
    }
    
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getDepartment() { return department; }
    
    @Override
    public boolean equals(Object obj) {
        // 省略...
    }
    
    @Override
    public int hashCode() {
        // 省略...
    }
    
    @Override
    public String toString() {
        return "Employee{name='" + name + "', age=" + age + ", department='" + department + "'}";
    }
}

// Recordでのデータ保持
public record Employee(String name, int age, String department) {
    // コンストラクタ、アクセサメソッド、equals、hashCode、toStringが自動生成される
}

public class RecordBasicExample {
    public static void main(String[] args) {
        Employee emp = new Employee("Alice", 25, "IT");
        
        // アクセサメソッドはフィールド名と同じ
        System.out.println("名前: " + emp.name());
        System.out.println("年齢: " + emp.age());
        System.out.println("部署: " + emp.department());
        
        // toStringが自動生成される
        System.out.println(emp); // Employee[name=Alice, age=25, department=IT]
        
        // 不変オブジェクトなので、新しいインスタンスを作成
        Employee emp2 = new Employee("Bob", 30, "Sales");
        System.out.println(emp2);
    }
}
```

### Recordの特徴とメリット

1. **ボイラープレートコードの削減**: コンストラクタ、getter、equals、hashCode、toStringが自動生成
2. **不変性**: 一度作成されたオブジェクトの値は変更不可
3. **スレッドセーフ**: 不変オブジェクトなのでマルチスレッド環境でも安全

### Recordとラムダ式の組み合わせ

```java
import java.util.*;
import java.util.stream.*;

public record Person(String name, int age, String city) {}

public class RecordWithLambdaExample {
    public static void main(String[] args) {
        List<Person> people = Arrays.asList(
            new Person("Alice", 25, "Tokyo"),
            new Person("Bob", 30, "Osaka"),
            new Person("Charlie", 35, "Tokyo"),
            new Person("Dave", 40, "Nagoya"),
            new Person("Eve", 45, "Tokyo")
        );
        
        // 30歳以上の人を抽出
        List<Person> adults = people.stream()
            .filter(person -> person.age() >= 30)
            .collect(Collectors.toList());
        
        System.out.println("30歳以上:");
        adults.forEach(System.out::println);
        
        // 都市別にグループ化
        Map<String, List<Person>> byCity = people.stream()
            .collect(Collectors.groupingBy(Person::city));
        
        System.out.println("\n都市別グループ:");
        byCity.forEach((city, persons) -> {
            System.out.println(city + ": " + persons.size() + "人");
            persons.forEach(p -> System.out.println("  " + p.name() + "(" + p.age() + ")"));
        });
        
        // 平均年齢を計算
        double averageAge = people.stream()
            .mapToInt(Person::age)
            .average()
            .orElse(0.0);
        
        System.out.println("\n平均年齢: " + averageAge);
        
        // 名前をアルファベット順にソート
        List<String> sortedNames = people.stream()
            .map(Person::name)
            .sorted()
            .collect(Collectors.toList());
        
        System.out.println("\nソートされた名前: " + sortedNames);
    }
}
```

### RecordでのCSVデータ処理

```java
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public record CsvPerson(String name, int age, String city) {
    // バリデーションを追加することも可能
    public CsvPerson {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("名前は必須です");
        }
        if (age < 0) {
            throw new IllegalArgumentException("年齢は0以上である必要があります");
        }
    }
    
    // カスタムメソッドを追加できる
    public boolean isAdult() {
        return age >= 20;
    }
    
    public String getDisplayName() {
        return name + "(" + age + "歳)";
    }
}

public class CsvRecordProcessor {
    
    public static List<CsvPerson> readCsvFile(String filename) {
        try {
            return Files.lines(Paths.get(filename))
                .map(line -> line.split(","))
                .filter(fields -> fields.length == 3)
                .map(fields -> new CsvPerson(
                    fields[0].trim(),
                    Integer.parseInt(fields[1].trim()),
                    fields[2].trim()
                ))
                .collect(Collectors.toList());
        } catch (IOException e) {
            System.err.println("ファイル読み込みエラー: " + e.getMessage());
            return Collections.emptyList();
        }
    }
    
    public static void main(String[] args) {
        // CSVデータの例: "Alice,25,Tokyo"
        // 実際にはファイルから読み込み
        List<CsvPerson> people = Arrays.asList(
            new CsvPerson("Alice", 25, "Tokyo"),
            new CsvPerson("Bob", 17, "Osaka"),
            new CsvPerson("Charlie", 35, "Tokyo"),
            new CsvPerson("Dave", 16, "Nagoya")
        );
        
        System.out.println("全データ:");
        people.forEach(System.out::println);
        
        // 成人のみを抽出
        System.out.println("\n成人のみ:");
        people.stream()
              .filter(CsvPerson::isAdult)
              .forEach(person -> System.out.println(person.getDisplayName()));
        
        // 都市別統計
        System.out.println("\n都市別人数:");
        people.stream()
              .collect(Collectors.groupingBy(CsvPerson::city, Collectors.counting()))
              .forEach((city, count) -> System.out.println(city + ": " + count + "人"));
    }
}
```

### Recordと関数型インターフェイスの活用

```java
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public record Product(String name, double price, String category) {
    
    public boolean isExpensive() {
        return price > 1000.0;
    }
    
    public Product withDiscount(double percentage) {
        double discountedPrice = price * (1.0 - percentage / 100.0);
        return new Product(name, discountedPrice, category);
    }
}

public class ProductProcessor {
    
    // 商品をフィルタリングする関数
    public static List<Product> filterProducts(List<Product> products, Predicate<Product> condition) {
        return products.stream()
                      .filter(condition)
                      .collect(Collectors.toList());
    }
    
    // 商品を変換する関数
    public static List<Product> transformProducts(List<Product> products, Function<Product, Product> transformer) {
        return products.stream()
                      .map(transformer)
                      .collect(Collectors.toList());
    }
    
    public static void main(String[] args) {
        List<Product> products = Arrays.asList(
            new Product("ノートPC", 80000.0, "電子機器"),
            new Product("マウス", 2000.0, "電子機器"),
            new Product("本", 1500.0, "書籍"),
            new Product("コーヒー", 500.0, "飲料"),
            new Product("スマートフォン", 120000.0, "電子機器")
        );
        
        System.out.println("全商品:");
        products.forEach(System.out::println);
        
        // 高額商品の抽出
        List<Product> expensiveProducts = filterProducts(products, Product::isExpensive);
        System.out.println("\n高額商品 (1000円以上):");
        expensiveProducts.forEach(System.out::println);
        
        // 電子機器の抽出
        List<Product> electronics = filterProducts(products, 
            product -> "電子機器".equals(product.category()));
        System.out.println("\n電子機器:");
        electronics.forEach(System.out::println);
        
        // 20%オフのセールを適用
        List<Product> saleProducts = transformProducts(products, 
            product -> product.withDiscount(20.0));
        System.out.println("\n20%オフセール価格:");
        saleProducts.forEach(System.out::println);
        
        // カテゴリ別平均価格
        Map<String, Double> averagePriceByCategory = products.stream()
            .collect(Collectors.groupingBy(
                Product::category,
                Collectors.averagingDouble(Product::price)
            ));
        
        System.out.println("\nカテゴリ別平均価格:");
        averagePriceByCategory.forEach((category, avgPrice) -> 
            System.out.println(category + ": " + String.format("%.0f円", avgPrice)));
        
        // 最も高い商品
        products.stream()
                .max(Comparator.comparingDouble(Product::price))
                .ifPresent(product -> 
                    System.out.println("\n最高額商品: " + product));
    }
}
```

### Recordの制約と注意点

1. **継承不可**: Recordは他のクラスを継承できない
2. **変更不可**: フィールドの値は変更できない
3. **フィールドの依存**: フィールドが可変オブジェクトの場合、Record自体は不変でも内部データが変更される可能性がある

```java
import java.util.*;

// 注意: Listは可変オブジェクト
public record TeamRecord(String name, List<String> members) {
    // コピーを作成して不変性を保証
    public TeamRecord(String name, List<String> members) {
        this.name = name;
        this.members = List.copyOf(members); // 不変リストとしてコピー
    }
    
    public List<String> members() {
        return members; // これは既に不変リストなので安全
    }
}

public class RecordImmutabilityExample {
    public static void main(String[] args) {
        List<String> memberList = new ArrayList<>();
        memberList.add("Alice");
        memberList.add("Bob");
        
        TeamRecord team = new TeamRecord("開発チーム", memberList);
        
        // 元のリストを変更してもRecord内のデータは影響を受けない
        memberList.add("Charlie");
        
        System.out.println("チームメンバー: " + team.members()); // [Alice, Bob]
        
        // Recordから取得したリストは不変
        // team.members().add("Dave"); // UnsupportedOperationException
    }
}
```

## まとめ

この章では、ラムダ式と関数型プログラミングの基礎、そしてモダンJavaのRecord機能を学習しました。関数型インタフェース、メソッド参照、高階関数、関数の合成などの概念と、Recordによる簡潔なデータクラスの作成を組み合わせることで、より簡潔で読みやすく、保守性の高いモダンJavaコードを書けるようになりました。

Recordは特にStream APIやラムダ式と組み合わせることで、データ処理や集計操作を非常に簡潔に記述でき、ボイラープレートコードを大幅に削減しながら型安全性と不変性を保証できます。