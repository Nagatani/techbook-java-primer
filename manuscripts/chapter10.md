# 第10章 Stream API

この章では、Java 8で導入されたStream APIについて学習します。コレクションの処理を関数型プログラミングスタイルで行う強力な機能を習得しましょう。

## 10.1 Stream APIとは

### 従来の方法 vs Stream API

```java
import java.util.*;
import java.util.stream.*;

public class StreamComparison {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 従来の方法：偶数を抽出して2倍にして合計を求める
        List<Integer> evenNumbers = new ArrayList<>();
        for (Integer num : numbers) {
            if (num % 2 == 0) {
                evenNumbers.add(num);
            }
        }
        
        List<Integer> doubled = new ArrayList<>();
        for (Integer num : evenNumbers) {
            doubled.add(num * 2);
        }
        
        int sum = 0;
        for (Integer num : doubled) {
            sum += num;
        }
        System.out.println("従来の方法: " + sum);
        
        // Stream API
        int streamSum = numbers.stream()
            .filter(n -> n % 2 == 0)    // 偶数をフィルタ
            .map(n -> n * 2)            // 2倍にマップ
            .mapToInt(Integer::intValue) // IntStreamに変換
            .sum();                     // 合計
        System.out.println("Stream API: " + streamSum);
    }
}
```

## 10.2 Streamの作成

```java
import java.util.*;
import java.util.stream.*;

public class StreamCreation {
    public static void main(String[] args) {
        // コレクションから
        List<String> list = Arrays.asList("a", "b", "c");
        Stream<String> streamFromList = list.stream();
        
        // 配列から
        String[] array = {"x", "y", "z"};
        Stream<String> streamFromArray = Arrays.stream(array);
        
        // 直接値から
        Stream<String> streamOfValues = Stream.of("apple", "banana", "cherry");
        
        // 空のStream
        Stream<String> emptyStream = Stream.empty();
        
        // 無限Stream
        Stream<Integer> infiniteStream = Stream.iterate(0, n -> n + 2);
        Stream<Double> randomStream = Stream.generate(Math::random);
        
        // 範囲指定
        IntStream range = IntStream.range(1, 10);          // 1-9
        IntStream rangeClosed = IntStream.rangeClosed(1, 10); // 1-10
        
        // ファイルから
        try {
            Stream<String> lines = Files.lines(Paths.get("example.txt"));
        } catch (Exception e) {
            // ファイル処理
        }
        
        // 使用例
        infiniteStream.limit(5).forEach(System.out::println);  // 0, 2, 4, 6, 8
        range.forEach(System.out::println);  // 1-9
    }
}
```

## 10.3 中間操作（Intermediate Operations）

### filter - フィルタリング

```java
import java.util.*;
import java.util.stream.*;

public class FilterExample {
    public static void main(String[] args) {
        List<String> words = Arrays.asList(
            "apple", "banana", "cherry", "date", "elderberry"
        );
        
        // 長さが5文字以上の単語
        words.stream()
             .filter(w -> w.length() >= 5)
             .forEach(System.out::println);
        
        // 'a'で始まる単語
        words.stream()
             .filter(w -> w.startsWith("a"))
             .forEach(System.out::println);
        
        // 複数条件
        words.stream()
             .filter(w -> w.length() > 4)
             .filter(w -> w.contains("e"))
             .forEach(System.out::println);
    }
}
```

### map - 変換

```java
import java.util.*;
import java.util.stream.*;

public class MapExample {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("hello", "world", "java", "stream");
        
        // 大文字に変換
        words.stream()
             .map(String::toUpperCase)
             .forEach(System.out::println);
        
        // 長さに変換
        words.stream()
             .map(String::length)
             .forEach(System.out::println);
        
        // カスタム変換
        words.stream()
             .map(w -> "「" + w + "」")
             .forEach(System.out::println);
        
        // 数値処理
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        numbers.stream()
               .map(n -> n * n)  // 平方
               .forEach(System.out::println);
    }
}
```

### flatMap - フラット化

```java
import java.util.*;
import java.util.stream.*;

public class FlatMapExample {
    public static void main(String[] args) {
        List<List<String>> nestedList = Arrays.asList(
            Arrays.asList("a", "b"),
            Arrays.asList("c", "d", "e"),
            Arrays.asList("f")
        );
        
        // ネストしたリストをフラット化
        nestedList.stream()
                  .flatMap(List::stream)
                  .forEach(System.out::println);  // a, b, c, d, e, f
        
        // 文字列を文字に分割
        List<String> sentences = Arrays.asList("hello world", "java stream");
        sentences.stream()
                 .flatMap(sentence -> Arrays.stream(sentence.split(" ")))
                 .forEach(System.out::println);  // hello, world, java, stream
        
        // Optionalのフラット化
        List<Optional<String>> optionals = Arrays.asList(
            Optional.of("apple"),
            Optional.empty(),
            Optional.of("banana")
        );
        
        optionals.stream()
                 .flatMap(Optional::stream)
                 .forEach(System.out::println);  // apple, banana
    }
}
```

