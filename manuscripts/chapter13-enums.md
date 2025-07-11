# 第13章 列挙型(Enums)

## 章末演習

本章で学んだ列挙型(Enums)を実践的な課題で確認しましょう。

### 演習課題へのアクセス

本書の演習課題は、以下のGitHubリポジトリで提供されています：

**リポジトリ**: `https://github.com/[your-repo]/java-primer-exercises`

### 第13章の課題構成

```
exercises/chapter13/
├── basic/              # 基礎課題（必須）
│   ├── README.md       # 詳細な課題説明
│   └── DayOfWeek.java
├── advanced/           # 発展課題（推奨）
├── challenge/          # チャレンジ課題（任意）
└── solutions/          # 解答例（実装後に参照）
```

### 学習の目標

本章の演習を通じて以下のスキルを習得します：
- Enumによる型安全な定数の定義と活用
- Enumへのメソッドとフィールドの追加
- 高度なEnumパターンの実装

### 課題の概要

1. **基礎課題**: 基本的なEnum定義と標準メソッドの活用
2. **発展課題**: メソッドを持つEnumとステートマシンの実装
3. **チャレンジ課題**: EnumSetやEnumMapを活用した高度な設計

詳細な課題内容と実装のヒントは、GitHubリポジトリの各課題フォルダ内のREADME.mdを参照してください。

**次のステップ**: 基礎課題が完了したら、第14章「例外処理の基礎と応用」に進みましょう。





## 完了確認チェックリスト

### 基礎レベル
- [ ] 基本的なEnumの定義と使用ができている
- [ ] 状態管理パターンが実装できている
- [ ] 関数型インターフェイスとの組み合わせができている
- [ ] 複雑なEnum設計システムが構築できている

### 技術要素
- [ ] Enumの利点と制限を理解している
- [ ] 適切な設計パターンでEnumを活用できている
- [ ] Switch文での網羅性チェックを活用できている
- [ ] ビジネスロジックをEnum内に適切にカプセル化できている

### 応用レベル
- [ ] EnumSet、EnumMapを効果的に活用できている
- [ ] 抽象メソッドとポリモーフィズムを活用できている
- [ ] パフォーマンスを考慮したEnum設計ができている
- [ ] レガシーコードからEnumへのリファクタリングができている

## 本章の学習目標

### 前提知識

本章を学習するためには、第7章で学んだfinalキーワードの理解とクラス設計の経験が必須となります。特に、不変性の概念と、なぜ値を変更できないことが設計上有益なのかを理解していることが重要です。また、定数の概念と`static final`フィールドを使った基本的な定数定義の経験があることで、enumがなぜ必要なのか、従来の定数定義の何が問題なのかを実感を持って理解できます。オブジェクト指向の基本概念、特にクラスとインスタンスの関係、静的メンバーと非静的メンバーの違いについても理解していることが求められます。

設計経験の面では、実際に定数管理で苦労した経験があると本章の内容がより深く理解できます。たとえば、整数定数を使った状態管理で、無効な値が設定されてバグが発生した経験や、定数の意味が分かりにくくてコードの可読性が低下した経験などがあれば、enumの価値を実感できるでしょう。また、switch文やif文を使った分岐処理の基本的な経験があることで、enumとの組み合わせによる改善効果を理解しやすくなります。

### 学習目標

本章では、Java特有の強力な機能であるenum（列挙型）について学習します。知識理解の面では、まずenumの設計思想と、従来の`static final`定数との根本的な違いを理解します。enumは単なる定数の集合ではなく、特殊な形式のクラスであり、型安全性を保証するしくみです。この設計により、コンパイル時に不正な値の使用を防ぎ、実行時エラーを大幅に削減できます。

enumのインスタンス性とsingleton性の理解も重要なポイントです。各enum定数は、そのenum型の唯一のインスタンスとして存在し、JVMによって管理されます。この特性により、`==`演算子での比較が可能になり、switch文での効率的な分岐処理が実現されています。また、values()やvalueOf()といったenum特有のメソッドがどのように動作し、どのような場面で活用できるのかを理解します。

技能習得の観点では、基本的なenumの定義から始め、フィールドやメソッドを持つ高度なenum設計まで段階的に習得します。単純な定数の列挙だけでなく、各定数に関連するデータや振る舞いを持たせることで、より表現力豊かな設計が可能になります。たとえば、曜日のenumに営業日かどうかを判定するメソッドを追加したり、HTTPステータスコードのenumに説明文を持たせたりといった実践的な設計パターンを学びます。

設計能力の面では、適切な抽象化レベルでenumを活用する判断力を養います。すべての定数をenumにすれば良いわけではなく、値の集合が明確に定義でき、将来的にも変更が少ない場合に適用すべきです。また、Strategy PatternやState Patternといったデザインパターンとenumを組み合わせることで、より拡張可能で保守性の高い設計を実現する方法も学びます。ドメイン駆動設計（DDD）におけるValue Objectとしてのenum活用など、実務で求められる高度な設計技法も習得します。

最終的な到達レベルとして、型安全性を最大限に活用したenum設計ができるようになることを目指します。複雑なビジネスロジックをenumで表現し、コードの意図を明確に伝えられます。また、既存のレガシーコードに含まれる整数定数や文字列定数を、適切にenumへリファクタリングする能力も身につけます。これにより、保守性と可読性が大幅に向上したコードベースを構築できます。



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

## より深い理解のために

本章で学んだEnumsをさらに深く理解したい方は、付録B.20「Enumsを使った高度な設計パターン」を参照してください。この付録では以下の高度なトピックを扱います：

- **状態機械の実装**: 複雑なワークフローエンジンやタスク管理システムの実装
- **戦略パターンとEnum**: 計算戦略やプロトコル実装での活用
- **EnumSetとEnumMapの活用**: 権限管理システムや設定管理での高速処理
- **パフォーマンス最適化**: EnumSetの内部実装とビット演算による高速化
- **実践的な応用例**: イベント駆動システムでの型安全なイベント定義

これらの技術は、ドメイン駆動設計や複雑なビジネスロジックの実装において重要な役割を果たします。

## まとめ

本章では、型安全な定数を扱うための`enum`について学びました。

-   **`enum`**は、取りうる値が限定された、独自の型を定義する機能です。
-   `int`定数などと比べて、**型安全**で、コードが**読みやすく**なります。
-   `switch`文と組み合わせることで、網羅的で安全な条件分岐を記述できます。
-   クラスのように、**フィールドやメソッドを持たせる**ことができ、より高機能な定数を定義できます。
-   列挙子ごとにメソッドを実装したり、インターフェイスを実装したりすることで、さらに柔軟な設計が可能です。

プログラムの中で「種類」「状態」「ステータス」などを表す値が出てきたら、まず`enum`が使えないか検討することは、高品質なコードを書くための良い習慣です。