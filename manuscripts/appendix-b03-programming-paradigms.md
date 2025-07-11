# 付録B.03 プログラミングパラダイムの進化と関数型プログラミングの数学的基礎

## 概要

本付録では、プログラミングパラダイムの歴史的発展を俯瞰し、特に関数型プログラミングの数学的基礎とJavaにおける実装について詳細に解説します。ラムダ計算、圏論、モナド、ファンクタなどの理論的概念が、実際のプログラミングにどのように応用されているかを理解することで、より深いレベルでJavaの関数型機能を活用できるようになります。

## 目次

1. プログラミングパラダイムの歴史的発展
2. 関数型プログラミングの数学的基礎
3. ラムダ計算とその実装
4. 圏論の基礎概念
5. モナドの理論と実践
6. ファンクタとその応用
7. Javaにおける関数型プログラミングの実装
8. 実践的応用例

---

## 1. プログラミングパラダイムの歴史的発展

### 1.1 パラダイムの定義と重要性

プログラミングパラダイムとは、プログラムの構造と実行に関する基本的な考え方の枠組みです。各パラダイムは、特定の問題領域に対する解決アプローチを提供し、プログラマの思考方法を形作ります。

### 1.2 主要なパラダイムの系譜

#### 命令型プログラミング（1940年代〜）
最初のプログラミングパラダイムは、コンピュータのハードウェアアーキテクチャを直接反映した命令型プログラミングでした。

```java
// 命令型スタイル：状態の変更を通じて計算を表現
int sum = 0;
for (int i = 1; i <= 10; i++) {
    sum += i;  // 状態を変更
}
```

#### 構造化プログラミング（1960年代〜）
ダイクストラの「GOTO文有害論」をきっかけに、プログラムの制御フローを構造化する動きが生まれました。

```java
// 構造化プログラミング：制御構造の明確化
public int calculateSum(int n) {
    if (n <= 0) {
        return 0;
    }
    int sum = 0;
    for (int i = 1; i <= n; i++) {
        sum += i;
    }
    return sum;
}
```

#### オブジェクト指向プログラミング（1960年代後半〜）
Simulaに始まり、Smalltalk、C++、Javaへと発展したオブジェクト指向は、データと処理を一体化する革新的なアプローチでした。

```java
// オブジェクト指向：データと振る舞いのカプセル化
public class Counter {
    private int value = 0;
    
    public void increment() {
        value++;
    }
    
    public int getValue() {
        return value;
    }
}
```

#### 関数型プログラミング（1950年代〜、2000年代に再評価）
数学的な関数の概念に基づく関数型プログラミングは、LISPから始まり、ML、Haskell、そして現代のマルチパラダイム言語へと発展しました。

```java
// 関数型スタイル：副作用のない関数の組み合わせ
IntStream.rangeClosed(1, 10)
    .reduce(0, Integer::sum);
```

### 1.3 パラダイムの統合と進化

現代のプログラミング言語は、複数のパラダイムを統合したマルチパラダイム言語が主流となっています。Javaも当初は純粋なオブジェクト指向言語でしたが、Java 8以降、関数型プログラミングの要素を積極的に取り入れています。

---

## 2. 関数型プログラミングの数学的基礎

### 2.1 関数型プログラミングの哲学

関数型プログラミングは、以下の原則に基づいています：

1. **参照透過性**：同じ入力に対して常に同じ出力を返す
2. **不変性**：データは一度作成されたら変更されない
3. **副作用の排除**：関数は外部の状態を変更しない
4. **高階関数**：関数を値として扱い、引数や戻り値にできる

### 2.2 数学的関数とプログラミング関数

数学における関数は、定義域から値域への写像として定義されます：

```
f: A → B
```

これは、集合Aの各要素を集合Bの要素に対応付ける規則を表します。

```java
// 数学的関数の実装例
Function<Integer, Integer> square = x -> x * x;
// f: Integer → Integer where f(x) = x²
```

### 2.3 合成可能性

関数型プログラミングの強力な特性の1つは、関数の合成可能性です：

```java
// 関数の合成：(f ∘ g)(x) = f(g(x))
Function<Integer, Integer> addOne = x -> x + 1;
Function<Integer, Integer> multiplyByTwo = x -> x * 2;

// 合成関数の作成
Function<Integer, Integer> addOneThenMultiply = 
    addOne.andThen(multiplyByTwo);
    
// または
Function<Integer, Integer> multiplyThenAddOne = 
    multiplyByTwo.compose(addOne);

// 実行
System.out.println(addOneThenMultiply.apply(3));    // (3 + 1) * 2 = 8
System.out.println(multiplyThenAddOne.apply(3));    // (3 * 2) + 1 = 7
```

