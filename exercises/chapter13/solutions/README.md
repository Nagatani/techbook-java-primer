# 第13章 例外処理 - 解答例

[基礎課題](../basic/)の解答例です。
基礎課題のREADMEの実行例は、この解答例を動かした出力です。
課題2〜4は複数のファイルからなるので、課題ごとにフォルダーを分けています。

## 解答例一覧

| 課題 | ファイル | 主な内容 |
|------|----------|----------|
| 課題1: 基本的な例外処理 | `task1-basic/BasicExceptionHandling.java` | try-catch-finally、複数のcatch、例外の投げ直し |
| 課題2: 銀行口座の独自例外 | `task2-bank/`（`BankAccountException.java`など7つ） | 検査例外の独自クラスと継承、エラーコード、例外の連鎖（cause） |
| 課題3: try-with-resourcesと後始末 | `task3-resource/TrackedResource.java`、`ResourceDemo.java` | `AutoCloseable`、閉じる順序、例外が起きたときの後始末 |
| 課題4: 失敗の知らせ方を使い分ける | `task4-design/`（`ValidationResult.java`など4つ） | 検証結果をまとめて返す・例外で止める・Optionalで返すの使い分け |

## 実行方法

```bash
cd exercises/chapter13/solutions/task2-bank
javac *.java
java BankDemo
```

課題1は`task1-basic/`で`BasicExceptionHandling`、課題3は`task3-resource/`で`ResourceDemo`、課題4は`task4-design/`で`ExceptionDesignDemo`を実行します。

## 実装のポイント

- 残高不足は、呼び出す側が対処すべき「起こりうる失敗」なので検査例外（`Exception`のサブクラス）にする。0以下の入金はプログラムの誤りなので、非検査例外の`IllegalArgumentException`にする
- `Bank`の`transfer`は、残高不足の例外を`TransferFailedException`に包み直して投げる。原因の例外は`getCause()`で取り出せる（例外の連鎖）
- 振込では、口座を2つとも見つけてから出金し、出金に成功したときだけ入金する。途中で失敗しても、どちらの残高も変わらない
- try-with-resourcesで開いた複数のリソースは、開いたのと逆の順に閉じられる。例外が起きたときは、`catch`より先に`close`が呼ばれる
- 課題4の`UserService`は、検査の誤りをすべて集めて返す`validate`、不正な値なら例外で止める`register`、見つからないことを`Optional`で返す`findById`を持つ
