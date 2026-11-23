# <b>11章</b> <span>ラムダ式と関数型インターフェイス</span> <small>関数型プログラミングへの誘い</small>

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

Java 8<span class="footnote">Java 8（Java SE 8）は2014年3月に公開されました。ラムダ式はJSR 335として仕様がまとめられ、OpenJDKのProject Lambdaで開発されました。</span>でラムダ式が導入される前、その場限りのインターフェイス実装を提供するためには匿名クラス（Anonymous Class）が使われていました。第8章で学んだとおり、匿名クラスは名前を持たないクラスで、とくにGUIのイベントリスナーなどで多用されていました。

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

実行結果：

```
Hello from anonymous class!
Hello from lambda!
```

ラムダ式<span class="footnote">「ラムダ」は、1930年代に数学者アロンゾ・チャーチが考えたラムダ計算（λ計算）に由来します。1958年に登場したLispも、名前のない関数を書くキーワードに`lambda`を使っていました。</span>は、匿名クラスの冗長な記述を簡潔に表現できます。`() ->` が「引数なしで何かを実行する」という意味になります。

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

このコード<span class="footnote">GUIアプリケーションで使う例で、`button`はあらかじめ`JButton`として作ってあるものとします。ボタンなどのGUIの部品は第17章で学びます。</span>では、`actionPerformed`という1つのメソッドを実装するためだけに、`new ActionListener() { ... }`という定型的な記述を書かなければならず、冗長でした。

ラムダ式は、この匿名クラスの記述を、本質的な処理だけを抜き出して劇的に簡潔にするために導入されました。
サンプルコード11-2をラムダ式で書くと、次のように1行になります<span class="footnote">サンプルコード11-2と同じく、`button`はあらかじめ`JButton`として作ってあるものとします。</span>。

<span class="listing-number">**サンプルコード11-3**</span>

```java
// ラムダ式を使った場合
button.addActionListener(e -> IO.println("ボタンがクリックされました！"));
```

## 関数型インターフェイス

ラムダ式は、どのような場所でも書けるわけではありません。
ラムダ式を書けるのは、変数への代入やメソッドの引数のように、関数型インターフェイス（Functional Interface）の型が求められる場所だけです。
ラムダ式を評価すると、そのインターフェイスを実装したオブジェクトができます。

関数型インターフェイスとは、実装すべき抽象メソッドが1つだけ定義されているインターフェイスのことです<span class="footnote">defaultメソッドとstaticメソッドは数えません。`equals`のように`Object`クラスのpublicメソッドと同じ形の抽象メソッドも数えないので、`equals`を宣言している`Comparator`も関数型インターフェイスです。</span>。`@FunctionalInterface` アノテーションを付けると、コンパイラーが抽象メソッドが1つだけかどうかをチェックしてくれるため、付けるとよいでしょう。

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

実行結果：

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
- 引数がないときは、`() -> IO.println("Hello")` のように空の括弧`()`を書く（省略できない）

### ラムダ式から参照できる変数と副作用

ラムダ式の本体からは、ラムダ式の外で宣言したローカル変数を参照できます。
ただし、参照できるのは、`final`を付けた変数か、実質的にfinalな変数に限られます<span class="footnote">Java 7までは匿名クラスから参照するローカル変数に`final`が必須でした。`final`のない実質的にfinalな変数を参照できるようになったのは、Java 8からです。</span>。
実質的にfinalな変数とは、`final`を付けていなくても、初期化したあとで一度も代入し直さない変数のことです。

たとえば、`int count = 0;`と宣言した変数を、ラムダ式のなかで`count++`と書いて増やそうとすると、コンパイルエラーになります。
ラムダ式の外で、あとから`count = 1;`のように代入し直した場合も、ラムダ式で`count`を参照した箇所がエラーになります。
javacは「ラムダ式から参照されるローカル変数は、finalまたは事実上のfinalである必要があります」と表示します。
英語の環境では「local variables referenced from a lambda expression must be final or effectively final」です。

この制限は変数への代入を禁じるもので、変数が指すオブジェクトの中身を変えることは禁じていません。
そのため、ラムダ式のなかから外のリストに要素を追加する書き方は、コンパイルできます。
しかし、ラムダ式の外の状態を書き換えない（副作用を持たない）書き方には、次のような利点があります。

- 同じ引数に対して同じ結果を返すので、ラムダ式だけを取り出して確かめやすい
- 呼び出す順序や回数で結果が変わらないので、処理を組み替えたり、関数を合成したりしやすい
- 複数のスレッドから同時に呼び出しても、共有している状態を壊さない（第15章）

ラムダ式で計算した結果は、外の変数に書き込まず、戻り値として返すようにしましょう。
書き換えの例は、章の後半の「ラムダ式から外部の状態を変えない」（サンプルコード11-17）で示します。

