# 第8章 チャレンジ課題（レコード）

## 学習目標
- sealedインターフェイスで、取り得る種類を限ったデータを表す
- sealedインターフェイスを実装するレコードと、レコードパターンを使った`switch`で種類ごとの処理を書く
- `default`のない`switch`で、すべての種類を扱ったことをコンパイラーに確かめさせる
- 列挙型とレコードを組み合わせる
- ほかのクラスと一緒にしか使わない値の組を、入れ子のレコードにする

## 課題一覧

### 課題1: 銀行口座の通帳
**ファイル**（このフォルダーのファイルは解答例です）:
```
challenge/
├── Transaction.java  取引（sealedインターフェイス）
├── Deposit.java      入金（レコード）
├── Withdrawal.java   出金（レコード）
├── Transfer.java     振込（レコード）
├── Channel.java      取引をした窓口（列挙型）
├── Fees.java         手数料・残高の増減・摘要を求めるswitch
├── Passbook.java     通帳（1行を表す入れ子のレコードEntryを含む）
└── BankApp.java      mainメソッド
```

入金・出金・振込の取引を順に記帳し、手数料と残高を計算するプログラムを作成してください。
日付は「1日」「25日」のような日の番号（整数）で扱います。

**要求仕様**:
- `Transaction`（sealedインターフェイス）
    - `permits`で`Deposit`・`Withdrawal`・`Transfer`の3つだけに実装を許す
    - 日を返す`day()`と金額を返す`amount()`を持つ
- `Deposit`・`Withdrawal`・`Transfer`（レコード）
    - 入金と出金の成分は日・金額・窓口、振込は日・金額・振込先・窓口
    - コンパクトコンストラクターで、金額が1円以上であること（振込は振込先があること）を確かめる
    - 成分の`day`と`amount`のアクセサーが、そのまま`Transaction`のメソッドの実装になる
- `Channel`（列挙型）
    - ATM・窓口・ネットの3つ。出金の手数料（110円・0円・0円）と振込の手数料（220円・660円・110円）を持つ
- `Fees`
    - `fee(Transaction t)`: 入金は0円、出金は窓口の出金手数料、振込は窓口の振込手数料。30,000円以上の振込はさらに220円を足す
    - `change(Transaction t)`: 残高の増減（入金はプラス、出金と振込はマイナス）
    - `describe(Transaction t)`: 「入金（窓口）」「振込 授業料（窓口）」のような摘要
    - 3つとも、レコードパターンを使った`switch`で書き、`default`を書かない
- `Passbook`（通帳）
    - 通帳の1行を、入れ子のレコード`Entry`（取引・手数料・記帳後の残高・受け付けたかどうか）で表し、配列で持つ
    - 取引を記帳するとき、手数料を含めて残高が足りなければ取引を受け付けず、取り消しとして記録する
    - 通帳の表示と、種類ごとの件数・手数料の合計の表示をする。件数を数える`switch`も`default`を書かない

**実行例**:
```
=== 取引のレコード ===
Deposit[day=1, amount=200000, channel=COUNTER]
Transfer[day=5, amount=65000, toAccount=家賃 大家 一郎, channel=ONLINE]
同じ内容の取引か: true

=== 手数料の確認 ===
ATMの振込 8,000円: 220円
ネットの振込 65,000円: 330円（30,000円以上）

=== 通帳 ===
山田 花子 様
  1日 入金（窓口） 200,000円 → 残高 200,000円
  3日 出金（ATM） 20,000円 手数料 110円 → 残高 179,890円
  5日 振込 家賃 大家 一郎（ネット） 65,000円 手数料 330円 → 残高 114,560円
  10日 振込 サークル会費（ATM） 8,000円 手数料 220円 → 残高 106,340円
  12日 出金（窓口） 30,000円 → 残高 76,340円
  20日 振込 授業料（窓口） 50,000円 手数料 880円 → 残高 25,460円
  25日 出金（ATM） 30,000円 → 残高不足のため取り消し
  25日 入金（ネット） 80,000円 → 残高 105,460円
  28日 出金（ATM） 10,000円 手数料 110円 → 残高 95,350円

=== 集計 ===
  入金 2件、出金 3件、振込 3件、取り消し 1件
  支払った手数料 1650円
  最終残高 95,350円
```

