# <b>13章</b> <span>Stream APIと高度なコレクション操作</span> <small>宣言的データ処理のマスター</small>

## 本章の学習目標

### 前提知識

#### 必須
- 第10章のコレクションフレームワーク（List、Set、Map）
- 第11章のジェネリクス
- 第12章のラムダ式と関数型インターフェイス

#### 推奨
- ループ処理でのデータ処理経験
- ソートやフィルタリングの実装経験
- 従来の命令型プログラミングでのデータ処理経験

### 学習目標

本章では、Java 8で導入されたStream APIと高度なコレクション操作について学習します。
まず知識理解の面では、Stream APIの設計思想とその利点を深く理解します。
Stream APIは、データの処理をより宣言的で可読性の高いスタイルで表現することを可能にします。
中間操作と終端操作の概念と種類を学び、遅延評価（lazy evaluation）のしくみを理解することで、メモリ効率とパフォーマンスのしくみを学びます。
並列ストリーム（parallel stream）の理解と注意点も重要な学習ポイントで、マルチコア環境でのパフォーマンス向上とその落とし穴を学びます。

技能習得の面では、Stream APIを使った宣言的なデータ処理技術を身につけます。
複雑なデータ変換とフィルタリングの実装方法を学び、実務で必要となるデータ操作スキルを習得します。
Collectorsクラスを使った柔軟なデータ収集方法を学び、並列処理による性能向上の実践的な技術も習得します。

データ処理能力の観点からは、大量データをメモリ使用量を抑えて高速に処理するプログラムを実装できるようになることが目標です。
宣言的なデータ処理パイプラインの設計方法を学び、従来の命令型処理との状況に応じた使い分けができます。
これにより、処理の意図が明確になり、後からの修正やデバッグが容易になります。

最終的な到達レベルとしては、複雑なデータ処理要件をStream APIで簡潔に実装できます。
カスタムCollectorを作成して専用のデータ収集処理を実装する技術、並列ストリームを活用したマルチコア環境での並列データ処理の実現、そして従来のループ処理とStream処理を状況に応じて使い分ける能力が、本章の最終目標です。



## データ構造の選択

第10章では基本的なコレクションを学びましたが、それぞれのインターフェイスには異なる特性を持つ実装クラスが存在します。
状況に応じて最適なものを選択することが、パフォーマンスの良いプログラムを書く鍵となります。

### `ArrayList` vs `LinkedList`

| 操作 | `ArrayList` (内部配列) | `LinkedList` (連結リスト) |
| :--- | :--- | :--- |
| 要素の取得 (get) | 高速 (O(1)) | 低速 (O(n)) |
| 先頭/末尾への追加・削除 | 低速 (O(n)) / 高速 (O(1)) | 高速 (O(1)) |
| 中間への追加・削除 | 低速 (O(n)) | 高速 (O(1)) |

#### 使い分けの指針

リストの実装クラスを選択する際は、アプリケーションの使用パターンを正確に分析することが重要です。
ArrayListは、要素の参照（取得）が多く、リストのサイズがあまり変化しない場合に最適であり、
実際にほとんどのケースで第一候補となる使いやすい実装です。
内部的に配列を使用しているため、インデックスでのランダムアクセスが非常に高速で、メモリ効率も優秀です。

一方、LinkedListは、リストの先頭や中間で頻繁に要素を追加・削除する場合に特に有効です。
連結リストの性質上、ノードの挿入や削除はO(1)で実行できるため、
データのサイズが頻繁に変更されるアプリケーションでは優位性を発揮します。
ただし、ランダムアクセスが必要な場合は、O(n)の時間計算量となりパフォーマンスが低下することに注意が必要です。

### `HashSet` vs `LinkedHashSet` vs `TreeSet`

| 特徴 | `HashSet` | `LinkedHashSet` | `TreeSet` |
| :--- | :--- | :--- | :--- |
| 順序 | 保証されない | 挿入順 | ソート順 |
| 性能 | 最速 | `HashSet`よりわずかに遅い | `log(n)`時間（比較的低速） |
| null要素 | 許可 | 許可 | 不可 |

#### 使い分けの指針

Setの実装クラスの選択は、アプリケーションの要件によって決まります。
HashSetは、順序が不要で、とにかく高速に重複を除きたい場合に最適な選択です。
ハッシュテーブルを内部で使用しているため、要素の検索、追加、削除が平均でO(1)と非常に高速です。

LinkedHashSetは、要素を追加した順序を保持したい場合に使います。
HashSetの性能をほぼ保ちながら、追加順序を記憶するためのリンク情報を内部で管理しています。
これにより、データの出力順序が予測可能になり、テストやデバッグが容易になります。

TreeSetは、要素を常にソートされた状態に保ちたい場合に使用します。
内部的に赤黒木（Red-Black Tree）というバランス木を使用しているため、操作の時間計算量はlog(n)となります。
HashSetよりは低速ですが、要素が常にソートされた状態で管理されます。
これにより、即座にソート済みのデータを取得できる利点があります。

## ラムダ式によるカスタムソート

コレクションを独自のルールでソートしたい場合、`Comparator`インターフェイスを使います。
Java 8から導入されたラムダ式を使うと、この`Comparator`の実装を非常に簡潔に記述できます。

### 匿名クラスからラムダ式へ

ラムダ式が登場する前は、`Comparator`をその場で実装するために匿名クラスが使われていました。

<span class="listing-number">**サンプルコード13-1**</span>

```java
import java.util.Comparator;
// ...
// 匿名クラスを使ったComparatorの実装（古い書き方）
Comparator<Student> scoreComparator = new Comparator<Student>() {
    @Override
    public int compare(Student s1, Student s2) {
        return Integer.compare(s2.getScore(), s1.getScore()); // 点数の降順
    }
};
```
この冗長な記述は、ラムダ式を使うと以下のように書き換えられます。

```java
// ラムダ式を使ったComparatorの実装
Comparator<Student> scoreComparator = (s1, s2) -> Integer.compare(s2.getScore(), s1.getScore());
```
`compare`メソッドの実装だけを抜き出したような形になり、非常にシンプルになりました。`->`の左側がメソッドの引数、右側が処理本体です。

### `Comparator`の便利なメソッド

Java 8以降、`Comparator`インターフェイスには、ラムダ式と組み合わせてソート条件をより宣言的に記述できる便利なメソッドが追加されました。

- `Comparator.comparing(keyExtractor)`: 比較のキーとなる値を抽出する関数を渡す
- `reversed()`: 比較順序を逆にする
- `thenComparing(other)`: 比較結果が同じだった場合の、次の比較条件を指定する

<span class="listing-number">**サンプルコード13-2**</span>

