# 第8章 コレクションフレームワーク

## 本章の学習目標

本章では、Javaのコレクションフレームワークについて学習します。配列は固定サイズのデータ構造ですが、実際のプログラムでは動的にサイズが変化するデータを扱う必要が頻繁にあります。コレクションフレームワークは、このようなニーズに応える柔軟で強力なデータ構造を提供します。

### 前提知識
- 第6章までの基本的なJava文法
- 配列の使い方と限界の理解
- インターフェイスの基本概念
- ジェネリクスの基礎（第9章で詳しく学習）

### 学習内容
- コレクションフレームワークの全体像と設計思想
- List、Set、Mapの基本的な使い方と特徴
- 各実装クラス（ArrayList、HashSet、HashMap等）の使い分け
- イテレータによる要素の走査
- コレクションの実践的な活用方法

## 8.1 なぜコレクションフレームワークが必要か

### 配列の限界

配列は基本的なデータ構造ですが、実践的なプログラミングでは以下の限界に直面します：

```java
// 配列の問題点1: サイズが固定
String[] students = new String[30];  // 30人分しか格納できない
// 31人目の学生を追加したい場合は？

// 配列の問題点2: 要素の削除が困難
// 配列の中間要素を削除すると「穴」ができる
students[5] = null;  // 削除？でも配列のサイズは変わらない

// 配列の問題点3: 便利なメソッドがない
// 要素の検索、ソート、重複除去などは自分で実装する必要がある
```

### コレクションフレームワークの利点

コレクションフレームワークはこれらの問題を解決します：

```java
// 動的なサイズ変更
List<String> studentList = new ArrayList<>();
studentList.add("田中");    // いくらでも追加可能
studentList.add("佐藤");
studentList.remove("田中");  // 簡単に削除

// 重複を許さないデータ構造
Set<String> uniqueNames = new HashSet<>();
uniqueNames.add("山田");
uniqueNames.add("山田");  // 2回目は追加されない

// キーと値のペアを管理
Map<String, Integer> scores = new HashMap<>();
scores.put("田中", 85);
scores.put("佐藤", 92);
```

## 8.2 コレクションフレームワークの全体像

Javaのコレクションフレームワークは、以下の主要なインターフェイスで構成されています：

### 主要インターフェイス

1. **Collection**: すべてのコレクションの基底インターフェイス
   - **List**: 順序付きコレクション（重複可）
   - **Set**: 重複を許さないコレクション
   - **Queue**: FIFO（先入れ先出し）のコレクション

2. **Map**: キーと値のペアを管理（Collectionとは独立）

### インターフェイス階層

```
Collection
├── List
│   ├── ArrayList
│   ├── LinkedList
│   └── Vector（レガシー）
├── Set
│   ├── HashSet
│   ├── TreeSet
│   └── LinkedHashSet
└── Queue
    ├── LinkedList（Listとの二重実装）
    └── PriorityQueue

Map
├── HashMap
├── TreeMap
├── LinkedHashMap
└── Hashtable（レガシー）
```

## 8.3 List インターフェイス

Listは順序を保持し、インデックスによるアクセスが可能なコレクションです。

### ArrayList vs LinkedList

```java
// ArrayList: 内部的に配列を使用
List<String> arrayList = new ArrayList<>();
arrayList.add("要素1");
arrayList.add("要素2");
String element = arrayList.get(0);  // 高速なランダムアクセス

// LinkedList: 双方向連結リストを使用
List<String> linkedList = new LinkedList<>();
linkedList.add("要素1");
linkedList.add(0, "先頭に挿入");  // 先頭への挿入が高速
```

**使い分けの指針：**
- **ArrayList**: ランダムアクセスが多い場合（get/set操作）
- **LinkedList**: 先頭・末尾での追加・削除が多い場合

### 主な操作

