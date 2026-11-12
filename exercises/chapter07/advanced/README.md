# 第7章 発展課題

## 学習目標
- 抽象クラスに処理の手順を1回だけ書き、違う部分を抽象メソッドとして子クラスに任せる（テンプレートメソッドパターン）
- 子クラスが必要なときだけオーバーライドするフックメソッドを用意する
- 継承関係のないクラスに、インターフェイスで共通の能力（can-do）を持たせる
- インターフェイスのdefaultメソッド・staticメソッド・定数を使い分ける
- 同じdefaultメソッドを持つ2つのインターフェイスを実装したときに、`インターフェイス名.super.メソッド名()`で解決する

## 課題一覧

### 課題1: 成績の付け方の切り替え
**ファイル**（このフォルダーのファイルは解答例です）:
```
advanced/task1-grading/
├── GradingPolicy.java       成績の付け方（抽象クラス）
├── LetterGrading.java       S・A・B・C・Dの5段階評価
├── PassFailGrading.java     合格・不合格の2段階評価
├── BonusLetterGrading.java  課題点を加算する5段階評価
└── GradingApp.java          mainメソッド
```

同じ点数の一覧から、付け方の違う成績表を作るプログラムを作成してください。

**要求仕様**:
- `GradingPolicy`（抽象クラス）
    - 付け方の名前を持つ
    - 点数から評価の文字列を返す`evaluate(int score)`と、合格かどうかを返す`isPassing(int score)`を抽象メソッドにする
    - 評価の前に点数を調整する`adjust(int score)`を、何もせずに点数を返すメソッドとして用意する（フックメソッド）
    - 成績表を表示する`report(String[] names, int[] scores)`をこのクラスに1回だけ書き、子クラスで変えられないよう`final`にする。各学生の行には、合格なら「○」、不合格なら「×」を付け、最後に合格者の数を表示する
- 子クラス
    - `LetterGrading`: 90点以上S、80点以上A、70点以上B、60点以上C、それ未満D。60点以上で合格
    - `PassFailGrading`: 合格点をコンストラクターで受け取り、評価は「合格」か「不合格」
    - `BonusLetterGrading`: `LetterGrading`を継承し、`adjust`だけをオーバーライドして課題点を加える（上限100点）
- mainでは、3つの付け方を`GradingPolicy`型の配列に入れ、同じ点数の一覧で成績表を表示する

**実行例**:
```
--- 5段階評価 ---
  ○ 青木 92点 S
  ○ 伊藤 78点 B
  ○ 上田 64点 C
  × 江口 55点 D
  ○ 小野 97点 S
  合格者: 4人 / 5人

--- 合否判定（70点以上で合格） ---
  ○ 青木 92点 合格
  ○ 伊藤 78点 合格
  × 上田 64点 不合格
  × 江口 55点 不合格
  ○ 小野 97点 合格
  合格者: 3人 / 5人

--- 5段階評価（課題点8点を加算、上限100点） ---
  ○ 青木 92→100点 S
  ○ 伊藤 78→86点 A
  ○ 上田 64→72点 B
  ○ 江口 55→63点 C
  ○ 小野 97→100点 S
  合格者: 5人 / 5人

5段階評価で85点はA
```

### 課題2: レンタルと販売の店
**ファイル**（このフォルダーのファイルは解答例です）:
```
advanced/task2-rental-shop/
├── Rentable.java   レンタルできる（インターフェイス）
├── Sellable.java   販売できる（インターフェイス）
├── Dvd.java        DVD（レンタルのみ）
├── Book.java       本（販売のみ）
├── GameSoft.java   ゲームソフト（レンタルも販売もできる）
└── ShopApp.java    mainメソッド
```

DVD・本・ゲームソフトを扱う店で、レンタル料金と販売価格を求めるプログラムを作成してください。
3つの商品クラスには共通の親クラスを作らず、「レンタルできる」「販売できる」という能力をインターフェイスで表します。

**要求仕様**:
- `Rentable`（インターフェイス）
    - 定数`MAX_DAYS`（一度に借りられる最長の日数、7日）
    - 抽象メソッド`getTitle()`と`dailyFee()`（1日あたりの料金）
    - defaultメソッド`canRent(int days)`（1日以上`MAX_DAYS`以下なら借りられる）、`rentalFee(int days)`（借りられない日数なら-1）、`describe()`
- `Sellable`（インターフェイス）
    - 定数`TAX_RATE`（10%）
    - 抽象メソッド`getTitle()`と`price()`（税抜きの価格）
    - staticメソッド`withTax(int price)`（税込みの金額、1円未満は切り捨て）
    - defaultメソッド`priceWithTax()`（`withTax`を使う）と`describe()`