### `Comparator`による並べ替え

`Comparator<T>`は、2つのオブジェクトを比べる`compare`メソッドを実装する関数型インターフェイスです。
そのため、リストを並べ替える`sort`メソッドに渡す比較のルールを、匿名クラスの代わりにラムダ式で書けます<span class="footnote">C言語の`qsort`関数では、比較のルールを関数ポインターで渡しました。Javaには関数ポインターがないので、関数型インターフェイスを実装したオブジェクトとして渡します。</span>。
`String`や`Integer`のように`Comparable`インターフェイスを実装したクラスは、自然な順序（文字列なら辞書順）を持ちます。
`Comparator`を使うと、自然な順序とは別の並べ方を、クラスを書き換えずに外から指定できます。
次のサンプルでは、学生を名前の順に並べる同じ処理を、匿名クラス、ラムダ式、`Comparator.comparing`の3通りで書いています。
`Student`は名前・学年・学生番号を持つクラスで、サンプルコード11-6でも使います。
本文では定義とimport文を省いています<span class="footnote">`Student`の定義を含む全文は、`https://github.com/Nagatani/techbook-java-primer/tree/main/sample-code/chapter11/11-05/`にあります。</span>。

<span class="listing-number">**サンプルコード11-5**　ComparatorLambdaExample.java（7〜37行目）</span>

```java
public class ComparatorLambdaExample {
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
            new Student("Charlie", 2, "S002"),
            new Student("Alice", 2, "S003"),
            new Student("Bob", 1, "S001")
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

実行結果：

```
ソート前: [Charlie(2年, S002), Alice(2年, S003), Bob(1年, S001)]
従来の方法: [Alice(2年, S003), Bob(1年, S001), Charlie(2年, S002)]
ラムダ式: [Alice(2年, S003), Bob(1年, S001), Charlie(2年, S002)]
メソッド参照: [Alice(2年, S003), Bob(1年, S001), Charlie(2年, S002)]
```

`Comparator.comparing`は、比較に使うキー（ここでは名前）を取り出す関数を受け取り、そのキーで比べる`Comparator`を作ります。
`Student::getName`は`s -> s.getName()`と同じ意味のメソッド参照で、本章の「メソッド参照」で詳しく学びます。

実際の業務では、1つの条件だけでなく、複数の条件を組み合わせて並べ替えることがよくあります。
`thenComparing`メソッドを使うと、前の条件で順序が決まらなかった要素どうしを次の条件で比べる`Comparator`を作れます。

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
        
        // 学年で並べ替え、同じ学年の場合は名前で並べ替え、
        // さらに同名の場合は学生番号で並べ替える
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

実行結果：

```
学生ソート後（学年→名前→学生番号）:
Alice(1年, S004)
Bob(1年, S001)
Alice(2年, S003)
Charlie(2年, S002)

降順ソート（学年降順→名前昇順）:
Alice(2年, S003)
Charlie(2年, S002)
Alice(1年, S004)
Bob(1年, S001)

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

