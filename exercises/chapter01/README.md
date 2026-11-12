# 第1章 Java入門と開発環境構築 - 演習課題

## 概要
このディレクトリは第1章「Java入門と開発環境構築」の演習課題です。
最初のJavaプログラムを書き、コンパイルして実行する流れと、`IO`クラスによる画面への出力・キーボードからの入力を練習します。
どの課題も、第1章で学ぶ内容（クラスと`main`メソッドの形、`IO.println`・`IO.print`・`IO.readln`、`formatted`による書式、`Integer.parseInt`）だけで解けます。
変数・演算子・条件分岐・繰り返しを使う課題は、第2章の演習（`exercises/chapter02/`）にあります。

## 学習目標
- Javaプログラムの基本構造（クラスと`main`メソッド）を書ける
- `javac`でコンパイルし、`java`で実行できる
- `IO.println`と`IO.print`を使い分けて画面に出力できる
- `IO.readln`でキーボードから1行を読み、`Integer.parseInt`で整数に変換できる
- コンパイルエラーのメッセージを読んで、プログラムを直せる

## 課題構成

### 基礎課題 (basic/)
1. **Exercise01_HelloWorld.java** - 名前と今年の年を表示する
2. **Exercise02_BusinessCard.java** - `IO.print`と`IO.println`で名刺を表示する
3. **Exercise03_Greeting.java** - 入力した名前と出身地であいさつする
4. **Exercise04_PriceCalculation.java** - 入力した値段から3個分・5個分の金額を表示する

### 発展課題 (advanced/)
1. **ProfileCard.java** - 入力した内容でプロフィールカードを作る
2. **ShoppingReceipt.java** - 入力した商品と個数からレシートを表示する

### チャレンジ課題 (challenge/)
1. **ClassHello.java** - コンパクトソースファイル（`CompactHello.java`）をクラスを宣言する形に書き直す
2. **FixErrors.java** - 誤りを含むプログラムを入力し、エラーメッセージを読んで直す

### 解答例 (solutions/)
- 基礎課題・発展課題・チャレンジ課題の解答例

## 実行方法

### コンパイルと実行
```bash
# コンパイル（Exercise01_HelloWorld.class ができる）
javac Exercise01_HelloWorld.java

# 実行（.class は付けない）
java Exercise01_HelloWorld
```

ソースファイルを直接実行することもできます（Java 11以降）。
```bash
java Exercise01_HelloWorld.java
```

開発環境の準備は付録A（`appendix/a-environment-setup/`）を参照してください。

## 学習の進め方

1. まずは自分で考える
    - エラーメッセージをしっかり読む
    - 第1章のサンプルコードを参考にする
    - 5〜10分考えてわからなければ、各フォルダーのREADME.mdのヒントを見る
2. コンパイルエラーが出たら
    - エラーメッセージの行番号を確認する
    - セミコロンの付け忘れ、大文字・小文字の間違いを確かめる
    - クラス名とファイル名が一致しているかを確かめる
3. 動作を確かめるコツ
    - 期待する出力と実際の出力を1行ずつ比べる
    - 少し書いたらコンパイル・実行し、小さな単位で動作を確かめる

## よくあるトラブルと解決方法

| 問題 | 原因 | 解決方法 |
|------|------|----------|
| `javac`が認識されない | JDKがインストールされていない、またはPATHが通っていない | 付録Aの手順でJDK 25をインストールし、`javac -version`で確かめる |
| `Could not find or load main class` | コンパイルしていない、またはクラス名を間違えている | `javac ファイル名.java`を実行し、`java クラス名`（`.class`なし）で実行する |
| `class X is public, should be declared in a file named X.java` | クラス名とファイル名が一致していない | ファイル名を`クラス名.java`にする |
| `cannot find symbol` | 名前のつづりや大文字・小文字の間違い（`io.println`など） | つづりと大文字・小文字を確認する |
| `';' expected` | セミコロンの付け忘れ | 該当行の末尾にセミコロンを付ける |
| `NumberFormatException` | `Integer.parseInt`に数字以外を渡した | 整数を入力する（第1章のコラム「Integer.parseIntのエラーから学ぶ」） |
| 文字化け | ファイルの文字コードが違う | UTF-8で保存する |

## 関連セクション
- 本書 第1章「Java入門と開発環境構築」
    + 「最初のJavaプログラム」
    + 「Javaプログラムの基本構文」（コンパクトソースファイル、画面に文字列を出力する方法）
    + 「標準入出力を扱うサンプルを動かしてみよう」

## 評価基準
- [ ] プログラムがエラーなくコンパイルできる
- [ ] 実行例と同じ出力になる
- [ ] クラス名とファイル名が一致している
- [ ] 入力を受け取る課題では、`IO.readln`の戻り値を変数で受け取っている

## 注意事項
- 日本語を含むファイルは文字コード（UTF-8）に注意してください
- パッケージ宣言は使用しないでください（パッケージは第4章で学びます）
- 解答例は`solutions/`ディレクトリにありますが、まず自分で考えてみましょう
- 基礎課題が終わったら、第2章「Java基本文法」に進みましょう
