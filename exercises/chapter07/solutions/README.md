# 第7章 抽象クラスとインターフェイス - 解答例

[基礎課題](../basic/)の解答例です。
課題1・2は雛形の`TODO`を埋めた形、課題3・4は新しく作るファイルの解答例で、第7章までに学んだ機能だけを使っています。

## 解答例一覧

| 課題 | ファイル | 主な内容 |
|------|----------|----------|
| 課題1: 図形の抽象クラス | `Shape.java`、`Circle.java`、`Rectangle.java`、`ShapeDemo.java` | 抽象メソッドの実装、抽象クラスに書いた共通の表示処理 |
| 課題2: 支払いのインターフェイス | `Payable.java`、`Employee.java`、`Invoice.java`、`PayableDemo.java` | 継承の関係にないクラスへのインターフェイスの実装、デフォルトメソッド、`instanceof` |
| 課題3: 動物の行動のインターフェイス | `Flyable.java`、`Swimmable.java`、`Walkable.java`、`Bird.java`、`Fish.java`、`Duck.java`、`AnimalInterfaceTest.java` | 複数のインターフェイスの実装、抽象メソッドの値を使うデフォルトメソッド |
| 課題4: 通知とログのインターフェイス | `Notifiable.java`、`Loggable.java`、`LogCounter.java`、`EmailNotifier.java`、`SMSNotifier.java`、`SystemLogger.java`、`NotificationTest.java` | デフォルトメソッドのオーバーライドと`インターフェイス名.super`、インターフェイスの静的メソッド |

各プログラムの出力は、基礎課題のREADMEにある実行例と同じです。

## 実行方法

解答例はパッケージ`chapter07.solutions`にあるので、`exercises/`フォルダーでコンパイルします。

```bash
cd exercises
javac -d out chapter07/solutions/*.java
java -cp out chapter07.solutions.ShapeDemo
java -cp out chapter07.solutions.PayableDemo
java -cp out chapter07.solutions.AnimalInterfaceTest
java -cp out chapter07.solutions.NotificationTest
```

## 実装のポイント

- `Shape`の`displayInfo`は、抽象メソッドの`calculateArea`を呼んでいる。抽象クラスのメソッドから抽象メソッドを呼ぶと、実行時には実際のサブクラスの実装が使われる
- 従業員と請求書は「何かの一種」という関係にないので、共通の親クラスではなくインターフェイスでまとめる
- `PayableDemo`の`payable instanceof Employee employee`は、型を確かめると同時に`Employee`型の変数に入れる（第5章のパターンマッチング）。`Payable`型の変数のままでは`displayEmployeeInfo`を呼べない
- `Flyable`の`fly`は、抽象メソッド`getAltitude`の値を使うデフォルトメソッド。高さだけを動物ごとに決めれば、表示の処理は1か所で済む
- `EmailNotifier`の`notify`は、`Notifiable.super.notify(message)`でインターフェイスのデフォルトメソッドを呼んでから`log`を呼ぶ。`super.notify(message)`とは書けない（`super`は親クラスを指す）
- インターフェイスのフィールドは定数になり値を変えられないので、ログの件数は`LogCounter`のstaticフィールドで数える。`LogCounter`は`public`を付けず、同じパッケージの中だけで使う
