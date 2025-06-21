# 第7章 コレクションフレームワーク

## はじめに：データ構造の進化とJavaコレクションフレームワークの革新

前章までで、オブジェクト指向プログラミングの基本概念を学習してきました。この章では、Javaプログラミングにおいて最も頻繁に使用され、かつ最も重要な機能の一つである「コレクションフレームワーク」について詳細に学習します。

コレクションフレームワークは、単なるデータ格納の仕組みではありません。これは、コンピュータサイエンスの長い歴史の中で蓄積されたデータ構造とアルゴリズムの知識を、実用的で使いやすい形でパッケージ化した、ソフトウェア工学の傑作の一つです。

### コンピュータサイエンスにおけるデータ構造の重要性

コンピュータプログラムの本質は「データの処理」です。どれほど複雑で高度なアプリケーションも、最終的にはデータの格納、検索、変更、削除といった基本的な操作の組み合わせで構成されています。そのため、データをどのように組織化し、管理するかという「データ構造」の選択は、プログラムの性能、保守性、拡張性を決定する最も重要な要素の一つです。

コンピュータサイエンスの初期から、研究者たちは様々なデータ構造を考案してきました。配列、連結リスト、スタック、キュー、ハッシュテーブル、二分探索木など、それぞれが特定の用途に最適化された特性を持っています。しかし、これらのデータ構造を一から実装することは、高度な専門知識と多大な時間を必要とします。

### プログラミング言語におけるデータ構造サポートの変遷

プログラミング言語の進化は、データ構造のサポート機能の充実と密接に関連しています。初期のプログラミング言語では、配列程度の基本的なデータ構造しか提供されていませんでした。プログラマーは、リストや木構造などの高度なデータ構造が必要な場合、ポインタや構造体を駆使して一から実装する必要がありました。

この状況は、以下のような深刻な問題を引き起こしていました：

**実装の複雑さ**：高度なデータ構造の実装には、メモリ管理、ポインタ操作、アルゴリズムの詳細な理解が必要で、多くのプログラマーにとって負担となっていました。

**バグの多発**：メモリリークやポインタエラーなど、低レベルの実装に起因するバグが頻発し、プログラムの信頼性が損なわれていました。

**車輪の再発明**：同じデータ構造を何度も実装することで、開発効率が著しく低下していました。

**性能の一貫性の欠如**：プログラマーの技量により、同じデータ構造でも性能に大きな差が生まれていました。

### ライブラリとフレームワークの概念

これらの問題を解決するため、1980年代頃から「ライブラリ」という概念が重要視されるようになりました。よく使用されるデータ構造やアルゴリズムを、再利用可能な形でパッケージ化し、プログラマーが簡単に利用できるようにする試みです。

しかし、単純なライブラリには限界もありました。異なるデータ構造間でインターフェイスが統一されていない、型安全性が確保されていない、相互運用性が低いなどの問題です。これらの課題を解決するため、1990年代に「フレームワーク」という、より統合的なアプローチが生まれました。

フレームワークは、単に機能を提供するだけでなく、一貫した設計思想とインターフェイスを持つ、包括的なソリューションです。プログラマーは、フレームワークが提供する統一されたインターフェイスを学習するだけで、多様なデータ構造を効率的に活用できるようになります。

### Javaコレクションフレームワークの革新性

Java 1.2（1998年）で導入されたコレクションフレームワークは、データ構造サポートにおける画期的な進歩を代表しています。このフレームワークの革新性は、以下の点にあります：

**統一されたインターフェイス設計**：List、Set、Mapという明確な概念でデータ構造を分類し、それぞれに統一されたインターフェイスを提供しました。これにより、プログラマーは具体的な実装の詳細を知らなくても、一貫した方法でデータを操作できます。

**実装の選択可能性**：同じインターフェイスに対して複数の実装を提供し、用途に応じて最適なものを選択できるようにしました。例えば、Listインターフェイスには、高速な要素アクセスに適したArrayListと、頻繁な挿入・削除に適したLinkedListが用意されています。

**型安全性の確保**：ジェネリクス（Java 1.5で導入）との組み合わせにより、コンパイル時の型チェックを実現し、実行時エラーを大幅に削減しました。

**アルゴリズムの統合**：Collections クラスを通じて、ソート、検索、シャッフルなどの汎用アルゴリズムを提供し、データ構造との緊密な連携を実現しました。

**イテレータパターンの実装**：すべてのコレクションに対して統一された反復処理の仕組みを提供し、for-each文やストリームAPIなどの言語機能との統合を可能にしました。

### 現代プログラミングにおけるコレクションの位置づけ

現代のソフトウェア開発において、コレクションフレームワークは空気のような存在になっています。Webアプリケーション、データ分析、機械学習、ゲーム開発など、あらゆる分野でコレクションが使用されており、その重要性はますます高まっています。

