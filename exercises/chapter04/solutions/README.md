# 第4章 クラスとインスタンス - 解答例

[基礎課題](../basic/)の解答例です。
基礎課題の雛形の`TODO`を埋めた形で書いてあり、第4章までに学んだ機能だけを使っています。

## 解答例一覧

| 課題 | ファイル | 主な内容 |
|------|----------|----------|
| 課題1: Employeeクラスのカプセル化 | `Employee.java`、`EmployeeTest.java` | `private`のフィールド、検査つきのsetter、コンストラクターからsetterを呼ぶ |
| 課題2: SecureBankAccountクラス | `SecureBankAccount.java`、`SecureBankAccountTest.java` | PINを確かめる`private`メソッド、PINが正しいときだけ残高を変える |
| 課題3: Productクラス | `Product.java`、`ProductTest.java` | 在庫の入荷・出荷、在庫を超える出荷を断る、税込価格 |
| 課題4: Gameクラス | `Game.java`、`GameTest.java` | `boolean`のフィールドで状態を持つ、`private`メソッドでレベルアップを判定する |

各`〜Test.java`の`main`の出力は、基礎課題のREADMEにある実行例と同じです。

## 実行方法

```bash
cd exercises/chapter04/solutions
javac *.java
java EmployeeTest
java GameTest
```

## 実装のポイント

- `Employee`の検査は`private`のメソッド`isValidAge`・`isValidSalary`にまとめ、コンストラクターとsetterの両方から使う。コンストラクターからsetterを呼ぶと、オブジェクトを作っただけで「年齢を30歳に変更しました。」と表示されてしまう
- コンストラクターの中でオーバーライドできるメソッド（`public`のsetter）を呼ぶ書き方は、第5章で学ぶ継承でサブクラスを作ったときにも問題になる（サブクラスの準備ができる前に、サブクラスのメソッドが呼ばれることがある）。`private`のメソッドならオーバーライドされない
- `SecureBankAccount`には、PINを返すgetterも、残高を直接変えるsetterもない。外から残高を変える方法を、PINを確かめる`deposit`・`withdraw`だけにしている
- PINの比較は`this.pin.equals(inputPin)`。`==`は同じ文字列のオブジェクトかどうかを比べるので、内容が同じでも`false`になることがある
- `getBalance`は、認証に失敗したら-1を返す。残高にあり得ない値で失敗を知らせる書き方で、第13章では例外で知らせる書き方を学ぶ
- `Game`のレベルは`score / 100 + 1`で求める。100点ごとに1つ上がり、350点ならレベル4になる
- `"%,.0f".formatted(400000.0)`は、3桁ごとにカンマを付け、小数点以下を出さずに「400,000」と表示する
