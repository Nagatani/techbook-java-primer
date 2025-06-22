# 第10章 Stream API

## はじめに：データ処理パラダイムの革命とStream APIの意義

前章でラムダ式と関数型プログラミングの基礎について学習しました。この章では、Java 8で同時に導入され、関数型プログラミングの威力を具体的に体験できる「Stream API」について詳細に学習していきます。

Stream APIは、単なる新しいライブラリの追加ではありません。これは、データ処理に対する従来のアプローチを根本的に変革し、より宣言的で理解しやすく、かつ高性能なプログラムの作成を可能にする、画期的な技術革新です。

### データ処理の歴史的変遷

コンピュータプログラミングの本質は「データの処理」です。入力されたデータを変換、集約、フィルタリングして、有用な情報を抽出することが、ほぼすべてのプログラムの基本的な役割です。この処理方法は、コンピュータサイエンスの発展とともに段階的に進化してきました。

**初期のデータ処理（1950年代〜1960年代）**：初期のプログラムでは、データ処理は主に配列とループを使用した命令的な手法で行われていました。プログラマーは、「どのようにデータを処理するか」の詳細な手順をすべて記述する必要がありました。

```fortran
DO 10 I = 1, N
   IF (ARRAY(I) .GT. THRESHOLD) THEN
      RESULT(J) = ARRAY(I) * 2
      J = J + 1
   ENDIF
10 CONTINUE
```

**構造化プログラミング時代（1970年代〜1980年代）**：C言語などの構造化プログラミング言語により、関数を使用したデータ処理の抽象化が進みました。しかし、依然として命令的なアプローチが主流でした。

```c
for (int i = 0; i < size; i++) {
    if (data[i] > threshold) {
        result[count++] = data[i] * 2;
    }
}
```

**オブジェクト指向時代（1990年代〜2000年代）**：Javaのようなオブジェクト指向言語では、データ構造（コレクション）と処理ロジックが組み合わされましたが、データ変換の記述は依然として命令的でした。

```java
List<Integer> result = new ArrayList<>();
for (Integer item : data) {
    if (item > threshold) {
        result.add(item * 2);
    }
}
```

### 命令的データ処理の問題点

従来の命令的データ処理アプローチには、以下のような深刻な問題がありました：

**可読性の低下**：「何をしたいか」よりも「どのようにするか」に焦点が当たるため、プログラムの意図が不明確になりがちでした。特に複雑なデータ変換では、本来の目的がコードの詳細に埋もれてしまいました。

**再利用性の欠如**：似たような処理パターン（フィルタリング、マッピング、集約など）であっても、毎回一から実装する必要があり、コードの重複が発生しやすい状況でした。

**エラーの多発**：ループのインデックス管理、一時変数の状態管理、null チェックなど、処理の本質とは関係ない詳細でバグが発生しやすく、保守性が低下していました。

**並行処理の困難さ**：マルチコアプロセッサの普及により並行処理の重要性が高まりましたが、従来の命令的アプローチでは、安全で効率的な並行処理の実装が極めて困難でした。

**最適化の限界**：コンパイラや実行時システムによる自動最適化が困難で、特にループ処理の効率化には限界がありました。

### 関数型データ処理の思想

これらの問題を解決するため、関数型プログラミングの分野では、数学的な関数概念に基づく宣言的なデータ処理手法が研究されてきました。

**高階関数による抽象化**：map、filter、reduceなどの高階関数により、よく使用されるデータ処理パターンを抽象化し、再利用可能な形で提供することが可能になりました。

**遅延評価（Lazy Evaluation）**：必要になるまで計算を延期することで、無限のデータストリームや大量のデータを効率的に処理できるようになりました。

**関数の合成（Function Composition）**：単純な関数を組み合わせて複雑な処理を構築することで、モジュラーで理解しやすいプログラムが作成できるようになりました。

