# 第10章 ジェネリクス：よくあるエラーと対処法

書籍『Java入門』第10章「ジェネリクス」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。

ジェネリクスの学習で遭遇する典型的なエラーと、その対処法について説明します。

## Raw typeの使用エラー

#### エラー例

ジェネリクスを使用せずにRaw typeでコレクションを使用した場合の問題を示しています。

```java
// × Raw typeの使用
List list = new ArrayList();
list.add("Hello");
String str = (String) list.get(0); // キャストが必要
```

#### 警告メッセージ
```
Note: GenericsExample.java uses unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for more details.
```

#### 対処法

正しいジェネリクスの使用方法です。

```java
// ○ ジェネリクスを使用
List<String> list = new ArrayList<>();
list.add("Hello");
String str = list.get(0); // キャスト不要
```

## 型パラメータの不適切な使用

#### エラー例

型パラメータを直接インスタンス化しようとした例です。

```java
// × 不適切な使用例
public class Container<T> {
    private T value;
    
    public void setNewValue() {
        this.value = new T(); // コンパイルエラー
    }
}
```

#### エラーメッセージ
```
error: unexpected type
  required: class
  found:    type parameter T
```

#### 対処法

ファクトリーメソッドを使用した正しい実装方法です。
`Supplier<T>`は「`T`型の値を1つ作って返す」処理を表すインターフェイスで、第11章で学ぶラムダ式と組み合わせて`new Container<>(() -> new StringBuilder())`のように使います。

```java
// ○ ファクトリーメソッドを使用
public class Container<T> {
    private T value;
    private Supplier<T> factory;
    
    public Container(Supplier<T> factory) {
        this.factory = factory;
    }
    
    public void setNewValue() {
        this.value = factory.get();
    }
}
```

## ワイルドカードの誤用

#### エラー例

上限境界ワイルドカードで要素を追加しようとした例です。

```java
// × ワイルドカードの誤用
List<? extends Number> numbers = new ArrayList<Integer>();
numbers.add(1); // コンパイルエラー
```

#### エラーメッセージ
```
error: incompatible types: int cannot be converted to CAP#1
  where CAP#1 is a fresh type-variable:
    CAP#1 extends Number from capture of ? extends Number
```

#### 対処法

ワイルドカードの正しい使用方法です。

```java
// ○ 適切な使用例
List<Integer> integers = new ArrayList<>();
integers.add(1);
List<? extends Number> numbers = integers; // 読み取り専用として使用
```

## 型消去に関する問題

#### エラー例

ジェネリクスで配列を作成しようとした際の問題です。

```java
// × 型消去による問題
public class GenericArray<T> {
    private T[] array;
    
    public GenericArray(int size) {
        array = new T[size]; // コンパイルエラー
    }
}
```

#### エラーメッセージ
```
error: generic array creation
```

#### 対処法

型消去を考慮した正しい実装方法です。

```java
// ○ 型消去を考慮した実装
public class GenericArray<T> {
    private T[] array;
    
    @SuppressWarnings("unchecked")
    public GenericArray(Class<T> clazz, int size) {
        array = (T[]) Array.newInstance(clazz, size);
    }
}
```

## 境界型パラメータの問題

#### エラー例

境界型パラメータで算術演算を直接実行しようとした例です。

```java
// × 境界型パラメータの誤用
public class NumberContainer<T extends Number> {
    private T value;
    
    public void multiplyBy(T factor) {
        this.value = this.value * factor; // コンパイルエラー
    }
}
```

#### エラーメッセージ
```
error: bad operand types for binary operator '*'
  first type:  T
  second type: T
```

#### 対処法

境界型パラメータの正しい使用方法です。

コンパイルできる全文：[chapter10-code/NumberContainer/](chapter10-code/NumberContainer/)

```java
// ○ 適切な境界型パラメータ
public class NumberContainer<T extends Number> {
    private T value;
    
    public void multiplyBy(double factor) {
        // Number型の共通メソッドを使用
        double result = this.value.doubleValue() * factor;
        // 結果の設定は型に応じて処理
    }
}
```

## var型推論の誤用

#### エラー例

var型推論を不適切に使用した例です。

```java
// × var型推論の誤用
var list = new ArrayList(); // Raw typeになる
var value = null; // コンパイルエラー
```

#### エラーメッセージ
```
error: cannot infer type for local variable value
  (variable initializer is 'null')
```

#### 対処法

var型推論の正しい使用方法です。

```java
// ○ 適切なvar使用例
var list = new ArrayList<String>(); // 型パラメータを明示
String value = null; // 明示的な型宣言
```

## 共通の対処戦略

1. コンパイル時の警告を無視しない
    + `-Xlint:unchecked`オプションを使用して詳細な警告を確認する
2. 型パラメータの制約を理解する
    + 何ができて何ができないかを明確に把握する
3. 型安全性を保つ設計パターンを使用する
    + Factory Method、Builder、Strategyパターンなどの活用
4. IDEの支援を活用する
    + Eclipse、IntelliJ IDEAなどの自動補完とエラー検出機能を使用
5. 段階的なリファクタリング
    + 既存のraw typeコードを少しずつジェネリクスに移行する