特に、ビッグデータ時代を迎えた現在、効率的なデータ処理は企業の競争力を左右する重要な要素となっています。Javaのコレクションフレームワークは、単純なデータ格納から、複雑なデータ分析まで、幅広いニーズに対応する基盤を提供しています。

また、関数型プログラミングの概念を取り入れたStream APIの導入により、宣言的なデータ処理が可能になり、より簡潔で理解しやすいコードの記述が実現されています。

### この章で学習する内容の意義

この章では、これらの歴史的背景と技術的意義を踏まえて、Javaコレクションフレームワークを体系的に学習していきます。単にAPIの使い方を覚えるのではなく、以下の点を重視して学習を進めます：

**適切なデータ構造の選択**：問題の性質を分析し、最適なコレクション実装を選択する判断力を養います。

**性能特性の理解**：各データ構造の時間計算量と空間計算量を理解し、スケーラブルなシステム設計の基礎を身につけます。

**実践的な使用パターン**：実際の開発現場でよく使用される効果的なコレクション活用パターンを習得します。

**モダンなJava機能との統合**：ラムダ式、ストリームAPI、関数型インターフェイスなどの現代的な機能との組み合わせ方を学習します。

コレクションフレームワークを深く理解することは、Javaプログラマーとしての基礎体力を大幅に向上させ、より高品質で効率的なソフトウェアの開発能力を身につけることにつながります。

## 7.1 コレクションフレームワークとは

### 配列の限界とコレクションの必要性

Javaプログラミングの初期に学ぶ配列は、複数のデータをまとめて管理するための基本的な手段です。しかし、配列には以下のような制限があります：

```c
// C言語の配列（固定サイズ）
int numbers[10];  // サイズ固定
int count = 0;

void addNumber(int num) {
    if (count < 10) {
        numbers[count++] = num;
    }
    // サイズを超えた場合の処理が困難
}
```

```java
// Javaの配列も同様の制限
int[] numbers = new int[10];
// 一度作成するとサイズを変更できない
// 要素の追加や削除に手間がかかる
```

これらの点を克服し、より柔軟かつ高機能なデータ管理を実現するのがコレクションフレームワークです：

```java
// Javaのコレクション（動的サイズ）
List<Integer> numbers = new ArrayList<>();

public void addNumber(int num) {
    numbers.add(num);  // サイズを気にする必要なし
    // 必要に応じて自動的にサイズが拡張される
}
```

### データ構造とコレクション

データを効率的に扱うための枠組みを「データ構造 (Data Structure)」と呼びます。Javaのコレクションフレームワークは、リスト、セット、マップといった代表的なデータ構造を、クラスやインターフェイスとして提供しています。

### インターフェイスとポリモーフィズムの役割

コレクションフレームワークの中心的な設計思想の1つに、インターフェイスに基づいたプログラミングがあります。主要なコレクションは、それぞれインターフェイスとして定義されています：

* `java.util.List`: 順序付けられた要素のコレクション。重複を許可します
* `java.util.Set`: 重複しない要素のコレクション。順序は保証されないか、特定の順序に従います
* `java.util.Map`: キーと値のペアを格納するコレクション。キーの重複は許可しません

```java
// Listインターフェイス型でArrayListのインスタンスを扱う
List<String> names = new ArrayList<>();
names.add("Alice");
names.add("Bob");

// 同じListインターフェイス型でLinkedListのインスタンスも扱える
// names = new LinkedList<>(); // 必要に応じて実装を切り替え可能
```

このようにインターフェイス型で変数を宣言することで、具体的な実装クラスに依存しないコードを書くことができます。これをポリモーフィズム（多態性）と呼び、コードの柔軟性や拡張性を高めます。

### ジェネリクス（Generics）と型安全性

コレクションフレームワークを扱う上で欠かせないのがジェネリクスです。ジェネリクスは、クラスやインターフェイスが扱うデータ型を、インスタンス化する際に指定する仕組みです。

```java
// String型の要素のみを格納できるListを宣言
List<String> stringList = new ArrayList<>();
stringList.add("Java");
// stringList.add(123); // コンパイルエラー！ String型以外は追加できない

// Integer型の要素のみを格納できるSetを宣言
Set<Integer> numberSet = new HashSet<>();
numberSet.add(100);
// numberSet.add("hello"); // コンパイルエラー！ Integer型以外は追加できない
```

ジェネリクスを使用する主な利点は「型安全性」の向上です。コレクションに格納するデータの型をコンパイル時に指定することで、意図しない型のデータが混入するのを防ぎます。

#### ダイヤモンド演算子と型推論

Java SE 7以降では、右辺のジェネリクス型指定を省略できる「ダイヤモンド演算子 (`<>`)」が導入されました：

```java
// Java SE 7以降
List<String> nameList = new ArrayList<>(); // ダイヤモンド演算子で型指定を省略
Map<Integer, String> userMap = new HashMap<>();

// Java SE 6以前
// List<String> nameListOld = new ArrayList<String>();
// Map<Integer, String> userMapOld = new HashMap<Integer, String>();
```

