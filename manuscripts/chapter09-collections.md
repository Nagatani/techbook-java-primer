# <b>10章</b> <span>コレクションフレームワーク</span> <small>柔軟なデータ構造と操作の基礎</small>

## 本章の学習目標

### この章で学ぶこと

1. コレクションの基本理解
    - 配列の限界とコレクションの必要性、List・Set・Mapの特徴と使い分け
2. 主要な実装クラス
    - ArrayList/LinkedList、HashSet/TreeSet、HashMap/TreeMapの使い分け
3. コレクションの操作
    - 基本操作（追加・削除・検索）、イテレータと拡張for文、Collectionsユーティリティ
4. 実践的な活用
    - パフォーマンスを考慮した選択、時間計算量、スレッドセーフティ

### この章を始める前に

配列の基本的な使い方とインターフェイスの概念を理解していれば準備完了です。

## なぜコレクションフレームワークが必要か


### 配列から始めよう

これまでの章で、複数のデータを扱うために配列を使ってきました。配列は基本的で重要なデータ構造ですが、実際のプログラム開発では、より柔軟なデータ管理が必要になることがあります。

まず、配列を使った簡単な例から始めましょう。

<span class="listing-number">**サンプルコード10-1**</span>

```java
// 学生の名前を管理するプログラム
public class StudentManager {
    public static void main(String[] args) {
        // 5人分の学生名を格納する配列
        String[] students = new String[5];
        students[0] = "田中";
        students[1] = "佐藤";
        students[2] = "鈴木";
        
        // 配列の内容を表示
        for (int i = 0; i < students.length; i++) {
            if (students[i] != null) {
                System.out.println("学生" + (i + 1) + ": " + students[i]);
            }
        }
    }
}
```

このプログラムは正常に動作しますが、実際の運用では次のような問題に直面します。

### 配列の限界

#### 問題1: サイズが固定

新学期になって6人目の学生が転入してきたらどうしましょう？配列のサイズは作成時に決まるため、後から変更できません。

以下のコードは、固定サイズ配列の限界を示すものです。すでに5人分のデータで配列が満杯になっている状況で、6人目を追加しようとするとArrayIndexOutOfBoundsExceptionが発生します。

<span class="listing-number">**サンプルコード10-2**</span>

```java
String[] students = new String[5];
// ... 5人分のデータを登録 ...
// 6人目を追加したい！
students[5] = "山田";  // ArrayIndexOutOfBoundsException!
```

従来の解決方法は、より大きな配列を作り直してコピーすることでした。

以下のコードは、配列のサイズを拡張するための複雑な手順を示しています。この処理には、新しい配列の作成、既存データの全件コピー、参照の付け替えという3つのステップが必要であり、データが増えるたびにこの処理を繰り返す必要があります。

<span class="listing-number">**サンプルコード10-3**</span>

```java
// 面倒な配列の拡張処理
String[] newStudents = new String[10];  // 大きめの配列を作成
for (int i = 0; i < students.length; i++) {
    newStudents[i] = students[i];  // 既存データをコピー
}
students = newStudents;  // 参照を付け替え
students[5] = "山田";    // やっと6人目を追加できる
```

#### 問題2: 要素の削除が困難

転校した学生のデータを削除する場合も複雑です。

<span class="listing-number">**サンプルコード10-4**</span>

```java
// 鈴木さん（インデックス2）が転校
students[2] = null;  // nullを代入しても...

// 配列に「穴」ができてしまう
// [田中, 佐藤, null, 高橋, 伊藤]

// 穴を埋めるには手動で詰める必要がある
for (int i = 2; i < students.length - 1; i++) {
    students[i] = students[i + 1];
}
students[students.length - 1] = null;
```

#### 問題3: 便利な操作の不足

よく使う操作も自分で実装してください。

<span class="listing-number">**サンプルコード10-5**</span>

```java
// 特定の学生が在籍しているか確認
boolean found = false;
for (String student : students) {
    if (student != null && student.equals("田中")) {
        found = true;
        break;
    }
}

// 学生数のカウント（nullを除く）
int count = 0;
for (String student : students) {
    if (student != null) {
        count++;
    }
}
```

### コレクションフレームワークの登場


これらの問題を解決するために、Javaはコレクションフレームワークを提供しています。同じ学生管理プログラムをArrayListで書き直してみましょう。

<span class="listing-number">**サンプルコード10-6**</span>