```java
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class Student {
    String name;
    int score;
    int height;
    public Student(String name, int score, int height) { this.name = name; this.score = score; this.height = height; }
    public String getName() { return name; }
    public int getScore() { return score; }
    public int getHeight() { return height; }
    @Override public String toString() { return name + "(score:" + score + ", height:" + height + ")"; }
}

public class AdvancedSortExample {
    public static void main(String[] args) {
        List<Student> students = new ArrayList<>();
        students.add(new Student("Bob", 92, 170));
        students.add(new Student("Alice", 85, 165));
        students.add(new Student("Charlie", 92, 180));

        // 点数が高い順、同じ点数の場合は身長が高い順にソート
        Comparator<Student> comparator = Comparator
            .comparingInt(Student::getScore).reversed() // 点数で比較し、逆順（降順）に
            .thenComparingInt(Student::getHeight).reversed(); // 次に身長で比較し、逆順（降順）に

        students.sort(comparator); // List.sort()に渡す

        System.out.println(students);
        // 出力: [Charlie(score:92, height:180), Bob(score:92, height:170), Alice(score:85, height:165)]
    }
}
```
`Student::getScore`はメソッド参照とよい、`student -> student.getScore()`というラムダ式をさらに簡潔にした書き方です。

## Stream APIによる現代的なコレクション操作

Java 8で導入されたStream APIは、コレクションの要素の集まりを「データの流れ（ストリーム）」として扱い、その流れに対してさまざまな処理を連結（パイプライン化）していくしくみです。

### なぜStream APIが必要なのか？

#### 従来のコレクション操作では、forループとif文を組み合わせた命令型のコードを書く必要がありました。このアプローチには以下のような課題がありました

1. 可読性の問題：ネストしたループや複雑な条件分岐により、コードの意図が読み取りにくい
2. 並列処理の困難さ：マルチコアCPUを活用した並列処理を実装するには、複雑なスレッド管理が必要
3. 再利用性の低さ：処理ロジックがループ構造と密結合し、再利用が困難
4. null安全性の欠如：nullチェックが散在し、NullPointerExceptionのリスクが常に存在

Stream APIは、関数型プログラミングの概念を取り入れることでこれらの課題を解決します。
データ処理を「変換のパイプライン」として表現することで、より宣言的で理解しやすいコードを実現します。

### 遅延評価のしくみ

Stream APIの重要な特徴の1つが遅延評価（Lazy Evaluation）です。
中間操作（filter、mapなど）は、終端操作（collect、forEachなど）が呼ばれるまで実際には実行されません。
これにより、以下の利点があります。

- 不要な計算を避けることができる
- メモリ効率が向上する
- 無限ストリームの処理が可能になる

<span class="listing-number">**サンプルコード13-3**</span>

```java
// この時点ではまだフィルタリングは実行されない
Stream<Integer> stream = numbers.stream()
    .filter(n -> {
        System.out.println("Filtering: " + n);
        return n > 10;
    });

// collect()が呼ばれて初めてフィルタリングが実行される
List<Integer> result = stream.collect(Collectors.toList());
```

ラムダ式と組み合わせることで、コレクション操作を非常に宣言的（「何をするか」を記述するスタイル）で、かつ簡潔に書くことができます。

### Stream操作の基本パターン

`コレクション.stream().中間操作1().中間操作2()...終端操作()`。

#### ストリーム処理の3つのフェーズ

1. ストリームの生成： 
   - `list.stream()`コレクションからストリームを生成
   - `Arrays.stream(array)`配列からストリームを生成
   - `Stream.of(値1, 値2, ...)`個別の値からストリームを生成
   - `Stream.generate()`や`Stream.iterate()`無限ストリームの生成

2. 中間操作（Intermediate Operations）: 
##### 中間操作は新しいストリームを返すため、メソッドチェインで連結できる。主な中間操作
   - `filter(Predicate)`条件に合う要素のみを通過させる
   - `map(Function)`各要素を別の形に変換する
   - `flatMap(Function)`ネストした構造を平坦化する
   - `sorted()`要素をソートする
   - `distinct()`重複を除去する
   - `limit(n)`最初のn個の要素に制限する
   - `skip(n)`最初のn個の要素をスキップする

3. 終端操作（Terminal Operations）: 
   終端操作はストリームを「消費」し、結果を生成する。終端操作が呼ばれて初めて、すべての処理が実行される。
   - `collect(Collector)`結果を収集する（最も柔軟な終端操作）
   - `forEach(Consumer)`各要素に対して処理を実行
   - `count()`要素数を返す
   - `reduce(BinaryOperator)`要素を集約して単一の結果を生成
   - `anyMatch(Predicate)`いずれかの要素が条件を満たすか
   - `allMatch(Predicate)`すべての要素が条件を満たすか
   - `findFirst()`/`findAny()`最初の要素/任意の要素を取得

### `filter`: 条件に合う要素だけを抽出

`filter`は、条件（`Predicate`）に一致する要素だけを残します。

<span class="listing-number">**サンプルコード13-4**</span>

```java
List<Student> list = ...;
// 点数が90点以上の生徒だけを抽出
List<Student> highScorers = list.stream()
    .filter(s -> s.getScore() >= 90)
    .collect(Collectors.toList());
```

### `map`: 要素を別の形に変換

`map`は、各要素に関数（`Function`）を適用し、別の値に変換します。

<span class="listing-number">**サンプルコード13-5**</span>

```java
// 生徒のリストから、名前のリストを生成
List<String> names = list.stream()
    .map(Student::getName)
    .collect(Collectors.toList());
```

### 組み合わせた例

<span class="listing-number">**サンプルコード13-6**</span>

```java
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StreamExample {
    public static void main(String[] args) {
        List<Student> students = new ArrayList<>();
        students.add(new Student("Bob", 92, 170));
        students.add(new Student("Alice", 85, 165));
        students.add(new Student("David", 75, 170));
        students.add(new Student("Charlie", 92, 180));

        // 点数が80点より高く、身長が170cm以上の生徒の名前を、アルファベット順で取得する
        List<String> result = students.stream() // 1. ストリームを生成
            .filter(s -> s.getScore() > 80)      // 2. 点数でフィルタリング
            .filter(s -> s.getHeight() >= 170)   // 3. 身長でフィルタリング
            .map(Student::getName)               // 4. 名前に変換
            .sorted()                            // 5. アルファベット順にソート
            .collect(Collectors.toList());       // 6. 結果をリストに集約

        System.out.println(result);
        // 出力: [Bob, Charlie]
    }
}
```
従来の`for`ループと`if`文を組み合わせるよりも、処理の流れが明確で読みやすいコードになるのがStream APIの大きな利点です。

## Optional型による安全なnull処理

### null参照の問題

