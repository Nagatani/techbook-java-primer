# 第12章 レコード(Records)

## 章末演習

本章で学んだRecordクラスの概念を活用して、実践的な練習課題に取り組みましょう。

### 演習の目標
Recordクラスを使った不変データ構造の設計と実装技術を習得します。

## 基礎レベル課題（必須）

### 課題1: 基本的なRecord定義と活用

基本的なRecordクラスを定義し、その特性を理解してください。

**要求仕様：**
- シンプルなRecord定義（Point、Person等）
- 自動生成されるメソッド（equals、hashCode、toString）の確認
- Recordのアクセサメソッドの使用
- Recordの不変性の確認
- 従来のクラスとの比較

**実行例：**
```
=== 基本的なRecord定義と活用 ===
座標Record:
point1: Point[x=3, y=4]
point2: Point[x=3, y=4]

アクセサメソッド:
X座標: 3
Y座標: 4

equals比較:
point1.equals(point2): true
point1 == point2: false

hashCode比較:
point1.hashCode(): 128
point2.hashCode(): 128

人物Record:
person: Person[name=田中太郎, age=30, email=tanaka@example.com]

不変性確認:
// 以下はコンパイルエラー
// person.name = "佐藤花子";  // フィールドは final

Record vs 従来クラス比較:
従来クラスの行数: 35行（getter、setter、equals、hashCode、toString）
Recordの行数: 1行（record Person(String name, int age) {}）
```

### 課題2: Recordを使ったデータ転送オブジェクト（DTO）

RecordをDTOとして活用し、データ転送の実装を行ってください。

**要求仕様：**
- ユーザー情報のRecord DTO
- 注文情報のRecord DTO（ネストしたRecord含む）
- JSON風データ構造の表現
- Recordのバリデーション機能
- Recordの変換メソッド

**実行例：**
```
=== Recordを使ったデータ転送オブジェクト ===
ユーザーDTO:
user: UserDTO[id=1001, name=田中太郎, email=tanaka@example.com, active=true]

注文DTO（ネストしたRecord）:
order: OrderDTO[
  orderId=ORD-2024-001,
  user=UserDTO[id=1001, name=田中太郎, email=tanaka@example.com, active=true],
  items=[
    OrderItem[productName=ノートPC, quantity=1, price=150000],
    OrderItem[productName=マウス, quantity=2, price=3000]
  ],
  totalAmount=156000,
  orderDate=2024-07-04T10:30:00
]

データ変換:
CSV形式: "1001,田中太郎,tanaka@example.com,true"
Map形式: {id=1001, name=田中太郎, email=tanaka@example.com, active=true}

バリデーション:
有効なユーザー: UserDTO[id=1001, name=田中太郎, email=tanaka@example.com, active=true] ✓
無効なユーザー: UserDTO[id=0, name=, email=invalid-email, active=false] ✗
エラー: [IDは1以上である必要があります, 名前は必須です, メールアドレスの形式が正しくありません]
```

### 課題3: Record と設定管理

Recordを使ったアプリケーション設定管理システムを実装してください。

**要求仕様：**
- 階層的な設定構造（Record内にRecord）
- 設定の読み込みと保存
- デフォルト値の提供
- 設定の部分更新（新しいRecordインスタンス生成）
- 設定の検証機能

**実行例：**
```
=== Record と設定管理 ===
デフォルト設定:
AppConfig[
  appName=MyApplication,
  version=1.0.0,
  database=DatabaseConfig[
    url=jdbc:h2:mem:testdb,
    username=sa,
    password=,
    maxConnections=10,
    timeout=30
  ],
  features=[logging, caching],
  debug=true
]

設定更新（部分更新）:
データベース接続数だ20に変更:
新しい設定: AppConfig[
  appName=MyApplication,
  version=1.0.0,
  database=DatabaseConfig[
    url=jdbc:h2:mem:testdb,
    username=sa,
    password=,
    maxConnections=20,
    timeout=30
  ],
  features=[logging, caching],
  debug=true
]

設定ファイル形式での出力:
app.name=MyApplication
app.version=1.0.0
database.url=jdbc:h2:mem:testdb
database.username=sa
database.password=
database.maxConnections=20
database.timeout=30
features=logging,caching
debug=true

設定検証:
✓ アプリケーション名が設定されています
✓ データベースURLが有効です
✓ 接続数が適切な範囲内です
✗ パスワードが設定されていません（警告）
```