**不変性の活用**：元のデータを変更することなく新しいデータを生成することで、副作用を排除し、並行処理の安全性を確保できるようになりました。

### 他言語におけるStream処理の発展

Java 8のStream APIは、他のプログラミング言語における先進的な取り組みを参考にして設計されました：

**LISP（1950年代〜）**：リスト処理の分野で、map、filter、reduceなどの概念が初めて導入されました。関数型データ処理の理論的基盤を提供しました。

**Haskell（1990年〜）**：純粋関数型言語として、遅延評価と無限リストの概念を確立しました。効率的で表現力豊かなデータ処理の手法を実現しました。

**Scala（2003年〜）**：JVM上で動作する関数型言語として、Java開発者にとって身近な環境で関数型データ処理の利点を示しました。

**C# LINQ（2007年〜）**：.NET Frameworkに統合された言語統合クエリとして、オブジェクト指向言語における関数型データ処理の成功例を提供しました。

**Python（list comprehensions）**：簡潔で読みやすい構文により、関数型データ処理の普及に貢献しました。

### Java 8 Stream APIの革新性

Java 8のStream APIは、これらの先進的な概念をJavaエコシステムに統合し、以下の革新的な特徴を実現しました：

**既存コレクションとの統合**：既存のList、Set、MapなどのコレクションからシームレスにStreamを生成でき、既存のコードベースへの影響を最小限に抑えながら新機能を導入できます。

**型安全性の保証**：Javaの強い型システムとの統合により、コンパイル時に多くのエラーを検出でき、実行時の安全性が向上しています。

**並行処理の自動化**：parallelStream()により、複雑な並行処理ロジックを記述することなく、マルチコア処理の恩恵を享受できます。

**遅延評価の実装**：中間操作（intermediate operations）は遅延評価され、終端操作（terminal operations）が呼ばれるまで実際の処理は実行されません。これにより、効率的なデータ処理が実現されています。

**専用プリミティブ型ストリーム**：IntStream、LongStream、DoubleStreamにより、ボクシング/アンボクシングのオーバーヘッドを回避し、高性能な数値処理が可能になっています。

### ビッグデータ時代におけるStream処理の重要性

現代のソフトウェア開発において、Stream処理の重要性はますます高まっています：

**ビッグデータ処理**：Apache Spark、Apache Flinkなどの分散処理フレームワークでは、Stream APIと類似した概念が中核的な役割を果たしています。Javaでの基礎学習が、これらの先進技術の理解につながります。

**リアルタイムデータ処理**：IoT、ソーシャルメディア、金融取引など、リアルタイムでのデータ処理が求められる分野では、Stream処理が標準的なアプローチとなっています。

**マイクロサービスアーキテクチャ**：サービス間でのデータ変換や集約において、Stream処理による宣言的なアプローチが可読性と保守性の向上に貢献しています。

**機械学習とデータサイエンス**：データの前処理、特徴抽出、結果の集約など、機械学習のパイプラインにおいてStream処理の概念が広く活用されています。

### 関数型思考への転換

Stream APIを効果的に活用するためには、従来の命令的思考から関数型思考への転換が重要です：

**What（何を）からHow（どのように）へ**：「ループをどう書くか」ではなく「データをどう変換したいか」に焦点を当てることで、より本質的な問題解決に集中できます。

**データフローの視覚化**：データが一連の変換を通じて流れていく様子を視覚的に理解することで、複雑な処理も直感的に把握できるようになります。

**関数の組み合わせ**：単純な操作を組み合わせて複雑な処理を構築する思考パターンにより、モジュラーで拡張性の高いプログラムが作成できます。

### この章で学習する内容の意義

この章では、これらの歴史的背景と技術的意義を踏まえて、Java Stream APIを体系的に学習していきます。単にAPIの使い方を覚えるのではなく、以下の点を重視して学習を進めます：

