# 第9章 解答例

## 概要

第9章では、Javaのジェネリクスについて学習します。型安全性の確保、ワイルドカードの活用、型消去の理解などを重視した実装を行います。

## 解答例一覧

### 1. 汎用ボックス (GenericBox系)

- **GenericBox.java**: ジェネリクスを活用した汎用ボックスクラス
- **GenericBoxTest.java**: GenericBoxクラスのテスト

#### 学習ポイント
- 基本的なジェネリクスの使用方法
- 境界ワイルドカード（? extends T, ? super T）
- ジェネリクスメソッドの実装
- 型安全なキャストと型チェック

### 2. ペアクラス (Pair系)

- **Pair.java**: ジェネリクスを使った汎用ペアクラス
- **PairTest.java**: Pairクラスのテスト

#### 学習ポイント
- 複数の型パラメータの使用
- 関数型インターフェースとの組み合わせ
- イミュータブルな設計
- ファクトリーメソッドの活用

### 3. ジェネリックスタック (GenericStack系)

- **GenericStack.java**: ジェネリクスを使ったスタッククラス
- **GenericStackTest.java**: GenericStackクラスのテスト

#### 学習ポイント
- コレクションのラッパークラス実装
- 関数型プログラミングとの統合
- Iterableインターフェースの実装
- @SafeVarargsアノテーションの使用

### 4. 型安全ビルダー (TypeSafeBuilder系)

- **TypeSafeBuilder.java**: 型安全なビルダーパターンの実装
- **TypeSafeBuilderTest.java**: TypeSafeBuilderクラスのテスト

#### 学習ポイント
- ビルダーパターンとジェネリクスの組み合わせ
- フルーエントインターフェースの実装
- 型安全性を保ったメソッドチェーン
- バリデーション機能の組み込み

## ジェネリクスの重要概念

### 1. 型パラメータ

```java
public class GenericBox<T> {
    private T content;
    
    public void setContent(T content) {
        this.content = content;
    }
    
    public T getContent() {
        return content;
    }
}
```

### 2. 境界ワイルドカード

#### 共変性（? extends T）
```java
// Tのサブタイプを受け入れる
public <U extends T> void copyFrom(GenericBox<U> other) {
    if (!other.isEmpty()) {
        setContent(other.getContent());
    }
}
```

#### 反変性（? super T）
```java
// Tのスーパータイプを受け入れる
public <U super T> void copyTo(GenericBox<U> other) {
    if (!isEmpty()) {
        other.setContent(getContent());
    }
}
```

### 3. ジェネリクスメソッド

```java
public <R> R applyOperation(Function<T, R> operation) {
    if (content == null) {
        return null;
    }
    return operation.apply(content);
}
```

### 4. 型安全なキャスト

```java
public <U> U getContentAs(Class<U> clazz) {
    if (content == null) {
        return null;
    }
    if (clazz.isInstance(content)) {
        return clazz.cast(content);
    }
    throw new ClassCastException("Cannot cast to " + clazz);
}
```

### 5. @SafeVarargs

```java
@SafeVarargs
public static <T> GenericStack<T> of(T... items) {
    GenericStack<T> stack = new GenericStack<>();
    for (T item : items) {
        stack.push(item);
    }
    return stack;
}
```

## 設計パターンとの組み合わせ

### 1. ファクトリーパターン

```java
public static <T> GenericBox<T> of(T value, Class<T> type) {
    GenericBox<T> box = new GenericBox<>(type);
    box.setContent(value);
    return box;
}
```

### 2. ビルダーパターン

```java
public static PersonBuilder personBuilder() {
    return new PersonBuilder();
}

// メソッドチェーンで型安全な構築
Person person = TypeSafeBuilder.personBuilder()
    .name("田中太郎")
    .age(30)
    .email("tanaka@example.com")
    .build();
```

### 3. フルーエントインターフェース

```java
// 段階的な構築を強制する型安全なインターフェース
Person person = TypeSafeBuilder.fluentPerson()
    .name("鈴木次郎")     // NameStep を返す
    .age(35)             // AgeStep を返す
    .email("suzuki@example.com")  // EmailStep を返す
    .build();            // Person を返す
```

## 実行方法

### テストの実行

```bash
# 全テストの実行
./gradlew test

# 特定の章のテストのみ実行
./gradlew test --tests "chapter09.*"
```

### 個別クラスの実行

```bash
# GenericBoxのテスト
java -cp build/classes/java/main:build/classes/java/test chapter09.solutions.GenericBoxTest

# TypeSafeBuilderのテスト
java -cp build/classes/java/main:build/classes/java/test chapter09.solutions.TypeSafeBuilderTest
```

## 重要なポイント

### 1. 型安全性の確保

- コンパイル時に型エラーを検出
- ClassCastExceptionを防ぐ
- 型情報の保持と活用

### 2. パフォーマンスの考慮

- 型消去によるランタイムオーバーヘッドの削減
- ボクシング/アンボクシングの最小化
- 適切な境界の設定

### 3. 可読性と保守性

- 明確な型情報による可読性向上
- 型安全なAPIの提供
- ドキュメントとしての型情報

### 4. ワイルドカードの使い分け

- **? extends T**: 読み取り専用（Producer）
- **? super T**: 書き込み専用（Consumer）
- **PECS原則**: Producer Extends, Consumer Super

## よくある落とし穴

### 1. 型消去

```java
// 実行時には型情報が失われる
List<String> strings = new ArrayList<>();
List<Integer> integers = new ArrayList<>();
// strings.getClass() == integers.getClass() // true
```

### 2. 配列との組み合わせ

```java
// コンパイルエラー
// List<String>[] arrays = new List<String>[10];

// 回避策
List<String>[] arrays = (List<String>[]) new List[10];
```

### 3. 静的コンテキスト

```java
public class GenericClass<T> {
    // エラー: 静的メンバでは型パラメータを使用できない
    // private static T staticField;
    
    // OK: ジェネリクスメソッドとして定義
    public static <U> void staticMethod(U parameter) {
        // ...
    }
}
```

## 発展的な学習

1. **高階カインド**: より複雑な型システムの理解
2. **型推論**: Diamond演算子やvarの活用
3. **ジェネリクスとリフレクション**: TypeTokenパターンの応用
4. **関数型プログラミング**: ジェネリクスと関数型インターフェースの組み合わせ

これらの解答例を通じて、Javaのジェネリクスシステムを深く理解し、型安全で再利用可能なコードを書く技術を身につけることができます。