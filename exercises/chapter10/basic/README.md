# 第10章 基本課題

## 学習目標

- 型パラメーターを持つクラス（ジェネリッククラス）を書き、キャストなしで値を取り出せる利点を確かめる
- 複数の型パラメーターを使い、型の順序を入れ替えるメソッドを書く
- 境界のある型パラメーター（`T extends Number`、`T extends Comparable<T>`）で、使えるメソッドを広げる
- ジェネリックメソッドと、`?`・`? extends`・`? super`のワイルドカードを使い分ける

## 課題一覧

このフォルダーのJavaファイルは、`TODO`の箇所を埋めて完成させる雛形です。
課題1と課題2は雛形を完成させ、課題3は新しくファイルを作ります。
解答例は[solutions/](../solutions/)にあります。

### 課題1: 何でも入る箱（ジェネリッククラス）

**ファイル**: `GenericBox.java`、`GenericBoxDemo.java`（課題2の`Pair.java`も使う）

値を1つだけ入れられる箱`GenericBox<T>`を完成させます。

**要求仕様**:

- 2つのコンストラクター（空の箱、値の入った箱）と、`put`・`get`・`isEmpty`・`clear`を書く
- `displayInfo`は、中身の型名（`getClass().getSimpleName()`）と値を表示し、空なら「空」と表示する
- `GenericBoxDemo`の`TODO`を埋めて、`String`・`Integer`・`Pair<String, Integer>`・`Double`の箱を試す
- `displayBoxContent(GenericBox<?> box)`は、どの型の箱でも受け取れることを確かめる

**実行例**:

```
=== GenericBoxデモ ===

--- String型のBox ---
GenericBox情報:
  型: String, 値: Hello, Generics!
取得: Hello, Generics!

--- Integer型のBox ---
GenericBox情報:
  型: Integer, 値: 42

--- Pair型のBox ---
GenericBox情報:
  型: Pair, 値: Pair{first=山田太郎, second=25}

--- Box操作デモ ---
空？: false
クリア後、空？: true
GenericBox情報:
  空

--- 型安全性 ---
取得した値: 型安全

--- ワイルドカード ---
Boxの内容: Hello, Generics!
Boxの内容: 42
```

### 課題2: ペア・数値の箱・ジェネリックメソッド

**ファイル**: `Pair.java`、`NumberBox.java`、`GenericUtils.java`、`PairDemo.java`

2つの値の組`Pair<F, S>`、数値だけを入れられる`NumberBox<T extends Number>`、ジェネリックメソッドを集めた`GenericUtils`を完成させます。

**要求仕様**:

- `Pair`の`swap`は、値の順序を入れ替えた`Pair<S, F>`を新しく作って返す。`equals`と`hashCode`は両方の値で比べる
- `NumberBox`は`Number`のメソッド（`doubleValue`・`intValue`）を使って、`getAsDouble`・`getAsInt`・`isPositive`・`compareTo`を書く。`compareTo`は型の違う`NumberBox`とも比べられる
- `GenericUtils`の`swap`・`find`・`printArray`は配列、`countNonNull`・`findMin`はリストを受け取る。`max`と`findMin`は`T extends Comparable<T>`の`compareTo`で比べる
- `combinePairs`は、1つ目の組の`first`と2つ目の組の`second`から新しい組を作る
- `PairDemo`の`TODO`を埋めて、各機能を試す

**実行例**:

```
=== Pairクラスデモ ===

--- String-Integer Pair ---
Pair情報:
First (String): 山田太郎
Second (Integer): 25

--- Integer-Double Pair ---
スコア: 85
評点: 3.5

--- Pairの入れ替え ---
元: Pair{first=項目, second=100}
入れ替え後: Pair{first=100, second=項目}

=== NumberBoxデモ ===
NumberBox情報:
  型: Integer
  値: 42
  double値: 42.0
  int値: 42
NumberBox情報:
  型: Double
  値: 3.14
  double値: 3.14
  int値: 3
比較結果: 1

=== GenericUtilsデモ ===

--- 配列要素の入れ替え ---
元の配列: [Hello, World, Java, Generics]
入れ替え後: [Java, World, Hello, Generics]

--- 要素の検索 ---
30のインデックス: 2

--- 最大値 ---
最大値: Banana

--- リストの最小値 ---
最小値: 1

--- Pairの結合 ---
結合結果: Pair{first=Start, second=End}
```

### 課題3: 数値のリストとワイルドカード

**ファイル**: `NumberLists.java`（新しく作る）

`? extends`で数値のリストを読み、`? super`で整数をリストに書き込みます。

**要求仕様**:

- `static double sum(List<? extends Number> list)`は、`List<Integer>`・`List<Double>`・`List<Number>`のどれでも合計を返す
- `static void addSquares(List<? super Integer> dest, int count)`は、1からcountまでの2乗を`dest`に加える。`List<Number>`にも`List<Object>`にも加えられる
- `sum`の中でリストに要素を加えようとすると、コンパイルエラーになることを確かめる（確かめたらその行は消す）

**実行例**:

```
整数 [1, 2, 3, 4, 5] の合計: 15.0
小数 [1.5, 2.5, 3.5] の合計: 7.5
List<Number>に追加: [1, 4, 9, 0.5]
List<Object>に追加: [平方数:, 1, 4, 9, 16]
List<Number>の合計: 14.5
```

## コンパイルと実行

課題1と課題2の雛形は`package chapter10.basic;`を宣言しているので、`exercises/`フォルダーでコンパイルします。

```bash
cd exercises
javac -d out chapter10/basic/*.java
java -cp out chapter10.basic.GenericBoxDemo
java -cp out chapter10.basic.PairDemo
```

課題3は、作ったファイルのあるフォルダーで`javac`と`java`を実行します。

## ヒント

- `Pair<S, F> swap()`では、戻り値の型パラメーターの順序が入れ替わる。`new Pair<>(second, first)`と書けば、ダイヤモンド演算子が型を補う
- `equals`で`Object`を`Pair`にキャストするときは`Pair<?, ?>`にする（型パラメーターは実行時に消えるため、`Pair<F, S>`かどうかは確かめられない）
- 値が`null`かもしれないフィールドの比較には`Objects.equals`、ハッシュ値には`Objects.hash`が使える
- 「値を取り出すだけなら`extends`、入れるだけなら`super`」と覚える

## 完了チェックリスト

- [ ] 課題1: キャストなしで箱から値を取り出せる
- [ ] 課題1: `GenericBox<?>`でどの型の箱も受け取れる
- [ ] 課題2: `swap`で型パラメーターの順序が入れ替わった組を返せる
- [ ] 課題2: 境界のある型パラメーターで`doubleValue`や`compareTo`を呼べる
- [ ] 課題3: `? extends`と`? super`の違いを、読み書きできる操作で説明できる

**次のステップ**: 基本課題が終わったら、[advanced/](../advanced/)の発展課題に進みましょう。