Javaには、`java.util.function`パッケージ<span class="footnote">このパッケージはJava 8で追加されました。`IntPredicate`のような基本データ型用のものを含めて、43個のインターフェイスがあります。</span>に、よく使われる汎用的な関数型インターフェイスが多数用意されています。これらを活用することで、自分でインターフェイスを定義する手間を省けます。

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
        // 11
        IO.println("'Hello World'の長さ: "
                + getLength.apply("Hello World"));

        // Consumer: 文字列を大文字で出力
        Consumer<String> printUpper = s -> IO.println(s.toUpperCase());
        IO.print("'hello'を大文字で出力: ");
        printUpper.accept("hello"); // HELLO

        // Supplier: 既定の値を供給
        Supplier<String> defaultName = () -> "名無し";
        IO.println("既定の名前: " + defaultName.get());
        
        // UnaryOperator: 同じ型の変換
        UnaryOperator<String> addPrefix = s -> "Hello, " + s;
        IO.println("挨拶付き: " + addPrefix.apply("Java"));
        
        // BinaryOperator: 同じ型の2つの値から1つの値を生成
        BinaryOperator<Integer> multiply = (a, b) -> a * b;
        IO.println("5 × 7 = " + multiply.apply(5, 7));
    }
}
```

実行結果：

```
''は空？: true
'Java'は空？: false
'Java'の長さ: 4
'Hello World'の長さ: 11
'hello'を大文字で出力: HELLO
既定の名前: 名無し
挨拶付き: Hello, Java
5 × 7 = 35
```

次の図は、表のうち`BinaryOperator`を除く5つのインターフェイスについて、抽象メソッドと主なdefaultメソッド、インターフェイスどうしの継承の関係をまとめたものです。
`UnaryOperator`は、引数と戻り値の型をそろえた`Function`として、`Function`を継承しています。
`compose`・`andThen`・`and`などのdefaultメソッドは、このあとの「関数の合成」で使います。

<div class="figure">

![java.util.functionの基本的な関数型インターフェイス](images/diagrams/chapter11-functional-interface-basic.svg)

</div>

引数を2つ受け取るものには、名前の先頭に「2つの」を表す`Bi`が付いた`BiFunction`・`BiConsumer`・`BiPredicate`があります。
`BinaryOperator`は、2つの引数と戻り値の型をそろえた`BiFunction`です。
関数型インターフェイスは`java.util.function`パッケージの外にもあります。
これまでに使った`Runnable`（`java.lang`パッケージ）と`Comparator`（`java.util`パッケージ）、本章の後半で使う`Callable`（`java.util.concurrent`パッケージ）も関数型インターフェイスなので、ラムダ式で書けます。

<div class="figure">

![引数が2つの関数型インターフェイスと、ほかのパッケージの関数型インターフェイス](images/diagrams/chapter11-functional-interface-extended.svg)

</div>

### 関数の合成

`java.util.function`の関数型インターフェイスには、2つの関数を組み合わせて新しい関数を作るdefaultメソッドがあります。
このように関数を組み合わせることを、関数の合成と呼びます。

`Function`の`andThen`と`compose`は、どちらも2つの関数をつなぎますが、適用する順序が逆です。
`f.andThen(g)`は、先に`f`を適用し、その結果に`g`を適用する関数を作ります。
`f.compose(g)`は、先に`g`を適用し、その結果に`f`を適用する関数を作ります。

`Predicate`には、条件を組み合わせる`and`（かつ）、`or`（または）、`negate`（否定）があります。
`and`と`or`は、`&&`や`||`と同じく、1つ目の条件で結果が決まれば2つ目の条件を調べません。

<span class="listing-number">**サンプルコード11-8**　FunctionComposition.java</span>

```java
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class FunctionComposition {
    public static void main(String[] args) {
        Function<Integer, Integer> plusOne = x -> x + 1;
        Function<Integer, Integer> twice = x -> x * 2;

        // andThen: plusOneを先に、twiceをあとに適用する
        Function<Integer, Integer> plusOneThenTwice =
                plusOne.andThen(twice);
        // compose: twiceを先に、plusOneをあとに適用する
        Function<Integer, Integer> twiceThenPlusOne =
                plusOne.compose(twice);

        // (5 + 1) * 2
        IO.println("andThen: " + plusOneThenTwice.apply(5));
        // 5 * 2 + 1
        IO.println("compose: " + twiceThenPlusOne.apply(5));

        Predicate<String> isEmpty = s -> s.isEmpty();
        Predicate<String> isShort = s -> s.length() < 5;
        Predicate<String> startsWithJ = s -> s.startsWith("J");

        // 空でない
        Predicate<String> notEmpty = isEmpty.negate();
        // 短く、かつJで始まる
        Predicate<String> shortAndJ = isShort.and(startsWithJ);
        // 空、またはJで始まる
        Predicate<String> emptyOrJ = isEmpty.or(startsWithJ);

        for (String word : List.of("Java", "", "JavaScript", "Go")) {
            IO.println("'%s': negate=%b, and=%b, or=%b".formatted(
                word, notEmpty.test(word), shortAndJ.test(word),
                emptyOrJ.test(word)));
        }
    }
}
```

実行結果：

```
andThen: 12
compose: 11
'Java': negate=true, and=true, or=true
'': negate=false, and=false, or=true
'JavaScript': negate=true, and=false, or=true
'Go': negate=true, and=false, or=false
```

同じ2つの関数でも、`andThen`では(5 + 1) × 2 = 12、`compose`では5 × 2 + 1 = 11になり、合成する順序で結果が変わります。
組み合わせて作った`Predicate`も、もとの`Predicate`と同じように`test`メソッドで使えます。
第12章で学ぶStream APIの`filter`メソッドにも、そのまま渡せます。

## メソッド参照

ラムダ式が既存のメソッドを呼び出すだけの場合、メソッド参照（Method Reference）という、さらに簡潔な記法が使えます。`クラス名::メソッド名`や`変数名::メソッド名`（変数はオブジェクトを指すもの）のように記述します。

| 種類 | 構文 | ラムダ式の例 |
| :--- | :--- | :--- |
| 静的メソッド参照 | `クラス名::静的メソッド名` | `s -> Integer.parseInt(s)` |
| インスタンスメソッド参照<br>（特定のインスタンス） | `変数名::メソッド名` | `s -> prefix.concat(s)` |
| インスタンスメソッド参照<br>（不特定のインスタンス） | `クラス名::メソッド名` | `s -> s.toUpperCase()` |
| コンストラクター参照 | `クラス名::new` | `() -> new ArrayList<>()` |

本書でよく使う`IO::println`は、`IO`クラスの静的メソッドへの参照なので、静的メソッド参照にあたります。ほかの資料でよく見かける`System.out::println`は、`System.out`というオブジェクト（特定のインスタンス）のメソッドへの参照です。

次のサンプルでは、ラムダ式を受け取る`List`のメソッドを2つ使います。
`forEach`は各要素を順に`Consumer`へ渡し、`replaceAll`は各要素を`UnaryOperator`の結果で置き換えます。

<span class="listing-number">**サンプルコード11-9**　MethodReferenceExample.java</span>

```java
import java.util.*;
import java.util.function.*;

