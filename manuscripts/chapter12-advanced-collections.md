# 第12章 Stream APIと高度なコレクション操作

## 本章の学習目標

### 前提知識

本章を学習するためには、いくつかの重要な前提知識が必要です。

**必須前提**：
- 第10章のコレクションフレームワーク（List、Set、Map）
- 第11章のジェネリクス
- 基本的なラムダ式の理解（本章で復習します）

**望ましい前提**：
- ループ処理でのデータ処理経験
- ソートやフィルタリングの実装経験

本章では、まずラムダ式の基本的な使い方をコレクションのソートを通じて復習し、その後でStream APIを使った高度なデータ処理に進みます。

さらに、より深い学習を望む学生には、データ処理を含む実用的なプログラム開発経験があることが推奨されます。特に、ループ処理とデータ変換の実装経験があると、Stream APIの利点と必要性をより深く理解できるでしょう。従来の命令型プログラミングでデータのフィルタリング、変換、集計を実装した経験があると、Stream APIの簡潔さと可読性の高さを実感できるでしょう。

### 学習目標

本章では、Java 8で導入されたStream APIと高度なコレクション操作について学習します。まず知識理解の面では、Stream APIの設計思想とその利点を深く理解します。Stream APIは、データの処理をより宣言的で可読性の高いスタイルで表現することを可能にします。中間操作と終端操作の概念と種類を学び、遅延評価（lazy evaluation）のしくみを理解することで、メモリ効率とパフォーマンスの話しくみを学びます。並列ストリーム（parallel stream）の理解と注意点も重要な学習ポイントで、マルチコア環境でのパフォーマンス向上とその落とし穴を学びます。

技能習得の面では、Stream APIを使った効率的なデータ処理技術を身につけます。複雑なデータ変換とフィルタリングの実装方法を学び、実務で必要となるデータ操作スキルを習得します。Collectorsクラスを使った柔軟なデータ収集方法を学び、並列処理による性能向上の実践的な技術も習得します。

データ処理能力の観点からは、大量データの効率的な処理プログラムを実装できるようになることが目標です。宣言的なデータ処理パイプラインの設計方法を学び、従来の命令型処理との効果的な使い分けができます。これにより、コードの可読性と保守性を大幅に向上させることができます。

最終的な到達レベルとしては、複雑なデータ処理要件をStream APIで効率的に実装できます。カスタムCollectorを作成して専用のデータ収集処理を実装する技術、並列ストリームを適切に活用した高性能データ処理の実現、そして従来のループ処理とStream処理を状況に応じて適切に使い分ける能力が、本章の最終目標です。



## 12.1 データ構造の選択

第8章では基本的なコレクションを学びましたが、それぞれのインターフェイスには異なる特性を持つ実装クラスが存在します。状況に応じて最適なものを選択することが、パフォーマンスの良いプログラムを書く鍵となります。

### `ArrayList` vs `LinkedList`

| 操作 | `ArrayList` (内部配列) | `LinkedList` (連結リスト) |
| :--- | :--- | :--- |
| **要素の取得 (get)** | **高速 (O(1))** | 低速 (O(n)) |
| **先頭/末尾への追加・削除** | 低速 (O(n)) / 高速 (O(1)) | **高速 (O(1))** |
| **中間への追加・削除** | 低速 (O(n)) | **高速 (O(1))** |

**使い分けの指針:**

リストの実装クラスを選択する際は、アプリケーションの使用パターンを正確に分析することが重要です。**ArrayList**は、要素の参照（取得）が多く、リストのサイズがあまり変化しない場合に最適であり、実際にほとんどのケースで第一候補となる使いやすい実装です。内部的に配列を使用しているため、インデックスでのランダムアクセスが非常に高速で、メモリ効率も優秀です。

一方、**LinkedList**は、リストの先頭や中間で頻繁に要素を追加・削除する場合に特に有効です。連結リストの性質上、ノードの挿入や削除はO(1)で実行できるため、データのサイズが頻繁に変更されるアプリケーションでは優位性を発揮します。ただし、ランダムアクセスが必要な場合は、パフォーマンスが大幅に低下することに注意が必要です。

### `HashSet` vs `LinkedHashSet` vs `TreeSet`

