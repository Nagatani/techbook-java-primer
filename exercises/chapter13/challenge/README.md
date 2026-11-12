# 第13章 チャレンジ課題

## 学習目標
- 独自例外の階層と例外の連鎖を、まとまったプログラムの中で使う
- `AutoCloseable`を実装したクラスを、try-with-resourcesで複数まとめて閉じる
- 1件の失敗で全体を止めず、記録して処理を続ける（止めるべき失敗とは区別する）
- 失敗したときに、途中まで変えた状態（在庫の取り置き）を元に戻す
- 一時的な失敗をやり直す（リトライ）
- 抑制された例外（`getSuppressed()`）とfinallyの動きを確かめる
- `java.util.logging`で、失敗の重さに合わせてログのレベルを使い分ける

## 課題一覧

### 課題1: 注文データの一括処理
**ファイル**（このフォルダーのファイルは解答例です）:
```
challenge/
├── OrderException.java         注文の例外の親クラス（行番号を持つ）
├── InvalidOrderException.java  書式や内容の誤り
├── OutOfStockException.java    在庫不足
├── PrinterException.java       印刷の失敗（やり直せば成功することがある）
├── Order.java                  1件の注文（レコード）と、1行からの変換
├── Inventory.java              商品の在庫
├── OrderSource.java            注文データを1行ずつ渡す読み取り口（AutoCloseable）
├── ReceiptPrinter.java         レシートのプリンター（AutoCloseable）
├── OrderProcessor.java         注文を1行ずつ処理する
├── LogSetup.java               ログを短い形で標準出力に出す設定
└── OrderBatchApp.java          mainメソッド
```

文房具店の注文データ（「注文番号,商品コード,数量」の行）をまとめて処理し、在庫を減らしてレシートを印刷するプログラムを作成してください。
ファイルの読み込みは第14章で学ぶので、注文データは文字列の配列で用意し、`OrderSource`がファイルの代わりに1行ずつ渡します。
プリンターも本物ではなく、決めた回の印刷で紙詰まりを起こす動作確認用のクラスにします。

**要求仕様**:
- 例外
    - `OrderException`（検査例外）を親とし、`InvalidOrderException`と`OutOfStockException`を子クラスにする。どれも注文データの行番号を持つ
    - `OutOfStockException`は商品コードと不足数を返すメソッドを持つ
    - `PrinterException`は注文の例外とは別の検査例外にする
- `Order.parse(行, 行番号)`
    - 項目が3つでない、数量が整数でない、数量が1未満のときは`InvalidOrderException`にする
    - 数量が整数でないときは、`NumberFormatException`を原因として包む
- `Inventory`
    - `reserve(注文)`は在庫から取り置く。扱っていない商品は`InvalidOrderException`、在庫不足は`OutOfStockException`にし、どちらも在庫を変えない
    - `cancel(注文)`は取り置きを取り消して在庫に戻す
- `OrderSource`と`ReceiptPrinter`
    - どちらも`AutoCloseable`を実装し、開いたとき・閉じたときにメッセージを出す
    - `OrderSource`は、指定した行の読み取りで`IOException`を投げる設定と、`close()`で`IOException`を投げる設定を持てる（動作確認用）
- `OrderProcessor`
    - `processAll`は、注文の失敗（`OrderException`）をログに出して記録し、次の行へ進む
    - 読み取りそのものの失敗（`IOException`）は続けられないので、`throws IOException`で呼び出し側に伝える
    - 印刷は最大3回まで試す。3回とも失敗したら在庫の取り置きを戻し、最後の`PrinterException`を原因にした`OrderException`を投げる
    - ログのレベルは、印刷のやり直しは`INFO`、注文の失敗は`WARNING`にする
- `OrderBatchApp`
    - 1回目は、`OrderSource`と`ReceiptPrinter`を1つのtry-with-resourcesで開いて9行の注文を処理し、成功・失敗の件数、失敗の一覧、在庫を表示する
    - 2回目は、2行目の読み取りで失敗し、閉じる処理でも失敗する`OrderSource`を使う。本来の例外のメッセージと、抑制された例外を表示する。finallyで処理済みの件数を表示する

