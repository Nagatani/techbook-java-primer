# 第10章 解答例

## 概要

第10章では、Java 8で導入されたStream APIについて学習します。関数型プログラミングのアプローチ、効率的なデータ処理、カスタムCollectorの作成などを重視した実装を行います。

## 解答例一覧

### 1. データ分析システム (DataAnalyzer系)

- **DataAnalyzer.java**: Stream APIを活用したデータ分析クラス
- **DataAnalyzerTest.java**: DataAnalyzerクラスのテスト

#### 学習ポイント
- 基本的なStream操作（filter, map, reduce）
- 統計計算（DoubleSummaryStatistics）
- グループ化とパーティショニング
- 並列処理との組み合わせ

### 2. 学生成績処理システム (StudentGradeProcessor系)

- **StudentGradeProcessor.java**: 学生の成績処理をStream APIで実装
- **StudentGradeProcessorTest.java**: StudentGradeProcessorクラスのテスト

#### 学習ポイント
- 複雑なグループ化操作
- Optional型の活用
- カスタムComparatorとソート
- ネストした集約処理

### 3. ログ処理システム (LogProcessor系)

- **LogProcessor.java**: ログ解析をStream APIで実装
- **LogProcessorTest.java**: LogProcessorクラスのテスト

#### 学習ポイント
- 時系列データの処理
- 正規表現との組み合わせ
- 複雑なフィルタリング条件
- 異常検知ロジック

### 4. カスタムCollector (CustomCollector系)

- **CustomCollector.java**: カスタムCollectorの実装例
- **CustomCollectorTest.java**: CustomCollectorクラスのテスト

#### 学習ポイント
- Collectorインターフェイスの実装
- 状態を持つ集約処理
- 並列処理対応
- 再利用可能なCollectorの設計

## Stream APIの主要操作

### 1. 中間操作（Intermediate Operations）

#### フィルタリング
```java
// 条件に合致する要素のみを残す
list.stream()
    .filter(student -> student.getScore() >= 80)
    .collect(Collectors.toList());
```

#### マッピング
```java
// 要素を別の形に変換
list.stream()
    .map(Student::getName)
    .collect(Collectors.toList());

// フラットマップ
list.stream()
    .flatMap(student -> student.getGrades().values().stream())
    .collect(Collectors.toList());
```

#### ソート
```java
// 単一条件でソート
list.stream()
    .sorted(Comparator.comparing(Student::getScore))
    .collect(Collectors.toList());

// 複合条件でソート
list.stream()
    .sorted(Comparator.comparing(Student::getScore)
                     .reversed()
                     .thenComparing(Student::getName))
    .collect(Collectors.toList());
```

### 2. 終端操作（Terminal Operations）

#### 集約
```java
// 基本的な集約
DoubleSummaryStatistics stats = students.stream()
    .mapToDouble(Student::getScore)
    .summaryStatistics();

// カスタム集約
double average = students.stream()
    .mapToDouble(Student::getScore)
    .average()
    .orElse(0.0);
```

#### 検索
```java
// 最初の要素
Optional<Student> first = students.stream()
    .filter(student -> student.getScore() >= 90)
    .findFirst();

// 最大値
Optional<Student> top = students.stream()
    .max(Comparator.comparing(Student::getScore));
```

#### 判定
```java
// すべてが条件を満たすか
boolean allPassed = students.stream()
    .allMatch(student -> student.getScore() >= 60);

// いずれかが条件を満たすか
boolean anyExcellent = students.stream()
    .anyMatch(student -> student.getScore() >= 95);
```

### 3. Collectorsの活用

#### 基本的なCollector
```java
// リストに収集
List<String> names = students.stream()
    .map(Student::getName)
    .collect(Collectors.toList());

// セットに収集
Set<String> departments = students.stream()
    .map(Student::getDepartment)
    .collect(Collectors.toSet());

// 文字列結合
String nameList = students.stream()
    .map(Student::getName)
    .collect(Collectors.joining(", "));
```

#### グループ化
```java
// 単純なグループ化
Map<String, List<Student>> byDepartment = students.stream()
    .collect(Collectors.groupingBy(Student::getDepartment));

// 下流Collectorとの組み合わせ
Map<String, Double> avgByDepartment = students.stream()
    .collect(Collectors.groupingBy(
        Student::getDepartment,
        Collectors.averagingDouble(Student::getScore)
    ));
```

