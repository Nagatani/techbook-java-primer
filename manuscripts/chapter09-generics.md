# 第9章 ジェネリクス

## 章末演習

本章で学んだジェネリクスの概念を活用して、実践的な練習課題に取り組みましょう。

### 演習の目標
ジェネリクスを使った型安全なプログラムの実装

### 基礎レベル課題

### 課題1: ジェネリックペアクラス

**学習目標：** ジェネリッククラスの基本、複数型パラメータの使用

**問題説明：**
2つの値を保持するジェネリックなペアクラスを作成し、型安全性を実装してください。

**技術的背景：型安全性とジェネリクスの必要性**

Java 5以前は、汎用的なコンテナクラスはObject型を使用していました。これが引き起こした問題：

```java
// ジェネリクス以前の危険なコード
List list = new ArrayList();
list.add("Hello");
list.add(42);  // 文字列と数値が混在
String str = (String)list.get(1);  // ClassCastException!
```

**ペアクラスの実用例：**
- **座標システム**：(x, y)座標、(緯度, 経度)の位置情報
- **キー・バリューペア**：Map.Entryの実装、設定項目と値のペア
- **関数の戻り値**：複数の値を返す必要がある場合（エラーコードとメッセージなど）
- **統計処理**：（平均値, 標準偏差）、（最小値, 最大値）などの結果

**型安全性がもたらす利点：**
- **コンパイル時エラー検出**：実行時エラーを未然に防ぐ
- **明示的な型キャスト不要**：コードの可読性向上
- **IDEの支援**：型情報に基づく自動補完とリファクタリング

この演習では、ジェネリクスの基本概念と、実務で頻繁に使用されるペアパターンの実装を学びます。

**要求仕様：**
- ジェネリッククラスPair<T, U>
- 2つの異なる型の値を保持
- getter、setter、swapメソッド
- equals、hashCode、toStringの実装
- 型安全性の確保

**実行例：**
```
=== ジェネリックペアクラステスト ===
文字列と数値のペア:
Pair: ("Hello", 42)

値の入れ替え:
スワップ前: ("Hello", 42)
スワップ後: (42, "Hello")

座標ペア:
座標: (3.14, 2.71)
X座標: 3.14
Y座標: 2.71

同じ型のペア:
名前ペア: ("田中", "太郎")
結合名: 田中太郎

型安全性テスト:
// 以下はコンパイルエラーになる
// pair.setFirst(123);  // String型にintは代入不可
```

**実装ヒント：**
```java
public class Pair<T, U> {
    private T first;
    private U second;
    
    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }
    
    // getter、setter、swapメソッドを実装
    // equals、hashCode、toStringも実装
}
```

### 課題2: ジェネリックコンテナクラス

**学習目標：** ジェネリックメソッド、境界付き型パラメータ、Iterableインターフェイス

**問題説明：**
型安全なコンテナクラスを作成し、ジェネリックメソッドを実装してください。

**技術的背景：汎用コンテナとジェネリックメソッドの威力**

独自のコンテナクラスが必要になる場面：
- **特殊なデータ構造**：循環バッファ、優先度付きキュー、遅延評価リスト
- **ドメイン特化コンテナ**：商品カタログ、ユーザーセッション、イベントストリーム
- **性能最適化**：特定用途に最適化されたメモリレイアウト

**ジェネリックメソッドの実用例：**
```java
// 型安全な変換処理
public <R> Container<R> map(Function<T, R> mapper) {
    // TからRへの変換を型安全に実行
}

// 境界付き型パラメータ
public static <T extends Comparable<T>> T max(Container<T> container) {
    // Comparable実装型のみ受け付ける
}
```

**境界付き型パラメータの重要性：**
- **型制約による安全性**：特定の操作が可能な型のみを受け付ける
- **メソッドの利用可能化**：T extends Numberなら、doubleValue()等が使える
- **設計意図の明確化**：このメソッドが何を期待しているかが明確

実際のフレームワークでの使用例：
- **Spring Framework**：ジェネリックなDIコンテナ
- **Apache Commons**：型安全なコレクションユーティリティ
- **Google Guava**：高度なジェネリックコレクション

この演習では、フレームワーク開発で必要となる高度なジェネリクス技術を学びます。

**要求仕様：**
- ジェネリッククラスContainer<T>
- 要素の追加、取得、削除メソッド
- ジェネリックメソッド（検索、変換、フィルタリング）
- Iterableインターフェイスの実装
- 型制約のある操作メソッド