public class MethodReferenceExample {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("apple", "banana", "cherry");
        
        IO.println("元のリスト: " + words);

        // 静的メソッド参照の例
        // s -> Integer.parseInt(s)
        Function<String, Integer> parser = Integer::parseInt;
        int number = parser.apply("123");
        IO.println("文字列から整数への変換: " + number + " + 1 = "
                + (number + 1));

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

        // コンストラクター参照
        // () -> new ArrayList<>()
        Supplier<List<String>> listFactory = ArrayList::new;
        List<String> newList = listFactory.get();
        newList.add("Hello");
        newList.add("World");
        IO.println("\n新しいリスト: " + newList);
    }
}
```

実行結果：

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

<span class="listing-number">**サンプルコード11-10**　LambdaVsMethodReference.java</span>

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

        // 引数を受け取るコンストラクターの参照
        // name -> new Person(name) と同じ
        Function<String, Person> toPerson = Person::new;
        List<Person> people = new ArrayList<>();
        for (String name : names) {
            people.add(toPerson.apply(name));
        }
        
        IO.println("\nPersonオブジェクトのリスト:");
        people.forEach(IO::println);
    }
}
```

実行結果：

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

サンプルコード11-9の`ArrayList::new`は引数のないコンストラクターの参照でしたが、引数を受け取るコンストラクターも参照できます。
`Person::new`を`Function<String, Person>`に代入すると、文字列を1つ受け取る`Person(String)`のコンストラクターを指します。

## ラムダ式の応用例

ラムダ式はコレクション操作だけでなく、Javaプログラムのさまざまな場面でコードを簡潔にします。

### スレッドの生成と実行

`Runnable`インターフェイス（`run`メソッドを持つ関数型インターフェイス）もラムダ式で簡単に実装できます。
スレッドの使い方は第15章で詳しく学びます。
ここでは、`new Thread(…)`に渡す`Runnable`をラムダ式で書けることに注目してください<span class="footnote">`start()`を呼ぶと、別のスレッドで`run`の処理を始めます。</span>。

<span class="listing-number">**サンプルコード11-11**　RunnableLambdaExample.java（1〜9、15〜22、28〜48行目）</span>

```java
public class RunnableLambdaExample {
    public static void main(String[] args) {
        IO.println("メインスレッド開始");
        
        // 匿名クラスでのRunnable
        Thread t1 = new Thread(new Runnable() {
            @Override
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
        Thread t3 = new Thread(() ->
                IO.println("Thread t3 (Simple Lambda) executed"));
        t3.start();
        
        // すべてのスレッドの完了を待機
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        IO.println("メインスレッド終了");
    }
}
```

実行結果：

```
メインスレッド開始
Thread t1 (Anonymous) is running...
Thread t2 (Lambda) is running...
Thread t3 (Simple Lambda) executed
Thread t2 (Lambda) finished
Thread t1 (Anonymous) finished
メインスレッド終了
```

`join()`は、そのスレッドの処理が終わるまで待つメソッドです。
3つのスレッドを待ってから表示するので、「メインスレッド終了」は最後に表示されます。
ただし、3つのスレッドのどれが先に動き始めるかは決まっていないので、最初の3行の順序は実行のたびに変わることがあります。

### スレッドプールへのタスクの受け渡し

並行処理でも、ラムダ式を使うと匿名クラスの定型的な記述を避け、タスクの処理内容だけを書けます。
`ExecutorService`は、用意したスレッドにタスクを割り振って実行するしくみで、第15章で学びます。
`Callable<String>`は値を返すタスクを表す関数型インターフェイスなので、タスクの中身をラムダ式で書けます。

<span class="listing-number">**サンプルコード11-12**　ExecutorLambdaExample.java（4〜18、23〜48行目）</span>

