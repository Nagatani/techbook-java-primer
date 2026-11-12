# 第12章 発展課題

## 学習目標
- `Collectors.groupingBy`に2つ目のCollector（`counting`・`mapping`・`collectingAndThen`）を渡して、グループごとに集計する
- `partitioningBy`で、条件に合うものと合わないものに分ける
- `Map`の`entrySet().stream()`で、集計の結果をさらに並べ替えて絞り込む
- 見つからないかもしれない値を`Optional`で返し、`map`・`flatMap`・`filter`・`orElse`・`ifPresentOrElse`で扱う
- `Optional::stream`と`flatMap`で、見つかった値だけを集める

## 課題一覧

### 課題1: 図書館の貸出記録の集計
**ファイル**（このフォルダーのファイルは解答例です）:
```
advanced/task1-loan-stats/
├── Loan.java          貸出記録（レコード）
├── LoanData.java      貸出記録の例
├── LoanStats.java     集計のメソッド
└── LoanStatsApp.java  mainメソッド
```

図書館の貸出記録（会員名・書名・ジャンル・貸し出した月）のリストから、いろいろな集計を求めるプログラムを作成してください。
集計はすべてStream APIで書き、`for`文で数を数える処理を書かないようにします。

**要求仕様**:
- `LoanStats`に、次の集計のメソッドを作る
    - `countByGenre()`: ジャンルごとの貸出件数（`groupingBy`と`counting`）
    - `topTitles(int n)`: よく借りられた本の上位n冊。件数の多い順、同じ件数なら書名の順
    - `membersByMonth()`: 月ごとの利用者数。同じ人が何冊借りても1人と数える（`mapping`で会員名の`Set`を作り、`collectingAndThen`で大きさにする）
    - `genresByMember()`: 会員ごとに借りたジャンルの一覧。重複をなくし、名前の順に並べる（`toCollection(TreeSet::new)`）
    - `membersWhoBorrowed(String genre)`: 指定したジャンルを1回でも借りた会員（`filter`・`map`・`distinct`・`sorted`）
    - `splitByCount(int min)`: 貸出回数がmin回以上の会員と、それ未満の会員に分ける（`partitioningBy`）
    - `mostActiveMember()`: 最も多く借りた会員。記録が空なら「なし」を返す（`max`が返す`Optional`を`map`と`orElse`で文字列にする）
- 結果の`Map`は`TreeMap`にして、表示の順が毎回同じになるようにする

**実行例**:
```
貸出記録: 13件

=== ジャンルごとの貸出件数 ===
  小説: 5件
  技術書: 5件
  歴史: 3件

=== よく借りられた本（上位3冊） ===
  1位 Java入門（4回）
  2位 坊っちゃん（3回）
  3位 こころ（2回）

=== 月ごとの利用者数 ===
  4月: 3人
  5月: 4人
  6月: 3人

=== 会員ごとに借りたジャンル ===
  伊藤: [小説]
  佐藤: [小説, 技術書, 歴史]
  田中: [技術書]
  鈴木: [小説, 技術書]
  高橋: [小説, 歴史]

=== 小説を借りた会員 ===
  [伊藤, 佐藤, 鈴木, 高橋]

=== 貸出回数で分ける（3回以上） ===
  3回以上: [佐藤, 鈴木]
  3回未満: [伊藤, 田中, 高橋]

=== 最も多く借りた会員 ===
  佐藤さん（5回）
  記録がない場合: なし
```

名前の順は文字コードの順なので、五十音順にはなりません（「伊藤」「佐藤」「田中」「鈴木」「高橋」の順）。

### 課題2: 会員情報の検索
**ファイル**（このフォルダーのファイルは解答例です）:
```
advanced/task2-member-search/
├── Member.java           会員（メールアドレスは未登録のこともある）
├── MemberDirectory.java  会員名簿（検索の結果をOptionalで返す）
└── MemberSearchApp.java  mainメソッド
```

店の会員名簿を検索するプログラムを作成してください。
会員番号が見つからないこと、メールアドレスが未登録であること、クーポンの番号が存在しないことを、`null`ではなく`Optional`で表します。

**要求仕様**:
- `Member`（会員）
    - 会員番号・氏名・メールアドレス・ポイントを持つ。メールアドレスは未登録なら`null`のまま持つ
    - `getEmail()`は`Optional<String>`を返す（フィールドを`Optional`にしない）
- `MemberDirectory`（会員名簿）
    - `findById(String id)`と`findByName(String part)`は、`filter`と`findFirst`で探し、`Optional<Member>`を返す
    - `emailOf(String id)`は、会員がいない場合も未登録の場合も空の`Optional`を返す（`flatMap(Member::getEmail)`）
    - `couponDiscount(String code)`は、`Map.get`の結果を`Optional.ofNullable`で包んで返す
    - `topMember()`は、ポイントが最も多い会員を返す
