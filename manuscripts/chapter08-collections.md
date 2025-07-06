# 第8章 コレクションフレームワーク

## 章末演習

本章で学んだコレクションフレームワークの概念を活用して、実践的な練習課題に取り組みましょう。

### 🎯 演習の目標
- コレクションフレームワークの基本概念と階層構造の理解
- List、Set、Mapの特性と使い分け
- ArrayList、LinkedList、HashSet、TreeSet、HashMap、TreeMapの実装の違い
- 効率的なデータ構造選択の判断基準
- Iteratorを使った安全な要素操作
- カスタムComparatorとComparableの実装による高度なソート機能

### 演習課題の難易度レベル

#### 🟢 基礎レベル（Basic）
- **目的**: コレクションの基本操作と特性の理解
- **所要時間**: 30-45分/課題
- **前提**: 本章の内容を理解していること
- **評価基準**: 
  - 適切なコレクション型の選択
  - 基本的な操作（追加、削除、検索）の実装
  - 性能特性の理解
  - Iteratorの適切な使用

#### 🟡 応用レベル（Applied）
- **目的**: 実践的なデータ構造活用と最適化
- **所要時間**: 45-60分/課題
- **前提**: 基礎レベルを完了していること
- **評価基準**:
  - 複雑な要件に対する適切な設計
  - パフォーマンスを考慮した実装
  - 大規模データへの対応
  - アルゴリズムの最適化

#### 🔴 発展レベル（Advanced）
- **目的**: 高度なアルゴリズムと商用レベルの実装
- **所要時間**: 60-90分/課題
- **前提**: 応用レベルを完了していること
- **評価基準**:
  - カスタムデータ構造の設計
  - 高度なアルゴリズムの実装
  - 実用的なアプリケーション開発
  - パフォーマンスの極限追求

#### ⚫ 挑戦レベル（Challenge）
- **目的**: 最先端技術への挑戦と革新的実装
- **所要時間**: 90分以上
- **前提**: 発展レベル完了と高度な技術への意欲
- **評価基準**:
  - 分散システムの設計・実装
  - マイクロ秒レベルの最適化
  - AI/機械学習の統合
  - 商用品質のシステム構築

---

## 🟢 基礎レベル課題（必須）

### 課題1: 学生管理システム（List活用）

**学習目標：** Listインターフェイスの理解、ArrayListとLinkedListの性能比較

**問題説明：**
学生情報を管理するシステムを作成し、Listの特性を活用してください。すべての学生は一意のID、名前、成績を持ちます。

**要求仕様：**
1. Studentクラス：
   - privateフィールド： `id`（String）、`name`（String）、`score`（int）
   - 適切なコンストラクタとゲッタの実装
   - toStringメソッドのオーバーライド

2. StudentManagerクラス：
   - Listを使った学生管理（追加、削除、検索）
   - 成績順、名前順でのソート機能
   - ArrayListとLinkedListの性能測定メソッド

**実行例：**
```
=== 学生管理システム（List活用）===
学生を追加:
ID: 001, 名前: 田中太郎, 成績: 85
ID: 002, 名前: 佐藤花子, 成績: 92
ID: 003, 名前: 鈴木一郎, 成績: 78

名前で検索（田中）: 田中太郎（成績: 85）

成績順ソート（降順）:
1位: 佐藤花子（92点）
2位: 田中太郎（85点）
3位: 鈴木一郎（78点）

ArrayList vs LinkedList 性能テスト:
ArrayList 追加時間: 15ms
LinkedList 追加時間: 8ms
ArrayList アクセス時間: 2ms
LinkedList アクセス時間: 45ms
```

