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

---

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

本章で学んだStream APIと高度なコレクション操作の概念を活用して、実践的な練習課題に取り組みましょう。

### 課題一覧

#### 課題1: 社員データ分析システム（基本）

社員データを分析するシステムを作成し、Stream APIを活用してください。

**技術的背景：ビジネスインテリジェンスにおけるデータ分析**

現代の企業では、人事データの分析が経営戦略の重要な要素となっています。Stream APIを使ったデータ分析の実用例：

**従来の課題：**
```java
// 複雑なネストループと条件分岐
Map<String, List<Employee>> byDept = new HashMap<>();
for (Employee e : employees) {
    if (!byDept.containsKey(e.getDepartment())) {
        byDept.put(e.getDepartment(), new ArrayList<>());
    }
    byDept.get(e.getDepartment()).add(e);
}

Map<String, Double> avgSalary = new HashMap<>();
for (String dept : byDept.keySet()) {
    double sum = 0;
    for (Employee e : byDept.get(dept)) {
        sum += e.getSalary();
    }
    avgSalary.put(dept, sum / byDept.get(dept).size());
}
```

**Stream APIによる解決：**
```java
// 1行で部署別平均給与を算出
Map<String, Double> avgSalary = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.averagingDouble(Employee::getSalary)
    ));
```

**実際のビジネスでの活用：**
- **人材配置最適化**：スキルと給与のバランス分析
- **昇進候補者の選定**：多面的な評価指標での絞り込み
- **離職リスク分析**：年齢、給与、部署の相関分析
- **ダイバーシティ推進**：属性別の統計分析

**Stream APIの利点：**
- **宣言的なコード**：「何を」求めるかを明確に表現
- **メンテナンス性**：ビジネスロジックの変更が容易
- **並列処理**：大量データでも高速処理可能
- **関数型プログラミング**：副作用のない安全なコード

この演習では、実際のビジネス分析で使われるデータ処理パターンを学びます。

**要求仕様**:
- Employeeクラス（名前、年齢、部署、給与）
- Stream APIによる多様な分析処理
- フィルタリング、ソート、グループ化、集約
- 部署別・年齢層別の統計分析

**実行例**:
```
=== 社員データ分析システム ===
全社員データ（10名）:
田中太郎（30歳・営業部・500万円）
佐藤花子（25歳・開発部・450万円）
...

年齢フィルタ（30歳以上）:
田中太郎（30歳・営業部・500万円）
鈴木一郎（35歳・開発部・600万円）
...

給与順ソート（上位3名）:
1位: 鈴木一郎（600万円）
2位: 田中太郎（500万円）
3位: 佐藤花子（450万円）

部署別集計:
営業部: 3名（平均給与: 480万円）
開発部: 4名（平均給与: 520万円）
総務部: 3名（平均給与: 420万円）

年齢層別分析:
20代: 3名（平均給与: 430万円）
30代: 5名（平均給与: 510万円）
40代: 2名（平均給与: 580万円）

全体統計:
総人数: 10名
平均給与: 485万円
最高給与: 600万円
最低給与: 380万円
給与合計: 4,850万円
```

#### 課題2: 商品売上分析システム（基本）

商品売上データを分析するシステムを作成し、高度なStream処理を実装してください。

**技術的背景：ECサイトにおけるリアルタイム分析**

Eコマースの急成長により、売上データのリアルタイム分析は競争優位性の源泉となっています：

**ビジネス課題：**
- **在庫最適化**：売上傾向から適性在庫を予測
- **価格戦略**：カテゴリ別の価格弾力性分析
- **顧客行動分析**：購買パターンの可視化
- **キャンペーン効果測定**：期間別の売上変動分析

**Stream APIの高度な活用：**
```java
// flatMapを使った複雑なデータ構造の処理
sales.stream()
    .collect(Collectors.groupingBy(
        Sale::getCategory,
        Collectors.collectingAndThen(
            Collectors.toList(),
            list -> {
                DoubleSummaryStatistics stats = list.stream()
                    .mapToDouble(Sale::getAmount)
                    .summaryStatistics();
                return new CategoryStats(
                    stats.getCount(),
                    stats.getSum(),
                    stats.getAverage(),
                    stats.getMax()
                );
            }
        )
    ));
```