JavaプログラミングにおいてNullPointerException（NPE）は最も頻繁に遭遇する実行時エラーの1つです。Tony Hoareは自身が発明したnull参照を「10億ドルの過ち」と呼びました。従来のJavaコードでは、nullチェックが至る所に散在し、コードの可読性と保守性を著しく低下させていました。

### Optional型の概念

Java 8で導入されたOptionalクラスは、「値が存在する場合と存在しない場合がある」という概念を型レベルで表現します。これにより、nullの可能性を明示的に扱い、コンパイル時にnull安全性を強制できます。

#### Optional型の主な利点
- 明示的なnull可能性メソッドの戻り値がnullになる可能性を型で表現
- 強制的なnullチェック値を取り出す前に存在確認を強制
- 関数型スタイルの処理map、filter、flatMapなどの操作をチェインできる

### Optionalの作成

#### Optionalオブジェクトを作成する3つの基本的な方法があります

<span class="listing-number">**サンプルコード13-7**</span>

```java
import java.util.Optional;

public class OptionalCreationExample {
    public static void main(String[] args) {
        // 1. of() - null以外の値を持つOptionalを作成
        // nullを渡すとNullPointerExceptionが発生する
        Optional<String> presentValue = Optional.of("Hello");
        System.out.println("of(): " + presentValue);  // Optional[Hello]
        
        // 2. ofNullable() - nullの可能性がある値をラップ
        // nullの場合は空のOptionalを返す
        String nullableString = null;
        Optional<String> nullableValue = Optional.ofNullable(nullableString);
        System.out.println("ofNullable(null): " + nullableValue);  // Optional.empty
        
        String nonNullString = "World";
        Optional<String> nonNullValue = Optional.ofNullable(nonNullString);
        System.out.println("ofNullable(\"World\"): " + nonNullValue);  // Optional[World]
        
        // 3. empty() - 空のOptionalを作成
        Optional<String> emptyValue = Optional.empty();
        System.out.println("empty(): " + emptyValue);  // Optional.empty
    }
}
```

### 値の取得方法

Optionalから値を取得する方法は、安全性のレベルに応じて複数用意されています。

<span class="listing-number">**サンプルコード13-8**</span>

```java
import java.util.Optional;
import java.util.NoSuchElementException;

public class OptionalRetrievalExample {
    public static void main(String[] args) {
        Optional<String> present = Optional.of("Hello");
        Optional<String> empty = Optional.empty();
        
        // 1. get() - 値が存在しない場合はNoSuchElementExceptionを投げる（非推奨）
        try {
            String value1 = present.get();
            System.out.println("get() with value: " + value1);  // Hello
            
            String value2 = empty.get();  // 例外発生！
        } catch (NoSuchElementException e) {
            System.out.println("get() on empty: " + e.getMessage());
        }
        
        // 2. orElse() - 値が存在しない場合のデフォルト値を指定
        String value3 = present.orElse("Default");
        String value4 = empty.orElse("Default");
        System.out.println("orElse() with value: " + value3);     // Hello
        System.out.println("orElse() on empty: " + value4);       // Default
        
        // 3. orElseGet() - 値が存在しない場合にSupplierから値を生成
        // orElseよりコストが低い（デフォルト値の生成コストが高い場合）
        String value5 = empty.orElseGet(() -> {
            System.out.println("Generating default value...");
            return "Generated Default";
        });
        System.out.println("orElseGet(): " + value5);  // Generated Default
        
        // 4. orElseThrow() - 値が存在しない場合に指定した例外を投げる
        try {
            String value6 = empty.orElseThrow(() -> 
                new IllegalStateException("値が存在しません"));
        } catch (IllegalStateException e) {
            System.out.println("orElseThrow(): " + e.getMessage());
        }
    }
}
```

### 値の存在確認と条件付き処理

値の存在を確認し、存在する場合のみ処理を実行する方法。

<span class="listing-number">**サンプルコード13-9**</span>

```java
import java.util.Optional;

public class OptionalPresenceExample {
    public static void main(String[] args) {
        Optional<String> present = Optional.of("Hello");
        Optional<String> empty = Optional.empty();
        
        // 1. isPresent() - 値が存在するかをboolean値で返す
        if (present.isPresent()) {
            System.out.println("isPresent: " + present.get());
        }
        
        // 2. isEmpty() - 値が存在しないかをboolean値で返す（Java 11以降）
        if (empty.isEmpty()) {
            System.out.println("isEmpty: 値が存在しません");
        }
        
        // 3. ifPresent() - 値が存在する場合のみ処理を実行（関数型スタイル）
        present.ifPresent(value -> 
            System.out.println("ifPresent: " + value));
        
        empty.ifPresent(value -> 
            System.out.println("この行は実行されません"));
        
        // 4. ifPresentOrElse() - 値の有無で処理を分岐（Java 9以降）
        present.ifPresentOrElse(
            value -> System.out.println("値あり: " + value),
            () -> System.out.println("値なし")
        );
        
        empty.ifPresentOrElse(
            value -> System.out.println("この行は実行されません"),
            () -> System.out.println("空のOptional")
        );
    }
}
```

### Optionalの変換操作

Optionalは関数型プログラミングのコンテナとして、map、flatMap、filterなどの操作をサポートします。

<span class="listing-number">**サンプルコード13-10**</span>

```java
import java.util.Optional;

public class OptionalTransformationExample {
    static class User {
        private String name;
        private Optional<String> email;
        
        public User(String name, String email) {
            this.name = name;
            this.email = Optional.ofNullable(email);
        }
        
        public String getName() { return name; }
        public Optional<String> getEmail() { return email; }
    }
    
    public static void main(String[] args) {
        Optional<User> user = Optional.of(new User("Alice", "alice@example.com"));
        Optional<User> userWithoutEmail = Optional.of(new User("Bob", null));
        
        // 1. map() - 値が存在する場合に変換を適用
        Optional<String> userName = user.map(User::getName);
        System.out.println("map: " + userName);  // Optional[Alice]
        
        // ネストしたmapの例
        Optional<Integer> nameLength = user
            .map(User::getName)
            .map(String::length);
        System.out.println("Nested map: " + nameLength);  // Optional[5]
        
        // 2. flatMap() - Optional<Optional<T>>を平坦化
        // getUserはOptional<User>を返し、getEmailもOptional<String>を返す
        Optional<String> email = user.flatMap(u -> u.getEmail());
        System.out.println("flatMap: " + email);  // Optional[alice@example.com]
        
        Optional<String> noEmail = userWithoutEmail.flatMap(u -> u.getEmail());
        System.out.println("flatMap (no email): " + noEmail);  // Optional.empty
        
        // 3. filter() - 条件を満たす場合のみ値を保持
        Optional<User> longNameUser = user
            .filter(u -> u.getName().length() > 3);
        System.out.println("filter (passed): " + longNameUser.isPresent());  // true
        
        Optional<User> shortNameUser = user
            .filter(u -> u.getName().length() > 10);
        System.out.println("filter (failed): " + shortNameUser.isPresent());  // false
    }
}
```

