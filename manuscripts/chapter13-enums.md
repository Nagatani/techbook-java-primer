# 第13章 列挙型(Enums)

## 章末演習

本章で学んだ列挙型（Enum）の概念を活用して、実践的な練習課題に取り組みましょう。

### 演習の目標
- 列挙型の基本概念と利点の理解
- 列挙型の定数定義と使用方法
- 列挙型のメソッドとフィールドの実装
- switch文での列挙型活用
- 列挙型を使った状態管理の実践
- EnumSetとEnumMapの効果的な活用

### 📁 課題の場所
演習課題は `exercises/chapter13/` ディレクトリに用意されています：

```
exercises/chapter13/
├── basic/          # 基本課題（必須）
│   ├── README.md   # 課題の詳細説明
│   ├── Status.java # 課題1: 基本的な列挙型定義
│   ├── StatusTest.java
│   ├── Planet.java # 課題2: メソッド付き列挙型
│   ├── PlanetTest.java
│   ├── OperationEnum.java # 課題3: 抽象メソッド付き列挙型
│   ├── OperationEnumTest.java
│   ├── StateMachine.java # 課題4: 状態管理システム
│   └── StateMachineTest.java
├── advanced/       # 発展課題（推奨）
└── challenge/      # 挑戦課題（上級者向け）
```

### 推奨する学習の進め方

1. **基本課題**から順番に取り組む
2. 各課題のREADME.mdで詳細を確認
3. ToDoコメントを参考に実装
4. 列挙型と定数の違いを理解する
5. 列挙型による型安全性の利点を習得する

基本課題が完了したら、`advanced/`の発展課題でより複雑な状態管理に挑戦してみましょう！

## 本章の学習目標

### 前提知識
**必須前提**：
- 第7章のfinalの理解とクラス設計経験
- 定数の概念と`static final`の基本的な使用
- オブジェクト指向の基本概念

**設計経験前提**：
- 定数管理の課題と型安全性の問題意識
- switch文とif文の基本的な使用経験

### 学習目標
**知識理解目標**：
- enum（列挙型）の設計思想と従来の定数との違い
- enumのインスタンス性とsingleton性の理解
- enum特有のメソッド（values、valueOf等）の理解
- enumとswitch文の関係性

**技能習得目標**：
- 基本的なenumの定義と使用
- フィールドとメソッドを持つ高度なenum設計
- enumを使った型安全なAPI設計
- enumとswitch文を使った効率的な分岐処理

**設計能力目標**：
- 適切な抽象化レベルでのenum活用
- 拡張可能なenum設計パターンの理解
- ドメインモデルにおけるenum活用

**到達レベルの指標**：
- 型安全性を重視したenum設計が実装できる
- 複雑なビジネスロジックをenumで表現できる
- enumを使った保守性の高いコードが作成できる
- 既存の定数をenumへ適切にリファクタリングできる

---

## 13.1 enumとは？

`enum`（列挙型）は、**決まったいくつかの値だけを取りうる型**を定義するための、特殊なクラスです。たとえば、「曜日（月火水木金土日）」や「信号の色（赤青黄）」のように、値の範囲が限定されているものを扱うのに最適です。

### なぜenumが必要なのか？

`enum`がない場合、このような定数は`public static final int`フィールドとして定義することが一般的でした。しかし、この方法には以下のような問題点があります。
-   **型安全でない**: `int`型ですので、`setSignal(100)`のような無効な値も代入できてしまう。
-   **意味が不明確**: `int`という型からは、それが「信号の色」を表すという情報がわからない。
-   **デバッグしにくい**: 変数の値を表示しても`0`としか出力されず、それが「赤」を意味するのかわからない。

`enum`は、これらの問題をすべて解決します。`enum`で定義された各要素（列挙子）は、その`enum`型のユニークなインスタンスとして扱われ、型安全性を保証します。

### 基本的な構文と使い方

```java
public enum DayOfWeek {
    SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY
}
```
これだけで、`DayOfWeek`という新しい型が作られ、その型が取りうる値は定義された7つの曜日に限定されます。

```java
public class EnumExample {
    public static void main(String[] args) {
        DayOfWeek today = DayOfWeek.WEDNESDAY;

        System.out.println("今日は " + today + " です。"); // toString()がオーバーライドされており、"WEDNESDAY"と表示される

        // 型安全なので、不正な値はコンパイルエラーになる
        // DayOfWeek tomorrow = "Thursday"; // エラー
    }
}
```