```java
public class ExecutorLambdaExample {
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
        // 順に実行すると合計3秒かかるタスクが、それより短い時間で終わったか
        IO.println("3秒未満で終わったか: " + (endTime - startTime < 3000));
    }
}
```

実行結果：

```
タスク実行開始...
実行結果:
Task 1 completed
Task 2 completed
Task 3 completed
3秒未満で終わったか: true
```

3つのタスクを順に実行すると、待ち時間の合計は3秒になります。
スレッドプールの4つのスレッドで同時に実行するので、もっとも長いタスクの1.5秒ほどで終わります。

### データバリデーション処理の関数型実装

複雑なバリデーションロジックは、関数型アプローチを使うことで、再利用可能で組み合わせ可能な小さな検証関数として実装できます。これにより、ビジネスルールの変更に柔軟に対応できるバリデーションシステムを構築できます。

<span class="listing-number">**サンプルコード11-13**　ValidationFramework.java（6〜26、40〜80行目）</span>

```java
public class ValidationFramework {
    // 基本的なバリデーション関数
    public static final Predicate<String> notEmpty = s -> s != null
            && !s.trim().isEmpty();
    public static final Predicate<String> validEmail = s -> s.matches(
            "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
    public static final Function<Integer, Predicate<String>> minLength =
            min -> s -> s.length() >= min;
    public static final Function<Integer, Predicate<String>> maxLength =
            max -> s -> s.length() <= max;
    
    // 複合バリデーション
    public static final Predicate<User> isValidUser = user ->
        notEmpty.test(user.getName()) &&
        validEmail.test(user.getEmail()) &&
        minLength.apply(8).test(user.getPassword());
    
    // エラーメッセージ付きバリデーション
    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        // ...
    }
    
    public static final Function<User,
            ValidationResult> validateUserWithErrors = user -> {
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
        User validUser =
                new User("太郎", "taro@example.com", "password123");
        User invalidUser = new User("", "invalid-email", "123");

        // 結果だけがわかればよいときは、isValidUserで判定する
        IO.println("isValidUser: " + isValidUser.test(validUser)
                + ", " + isValidUser.test(invalidUser));

        // 理由も知りたいときは、validateUserWithErrorsで検証する
        IO.println("\n有効なユーザーのバリデーション:");
        ValidationResult result1 =
                validateUserWithErrors.apply(validUser);
        IO.println(result1);
        
        IO.println("\n無効なユーザーのバリデーション:");
        ValidationResult result2 =
                validateUserWithErrors.apply(invalidUser);
        IO.println(result2);
    }
}
```

実行結果：

```
isValidUser: true, false

有効なユーザーのバリデーション:
有効

無効なユーザーのバリデーション:
無効: 名前は必須です, 有効なメールアドレスを入力してください, パスワードは8文字以上である必要があります
```

`minLength`は、最小の文字数を受け取り、その文字数以上かを判定する`Predicate`を返す関数です。
`minLength.apply(8)`のように数を変えるだけで、別の条件の検証関数を作れます。
`isValidUser`は3つの検証を`&&`でつないで結果だけを返し、`validateUserWithErrors`は満たさなかった条件をすべてエラーメッセージとして集めます。

## 関数型プログラミングの実践例

ここからは、ラムダ式と関数型インターフェイスを使った、実務に近い設計の例を紹介します。
本章の基本を学んだあとに読む発展的な内容で、多くは考え方を示すための断片的なコードです。
Stream API（第12章）を使う例もあるので、最初はラムダ式をどこに渡しているかに注目して読んでください。
たとえば`list.stream().map(…).collect(Collectors.toList())`は、各要素を`map`に渡したラムダ式で変換し、結果を新しいリストに集める書き方です。

### 高階関数による処理の抽象化

高階関数（Higher-Order Function）は、関数を引数として受け取ったり、関数を戻り値として返したりする関数です。これにより、共通的な処理パターンを抽象化できます。

