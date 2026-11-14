# 第10章 ジェネリクス：よくあるエラーと対処法

書籍『Java入門』第10章「ジェネリクス」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。

ジェネリクスの学習で遭遇する典型的なエラーと、その対処法について説明します。

## Raw typeの使用エラー

### エラー例

ジェネリクスを使用せずにRaw typeでコレクションを使用した場合の問題を示しています。

```java
// × Raw typeの使用
List list = new ArrayList();
list.add("Hello");
String str = (String) list.get(0); // キャストが必要
```

### 警告メッセージ

```
Note: GenericsExample.java uses unchecked or unsafe operations.
Note: Recompile with -Xlint:unchecked for details.
```

### 対処法

正しいジェネリクスの使用方法です。

```java
// ○ ジェネリクスを使用
List<String> list = new ArrayList<>();
list.add("Hello");
String str = list.get(0); // キャスト不要
```

## 型パラメーターの不適切な使用

### エラー例1：型パラメーターのインスタンスを作る

型パラメーターを直接インスタンス化しようとした例です。

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

値を作る処理を外から受け取る実装方法です。
`Supplier<T>`は「`T`型の値を1つ作って返す」処理を表すインターフェイスで、第11章で学ぶラムダ式と組み合わせて`new Container<>(() -> new StringBuilder())`のように使います。

```java
import java.util.function.Supplier;

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

### エラー例2：型パラメーターの配列を作る

型パラメーターを要素の型にした配列は、`new`で作れません。

```java
// × 型パラメーターの配列の生成
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

実行時には`T`が何の型かわからないため、要素の型を表す`Class`オブジェクトを受け取って配列を作ります。
多くの場合は、配列の代わりに`List<T>`を使うほうが簡単です。

```java
import java.lang.reflect.Array;

// ○ 要素の型をClassオブジェクトで受け取って配列を作る
public class GenericArray<T> {
    private T[] array;
    
    @SuppressWarnings("unchecked")
    public GenericArray(Class<T> clazz, int size) {
        array = (T[]) Array.newInstance(clazz, size);
    }
}
```

### エラー例3：staticメンバーでクラスの型パラメーターを使う

クラスの型パラメーター`T`は、インスタンスごとに決まります（`Container<String>`と`Container<Integer>`）。
インスタンスに属さないstaticメンバーでは、`T`を使えません。

```java
// × staticメンバーでクラスの型パラメーターを使う
public class Container<T> {
    private static T defaultValue;  // コンパイルエラー

    public static T getDefault() {  // コンパイルエラー
        return defaultValue;
    }
}
```

#### エラーメッセージ

```
error: non-static type variable T cannot be referenced from a static context
```

#### 対処法

インスタンスのメンバーにするか、staticメソッド自身に型パラメーターを宣言します。

```java
// ○ インスタンスのメンバーにする／staticメソッドに型パラメーターを宣言する
public class Container<T> {
    private T defaultValue;

    public T getDefault() {
        return defaultValue;
    }

    public static <U> Container<U> empty() {
        return new Container<>();
    }
}
```

## ワイルドカードの誤用

### エラー例

上限境界ワイルドカードで要素を追加しようとした例です。

```java
// × ワイルドカードの誤用
List<? extends Number> numbers = new ArrayList<Integer>();
numbers.add(1); // コンパイルエラー
```

### エラーメッセージ

```
error: incompatible types: int cannot be converted to CAP#1
  where CAP#1 is a fresh type-variable:
    CAP#1 extends Number from capture of ? extends Number
```

### 対処法

ワイルドカードの正しい使用方法です。

```java
// ○ 適切な使用例
List<Integer> integers = new ArrayList<>();
integers.add(1);
List<? extends Number> numbers = integers; // 読み取り専用として使用
```

## 型消去に関する問題

ジェネリクスの型引数は、コンパイル時の検査にだけ使われ、実行時には消えます（型消去）。
`List<String>`も`List<Integer>`も、実行時にはただの`List`です。
そのため、実行時に型引数を区別する必要がある書き方はコンパイルエラーになります。

