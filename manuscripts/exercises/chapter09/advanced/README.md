# 第9章 発展課題：ジェネリクスの高度な活用

## 課題概要

ジェネリクスの高度な機能を活用し、型安全で柔軟なライブラリレベルのコードを実装します。実務で必要となる複雑な型関係の設計能力を養います。

## 課題1：型安全な異種コンテナ

### 目的
- 型トークンパターンの理解
- Class<T>を使った型安全性の確保
- 異なる型を安全に格納する設計

### 要求仕様

1. **`TypeSafeMap`の実装**
   ```java
   public class TypeSafeMap {
       private Map<Class<?>, Object> map = new HashMap<>();
       
       public <T> void put(Class<T> type, T value) {
           map.put(type, value);
       }
       
       public <T> T get(Class<T> type) {
           return type.cast(map.get(type));
       }
       
       public <T> T getOrDefault(Class<T> type, T defaultValue) {
           // 実装
       }
   }
   ```

2. **複数値の格納**
   ```java
   public class MultiValueTypeMap {
       private Map<Class<?>, List<?>> map = new HashMap<>();
       
       public <T> void add(Class<T> type, T value) {
           // 型安全にリストに追加
       }
       
       public <T> List<T> getAll(Class<T> type) {
           // 型安全にリストを返す
       }
       
       public <T> Stream<T> stream(Class<T> type) {
           // 型安全なストリームを返す
       }
   }
   ```

3. **階層型の型安全マップ**
   ```java
   public class HierarchicalTypeMap {
       // スーパークラスで登録した値をサブクラスでも取得可能
       public <T> void register(Class<T> type, T value) { }
       
       public <T> T find(Class<T> type) {
           // typeまたはそのスーパークラスで登録された値を返す
       }
       
       public <T> List<T> findAll(Class<T> type) {
           // typeのサブクラスで登録されたすべての値を返す
       }
   }
   ```

### 実装のヒント
```java
// 型安全なキャスト
@SuppressWarnings("unchecked")
private <T> List<T> getListForType(Class<T> type) {
    List<?> list = map.get(type);
    if (list == null) {
        list = new ArrayList<T>();
        map.put(type, list);
    }
    return (List<T>) list;
}
```

### 評価ポイント
- 型トークンパターンの正しい理解
- 型安全性の完全な保証
- 継承関係を考慮した設計
- パフォーマンスの考慮

## 課題2：ジェネリックビルダーパターン

### 目的
- 再帰的型境界の活用
- 流暢なAPIの設計
- 型安全なビルダーパターン

### 要求仕様

1. **基本的なビルダー**
   ```java
   public abstract class AbstractBuilder<T, B extends AbstractBuilder<T, B>> {
       protected abstract B self();
       
       public abstract T build();
       
       // 共通のビルダーメソッド
       public B withId(String id) {
           // 実装
           return self();
       }
   }
   ```

2. **階層的なビルダー**
   ```java
   // 基底クラス
   public abstract class Vehicle {
       public abstract static class Builder<T extends Vehicle, B extends Builder<T, B>> 
               extends AbstractBuilder<T, B> {
           protected String brand;
           protected String model;
           
           public B brand(String brand) {
               this.brand = brand;
               return self();
           }
           
           public B model(String model) {
               this.model = model;
               return self();
           }
       }
   }
   
   // 具象クラス
   public class Car extends Vehicle {
       private final int doors;
       
       public static class Builder extends Vehicle.Builder<Car, Builder> {
           private int doors;
           
           @Override
           protected Builder self() {
               return this;
           }
           
           public Builder doors(int doors) {
               this.doors = doors;
               return this;
           }
           
           @Override
           public Car build() {
               return new Car(this);
           }
       }
   }
   ```

