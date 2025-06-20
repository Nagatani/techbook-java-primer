# 第7章 コレクションフレームワーク

この章では、Javaのコレクションフレームワークについて学習します。C言語の配列と比較しながら、より柔軟で強力なデータ構造の使い方を習得しましょう。

## 7.1 コレクションフレームワークとは

### C言語の配列との比較

```c
// C言語の配列（固定サイズ）
int numbers[10];  // サイズ固定
int count = 0;

void addNumber(int num) {
    if (count < 10) {
        numbers[count++] = num;
    }
}
```

```java
// Javaのコレクション（動的サイズ）
List<Integer> numbers = new ArrayList<>();

public void addNumber(int num) {
    numbers.add(num);  // サイズを気にする必要なし
}
```

## 7.2 List インタフェース

### ArrayList

```java
import java.util.*;

public class ArrayListExample {
    public static void main(String[] args) {
        // ArrayListの作成
        List<String> fruits = new ArrayList<>();
        
        // 要素の追加
        fruits.add("りんご");
        fruits.add("バナナ");
        fruits.add("オレンジ");
        
        // 特定位置に挿入
        fruits.add(1, "ぶどう");
        
        // 要素の取得
        String first = fruits.get(0);
        System.out.println("最初の果物: " + first);
        
        // 要素の変更
        fruits.set(2, "みかん");
        
        // 要素の削除
        fruits.remove("バナナ");
        fruits.remove(0);  // インデックスで削除
        
        // サイズの確認
        System.out.println("果物の数: " + fruits.size());
        
        // 全要素の表示
        for (String fruit : fruits) {
            System.out.println(fruit);
        }
    }
}
```

### LinkedList

```java
import java.util.*;

public class LinkedListExample {
    public static void main(String[] args) {
        LinkedList<Integer> numbers = new LinkedList<>();
        
        // 末尾に追加
        numbers.add(10);
        numbers.add(20);
        numbers.add(30);
        
        // 先頭に追加
        numbers.addFirst(5);
        
        // 末尾に追加
        numbers.addLast(40);
        
        // 先頭と末尾の取得
        System.out.println("先頭: " + numbers.getFirst());
        System.out.println("末尾: " + numbers.getLast());
        
        // 先頭と末尾の削除
        numbers.removeFirst();
        numbers.removeLast();
        
        System.out.println(numbers);  // [10, 20, 30]
    }
}
```

### ArrayList vs LinkedList

| 操作 | ArrayList | LinkedList |
|------|-----------|------------|
| インデックスアクセス | O(1) | O(n) |
| 先頭への挿入/削除 | O(n) | O(1) |
| 末尾への挿入/削除 | O(1) | O(1) |
| 中間への挿入/削除 | O(n) | O(n) |

## 7.3 Set インタフェース

### HashSet

```java
import java.util.*;

public class HashSetExample {
    public static void main(String[] args) {
        Set<String> uniqueWords = new HashSet<>();
        
        // 重複を自動的に排除
        uniqueWords.add("Java");
        uniqueWords.add("Python");
        uniqueWords.add("Java");    // 重複は追加されない
        uniqueWords.add("C++");
        
        System.out.println("ユニークな言語数: " + uniqueWords.size());  // 3
        
        // 存在確認
        if (uniqueWords.contains("Java")) {
            System.out.println("Javaが含まれています");
        }
        
        // 全要素の表示（順序は保証されない）
        for (String word : uniqueWords) {
            System.out.println(word);
        }
    }
}
```

### TreeSet

```java
import java.util.*;

public class TreeSetExample {
    public static void main(String[] args) {
        Set<Integer> sortedNumbers = new TreeSet<>();
        
        // 自動的にソートされる
        sortedNumbers.add(30);
        sortedNumbers.add(10);
        sortedNumbers.add(20);
        sortedNumbers.add(10);  // 重複は追加されない
        
        System.out.println(sortedNumbers);  // [10, 20, 30]
        
        // NavigableSetの機能
        TreeSet<Integer> treeSet = (TreeSet<Integer>) sortedNumbers;
        System.out.println("最小値: " + treeSet.first());
        System.out.println("最大値: " + treeSet.last());
        System.out.println("15より大きい最小値: " + treeSet.higher(15));
    }
}
```