---

## 3. ラムダ計算とその実装

### 3.1 ラムダ計算の基礎

ラムダ計算（λ-calculus）は、1930年代にアロンゾ・チャーチによって開発された形式体系で、計算可能性の数学的モデルを提供します。

#### 基本構文

ラムダ計算の式は以下の3つの要素から構成されます：

1. **変数**：x, y, z, ...
2. **抽象化**：λx.M（xを引数とする関数）
3. **適用**：M N（関数Mに引数Nを適用）

```java
// ラムダ計算の基本形式をJavaで表現

// 恒等関数：λx.x
Function<Object, Object> identity = x -> x;

// 定数関数：λx.λy.x
Function<Object, Function<Object, Object>> constant = x -> y -> x;

// 関数適用：(λx.x + 1) 5
Function<Integer, Integer> increment = x -> x + 1;
int result = increment.apply(5); // 6
```

### 3.2 カリー化と部分適用

カリー化は、複数引数の関数を単一引数の関数の連鎖に変換する技法です：

```java
// 通常の2引数関数
BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;

// カリー化された関数
Function<Integer, Function<Integer, Integer>> curriedAdd = a -> b -> a + b;

// 部分適用の例
Function<Integer, Integer> add5 = curriedAdd.apply(5);
System.out.println(add5.apply(3)); // 8

// 汎用的なカリー化ユーティリティ
public class CurryingUtils {
    public static <A, B, C> Function<A, Function<B, C>> curry(
            BiFunction<A, B, C> biFunction) {
        return a -> b -> biFunction.apply(a, b);
    }
    
    public static <A, B, C, D> Function<A, Function<B, Function<C, D>>> curry(
            TriFunction<A, B, C, D> triFunction) {
        return a -> b -> c -> triFunction.apply(a, b, c);
    }
}

// 使用例
BiFunction<Double, Double, Double> multiply = (a, b) -> a * b;
Function<Double, Function<Double, Double>> curriedMultiply = 
    CurryingUtils.curry(multiply);

// 税率計算の部分適用
Function<Double, Double> withTax = curriedMultiply.apply(1.1);
System.out.println(withTax.apply(100.0)); // 110.0
```

### 3.3 ベータ簡約

ラムダ計算における計算は、ベータ簡約によって行われます：

```
(λx.M) N → M[x := N]
```

これは、関数適用時に引数を代入することを表します：

```java
// ベータ簡約の例
// (λx.x * x) 5 → 5 * 5 → 25

Function<Integer, Integer> square = x -> x * x;
int result = square.apply(5); // 25

// より複雑な例
// (λf.λx.f (f x)) (λy.y + 1) 5
// → (λx.(λy.y + 1) ((λy.y + 1) x)) 5
// → (λy.y + 1) ((λy.y + 1) 5)
// → (λy.y + 1) 6
// → 7

Function<Function<Integer, Integer>, Function<Integer, Integer>> twice = 
    f -> x -> f.apply(f.apply(x));

Function<Integer, Integer> increment = y -> y + 1;
int result2 = twice.apply(increment).apply(5); // 7
```

### 3.4 チャーチ数

ラムダ計算では、自然数も関数として表現できます（チャーチ数）：

```java
// チャーチ数の実装
interface ChurchNumeral {
    <T> Function<T, T> apply(Function<T, T> f);
}

// 0 = λf.λx.x
ChurchNumeral zero = new ChurchNumeral() {
    @Override
    public <T> Function<T, T> apply(Function<T, T> f) {
        return x -> x;
    }
};

// 1 = λf.λx.f x
ChurchNumeral one = new ChurchNumeral() {
    @Override
    public <T> Function<T, T> apply(Function<T, T> f) {
        return x -> f.apply(x);
    }
};

// 2 = λf.λx.f (f x)
ChurchNumeral two = new ChurchNumeral() {
    @Override
    public <T> Function<T, T> apply(Function<T, T> f) {
        return x -> f.apply(f.apply(x));
    }
};

// 後者関数：succ = λn.λf.λx.f (n f x)
ChurchNumeral succ(ChurchNumeral n) {
    return new ChurchNumeral() {
        @Override
        public <T> Function<T, T> apply(Function<T, T> f) {
            return x -> f.apply(n.apply(f).apply(x));
        }
    };
}

// チャーチ数を通常の整数に変換
int toInt(ChurchNumeral n) {
    return n.<Integer>apply(x -> x + 1).apply(0);
}

// 使用例
System.out.println(toInt(zero));        // 0
System.out.println(toInt(one));         // 1
System.out.println(toInt(succ(two)));   // 3
```

