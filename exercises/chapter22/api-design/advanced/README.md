# 第22章 発展課題（APIドキュメントとライブラリ設計）

## 学習目標
- ライブラリを使う人の立場で、クラス・メソッド・パッケージにJavadocを書く
- `@param`・`@return`・`@throws`で、引数の条件・戻り値・例外の約束を書く
- `{@code}`・`{@link}`・`<pre>{@code ...}</pre>`で、コードの名前や使用例を書く
- `javadoc`コマンドの`-Xdoclint:all`で、書き漏れのないドキュメントを作る
- `@since`・`@deprecated`と`@Deprecated`で、APIの変更を利用者に伝える

## 課題一覧

### 課題1: 成績表ライブラリのAPIドキュメント
**ファイル**（このフォルダーのファイルは解答例です）:
```
advanced/task1-scorebook/
└── src/
    ├── scorebook/
    │   ├── package-info.java   パッケージの説明
    │   ├── ScoreBook.java      成績表（ライブラリの中心のクラス）
    │   └── ScoreEntry.java     1人の成績（レコード）
    └── demo/
        └── ScoreBookDemo.java  ライブラリを使う側のmainメソッド
```

科目の成績を記録して集計する小さなライブラリ`scorebook`を作り、Javadocを付けてください。
ライブラリを使う人はソースコードを読まず、生成したAPIドキュメントだけを読んで使うものとします。

**要求仕様**:
- `ScoreBook`（成績表）
    - コンストラクターで科目名を受け取る
    - `add(名前, 点数)`で登録し、登録した`ScoreEntry`を返す。同じ名前は登録できない
    - `size()`、`entries()`（登録した順の一覧）、`average()`（平均点）、`max()`（最高点の成績）、`countAtLeast(点数)`（その点数以上の人数）を持つ
    - `entries()`は変更できないリストを返す
    - 1人も登録されていないときの`average()`と`max()`は、どちらも`IllegalStateException`を投げる（メソッドごとに例外の種類を変えない）
- `ScoreEntry`（1人の成績）
    - 名前と点数を持つレコード。名前が空、点数が0〜100の範囲外なら`IllegalArgumentException`を投げる
- Javadoc
    - クラスの説明に、使用例を`<pre>{@code ...}</pre>`で書く
    - 公開するすべてのコンストラクター・メソッドに、説明・`@param`・`@return`・`@throws`を書く。例外はどんなときに投げるかまで書く
    - 引数の範囲（0〜100）、空のときの戻り値、戻り値のリストを変更できないこと、最高点が並んだときにどれを返すか、のように、使う人が迷う点を書く
    - レコードの各要素は、クラスの説明の`@param`で書く
    - `package-info.java`にパッケージの説明を書く
    - `javadoc -Xdoclint:all`で、警告もエラーも出ない

**実行例**（`ScoreBookDemo`の実行結果）:
```
科目: プログラミング演習（4人）
  青木 82点
  石井 67点
  上田 45点
  江藤 91点
平均点: 71.3
最高点: 江藤 91点
60点以上: 3人
登録できません: 石井はすでに登録されています
entries()の戻り値は変更できません
平均点を求められません: 成績が登録されていません
```

後半の3行は、Javadocの`@throws`と戻り値の説明に書いたとおりに例外が発生することを確かめています。

### 課題2: 非推奨にして新しいAPIへ移す
**ファイル**（このフォルダーのファイルは解答例です）:
```
advanced/task2-deprecation/
└── src/
    ├── price/
    │   ├── package-info.java     パッケージの説明と変更履歴
    │   ├── PriceCalculator.java  税込価格の計算
    │   └── TaxRate.java          税率（1.1で追加）
    └── demo/
        ├── ReceiptDemo.java      新しいAPIを使うプログラム
        └── OldClient.java        1.0のAPIを使っていたプログラム
```

税込価格を計算するライブラリ`price`の1.0には、次のメソッドしかありませんでした。

```java
public static double withTax(int price)   // 10%の税込価格を返す
```

このメソッドには、軽減税率（8%）を選べないことと、戻り値が`double`なので`242.00000000000003`のような端数が残ることの2つの問題があります。
1.0を使っているプログラムを壊さずに、1.1で新しいAPIへ移してください。

**要求仕様**:
- 1.1で加えるもの
    - 税率を表すenum`TaxRate`（`STANDARD`は10%、`REDUCED`は8%）
    - `withTax(int price, TaxRate rate)`（1円未満を切り捨てた`int`を返す）と、`taxOf(int price, TaxRate rate)`（消費税の額）
    - 加えたクラスとメソッドに`@since 1.1`を付ける
- 1.0のメソッドの扱い
    - 削除せず、動きも変えない（1.0の利用者のプログラムがそのまま動く）
    - `@Deprecated(since = "1.1", forRemoval = true)`を付ける
    - Javadocの`@deprecated`に、非推奨にした理由ではなく「代わりに何を使えばよいか」を`{@link}`で書く。削除する予定の版と、戻り値の型が変わることも書く
- `package-info.java`に変更履歴を書く
- `OldClient`は、`@SuppressWarnings("removal")`を外してコンパイルすると、非推奨の警告が出ることを確かめるためのプログラムにする

