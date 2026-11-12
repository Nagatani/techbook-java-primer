# 第1章 解答例とポイント解説

## この章で学ぶこと

第1章はJavaプログラミングの最初の一歩です。
クラスと`main`メソッドの形でプログラムを書き、コンパイル・実行する流れと、`IO`クラスによる入出力を身につけます。

## 解答例の一覧

| ファイル | 課題 |
|------|------|
| `Exercise01_HelloWorld.java` | 基礎課題1: Hello World の拡張 |
| `Exercise02_BusinessCard.java` | 基礎課題2: `IO.print`と`IO.println`で名刺を表示する |
| `Exercise03_Greeting.java` | 基礎課題3: 入力した名前であいさつする |
| `Exercise04_PriceCalculation.java` | 基礎課題4: 入力した値段で計算する |
| `ProfileCard.java` | 発展課題1: プロフィールカード |
| `ShoppingReceipt.java` | 発展課題2: レシート |
| `ClassHello.java` | チャレンジ課題1: コンパクトソースファイルをクラスを宣言する形に書き直す |
| `FixErrors.java` | チャレンジ課題2: エラーを読んでプログラムを直す |

入力を受け取る解答例は、パイプで入力を渡しても動かせます。
```bash
javac Exercise03_Greeting.java
printf '田中太郎\n北海道\n' | java Exercise03_Greeting
```

## 課題ごとのポイント

### 基礎課題1・2: 出力
- `IO.println`は値を出力してから改行し、`IO.print`は改行しない
- 引数なしの`IO.println()`は改行だけを出力する
- 文字列の中の`\n`も改行になる
- `formatted`を使うと、C言語のprintfのように`%s`・`%d`で書式を指定できる

### 基礎課題3・4: 入力
- `IO.readln(メッセージ)`はメッセージを表示し、Enterキーまでの1行を文字列で返す
- 計算に使うときは`Integer.parseInt`で整数に変換する
    + `input * 3`はコンパイルエラーになり、`input + 3`は文字列の連結になる

### チャレンジ課題1: クラスを宣言する形への書き直し
- `public class ClassHello { ... }`でクラスを宣言する
- `void main()`を`public static void main(String[] args)`にする
- コンパクトソースファイルのフィールド`greeting`を、`static`な`main`メソッドからそのまま使うことはできない
    + この解答例では`main`メソッドの中の変数にした（`static`の意味は第3章で学ぶ）

### チャレンジ課題2: エラーの読み方
- `class FixError is public, should be declared in a file named FixError.java`
    + クラス名とファイル名の不一致
- `';' expected`
    + セミコロンの付け忘れ
- `unclosed character literal`
    + 文字列を単一引用符で囲んだ
- `cannot find symbol`（`io`）
    + 大文字・小文字の間違い
- 実行時の「メイン・メソッドがクラスFixErrorsで見つかりません」（英語の環境では`Main method not found in class FixErrors`）
    + `mian`のつづりの誤り。コンパイルは通るが、実行の開始点として認識されない
- エラーは一度にすべて出るとは限らない
    + 構文の誤り（セミコロン、引用符）を直すと、次に名前の誤り（クラス名とファイル名、`io`）が報告される

## デバッグのコツ

1. エラーメッセージの行番号を見て、その行と1つ前の行を確かめる
2. 最初のエラーから順に直す（1つの誤りが複数のエラーを生むことがある）
3. 少し書くたびにコンパイル・実行する

## 次のステップ

第1章が終わったら、第2章「Java基本文法」に進みましょう。
第2章では、変数とデータ型、演算子、条件分岐と繰り返し、配列、staticメソッド、文字列を学びます。
変数や計算を使う課題（自己紹介、四則演算、データ型など）は、第2章の演習にあります。