| 特徴 | `HashSet` | `LinkedHashSet` | `TreeSet` |
| :--- | :--- | :--- | :--- |
| **順序** | 保証されない | **挿入順** | **ソート順** |
| **性能** | **最速** | `HashSet`よりわずかに遅い | `log(n)`時間（比較的低速） |
| **null要素** | 許可 | 許可 | 不可 |

**使い分けの指針:**

Setの実装クラスの選択は、アプリケーションの要件によって決まります。**HashSet**は、順序が不要で、とにかく高速に重複を除きたい場合に最適な選択です。ハッシュテーブルを内部で使用しているため、要素の検索、追加、削除が平均でO(1)と非常に高速です。

**LinkedHashSet**は、要素を追加した順序を保持したい場合に使います。HashSetの性能をほぼ保ちながら、追加順序を記憶するためのリンク情報を内部で管理しています。これにより、データの出力順序が予測可能になり、テストやデバッグが容易になります。

**TreeSet**は、要素を常にソートされた状態に保ちたい場合に使用します。内部的に赤黒木（Red-Black Tree）というバランス木を使用しているため、操作の時間計算量はlog(n)となり、HashSetよりは低速ですが、要素が常にソートされた状態で管理されるため、即座にソート済みのデータを取得できる利点があります。

## 12.2 ラムダ式によるカスタムソート

コレクションを独自のルールでソートしたい場合、`Comparator`インターフェイスを使います。Java 8から導入された**ラムダ式**を使うと、この`Comparator`の実装を非常に簡潔に記述できます。

### 匿名クラスからラムダ式へ

ラムダ式が登場する前は、`Comparator`をその場で実装するために**匿名クラス**が使われていました。

<span class="listing-number">**サンプルコード12-1**</span>
```java
import java.util.Comparator;
// ...
// 匿名クラスを使ったComparatorの実装（古い書き方）
Comparator<Student> scoreComparator = new Comparator<Student>() {
    @Override
    public int compare(Student s1, Student s2) {
        return Integer.compare(s2.getScore(), s1.getScore()); // 点数の降順
    }
};
```
この冗長な記述は、ラムダ式を使うと以下のように書き換えられます。

```java
// ラムダ式を使ったComparatorの実装
Comparator<Student> scoreComparator = (s1, s2) -> Integer.compare(s2.getScore(), s1.getScore());
```
`compare`メソッドの実装だけを抜き出したような形になり、非常にシンプルになりました。`->`の左側がメソッドの引数、右側が処理本体です。

### `Comparator`の便利なメソッド

Java 8以降、`Comparator`インターフェイスには、ラムダ式と組み合わせてソート条件をより宣言的に記述できる便利なメソッドが追加されました。

- `Comparator.comparing(keyExtractor)`: 比較のキーとなる値を抽出する関数を渡す。
- `reversed()`: 比較順序を逆にする。
- `thenComparing(other)`: 比較結果が同じだった場合の、次の比較条件を指定する。

<span class="listing-number">**サンプルコード12-2**</span>
```java
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class Student {
    String name;
    int score;
    int height;
    public Student(String name, int score, int height) { this.name = name; this.score = score; this.height = height; }
    public String getName() { return name; }
    public int getScore() { return score; }
    public int getHeight() { return height; }
    @Override public String toString() { return name + "(score:" + score + ", height:" + height + ")"; }
}

public class AdvancedSortExample {
    public static void main(String[] args) {
        List<Student> students = new ArrayList<>();
        students.add(new Student("Bob", 92, 170));
        students.add(new Student("Alice", 85, 165));
        students.add(new Student("Charlie", 92, 180));

        // 点数が高い順、同じ点数の場合は身長が高い順にソート
        Comparator<Student> comparator = Comparator
            .comparingInt(Student::getScore).reversed() // 点数で比較し、逆順（降順）に
            .thenComparingInt(Student::getHeight).reversed(); // 次に身長で比較し、逆順（降順）に

        students.sort(comparator); // List.sort()に渡す

        System.out.println(students);
        // 出力: [Charlie(score:92, height:180), Bob(score:92, height:170), Alice(score:85, height:165)]
    }
}
```
`Student::getScore`は**メソッド参照**とよい、`student -> student.getScore()`というラムダ式をさらに簡潔にした書き方です。