```java
List<String> list = new ArrayList<>();

// 追加
list.add("Java");
list.add(0, "Hello");  // 指定位置に挿入

// 取得
String first = list.get(0);
int size = list.size();

// 削除
list.remove("Java");
list.remove(0);  // インデックスで削除

// 検索
boolean contains = list.contains("Hello");
int index = list.indexOf("Java");

// 繰り返し処理
for (String item : list) {
    System.out.println(item);
}
```

## 8.4 Set インターフェイス

Setは重複要素を許さないコレクションです。数学的な集合の概念に対応します。

### HashSet vs TreeSet vs LinkedHashSet

```java
// HashSet: ハッシュテーブルを使用（順序は保証されない）
Set<Integer> hashSet = new HashSet<>();
hashSet.add(3);
hashSet.add(1);
hashSet.add(2);
// 出力順序は不定

// TreeSet: ソートされた順序を維持
Set<Integer> treeSet = new TreeSet<>();
treeSet.add(3);
treeSet.add(1);
treeSet.add(2);
// 出力: 1, 2, 3（昇順）

// LinkedHashSet: 挿入順序を維持
Set<Integer> linkedHashSet = new LinkedHashSet<>();
linkedHashSet.add(3);
linkedHashSet.add(1);
linkedHashSet.add(2);
// 出力: 3, 1, 2（挿入順）
```

### 集合演算

```java
Set<String> set1 = new HashSet<>(Arrays.asList("A", "B", "C"));
Set<String> set2 = new HashSet<>(Arrays.asList("B", "C", "D"));

// 和集合
Set<String> union = new HashSet<>(set1);
union.addAll(set2);  // {A, B, C, D}

// 積集合
Set<String> intersection = new HashSet<>(set1);
intersection.retainAll(set2);  // {B, C}

// 差集合
Set<String> difference = new HashSet<>(set1);
difference.removeAll(set2);  // {A}
```

## 8.5 Map インターフェイス

Mapはキーと値のペアを管理するデータ構造です。辞書やハッシュテーブルとも呼ばれます。

### HashMap vs TreeMap vs LinkedHashMap

```java
// HashMap: 高速な検索・挿入（順序は保証されない）
Map<String, Integer> hashMap = new HashMap<>();
hashMap.put("Java", 25);
hashMap.put("Python", 30);
hashMap.put("JavaScript", 27);

// TreeMap: キーでソートされた順序を維持
Map<String, Integer> treeMap = new TreeMap<>();
treeMap.putAll(hashMap);
// キーのアルファベット順で格納

// LinkedHashMap: 挿入順序を維持
Map<String, Integer> linkedHashMap = new LinkedHashMap<>();
linkedHashMap.putAll(hashMap);
// 挿入した順序で格納
```

### 主な操作

```java
Map<String, String> map = new HashMap<>();

// 追加・更新
map.put("JP", "日本");
map.put("US", "アメリカ");

// 取得
String country = map.get("JP");
String defaultValue = map.getOrDefault("UK", "不明");

// 削除
map.remove("US");

// 存在確認
boolean hasKey = map.containsKey("JP");
boolean hasValue = map.containsValue("日本");

// すべてのキー・値を処理
for (String key : map.keySet()) {
    System.out.println(key + ": " + map.get(key));
}

// エントリーセットで処理（より効率的）
for (Map.Entry<String, String> entry : map.entrySet()) {
    System.out.println(entry.getKey() + ": " + entry.getValue());
}
```

## 8.6 イテレータとfor-each文

### イテレータの基本

```java
List<String> list = Arrays.asList("A", "B", "C");

// イテレータを使った走査
Iterator<String> iter = list.iterator();
while (iter.hasNext()) {
    String element = iter.next();
    System.out.println(element);
}

// 走査中の安全な削除
Iterator<String> iter2 = list.iterator();
while (iter2.hasNext()) {
    String element = iter2.next();
    if (element.equals("B")) {
        iter2.remove();  // イテレータ経由で削除
    }
}
```

### 拡張for文（for-each）