### エラー例1：instanceofで型引数まで判定する

```java
import java.util.List;

public class TypeCheck {
    public static void printIfStringList(Object obj) {
        if (obj instanceof List<String>) {  // コンパイルエラー
            IO.println("List<String>です");
        }
    }
}
```

#### エラーメッセージ

```
error: Object cannot be safely cast to List<String>
```

#### 対処法

型引数はワイルドカード`<?>`にして`List`であることだけを判定し、要素の型は1つずつ確かめます。

```java
import java.util.List;

public class TypeCheck {
    public static void printIfList(Object obj) {
        if (obj instanceof List<?> list) {  // 型引数はワイルドカードで判定する
            for (Object element : list) {
                if (element instanceof String s) {  // 要素の型は1つずつ確かめる
                    IO.println("文字列: " + s);
                }
            }
        }
    }
}
```

### エラー例2：型引数だけが異なるオーバーロード

```java
import java.util.List;

public class Printer {
    public void print(List<String> list) { }
    public void print(List<Integer> list) { }  // コンパイルエラー
}
```

#### エラーメッセージ

```
error: name clash: print(List<Integer>) and print(List<String>) have the same erasure
```

型消去のあとは、どちらも`print(List)`という同じシグネチャになるため、区別できません。

#### 対処法

メソッドの名前を分けます。

```java
import java.util.List;

public class Printer {
    public void printStrings(List<String> list) { }
    public void printIntegers(List<Integer> list) { }
}
```

## 境界型パラメーターの問題

### エラー例

境界型パラメーターで算術演算を直接実行しようとした例です。

```java
// × 境界型パラメータの誤用
public class NumberContainer<T extends Number> {
    private T value;
    
    public void multiplyBy(T factor) {
        this.value = this.value * factor; // コンパイルエラー
    }
}
```

### エラーメッセージ

```
error: bad operand types for binary operator '*'
  first type:  T
  second type: T
```

### 対処法

境界型パラメーターの正しい使用方法です。
`T`型の値どうしを`*`で計算することはできませんが、`Number`のメソッド`doubleValue()`で`double`に変換すれば計算できます。
ただし、計算結果の`double`を`T`型のフィールドに戻すことはできないため、結果は`double`として返します。

コンパイルできる全文：[chapter10-code/NumberContainer/](chapter10-code/NumberContainer/)

```java
// ○ 適切な境界型パラメータ
public class NumberContainer<T extends Number> {
    private final T value;
    
    public NumberContainer(T value) {
        this.value = value;
    }
    
    public double multipliedBy(double factor) {
        // Number型の共通メソッドでdoubleに変換してから計算し、結果を返す
        return this.value.doubleValue() * factor;
    }
}
```

## var型推論の誤用

### エラー例

var型推論を不適切に使用した例です。

```java
// × var型推論の誤用
var list = new ArrayList(); // Raw typeになる
var value = null; // コンパイルエラー
```

### エラーメッセージ

```
error: cannot infer type for local variable value
  (variable initializer is 'null')
```

### 対処法

var型推論の正しい使用方法です。

```java
// ○ 適切なvar使用例
var list = new ArrayList<String>(); // 型パラメータを明示
String value = null; // 明示的な型宣言
```

## 共通の対処戦略

1. コンパイル時の警告を無視しない
    + `-Xlint:unchecked`オプションを使用して詳細な警告を確認する
2. 型パラメーターの制約を理解する
    + 何ができて何ができないかを明確に把握する
3. 型安全性を保つ設計パターンを使用する
    + Factory Method、Builder、Strategyパターンなどの活用
4. IDEの支援を活用する
    + Eclipse、IntelliJ IDEAなどの自動補完とエラー検出機能を使用
5. 段階的なリファクタリング
    + 既存のraw typeコードを少しずつジェネリクスに移行する
