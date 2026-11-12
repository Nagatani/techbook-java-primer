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

※ このコードはコンパイルエラーになります。実際に実行することはできません。

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

### エラー例

```java
// × 検査例外のハンドリング
List<String> files = Arrays.asList("file1.txt", "file2.txt");
files.stream()
    .map(file -> Files.readString(Path.of(file)))  // IOException発生
    .collect(Collectors.toList());
```

### エラーメッセージ

```
error: unreported exception IOException; must be caught or declared to be thrown
```

### 対処法

```java
// ○ 例外をランタイム例外でラップ
List<String> contents = files.stream()
    .map(file -> {
        try {
            return Files.readString(Path.of(file));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    })
    .collect(Collectors.toList());

// またはOptionalを使用した安全な処理（読めなかったファイルは除く）
List<String> readableContents = files.stream()
    .map(file -> {
        try {
            return Optional.of(Files.readString(Path.of(file)));
        } catch (IOException e) {
            return Optional.<String>empty();
        }
    })
    .filter(Optional::isPresent)
    .map(Optional::get)
    .collect(Collectors.toList());
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
// × プリミティブ型のストリーム（IntStream）をListに集めようとしている
List<String> strings = Arrays.asList("1", "2", "3");
List<Integer> numbers = strings.stream()
    .mapToInt(Integer::parseInt)  // IntStreamになる
    .collect(Collectors.toList());
```

### エラーメッセージ

```
error: method collect in interface IntStream cannot be applied to given types;
  required: Supplier<R>,ObjIntConsumer<R>,BiConsumer<R,R>
  found:    Collector<Object,CAP#1,List<Object>>
```

### 原因

`mapToInt`が返す`IntStream`は`int`をそのまま扱うストリームで、`Collector`を受け取る`collect`メソッドを持ちません。

### 対処法

```java
// ○ mapを使えば、parseIntが返すintはIntegerへ自動変換（オートボクシング）される
List<Integer> numbers = strings.stream()
    .map(Integer::parseInt)
    .collect(Collectors.toList());

// またはIntStreamのboxed()でStream<Integer>に戻す
List<Integer> boxedNumbers = strings.stream()
    .mapToInt(Integer::parseInt)
    .boxed()
    .collect(Collectors.toList());
```

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