```java
// より簡潔な記述
for (String element : list) {
    System.out.println(element);
}

// ただし、走査中の削除はできない
// for (String element : list) {
//     if (element.equals("B")) {
//         list.remove(element);  // ConcurrentModificationException!
//     }
// }
```

## 8.7 コレクションの選択指針

### パフォーマンス特性

| 操作 | ArrayList | LinkedList | HashSet | TreeSet | HashMap | TreeMap |
|------|-----------|------------|---------|---------|---------|---------|
| 追加（末尾） | O(1) | O(1) | O(1) | O(log n) | O(1) | O(log n) |
| 削除（インデックス） | O(n) | O(n) | - | - | - | - |
| 検索（値） | O(n) | O(n) | O(1) | O(log n) | - | - |
| 取得（インデックス） | O(1) | O(n) | - | - | - | - |
| 取得（キー） | - | - | - | - | O(1) | O(log n) |

### 使用場面の例

1. **ArrayList**: 
   - 学生名簿、商品リスト
   - インデックスアクセスが多い場合

2. **LinkedList**:
   - 待ち行列、履歴管理
   - 先頭・末尾での操作が多い場合

3. **HashSet**:
   - ユニークなID管理、重複チェック
   - 順序が重要でない場合

4. **TreeSet**:
   - ランキング、ソート済みデータ
   - 常にソートされた状態が必要な場合

5. **HashMap**:
   - 辞書、キャッシュ、設定値管理
   - キーによる高速検索が必要な場合

6. **TreeMap**:
   - ソート済み辞書、範囲検索
   - キーの順序が重要な場合

## 章末演習

本章で学んだコレクションフレームワークの概念を活用して、実践的な練習課題に取り組みましょう。

### 演習の目標
本章で学んだList、Set、Mapの特性を活かした実装を行います。

**注意：** 第8章以降は、問題文と期待される動作のみを提示します。実装方法は自分で考えてください。
課題は段階的に難易度が上がるよう設計されています。基礎レベルでコレクションの基本を習得し、応用レベルで実践的な活用方法を学び、挑戦レベルでより高度な設計パターンに取り組みます。



## 基礎レベル課題（必須）

### 課題1: 学生管理システム（List活用）

**学習目標：** Listインターフェイスの理解、ArrayListとLinkedListの性能比較

**問題説明：**
学生情報を管理するシステムを作成し、Listの特性を活用してください。すべての学生は一意のID、名前、成績を持ちます。

**技術的背景：データ構造の選択がもたらす性能影響**

実際の業務システムでは、データ構造の選択が性能に大きく影響します。学生管理システムのような場面では：

**ArrayListが適している場合：**
- **成績一覧の表示**：インデックスによる高速アクセス（O(1)）が必要
- **ランキング表示**：特定順位の学生をすばやく取得
- **一括処理**：全学生のデータを順次処理

**LinkedListが適している場合：**
- **リアルタイム登録システム**：頻繁な追加・削除が発生
- **待機リスト管理**：先頭・末尾での操作が多い
- **履歴管理**：新しいデータを先頭に追加し続ける

実際の事例：
- **大学の成績管理システム**：数万人の学生データを扱う際、不適切なList実装により検索が数秒かかる問題が発生
- **オンライン試験システム**：受験者リストの管理でLinkedListを使用し、ランダムアクセスで性能劣化

この演習では、データ構造の特性を理解し、要件に応じた最適な選択ができる能力を養います。

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


### 課題2: 単語カウンタ（Map活用）

**学習目標：** Mapインターフェイスの理解、HashMapとTreeMapの使い分け

**問題説明：**
テキストの単語出現頻度を集計するシステムを作成し、Mapの特性を活用してください。

**技術的背景：大規模テキスト処理とMap実装の選択**

単語カウントは、多くの実用システムで必要とされる基本的な処理です：