- `MemberSearchApp`（mainメソッド）
    - 見つからない場合の表示を`orElse`で、見つかった場合と見つからない場合の処理を`ifPresentOrElse`で書く
    - 1000ポイント以上の会員かどうかを`filter`で判定する
    - 会員番号のリストから、見つかった会員の名前とメールアドレスだけを`flatMap(Optional::stream)`で集める
    - `isPresent()`と`get()`の組み合わせは使わない

**実行例**:
```
=== 会員番号で探す ===
  M001: 佐藤花子
  M009: 該当する会員はいません

=== メールアドレス ===
  M001: hanako@example.com
  M002: （なし）
  M009: （なし）

=== 名前で探す ===
  「花子」: M001 佐藤花子
  「次郎」: 見つかりません

=== ゴールド会員（1000ポイント以上）か ===
  M001: ゴールド会員
  M002: 一般または該当なし
  M009: 一般または該当なし

=== クーポン ===
  WELCOME: 300円引き → 1700円
  SUMMER: 0円引き → 2000円

=== まとめて探す（見つかった会員だけ） ===
  [高橋健太, 佐藤花子]

=== メールを送れる会員 ===
  [hanako@example.com, kenta@example.com]

=== ポイントが最も多い会員 ===
  高橋健太（2500ポイント）
```

## コンパイルと実行

課題ごとのフォルダーでコンパイルし、実行します。

```bash
cd task1-loan-stats
javac *.java
java LoanStatsApp

cd ../task2-member-search
javac *.java
java MemberSearchApp
```

## 実装のヒント

```java
// groupingBy(分類の関数, Mapを作る関数, グループごとの集計)
Map<Integer, Integer> members = loans.stream()
        .collect(Collectors.groupingBy(Loan::month, TreeMap::new,
                Collectors.collectingAndThen(
                        Collectors.mapping(Loan::member, Collectors.toSet()),  // 会員名の集合
                        Set::size)));                                         // その大きさ

// 見つからないかもしれない値を、つないで処理する
String email = directory.findById(id)      // Optional<Member>
        .flatMap(Member::getEmail)          // Optional<String>（getEmailもOptionalを返す）
        .orElse("（なし）");
```

- `groupingBy`の結果を件数の多い順に並べるには、`entrySet().stream()`を`Map.Entry.comparingByValue()`で並べる。降順にして同じ件数を書名の順にするときは、`Map.Entry.<String, Long>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey())`と型引数を明示する（第11章で学んだ`reversed`と`thenComparing`）
- `partitioningBy`の結果は`Map<Boolean, ...>`で、`get(true)`と`get(false)`で取り出す。該当がなくても空のリストが入る
- `map`に渡す関数が`Optional`を返すときは`flatMap`を使う。`map`を使うと`Optional<Optional<String>>`になる
- `Optional::stream`は、値があれば要素1つ、なければ空のストリームを返す。`flatMap`に渡すと、空の結果を飛ばして値だけを集められる
- `HashMap`で集計すると表示の順が決まらない。`groupingBy`の2つ目の引数に`TreeMap::new`を渡す

## 応用のポイント

1. 集計を組み合わせる
    - `groupingBy`の2つ目の引数に別のCollectorを渡すと、「グループごとに数える」「グループごとに集合にする」などを1回の`collect`で書ける
2. 集計の結果をもう一度ストリームにする
    - 件数の`Map`を`entrySet().stream()`で並べ替えると、「上位n件」のような二段階の集計を書ける
3. 値がないことを型で表す
    - `Optional`を返すメソッドは、呼び出し側に「ない場合」の扱いを書かせる。`null`の確認を忘れて`NullPointerException`になることを防げる

## 発展（任意）

- 課題1に「月ごとに最もよく借りられたジャンル」を求めるメソッドを加える
- 課題1の会員名を五十音順に並べるには何が必要かを調べる（`java.text.Collator`）
- 課題2に、名前の一部に一致する会員をすべて返す`findAllByName(String part)`を加える。戻り値を`Optional<List<Member>>`ではなく`List<Member>`にするのはなぜかを考える

## 完了チェックリスト

- [ ] 課題1の集計を、`for`文ではなくStream APIとCollectorsで書いている
- [ ] `groupingBy`の結果を`TreeMap`にして、表示の順が毎回同じになっている
- [ ] `partitioningBy`で2つに分けている
- [ ] 課題2で、見つからない場合の処理を`orElse`や`ifPresentOrElse`で書き、`isPresent()`と`get()`を組み合わせていない
- [ ] `Optional`を返す関数を`map`ではなく`flatMap`でつないでいる
- [ ] フィールドや引数に`Optional`を使っていない

**次のステップ**: 発展課題が完了したら、challenge/のチャレンジ課題に挑戦しましょう。
