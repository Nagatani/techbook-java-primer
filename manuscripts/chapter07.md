# 第7章 コレクションフレームワークの基礎

## 🎯総合演習プロジェクトへのステップ

配列の限界を超え、より柔軟なデータ管理を実現するコレクションフレームワークは、**総合演習プロジェクト「TODOリストアプリケーション」** の中核を担います。

- **`ArrayList<Task>`**: この章で学ぶ`ArrayList`は、アプリケーション内で複数の`Task`オブジェクトを管理するためのリストとして使用します。タスクの追加、削除、一覧表示といった基本機能は、すべて`ArrayList`の操作に基づいています。
- **動的なタスク管理**: ユーザーがタスクを自由に追加・削除できるのは、コレクションが持つ動的なサイズ変更機能のおかげです。

この章を学ぶことで、アプリケーションのデータをメモリ上で効率的に扱うための、Javaプログラマ必須のスキルが身につきます。

## 📋 本章の学習目標

### 前提知識
- **クラスとオブジェクト**: `new`キーワードを使ったインスタンスの生成。
- **インターフェイス**: インターフェイスの基本的な役割の理解。
- **基本的なデータ構造**: 配列の概念と使い方。

### 到達目標

#### 知識理解目標
- 配列の限界とコレクションフレームワークの必要性を理解する。
- `List`, `Set`, `Map`という3つの主要なインターフェイスの特性（順序、重複）を理解する。
- ジェネリクスを使った型安全なコレクションのプログラミング方法を理解する。
- `ArrayList`, `HashSet`, `HashMap`の基本的な役割と用途を説明できる。

#### 技能習得目標
- `ArrayList`を作成し、要素の追加、取得、削除ができる。
- `HashSet`を作成し、重複しない要素を管理できる。
- `HashMap`を作成し、キーと値のペアを追加、取得、削除できる。
- for-eachループを使って、コレクションの全要素を反復処理できる。

---

## 7.1 データ管理の進化：配列からコレクションへ

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

Javaのコレクションフレームワークは、データを効率的に扱うためのさまざまな「データ構造」を、再利用可能なクラスやインターフェイスとして���系的にまとめたものです。

- **動的なサイズ**: 要素の数に応じて自動的にサイズが調整されます。
- **豊富な機能**: 要素の追加、削除、検索、ソートなどの便利なメソッドが標準で提供されています。

## 7.2 ジェネリクスと型安全性

コレクションフレームワークを安全に使う上で欠かせないのが**ジェネリクス（Generics）** です。ジェネリクスは、コレクションが「どの型のデータ」を格納するのかを、コンパイル時に明確に指定するための仕組みです。

```java
// String型の要素だけを格納できるListを宣言
List<String> stringList = new ArrayList<>();

stringList.add("Java");
// stringList.add(123); // コンパイルエラー！ String型以外は追加できない
```

これにより、意図しない型のデータが混入するのを防ぎ、プログラムの安全性を大幅に向上させます。`< >`の中に型名を記述し、Java 7以降は右辺の型名を省略できる**ダイヤモンド演算子** (`<>`)が使えます。

## 7.3 `List`インターフェイス：順序のあるデータ列

`List`は、**順序付けられた**要素のコレクションで、**重複を許可します**。配列に最も近い感覚で使え、追加した順に要素が格納されます。

### `ArrayList`：もっとも身近なリスト

`ArrayList`は、`List`インターフェイスのもっとも代表的な実装クラスです。内部的に配列を使っており、要素へのランダムアクセス（インデックス指定での取得）が非常に高速です。

#### `ArrayList`の基本的な使い方

```java
import java.util.ArrayList;
import java.util.List;

public class ArrayListExample {
    public static void main(String[] args) {
        // String型の要素を格納するArrayListを作成
        List<String> fruits = new ArrayList<>();

        // 1. 要素の追加 (add)
        fruits.add("りんご");
        fruits.add("バナナ");
        fruits.add("みかん");
        fruits.add("りんご"); // 重複も許可される

        System.out.println("現在のリスト: " + fruits);

        // 2. 要素の取得 (get)
        // インデックスは0から始まる
        String secondFruit = fruits.get(1);
        System.out.println("2番目の果物: " + secondFruit);

        // 3. 要素数の取得 (size)
        System.out.println("果物の数: " + fruits.size());

        // 4. 要素の削除 (remove)
        fruits.remove("バナナ"); // 値で削除
        System.out.println("「バナナ」を削除後: " + fruits);
        fruits.remove(0);      // インデックスで削除
        System.out.println("先頭を削除後: " + fruits);

        // 5. 全要素の反復処理 (for-each)
        System.out.println("残りの果物:");
        for (String fruit : fruits) {
            System.out.println("- " + fruit);
        }
    }
}
```

