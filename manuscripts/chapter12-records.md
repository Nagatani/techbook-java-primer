# 第12章 レコード(Records)

## 📝 章末演習

本章で学んだRecordクラスの概念を活用して、実践的な練習課題に取り組みましょう。

### 🎯 演習の目標
- Recordクラスの基本概念と利点の理解
- 不変データ構造としてのRecordの活用
- 従来のクラスとRecordの使い分け
- Recordの制限事項と適用場面の理解
- Recordを使ったデータ中心設計の実践
- データ転送オブジェクト（DTO）としてのRecordの効果的な活用

### 📁 課題の場所
演習課題は `exercises/chapter12/` ディレクトリに用意されています：

```
exercises/chapter12/
├── basic/          # 基本課題（必須）
│   ├── README.md   # 課題の詳細説明
│   ├── PersonRecord.java # 課題1: 基本的なRecord定義と活用
│   ├── PersonRecordTest.java
│   ├── ImmutableData.java # 課題2: 不変データ構造
│   ├── ImmutableDataTest.java
│   ├── DTOExample.java # 課題3: データ転送オブジェクト
│   └── DTOExampleTest.java
├── advanced/       # 発展課題（推奨）
└── challenge/      # 挑戦課題（上級者向け）
```

### 🚀 推奨する学習の進め方

1. **基本課題**から順番に取り組む
2. 各課題のREADME.mdで詳細を確認
3. ToDoコメントを参考に実装
4. Recordと従来のクラスの違いを理解する
5. 不変性の利点と活用場面を習得する

基本課題が完了したら、`advanced/`の発展課題でより複雑なデータ構造設計に挑戦してみましょう！

## 📋 本章の学習目標

### 前提知識
**必須前提**：
- 第7章の不変性とfinalの理解
- クラス設計と実装の経験
- equals、hashCode、toStringメソッドの基本理解

**設計経験前提**：
- データクラスの実装経験とその課題の理解
- ボイラープレートコードの問題意識

### 学習目標
**知識理解目標**：
- Recordの設計思想と従来のクラスとの違い
- 不変データ構造の利点と適用場面
- Recordの制約事項と限界
- データ指向プログラミングの基本概念

**技能習得目標**：
- Recordを使った簡潔なデータクラスの定義
- Recordのコンストラクタとメソッドのカスタマイズ
- 既存のクラスからRecordへのリファクタリング
- RecordとGenericsの組み合わせ

**設計能力目標**：
- データ中心設計における適切なRecord活用
- 不変性を活用した安全なAPI設計
- Recordとクラスの使い分け判断

**到達レベルの指標**：
- データ表現に適したRecordが効率的に定義できる
- 複雑なデータ構造でもRecordを適切に活用できる
- 不変性の利点を活かしたスレッドセーフな設計ができる
- 既存のコードベースにRecordを適切に導入できる

---

## 12.1 Recordとは？

**Record**は、Java 16で正式に導入された、**不変（immuテーブル）なデータを保持するため**の、簡潔なクラスを定義するための機能です。

これまで、データを保持するためだけのクラス（データクラスやデータキャリアと呼ばれる）を作るには、多くの定型的なコード（ボイラープレートコード）が必要でした。

-   `private final`なフィールド
-   全フィールドを初期化するコンストラクタ
-   全フィールドのゲッタメソッド（アクセサ）
-   `equals()`, `hashCode()`, `toString()` メソッドのオーバーライド

`Record`は、これらの定型コードを**コンパイラが自動的に生成**してくれます。

### 基本的な構文

```java
public record Person(String name, int age) {}
```

これだけで、以下をすべて定義したのとほぼ同じ意味になります。

```java
// 上記のRecord定義とほぼ等価なクラス
public final class Person { // finalクラスになる
    private final String name; // private finalフィールド
    private final int age;

    public Person(String name, int age) { // コンストラクタ
        this.name = name;
        this.age = age;
    }

    public String name() { return this.name; } // アクセサメソッド（getXXXではない）
    public int age() { return this.age; }

    @Override
    public boolean equals(Object o) { /* 全フィールドを比較する実装 */ }

    @Override
    public int hashCode() { /* 全フィールドから計算する実装 */ }

    @Override
    public String toString() { /* 全フィールドを表示する実装 */ }
}
```