## 10.3 Stream APIによる現代的なコレクション操作

Java 8で導入された**Stream API**は、コレクションの要素の集まりを「データの流れ（ストリーム）」として扱い、その流れに対してさまざまな処理を連結（パイプライン化）していくしくみです。

### なぜStream APIが必要なのか

従来のコレクション操作では、forループとif文を組み合わせた命令型のコードを書く必要がありました。このアプローチには以下のような課題がありました：

1. **可読性の問題**：ネストしたループや複雑な条件分岐により、コードの意図が読み取りにくい
2. **並列処理の困難さ**：マルチコアCPUを活用した並列処理を実装するには、複雑なスレッド管理が必要
3. **再利用性の低さ**：処理ロジックがループ構造と密結合し、再利用が困難

Stream APIは、関数型プログラミングの概念を取り入れることでこれらの課題を解決します。データ処理を「変換のパイプライン」として表現することで、より宣言的で理解しやすいコードを実現します。

### 遅延評価のしくみ

Stream APIの重要な特徴の1つが**遅延評価（Lazy Evaluation）**です。中間操作（filter、mapなど）は、終端操作（collect、forEachなど）が呼ばれるまで実際には実行されません。これにより：

- 不要な計算を避けることができる
- メモリ効率が向上する
- 無限ストリームの処理が可能になる

<span class="listing-number">**サンプルコード12-3**</span>
```java
// この時点ではまだフィルタリングは実行されない
Stream<Integer> stream = numbers.stream()
    .filter(n -> {
        System.out.println("Filtering: " + n);
        return n > 10;
    });

// collect()が呼ばれて初めてフィルタリングが実行される
List<Integer> result = stream.collect(Collectors.toList());
```

ラムダ式と組み合わせることで、コレクション操作を非常に宣言的（「何をするか」を記述するスタイル）で、かつ簡潔に書くことができます。

### Stream操作の基本パターン

`コレクション.stream().中間操作1().中間操作2()...終端操作()`

#### ストリーム処理の3つのフェーズ

1. **ストリームの生成**: 
   - `list.stream()`：コレクションからストリームを生成
   - `Arrays.stream(array)`：配列からストリームを生成
   - `Stream.of(値1, 値2, ...)`：個別の値からストリームを生成
   - `Stream.generate()`や`Stream.iterate()`：無限ストリームの生成

2. **中間操作（Intermediate Operations）**: 
   中間操作は新しいストリームを返すため、メソッドチェインで連結できます。主な中間操作：
   - `filter(Predicate)`：条件に合う要素のみを通過させる
   - `map(Function)`：各要素を別の形に変換する
   - `flatMap(Function)`：ネストした構造を平坦化する
   - `sorted()`：要素をソートする
   - `distinct()`：重複を除去する
   - `limit(n)`：最初のn個の要素に制限する
   - `skip(n)`：最初のn個の要素をスキップする

3. **終端操作（Terminal Operations）**: 
   終端操作はストリームを「消費」し、結果を生成します。終端操作が呼ばれて初めて、すべての処理が実行されます：
   - `collect(Collector)`：結果を収集する（最も柔軟な終端操作）
   - `forEach(Consumer)`：各要素に対して処理を実行
   - `count()`：要素数を返す
   - `reduce(BinaryOperator)`：要素を集約して単一の結果を生成
   - `anyMatch(Predicate)`：いずれかの要素が条件を満たすか
   - `allMatch(Predicate)`：すべての要素が条件を満たすか
   - `findFirst()`/`findAny()`：最初の要素/任意の要素を取得

### `filter`: 条件に合う要素だけを抽出

`filter`は、条件（`Predicate`）に一致する要素だけを残します。

<span class="listing-number">**サンプルコード12-4**</span>
```java
List<Student> list = ...;
// 点数が90点以上の生徒だけを抽出
List<Student> highScorers = list.stream()
    .filter(s -> s.getScore() >= 90)
    .collect(Collectors.toList());
```

### `map`: 要素を別の形に変換

`map`は、各要素に関数（`Function`）を適用し、別の値に変換します。