```java
import java.util.ArrayList;
import java.util.List;

public class StudentManagerImproved {
    public static void main(String[] args) {
        // 動的に拡張可能なリスト
        List<String> students = new ArrayList<>();
        
        // 要素の追加（サイズを気にしない）
        students.add("田中");
        students.add("佐藤");
        students.add("鈴木");
        students.add("高橋");
        students.add("伊藤");
        students.add("山田");  // 6人目も問題なく追加
        
        // 要素の削除（自動的に詰められる）
        students.remove("鈴木");  // 簡単に削除
        
        // 便利なメソッド
        System.out.println("学生数: " + students.size());
        System.out.println("田中さんは在籍？ " + students.contains("田中"));
        
        // 拡張for文もそのまま使える
        for (String student : students) {
            System.out.println(student);
        }
    }
}
```

このように、コレクションフレームワークを使うことで、データの管理が格段に簡単になります。

## コレクションフレームワークの全体像

Javaのコレクションフレームワークは、以下の主要なインターフェイスで構成されています。

### 主要インターフェイス

1. Collection: すべてのコレクションの基底インターフェイス
   - List: 順序付きコレクション（重複可）
   - Set: 重複を許さないコレクション
   - Queue: FIFO（先入れ先出し）のコレクション

2. Map: キーと値のペアを管理（Collectionとは独立）


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

## List インターフェイス

Listは順序を保持し、インデックスによるアクセスが可能なコレクションです。


### ArrayList vs LinkedList

以下のコードは、ArrayListとLinkedListの基本的な使い方と、それぞれの特性を比較するための例です。同じ操作でも、内部構造の違いにより性能特性が異なることを理解しましょう。

<span class="listing-number">**サンプルコード10-7**</span>

```java
// ArrayList: 内部的に配列を使用
List<String> arrayList = new ArrayList<>();
arrayList.add("要素1");
arrayList.add("要素2");
String element = arrayList.get(0);  // O(1)時間のランダムアクセス

// LinkedList: 双方向連結リストを使用
List<String> linkedList = new LinkedList<>();
linkedList.add("要素1");
linkedList.add(0, "先頭に挿入");  // 先頭への挿入がO(1)時間
```

#### 使い分けの指針
- ArrayList: ランダムアクセスが多い場合（get/set操作）
- LinkedList: 先頭・末尾での追加・削除が多い場合

### 主な操作

これらの操作を実際に使ってみましょう。以下のコードは、Listインターフェイスの基本操作を示しています。

<span class="listing-number">**サンプルコード10-8**</span>

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

## Set インターフェイス

Setは重複要素を許さないコレクションです。数学的な集合の概念に対応します。

### HashSet vs TreeSet vs LinkedHashSet

Setの実装クラスは、内部構造の違いにより異なる特性を持ちます。データの順序が重要かどうか、検索や挿入の頻度、ソートが必要かどうかを考慮して選択します。

<span class="listing-number">**サンプルコード10-9**</span>

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

#### 使い分けの指針
- HashSet: 平均的にO(1)の高速な性能が必要で、順序は重要でない場合。重複チェックや集合演算に最適
- TreeSet: 常にソートされた状態を保ちたい場合。範囲検索や順序が大切な処理に適している
- LinkedHashSet: 挿入順序を保持しつつ重複を排除したい場合。キャッシュやログ処理に有効

### 集合演算

Setは数学の集合論に基づく演算を簡単に実装できます。
これらの演算は、データ分析、権限管理、フィルタリング処理などで頻繁に使用されます。
たとえば、ユーザーの権限セットとアクセス要求権限セットの積集合を求めることで、実際にアクセス可能な機能を判定できます。

<span class="listing-number">**サンプルコード10-10**</span>

```java
Set<String> set1 = new HashSet<>(Arrays.asList("A", "B", "C"));
Set<String> set2 = new HashSet<>(Arrays.asList("B", "C", "D"));

// 和集合：すべての要素を含む集合
Set<String> union = new HashSet<>(set1);
union.addAll(set2);  // {A, B, C, D}

// 積集合：両方に共通する要素のみ
Set<String> intersection = new HashSet<>(set1);
intersection.retainAll(set2);  // {B, C}

// 差集合：set1にあってset2にない要素
Set<String> difference = new HashSet<>(set1);
difference.removeAll(set2);  // {A}
```

これらの集合演算は、データベースのJOIN操作や、アクセス制御、重複検出などの実用的な場面で威力を発揮します。

## Map インターフェイス

Mapはキーと値のペアを管理するデータ構造です。辞書やハッシュテーブルとも呼ばれます。

