# 第1章 チャレンジ課題

## 学習目標
- Java 25のコンパクトソースファイルと、本書の書き方（クラスを宣言する形）の違いを理解する
- コンパイルエラーと実行時のエラーのメッセージを読み、原因を見つけて直す

## 課題一覧

### 課題1: コンパクトソースファイルをクラスを宣言する形に書き直す
**ファイル名**: `ClassHello.java`（出発点は`CompactHello.java`）

`CompactHello.java`は、Java 25のコンパクトソースファイルで書いたプログラムです。
クラスの宣言がなく、`main`メソッドも`public`・`static`・`String[] args`を省いています。
同じ動きをするプログラムを、本書の書き方（クラスを宣言し、`public static void main(String[] args)`から実行する形）で`ClassHello.java`に書いてください。

**実行例**（「田中太郎」と入力した場合）:
```
お名前を入力: 田中太郎
ようこそ、Javaの世界へ
田中太郎さん、最初のプログラムを動かしてみましょう
```

**確かめること**:
- `java CompactHello.java`と`java ClassHello.java`の両方を実行し、同じ結果になること
- `javac ClassHello.java`でコンパイルしてから`java ClassHello`で実行できること

**ヒント**:
- 第1章の節「Java 25の新しい書き方：クラス宣言を省いたプログラム」の比較表を見直す
- `CompactHello.java`のフィールド`greeting`は、`main`メソッドの中の変数にしてよい

### 課題2: エラーを読んでプログラムを直す
**ファイル名**: `FixErrors.java`

次のプログラムには誤りが5つあります。
`FixErrors.java`という名前のファイルにそのまま入力し、コンパイルと実行を繰り返して、エラーメッセージを手がかりにすべて直してください。

```java
public class FixError {
    public static void mian(String[] args) {
        io.println("エラーを直せました")
        IO.println('コンパイルと実行の流れを確かめましょう');
    }
}
```

**直したあとの実行結果**:
```
エラーを直せました
コンパイルと実行の流れを確かめましょう
```

**ヒント**:
- 一度に全部直そうとせず、エラーメッセージの最初の1件から順に直す
- コンパイルが通っても、実行したときに出るエラーもある
- 第1章の節「よくあるエラーと対処法」と、付録H（`appendix/h-common-errors/chapters/chapter01.md`）を参照する

## 完了チェックリスト

- [ ] 課題1: `ClassHello.java`が`CompactHello.java`と同じ結果になった
- [ ] 課題2: 5つの誤りをすべて直し、エラーメッセージとの対応を説明できる

**次のステップ**: チャレンジ課題が完了したら、第2章「Java基本文法」に進みましょう。