### Stream APIとの連携

OptionalはStream APIと密接に連携し、ストリーム処理の結果としてよく使用されます。

<span class="listing-number">**サンプルコード13-11**</span>

```java
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OptionalStreamExample {
    static class Product {
        private String name;
        private double price;
        
        public Product(String name, double price) {
            this.name = name;
            this.price = price;
        }
        
        public String getName() { return name; }
        public double getPrice() { return price; }
    }
    
    public static void main(String[] args) {
        List<Product> products = Arrays.asList(
            new Product("Laptop", 1200.0),
            new Product("Mouse", 25.0),
            new Product("Keyboard", 75.0),
            new Product("Monitor", 300.0)
        );
        
        // findFirst() - 最初の要素をOptionalで返す
        Optional<Product> firstExpensive = products.stream()
            .filter(p -> p.getPrice() > 100)
            .findFirst();
        
        firstExpensive.ifPresent(p -> 
            System.out.println("最初の高額商品: " + p.getName()));
        
        // findAny() - 任意の要素をOptionalで返す（並列処理に最適）
        Optional<Product> anyProduct = products.parallelStream()
            .filter(p -> p.getPrice() < 50)
            .findAny();
        
        System.out.println("安価な商品: " + 
            anyProduct.map(Product::getName).orElse("なし"));
        
        // max/min - 最大値・最小値をOptionalで返す
        Optional<Product> mostExpensive = products.stream()
            .max((p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));
        
        mostExpensive.ifPresent(p -> 
            System.out.println("最高額商品: " + p.getName() + " ($" + p.getPrice() + ")"));
        
        // OptionalをStreamに変換（Java 9以降）
        List<String> expensiveProductNames = products.stream()
            .map(p -> p.getPrice() > 500 ? Optional.of(p.getName()) : Optional.<String>empty())
            .flatMap(Optional::stream)  // Optional -> Stream変換
            .collect(Collectors.toList());
        
        System.out.println("高額商品リスト: " + expensiveProductNames);
    }
}
```

### 実践的な使用例

実際のアプリケーションでOptionalを効果的に使用する例を見てみましょう。

<span class="listing-number">**サンプルコード13-12**</span>

```java
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class OptionalPracticalExample {
    // データベースの代わりとなるメモリ内ストレージ
    static class UserRepository {
        private Map<Long, User> users = new HashMap<>();
        
        public UserRepository() {
            users.put(1L, new User(1L, "Alice", "alice@example.com"));
            users.put(2L, new User(2L, "Bob", null));  // メールアドレスなし
        }
        
        // Optionalを返すことで、ユーザーが見つからない可能性を明示
        public Optional<User> findById(Long id) {
            return Optional.ofNullable(users.get(id));
        }
    }
    
    static class User {
        private Long id;
        private String name;
        private String email;
        
        public User(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
        
        public Long getId() { return id; }
        public String getName() { return name; }
        public Optional<String> getEmail() { 
            return Optional.ofNullable(email); 
        }
    }
    
    static class ConfigService {
        private Map<String, String> config = new HashMap<>();
        
        public ConfigService() {
            config.put("app.name", "MyApp");
            config.put("app.version", "1.0.0");
            // app.timeoutは設定されていない
        }
        
        // 設定値の取得（存在しない可能性を考慮）
        public Optional<String> getConfig(String key) {
            return Optional.ofNullable(config.get(key));
        }
        
        // タイムアウト値の取得（デフォルト値付き）
        public int getTimeout() {
            return getConfig("app.timeout")
                .map(Integer::parseInt)
                .orElse(30);  // デフォルト30秒
        }
    }
    
    public static void main(String[] args) {
        UserRepository repo = new UserRepository();
        ConfigService config = new ConfigService();
        
        // データベース検索の例
        Long userId = 1L;
        String emailDomain = repo.findById(userId)
            .flatMap(User::getEmail)
            .map(email -> email.substring(email.indexOf("@") + 1))
            .orElse("ドメインなし");
        
        System.out.println("ユーザー " + userId + " のメールドメイン: " + emailDomain);
        
        // 存在しないユーザーの処理
        Long nonExistentId = 999L;
        repo.findById(nonExistentId)
            .ifPresentOrElse(
                user -> System.out.println("ユーザー名: " + user.getName()),
                () -> System.out.println("ユーザーID " + nonExistentId + " は存在しません")
            );
        
        // 設定値の取得
        String appName = config.getConfig("app.name")
            .orElse("Unknown App");
        System.out.println("アプリケーション名: " + appName);
        
        int timeout = config.getTimeout();
        System.out.println("タイムアウト値: " + timeout + "秒");
    }
}
```

### アンチパターンと注意点

Optionalを使用する際に避けるべき一般的な間違いとベストプラクティス。

<span class="listing-number">**サンプルコード13-13**</span>

```java
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

public class OptionalAntiPatternsExample {
    static class BadExample {
        // アンチパターン1: フィールドにOptionalを使用（避けるべき）
        private Optional<String> name = Optional.empty();  // 悪い例
        
        // アンチパターン2: メソッド引数にOptionalを使用（避けるべき）
        public void processUser(Optional<String> userName) {  // 悪い例
            // 処理...
        }
        
        // アンチパターン3: コレクションにOptionalを使用（避けるべき）
        public Optional<List<String>> getNames() {  // 悪い例
            // 空のリストを返すべき
            return Optional.empty();
        }
    }
    
    static class GoodExample {
        // 良い例1: フィールドは通常の型で、getterでOptionalを返す
        private String name;  // nullableフィールド
        
        public Optional<String> getName() {
            return Optional.ofNullable(name);
        }
        
        // 良い例2: メソッド引数は通常の型で、内部でOptionalに変換
        public void processUser(String userName) {
            Optional.ofNullable(userName)
                .filter(name -> !name.isEmpty())
                .ifPresent(name -> System.out.println("Processing: " + name));
        }
        
        // 良い例3: コレクションは空のコレクションを返す
        public List<String> getNames() {
            // nullの代わりに空のリストを返す
            return names != null ? names : new ArrayList<>();
        }
        
        private List<String> names;
    }
    
    public static void main(String[] args) {
        // アンチパターン4: get()の過度な使用
        Optional<String> optional = Optional.of("value");
        
        // 悪い例：isPresentとgetの組み合わせ
        if (optional.isPresent()) {
            String value = optional.get();
            System.out.println(value.toUpperCase());
        }
        
        // 良い例：mapを使用
        optional.map(String::toUpperCase)
            .ifPresent(System.out::println);
        
        // アンチパターン5: Optional<Optional<T>>の作成
        // これは避けて、flatMapを使用すべき
        
        // 注意点：Optionalは値の一時的なコンテナ
        // - Serializableではない
        // - 長期保存には適さない
        // - APIの戻り値として使用し、フィールドには使わない
    }
}
```