<span class="listing-number">**サンプルコード12-5**</span>
```java
// 生徒のリストから、名前のリストを生成
List<String> names = list.stream()
    .map(Student::getName)
    .collect(Collectors.toList());
```

### 組み合わせた例

<span class="listing-number">**サンプルコード12-6**</span>
```java
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StreamExample {
    public static void main(String[] args) {
        List<Student> students = new ArrayList<>();
        students.add(new Student("Bob", 92, 170));
        students.add(new Student("Alice", 85, 165));
        students.add(new Student("David", 75, 170));
        students.add(new Student("Charlie", 92, 180));

        // 点数が80点より高く、身長が170cm以上の生徒の名前を、アルファベット順で取得する
        List<String> result = students.stream() // 1. ストリームを生成
            .filter(s -> s.getScore() > 80)      // 2. 点数でフィルタリング
            .filter(s -> s.getHeight() >= 170)   // 3. 身長でフィルタリング
            .map(Student::getName)               // 4. 名前に変換
            .sorted()                            // 5. アルファベット順にソート
            .collect(Collectors.toList());       // 6. 結果をリストに集約

        System.out.println(result);
        // 出力: [Bob, Charlie]
    }
}
```
従来の`for`ループと`if`文を組み合わせるよりも、処理の流れが明確で読みやすいコードになるのがStream APIの大きな利点です。

## 10.4 並列ストリームによるパフォーマンス向上

### マルチコア時代のプログラミング

現代のCPUは複数のコアを持ち、複数の処理を同時に実行できます。しかし、従来のループ処理はシングルスレッドで実行されるため、1つのコアしか使用しません。Stream APIの**並列ストリーム**を使うと、データ処理を自動的に複数のコアに分散して実行できます。

### 並列ストリームの使い方

並列ストリームの使用は非常に簡単で、`.stream()`の代わりに`.parallelStream()`を使うだけです：

<span class="listing-number">**サンプルコード12-7**</span>
```java
// シーケンシャル（直列）処理
long sum = numbers.stream()
    .filter(n -> n % 2 == 0)
    .mapToLong(n -> n * n)
    .sum();

// 並列処理
long sumParallel = numbers.parallelStream()
    .filter(n -> n % 2 == 0)
    .mapToLong(n -> n * n)
    .sum();
```

### 並列ストリームの内部動作

並列ストリームは、Javaの**Fork/Joinフレームワーク**を使用して実装されています。処理の流れは以下のようになります：

1. **分割（Split）**：データを複数の小さなチャンクに分割
2. **処理（Process）**：各チャンクを異なるスレッドで並列に処理
3. **統合（Combine）**：各スレッドの結果を統合して最終結果を生成

<span class="listing-number">**サンプルコード12-8**</span>
```java
import java.util.stream.IntStream;
import java.time.Duration;
import java.time.Instant;

public class ParallelStreamExample {
    public static void main(String[] args) {
        int size = 100_000_000;
        
        // シーケンシャル処理の計測
        Instant start = Instant.now();
        double sumSeq = IntStream.range(0, size)
            .mapToDouble(Math::sqrt)
            .sum();
        Duration seqTime = Duration.between(start, Instant.now());
        
        // 並列処理の計測
        start = Instant.now();
        double sumPar = IntStream.range(0, size)
            .parallel()
            .mapToDouble(Math::sqrt)
            .sum();
        Duration parTime = Duration.between(start, Instant.now());
        
        System.out.println("シーケンシャル: " + seqTime.toMillis() + "ms");
        System.out.println("並列: " + parTime.toMillis() + "ms");
        System.out.println("高速化率: " + 
            (double)seqTime.toMillis() / parTime.toMillis() + "倍");
    }
}
```

### 並列ストリームの注意点

並列ストリームは強力な機能ですが、すべての状況で性能が向上するわけではありません：

#### 1. オーバーヘッドの問題
スレッドの作成やコンテキストスイッチにはコストがかかります。データ量が少ない場合、このオーバーヘッドが並列化の利点を上回ることがあります。

#### 2. スレッドセーフティ
並列処理では、複数のスレッドが同じデータにアクセスする可能性があるため、スレッドセーフでない操作は避ける必要があります：

