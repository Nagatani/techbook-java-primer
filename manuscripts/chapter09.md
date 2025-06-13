# 第9章 ラムダ式と関数型インタフェース

この章では、Java 8で導入されたラムダ式と関数型プログラミングについて学習します。C言語の関数ポインタとの比較も含めて、モダンなJavaプログラミング手法を習得しましょう。

## 9.1 ラムダ式とは

### C言語の関数ポインタとの比較

```c
// C言語の関数ポインタ
int add(int a, int b) {
    return a + b;
}

int (*operation)(int, int) = add;
int result = operation(5, 3);
```

```java
// Javaのラムダ式
BinaryOperator<Integer> operation = (a, b) -> a + b;
int result = operation.apply(5, 3);
```

### 従来の匿名クラス vs ラムダ式

```java
import java.util.*;

public class LambdaComparison {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("田中", "佐藤", "鈴木", "高橋");
        
        // 従来の匿名クラス
        Collections.sort(names, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareTo(s2);
            }
        });
        
        // ラムダ式
        Collections.sort(names, (s1, s2) -> s1.compareTo(s2));
        
        // メソッド参照（さらに簡潔）
        Collections.sort(names, String::compareTo);
    }
}
```

## 9.2 関数型インタフェース

### 標準の関数型インタフェース

```java
import java.util.function.*;

public class FunctionalInterfaceExample {
    public static void main(String[] args) {
        // Predicate<T>: T -> boolean
        Predicate<String> isEmpty = s -> s.isEmpty();
        Predicate<Integer> isEven = n -> n % 2 == 0;
        
        System.out.println(isEmpty.test(""));      // true
        System.out.println(isEven.test(4));       // true
        
        // Function<T, R>: T -> R
        Function<String, Integer> stringLength = s -> s.length();
        Function<Integer, String> intToString = n -> String.valueOf(n);
        
        System.out.println(stringLength.apply("Hello"));  // 5
        System.out.println(intToString.apply(123));        // "123"
        
        // Consumer<T>: T -> void
        Consumer<String> printer = s -> System.out.println(s);
        Consumer<Integer> doubler = n -> System.out.println(n * 2);
        
        printer.accept("Hello World");
        doubler.accept(5);  // 10
        
        // Supplier<T>: () -> T
        Supplier<Double> randomValue = () -> Math.random();
        Supplier<String> greeting = () -> "こんにちは";
        
        System.out.println(randomValue.get());
        System.out.println(greeting.get());
        
        // BinaryOperator<T>: (T, T) -> T
        BinaryOperator<Integer> add = (a, b) -> a + b;
        BinaryOperator<String> concat = (s1, s2) -> s1 + s2;
        
        System.out.println(add.apply(10, 5));        // 15
        System.out.println(concat.apply("Java", " 8")); // "Java 8"
    }
}
```

### カスタム関数型インタフェース

```java
@FunctionalInterface
public interface MathOperation {
    double calculate(double a, double b);
}

@FunctionalInterface
public interface StringProcessor {
    String process(String input);
}

public class CustomFunctionalInterface {
    public static void main(String[] args) {
        // カスタム関数型インターフェースの使用
        MathOperation addition = (a, b) -> a + b;
        MathOperation multiplication = (a, b) -> a * b;
        MathOperation power = (a, b) -> Math.pow(a, b);
        
        System.out.println(addition.calculate(5, 3));        // 8.0
        System.out.println(multiplication.calculate(4, 7));  // 28.0
        System.out.println(power.calculate(2, 3));           // 8.0
        
        StringProcessor upperCase = s -> s.toUpperCase();
        StringProcessor reverse = s -> new StringBuilder(s).reverse().toString();
        StringProcessor addPrefix = s -> "【重要】" + s;
        
        System.out.println(upperCase.process("hello"));      // "HELLO"
        System.out.println(reverse.process("Java"));         // "avaJ"
        System.out.println(addPrefix.process("お知らせ"));    // "【重要】お知らせ"
    }
}
```

## 9.3 メソッド参照

### 各種メソッド参照

