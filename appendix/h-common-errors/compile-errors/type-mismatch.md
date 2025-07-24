# 型の不一致エラー

## 概要

型の不一致エラーは、変数の型と代入される値の型が一致しない場合に発生するコンパイルエラーです。Javaは強い型付け言語であるため、型の整合性は厳密にチェックされます。

## エラーメッセージの例

```
error: incompatible types: String cannot be converted to int
error: incompatible types: possible lossy conversion from double to int
error: incompatible types: List<String> cannot be converted to List<Integer>
```

## 典型的な発生パターン

### パターン1：基本型の不一致

```java
// エラー例1：文字列を数値型に代入
int number = "123";  // エラー: String cannot be converted to int

// エラー例2：小数を整数型に代入
int count = 3.14;  // エラー: possible lossy conversion from double to int

// エラー例3：大きな整数を小さな型に代入
byte small = 1000;  // エラー: possible lossy conversion from int to byte
```

### パターン2：参照型の不一致

```java
// エラー例1：異なるクラス間の代入
String text = new Integer(42);  // エラー: Integer cannot be converted to String

// エラー例2：配列型の不一致
int[] numbers = new String[5];  // エラー: String[] cannot be converted to int[]

// エラー例3：継承関係のない型への代入
List<String> list = new HashMap<>();  // エラー: HashMap cannot be converted to List
```

### パターン3：ジェネリクスの型不一致

```java
// エラー例1：ジェネリック型パラメータの不一致
List<String> strings = new ArrayList<Integer>();  // エラー

// エラー例2：ワイルドカードの誤用
List<? extends Number> numbers = new ArrayList<String>();  // エラー

// エラー例3：raw typeとジェネリクスの混在
List<String> list = new ArrayList();  // 警告（エラーではないが危険）
list.add(123);  // 実行時にClassCastExceptionの可能性
```

### パターン4：メソッドの戻り値型

```java
public class Calculator {
    // エラー：戻り値の型が一致しない
    public int calculate() {
        return "結果";  // エラー: String cannot be converted to int
    }
    
    // エラー：voidメソッドで値を返す
    public void process() {
        return 42;  // エラー: unexpected return value
    }
}
```

## 解決策

### 1. 型変換（キャスト）

```java
// 数値文字列の変換
int number = Integer.parseInt("123");

// 小数の整数への変換（明示的キャスト）
int count = (int) 3.14;  // 小数部分は切り捨て

// オブジェクトの文字列変換
String text = String.valueOf(42);
// または
String text2 = Integer.toString(42);
```

### 2. 適切な型の使用

```java
// 正しい型を使用
double pi = 3.14;  // intではなくdouble
long bigNumber = 1000000000L;  // intではなくlong

// 適切なコレクション型
Map<String, Integer> map = new HashMap<>();  // ListではなくMap
Set<String> uniqueValues = new HashSet<>();  // 重複を許さない場合
```

### 3. ジェネリクスの正しい使用

```java
// 型パラメータを一致させる
List<String> strings = new ArrayList<String>();
// または型推論を使用（Java 7以降）
List<String> strings2 = new ArrayList<>();

// 共通の親型を使用
List<? extends Number> numbers = new ArrayList<Integer>();
numbers = new ArrayList<Double>();  // OK
```

### 4. 型チェックとキャスト

```java
public void processObject(Object obj) {
    // 型チェック後にキャスト
    if (obj instanceof String) {
        String str = (String) obj;
        System.out.println(str.toUpperCase());
    } else if (obj instanceof Integer) {
        Integer num = (Integer) obj;
        System.out.println(num * 2);
    }
}
```

## 高度な解決策

### 1. ジェネリックメソッド