**実装ヒント：**
```java
public class StudentManager {
    private List<Student> students;
    
    public StudentManager() {
        this.students = new ArrayList<>();  // またはLinkedList
    }
    
    public void addStudent(Student student) {
        students.add(student);
    }
    
    public void sortByScore() {
        // Comparator.comparingInt()を使用
        students.sort(Comparator.comparingInt(Student::getScore).reversed());
    }
    
    public void performanceTest() {
        // System.currentTimeMillis()で時間測定
        long startTime = System.currentTimeMillis();
        // 処理...
        long endTime = System.currentTimeMillis();
        System.out.println("処理時間: " + (endTime - startTime) + "ms");
    }
}
```

### 課題2: 単語カウンタ（Map活用）

**学習目標：** Mapインターフェイスの理解、HashMapとTreeMapの使い分け

**問題説明：**
テキストの単語出現頻度を集計するシステムを作成し、Mapの特性を活用してください。

**要求仕様：**
1. WordCounterクラス：
   - テキストから単語を抽出して出現回数をカウント
   - 大文字小文字を区別しない
   - 頻度順、アルファベット順でのソート表示
   - 統計情報の提供（総単語数、ユニーク単語数など）

**実行例：**
```
=== 単語カウンター（Map活用）===
入力テキスト: "Java is fun. Java is powerful. Programming with Java is exciting."

単語出現頻度（HashMap使用）:
Java: 3回
is: 3回
fun: 1回
powerful: 1回
Programming: 1回
with: 1回
exciting: 1回

アルファベット順（TreeMap使用）:
exciting: 1回
fun: 1回
is: 3回
Java: 3回
powerful: 1回
Programming: 1回
with: 1回

出現頻度順（上位3位）:
1位: Java（3回）
1位: is（3回）
3位: fun（1回）

統計情報:
総単語数: 9個
ユニーク単語数: 7個
平均出現回数: 1.29回
```

**実装ヒント：**
```java
public class WordCounter {
    private Map<String, Integer> wordCount;
    
    public void countWords(String text) {
        wordCount = new HashMap<>();
        String[] words = text.toLowerCase().split("\\W+");
        
        for (String word : words) {
            if (!word.isEmpty()) {
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }
    }
    
    public void displayByFrequency() {
        // Map.Entryを使ってソート
        wordCount.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue() + "回"));
    }
}
```

### 課題3: 重複除去システム（Set活用）

**学習目標：** Setインターフェイスの理解、集合演算の実装

**問題説明：**
データの重複を除去するシステムを作成し、Setの特性を活用してください。

**要求仕様：**
1. DuplicateRemoverクラス：
   - 文字列リストから重複を除去
   - HashSetとTreeSetの使い分け
   - 集合演算（和集合、積集合、差集合）の実装
   - カスタムオブジェクトの重複判定

**実行例：**
```
=== 重複除去システム（Set活用）===
元のリスト: [apple, banana, apple, orange, banana, grape]

HashSet で重複除去（挿入順不保証）:
結果: [banana, orange, apple, grape]
処理時間: 5ms

TreeSet で重複除去（自動ソート）:
結果: [apple, banana, grape, orange]
処理時間: 12ms

集合演算テスト:
セットA: [Java, Python, C++]
セットB: [Python, JavaScript, Java]

和集合: [Java, Python, C++, JavaScript]
積集合: [Java, Python]
差集合（A-B）: [C++]
差集合（B-A）: [JavaScript]

カスタムオブジェクト重複除去:
Person{name='田中', age=25}
Person{name='佐藤', age=30}
Person{name='鈴木', age=25}
```

### 課題4: 在庫管理システム（複合コレクション活用）

**学習目標：** 複数のコレクションを組み合わせた実践的なシステム設計

**問題説明：**
商品在庫を管理するシステムを作成し、複数のコレクションを組み合わせて活用してください。

**要求仕様：**
1. Productクラス（ID、名前、価格、カテゴリ）
2. InventoryManagerクラス：
   - カテゴリ別商品管理（Map<String, List<Product>>）
   - 価格帯別集計（TreeMap使用）
   - 在庫検索・フィルタリング機能
   - 統計情報の提供

