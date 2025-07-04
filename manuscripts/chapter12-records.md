# 第12章 Recordによる不変なデータクラス

## 🎯総合演習プロジェクトへのステップ

本章で学ぶ`Record`は、**総合演習プロジェクト「ToDoリストアプリケーション」** において、タスクのデータを表現する`Task`クラスを、より安全で簡潔に定義するために活用できます。

- **`Task`クラスの簡素化**: `name`, `dueDate`, `isCompleted`といったデータを保持するだけの`Task`クラスを、従来の冗長な書き方ではなく、`Record`を使って1行で定義できます。
  ```java
  // 従来のクラス定義
  public final class Task { // finalにする
      private final String name;
      // ... getter, equals, hashCode, toString ...
  }

  // Recordによる定義
  public record Task(String name, LocalDate dueDate, boolean isCompleted) {}
  ```
- **不変性の保証**: `Record`で定義したクラスは、フィールドが自動的に`final`となり、一度作成すると変更できない「不変（イミュータブル）」なオブジェクトになります。これにより、意図しないデータの上書きを防ぎ、プログラムの安全性を高めます。

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

    public String name() { return this.name; } // アクセサメソッド（getXXX���はない）
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