## 7.4 `Set`インターフェイス：重複のないユニークな集合

`Set`は、**重複しない**要素のコレクションです。順序は保証されません（追加した順に取り出せるとは限りません）。ユーザーIDやメールアドレスなど、一意性を保証したいデータの管理に適しています。

### `HashSet`：高速な重複排除

`HashSet`は、`Set`インターフェイスのもっとも代表的な実装クラスです。ハッシュという仕組みを使い、要素の追加や検索を非常に高速に行います。

#### `HashSet`の基本的な使い方

```java
import java.util.HashSet;
import java.util.Set;

public class HashSetExample {
    public static void main(String[] args) {
        // タグを管理するHashSetを作成
        Set<String> tags = new HashSet<>();

        // 1. 要素の追加 (add)
        tags.add("Java");
        tags.add("プログラミング");
        tags.add("初心者向け");

        // 2. 重複する要素を追加しようとする
        boolean added = tags.add("Java"); // すでに存在するため追加されない

        System.out.println("タグ一覧: " + tags);
        System.out.println("「Java」は追加されたか？: " + added); // false

        // 3. 要素の存在確認 (contains)
        if (tags.contains("プログラミング")) {
            System.out.println("「プログラミング」タグは存在します。");
        }

        // 4. 要素の削除 (remove)
        tags.remove("初心者向け");
        System.out.println("削除後のタグ: " + tags);
    }
}
```

## 7.5 `Map`インターフェイス：キーと値のペア

`Map`は、一意の**キー（Key）** と、それに対応する**値（Value）** のペアを格納するコレクションです。キーを使って高速に値を取得できます。

### `HashMap`：もっとも使われるマップ

`HashMap`は、`Map`インターフェイスのもっとも代表的な実装クラスです。`HashSet`と同様にハッシュを使い、キーによる高速な値の検索を実現します。

#### `HashMap`の基本的な使い方

```java
import java.util.HashMap;
import java.util.Map;

public class HashMapExample {
    public static void main(String[] args) {
        // 学生の点数を管理するHashMapを作成 (キー: 名前, 値: 点数)
        Map<String, Integer> scores = new HashMap<>();

        // 1. 要素の追加 (put)
        scores.put("Alice", 85);
        scores.put("Bob", 92);
        scores.put("Charlie", 78);
        scores.put("Alice", 95); // 同じキーでputすると、値が上書きされる

        System.out.println("全員の点数: " + scores);

        // 2. 要素の取得 (get)
        int bobScore = scores.get("Bob");
        System.out.println("Bobの点数: " + bobScore);

        // 3. キーの存在確認 (containsKey)
        if (scores.containsKey("David")) {
            System.out.println("Davidのデータは存在します。");
        } else {
            System.out.println("Davidのデータはありません。");
        }

        // 4. 全要素の反復処理 (keySetとfor-each)
        System.out.println("--- 点数一覧 ---");
        for (String name : scores.keySet()) {
            int score = scores.get(name);
            System.out.println(name + ": " + score + "点");
        }
    }
}
```

## まとめ

本章では、コレクションフレームワークの3つの基本インターフェイスと、その代表的な実装クラスを学びました。

| インターフェイス | 特徴 | 代表的な実装 | 主な用途 |
| :--- | :--- | :--- | :--- |
| `List` | 順序あり、重複OK | `ArrayList` | 順番にデータを並べたいとき |
| `Set` | 順序なし、重複NG | `HashSet` | 重複をなくしたいとき |
| `Map` | キーと値のペア | `HashMap` | IDと情報のように紐付けて管理したいとき |

これらのコレクションを適切に使い分けることが、効率的で読みやすいプログラムを書くための第一歩です。次章では、これらのコレクションをさらに便利に使うための応用的なテクニックを学びます。