---

## 4. 圏論の基礎概念

### 4.1 圏論とは

圏論（Category Theory）は、数学的構造とその間の関係を研究する抽象的な理論です。プログラミングにおいては、型と関数の関係を理解するための強力なフレームワークを提供します。

### 4.2 圏の定義

圏は以下の要素から構成されます：

1. **対象（Objects）**：型やデータ構造
2. **射（Morphisms/Arrows）**：対象間の関数
3. **合成（Composition）**：射の合成規則
4. **恒等射（Identity）**：各対象に対する恒等関数

```java
// Javaにおける圏の要素

// 対象：型
class A {}
class B {}
class C {}

// 射：関数
Function<A, B> f = a -> new B();
Function<B, C> g = b -> new C();

// 合成
Function<A, C> h = f.andThen(g);  // g ∘ f

// 恒等射
Function<A, A> idA = Function.identity();
```

### 4.3 圏論の法則

圏は以下の法則を満たす必要があります：

1. **結合法則**：`(h ∘ g) ∘ f = h ∘ (g ∘ f)`
2. **単位法則**：`id ∘ f = f` かつ `f ∘ id = f`

```java
// 結合法則の検証
Function<Integer, Integer> f = x -> x + 1;
Function<Integer, Integer> g = x -> x * 2;
Function<Integer, Integer> h = x -> x * x;

Function<Integer, Integer> composition1 = f.andThen(g).andThen(h);
Function<Integer, Integer> composition2 = f.andThen(g.andThen(h));

// 両者は同じ結果を生成
System.out.println(composition1.apply(3)); // ((3 + 1) * 2)² = 64
System.out.println(composition2.apply(3)); // ((3 + 1) * 2)² = 64

// 単位法則の検証
Function<Integer, Integer> identity = Function.identity();
Function<Integer, Integer> fWithId1 = f.andThen(identity);
Function<Integer, Integer> fWithId2 = identity.andThen(f);

System.out.println(f.apply(5));        // 6
System.out.println(fWithId1.apply(5)); // 6
System.out.println(fWithId2.apply(5)); // 6
```

---

## 5. モナドの理論と実践

### 5.1 モナドの定義

モナド（Monad）は、計算の文脈を扱うための数学的構造です。プログラミングにおいては、副作用を含む計算を純粋な関数として扱うための抽象化を提供します。

モナドは以下の3つの要素から構成されます：

1. **型コンストラクタ** `M<T>`
2. **return（unit）関数**：`T → M<T>`
3. **bind（flatMap）関数**：`M<T> → (T → M<U>) → M<U>`

### 5.2 モナド則

モナドは以下の3つの法則を満たす必要があります：

1. **左単位元則**：`return(a).flatMap(f) = f(a)`
2. **右単位元則**：`m.flatMap(return) = m`
3. **結合則**：`m.flatMap(f).flatMap(g) = m.flatMap(x -> f(x).flatMap(g))`

### 5.3 JavaにおけるOptionalモナド

JavaのOptionalはモナドの性質を持っています：

```java
// Optionalモナドの実装例

// return (of): T → Optional<T>
Optional<Integer> returnExample = Optional.of(42);

// flatMap: Optional<T> → (T → Optional<U>) → Optional<U>
Optional<String> result = Optional.of(5)
    .flatMap(x -> Optional.of(x * 2))
    .flatMap(x -> Optional.of("Result: " + x));

// モナド則の検証

// 1. 左単位元則
Function<Integer, Optional<String>> f = x -> Optional.of("Value: " + x);
Optional<String> left1 = Optional.of(10).flatMap(f);
Optional<String> left2 = f.apply(10);
System.out.println(left1.equals(left2)); // true

// 2. 右単位元則
Optional<Integer> m = Optional.of(20);
Optional<Integer> right1 = m.flatMap(Optional::of);
Optional<Integer> right2 = m;
System.out.println(right1.equals(right2)); // true

// 3. 結合則
Function<Integer, Optional<Integer>> addOne = x -> Optional.of(x + 1);
Function<Integer, Optional<Integer>> multiplyTwo = x -> Optional.of(x * 2);

Optional<Integer> assoc1 = Optional.of(5)
    .flatMap(addOne)
    .flatMap(multiplyTwo);

Optional<Integer> assoc2 = Optional.of(5)
    .flatMap(x -> addOne.apply(x).flatMap(multiplyTwo));

System.out.println(assoc1.equals(assoc2)); // true
```

### 5.4 カスタムモナドの実装

独自のモナドを実装する例：

