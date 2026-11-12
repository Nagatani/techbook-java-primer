# 第12章 チャレンジ課題

## 学習目標
- 文字列のデータを`map`と`flatMap`でオブジェクトのリストに変換する
- `groupingBy`と`averagingInt`・`summingInt`・`maxBy`・`partitioningBy`・`mapping`を組み合わせて、いろいろな単位で集計する
- 集計の結果を`entrySet().stream()`で並べ替え、`limit`で上位を取り出す
- `anyMatch`・`allMatch`で条件を確かめる
- `maxBy`や`findFirst`が返す`Optional`を、`map`と`orElse`で扱う
- 順次処理と並列処理（`parallelStream`）で、同じ結果になる集計を書く

## 課題一覧

### 課題1: 成績の集計レポート
**ファイル**（このフォルダーのファイルは解答例です）:
```
challenge/
├── Score.java           1人の学生の1科目の点数（レコード）
├── GradeData.java       成績のデータ（文字列のリスト）
├── ScoreParser.java     文字列のデータをScoreのリストに変換する
├── GradeReport.java     集計のメソッド
└── GradeReportApp.java  mainメソッド
```

クラスの成績のデータから、科目別・クラス別・学生別の集計レポートを作るプログラムを作成してください。
データは1行が1人の学生で、次のように1行の中に複数の科目の点数が並んでいます。

```
S001,佐藤,A組,国語:78;数学:85;英語:62
```

**要求仕様**:
- `ScoreParser`（データの変換）
    - 1行を「学籍番号・氏名・クラス・科目・点数」の`Score`に分ける。1行から科目の数だけの`Score`ができる
    - すべての行を`flatMap`でつないで、1つの`List<Score>`にする
- `GradeReport`（集計）
    - 科目ごとの平均点（`averagingInt`）と、科目ごとの最高点の`Score`（`maxBy`。結果は`Optional<Score>`）
    - クラスごとの平均点
    - 学生ごとの合計点（`summingInt`）と、合計点の高い順の上位n人（同点は学籍番号の順）
    - 科目ごとの合格者数と不合格者数（60点以上で合格。`partitioningBy`と`counting`）
    - 不合格の科目がある学生と、その科目の一覧（`filter`のあと`groupingBy`と`mapping`）
    - 全科目に合格した学生がいるか（`anyMatch`）、全員の全科目が指定した点数以上か（`allMatch`）
    - 学籍番号で学生の合計点を探す。見つからなければ空の`Optional`を返す
    - 全員の全科目の点数の合計を、`stream()`と`parallelStream()`の両方で求める
- 結果の`Map`は`TreeMap`にして、表示の順が毎回同じになるようにする
- 平均点は小数第1位まで表示する（`"%.1f".formatted(値)`）

**実行例**:
```
学生 6人、点数 18件

=== 科目ごとの平均点と最高点 ===
  国語: 平均 74.8点、最高 伊藤 95点
  数学: 平均 71.5点、最高 鈴木 92点
  英語: 平均 73.7点、最高 高橋 91点

=== クラスごとの平均点 ===
  A組: 77.1点
  B組: 69.6点

=== 合計点の順位（上位3人） ===
  1位 S005 伊藤 252点
  2位 S003 高橋 226点
  3位 S001 佐藤 225点

=== 科目ごとの合格者数（60点以上） ===
  国語: 合格 5人、不合格 1人
  数学: 合格 4人、不合格 2人
  英語: 合格 5人、不合格 1人

=== 不合格の科目がある学生 ===
  S002 鈴木: [国語]
  S003 高橋: [数学]
  S004 田中: [数学, 英語]

=== 条件の確認 ===
  全科目合格の学生がいる: true
  全員が全科目40点以上: true
  全員が全科目50点以上: false

=== 学籍番号で合計点を探す ===
  S003: 226点
  S010: 該当する学生はいません

=== 全点数の合計（順次処理と並列処理） ===
  順次処理: 1320
  並列処理: 1320
```

## コンパイルと実行

```bash
javac *.java
java GradeReportApp
```

## 実装のヒント

```java
// 1行 → 複数のScore
public static Stream<Score> parseLine(String line) {
    String[] parts = line.split(",");
    return Arrays.stream(parts[3].split(";"))   // "国語:78" "数学:85" "英語:62"
            .map(item -> item.split(":"))
            .map(kv -> new Score(parts[0], parts[1], parts[2], kv[0], Integer.parseInt(kv[1])));
}

// すべての行 → List<Score>。mapではStream<Stream<Score>>になるのでflatMapを使う
List<Score> scores = lines.stream().flatMap(ScoreParser::parseLine).toList();

// 科目ごとに、合格と不合格の人数
Map<String, Map<Boolean, Long>> counts = scores.stream()
        .collect(Collectors.groupingBy(Score::subject, TreeMap::new,
                Collectors.partitioningBy(s -> s.point() >= 60, Collectors.counting())));
```

- `maxBy`は、グループが空のときに備えて`Optional`を返す。表示するときは`map`で文字列にし、`orElse`で「なし」を補う
- 学生ごとの集計のキーは「学籍番号 氏名」の文字列にすると、`TreeMap`で学籍番号の順に並び、表示にもそのまま使える
- `anyMatch`のラムダ式の中で毎回`failedSubjects()`を呼ぶと、学生の数だけ集計をやり直す。先に1回求めて変数に入れておく
- 並列ストリームで合計を求めるときは、外の変数に足し込まず、`mapToInt(...).sum()`のように終端操作で求める。外の変数を書き換えると、結果が実行のたびに変わることがある
- データの形は決まっているものとして、数値に変換できない場合の例外処理は書かなくてよい（例外処理は第13章で学ぶ）

## 応用のポイント

1. データの形を変えてから集計する
    - 1行に複数の値が入ったデータを、`flatMap`で「1件1値」の形にそろえると、どの集計も同じ`List<Score>`から書ける
2. Collectorの組み合わせ
    - `groupingBy`の中に`partitioningBy`を入れるように、Collectorを入れ子にすると「科目ごとに、合否ごとに、数える」を1回の`collect`で書ける
3. 並列処理を使ってよい処理
    - 合計のように、要素どうしが関係せず順序にも左右されない処理は、`parallelStream()`にしても同じ結果になる。データが少ないと並列化の準備のほうが重くなるので、速くなるとは限らない

## 発展（任意）

- 科目ごとの順位表（その科目の点数の高い順）を作るメソッドを加える
- クラスごとに、科目ごとの平均点を求める（`groupingBy`を2段に入れ子にする）
- 偏差値（(点数 − 平均) ÷ 標準偏差 × 10 + 50）を科目ごとに求めて表示する

## 完了チェックリスト

- [ ] `flatMap`で、1行のデータを複数の`Score`に分けている
- [ ] 平均・合計・最高点・合否の人数を、Collectorsの組み合わせで求めている
- [ ] 集計の結果を`TreeMap`にして、表示の順が毎回同じになっている
- [ ] `maxBy`や`findFirst`の`Optional`を、`get()`を使わずに扱っている
- [ ] 並列ストリームで、外の変数を書き換えずに合計を求めている

**次のステップ**: チャレンジ課題が完了したら、第13章「例外処理」に進みましょう。
