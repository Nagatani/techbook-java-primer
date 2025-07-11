# 第9章 基礎課題：ジェネリクス

## 課題概要

本章で学んだジェネリクスの基本概念を実践的に理解します。型安全で再利用可能なクラスとメソッドの設計・実装能力を養います。

## 課題1：汎用ペアクラスの実装

### 目的
- 基本的なジェネリッククラスの作成
- 型パラメータの理解
- ジェネリックメソッドの実装

### 要求仕様

1. **`Pair<T, U>`クラスの実装**
   ```java
   public class Pair<T, U> {
       private T first;
       private U second;
       
       // コンストラクタ
       public Pair(T first, U second) {
           this.first = first;
           this.second = second;
       }
       
       // getter/setter
       // equals/hashCode/toString
   }
   ```

2. **ユーティリティメソッド**
   - 要素の入れ替え：`swap()`
   - ファクトリメソッド：`static <T, U> Pair<T, U> of(T first, U second)`
   - 変換メソッド：`<V, W> Pair<V, W> map(Function<T, V> f1, Function<U, W> f2)`

3. **使用例**
   ```java
   // 基本的な使用
   Pair<String, Integer> nameAge = new Pair<>("Alice", 25);
   
   // ファクトリメソッド
   Pair<String, Double> scoreData = Pair.of("Math", 95.5);
   
   // 変換
   Pair<String, String> converted = nameAge.map(
       name -> name.toUpperCase(),
       age -> age.toString()
   );
   ```

### 実装のヒント
- ダイヤモンド演算子の活用
- null安全性の考慮
- イミュータブルな設計も検討

### 評価ポイント
- 型パラメータの正しい使用
- ジェネリックメソッドの実装
- equals/hashCodeの適切な実装
- 使いやすいAPIの設計

## 課題2：型安全なコンテナクラス

### 目的
- 境界のある型パラメータの理解
- ワイルドカードの基本的な使用
- 型安全性の確保

### 要求仕様

1. **`Container<T>`インターフェイス**
   ```java
   public interface Container<T> {
       void add(T item);
       T get(int index);
       int size();
       List<T> getAll();
   }
   ```

2. **特殊なコンテナの実装**
   - `NumberContainer<T extends Number>`：数値専用コンテナ
   - `ComparableContainer<T extends Comparable<T>>`：比較可能要素のコンテナ

3. **NumberContainerの追加機能**
   ```java
   public class NumberContainer<T extends Number> implements Container<T> {
       private List<T> items = new ArrayList<>();
       
       // Container<T>のメソッド実装
       
       // 数値専用メソッド
       public double sum() {
           // すべての要素の合計
       }
       
       public double average() {
           // 平均値の計算
       }
       
       public T max() {
           // 最大値（Comparatorを使用）
       }
   }
   ```

### 実装のヒント
```java
// ワイルドカードを使ったユーティリティメソッド
public static double sumAll(Container<? extends Number> container) {
    double sum = 0;
    for (int i = 0; i < container.size(); i++) {
        sum += container.get(i).doubleValue();
    }
    return sum;
}
```

### 評価ポイント
- 境界付き型パラメータの適切な使用
- Number型のメソッド活用
- ワイルドカードの理解
- 型安全性の維持

## 課題3：ジェネリックスタック

### 目的
- 既存のデータ構造のジェネリック化
- 配列とジェネリクスの制限事項の理解
- 型消去の影響を考慮した実装

### 要求仕様

1. **`GenericStack<T>`クラス**
   ```java
   public class GenericStack<T> {
       private static final int DEFAULT_CAPACITY = 10;
       private Object[] elements; // 型消去のため、T[]は作成できない
       private int size = 0;
       
       public void push(T item) { }
       public T pop() { }
       public T peek() { }
       public boolean isEmpty() { }
   }
   ```

2. **追加機能**
   - 容量の自動拡張
   - `pushAll(Collection<? extends T> collection)`
   - `popAll(Collection<? super T> collection)`
   - イテレータの実装

3. **型制約付きスタック**
   ```java
   public class ComparableStack<T extends Comparable<T>> 
           extends GenericStack<T> {
       public T min() {
           // スタック内の最小値を返す（空の場合は例外）
       }
       
       public List<T> getSorted() {
           // ソートされたリストを返す（スタックは変更しない）
       }
   }
   ```

### 実装のヒント
```java
// PECS (Producer Extends, Consumer Super) の原則
public void pushAll(Collection<? extends T> src) {
    for (T item : src) {
        push(item);
    }
}

public void popAll(Collection<? super T> dst) {
    while (!isEmpty()) {
        dst.add(pop());
    }
}
```

### 評価ポイント
- 配列作成の制限への対処
- ワイルドカードの適切な使用（PECS原則）
- 型安全なキャスト
- 境界チェックとエラーハンドリング

## 課題4：型変換ユーティリティ

### 目的
- ジェネリックメソッドの高度な使用
- 型推論の理解
- 実用的なユーティリティの作成

### 要求仕様

1. **`TypeConverter`クラス**
   ```java
   public class TypeConverter {
       // リストの型変換
       public static <T, R> List<R> convertList(
           List<T> source, 
           Function<T, R> converter) {
           // 実装
       }
       
       // マップの値の型変換
       public static <K, V, R> Map<K, R> convertMapValues(
           Map<K, V> source,
           Function<V, R> converter) {
           // 実装
       }
       
       // 条件付き変換
       public static <T, R> List<R> convertIf(
           List<T> source,
           Predicate<T> condition,
           Function<T, R> converter) {
           // 実装
       }
   }
   ```

2. **複雑な変換**
   ```java
   // ネストしたコレクションの変換
   public static <T, R> List<List<R>> convertNested(
       List<List<T>> source,
       Function<T, R> converter) {
       // 実装
   }
   
   // 複数の型を統一
   public static <T> List<T> merge(
       List<? extends T> list1,
       List<? extends T> list2) {
       // 実装
   }
   ```

3. **使用例**
   ```java
   // 文字列を整数に変換
   List<String> strings = Arrays.asList("1", "2", "3");
   List<Integer> integers = TypeConverter.convertList(
       strings, Integer::parseInt);
   
   // 条件付き変換
   List<String> filtered = TypeConverter.convertIf(
       integers,
       n -> n > 1,
       String::valueOf);
   ```

### 評価ポイント
- ジェネリックメソッドの設計
- 関数型インターフェイスの活用
- 型推論を活用した使いやすいAPI
- null安全性とエラーハンドリング

## 提出方法

1. 各課題のソースコードとテストコード
2. 使用例を含むMainクラス
3. ジェネリクスを使用した利点の説明
4. 遭遇した型関連の問題と解決方法

## 発展学習の提案

- **型トークン**：Class<T>を使った実行時の型情報保持
- **再帰的型境界**：`<T extends Comparable<T>>`の深い理解
- **ジェネリックシングルトン**：型安全なシングルトンパターン
- **ビルダーパターン**：ジェネリクスを使った流暢なAPI設計

## 参考リソース

- Effective Java（Joshua Bloch）：項目26-33
- Java Generics and Collections（Maurice Naftalin、Philip Wadler）
- Oracle公式チュートリアル：Generics