# 第13章 発展課題

## 学習目標
- 業務の失敗の種類に合わせて、独自の例外クラスを親子関係（階層）で設計する
- 検査例外（業務上起こりうる失敗）と非検査例外（プログラムの誤り）を使い分ける
- 例外の種類ごとにcatchを分け、子クラスを先に、親クラスを後に書く
- 処理が同じ例外をマルチキャッチ（`catch (A | B e)`）でまとめる
- 低い層の例外を、意味のわかる独自例外に包み直す（例外の連鎖）

## 課題一覧

### 課題1: 図書館の貸出と例外の階層
**ファイル**（このフォルダーのファイルは解答例です）:
```
advanced/task1-library/
├── LibraryException.java          図書館の例外の親クラス（検査例外）
├── BookNotFoundException.java     登録されていない本
├── BookUnavailableException.java  貸出中の本
├── LoanLimitException.java        貸出冊数の上限を超えた
├── Library.java                   貸出と返却を管理する図書館
└── LibraryApp.java                mainメソッド
```

図書館の貸出で起こる失敗を、独自の例外クラスの階層で表すプログラムを作成してください。
日付は「1日」「12日」のような日の番号（整数）で扱います。

**要求仕様**:
- `LibraryException`（親クラス）
    - `Exception`を継承した抽象クラスにする（業務上起こりうる失敗なので検査例外）
    - 失敗にかかわる資料番号を持ち、`getBookId()`で返す
- 子クラス
    - `BookNotFoundException`: 登録されていない資料番号を指定した
    - `BookUnavailableException`: ほかの人が借りている。返却予定日を持つ
    - `LoanLimitException`: 1人が借りられる冊数（2冊）を超えた。利用者の名前と上限を持つ
    - メッセージは利用者にそのまま見せられる日本語にする
- `Library`
    - `lend(利用者, 資料番号, 今日)`は貸出期間14日で貸し出し、返却期限の日を返す。失敗は上の例外で知らせ、`throws LibraryException`と宣言する
    - `giveBack(資料番号)`は返却し、借りていた人の名前を返す
    - 利用者の名前が空（呼び出し方の誤り）は`IllegalArgumentException`、貸していない本の返却は`IllegalStateException`にする（非検査例外）
- `LibraryApp`
    - 貸出では、貸出中と冊数超過のときに利用者への案内を変える（子クラスごとのcatch）。それ以外は親クラスのcatchで受け取る
    - 親クラスのcatchだけで受け取り、`getClass().getSimpleName()`で実際の種類を表示する例も示す
    - 返却では、`BookNotFoundException`と`IllegalStateException`をマルチキャッチでまとめる

**実行例**:
```
=== 貸出（例外の種類ごとに処理を変える） ===
1日: 佐藤さんに「Java入門」を貸し出しました（返却期限 15日）
2日: 「Java入門」は貸出中です（15日に返却予定） → 15日以降に来てください
2日: 資料番号 B999 の本は登録されていません
3日: 佐藤さんに「アルゴリズム図鑑」を貸し出しました（返却期限 17日）
3日: 佐藤さんはすでに2冊借りています → 返却してから借りてください

=== 親クラスでまとめて受け取る ===
高橋さんにB003を貸し出しました（返却期限 19日）
貸出できません（BookUnavailableException、資料番号 B002）: 「アルゴリズム図鑑」は貸出中です（17日に返却予定）
貸出できません（BookNotFoundException、資料番号 B000）: 資料番号 B000 の本は登録されていません

=== 返却 ===
佐藤さんからB001が返却されました
返却できません: 「Java入門」は貸し出していません
返却できません: 資料番号 B404 の本は登録されていません

=== プログラムの誤り（非検査例外） ===
呼び出し方の誤り: 利用者の名前が空です
```

### 課題2: ゲームの設定の読み込みと例外の連鎖
**ファイル**（このフォルダーのファイルは解答例です）:
```
advanced/task2-config/
├── ConfigException.java  設定の誤りを表す例外（行番号と原因を持つ）
├── GameConfig.java       「キー=値」の行を読む設定
└── ConfigApp.java        mainメソッド
```

「キー=値」の形で書かれたゲームの設定を読み込み、誤りがあれば行番号と原因を添えて知らせるプログラムを作成してください。
ファイルの読み込みは第14章で学ぶので、設定の行は文字列の配列で用意します。