**実行例：**
```
=== 在庫管理システム（複合コレクション活用）===
商品登録:
[家電] ノートPC（150,000円）
[家電] スマートフォン（80,000円）
[書籍] Java入門（3,000円）
[書籍] Python基礎（2,500円）
[食品] りんご（500円）

カテゴリ別在庫:
家電カテゴリ: 2商品
  - ノートPC: 150,000円
  - スマートフォン: 80,000円
書籍カテゴリ: 2商品
  - Java入門: 3,000円
  - Python基礎: 2,500円
食品カテゴリ: 1商品
  - りんご: 500円

価格帯別集計:
0-1,000円: 1商品
1,001-10,000円: 2商品
10,001-100,000円: 1商品
100,001円以上: 1商品

検索結果（価格5,000円以下）:
Java入門（3,000円）
Python基礎（2,500円）
りんご（500円）

統計情報:
総商品数: 5個
平均価格: 47,200円
最高価格: 150,000円（ノートPC）
最低価格: 500円（りんご）
```

---

## 🟡 応用レベル課題（推奨）

### 課題1: 動的配列ベースのデータベース

**学習目標：** ArrayListを使った高性能なインメモリデータベースの実装

**問題説明：**
ArrayListを使用して、簡易的なインメモリデータベースを実装してください。CRUD操作、インデックス機能、トランザクション管理などを含みます。

**要求仕様：**
- 動的なスキーマ管理
- インデックスによる高速検索
- トランザクション機能
- クエリ最適化

### 課題2: ソート可能オブジェクトシステム

**学習目標：** 複数基準でのソート、ComparatorとComparableの活用

**問題説明：**
学生情報を管理し、複数の基準（名前、成績、学籍番号）でソートできるシステムを作成してください。

**要求仕様：**
- Comparableインターフェイスの実装
- 複数のComparatorの作成
- ソート基準の動的切り替え
- 安定ソートの実現

---

## 🔴 発展レベル課題（挑戦）

### 高度なコレクション活用

**学習目標：** 実用的なアプリケーションレベルのコレクション活用

**課題内容：**
- カスタムコレクションクラスの作成
- 並行処理対応のコレクション活用
- メモリ効率を考慮した大規模データ処理
- 高度な検索・フィルタリング機能

---

## ⚫ 挑戦レベル課題（上級者向け）

### 課題1: 分散インメモリ検索エンジン

**学習目標：** 分散システムアーキテクチャ、検索エンジン技術、機械学習の統合

**問題説明：**
Elasticsearch風の分散検索エンジンをコレクションフレームワークベースで実装してください。

**要求仕様：**
- 全文検索エンジンの実装
- 分散インデックス管理
- リアルタイム検索
- 機械学習による関連度スコアリング

### 課題2: 高頻度取引システム

**学習目標：** マイクロ秒レベルの超高性能実装、金融システム特有の要求への対応

**問題説明：**
金融市場の高頻度取引システムを極限の性能で実装してください。

**要求仕様：**
- マイクロ秒レベルの取引実行
- リアルタイム市場データ処理
- リスク管理システム
- 機械学習による価格予測

### 課題3: 自律分散ロボット制御システム

**学習目標：** 分散協調制御、リアルタイムシステム、AI統合

**問題説明：**
複数ロボットが協調する自律分散制御システムを実装してください。

**要求仕様：**
- リアルタイム分散協調制御
- 動的環境認識・地図生成
- 群知能アルゴリズム
- 人間との協調作業

---

## 💡 実装のヒント

### コレクション選択の指針
1. **List**: 順序が重要、インデックスアクセスが必要な場合
2. **Set**: 重複を許可しない、高速な存在確認が必要な場合
3. **Map**: キーと値のペアで管理、高速な検索が必要な場合

### パフォーマンスの考慮
- **ArrayList vs LinkedList**: ランダムアクセスが多い → ArrayList、挿入・削除が多い → LinkedList
- **HashMap vs TreeMap**: 順序不要で高速 → HashMap、ソート順が必要 → TreeMap
- **HashSet vs TreeSet**: 順序不要で高速 → HashSet、ソート順が必要 → TreeSet

