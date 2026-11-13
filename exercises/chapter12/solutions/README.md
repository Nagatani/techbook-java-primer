# 第12章 Stream APIと高度なコレクション操作 - 解答例

[基礎課題](../basic/)の解答例です。
基礎課題の雛形の`TODO`を埋めた形で書いてあり、第12章までに学んだ機能だけを使っています。
別の書き方ができる箇所には、その書き方をコメントで添えています。

## 解答例一覧

| 課題 | ファイル | 主な内容 |
|------|----------|----------|
| 課題1: 学生データの分析 | `Student.java`、`StudentAnalyzer.java`、`StreamDemo.java` | `filter`・`map`・`sorted`・`limit`、`groupingBy`、`mapToDouble`と`average` |
| 課題2: 商品の売上の分析 | `Product.java`、`SalesAnalyzer.java`、`StreamDemo.java` | `groupingBy`と`summingDouble`、範囲での絞り込み、売上の順の上位 |
| 課題3: 数のリストのパイプライン | `NumberPipeline.java` | `partitioningBy`・`reduce`・`flatMap`・`anyMatch`・`allMatch`・`findFirst` |

`SolutionCheck.java`は、各課題の解答例が期待どおりに動くかを確かめるプログラムです。
期待する値と実際の値を比べて「OK」か「NG」を表示します。
第21章で学ぶJUnitを使うと、同じ確認をテストとして書けます。

各プログラムの出力は、基礎課題のREADMEにある実行例と同じです。

## 実行方法

解答例はパッケージ`chapter12.solutions`にあるので、`exercises/`フォルダーでコンパイルします。

```bash
cd exercises
javac -d out chapter12/solutions/*.java
java -cp out chapter12.solutions.StreamDemo
java -cp out chapter12.solutions.NumberPipeline
```

動作確認のプログラムは次のように実行します。
すべて「OK」になり、最後に「N件中N件OK」と表示されれば、解答例は期待どおりに動いています。

```bash
cd exercises
java -cp out chapter12.solutions.SolutionCheck
```

## 実装のポイント

- `groupingBy(Student::getDepartment)`だけで集めると`HashMap`になり、表示の順序が決まらない。`groupingBy(分け方, TreeMap::new, Collectors.toList())`のように、入れるマップの作り方を渡すと、キーの順に並ぶ
- `groupingBy`の3つ目の引数には、グループの中の要素の集め方を渡す。`summingDouble`なら合計、`counting`なら件数になる
- `mapToDouble(...).average()`は`OptionalDouble`を返す。空のリストでも例外にならないよう、`orElse(0.0)`で値を決める
- `reduce(0, Integer::sum)`の0は、要素がないときの結果にもなる。積を求めるときは初期値を1にする
- `findFirst`は条件に合う最初の要素を`Optional`で返す。`map(String::valueOf).orElse("なし")`のように、値がないときの表示まで1つの式で書ける

## 別の書き方

| 箇所 | 解答例の書き方 | 別の書き方 |
|------|----------------|------------|
| リストに集める | `collect(Collectors.toList())` | `toList()`（Java 16以降。返すリストは変更できない） |
| 整数の合計 | `reduce(0, Integer::sum)` | `mapToInt(Integer::intValue).sum()` |