### HashMap vs TreeMap vs LinkedHashMap

Map実装の選択は、データ量、検索頻度、順序の重要性によって決まります。
辞書アプリではTreeMapで五十音順を維持し、キャッシュではLinkedHashMapでLRU（最近最少使用）を実装します。
一般的なデータ検索ではHashMapで最高速度を実現します。

<span class="listing-number">**サンプルコード10-11**</span>

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

#### パフォーマンスと用途
- HashMap: 平均的にO(1)の高速な検索性能。設定ファイルや一般的なデータ検索に最適
- TreeMap: ソート済み処理（O(log n)）。辞書、ランキング、範囲検索が必要な場合
- LinkedHashMap: 順序保持とLRU実装。キャッシュや履歴管理に適している

### 主な操作

Mapは辞書的なデータ管理の核となる操作を提供します。基本的なCRUD操作（Create, Read, Update, Delete）に加え、キーや値の存在確認、全要素の走査など、実用的なメソッドが豊富に用意されています。

<span class="listing-number">**サンプルコード10-12**</span>

```java
Map<String, String> map = new HashMap<>();

// 追加・更新：putは新規追加と既存更新の両方を行う
map.put("JP", "日本");
map.put("US", "アメリカ");

// 取得：存在しないキーの安全な処理
String country = map.get("JP");  // "日本"を返す
String defaultValue = map.getOrDefault("UK", "不明");  // "不明"を返す

// 削除：指定したキーと値のペアを削除
map.remove("US");

// 存在確認：処理前の事前チェックに使用
boolean hasKey = map.containsKey("JP");
boolean hasValue = map.containsValue("日本");

// すべてのキー・値を処理：キーセットでの走査
for (String key : map.keySet()) {
    System.out.println(key + ": " + map.get(key));
}

// エントリーセットで処理（より効率的）：キーと値を同時取得
for (Map.Entry<String, String> entry : map.entrySet()) {
    System.out.println(entry.getKey() + ": " + entry.getValue());
}
```

#### 重要なポイント
- `getOrDefault()`はnullチェックの煩雑さを解消し、安全なデフォルト値処理を実現
- エントリーセットでの走査は、内部的にキーでの値検索を避けるため、大きなMapでは2倍以上高速になることがある
- `containsKey()`での事前チェックにより、存在しないキーへのアクセスエラーを防止

## イテレータとfor-each文

### イテレータの基本

イテレータ（Iterator）は、コレクションの要素を順次アクセスするための統一されたインターフェイスです。
for-each文は内部的にイテレータを使用しているため、理解しておくことで、より柔軟なコレクション操作が可能になります。
とくに、走査中に要素を削除する必要がある場合には、イテレータが重要です。

<span class="listing-number">**サンプルコード10-13**</span>

```java
List<String> list = Arrays.asList("A", "B", "C");

// イテレータを使った走査：全コレクション型で統一された方法
Iterator<String> iter = list.iterator();
while (iter.hasNext()) {
    String element = iter.next();
    System.out.println(element);
}

// 走査中の安全な削除：ConcurrentModificationExceptionを回避
Iterator<String> iter2 = list.iterator();
while (iter2.hasNext()) {
    String element = iter2.next();
    if (element.equals("B")) {
        iter2.remove();  // イテレータ経由で削除
    }
}
```

#### イテレータの利点
- 一貫性List、Set、Mapに関係なく同じパターンで要素走査が可能
- 安全性走査中の削除でも`ConcurrentModificationException`が発生しない
- 柔軟性
-    + 条件分岐を含む複雑な削除処理で威力を発揮
- 効率性LinkedListなどで、for-each文より高速な場合がある

### 拡張for文（for-each）

拡張for文（for-each文）は、Java 5で導入された簡潔で読みやすいコレクション走査の記法です。
内部的にはイテレータを使用しているため、すべてのコレクション型で使用できます。
日常的なコレクション処理の大部分は、この記法で十分カバーできます。

<span class="listing-number">**サンプルコード10-14**</span>

