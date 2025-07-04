## 7.6 コレクションの高度な操作

### Collections ユーティリティクラス

`Collections`クラスには、コレクションを操作するための便利なstaticメソッドが多数用意されています。

```java
import java.util.*;

public class CollectionsUtilityExample {
    public static void main(String[] args) {
        List<Integer> numbers = new ArrayList<>(Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6));
        
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
        
        // 二分探索（ソート済みリストが必要）
        Collections.binarySearch(numbers, 5);
        System.out.println("5の位置: " + index);
        
        // 要素の置換
        Collections.fill(numbers, 0);
        System.out.println("全て0に置換: " + numbers);
        
        // 不変コレクションの作成
        List<String> immutableList = Collections.unmodifiableList(
            Arrays.asList("読み専用", "変更不可", "安全"));
        // immutableList.add("追加"); // UnsupportedOperationException
        
        // 空のコレクション
        List<String> emptyList = Collections.emptyList();
        Set<String> emptySet = Collections.emptySet();
        Map<String, String> emptyMap = Collections.emptyMap();
    }
}
```

### 配列とコレクションの相互変換

```java
import java.util.*;

public class ArrayCollectionConversion {
    public static void main(String[] args) {
        // 配列からListへ
        String[] array = {"Apple", "Banana", "Cherry"};
        List<String> listFromArray = Arrays.asList(array);
        System.out.println("配列からList: " + listFromArray);
        
        // 注意：Arrays.asListで作成されたListは固定サイズ
        // listFromArray.add("Date"); // UnsupportedOperationException
        
        // 変更可能なListを作成
        List<String> mutableList = new ArrayList<>(Arrays.asList(array));
        mutableList.add("Date");
        System.out.println("変更可能なList: " + mutableList);
        
        // ListからListへ（Java 8以降）
        List<String> listFromStream = Arrays.stream(array)
                                          .collect(Collectors.toList());
        
        // Listから配列へ
        String[] arrayFromList = mutableList.toArray(new String[0]);
        System.out.println("Listから配列: " + Arrays.toString(arrayFromList));
        
        // Java 11以降の便利なメソッド
        List<String> listFromOf = List.of("A", "B", "C"); // 不変リスト
        Set<String> setFromOf = Set.of("X", "Y", "Z"); // 不変セット
        Map<String, Integer> mapFromOf = Map.of("one", 1, "two", 2); // 不変マップ
    }
}
```

## 7.7 Stream APIとの連携

### 基本的なStream操作

```java
import java.util.*;
import java.util.stream.*;

public class StreamWithCollections {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("apple", "banana", "cherry", "date", "elderberry");
        
        // 長さが5文字以上の単語をフィルタリング
        List<String> longWords = words.stream()
                                     .filter(word -> word.length() >= 5)
                                     .collect(Collectors.toList());
        System.out.println("5文字以上の単語: " + longWords);
        
        // 大文字に変換
        List<String> upperWords = words.stream()
                                      .map(String::toUpperCase)
                                      .collect(Collectors.toList());
        System.out.println("大文字変換: " + upperWords);
        
        // 文字数でソート
        List<String> sortedByLength = words.stream()
                                          .sorted(Comparator.comparing(String::length))
                                          .collect(Collectors.toList());
        System.out.println("文字数順: " + sortedByLength);
        
        // 文字数でグループ化
        Map<Integer, List<String>> groupedByLength = words.stream()
                .collect(Collectors.groupingBy(String::length));
        System.out.println("文字数でグループ化: " + groupedByLength);
        
        // 統計情報
        IntSummaryStatistics stats = words.stream()
                .mapToInt(String::length)
                .summaryStatistics();
        System.out.println("文字数統計: " + stats);
    }
}
```

### 並列処理

