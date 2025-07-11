# 第10章 Stream APIと高度なコレクション操作

## 本章の学習目標

### 前提知識

本章を学習するためには、いくつかの重要な前提知識が必要です。まず必須の前提として、第9章で学んだラムダ式と関数型インターフェイスの概念を十分に習得していることが求められます。Stream APIは関数型プログラミングの概念を大幅に活用しているため、ラムダ式のしくみや流れるようなコーディング能力が不可欠です。また、コレクションフレームワークの実践的な使用経験も必要で、List、Set、Mapの特性と使い分けについての理解が前提となります。関数型プログラミングスタイルの基本的な理解も重要で、データの変換と処理を宣言的に表現する方法を理解している必要があります。

さらに、より深い学習を望む学生には、データ処理を含む実用的なプログラム開発経験があることが推奨されます。特に、ループ処理とデータ変換の実装経験があると、Stream APIの利点と必要性をより深く理解できるでしょう。従来の命令型プログラミングでデータのフィルタリング、変換、集計を実装した経験があると、Stream APIの簡潔さと可読性の高さを実感できるでしょう。

### 学習目標

本章では、Java 8で導入されたStream APIと高度なコレクション操作について学習します。まず知識理解の面では、Stream APIの設計思想とその利点を深く理解します。Stream APIは、データの処理をより宣言的で可読性の高いスタイルで表現することを可能にします。中間操作と終端操作の概念と種類を学び、遅延評価（lazy evaluation）のしくみを理解することで、メモリ効率とパフォーマンスの話しくみを学びます。並列ストリーム（parallel stream）の理解と注意点も重要な学習ポイントで、マルチコア環境でのパフォーマンス向上とその落とし穴を学びます。

技能習得の面では、Stream APIを使った効率的なデータ処理技術を身につけます。複雑なデータ変換とフィルタリングの実装方法を学び、実務で必要となるデータ操作スキルを習得します。Collectorsクラスを使った柔軟なデータ収集方法を学び、並列処理による性能向上の実践的な技術も習得します。

データ処理能力の観点からは、大量データの効率的な処理プログラムを実装できるようになることが目標です。宣言的なデータ処理パイプラインの設計方法を学び、従来の命令型処理との効果的な使い分けができます。これにより、コードの可読性と保守性を大幅に向上させることができます。

最終的な到達レベルとしては、複雑なデータ処理要件をStream APIで効率的に実装できます。カスタムCollectorを作成して専用のデータ収集処理を実装する技術、並列ストリームを適切に活用した高性能データ処理の実現、そして従来のループ処理とStream処理を状況に応じて適切に使い分ける能力が、本章の最終目標です。



## 10.1 データ構造の選択

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

## 10.2 ラムダ式によるカスタムソート

コレクションを独自のルールでソートしたい場合、`Comparator`インターフェイスを使います。Java 8から導入された**ラムダ式**を使うと、この`Comparator`の実装を非常に簡潔に記述できます。

### 匿名クラスからラムダ式へ

ラムダ式が登場する前は、`Comparator`をその場で実装するために**匿名クラス**が使われていました。

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

```java
List<Student> list = ...;
// 点数が90点以上の生徒だけを抽出
List<Student> highScorers = list.stream()
    .filter(s -> s.getScore() >= 90)
    .collect(Collectors.toList());
```

### `map`: 要素を別の形に変換

`map`は、各要素に関数（`Function`）を適用し、別の値に変換します。

```java
// 生徒のリストから、名前のリストを生成
List<String> names = list.stream()
    .map(Student::getName)
    .collect(Collectors.toList());
```

### 組み合わせた例

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

## まとめ

本章では、コレクションフレームワークの応用的な側面と、それを操るための現代的な手法を学びました。

- **データ構造の選択**: `ArrayList`, `LinkedList`, `HashSet`, `TreeSet`など、状況に応じて最適な実装クラスを選択することが重要です。
- **ラムダ式と`Comparator`**: ラムダ式を使うことで、独自のソートロジックを簡潔かつ宣言的に記述できます。
- **Stream API**: `filter`, `map`, `sorted`, `collect`などの操作を連結することで、複雑なコレクション操作を直感的に記述できます。

これらの知識を身につけることで、より効率的で、保守性が高く、そして読みやすいJavaコードを書くことが可能になります。

## 章末演習

本章で学んだStream APIと高度なコレクション操作を実践的な課題で確認しましょう。

### 演習課題へのアクセス

本書の演習課題は、以下のGitHubリポジトリで提供されています：

**リポジトリ**: `https://github.com/[your-repo]/java-primer-exercises`

### 第10章の課題構成

```
exercises/chapter10/
├── basic/              # 基礎課題（必須）
│   ├── README.md       # 詳細な課題説明
│   └── Employee.java
├── advanced/           # 発展課題（推奨）
├── challenge/          # チャレンジ課題（任意）
└── solutions/          # 解答例（実装後に参照）
```

### 学習の目標

本章の演習を通じて以下のスキルを習得します：
- Stream APIを使った効率的なデータ処理
- 並列ストリームによるパフォーマンス向上
- 関数型プログラミングスタイルでの実装

### 課題の概要

1. **基礎課題**: 社員データ分析、商品売上分析など、Stream APIの基本的な活用
2. **発展課題**: テキスト解析システム、複雑なデータ変換パイプラインの実装
3. **チャレンジ課題**: 大規模データ処理、カスタムコレクターの実装など、高度な技術の活用

詳細な課題内容と実装のヒントは、GitHubリポジトリの各課題フォルダ内のREADME.mdを参照してください。

**次のステップ**: 基礎課題が完了したら、第11章「例外処理とエラーハンドリング」に進みましょう。




## Deep Dive: Stream APIの内部実装

Stream APIをより深く理解したい方のために、その内部実装とパフォーマンス最適化について詳しく解説した付録を用意しています。

**付録B.08「Stream APIの内部実装とパフォーマンス最適化」では以下の内容を扱っています：**

- **Spliteratorのしくみ**: Streamの分割可能イテレータの詳細
- **並列処理とFork/Joinフレームワーク**: 並列Streamの内部動作
- **カスタムコレクターの実装**: 高性能なコレクターの作成方法
- **パフォーマンスベンチマーキング**: JMHを使った正確な測定手法

これらの知識は、大規模データ処理や高性能アプリケーションの開発において特に有用です。基本的なStream APIの使い方に慣れた後、さらなる最適化を求める場合にぜひ参照してください。

→ **[付録B.08を読む](../appendix-b08-stream-api-internals.md)**
- **エラー処理**: 例外やnullを適切に処理しているか
- **パフォーマンス**: 必要に応じて並列処理を活用しているか