# <b>11章</b> <span>ラムダ式と関数インターフェイス</span> <small>関数型プログラミングへの誘い</small>

## 本章の学習目標

### この章で学ぶこと

1. ラムダ式の基本
    - 匿名関数の簡潔な表現方法、関数型インターフェイスとの関係
2. 標準関数型インターフェイス
    - Function、Predicate、Consumer、Supplierの使い分けと活用
3. メソッド参照
    - 既存メソッドの参照による簡潔な記述、4種類の参照形式
4. 関数型プログラミングスタイル
    - 宣言的プログラミング、関数の合成、副作用の最小化

### この章を始める前に

第10章までのオブジェクト指向プログラミングとジェネリクス、インターフェイスの設計と実装、コレクションフレームワークの使用経験があれば準備完了です。

## 匿名クラスからラムダ式へ

Java 8でラムダ式が導入される前、その場限りのインターフェイス実装を提供するためには匿名クラス（Anonymous Class）が使われていました。これは名前を持たないクラスで、とくにGUIのイベントリスナーなどで多用されていました。

本章のサンプルには、ラムダ式を渡す先として、後の章で学ぶ機能も出てきます。
主なものは、`Thread`と`ExecutorService`（第15章）と、GUIのイベントリスナー（第17章・第18章）です。
章の後半の「関数型プログラミングの実践例」では、第12章で学ぶStream API（`list.stream().filter(…).map(…)`）も使います。
これらは「ラムダ式をどこに渡しているか」に注目して読み、詳しい使い方は各章で確認してください。

### ラムダ式への第一歩：簡単な例から始める

まず、もっとも簡単な例でラムダ式の基本を理解しましょう。Runnableインターフェイスを使った例を見てみます。

以下のコードは、匿名クラスとラムダ式の記法を比較した例です。Runnableインターフェイスのrun()メソッドを実装する際、匿名クラスでは定型的な記述が必要でしたが、ラムダ式では本質的な処理内容だけを記述できます。これがラムダ式の最大の利点である簡潔性です。

<span class="listing-number">**サンプルコード11-1**　LambdaIntroExample.java</span>

```java
public class LambdaIntroExample {
    public static void main(String[] args) {
        // 従来の匿名クラスを使った方法
        Runnable task1 = new Runnable() {
            @Override
            public void run() {
                IO.println("Hello from anonymous class!");
            }
        };

        // ラムダ式を使った方法（同じ動作）
        Runnable task2 = () -> IO.println("Hello from lambda!");

        // 実行（どちらも同じ結果）
        task1.run();  // Hello from anonymous class!
        task2.run();  // Hello from lambda!
    }
}
```

**実行結果:**

```
Hello from anonymous class!
Hello from lambda!
```

ラムダ式は、匿名クラスの冗長な記述を簡潔に表現できます。`() ->` が「引数なしで何かを実行する」という意味になります。

<span class="listing-number">**サンプルコード11-2**</span>

```java
// 匿名クラスを使ったボタンのクリック処理
button.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        IO.println("ボタンがクリックされました！");
    }
});
```

<span class="footnote">※ このコードはGUIアプリケーション内での使用例を示しています。buttonオブジェクトは事前にJButtonとして定義されている前提です。</span>
このコードは、`actionPerformed`という1つのメソッドを実装するためだけに、`new ActionListener() { ... }`という定型的な記述が多く、冗長でした。

ラムダ式は、この匿名クラスの記述を、本質的な処理だけを抜き出して劇的に簡潔にするために導入されました。
サンプルコード11-2をラムダ式で書くと、次のように1行になります。

<span class="listing-number">**サンプルコード11-3**</span>

```java
// ラムダ式を使った場合
button.addActionListener(e -> IO.println("ボタンがクリックされました！"));
```

<span class="footnote">※ サンプルコード11-2と同じく、buttonオブジェクトは事前にJButtonとして定義されている前提です。</span>

## 関数型インターフェイス

ラムダ式は、どのような場所でも書けるわけではありません。ラムダ式は、関数型インターフェイス（Functional Interface）として扱われます。

<div class="figure">

![基本的な関数型インターフェイス](images/diagrams/chapter11-functional-interface-basic.svg)

</div>

<div class="figure">

![拡張関数型インターフェイス](images/diagrams/chapter11-functional-interface-extended.svg)

</div>

関数型インターフェイスとは、実装すべき抽象メソッドが1つだけ定義されているインターフェイスのことです。`@FunctionalInterface` アノテーションを付けると、コンパイラーが抽象メソッドが1つだけかどうかをチェックしてくれるため、付けるとよいでしょう。

<span class="listing-number">**サンプルコード11-4**　FunctionalInterfaceExample.java</span>

```java
@FunctionalInterface
interface MyFunction {
    int calculate(int x, int y);
}

public class FunctionalInterfaceExample {
    public static void main(String[] args) {
        // ラムダ式を関数型インターフェイス型の変数に代入
        MyFunction addition = (a, b) -> a + b;
        MyFunction subtraction = (a, b) -> a - b;

        IO.println("足し算: " + addition.calculate(10, 5)); // 15
        IO.println("引き算: " + subtraction.calculate(10, 5)); // 5
    }
}
```

**実行結果:**

```
足し算: 15
引き算: 5
```

サンプルコード11-2の`ActionListener`も、実装すべき抽象メソッドが`actionPerformed`の1つだけなので、関数型インターフェイスです。
そのため、サンプルコード11-3のようにラムダ式で置き換えられました。

### ラムダ式の構文バリエーション

ラムダ式は、状況に応じて記述をさらに簡略化できます。

- `(int a, int b) -> ...` は `(a, b) -> ...` と書ける
- `(a) -> ...` は `a -> ...` と書ける
- `a -> { return a * 2; }` は `a -> a * 2` と書ける
- `() -> IO.println("Hello");` のように括弧だけを書く

### `Comparator`による並べ替え

`Comparator<T>`は、2つのオブジェクトを比べる`compare`メソッドを実装する関数型インターフェイスです。
そのため、リストを並べ替える`sort`メソッドに渡す比較のルールを、匿名クラスの代わりにラムダ式で書けます。
`String`や`Integer`のように`Comparable`インターフェイスを実装したクラスは、自然な順序（文字列なら辞書順）を持ちます。
`Comparator`を使うと、自然な順序とは別の並べ方を、クラスを書き換えずに外から指定できます。
次のサンプルでは、学生を名前の順に並べる同じ処理を、匿名クラス、ラムダ式、`Comparator.comparing`の3通りで書いています。