**実行例**:
```
=== 1回目: 注文データの処理 ===
[orders-1.csv] を開きました
[プリンター] に接続しました
  レシート: 注文1001 A100 x 2
  [INFO] 注文1002: 紙詰まりが起きました（1回目） → やり直します
  レシート: 注文1002 B200 x 1
  [WARNING] 3行目: 在庫が足りません（B200、注文 5個、在庫 2個） 不足 3個
  [WARNING] 4行目: 商品コード X999 は扱っていません
  [WARNING] 5行目: 数量が整数ではありません: 二
      原因: NumberFormatException: For input string: "二"
  [INFO] 注文1006: 紙詰まりが起きました（1回目） → やり直します
  [INFO] 注文1006: 紙詰まりが起きました（2回目） → やり直します
  [INFO] 注文1006: 紙詰まりが起きました（3回目） → あきらめます
  [WARNING] 6行目: レシートを印刷できないため注文1006を取り消しました
      原因: PrinterException: 紙詰まりが起きました
  [WARNING] 7行目: 項目の数が3つではありません: 1007,C300
  レシート: 注文1008 C300 x 4
  [WARNING] 9行目: 数量は1以上にしてください: 0
[プリンター] との接続を切りました
[orders-1.csv] を閉じました
--- 結果 ---
成功 3件、失敗 6件
  3行目 OutOfStockException: 在庫が足りません（B200、注文 5個、在庫 2個）
  4行目 InvalidOrderException: 商品コード X999 は扱っていません
  5行目 InvalidOrderException: 数量が整数ではありません: 二
  6行目 OrderException: レシートを印刷できないため注文1006を取り消しました
  7行目 InvalidOrderException: 項目の数が3つではありません: 1007,C300
  9行目 InvalidOrderException: 数量は1以上にしてください: 0
在庫: {A100=8, B200=2, C300=1}

=== 2回目: 読み取りの途中で失敗する ===
[orders-2.csv] を開きました
[プリンター] に接続しました
  レシート: 注文2001 A100 x 1
[プリンター] との接続を切りました
[orders-2.csv] を閉じました
処理を中断しました: 2行目を読み取れません（接続が切れました）
  抑制された例外: [orders-2.csv] の後始末に失敗しました
処理済み 1件（finallyで必ず表示）
在庫: {A100=7, B200=2, C300=1}
```

在庫は最初、A100が10個、B200が3個、C300が5個です。
注文1006は3回とも印刷に失敗したので、取り置いた3個がA100の在庫に戻っています（A100は注文1001の2個だけが減って8個）。
try-with-resourcesで開いた資源は、開いた順と逆の順（プリンター、読み取り口の順）に閉じられます。

## コンパイルと実行

```bash
javac *.java
java OrderBatchApp
```

## 実装のヒント

```java
// 2つの資源を1つのtry-with-resourcesで開く。例外が起きても両方閉じられる
try (OrderSource source = new OrderSource("orders-1.csv", orders);
     ReceiptPrinter printer = new ReceiptPrinter(Set.of(2, 4, 5, 6))) {
    processor.processAll(source, printer);
} catch (IOException e) {
    IO.println("処理を中断しました: " + e.getMessage());
    for (Throwable suppressed : e.getSuppressed()) {
        IO.println("  抑制された例外: " + suppressed.getMessage());
    }
}

// やり直しても失敗したら、取り置きを戻してから原因を包んで投げる
inventory.cancel(order);
throw new OrderException("レシートを印刷できないため注文" + order.orderId() + "を取り消しました",
        order.lineNumber(), last);
```

- 1行ごとの処理をtry-catchで囲むと、その行の失敗だけを記録してループを続けられる。読み取りの失敗はループの外へ伝え、処理全体を止める
- catchの順は`OutOfStockException`（子クラス）を先に、`OrderException`（親クラス）を後にする
- try-with-resourcesの本体で例外が起きたあと、`close()`でも例外が起きると、`close()`の例外は本体の例外の「抑制された例外」として付け加えられ、本体の例外が呼び出し側に届く
- `Logger.getLogger("")`で得られるルートのロガーには、日時を付けて標準エラー出力に出す`ConsoleHandler`が付いている。解答例では、これを外して自前の`Handler`（匿名クラス）を付け、レベルとメッセージだけを標準出力に出している
- `logger.log(Level.WARNING, メッセージ, 例外)`のように例外を渡すと、`Handler`は`LogRecord`の`getThrown()`でその例外を受け取れる

## 応用のポイント

1. 続ける失敗と止める失敗
    - 1件の注文の誤りは記録して次へ進み、データを読めなくなったときは全体を止める。どちらにするかで、catchを置く場所が変わる
2. 状態を元に戻す
    - 在庫を取り置いたあとで失敗したら、例外を投げる前に取り置きを戻す。失敗した注文が在庫を減らしたままにならない
3. 例外の連鎖とログ
    - 利用者に見せるメッセージは「注文を取り消した」、原因の「紙詰まり」はcauseとログに残す

## 発展（任意）

- 失敗した注文の行だけを集めて、あとで直して流し直せる「再処理用の一覧」を表示する
- 印刷をやり直す前に待つ時間を、1回目は100ミリ秒、2回目は200ミリ秒と延ばす（`Thread.sleep`は`InterruptedException`を投げるので、その扱いも考える）
- `OrderProcessor`のテストをJUnitで書く（第21章）。`assertThrows`で`Order.parse`の例外を確かめる

## 完了チェックリスト

- [ ] 注文の失敗が`OrderException`の階層で表され、行番号を持っている
- [ ] `NumberFormatException`と`PrinterException`が、causeとしてたどれる
- [ ] 1件の失敗で処理が止まらず、読み取りの失敗では止まる
- [ ] 印刷を3回やり直し、だめなら在庫の取り置きが戻る
- [ ] 2つの資源が、例外が起きたときも開いた順と逆の順に閉じられる
- [ ] 抑制された例外を表示できる

**次のステップ**: チャレンジ課題が完了したら、第14章「ファイルI/O」に進みましょう。第14章では`OrderSource`の代わりに本物のファイルから読み込めるようになります。