```java
// Resultモナド：成功/失敗を表現
public abstract class Result<T> {
    
    // return (of)
    public static <T> Result<T> of(T value) {
        return new Success<>(value);
    }
    
    public static <T> Result<T> error(String message) {
        return new Failure<>(message);
    }
    
    // flatMap (bind)
    public abstract <U> Result<U> flatMap(Function<T, Result<U>> f);
    
    // map (ファンクタとしての機能)
    public abstract <U> Result<U> map(Function<T, U> f);
    
    // 実装クラス
    private static class Success<T> extends Result<T> {
        private final T value;
        
        Success(T value) {
            this.value = value;
        }
        
        @Override
        public <U> Result<U> flatMap(Function<T, Result<U>> f) {
            try {
                return f.apply(value);
            } catch (Exception e) {
                return new Failure<>(e.getMessage());
            }
        }
        
        @Override
        public <U> Result<U> map(Function<T, U> f) {
            try {
                return new Success<>(f.apply(value));
            } catch (Exception e) {
                return new Failure<>(e.getMessage());
            }
        }
    }
    
    private static class Failure<T> extends Result<T> {
        private final String error;
        
        Failure(String error) {
            this.error = error;
        }
        
        @Override
        public <U> Result<U> flatMap(Function<T, Result<U>> f) {
            return new Failure<>(error);
        }
        
        @Override
        public <U> Result<U> map(Function<T, U> f) {
            return new Failure<>(error);
        }
    }
}

// 使用例
public class ResultMonadExample {
    public static Result<Integer> divide(int a, int b) {
        if (b == 0) {
            return Result.error("Division by zero");
        }
        return Result.of(a / b);
    }
    
    public static Result<Double> sqrt(int x) {
        if (x < 0) {
            return Result.error("Cannot take square root of negative number");
        }
        return Result.of(Math.sqrt(x));
    }
    
    public static void main(String[] args) {
        // モナディックな計算チェーン
        Result<Double> result = divide(20, 4)
            .flatMap(ResultMonadExample::sqrt)
            .map(x -> x * 2);
        
        // パターンマッチングで結果を処理
        switch (result) {
            case Result.Success<Double> s -> 
                System.out.println("Result: " + s.value);
            case Result.Failure<Double> f -> 
                System.out.println("Error: " + f.error);
        }
    }
}
```

### 5.5 Streamモナド

JavaのStreamもモナド的な性質を持っています：

```java
// Streamモナドの例

// return: T → Stream<T>
Stream<Integer> single = Stream.of(42);

// flatMap: Stream<T> → (T → Stream<U>) → Stream<U>
Stream<String> words = Stream.of("Hello World", "Java Programming")
    .flatMap(line -> Arrays.stream(line.split(" ")));

// モナディックな計算の連鎖
List<Integer> result = Stream.of(1, 2, 3, 4, 5)
    .flatMap(x -> Stream.of(x, x * x))  // 各要素とその二乗
    .filter(x -> x % 2 == 0)            // 偶数のみ
    .map(x -> x * 10)                   // 10倍
    .collect(Collectors.toList());

System.out.println(result); // [20, 40, 160]

// モナド則の活用例
Function<String, Stream<Character>> toChars = 
    s -> s.chars().mapToObj(c -> (char) c);

Function<Character, Stream<String>> toAscii = 
    c -> Stream.of(String.valueOf((int) c));

// 関数の合成
Stream<String> asciiCodes = Stream.of("AB")
    .flatMap(toChars)
    .flatMap(toAscii);

asciiCodes.forEach(System.out::println); // 65, 66
```

---

## 6. ファンクタとその応用

### 6.1 ファンクタの定義

ファンクタ（Functor）は、ある圏から別の圏への構造を保つ写像です。プログラミングにおいては、コンテナ型の値を変換する抽象化として理解できます。

ファンクタは以下の要素を持ちます：
- **map関数**：`F<A> → (A → B) → F<B>`

### 6.2 ファンクタ則

ファンクタは以下の法則を満たす必要があります：

1. **恒等則**：`fmap(id) = id`
2. **合成則**：`fmap(f ∘ g) = fmap(f) ∘ fmap(g)`

### 6.3 Javaにおけるファンクタの実装