### sorted - ソート

```java
import java.util.*;
import java.util.stream.*;

public class SortedExample {
    static class Person {
        String name;
        int age;
        
        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
        
        @Override
        public String toString() {
            return name + "(" + age + ")";
        }
    }
    
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6);
        
        // 自然順序でソート
        numbers.stream()
               .sorted()
               .forEach(System.out::println);
        
        // 逆順でソート
        numbers.stream()
               .sorted(Comparator.reverseOrder())
               .forEach(System.out::println);
        
        List<Person> people = Arrays.asList(
            new Person("田中", 25),
            new Person("佐藤", 30),
            new Person("鈴木", 20)
        );
        
        // 年齢でソート
        people.stream()
              .sorted(Comparator.comparing(p -> p.age))
              .forEach(System.out::println);
        
        // 名前でソート
        people.stream()
              .sorted(Comparator.comparing(p -> p.name))
              .forEach(System.out::println);
        
        // 複数条件でソート
        people.stream()
              .sorted(Comparator.comparing((Person p) -> p.age)
                               .thenComparing(p -> p.name))
              .forEach(System.out::println);
    }
}
```

### distinct, limit, skip

```java
import java.util.*;
import java.util.stream.*;

public class StreamOperations {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 2, 3, 3, 3, 4, 5, 5);
        
        // 重複除去
        numbers.stream()
               .distinct()
               .forEach(System.out::println);  // 1, 2, 3, 4, 5
        
        // 最初の3つ
        numbers.stream()
               .limit(3)
               .forEach(System.out::println);  // 1, 2, 2
        
        // 最初の3つをスキップ
        numbers.stream()
               .skip(3)
               .forEach(System.out::println);  // 3, 3, 3, 4, 5, 5
        
        // 組み合わせ
        numbers.stream()
               .distinct()      // 重複除去
               .skip(2)         // 最初の2つをスキップ
               .limit(2)        // 2つまで
               .forEach(System.out::println);  // 3, 4
    }
}
```

## 10.4 終端操作（Terminal Operations）

### forEach, forEachOrdered

```java
import java.util.*;
import java.util.stream.*;

public class ForEachExample {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("apple", "banana", "cherry");
        
        // 各要素に処理を適用
        words.stream().forEach(System.out::println);
        
        // 並列処理でも順序を保証
        words.parallelStream().forEachOrdered(System.out::println);
        
        // 副作用のある処理
        List<String> result = new ArrayList<>();
        words.stream().forEach(result::add);
        System.out.println(result);
    }
}
```

### collect - コレクション収集

```java
import java.util.*;
import java.util.stream.*;
import static java.util.stream.Collectors.*;

public class CollectExample {
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
            return name + "(" + age + "," + department + ")";
        }
    }
    
    public static void main(String[] args) {
        List<Person> people = Arrays.asList(
            new Person("田中", 25, "開発"),
            new Person("佐藤", 30, "営業"),
            new Person("鈴木", 35, "開発"),
            new Person("高橋", 28, "営業")
        );
        
        // リストに収集
        List<String> names = people.stream()
                                  .map(Person::getName)
                                  .collect(toList());
        
        // セットに収集
        Set<String> departments = people.stream()
                                       .map(Person::getDepartment)
                                       .collect(toSet());
        
        // 文字列結合
        String nameList = people.stream()
                                .map(Person::getName)
                                .collect(joining(", "));
        
        // 部署でグループ化
        Map<String, List<Person>> byDepartment = people.stream()
                                                      .collect(groupingBy(Person::getDepartment));
        
        // 部署ごとの人数
        Map<String, Long> countByDepartment = people.stream()
                                                   .collect(groupingBy(Person::getDepartment, counting()));
        
        // 部署ごとの平均年齢
        Map<String, Double> avgAgeByDepartment = people.stream()
                                                      .collect(groupingBy(
                                                          Person::getDepartment,
                                                          averagingDouble(Person::getAge)
                                                      ));
        
        // 年齢で分割（30歳未満とそれ以上）
        Map<Boolean, List<Person>> partitioned = people.stream()
                                                      .collect(partitioningBy(p -> p.getAge() < 30));
        
        System.out.println("名前一覧: " + names);
        System.out.println("部署一覧: " + departments);
        System.out.println("名前結合: " + nameList);
        System.out.println("部署別: " + byDepartment);
        System.out.println("部署別人数: " + countByDepartment);
        System.out.println("部署別平均年齢: " + avgAgeByDepartment);
        System.out.println("年齢で分割: " + partitioned);
    }
}
```

