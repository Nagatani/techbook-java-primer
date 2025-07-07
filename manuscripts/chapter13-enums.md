# 第13章 列挙型(Enums)

## 章末演習

本章で学んだ列挙型（Enum）の概念を活用して、実践的な練習課題に取り組みましょう。

### 演習の目標
Enum（列挙型）を使った型安全なプログラミング技術を習得します。

---

## 基礎レベル課題（必須）

### 課題1: 基本的なEnum定義と活用

基本的なEnumクラスを定義し、その特性を理解してください。

**技術的背景：型安全な定数管理の重要性**

従来の定数定義方法には多くの問題がありました：

**整数定数の問題：**
```java
// アンチパターン：int定数
public static final int MONDAY = 0;
public static final int TUESDAY = 1;
// ...
public static final int PRIORITY_HIGH = 0;
public static final int PRIORITY_LOW = 1;

// 問題：異なる意味の定数を混同可能
setDay(PRIORITY_HIGH);  // コンパイルエラーにならない！
```

**文字列定数の問題：**
```java
// 文字列比較の罠
if (status == "PENDING") {  // 文字列の==比較は危険
    // ...
}
```

**Enumによる解決：**
- **型安全性**：異なるEnum型は混同できない
- **名前空間**：定数が論理的にグループ化される
- **メソッド追加可能**：振る舞いを持つ定数
- **switch文での網羅性チェック**：すべてのケースの処理を強制

**実際の活用例：**
- **HTTPステータスコード**：200 OK、404 Not Foundなど
- **ログレベル**：DEBUG、INFO、WARN、ERROR
- **権限レベル**：ADMIN、USER、GUEST
- **設定値**：環境（DEV、TEST、PROD）

この演習では、保守性と安全性を高めるEnum設計の基礎を学びます。

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

### 課題2: 注文状態管理システム

注文の状態をEnumで管理し、状態遷移を実装してください。

**技術的背景：状態機械とEnumの相性**

注文処理のような状態遷移を伴うシステムでは、状態管理が複雑になりがちです：

**従来の状態管理の問題：**
```java
// 文字列による状態管理（エラーが起きやすい）
String status = "PENDING";
if (status.equals("PEDING")) {  // タイポに気づかない
    status = "CONFIRMED";
}

// 不正な状態遷移を防げない
status = "DELIVERED";  // PENDINGから直接DELIVEREDへ
```

**Enumによる状態機械の実装：**
```java
public enum OrderStatus {
    PENDING {
        @Override
        public boolean canTransitionTo(OrderStatus next) {
            return next == CONFIRMED || next == CANCELLED;
        }
    },
    CONFIRMED {
        @Override
        public boolean canTransitionTo(OrderStatus next) {
            return next == PREPARING || next == CANCELLED;
        }
    },
    // ... 他の状態
    
    public abstract boolean canTransitionTo(OrderStatus next);
}
```

**実際のシステムでの活用：**
- **ECサイト**：注文→支払い→配送→完了の流れ
- **ワークフロー**：申請→承認→実行→完了
- **チケット管理**：新規→対応中→解決→クローズ
- **製造ライン**：設計→製造→検査→出荷

**状態機械の利点：**
- **不正な遷移の防止**：ビジネスルールの強制
- **可視化**：状態遷移図との対応が明確
- **テスタビリティ**：各状態の振る舞いを独立してテスト

この演習では、実務で頻繁に必要となる状態管理パターンを学びます。

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

### 課題3: 計算機の演算子Enum

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

### 課題4: ゲーム設定のEnum活用

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

## 実装のヒント

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

## 実装環境

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

設計経験の面では、実際に定数管理で苦労した経験があると本章の内容がより深く理解できます。例えば、整数定数を使った状態管理で、無効な値が設定されてバグが発生した経験や、定数の意味が分かりにくくてコードの可読性が低下した経験などがあれば、enumの価値を実感できるでしょう。また、switch文やif文を使った分岐処理の基本的な経験があることで、enumとの組み合わせによる改善効果を理解しやすくなります。

### 学習目標

本章では、Java特有の強力な機能であるenum（列挙型）について学習します。知識理解の面では、まずenumの設計思想と、従来の`static final`定数との根本的な違いを理解します。enumは単なる定数の集合ではなく、特殊な形式のクラスであり、型安全性を保証する仕組みです。この設計により、コンパイル時に不正な値の使用を防ぎ、実行時エラーを大幅に削減できます。

enumのインスタンス性とsingleton性の理解も重要なポイントです。各enum定数は、そのenum型の唯一のインスタンスとして存在し、JVMによって管理されます。この特性により、`==`演算子での比較が可能になり、switch文での効率的な分岐処理が実現されています。また、values()やvalueOf()といったenum特有のメソッドがどのように動作し、どのような場面で活用できるのかを理解します。

技能習得の観点では、基本的なenumの定義から始め、フィールドやメソッドを持つ高度なenum設計まで段階的に習得します。単純な定数の列挙だけでなく、各定数に関連するデータや振る舞いを持たせることで、より表現力豊かな設計が可能になります。例えば、曜日のenumに営業日かどうかを判定するメソッドを追加したり、HTTPステータスコードのenumに説明文を持たせたりといった実践的な設計パターンを学びます。

設計能力の面では、適切な抽象化レベルでenumを活用する判断力を養います。すべての定数をenumにすれば良いわけではなく、値の集合が明確に定義でき、将来的にも変更が少ない場合に適用すべきです。また、Strategy PatternやState Patternといったデザインパターンとenumを組み合わせることで、より拡張可能で保守性の高い設計を実現する方法も学びます。ドメイン駆動設計（DDD）におけるValue Objectとしてのenum活用など、実務で求められる高度な設計技法も習得します。

最終的な到達レベルとして、型安全性を最大限に活用したenum設計ができるようになることを目指します。複雑なビジネスロジックをenumで表現し、コードの意図を明確に伝えられるようになります。また、既存のレガシーコードに含まれる整数定数や文字列定数を、適切にenumへリファクタリングする能力も身につけます。これにより、保守性と可読性が大幅に向上したコードベースを構築できるようになります。

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