さらに、Java SE 10からは`var`を用いたローカル変数の型推論も導入されました：

```java
// Java SE 10以降（varキーワード）
var inferredList = new ArrayList<String>(); // inferredListはArrayList<String>型と推論される
inferredList.add("Type Inference");
```

## 7.2 Listインターフェース

### Listの基本概念

`List`は順序付きのコレクションで、重複する要素を許可します。配列と似ていますが、サイズが動的に変更可能です。

### ArrayList

`ArrayList`は、内部的に配列を使って実装されたリストです。ランダムアクセスが高速で、一般的に最もよく使用されます。

```java
import java.util.*;

public class ArrayListExample {
    public static void main(String[] args) {
        // ArrayListの作成
        List<String> fruits = new ArrayList<>();
        
        // 要素の追加
        fruits.add("りんご");
        fruits.add("バナナ");
        fruits.add("オレンジ");
        
        // 特定位置に挿入
        fruits.add(1, "ぶどう");
        
        // 要素の取得
        System.out.println("1番目の果物: " + fruits.get(1));
        
        // サイズの確認
        System.out.println("果物の数: " + fruits.size());
        
        // 要素の削除
        fruits.remove("バナナ");
        fruits.remove(0); // インデックスで削除
        
        // 全要素の表示
        System.out.println("全ての果物:");
        for (String fruit : fruits) {
            System.out.println("- " + fruit);
        }
        
        // 要素の存在確認
        if (fruits.contains("りんご")) {
            System.out.println("りんごが含まれています");
        }
        
        // リストのクリア
        fruits.clear();
        System.out.println("リストが空かどうか: " + fruits.isEmpty());
    }
}
```

### LinkedList

`LinkedList`は、リンクリスト構造で実装されたリストです。要素の挿入・削除が頻繁な場合に有利です。

```java
import java.util.*;

public class LinkedListExample {
    public static void main(String[] args) {
        LinkedList<Integer> numbers = new LinkedList<>();
        
        // 要素の追加
        numbers.add(10);
        numbers.add(20);
        numbers.add(30);
        
        // 先頭と末尾への追加（LinkedListの特徴）
        numbers.addFirst(5);
        numbers.addLast(40);
        
        System.out.println("リスト: " + numbers); // [5, 10, 20, 30, 40]
        
        // 先頭と末尾の要素を取得・削除
        System.out.println("先頭要素: " + numbers.getFirst());
        System.out.println("末尾要素: " + numbers.getLast());
        
        numbers.removeFirst();
        numbers.removeLast();
        
        System.out.println("削除後: " + numbers); // [10, 20, 30]
    }
}
```

### ArrayList vs LinkedList

| 操作 | ArrayList | LinkedList |
|------|-----------|------------|
| ランダムアクセス（get/set） | O(1) | O(n) |
| 末尾への追加（add） | O(1)* | O(1) |
| 先頭への挿入（add(0, element)） | O(n) | O(1) |
| 中間への挿入 | O(n) | O(n) |
| 削除 | O(n) | O(1)** |

*容量拡張が必要な場合はO(n)  
**削除対象のノードへの参照がある場合

### ListのソートとComparator

```java
import java.util.*;

public class ListSortExample {
    public static void main(String[] args) {
        List<String> names = new ArrayList<>(Arrays.asList("Charlie", "Alice", "Bob"));
        
        // 自然順序でソート
        Collections.sort(names);
        System.out.println("自然順序: " + names); // [Alice, Bob, Charlie]
        
        // 逆順でソート
        Collections.sort(names, Collections.reverseOrder());
        System.out.println("逆順: " + names); // [Charlie, Bob, Alice]
        
        // カスタムComparatorでソート（文字列長順）
        Collections.sort(names, Comparator.comparing(String::length));
        System.out.println("文字列長順: " + names);
        
        // Java 8のStreamを使用したソート
        List<String> sorted = names.stream()
                                  .sorted()
                                  .collect(Collectors.toList());
        System.out.println("Streamでソート: " + sorted);
    }
}
```

## 7.3 Setインターフェース

### Setの基本概念

`Set`は重複を許可しないコレクションです。数学の集合の概念に対応しています。

### HashSet

`HashSet`は、ハッシュテーブルを使って実装されたセットです。要素の順序は保証されませんが、高速な検索・追加・削除が可能です。