### reduce - 削減操作

```java
import java.util.*;
import java.util.stream.*;

public class ReduceExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        // 合計
        Optional<Integer> sum = numbers.stream().reduce((a, b) -> a + b);
        System.out.println("合計: " + sum.orElse(0));
        
        // 初期値付きの合計
        Integer sumWithIdentity = numbers.stream().reduce(0, (a, b) -> a + b);
        System.out.println("合計（初期値付き）: " + sumWithIdentity);
        
        // 最大値
        Optional<Integer> max = numbers.stream().reduce(Integer::max);
        System.out.println("最大値: " + max.orElse(0));
        
        // 積
        Optional<Integer> product = numbers.stream().reduce((a, b) -> a * b);
        System.out.println("積: " + product.orElse(0));
        
        // 文字列の連結
        List<String> words = Arrays.asList("Java", "Stream", "API");
        String joined = words.stream().reduce("", (a, b) -> a + " " + b);
        System.out.println("連結: " + joined.trim());
        
        // 複雑な例：単語の長さの合計
        Integer totalLength = words.stream()
                                  .map(String::length)
                                  .reduce(0, Integer::sum);
        System.out.println("総文字数: " + totalLength);
    }
}
```

### find, match, count

```java
import java.util.*;
import java.util.stream.*;

public class TerminalOperations {
    public static void main(String[] args) {
        List<String> words = Arrays.asList("apple", "banana", "cherry", "date");
        
        // 要素の検索
        Optional<String> first = words.stream()
                                     .filter(w -> w.startsWith("b"))
                                     .findFirst();
        System.out.println("最初のb開始: " + first.orElse("なし"));
        
        Optional<String> any = words.stream()
                                   .filter(w -> w.length() > 5)
                                   .findAny();
        System.out.println("5文字超の任意: " + any.orElse("なし"));
        
        // マッチング
        boolean anyMatch = words.stream().anyMatch(w -> w.startsWith("a"));
        boolean allMatch = words.stream().allMatch(w -> w.length() > 3);
        boolean noneMatch = words.stream().noneMatch(w -> w.startsWith("z"));
        
        System.out.println("a開始があるか: " + anyMatch);
        System.out.println("全て3文字超か: " + allMatch);
        System.out.println("z開始がないか: " + noneMatch);
        
        // カウント
        long count = words.stream()
                         .filter(w -> w.contains("a"))
                         .count();
        System.out.println("aを含む単語数: " + count);
        
        // 数値統計
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        IntSummaryStatistics stats = numbers.stream()
                                           .mapToInt(Integer::intValue)
                                           .summaryStatistics();
        
        System.out.println("統計: " + stats);
        System.out.println("平均: " + stats.getAverage());
        System.out.println("最大: " + stats.getMax());
        System.out.println("最小: " + stats.getMin());
    }
}
```

## 10.5 並列ストリーム

```java
import java.util.*;
import java.util.stream.*;

public class ParallelStreamExample {
    public static void main(String[] args) {
        List<Integer> numbers = IntStream.rangeClosed(1, 1000000)
                                        .boxed()
                                        .collect(Collectors.toList());
        
        // シーケンシャル処理
        long startTime = System.currentTimeMillis();
        long sum1 = numbers.stream()
                          .mapToLong(Integer::longValue)
                          .sum();
        long endTime = System.currentTimeMillis();
        System.out.println("シーケンシャル: " + (endTime - startTime) + "ms");
        
        // 並列処理
        startTime = System.currentTimeMillis();
        long sum2 = numbers.parallelStream()
                          .mapToLong(Integer::longValue)
                          .sum();
        endTime = System.currentTimeMillis();
        System.out.println("並列: " + (endTime - startTime) + "ms");
        
        System.out.println("結果は同じ: " + (sum1 == sum2));
        
        // 並列処理での注意点
        List<Integer> results = new ArrayList<>();
        
        // 危険：非同期安全でない操作
        // numbers.parallelStream().forEach(results::add);  // データ競合の可能性
        
        // 安全：collect使用
        List<Integer> safeResults = numbers.parallelStream()
                                          .filter(n -> n % 2 == 0)
                                          .collect(Collectors.toList());
    }
}
```