## 7.4 Map インタフェース

### HashMap

```java
import java.util.*;

public class HashMapExample {
    public static void main(String[] args) {
        Map<String, Integer> scores = new HashMap<>();
        
        // キーと値のペアを追加
        scores.put("田中", 85);
        scores.put("佐藤", 92);
        scores.put("鈴木", 78);
        
        // 値の取得
        Integer tanakaScore = scores.get("田中");
        System.out.println("田中の点数: " + tanakaScore);
        
        // 存在しないキーの場合のデフォルト値
        Integer unknownScore = scores.getOrDefault("山田", 0);
        System.out.println("山田の点数: " + unknownScore);
        
        // キーの存在確認
        if (scores.containsKey("佐藤")) {
            System.out.println("佐藤のデータがあります");
        }
        
        // 全エントリの表示
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        
        // キーのみ、値のみの取得
        Set<String> names = scores.keySet();
        Collection<Integer> scoreValues = scores.values();
    }
}
```

### TreeMap

```java
import java.util.*;

public class TreeMapExample {
    public static void main(String[] args) {
        Map<String, String> dictionary = new TreeMap<>();
        
        dictionary.put("cat", "猫");
        dictionary.put("dog", "犬");
        dictionary.put("bird", "鳥");
        dictionary.put("apple", "りんご");
        
        // キーがアルファベット順でソートされる
        for (String key : dictionary.keySet()) {
            System.out.println(key + " = " + dictionary.get(key));
        }
    }
}
```

## 7.5 Queue インタフェース

### PriorityQueue

```java
import java.util.*;

public class PriorityQueueExample {
    public static void main(String[] args) {
        // 優先度付きキュー（小さい値が高優先度）
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        
        pq.add(30);
        pq.add(10);
        pq.add(20);
        
        // 優先度順に取り出される
        while (!pq.isEmpty()) {
            System.out.println(pq.poll());  // 10, 20, 30
        }
        
        // カスタム比較器を使用
        PriorityQueue<String> stringPQ = new PriorityQueue<>(
            (a, b) -> b.compareTo(a)  // 降順
        );
        
        stringPQ.add("banana");
        stringPQ.add("apple");
        stringPQ.add("cherry");
        
        while (!stringPQ.isEmpty()) {
            System.out.println(stringPQ.poll());  // cherry, banana, apple
        }
    }
}
```

## 7.6 実践的な使用例

### 単語カウンター

```java
import java.util.*;

public class WordCounter {
    public static void main(String[] args) {
        String text = "Java is great Java is powerful Java is fun";
        String[] words = text.split(" ");
        
        Map<String, Integer> wordCount = new HashMap<>();
        
        for (String word : words) {
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }
        
        // 結果表示
        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
```

### 学生管理システム

```java
import java.util.*;

class Student {
    private String name;
    private int id;
    private List<String> courses;
    
    public Student(String name, int id) {
        this.name = name;
        this.id = id;
        this.courses = new ArrayList<>();
    }
    
    public void addCourse(String course) {
        courses.add(course);
    }
    
    // getters
    public String getName() { return name; }
    public int getId() { return id; }
    public List<String> getCourses() { return new ArrayList<>(courses); }
    
    @Override
    public String toString() {
        return name + " (ID: " + id + ") - " + courses;
    }
}

public class StudentManager {
    private Map<Integer, Student> students = new HashMap<>();
    
    public void addStudent(Student student) {
        students.put(student.getId(), student);
    }
    
    public Student getStudent(int id) {
        return students.get(id);
    }
    
    public List<Student> getAllStudents() {
        return new ArrayList<>(students.values());
    }
    
    public Set<String> getAllCourses() {
        Set<String> allCourses = new HashSet<>();
        for (Student student : students.values()) {
            allCourses.addAll(student.getCourses());
        }
        return allCourses;
    }
    
    public static void main(String[] args) {
        StudentManager manager = new StudentManager();
        
        Student s1 = new Student("田中太郎", 1001);
        s1.addCourse("Java");
        s1.addCourse("データベース");
        
        Student s2 = new Student("佐藤花子", 1002);
        s2.addCourse("Java");
        s2.addCourse("Web開発");
        
        manager.addStudent(s1);
        manager.addStudent(s2);
        
        System.out.println("全学生:");
        for (Student student : manager.getAllStudents()) {
            System.out.println(student);
        }
        
        System.out.println("\n全コース:");
        for (String course : manager.getAllCourses()) {
            System.out.println(course);
        }
    }
}
```