### Optionalのベストプラクティス

1. APIデザイン： メソッドの戻り値にOptionalを使用し、nullを返す可能性を明示
2. チェイン操作： map、flatMap、filterを活用して宣言的なコードを書く
3. デフォルト値： orElse、orElseGetを使用してコストやパフォーマンスを考慮したデフォルト値を提供
4. 早期リターン： 値が存在しない場合の処理を早めに行う
5. Stream統合： Stream APIと組み合わせて、より表現力豊かなコードを実現

Optionalをメソッドの戻り値やチェイン操作で使用することで、NullPointerExceptionを防ぎ、より安全で読みやすいコードを書くことができます。

## 並列ストリームによるパフォーマンス向上

### マルチコア時代のプログラミング

現代のCPUは複数のコアを持ち、複数の処理を同時に実行できます。しかし、従来のループ処理はシングルスレッドで実行されるため、1つのコアしか使用しません。Stream APIの並列ストリームを使うと、データ処理を自動的に複数のコアに分散して実行できます。

### 並列ストリームの使い方

並列ストリームの使用は非常に簡単で、`.stream()`の代わりに`.parallelStream()`を使うだけです。

<span class="listing-number">**サンプルコード13-14**</span>

```java
// シーケンシャル（直列）処理
long sum = numbers.stream()
    .filter(n -> n % 2 == 0)
    .mapToLong(n -> n * n)
    .sum();

// 並列処理
long sumParallel = numbers.parallelStream()
    .filter(n -> n % 2 == 0)
    .mapToLong(n -> n * n)
    .sum();
```

### 並列ストリームの内部動作

並列ストリームは、JavaのFork/Joinフレームワークを使用して実装されています。処理の流れは以下のようになります。

1. 分割（Split）：データを複数の小さなチャンクに分割
2. 処理（Process）：各チャンクを異なるスレッドで並列に処理
3. 統合（Combine）：各スレッドの結果を統合して最終結果を生成

<span class="listing-number">**サンプルコード13-15**</span>

```java
import java.util.stream.IntStream;
import java.time.Duration;
import java.time.Instant;

public class ParallelStreamExample {
    public static void main(String[] args) {
        int size = 100_000_000;
        
        // シーケンシャル処理の計測
        Instant start = Instant.now();
        double sumSeq = IntStream.range(0, size)
            .mapToDouble(Math::sqrt)
            .sum();
        Duration seqTime = Duration.between(start, Instant.now());
        
        // 並列処理の計測
        start = Instant.now();
        double sumPar = IntStream.range(0, size)
            .parallel()
            .mapToDouble(Math::sqrt)
            .sum();
        Duration parTime = Duration.between(start, Instant.now());
        
        System.out.println("シーケンシャル: " + seqTime.toMillis() + "ms");
        System.out.println("並列: " + parTime.toMillis() + "ms");
        System.out.println("高速化率: " + 
            (double)seqTime.toMillis() / parTime.toMillis() + "倍");
    }
}
```

### 並列ストリームの注意点

並列ストリームは強力な機能ですが、すべての状況で性能が向上するわけではありません。

#### オーバーヘッドの問題
スレッドの作成やコンテキストスイッチにはコストがかかります。データ量が少ない場合、このオーバーヘッドが並列化の利点を上回ることがあります。

#### スレッドセーフティ
並列処理では、複数のスレッドが同じデータにアクセスする可能性があるため、スレッドセーフでない操作は避ける必要があります。

<span class="listing-number">**サンプルコード13-16**</span>

```java
// スレッドセーフでない例（避けるとよい）
List<Integer> results = new ArrayList<>();  // スレッドセーフでない
numbers.parallelStream()
    .forEach(n -> results.add(n * 2));  // データ競合の危険！

// スレッドセーフな代替方法
List<Integer> results = numbers.parallelStream()
    .map(n -> n * 2)
    .collect(Collectors.toList());  // スレッドセーフ
```

#### 順序の保証
並列ストリームでは、処理の順序が保証されない場合があります。順序が重要な場合は、`forEachOrdered()`を使用するか、シーケンシャルストリームを使うことが重要です。

### 並列ストリームを使うとよい場合

以下の条件がそろった場合に、並列ストリームの使用を検討しましょう。

1. 大量のデータ：数千以上の要素を処理する場合
2. CPU集約的な処理：各要素の処理に計算コストがかかる場合
3. 状態を共有しない処理：各要素が独立して処理できる場合
4. 順序が重要でない場合：出力の順序が問題にならない場合

## 高度なStream操作

### flatMapによる複雑なデータ変換

`flatMap`は、ネストした構造を平坦化するために使用される重要な操作です。各要素をストリームに変換し、それらをすべて結合して1つのストリームにします。これは、リストのリストを単一のリストに変換する場合や、文字列を単語に分割する場合などに特に有用です。