<span class="listing-number">**サンプルコード11-5**</span>

```java
public class ComparatorLambdaExample {
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
            new Student("Charlie"),
            new Student("Alice"),
            new Student("Bob")
        );
        
        IO.println("ソート前: " + students);
        
        // 従来の方法：冗長で理解しにくい
        List<Student> students1 = new ArrayList<>(students);
        Collections.sort(students1, new Comparator<Student>() {
            @Override
            public int compare(Student s1, Student s2) {
                return s1.getName().compareTo(s2.getName());
            }
        });
        IO.println("従来の方法: " + students1);

        // ラムダ式：簡潔で意図が明確
        List<Student> students2 = new ArrayList<>(students);
        students2.sort((s1, s2) -> s1.getName().compareTo(s2.getName()));
        IO.println("ラムダ式: " + students2);

        // メソッド参照：さらに簡潔
        List<Student> students3 = new ArrayList<>(students);
        students3.sort(Comparator.comparing(Student::getName));
        IO.println("メソッド参照: " + students3);
    }
}
```

**実行結果:**

```
ソート前: [Charlie, Alice, Bob]
従来の方法: [Alice, Bob, Charlie]
ラムダ式: [Alice, Bob, Charlie]
メソッド参照: [Alice, Bob, Charlie]
```

`Comparator.comparing`は、比較に使うキー（ここでは名前）を取り出す関数を受け取り、そのキーで比べる`Comparator`を作ります。
`Student::getName`は`s -> s.getName()`と同じ意味のメソッド参照で、本章の「メソッド参照」で詳しく学びます。

実際の業務では、単一の条件だけでなく、複数の条件を組み合わせた並び替えが必要になることが多くあります。Comparatorインターフェイスでは、`thenComparing`メソッドを使って複数の条件を論理的に組み合わせることができ、これにより複雑な並び替えロジックも直感的に表現できます。

<span class="listing-number">**サンプルコード11-6**　MultiConditionSortExample.java（7〜53行目）</span>

```java
public class MultiConditionSortExample {
    public static void main(String[] args) {
        // 学生のサンプルデータ
        List<Student> students = Arrays.asList(
            new Student("Alice", 2, "S003"),
            new Student("Bob", 1, "S001"),
            new Student("Charlie", 2, "S002"),
            new Student("Alice", 1, "S004")
        );
        
        // 学年で並び替え、同じ学年の場合は名前で並び替え、
        // さらに同名の場合は学生番号で並び替え
        List<Student> sortedStudents = new ArrayList<>(students);
        sortedStudents.sort(
            Comparator.comparing(Student::getGrade)
                .thenComparing(Student::getName)
                .thenComparing(Student::getStudentId)
        );
        IO.println("学生ソート後（学年→名前→学生番号）:");
        sortedStudents.forEach(IO::println);

        // 降順ソートの組み合わせ
        List<Student> reversedStudents = new ArrayList<>(students);
        reversedStudents.sort(
            Comparator.comparing(Student::getGrade).reversed()
                .thenComparing(Student::getName)
        );
        IO.println("\n降順ソート（学年降順→名前昇順）:");
        reversedStudents.forEach(IO::println);

        // 複雑な条件：優先度、作成日時、タイトルの順
        List<Task> tasks = Arrays.asList(
            new Task(1, LocalDateTime.now().minusHours(3), "Task C"),
            new Task(3, LocalDateTime.now().minusHours(1), "Task A"),
            new Task(1, LocalDateTime.now().minusHours(2), "Task B")
        );
        
        List<Task> sortedTasks = new ArrayList<>(tasks);
        sortedTasks.sort(
            Comparator.comparing(Task::getPriority).reversed()
                .thenComparing(Task::getCreatedAt)
                .thenComparing(Task::getTitle)
        );
        IO.println("\nタスクソート後（優先度降順→作成日時→タイトル）:");
        sortedTasks.forEach(IO::println);
    }
}
```

**実行結果:**

```
学生ソート後（学年→名前→学生番号）:
Student{name='Alice', grade=1, id='S004'}
Student{name='Bob', grade=1, id='S001'}
Student{name='Alice', grade=2, id='S003'}
Student{name='Charlie', grade=2, id='S002'}

降順ソート（学年降順→名前昇順）:
Student{name='Alice', grade=2, id='S003'}
Student{name='Charlie', grade=2, id='S002'}
Student{name='Alice', grade=1, id='S004'}
Student{name='Bob', grade=1, id='S001'}

タスクソート後（優先度降順→作成日時→タイトル）:
Task{priority=3, title='Task A'}
Task{priority=1, title='Task C'}
Task{priority=1, title='Task B'}
```

`reversed()`は、それまでに組み立てた比較条件の全体を逆にします。
たとえば、サンプルコード11-6の2つ目の並べ替えを学年の降順・名前の降順にしようとして、`thenComparing(Student::getName)`の後ろにも`reversed()`を付けたとします。
すると、最後の`reversed()`が学年の降順まで逆にするため、学年の昇順・名前の降順に並んでしまいます。
2つ目以降の条件だけを降順にしたいときは、`thenComparing(Comparator.comparing(Student::getName).reversed())`のように、逆順にした`Comparator`を`thenComparing`に渡します。
また、整数のキーで比べるときは、`comparing`の代わりに`comparingInt`を使うと、`int`を`Integer`に変換せずに比べられます。

ここで組み立てた`Comparator`は、第12章で学ぶStream APIの`sorted`メソッドにもそのまま渡せます。

### `java.util.function` パッケージ

Javaには、`java.util.function`パッケージに、よく使われる汎用的な関数型インターフェイスが多数用意されています。これらを活用することで、自分でインターフェイスを定義する手間を省けます。