## 7.7 コレクションのユーティリティ

### Collections クラス

```java
import java.util.*;

public class CollectionsUtilExample {
    public static void main(String[] args) {
        List<Integer> numbers = new ArrayList<>(Arrays.asList(3, 1, 4, 1, 5, 9));
        
        // ソート
        Collections.sort(numbers);
        System.out.println("ソート後: " + numbers);
        
        // 逆順
        Collections.reverse(numbers);
        System.out.println("逆順: " + numbers);
        
        // シャッフル
        Collections.shuffle(numbers);
        System.out.println("シャッフル後: " + numbers);
        
        // 最大値・最小値
        System.out.println("最大値: " + Collections.max(numbers));
        System.out.println("最小値: " + Collections.min(numbers));
        
        // 不変リストの作成
        List<String> immutableList = Collections.unmodifiableList(
            Arrays.asList("a", "b", "c")
        );
    }
}
```

## 7.8 練習問題

1. 整数のリストから重複を除去し、降順でソートするプログラムを作成してください。

2. 文章を入力として受け取り、各文字の出現回数をカウントするプログラムを作成してください。

3. 商品管理システムを作成し、商品をカテゴリ別に分類する機能を実装してください。

## 7.9 匿名クラスとラムダ式

### 7.9.1 匿名クラスの基本

匿名クラスは、その場限りのクラス実装を提供する機能です。主にインターフェイスの実装やイベント処理に使用されます。

```java
import java.util.*;

public class AnonymousClassExample {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("apple", "banana", "cherry");
        
        // Comparatorインターフェイスを匿名クラスで実装
        Collections.sort(words, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return a.length() - b.length();  // 文字数でソート
            }
        });
        
        System.out.println(words);  // [apple, banana, cherry]
    }
}
```

### 7.9.2 ラムダ式への進化

Java 8以降、単一の抽象メソッドを持つインターフェイス（関数型インターフェイス）は、ラムダ式で簡潔に記述できます。

```java
import java.util.*;

public class LambdaEvolutionExample {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("apple", "banana", "cherry");
        
        // 匿名クラス版
        Collections.sort(words, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return a.length() - b.length();
            }
        });
        
        // ラムダ式版（段階的簡略化）
        // ステップ1: ラムダ式の基本形
        Collections.sort(words, (String a, String b) -> {
            return a.length() - b.length();
        });
        
        // ステップ2: 型推論の利用
        Collections.sort(words, (a, b) -> {
            return a.length() - b.length();
        });
        
        // ステップ3: 単一式の簡略化
        Collections.sort(words, (a, b) -> a.length() - b.length());
        
        // ステップ4: メソッド参照の利用
        Collections.sort(words, Comparator.comparing(String::length));
    }
}
```

### 7.9.3 関数型インターフェイスの活用

Javaには多くの標準関数型インターフェイスが用意されています。

