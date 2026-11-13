# 第10章 ジェネリクス - 解答例

[基礎課題](../basic/)の解答例です。
基礎課題の雛形の`TODO`を埋めた形で書いてあり、第10章までに学んだ機能だけを使っています。
後の章で学ぶ機能を使うと短く書ける箇所には、その書き方をコメントで添えています。

## 解答例一覧

| 課題 | ファイル | 主な内容 |
|------|----------|----------|
| 課題1: 何でも入る箱 | `GenericBox.java`、`GenericBoxDemo.java` | ジェネリッククラス、`GenericBox<?>`で受け取るメソッド |
| 課題2: ペア・数値の箱・ジェネリックメソッド | `Pair.java`、`NumberBox.java`、`GenericUtils.java`、`PairDemo.java` | 複数の型パラメーター、境界のある型パラメーター、ジェネリックメソッド |
| 課題3: 数値のリストとワイルドカード | `NumberLists.java` | `? extends Number`で読み、`? super Integer`で書き込む |

`SolutionCheck.java`は、各課題の解答例が期待どおりに動くかを確かめるプログラムです。
期待する値と実際の値を比べて「OK」か「NG」を表示します。
第21章で学ぶJUnitを使うと、同じ確認をテストとして書けます。

各プログラムの出力は、基礎課題のREADMEにある実行例と同じです。

## 実行方法

解答例はパッケージ`chapter10.solutions`にあるので、`exercises/`フォルダーでコンパイルします。

```bash
cd exercises
javac -d out chapter10/solutions/*.java
java -cp out chapter10.solutions.GenericBoxDemo
java -cp out chapter10.solutions.PairDemo
java -cp out chapter10.solutions.NumberLists
```

動作確認のプログラムは次のように実行します。
すべて「OK」になり、最後に「N件中N件OK」と表示されれば、解答例は期待どおりに動いています。

```bash
cd exercises
java -cp out chapter10.solutions.SolutionCheck
```

## 実装のポイント

- `GenericBox`の`displayInfo`は、型名を中身のオブジェクトの`getClass()`から取り出す。型パラメーター`T`は実行時に消えているので、`T.class`とは書けない
- `Pair`の`swap`の戻り値は`Pair<S, F>`。`new Pair<>(second, first)`と書けば、ダイヤモンド演算子が型を補う
- `Pair`の`equals`では、`Object`を`Pair<?, ?>`にキャストする。値は`Objects.equals`で比べるので、`null`の値を持つ組どうしも比べられる
- `NumberBox<T extends Number>`の中では、`T`の値に`Number`のメソッドを呼べる。`compareTo`の引数を`NumberBox<?>`にしたので、`NumberBox<Integer>`と`NumberBox<Double>`を比べられる
- `GenericUtils`の`max`・`findMin`は、`T extends Comparable<T>`の`compareTo`で比べる。`String`や`Integer`は`Comparable`を実装している
- `NumberLists`の`sum`の中で`list.add(1)`と書くと、コンパイルエラーになる。`List<? extends Number>`は`List<Double>`かもしれないので、`Integer`を入れられない

## 後の章の機能を使った書き方

コメントに書いた別の書き方をまとめます。
どれも第10章の書き方と同じ結果になります。

| 箇所 | 第10章の書き方 | 後の章の機能を使った書き方 |
|------|----------------|----------------------------|
| `null`でない要素の数 | 拡張for文で数える | `stream().filter(Objects::nonNull).count()`（第11章のメソッド参照、第12章のStream API） |
| リストの最小値 | 拡張for文と`compareTo` | `stream().min(Comparator.naturalOrder()).orElse(null)`（第12章のStream APIとOptional） |
| 数値のリストの合計 | 拡張for文で足す | `stream().mapToDouble(Number::doubleValue).sum()`（第12章） |