| インターフェイス | 抽象メソッド | 説明 |
| :--- | :--- | :--- |
| `Predicate<T>` | `boolean test(T t)` | T型を受け取り、`boolean`値を返す（判定） |
| `Function<T, R>` | `R apply(T t)` | T型を受け取り、R型を返す（変換） |
| `Consumer<T>` | `void accept(T t)` | T型を受け取り、何も返さない（消費） |
| `Supplier<T>` | `T get()` | 何も受け取らず、T型を返す（供給） |
| `UnaryOperator<T>` | `T apply(T t)` | T型を受け取り、同じT型を返す（単項演算） |
| `BinaryOperator<T>` | `T apply(T t1, T t2)` | 同じT型を2つ受け取り、同じT型を返す（二項演算） |

<span class="listing-number">**サンプルコード11-7**　StandardFunctionalInterfaces.java</span>

```java
import java.util.function.*;

public class StandardFunctionalInterfaces {
    public static void main(String[] args) {
        // Predicate: 文字列が空かどうかを判定
        Predicate<String> isEmpty = s -> s.isEmpty();
        IO.println("''は空？: " + isEmpty.test("")); // true
        IO.println("'Java'は空？: " + isEmpty.test("Java")); // false

        // Function: 文字列を長さに変換
        Function<String, Integer> getLength = s -> s.length();
        IO.println("'Java'の長さ: " + getLength.apply("Java")); // 4
        IO.println("'Hello World'の長さ: " + getLength.apply("Hello World")); // 11

        // Consumer: 文字列を大文字で出力
        Consumer<String> printUpper = s -> IO.println(s.toUpperCase());
        IO.print("'hello'を大文字で出力: ");
        printUpper.accept("hello"); // HELLO

        // Supplier: 現在時刻を供給
        Supplier<Long> currentTime = () -> System.currentTimeMillis();
        IO.println("現在時刻: " + currentTime.get());
        
        // UnaryOperator: 同じ型の変換
        UnaryOperator<String> addPrefix = s -> "Hello, " + s;
        IO.println("挨拶付き: " + addPrefix.apply("Java"));
        
        // BinaryOperator: 同じ型の2つの値から1つの値を生成
        BinaryOperator<Integer> multiply = (a, b) -> a * b;
        IO.println("5 × 7 = " + multiply.apply(5, 7));
    }
}
```

**実行結果:**

```
''は空？: true
'Java'は空？: false
'Java'の長さ: 4
'Hello World'の長さ: 11
'hello'を大文字で出力: HELLO
現在時刻: 1754043968876
挨拶付き: Hello, Java
5 × 7 = 35
```

## メソッド参照

ラムダ式が既存のメソッドを呼びだすだけの場合、メソッド参照（Method Reference）という、さらに簡潔な記法が使えます。`クラス名::メソッド名`や`インスタンス変数::メソッド名`のように記述します

| 種類 | 構文 | ラムダ式の例 |
| :--- | :--- | :--- |
| 静的メソッド参照 | `クラス名::静的メソッド名` | `s -> Integer.parseInt(s)` |
| インスタンスメソッド参照<br>(特定のインスタンス) | `インスタンス変数::メソッド名` | `s -> prefix.concat(s)` |
| インスタンスメソッド参照<br>(不特定のインスタンス) | `クラス名::メソッド名` | `s -> s.toUpperCase()` |
| コンストラクター参照 | `クラス名::new` | `() -> new ArrayList<>()` |

本書でよく使う`IO::println`は、`IO`クラスの静的メソッドへの参照なので、静的メソッド参照にあたります。ほかの資料でよく見かける`System.out::println`は、`System.out`というオブジェクト（特定のインスタンス）のメソッドへの参照です。

次のサンプルでは、ラムダ式を受け取る`List`のメソッドを2つ使います。
`forEach`は各要素を順に`Consumer`へ渡し、`replaceAll`は各要素を`UnaryOperator`の結果で置き換えます。

<span class="listing-number">**サンプルコード11-8**　MethodReferenceExample.java</span>

```java
import java.util.*;
import java.util.function.*;

public class MethodReferenceExample {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("apple", "banana", "cherry");
        
        IO.println("元のリスト: " + words);

        // 静的メソッド参照の例
        Function<String, Integer> parser = Integer::parseInt;  // s -> Integer.parseInt(s)
        int number = parser.apply("123");
        IO.println("文字列から整数への変換: " + number + " + 1 = " + (number + 1));

        // 静的メソッド参照（IO.printlnは静的メソッド）
        IO.println("\n各単語を出力:");
        words.forEach(IO::println);  // s -> IO.println(s)

        // インスタンスメソッド参照（特定のインスタンス）
        String prefix = "fruit: ";
        List<String> labeled = new ArrayList<>(words);
        labeled.replaceAll(prefix::concat);  // s -> prefix.concat(s)
        IO.println("\n接頭辞を付ける:");
        labeled.forEach(IO::println);

        // インスタンスメソッド参照（不特定のインスタンス）
        List<String> upper = new ArrayList<>(words);
        upper.replaceAll(String::toUpperCase);  // s -> s.toUpperCase()
        IO.println("\n大文字変換:");
        upper.forEach(IO::println);

        // コンストラクタ参照
        Supplier<List<String>> listFactory = ArrayList::new;  // () -> new ArrayList<>()
        List<String> newList = listFactory.get();
        newList.add("Hello");
        newList.add("World");
        IO.println("\n新しいリスト: " + newList);
    }
}
```

**実行結果:**

```
元のリスト: [apple, banana, cherry]
文字列から整数への変換: 123 + 1 = 124

各単語を出力:
apple
banana
cherry

接頭辞を付ける:
fruit: apple
fruit: banana
fruit: cherry

大文字変換:
APPLE
BANANA
CHERRY

新しいリスト: [Hello, World]
```

### ラムダ式とメソッド参照の比較

メソッド参照は、既存のメソッドを呼び出すだけのラムダ式をより簡潔に表現する方法です。
同じ処理をラムダ式とメソッド参照で書き比べてみましょう。
第12章で学ぶStream APIでも、メソッド参照をよく使います。

<span class="listing-number">**サンプルコード11-9**　LambdaVsMethodReference.java</span>

