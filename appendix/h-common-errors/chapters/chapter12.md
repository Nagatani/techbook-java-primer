# 第12章 Stream APIと高度なコレクション操作：よくあるエラーと対処法

書籍『Java入門』第12章「Stream APIと高度なコレクション操作」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。

Stream APIと高度なコレクション操作の学習で遭遇する典型的なエラーと、その対処法について説明します。

## Stream操作のチェーンエラー

#### エラー例

以下のコードは、Stream操作のチェーンでよくある間違いの例です。

```java
// × 操作の順序を誤ったStream操作のチェーン
List<String> words = Arrays.asList("apple", "banana", "avocado", "cherry");
words.stream()
    .filter(word -> word.length() > 5)
    .map(String::toUpperCase)
    .filter(word -> word.startsWith("a"))  // すでに大文字に変換済み
    .forEach(IO::println);
```

##### 問題点
- `map(String::toUpperCase)`で大文字に変換したあとに、小文字の`"a"`で始まるかを調べているため、条件に一致する要素がなく、何も出力されない
- コンパイルエラーにも実行時エラーにもならないため、誤りに気付きにくい

#### 対処法

以下のコードは、上記の問題を解決した正しいStream操作の例です。

```java
// ○ 絞り込みを先に行うStream操作のチェーン
List<String> words = Arrays.asList("apple", "banana", "avocado", "cherry");
words.stream()
    .filter(word -> word.length() > 5)
    .filter(word -> word.startsWith("a"))  // 大文字変換前にフィルタ
    .map(String::toUpperCase)
    .forEach(IO::println);
```

実行結果：
```
AVOCADO
```

絞り込み（`filter`）を変換（`map`）より先に行うと、条件が書きやすくなるうえ、変換する要素の数も減らせます。

## 終端操作の忘れ

#### エラー例

以下のコードは、終端操作を忘れてしまった場合の問題を示しています。

```java
// × 終端操作を忘れた場合
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
numbers.stream()
    .filter(n -> n % 2 == 0)
    .map(n -> n * 2); // 終端操作がない
```

##### 問題点
- 終端操作がないため、中間操作は実行されない（遅延評価）
- コンパイルエラーは発生しないが、期待した処理が行われない

#### 対処法

以下のコードは、終端操作を追加して問題を解決した例です。

```java
// ○ 終端操作を追加
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
List<Integer> evenDoubled = numbers.stream()
    .filter(n -> n % 2 == 0)
    .map(n -> n * 2)
    .collect(Collectors.toList()); // 終端操作
```

## 並列処理での問題

#### エラー例

以下のコードは、並列処理でのスレッドセーフティの問題を示しています。

```java
// × 並列処理での共有状態の変更
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
List<Integer> results = new ArrayList<>(); // 非同期安全でない

numbers.parallelStream()
    .filter(n -> n % 2 == 0)
    .forEach(n -> results.add(n * 2)); // 競合状態が発生
```

##### 問題点
- 複数のスレッドが同時に`ArrayList`に追加操作を行う
- データの破損や`ConcurrentModificationException`の発生

#### 対処法

以下のコードは、並列処理で安全にデータを収集する方法を示しています。

```java
// ○ 並列処理に適した方法
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// 方法1: collectを使用
List<Integer> results = numbers.parallelStream()
    .filter(n -> n % 2 == 0)
    .map(n -> n * 2)
    .collect(Collectors.toList());

// 方法2: 同期化されたコレクション
List<Integer> results = Collections.synchronizedList(new ArrayList<>());
numbers.parallelStream()
    .filter(n -> n % 2 == 0)
    .forEach(n -> results.add(n * 2));
```

## Optionalの誤用

#### エラー例

以下のコードは、Optionalの誤った使用方法の例です。

```java
// × Optionalの誤った使用
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
Optional<String> result = names.stream()
    .filter(name -> name.startsWith("D"))
    .findFirst();

if (result.isPresent()) {
    IO.println(result.get());
} else {
    IO.println("Not found");
}
```