```java
// ファンクタインターフェイス
public interface Functor<T> {
    <R> Functor<R> map(Function<T, R> f);
}

// Optionalファンクタ
public class OptionalFunctor<T> implements Functor<T> {
    private final Optional<T> value;
    
    public OptionalFunctor(Optional<T> value) {
        this.value = value;
    }
    
    @Override
    public <R> OptionalFunctor<R> map(Function<T, R> f) {
        return new OptionalFunctor<>(value.map(f));
    }
    
    public Optional<T> get() {
        return value;
    }
}

// リストファンクタ
public class ListFunctor<T> implements Functor<T> {
    private final List<T> values;
    
    public ListFunctor(List<T> values) {
        this.values = new ArrayList<>(values);
    }
    
    @Override
    public <R> ListFunctor<R> map(Function<T, R> f) {
        return new ListFunctor<>(
            values.stream()
                .map(f)
                .collect(Collectors.toList())
        );
    }
    
    public List<T> get() {
        return new ArrayList<>(values);
    }
}

// ファンクタ則の検証
public class FunctorLawsTest {
    public static void main(String[] args) {
        // 恒等則のテスト
        ListFunctor<Integer> numbers = new ListFunctor<>(List.of(1, 2, 3));
        ListFunctor<Integer> identity = numbers.map(Function.identity());
        System.out.println(numbers.get().equals(identity.get())); // true
        
        // 合成則のテスト
        Function<Integer, Integer> addOne = x -> x + 1;
        Function<Integer, Integer> multiplyTwo = x -> x * 2;
        
        ListFunctor<Integer> composed1 = numbers
            .map(addOne)
            .map(multiplyTwo);
            
        ListFunctor<Integer> composed2 = numbers
            .map(addOne.andThen(multiplyTwo));
            
        System.out.println(composed1.get().equals(composed2.get())); // true
    }
}
```

### 6.4 二重ファンクタとトラバース

```java
// 二重ファンクタの扱い
public class BiFunctor {
    // Optional<List<T>>の変換
    public static <T, R> Optional<List<R>> mapOptionalList(
            Optional<List<T>> optList, 
            Function<T, R> f) {
        return optList.map(list -> 
            list.stream()
                .map(f)
                .collect(Collectors.toList())
        );
    }
    
    // List<Optional<T>>の変換
    public static <T, R> List<Optional<R>> mapListOptional(
            List<Optional<T>> listOpt, 
            Function<T, R> f) {
        return listOpt.stream()
            .map(opt -> opt.map(f))
            .collect(Collectors.toList());
    }
    
    // トラバース：List<Optional<T>> → Optional<List<T>>
    public static <T> Optional<List<T>> sequence(List<Optional<T>> listOpt) {
        return listOpt.stream()
            .reduce(
                Optional.of(new ArrayList<>()),
                (acc, opt) -> acc.flatMap(list -> 
                    opt.map(value -> {
                        List<T> newList = new ArrayList<>(list);
                        newList.add(value);
                        return newList;
                    })
                ),
                (a, b) -> a  // 並列処理では使用されない
            );
    }
}

// 使用例
List<Optional<Integer>> maybeNumbers = List.of(
    Optional.of(1),
    Optional.of(2),
    Optional.of(3)
);

Optional<List<Integer>> allNumbers = BiFunctor.sequence(maybeNumbers);
System.out.println(allNumbers); // Optional[[1, 2, 3]]

// 一つでもemptyがあれば全体がempty
List<Optional<Integer>> withEmpty = List.of(
    Optional.of(1),
    Optional.empty(),
    Optional.of(3)
);

Optional<List<Integer>> result = BiFunctor.sequence(withEmpty);
System.out.println(result); // Optional.empty
```

---

## 7. Javaにおける関数型プログラミングの実装

### 7.1 Java 8での関数型機能の導入

Java 8は、関数型プログラミングの要素を大幅に取り入れました：

1. **ラムダ式**：匿名関数の簡潔な記法
2. **関数型インターフェイス**：`@FunctionalInterface`
3. **Stream API**：関数型のデータ処理パイプライン
4. **Optional**：null安全性のためのモナド

### 7.2 高階関数の実装パターン

```java
public class HigherOrderFunctions {
    
    // 関数を返す関数
    public static Function<Integer, Integer> multiplier(int factor) {
        return x -> x * factor;
    }
    
    // 関数を引数に取る関数
    public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        return list.stream()
            .filter(predicate)
            .collect(Collectors.toList());
    }
    
    // 関数の合成を行う関数
    public static <A, B, C> Function<A, C> compose(
            Function<B, C> f, 
            Function<A, B> g) {
        return x -> f.apply(g.apply(x));
    }
    
    // メモ化（記憶化）
    public static <T, R> Function<T, R> memoize(Function<T, R> f) {
        Map<T, R> cache = new ConcurrentHashMap<>();
        return x -> cache.computeIfAbsent(x, f);
    }
    
    public static void main(String[] args) {
        // 使用例
        Function<Integer, Integer> times3 = multiplier(3);
        System.out.println(times3.apply(5)); // 15
        
        List<Integer> evens = filter(List.of(1, 2, 3, 4, 5), x -> x % 2 == 0);
        System.out.println(evens); // [2, 4]
        
        // 高価な計算のメモ化
        Function<Integer, Integer> expensiveFunction = x -> {
            System.out.println("Computing for " + x);
            return x * x * x;
        };
        
        Function<Integer, Integer> memoized = memoize(expensiveFunction);
        
        System.out.println(memoized.apply(5)); // Computing for 5 → 125
        System.out.println(memoized.apply(5)); // 125 (キャッシュから)
    }
}
```

