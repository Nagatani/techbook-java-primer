# 第8章 Javaの特殊なクラス形式：よくあるエラーと対処法

書籍『Java入門』第8章「Javaの特殊なクラス形式」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。

## enum定数の大文字小文字を間違える

### 原因と対処法

```java
// 間違い
if (status == Status.active) {  // コンパイルエラー
    // 処理
}

// 正しい
if (status == Status.ACTIVE) {
    // 処理
}
```

## switch文のcaseにenumの型名を付ける（Java 20以前のエラー）

### 原因と対処法

本書が対象とするJava 25では、列挙型のswitchの`case`に`Status.ACTIVE`のような型名付きの定数を書いても、エラーにはなりません。
エラーになるのは、Java 20以前のコンパイラーでコンパイルしたときです。
Java 21から型名付きでも書けるようになりましたが、switchの対象が列挙型であることは明らかなので、定数名だけを書くのが一般的です。
古いバージョンのJavaでも動かすコードや、古い書籍・記事のコードを読むときは、この違いに注意してください。

```java
// Java 20以前ではコンパイルエラー（Java 21以降は書けるが冗長）
switch (status) {
    case Status.ACTIVE:
        break;
}

// 一般的な書き方（どのバージョンでも使える）
switch (status) {
    case ACTIVE:
        break;
}
```
