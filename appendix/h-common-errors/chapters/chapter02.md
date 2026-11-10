# 第2章 Java基本文法：よくあるエラーと対処法

書籍『Java入門』第2章「Java基本文法」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。

## セミコロン忘れ

### エラーメッセージ
```
error: ';' expected
```

### 原因と対処

```java
// エラー例
int x = 10  // セミコロンがない

// 修正版
int x = 10; // セミコロンを追加
```

## 大文字・小文字の間違い

### エラーメッセージ
```
error: cannot find symbol
```

### 原因と対処

```java
// エラー例
io.println("Hello");  // ioが小文字

// 修正版
IO.println("Hello");  // IOを大文字で書く
```

## クラス名とファイル名の不一致

### エラーメッセージ
```
error: class HelloWorld is public, should be declared in a file named HelloWorld.java
```

### 原因と対処
- ファイル名 `Sample.java`
- クラス名 `public class HelloWorld`
- → ファイル名を`HelloWorld.java`に変更、またはクラス名を`Sample`に変更