```java
import java.util.*;
import java.util.function.*;

public class FunctionalInterfaceExample {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("Java", "Python", "C++", "JavaScript");
        
        // Predicate<T>: boolean test(T t)
        Predicate<String> isLongName = name -> name.length() > 4;
        words.stream()
             .filter(isLongName)
             .forEach(System.out::println);  // Python, JavaScript
        
        // Function<T, R>: R apply(T t)
        Function<String, Integer> getLength = String::length;
        words.stream()
             .map(getLength)
             .forEach(System.out::println);  // 4, 6, 3, 10
        
        // Consumer<T>: void accept(T t)
        Consumer<String> printer = word -> System.out.println("言語: " + word);
        words.forEach(printer);
        
        // Supplier<T>: T get()
        Supplier<String> randomWord = () -> words.get(new Random().nextInt(words.size()));
        System.out.println("ランダム: " + randomWord.get());
    }
}
```

### 7.9.4 ラムダ式によるコレクション操作

```java
import java.util.*;
import java.util.stream.*;

public class LambdaCollectionExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 偶数のみを抽出し、2乗して、降順でソート
        List<Integer> result = numbers.stream()
            .filter(n -> n % 2 == 0)        // 偶数のみ
            .map(n -> n * n)                // 2乗
            .sorted((a, b) -> b - a)        // 降順ソート
            .collect(Collectors.toList());
        
        System.out.println(result);  // [100, 64, 36, 16, 4]
        
        // グループ化処理
        List<String> words = Arrays.asList("apple", "banana", "apricot", "blueberry", "cherry");
        
        Map<Character, List<String>> groupedByFirstChar = words.stream()
            .collect(Collectors.groupingBy(word -> word.charAt(0)));
        
        groupedByFirstChar.forEach((key, value) -> 
            System.out.println(key + ": " + value));
        // a: [apple, apricot]
        // b: [banana, blueberry]
        // c: [cherry]
    }
}
```

## 7.10 Stream APIの高度な活用

### 7.10.1 基本的なStream操作

```java
import java.util.*;
import java.util.stream.*;

public class StreamBasicsExample {
    public static void main(String[] args) {
        List<String> languages = Arrays.asList("Java", "Python", "C++", "JavaScript", "Go");
        
        // filter: 条件に合う要素のみを抽出
        languages.stream()
                 .filter(lang -> lang.length() <= 4)
                 .forEach(System.out::println);  // Java, C++, Go
        
        // map: 各要素を変換
        languages.stream()
                 .map(String::toUpperCase)
                 .forEach(System.out::println);  // JAVA, PYTHON, C++, JAVASCRIPT, GO
        
        // sorted: ソート
        languages.stream()
                 .sorted()
                 .forEach(System.out::println);  // C++, Go, Java, JavaScript, Python
        
        // distinct: 重複除去
        Arrays.asList("a", "b", "a", "c", "b").stream()
              .distinct()
              .forEach(System.out::println);  // a, b, c
    }
}
```

### 7.10.2 集約操作

```java
import java.util.*;
import java.util.stream.*;

public class StreamAggregationExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // count: 要素数
        long count = numbers.stream()
                           .filter(n -> n % 2 == 0)
                           .count();
        System.out.println("偶数の個数: " + count);  // 5
        
        // reduce: 集約処理
        int sum = numbers.stream()
                        .reduce(0, (a, b) -> a + b);
        System.out.println("合計: " + sum);  // 55
        
        // より簡潔な集約
        int product = numbers.stream()
                            .reduce(1, Integer::max);
        System.out.println("最大値: " + product);  // 10
        
        // Optional を返すreduce
        Optional<Integer> min = numbers.stream()
                                      .reduce(Integer::min);
        min.ifPresent(value -> System.out.println("最小値: " + value));  // 1
    }
}
```

### 7.10.3 メソッド参照

ラムダ式をさらに簡潔に記述するためのメソッド参照の活用方法：