```java
import java.util.*;

public class HashSetExample {
    public static void main(String[] args) {
        Set<String> uniqueWords = new HashSet<>();
        
        // 要素の追加
        uniqueWords.add("Java");
        uniqueWords.add("Python");
        uniqueWords.add("Java"); // 重複は無視される
        uniqueWords.add("C++");
        
        System.out.println("ユニークな単語: " + uniqueWords);
        System.out.println("単語数: " + uniqueWords.size()); // 3
        
        // 要素の存在確認
        if (uniqueWords.contains("Java")) {
            System.out.println("Javaが含まれています");
        }
        
        // 全要素の反復処理
        for (String word : uniqueWords) {
            System.out.println("言語: " + word);
        }
        
        // 別のSetとの集合演算
        Set<String> otherLanguages = new HashSet<>(Arrays.asList("JavaScript", "Java", "Go"));
        
        // 和集合
        Set<String> union = new HashSet<>(uniqueWords);
        union.addAll(otherLanguages);
        System.out.println("和集合: " + union);
        
        // 積集合
        Set<String> intersection = new HashSet<>(uniqueWords);
        intersection.retainAll(otherLanguages);
        System.out.println("積集合: " + intersection);
        
        // 差集合
        Set<String> difference = new HashSet<>(uniqueWords);
        difference.removeAll(otherLanguages);
        System.out.println("差集合: " + difference);
    }
}
```

### LinkedHashSet

`LinkedHashSet`は、要素の挿入順序を保持するセットです。

```java
import java.util.*;

public class LinkedHashSetExample {
    public static void main(String[] args) {
        Set<String> orderedSet = new LinkedHashSet<>();
        orderedSet.add("第1要素");
        orderedSet.add("第2要素");
        orderedSet.add("第3要素");
        orderedSet.add("第1要素"); // 重複は無視されるが、順序は維持される
        
        System.out.println("挿入順序が保持されたSet: " + orderedSet);
        // 結果: [第1要素, 第2要素, 第3要素]
    }
}
```

### TreeSet

`TreeSet`は、要素を自然順序（またはComparatorによる順序）でソートして格納するセットです。

```java
import java.util.*;

public class TreeSetExample {
    public static void main(String[] args) {
        TreeSet<Integer> sortedNumbers = new TreeSet<>();
        sortedNumbers.add(30);
        sortedNumbers.add(10);
        sortedNumbers.add(20);
        sortedNumbers.add(10); // 重複は無視される
        
        System.out.println("ソート済みの数値: " + sortedNumbers); // [10, 20, 30]
        
        // TreeSet特有のメソッド
        System.out.println("最小値: " + sortedNumbers.first());
        System.out.println("最大値: " + sortedNumbers.last());
        System.out.println("20より小さい値: " + sortedNumbers.headSet(20));
        System.out.println("20以上の値: " + sortedNumbers.tailSet(20));
        
        // 範囲指定
        TreeSet<String> words = new TreeSet<>(Arrays.asList("apple", "banana", "cherry", "date"));
        System.out.println("b〜cの範囲: " + words.subSet("b", "d"));
    }
}
```

## 7.4 Mapインターフェース

### Mapの基本概念

`Map`は、キーと値のペアを格納するコレクションです。キーは重複できませんが、値は重複可能です。

### HashMap

`HashMap`は、ハッシュテーブルを使って実装されたマップです。

```java
import java.util.*;

public class HashMapExample {
    public static void main(String[] args) {
        Map<String, Integer> scores = new HashMap<>();
        
        // キーと値のペアを追加
        scores.put("Alice", 85);
        scores.put("Bob", 92);
        scores.put("Charlie", 78);
        scores.put("Alice", 90); // 既存のキーの場合、値が更新される
        
        System.out.println("スコア: " + scores);
        
        // 値の取得
        Integer aliceScore = scores.get("Alice");
        System.out.println("Aliceのスコア: " + aliceScore);
        
        // 存在しないキーに対する安全な取得
        Integer davidScore = scores.getOrDefault("David", 0);
        System.out.println("Davidのスコア: " + davidScore);
        
        // キーの存在確認
        if (scores.containsKey("Bob")) {
            System.out.println("Bobのデータが存在します");
        }
        
        // 値の存在確認
        if (scores.containsValue(92)) {
            System.out.println("92点の人がいます");
        }
        
        // 全てのキーを取得
        System.out.println("受験者一覧:");
        for (String name : scores.keySet()) {
            System.out.println("- " + name);
        }
        
        // 全ての値を取得
        System.out.println("スコア一覧: " + scores.values());
        
        // キーと値のペアを取得
        System.out.println("全てのエントリ:");
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + "点");
        }
        
        // Java 8のforEachを使用
        System.out.println("Java 8 forEach:");
        scores.forEach((name, score) -> 
            System.out.println(name + "さんのスコア: " + score + "点"));
    }
}
```

### LinkedHashMap

`LinkedHashMap`は、挿入順序または最後にアクセスした順序を保持するマップです。

