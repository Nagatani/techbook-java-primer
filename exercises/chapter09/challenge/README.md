# 第9章 チャレンジ課題

## 学習目標
- List・Set・Mapとその実装クラスを、データの使い方に合わせて選び分ける
- 値がコレクションのMap（`Map<String, TreeSet<Integer>>`）で、1つのキーに複数の値を対応づける
- `retainAll`・`addAll`・`removeAll`で積集合・和集合・差集合を求める
- `TreeMap`の並び順を使って、回数の多い順に取り出す
- `entrySet()`の`Iterator`で、Mapを走査しながら要素を取り除く

## 課題一覧

### 課題1: 文章の索引と検索
**ファイル**（このフォルダーのファイルは解答例です）:
```
challenge/
├── TextIndex.java     文章の索引
└── TextIndexApp.java  mainメソッド
```

本の巻末にある索引のように、単語ごとにその単語が出てくる行の番号をまとめるプログラムを作成してください。
索引を使って、「両方の単語を含む行」「どちらかを含む行」「一方を含み他方を含まない行」を検索します。
文章は英語の短い文の配列をmainメソッドに書いておきます。

**要求仕様**:
- 単語の取り出し
    - 小文字にし、ピリオドとカンマを除き、空白で区切る
    - 「a」「the」のように、どの行にも出てくる語（ストップワード）は索引に入れない。ストップワードは`HashSet`で持つ
- `TextIndex`（索引）が持つデータと、選ぶコレクション
    - 行: 番号で取り出すので`ArrayList`
    - 索引: 単語から行番号を引き、辞書順に表示するので`TreeMap`。行番号は重複なしで小さい順に並べるので`TreeSet`
    - 単語の出てきた回数: 単語から引くだけなので`HashMap`。同じ行に2回出てくれば2回と数える
    - 出てきた順の単語: 重複なしで、出てきた順を保つので`LinkedHashSet`
- 検索
    - 単語が出てくる行（索引にない単語なら空の集合）
    - すべての単語を含む行（積集合）、どれかを含む行（和集合）、ある単語を含み別の単語を含まない行（差集合）
    - 検索の結果を作るとき、索引の中の集合そのものを書き換えない（コピーしてから集合演算をする）
- 集計と整理
    - 出てきた回数をキー、その回数の単語の集合を値にした`TreeMap`を作り、回数の多い順に2回以上の単語を表示する
    - 出てきた回数が指定より少ない単語を索引から取り除き、取り除いた数を返す

**実行例**:
```
=== 文章 ===
  1: Java is a programming language.
  2: A list keeps elements in order.
  3: A set does not keep duplicate elements.
  4: A map connects a key to a value.
  5: Java collections include list, set, and map.
  6: Choose a list when the order of elements matters, and choose a set when it does not.

=== 索引（辞書順） ===
  choose [6]
  collections [5]
  connects [4]
  duplicate [3]
  elements [2, 3, 6]
  include [5]
  java [1, 5]
  keep [3]
  keeps [2]
  key [4]
  language [1]
  list [2, 5, 6]
  map [4, 5]
  matters [6]
  order [2, 6]
  programming [1]
  set [3, 5, 6]
  value [4]

=== 出てきた順の単語 ===
  [java, programming, language, list, keeps, elements, order, set, keep, duplicate, map, connects, key, value, collections, include, choose, matters]

=== よく出てくる単語 ===
  3回: [elements, list, set]
  2回: [choose, java, map, order]

=== 検索 ===
java と list の両方: 1行
  5: Java collections include list, set, and map.
list か map: 4行
  2: A list keeps elements in order.
  4: A map connects a key to a value.
  5: Java collections include list, set, and map.
  6: Choose a list when the order of elements matters, and choose a set when it does not.
elements を含み set を含まない: 1行
  2: A list keeps elements in order.
queue: 0行

=== 1回しか出てこない単語を索引から除く ===
  11語を除きました
  choose [6]
  elements [2, 3, 6]
  java [1, 5]
  list [2, 5, 6]
  map [4, 5]
  order [2, 6]
  set [3, 5, 6]
```

「choose」は6行目に2回出てくるので、行は`[6]`だけですが回数は2回です。
そのため、最後の整理でも索引に残ります。

## コンパイルと実行

```bash
javac *.java
java TextIndexApp
```

## 実装のヒント

```java
// 1つの単語に複数の行番号を対応づける
TreeSet<Integer> numbers = index.get(word);
if (numbers == null) {
    numbers = new TreeSet<>();
    index.put(word, numbers);
}
numbers.add(lineNumber);  // 同じ行に2回出ても、行番号は1つだけ

// 回数を数える
counts.put(word, counts.getOrDefault(word, 0) + 1);

// 積集合: コピーを作ってからretainAllする
Set<Integer> result = new TreeSet<>(index.get("java"));
result.retainAll(index.get("list"));
```

- `retainAll`などは呼び出した集合そのものを書き換える。索引の`TreeSet`をそのまま使うと索引が壊れるので、`new TreeSet<>(元の集合)`でコピーする
- `TreeMap`の`descendingMap()`は、キーの大きい順に並んだMapを返す。回数をキーにすれば、多い順に取り出せる
- Mapを走査しながら取り除くには、`index.entrySet().iterator()`で`Iterator`を作り、`remove`を呼ぶ
- `String`の`split(" ")`で空白ごとに区切れる。空の文字列が混じったら飛ばす
- 回数で並べ替える`Comparator`やStream APIは第11章・第12章で学ぶ。ここでは回数をキーにした`TreeMap`で並べる

## 応用のポイント

1. データの使い方からコレクションを選ぶ
    - 同じ「単語の集まり」でも、引く・数える・順に並べる・出てきた順を覚えるといった使い方ごとに、適した実装が違う
2. 1対多の対応
    - 値をコレクションにしたMapで、1つのキー（単語）に複数の値（行番号）を対応づけられる
3. 集合演算
    - 「両方」「どれか」「〜を除く」は、Setの`retainAll`・`addAll`・`removeAll`で短く書ける

## 発展（任意）

- 検索語の一部だけで探す（例: 「coll」で始まる単語の行）。`TreeMap`の`subMap`や`tailMap`を調べて使う
- 「collection」と「collections」を同じ単語として扱う（末尾のsを取る）と、索引と検索の結果がどう変わるかを確かめる

## 完了チェックリスト

- [ ] 索引が単語の辞書順に並び、行番号が重複なしで小さい順に並んでいる
- [ ] ストップワードが索引に入っていない
- [ ] 積集合・和集合・差集合の検索ができ、検索のあとも索引が変わっていない
- [ ] 回数の多い順に単語を表示できる
- [ ] Mapを走査しながら、`Iterator`の`remove`で単語を取り除いている

**次のステップ**: チャレンジ課題が完了したら、第10章「ジェネリクス」に進みましょう。
