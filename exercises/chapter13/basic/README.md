# 第13章 基本課題

## 学習目標

- try-catch-finallyの流れと、例外の種類に応じたcatchの書き分けを理解する
- 検査例外と非検査例外の違いを理解し、独自の例外クラスを階層にして作る
- 例外の連鎖（原因の例外を包んで投げ直す）で、失敗の理由をたどれるようにする
- try-with-resourcesとAutoCloseableで、例外が起きても確実に後始末する
- 「例外で知らせる」「結果をまとめて返す」「Optionalで返す」を使い分ける

## 課題一覧

このフォルダーには解答例のコードを置いていません。各課題の「作成するファイル」は、自分で作成するファイルの名前の例です。近い内容の解答例とテストは[solutions/](../solutions/)にあります。

### 課題1: 基本的な例外処理

**作成するファイル**: `BasicExceptionHandling.java`

よく出会う実行時の例外を起こし、catchとfinallyの動きを確かめます。

**要求仕様**:

- 0での割り算（`ArithmeticException`）、`null`のメソッド呼び出し（`NullPointerException`）、配列の範囲外（`ArrayIndexOutOfBoundsException`）を、それぞれメソッドの中でcatchする
- 割り算と配列のメソッドには`finally`を付け、例外が起きても実行されることを確かめる
- 文字列を数値に変えて配列の添字に使うメソッドで、`NumberFormatException`と`ArrayIndexOutOfBoundsException`を別々のcatchで受け、どちらの場合も代わりの値0を返す
- 内側のメソッドでcatchした例外を`throw e;`で投げ直し、mainでもう一度catchする

**実行例**:

```
--- ゼロ除算 ---
計算実行: 10 / 0
例外発生: java.lang.ArithmeticException: / by zero
finally実行: 計算処理を終了します

--- null参照 ---
文字列の長さを求める: null
例外発生: java.lang.NullPointerException

--- 配列の範囲外 ---
配列アクセス: array[10] (配列サイズ: 5)
例外発生: Index 10 out of bounds for length 5
finally実行: 配列処理を終了します

--- 複数のcatch ---
入力値: "abc"
NumberFormatException: 数値に変換できません
代わりの値を使います: 0
結果: 0
入力値: "5"
ArrayIndexOutOfBoundsException: 0〜2で指定してください
代わりの値を使います: 0
結果: 0
入力値: "1"
結果: 20

--- 例外の投げ直し ---
内側のメソッド: 変換に失敗したので、呼び出し元へ投げ直します
外側のmain: For input string: "12x"
```

### 課題2: 銀行口座の独自例外

**作成するファイル**: `BankAccountException.java`、`InsufficientFundsException.java`、`InvalidAccountException.java`、`TransferFailedException.java`、`BankAccount.java`、`Bank.java`、`BankDemo.java`

口座の取引で起きる失敗を、独自の例外クラスの階層で表します。

**要求仕様**:

- `BankAccountException`は`Exception`を継承した検査例外で、エラーコード（`INSUFFICIENT_FUNDS`など）を持つ
- `InsufficientFundsException`（残高不足）、`InvalidAccountException`（口座がない）、`TransferFailedException`（振込の失敗）は`BankAccountException`を継承する。前の2つは`getDetail()`で詳しい情報を返す
- `BankAccount.withdraw`は残高が足りないと`InsufficientFundsException`を投げる。`deposit`は0円以下の入金に`IllegalArgumentException`（非検査例外）を投げる
- `Bank.transfer`は口座がなければ`InvalidAccountException`を投げる。出金に失敗したら`TransferFailedException`に原因として包んで投げ直し、どちらの口座の残高も変えない
- `BankDemo`では、具体的な例外のcatchを先に、親の`BankAccountException`のcatchを後に書く

**実行例**:

```
--- 通常の出金 ---
口座12345から30,000円出金: 成功（残高: 70,000円）

--- 残高不足 ---
例外: InsufficientFundsException
エラーコード: INSUFFICIENT_FUNDS
メッセージ: 残高が不足しています
詳細: 要求額: 80,000円, 現在残高: 50,000円, 不足額: 30,000円

--- 存在しない口座 ---
例外: InvalidAccountException
エラーコード: ACCOUNT_NOT_FOUND
詳細: 口座番号: 99999

--- 振込の失敗と例外の連鎖 ---
例外: TransferFailedException: 振込に失敗しました
原因: InsufficientFundsException: 残高が不足しています
口座12345の残高: 70,000円（変わらない）
口座67890の残高: 50,000円（変わらない）

--- 非検査例外 ---
例外: 入金額は1円以上にしてください: -500
```

