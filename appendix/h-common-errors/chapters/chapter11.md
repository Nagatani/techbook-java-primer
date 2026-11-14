# 第11章 ラムダ式と関数型インターフェイス：よくあるエラーと対処法

書籍『Java入門』第11章「ラムダ式と関数型インターフェイス」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。

ラムダ式と関数型インターフェイスの学習で遭遇する典型的なエラーと、その対処法について説明します。

## ラムダ式のシンタックスエラー

### エラー例

```java
// × 不正なラムダ式の構文
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
names.forEach(name -> {
    IO.println(name)  // セミコロン忘れ
});
```

このコードはコンパイルエラーになるため、実行できません。

### エラーメッセージ

```
error: ';' expected
```

### 対処法

コンパイルできる全文：[chapter11-code/LambdaSyntaxFixExample/](chapter11-code/LambdaSyntaxFixExample/)

```java
import java.util.Arrays;
import java.util.List;

public class LambdaSyntaxFixExample {
    public static void main(String[] args) {
        // 正しいラムダ式の構文
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        
        IO.println("波括弧を使用した形式:");
        names.forEach(name -> {
            IO.println(name);  // セミコロンを追加
        });

        // または単一式の場合は波括弧を省略
        IO.println("\n波括弧を省略した形式:");
        names.forEach(name -> IO.println(name));
    }
}
```

実行結果：
```
波括弧を使用した形式:
Alice
Bob
Charlie

波括弧を省略した形式:
Alice
Bob
Charlie
```

## 型推論の問題

### エラー例

```java
// × ラムダ式をvarで受けると、どの関数型インターフェイスか決まらない
var comp = (String x, String y) -> x.length() - y.length();
```

### エラーメッセージ

```
error: cannot infer type for local variable comp
  (lambda expression needs an explicit target-type)
```

### 原因

ラムダ式は、代入先の型（ターゲット型）から、どの関数型インターフェイスを実装するのかが決まります。
`var`では代入先の型が決まらないため、コンパイラーはラムダ式の型を推論できません。

### 対処法

```java
// ○ 代入先の関数型インターフェイスを明示する（引数の型は推論される）
Comparator<String> comp = (x, y) -> x.length() - y.length();

// またはメソッド参照を使用
Comparator<String> byLength = Comparator.comparing(String::length);
```

## 例外処理での問題

ここでは、ファイルの中身を文字列として読む`Files.readString`（第14章で学びます）を例にします。
このメソッドは、読み込みに失敗すると検査例外`IOException`（第13章で学びます）をスローします。

### エラー例

```java
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

// × Functionのapplyは検査例外をスローできない
Function<String, String> reader = file -> Files.readString(Path.of(file));  // コンパイルエラー
```

### エラーメッセージ

```
error: unreported exception IOException; must be caught or declared to be thrown
```

### 原因

`Function`の抽象メソッド`apply`には`throws`の宣言がありません。
そのため、`Function`として書いたラムダ式の中では、検査例外をスローする処理をそのまま呼び出せません。

### 対処法

1つめの方法は、ラムダ式の中で例外をキャッチし、検査されない例外（`UncheckedIOException`）に包み直すことです。

```java
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

// ○ 例外をラムダ式の中でキャッチし、検査されない例外でラップする
Function<String, String> reader = file -> {
    try {
        return Files.readString(Path.of(file));
    } catch (IOException e) {
        throw new UncheckedIOException(e);
    }
};
```

2つめの方法は、`throws`を宣言した関数型インターフェイスを自分で定義することです。
呼び出す側で`IOException`を処理します。

```java
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// ○ 検査例外を宣言した関数型インターフェイスを定義する
@FunctionalInterface
interface FileReaderFunction {
    String read(String file) throws IOException;
}

FileReaderFunction reader = file -> Files.readString(Path.of(file));
// reader.read("file1.txt")を呼び出す側で、IOExceptionをキャッチするか宣言する
```

## 変数スコープとキャプチャの問題