3. **条件付きビルダー**
   ```java
   public interface Validator<T> {
       void validate(T object) throws ValidationException;
   }
   
   public abstract class ValidatingBuilder<T, B extends ValidatingBuilder<T, B>> 
           extends AbstractBuilder<T, B> {
       private List<Validator<T>> validators = new ArrayList<>();
       
       public B addValidator(Validator<T> validator) {
           validators.add(validator);
           return self();
       }
       
       @Override
       public T build() {
           T object = doBuild();
           validators.forEach(v -> v.validate(object));
           return object;
       }
       
       protected abstract T doBuild();
   }
   ```

### 評価ポイント
- 再帰的型境界の正しい使用
- 継承階層での型安全性
- 使いやすいAPIの設計
- バリデーション機能の統合

## 課題3：関数型データ構造

### 目的
- イミュータブルなジェネリックデータ構造
- 高階関数との組み合わせ
- 型安全な関数合成

### 要求仕様

1. **`Either<L, R>`型**
   ```java
   public abstract class Either<L, R> {
       // ファクトリメソッド
       public static <L, R> Either<L, R> left(L value) { }
       public static <L, R> Either<L, R> right(R value) { }
       
       // 判定メソッド
       public abstract boolean isLeft();
       public abstract boolean isRight();
       
       // 値の取得
       public abstract L getLeft();
       public abstract R getRight();
       
       // 関数型操作
       public abstract <T> Either<L, T> map(Function<R, T> mapper);
       public abstract <T> Either<T, R> mapLeft(Function<L, T> mapper);
       public abstract <T> Either<L, T> flatMap(Function<R, Either<L, T>> mapper);
       
       // パターンマッチング風
       public abstract <T> T fold(Function<L, T> leftMapper, Function<R, T> rightMapper);
   }
   ```

2. **`Try<T>`型**
   ```java
   public abstract class Try<T> {
       public static <T> Try<T> of(Supplier<T> supplier) {
           try {
               return new Success<>(supplier.get());
           } catch (Exception e) {
               return new Failure<>(e);
           }
       }
       
       public abstract boolean isSuccess();
       public abstract boolean isFailure();
       
       public abstract T get() throws Exception;
       public abstract T getOrElse(T defaultValue);
       public abstract <U> Try<U> map(Function<T, U> mapper);
       public abstract <U> Try<U> flatMap(Function<T, Try<U>> mapper);
       public abstract Try<T> recover(Function<Exception, T> recovery);
   }
   ```

3. **`Lazy<T>`型**
   ```java
   public class Lazy<T> {
       private volatile Supplier<T> supplier;
       private volatile T value;
       
       private Lazy(Supplier<T> supplier) {
           this.supplier = supplier;
       }
       
       public static <T> Lazy<T> of(Supplier<T> supplier) {
           return new Lazy<>(supplier);
       }
       
       public T get() {
           // スレッドセーフな遅延初期化
       }
       
       public <R> Lazy<R> map(Function<T, R> mapper) {
           return Lazy.of(() -> mapper.apply(get()));
       }
       
       public <R> Lazy<R> flatMap(Function<T, Lazy<R>> mapper) {
           return Lazy.of(() -> mapper.apply(get()).get());
       }
   }
   ```

### 評価ポイント
- 関数型プログラミングパターンの理解
- イミュータブルな設計
- スレッドセーフティ
- 使いやすく直感的なAPI

## 提出方法

1. 各課題の完全な実装とテストコード
2. 使用例とベンチマーク
3. 設計判断の説明ドキュメント
4. 既存ライブラリとの比較分析

## 発展学習の提案

- **型レベルプログラミング**：ファントム型、型証明
- **高カインド型**：他言語での実装との比較
- **モナド的な設計**：flatMapチェーンの活用
- **型クラス風パターン**：Javaでの制限と回避策

## 参考リソース

- Functional Programming in Java（Venkat Subramaniam）
- Category Theory for Programmers（Bartosz Milewski）
- Vavr（旧Javaslang）ライブラリのソースコード
- Effective Java 第3版（Joshua Bloch）