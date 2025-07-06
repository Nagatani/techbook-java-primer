# 第13章 列挙型(Enums)

## 章末演習

本章で学んだ列挙型（Enum）の概念を活用して、実践的な練習課題に取り組みましょう。

### 🎯 演習の目標
- Enum（列挙型）の基本概念と利点の理解
- 定数としてのEnumの適切な使用
- Enumのメソッドとフィールドの活用
- 状態マシンパターンの実装
- EnumとSwitch文の効果的な組み合わせ
- EnumSetとEnumMapを活用した高性能なコレクション操作

### 演習課題の難易度レベル

#### 🟢 基礎レベル（Basic）
- **目的**: Enumの基本概念と構文の理解
- **所要時間**: 25-35分/課題
- **前提**: 本章の内容を理解していること
- **評価基準**: 
  - Enumの正しい定義と使用
  - 基本メソッドの理解と活用
  - Switch文での適切な使用
  - 型安全性の理解

#### 🟡 応用レベル（Applied）  
- **目的**: 実践的なEnum活用と状態管理
- **所要時間**: 35-50分/課題
- **前提**: 基礎レベルを完了していること
- **評価基準**:
  - 状態管理パターンの実装
  - ビジネスロジックとEnumの統合
  - 関数型インターフェイスとの組み合わせ
  - 複雑な設定管理システム

#### 🔴 発展レベル（Advanced）
- **目的**: Enumの高度な活用とアーキテクチャ設計
- **所要時間**: 50-70分/課題
- **前提**: 応用レベルを完了していること
- **評価基準**:
  - 抽象メソッドとポリモーフィズム
  - EnumSet、EnumMapの効果的な活用
  - フレームワークレベルの設計
  - パフォーマンスを考慮した実装

---

## 🟢 基礎レベル課題（必須）

### 課題1: 基本的なEnum定義と活用

**学習目標：** Enumの基本構文、基本メソッド、Switch文での活用

**問題説明：**
基本的なEnumクラスを定義し、その特性を理解してください。

**要求仕様：**
- 曜日を表すEnum（メソッド付き）
- 優先度を表すEnum（数値付き）
- Enumの基本メソッド（values、valueOf、ordinal等）
- Switch文でのEnum活用
- Enumの比較とソート

**実行例：**
```
=== 基本的なEnum定義と活用 ===
曜日Enum:
今日: MONDAY
明日: TUESDAY
今日は平日です: true
今日は週末です: false

全曜日の表示:
MONDAY: 月曜日（平日）
TUESDAY: 火曜日（平日）
WEDNESDAY: 水曜日（平日）
THURSDAY: 木曜日（平日）
FRIDAY: 金曜日（平日）
SATURDAY: 土曜日（週末）
SUNDAY: 日曜日（週末）

優先度Enum:
総急: URGENT（レベル: 1）
高: HIGH（レベル: 2）
中: MEDIUM（レベル: 3）
低: LOW（レベル: 4）

優先度ソート:
[URGENT(1), HIGH(2), MEDIUM(3), LOW(4)]

Switch文での活用:
URGENT → 即座に対応が必要です
HIGH → 今日中に対応してください
MEDIUM → 今週中に対応してください
LOW → 時間があるときに対応してください

Enum比較:
URGENT.compareTo(LOW): -3
URGENT < LOW: true（数値が小さいほう が優先度高）
```

**実装ヒント：**
```java
public enum DayOfWeek {
    MONDAY("Monday", true),
    TUESDAY("Tuesday", true),
    WEDNESDAY("Wednesday", true),
    THURSDAY("Thursday", true),
    FRIDAY("Friday", true),
    SATURDAY("Saturday", false),
    SUNDAY("Sunday", false);
    
    private final String displayName;
    private final boolean isWeekday;
    
    DayOfWeek(String displayName, boolean isWeekday) {
        this.displayName = displayName;
        this.isWeekday = isWeekday;
    }
    
    public boolean isWeekday() { return isWeekday; }
    public boolean isWeekend() { return !isWeekday; }
    public String getDisplayName() { return displayName; }
}

public enum Priority {
    URGENT(1), HIGH(2), MEDIUM(3), LOW(4);
    
    private final int level;
    
    Priority(int level) {
        this.level = level;
    }
    
    public int getLevel() { return level; }
    
    public String getDescription() {
        return switch (this) {
            case URGENT -> "即座に対応が必要です";
            case HIGH -> "今日中に対応してください";
            case MEDIUM -> "今週中に対応してください";
            case LOW -> "時間があるときに対応してください";
        };
    }
}
```