- 商品クラス
    - `Dvd`: `Rentable`を実装する。新作は1日300円で2日まで、旧作は1日100円。新作の条件は`canRent`をオーバーライドし、`Rentable.super.canRent(days)`に条件を足して書く
    - `Book`: `Sellable`を実装する
    - `GameSoft`: 両方を実装する。1日の料金は販売価格の5%（10円未満は切り捨て）。`describe()`は両方のインターフェイスにあるので、オーバーライドして両方の結果をつなげる
- mainでは、同じ`GameSoft`のオブジェクトを`Rentable`型の配列と`Sellable`型の配列の両方に入れ、それぞれ種類を調べずに料金や価格を表示する

**実行例**:
```
=== 商品の説明 ===
宇宙の旅2: レンタル 1日300円
深海の世界: レンタル 1日100円
Java入門: 販売 3080円（税込）
パズル王国: レンタル 1日290円 / 販売 6578円（税込）

=== 3日間借りたときの料金 ===
  宇宙の旅2 3日間は借りられません
  深海の世界 300円
  パズル王国 870円

=== 2日間借りたときの料金 ===
  宇宙の旅2 600円
  深海の世界 200円
  パズル王国 580円

=== 販売価格の一覧 ===
  Java入門 2800円 → 税込 3080円
  パズル王国 5980円 → 税込 6578円
  まとめて買うと 税込 9658円

一度に借りられるのは最長7日です
```

## コンパイルと実行

```bash
cd task1-grading
javac *.java
java GradingApp

cd ../task2-rental-shop
javac *.java
java ShopApp
```

## 実装のヒント

```java
public abstract class GradingPolicy {
    public abstract String evaluate(int score);  // 本体を書かない

    protected int adjust(int score) {            // フック: 標準では何もしない
        return score;
    }

    public final void report(String[] names, int[] scores) {
        // adjust → evaluate → isPassing の順に呼ぶ手順は、ここにだけ書く
    }
}

public class GameSoft implements Rentable, Sellable {
    @Override
    public String describe() {
        return Rentable.super.describe() + " / " + Sellable.super.describe();
    }
}
```

- 抽象クラスは`new`できない。子クラスは抽象メソッドをすべて実装しないと、子クラスも抽象クラスにしなければならない
- `report`の中で呼んだ`evaluate`や`adjust`は、オブジェクトの実際のクラスのメソッドになる。手順は親、中身は子という分担になる
- インターフェイスに書いたフィールドは、何も付けなくても`public static final`の定数になる
- 2つのインターフェイスに同じ抽象メソッド（`getTitle()`）があるときは、実装を1つ書けば両方を満たす。同じdefaultメソッドがあるときは、どちらを使うかをコンパイラーが決められないので、オーバーライドが必要になる
- staticメソッドはオブジェクトではなく`Sellable.withTax(total)`のようにインターフェイス名で呼ぶ

## 応用のポイント

1. 抽象クラスを選ぶ場面
    - 成績の付け方は「付け方の一種」というis-a関係で、名前のフィールドと成績表の手順を共有したいので抽象クラスにした
2. インターフェイスを選ぶ場面
    - DVDと本には共通の親がなく、「レンタルできる」「販売できる」という能力だけを共有したいのでインターフェイスにした
3. defaultメソッドの役割
    - 料金の計算をインターフェイスに書いておくと、実装クラスは1日の料金や価格を返すだけで済む

## 発展（任意）

- 課題1に、平均点との差で評価を決める`RelativeGrading`を加える。`report`を変えずに追加できることを確かめる
- 課題2に、販売価格から割引する`Sellable`のdefaultメソッド（例: 中古品は30%引き）を加え、`Book`と`GameSoft`の両方で使えることを確かめる

## 完了チェックリスト

- [ ] `GradingPolicy`は`abstract`で、`evaluate`と`isPassing`が抽象メソッドになっている
- [ ] 成績表を表示する手順が`GradingPolicy`の1か所にだけ書かれている
- [ ] `BonusLetterGrading`はフックメソッドだけをオーバーライドしている
- [ ] `Dvd`・`Book`・`GameSoft`に共通の親クラスがなく、インターフェイスで能力を表している
- [ ] `GameSoft`の`describe()`で`Rentable.super`と`Sellable.super`を使っている
- [ ] staticメソッドをインターフェイス名で呼んでいる

**次のステップ**: 発展課題が完了したら、challenge/のチャレンジ課題で、抽象クラス・インターフェイス・ファクトリを組み合わせたプログラムに挑戦しましょう。