## 13.2 enumの便利な機能

Javaの`enum`は、暗黙的に`java.lang.Enum`クラスを継承しており、便利なメソッドが標準で提供されています。

-   `values()`: すべての列挙子を定義順に格納した配列を返します。
-   `valueOf(String name)`: 指定された名前の列挙子を返します。
-   `name()`: 列挙子の名前（定義した通りの文字列）を返します。
-   `ordinal()`: 列挙子の定義順序（ゼロから始まる）を返します。

```java
// DayOfWeek.values() を使って全曜日をループ処理
for (DayOfWeek day : DayOfWeek.values()) {
    System.out.println(day.name() + " は " + day.ordinal() + "番目");
}

// 文字列からenumインスタンスを生成
DayOfWeek friday = DayOfWeek.valueOf("FRIDAY");
System.out.println(friday); // FRIDAY
```

### `switch`文との連携

`enum`は`switch`文と非常に相性が良く、安全で読みやすいコードを書くことができます。

```java
public class TrafficLight {
    public static void checkSignal(DayOfWeek day) {
        // Java 14以降のswitch式を使うと、より簡潔に書ける
        String typeOfDay = switch (day) {
            case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> "平日";
            case SATURDAY, SUNDAY -> "週末";
        };
        System.out.println(day + " は " + typeOfDay + " です。");
    }
}
```
`case`句では、`DayOfWeek.MONDAY`のように書く必要はなく、`MONDAY`とだけ書ける点に注意してください。

## 13.3 フィールドとメソッドを持つenum

`enum`は単なる定数の集まりではなく、クラスのようにフィールドやコンストラクタ、メソッドを持たせることができます。これにより、定数に関連するデータや振る舞いをカプセル化できます。

```java
public enum Planet {
    // 各列挙子を定義する際に、コンストラクタの引数を渡す
    MERCURY (3.303e+23, 2.4397e6),
    VENUS   (4.869e+24, 6.0518e6),
    EARTH   (5.976e+24, 6.37814e6);
    // ... 他の惑星

    private final double mass;   // 質量 (kg)
    private final double radius; // 半径 (m)

    // コンストラクタは暗黙的にprivate
    Planet(double mass, double radius) {
        this.mass = mass;
        this.radius = radius;
    }

    // 通常のメソッド
    public double surfaceGravity() {
        final double G = 6.67300E-11;
        return G * mass / (radius * radius);
    }
}

public class PlanetTest {
    public static void main(String[] args) {
        System.out.println("地球の表面重力: " + Planet.EARTH.surfaceGravity());
    }
}
```

## 13.4 さらに高度な使い方

### 列挙子ごとのメソッド実装

各列挙子でメソッドをオーバーライドすることで、列挙子ごとに異なる振る舞いをさせることができます。これは**ストラテジーパターン**を簡潔に実現する方法の1つです。

```java
public enum Operation {
    PLUS("+") {
        public double apply(double x, double y) { return x + y; }
    },
    MINUS("-") {
        public double apply(double x, double y) { return x - y; }
    };

    private final String symbol;
    Operation(String symbol) { this.symbol = symbol; }
    public abstract double apply(double x, double y); // 抽象メソッド
}
```
`op.apply(x, y)`という同じ呼び出し方で、`PLUS`なら足し算、`MINUS`なら引き算が実行されます。

### インターフェイスの実装

`enum`はクラスを継承できませんが、インターフェイスを実装することは可能です。

```java
interface Loggable {
    String getLogMessage();
}

public enum Status implements Loggable {
    SUCCESS {
        public String getLogMessage() { return "処理が成功しました。"; }
    },
    ERROR {
        public String getLogMessage() { return "エラーが発生しました。"; }
    };
}
```

## まとめ

本章では、型安全な定数を扱うための`enum`について学びました。

-   **`enum`**は、取りうる値が限定された、独自の型を定義する機能です。
-   `int`定数などと比べて、**型安全**で、コードが**読みやすく**なります。
-   `switch`文と組み合わせることで、網羅的で安全な条件分岐を記述できます。
-   クラスのように、**フィールドやメソッドを持たせる**ことができ、より高機能な定数を定義できます。
-   列挙子ごとにメソッドを実装したり、インターフェイスを実装したりすることで、さらに柔軟な設計が可能です。

プログラムの中で「種類」「状態」「ステータス」などを表す値が出てきたら、まず`enum`が使えないか検討することは、高品質なコードを書くための良い習慣です。