<span class="listing-number">**サンプルコード12-9**</span>
```java
// スレッドセーフでない例（避けるべき）
List<Integer> results = new ArrayList<>();  // スレッドセーフでない
numbers.parallelStream()
    .forEach(n -> results.add(n * 2));  // データ競合の危険！

// スレッドセーフな代替方法
List<Integer> results = numbers.parallelStream()
    .map(n -> n * 2)
    .collect(Collectors.toList());  // スレッドセーフ
```

#### 3. 順序の保証
並列ストリームでは、処理の順序が保証されない場合があります。順序が重要な場合は、`forEachOrdered()`を使用するか、シーケンシャルストリームを使う必要があります。

### 並列ストリームを使うべき場合

以下の条件がそろった場合に、並列ストリームの使用を検討しましょう：

1. **大量のデータ**：数千以上の要素を処理する場合
2. **CPU集約的な処理**：各要素の処理に計算コストがかかる場合
3. **状態を共有しない処理**：各要素が独立して処理できる場合
4. **順序が重要でない場合**：出力の順序が問題にならない場合

## 12.5 高度なStream操作

### flatMapによる複雑なデータ変換

`flatMap`は、ネストした構造を平坦化するために使用される重要な操作です。各要素をストリームに変換し、それらをすべて結合して1つのストリームにします。これは、リストのリストを単一のリストに変換する場合や、文字列を単語に分割する場合などに特に有用です。

<span class="listing-number">**サンプルコード12-10**</span>
```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FlatMapExample {
    public static void main(String[] args) {
        List<List<String>> nestedList = Arrays.asList(
            Arrays.asList("Java", "Python"),
            Arrays.asList("JavaScript", "Go"),
            Arrays.asList("Rust", "C++")
        );
        
        // flatMapを使ってネストしたリストを平坦化する
        // この例では、リストのリストから単一の文字列リストを作成
        List<String> flatList = nestedList.stream()
            .flatMap(List::stream)  // 各内部リストをストリームに変換して結合
            .collect(Collectors.toList());
        
        System.out.println(flatList);
        // 出力: [Java, Python, JavaScript, Go, Rust, C++]
        
        // 文字列を単語に分割する例
        List<String> sentences = Arrays.asList(
            "Hello world",
            "Java programming",
            "Stream API"
        );
        
        // 各文字列を単語に分割し、すべての単語を1つのリストにまとめる
        List<String> words = sentences.stream()
            .flatMap(sentence -> Arrays.stream(sentence.split(" ")))
            .collect(Collectors.toList());
        
        System.out.println(words);
        // 出力: [Hello, world, Java, programming, Stream, API]
    }
}
```

`flatMap`は、一対多の関係を持つデータを処理する際に非常に強力です。各学生が複数の科目を履修している場合など、実世界のデータ構造でよく遭遇するパターンを効率的に処理できます。

### reduceによる集約処理

`reduce`操作は、ストリームの要素を単一の結果に集約するための柔軟な方法を提供します。合計、最大値、最小値の計算や、文字列の結合など、カスタムの集約処理を実装できます。

<span class="listing-number">**サンプルコード12-11**</span>
```java
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ReduceExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        // 基本的なreduce操作：数値の合計を計算
        // reduce(初期値, 二項演算子)の形式
        int sum = numbers.stream()
            .reduce(0, (a, b) -> a + b);  // 初期値0から開始し、各要素を加算
        System.out.println("合計: " + sum); // 出力: 合計: 15
        
        // より複雑な例：最大値を求める（初期値なし）
        // この場合、空のストリームの可能性があるためOptionalが返される
        Optional<Integer> max = numbers.stream()
            .reduce((a, b) -> a > b ? a : b);
        max.ifPresent(value -> System.out.println("最大値: " + value)); // 出力: 最大値: 5
        
        // 文字列の結合例
        List<String> words = Arrays.asList("Java", "is", "awesome");
        String sentence = words.stream()
            .reduce("", (result, word) -> result.isEmpty() ? word : result + " " + word);
        System.out.println(sentence); // 出力: Java is awesome
        
        // 複雑な集約：学生の点数から統計情報を計算
        List<Student> students = Arrays.asList(
            new Student("Alice", 85, 165),
            new Student("Bob", 92, 170),
            new Student("Charlie", 78, 180)
        );
        
        // 全学生の平均点を計算（reduceを使った方法）
        double average = students.stream()
            .mapToInt(Student::getScore)
            .reduce(0, Integer::sum) / (double) students.size();
        System.out.println("平均点: " + average); // 出力: 平均点: 85.0
    }
}
```