```java
// より簡潔な記述：もっとも一般的なコレクション走査方法
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

#### 拡張for文の特徴
- 簡潔性従来のインデックスループやイテレータより短く書ける
- 安全性インデックス範囲外エラーが発生しない
- 可読性コードの意図が明確で、保守しやすい
- 制限走査中の削除操作はできない（イテレータが必要）

#### 使い分けの指針
- 拡張for文
-    + 要素の参照のみ、または新しいコレクションへの変換
- イテレータ
-    + 走査中の削除、複雑な条件処理
- 従来のforループ
-    + インデックスが必要、逆順アクセス

## コレクションの選択指針

### パフォーマンス特性

| 操作 | ArrayList | LinkedList | HashSet | TreeSet | HashMap | TreeMap |
|------|-----------|------------|---------|---------|---------|---------|
| 追加（末尾） | O(1) | O(1) | O(1) | O(log n) | O(1) | O(log n) |
| 削除（インデックス） | O(n) | O(n) | - | - | - | - |
| 検索（値） | O(n) | O(n) | O(1) | O(log n) | - | - |
| 取得（インデックス） | O(1) | O(n) | - | - | - | - |
| 取得（キー） | - | - | - | - | O(1) | O(log n) |

### 使用場面の例

1. ArrayList: 
   - 学生名簿、商品リスト
   - インデックスアクセスが多い場合
   - **実例**: Androidの`RecyclerView`は内部的にArrayListを使用し、画面に表示される項目を効率的に管理

2. LinkedList:
   - 待ち行列、履歴管理
   - 先頭・末尾での操作が多い場合
   - **実例**: ブラウザの戻る/進む機能、Undo/Redo機能の実装

3. HashSet:
   - ユニークなID管理、重複チェック
   - 順序が重要でない場合
   - **実例**: データベースのインデックス、重複メールアドレスの検出

4. TreeSet:
   - ランキング、ソート済みデータ
   - 常にソートされた状態が必要な場合
   - **実例**: ゲームのハイスコア管理、アルファベット順の辞書アプリ

5. HashMap:
   - 辞書、キャッシュ、設定値管理
   - キーによる高速検索が必要な場合
   - **実例**: Webアプリケーションのセッション管理、設定ファイルの解析結果保存

6. TreeMap:
   - ソート済み辞書、範囲検索
   - キーの順序が大切な場合
   - **実例**: ログファイルのタイムスタンプ管理、価格帯での商品検索

## 今後の学習へ向けて

### Java 8以降の進化

Java 8で導入されたStream APIは、コレクションの処理方法を革新しました。

```java
// 従来の方法
List<String> result = new ArrayList<>();
for (String s : list) {
    if (s.length() > 5) {
        result.add(s.toUpperCase());
    }
}