<span class="listing-number">**サンプルコード11-14**　HigherOrderFunctionExample.java（4〜49行目）</span>

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
            data -> new TradingDecision(data.getPrice() > 100
            ? TradeType.BUY : TradeType.SELL);
            
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
        TradingDecision highRiskDecision =
                safeStrategy.apply(highRiskData);
        IO.println("高リスク時の決定: " + highRiskDecision);
    }
    
    public static void main(String[] args) {
        HigherOrderFunctionExample example =
                new HigherOrderFunctionExample();
        example.demonstrateHigherOrderFunction();
    }
}
```

実行結果：

```
安全な取引決定: TradingDecision{BUY}
高リスク時の決定: TradingDecision{HOLD}
```

`withRiskManagement`は、取引の戦略を表す関数とリスクを判定する条件を受け取り、条件を満たすときだけ戦略を適用する新しい関数を返します。
変動率が0.8の2つ目のデータは条件を満たさないので、戦略を適用せずに既定の値の`HOLD`を返します。

### 設計パターンを関数で書く

処理を受け取って別の処理を返す高階関数は、失敗した処理を呼び出し直す「再試行（リトライ）」にも使えます。
試したい処理をラムダ式で渡し、失敗したら何回か呼び出し直すメソッドは、例外の扱いとあわせて第13章「例外処理」の「リトライパターン」で書きます。

オブジェクト指向の設計パターンの一部も、振る舞いをラムダ式で渡すと、振る舞いごとのクラスを作らずに書けます。
イベントの型ごとのハンドラー、Webのミドルウェア、テンプレートメソッド、イベントソーシング、ファクトリ、ビルダーを関数で書いた例を用意しました。
それらの例は、付録B.02「プログラミングパラダイム」で紹介しています<span class="footnote">`https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b02-programming-paradigms/`</span>。

### 関数合成によるビジネスロジックの設計

業務の処理を、入力から結果を返すだけの小さな関数に分けておくと、関数ごとに確かめやすく、組み替えもしやすくなります。
サンプルコード11-15は、注文の検証、金額の計算、割引、消費税の加算、明細の文字列の作成を、それぞれ1つのメソッドにしています。

<span class="listing-number">**サンプルコード11-15**　OrderPipeline.java（3〜14、41〜48、57〜58行目）</span>

```java
public class OrderPipeline {
    // 注文：品名、単価、数量、金額
    record Order(String item, int unitPrice, int quantity, int total) {}

    // 不正な注文なら例外をスローする
    static Order validate(Order order) {
        if (order.quantity() <= 0) {
            throw new IllegalArgumentException("数量が不正です: "
                    + order.quantity());
        }
        return order;
    }
    // ...
    public static void main(String[] args) {
        // 各段階の関数をandThenで1つの関数にまとめる
        Function<Order, Order> validate = OrderPipeline::validate;
        Function<Order, String> processOrder = validate
            .andThen(OrderPipeline::calculateSubtotal)
            .andThen(OrderPipeline::applyDiscount)
            .andThen(OrderPipeline::addTax)
            .andThen(OrderPipeline::toReceipt);
            // ...
    }
}
```

`calculateSubtotal`・`applyDiscount`・`addTax`・`toReceipt`は、受け取った注文から新しい`Order`や文字列を返すメソッドです<span class="footnote">全文は`https://github.com/Nagatani/techbook-java-primer/tree/main/sample-code/chapter11/11-15/`にあります。</span>。
メソッド参照のままでは`andThen`を呼び出せないので、最初の`validate`だけを`Function<Order, Order>`型の変数に入れています。

実行結果：

```
ノート × 5 = 1100円
電卓 × 3 = 11880円
エラー: 数量が不正です: 0
```

本章の「関数の合成」で学んだ`andThen`で、5つの処理を1つの関数にまとめています。
`processOrder.apply(order)`と呼ぶと、5つの処理が順に適用されます。
電卓の注文は、金額12000円が10000円以上なので1割引きの10800円になり、消費税を加えて11880円になります。
数量が0の注文は、最初の`validate`で例外がスローされ、あとの処理は行われません。

### 実世界での応用例：リアクティブストリーム処理

動画配信サービスのNetflixは、大量のAPIリクエストを処理するために、関数型プログラミングとリアクティブストリームを活用してきました<span class="footnote">NetflixはMicrosoftのReactive Extensions（Rx）をJavaに移植したRxJavaを開発しました。2013年にオープンソースとして公開しています。</span>。
ここでは、その考え方を、動画のおすすめを計算するサービスを題材に説明します。
おすすめを計算するには、利用者の情報、視聴履歴、人気の動画の3つを、それぞれ別のサービスから取得します。

同期的なアプローチでは、3つのサービスの呼び出しを順に待つため、応答までの時間は3つの待ち時間の合計になります。
リアクティブなアプローチでは、3つを同時に呼び出し、結果がそろったときに行う計算をラムダ式で渡しておきます。
そのため、待ち時間はもっとも遅い1つの分で済みます。
また、応答を待つ間にスレッドを占有しないため、同じ数のスレッドでより多くのリクエストを処理できます。
Javaでこのような処理を書くときは、Reactorなどの外部ライブラリを使います<span class="footnote">Java 9で、リアクティブストリームの共通のインターフェイスが`java.util.concurrent.Flow`として標準ライブラリに入りました。ただし、処理を組み立てるメソッドは持たないので、実際にはライブラリと組み合わせて使います。</span>。

### 関数型プログラミングのベストプラクティス

ここまでの例のように、ラムダ式で処理を組み立てるときに気をつけたいことを2つ挙げます。

