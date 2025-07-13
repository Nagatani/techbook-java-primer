# 第10章 コレクションフレームワーク

## 本章の学習目標

本章では、Javaのコレクションフレームワークについて学習します。配列は固定サイズのデータ構造ですが、実際のプログラムでは動的にサイズが変化するデータを扱う必要が頻繁にあります。コレクションフレームワークは、このようなニーズに応える柔軟で強力なデータ構造を提供します。

### 前提知識
- 第7章までの基本的なJava文法
- 配列の使い方と限界の理解
- インターフェイスの基本概念
- ジェネリクスの基礎（第11章で詳しく学習）

### 学習内容
- コレクションフレームワークの全体像と設計思想
- List、Set、Mapの基本的な使い方と特徴
- 各実装クラス（ArrayList、HashSet、HashMap等）の使い分け
- イテレータによる要素の走査
- コレクションの実践的な活用方法

## 10.1 なぜコレクションフレームワークが必要か

### 配列から始めよう

これまでの章で、複数のデータを扱うために配列を使ってきました。配列は基本的で大切なデータ構造ですが、実際のプログラム開発では、より柔軟なデータ管理が必要になることがあります。

まず、配列を使った簡単な例から始めましょう：

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

**問題1: サイズが固定**

新学期になって6人目の学生が転入してきたらどうしましょう？配列のサイズは作成時に決まるため、後から変更できません：

<span class="listing-number">**サンプルコード10-2**</span>

```java
String[] students = new String[5];
// ... 5人分のデータを登録 ...
// 6人目を追加したい！
students[5] = "山田";  // ArrayIndexOutOfBoundsException!
```

従来の解決方法は、より大きな配列を作り直してコピーすることでした：

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

**問題2: 要素の削除が困難**

転校した学生のデータを削除する場合も複雑です：

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

**問題3: 便利な操作の不足**

よく使う操作も自分で実装する必要があります：

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

これらの問題を解決するために、Javaはコレクションフレームワークを提供しています。同じ学生管理プログラムをArrayListで書き直してみましょう：

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

## 10.2 コレクションフレームワークの全体像

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

## 10.3 List インターフェイス

Listは順序を保持し、インデックスによるアクセスが可能なコレクションです。

### ArrayList vs LinkedList

<span class="listing-number">**サンプルコード10-7**</span>

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

## 10.4 Set インターフェイス

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

**使い分けの指針：**
- **HashSet**: 最高の性能（O(1)）が必要で、順序は重要でない場合。重複チェックや集合演算に最適
- **TreeSet**: 常にソートされた状態を保ちたい場合。範囲検索や順序が大切な処理に適している
- **LinkedHashSet**: 挿入順序を保持しつつ重複を排除したい場合。キャッシュやログ処理に有効

### 集合演算

Setは数学の集合論に基づく演算を簡単に実装できます。これらの演算は、データ分析、権限管理、フィルタリング処理などで頻繁に使用されます。例えば、ユーザーの権限セットとアクセス要求権限セットの積集合を求めることで、実際にアクセス可能な機能を判定できます。

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

## 10.5 Map インターフェイス

Mapはキーと値のペアを管理するデータ構造です。辞書やハッシュテーブルとも呼ばれます。

### HashMap vs TreeMap vs LinkedHashMap

Map実装の選択は、データ量、検索頻度、順序の重要性によって決まります。辞書アプリではTreeMapで五十音順を維持し、キャッシュではLinkedHashMapでLRU（最近最少使用）を実装し、一般的なデータ検索ではHashMapで最高速度を実現します。

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

**パフォーマンスと用途：**
- **HashMap**: 最高の検索性能（O(1)）。設定ファイルや一般的なデータ検索に最適
- **TreeMap**: ソート済み処理（O(log n)）。辞書、ランキング、範囲検索が必要な場合
- **LinkedHashMap**: 順序保持とLRU実装。キャッシュや履歴管理に適している

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

**大切なポイント：**
- `getOrDefault()`はnullチェックの煩雑さを解消し、安全なデフォルト値処理を実現
- エントリーセットでの走査は、内部的にキーでの値検索を避けるため、大きなMapでは大幅に高速
- `containsKey()`での事前チェックにより、存在しないキーへのアクセスエラーを防止

## 10.6 イテレータとfor-each文

### イテレータの基本

イテレータ（Iterator）は、コレクションの要素を順次アクセスするための統一されたインターフェイスです。for-each文は内部的にイテレータを使用しているため、理解しておくことで、より柔軟なコレクション操作が可能になります。特に、走査中に要素を削除する必要がある場合には、イテレータが大切です。

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

**イテレータの利点：**
- **一貫性**: List、Set、Mapに関係なく同じパターンで要素走査が可能
- **安全性**: 走査中の削除でも`ConcurrentModificationException`が発生しない
- **柔軟性**: 条件分岐を含む複雑な削除処理で威力を発揮
- **効率性**: LinkedListなどで、for-each文より高速な場合がある

### 拡張for文（for-each）