// Stream API
List<String> result = list.stream()
    .filter(s -> s.length() > 5)
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```

Stream APIは関数型プログラミングのパラダイムを取り入れ、より宣言的で読みやすいコードを可能にします。

### 不変コレクション

Java 9以降、不変（Immutable）コレクションの作成が簡単になりました。

```java
List<String> list = List.of("A", "B", "C");  // 変更不可
Set<String> set = Set.of("X", "Y", "Z");      // 変更不可
Map<String, Integer> map = Map.of("key1", 1, "key2", 2);  // 変更不可
```

不変コレクションはスレッドセーフであり、予期しない変更から保護されるため、より堅牢なプログラムの作成に役立ちます。

## 章末演習

### 演習課題へのアクセス
本章の演習課題は、GitHubリポジトリで提供されています。<br>
`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter10/`

### 課題構成
- 本章の基本概念の理解確認
- 応用的な実装練習
- 実践的な総合問題

詳細な課題内容と実装のヒントは、各課題フォルダ内のREADME.mdを参照してください。

1. 基礎課題
    + 学生管理システム、単語カウンタ、重複除去システムなど、各コレクションの基本的な活用
2. 発展課題
    + 動的配列ベースのデータベース、複雑なソート処理の実装
3. チャレンジ課題
    + 簡易検索エンジン、図書館管理システムなど、実用的なアプリケーション開発

詳細な課題内容と実装のヒントは、GitHubリポジトリの各課題フォルダ内のREADME.mdを参照してください。

次のステップ： 基礎課題が完了したら、第11章「ジェネリクス」に進みましょう。

## 本章の学習目標

### 前提知識

本章を学習するためのポイントとなる前提として、第8章で学んだ列挙型とレコードの概念を理解していることがポイントです。とくに、データを効率的に管理する必要性と、配列の限界について体験的に理解していることが重要です。また、配列を使った基本的なプログラミング経験があり、複数のデータを扱う処理に慣れていることが求められます。さらに、Java 8以降で導入された関数型プログラミングスタイルの基本的な考え方、とくにラムダ式やメソッド参照について触れた経験があることで、本章の後半で扱う高度なコレクション操作をスムーズに理解できます。

実践経験の前提としては、実際のデータ処理を含む実用的なプログラムを開発した経験があることがあるとよいです。たとえば、CSVファイルの読み込みや、データベースから取得した情報の加工など、現実的なデータ処理タスクに取り組んだ経験があれば、コレクションフレームワークの有用性をより深く理解できます。また、ループ処理を使ったデータ変換やフィルタリング、集計処理などを実装した経験があることで、コレクションAPIが提供する便利なメソッドの価値を実感できるでしょう。

### 学習目標

本章では、Javaのコレクションフレームワークを深く理解し、実践的に活用できるようになることを目指します。知識理解の面では、まずコレクションフレームワークの全体設計と階層構造を把握します。これは単なる便利なクラスの寄せ集めではなく、インターフェイスと実装を明確に分離し、共通の操作を統一的に提供する洗練された設計になっています。この設計思想を理解することで、なぜ複数のコレクション型が存在するのか、それぞれがどのような場面で有効なのかが明確になります。

List、Set、Mapという3つの主要なインターフェイスの特徴と使い分けも重要な学習ポイントです。Listは順序を保持し重複を許可する、Setは重複を許可しない、Mapはキーと値のペアを管理するという基本的な違いから始まり、それぞれが解決しようとしている問題領域の違いを理解します。さらに、各インターフェイスの具体的な実装クラス（ArrayList、LinkedList、HashSet、TreeSet、HashMap、TreeMapなど）がどのような内部構造を持ち、どのような性能特性を示すのかを学びます。たとえば、ArrayListが配列ベースで高速なランダムアクセスを提供する一方、LinkedListが連結リストベースで高速な挿入・削除を可能にすることなど、実装の違いが性能に与える影響を理解します。

技能習得の面では、データ特性に合ったコレクション型を選択し、正しく使用する能力を身につけます。単に動作するコードを書くだけでなく、データの特性や処理の要件に応じて最適なコレクション型を選択できるようになることが目標です。また、for-each文、イテレータ、Stream APIなど、コレクションを処理するさまざまな方法を理解し、状況に応じて使い分けられるようになります。コレクションのソートや検索といった一般的な操作についても、標準APIを活用した最小計算量での実装方法を習得します。

アルゴリズム的思考の観点では、データ構造と処理効率の密接な関係を理解することが重要です。なぜハッシュテーブルが高速な検索を可能にするのか、なぜ二分探索木が順序付きデータの管理に適しているのかといった、アルゴリズムとデータ構造の基本原理を学びます。また、メモリ使用量と実行速度のトレードオフについても理解を深め、限られたリソースのなかで最適な選択ができる判断力を養います。

最終的な到達レベルとしては、複雑なデータ処理要件に対して計算量特性を考慮したコレクションを選択し、100万件のデータでも1秒以内に処理可能なプログラムを実装できることを目指します。大量のデータを扱う場合でも、O(1)アクセスのHashMapやO(log n)検索のTreeMapなど、データ構造の選択により計算量を削減した処理が可能になることを実感し、実務レベルのプログラムが作成できるようになります。カスタムComparatorを使った複雑なソート処理の実装や、メモリ使用量と実行速度のバランスを考慮したプログラム設計など、高度な技術も習得します。さらに、拡張for文やイテレータを自在に使いこなし、配列とListの相互変換など、実践的なデータ操作技術も身につけます。

## データ管理の進化：配列からコレクションへ

Javaプログラミングの初期に学ぶ配列は、複数のデータをまとめて管理するための基本的な手段です。しかし、実用的なアプリケーションを開発するには、配列だけでは不十分な場面が多くあります。

### 配列の限界

配列には、主に2つの大きな制限があります。

1. サイズが固定
    + 配列は、一度作成するとそのサイズを変更できません。
2. 機能が限定的
    + 要素の追加や削除、検索といった一般的な操作を自前で実装する必要があり、手間がかかる。

<span class="listing-number">**サンプルコード10-15**</span>

```java
// 配列の例：要素を追加するのも一苦労
String[] users = new String[3];
users[0] = "Alice";
users[1] = "Bob";
users[2] = "Charlie";
// これ以上ユーザーを追加するには、より大きな新しい配列を作って、
// 全ての要素をコピーし直す必要があります。
```

こうした配列の不便さを解消し、より柔軟で高機能なデータ管理を実現するのがコレクションフレームワークです。

### コレクションフレームワークとは？

Javaのコレクションフレームワークは、データを効率的に扱うためのさまざまな「データ構造」を、再利用可能なクラスやインターフェイスとして体系的にまとめたものです。

- 動的なサイズ
-    + 要素の数に応じて自動的にサイズが調整される
- 豊富な機能
-    + 要素の追加、削除、検索、ソートなどの便利なメソッドが標準で提供されている
- ポリモーフィズム
-    + `List`, `Set`, `Map`といったインターフェイスを通じて実装を切り替え可能で、柔軟な設計ができる
- 型安全
-    + ジェネリクスにより、コレクションに格納するデータの型をコンパイル時に保証しる

## `List`インターフェイス：順序のあるデータ列

`List`は、順序付けられた要素のコレクションで、重複を許可します。追加した順に要素が格納され、インデックス（添え字）によって各要素にアクセスできます。配列にもっとも近い感覚で使えます。

### 代表的な実装クラス

* `java.util.ArrayList`: 内部的に配列を使用。要素へのランダムアクセス（インデックス指定での取得）が非常に高速
* `java.util.LinkedList`: 内部的に双方向連結リスト構造を使用。要素の追加や削除（とくにリストの先頭や中間）が高速

### `ArrayList`の基本的な使い方

まず、ArrayListの基本的な使い方を具体的なコード例で見てみましょう。

<span class="listing-number">**サンプルコード10-16**</span>

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
| 要素の取得(get) | 高速 | 遅い |
| 要素の追加/削除(中間) | 遅い（後続要素のシフトが発生） | 高速 |
| メモリ使用量 | 比較的少ない | やや多い（前後の要素への参照を持つため） |

#### 使い分けの目安
* `ArrayList` 要素の参照（読み取り）が多く、リストのサイズがあまり変化しない場合に最適
* `LinkedList` 要素の追加や削除が頻繁に発生する場合（とくにリストの先頭や中間）に適している

### Listの要素を順に処理する方法

#### 拡張for文 (推奨)
もっとも簡潔で一般的な方法です。

以下のコードは、拡張for文を使用したコレクションの走査方法を示しています。この記法により、インデックスを意識することなく、すべての要素にアクセスできます。

<span class="listing-number">**サンプルコード10-17**</span>

```java
for (String name : nameList) {
    System.out.println(name);
}
```

#### イテレータ (Iterator)
ループ中にコレクションから要素を安全に削除したい場合に使います。

以下のコードは、イテレータを使用してループ中に要素を安全に削除する方法を示しています。拡張for文では実現できない、動的な要素の削除が可能です。

<span class="listing-number">**サンプルコード10-18**</span>

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

実際のアプリケーションでは、配列とListを相互に変換する場面が多々あります。以下のコードは、その方法を示しています。

<span class="listing-number">**サンプルコード10-19**</span>

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
注意： `Arrays.asList()`が直接返す`List`はサイズが固定されており、`add`や`remove`ができないため、変更可能な`ArrayList`でラップするのが一般的です。

## `Set`インターフェイス：重複のないユニークな集合

`Set`は、重複しない要素のコレクションです。「集合」という数学的な概念に対応します。ユーザーIDやメールアドレスなど、一意性を保証したいデータの管理に適しています。

### 代表的な実装クラス

* `java.util.HashSet`: もっとも一般的。順序は保証されないが、追加・検索が非常に高速
* `java.util.LinkedHashSet`: 要素が挿入された順序を保持する
* `java.util.TreeSet`: 要素を自然順序（ソート順）で保持する

### `HashSet`の基本的な使い方

HashSetの基本的な操作を実際のコード例で確認してみましょう。

<span class="listing-number">**サンプルコード10-20**</span>

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

## `Map`インターフェイス：キーと値のペア

`Map`は、一意のキー（Key）と、それに対応する値（Value）のペアを格納するコレクションです。キーを使って高速に値を取得できます。辞書や連想配列とも呼ばれます。

### 代表的な実装クラス

* `java.util.HashMap`: もっとも一般的。キーの順序は保証されないが、非常に高速
* `java.util.LinkedHashMap`: キーが挿入された順序を保持する
* `java.util.TreeMap`: キーを自然順序（ソート順）で保持する

### `HashMap`の基本的な使い方

Mapの具体的な操作方法を、学生の点数管理を例にして見てみましょう。

<span class="listing-number">**サンプルコード10-21**</span>

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

※ 本章の高度な内容については、付録B.06「コレクションの内部実装」を参照してください。
（`https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b06-collection-internals/`）

## よくあるエラーと対処法

コレクションフレームワークを学習する際によく遭遇するエラーとその対処法を以下にまとめます。

### NullPointerException関連

#### 初期化忘れ

エラーメッセージ。
```
Exception in thread "main" java.lang.NullPointerException
```

原因と対処。

以下のコードは、コレクションを初期化せずに使用しようとした例です。

<span class="listing-number">**サンプルコード10-22**</span>

```java
// エラー例
List<String> list;  // 宣言のみ、初期化していない
list.add("Hello");  // NullPointerException

