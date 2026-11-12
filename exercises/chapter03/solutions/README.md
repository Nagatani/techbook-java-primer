# 第3章 オブジェクト指向の考え方 - 解答例

[基礎課題](../basic/)の解答例です。
基礎課題の雛形の`TODO`を埋めた形で書いてあり、第3章までに学んだ機能だけを使っています。
課題1〜3（Person・Book・Circle）はフィールドとメソッドだけを持つクラス、課題4〜7はコンストラクターとオーバーロードを使うクラスです。

## 解答例一覧

| 課題 | ファイル | 主な内容 |
|------|----------|----------|
| 課題1: Personクラス | `Person.java`、`PersonTest.java` | フィールドとメソッド、オブジェクトの生成 |
| 課題2: Bookクラス | `Book.java`、`BookTest.java` | 値を返すメソッド（税込価格）、別のオブジェクトを引数に受け取るメソッド |
| 課題3: Circleクラス | `Circle.java`、`CircleTest.java` | `Math.PI`を使う計算、`"%.2f".formatted(...)`での表示 |
| 課題4: Studentクラス | `Student.java`、`StudentTest.java` | 3つのコンストラクター、`this(...)`で別のコンストラクターを呼ぶ |
| 課題5: Rectangleクラス | `Rectangle.java`、`RectangleTest.java` | コンストラクターと`setSize`のオーバーロード |
| 課題6: BankAccountクラス | `BankAccount.java`、`BankAccountTest.java` | `this.フィールド名`と引数の区別、`withdraw`のオーバーロード（手数料つき） |
| 課題7: Timeクラス | `Time.java`、`TimeTest.java` | `addTime`のオーバーロード、繰り上げの処理（正規化） |

各`〜Test.java`の`main`の出力は、基礎課題のREADMEにある実行例と同じです。

## 実行方法

```bash
cd exercises/chapter03/solutions
javac *.java
java PersonTest
java TimeTest
```

## 実装のポイント

- `Book`の`comparePriceWith(Book other)`では、相手のフィールドを`other.price`のように参照する。同じクラスのオブジェクトなので、フィールドに直接触れられる
- 税込価格の`(int) (price * 1.1)`は、小数点以下を切り捨てる。`(int) price * 1.1`と書くと、先に`price`だけが`int`になり、結果は`double`のままになる
- `Student()`と`Student(String name)`は、`this("未設定", 0, "未設定")`のように一番多くの引数を受け取るコンストラクターを呼ぶ。値を入れる処理を1か所にまとめられる
- `this(...)`は、コンストラクターの最初の文に書く
- `Time`では、どのコンストラクター・`addTime`も最後に`normalize()`を呼び、秒→分→時の順に繰り上げる。23時45分30秒に1時間30分45秒を足すと、25時16分15秒を経て1時16分15秒になる
- `addTime(int minutes, boolean isMinute)`は、`addTime(int seconds)`と引数の数を変えて区別するための形。第4章では、`addMinutes`のように名前を変えるほうがわかりやすい場合も学ぶ

## 後の章で学ぶこと

- フィールドを`private`にし、getterとsetterを通して値を変える書き方（カプセル化）は第4章で学ぶ。この章の解答例は、`person.name = "田中太郎"`のようにフィールドに直接値を入れている
- `toString`をオーバーライドすると、`IO.println(person)`でオブジェクトの内容を表示できる（第5章）。この章では`introduce`や`showInfo`のような表示用のメソッドを書く