```java
import java.util.*;
import java.util.function.Function;

public class LambdaVsMethodReference {
    public static void main(String[] args) {
        // ラムダ式 vs メソッド参照の比較
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        
        IO.println("元のリスト: " + names);
        
        // ラムダ式：やや冗長
        IO.println("\nラムダ式を使用:");
        List<String> upper1 = new ArrayList<>(names);
        upper1.replaceAll(name -> name.toUpperCase());
        upper1.forEach(name -> IO.println(name));

        // メソッド参照：簡潔で意図が明確
        IO.println("\nメソッド参照を使用:");
        List<String> upper2 = new ArrayList<>(names);
        upper2.replaceAll(String::toUpperCase);
        upper2.forEach(IO::println);

        // 引数を受け取るコンストラクタの参照
        Function<String, Person> toPerson = Person::new;  // name -> new Person(name) と同じ
        List<Person> people = new ArrayList<>();
        for (String name : names) {
            people.add(toPerson.apply(name));
        }
        
        IO.println("\nPersonオブジェクトのリスト:");
        people.forEach(IO::println);
    }
}
```

**実行結果:**

```
元のリスト: [Alice, Bob, Charlie]

ラムダ式を使用:
ALICE
BOB
CHARLIE

メソッド参照を使用:
ALICE
BOB
CHARLIE

Personオブジェクトのリスト:
Person{name='Alice'}
Person{name='Bob'}
Person{name='Charlie'}
```

サンプルコード11-8の`ArrayList::new`は引数のないコンストラクターの参照でしたが、引数を受け取るコンストラクターも参照できます。
`Person::new`を`Function<String, Person>`に代入すると、文字列を1つ受け取る`Person(String)`のコンストラクターを指します。

## ラムダ式の応用例

ラムダ式はコレクション操作だけでなく、Javaプログラムのさまざまな場面でコードを簡潔にします。

### スレッドの生成と実行

`Runnable`インターフェイス（`run`メソッドを持つ関数型インターフェイス）もラムダ式で簡単に実装できます。
スレッドの使い方は第15章で詳しく学びます。
ここでは、`new Thread(…)`に渡す`Runnable`をラムダ式で書けることに注目してください（`start()`を呼ぶと、別のスレッドで`run`の処理を始めます）。

<span class="listing-number">**サンプルコード11-10**　RunnableLambdaExample.java（1〜8、14〜21、27〜33、44〜46行目）</span>

```java
public class RunnableLambdaExample {
    public static void main(String[] args) {
        IO.println("メインスレッド開始");
        
        // 匿名クラスでのRunnable
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                IO.println("Thread t1 (Anonymous) is running...");
                // ...
                IO.println("Thread t1 (Anonymous) finished");
            }
        });
        t1.start();

        // ラムダ式でのRunnable
        Thread t2 = new Thread(() -> {
            IO.println("Thread t2 (Lambda) is running...");
            // ...
            IO.println("Thread t2 (Lambda) finished");
        });
        t2.start();
        
        // より簡潔なラムダ式（単一ステートメント）
        Thread t3 = new Thread(() -> IO.println("Thread t3 (Simple Lambda) executed"));
        t3.start();
        // ...
        IO.println("メインスレッド終了");
    }
}
```

**実行結果:**

```
メインスレッド開始
Thread t1 (Anonymous) is running...
Thread t2 (Lambda) is running...
Thread t3 (Simple Lambda) executed
Thread t2 (Lambda) finished
Thread t1 (Anonymous) finished
メインスレッド終了
```

### スレッドプールへのタスクの受け渡し

並行処理において、ラムダ式はとくに威力を発揮します。従来の匿名クラスによる冗長な記述を避け、処理の本質的な内容に集中できるようになります。とくに、ExecutorServiceと組み合わせることで、スレッドプールを効率的に活用した並行処理を簡潔に記述できます。
`ExecutorService`は、用意したスレッドにタスクを割り振って実行するしくみで、第15章で学びます。
`Callable<String>`は値を返すタスクを表す関数型インターフェイスなので、タスクの中身をラムダ式で書けます。

<span class="listing-number">**サンプルコード11-11**　MethodReferenceConcurrencyExample.java（4〜18、23〜47行目）</span>

```java
public class MethodReferenceConcurrencyExample {
    public static void main(String[] args) {
        // ExecutorServiceとラムダ式の組み合わせ
        ExecutorService executor = Executors.newFixedThreadPool(4);
        
        // 複数のタスクを並行実行
        List<Callable<String>> tasks = Arrays.asList(
            () -> {
                Thread.sleep(1000);  // 1秒待機
                return "Task 1 completed";
            },
            () -> {
                Thread.sleep(500);   // 0.5秒待機
                return "Task 2 completed";
            }, 
            // ...
        );
        
        IO.println("タスク実行開始...");
        long startTime = System.currentTimeMillis();
        
        try {
            List<Future<String>> results = executor.invokeAll(tasks);
            IO.println("実行結果:");
            results.forEach(future -> {
                try {
                    IO.println(future.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
        
        long endTime = System.currentTimeMillis();
        IO.println("実行時間: " + (endTime - startTime) + "ms");
    }
}
```

**実行結果:**

```
タスク実行開始...
実行結果:
Task 1 completed
Task 2 completed
Task 3 completed
実行時間: 1511ms
```

### データバリデーション処理の関数型実装

複雑なバリデーションロジックは、関数型アプローチを使うことで、再利用可能で組み合わせ可能な小さな検証関数として実装できます。これにより、ビジネスルールの変更に柔軟に対応できるバリデーションシステムを構築できます。

<span class="listing-number">**サンプルコード11-12**　ValidationFramework.java（6〜22、36〜67行目）</span>