25日の1回目の出金は、残高25,460円に対して30,000円と手数料110円が必要なので取り消されます。

## コンパイルと実行

```bash
javac *.java
java BankApp
```

## 実装のヒント

```java
public sealed interface Transaction permits Deposit, Withdrawal, Transfer {
    int day();
    int amount();
}

public record Deposit(int day, int amount, Channel channel) implements Transaction {
    // ...
}

public static int fee(Transaction t) {
    return switch (t) {
        case Deposit d -> 0;
        case Withdrawal(int day, int amount, Channel channel) -> channel.getWithdrawalFee();
        case Transfer(int day, int amount, String to, Channel channel) ->
                amount >= LARGE_TRANSFER ? channel.getTransferFee() + 220 : channel.getTransferFee();
        // Transactionはsealedなので、defaultは要らない
    };
}
```

- レコードはインターフェイスを実装できる。成分と同じ名前・型のアクセサーが自動で作られるので、`day()`と`amount()`を改めて書く必要はない
- `case Withdrawal(int day, int amount, Channel channel)`のようなレコードパターンは、型を確かめたうえで成分を変数に取り出す。成分を使わないときは`case Deposit d`のような型のパターンでもよい
- 振込の手数料は、本文で学んだガード（`when`）を使って、金額で2つのcaseに分けても書ける。ガードの付いたcaseを先に書く

```java
case Transfer(int day, int amount, String to, Channel channel) when amount >= LARGE_TRANSFER ->
        channel.getTransferFee() + 220;
case Transfer(int day, int amount, String to, Channel channel) -> channel.getTransferFee();
```

- sealedインターフェイスの`switch`で`default`を書かずにおくと、取引の種類を足したときに、caseの足りない`switch`をコンパイラーがエラーで知らせてくれる
- クラスの中に宣言したレコード（`Passbook.Entry`）は、暗黙に`static`になる。外からは`Passbook.Entry`という名前で使う
- 列挙型の値（窓口ごとの手数料）とレコードの成分（取引の内容）を組み合わせると、手数料の表を`if`の連続で書かずに済む

## 応用のポイント

1. データと処理を分ける
    - 取引のレコードはデータだけを持ち、手数料や摘要の計算は`Fees`の`switch`にまとめた（本文のデータ指向プログラミング）
2. 種類を限る
    - `sealed`で取引の種類を3つに限ったので、`switch`がすべての種類を扱っているかをコンパイラーが確かめられる
3. 列挙型とレコードの組み合わせ
    - 種類の決まった選択肢（窓口）は列挙型、取引ごとに違う値の組はレコードにした

## 発展（任意）

- 取引の種類に「利息」（日・金額だけを持ち、手数料は0円）を加える。`permits`に加えたあと、どの`switch`がコンパイルエラーになるかを確かめてから直す
- 窓口ごとの手数料の合計を、`EnumMap<Channel, Integer>`で集計する

## 完了チェックリスト

- [ ] `Transaction`がsealedインターフェイスで、3つのレコードだけが実装している
- [ ] 各レコードのコンパクトコンストラクターで金額を確かめている
- [ ] `Fees`の3つのメソッドが、レコードパターンを使った`default`のない`switch`で書かれている
- [ ] 残高が足りない取引を取り消しとして記録している
- [ ] 通帳の1行が入れ子のレコード`Entry`になっている

**次のステップ**: チャレンジ課題が完了したら、enums/の列挙型の課題に取り組むか、第9章「コレクションフレームワーク」に進みましょう。