### Recordのメリット・デメリット

**メリット:**
- **簡潔さ**: ボイラープレートコードを劇的に削減できます。
- **不変性（Immutability）**: フィールドはすべて`final`となり、一度作成したオブジェクトの状態は変更できません。これにより、プログラムの安全性が向上し、特にマルチスレッド環境で安心して扱えます。
- **明確な意図**: このクラスが「データを保持するためのものである」という意図が明確になります。

**デメリット:**
- **拡張性の制限**: `Record`は暗黙的に`final`であり、ほかのクラスを継承したり、ほかのクラスに継承させたりすることはできません。
- **可変オブジェクトには不向き**: 状態を変更する必要があるオブジェクトには使えません。

## 12.2 Recordの使い方

`Record`の使い方は、通常のクラスとほとんど同じです。

```java
public class RecordExample {
    public static void main(String[] args) {
        // インスタンスの作成
        Person alice = new Person("Alice", 30);
        Person bob = new Person("Bob", 40);
        Person alice2 = new Person("Alice", 30);

        // アクセサメソッドで値を取得（メソッド名はフィールド名と同じ）
        System.out.println("名前: " + alice.name());
        System.out.println("年齢: " + alice.age());

        // toString()が自動生成されている
        System.out.println(alice); // Person[name=Alice, age=30]

        // equals()が全フィールドを比較するように自動生成されている
        System.out.println("aliceとbobは等しいか？: " + alice.equals(bob));     // false
        System.out.println("aliceとalice2は等しいか？: " + alice.equals(alice2)); // true
    }
}
```

### コンパクトコンストラクタ

`Record`では、引数のバリデーション（検証）などのために、**コンパクトコンストラクタ**という特別な構文が使えます。引数リストを省略して記述し、フィールドへの代入（`this.x = x;`）は暗黙的に行われます。

```java
public record PositivePoint(int x, int y) {
    // コンパクトコンストラクタ
    public PositivePoint {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("座標は負の値にできません");
        }
        // ここで this.x = x; のような代入は不要（自動的に行われる）
    }
}
```

## 12.3 実践例：CSVファイルの読み込み

`Record`は、ファイルから読み込んだ構造化されたデータを保持するのに非常に適しています。

**data.csv**
```csv
Alice,25,Tokyo
Bob,30,Osaka
Charlie,35,Nagoya
```

このCSVの各行を表現する`PersonRecord`を定義します。

```java
// PersonRecord.java
public record PersonRecord(String name, int age, String city) {}
```

```java
// CsvReader.java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class CsvReader {
    public static void main(String[] args) {
        Path filePath = Path.of("data.csv");
        try {
            List<PersonRecord> persons = Files.lines(filePath) // ファイルを1行ずつのStreamに
                .map(line -> line.split(",")) // 各行をカンマで分割
                .filter(fields -> fields.length == 3) // 配列の長さが3であることを確認
                .map(fields -> new PersonRecord(
                    fields[0],                      // name
                    Integer.parseInt(fields[1]),    // age
                    fields[2]                       // city
                ))
                .collect(Collectors.toList()); // 結果をListに集約

            persons.forEach(System.out::println);

        } catch (IOException e) {
            System.err.println("ファイルの読み込みに失敗しました: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("年齢の形式が正しくありません: " + e.getMessage());
        }
    }
}
```
この例では、Stream APIと組み合わせることで、ファイル読み込みから`Record`への変換、リストへの格納までを非常に簡潔に記述できています。

## まとめ

本章では、データを簡潔かつ安全に扱うための`Record`について学びました。

-   **Record**は、不変なデータを保持するクラスを簡潔に定義するための機能です。
-   コンストラクタ、アクセサメソッド、`equals`, `hashCode`, `toString`が自動的に生成されます。
-   オブジェクトの**不変性**を保証し、プログラムの安全性を高めます。
-   データ転送オブジェクト（DTO）や、設定値の保持、ファイルから読み��んだデータの格納など、さまざまな場面で活用できます。

特に、Stream APIと組み合わせることで、データ処理を非常に簡潔かつ安全に記述できます。