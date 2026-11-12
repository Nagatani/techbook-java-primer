# 第8章 列挙型 - 解答例

[基礎課題](../basic/)の解答例です。
第8章までに学んだ機能だけを使っています。

## 解答例一覧

| 課題 | ファイル | 主な内容 |
|------|----------|----------|
| 課題1: 曜日と優先度 | `DayOfWeek.java`、`Priority.java`、`BasicEnumTest.java` | フィールドとメソッドを持つ列挙型、`values`・`valueOf`・`ordinal`・`compareTo`、列挙型の`switch` |
| 課題2: 注文の状態 | `OrderStatus.java`、`Order.java`、`OrderStatusTest.java` | `switch`式で次の状態を決める、`EnumSet`でキャンセルできる状態を表す |
| 課題3: 四則演算の列挙型 | `Operation.java`、`OperationTest.java` | 列挙子ごとの抽象メソッドの実装、一部の列挙子だけがオーバーライドするメソッド |

各プログラムの出力は、基礎課題のREADMEにある実行例と同じです。

## 実行方法

```bash
cd exercises/chapter08/enums/solutions
javac *.java
java BasicEnumTest
java OrderStatusTest
java OperationTest
```

## 実装のポイント

- `DayOfWeek`の`next`は`values()[(ordinal() + 1) % values().length]`。最後の列挙子の次が最初に戻る
- 週末かどうかをフィールドにせず`isWeekend`で求めているのは、列挙子の並びと名前から決まる値だから。フィールドにするのは、列挙子ごとに自由に決める値（日本語の名前、レベル）
- `Priority`の`compareTo`は、レベルのフィールドではなく、列挙子を書いた順で比べる。`Arrays.sort`の結果も書いた順になる
- `OrderStatus`の`nextStatus`の`switch`式は、6つの列挙子をすべて書いているので`default`が要らない。状態を足すと、書き漏れがコンパイルエラーになる
- `Operation`の`DIVIDE`は、`apply`に加えて`canApply`もオーバーライドしている。列挙子ごとの`{ }`の中には、ふつうのクラスと同じようにメソッドをいくつでも書ける
- `double`を0で割っても例外にならず、`Infinity`になる。`format`は、割る前に`canApply`で確かめる

## 後の章の機能を使った書き方

- 状態ごとにできる操作の一覧を持たせるなら、第9章で学ぶ`List`や、第8章の`EnumMap`を使う
- 第11章で学ぶラムダ式を使うと、`Operation`の計算をコンストラクターに渡せる

```java
ADD("+", (x, y) -> x + y),
// ...
Operation(String symbol, DoubleBinaryOperator operator) { ... }
```