```java
public class ValidationFramework {
    // 基本的なバリデーション関数
    public static Predicate<String> notEmpty = s -> s != null && !s.trim().isEmpty();
    public static Predicate<String> validEmail = s -> s.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    public static Function<Integer, Predicate<String>> minLength = min -> s -> s.length() >= min;
    public static Function<Integer, Predicate<String>> maxLength = max -> s -> s.length() <= max;
    
    // 複合バリデーション
    public static Predicate<User> validUser = user ->
        notEmpty.test(user.getName()) &&
        validEmail.test(user.getEmail()) &&
        minLength.apply(8).test(user.getPassword());
    
    // エラーメッセージ付きバリデーション
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        // ...
    }
    
    public static Function<User, ValidationResult> validateUserWithErrors = user -> {
        List<String> errors = new ArrayList<>();
        
        if (!notEmpty.test(user.getName())) {
            errors.add("名前は必須です");
        }
        if (!validEmail.test(user.getEmail())) {
            errors.add("有効なメールアドレスを入力してください");
        }
        if (!minLength.apply(8).test(user.getPassword())) {
            errors.add("パスワードは8文字以上である必要があります");
        }
        
        return new ValidationResult(errors.isEmpty(), errors);
    };
    
    public static void main(String[] args) {
        // テストユーザー
        User validUser = new User("太郎", "taro@example.com", "password123");
        User invalidUser = new User("", "invalid-email", "123");
        
        IO.println("有効なユーザーのバリデーション:");
        ValidationResult result1 = validateUserWithErrors.apply(validUser);
        IO.println(result1);
        
        IO.println("\n無効なユーザーのバリデーション:");
        ValidationResult result2 = validateUserWithErrors.apply(invalidUser);
        IO.println(result2);
    }
}
```

**実行結果:**

```
有効なユーザーのバリデーション:
有効

無効なユーザーのバリデーション:
無効: 名前は必須です, 有効なメールアドレスを入力してください, パスワードは8文字以上である必要があります
```

## 関数型プログラミングの実践例

ここからは、ラムダ式と関数型インターフェイスを使った、実務に近い設計の例を紹介します。
本章の基本を学んだあとに読む発展的な内容で、多くは考え方を示すための断片的なコードです。
Stream API（第12章）や、外部ライブラリのReactorを使う例もあるので、最初はラムダ式をどこに渡しているかに注目して読んでください。
たとえば`list.stream().map(…).collect(Collectors.toList())`は、各要素を`map`に渡したラムダ式で変換し、結果を新しいリストに集める書き方です。

### 高階関数による処理の抽象化

高階関数（Higher-Order Function）は、関数を引数として受け取ったり、関数を戻り値として返したりする関数です。これにより、共通的な処理パターンを抽象化できます。

<span class="listing-number">**サンプルコード11-13**　HigherOrderFunctionExample.java（4〜46行目）</span>

```java
public class HigherOrderFunctionExample {
    // 高階関数によるリスク管理の抽象化
    public <T, R> Function<T, R> withRiskManagement(
        Function<T, R> strategy,
        Predicate<T> riskCheck,
        R defaultValue) {
        
        return input -> {
            if (riskCheck.test(input)) {
                return strategy.apply(input);
            }
            return defaultValue;
        };
    }
    
    // 使用例：取引戦略にリスク管理を適用
    public void demonstrateHigherOrderFunction() {
        Function<MarketData, TradingDecision> baseStrategy = 
            data -> new TradingDecision(data.getPrice() > 100 ? TradeType.BUY : TradeType.SELL);
            
        Function<MarketData, TradingDecision> safeStrategy = 
            withRiskManagement(
                baseStrategy,
                data -> data.getVolatility() < 0.5,  // リスクチェック
                TradingDecision.HOLD  // デフォルト値
            );
            
        // 実際の取引実行
        MarketData currentMarketData = new MarketData(120.0, 0.3);
        TradingDecision decision = safeStrategy.apply(currentMarketData);
        IO.println("安全な取引決定: " + decision);
        
        // 高リスクの場合
        MarketData highRiskData = new MarketData(90.0, 0.8);
        TradingDecision highRiskDecision = safeStrategy.apply(highRiskData);
        IO.println("高リスク時の決定: " + highRiskDecision);
    }
    
    public static void main(String[] args) {
        HigherOrderFunctionExample example = new HigherOrderFunctionExample();
        example.demonstrateHigherOrderFunction();
    }
}
```

**実行結果:**

```
安全な取引決定: TradingDecision{BUY}
高リスク時の決定: TradingDecision{HOLD}
```

### 関数型プログラミングのベストプラクティス

アンチパターン1: 過度なラムダネスト。

<span class="listing-number">**サンプルコード11-14**</span>

```java
// 悪い例：読みづらいネストされたラムダ
result = list.stream()
    .map(x -> {
        return transform(x, y -> {
            return process(y, z -> {
                return calculate(z);
            });
        });
    })
    .collect(Collectors.toList());

// 良い例：メソッド参照とヘルパーメソッドを使用
result = list.stream()
    .map(this::transformItem)
    .collect(Collectors.toList());

private Item transformItem(Item x) {
    return transform(x, this::processItem);
}
```

<span class="footnote">※ このコードは設計上のアンチパターンを示す例のため、実行可能な完全なコードではありません。可読性の観点から、ラムダ式の過度なネストは避けるべきことを説明しています。</span>

アンチパターン2: 副作用の濫用。

<span class="listing-number">**サンプルコード11-15**</span>

```java
// 悪い例：ラムダ内で外部状態を変更
List<String> results = new ArrayList<>();
items.forEach(item -> {
    results.add(processItem(item));  // 副作用！
});

// 良い例：純粋な関数型アプローチ
List<String> processed = items.stream()
    .map(this::processItem)
    .collect(Collectors.toList());
```

<span class="footnote">※ このコードは副作用のアンチパターンを示す例です。実際のprocessItemメソッドの実装は省略されています。関数型プログラミングでは副作用を避け、純粋関数を使用することが推奨されます。</span>

### 実世界での応用例：リアクティブストリーム処理

動画配信サービスのNetflixは、大量のAPIリクエストを処理するために、関数型プログラミングとリアクティブストリームを活用してきました。
ここでは、その考え方を、動画のおすすめを計算するサービスを題材に示します。

<span class="listing-number">**サンプルコード11-16**</span>

```java
// 従来の同期的アプローチ（スケールしない）
public class VideoRecommendationService {
    public List<Video> getRecommendations(String userId) {
        User user = userService.getUser(userId);  // ブロッキング
        List<Video> watched = historyService.getWatchHistory(userId);
                                                        // ブロッキング
        List<Video> trending = trendingService.getTrending();  // ブロッキング
        
        return recommendationEngine.calculate(user, watched, trending);
    }
}
```

<span class="listing-number">**サンプルコード11-17**</span>

