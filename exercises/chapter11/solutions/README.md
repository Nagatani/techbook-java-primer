# 第11章 ラムダ式と関数型インターフェイス - 解答例

[基礎課題](../basic/)の解答例です。
基礎課題の雛形の`TODO`を埋めた形で書いてあり、第11章までに学んだ機能だけを使っています。
ラムダ式はメソッド参照でも書ける箇所が多いので、その書き方をコメントで添えています。
後の章で学ぶ機能（第12章のStream API）を使った書き方も、コメントで示しています。

## 解答例一覧

| 課題 | ファイル | 主な内容 |
|------|----------|----------|
| 課題1: 標準の関数型インターフェイス | `StandardFunctionalDemo.java` | Function・Predicate・Consumer・Supplier・BiFunction・UnaryOperatorをラムダ式で実装する |
| 課題2: 独自の関数型インターフェイス | `StringProcessor.java`、`Calculator.java`、`CustomFunctionalDemo.java` | デフォルトメソッドで処理をつなげ、静的メソッドで計算のラムダ式を返す |
| 課題3: メソッド参照と関数の合成 | `MethodRefs.java` | 3種類のメソッド参照、`Predicate`の`and`・`negate`、`Function`の`andThen` |

`SolutionCheck.java`は、各課題の解答例が期待どおりに動くかを確かめるプログラムです。
期待する値と実際の値を比べて「OK」か「NG」を表示します。
第21章で学ぶJUnitを使うと、同じ確認をテストとして書けます。

各プログラムの出力は、基礎課題のREADMEにある実行例と同じです。

## 実行方法

解答例はパッケージ`chapter11.solutions`にあるので、`exercises/`フォルダーでコンパイルします。

```bash
cd exercises
javac -d out chapter11/solutions/*.java
java -cp out chapter11.solutions.StandardFunctionalDemo
java -cp out chapter11.solutions.CustomFunctionalDemo
java -cp out chapter11.solutions.MethodRefs
```

動作確認のプログラムは次のように実行します。
すべて「OK」になり、最後に「N件中N件OK」と表示されれば、解答例は期待どおりに動いています。

```bash
cd exercises
java -cp out chapter11.solutions.SolutionCheck
```

## 実装のポイント

- `Supplier<List<String>>`の`() -> new ArrayList<>()`は、`get`を呼ぶたびに本体が実行されるので、毎回新しいリストを返す
- `StringProcessor`の`withTrim`は`s -> process(s.trim())`。この`process`は、`withTrim`を呼んだインスタンス（`toUpper`など）の`process`になる
- `andThen(after)`は`s -> after.process(process(s))`。自分の処理が先、`after`が後になる
- `Calculator.divide()`は、本体が複数の文になるので、ラムダ式を`{ }`のブロックで書き、`return`で値を返す
- `MethodRefs`では、`List.of`で作ったリストを`ArrayList`にコピーしてから`replaceAll`・`sort`・`removeIf`を呼ぶ。`List.of`のリストは変更できず、呼ぶと`UnsupportedOperationException`になる
- `sort`は安定な並べ替えなので、短い順に並べると、同じ長さの語は直前の辞書順のまま残る

## 別の書き方

コメントに書いた別の書き方をまとめます。
どれも解答例と同じ結果になります。

| 箇所 | 解答例の書き方 | 別の書き方 |
|------|----------------|------------|
| 文字列の長さ | `s -> s.length()` | `String::length`（第11章のメソッド参照） |
| 新しいリストを作る | `() -> new ArrayList<>()` | `ArrayList::new`（第11章のコンストラクター参照） |
| 大きいほう | `(a, b) -> a >= b ? a : b` | `Math::max`（第11章の静的メソッド参照） |
| べき乗 | `(x, y) -> Math.pow(x, y)` | `Math::pow` |
| 短い順の`Comparator` | `(a, b) -> Integer.compare(a.length(), b.length())` | `Comparator.comparingInt(String::length)`（第11章） |
| 小文字にして並べる | `ArrayList`にコピーし、`replaceAll`と`sort`で書き換える | `stream().map(String::toLowerCase).sorted(...).toList()`で、元のリストを変えずに新しいリストを作る（第12章のStream API） |