```java
import java.util.*;
import java.util.function.*;

public class MethodReferenceExample {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("apple", "banana", "cherry");
        
        // 静的メソッド参照
        words.forEach(System.out::println);
        
        // インスタンスメソッド参照（特定のオブジェクト）
        String prefix = "Fruit: ";
        Function<String, String> addPrefix = prefix::concat;
        
        // インスタンスメソッド参照（任意のオブジェクト）
        Function<String, String> toUpper = String::toUpperCase;
        Function<String, Integer> getLength = String::length;
        
        words.stream()
             .map(String::toUpperCase)  // メソッド参照
             .forEach(System.out::println);
        
        // コンストラクタ参照
        Supplier<List<String>> listSupplier = ArrayList::new;
        Function<String, StringBuilder> sbBuilder = StringBuilder::new;
        
        List<String> newList = listSupplier.get();
        StringBuilder sb = sbBuilder.apply("Hello");
    }
    
    // 静的メソッドの例
    public static void printWithPrefix(String s) {
        System.out.println("Value: " + s);
    }
}
```

## 9.4 高階関数

### 関数を引数として受け取る

```java
import java.util.*;
import java.util.function.*;

public class HigherOrderFunction {
    
    // 高階関数：関数を引数として受け取る
    public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (T item : list) {
            if (predicate.test(item)) {
                result.add(item);
            }
        }
        return result;
    }
    
    public static <T, R> List<R> map(List<T> list, Function<T, R> mapper) {
        List<R> result = new ArrayList<>();
        for (T item : list) {
            result.add(mapper.apply(item));
        }
        return result;
    }
    
    public static <T> T reduce(List<T> list, T identity, BinaryOperator<T> accumulator) {
        T result = identity;
        for (T item : list) {
            result = accumulator.apply(result, item);
        }
        return result;
    }
    
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 偶数のフィルタリング
        List<Integer> evenNumbers = filter(numbers, n -> n % 2 == 0);
        System.out.println("偶数: " + evenNumbers);
        
        // 2倍にマッピング
        List<Integer> doubled = map(numbers, n -> n * 2);
        System.out.println("2倍: " + doubled);
        
        // 合計の計算
        Integer sum = reduce(numbers, 0, (a, b) -> a + b);
        System.out.println("合計: " + sum);
        
        // チェイニング
        List<String> result = map(
            filter(numbers, n -> n % 2 == 0),
            n -> "Number: " + n
        );
        System.out.println("結果: " + result);
    }
}
```

### 関数を返す関数

```java
import java.util.function.*;

public class FunctionFactory {
    
    // 関数を返す関数
    public static Function<Integer, Integer> multiplier(int factor) {
        return x -> x * factor;
    }
    
    public static Predicate<String> lengthChecker(int minLength) {
        return s -> s.length() >= minLength;
    }
    
    public static Function<String, String> decorator(String prefix, String suffix) {
        return s -> prefix + s + suffix;
    }
    
    public static void main(String[] args) {
        // 乗算器の作成
        Function<Integer, Integer> doubler = multiplier(2);
        Function<Integer, Integer> tripler = multiplier(3);
        
        System.out.println(doubler.apply(5));  // 10
        System.out.println(tripler.apply(4));  // 12
        
        // 長さチェッカーの作成
        Predicate<String> longEnough = lengthChecker(5);
        
        System.out.println(longEnough.test("Hello"));     // true
        System.out.println(longEnough.test("Hi"));        // false
        
        // デコレーターの作成
        Function<String, String> htmlTag = decorator("<h1>", "</h1>");
        Function<String, String> bracket = decorator("[", "]");
        
        System.out.println(htmlTag.apply("タイトル"));      // "<h1>タイトル</h1>"
        System.out.println(bracket.apply("重要"));         // "[重要]"
    }
}
```

## 9.5 関数の合成

```java
import java.util.function.*;

public class FunctionComposition {
    public static void main(String[] args) {
        Function<String, String> addHello = s -> "Hello " + s;
        Function<String, String> addExclamation = s -> s + "!";
        Function<String, String> toUpper = String::toUpperCase;
        
        // andThen: f.andThen(g) = g(f(x))
        Function<String, String> greetingComposed = addHello
            .andThen(addExclamation)
            .andThen(toUpper);
        
        System.out.println(greetingComposed.apply("World"));  // "HELLO WORLD!"
        
        // compose: f.compose(g) = f(g(x))
        Function<String, Integer> getLength = String::length;
        Function<Integer, Integer> doubleValue = x -> x * 2;
        
        Function<String, Integer> lengthDoubler = doubleValue.compose(getLength);
        System.out.println(lengthDoubler.apply("Hello"));  // 10
        
        // Predicateの合成
        Predicate<String> isLong = s -> s.length() > 5;
        Predicate<String> startsWithA = s -> s.startsWith("A");
        
        Predicate<String> longAndStartsWithA = isLong.and(startsWithA);
        Predicate<String> shortOrStartsWithA = isLong.negate().or(startsWithA);
        
        System.out.println(longAndStartsWithA.test("Application"));  // true
        System.out.println(shortOrStartsWithA.test("App"));          // true
    }
}
```