**実際の応用例：**
- **検索エンジン**：Webページの単語出現頻度からTF-IDF値を計算
- **スパムフィルタ**：特定単語の出現パターンからスパム判定
- **自然言語処理**：形態素解析後の単語統計
- **ログ解析**：エラーメッセージの頻度分析

**Map実装の使い分け：**
- **HashMap**：高速な検索・更新が必要な場合（O(1)の平均計算量）
- **TreeMap**：常にソートされた状態を維持する必要がある場合（O(log n)）
- **LinkedHashMap**：挿入順序を保持したい場合

実際の問題事例：
- **ニュースサイトのトレンド分析**：リアルタイムで数百万の記事から単語を集計する際、TreeMapを使用してメモリ不足
- **SNS分析システム**：ハッシュ衝突により、特定の単語でHashMapの性能が劣化

この演習では、テキスト処理の基本パターンと、データ構造選択の重要性を学びます。

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

**技術的背景：データクレンジングと集合演算の実用性**

重複データの問題は、多くのシステムで深刻な影響を及ぼします：

**実際の問題事例：**
- **顧客データベース**：同一顧客の重複登録により、マーケティング費用が倍増
- **在庫管理システム**：商品コードの重複により、在庫数の不整合が発生
- **メール配信システム**：アドレスの重複により、同じユーザーに複数回配信

**Setを使った解決策：**
- **データインポート時の重複チェック**：CSVファイルからのデータ取り込み
- **マスターデータの整合性確保**：定期的な重複除去バッチ処理
- **リアルタイム重複検知**：新規登録時の即座のチェック

**集合演算の実用例：**
- **権限管理**：ユーザーの権限 = 個人権限 ∪ グループ権限
- **商品推薦**：推薦商品 = (類似ユーザーの購入商品 ∩ 在庫あり商品) - 購入済み商品
- **差分同期**：更新対象 = 新データ - 既存データ

この演習では、データ品質管理の基本技術と、集合論の実践的応用を学びます。

**要求仕様：**
1. DuplicateRemoverクラス：
   - 文字列リストから重複を除去
   - HashSetとTreeSetの使い分け
   - 集合演算（和集合、積集合、差集合）の実装
   - カスタムオブジェクトの重複判定

**実行例：**
```
=== 重複除去システム（Set活用）===
元のリスト：[Apple, banana, Apple, orange, banana, grape]

HashSetで重複除去（挿入順不保証）:
結果：[banana, orange, Apple, grape]
処理時間： 5ms

TreeSetで重複除去（自動ソート）:
結果：[Apple, banana, grape, orange]
処理時間： 12ms

集合演算テスト：
セットA:[Java, Python, C++]
セットB:[Python, JavaScript, Java]

和集合：[Java, Python, C++, JavaScript]
積集合：[Java, Python]
差集合（A-B）:[C++]
差集合（B-A）:[JavaScript]

カスタムオブジェクト重複除去：
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
商品登録：
[家電］ノートPC（150,000円）
[家電］スマートフォン（80,000円）
[書籍］Java入門（3,000円）
[書籍］Python基礎（2,500円）
[食品］りんご（500円）

カテゴリ別在庫：
家電カテゴリ： 2商品
  - ノートPC: 150,000円
  - スマートフォン： 80,000円
書籍カテゴリ： 2商品
  - Java入門： 3,000円
  - Python基礎： 2,500円
食品カテゴリ： 1商品
  - りんご： 500円

価格帯別集計：
0-1,000円： 1商品
1,001-10,000円： 2商品
10,001-100,000円： 1商品
100,001円以上： 1商品

検索結果（価格5,000円以下）:
Java入門（3,000円）
Python基礎（2,500円）
りんご（500円）

統計情報：
総商品数： 5個
平均価格： 47,200円
最高価格： 150,000円（ノートPC）
最低価格： 500円（りんご）
```



## 応用レベル課題（推奨）

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



## 発展レベル課題（挑戦）

### 高度なコレクション活用

**学習目標：** 実用的なアプリケーションレベルのコレクション活用