```java
// リアクティブ・関数型アプローチ（高スケーラビリティ）
public class ReactiveVideoRecommendationService {
    public Mono<List<Video>> getRecommendations(String userId) {
        return Mono.zip(
            userService.getUserAsync(userId),
            historyService.getWatchHistoryAsync(userId),
            trendingService.getTrendingAsync()
        )
        .map(tuple -> {
            User user = tuple.getT1();
            List<Video> watched = tuple.getT2();
            List<Video> trending = tuple.getT3();
            
            return recommendationEngine.calculate(user, watched, trending);
        })
        .timeout(Duration.ofMillis(100))
        .onErrorReturn(Collections.emptyList());
    }
}
```

<span class="footnote">※ このコードは高度なリアクティブプログラミングの概念例です。実際の動作にはReactor（Mono, Flux）やSpring WebFluxなどの追加ライブラリが必要です。Netflix等の大規模システムでの関数型アプローチの応用例として理解してください。</span>

同期的なアプローチでは、3つのサービスの呼び出しを順に待つため、応答までの時間は3つの待ち時間の合計になります。
リアクティブなアプローチでは3つを同時に呼び出すので、待ち時間はもっとも遅い1つの分で済みます。
また、応答を待つ間にスレッドを占有しないため、同じ数のスレッドでより多くのリクエストを処理できます。

### 関数型プログラミングの実装パターン集

1. リトライとサーキットブレーカー

<span class="listing-number">**サンプルコード11-18**　ResilientService.java（3〜18、30行目）</span>

```java
public class ResilientService {
    // 関数型リトライメカニズム
    public <T> Supplier<T> withRetry(Supplier<T> supplier, int maxAttempts) {
        return () -> {
            for (int attempt = 1; ; attempt++) {
                try {
                    return supplier.get();  // 成功したら結果を返す
                } catch (RuntimeException e) {
                    if (attempt >= maxAttempts) {
                        throw new RuntimeException("Max attempts reached", e);
                    }
                    // 失敗したら、もう一度呼び出す
                }
            }
        };
    }
    // ...
}
```

#### イベント処理システムでの関数型アプローチ

イベント駆動アーキテクチャにおいて、関数型プログラミングはとくに有効です。イベントハンドラーをラムダ式として定義し、関数の組み合わせでイベント処理パイプラインを構築することで、保守性と拡張性の高いシステムを実現できます。

<span class="listing-number">**サンプルコード11-19**</span>

```java
public class EventProcessor {
    private final Map<Class<?>, List<Consumer<Object>>> handlers = new ConcurrentHashMap<>();
    
    // イベントハンドラの登録
    public <T> void on(Class<T> eventType, Consumer<T> handler) {
        handlers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                .add((Consumer<Object>) handler);
    }
    
    // 関数型フィルタとトランスフォーマー
    public <T> void onFiltered(Class<T> eventType, 
                              Predicate<T> filter,
                              Consumer<T> handler) {
        on(eventType, event -> {
            if (filter.test(event)) {
                handler.accept(event);
            }
        });
    }
    
    // 使用例
    public void setupEventHandlers() {
        // 単純なハンドラ
        on(OrderCreated.class, order -> logger.info("Order created: {}", order.getId()));
        
        // フィルタ付きハンドラ
        onFiltered(OrderCreated.class,
            order -> order.getAmount() > 1000,
            order -> notificationService.sendHighValueOrderAlert(order));
    }
}
```

2. 関数合成によるミドルウェアパターン

<span class="listing-number">**サンプルコード11-20**　MiddlewareChain.java（3、9〜32、48〜52、61行目）</span>

```java
public class MiddlewareChain {
    // ...
    // HTTPリクエスト処理のミドルウェア
    @FunctionalInterface
    interface Middleware extends Function<Request, Response> {
        default Middleware then(Middleware next) {
            return request -> {
                Response response = this.apply(request);
                if (response.isSuccess()) {
                    return next.apply(request);
                }
                return response;
            };
        }
    }
    
    // 認証ミドルウェア
    Middleware authenticate = request -> {
        String token = request.getHeader("Authorization");
        if (tokenService.isValid(token)) {
            request.setAttribute("user", tokenService.getUser(token));
            return Response.success();
        }
        return Response.unauthorized();
    };
    
    // ...
    // ミドルウェアチェーンの構築
    Middleware pipeline = logging
        .then(rateLimiting)
        .then(authenticate)
        .then(request -> businessLogic.handle(request));
        // ...
}
```

`Middleware`は`Function`を継承しているため、`andThen`という名前のメソッドをすでに持っています。
同じ名前で合成メソッドを定義すると、ラムダ式を渡したときにどちらを呼ぶのかが決まらずコンパイルエラーになるので、ここでは`then`という名前にしています。

#### テンプレートメソッドパターンの関数型実装

テンプレートメソッドパターンでは、アルゴリズムの骨格を定義し、具体的な処理ステップをサブクラスに委ねます。関数型アプローチでは、これを継承ではなく関数の組み合わせとして実現でき、より柔軟で再利用しやすい設計が可能になります。

<span class="listing-number">**サンプルコード11-21**</span>

```java
// データ処理の基本的なテンプレート
public class DataProcessingTemplate {
    public <T, R> R processData(
        T input,
        Function<T, T> preprocessor,
        Function<T, R> processor,
        Function<R, R> postprocessor) {
        
        T preprocessed = preprocessor.apply(input);
        R processed = processor.apply(preprocessed);
        return postprocessor.apply(processed);
    }
    
    // 使用例：CSVデータの処理
    public List<Customer> processCsvData(String csvData) {
        return processData(
            csvData,
            data -> data.trim().toLowerCase(),  // 前処理
            this::parseCustomers,              // メイン処理
            customers -> {                      // 後処理：無効なデータを除く
                customers.removeIf(c -> !c.isValid());
                return customers;
            }
        );
    }
}
```

3. イベントソーシングとCQRS

<span class="listing-number">**サンプルコード11-22**　EventSourcedAccount.java（3〜36行目）</span>