#### パーティショニング
```java
// 条件による二分割
Map<Boolean, List<Student>> partitioned = students.stream()
    .collect(Collectors.partitioningBy(
        student -> student.getScore() >= 80
    ));
```

## カスタムCollectorの実装

### 基本構造
```java
public static <T> Collector<T, ?, List<T>> toImmutableList() {
    return Collector.of(
        ArrayList::new,           // supplier
        List::add,               // accumulator
        (list1, list2) -> {      // combiner
            list1.addAll(list2);
            return list1;
        },
        Collections::unmodifiableList  // finisher
    );
}
```

### 特性の指定
```java
public static <T> Collector<T, ?, Set<T>> toSet() {
    return Collector.of(
        HashSet::new,
        Set::add,
        (set1, set2) -> { set1.addAll(set2); return set1; },
        Collector.Characteristics.UNORDERED,
        Collector.Characteristics.IDENTITY_FINISH
    );
}
```

## パフォーマンスの考慮事項

### 1. 並列ストリーム

```java
// 並列処理で統計計算
DoubleSummaryStatistics stats = largeDataSet.parallelStream()
    .mapToDouble(DataPoint::getValue)
    .summaryStatistics();
```

### 2. 遅延評価

```java
// 中間操作は遅延評価される
Stream<String> filtered = list.stream()
    .filter(s -> s.length() > 5)  // まだ実行されない
    .map(String::toUpperCase);    // まだ実行されない

// 終端操作で初めて実行される
List<String> result = filtered.collect(Collectors.toList());
```

### 3. ショートサーキット

```java
// 条件を満たす最初の要素で処理が停止
Optional<Student> found = students.stream()
    .filter(student -> student.getScore() >= 95)
    .findFirst();  // 最初の要素が見つかったら処理終了
```

## 実行方法

### テストの実行

```bash
# 全テストの実行
./gradlew test

# 特定の章のテストのみ実行
./gradlew test --tests "chapter10.*"
```

### 個別システムの実行

```bash
# データ分析システムのテスト
java -cp build/classes/java/main:build/classes/java/test chapter10.solutions.DataAnalyzerTest

# カスタムCollectorのテスト
java -cp build/classes/java/main:build/classes/java/test chapter10.solutions.CustomCollectorTest
```

## 重要なポイント

### 1. 関数型プログラミングの原則

- **不変性**: Stream操作は元のデータを変更しない
- **純粋関数**: 副作用のない関数を使用
- **宣言的**: 「何を」するかに焦点（「どのように」ではなく）

### 2. メソッドリファレンスの活用

```java
// ラムダ式
students.stream().map(s -> s.getName())

// メソッドリファレンス（推奨）
students.stream().map(Student::getName)
```

### 3. Optional型の適切な使用

```java
// 悪い例
Optional<Student> student = findStudent(id);
if (student.isPresent()) {
    return student.get().getName();
}

// 良い例
return findStudent(id)
    .map(Student::getName)
    .orElse("Unknown");
```

### 4. ストリームの再利用不可

```java
Stream<String> stream = list.stream();
stream.filter(s -> s.length() > 5).count();  // OK
// stream.map(String::toUpperCase).count();  // 例外発生
```

## よくある間違い

### 1. 副作用のある操作

```java
// 悪い例：外部状態を変更
List<String> result = new ArrayList<>();
stream.forEach(result::add);  // 副作用あり

// 良い例：Collectorを使用
List<String> result = stream.collect(Collectors.toList());
```

### 2. 不適切な並列化

```java
// 悪い例：小さなデータセットで並列化
list.parallelStream()  // オーバーヘッドが大きい可能性

// 良い例：十分に大きなデータセットで並列化
largeList.parallelStream()
```

## 発展的な学習

1. **リアクティブストリーム**: RxJavaやProject Reactorとの比較
2. **パフォーマンスチューニング**: JMHを使ったベンチマーク
3. **関数型ライブラリ**: Vavr（旧Javaslang）などの活用
4. **並行プログラミング**: CompletableFutureとの組み合わせ

これらの解答例を通じて、Stream APIの強力な機能を理解し、効率的で読みやすいデータ処理コードを書く技術を身につけることができます。