**実行例：**
```
=== ジェネリックコンテナクラステスト ===
文字列コンテナ:
追加: Hello, World, Java, Programming
内容: [Hello, World, Java, Programming]

検索テスト:
"Java"が含まれているか: true
"Python"が含まれているか: false

数値コンテナ:
追加: 1, 2, 3, 4, 5
内容: [1, 2, 3, 4, 5]

変換テスト（数値を2倍）:
元の値: [1, 2, 3, 4, 5]
変換後: [2, 4, 6, 8, 10]

フィルタリングテスト（偶数のみ）:
フィルタ前: [2, 4, 6, 8, 10]
フィルタ後: [2, 4, 6, 8, 10]

型制約テスト（Comparable要素のソート）:
ソート前: [banana, apple, orange]
ソート後: [apple, banana, orange]
```

**実装ヒント：**
```java
public class Container<T> implements Iterable<T> {
    private List<T> items = new ArrayList<>();
    
    public void add(T item) {
        items.add(item);
    }
    
    // ジェネリックメソッドの例
    public <R> Container<R> map(Function<T, R> mapper) {
        Container<R> result = new Container<>();
        for (T item : items) {
            result.add(mapper.apply(item));
        }
        return result;
    }
    
    // 境界付き型パラメータの例
    public static <T extends Comparable<T>> void sort(Container<T> container) {
        // Comparableを実装した型のみ受け付ける
    }
}
```

### 課題3: ワイルドカード活用システム

**学習目標：** 境界付きワイルドカード、PECS原則、共変・反変の理解

**問題説明：**
ワイルドカードを活用した数値処理システムを作成し、共変・反変を実装してください。

**技術的背景：ワイルドカードとPECS原則の実践的重要性**

ワイルドカードは、ジェネリクスの柔軟性を高める重要な機能です。しかし、誤った使用は深刻な問題を引き起こします：

**実際の問題事例：**
```java
// 危険なコード：型安全性の破壊
List<String> strings = new ArrayList<>();
List<Object> objects = strings;  // コンパイルエラー！
objects.add(42);  // もし許可されたら、String型リストに整数が混入
```

**PECS原則（Producer Extends, Consumer Super）：**
- **Producer（データを提供）**：`? extends T` - 読み取り専用
- **Consumer（データを受け取る）**：`? super T` - 書き込み可能

**実用例：**
- **Collections.copy()**：`copy(List<? super T> dest, List<? extends T> src)`
- **Stream API**：`Stream<? extends T>`で柔軟な処理
- **Comparator**：`Comparator<? super T>`で親クラスの比較も可能

**ワイルドカードが必要な場面：**
- **統計処理**：Integer、Double、BigDecimalなど様々な数値型を統一的に処理
- **データ変換**：異なる型のコレクション間でのデータ移動
- **フレームワーク開発**：汎用的なAPIの設計

この演習では、型安全性を保ちながら柔軟なAPIを設計する技術を学びます。

**要求仕様：**
- 境界付きワイルドカード（? extends、? super）の活用
- 数値リストの統計処理（上限境界ワイルドカード）
- 要素追加処理（下限境界ワイルドカード）
- PECS原則（Producer Extends, Consumer Super）の実践

**実行例：**
```
=== ワイルドカード活用システムテスト ===
整数リスト: [1, 2, 3, 4, 5]
統計情報:
合計: 15
平均: 3.0
最大値: 5
最小値: 1

Double リスト: [1.5, 2.5, 3.5]
統計情報:
合計: 7.5
平均: 2.5
最大値: 3.5
最小値: 1.5

Number リストへの追加テスト:
追加前: []
Integer追加後: [10, 20, 30]
Double追加後: [10, 20, 30, 3.14, 2.71]

型の共変性テスト:
List<Integer> を List<? extends Number> として処理可能
List<Double> を List<? extends Number> として処理可能

型の反変性テスト:
List<Number> を List<? super Integer> として処理可能
List<Object> を List<? super Integer> として処理可能
```

**実装ヒント：**
```java
public class NumberProcessor {
    // Producer: extendsを使用（読み取り専用）
    public static double sum(List<? extends Number> numbers) {
        double total = 0.0;
        for (Number num : numbers) {
            total += num.doubleValue();
        }
        return total;
    }
    
    // Consumer: superを使用（書き込み可能）
    public static void addNumbers(List<? super Integer> list) {
        list.add(10);
        list.add(20);
        list.add(30);
    }
}
```

### 課題4: ジェネリック型変換ユーティリティ

**学習目標：** 高度なジェネリックメソッド、Function活用、Optional連携

**問題説明：**
型変換を行うジェネリックユーティリティクラスを作成し、高度なジェネリクス機能を実装してください。