**宣言的プログラミングの習得**：「何をしたいか」を明確に表現するプログラミングスタイルを身につけ、コードの可読性と保守性を向上させます。

**効率的なデータ処理技術**：大量のデータを効率的に処理するための技術を習得し、現代的なアプリケーション開発の基礎を築きます。

**並行処理の理解**：Stream APIの並行処理機能を通じて、マルチコア時代のプログラミング技術を理解します。

**関数型設計パターン**：map-filter-reduceパターンなど、関数型プログラミングの基本的な設計パターンを習得します。

**現代技術への橋渡し**：ビッグデータ処理、リアクティブプログラミング、クラウドネイティブ開発など、現代的な技術領域への基礎知識を身につけます。

Stream APIを深く理解することは、Javaプログラマーとしての表現力を大幅に向上させ、データドリブンな現代のソフトウェア開発において不可欠な技術を身につけることにつながります。この章を通じて、単なる「新しいライブラリの使い方」を超えて、「データ処理に対する新しい思考方法」を習得していきましょう。

この章では、Java 8で導入されたStream APIについて学習します。コレクションの処理を関数型プログラミングスタイルで行う強力な機能を習得しましょう。

## 10.1 Stream APIとは

### 従来の方法 vs Stream API

```java
import java.util.*;
import java.util.stream.*;

public class StreamComparison {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 従来の方法：偶数を抽出して2倍にして合計を求める
        List<Integer> evenNumbers = new ArrayList<>();
        for (Integer num : numbers) {
            if (num % 2 == 0) {
                evenNumbers.add(num);
            }
        }
        
        List<Integer> doubled = new ArrayList<>();
        for (Integer num : evenNumbers) {
            doubled.add(num * 2);
        }
        
        int sum = 0;
        for (Integer num : doubled) {
            sum += num;
        }
        System.out.println("従来の方法: " + sum);
        
        // Stream API
        int streamSum = numbers.stream()
            .filter(n -> n % 2 == 0)    // 偶数をフィルタ
            .map(n -> n * 2)            // 2倍にマップ
            .mapToInt(Integer::intValue) // IntStreamに変換
            .sum();                     // 合計
        System.out.println("Stream API: " + streamSum);
    }
}
```

## 10.2 Streamの作成

```java
import java.util.*;
import java.util.stream.*;

public class StreamCreation {
    public static void main(String[] args) {
        // コレクションから
        List<String> list = Arrays.asList("a", "b", "c");
        Stream<String> streamFromList = list.stream();
        
        // 配列から
        String[] array = {"x", "y", "z"};
        Stream<String> streamFromArray = Arrays.stream(array);
        
        // 直接値から
        Stream<String> streamOfValues = Stream.of("apple", "banana", "cherry");
        
        // 空のStream
        Stream<String> emptyStream = Stream.empty();
        
        // 無限Stream
        Stream<Integer> infiniteStream = Stream.iterate(0, n -> n + 2);
        Stream<Double> randomStream = Stream.generate(Math::random);
        
        // 範囲指定
        IntStream range = IntStream.range(1, 10);          // 1-9
        IntStream rangeClosed = IntStream.rangeClosed(1, 10); // 1-10
        
        // ファイルから
        try {
            Stream<String> lines = Files.lines(Paths.get("example.txt"));
        } catch (Exception e) {
            // ファイル処理
        }
        
        // 使用例
        infiniteStream.limit(5).forEach(System.out::println);  // 0, 2, 4, 6, 8
        range.forEach(System.out::println);  // 1-9
    }
}
```

## 10.3 中間操作（Intermediate Operations）

### filter - フィルタリング