`reduce`は非常に強力ですが、多くの一般的な集約操作には専用のメソッド（`sum()`, `max()`, `min()`など）や`collect()`操作が用意されているため、それらを優先して使用することが推奨されます。

### Collectorsによる高度なデータ収集

`Collectors`クラスは、ストリームの要素を様々な形で収集するための豊富なメソッドを提供します。単純なリスト作成から、複雑なグループ化や統計処理まで対応できます。

<span class="listing-number">**サンプルコード12-12**</span>
```java
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CollectorsExample {
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
            new Student("Alice", 85, 165),
            new Student("Bob", 92, 170),
            new Student("Charlie", 78, 180),
            new Student("David", 92, 175),
            new Student("Eve", 88, 160)
        );
        
        // 基本的なcollect操作：リストへの収集
        // 高得点（85点以上）の学生の名前をリストで取得
        List<String> highScorers = students.stream()
            .filter(s -> s.getScore() >= 85)
            .map(Student::getName)
            .collect(Collectors.toList());
        System.out.println("高得点者: " + highScorers);
        
        // グループ化の例：点数でグループ分け
        // 同じ点数の学生をグループ化する実用的な例
        Map<Integer, List<Student>> scoreGroups = students.stream()
            .collect(Collectors.groupingBy(Student::getScore));
        System.out.println("点数別グループ: " + scoreGroups);
        
        // より複雑な変換：条件による分類
        // 80点以上かどうかで学生を分類
        Map<Boolean, List<Student>> passFail = students.stream()
            .collect(Collectors.partitioningBy(s -> s.getScore() >= 80));
        System.out.println("合格者: " + passFail.get(true).size() + "人");
        System.out.println("不合格者: " + passFail.get(false).size() + "人");
    }
}
```

`Collectors.groupingBy()`は、データを特定の条件でグループ化する際に非常に強力で、データ分析やレポート作成において頻繁に使用されます。データベースのGROUP BY句に相当する処理をJavaのコード内で簡潔に実現できます。

### Optionalによる安全なデータ処理

`Optional`クラスは、null値が存在する可能性がある処理を安全に扱うためのJava 8で導入された重要な仕組みです。従来のnullポインタ例外を避けながら、よりエレガントなコードを書くことができます。

<span class="listing-number">**サンプルコード12-13**</span>
```java
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class OptionalExample {
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
            new Student("Alice", 85, 165),
            new Student("Bob", 92, 170),
            new Student("Charlie", 78, 180)
        );
        
        // Optionalの基本使用：最初の高得点者を安全に取得
        // findFirst()はOptional<Student>を返すため、null安全
        Optional<Student> firstHighScorer = students.stream()
            .filter(s -> s.getScore() >= 90)
            .findFirst();
        
        // 値が存在する場合のみ処理を実行
        firstHighScorer.ifPresent(student -> 
            System.out.println("最初の高得点者: " + student.getName()));
        
        // デフォルト値を使用した安全な処理
        String topScorerName = firstHighScorer
            .map(Student::getName)
            .orElse("該当者なし");
        System.out.println("トップスコアラー: " + topScorerName);
    }
}
```

### Optionalの連鎖操作

`Optional`は、複数の操作を安全に連鎖させることができ、従来のif-null チェックの連続を大幅に簡素化します。

<span class="listing-number">**サンプルコード12-14**</span>
```java
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class OptionalChainingExample {
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
            new Student("Alice", 85, 165),
            new Student("Bob", 92, 170),
            new Student("Charlie", 78, 180)
        );
        
        // Optionalの連鎖：複数の変換を安全に実行
        // 最高得点者の名前を大文字で取得（該当者がいない場合は"N/A"）
        String result = students.stream()
            .max((s1, s2) -> Integer.compare(s1.getScore(), s2.getScore()))
            .map(Student::getName)          // 学生が見つかれば名前を取得
            .map(String::toUpperCase)       // 名前を大文字に変換
            .orElse("N/A");                 // 見つからない場合のデフォルト値
        
        System.out.println("最高得点者（大文字）: " + result);
    }
}
```