```java
import java.util.*;
import java.util.stream.*;

public class MethodReferenceExample {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("hello", "world", "java", "stream");
        
        // 静的メソッド参照
        words.stream()
             .map(String::toUpperCase)  // s -> s.toUpperCase()
             .forEach(System.out::println);
        
        // インスタンスメソッド参照
        List<Integer> lengths = words.stream()
                                    .map(String::length)  // s -> s.length()
                                    .collect(Collectors.toList());
        
        // コンストラクタ参照
        List<StringBuilder> builders = words.stream()
                                           .map(StringBuilder::new)  // s -> new StringBuilder(s)
                                           .collect(Collectors.toList());
        
        // 任意オブジェクトのインスタンスメソッド参照
        List<String> sorted = words.stream()
                                  .sorted(String::compareToIgnoreCase)
                                  .collect(Collectors.toList());
        System.out.println(sorted);  // [hello, java, stream, world]
    }
}
```

### 7.10.4 コレクタ (Collectors) の活用

```java
import java.util.*;
import java.util.stream.*;

public class CollectorsExample {
    static class Person {
        String name;
        int age;
        String department;
        
        Person(String name, int age, String department) {
            this.name = name;
            this.age = age;
            this.department = department;
        }
        
        // getters
        public String getName() { return name; }
        public int getAge() { return age; }
        public String getDepartment() { return department; }
        
        @Override
        public String toString() {
            return name + "(" + age + ", " + department + ")";
        }
    }
    
    public static void main(String[] args) {
        List<Person> people = Arrays.asList(
            new Person("田中", 25, "開発"),
            new Person("佐藤", 30, "営業"),
            new Person("鈴木", 28, "開発"),
            new Person("高橋", 35, "営業"),
            new Person("伊藤", 22, "開発")
        );
        
        // 部署別グループ化
        Map<String, List<Person>> byDepartment = people.stream()
            .collect(Collectors.groupingBy(Person::getDepartment));
        
        byDepartment.forEach((dept, persons) -> {
            System.out.println(dept + ": " + persons);
        });
        
        // 部署別平均年齢
        Map<String, Double> avgAgeByDept = people.stream()
            .collect(Collectors.groupingBy(
                Person::getDepartment,
                Collectors.averagingInt(Person::getAge)
            ));
        
        avgAgeByDept.forEach((dept, avgAge) -> 
            System.out.println(dept + "の平均年齢: " + avgAge));
        
        // 名前のリストを取得
        String names = people.stream()
                            .map(Person::getName)
                            .collect(Collectors.joining(", "));
        System.out.println("全員: " + names);  // 田中, 佐藤, 鈴木, 高橋, 伊藤
    }
}
```

### 7.10.5 並列ストリーム

大量のデータを処理する際の並列処理の活用：

```java
import java.util.*;
import java.util.stream.*;

public class ParallelStreamExample {
    public static void main(String[] args) {
        // 大量のデータを生成
        List<Integer> largeList = IntStream.range(1, 1000000)
                                          .boxed()
                                          .collect(Collectors.toList());
        
        // 通常のストリーム処理時間測定
        long startTime = System.currentTimeMillis();
        long sum1 = largeList.stream()
                            .filter(n -> n % 2 == 0)
                            .mapToLong(n -> n * n)
                            .sum();
        long endTime = System.currentTimeMillis();
        System.out.println("通常ストリーム: " + (endTime - startTime) + "ms");
        
        // 並列ストリーム処理時間測定
        startTime = System.currentTimeMillis();
        long sum2 = largeList.parallelStream()
                            .filter(n -> n % 2 == 0)
                            .mapToLong(n -> n * n)
                            .sum();
        endTime = System.currentTimeMillis();
        System.out.println("並列ストリーム: " + (endTime - startTime) + "ms");
        
        System.out.println("結果は同じ: " + (sum1 == sum2));
    }
}
```

## まとめ

この章では、Javaのコレクションフレームワークの主要なインタフェースと実装クラス、そして匿名クラスからラムダ式への進化、Stream APIを活用した高度なデータ処理について学習しました。List、Set、Map、Queueなどの特性を理解し、ラムダ式と関数型プログラミングの概念を活用することで、より簡潔で表現力豊かなJavaプログラムを作成できるようになりました。