```java
import java.util.*;
import java.util.stream.*;

public class FilterExample {
    public static void main(String[] args) {
        List<String> words = Arrays.asList(
            "apple", "banana", "cherry", "date", "elderberry"
        );
        
        // 長さが5文字以上の単語
        words.stream()
             .filter(w -> w.length() >= 5)
             .forEach(System.out::println);
        
        // 'a'で始まる単語
        words.stream()
             .filter(w -> w.startsWith("a"))
             .forEach(System.out::println);
        
        // 複数条件
        words.stream()
             .filter(w -> w.length() > 4)
             .filter(w -> w.contains("e"))
             .forEach(System.out::println);
    }
}
```

### map - 変換

```java
import java.util.*;
import java.util.stream.*;

public class MapExample {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("hello", "world", "java", "stream");
        
        // 大文字に変換
        words.stream()
             .map(String::toUpperCase)
             .forEach(System.out::println);
        
        // 長さに変換
        words.stream()
             .map(String::length)
             .forEach(System.out::println);
        
        // カスタム変換
        words.stream()
             .map(w -> "「" + w + "」")
             .forEach(System.out::println);
        
        // 数値処理
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        numbers.stream()
               .map(n -> n * n)  // 平方
               .forEach(System.out::println);
    }
}
```

### flatMap - フラット化

```java
import java.util.*;
import java.util.stream.*;

public class FlatMapExample {
    public static void main(String[] args) {
        List<List<String>> nestedList = Arrays.asList(
            Arrays.asList("a", "b"),
            Arrays.asList("c", "d", "e"),
            Arrays.asList("f")
        );
        
        // ネストしたリストをフラット化
        nestedList.stream()
                  .flatMap(List::stream)
                  .forEach(System.out::println);  // a, b, c, d, e, f
        
        // 文字列を文字に分割
        List<String> sentences = Arrays.asList("hello world", "java stream");
        sentences.stream()
                 .flatMap(sentence -> Arrays.stream(sentence.split(" ")))
                 .forEach(System.out::println);  // hello, world, java, stream
        
        // Optionalのフラット化
        List<Optional<String>> optionals = Arrays.asList(
            Optional.of("apple"),
            Optional.empty(),
            Optional.of("banana")
        );
        
        optionals.stream()
                 .flatMap(Optional::stream)
                 .forEach(System.out::println);  // apple, banana
    }
}
```

### sorted - ソート

```java
import java.util.*;
import java.util.stream.*;

public class SortedExample {
    static class Person {
        String name;
        int age;
        
        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
        
        @Override
        public String toString() {
            return name + "(" + age + ")";
        }
    }
    
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6);
        
        // 自然順序でソート
        numbers.stream()
               .sorted()
               .forEach(System.out::println);
        
        // 逆順でソート
        numbers.stream()
               .sorted(Comparator.reverseOrder())
               .forEach(System.out::println);
        
        List<Person> people = Arrays.asList(
            new Person("田中", 25),
            new Person("佐藤", 30),
            new Person("鈴木", 20)
        );
        
        // 年齢でソート
        people.stream()
              .sorted(Comparator.comparing(p -> p.age))
              .forEach(System.out::println);
        
        // 名前でソート
        people.stream()
              .sorted(Comparator.comparing(p -> p.name))
              .forEach(System.out::println);
        
        // 複数条件でソート
        people.stream()
              .sorted(Comparator.comparing((Person p) -> p.age)
                               .thenComparing(p -> p.name))
              .forEach(System.out::println);
    }
}
```

### distinct, limit, skip

```java
import java.util.*;
import java.util.stream.*;

public class StreamOperations {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 2, 3, 3, 3, 4, 5, 5);
        
        // 重複除去
        numbers.stream()
               .distinct()
               .forEach(System.out::println);  // 1, 2, 3, 4, 5
        
        // 最初の3つ
        numbers.stream()
               .limit(3)
               .forEach(System.out::println);  // 1, 2, 2
        
        // 最初の3つをスキップ
        numbers.stream()
               .skip(3)
               .forEach(System.out::println);  // 3, 3, 3, 4, 5, 5
        
        // 組み合わせ
        numbers.stream()
               .distinct()      // 重複除去
               .skip(2)         // 最初の2つをスキップ
               .limit(2)        // 2つまで
               .forEach(System.out::println);  // 3, 4
    }
}
```