<span class="listing-number">**サンプルコード13-17**</span>

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FlatMapExample {
    public static void main(String[] args) {
        List<List<String>> nestedList = Arrays.asList(
            Arrays.asList("Java", "Python"),
            Arrays.asList("JavaScript", "Go"),
            Arrays.asList("Rust", "C++")
        );
        
        // flatMapを使ってネストしたリストを平坦化する
        // この例では、リストのリストから単一の文字列リストを作成
        List<String> flatList = nestedList.stream()
            .flatMap(List::stream)  // 各内部リストをストリームに変換して結合
            .collect(Collectors.toList());
        
        System.out.println(flatList);
        // 出力: [Java, Python, JavaScript, Go, Rust, C++]
        
        // 文字列を単語に分割する例
        List<String> sentences = Arrays.asList(
            "Hello world",
            "Java programming",
            "Stream API"
        );
        
        // 各文字列を単語に分割し、すべての単語を1つのリストにまとめる
        List<String> words = sentences.stream()
            .flatMap(sentence -> Arrays.stream(sentence.split(" ")))
            .collect(Collectors.toList());
        
        System.out.println(words);
        // 出力: [Hello, world, Java, programming, Stream, API]
    }
}
```

`flatMap`は、一対多の関係を持つデータを処理する際に非常に強力です。各学生が複数の科目を履修している場合など、実世界のデータ構造でよく遭遇するパターンを簡潔に処理できます。

### reduceによる集約処理

`reduce`操作は、ストリームの要素を単一の結果に集約するための柔軟な方法を提供します。合計、最大値、最小値の計算や、文字列の結合など、カスタムの集約処理を実装できます。

<span class="listing-number">**サンプルコード13-18**</span>

```java
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ReduceExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        // 基本的なreduce操作：数値の合計を計算
        // reduce(初期値, 二項演算子)の形式
        int sum = numbers.stream()
            .reduce(0, (a, b) -> a + b);  // 初期値0から開始し、各要素を加算
        System.out.println("合計: " + sum); // 出力: 合計: 15
        
        // より複雑な例：最大値を求める（初期値なし）
        // この場合、空のストリームの可能性があるためOptionalが返される
        Optional<Integer> max = numbers.stream()
            .reduce((a, b) -> a > b ? a : b);
        max.ifPresent(value -> System.out.println("最大値: " + value)); // 出力: 最大値: 5
        
        // 文字列の結合例
        List<String> words = Arrays.asList("Java", "is", "awesome");
        String sentence = words.stream()
            .reduce("", (result, word) -> result.isEmpty() ? word : result + " " + word);
        System.out.println(sentence); // 出力: Java is awesome
        
        // 複雑な集約：学生の点数から統計情報を計算
        List<Student> students = Arrays.asList(
            new Student("Alice", 85, 165),
            new Student("Bob", 92, 170),
            new Student("Charlie", 78, 180)
        );
        
        // 全学生の平均点を計算（reduceを使った方法）
        double average = students.stream()
            .mapToInt(Student::getScore)
            .reduce(0, Integer::sum) / (double) students.size();
        System.out.println("平均点: " + average); // 出力: 平均点: 85.0
    }
}
```

`reduce`は非常に強力ですが、多くの一般的な集約操作には専用のメソッド（`sum()`, `max()`, `min()`など）や`collect()`操作が用意されているため、それらを優先して使用することが推奨されます。

### Collectorsによる高度なデータ収集

`Collectors`クラスは、ストリームの要素を様々な形で収集するための豊富なメソッドを提供します。単純なリスト作成から、複雑なグループ化や統計処理まで対応できます。

<span class="listing-number">**サンプルコード13-19**</span>

```java
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CollectorsExample {
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
            new Student("Alice", 85, 165),
            new Student("Bob", 92, 170),
            new Student("Charlie", 78, 180),
            new Student("David", 92, 175),
            new Student("Eve", 88, 160)
        );
        
        // 基本的なcollect操作：リストへの収集
        // 高得点（85点以上）の学生の名前をリストで取得
        List<String> highScorers = students.stream()
            .filter(s -> s.getScore() >= 85)
            .map(Student::getName)
            .collect(Collectors.toList());
        System.out.println("高得点者: " + highScorers);
        
        // グループ化の例：点数でグループ分け
        // 同じ点数の学生をグループ化する実用的な例
        Map<Integer, List<Student>> scoreGroups = students.stream()
            .collect(Collectors.groupingBy(Student::getScore));
        System.out.println("点数別グループ: " + scoreGroups);
        
        // より複雑な変換：条件による分類
        // 80点以上かどうかで学生を分類
        Map<Boolean, List<Student>> passFail = students.stream()
            .collect(Collectors.partitioningBy(s -> s.getScore() >= 80));
        System.out.println("合格者: " + passFail.get(true).size() + "人");
        System.out.println("不合格者: " + passFail.get(false).size() + "人");
    }
}
```

`Collectors.groupingBy()`は、データを特定の条件でグループ化する際に非常に強力で、データ分析やレポート作成において頻繁に使用されます。データベースのGROUP BY句に相当する処理をJavaのコード内で簡潔に実現できます。

### Optionalによる安全なデータ処理

`Optional`クラスは、null値が存在する可能性がある処理を安全に扱うためのJava 8で導入された重要な仕組みです。従来のnullポインタ例外を避けながら、よりエレガントなコードを書くことができます。

<span class="listing-number">**サンプルコード13-20**</span>

```java
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class OptionalExample {
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
            new Student("Alice", 85, 165),
            new Student("Bob", 92, 170),
            new Student("Charlie", 78, 180)
        );
        
        // Optionalの基本使用：最初の高得点者を安全に取得
        // findFirst()はOptional<Student>を返すため、null安全
        Optional<Student> firstHighScorer = students.stream()
            .filter(s -> s.getScore() >= 90)
            .findFirst();
        
        // 値が存在する場合のみ処理を実行
        firstHighScorer.ifPresent(student -> 
            System.out.println("最初の高得点者: " + student.getName()));
        
        // デフォルト値を使用した安全な処理
        String topScorerName = firstHighScorer
            .map(Student::getName)
            .orElse("該当者なし");
        System.out.println("トップスコアラー: " + topScorerName);
    }
}
```

### Optionalの連鎖操作

`Optional`は、複数の操作を安全に連鎖させることができ、従来のif-nullチェックの連続をメソッドチェーンで簡潔に表現できます。

<span class="listing-number">**サンプルコード13-21**</span>

```java
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class OptionalChainingExample {
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
            new Student("Alice", 85, 165),
            new Student("Bob", 92, 170),
            new Student("Charlie", 78, 180)
        );
        
        // Optionalの連鎖：複数の変換を安全に実行
        // 最高得点者の名前を大文字で取得（該当者がいない場合は"N/A"）
        String result = students.stream()
            .max((s1, s2) -> Integer.compare(s1.getScore(), s2.getScore()))
            .map(Student::getName)          // 学生が見つかれば名前を取得
            .map(String::toUpperCase)       // 名前を大文字に変換
            .orElse("N/A");                 // 見つからない場合のデフォルト値
        
        System.out.println("最高得点者（大文字）: " + result);
    }
}
```

### null安全な処理パターン

実際のアプリケーションでは、nullが混入する可能性があるデータを安全に処理します。`Optional`を活用することで、予期しないnullポインタ例外を防げます。

<span class="listing-number">**サンプルコード13-22**</span>

```java
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class NullSafeProcessingExample {
    public static void main(String[] args) {
        // nullが混入する可能性があるデータ
        List<String> namesWithNull = Arrays.asList("Alice", null, "Bob", "", "Charlie");
        
        // null安全な処理：nullや空文字列を確実に処理
        List<String> validNames = namesWithNull.stream()
            .filter(Objects::nonNull)           // nullを除外
            .filter(name -> !name.trim().isEmpty()) // 空文字列を除外
            .map(String::toUpperCase)           // 大文字に変換
            .collect(Collectors.toList());
        
        System.out.println("有効な名前: " + validNames);
        // 出力: 有効な名前: [ALICE, BOB, CHARLIE]
        
        // Optionalでnullをラップしてnullセーフ処理
        Optional<String> nullableName = Optional.ofNullable(null);
        String safeName = nullableName
            .filter(name -> !name.isEmpty())
            .map(String::toUpperCase)
            .orElse("UNKNOWN");
        
        System.out.println("安全な名前処理: " + safeName); // 出力: 安全な名前処理: UNKNOWN
    }
}
```

### 早期終了操作の活用

Stream APIには、すべての要素を処理せずに早期に結果を返す操作があります。これらは大量のデータを高速に処理する際に重要です。

<span class="listing-number">**サンプルコード13-23**</span>

```java
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ShortCircuitExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 早期終了操作：条件を満たす要素が見つかれば処理を停止
        // すべての要素が偶数かチェック（1つでも奇数があればfalse）
        boolean allEven = numbers.stream()
            .peek(n -> System.out.println("チェック中: " + n)) // 処理の流れを観察
            .allMatch(n -> n % 2 == 0);
        System.out.println("すべて偶数: " + allEven);
        
        // いずれかの要素が条件を満たすかチェック
        boolean hasLargeNumber = numbers.stream()
            .anyMatch(n -> n > 7);
        System.out.println("7より大きい数あり: " + hasLargeNumber);
        
        // 最初のn個に制限して処理
        List<Integer> firstThree = numbers.stream()
            .limit(3)  // 最初の3要素のみ処理
            .collect(Collectors.toList());
        System.out.println("最初の3つ: " + firstThree);
    }
}
```

早期終了操作は、特に大量のデータや無限ストリームを扱う際に、パフォーマンスの向上とメモリ効率の最適化に大きく貢献します。

### 実用的なtoArray操作

ストリーム処理の結果を配列として取得したい場合、`toArray()`メソッドを使用します。型安全な配列を取得するためには、正しい配列コンストラクタを指定することが重要です。

<span class="listing-number">**サンプルコード13-24**</span>

```java
import java.util.Arrays;
import java.util.List;