### メモリ効率
- 初期容量の適切な設定
- 不要な要素の削除
- 適切なコレクション型の選択

---

## 🔗 実装環境

演習課題の詳細な実装テンプレート、テストコード、解答例は以下のディレクトリを参照してください：

```
exercises/chapter08/
├── basic/          # 基礎レベル課題
│   ├── README.md   # 詳細な課題説明
│   ├── Student.java
│   ├── StudentManager.java
│   └── StudentManagerTest.java
├── advanced/       # 応用・発展レベル課題
│   └── README.md
├── challenge/      # 挑戦レベル課題
└── solutions/      # 解答例（実装完了後に参照）
```

---

## ✅ 完了確認チェックリスト

### 基礎レベル
- [ ] 学生管理システムでListの特性を理解できている
- [ ] 単語カウンタでMapを効率的に使用できている
- [ ] 重複除去システムでSetの特性を活用できている
- [ ] 在庫管理システムで複合コレクションを設計できている

### 技術要素
- [ ] 各コレクションの時間計算量を理解している
- [ ] 適切なコレクション選択ができている
- [ ] Iteratorを使った安全な操作ができている
- [ ] ComparatorとComparableを使い分けられている

### 応用・発展レベル
- [ ] 大規模データを効率的に処理できている
- [ ] カスタムデータ構造を設計できている
- [ ] パフォーマンスを考慮した実装ができている
- [ ] 実用的なアプリケーションを構築できている

## 本章の学習目標

### 前提知識
**必須前提**：
- 第6章のコレクションフレームワークの理解と実践経験
- 配列の基本的な使用経験
- 関数型プログラミングスタイルの基本的な理解

**実践経験前提**：
- データ処理を含む実用的なプログラム開発経験
- ループ処理とデータ変換の実装経験

### 学習目標
**知識理解目標**：
- コレクションフレームワークの全体設計と階層構造
- List、Set、Mapインターフェイスの特徴と使い分け
- 各実装クラス（ArrayList、LinkedList、HashSet、TreeSet、HashMap等）の性能特性
- イテレータパターンの理解

**技能習得目標**：
- 適切なコレクション型の選択と使用
- コレクションを使った効率的なデータ処理
- for-each文とイテレータの使い分け
- コレクションのソートと検索の実装

**アルゴリズム的思考目標**：
- データ構造と処理効率の関係の理解
- 問題に応じた最適なデータ構造の選択能力
- メモリ使用量と実行速度のトレードオフの理解

**到達レベルの指標**：
- 複雑なデータ処理要件に対して適切なコレクションが選択・使用できる
- 大量データの効率的な処理プログラムが実装できる
- カスタムComparatorを使った複雑なソート処理が実装できる
- メモリ効率と実行効率を考慮したプログラムが作成できる
- 拡張for文やイテレータを使って、コレクションの全要素を処理できる。
- 配列と`List`を相互に変換できる。

---

## 8.1 データ管理の進化：配列からコレクションへ

Javaプログラミングの初期に学ぶ配列は、複数のデータをまとめて管理するための基本的な手段です。しかし、実用的なアプリケーションを開発するには、配列だけでは不十分な場面が多くあります。

### 配列の限界

配列には、主に2つの大きな制限があります。

1.  **サイズが固定**: 配列は、一度作成するとそのサイズを変更できません。
2.  **機能が限定的**: 要素の追加や削除、検索といった一般的な操作を自前で実装する必要があり、手間がかかります。

```java
// 配列の例：要素を追加するのも一苦労
String[] users = new String[3];
users[0] = "Alice";
users[1] = "Bob";
users[2] = "Charlie";
// これ以上ユーザーを追加するには、より大きな新しい配列を作って、
// 全ての要素をコピーし直す必要がある。
```

こうした配列の不便さを解消し、より柔軟で高機能なデータ管理を実現するのが**コレクションフレームワーク**です。

### コレクションフレームワークとは？