### 7.3 遅延評価の実装

```java
public class LazyEvaluation {
    
    // 遅延評価を実現するSupplier
    public static class Lazy<T> {
        private Supplier<T> supplier;
        private T value;
        private boolean evaluated = false;
        
        public Lazy(Supplier<T> supplier) {
            this.supplier = supplier;
        }
        
        public T get() {
            if (!evaluated) {
                value = supplier.get();
                evaluated = true;
                supplier = null; // メモリリークを防ぐ
            }
            return value;
        }
        
        public <R> Lazy<R> map(Function<T, R> f) {
            return new Lazy<>(() -> f.apply(get()));
        }
        
        public <R> Lazy<R> flatMap(Function<T, Lazy<R>> f) {
            return new Lazy<>(() -> f.apply(get()).get());
        }
    }
    
    // 無限リストの実装
    public static class LazyList<T> {
        private final T head;
        private final Lazy<LazyList<T>> tail;
        
        public LazyList(T head, Supplier<LazyList<T>> tail) {
            this.head = head;
            this.tail = new Lazy<>(tail);
        }
        
        public T head() {
            return head;
        }
        
        public LazyList<T> tail() {
            return tail.get();
        }
        
        public Stream<T> stream() {
            return Stream.iterate(this, LazyList::tail)
                .map(LazyList::head);
        }
        
        // 無限の自然数列
        public static LazyList<Integer> naturals(int start) {
            return new LazyList<>(start, () -> naturals(start + 1));
        }
        
        // フィボナッチ数列
        public static LazyList<Integer> fibonacci() {
            return fibonacciHelper(0, 1);
        }
        
        private static LazyList<Integer> fibonacciHelper(int a, int b) {
            return new LazyList<>(a, () -> fibonacciHelper(b, a + b));
        }
    }
    
    public static void main(String[] args) {
        // 遅延評価の例
        Lazy<Integer> lazyValue = new Lazy<>(() -> {
            System.out.println("Computing expensive value...");
            return 42;
        });
        
        System.out.println("Lazy value created");
        System.out.println("Value: " + lazyValue.get()); // ここで初めて計算
        System.out.println("Value: " + lazyValue.get()); // 2回目はキャッシュ
        
        // 無限リストの使用
        LazyList.naturals(1)
            .stream()
            .limit(10)
            .forEach(System.out::println); // 1, 2, 3, ..., 10
            
        // フィボナッチ数列
        LazyList.fibonacci()
            .stream()
            .limit(10)
            .forEach(System.out::println); // 0, 1, 1, 2, 3, 5, 8, 13, 21, 34
    }
}
```

---

## 8. 実践的応用例

### 8.1 パーサーコンビネータ

関数型プログラミングの応用例として、パーサーコンビネータを実装します：