**Optionalの実践的活用：**
- **nullポインタ例外の回避**：データ欠損への対応
- **デフォルト値の提供**：分析結果が存在しない場合の処理
- **連鎖的な処理**：map、filter、orElseの組み合わせ

**実際のシステムでの応用：**
- **Amazon**：リアルタイムレコメンデーション
- **楽天**：Flashセールの動的価格調整
- **メルカリ**：カテゴリ別の需要予測

この演習では、実際のECサイトで使われる分析手法を学びます。

**要求仕様**:
- Saleクラス（商品名、カテゴリ、売上金額、売上日）
- 期間別・カテゴリ別の売上分析
- Stream APIの高度な操作（flatMap、partition等）
- Optionalを活用した安全なデータアクセス

**実行例**:
```
=== 商品売上分析システム ===
売上データ（15件）:
2024-07-01: ノートPC（家電・150,000円）
2024-07-01: Java入門（書籍・3,000円）
...

月別売上集計:
2024年7月: 1,250,000円（8件）
2024年8月: 980,000円（7件）

カテゴリ別分析:
家電: 750,000円（3件・平均250,000円）
書籍: 45,000円（5件・平均9,000円）
食品: 80,000円（4件・平均20,000円）
衣料: 120,000円（3件・平均40,000円）

高額商品分析（10万円以上）:
ノートPC: 150,000円
デジタルカメラ: 120,000円
タブレット: 80,000円

売上トレンド:
最高売上日: 2024-07-15（350,000円）
最低売上日: 2024-08-05（15,000円）

Optional活用例:
最高額商品: Optional[ノートPC（150,000円）]
該当なし検索: Optional.empty
```

#### 課題3: テキスト解析システム（応用）

テキストデータを解析するシステムを作成し、文字列処理でのStream活用を実装してください。

**技術的背景：自然言語処理とテキストマイニング**

テキスト解析は、ビッグデータ時代の重要な技術です。Stream APIと並列処理の組み合わせが威力を発揮する分野：

**実際の応用分野：**
- **ソーシャルメディア分析**：Twitter、Facebookの感情分析
- **カスタマーレビュー分析**：商品評価の自動分類
- **文書要約システム**：重要キーワードの抽出
- **スパム検出**：メール、コメントのフィルタリング

**並列ストリームの効果的な使用：**
```java
// 大量テキストの並列処理
long wordCount = files.parallelStream()
    .flatMap(file -> readLines(file).stream())
    .flatMap(line -> Arrays.stream(line.split("\\s+")))
    .filter(word -> word.length() > 3)
    .distinct()
    .count();
```

**パフォーマンス最適化のポイント：**
- **データ分割の粒度**：適切なチャンクサイズの選択
- **CPU集約的処理**：正規表現のコンパイル最適化
- **メモリ効率**：中間結果の最小化
- **I/O処理**：ファイル読み込みの並列化

**実際の事例：**
- **Google**：検索クエリの自動補完
- **Amazon**：レビューからの商品特徴抽出
- **新聞社**：記事の自動カテゴリ分類

**注意点：**
- **文字エンコーディング**：UTF-8対応
- **言語特性**：日本語の形態素解析
- **メモリ使用量**：大規模テキストでのOutOfMemory対策

この演習では、実用的なテキスト処理技術を学びます。

**要求仕様**:
- テキストファイルの読み込みと単語分析
- Stream APIによる文字列処理
- 並列ストリームによる性能向上
- 正規表現と組み合わせた高度な解析

**実行例**:
```
=== テキスト解析システム ===
入力テキスト:
"Java is a programming language. Java is platform independent.
Stream API makes Java programming more functional."

基本統計:
総文字数: 145文字
総単語数: 23語
総行数: 3行
ユニーク単語数: 18語

単語頻度分析（上位5位）:
1位: Java（3回）
2位: is（2回）
3位: programming（2回）
4位: a（1回）
5位: language（1回）

長さ別単語分析:
1-3文字: 8語（35%）
4-6文字: 7語（30%）
7-9文字: 6語（26%）
10文字以上: 2語（9%）

特徴的な単語（6文字以上）:
programming（2回）
language（1回）
platform（1回）
independent（1回）
functional（1回）

並列処理性能テスト:
シーケンシャル処理: 15ms
並列処理: 8ms
性能向上率: 46.7%
```

