# 第11章 チャレンジ課題：ラムダ式と関数型インターフェイス

## 概要
関数型プログラミングの高度な概念を実装する挑戦的な課題です。カリー化、部分適用、モナド的な操作などを通じて、関数型プログラミングの真髄を体験します。

## 課題一覧

### 課題1: 関数型パーサーコンビネータの実装
`ParserCombinator.java`を作成し、以下を実装してください：

簡単なパーサーコンビネータライブラリを作成します。

```java
@FunctionalInterface
public interface Parser<T> {
    Optional<Pair<T, String>> parse(String input);
    
    // コンビネータメソッド
    default <U> Parser<U> map(Function<T, U> f) {
        return input -> this.parse(input)
            .map(pair -> Pair.of(f.apply(pair.getFirst()), pair.getSecond()));
    }
    
    default <U> Parser<U> flatMap(Function<T, Parser<U>> f) {
        // 実装してください
    }
    
    default Parser<T> or(Parser<T> other) {
        // 実装してください
    }
}
```

実装すべきパーサー：
1. 文字パーサー：特定の文字を認識
2. 数字パーサー：数字を認識してIntegerに変換
3. 文字列パーサー：特定の文字列を認識
4. 繰り返しパーサー：パーサーを0回以上繰り返す
5. 連結パーサー：複数のパーサーを順番に適用

使用例：
```java
// "123+456"のような式をパースする
Parser<Integer> number = digit.many1().map(digits -> 
    Integer.parseInt(String.join("", digits)));
    
Parser<Expression> expr = number
    .flatMap(left -> character('+')
        .flatMap(op -> number
            .map(right -> new Addition(left, right))));
```

### 課題2: リアクティブストリームの簡易実装
`ReactiveStream.java`を作成し、以下を実装してください：

```java
public class Observable<T> {
    private List<Consumer<T>> observers = new ArrayList<>();
    
    public void subscribe(Consumer<T> observer) {
        observers.add(observer);
    }
    
    public void emit(T value) {
        observers.forEach(observer -> observer.accept(value));
    }
    
    // 実装すべきメソッド
    public <R> Observable<R> map(Function<T, R> mapper) {
        // 新しいObservableを返し、元のObservableの値を変換
    }
    
    public Observable<T> filter(Predicate<T> predicate) {
        // 条件に合う値のみを通す新しいObservableを返す
    }
    
    public <R> Observable<R> flatMap(Function<T, Observable<R>> mapper) {
        // 各値に対してObservableを生成し、それらを平坦化
    }
    
    public Observable<T> debounce(long milliseconds) {
        // 指定時間内の連続した値を無視
    }
}
```

使用例：
```java
Observable<String> userInput = new Observable<>();
userInput
    .debounce(300)
    .filter(text -> text.length() >= 3)
    .flatMap(text -> searchAPI(text))
    .subscribe(results -> updateUI(results));
```

## 実装のヒント

### パーサーコンビネータの基本
```java
// 基本的なパーサーの実装
public static Parser<Character> character(char c) {
    return input -> {
        if (input.isEmpty() || input.charAt(0) != c) {
            return Optional.empty();
        }
        return Optional.of(Pair.of(c, input.substring(1)));
    };
}
```

### リアクティブプログラミングの概念
```java
// バックプレッシャーの考慮
public class BackpressureObservable<T> extends Observable<T> {
    private final int bufferSize;
    private final Queue<T> buffer = new LinkedList<>();
    
    public void emit(T value) {
        if (buffer.size() < bufferSize) {
            buffer.offer(value);
            processBuffer();
        }
    }
}
```

## 提出前チェックリスト
- [ ] すべての必須メソッドが実装されている
- [ ] 適切な例外処理が実装されている
- [ ] スレッドセーフティが考慮されている（必要な場合）
- [ ] 包括的なテストケースが作成されている

## 評価基準
- 関数型プログラミングの高度な概念を理解しているか
- 抽象的な概念を具体的なコードに落とし込めているか
- エッジケースを適切に処理できているか
- コードの拡張性と再利用性が高いか

## ボーナス課題
時間に余裕がある場合は、以下の追加実装に挑戦してください：
- パーサーコンビネータでJSON パーサーを実装
- リアクティブストリームで複数のObservableを結合する`merge`や`zip`メソッドを実装
- 関数のメモ化（memoization）を汎用的に実装するユーティリティを作成