### 課題4: Record とパターンマッチング（Java 17+）

Recordとパターンマッチングを組み合わせた処理を実装してください。

**要求仕様：**
- 図形を表すRecord（Circle、Rectangle、Triangle）
- sealed interfaceとの組み合わせ
- パターンマッチング（switch式）の活用
- Recordのデコンストラクション
- 型安全な処理の実装

**実行例：**
```
=== Record とパターンマッチング ===
図形Record定義:
circle: Circle[radius=5.0]
rectangle: Rectangle[width=4.0, height=3.0]
triangle: Triangle[base=6.0, height=4.0]

面積計算（パターンマッチング）:
円の面積: 78.54
長方形の面積: 12.0
三角形の面積: 12.0

周囲計算（パターンマッチング）:
円の周囲: 31.42
長方形の周囲: 14.0
三角形の周囲: （計算複雑）

Record デコンストラクション:
circle(5.0) → 半径: 5.0
rectangle(4.0, 3.0) → 幅: 4.0, 高さ: 3.0
triangle(6.0, 4.0) → 底辺: 6.0, 高さ: 4.0

図形分類:
小さい図形（面積20未満）: [circle, rectangle, triangle]
中くらいの図形（面積20-100）: []
大きい図形（面積100以上）: []

型安全性確認:
switch 文で全ケースを網羅
コンパイル時に型安全性を保証
```

---

## 実装のヒント

### Record の基本原則

Recordの設計と実装において、4つの基本原則を理解することが重要です。**簡潔性**の原則では、定義は最小限にとどめながら機能は最大限に発揮させることを目指します。コンパクトで読みやすいコードを記述しながら、必要な機能はすべて提供される設計となっています。**不変性**の原則では、Recordのすべてのフィールドが自動的にfinalとして扱われ、一度作成されたオブジェクトの状態は変更できません。これにより、マルチスレッド環境での安全性や関数型プログラミングでの扱いやすさが保証されます。**透明性**の原則では、データ構造が一目で理解できるよう設計されており、Record定義を見るだけでそのオブジェクトが持つデータとその型を完全に把握できます。**自動生成**の原則では、equals、hashCode、toStringといった基本的なメソッドがコンパイラによって自動的に実装され、開発者が手動で記述する必要がありません。

### よくある落とし穴

Recordを使用する際に開発者が陥りやすい落とし穴を理解しておくことが重要です。まず、Recordは暗黙的にfinalクラスとして定義されるため、継承することができません。この制限は、Recordの「データを透明に保持する」という設計思想を守るためのものですが、継承による拡張を前提とした設計には使用できません。また、コンストラクタパラメータ以外のフィールドを追加することもできません。Record内で定義できるのは、コンストラクタで受け取るパラメータに対応するフィールドのみで、追加のインスタンス変数は宣言できません。アクセサメソッドの命名規則も従来と異なり、getプレフィックスは使用されません。従来のgetName()ではなく、name()のようにフィールド名そのものがメソッド名となります。コンパクトコンストラクタを使用する際は、パラメータ名を省略するため、パラメータの順序や意味を正確に把握しておく必要があります。

### 設計のベストプラクティス

Recordを効果的に活用するための設計パターンとベストプラクティスを理解することが重要です。RecordはDTO（Data Transfer Object）や値オブジェクト（Value Object）に最適で、システム間やレイヤー間でのデータ転送において、その簡潔性と型安全性を最大限に活用できます。ビジネスロジックは別のクラスに分離することが推奨され、Recordは純粋にデータの保持に専念させることで、関心の分離を明確にし、保守性を向上させることができます。コンパクトコンストラクタを活用してバリデーションを実装することで、不正なデータでのオブジェクト生成を防ぎ、データの整合性を保証できます。また、Java 17以降のsealed interfaceと組み合わせることで、型安全性をさらに向上させ、パターンマッチングでの網羅性チェックを活用した堅牢なコードを実装できます。これらのパターンを適切に組み合わせることで、現代的なJavaアプリケーションの設計において、Record の持つ潜在能力を最大限に引き出すことが可能になります。

---

## 実装環境

演習課題の詳細な実装テンプレート、テストコード、解答例は以下のディレクトリを参照してください：