### 課題2: 注文状態管理システム

**学習目標：** 状態パターン、ビジネスロジックのEnum内実装、状態遷移検証

**問題説明：**
注文の状態をEnumで管理し、状態遷移を実装してください。

**要求仕様：**
- 注文状態を表すEnum（状態遷移メソッド付き）
- 各状態での可能なアクション定義
- 状態遷移の検証機能
- 状態履歴の管理
- ビジネスロジックの実装

**実行例：**
```
=== 注文状態管理システム ===
注文作成:
注文ID: ORD-001
初期状態: PENDING（注文受付中）

状態遷移テスト:
PENDING → CONFIRMED（注文確認済み）
CONFIRMED → PREPARING（準備中）
PREPARING → SHIPPED（発送済み）
SHIPPED → DELIVERED（配達完了）

各状態での可能なアクション:
PENDING: [確認, キャンセル]
CONFIRMED: [準備開始, キャンセル]
PREPARING: [発送, キャンセル]
SHIPPED: [配達完了]
DELIVERED: [返品受付]

無効な状態遷移テスト:
PENDING → SHIPPED: ✗ 無効な遷移です
DELIVERED → PENDING: ✗ 無効な遷移です

状態履歴:
2024-07-04 10:00:00 - PENDING
2024-07-04 10:30:00 - CONFIRMED
2024-07-04 14:00:00 - PREPARING
2024-07-04 17:00:00 - SHIPPED
2024-07-05 09:00:00 - DELIVERED

ビジネスルール:
キャンセル可能期間: PENDING, CONFIRMED, PREPARING
返品可能状態: DELIVERED
追跡可能状態: SHIPPED, DELIVERED
```

**実装ヒント：**
```java
public enum OrderStatus {
    PENDING("注文受付中", Set.of(CONFIRMED, CANCELLED)),
    CONFIRMED("注文確認済み", Set.of(PREPARING, CANCELLED)),
    PREPARING("準備中", Set.of(SHIPPED, CANCELLED)),
    SHIPPED("発送済み", Set.of(DELIVERED)),
    DELIVERED("配達完了", Set.of(RETURNED)),
    CANCELLED("キャンセル済み", Set.of()),
    RETURNED("返品済み", Set.of());
    
    private final String description;
    private final Set<OrderStatus> allowedTransitions;
    
    OrderStatus(String description, Set<OrderStatus> allowedTransitions) {
        this.description = description;
        this.allowedTransitions = allowedTransitions;
    }
    
    public boolean canTransitionTo(OrderStatus next) {
        return allowedTransitions.contains(next);
    }
    
    public boolean isCancellable() {
        return this == PENDING || this == CONFIRMED || this == PREPARING;
    }
    
    public List<String> getAvailableActions() {
        return switch (this) {
            case PENDING -> List.of("確認", "キャンセル");
            case CONFIRMED -> List.of("準備開始", "キャンセル");
            case PREPARING -> List.of("発送", "キャンセル");
            case SHIPPED -> List.of("配達完了");
            case DELIVERED -> List.of("返品受付");
            default -> List.of();
        };
    }
}
```

### 課題3: 計算機の演算子Enum

**学習目標：** 関数型インターフェイスとの統合、複雑なビジネスロジック実装

**問題説明：**
計算機の演算子をEnumで実装し、関数型インターフェイスと組み合わせてください。

**要求仕様：**
- 四則演算を表すEnum（関数実装付き）
- 関数型インターフェイスとの組み合わせ
- 演算子の優先度実装
- 複雑な計算式の処理
- 演算履歴の管理

**実行例：**
```
=== 計算機の演算子Enum ===
基本演算テスト:
10 + 5 = 15.0
10 - 5 = 5.0
10 * 5 = 50.0
10 / 5 = 2.0

演算子の優先度:
MULTIPLY: 2
DIVIDE: 2
ADD: 1
SUBTRACT: 1

複雑な計算（優先度考慮）:
式: 2 + 3 * 4
解析順序: [3 * 4 = 12, 2 + 12 = 14]
結果: 14.0

連続計算:
初期値: 10
+5 = 15.0
*2 = 30.0
-8 = 22.0
/2 = 11.0

演算履歴:
10.0 + 5.0 = 15.0
15.0 * 2.0 = 30.0
30.0 - 8.0 = 22.0
22.0 / 2.0 = 11.0

特殊ケース処理:
10 / 0 = エラー: ゼロ除算は実行できません
√-1 = エラー: 負の数の平方根は計算できません
```