**実行例**（`ReceiptDemo`の実行結果）:
```
おにぎり 150円 → 162円（税率8%）
緑茶 128円 → 138円（税率8%）
ノート 220円 → 242円（税率10%）
ボールペン 110円 → 121円（税率10%）
合計 663円
緑茶の消費税: 10円
```

**実行例**（`OldClient`の実行結果）:
```
緑茶 128円 → 140.8円
ノート 220円 → 242.00000000000003円
```

`OldClient`から`@SuppressWarnings("removal")`の行を消してコンパイルすると、次の警告が出ます。

```
demo/OldClient.java:18: 警告: [removal] PriceCalculatorのwithTax(int)は推奨されておらず、削除用にマークされています
```

## コンパイルと実行

各課題のフォルダー（`task1-scorebook`など）で、次のように実行します。
`-d`で出力先を分けると、ソースのフォルダーに`.class`ファイルが散らばりません。

```bash
# 課題1
cd task1-scorebook
javac -d out src/scorebook/*.java src/demo/*.java
java -cp out demo.ScoreBookDemo

# APIドキュメントの生成（-Xdoclint:allで書き漏れを検査する）
javadoc -Xdoclint:all -d doc -encoding UTF-8 -docencoding UTF-8 -charset UTF-8 -sourcepath src scorebook
```

```bash
# 課題2
cd task2-deprecation
javac -d out src/price/*.java src/demo/*.java
java -cp out demo.ReceiptDemo
java -cp out demo.OldClient
javadoc -Xdoclint:all -d doc -encoding UTF-8 -docencoding UTF-8 -charset UTF-8 -sourcepath src price
```

生成した`doc/index.html`をブラウザーで開いて、使う人の立場で読み直します。
`javadoc`の最後の引数はパッケージ名です。
`demo`パッケージはライブラリではないので、ドキュメントに含めていません。

## 実装のヒント

```java
/**
 * 平均点を返します。
 *
 * @return 平均点
 * @throws IllegalStateException 1人も登録されていないとき
 */
public double average() { ... }
```

```java
/**
 * 標準税率（10%）で税込価格を計算します。
 *
 * @param price 税抜価格（0以上）
 * @return 税込価格（端数を含む）
 * @deprecated 1.1から非推奨です。2.0で削除する予定です。
 *             代わりに{@link #withTax(int, TaxRate)}に{@link TaxRate#STANDARD}を渡してください。
 */
@Deprecated(since = "1.1", forRemoval = true)
public static double withTax(int price) { ... }
```

- `-Xdoclint:all`は、説明のないメソッド、`@param`の書き忘れ、閉じていないHTMLタグなどを警告する。既定のコンストラクター（書かなかったコンストラクター）にも説明がないと警告されるので、公開するクラスにはコンストラクターを書いて説明を付ける。インスタンスを作らせないクラスは`private`のコンストラクターにする
- 使用例の中の`<`や`{`は、`<pre>{@code ...}</pre>`の中ならそのまま書ける
- クラスの説明の中の見出しは`<h2>`、メソッドの説明の中の見出しは`<h4>`以下にする（メソッドで`<h2>`を使うとエラーになる）
- `@Deprecated`はコンパイラーに、`@deprecated`（Javadocのタグ）は人に伝えるためのもの。両方を付ける
- 1.0のメソッドの中身は、新しいメソッドを呼ぶ形にしない。戻り値の型も計算の結果も変わり、1.0の利用者のプログラムの動きが変わってしまう
- 利用者のプログラム（`demo`）は、`private`のメンバーを使えないので、公開したAPIだけで書けるかを確かめられる

## 応用のポイント

1. ドキュメントは約束
    - `@throws`や「変更できないリストを返す」と書いたことは、利用者がそれを前提にプログラムを書く。後から変えると、利用者のプログラムが壊れる
2. 例外の種類をそろえる
    - 同じ「データがない」状況で、メソッドごとに違う例外を投げると、利用者が覚えることが増える。ドキュメントを書くと、こうした不ぞろいに気付きやすい
3. 非推奨は移行の期間をつくる
    - いきなり削除せず、1.1で非推奨にして代わりを示し、2.0で削除する。セマンティックバージョニングでは、互換性のない変更はメジャー版（1→2）を上げるときに行う

## 発展（任意）

- `javadoc`に`-link https://docs.oracle.com/en/java/javase/25/docs/api/`を付けて、`List`や`IllegalStateException`をJDKのドキュメントへのリンクにする
- 課題1のライブラリに、点数の分布（10点ごとの人数）を返すメソッドを`@since 1.1`で加える。戻り値の形（配列、`Map`など）をどう説明するかを考える
- 課題2を第22章で学んだMavenのプロジェクトにし、`mvn javadoc:javadoc`でドキュメントを生成する

## 完了チェックリスト

- [ ] `javadoc -Xdoclint:all`で、警告もエラーも出ない
- [ ] すべての`@throws`に、どんなときに投げるかが書かれている
- [ ] 生成した`doc/index.html`だけを読んで、ライブラリを使うプログラムが書ける
- [ ] 1.0のメソッドを消さずに非推奨にし、代わりのメソッドへのリンクを書いた
- [ ] 非推奨のメソッドを呼ぶと、コンパイル時に警告が出る

**次のステップ**: 発展課題が完了したら、チャレンジ課題に挑戦しましょう。