// 修正版
List<String> list = new ArrayList<>();  // 初期化を忘れずに
list.add("Hello");  // OK
```

#### null要素の扱い

次のコードは、null要素を含むSetを扱う際の注意点を示しています。

<span class="listing-number">**サンプルコード10-23**</span>

```java
// エラー例
Set<String> set = new HashSet<>();
set.add(null);  // HashSetはnullを1つだけ保持可能
set.add(null);  // 重複は除去される

String first = set.iterator().next();
int length = first.length();  // first がnullの場合はNullPointerException

// 修正版
Set<String> set = new HashSet<>();
set.add("Hello");
set.add("World");

for (String s : set) {
    if (s != null) {  // null チェック
        System.out.println(s.length());
    }
}
```

### ClassCastException関連

#### ジェネリクスを使わない場合

エラーメッセージ。
```
Exception in thread "main" java.lang.ClassCastException: java.lang.Integer cannot be cast to java.lang.String
```

原因と対処。

以下のコードは、ジェネリクスを使用しない場合の問題を示しています。

<span class="listing-number">**サンプルコード10-24**</span>

```java
// エラー例（ジェネリクスなし）
List list = new ArrayList();  // Raw type（非推奨）
list.add("Hello");
list.add(123);  // 異なる型も追加可能

String s = (String) list.get(1);  // ClassCastException