## 10.4 終端操作（Terminal Operations）

### forEach, forEachOrdered

```java
import java.util.*;
import java.util.stream.*;

public class ForEachExample {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("apple", "banana", "cherry");
        
        // 各要素に処理を適用
        words.stream().forEach(System.out::println);
        
        // 並列処理でも順序を保証
        words.parallelStream().forEachOrdered(System.out::println);
        
        // 副作用のある処理
        List<String> result = new ArrayList<>();
        words.stream().forEach(result::add);
        System.out.println(result);
    }
}
```

### collect - コレクション収集

```java
import java.util.*;
import java.util.stream.*;
import static java.util.stream.Collectors.*;

public class CollectExample {
    static class Person {
        String name;
        int age;
        String department;
        
        Person(String name, int age, String department) {
            this.name = name;
            this.age = age;
            this.department = department;
        }
        
        // getters
        public String getName() { return name; }
        public int getAge() { return age; }
        public String getDepartment() { return department; }
        
        @Override
        public String toString() {
            return name + "(" + age + "," + department + ")";
        }
    }
    
    public static void main(String[] args) {
        List<Person> people = Arrays.asList(
            new Person("田中", 25, "開発"),
            new Person("佐藤", 30, "営業"),
            new Person("鈴木", 35, "開発"),
            new Person("高橋", 28, "営業")
        );
        
        // リストに収集
        List<String> names = people.stream()
                                  .map(Person::getName)
                                  .collect(toList());
        
        // セットに収集
        Set<String> departments = people.stream()
                                       .map(Person::getDepartment)
                                       .collect(toSet());
        
        // 文字列結合
        String nameList = people.stream()
                                .map(Person::getName)
                                .collect(joining(", "));
        
        // 部署でグループ化
        Map<String, List<Person>> byDepartment = people.stream()
                                                      .collect(groupingBy(Person::getDepartment));
        
        // 部署ごとの人数
        Map<String, Long> countByDepartment = people.stream()
                                                   .collect(groupingBy(Person::getDepartment, counting()));
        
        // 部署ごとの平均年齢
        Map<String, Double> avgAgeByDepartment = people.stream()
                                                      .collect(groupingBy(
                                                          Person::getDepartment,
                                                          averagingDouble(Person::getAge)
                                                      ));
        
        // 年齢で分割（30歳未満とそれ以上）
        Map<Boolean, List<Person>> partitioned = people.stream()
                                                      .collect(partitioningBy(p -> p.getAge() < 30));
        
        System.out.println("名前一覧: " + names);
        System.out.println("部署一覧: " + departments);
        System.out.println("名前結合: " + nameList);
        System.out.println("部署別: " + byDepartment);
        System.out.println("部署別人数: " + countByDepartment);
        System.out.println("部署別平均年齢: " + avgAgeByDepartment);
        System.out.println("年齢で分割: " + partitioned);
    }
}
```

### reduce - 削減操作

```java
import java.util.*;
import java.util.stream.*;

public class ReduceExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        // 合計
        Optional<Integer> sum = numbers.stream().reduce((a, b) -> a + b);
        System.out.println("合計: " + sum.orElse(0));
        
        // 初期値付きの合計
        Integer sumWithIdentity = numbers.stream().reduce(0, (a, b) -> a + b);
        System.out.println("合計（初期値付き）: " + sumWithIdentity);
        
        // 最大値
        Optional<Integer> max = numbers.stream().reduce(Integer::max);
        System.out.println("最大値: " + max.orElse(0));
        
        // 積
        Optional<Integer> product = numbers.stream().reduce((a, b) -> a * b);
        System.out.println("積: " + product.orElse(0));
        
        // 文字列の連結
        List<String> words = Arrays.asList("Java", "Stream", "API");
        String joined = words.stream().reduce("", (a, b) -> a + " " + b);
        System.out.println("連結: " + joined.trim());
        
        // 複雑な例：単語の長さの合計
        Integer totalLength = words.stream()
                                  .map(String::length)
                                  .reduce(0, Integer::sum);
        System.out.println("総文字数: " + totalLength);
    }
}
```