#### ラムダ式を深く入れ子にしない

ラムダ式の本体にさらにラムダ式を書くと、どの処理がどの値を扱っているのかを追いにくくなります。
サンプルコード11-16は、「名前:点数」を並べた行から行ごとの合計点を求める処理を、2通りに書いたものです。

<span class="listing-number">**サンプルコード11-16**　NestedLambdaExample.java（5〜47行目）</span>

```java
public class NestedLambdaExample {
    public static void main(String[] args) {
        // 1行に「名前:点数」を並べたデータから、行ごとの合計点を求める
        List<String> lines =
                List.of("Alice:80,Bob:65", "Carol:90,Dave:70");

        // 悪い例：ラムダ式の中にラムダ式を重ねていて、何をしているか読み取りにくい
        List<Integer> bad = lines.stream()
            .map(line -> {
                return Arrays.stream(line.split(","))
                    .map(entry -> {
                        String[] parts = entry.split(":");
                        return Integer.parseInt(parts[1]);
                    })
                    .reduce(0, (a, b) -> {
                        return a + b;
                    });
            })
            .collect(Collectors.toList());

        // 良い例：処理に名前を付けたメソッドに分け、メソッド参照で渡す
        List<Integer> good = lines.stream()
            .map(NestedLambdaExample::sumScores)
            .collect(Collectors.toList());

        IO.println("悪い例: " + bad);
        IO.println("良い例: " + good);
    }

    // 1行の合計点を求める
    private static int sumScores(String line) {
        int total = 0;
        for (String entry : line.split(",")) {
            total += scoreOf(entry);
        }
        return total;
    }

    // 「名前:点数」から点数を取り出す
    private static int scoreOf(String entry) {
        return Integer.parseInt(entry.split(":")[1]);
    }
}
```

実行結果：

```
悪い例: [145, 160]
良い例: [145, 160]
```

2つの書き方の結果は同じです。
悪い例では、行を区切る処理、点数を取り出す処理、足し合わせる処理が1つの式に入れ子になっています。
よい例では、それぞれを`sumScores`と`scoreOf`という名前のメソッドに分けたので、メソッド名から処理の内容がわかります。
メソッドに分けると、`scoreOf("Alice:80")`のように1つずつ呼び出して確かめることもできます。

#### ラムダ式から外部の状態を変えない

サンプルコード11-17は、文字列のリストを大文字に変換した結果を、2通りの方法で作ります。

<span class="listing-number">**サンプルコード11-17**　SideEffectExample.java（5〜27行目）</span>

```java
public class SideEffectExample {
    public static void main(String[] args) {
        List<String> items = List.of("apple", "banana", "cherry");

        // 悪い例：ラムダ式の中から、外のリストを書き換えている（副作用）
        List<String> results = new ArrayList<>();
        items.forEach(item -> {
            results.add(processItem(item));
        });

        // 良い例：変換した結果を、新しいリストとして受け取る
        List<String> processed = items.stream()
            .map(SideEffectExample::processItem)
            .collect(Collectors.toList());

        IO.println("悪い例: " + results);
        IO.println("良い例: " + processed);
    }

    private static String processItem(String item) {
        return item.toUpperCase();
    }
}
```

実行結果：

```
悪い例: [APPLE, BANANA, CHERRY]
良い例: [APPLE, BANANA, CHERRY]
```

この例では、どちらも同じ結果になります。
しかし悪い例のラムダ式は、外にある`results`に要素を追加するので、呼び出す順序や回数によって結果が変わります。
たとえば、同じラムダ式を第12章で学ぶ並列ストリームの`forEach`に渡すと、複数のスレッドから同時に呼び出され、`ArrayList`への追加がぶつかって要素が欠けることがあります。
よい例のラムダ式は、受け取った値から新しい値を返すだけなので、どの順序で呼び出しても同じ結果になります。

### ラムダ式の内部実装とメモリ使用

ラムダ式を評価すると、関数型インターフェイスを実装したオブジェクトができます<span class="footnote">匿名クラスはコンパイルすると`Outer$1.class`のような別のクラスファイルになりますが、ラムダ式はなりません。ラムダ式の本体は同じクラスのprivateメソッドになり、実行時に`invokedynamic`命令（Java 7で追加）を使ってオブジェクトを作ります。</span>。
サンプルコード11-18は、同じラムダ式を2回評価してできたオブジェクトが、同じインスタンスかどうかを`==`で比べます。

<span class="listing-number">**サンプルコード11-18**　LambdaCapture.java（5〜25行目）</span>