## 10.6 実践的な例

### ファイル処理

```java
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class FileProcessingExample {
    public static void main(String[] args) throws Exception {
        // サンプルファイルの作成
        List<String> lines = Arrays.asList(
            "apple,100,fruit",
            "banana,80,fruit",
            "carrot,60,vegetable",
            "date,120,fruit",
            "eggplant,90,vegetable"
        );
        
        Path tempFile = Files.createTempFile("sample", ".csv");
        Files.write(tempFile, lines);
        
        // ファイルの読み込みと処理
        Map<String, Double> avgPriceByCategory = Files.lines(tempFile)
            .map(line -> line.split(","))
            .collect(Collectors.groupingBy(
                parts -> parts[2],  // カテゴリでグループ
                Collectors.averagingDouble(parts -> Double.parseDouble(parts[1]))
            ));
        
        System.out.println("カテゴリ別平均価格: " + avgPriceByCategory);
        
        // 高価な果物の検索
        List<String> expensiveFruits = Files.lines(tempFile)
            .map(line -> line.split(","))
            .filter(parts -> "fruit".equals(parts[2]))
            .filter(parts -> Double.parseDouble(parts[1]) > 90)
            .map(parts -> parts[0])
            .collect(Collectors.toList());
        
        System.out.println("高価な果物: " + expensiveFruits);
        
        // クリーンアップ
        Files.delete(tempFile);
    }
}
```

### データ分析

```java
import java.util.*;
import java.util.stream.*;
import static java.util.stream.Collectors.*;

public class DataAnalysisExample {
    static class Sale {
        String product;
        String region;
        int quantity;
        double price;
        
        Sale(String product, String region, int quantity, double price) {
            this.product = product;
            this.region = region;
            this.quantity = quantity;
            this.price = price;
        }
        
        double getRevenue() { return quantity * price; }
        
        // getters
        public String getProduct() { return product; }
        public String getRegion() { return region; }
        public int getQuantity() { return quantity; }
        public double getPrice() { return price; }
        
        @Override
        public String toString() {
            return String.format("%s(%s): %d×%.2f=%.2f", 
                               product, region, quantity, price, getRevenue());
        }
    }
    
    public static void main(String[] args) {
        List<Sale> sales = Arrays.asList(
            new Sale("ノートPC", "東京", 10, 80000),
            new Sale("ノートPC", "大阪", 8, 82000),
            new Sale("タブレット", "東京", 15, 45000),
            new Sale("タブレット", "大阪", 12, 46000),
            new Sale("スマートフォン", "東京", 25, 70000),
            new Sale("スマートフォン", "大阪", 20, 72000)
        );
        
        // 総売上
        double totalRevenue = sales.stream()
                                  .mapToDouble(Sale::getRevenue)
                                  .sum();
        System.out.println("総売上: " + totalRevenue);
        
        // 商品別売上
        Map<String, Double> revenueByProduct = sales.stream()
            .collect(groupingBy(Sale::getProduct, 
                               summingDouble(Sale::getRevenue)));
        System.out.println("商品別売上: " + revenueByProduct);
        
        // 地域別平均価格
        Map<String, Double> avgPriceByRegion = sales.stream()
            .collect(groupingBy(Sale::getRegion,
                               averagingDouble(Sale::getPrice)));
        System.out.println("地域別平均価格: " + avgPriceByRegion);
        
        // 最高売上の商品
        Optional<Sale> topSale = sales.stream()
            .max(Comparator.comparing(Sale::getRevenue));
        System.out.println("最高売上: " + topSale.orElse(null));
        
        // 売上上位3商品
        List<Sale> top3 = sales.stream()
            .sorted(Comparator.comparing(Sale::getRevenue).reversed())
            .limit(3)
            .collect(toList());
        System.out.println("売上上位3:");
        top3.forEach(System.out::println);
    }
}
```

## 10.7 練習問題

1. 文字列のリストから、5文字以上の単語を抽出し、アルファベット順にソートして重複を除去するプログラムを作成してください。

2. 学生のテストスコアを管理するシステムを作成し、科目別平均点、学生別総合点などを計算してください。

3. ログファイルを模したデータから、エラーレベル別の集計を行うプログラムを作成してください。

## まとめ

この章では、Stream APIを使用したデータ処理の方法を学習しました。関数型プログラミングのアプローチにより、簡潔で読みやすいコードによってコレクションの操作を行えるようになりました。中間操作と終端操作の組み合わせにより、複雑なデータ処理も効率的に実装できます。