**課題内容：**
- カスタムコレクションクラスの作成
- 並行処理対応のコレクション活用
- メモリ効率を考慮した大規模データ処理
- 高度な検索・フィルタリング機能



## 挑戦レベル課題（上級者向け）

### 課題1: 簡易テキスト検索エンジン

**学習目標：** 転置インデックス、検索アルゴリズム、コレクションの高度な活用

**問題説明：**
複数のテキストファイルから単語を検索できる簡易検索エンジンを実装してください。

**要求仕様：**
- 単語の出現位置を記録する転置インデックス（Map<String, List<DocumentPosition>>）
- AND/OR検索の実装
- 検索結果のランキング（出現頻度順）
- 大文字小文字を区別しない検索

**実装のポイント：**
- HashMapで単語をキーとしたインデックス管理
- TreeMapで検索結果の自動ソート
- SetとListを組み合わせた効率的な検索処理

### 課題2: 図書館管理システム

**学習目標：** 複数コレクションの連携、実用的なCRUD操作、データの一貫性管理

**問題説明：**
図書の貸出管理ができる図書館システムを実装してください。

**要求仕様：**
- 書籍管理（Map<String, Book>でISBNをキーに管理）
- 利用者管理（Map<String, User>で利用者IDをキーに管理）
- 貸出履歴（List<LoanRecord>で時系列管理）
- 期限切れ図書の検索（TreeSetで日付順管理）
- ジャンル別・著者別の集計機能

**実装のポイント：**
- 適切なコレクションの選択による効率化
- データの整合性チェック（同じ本の二重貸出防止など）
- 日付を使った期限管理の実装

### 課題3: ゲームのランキングシステム

**学習目標：** ソート済みコレクション、リアルタイム更新、効率的なデータ構造

**問題説明：**
オンラインゲームのスコアランキングシステムを実装してください。

**要求仕様：**
- プレイヤーのスコア管理（TreeMapで自動ソート）
- トップ10ランキングの表示
- 同点の場合の順位付けルール
- 週間・月間ランキングの管理
- プレイヤーの順位検索機能

**実装のポイント：**
- TreeMapとComparatorを使った効率的なランキング管理
- 複数期間のランキングを効率的に管理する方法
- 大量データでも高速に動作する設計



## 実装のヒント

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



## 実装環境

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



## 完了確認チェックリスト

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

本章を学習するための必須前提として、第6章で学んだコレクションフレームワークの基本概念を理解し、実際に使用した経験が必要です。特に、データを効率的に管理する必要性と、配列の限界について体験的に理解していることが重要です。また、配列を使った基本的なプログラミング経験があり、複数のデータを扱う処理に慣れていることが求められます。さらに、Java 8以降で導入された関数型プログラミングスタイルの基本的な考え方、特にラムダ式やメソッド参照について触れた経験があることで、本章の後半で扱う高度なコレクション操作をスムーズに理解できます。

実践経験の前提としては、実際のデータ処理を含む実用的なプログラムを開発した経験があることが望ましいです。例えば、CSVファイルの読み込みや、データベースから取得した情報の加工など、現実的なデータ処理タスクに取り組んだ経験があれば、コレクションフレームワークの有用性をより深く理解できます。また、ループ処理を使ったデータ変換やフィルタリング、集計処理などを実装した経験があることで、コレクションAPIが提供する便利なメソッドの価値を実感できるでしょう。

### 学習目標

本章では、Javaのコレクションフレームワークを深く理解し、実践的に活用できるようになることを目指します。知識理解の面では、まずコレクションフレームワークの全体設計と階層構造を把握します。これは単なる便利なクラスの寄せ集めではなく、インターフェイスと実装を明確に分離し、共通の操作を統一的に提供する洗練された設計になっています。この設計思想を理解することで、なぜ複数のコレクション型が存在するのか、それぞれがどのような場面で有効なのかが明確になります。