public class ToArrayExample {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");
        
        // toArrayの正しい使い方：型安全な配列を取得
        // String[]::newを指定することで、正しい型の配列が生成される
        String[] nameArray = names.stream()
            .filter(name -> name.length() > 3)  // 3文字より長い名前のみ
            .map(String::toUpperCase)           // 大文字に変換
            .toArray(String[]::new);            // String配列として取得
        
        System.out.println("結果配列: " + Arrays.toString(nameArray));
        // 出力: 結果配列: [ALICE, CHARLIE, DAVID]
        
        // サイズを指定した配列生成も可能
        String[] sizedArray = names.stream()
            .toArray(size -> new String[size]);
        System.out.println("サイズ指定配列: " + Arrays.toString(sizedArray));
    }
}
```

### 複雑な処理パイプラインの実装

実際のアプリケーションでは、複数のStream操作を組み合わせた複雑な処理パイプラインを構築することがよくあります。以下の例では、実用的なデータ処理シナリオを示します。

<span class="listing-number">**サンプルコード13-25**</span>

```java
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ComplexProcessingExample {
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
            new Student("Alice", 85, 165),
            new Student("Bob", 92, 170),
            new Student("Charlie", 78, 180),
            new Student("David", 92, 175),
            new Student("Eve", 88, 160),
            new Student("Frank", 82, 178),
            new Student("Grace", 95, 168)
        );
        
        // 複雑な処理：高得点者（85点以上）を身長でグループ化し、
        // 各グループで最高得点者の名前を取得
        Map<String, String> heightGroupTopScorers = students.stream()
            .filter(s -> s.getScore() >= 85)                    // 高得点者のみ
            .collect(Collectors.groupingBy(                     // 身長でグループ化
                s -> s.getHeight() >= 170 ? "高身長" : "標準身長",
                Collectors.collectingAndThen(                   // グループ内での最高得点者を取得
                    Collectors.maxBy((s1, s2) -> Integer.compare(s1.getScore(), s2.getScore())),
                    opt -> opt.map(Student::getName).orElse("該当者なし")
                )
            ));
        
        System.out.println("身長グループ別最高得点者:");
        heightGroupTopScorers.forEach((group, name) -> 
            System.out.println(group + ": " + name));
        
        // さらに複雑な例：統計情報の収集
        Map<String, Double> heightGroupAverages = students.stream()
            .collect(Collectors.groupingBy(
                s -> s.getHeight() >= 170 ? "高身長" : "標準身長",
                Collectors.averagingInt(Student::getScore)
            ));
        
        System.out.println("\n身長グループ別平均点:");
        heightGroupAverages.forEach((group, avg) -> 
            System.out.printf("%s: %.1f点%n", group, avg));
    }
}
```

このような複雑な処理も、Stream APIを使用することで、従来のネストしたループや一時的な変数を使った処理よりもはるかに読みやすく、保守しやすいコードとして実装できます。

## まとめ

本章では、コレクションフレームワークの応用的な側面と、それを操るための現代的な手法を学びました。

- データ構造の選択：`ArrayList`, `LinkedList`, `HashSet`, `TreeSet`など、状況に応じて最適な実装クラスを選択することが重要である
- ラムダ式と`Comparator`: ラムダ式を使うことで、独自のソートロジックを簡潔かつ宣言的に記述できる
- Stream API: `filter`, `map`, `sorted`, `collect`などの操作を連結することで、複雑なコレクション操作を直感的に記述できる
- Optional型：null参照の問題を型レベルで解決し、NullPointerExceptionを防ぐ安全なプログラミングを実現する
- 並列ストリーム：マルチコアCPUを活用した並列処理により、大量データの高速処理が可能になる
- 高度なStream操作：`flatMap`によるデータ平坦化、`reduce`による集約処理、早期終了操作など、実用的な高度技術を習得できる

これらの知識を身につけることで、簡潔で、保守性が高く、そして読みやすいJavaコードを書くことが可能になります。

## 章末演習

### 演習課題へのアクセス
本章の演習課題は、GitHubリポジトリで提供されています。
`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter13/`

### 課題構成
- 本章の基本概念の理解確認
- 応用的な実装練習
- 実践的な総合問題

詳細な課題内容と実装のヒントは、各課題フォルダ内のREADME.mdを参照してください。

## よくあるエラーと対処法

Stream APIと高度なコレクション操作の学習で遭遇する典型的なエラーと、その対処法について説明します。

### Stream操作のチェーンエラー

##### エラー例
```java
// ❌ 非効率的なStream操作のチェーン
List<String> words = Arrays.asList("apple", "banana", "cherry");
words.stream()
    .filter(word -> word.length() > 5)
    .map(String::toUpperCase)
    .filter(word -> word.startsWith("A"))  // 既にUPPERCASEに変換済み
    .forEach(System.out::println);