```java
import java.util.*;

public class LinkedHashMapExample {
    public static void main(String[] args) {
        // 挿入順序を保持するLinkedHashMap
        Map<String, String> insertionOrderMap = new LinkedHashMap<>();
        insertionOrderMap.put("first", "1番目");
        insertionOrderMap.put("second", "2番目");
        insertionOrderMap.put("third", "3番目");
        
        System.out.println("挿入順序を保持: " + insertionOrderMap);
        
        // アクセス順序を保持するLinkedHashMap（LRUキャッシュの実装に使用）
        Map<String, String> accessOrderMap = new LinkedHashMap<>(16, 0.75f, true);
        accessOrderMap.put("A", "値A");
        accessOrderMap.put("B", "値B");
        accessOrderMap.put("C", "値C");
        
        // Aにアクセス
        accessOrderMap.get("A");
        System.out.println("Aアクセス後: " + accessOrderMap); // Aが最後に移動
    }
}
```

### TreeMap

`TreeMap`は、キーをソートして格納するマップです。

```java
import java.util.*;

public class TreeMapExample {
    public static void main(String[] args) {
        TreeMap<String, Integer> sortedMap = new TreeMap<>();
        sortedMap.put("Charlie", 78);
        sortedMap.put("Alice", 90);
        sortedMap.put("Bob", 85);
        
        System.out.println("キーでソート済み: " + sortedMap);
        // 結果: {Alice=90, Bob=85, Charlie=78}
        
        // TreeMap特有のメソッド
        System.out.println("最初のキー: " + sortedMap.firstKey());
        System.out.println("最後のキー: " + sortedMap.lastKey());
        
        // 範囲指定
        System.out.println("AからBの範囲: " + sortedMap.subMap("A", "C"));
    }
}
```

## 7.5 コレクションの選択指針

### パフォーマンス特性の比較

| データ構造 | 検索 | 挿入 | 削除 | 特徴 |
|-----------|------|------|------|------|
| ArrayList | O(1)* | O(1)** | O(n) | ランダムアクセス高速 |
| LinkedList | O(n) | O(1) | O(1)*** | 挿入・削除高速 |
| HashSet | O(1) | O(1) | O(1) | 重複なし、順序なし |
| LinkedHashSet | O(1) | O(1) | O(1) | 重複なし、挿入順序保持 |
| TreeSet | O(log n) | O(log n) | O(log n) | 重複なし、ソート済み |
| HashMap | O(1) | O(1) | O(1) | キー-値ペア、順序なし |
| LinkedHashMap | O(1) | O(1) | O(1) | キー-値ペア、順序保持 |
| TreeMap | O(log n) | O(log n) | O(log n) | キー-値ペア、キーでソート |

*インデックスアクセスの場合  
**末尾挿入の場合  
***削除対象への参照がある場合

### 使い分けの指針

#### Listの選択
- **ArrayList**: 一般的な用途、ランダムアクセスが必要な場合
- **LinkedList**: 頻繁な挿入・削除が必要な場合

#### Setの選択
- **HashSet**: 重複排除が主目的、順序不要
- **LinkedHashSet**: 重複排除かつ挿入順序保持が必要
- **TreeSet**: 重複排除かつソート済み状態が必要

#### Mapの選択
- **HashMap**: 一般的なキー-値ペア、順序不要
- **LinkedHashMap**: キー-値ペアかつ順序保持が必要
- **TreeMap**: キー-値ペアかつキーでソート済み状態が必要

## 7.6 コレクションの高度な操作

### Collections ユーティリティクラス

`Collections`クラスには、コレクションを操作するための便利なstaticメソッドが多数用意されています。

```java
import java.util.*;

public class CollectionsUtilityExample {
    public static void main(String[] args) {
        List<Integer> numbers = new ArrayList<>(Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6));
        
        // ソート
        Collections.sort(numbers);
        System.out.println("ソート後: " + numbers);
        
        // 逆順
        Collections.reverse(numbers);
        System.out.println("逆順: " + numbers);
        
        // シャッフル
        Collections.shuffle(numbers);
        System.out.println("シャッフル後: " + numbers);
        
        // 最大値・最小値
        System.out.println("最大値: " + Collections.max(numbers));
        System.out.println("最小値: " + Collections.min(numbers));
        
        // 二分探索（ソート済みリストが必要）
        Collections.sort(numbers);
        int index = Collections.binarySearch(numbers, 5);
        System.out.println("5の位置: " + index);
        
        // 要素の置換
        Collections.fill(numbers, 0);
        System.out.println("全て0に置換: " + numbers);
        
        // 不変コレクションの作成
        List<String> immutableList = Collections.unmodifiableList(
            Arrays.asList("読み専用", "変更不可", "安全"));
        // immutableList.add("追加"); // UnsupportedOperationException
        
        // 空のコレクション
        List<String> emptyList = Collections.emptyList();
        Set<String> emptySet = Collections.emptySet();
        Map<String, String> emptyMap = Collections.emptyMap();
    }
}
```

### 配列とコレクションの相互変換

