# 第11章 発展課題：ラムダ式と関数型インターフェイス

## 概要
基礎課題を完了した方向けの発展的な課題です。カスタム関数型インターフェイスの設計、高階関数の実装、実践的なラムダ式の活用を学びます。

## 課題一覧

### 課題1: カスタム関数型インターフェイスの設計
`CustomFunctionalInterfaces.java`を作成し、以下を実装してください：

1. **3引数の関数型インターフェイス**
   ```java
   @FunctionalInterface
   public interface TriFunction<T, U, V, R> {
       R apply(T t, U u, V v);
   }
   ```
   - 3つの数値の最大値を返す実装
   - 3つの文字列を結合する実装
   - デフォルトメソッドandThen()の実装

2. **例外を扱う関数型インターフェイス**
   ```java
   @FunctionalInterface
   public interface ThrowingFunction<T, R, E extends Exception> {
       R apply(T t) throws E;
   }
   ```
   - ファイル読み込みを行う実装
   - 例外をRuntimeExceptionでラップするヘルパーメソッド

### 課題2: 高階関数の実装
`HigherOrderFunctions.java`を作成し、以下を実装してください：

1. **関数を返す関数**
   - 指定された値を加算する関数を返すメソッド
   ```java
   public static Function<Integer, Integer> createAdder(int n)
   ```

2. **関数を引数に取る関数**
   - リストの各要素に関数を適用して新しいリストを返す
   ```java
   public static <T, R> List<R> map(List<T> list, Function<T, R> mapper)
   ```

3. **関数の合成**
   - 複数の関数を合成して新しい関数を作成
   ```java
   public static <T> Function<T, T> compose(Function<T, T>... functions)
   ```

### 課題3: 実践的なラムダ式の活用
`PracticalLambdaUsage.java`を作成し、以下を実装してください：

1. **遅延評価の実装**
   ```java
   public class LazyValue<T> {
       private Supplier<T> supplier;
       private T value;
       private boolean computed = false;
       
       // getメソッドで初めて値を計算
   }
   ```

2. **Builder パターンとラムダ式**
   ```java
   public class PersonBuilder {
       public PersonBuilder with(Consumer<PersonBuilder> builderFunction) {
           builderFunction.accept(this);
           return this;
       }
   }
   ```
   使用例：
   ```java
   Person person = new PersonBuilder()
       .with(b -> {
           b.name("Alice");
           b.age(25);
           b.email("alice@example.com");
       })
       .build();
   ```

## 実装のヒント

### カスタム関数型インターフェイスの設計指針
```java
@FunctionalInterface
public interface Combiner<T> {
    T combine(T a, T b);
    
    // デフォルトメソッドで機能を拡張
    default Combiner<T> andThen(Combiner<T> after) {
        return (a, b) -> after.combine(this.combine(a, b), b);
    }
}
```

### 高階関数のパターン
```java
// カリー化の実装例
public static Function<Integer, Function<Integer, Integer>> 
    curriedAdd = x -> y -> x + y;

// 使用方法
Function<Integer, Integer> add5 = curriedAdd.apply(5);
int result = add5.apply(3); // 8
```

## 提出前チェックリスト
- [ ] カスタム関数型インターフェイスに@FunctionalInterfaceを付与
- [ ] 高階関数が正しく動作することを確認
- [ ] ジェネリクスを適切に使用
- [ ] 例外処理が適切に実装されている

## 評価基準
- カスタム関数型インターフェイスの設計が適切か
- 高階関数の概念を理解し実装できているか
- 実践的な問題をラムダ式で解決できているか
- コードの再利用性と拡張性