// 修正版（ジェネリクス使用）
List<String> list = new ArrayList<>();  // 型を明示
list.add("Hello");
// list.add(123);  // コンパイルエラーで防げる

String s = list.get(0);  // キャストが不要、型安全
```

### UnsupportedOperationException関連

#### 不変リストの変更

エラーメッセージ。
```
Exception in thread "main" java.lang.UnsupportedOperationException
```

原因と対処。

次のコードは、不変リストを変更しようとした場合の問題を示しています。

<span class="listing-number">**サンプルコード10-25**</span>

```java
// エラー例
List<String> list = List.of("A", "B", "C");  // 不変リスト（Java 9+）
list.add("D");  // UnsupportedOperationException

// 修正版1：可変リストを作成
List<String> list = new ArrayList<>(List.of("A", "B", "C"));
list.add("D");  // OK

// 修正版2：不変の特性を理解して使用
List<String> immutableList = List.of("A", "B", "C");
List<String> mutableList = new ArrayList<>(immutableList);
mutableList.add("D");  // OK
```

### ConcurrentModificationException関連

#### 反復中の変更

エラーメッセージ。
```
Exception in thread "main" java.util.ConcurrentModificationException
```

原因と対処。

以下のコードは、反復処理中にコレクションを変更しようとした例です。

<span class="listing-number">**サンプルコード10-26**</span>

```java
// エラー例
List<String> list = new ArrayList<>();
list.add("A");
list.add("B");
list.add("C");

for (String s : list) {
    if (s.equals("B")) {
        list.remove(s);  // ConcurrentModificationException
    }
}

// 修正版1：Iteratorを使用
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    String s = it.next();
    if (s.equals("B")) {
        it.remove();  // Iteratorのremoveメソッドを使用
    }
}

// 修正版2：removeIfメソッドを使用（Java 8+）
list.removeIf(s -> s.equals("B"));  // 安全な削除

// 修正版3：逆順でインデックスループ
for (int i = list.size() - 1; i >= 0; i--) {
    if (list.get(i).equals("B")) {
        list.remove(i);  // インデックスが前にずれる影響を回避
    }
}
```

### IndexOutOfBoundsException関連

#### 配列の範囲外アクセス

エラーメッセージ。
```
Exception in thread "main" java.lang.IndexOutOfBoundsException: Index 5 out of bounds for length 3
```

原因と対処。

次のコードは、配列の範囲外にアクセスしようとした例です。

<span class="listing-number">**サンプルコード10-27**</span>

```java
// エラー例
List<String> list = new ArrayList<>();
list.add("A");
list.add("B");
list.add("C");

String item = list.get(5);  // IndexOutOfBoundsException