```java
import java.util.*;

public class ArrayCollectionConversion {
    public static void main(String[] args) {
        // 配列からListへ
        String[] array = {"Apple", "Banana", "Cherry"};
        List<String> listFromArray = Arrays.asList(array);
        System.out.println("配列からList: " + listFromArray);
        
        // 注意：Arrays.asListで作成されたListは固定サイズ
        // listFromArray.add("Date"); // UnsupportedOperationException
        
        // 変更可能なListを作成
        List<String> mutableList = new ArrayList<>(Arrays.asList(array));
        mutableList.add("Date");
        System.out.println("変更可能なList: " + mutableList);
        
        // ListからListへ（Java 8以降）
        List<String> listFromStream = Arrays.stream(array)
                                          .collect(Collectors.toList());
        
        // Listから配列へ
        String[] arrayFromList = mutableList.toArray(new String[0]);
        System.out.println("Listから配列: " + Arrays.toString(arrayFromList));
        
        // Java 11以降の便利なメソッド
        List<String> listFromOf = List.of("A", "B", "C"); // 不変リスト
        Set<String> setFromOf = Set.of("X", "Y", "Z"); // 不変セット
        Map<String, Integer> mapFromOf = Map.of("one", 1, "two", 2); // 不変マップ
    }
}
```

## 7.7 Stream APIとの連携

### 基本的なStream操作

```java
import java.util.*;
import java.util.stream.*;

public class StreamWithCollections {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("apple", "banana", "cherry", "date", "elderberry");
        
        // 長さが5文字以上の単語をフィルタリング
        List<String> longWords = words.stream()
                                     .filter(word -> word.length() >= 5)
                                     .collect(Collectors.toList());
        System.out.println("5文字以上の単語: " + longWords);
        
        // 大文字に変換
        List<String> upperWords = words.stream()
                                      .map(String::toUpperCase)
                                      .collect(Collectors.toList());
        System.out.println("大文字変換: " + upperWords);
        
        // 文字数でソート
        List<String> sortedByLength = words.stream()
                                          .sorted(Comparator.comparing(String::length))
                                          .collect(Collectors.toList());
        System.out.println("文字数順: " + sortedByLength);
        
        // 文字数でグループ化
        Map<Integer, List<String>> groupedByLength = words.stream()
                .collect(Collectors.groupingBy(String::length));
        System.out.println("文字数でグループ化: " + groupedByLength);
        
        // 統計情報
        IntSummaryStatistics stats = words.stream()
                .mapToInt(String::length)
                .summaryStatistics();
        System.out.println("文字数統計: " + stats);
    }
}
```

### 並列処理

```java
import java.util.*;
import java.util.stream.*;

public class ParallelStreamExample {
    public static void main(String[] args) {
        List<Integer> numbers = IntStream.rangeClosed(1, 1_000_000)
                                        .boxed()
                                        .collect(Collectors.toList());
        
        // 順次処理
        long start = System.currentTimeMillis();
        long sum1 = numbers.stream()
                          .mapToLong(Integer::longValue)
                          .sum();
        long time1 = System.currentTimeMillis() - start;
        System.out.println("順次処理: " + sum1 + " (時間: " + time1 + "ms)");
        
        // 並列処理
        start = System.currentTimeMillis();
        long sum2 = numbers.parallelStream()
                          .mapToLong(Integer::longValue)
                          .sum();
        long time2 = System.currentTimeMillis() - start;
        System.out.println("並列処理: " + sum2 + " (時間: " + time2 + "ms)");
    }
}
```

## 7.8 実践的なコレクション活用例

### 単語カウンター

```java
import java.util.*;
import java.util.stream.*;

public class WordCounter {
    public static void main(String[] args) {
        String text = "Java is a popular programming language. " +
                     "Java is object-oriented. Java is platform-independent.";
        
        // 単語の出現回数をカウント
        Map<String, Long> wordCount = Arrays.stream(text.toLowerCase().split("\\s+"))
                .map(word -> word.replaceAll("[^a-z]", "")) // 記号を除去
                .filter(word -> !word.isEmpty())
                .collect(Collectors.groupingBy(
                    word -> word,
                    Collectors.counting()
                ));
        
        System.out.println("単語の出現回数:");
        wordCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> 
                    System.out.println(entry.getKey() + ": " + entry.getValue()));
    }
}
```

### 学生成績管理システム