#### 課題4: データ変換パイプライン（上級）

複雑なデータ変換パイプラインを作成し、Stream APIの高度な機能を実装してください。

**技術的背景：ETLプロセスとデータパイプライン**

現代のデータエンジニアリングにおいて、ETL（Extract, Transform, Load）プロセスはもと幹技術です：

**実際のユースケース：**
- **データウェアハウス構築**：異種データソースの統合
- **リアルタイムダッシュボード**：ストリーミングデータの処理
- **機械学習の前処理**：データクレンジングと特徴量生成
- **マイクロサービス間連携**：データ形式の変換

**カスタムコレクターの実装：**
```java
// 統計情報を収集するカスタムコレクター
public static <T> Collector<T, ?, Statistics<T>> 
    toStatistics(Function<T, Double> mapper) {
    return Collector.of(
        Statistics::new,
        (stats, item) -> stats.accept(mapper.apply(item)),
        (stats1, stats2) -> stats1.combine(stats2),
        Function.identity()
    );
}
```

**ストリームの合成と分岐：**
```java
// Teeingコレクター（Java 12+）を使った同時集計
Map.Entry<List<Integer>, List<Integer>> result = 
    numbers.stream().collect(
        Collectors.teeing(
            Collectors.filtering(n -> n % 2 == 0, Collectors.toList()),
            Collectors.filtering(n -> n % 2 != 0, Collectors.toList()),
            Map::entry
        )
    );
```

**例外処理の実装パターン：**
- **Try-Catchラッパ**：ラムダ内での例外処理
- **Either型**：成功/失敗を表現するデータ型
- **Validation**：複数のエラーを蓄積

**実際の企業での活用：**
- **Netflix**：ビデオストリーミングのメタデータ処理
- **Spotify**：音楽推薦のためのデータ変換
- **Uber**：リアルタイム位置情報の処理

この演習では、プロダクションレベルのデータ処理技術を学びます。

**要求仕様**:
- 複数段階のデータ変換処理
- カスタムコレクターの実装
- ストリームの合成と分岐
- 例外処理を含む安全なパイプライン

**実行例**:
```
=== データ変換パイプライン ===
入力データ: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

パイプライン処理1（数値処理）:
元データ → 偶数フィルタ → 二乗 → 文字列変換
結果: ["4", "16", "36", "64", "100"]

パイプライン処理2（分岐処理）:
奇数グループ: [1, 3, 5, 7, 9]
偶数グループ: [2, 4, 6, 8, 10]
奇数合計: 25
偶数合計: 30

カスタムコレクター使用:
統計情報: {count=10, sum=55, average=5.5, min=1, max=10}

並列パイプライン:
大量データ処理（100万件）
シーケンシャル: 245ms
並列処理: 98ms
性能向上: 60%

例外安全パイプライン:
"123" → 123 ✓
"abc" → エラー（スキップ）
"456" → 456 ✓
"def" → エラー（スキップ）
成功件数: 2件, エラー件数: 2件
```

### 課題の場所
演習課題の詳細と解答例は `exercises/chapter10/` ディレクトリに用意されています：

```
exercises/chapter10/
├── basic/          # 基本課題（必須）
│   ├── README.md   # 課題の詳細説明
│   ├── Employee.java
│   ├── EmployeeAnalyzer.java
│   ├── Sale.java
│   ├── SalesAnalyzer.java
│   └── ...
├── advanced/       # 発展課題（推奨）
└── challenge/      # 挑戦課題（上級者向け）
```

### Stream APIのポイント

1. **中間操作**: filter、map、sorted、distinct等（遅延評価）
2. **終端操作**: collect、reduce、forEach等（実際の処理実行）
3. **並列処理**: parallelStream() で自動並列化
4. **Optional**: null安全なプログラミング
5. **Collectors**: 豊富な集約操作の提供
6. **関数型インターフェイス**: Predicate、Function、Consumer等

### 評価基準

各課題は以下の観点で評価されます：

- **正確性**: 要求仕様を満たしているか
- **効率性**: Stream APIを効果的に活用しているか
- **可読性**: コードが理解しやすく整理されているか

---

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