```java
import java.util.*;
import java.util.stream.*;

public class ParallelStreamExample {
    public static void main(String[] args) {
        List<Integer> numbers = IntStream.rangeClosed(1, 1_000_000)
                                        .boxed()
                                        .collect(Collectors.toList());
        
        // 順次処理
        long start = System.currentTimeMillis();
        long sum1 = numbers.stream()
                          .mapToLong(Integer::longValue)
                          .sum();
        long time1 = System.currentTimeMillis() - start;
        System.out.println("順次処理: " + sum1 + " (時間: " + time1 + "ms)");
        
        // 並列処理
        start = System.currentTimeMillis();
        long sum2 = numbers.parallelStream()
                          .mapToLong(Integer::longValue)
                          .sum();
        long time2 = System.currentTimeMillis() - start;
        System.out.println("並列処理: " + sum2 + " (時間: " + time2 + "ms)");
    }
}
```

## 7.8 実践的なコレクション活用例

### 単語カウンタ

```java
import java.util.*;
import java.util.stream.*;

public class WordCounter {
    public static void main(String[] args) {
        String text = "Java is a popular programming language. " +
                     "Java is object-oriented. Java is platform-independent.";
        
        // 単語の出現回数をカウント
        Map<String, Long> wordCount = Arrays.stream(text.toLowerCase().split("\\s+"))
                .map(word -> word.replaceAll("[^a-z]", "")) // 記号を除去
                .filter(word -> !word.isEmpty())
                .collect(Collectors.groupingBy(
                    word -> word,
                    Collectors.counting()
                ));
        
        System.out.println("単語の出現回数:");
        wordCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> 
                    System.out.println(entry.getKey() + ": " + entry.getValue()));
    }
}
```

### 学生成績管理システム

```java
import java.util.*;
import java.util.stream.*;

class Student {
    private String name;
    private String subject;
    private int score;
    
    public Student(String name, String subject, int score) {
        this.name = name;
        this.subject = subject;
        this.score = score;
    }
    
    // getter メソッド
    public String getName() { return name; }
    public String getSubject() { return subject; }
    public int getScore() { return score; }
    
    @Override
    public String toString() {
        return String.format("%s(%s: %d点)", name, subject, score);
    }
}

public class StudentGradeManager {
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
            new Student("Alice", "数学", 85),
            new Student("Bob", "数学", 92),
            new Student("Alice", "英語", 78),
            new Student("Charlie", "数学", 88),
            new Student("Bob", "英語", 85),
            new Student("Charlie", "英語", 90)
        );
        
        // 学生別の平均点
        Map<String, Double> averageByStudent = students.stream()
                .collect(Collectors.groupingBy(
                    Student::getName,
                    Collectors.averagingInt(Student::getScore)
                ));
        
        System.out.println("学生別平均点:");
        averageByStudent.forEach((name, avg) -> 
            System.out.printf("%s: %.1f点%n", name, avg));
        
        // 科目別の平均点
        Map<String, Double> averageBySubject = students.stream()
                .collect(Collectors.groupingBy(
                    Student::getSubject,
                    Collectors.averagingInt(Student::getScore)
                ));
        
        System.out.println("\n科目別平均点:");
        averageBySubject.forEach((subject, avg) -> 
            System.out.printf("%s: %.1f点%n", subject, avg));
        
        // 90点以上の成績
        List<Student> topScores = students.stream()
                .filter(student -> student.getScore() >= 90)
                .sorted(Comparator.comparing(Student::getScore).reversed())
                .collect(Collectors.toList());
        
        System.out.println("\n90点以上の成績:");
        topScores.forEach(System.out::println);
    }
}
```

## 7.9 匿名クラスとラムダ式

### 匿名クラスからラムда式への進化

Java 8より前では、関数型の処理を記述するために匿名クラスを使用していました。ラムダ式の導入により、より簡潔で読みやすいコードを書けるようになりました。

```java
import java.util.*;
import java.util.function.*;

public class AnonymousToLambda {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Charlie", "Alice", "Bob");
        
        // 従来の匿名クラス
        Collections.sort(names, new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return a.compareTo(b);
            }
        });
        System.out.println("匿名クラスでソート: " + names);
        
        // ラムダ式
        Collections.sort(names, (a, b) -> b.compareTo(a));
        System.out.println("ラムダ式で逆順ソート: " + names);
        
        // メソッド参照
        names.sort(String::compareTo);
        System.out.println("メソッド参照でソート: " + names);
    }
}
```