```java
public class EventSourcedAccount {
    // イベントの定義
    sealed interface AccountEvent permits 
        AccountCreated, MoneyDeposited, MoneyWithdrawn {}
    

    record AccountCreated(String accountId, String owner) implements AccountEvent {}
    record MoneyDeposited(String accountId, Money amount) implements AccountEvent {}
    record MoneyWithdrawn(String accountId, Money amount) implements AccountEvent {}
    
    // イベントを順に適用して、現在の状態を求める
    public class AccountProjection {
        public AccountState project(List<AccountEvent> events) {
            AccountState state = AccountState.empty();
            for (AccountEvent event : events) {
                state = applyEvent(state, event);
            }
            return state;
        }
        
        private AccountState applyEvent(AccountState state, AccountEvent event) {
            return switch (event) {
                case AccountCreated(var id, var owner) -> 
                    new AccountState(id, owner, Money.ZERO);
                    
                case MoneyDeposited(var id, var amount) -> 
                    state.withBalance(state.balance().add(amount));
                    
                case MoneyWithdrawn(var id, var amount) -> 
                    state.withBalance(state.balance().subtract(amount));
            };
        }
    }
}
```

<span class="footnote">※ このコードはJava 21以降のsealed interface、record、switch式のパターンマッチング（レコードパターン）を使用した高度な例です。実際の動作にはAccountState、Moneyクラスの実装が必要です。イベントソーシングの設計パターンを関数型で表現した概念例として理解してください。</span>

`project`メソッドは、空の状態から始めてイベントを古い順に1つずつ適用し、現在の状態を求めます。
第12章で学ぶStreamの`reduce`を使うと、この繰り返しを1つの式で書けます。

#### ファクトリパターンの関数型実装

従来のファクトリパターンでは、オブジェクトの生成ロジックを専用のクラスにカプセル化していましたが、関数型アプローチでは、Supplier関数やFunction関数を使ってより柔軟なファクトリを実装できます。
これにより、実行時の条件に応じて異なる生成戦略を動的に選択することが可能になります。

<span class="listing-number">**サンプルコード11-23**</span>

```java
// 関数型ファクトリパターン
public class ProcessorFactory {
    private final Map<String, Supplier<DataProcessor>> processors = Map.of(
        "csv", CsvProcessor::new,
        "xml", XmlProcessor::new,
        "json", JsonProcessor::new
    );
    
    public DataProcessor createProcessor(String type) {
        Supplier<DataProcessor> supplier = processors.get(type);
        if (supplier == null) {
            throw new IllegalArgumentException("未対応の形式: " + type);
        }
        return supplier.get();  // ここではじめてオブジェクトを生成する
    }
    
    // さらに高度な例：パラメータ化されたファクトリ
    public Function<ProcessorConfig, DataProcessor> createConfigurableFactory(
        String type) {
        return config -> createProcessor(type).configure(config);
    }
}
```

<span class="footnote">※ このコードは関数型ファクトリパターンの概念例です。実際の動作にはDataProcessor、CsvProcessor、XmlProcessor、JsonProcessor、ProcessorConfigクラスの実装が必要です。従来のファクトリパターンを関数型で簡潔に表現した設計例として理解してください。</span>

### メモリ効率とパフォーマンス最適化

#### ラムダ式の内部実装とメモリ使用

<span class="listing-number">**サンプルコード11-24**</span>

```java
public class LambdaPerformance {
    // ラムダ式のキャプチャによるメモリ影響
    public void demonstrateCapture() {
        // キャプチャなし - 静的にインスタンス化される
        Function<Integer, Integer> noCapture = x -> x * 2;
        
        // 実質的にfinal変数のキャプチャ
        int multiplier = 3;
        Function<Integer, Integer> withCapture = x -> x * multiplier;
        // 新しいインスタンスが生成される
        
        // パフォーマンステスト
        long start = System.nanoTime();
        IntStream.range(0, 1_000_000)
            .map(x -> x * 2)  // インライン化される
            .sum();
        long inlineTime = System.nanoTime() - start;
        
        start = System.nanoTime();
        IntStream.range(0, 1_000_000)
            .map(noCapture::apply)  // メソッド参照
            .sum();
        long methodRefTime = System.nanoTime() - start;
        
        IO.println("Inline: %dms, Method ref: %dms".formatted(
            inlineTime / 1_000_000, 
            methodRefTime / 1_000_000));
    }
}
```


#### デザインパターンでの関数型アプローチ活用

関数型プログラミングは、従来のデザインパターンをより簡潔かつ柔軟に実装する手段を提供します。
とくに、戦略パターン、コマンドパターン、テンプレートメソッドパターンなどでは、ラムダ式を使うことでクラス定義を省略して直接振る舞いを定義できます。
以下では、実際のビジネスロジックでよく使われるパターンを関数型アプローチで実装する方法を紹介します。

### 実践的なデザインパターン：関数型ビルダー

<span class="listing-number">**サンプルコード11-25**　FunctionalBuilder.java（5、26〜43、45〜56行目）</span>

```java
public class FunctionalBuilder {
    // ...
    // 関数型ビルダーパターン
    public static class FunctionalPersonBuilder {
        private final List<Consumer<Person>> operations = new ArrayList<>();
        
        public FunctionalPersonBuilder with(Consumer<Person> operation) {
            operations.add(operation);
            return this;
        }
        
        public Person build() {
            Person person = new Person();
            operations.forEach(op -> op.accept(person));
            return person;
        }
    }
    
    // 使用例
    public void demonstrateBuilders() {
        // ...
        // 関数型ビルダーの利点：動的な構築ロジック
        Person person = new FunctionalPersonBuilder()
            .with(p -> p.setName("Alice"))
            .with(p -> p.setAge(30))
            .with(p -> {
                if (p.getAge() >= 18) {
                    p.grantAdultPrivileges();
                }
            })
            .build();
    }
}
```

設定ビルダーパターンの関数型実装。

設定やコンフィギュレーションを構築する際、関数型ビルダーパターンはとくに威力を発揮します。条件分岐を含む複雑な設定ロジックを、ラムダ式を使って直感的に表現できます。

<span class="listing-number">**サンプルコード11-26**</span>