##### 問題点
- `isPresent()`と`get()`の組み合わせは冗長
- Optionalの利点が活かされていない

#### 対処法

以下のコードは、Optionalの正しい使用方法を示しています。

```java
// ○ Optionalの正しい使用
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
names.stream()
    .filter(name -> name.startsWith("D"))
    .findFirst()
    .ifPresentOrElse(
        IO::println,
        () -> IO.println("Not found")
    );

// またはデフォルト値を使用
String result = names.stream()
    .filter(name -> name.startsWith("D"))
    .findFirst()
    .orElse("Not found");
```

## パフォーマンスの問題

#### エラー例

以下のコードは、非効率なStream操作の例です。

```java
// × 不効率なStream操作
List<Integer> numbers = IntStream.range(1, 1000000)
    .boxed()
    .collect(Collectors.toList());

// 毎回新しいStreamを作成
int sum = numbers.stream()
    .filter(n -> n % 2 == 0)
    .mapToInt(Integer::intValue)
    .sum();

int count = numbers.stream()
    .filter(n -> n % 2 == 0)
    .mapToInt(Integer::intValue)
    .reduce(0, Integer::sum);
```

##### 問題点
- 同じフィルタリング処理を複数回実行
- プリミティブ型の不要なボクシング/アンボクシング

#### 対処法

以下のコードは、パフォーマンスを改善した処理の例です。

```java
// ○ 効率的なStream操作
// 最初からIntStreamを使用
IntStream numbers = IntStream.range(1, 1000000);

// 単一のStreamで複数の結果を取得
IntSummaryStatistics stats = numbers
    .filter(n -> n % 2 == 0)
    .summaryStatistics();

IO.println("Sum: " + stats.getSum());
IO.println("Count: " + stats.getCount());
```

## 例外処理の問題

この項のサンプルは、第13章「例外処理」で学ぶチェック例外と、第14章「ファイルI/O」で学ぶ`Files.readString`を使います。
ここでは、ラムダ式の本体からはチェック例外をそのまま投げられない、という点だけを押さえてください。

#### エラー例

以下のコードは、Stream内での例外処理の問題を示しています。

```java
// × Stream内での例外処理の問題
List<String> files = Arrays.asList("file1.txt", "file2.txt", "nonexistent.txt");
List<String> contents = files.stream()
    .map(file -> {
        return Files.readString(Path.of(file)); // IOException
    })
    .collect(Collectors.toList());
```

##### エラーメッセージ
```
error: unreported exception IOException; must be caught or declared to be thrown
```

#### 対処法

以下のコードは、Stream内での例外を適切に処理する方法を示しています。

```java
// ○ Stream内での確実な例外処理
List<String> files = Arrays.asList("file1.txt", "file2.txt", "nonexistent.txt");

// 方法1: 例外を無視してフィルタ
List<String> contents = files.stream()
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

// 方法2: 例外をランタイム例外でラップ
List<String> contents = files.stream()
    .map(file -> {
        try {
            return Files.readString(Path.of(file));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    })
    .collect(Collectors.toList());
```

## 共通の対処戦略

1. Stream操作の順序を最適化する
    + フィルタリングは早い段階で実行し、処理量を減らす
2. 終端操作を必ず含める
    + 中間操作だけでは実際の処理が行われない
3. 並列処理の制限を理解する
    + 共有状態の変更を避け、スレッドセーフな操作を使用
4. Optionalを効果的に活用する
    + `isPresent()`と`get()`の組み合わせを避ける
5. プリミティブ型Streamを使用する
    + 数値計算処理では`IntStream`、`LongStream`、`DoubleStream`を使用
6. 例外処理を確実に行う
    + チェック例外は事前に処理するか、ランタイム例外でラップする
7. パフォーマンステストを実施する
    + 大量データでの並列処理の効果を測定する