## 9.6 実践的な例

### イベント処理システム

```java
import java.util.*;
import java.util.function.*;

class Event {
    private String type;
    private String message;
    private long timestamp;
    
    public Event(String type, String message) {
        this.type = type;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getType() { return type; }
    public String getMessage() { return message; }
    public long getTimestamp() { return timestamp; }
    
    @Override
    public String toString() {
        return String.format("[%s] %s (%d)", type, message, timestamp);
    }
}

public class EventProcessor {
    private List<Consumer<Event>> handlers = new ArrayList<>();
    
    public void addHandler(Consumer<Event> handler) {
        handlers.add(handler);
    }
    
    public void addConditionalHandler(Predicate<Event> condition, Consumer<Event> handler) {
        handlers.add(event -> {
            if (condition.test(event)) {
                handler.accept(event);
            }
        });
    }
    
    public void processEvent(Event event) {
        handlers.forEach(handler -> handler.accept(event));
    }
    
    public static void main(String[] args) {
        EventProcessor processor = new EventProcessor();
        
        // 全イベントをログに記録
        processor.addHandler(event -> 
            System.out.println("LOG: " + event));
        
        // エラーイベントのみ特別処理
        processor.addConditionalHandler(
            event -> "ERROR".equals(event.getType()),
            event -> System.err.println("ERROR ALERT: " + event.getMessage())
        );
        
        // 情報イベントをファイルに保存（模擬）
        processor.addConditionalHandler(
            event -> "INFO".equals(event.getType()),
            event -> System.out.println("SAVED TO FILE: " + event.getMessage())
        );
        
        // テストイベント
        processor.processEvent(new Event("INFO", "システム開始"));
        processor.processEvent(new Event("ERROR", "データベース接続エラー"));
        processor.processEvent(new Event("DEBUG", "デバッグ情報"));
    }
}
```

### 計算パイプライン

```java
import java.util.function.*;

public class CalculationPipeline {
    
    public static class Pipeline<T> {
        private Function<T, T> function;
        
        public Pipeline(Function<T, T> function) {
            this.function = function;
        }
        
        public Pipeline<T> then(Function<T, T> next) {
            return new Pipeline<>(function.andThen(next));
        }
        
        public T execute(T input) {
            return function.apply(input);
        }
    }
    
    public static void main(String[] args) {
        // 数値処理パイプライン
        Pipeline<Integer> numberPipeline = new Pipeline<Integer>(x -> x)
            .then(x -> x + 10)      // 10を加算
            .then(x -> x * 2)       // 2倍
            .then(x -> x - 5);      // 5を減算
        
        System.out.println(numberPipeline.execute(5));  // ((5+10)*2)-5 = 25
        
        // 文字列処理パイプライン
        Pipeline<String> stringPipeline = new Pipeline<String>(s -> s)
            .then(String::trim)
            .then(String::toLowerCase)
            .then(s -> s.replaceAll("\\s+", "_"))
            .then(s -> "processed_" + s);
        
        String result = stringPipeline.execute("  Hello World  ");
        System.out.println(result);  // "processed_hello_world"
    }
}
```

## 9.7 練習問題

1. 整数のリストを受け取り、偶数のみを抽出して2倍にし、降順でソートする関数を作成してください。

2. 文字列の検証を行う関数を組み合わせて、複合的な検証ルールを作成してください。

3. 簡単な電卓アプリケーションを作成し、演算を関数として渡せるようにしてください。

## まとめ

この章では、ラムダ式と関数型プログラミングの基礎を学習しました。関数型インタフェース、メソッド参照、高階関数、関数の合成など、モダンJavaの重要な概念を理解することで、より簡潔で読みやすいコードを書けるようになりました。