```
exercises/chapter12/
├── basic/          # 基礎レベル課題
│   ├── README.md   # 詳細な課題説明
│   ├── BasicRecord.java
│   ├── UserDTO.java
│   ├── AppConfig.java
│   └── Shape.java
├── advanced/       # 応用レベル課題
├── challenge/      # 発展レベル課題
└── solutions/      # 解答例（実装完了後に参照）
```

---

## 完了確認チェックリスト

### 基礎レベル
- [ ] 基本的なRecordの定義と特性が理解できている
- [ ] RecordをDTOとして適切に活用できている
- [ ] 複雑なRecord構造と設定管理ができている
- [ ] パターンマッチングと組み合わせて使えている

### 技術要素
- [ ] Recordの利点と制限を理解している
- [ ] 従来のクラスとの使い分けができている
- [ ] 不変性を保った設計ができている
- [ ] コンパクトコンストラクタでバリデーションができている

### 応用レベル
- [ ] シリアライゼーションやJSON連携ができている
- [ ] パフォーマンスを考慮した設計ができている
- [ ] フレームワークやライブラリとの統合ができている
- [ ] レガシーコードからRecordへのマイグレーションができている

## 本章の学習目標

### 前提知識

本章を学習するためには、いくつかの重要な前提知識が必要です。まず必須の前提として、第7章で学んだ不変性とfinalの概念を十分に理解していることが求められます。Recordは本質的に不変オブジェクトを生成するため、不変性の利点と実装方法についての理解が不可欠です。また、従来のクラス設計と実装の経験も重要で、コンストラクタ、getter/setterメソッド、そしてequals、hashCode、toStringメソッドの基本的な理解が必要です。これらの知識があることで、Recordが自動生成するメソッドの意味と価値を深く理解できるでしょう。

さらに、実践的な開発経験として、データクラスの実装経験とその課題を理解していることが推奨されます。従来のJavaでデータを保持するためだけのクラスを作成した際に発生するボイラープレートコード（定型的なコード）の問題を経験していると、Recordの簡潔さと効率性をより深く実感できます。オブジェクト指向設計における「データ」と「振る舞い」の分離についての理解も重要で、Recordがデータ表現に特化した設計であることの意義を理解する基礎となります。

### 学習目標

本章では、Java 14で導入され、Java 16で正式版となったRecordクラスの包括的な知識と実践技術を習得します。知識理解の面では、まずRecordの設計思想と従来のクラスとの根本的な違いを深く理解します。Recordは「データの透明な保持」という明確な目的を持つ言語機能で、従来のデータクラスで必要だった大量のボイラープレートコードを劇的に削減します。不変データ構造の利点と適用場面を学び、マルチスレッド環境での安全性や関数型プログラミングとの親和性を理解します。また、Recordの制約事項と限界を把握することで、適切な使い分けができるようになります。データ指向プログラミングの基本概念も学び、オブジェクト指向設計における新しいアプローチを理解します。

技能習得の面では、Recordを使った簡潔なデータクラスの定義方法を習得します。コンパクトコンストラクタやカスタムメソッドの実装により、バリデーションや特別な処理を組み込む技術を学びます。既存のクラスからRecordへのリファクタリング手法も重要なスキルで、レガシーコードの改善に活用できます。RecordとGenericsの組み合わせや、ネストしたRecordの活用方法も習得し、複雑なデータ構造を効率的に表現できるようになります。

設計能力の観点からは、データ中心設計における適切なRecord活用方法を学びます。不変性を活かした安全なAPI設計の手法を習得し、スレッドセーフで保守性の高いシステムを構築できるようになります。Recordとクラスの使い分け判断も重要で、それぞれの特性を理解した上で最適な選択ができるようになります。

最終的な到達レベルとしては、データ表現に適したRecordを効率的に定義できるようになります。複雑なデータ構造でもRecordを適切に活用し、不変性の利点を活かしたスレッドセーフな設計ができるようになります。そして、既存のコードベースにRecordを適切に導入し、コードの品質と保守性を向上させることが、本章の最終目標です。

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
-   データ転送オブジェクト（DTO）や、設定値の保持、ファイルから読み込んだデータの格納など、さまざまな場面で活用できます。

特に、Stream APIと組み合わせることで、データ処理を非常に簡潔かつ安全に記述できます。