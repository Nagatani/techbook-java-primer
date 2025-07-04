# 第8章 コレクションの応用とデータ構造

## 📋 本章の学習目標

### 前提知識
- **第7章のコレクション基礎**: `List`, `Set`, `Map`の基本概念と`ArrayList`, `HashSet`, `HashMap`の使い方。
- **インターフェイスとポリモーフィズム**: インターフェイス型でインスタンスを扱う利点の理解。
- **オブジェクトの比較**: `equals`と`hashCode`の役割の基本的な理解。

### 到達目標

#### 知識理解目標
- `ArrayList`と`LinkedList`の性能特性の違いを理解し、使い分けを説明できる。
- `HashSet`, `LinkedHashSet`, `TreeSet`の特性（順序、ソート）の違いを理解し、使い分けを説明できる。
- `HashMap`, `LinkedHashMap`, `TreeMap`の特性の違いを理解し、使い分けを説明できる。
- `Comparator`を使ったカスタムソートの概念を理解する。
- `Collections`ユーティリティクラスの便利な機能を知る。

#### 技能習得目標
- 用途に応じて、`LinkedList`や`TreeSet`などの応用的なコレクションを選択して使用できる。
- `Comparator`を実装（��たはラムダ式で記述）し、オブジェクトのリストを特定のルールでソートできる。
- `Collections.sort()`や`Collections.reverse()`などのユーティリティメソッドを使える。
- 配列と`List`を相互に変換できる。

---

## 8.1 Listの応用：ArrayList vs LinkedList

前章では`ArrayList`を学びましたが、`List`インターフェイスにはもう1つ重要な実装クラス`LinkedList`があります。この2つは、内部的なデータの持ち方（データ構造）が異なり、それによって得意な操作と不得意な操作が生まれます。

### データ構造の違い

- **`ArrayList`**: 内部的に**配列**を使用します。メモリ上で連続した領域にデータを格納します。
- **`LinkedList`**: **リンクリスト（連結リスト）** という構造を使用します。各要素が「次の要素はどこか」という情報（ポインタ）を数珠つなぎのように持っています。

| 操作 | `ArrayList` (配列) | `LinkedList` (連結リスト) |
| :--- | :--- | :--- |
| **要素の取得 (get)** | **高速 (O(1))**<br>インデックスで直接アクセスできる。 | **低速 (O(n))**<br>先頭から順番にたどる必要がある。 |
| **先頭/末尾への追加・削除** | 低速 (O(n))<br>先頭操作は全要素のシフトが必要。 | **高速 (O(1))**<br>つなぎ変えるだけで済む。 |
| **中間への追加・削除** | 低速 (O(n))<br>後続要素のシフトが必要。 | 低速 (O(n))<br>目的の場所までたどる必要がある。 |

**使い分けの指針:**
- **`ArrayList`**: 要素の参照（取得）が多く、リストの末尾以外での追加・削除が少ない場合に最適。**ほとんどのケースで第一候補となります。**
- **`LinkedList`**: リストの先頭や末尾で頻繁に要素を追加・削除するような、キュー（待ち行列）やスタックのような使い方をする場合に有効です。

```java
import java.util.LinkedList;

public class LinkedListExample {
    public static void main(String[] args) {
        LinkedList<String> queue = new LinkedList<>();

        // キューの末尾に要素を追加 (offer)
        queue.offer("タスク1");
        queue.offer("タスク2");
        queue.offer("タスク3");
        System.out.println("現在のキュー: " + queue);

        // キューの先頭から要素を取り出す (poll)
        String nextTask = queue.poll();
        System.out.println("実行するタスク: " + nextTask);
        System.out.println("残りのキュー: " + queue);
    }
}
```

## 8.2 Setの応用：順序とソートを使い分ける

`HashSet`は順序を保証しませんが、用途によっては「追加した順」や「ソートされた順」で要素を管理したい場合があります。

### `LinkedHashSet`：挿入順序を保持するSet

`LinkedHashSet`は、`HashSet`の高速な検索性能を維持しつつ、**要素が追加された順序を記憶します**。

```java
import java.util.LinkedHashSet;
import java.util.Set;

public class LinkedHashSetExample {
    public static void main(String[] args) {
        Set<String> history = new LinkedHashSet<>();
        history.add("ページAを表示");
        history.add("ページCを表示");
        history.add("ページBを表示");
        history.add("ページAを表示"); // 重複は無視される

        // 追加した順序で表示される
        System.out.println("閲覧履歴: " + history);
        // 出力: [ページAを表示, ページCを表示, ページBを表示]
    }
}
```

### `TreeSet`：自動でソートされるSet