### エラー例

```java
// × 変数キャプチャの問題
public void processNumbers(List<Integer> numbers) {
    int threshold = 10;
    threshold = 20;  // 変数の変更
    
    List<Integer> filtered = numbers.stream()
        .filter(n -> n > threshold)  // エラー: 実質的にfinalではありません
        .collect(Collectors.toList());
}
```

### エラーメッセージ

```
error: local variables referenced from a lambda expression must be final or effectively final
```

### 対処法

```java
// ○ 実質的にfinalな変数を使用
public void processNumbers(List<Integer> numbers) {
    final int threshold = 20;  // finalキーワード
    
    List<Integer> filtered = numbers.stream()
        .filter(n -> n > threshold)
        .collect(Collectors.toList());
}

// または、変更し終えた値を別の変数にコピーし、ラムダ式からはその変数を参照する
public void processNumbersWithCopy(List<Integer> numbers) {
    int threshold = 10;
    threshold = 20;  // 変更する変数はラムダ式から参照しない
    int currentThreshold = threshold;  // 以後変更しないので実質的にfinal
    
    List<Integer> filtered = numbers.stream()
        .filter(n -> n > currentThreshold)
        .collect(Collectors.toList());
}
```

## 関数型インターフェイスの実装間違い

### エラー例

```java
// × 関数型インターフェイスの誤用
@FunctionalInterface
interface Calculator {
    int calculate(int a, int b);
    int subtract(int a, int b);  // 複数の抽象メソッド
}
```

### エラーメッセージ

```
error: Unexpected @FunctionalInterface annotation
  Calculator is not a functional interface
    multiple non-overriding abstract methods found in interface Calculator
```

### 対処法

```java
// ○ 単一の抽象メソッドを持つ関数型インターフェイス
@FunctionalInterface
interface Calculator {
    int calculate(int a, int b);
    
    // デフォルトメソッドまたは静的メソッドは許可される
    default int subtract(int a, int b) {
        return a - b;
    }
}

// 使用例
Calculator adder = (a, b) -> a + b;
Calculator multiplier = (a, b) -> a * b;
```

## メソッド参照の型不一致

### エラー例

```java
import java.util.function.Supplier;

// × 引数を取らないSupplierに、引数が必要なInteger.parseIntを参照させている
Supplier<Integer> parser = Integer::parseInt;
```

### エラーメッセージ

```
error: incompatible types: invalid method reference
    no suitable method found for parseInt(no arguments)
        method Integer.parseInt(String,int) is not applicable
          (actual and formal argument lists differ in length)
        method Integer.parseInt(CharSequence,int,int,int) is not applicable
          (actual and formal argument lists differ in length)
        method Integer.parseInt(String) is not applicable
          (actual and formal argument lists differ in length)
```

### 原因

メソッド参照は、関数型インターフェイスの抽象メソッドと同じ引数で呼び出せるメソッドを指す必要があります。
`Supplier<Integer>`の`get()`は引数を取りませんが、`Integer.parseInt`には変換する文字列を渡す必要があるため、引数の数が合いません。

### 対処法

```java
import java.util.function.Function;

// ○ Stringを受け取ってIntegerを返すFunctionにする
Function<String, Integer> parser = Integer::parseInt;
IO.println(parser.apply("123") + 1);  // 124
```

`parseInt`が返す`int`は、`Function<String, Integer>`の戻り値`Integer`へ自動で変換（オートボクシング）されます。

## 共通の対処戦略

1. IDEの支援を活用する
    + 自動補完とエラー検出機能を使用
2. 段階的な開発
    + 複雑なラムダ式は段階的に構築する
3. 型注釈の活用
    + 型推論が曖昧な場合に明示的な型指定を行う
4. 例外処理の統一
    + プロジェクト全体で一貫した例外処理戦略を採用
5. テストの充実
    + ラムダ式の動作を確認するユニットテストを記述
6. コードレビュー
    + 可読性とパフォーマンスの観点からレビューを実施