### find, match, count

```java
import java.util.*;
import java.util.stream.*;

public class TerminalOperations {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("apple", "banana", "cherry", "date");
        
        // 要素の検索
        Optional<String> first = words.stream()
                                     .filter(w -> w.startsWith("b"))
                                     .findFirst();
        System.out.println("最初のb開始: " + first.orElse("なし"));
        
        Optional<String> any = words.stream()
                                   .filter(w -> w.length() > 5)
                                   .findAny();
        System.out.println("5文字超の任意: " + any.orElse("なし"));
        
        // マッチング
        boolean anyMatch = words.stream().anyMatch(w -> w.startsWith("a"));
        boolean allMatch = words.stream().allMatch(w -> w.length() > 3);
        boolean noneMatch = words.stream().noneMatch(w -> w.startsWith("z"));
        
        System.out.println("a開始があるか: " + anyMatch);
        System.out.println("全て3文字超か: " + allMatch);
        System.out.println("z開始がないか: " + noneMatch);
        
        // カウント
        long count = words.stream()
                         .filter(w -> w.contains("a"))
                         .count();
        System.out.println("aを含む単語数: " + count);
        
        // 数値統計
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        IntSummaryStatistics stats = numbers.stream()
                                           .mapToInt(Integer::intValue)
                                           .summaryStatistics();
        
        System.out.println("統計: " + stats);
        System.out.println("平均: " + stats.getAverage());
        System.out.println("最大: " + stats.getMax());
        System.out.println("最小: " + stats.getMin());
    }
}
```

## 10.5 並列ストリーム

```java
import java.util.*;
import java.util.stream.*;

public class ParallelStreamExample {
    public static void main(String[] args) {
        List<Integer> numbers = IntStream.rangeClosed(1, 1000000)
                                        .boxed()
                                        .collect(Collectors.toList());
        
        // シーケンシャル処理
        long startTime = System.currentTimeMillis();
        long sum1 = numbers.stream()
                          .mapToLong(Integer::longValue)
                          .sum();
        long endTime = System.currentTimeMillis();
        System.out.println("シーケンシャル: " + (endTime - startTime) + "ms");
        
        // 並列処理
        startTime = System.currentTimeMillis();
        long sum2 = numbers.parallelStream()
                          .mapToLong(Integer::longValue)
                          .sum();
        endTime = System.currentTimeMillis();
        System.out.println("並列: " + (endTime - startTime) + "ms");
        
        System.out.println("結果は同じ: " + (sum1 == sum2));
        
        // 並列処理での注意点
        List<Integer> results = new ArrayList<>();
        
        // 危険：非同期安全でない操作
        // numbers.parallelStream().forEach(results::add);  // データ競合の可能性
        
        // 安全：collect使用
        List<Integer> safeResults = numbers.parallelStream()
                                          .filter(n -> n % 2 == 0)
                                          .collect(Collectors.toList());
    }
}
```

## 10.6 実践的な例

### ファイル処理