Javaのコレクションフレームワークは、データを効率的に扱うためのさまざまな「データ構造」を、再利用可能なクラスやインターフェイスとして体系的にまとめたものです。

- **動的なサイズ**: 要素の数に応じて自動的にサイズが調整されます。
- **豊富な機能**: 要素の追加、削除、検索、ソートなどの便利なメソッドが標準で提供されています。
- **ポリモーフィズム**: `List`, `Set`, `Map`といったインターフェイスを通じて実装を切り替え可能で、柔軟な設計ができます。
- **型安全**: ジェネリクスにより、コレクションに格納するデータの型をコンパイル時に保証します。

## 8.2 `List`インターフェイス：順序のあるデータ列

`List`は、**順序付けられた**要素のコレクションで、**重複を許可します**。追加した順に要素が格納され、インデックス（添え字）によって各要素にアクセスできます。配列に最も近い感覚で使えます。

### 代表的な実装クラス

*   `java.util.ArrayList`: 内部的に配列を使用。要素へのランダムアクセス（インデックス指定での取得）が非常に高速。
*   `java.util.LinkedList`: 内部的に双方向連結リスト構造を使用。要素の追加や削除（特にリストの先頭や中間）が高速。

### `ArrayList`の基本的な使い方

```java
import java.util.ArrayList;
import java.util.List;

public class ArrayListExample {
    public static void main(String[] args) {
        // String型の要素を格納するArrayListを作成
        // 変数の型はインターフェイス型で宣言するのが一般的
        List<String> fruits = new ArrayList<>();

        // 1. 要素の追加 (add)
        fruits.add("りんご");
        fruits.add("バナナ");
        fruits.add("みかん");
        fruits.add("りんご"); // 重複も許可される

        System.out.println("現在のリスト: " + fruits);

        // 2. 要素の取得 (get)
        String secondFruit = fruits.get(1); // インデックスは0から始まる
        System.out.println("2番目の果物: " + secondFruit);

        // 3. 要素数の取得 (size)
        System.out.println("果物の数: " + fruits.size());

        // 4. 要素の削除 (remove)
        fruits.remove("バナナ"); // 値で削除
        fruits.remove(0);      // インデックスで削除
        System.out.println("削除後のリスト: " + fruits);

        // 5. 全要素の反復処理 (for-each)
        System.out.println("残りの果物:");
        for (String fruit : fruits) {
            System.out.println("- " + fruit);
        }
    }
}
```

### `ArrayList` vs `LinkedList`

| 特徴 | `ArrayList` (内部配列) | `LinkedList` (連結リスト) |
| :--- | :--- | :--- |
| **要素の取得(get)** | **高速** | 遅い |
| **要素の追加/削除(中間)** | 遅い（後続要素のシフトが発生） | **高速** |
| **メモリ使用量** | 比較的少ない | やや多い（前後の要素への参照を持つため） |

**使い分けの目安:**
*   **`ArrayList`**: 要素の参照（読み取り）が多く、リストのサイズがあまり変化しない場合に最適。
*   **`LinkedList`**: 要素の追加や削除が頻繁に発生する場合（特にリストの先頭や中間）に適している。

### Listの要素を順に処理する方法

#### 拡張for文 (推奨)
最も簡潔で一般的な方法です。
```java
for (String name : nameList) {
    System.out.println(name);
}
```

#### イテレータ (Iterator)
ループ中にコレクションから要素を安全に削除したい場合に使います。
```java
Iterator<String> iterator = cityList.iterator();
while (iterator.hasNext()) {
    String city = iterator.next();
    if (city.equals("Kyoto")) {
        iterator.remove(); // 安全に削除
    }
}
```

### 配列とListの相互変換

`java.util.Arrays`クラスや`List`インターフェイスのメソッドを利用します。

