# 第10章 Stream APIと高度なコレクション操作

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

## 章末演習

本章で学んだStream APIと高度なコレクション操作の概念を活用して、実践的な練習課題に取り組みましょう。

### 課題一覧

#### 🟢 課題1: 社員データ分析システム（基本）
**難易度**: ★★☆☆☆

社員データを分析するシステムを作成し、Stream APIを活用してください。

**要求仕様**:
- Employee クラス（名前、年齢、部署、給与）
- Stream API による多様な分析処理
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

**実装のヒント**:
- Collectors.groupingBy() で部署別グループ化
- Collectors.averagingDouble() で平均給与計算
- Comparator.comparing() でソート条件指定

#### 🟢 課題2: 商品売上分析システム（基本）
**難易度**: ★★★☆☆

商品売上データを分析するシステムを作成し、高度なStream処理を実装してください。

**要求仕様**:
- Sale クラス（商品名、カテゴリ、売上金額、売上日）
- 期間別・カテゴリ別の売上分析
- Stream API の高度な操作（flatMap、partition等）
- Optional を活用した安全なデータアクセス

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

**実装のヒント**:
- LocalDate.parse() で日付変換
- Stream.flatMap() で入れ子構造の平坦化
- Optional.map() でチェーン処理

#### 🟡 課題3: テキスト解析システム（応用）
**難易度**: ★★★★☆

テキストデータを解析するシステムを作成し、文字列処理でのStream活用を実装してください。

**要求仕様**:
- テキストファイルの読み込みと単語分析
- Stream API による文字列処理
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

**実装のヒント**:
- Files.lines() でファイル読み込み
- String.split() と Pattern.compile() で単語分割
- parallelStream() で並列処理

#### 🔴 課題4: データ変換パイプライン（上級）
**難易度**: ★★★★★

複雑なデータ変換パイプラインを作成し、Stream APIの高度な機能を実装してください。

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

**実装のヒント**:
- Collector.of() でカスタムコレクター作成
- Stream.concat() でストリーム結合
- try-catch と filter() で例外処理

### 📁 課題の場所
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
- **エラー処理**: 例外やnullを適切に処理しているか
- **パフォーマンス**: 必要に応じて並列処理を活用しているか