```java
public class ConfigurationBuilder {
    public static class DatabaseConfig {
        private String host;
        private int port;
        private String database;
        private boolean sslEnabled;
        
        // 設定メソッド（省略）
    }
    
    public DatabaseConfig buildDatabaseConfig(String environment) {
        return new FunctionalConfigBuilder<DatabaseConfig>()
            .with(config -> config.setHost("localhost"))  // デフォルト設定
            .with(config -> config.setPort(5432))
            .whenCondition("production".equals(environment),
                config -> {
                    config.setHost("prod-db.company.com");
                    config.setSslEnabled(true);
                })
            .whenCondition("test".equals(environment),
                config -> config.setDatabase("test_db"))
            .build(DatabaseConfig::new);
    }
}
```

### 産業界での採用事例：金融取引システム

高頻度取引（HFT）システムを題材に、関数型アプローチの例を示します。

<span class="listing-number">**サンプルコード11-27**</span>

```java
public class TradingSystem {
    // マーケットデータのストリーム処理
    public class MarketDataProcessor {
        // 価格変動の分析
        public Flux<TradingSignal> analyzeMarketData(Flux<MarketTick> ticks) {
            return ticks
                .window(Duration.ofSeconds(1))
                .flatMap(window -> window
                    .collect(Collectors.toList())
                    .map(this::calculateVolatility)
                )
                .filter(volatility -> volatility > THRESHOLD)
                .map(this::generateTradingSignal)
                .onBackpressureBuffer(1000)
                .publishOn(Schedulers.parallel());
        }
        
        // 複雑な取引戦略の組み合わせ
        public Function<MarketData, TradingDecision> combineStrategies(
            List<TradingStrategy> strategies) {
            
            return marketData -> strategies.stream()
                .map(strategy -> strategy.evaluate(marketData))
                .reduce(TradingDecision.NEUTRAL, 
                    TradingDecision::combine);
        }
    }
}
```

<span class="footnote">※ このコードは外部ライブラリReactorの`Flux`を使った概念例です。動かすには、ライブラリと、MarketTick、TradingSignalなどのクラスの実装が必要です。</span>

#### 関数型アプローチによるビジネスロジック設計

複雑なビジネスロジックを関数型で設計することで、テストしやすく、再利用可能で、理解しやすいコードを作成できます。各機能を純粋関数として実装し、関数の組み合わせで複雑な処理を表現します。

<span class="listing-number">**サンプルコード11-28**</span>

```java
public class BusinessLogicProcessor {
    // 純粋関数による価格計算
    public Function<Order, BigDecimal> calculatePrice = order -> {
        BigDecimal basePrice = order.getBasePrice();
        BigDecimal discount = applyDiscount(order);
        BigDecimal tax = calculateTax(basePrice.subtract(discount));
        return basePrice.subtract(discount).add(tax);
    };
    
    // 処理の各段階を関数として用意する
    private final Function<Order, Order> validate = this::validateOrder;  // 不正な注文なら例外をスローする
    private final Function<Order, Order> applyRules = this::applyBusinessRules;
    private final Function<Order, Order> finalPrice = this::calculateFinalPrice;

    // 関数合成による複雑な処理（validate → applyRules → finalPrice → generateProcessedOrder の順に適用）
    public Function<Order, ProcessedOrder> processOrder =
        validate.andThen(applyRules)
                .andThen(finalPrice)
                .andThen(this::generateProcessedOrder);
}
```

`andThen`は`Function`のdefaultメソッドで、2つの関数をつなぎ、前の関数の結果を次の関数に渡す新しい関数を作ります。
`processOrder.apply(order)`と呼ぶと、4つの処理が順に適用されます。

## よくあるエラーと対処法

ラムダ式は短く書ける反面、型が省略されているため、エラーメッセージがわかりにくくなることがあります。本章の内容に関連して出会いやすいエラーは次のとおりです。

- ラムダ式の構文エラー
    + 複数の文を書くときの波括弧と`return`の書き忘れ、引数の括弧の誤りなど
- 型推論の失敗
    + ラムダ式を代入する先の関数型インターフェイスが決まらないと、型を推論できない
- チェック例外の扱い
    + `Function`などの標準の関数型インターフェイスは、チェック例外をスローできない
- 変数のキャプチャ
    + ラムダ式のなかから参照できるローカル変数は、実質的にfinalなものに限られる
- 関数型インターフェイスの実装の誤り
    + 抽象メソッドが2つ以上あるインターフェイスには、ラムダ式を代入できない
- メソッド参照の型の不一致
    + 参照するメソッドの引数や戻り値が、関数型インターフェイスの抽象メソッドと合わない

エラーの例とエラーメッセージ、修正したコード（コンパイルできる全文を含む）、共通の対処戦略は、GitHubリポジトリにまとめています。<br>
`https://github.com/Nagatani/techbook-java-primer/blob/main/appendix/h-common-errors/chapters/chapter11.md`

## まとめ

本章では、Java 8で導入されたラムダ式と関数型インターフェイスについて学びました。

- ラムダ式は、匿名関数を簡潔に記述するための構文で、冗長な匿名クラスを置き換える
- ラムダ式は、抽象メソッドが1つだけの関数型インターフェイスとして扱われる
- `Predicate`, `Function`, `Consumer`, `Supplier`など、汎用的な関数型インターフェイスが標準で用意されている
- メソッド参照を使うと、既存のメソッドを呼びだすだけのラムダ式をさらに簡潔に書ける

これらの機能を使いこなすと、より宣言的で簡潔なコードを書けます。
ただし、ラムダ式を入れ子にしすぎると読みにくくなるため、可読性を優先して使い分けることが大切です。

## 章末演習

### 演習課題へのアクセス
本章の演習課題は、GitHubリポジトリで提供されています。<br>
`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter11/`

### 課題構成
- 本章の基本概念の理解確認
- 応用的な実装練習
- 実践的な総合問題

1. 基礎課題
    + 標準関数型インターフェイスの活用とラムダ式の基本構文
2. 発展課題
    + カスタム関数型インターフェイスの設計とメソッド参照
3. チャレンジ課題
    + 高階関数による関数型プログラミング

詳細な課題内容と実装のヒントは、GitHubリポジトリの各課題フォルダー内のREADME.mdを参照してください。

次のステップ： 基礎課題が完了したら、第12章「Stream APIと高度なコレクション操作」に進みましょう。

※ 本章の高度な内容については、付録B.02「プログラミングパラダイム」を参照してください。
（`https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b02-programming-paradigms/`）