### null安全な処理パターン

実際のアプリケーションでは、nullが混入する可能性があるデータを安全に処理する必要があります。`Optional`を活用することで、予期しないnullポインタ例外を防げます。

<span class="listing-number">**サンプルコード12-15**</span>
```java
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class NullSafeProcessingExample {
    public static void main(String[] args) {
        // nullが混入する可能性があるデータ
        List<String> namesWithNull = Arrays.asList("Alice", null, "Bob", "", "Charlie");
        
        // null安全な処理：nullや空文字列を適切に処理
        List<String> validNames = namesWithNull.stream()
            .filter(Objects::nonNull)           // nullを除外
            .filter(name -> !name.trim().isEmpty()) // 空文字列を除外
            .map(String::toUpperCase)           // 大文字に変換
            .collect(Collectors.toList());
        
        System.out.println("有効な名前: " + validNames);
        // 出力: 有効な名前: [ALICE, BOB, CHARLIE]
        
        // Optionalでnullをラップしてnullセーフ処理
        Optional<String> nullableName = Optional.ofNullable(null);
        String safeName = nullableName
            .filter(name -> !name.isEmpty())
            .map(String::toUpperCase)
            .orElse("UNKNOWN");
        
        System.out.println("安全な名前処理: " + safeName); // 出力: 安全な名前処理: UNKNOWN
    }
}
```

### 早期終了操作の活用

Stream APIには、すべての要素を処理せずに早期に結果を返す操作があります。これらは大量のデータを効率的に処理する際に重要です。

<span class="listing-number">**サンプルコード12-16**</span>
```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ShortCircuitExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 早期終了操作：条件を満たす要素が見つかれば処理を停止
        // すべての要素が偶数かチェック（1つでも奇数があればfalse）
        boolean allEven = numbers.stream()
            .peek(n -> System.out.println("チェック中: " + n)) // 処理の流れを観察
            .allMatch(n -> n % 2 == 0);
        System.out.println("すべて偶数: " + allEven);
        
        // いずれかの要素が条件を満たすかチェック
        boolean hasLargeNumber = numbers.stream()
            .anyMatch(n -> n > 7);
        System.out.println("7より大きい数あり: " + hasLargeNumber);
        
        // 最初のn個に制限して処理
        List<Integer> firstThree = numbers.stream()
            .limit(3)  // 最初の3要素のみ処理
            .collect(Collectors.toList());
        System.out.println("最初の3つ: " + firstThree);
    }
}
```

早期終了操作は、特に大量のデータや無限ストリームを扱う際に、パフォーマンスの向上とメモリ効率の最適化に大きく貢献します。

### 実用的なtoArray操作

ストリーム処理の結果を配列として取得したい場合、`toArray()`メソッドを使用します。型安全な配列を取得するためには、適切な配列コンストラクタを指定することが重要です。

<span class="listing-number">**サンプルコード12-17**</span>
```java
import java.util.Arrays;
import java.util.List;

public class ToArrayExample {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");
        
        // toArrayの正しい使い方：型安全な配列を取得
        // String[]::newを指定することで、適切な型の配列が生成される
        String[] nameArray = names.stream()
            .filter(name -> name.length() > 3)  // 3文字より長い名前のみ
            .map(String::toUpperCase)           // 大文字に変換
            .toArray(String[]::new);            // String配列として取得
        
        System.out.println("結果配列: " + Arrays.toString(nameArray));
        // 出力: 結果配列: [ALICE, CHARLIE, DAVID]
        
        // サイズを指定した配列生成も可能
        String[] sizedArray = names.stream()
            .toArray(size -> new String[size]);
        System.out.println("サイズ指定配列: " + Arrays.toString(sizedArray));
    }
}
```

### 複雑な処理パイプラインの実装

実際のアプリケーションでは、複数のStream操作を組み合わせた複雑な処理パイプラインを構築することがよくあります。以下の例では、実用的なデータ処理シナリオを示します。