```java
import java.util.*;
import java.util.stream.*;

class Student {
    private String name;
    private String subject;
    private int score;
    
    public Student(String name, String subject, int score) {
        this.name = name;
        this.subject = subject;
        this.score = score;
    }
    
    // getter メソッド
    public String getName() { return name; }
    public String getSubject() { return subject; }
    public int getScore() { return score; }
    
    @Override
    public String toString() {
        return String.format("%s(%s: %d点)", name, subject, score);
    }
}

public class StudentGradeManager {
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
            new Student("Alice", "数学", 85),
            new Student("Bob", "数学", 92),
            new Student("Alice", "英語", 78),
            new Student("Charlie", "数学", 88),
            new Student("Bob", "英語", 85),
            new Student("Charlie", "英語", 90)
        );
        
        // 学生別の平均点
        Map<String, Double> averageByStudent = students.stream()
                .collect(Collectors.groupingBy(
                    Student::getName,
                    Collectors.averagingInt(Student::getScore)
                ));
        
        System.out.println("学生別平均点:");
        averageByStudent.forEach((name, avg) -> 
            System.out.printf("%s: %.1f点%n", name, avg));
        
        // 科目別の平均点
        Map<String, Double> averageBySubject = students.stream()
                .collect(Collectors.groupingBy(
                    Student::getSubject,
                    Collectors.averagingInt(Student::getScore)
                ));
        
        System.out.println("\n科目別平均点:");
        averageBySubject.forEach((subject, avg) -> 
            System.out.printf("%s: %.1f点%n", subject, avg));
        
        // 90点以上の成績
        List<Student> topScores = students.stream()
                .filter(student -> student.getScore() >= 90)
                .sorted(Comparator.comparing(Student::getScore).reversed())
                .collect(Collectors.toList());
        
        System.out.println("\n90点以上の成績:");
        topScores.forEach(System.out::println);
    }
}
```

## 7.9 匿名クラスとラムダ式

### 匿名クラスからラムダ式への進化

Java 8より前では、関数型の処理を記述するために匿名クラスを使用していました。ラムダ式の導入により、より簡潔で読みやすいコードを書けるようになりました。

```java
import java.util.*;
import java.util.function.*;

public class AnonymousToLambda {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Charlie", "Alice", "Bob");
        
        // 従来の匿名クラス
        Collections.sort(names, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareTo(s2);
            }
        });
        System.out.println("匿名クラスでソート: " + names);
        
        // ラムダ式（簡潔版）
        names = Arrays.asList("Charlie", "Alice", "Bob");
        Collections.sort(names, (s1, s2) -> s1.compareTo(s2));
        System.out.println("ラムダ式でソート: " + names);
        
        // メソッド参照（さらに簡潔）
        names = Arrays.asList("Charlie", "Alice", "Bob");
        Collections.sort(names, String::compareTo);
        System.out.println("メソッド参照でソート: " + names);
    }
}
```

### 関数型インターフェイスの活用

```java
import java.util.*;
import java.util.function.*;

public class FunctionalInterfaceExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // Predicate<T>: T -> boolean
        Predicate<Integer> isEven = n -> n % 2 == 0;
        List<Integer> evenNumbers = numbers.stream()
                                          .filter(isEven)
                                          .collect(Collectors.toList());
        System.out.println("偶数: " + evenNumbers);
        
        // Function<T, R>: T -> R
        Function<Integer, String> numberToString = n -> "数値: " + n;
        List<String> stringNumbers = numbers.stream()
                                           .map(numberToString)
                                           .collect(Collectors.toList());
        System.out.println("文字列変換: " + stringNumbers.subList(0, 3));
        
        // Consumer<T>: T -> void
        Consumer<String> printer = s -> System.out.println("出力: " + s);
        Arrays.asList("Hello", "World").forEach(printer);
        
        // Supplier<T>: () -> T
        Supplier<Double> randomSupplier = () -> Math.random();
        System.out.println("ランダム値: " + randomSupplier.get());
        
        // BinaryOperator<T>: (T, T) -> T
        BinaryOperator<Integer> multiply = (a, b) -> a * b;
        Optional<Integer> product = numbers.stream()
                                          .reduce(multiply);
        System.out.println("総積: " + product.orElse(0));
    }
}
```

### メソッド参照の種類

```java
import java.util.*;
import java.util.function.*;

public class MethodReferenceExample {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("apple", "banana", "cherry");
        
        // 1. 静的メソッド参照
        words.forEach(System.out::println);
        
        // 2. インスタンスメソッド参照（特定のオブジェクト）
        String prefix = "Fruit: ";
        Function<String, String> addPrefix = prefix::concat;
        words.stream()
             .map(addPrefix)
             .forEach(System.out::println);
        
        // 3. インスタンスメソッド参照（任意のオブジェクト）
        words.stream()
             .map(String::toUpperCase)
             .forEach(System.out::println);
        
        // 4. コンストラクタ参照
        Supplier<List<String>> listSupplier = ArrayList::new;
        List<String> newList = listSupplier.get();
        
        Function<String, StringBuilder> sbBuilder = StringBuilder::new;
        StringBuilder sb = sbBuilder.apply("Hello");
        System.out.println("StringBuilder: " + sb);
    }
}
```

## 7.10 高度なStream API

### 複雑なデータ処理

