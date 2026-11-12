# 第8章 基本課題（列挙型）

## 学習目標

- 列挙型（`enum`）で、決まった値の集まりを型として表す
- 列挙子にフィールドとメソッドを持たせ、`values`・`valueOf`・`ordinal`・`compareTo`を使う
- 列挙型の`switch`で、列挙子ごとに処理を分ける
- 状態の移り方を列挙型のメソッドにまとめ、`EnumSet`で列挙子の集まりを表す
- 列挙子ごとに抽象メソッドを実装する

## 課題一覧

このフォルダーには雛形がありません。課題ごとにファイルを新しく作ります。
解答例は[solutions/](../solutions/)にあります。

### 課題1: 曜日と優先度

**ファイル**: `DayOfWeek.java`、`Priority.java`、`BasicEnumTest.java`

**要求仕様**:

- `DayOfWeek`は月曜日から日曜日までの列挙子と、日本語の名前のフィールドを持つ。`isWeekend`で週末かどうか、`next`で次の曜日を返す（日曜日の次は月曜日）
- `Priority`はURGENT・HIGH・MEDIUM・LOWの列挙子と、レベル（1〜4）・対応の指示のフィールドを持つ。`toString`は`URGENT(1)`の形にする
- `values`で全曜日を`ordinal`と一緒に表示し、`valueOf("FRIDAY")`で列挙子を取り出す
- 優先度の配列を`Arrays.sort`で並べ替え、`compareTo`で比べる
- `switch`で、優先度ごとに色を決める

**実行例**:

```
=== 基本的なEnum定義と活用 ===
今日: MONDAY（月曜日）
明日: TUESDAY
日曜日の次: MONDAY
今日は週末: false

全曜日:
0: MONDAY 月曜日（平日）
1: TUESDAY 火曜日（平日）
2: WEDNESDAY 水曜日（平日）
3: THURSDAY 木曜日（平日）
4: FRIDAY 金曜日（平日）
5: SATURDAY 土曜日（週末）
6: SUNDAY 日曜日（週末）

valueOf("FRIDAY"): 金曜日

優先度ごとの対応:
URGENT(1) → 即座に対応が必要です
HIGH(2) → 今日中に対応してください
MEDIUM(3) → 今週中に対応してください
LOW(4) → 時間があるときに対応してください

並べ替えた優先度: [URGENT(1), URGENT(1), HIGH(2), MEDIUM(3), LOW(4)]
URGENT.compareTo(LOW): -3

switchでの分岐:
URGENT: 赤
HIGH: 橙
MEDIUM: 黄
LOW: 緑
```

### 課題2: 注文の状態

**ファイル**: `OrderStatus.java`、`Order.java`、`OrderStatusTest.java`

**要求仕様**:

- `OrderStatus`はPENDING（注文受付中）・CONFIRMED・PREPARING・SHIPPED・DELIVERED・CANCELLEDの列挙子と、日本語の表示名を持つ
- `nextStatus`は`switch`で次の状態を返す（配達完了とキャンセル済みには次がないので`null`）
- `canTransitionTo`は、次の状態か、キャンセルできる状態からのキャンセルだけを許す。キャンセルできる状態は`EnumSet.of(PENDING, CONFIRMED, PREPARING)`で表す
- `Order`は今の状態と、移った状態の履歴（配列）を持つ。`changeStatus`は移れないときに状態を変えず`false`を返す

**実行例**:

```
=== 注文状態管理システム ===
注文ID: ORD-001
初期状態: PENDING（注文受付中）

状態遷移:
PENDING → CONFIRMED（注文確認済み）
CONFIRMED → PREPARING（準備中）
PREPARING → SHIPPED（発送済み）
SHIPPED → DELIVERED（配達完了）
履歴: PENDING → CONFIRMED → PREPARING → SHIPPED → DELIVERED

状態遷移の検査:
PENDING → SHIPPED: ✗ 無効な遷移です
DELIVERED → PENDING: ✗ 無効な遷移です
SHIPPED → CANCELLED: ✗ 無効な遷移です
CONFIRMED → CANCELLED: ✓ 移れます

キャンセルした注文の履歴: PENDING → CONFIRMED → CANCELLED
キャンセル後に準備へ進めるか: false

キャンセルできる状態: PENDING CONFIRMED PREPARING
```

### 課題3: 四則演算の列挙型

**ファイル**: `Operation.java`、`OperationTest.java`

**要求仕様**:

- `Operation`はADD・SUBTRACT・MULTIPLY・DIVIDEの列挙子と、記号（`+`など）のフィールドを持つ
- 抽象メソッド`apply(double x, double y)`を宣言し、列挙子ごとに`{ }`の中で実装する
- `canApply`はふつう`true`を返し、DIVIDEだけが「割る数が0でない」ときに`true`を返すようオーバーライドする
- `fromSymbol`は記号から列挙子を探し、なければ`null`を返す
- `format`は`10.0 + 5.0 = 15.0`の形の式を返し、計算できないときはエラーの文にする

**実行例**:

```
=== 計算機の演算子Enum ===
基本演算:
10.0 + 5.0 = 15.0
10.0 - 5.0 = 5.0
10.0 * 5.0 = 50.0
10.0 / 5.0 = 2.0

連続計算（初期値 10）:
10.0 + 5.0 = 15.0
15.0 * 2.0 = 30.0
30.0 - 8.0 = 22.0
22.0 / 2.0 = 11.0
結果: 11.0

特殊ケース:
10.0 / 0.0 = エラー: 0で割ることはできません
記号 % の演算子: null
```

## コンパイルと実行

```bash
cd exercises/chapter08/enums/basic
javac *.java
java BasicEnumTest
```

## ヒント

- 列挙型のコンストラクターは`private`になり、外から`new`できない。列挙子はクラスを読み込んだときに1つずつだけ作られる
- 列挙子どうしは`==`で比べてよい（同じ列挙子は1つしかない）
- `switch`の`case`には、`Priority.URGENT`ではなく`URGENT`と列挙子の名前だけを書く
- `switch`式ですべての列挙子を書くと`default`が要らない。列挙子を足したときに、書き漏れがコンパイルエラーで見つかる
- `values()`が返す配列の順序と`ordinal()`の値は、列挙子を書いた順になる

## 完了チェックリスト

- [ ] 課題1: 列挙子にフィールドとメソッドを持たせられる
- [ ] 課題1: `values`・`valueOf`・`ordinal`・`compareTo`を使える
- [ ] 課題2: 状態の移り方を、列挙型のメソッドにまとめられる
- [ ] 課題3: 列挙子ごとに抽象メソッドを実装できる
- [ ] 定数を`int`で表す書き方と比べた、列挙型の利点を説明できる

**次のステップ**: 基本課題が完了したら、[advanced/](../advanced/)の発展課題に進みましょう。