<span class="listing-number">**サンプルコード12-18**</span>
```java
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ComplexProcessingExample {
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
            new Student("Alice", 85, 165),
            new Student("Bob", 92, 170),
            new Student("Charlie", 78, 180),
            new Student("David", 92, 175),
            new Student("Eve", 88, 160),
            new Student("Frank", 82, 178),
            new Student("Grace", 95, 168)
        );
        
        // 複雑な処理：高得点者（85点以上）を身長でグループ化し、
        // 各グループで最高得点者の名前を取得
        Map<String, String> heightGroupTopScorers = students.stream()
            .filter(s -> s.getScore() >= 85)                    // 高得点者のみ
            .collect(Collectors.groupingBy(                     // 身長でグループ化
                s -> s.getHeight() >= 170 ? "高身長" : "標準身長",
                Collectors.collectingAndThen(                   // グループ内での最高得点者を取得
                    Collectors.maxBy((s1, s2) -> Integer.compare(s1.getScore(), s2.getScore())),
                    opt -> opt.map(Student::getName).orElse("該当者なし")
                )
            ));
        
        System.out.println("身長グループ別最高得点者:");
        heightGroupTopScorers.forEach((group, name) -> 
            System.out.println(group + ": " + name));
        
        // さらに複雑な例：統計情報の収集
        Map<String, Double> heightGroupAverages = students.stream()
            .collect(Collectors.groupingBy(
                s -> s.getHeight() >= 170 ? "高身長" : "標準身長",
                Collectors.averagingInt(Student::getScore)
            ));
        
        System.out.println("\n身長グループ別平均点:");
        heightGroupAverages.forEach((group, avg) -> 
            System.out.printf("%s: %.1f点%n", group, avg));
    }
}
```

このような複雑な処理も、Stream APIを使用することで、従来のネストしたループや一時的な変数を使った処理よりもはるかに読みやすく、保守しやすいコードとして実装できます。

## まとめ

本章では、コレクションフレームワークの応用的な側面と、それを操るための現代的な手法を学びました。

- **データ構造の選択**: `ArrayList`, `LinkedList`, `HashSet`, `TreeSet`など、状況に応じて最適な実装クラスを選択することが重要です。
- **ラムダ式と`Comparator`**: ラムダ式を使うことで、独自のソートロジックを簡潔かつ宣言的に記述できます。
- **Stream API**: `filter`, `map`, `sorted`, `collect`などの操作を連結することで、複雑なコレクション操作を直感的に記述できます。
- **高度なStream操作**: `flatMap`によるデータ平坦化、`reduce`による集約処理、`Optional`による安全なnull処理など、実用的な高度技術を習得できます。
- **並列ストリーム**: マルチコアCPUを活用した並列処理により、大量データの効率的な処理が可能になります。

これらの知識を身につけることで、より効率的で、保守性が高く、そして読みやすいJavaコードを書くことが可能になります。

## 章末演習

本章で学んだStream APIと高度なコレクション操作を実践しましょう。

### 演習課題へのアクセス

**リポジトリ**: `https://github.com/[your-repo]/java-primer-exercises`

```
exercises/chapter10/
├── basic/              # 基礎課題
├── advanced/           # 発展課題
├── challenge/          # チャレンジ課題
└── solutions/          # 解答例
```

### 主な課題
- 社員データの分析（filter、map、collect）
- 売上データの集計（groupingBy、統計処理）
- 並列ストリームのパフォーマンス測定

詳細はGitHubリポジトリを参照してください。

**次章**: 第13章「ラムダ式と関数型インターフェイス」




## より深い理解のために

本章で学んだStream APIについて、さらに深く理解したい方は、GitHubリポジトリの付録資料を参照してください：

**付録リソース**: `/appendix/b10-stream-api-internals/`

この付録では以下の高度なトピックを扱います：

- **Spliteratorの仕組み**: 並列処理を支える分割可能なイテレータの実装詳細
- **Fork/Joinフレームワーク**: 並列ストリームの内部で使用される並列処理メカニズム
- **カスタムコレクターの実装**: 独自の集約処理を実装する高度なテクニック
- **パフォーマンス最適化**: JMHを使ったベンチマーキングと最適化手法
- **エラー処理**: 例外やnullを適切に処理しているか
- **パフォーマンス**: 必要に応じて並列処理を活用しているか