```java
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class FileProcessingExample {
    public static void main(String[] args) throws Exception {
        // サンプルファイルの作成
        List<String> lines = Arrays.asList(
            "apple,100,fruit",
            "banana,80,fruit",
            "carrot,60,vegetable",
            "date,120,fruit",
            "eggplant,90,vegetable"
        );
        
        Path tempFile = Files.createTempFile("sample", ".csv");
        Files.write(tempFile, lines);
        
        // ファイルの読み込みと処理
        Map<String, Double> avgPriceByCategory = Files.lines(tempFile)
            .map(line -> line.split(","))
            .collect(Collectors.groupingBy(
                parts -> parts[2],  // カテゴリでグループ
                Collectors.averagingDouble(parts -> Double.parseDouble(parts[1]))
            ));
        
        System.out.println("カテゴリ別平均価格: " + avgPriceByCategory);
        
        // 高価な果物の検索
        List<String> expensiveFruits = Files.lines(tempFile)
            .map(line -> line.split(","))
            .filter(parts -> "fruit".equals(parts[2]))
            .filter(parts -> Double.parseDouble(parts[1]) > 90)
            .map(parts -> parts[0])
            .collect(Collectors.toList());
        
        System.out.println("高価な果物: " + expensiveFruits);
        
        // クリーンアップ
        Files.delete(tempFile);
    }
}
```

### データ分析

```java
import java.util.*;
import java.util.stream.*;
import static java.util.stream.Collectors.*;

public class DataAnalysisExample {
    static class Sale {
        String product;
        String region;
        int quantity;
        double price;
        
        Sale(String product, String region, int quantity, double price) {
            this.product = product;
            this.region = region;
            this.quantity = quantity;
            this.price = price;
        }
        
        double getRevenue() { return quantity * price; }
        
        // getters
        public String getProduct() { return product; }
        public String getRegion() { return region; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }
        
        @Override
        public String toString() {
            return String.format("%s(%s): %d×%.2f=%.2f", 
                               product, region, quantity, price, getRevenue());
        }
    }
    
    public static void main(String[] args) {
        List<Sale> sales = Arrays.asList(
            new Sale("ノートPC", "東京", 10, 80000),
            new Sale("ノートPC", "大阪", 8, 82000),
            new Sale("タブレット", "東京", 15, 45000),
            new Sale("タブレット", "大阪", 12, 46000),
            new Sale("スマートフォン", "東京", 25, 70000),
            new Sale("スマートフォン", "大阪", 20, 72000)
        );
        
        // 総売上
        double totalRevenue = sales.stream()
                                  .mapToDouble(Sale::getRevenue)
                                  .sum();
        System.out.println("総売上: " + totalRevenue);
        
        // 商品別売上
        Map<String, Double> revenueByProduct = sales.stream()
            .collect(groupingBy(Sale::getProduct, 
                               summingDouble(Sale::getRevenue)));
        System.out.println("商品別売上: " + revenueByProduct);
        
        // 地域別平均価格
        Map<String, Double> avgPriceByRegion = sales.stream()
            .collect(groupingBy(Sale::getRegion,
                               averagingDouble(Sale::getPrice)));
        System.out.println("地域別平均価格: " + avgPriceByRegion);
        
        // 最高売上の商品
        Optional<Sale> topSale = sales.stream()
            .max(Comparator.comparing(Sale::getRevenue));
        System.out.println("最高売上: " + topSale.orElse(null));
        
        // 売上上位3商品
        List<Sale> top3 = sales.stream()
            .sorted(Comparator.comparing(Sale::getRevenue).reversed())
            .limit(3)
            .collect(toList());
        System.out.println("売上上位3:");
        top3.forEach(System.out::println);
    }
}
```

## 10.7 練習問題

1. 文字列のリストから、5文字以上の単語を抽出し、アルファベット順にソートして重複を除去するプログラムを作成してください。

2. 学生のテストスコアを管理するシステムを作成し、科目別平均点、学生別総合点などを計算してください。

3. ログファイルを模したデータから、エラーレベル別の集計を行うプログラムを作成してください。

## まとめ

この章では、Stream APIを使用したデータ処理の方法を学習しました。関数型プログラミングのアプローチにより、簡潔で読みやすいコードによってコレクションの操作を行えるようになりました。中間操作と終端操作の組み合わせにより、複雑なデータ処理も効率的に実装できます。