// 修正版：サイズをチェック
if (index >= 0 && index < list.size()) {
    String item = list.get(index);  // 安全なアクセス
} else {
    System.out.println("無効なインデックス: " + index);
}

// 修正版2：try-catchで例外処理
try {
    String item = list.get(index);
    System.out.println(item);
} catch (IndexOutOfBoundsException e) {
    System.out.println("リストの範囲外です: " + e.getMessage());
}
```

### Map関連のエラー

#### 存在しないキーのアクセス

Mapで存在しないキーにアクセスする際の注意点を示すコード例です。

<span class="listing-number">**サンプルコード10-28**</span>

```java
// 注意が必要な例
Map<String, Integer> map = new HashMap<>();
map.put("key1", 100);

Integer value = map.get("key2");  // null が返される
int result = value + 10;  // NullPointerException

// 修正版1：null チェック
Integer value = map.get("key2");
if (value != null) {
    int result = value + 10;
} else {
    System.out.println("キーが存在しません");
}

// 修正版2：getOrDefaultメソッドを使用
int value = map.getOrDefault("key2", 0);  // デフォルト値0
int result = value + 10;  // 安全

// 修正版3：containsKeyで事前チェック
if (map.containsKey("key2")) {
    int value = map.get("key2");
    int result = value + 10;
} else {
    System.out.println("キーが存在しません");
}
```

### equals()とhashCode()の問題

#### カスタムオブジェクトをHashSetやHashMapのキーで使用

カスタムオブジェクトをHashSetやHashMapで使用する際の問題を示すコード例です。

<span class="listing-number">**サンプルコード10-29**</span>

```java
// 問題のある例
class Person {
    private String name;
    
    public Person(String name) {
        this.name = name;
    }
    
    // equals()とhashCode()をオーバーライドしていない
}

Set<Person> persons = new HashSet<>();
persons.add(new Person("Alice"));
persons.add(new Person("Alice"));  // 同じ名前だが別のオブジェクト

System.out.println(persons.size());  // 2（重複削除されない）

// 修正版：equals()とhashCode()を適切に実装
class Person {
    private String name;
    
    public Person(String name) {
        this.name = name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return Objects.equals(name, person.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
```

### 型安全性の問題

#### Raw typeの使用

型安全性を損なうRaw typeの使用例を示しています。

<span class="listing-number">**サンプルコード10-30**</span>

```java
// 非推奨な例
List list = new ArrayList();  // Raw type
list.add("String");
list.add(123);  // 異なる型も追加可能

// 型安全性が保証されない
String s = (String) list.get(1);  // 実行時エラーの可能性

// 推奨な例
List<String> stringList = new ArrayList<>();  // ジェネリクス使用
stringList.add("String");
// stringList.add(123);  // コンパイルエラー

String s = stringList.get(0);  // キャスト不要、型安全
```

### デバッグのコツ

1. 型安全なジェネリクスの使用
   - Raw typeを避け、常に型パラメータを指定
   - コンパイル時に型の安全性を確保

2. null安全なコーディング
   - 要素をコレクションに追加する前にnullチェック
   - getOrDefaultやOptionalの活用

3. 安全なイテレーション
   - 拡張for文やStream APIの活用
   - 反復中の変更を避ける

4. equals（）とhashCode()の実装
   - カスタムオブジェクトをコレクションで使用する場合は必須
   - IDEの自動生成機能やLombokの活用

5. 例外処理の実装
   - IndexOutOfBoundsExceptionやNullPointerExceptionの適時捕捉と処理
   - 防御的プログラミングの実践

これらの対処法を参考に、安全で正確なコレクション操作を実装してください。

## まとめ

本章では、コレクションフレームワークの3つの基本インターフェイスと、その代表的な実装クラスを学びました

| インターフェイス | 特徴 | 代表的な実装 | 主な用途 |
| :--- | :--- | :--- | :--- |
| `List` | 順序あり、重複OK | `ArrayList`, `LinkedList` | 順番にデータを並べたいとき |
| `Set` | 順序なし(一部あり)、重複NG | `HashSet`, `LinkedHashSet` | 重複をなくしたいとき、順序が必要な場合も |
| `Map` | キーと値のペア | `HashMap`, `TreeMap` | IDと情報のように紐付けて管理したいとき |

これらのコレクションを適切に使い分けることが、効率的で読みやすいプログラムを書くための第一歩です。次章では、これらのコレクションを安全かつ柔軟に扱うための「ジェネリクス」について詳しく学びます。