```java
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

// 配列からListへ
String[] nameArray = {"Alice", "Bob"};
List<String> nameList = new ArrayList<>(Arrays.asList(nameArray));
nameList.add("Charlie"); // 変更可能

// Listから配列へ
List<String> fruitList = new ArrayList<>();
fruitList.add("Apple");
fruitList.add("Banana");
String[] fruitArray = fruitList.toArray(new String[0]);
```
**注意:** `Arrays.asList()`が直接返す`List`はサイズが固定されており、`add`や`remove`ができないため、変更可能な`ArrayList`でラップするのが一般的です。

## 8.3 `Set`インターフェイス：重複のないユニークな集合

`Set`は、**重複しない**要素のコレクションです。「集合」という数学的な概念に対応します。ユーザーIDやメールアドレスなど、一意性を保証したいデータの管理に適しています。

### 代表的な実装クラス

*   `java.util.HashSet`: 最も一般的。順序は保証されないが、追加・検索が非常に高速。
*   `java.util.LinkedHashSet`: 要素が**挿入された順序**を保持する。
*   `java.util.TreeSet`: 要素を**自然順序（ソート順）**で保持する。

### `HashSet`の基本的な使い方

```java
import java.util.HashSet;
import java.util.Set;

public class HashSetExample {
    public static void main(String[] args) {
        Set<String> tags = new HashSet<>();

        // 1. 要素の追加 (add)
        tags.add("Java");
        tags.add("プログラミング");
        boolean added = tags.add("Java"); // すでに存在するため追加されない (falseが返る)

        System.out.println("タグ一覧: " + tags); // 順序は保証されない

        // 2. 要素の存在確認 (contains)
        if (tags.contains("プログラミング")) {
            System.out.println("「プログラミング」タグは存在します。");
        }
    }
}
```

## 8.4 `Map`インターフェイス：キーと値のペア

`Map`は、一意の**キー（Key）** と、それに対応する**値（Value）** のペアを格納するコレクションです。キーを使って高速に値を取得できます。辞書や連想配列とも呼ばれます。

### 代表的な実装クラス

*   `java.util.HashMap`: 最も一般的。キーの順序は保証されないが、非常に高速。
*   `java.util.LinkedHashMap`: キーが**挿入された順序**を保持する。
*   `java.util.TreeMap`: キーを**自然順序（ソート順）**で保持する。

### `HashMap`の基本的な使い方

```java
import java.util.HashMap;
import java.util.Map;

public class HashMapExample {
    public static void main(String[] args) {
        // 学生の点数を管理 (キー: 名前, 値: 点数)
        Map<String, Integer> scores = new HashMap<>();

        // 1. 要素の追加 (put)
        scores.put("Alice", 85);
        scores.put("Bob", 92);
        scores.put("Alice", 95); // 同じキーでputすると、値が上書きされる

        System.out.println("全員の点数: " + scores);

        // 2. 要素の取得 (get)
        int bobScore = scores.get("Bob");
        System.out.println("Bobの点数: " + bobScore);

        // 3. 全要素の反復処理 (entrySetとfor-each)
        System.out.println("--- 点数一覧 ---");
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            String name = entry.getKey();
            int score = entry.getValue();
            System.out.println(name + ": " + score + "点");
        }
    }
}
```

## まとめ

本章では、コレクションフレームワークの3つの基本インターフェイスと、その代表的な実装クラスを学びました。

| インターフェイス | 特徴 | 代表的な実装 | 主な用途 |
| :--- | :--- | :--- | :--- |
| `List` | 順序あり、重複OK | `ArrayList`, `LinkedList` | 順番にデータを並べたいとき |
| `Set` | 順序なし(一部あり)、重複NG | `HashSet`, `LinkedHashSet` | 重複をなくしたいとき、順序が必要な場合も |
| `Map` | キーと値のペア | `HashMap`, `TreeMap` | IDと情報のように紐付けて管理したいとき |

これらのコレクションを適切に使い分けることが、効率的で読みやすいプログラムを書くための第一歩です。次章では、これらのコレクションを安全かつ柔軟に扱うための「ジェネリクス」について詳しく学びます。