**要求仕様：**
- ジェネリックメソッドによる型変換
- Functionインターフェイスを活用した変換関数
- 複数段階の型変換チェイン
- Optionalを活用した安全な型変換

**実行例：**
```
=== ジェネリック型変換ユーティリティテスト ===
基本的な型変換:
文字列 "123" → 整数: 123
整数 42 → 文字列: "42"
Double 3.14 → 文字列: "3.14"

リスト変換:
文字列リスト: ["1", "2", "3", "4", "5"]
整数リスト: [1, 2, 3, 4, 5]
二乗リスト: [1, 4, 9, 16, 25]

チェーン変換:
"123" → 整数 → 二乗 → 文字列: "15129"

安全な変換（Optional使用）:
"123" → 整数: Optional[123]
"abc" → 整数: Optional.empty

フィルタ付き変換:
文字列リスト: ["1", "abc", "3", "def", "5"]
数値のみ変換: [1, 3, 5]

カスタム変換:
Person{name='田中', age=25} → "田中 (25歳)"
Person{name='佐藤', age=30} → "佐藤 (30歳)"
```

---

## 実装のヒント

### ジェネリクスの基本原則
1. **型パラメータの命名**: T（Type）、E（Element）、K（Key）、V（Value）
2. **ダイヤモンド演算子**: new ArrayList<>() で型推論
3. **境界付き型**: extendsで上限、superで下限
4. **PECS原則**: Producer Extends, Consumer Super

### よくある落とし穴
- プリミティブ型は使えない（Integer等のラッパクラスを使用）
- 型消去により実行時の型情報は失われる
- 配列とジェネリクスの相性問題
- ワイルドカードでの要素追加制限

### 設計のベストプラクティス
- 可能な限り具体的な型を使用
- ワイルドカードは必要な場合のみ使用
- 境界付き型で型安全性を高める
- 型推論を活用してコードを簡潔に

---

## 実装環境

演習課題の詳細な実装テンプレート、テストコード、解答例は以下のディレクトリを参照してください：

```
exercises/chapter09/
├── basic/          # 基礎レベル課題
│   ├── README.md   # 詳細な課題説明
│   ├── Pair.java
│   ├── Container.java
│   ├── NumberProcessor.java
│   └── TypeConverter.java
├── advanced/       # 応用レベル課題（Map関連の課題が誤って配置）
├── challenge/      # 発展レベル課題（Map関連の課題が誤って配置）
└── solutions/      # 解答例（実装完了後に参照）
```

**注意**: advanced/とchallenge/ディレクトリの内容は現在Map/HashMapに関する課題になっているため、ジェネリクスの応用・発展課題については基礎レベルの内容を深めることで学習してください。

---

## 完了確認チェックリスト

### 基礎レベル
- [ ] ジェネリックペアクラスで型安全性が確保されている
- [ ] ジェネリックコンテナで型制約が活用されている
- [ ] ワイルドカードでPECS原則が実践されている
- [ ] 高度なジェネリックメソッドが実装されている

### 技術要素
- [ ] 型パラメータと境界の使い分けができている
- [ ] ジェネリクスの制限事項を理解している
- [ ] 型推論を活用してコードが簡潔になっている
- [ ] 型消去の影響を理解している

### 発展的理解
- [ ] 再帰的ジェネリクス（T extends Comparable<T>）が理解できている
- [ ] ジェネリックメソッドとクラスの使い分けができている
- [ ] 型安全なAPIが設計できている
- [ ] 既存のジェネリックAPIを効果的に活用できている

## 本章の学習目標

### 前提知識
**必須前提**：
- 第8章のコレクションフレームワークの理解と実践経験
- 型システムの基本的な理解
- オブジェクト指向の継承とポリモーフィズムの理解

**実践経験前提**：
- コレクションを使った実用的なプログラム開発経験
- 型安全性の問題（ClassCastException等）の経験

### 学習目標
**知識理解目標**：
- ジェネリクスの設計目的と利点の深い理解
- 型パラメータと型引数の概念
- 境界ワイルドカード（? extends、? super）の理解
- 型消去（Type Erasure）のしくみと制限事項

**技能習得目標**：
- ジェネリッククラスとインターフェイスの設計・実装
- ジェネリックメソッドの効果的な使用
- 境界ワイルドカードを使った柔軟な型設計
- 既存のジェネリックAPIの効果的な活用

**設計能力目標**：
- 型安全で再利用可能なライブラリの設計
- 複雑な型関係を持つシステムの設計
- ジェネリクスを活用したAPIの設計