```java
public class TypeSafeConverter {
    // 型安全な変換メソッド
    @SuppressWarnings("unchecked")
    public static <T> T convert(Object obj, Class<T> targetType) {
        if (targetType.isInstance(obj)) {
            return (T) obj;
        }
        
        // 基本的な型変換
        if (targetType == String.class) {
            return (T) String.valueOf(obj);
        } else if (targetType == Integer.class && obj instanceof String) {
            return (T) Integer.valueOf((String) obj);
        }
        
        throw new IllegalArgumentException("Cannot convert " + obj + " to " + targetType);
    }
}
```

### 2. ビルダーパターンで型安全性を確保

```java
public class TypeSafeBuilder<T> {
    private final Class<T> type;
    private T value;
    
    private TypeSafeBuilder(Class<T> type) {
        this.type = type;
    }
    
    public static <T> TypeSafeBuilder<T> of(Class<T> type) {
        return new TypeSafeBuilder<>(type);
    }
    
    public TypeSafeBuilder<T> with(T value) {
        this.value = value;
        return this;
    }
    
    public T build() {
        return Objects.requireNonNull(value, "Value must be set");
    }
}

// 使用例
String result = TypeSafeBuilder.of(String.class)
    .with("Hello")
    .build();
```

### 3. 関数型インターフェイスでの型変換

```java
@FunctionalInterface
interface TypeConverter<F, T> {
    T convert(F from);
}

public class Converters {
    public static final TypeConverter<String, Integer> STRING_TO_INT = 
        str -> Integer.parseInt(str);
    
    public static final TypeConverter<Double, Integer> DOUBLE_TO_INT = 
        d -> d.intValue();
    
    // 使用例
    public static <F, T> List<T> convertList(List<F> from, 
                                            TypeConverter<F, T> converter) {
        return from.stream()
            .map(converter::convert)
            .collect(Collectors.toList());
    }
}
```

## プリミティブ型とラッパークラス

### オートボクシング/アンボクシングの注意点

```java
// オートボクシング（自動変換）
Integer boxed = 42;  // int -> Integer

// アンボクシング（自動変換）
int primitive = boxed;  // Integer -> int

// 注意：nullの場合
Integer nullValue = null;
int value = nullValue;  // 実行時にNullPointerException

// 安全な処理
int safeValue = (nullValue != null) ? nullValue : 0;
```

### 型の拡大と縮小

```java
// 拡大変換（安全）
byte b = 10;
int i = b;  // OK: byte -> int
long l = i;  // OK: int -> long
double d = l;  // OK: long -> double

// 縮小変換（明示的キャストが必要）
double pi = 3.14159;
float f = (float) pi;  // 精度が落ちる可能性
int rounded = (int) pi;  // 3になる（小数部切り捨て）
```

## デバッグのコツ

### 1. 型情報の確認

```java
public void debugType(Object obj) {
    System.out.println("値: " + obj);
    System.out.println("型: " + obj.getClass().getName());
    System.out.println("型階層:");
    
    Class<?> clazz = obj.getClass();
    while (clazz != null) {
        System.out.println("  " + clazz.getName());
        for (Class<?> iface : clazz.getInterfaces()) {
            System.out.println("    implements " + iface.getName());
        }
        clazz = clazz.getSuperclass();
    }
}
```

### 2. コンパイルエラーの詳細確認

```bash
# 詳細なエラー情報を表示
javac -Xlint:all MyClass.java

# 型推論の詳細を表示
javac -XDverboseResolution=all MyClass.java
```

## ベストプラクティス

1. **明示的な型宣言**: var使用時も初期値で型が明確になるようにする
2. **適切な型の選択**: 用途に応じた最適な型を選ぶ
3. **型変換の明示化**: 暗黙の変換に頼らず、意図を明確にする
4. **ジェネリクスの活用**: raw typeを避け、型安全性を保つ
5. **IDEの活用**: 型推論や自動修正機能を活用する

## 関連項目

- [symbol-not-found.md](symbol-not-found.md)
- [access-control.md](access-control.md)
- ジェネリクスガイド
- 型システムの詳細