拡張for文（for-each文）は、Java 5で導入された簡潔で読みやすいコレクション走査の記法です。内部的にはイテレータを使用しているため、すべてのコレクション型で使用できます。日常的なコレクション処理の大部分は、この記法で十分カバーできます。

<span class="listing-number">**サンプルコード10-14**</span>

```java
// より簡潔な記述：最も一般的なコレクション走査方法
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

**拡張for文の特徴：**
- **簡潔性**: 従来のインデックスループやイテレータより短く書ける
- **安全性**: インデックス範囲外エラーが発生しない
- **可読性**: コードの意図が明確で、保守しやすい
- **制限**: 走査中の削除操作はできない（イテレータが必要）

**使い分けの指針：**
- **拡張for文**: 要素の参照のみ、または新しいコレクションへの変換
- **イテレータ**: 走査中の削除、複雑な条件処理
- **従来のforループ**: インデックスが必要、逆順アクセス

## 10.7 コレクションの選択指針

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
   - キーの順序が大切な場合

## 章末演習

本章で学んだコレクションフレームワークを実践的な課題で確認しましょう。

### 演習課題へのアクセス

本書の演習課題は、以下のGitHubリポジトリで提供されています：

**リポジトリ**: `https://github.com/[your-repo]/java-primer-exercises`

### 第10章の課題構成

```
exercises/chapter10/
├── basic/              # 基礎課題（ポイント）
│   ├── README.md       # 詳細な課題説明
│   └── Student.java
├── advanced/           # 発展課題（推奨）
├── challenge/          # チャレンジ課題（任意）
└── solutions/          # 解答例（実装後に参照）
```

### 学習の目標

本章の演習を通じて以下のスキルを習得します：
- List、Set、Mapの特性を活かした実装
- 適切なコレクション型の選択と活用
- パフォーマンスを考慮したデータ構造の設計

### 課題の概要

1. **基礎課題**: 学生管理システム、単語カウンタ、重複除去システムなど、各コレクションの基本的な活用
2. **発展課題**: 動的配列ベースのデータベース、複雑なソート処理の実装
3. **チャレンジ課題**: 簡易検索エンジン、図書館管理システムなど、実用的なアプリケーション開発

詳細な課題内容と実装のヒントは、GitHubリポジトリの各課題フォルダ内のREADME.mdを参照してください。

**次のステップ**: 基礎課題が完了したら、第11章「ジェネリクス」に進みましょう。



## 本章の学習目標

### 前提知識

本章を学習するためのポイントとなる前提として、第8章で学んだ列挙型の概念と第9章で学んだレコードの活用方法を理解していることがポイントです。特に、データを効率的に管理する必要性と、配列の限界について体験的に理解していることが大切です。また、配列を使った基本的なプログラミング経験があり、複数のデータを扱う処理に慣れていることが求められます。さらに、Java 8以降で導入された関数型プログラミングスタイルの基本的な考え方、特にラムダ式やメソッド参照について触れた経験があることで、本章の後半で扱う高度なコレクション操作をスムーズに理解できます。

実践経験の前提としては、実際のデータ処理を含む実用的なプログラムを開発した経験があることがあるとよいです。例えば、CSVファイルの読み込みや、データベースから取得した情報の加工など、現実的なデータ処理タスクに取り組んだ経験があれば、コレクションフレームワークの有用性をより深く理解できます。また、ループ処理を使ったデータ変換やフィルタリング、集計処理などを実装した経験があることで、コレクションAPIが提供する便利なメソッドの価値を実感できるでしょう。

### 学習目標

本章では、Javaのコレクションフレームワークを深く理解し、実践的に活用できるようになることを目指します。知識理解の面では、まずコレクションフレームワークの全体設計と階層構造を把握します。これは単なる便利なクラスの寄せ集めではなく、インターフェイスと実装を明確に分離し、共通の操作を統一的に提供する洗練された設計になっています。この設計思想を理解することで、なぜ複数のコレクション型が存在するのか、それぞれがどのような場面で有効なのかが明確になります。

List、Set、Mapという3つの主要なインターフェイスの特徴と使い分けも大切な学習ポイントです。Listは順序を保持し重複を許可する、Setは重複を許可しない、Mapはキーと値のペアを管理するという基本的な違いから始まり、それぞれが解決しようとしている問題領域の違いを理解します。さらに、各インターフェイスの具体的な実装クラス（ArrayList、LinkedList、HashSet、TreeSet、HashMap、TreeMapなど）がどのような内部構造を持ち、どのような性能特性を示すのかを学びます。例えば、ArrayListが配列ベースで高速なランダムアクセスを提供する一方、LinkedListが連結リストベースで高速な挿入・削除を可能にすることなど、実装の違いが性能に与える影響を理解します。

技能習得の面では、適切なコレクション型を選択し、効率的に使用する能力を身につけます。単に動作するコードを書くだけでなく、データの特性や処理の要件に応じて最適なコレクション型を選択できるようになることが目標です。また、for-each文、イテレータ、Stream APIなど、コレクションを処理するさまざまな方法を理解し、状況に応じて使い分けられるようになります。コレクションのソートや検索といった一般的な操作についても、標準APIを活用した効率的な実装方法を習得します。