**到達レベルの指標**：
- 型安全で再利用可能なジェネリッククラスが独力で設計・実装できる
- 複雑なジェネリック型を含むAPIが理解・活用できる
- 型関連のコンパイルエラーを適切に解決できる
- ジェネリクスを使った柔軟で保守性の高いライブラリが作成できる

---

## 9.1 なぜジェネリクスが必要なのか？

### 型安全性の歴史的背景

プログラミング言語における型システムの目的は、プログラムの正しさをコンパイル時に検証し、実行時エラーを減らすことです。しかし、汎用的なデータ構造（コレクションなど）を実装する際には、「どんな型でも扱える」柔軟性と「型の安全性」を両立させる必要がありました。

Java 5より前の時代、コレクションは「あらゆるオブジェクト」を格納できる`Object`型の入れ物でした。これは一見便利に思えますが、大きな問題を抱えていました。

```java
// ジェネリクスがなかった時代のコード（現在は非推奨）
import java.util.ArrayList;
import java.util.List;

public class WithoutGenericsExample {
    public static void main(String[] args) {
        List list = new ArrayList();
        list.add("Hello");
        list.add(123); // 文字列も数値も、何でも入ってしまう！

        // 取り出すときに、本来の型を覚えておいて、キャストする必要がある
        String message = (String) list.get(0); // OK

        // もし間違った型で取り出そうとすると...
        // コンパイルは通ってしまうが、実行時に ClassCastException が発生！
        // String wrong = (String) list.get(1); 
    }
}
```
このように、コンパイラは型の誤りをチェックできず、バグが実行時まで発見されない危険な状態でした。

この問題を解決するためにJava 5で導入されたのが**ジェネリクス（Generics）** です。

### ジェネリクスの基本概念

ジェネリクスは、クラスやインターフェイスが「どの型のデータ」を扱うのかを、**クラスを定義または利用する時点**で指定できるようにするしくみです。これは、「パラメータ化された型（Parameterized Types）」とも呼ばれ、以下のような利点を提供します：

1. **コンパイル時の型検査**：型の不一致をコンパイル時に検出し、実行時エラーを防ぐ
2. **キャストの除去**：明示的な型キャストが不要になり、コードがより安全で読みやすくなる
3. **コードの再利用性**：同じロジックを異なる型に対して適用できる

### 型消去（Type Erasure）のしくみ

Javaのジェネリクスの重要な特徴の一つが「型消去」です。これは、コンパイル時にジェネリック型情報が検査された後、実行時にはその情報が削除されるしくみです。これにより：

- 既存のコードとの後方互換性が保たれる
- 実行時のパフォーマンスオーバーヘッドがない
- ただし、実行時に型情報を取得できないという制限もある

`List<String>`のように、クラス名の後に山括弧`< >`で型を指定します。これを**型パラメータ**と呼びます。

```java
import java.util.ArrayList;
import java.util.List;

public class WithGenericsExample {
    public static void main(String[] args) {
        // String型の要素だけを格納できるListを宣言
        List<String> stringList = new ArrayList<>();

        stringList.add("Java");
        // stringList.add(123); // コンパイルエラー！ String型以外は追加できない

        // 取り出すときも、キャストは不要
        String element = stringList.get(0); // 安全！
        System.out.println(element);
    }
}
```

ジェネリクスを使うことで、コンパイラが型の整合性をチェックしてくれるため、意図しない型のデータが混入するのを防ぎ、プログラムの安全性を大幅に向上させることができます。

## 9.2 ジェネリッククラスの作成

ジェネリクスはコレクションだけでなく、自作のクラスにも適用できます。これにより、特定の型に依存しない、再利用性の高いクラスを作成できます。

### 型パラメータの命名慣習

ジェネリクスで使用される型パラメータには、慣習的な命名規則があります：

- `T` - Type（一般的な型）
- `E` - Element（コレクションの要素）
- `K` - Key（マップのキー）
- `V` - Value（マップの値）
- `N` - Number（数値）
- `S`, `U`, `V` - 2番目、3番目、4番目の型

これらの慣習に従うことで、コードの可読性が向上し、他の開発者にとっても理解しやすいコードとなります。

### 基本的なジェネリッククラス

クラス名の後に`<T>`のような型パラメータを宣言します。`T`は"Type"の頭文字で、慣習的に使われるプレースホルダです。

```java
// Tという型パラメータを持つジェネリッククラス
public class Box<T> {
    private T content;

    public void setContent(T content) {
        this.content = content;
    }

    public T getContent() {
        return this.content;
    }
}

public class BoxExample {
    public static void main(String[] args) {
        // Stringを扱うBox
        Box<String> stringBox = new Box<>();
        stringBox.setContent("Hello Generics");
        String str = stringBox.getContent();
        System.out.println(str);

        // Integerを扱うBox
        Box<Integer> integerBox = new Box<>();
        integerBox.setContent(100);
        int num = integerBox.getContent();
        System.out.println(num);
    }
}
```