```java
import java.util.*;
import java.util.stream.*;

class Product {
    private String name;
    private String category;
    private double price;
    
    public Product(String name, String category, double price) {
        this.name = name;
        this.category = category;
        this.price = price;
    }
    
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    
    @Override
    public String toString() {
        return String.format("%s(%s: ¥%.0f)", name, category, price);
    }
}

public class AdvancedStreamExample {
    public static void main(String[] args) {
        List<Product> products = Arrays.asList(
            new Product("ノートPC", "電子機器", 80000),
            new Product("マウス", "電子機器", 2000),
            new Product("本", "書籍", 1500),
            new Product("コーヒー", "飲料", 500),
            new Product("スマートフォン", "電子機器", 120000),
            new Product("雑誌", "書籍", 800)
        );
        
        // カテゴリ別の商品数と平均価格
        Map<String, Map<String, Object>> categoryStats = products.stream()
                .collect(Collectors.groupingBy(
                    Product::getCategory,
                    Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> Map.of(
                            "count", list.size(),
                            "avgPrice", list.stream().mapToDouble(Product::getPrice).average().orElse(0.0),
                            "products", list
                        )
                    )
                ));
        
        System.out.println("カテゴリ別統計:");
        categoryStats.forEach((category, stats) -> {
            System.out.printf("%s: %d商品, 平均価格¥%.0f%n", 
                category, stats.get("count"), stats.get("avgPrice"));
        });
        
        // 価格帯別グループ化
        Map<String, List<Product>> priceRanges = products.stream()
                .collect(Collectors.groupingBy(product -> {
                    double price = product.getPrice();
                    if (price < 1000) return "安価";
                    else if (price < 10000) return "中価格";
                    else return "高価格";
                }));
        
        System.out.println("\n価格帯別商品:");
        priceRanges.forEach((range, productList) -> {
            System.out.println(range + ": " + productList.size() + "商品");
            productList.forEach(p -> System.out.println("  " + p));
        });
        
        // 最も高価な商品をカテゴリ別に取得
        Map<String, Optional<Product>> mostExpensiveByCategory = products.stream()
                .collect(Collectors.groupingBy(
                    Product::getCategory,
                    Collectors.maxBy(Comparator.comparing(Product::getPrice))
                ));
        
        System.out.println("\nカテゴリ別最高価格商品:");
        mostExpensiveByCategory.forEach((category, product) -> 
            product.ifPresent(p -> System.out.println(category + ": " + p)));
    }
}
```

### 並列ストリームの活用

```java
import java.util.*;
import java.util.stream.*;

public class ParallelProcessingExample {
    public static void main(String[] args) {
        // 大きなデータセットでの処理比較
        List<Integer> largeList = IntStream.rangeClosed(1, 10_000_000)
                                          .boxed()
                                          .collect(Collectors.toList());
        
        // 順次処理での素数カウント
        long start = System.currentTimeMillis();
        long primeCount1 = largeList.stream()
                                   .filter(ParallelProcessingExample::isPrime)
                                   .count();
        long sequentialTime = System.currentTimeMillis() - start;
        
        // 並列処理での素数カウント
        start = System.currentTimeMillis();
        long primeCount2 = largeList.parallelStream()
                                   .filter(ParallelProcessingExample::isPrime)
                                   .count();
        long parallelTime = System.currentTimeMillis() - start;
        
        System.out.printf("素数の数: %d%n", primeCount1);
        System.out.printf("順次処理時間: %dms%n", sequentialTime);
        System.out.printf("並列処理時間: %dms%n", parallelTime);
        System.out.printf("高速化率: %.2fx%n", (double)sequentialTime / parallelTime);
    }
    
    private static boolean isPrime(int n) {
        if (n < 2) return false;
        if (n == 2) return true;
        if (n % 2 == 0) return false;
        
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }
}
```

## まとめ

この章では、Javaのコレクションフレームワークについて包括的に学習しました。以下の重要なポイントを覚えておきましょう：

### 主要なポイント

1. **適切なコレクションの選択**: List、Set、Mapの特性を理解し、用途に応じて選択する
2. **ジェネリクスの活用**: 型安全性を確保し、キャストエラーを防ぐ
3. **インターフェイスベースのプログラミング**: 具体的な実装に依存しない柔軟なコード
4. **パフォーマンス特性の理解**: データサイズや操作頻度に応じた最適な選択
5. **Stream APIとの連携**: 関数型プログラミングによる簡潔で読みやすいコード
6. **ラムダ式とメソッド参照**: 匿名クラスからの進化による簡潔な記述

### 実践での活用

- **ArrayList**: 一般的なリスト操作
- **HashMap**: キー-値ペアの管理
- **HashSet**: 重複排除
- **Stream API**: データの変換・フィルタリング・集計
- **ラムダ式**: 簡潔な関数型処理

コレクションフレームワークは、効率的で保守性の高いJavaプログラムを作成するための基盤となる重要な技術です。適切に活用することで、より品質の高いアプリケーションを開発できるようになります。