List、Set、Mapという3つの主要なインターフェイスの特徴と使い分けも重要な学習ポイントです。Listは順序を保持し重複を許可する、Setは重複を許可しない、Mapはキーと値のペアを管理するという基本的な違いから始まり、それぞれが解決しようとしている問題領域の違いを理解します。さらに、各インターフェイスの具体的な実装クラス（ArrayList、LinkedList、HashSet、TreeSet、HashMap、TreeMapなど）がどのような内部構造を持ち、どのような性能特性を示すのかを学びます。例えば、ArrayListが配列ベースで高速なランダムアクセスを提供する一方、LinkedListが連結リストベースで高速な挿入・削除を可能にすることなど、実装の違いが性能に与える影響を理解します。

技能習得の面では、適切なコレクション型を選択し、効率的に使用する能力を身につけます。単に動作するコードを書くだけでなく、データの特性や処理の要件に応じて最適なコレクション型を選択できるようになることが目標です。また、for-each文、イテレータ、Stream APIなど、コレクションを処理するさまざまな方法を理解し、状況に応じて使い分けられるようになります。コレクションのソートや検索といった一般的な操作についても、標準APIを活用した効率的な実装方法を習得します。

アルゴリズム的思考の観点では、データ構造と処理効率の密接な関係を理解することが重要です。なぜハッシュテーブルが高速な検索を可能にするのか、なぜ二分探索木が順序付きデータの管理に適しているのかといった、アルゴリズムとデータ構造の基本原理を学びます。また、メモリ使用量と実行速度のトレードオフについても理解を深め、限られたリソースの中で最適な選択ができる判断力を養います。

最終的な到達レベルとしては、複雑なデータ処理要件に対して適切なコレクションを選択し、効率的なプログラムを実装できることを目指します。大量のデータを扱う場合でも、適切なデータ構造の選択により高速な処理が可能になることを実感し、実務レベルのプログラムが作成できるようになります。カスタムComparatorを使った複雑なソート処理の実装や、メモリ効率と実行効率のバランスを考慮したプログラム設計など、高度な技術も習得します。さらに、拡張for文やイテレータを自在に使いこなし、配列とListの相互変換など、実践的なデータ操作技術も身につけます。



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

## より深い理解のために

本章で学んだコレクションフレームワークの内部実装について、さらに深く理解したい方は、付録B.10「ハッシュテーブルとレッドブラックツリーの内部実装」を参照してください。この付録では以下の高度なトピックを扱います：

- **HashMapの内部構造**: ハッシュテーブルの実装、Java 8でのツリー化、リサイズとリハッシュ
- **TreeMapとレッドブラックツリー**: 自己平衡二分探索木の実装、挿入・削除時のバランシング
- **ConcurrentHashMapの実装**: セグメント化、ロックフリーアルゴリズム、並行性の最適化
- **パフォーマンス特性**: ハッシュ関数の重要性、メモリ効率、ベンチマーク結果

これらの知識は、大規模なデータを扱うアプリケーションの性能最適化や、カスタムデータ構造の実装に役立ちます。

## まとめ

本章では、コレクションフレームワークの3つの基本インターフェイスと、その代表的な実装クラスを学びました。

| インターフェイス | 特徴 | 代表的な実装 | 主な用途 |
| :--- | :--- | :--- | :--- |
| `List` | 順序あり、重複OK | `ArrayList`, `LinkedList` | 順番にデータを並べたいとき |
| `Set` | 順序なし(一部あり)、重複NG | `HashSet`, `LinkedHashSet` | 重複をなくしたいとき、順序が必要な場合も |
| `Map` | キーと値のペア | `HashMap`, `TreeMap` | IDと情報のように紐付けて管理したいとき |

これらのコレクションを適切に使い分けることが、効率的で読みやすいプログラムを書くための第一歩です。次章では、これらのコレクションを安全かつ柔軟に扱うための「ジェネリクス」について詳しく学びます。