### 課題3: try-with-resourcesと後始末

**作成するファイル**: `TrackedResource.java`、`ResourceDemo.java`

開いたときと閉じたときにメッセージを出すリソースを作り、try-with-resourcesがいつ`close`を呼ぶかを確かめます。

**要求仕様**:

- `TrackedResource`は`AutoCloseable`を実装し、コンストラクターで「〜を開きました」、`close`で「〜を閉じました」と表示する
- `use(String work)`は、空の文字列を渡されたら`IllegalStateException`を投げる
- 1つのリソース、2つのリソース（開いた順と逆の順に閉じられる）、処理中に例外が起きる場合（catchより先に`close`が呼ばれる）を試す
- 比べるために、finallyで`close`を呼ぶ書き方も書く

**実行例**:

```
--- 1つのリソース ---
接続A を開きました
接続A で処理: データを読む
接続A を閉じました

--- 2つのリソース（閉じる順序） ---
入力 を開きました
出力 を開きました
入力 で処理: 1行読む
出力 で処理: 1行書く
出力 を閉じました
入力 を閉じました

--- 処理中の例外 ---
接続B を開きました
接続B を閉じました
catch: 接続B で処理に失敗しました
finally: 後始末が終わりました

--- finallyで閉じる書き方（比較用） ---
接続C を開きました
接続C で処理: データを書く
接続C を閉じました
```

ファイルの読み書きでのtry-with-resourcesは、第14章で学びます。

### 課題4: 失敗の知らせ方を使い分ける

**作成するファイル**: `ValidationResult.java`、`User.java`、`UserService.java`、`ExceptionDesignDemo.java`

利用者の登録で、失敗の知らせ方を3通りに使い分けます。

**要求仕様**:

- `ValidationResult`は入力の誤りをリストに集める。`validate`は最初の誤りで止めず、名前・メールアドレス・年齢の誤りをすべて集めて返す
- `register`は、誤りがあれば`IllegalArgumentException`を投げる（呼び出し側の誤りは例外で知らせる）
- `findById`は、見つからないことを例外ではなく`Optional<User>`で返す（見つからないのはよくあることなので）
- `User`はRecord（第8章）で書く

**実行例**:

```
--- 検証結果をまとめて返す ---
田中太郎: 成功
空の名前: 失敗
- 名前が空です
- メールアドレスの形式が正しくありません
- 年齢は0以上にしてください

--- 不正な値は例外で知らせる ---
登録: User[id=1001, name=田中太郎, email=tanaka@example.com, age=25]
登録できません: メールアドレスの形式が正しくありません

--- 見つからないことはOptionalで返す ---
ID=1001: 田中太郎
ID=9999: ゲスト
```

## コンパイルと実行

課題ごとにフォルダーを分け、そのフォルダーで実行します。

```bash
javac *.java
java BankDemo
```

## ヒント

### 課題1のヒント

- catchは具体的な例外から順に書く（親の例外を先に書くと、子のcatchに届かずコンパイルエラーになる）
- `finally`は、tryの中で`return`しても実行される
- `e.getMessage()`は例外のメッセージだけ、`e`を文字列にすると「クラス名: メッセージ」になる

### 課題2のヒント

- 検査例外は`extends Exception`、非検査例外は`extends RuntimeException`
- `super(message, cause)`で原因の例外を渡すと、`getCause()`で取り出せる
- 振込で残高を変えないためには、両方の口座が見つかり、出金に成功してから入金する

### 課題3のヒント

- 複数のリソースは`try (A a = ...; B b = ...)`のように`;`で区切る
- try-with-resourcesでは、catchやfinallyより先にリソースが閉じられる

### 課題4のヒント

- 誤りを集めるときは、`if`を並べて条件ごとにエラーを足す（`else if`にしない）
- `Optional`の`map`と`orElse`で、見つかったときと見つからないときの値を1行で書ける（第12章）

## 完了チェックリスト

- [ ] 課題1: catchの順序とfinallyの動きを説明できる
- [ ] 課題2: 独自の例外を階層にし、具体的な例外から順にcatchできる
- [ ] 課題2: 例外の連鎖で、原因の例外をたどれる
- [ ] 課題3: try-with-resourcesがリソースを閉じる順序とタイミングを説明できる
- [ ] 課題4: 例外・結果のまとめ・Optionalの使い分けを説明できる

**次のステップ**: 基本課題が終わったら、[advanced/](../advanced/)の発展課題に進みましょう。
