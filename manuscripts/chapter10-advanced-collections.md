# 第10章 Stream APIと高度なコレクション操作

## 章末演習

本章で学んだStream APIと高度なコレクション操作の概念を活用して、実践的な練習課題に取り組みましょう。

### 演習の目標
- Stream APIの基本概念と利用方法の理解
- 中間操作（filter、map、sorted等）と終端操作（collect、reduce等）の使い分け
- データ処理パイプラインの構築
- 並列ストリームによる性能向上
- Optionalクラスによるnull安全プログラミング
- カスタムCollectorの実装による独自集約処理の開発

### 📁 課題の場所
演習課題は `exercises/chapter10/` ディレクトリに用意されています：

```
exercises/chapter10/
├── basic/          # 基本課題（必須）
│   ├── README.md   # 課題の詳細説明
│   ├── Employee.java # 課題1: 社員データ分析システム
│   ├── EmployeeAnalyzer.java
│   ├── EmployeeAnalyzerTest.java
│   ├── StudentGradeProcessor.java # 課題2: 学生成績処理システム
│   ├── StudentGradeProcessorTest.java
│   ├── LogProcessor.java # 課題3: ログ解析システム
│   ├── LogProcessorTest.java
│   ├── DataAnalyzer.java # 課題4: 高度データ分析
│   ├── DataAnalyzerTest.java
│   ├── CustomCollector.java # 課題5: カスタムコレクター
│   └── CustomCollectorTest.java
├── advanced/       # 発展課題（推奨）
└── challenge/      # 挑戦課題（上級者向け）
```

### 推奨する学習の進め方

1. **基本課題**から順番に取り組む
2. 各課題のREADME.mdで詳細を確認
3. ToDoコメントを参考に実装
4. Stream APIのパイプライン処理を理解する
5. 並列処理と逐次処理の使い分けを習得する

基本課題が完了したら、`advanced/`の発展課題でより複雑なデータ処理パイプラインに挑戦してみましょう！

## 本章の学習目標

### 前提知識
**必須前提**：
- 第9章のラムダ式と関数型インターフェイスの習得
- コレクションフレームワークの実践的な使用経験
- 関数型プログラミングスタイルの基本的な理解

**実践経験前提**：
- データ処理を含む実用的なプログラム開発経験
- ループ処理とデータ変換の実装経験

### 学習目標
**知識理解目標**：
- Stream APIの設計思想と利点
- 中間操作と終端操作の概念と種類
- 遅延評価（lazy evaluation）のしくみ
- 並列ストリーム（parallel stream）の理解と注意点

**技能習得目標**：
- Stream APIを使った効率的なデータ処理
- 複雑なデータ変換とフィルタリングの実装
- Collectorsを使った柔軟なデータ収集
- 並列処理による性能向上の実践

**データ処理能力目標**：
- 大量データの効率的な処理プログラム実装
- 宣言的なデータ処理パイプラインの設計
- 従来の命令型処理との効果的な使い分け

**到達レベルの指標**：
- 複雑なデータ処理要件をStream APIで効率的に実装できる
- カスタムCollectorを使った専用のデータ収集処理が実装できる
- 並列ストリームを適切に活用した高性能データ処理ができる
- 従来のループ処理とStream処理を適切に使い分けできる

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
- **`ArrayList`**: 要素の参照（取得）が多く、リストのサイズがあまり変化しない場合に最適。**ほとんどのケースで第一候補となります。**
- **`LinkedList`**: リストの先頭や中間で頻繁に要素を追加・削除する場合に有効です。

### `HashSet` vs `LinkedHashSet` vs `TreeSet`

| 特徴 | `HashSet` | `LinkedHashSet` | `TreeSet` |
| :--- | :--- | :--- | :--- |
| **順序** | 保証されない | **挿入順** | **ソート順** |
| **性能** | **最速** | `HashSet`よりわずかに遅い | `log(n)`時間（比較的低速） |
| **null要素** | 許可 | 許可 | 不可 |

**使い分けの指針:**
- **`HashSet`**: 順序が不要で、とにかく高速に重複を除きたい場合に最適。
- **`LinkedHashSet`**: 要素を追加した順序を保持したい場合に使う。
- **`TreeSet`**: 要素を常にソートされた状態に保ちたい場合に使う。

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

Java 8で導入された**Stream API**は、コレクションの要素の集まりを「データの流れ（ストリーム）」として扱い、その流れに対してさまざまな処理を連結（パイプライン化）していくしくみです。ラムダ式と組み合わせることで、コレクション操作を非常に宣言的（「何をするか」を記述するスタイル）で、かつ簡潔に書くことができます。

### Stream操作の基本パターン

`コレクション.stream().中間操作1().中間操作2()...終端操作()`

- **ストリームの生成**: `list.stream()`のように、コレクションからストリームを生成します。
- **中間操作**: `filter`（フィルタリング）、`map`（変換）、`sorted`（ソート）など。処理結果として新しいストリームを返します。何度でも連結できます。
- **終端操作**: `forEach`（繰り返し処理）、`collect`（結果をコレクションに集約）、`count`（要素数を数える）など。ストリームの処理を最終的に実行し、結果を返します。

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

## まとめ

本章では、コレクションフレームワークの応用的な側面と、それを操るための現代的な手法を学びました。

- **データ構造の選択**: `ArrayList`, `LinkedList`, `HashSet`, `TreeSet`など、状況に応じて最適な実装クラスを選択することが重要です。
- **ラムダ式と`Comparator`**: ラムダ式を使うことで、独自のソートロジックを簡潔かつ宣言的に記述できます。
- **Stream API**: `filter`, `map`, `sorted`, `collect`などの操作を連結することで、複雑なコレクション操作を直感的に記述できます。

これらの知識を身につけることで、より効率的で、保守性が高く、そして読みやすいJavaコードを書くことが可能になります。