```

###### 問題点
- `map(String::toUpperCase)`後に`filter(word -> word.startsWith("A"))`を使用しているが、大文字変換後なので条件が一致しない

##### 対処法
```java
// ✅ 効率的なStream操作のチェーン
List<String> words = Arrays.asList("apple", "banana", "cherry");
words.stream()
    .filter(word -> word.length() > 5)
    .filter(word -> word.startsWith("a"))  // 大文字変換前にフィルタ
    .map(String::toUpperCase)
    .forEach(System.out::println);
```

### 終端操作の忘れ

##### エラー例
```java
// ❌ 終端操作を忘れた場合
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
numbers.stream()
    .filter(n -> n % 2 == 0)
    .map(n -> n * 2); // 終端操作がない
```

###### 問題点
- 終端操作がないため、中間操作は実行されない（遅延評価）
- コンパイルエラーは発生しないが、期待した処理が行われない

##### 対処法
```java
// ✅ 終端操作を追加
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
List<Integer> evenDoubled = numbers.stream()
    .filter(n -> n % 2 == 0)
    .map(n -> n * 2)
    .collect(Collectors.toList()); // 終端操作
```

### 並列処理での問題

##### エラー例
```java
// ❌ 並列処理での共有状態の変更
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
List<Integer> results = new ArrayList<>(); // 非同期安全でない

numbers.parallelStream()
    .filter(n -> n % 2 == 0)
    .forEach(n -> results.add(n * 2)); // 競合状態が発生
```

###### 問題点
- 複数のスレッドが同時に`ArrayList`に追加操作を行う
- データの破損や`ConcurrentModificationException`の発生

##### 対処法
```java
// ✅ 並列処理に適した方法
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);

// 方法1: collectを使用
List<Integer> results = numbers.parallelStream()
    .filter(n -> n % 2 == 0)
    .map(n -> n * 2)
    .collect(Collectors.toList());

// 方法2: 同期化されたコレクション
List<Integer> results = Collections.synchronizedList(new ArrayList<>());
numbers.parallelStream()
    .filter(n -> n % 2 == 0)
    .forEach(n -> results.add(n * 2));
```

### Optionalの誤用

##### エラー例
```java
// ❌ Optionalの誤った使用
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
Optional<String> result = names.stream()
    .filter(name -> name.startsWith("D"))
    .findFirst();

if (result.isPresent()) {
    System.out.println(result.get());
} else {
    System.out.println("Not found");
}
```

###### 問題点
- `isPresent()`と`get()`の組み合わせは冗長
- Optionalの利点が活かされていない

##### 対処法
```java
// ✅ Optionalの正しい使用
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
names.stream()
    .filter(name -> name.startsWith("D"))
    .findFirst()
    .ifPresentOrElse(
        System.out::println,
        () -> System.out.println("Not found")
    );

// またはデフォルト値を使用
String result = names.stream()
    .filter(name -> name.startsWith("D"))
    .findFirst()
    .orElse("Not found");
```

### パフォーマンスの問題

##### エラー例
```java
// ❌ 不効率なStream操作
List<Integer> numbers = IntStream.range(1, 1000000)
    .boxed()
    .collect(Collectors.toList());

// 毎回新しいStreamを作成
int sum = numbers.stream()
    .filter(n -> n % 2 == 0)
    .mapToInt(Integer::intValue)
    .sum();

int count = numbers.stream()
    .filter(n -> n % 2 == 0)
    .mapToInt(Integer::intValue)
    .reduce(0, Integer::sum);
```

###### 問題点
- 同じフィルタリング処理を複数回実行
- プリミティブ型の不要なボクシング/アンボクシング

##### 対処法
```java
// ✅ 効率的なStream操作
// 最初からIntStreamを使用
IntStream numbers = IntStream.range(1, 1000000);

// 単一のStreamで複数の結果を取得
IntSummaryStatistics stats = numbers
    .filter(n -> n % 2 == 0)
    .summaryStatistics();

System.out.println("Sum: " + stats.getSum());
System.out.println("Count: " + stats.getCount());
```

### 例外処理の問題

##### エラー例
```java
// ❌ Stream内での例外処理の問題
List<String> files = Arrays.asList("file1.txt", "file2.txt", "nonexistent.txt");
List<String> contents = files.stream()
    .map(file -> {
        return Files.readString(Path.of(file)); // IOException
    })
    .collect(Collectors.toList());
```

###### エラーメッセージ
```
error: Unhandled exception type IOException
```

##### 対処法
```java
// ✅ Stream内での確実な例外処理
List<String> files = Arrays.asList("file1.txt", "file2.txt", "nonexistent.txt");

// 方法1: 例外を無視してフィルタ
List<String> contents = files.stream()
    .map(file -> {
        try {
            return Optional.of(Files.readString(Path.of(file)));
        } catch (IOException e) {
            return Optional.<String>empty();
        }
    })
    .filter(Optional::isPresent)
    .map(Optional::get)
    .collect(Collectors.toList());

// 方法2: 例外をランタイム例外でラップ
List<String> contents = files.stream()
    .map(file -> {
        try {
            return Files.readString(Path.of(file));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    })
    .collect(Collectors.toList());
```

### 共通の対処戦略

1. Stream操作の順序を最適化する： フィルタリングは早い段階で実行し、処理量を減らす
2. 終端操作を必ず含める： 中間操作だけでは実際の処理が行われない
3. 並列処理の制限を理解する： 共有状態の変更を避け、スレッドセーフな操作を使用
4. Optionalを効果的に活用する： `isPresent()`と`get()`の組み合わせを避ける
5. プリミティブ型Streamを使用する： 数値計算処理では`IntStream`、`LongStream`、`DoubleStream`を使用
6. 例外処理を確実に行う： チェック例外は事前に処理するか、ランタイム例外でラップする
7. パフォーマンステストを実施する： 大量データでの並列処理の効果を測定する

## まとめ

本章では、コレクションの高度な使用方法とJava 8で導入されたStream APIについて詳しく学習しました。

- ラムダ式と`Comparator`: ラムダ式を使うことで、独自のソートロジックを簡潔かつ宣言的に記述できる
- Stream API: `filter`, `map`, `sorted`, `collect`などの操作を連結することで、複雑なコレクション操作を直感的に記述できる
- Optional型：null参照の問題を型レベルで解決し、NullPointerExceptionを防ぐ安全なプログラミングを実現する
- 並列ストリーム：マルチコアCPUを活用した並列処理により、大量データの高速処理が可能になる
- 高度なStream操作：`flatMap`によるデータ平坦化、`reduce`による集約処理、早期終了操作など、実用的な高度技術を習得できる

これらの知識を身につけることで、簡潔で、保守性が高く、そして読みやすいJavaコードを書くことが可能になります。