```java
public class ParserCombinator {
    
    // パーサーの基本型
    @FunctionalInterface
    public interface Parser<T> {
        Optional<Pair<T, String>> parse(String input);
    }
    
    // 補助クラス
    public static class Pair<A, B> {
        public final A first;
        public final B second;
        
        public Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }
    }
    
    // 基本的なパーサー
    public static Parser<Character> charParser(char c) {
        return input -> {
            if (input.isEmpty() || input.charAt(0) != c) {
                return Optional.empty();
            }
            return Optional.of(new Pair<>(c, input.substring(1)));
        };
    }
    
    public static Parser<String> stringParser(String s) {
        return input -> {
            if (input.startsWith(s)) {
                return Optional.of(new Pair<>(s, input.substring(s.length())));
            }
            return Optional.empty();
        };
    }
    
    // パーサーコンビネータ
    public static <T> Parser<T> or(Parser<T> p1, Parser<T> p2) {
        return input -> {
            Optional<Pair<T, String>> result = p1.parse(input);
            if (result.isPresent()) {
                return result;
            }
            return p2.parse(input);
        };
    }
    
    public static <A, B> Parser<Pair<A, B>> and(Parser<A> p1, Parser<B> p2) {
        return input -> p1.parse(input).flatMap(r1 ->
            p2.parse(r1.second).map(r2 ->
                new Pair<>(new Pair<>(r1.first, r2.first), r2.second)
            )
        );
    }
    
    public static <T> Parser<List<T>> many(Parser<T> parser) {
        return input -> {
            List<T> results = new ArrayList<>();
            String remaining = input;
            
            while (true) {
                Optional<Pair<T, String>> result = parser.parse(remaining);
                if (result.isEmpty()) {
                    break;
                }
                results.add(result.get().first);
                remaining = result.get().second;
            }
            
            return Optional.of(new Pair<>(results, remaining));
        };
    }
    
    // 数値パーサーの例
    public static Parser<Integer> digit() {
        return input -> {
            if (input.isEmpty() || !Character.isDigit(input.charAt(0))) {
                return Optional.empty();
            }
            int digit = Character.getNumericValue(input.charAt(0));
            return Optional.of(new Pair<>(digit, input.substring(1)));
        };
    }
    
    public static Parser<Integer> number() {
        return input -> {
            Parser<List<Integer>> digits = many(digit());
            return digits.parse(input).flatMap(result -> {
                if (result.first.isEmpty()) {
                    return Optional.empty();
                }
                int value = result.first.stream()
                    .reduce(0, (acc, d) -> acc * 10 + d);
                return Optional.of(new Pair<>(value, result.second));
            });
        };
    }
    
    public static void main(String[] args) {
        // 使用例
        Parser<Character> aParser = charParser('a');
        Parser<Character> bParser = charParser('b');
        Parser<Character> aOrB = or(aParser, bParser);
        
        System.out.println(aOrB.parse("abc")); // Optional[(a, bc)]
        System.out.println(aOrB.parse("bcd")); // Optional[(b, cd)]
        System.out.println(aOrB.parse("xyz")); // Optional.empty
        
        // 数値のパース
        Parser<Integer> num = number();
        System.out.println(num.parse("123abc")); // Optional[(123, abc)]
        
        // 複雑なパーサーの組み合わせ
        Parser<Pair<String, Integer>> assignment = 
            and(stringParser("x="), number());
        System.out.println(assignment.parse("x=42")); // Optional[((x=, 42), )]
    }
}
```

### 8.2 代数的データ型の実装

```java
// sealed classを使った代数的データ型
public sealed interface Expression {
    
    record Constant(double value) implements Expression {}
    record Variable(String name) implements Expression {}
    record Add(Expression left, Expression right) implements Expression {}
    record Multiply(Expression left, Expression right) implements Expression {}
    
    // パターンマッチングによる評価
    default double evaluate(Map<String, Double> environment) {
        return switch (this) {
            case Constant(var value) -> value;
            case Variable(var name) -> environment.getOrDefault(name, 0.0);
            case Add(var left, var right) -> 
                left.evaluate(environment) + right.evaluate(environment);
            case Multiply(var left, var right) -> 
                left.evaluate(environment) * right.evaluate(environment);
        };
    }
    
    // 式の簡約
    default Expression simplify() {
        return switch (this) {
            case Constant c -> c;
            case Variable v -> v;
            case Add(var left, var right) -> {
                Expression l = left.simplify();
                Expression r = right.simplify();
                
                if (l instanceof Constant(var lv) && r instanceof Constant(var rv)) {
                    yield new Constant(lv + rv);
                }
                if (l instanceof Constant(var v) && v == 0) yield r;
                if (r instanceof Constant(var v) && v == 0) yield l;
                yield new Add(l, r);
            }
            case Multiply(var left, var right) -> {
                Expression l = left.simplify();
                Expression r = right.simplify();
                
                if (l instanceof Constant(var lv) && r instanceof Constant(var rv)) {
                    yield new Constant(lv * rv);
                }
                if (l instanceof Constant(var v) && v == 0) yield new Constant(0);
                if (r instanceof Constant(var v) && v == 0) yield new Constant(0);
                if (l instanceof Constant(var v) && v == 1) yield r;
                if (r instanceof Constant(var v) && v == 1) yield l;
                yield new Multiply(l, r);
            }
        };
    }
    
    // 式の表示
    default String show() {
        return switch (this) {
            case Constant(var value) -> String.valueOf(value);
            case Variable(var name) -> name;
            case Add(var left, var right) -> 
                "(" + left.show() + " + " + right.show() + ")";
            case Multiply(var left, var right) -> 
                "(" + left.show() + " * " + right.show() + ")";
        };
    }
}

// 使用例
public class AlgebraicDataTypeExample {
    public static void main(String[] args) {
        // (x + 2) * (y + 3)
        Expression expr = new Expression.Multiply(
            new Expression.Add(
                new Expression.Variable("x"),
                new Expression.Constant(2)
            ),
            new Expression.Add(
                new Expression.Variable("y"),
                new Expression.Constant(3)
            )
        );
        
        System.out.println("Expression: " + expr.show());
        
        Map<String, Double> env = Map.of("x", 5.0, "y", 7.0);
        System.out.println("Evaluation: " + expr.evaluate(env)); // 70.0
        
        // 簡約の例
        Expression toSimplify = new Expression.Add(
            new Expression.Constant(0),
            new Expression.Multiply(
                new Expression.Variable("x"),
                new Expression.Constant(1)
            )
        );
        
        System.out.println("Before: " + toSimplify.show());
        System.out.println("After: " + toSimplify.simplify().show());
    }
}
```