**要求仕様**:
- `ConfigException`
    - `Exception`を継承し、問題のあった行の番号を持つ
    - 原因の例外を受け取るコンストラクター（`super(message, cause)`を呼ぶ）と、受け取らないコンストラクターを持つ
- `GameConfig`
    - `parse(行の配列)`で読み込む。空行と`#`で始まる行は読み飛ばす
    - `=`がない行、同じキーが2回ある行は`ConfigException`にする
    - `getInt(キー, 既定値, 最小, 最大)`は、キーがなければ既定値を返す。整数でなければ`NumberFormatException`を`ConfigException`に包んで投げる。範囲外も`ConfigException`にする
    - `getRequired(キー)`は、キーがなければ`ConfigException`にする
- `ConfigApp`
    - 6つの設定を順に読み込み、失敗したら誤りを表示して「既定の設定で起動します」と出す
    - 誤りの表示では、`getCause()`を`null`になるまでたどり、原因の種類とメッセージを出す

**実行例**:
```
=== 設定1 ===
読み込みました: 名前=さくら、音量=80、難易度=3

=== 設定2 ===
読み込みました: 名前=たろう、音量=50、難易度=2

=== 設定3 ===
設定の誤り: 2行目: volume の値が整数ではありません
  原因: java.lang.NumberFormatException: For input string: "大きめ"
既定の設定で起動します

=== 設定4 ===
設定の誤り: 2行目: level は1から5の範囲で指定してください（9）
既定の設定で起動します

=== 設定5 ===
設定の誤り: 2行目: 「キー=値」の形ではありません: volume 30
既定の設定で起動します

=== 設定6 ===
設定の誤り: 必須のキー playerName がありません
既定の設定で起動します
```

設定2は音量（volume）を書いていないので、既定値の50になります。

## コンパイルと実行

```bash
cd task1-library
javac *.java
java LibraryApp

cd ../task2-config
javac *.java
java ConfigApp
```

## 実装のヒント

```java
// 子クラスのcatchを先に、親クラスのcatchを後に書く
try {
    library.lend(member, bookId, today);
} catch (BookUnavailableException e) {
    // 貸出中のときだけの案内
} catch (LibraryException e) {
    // それ以外の図書館の例外
}

// 低い層の例外を包み直し、原因をcauseに残す
try {
    value = Integer.parseInt(text);
} catch (NumberFormatException e) {
    throw new ConfigException(key + " の値が整数ではありません", lineNumber, e);
}
```

- 親クラスのcatchを先に書くと、後ろの子クラスのcatchに届かないため、コンパイルエラー（`例外BookUnavailableExceptionはすでに捕捉されています`）になる
- マルチキャッチでまとめる例外どうしは、親子関係にあってはいけない（`catch (LibraryException | BookNotFoundException e)`はエラー）
- 例外のメッセージには、何が・どこで・どうおかしいかを入れる。資料番号や行番号はフィールドにも持たせておくと、呼び出し側が表示を組み立てやすい
- `NumberFormatException`をそのまま投げると、呼び出し側は「どの設定のどの行か」がわからない。包み直してもcauseに残せば、原因の情報は失われない

## 応用のポイント

1. 例外の階層
    - 呼び出し側は、細かく扱いたい失敗は子クラスで、まとめてよい失敗は親クラスで受け取れる
2. 検査例外と非検査例外
    - 本が貸出中なのは正しいプログラムでも起こるので検査例外、名前が空なのは呼び出し側の誤りなので非検査例外にする
3. 例外の連鎖
    - 利用者に見せるメッセージは上の層の言葉で書き、調査に必要な低い層の情報はcauseに残す

## 発展（任意）

- 課題1に、延滞している本があると借りられない`OverdueException`を加える。`LibraryApp`のどのcatchで受け取られるかを確かめる
- 課題2で、最初の誤りで止めずにすべての行の誤りを集め、まとめて表示するようにする（`addSuppressed`を使う方法もある）

## 完了チェックリスト

- [ ] 独自例外が`LibraryException`を親とする階層になっている
- [ ] 業務上の失敗は検査例外、呼び出し方の誤りは非検査例外になっている
- [ ] catchが子クラス、親クラスの順に並んでいる
- [ ] マルチキャッチを使っている
- [ ] 包み直した例外の`getCause()`で、元の`NumberFormatException`をたどれる

**次のステップ**: 発展課題が完了したら、challenge/のチャレンジ課題で、例外の階層・連鎖・リソース管理・ログをまとめて使ってみましょう。