```java
public class LambdaCapture {
    public static void main(String[] args) {
        List<IntUnaryOperator> noCaptures = new ArrayList<>();
        List<IntUnaryOperator> captures = new ArrayList<>();

        // 同じラムダ式を2回評価して、できたオブジェクトを比べる
        for (int i = 1; i <= 2; i++) {
            // キャプチャなし：外側の変数を使わない
            noCaptures.add(x -> x * 2);

            // キャプチャあり：外側の変数multiplierを使う
            int multiplier = i;
            captures.add(x -> x * multiplier);
        }

        IO.println("キャプチャなし: 同じインスタンスか = "
            + (noCaptures.get(0) == noCaptures.get(1)));
        IO.println("キャプチャあり: 同じインスタンスか = "
            + (captures.get(0) == captures.get(1)));
    }
}
```

実行結果：

```
キャプチャなし: 同じインスタンスか = true
キャプチャあり: 同じインスタンスか = false
```

外側の変数をキャプチャしないラムダ式は、OpenJDKの実装では同じインスタンスを使い回します。
キャプチャするラムダ式は、評価するたびに、キャプチャした値を持つ新しいインスタンスを作ります。
ただし、インスタンスを使い回すかどうかは言語仕様で決められておらず、JVMの実装によって変わることがあります。
ラムダ式のオブジェクトを`==`で比べる処理を、プログラムの動作に使ってはいけません。
キャプチャによる性能の違いは小さいことが多いので、気になるときはJMH（Java Microbenchmark Harness）<span class="footnote">JMHはOpenJDKのプロジェクトとして開発されている、小さな処理の実行時間を測るためのツールです。JVMが実行中にコードを最適化する影響を考えて測定します。</span>などのツールで測ってから判断します。

## よくあるエラーと対処法

ラムダ式は短く書ける反面、型が省略されているため、エラーメッセージがわかりにくくなることがあります。本章の内容に関連して出会いやすいエラーは次のとおりです。

- ラムダ式の構文エラー
    + 複数の文を書くときの波括弧と`return`の書き忘れ、引数の括弧の誤りなど
- 型推論の失敗
    + ラムダ式を代入する先の関数型インターフェイスが決まらないと、型を推論できない
- 検査例外の扱い
    + `Function`などの標準の関数型インターフェイスは、検査例外（第13章）をスローできない
- 変数のキャプチャ
    + ラムダ式のなかから参照できるローカル変数は、finalか実質的にfinalなものに限られる
- 関数型インターフェイスの実装の誤り
    + 抽象メソッドが2つ以上あるインターフェイスには、ラムダ式を代入できない
- メソッド参照の型の不一致
    + 参照するメソッドの引数や戻り値が、関数型インターフェイスの抽象メソッドと合わない

エラーの例とエラーメッセージ、修正したコード（コンパイルできる全文を含む）、共通の対処戦略は、GitHubリポジトリにまとめています。<br>
`https://github.com/Nagatani/techbook-java-primer/blob/main/appendix/e-common-errors/chapters/chapter11.md`

## まとめ

本章では、Java 8で導入されたラムダ式と関数型インターフェイスについて学びました。

- ラムダ式は、匿名関数を簡潔に記述するための構文で、冗長な匿名クラスを置き換える
- ラムダ式は、抽象メソッドが1つだけの関数型インターフェイスの型が求められる場所に書け、そのインターフェイスを実装したオブジェクトになる
- `Predicate`・`Function`・`Consumer`・`Supplier`など、汎用的な関数型インターフェイスが標準で用意されている
- メソッド参照を使うと、既存のメソッドを呼び出すだけのラムダ式をさらに簡潔に書ける

これらの機能を使いこなすと、より宣言的で簡潔なコードを書けます。
ただし、ラムダ式を入れ子にしすぎると読みにくくなるため、可読性を優先して使い分けることが大切です。

本章の高度な内容については、付録B.02「プログラミングパラダイム」を参照してください<span class="footnote">`https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b02-programming-paradigms/`</span>。

## 章末演習

### 演習課題へのアクセス
本章の演習課題は、GitHubリポジトリで提供されています。<br>
`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter11/`

### 課題構成

1. 基礎課題
    + 標準関数型インターフェイスの活用とラムダ式の基本構文
2. 発展課題
    + カスタム関数型インターフェイスの設計、メソッド参照、Comparatorによる並べ替え
3. チャレンジ課題
    + 高階関数による関数型プログラミング

詳細な課題内容と実装のヒントは、GitHubリポジトリの各課題フォルダー内のREADME.mdを参照してください<span class="footnote">各課題フォルダーは`https://github.com/Nagatani/techbook-java-primer/tree/main/exercises/chapter11/`の下の`basic/`・`advanced/`・`challenge/`です。</span>。

基礎課題が完了したら、第12章「Stream APIと高度なコレクション操作」に進みましょう。