`TreeSet`は、要素を追加すると**自動的にソートされた状態を維持します**。数値や文字列など、自然な順序を持つ要素は自動でソートされます。カスタムオブジェクトを格納する場合は、ソート順のルールを定義する必要があります（`Comparable`インターフェイスの実装）。

```java
import java.util.Set;
import java.util.TreeSet;

public class TreeSetExample {
    public static void main(String[] args) {
        Set<Integer> sortedScores = new TreeSet<>();
        sortedScores.add(85);
        sortedScores.add(92);
        sortedScores.add(78);
        sortedScores.add(92); // 重複は無視

        // 自動的に昇順でソートされている
        System.out.println("点数一覧: " + sortedScores);
        // 出力: [78, 85, 92]
    }
}
```

## 8.3 Mapの応用：順序とソート

`Map`にも`Set`と同様に、順序やソートを扱うための実装クラスがあります。

- **`LinkedHashMap`**: **キーが追加された順序**を保持します。
- **`TreeMap`**: **キーを自動的にソート**して管理します。

```java
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class OrderedMapExample {
    public static void main(String[] args) {
        // 挿入順序を保持するLinkedHashMap
        Map<String, Integer> linkedMap = new LinkedHashMap<>();
        linkedMap.put("B", 2);
        linkedMap.put("C", 3);
        linkedMap.put("A", 1);
        System.out.println("LinkedHashMap: " + linkedMap);
        // 出力: {B=2, C=3, A=1}

        // キーでソートするTreeMap
        Map<String, Integer> treeMap = new TreeMap<>();
        treeMap.put("B", 2);
        treeMap.put("C", 3);
        treeMap.put("A", 1);
        System.out.println("TreeMap:       " + treeMap);
        // 出力: {A=1, B=2, C=3}
    }
}
```

## 8.4 コレクションのソートと`Comparator`

`List`を自然な順序（数値の大小、文字列の辞書順）以外でソートしたい場合、**`Comparator`** を使って独自の比較ルールを定義します。

### `Comparator`によるカスタムソート

`Student`オブジェクトのリストを、点数が高い順にソートする例を見てみましょう。

```java
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class Student {
    String name;
    int score;

    public Student(String name, int score) {
        this.name = name;
        this.score = score;
    }

    @Override
    public String toString() {
        return name + "(" + score + "点)";
    }
}

// 点数を比較するためのComparatorクラス
class ScoreComparator implements Comparator<Student> {
    @Override
    public int compare(Student s1, Student s2) {
        // s1がs2より「大きい」場合に負の値を返すことで降順にする
        if (s1.score > s2.score) {
            return -1;
        } else if (s1.score < s2.score) {
            return 1;
        } else {
            return 0;
        }
        // 簡潔な書き方: return Integer.compare(s2.score, s1.score);
    }
}

public class SortExample {
    public static void main(String[] args) {
        List<Student> students = new ArrayList<>();
        students.add(new Student("Alice", 85));
        students.add(new Student("Bob", 92));
        students.add(new Student("Charlie", 78));

        // Comparatorを使ってソート
        students.sort(new ScoreComparator());

        System.out.println("点数順ランキング: " + students);
    }
}
```

### ラムダ式による`Comparator`の簡略化

Java 8以降では、ラムダ式を使うことで`Comparator`をより簡潔に記述できます。

```java
// 上記の students.sort(new ScoreComparator()); と同じ意味
students.sort((s1, s2) -> Integer.compare(s2.score, s1.score));

// さらにComparatorの静的メソッドを使うと、より読みやすくなる
students.sort(Comparator.comparingInt(s -> s.score).reversed());
```

## 8.5 `Collections`ユーティリティクラス

`Collections`クラス（`Collection`ではない点に注意）には、コレクションを操作するための便利な静的メソッドが多数用意されています。

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollectionsUtilExample {
    public static void main(String[] args) {
        List<Integer> numbers = new ArrayList<>();
        numbers.add(30);
        numbers.add(10);
        numbers.add(20);

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
    }
}
```

## まとめ

本章では、コレクションフレームワークの応用的な側面を学びました。

- **データ構造の選択**: `ArrayList`と`LinkedList`、`HashSet`と`TreeSet`など、それぞれの長所・短所を理解し、状況に応じて最適な実装クラスを選択することが重要です。
- **順序とソート**: `LinkedHashSet`/`LinkedHashMap`は挿入順を、`TreeSet`/`TreeMap`は自然順序または`Comparator`によるソート順を維持します。
- **カスタムソート**: `Comparator`を使うことで、独自のルールで`List`をソートできます。

これらの知識を身につけることで、より効率的で、要件に即したデータ管理が可能になります。次章では、コレクションのデータをより強力かつ宣言的に扱うための「ラムダ式」と「Stream API」について学びます。