アルゴリズム的思考の観点では、データ構造と処理効率の密接な関係を理解することが大切です。なぜハッシュテーブルが高速な検索を可能にするのか、なぜ二分探索木が順序付きデータの管理に適しているのかといった、アルゴリズムとデータ構造の基本原理を学びます。また、メモリ使用量と実行速度のトレードオフについても理解を深め、限られたリソースの中で最適な選択ができる判断力を養います。

最終的な到達レベルとしては、複雑なデータ処理要件に対して適切なコレクションを選択し、効率的なプログラムを実装できることを目指します。大量のデータを扱う場合でも、適切なデータ構造の選択により高速な処理が可能になることを実感し、実務レベルのプログラムが作成できるようになります。カスタムComparatorを使った複雑なソート処理の実装や、メモリ効率と実行効率のバランスを考慮したプログラム設計など、高度な技術も習得します。さらに、拡張for文やイテレータを自在に使いこなし、配列とListの相互変換など、実践的なデータ操作技術も身につけます。



## 10.8 データ管理の進化：配列からコレクションへ

Javaプログラミングの初期に学ぶ配列は、複数のデータをまとめて管理するための基本的な手段です。しかし、実用的なアプリケーションを開発するには、配列だけでは不十分な場面が多くあります。

### 配列の限界

配列には、主に2つの大きな制限があります。

1.  **サイズが固定**: 配列は、一度作成するとそのサイズを変更できません。
2.  **機能が限定的**: 要素の追加や削除、検索といった一般的な操作を自前で実装する必要があり、手間がかかります。

<span class="listing-number">**サンプルコード10-15**</span>

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

## 10.9 `List`インターフェイス：順序のあるデータ列

`List`は、**順序付けられた**要素のコレクションで、**重複を許可します**。追加した順に要素が格納され、インデックス（添え字）によって各要素にアクセスできます。配列に最も近い感覚で使えます。

### 代表的な実装クラス

*   `java.util.ArrayList`: 内部的に配列を使用。要素へのランダムアクセス（インデックス指定での取得）が非常に高速。
*   `java.util.LinkedList`: 内部的に双方向連結リスト構造を使用。要素の追加や削除（特にリストの先頭や中間）が高速。

### `ArrayList`の基本的な使い方

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
| **要素の取得(get)** | **高速** | 遅い |
| **要素の追加/削除(中間)** | 遅い（後続要素のシフトが発生） | **高速** |
| **メモリ使用量** | 比較的少ない | やや多い（前後の要素への参照を持つため） |

**使い分けの目安:**
*   **`ArrayList`**: 要素の参照（読み取り）が多く、リストのサイズがあまり変化しない場合に最適。
*   **`LinkedList`**: 要素の追加や削除が頻繁に発生する場合（特にリストの先頭や中間）に適している。

### Listの要素を順に処理する方法

#### 拡張for文 (推奨)
最も簡潔で一般的な方法です。
<span class="listing-number">**サンプルコード10-17**</span>

```java
for (String name : nameList) {
    System.out.println(name);
}
```

#### イテレータ (Iterator)
ループ中にコレクションから要素を安全に削除したい場合に使います。
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

`java.util.Arrays`クラスや`List`インターフェイスのメソッドを利用します。

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
**注意:** `Arrays.asList()`が直接返す`List`はサイズが固定されており、`add`や`remove`ができないため、変更可能な`ArrayList`でラップするのが一般的です。

## 10.10 `Set`インターフェイス：重複のないユニークな集合

`Set`は、**重複しない**要素のコレクションです。「集合」という数学的な概念に対応します。ユーザーIDやメールアドレスなど、一意性を保証したいデータの管理に適しています。

### 代表的な実装クラス

*   `java.util.HashSet`: 最も一般的。順序は保証されないが、追加・検索が非常に高速。
*   `java.util.LinkedHashSet`: 要素が**挿入された順序**を保持する。
*   `java.util.TreeSet`: 要素を**自然順序（ソート順）**で保持する。

### `HashSet`の基本的な使い方

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

## 10.11 `Map`インターフェイス：キーと値のペア

`Map`は、一意の**キー（Key）** と、それに対応する**値（Value）** のペアを格納するコレクションです。キーを使って高速に値を取得できます。辞書や連想配列とも呼ばれます。

### 代表的な実装クラス

*   `java.util.HashMap`: 最も一般的。キーの順序は保証されないが、非常に高速。
*   `java.util.LinkedHashMap`: キーが**挿入された順序**を保持する。
*   `java.util.TreeMap`: キーを**自然順序（ソート順）**で保持する。

### `HashMap`の基本的な使い方

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

## より深い理解のために

本章で学んだコレクションフレームワークの内部実装について、さらに深く理解したい方は、GitHubリポジトリの付録資料を参照してください：

**付録リソース**: `/appendix/b08-collection-internals/`

この付録では以下の高度なトピックを扱います：

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