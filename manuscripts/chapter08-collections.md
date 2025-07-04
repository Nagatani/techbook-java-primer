# 第8章 コレクションフレームワーク

## 📝 章末演習

本章で学んだコレクションフレームワークの概念を活用して、実践的な練習課題に取り組みましょう。

### 🎯 演習の目標
- コレクションフレームワークの基本概念と階層構造の理解
- List、Set、Mapの特性と使い分け
- ArrayList、LinkedList、HashSet、TreeSet、HashMap、TreeMapの実装の違い
- 効率的なデータ構造選択の判断基準
- Iteratorを使った安全な要素操作
- カスタムComparatorとComparableの実装による高度なソート機能

### 📁 課題の場所
演習課題は `exercises/chapter08/` ディレクトリに用意されています：

```
exercises/chapter08/
├── basic/          # 基本課題（必須）
│   ├── README.md   # 課題の詳細説明
│   ├── Student.java # 課題1: 学生管理システム（List活用）
│   ├── StudentManager.java
│   ├── StudentManagerTest.java
│   ├── WordCounter.java # 課題2: 単語カウンター（Map活用）
│   ├── WordCounterTest.java
│   ├── DuplicateRemover.java # 課題3: 重複除去システム（Set活用）
│   ├── DuplicateRemoverTest.java
│   ├── Product.java # 課題4: 在庫管理システム（複合コレクション活用）
│   ├── InventoryManager.java
│   └── InventoryManagerTest.java
├── advanced/       # 発展課題（推奨）
└── challenge/      # 挑戦課題（上級者向け）
```

### 🚀 推奨する学習の進め方

1. **基本課題**から順番に取り組む
2. 各課題のREADME.mdで詳細を確認
3. TODOコメントを参考に実装
4. List、Set、Mapの特性を理解して適切に使い分ける
5. 時間計算量を意識した効率的なデータ構造選択を行う

基本課題が完了したら、`advanced/`の発展課題でより複雑なデータ構造活用に挑戦してみましょう！

## 📋 本章の学習目標

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