**実装ヒント：**
```java
public enum Operation {
    ADD("+", 1, (x, y) -> x + y),
    SUBTRACT("-", 1, (x, y) -> x - y),
    MULTIPLY("*", 2, (x, y) -> x * y),
    DIVIDE("/", 2, (x, y) -> {
        if (y == 0) {
            throw new ArithmeticException("ゼロ除算は実行できません");
        }
        return x / y;
    });
    
    private final String symbol;
    private final int precedence;
    private final BinaryOperator<Double> operation;
    
    Operation(String symbol, int precedence, BinaryOperator<Double> operation) {
        this.symbol = symbol;
        this.precedence = precedence;
        this.operation = operation;
    }
    
    public double apply(double x, double y) {
        return operation.apply(x, y);
    }
    
    public String getSymbol() { return symbol; }
    public int getPrecedence() { return precedence; }
}
```

### 課題4: ゲーム設定のEnum活用

**学習目標：** 複数Enumの組み合わせ設計、動的設定システム

**問題説明：**
ゲーム設定を管理するEnumシステムを実装してください。

**要求仕様：**
- 難易度レベルを表すEnum（パラメータ付き）
- ゲームモードを表すEnum（機能設定付き）
- パワーアップアイテムのEnum（効果実装付き）
- 設定の組み合わせ検証
- 動的な設定変更システム

**実行例：**
```
=== ゲーム設定のEnum活用 ===
難易度設定:
EASY: 敵HP×0.5, プレイヤーHP×2.0, 経験値×1.0
NORMAL: 敵HP×1.0, プレイヤーHP×1.0, 経験値×1.0
HARD: 敵HP×1.5, プレイヤーHP×0.7, 経験値×1.5
EXTREME: 敵HP×2.0, プレイヤーHP×0.5, 経験値×2.0

ゲームモード:
STORY: ストーリーモード（セーブ可能、チェックポイント有り）
SURVIVAL: サバイバルモード（セーブ不可、連続プレイ）
CHALLENGE: チャレンジモード（特殊ルール、リーダーボード）
MULTIPLAYER: マルチプレイヤー（オンライン、最大4人）

パワーアップアイテム:
SPEED_BOOST: 速度アップ（効果時間: 30秒）
DOUBLE_DAMAGE: ダメージ2倍（効果時間: 20秒）
INVINCIBLE: 無敵状態（効果時間: 10秒）
HEALTH_REGEN: HP回復（効果時間: 60秒）

設定組み合わせ検証:
✓ EASY + STORY: 初心者向け設定
✓ HARD + SURVIVAL: 上級者向け設定
✗ EXTREME + MULTIPLAYER: 不適切な組み合わせ
理由: EXTREMEは単人プレイ専用です

現在の設定:
難易度: NORMAL
モード: STORY
有効なパワーアップ: [SPEED_BOOST, HEALTH_REGEN]
設定スコア: 100点（バランス良好）

設定推奨:
初心者: EASY + STORY
経験者: NORMAL + CHALLENGE
エキスパート: HARD + SURVIVAL
```

---

## 💡 実装のヒント

### Enum の基本原則
1. **型安全性**: コンパイル時の値チェック
2. **メソッド追加**: Enumに独自メソッドを実装可能
3. **シングルトン**: 各Enum要素は唯一のインスタンス
4. **比較**: ==での比較が安全

### よくある落とし穴
- Enumは継承できない（設計上の制約）
- コンストラクタは暗黙的にprivate
- ordinal()に依存した設計は危険
- Switch文での網羅性チェック活用

### 設計のベストプラクティス
- 状態や種類を表す値にEnumを活用
- 定数クラスよりEnumを優先
- Switch文での網羅性を活用
- ビジネスロジックをEnum内にカプセル化

---

## 🔗 実装環境

演習課題の詳細な実装テンプレート、テストコード、解答例は以下のディレクトリを参照してください：

```
exercises/chapter13/
├── basic/          # 基礎レベル課題
│   ├── README.md   # 詳細な課題説明
│   ├── DayOfWeek.java
│   ├── OrderStatus.java
│   ├── Operation.java
│   └── GameConfig.java
├── advanced/       # 応用レベル課題
├── challenge/      # 発展レベル課題
└── solutions/      # 解答例（実装完了後に参照）
```

---

## ✅ 完了確認チェックリスト

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