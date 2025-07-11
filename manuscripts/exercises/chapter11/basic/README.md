# 第11章 基礎課題：ラムダ式と関数型インターフェイス

## 概要
本章で学んだラムダ式と関数型インターフェイスの基本的な使い方を練習します。標準関数型インターフェイスの活用とラムダ式の基本構文を身につけましょう。

## 課題一覧

### 課題1: 標準関数型インターフェイスの基本
`BasicFunctionalInterfaces.java`を作成し、以下の機能を実装してください：

1. **Function<T,R>の活用**
   - 文字列を大文字に変換する関数
   - 整数を2倍にする関数
   - 2つの関数を合成して実行

2. **Predicate<T>の活用**
   - 偶数判定を行うPredicate
   - 文字列が5文字以上かを判定するPredicate
   - 2つのPredicateをAND/ORで組み合わせる

3. **Consumer<T>の活用**
   - リストの各要素を出力するConsumer
   - オブジェクトの状態を変更するConsumer

4. **Supplier<T>の活用**
   - 現在時刻を返すSupplier
   - ランダムな数値を生成するSupplier

### 課題2: ラムダ式の基本構文
`LambdaSyntaxPractice.java`を作成し、以下を実装してください：

1. **様々な形式のラムダ式**
   - 引数なし：`() -> "Hello"`
   - 引数1つ：`x -> x * 2`
   - 引数2つ：`(x, y) -> x + y`
   - ブロック形式：`x -> { return x * x; }`

2. **型推論の理解**
   - 明示的な型指定：`(Integer x) -> x * 2`
   - 型推論を使用：`x -> x * 2`

### 課題3: コレクションとラムダ式
`CollectionLambdaPractice.java`を作成し、以下を実装してください：

1. **リストの操作**
   ```java
   List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");
   ```
   - forEach()を使って全要素を出力
   - removeIf()を使って条件に合う要素を削除
   - sort()を使ってカスタムソート

2. **マップの操作**
   ```java
   Map<String, Integer> scores = new HashMap<>();
   ```
   - computeIfAbsent()を使った値の計算
   - merge()を使った値の更新

### 課題4: メソッド参照の基本
`MethodReferencePractice.java`を作成し、以下を実装してください：

1. **静的メソッド参照**
   - `Integer::parseInt`の使用例
   - 自作の静的メソッドへの参照

2. **インスタンスメソッド参照**
   - `String::toUpperCase`の使用例
   - 特定インスタンスのメソッド参照

3. **コンストラクタ参照**
   - `ArrayList::new`の使用例
   - 自作クラスのコンストラクタ参照

## 実装のヒント

### ラムダ式の基本形
```java
// 基本的なラムダ式
Function<String, Integer> strLength = s -> s.length();

// ブロック形式（複数行の処理）
Function<String, String> process = s -> {
    String trimmed = s.trim();
    String upper = trimmed.toUpperCase();
    return upper;
};
```

### メソッド参照の変換
```java
// ラムダ式
list.forEach(s -> System.out.println(s));

// メソッド参照に変換
list.forEach(System.out::println);
```

## 提出前チェックリスト
- [ ] すべてのクラスにJavadocコメントを記述
- [ ] 各メソッドに適切なコメントを追加
- [ ] コンパイルエラーがないことを確認
- [ ] 実行して期待通りの結果が出ることを確認

## 評価基準
- 標準関数型インターフェイスを正しく使用できているか
- ラムダ式の構文を正しく理解しているか
- メソッド参照を適切に使用できているか
- コードの可読性と適切なコメント