### ダイヤモンド演算子

Java 7からは、右辺の型パラメータを省略できる**ダイヤモンド演算子** (`<>`)が導入され、より簡潔に記述できるようになりました。コンパイラが左辺の宣言から型を推論してくれます。

```java
// Java 7以降の推奨される書き方
Map<String, List<Integer>> complexMap = new HashMap<>();
```

## 9.3 ジェネリックメソッド

クラス全体ではなく、特定のメソッドだけをジェネリックにすることも可能です。メソッドの戻り値の型の前に型パラメータを宣言します。

```java
public class GenericMethodExample {
    // Tという型パラメータを持つジェネリックメソッド
    public static <T> void printArray(T[] inputArray) {
        for (T element : inputArray) {
            System.out.printf("%s ", element);
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Integer[] intArray = { 1, 2, 3 };
        String[] stringArray = { "A", "B", "C" };

        System.out.print("Integer配列: ");
        printArray(intArray); // TはIntegerと推論される

        System.out.print("String配列: ");
        printArray(stringArray); // TはStringと推論される
    }
}
```

## 9.4 型制約（境界のある型パラメータ）

ジェネリクスで使える型を、特定のクラスのサブクラスや、特定のインターフェイスを実装したクラスに限定したい場合があります。これを**境界のある型パラメータ**とよい、`extends`キーワードを使います。

`<T extends Number>`と書くと、「Tは`Number`クラスまたはそのサブクラス」という制約を課すことができます。

```java
// Numberまたはそのサブクラスしか扱えないNumericBox
public class NumericBox<T extends Number> {
    private T number;

    public void setNumber(T number) {
        this.number = number;
    }

    // TはNumberであることが保証されているので、Numberクラスのメソッドを呼べる
    public double getDoubleValue() {
        return this.number.doubleValue();
    }
}

public class BoundedTypeExample {
    public static void main(String[] args) {
        NumericBox<Integer> intBox = new NumericBox<>();
        intBox.setNumber(10);
        System.out.println(intBox.getDoubleValue()); // 10.0

        NumericBox<Double> doubleBox = new NumericBox<>();
        doubleBox.setNumber(3.14);
        System.out.println(doubleBox.getDoubleValue()); // 3.14

        // NumericBox<String> stringBox = new NumericBox<>(); // コンパイルエラー！
    }
}
```

## 9.5 ワイルドカード

ジェネリクスを使ったメソッドの引数などで、より柔軟にさまざまな型を受け入れたい場合に**ワイルドカード (`?`)** を使います。

-   `List<?>`: **未知の型**のリスト。どんな型のリストでも受け取れるが、安全のため`null`以外の要素を追加することはできない。
-   `List<? extends Type>`: **上限境界ワイルドカード**。`Type`またはそのサブクラスのリストを受け取れる。主にデータの**読み取り（Producer）**に使う。
-   `List<? super Type>`: **下限境界ワイルドカード**。`Type`またはそのスーパークラスのリストを受け取れる。主にデータの**書き込み（Consumer）**に使う。

```java
import java.util.List;
import java.util.ArrayList;

public class WildcardExample {
    // 上限境界ワイルドカード：Numberとそのサブクラスのリストを受け取り、合計値を計算する
    public static double sum(List<? extends Number> numberList) {
        double total = 0.0;
        for (Number num : numberList) {
            total += num.doubleValue();
        }
        return total;
    }

    public static void main(String[] args) {
        List<Integer> intList = List.of(1, 2, 3);
        List<Double> doubleList = List.of(1.1, 2.2, 3.3);

        System.out.println("Integerの合計: " + sum(intList));
        System.out.println("Doubleの合計: " + sum(doubleList));
    }
}
```

## まとめ

本章では、Javaの型安全性を支える重要な機能であるジェネリクスについて学びました。

-   **ジェネリクス**は、クラスやメソッドが扱うデータ型をコンパイル時に指定し、型の安全性を保証するしくみです。
-   これにより、実行時エラーである`ClassCastException`を未然に防ぐことができます。
-   自作のクラスやメソッドもジェネリックにすることで、**再利用性**を高めることができます。
-   **型制約**や**ワイルドカード**を使うことで、より柔軟で安全なメソッド設計が可能になります。

コレクションを扱う際には、必ずジェネリクスを使い、その恩恵を最大限に活用することが、現代的なJavaプログラミングの基本です。