### 8.3 効果システムの実装

```java
// IOモナドの簡易実装
public abstract class IO<T> {
    
    // 実行
    public abstract T unsafeRun();
    
    // pure (return)
    public static <T> IO<T> pure(T value) {
        return new IO<T>() {
            @Override
            public T unsafeRun() {
                return value;
            }
        };
    }
    
    // flatMap (bind)
    public <U> IO<U> flatMap(Function<T, IO<U>> f) {
        return new IO<U>() {
            @Override
            public U unsafeRun() {
                T result = IO.this.unsafeRun();
                return f.apply(result).unsafeRun();
            }
        };
    }
    
    // map
    public <U> IO<U> map(Function<T, U> f) {
        return flatMap(t -> pure(f.apply(t)));
    }
    
    // 基本的なIO操作
    public static IO<String> readLine() {
        return new IO<String>() {
            @Override
            public String unsafeRun() {
                return new Scanner(System.in).nextLine();
            }
        };
    }
    
    public static IO<Void> println(String s) {
        return new IO<Void>() {
            @Override
            public Void unsafeRun() {
                System.out.println(s);
                return null;
            }
        };
    }
    
    // 合成されたIO操作
    public static IO<Void> echo() {
        return readLine()
            .flatMap(line -> println("You entered: " + line));
    }
    
    // より複雑な例
    public static IO<Integer> getNumber() {
        return println("Enter a number:")
            .flatMap(_ -> readLine())
            .map(Integer::parseInt);
    }
    
    public static IO<Void> calculator() {
        return getNumber()
            .flatMap(a -> getNumber()
                .flatMap(b -> {
                    int sum = a + b;
                    return println("Sum: " + sum);
                })
            );
    }
    
    // 並列実行
    public <U> IO<Pair<T, U>> par(IO<U> other) {
        return new IO<Pair<T, U>>() {
            @Override
            public Pair<T, U> unsafeRun() {
                CompletableFuture<T> f1 = CompletableFuture.supplyAsync(
                    () -> IO.this.unsafeRun()
                );
                CompletableFuture<U> f2 = CompletableFuture.supplyAsync(
                    () -> other.unsafeRun()
                );
                
                try {
                    return new Pair<>(f1.get(), f2.get());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
    
    // エラーハンドリング
    public IO<T> handleError(Function<Exception, T> handler) {
        return new IO<T>() {
            @Override
            public T unsafeRun() {
                try {
                    return IO.this.unsafeRun();
                } catch (Exception e) {
                    return handler.apply(e);
                }
            }
        };
    }
}

// 使用例
public class IOExample {
    public static void main(String[] args) {
        // 純粋な関数型プログラム
        IO<Void> program = IO.println("Welcome to FP Calculator!")
            .flatMap(_ -> IO.calculator())
            .flatMap(_ -> IO.println("Goodbye!"));
        
        // 副作用はmainの最後でのみ実行
        program.unsafeRun();
    }
}
```

## まとめ

関数型プログラミングの数学的基礎を理解することで、以下のような利点が得られます：

1. **抽象化の力**：モナドやファンクタなどの抽象概念により、複雑な計算パターンを簡潔に表現できる
2. **合成可能性**：小さな関数を組み合わせて複雑な処理を構築できる
3. **推論可能性**：数学的法則に基づいたコードは、その振る舞いを推論しやすい
4. **再利用性**：高度に抽象化されたコードは、さまざまな文脈で再利用可能

Javaは純粋な関数型言語ではありませんが、Java 8以降の機能により、関数型プログラミングの多くの利点を享受できるようになりました。ラムダ計算、圏論、モナド、ファンクタなどの理論的基礎を理解することで、より洗練された、保守性の高いコードを書くことができます。

これらの概念は初めは難しく感じるかもしれませんが、実践を通じて徐々に理解を深めることで、プログラミングに対する新たな視点を得ることができるでしょう。関数型プログラミングは、単なるプログラミングスタイルではなく、計算とは何かという根本的な問いに対する一つの答えなのです。