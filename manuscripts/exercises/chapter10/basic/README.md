# 第10章 基礎課題：Stream APIと高度なコレクション操作

## 課題概要

本章で学んだStream APIの基本操作を実践的に習得します。ラムダ式と組み合わせた宣言的なデータ処理の実装能力を養います。

## 課題1：社員データ分析システム

### 目的
- Stream APIの基本操作（filter、map、collect）
- Collectorsの活用
- ラムダ式とメソッド参照

### 要求仕様

1. **データモデル**
   ```java
   public class Employee {
       private String id;
       private String name;
       private String department;
       private int age;
       private double salary;
       private LocalDate hireDate;
       private List<String> skills;
       
       // コンストラクタ、getter/setter
   }
   ```

2. **分析機能の実装**
   ```java
   public class EmployeeAnalyzer {
       private List<Employee> employees;
       
       // 部署別の平均給与
       public Map<String, Double> getAverageSalaryByDepartment() {
           return employees.stream()
               .collect(Collectors.groupingBy(
                   Employee::getDepartment,
                   Collectors.averagingDouble(Employee::getSalary)
               ));
       }
       
       // 年齢層別の人数（20代、30代、40代、50代以上）
       public Map<String, Long> getCountByAgeGroup() {
           // 実装
       }
       
       // スキルを3つ以上持つ社員の一覧
       public List<Employee> getMultiSkilledEmployees() {
           // 実装
       }
       
       // 入社年度別の社員数
       public Map<Integer, Long> getCountByHireYear() {
           // 実装
       }
   }
   ```

3. **高度な分析**
   - 部署別の給与上位3名
   - スキル別の平均給与
   - 勤続年数と給与の相関データ

### 実装のヒント
```java
// 複数条件でのフィルタリング
List<Employee> seniorHighEarners = employees.stream()
    .filter(e -> e.getAge() >= 40)
    .filter(e -> e.getSalary() > 600000)
    .sorted(Comparator.comparing(Employee::getSalary).reversed())
    .collect(Collectors.toList());

// カスタムコレクター
Map<String, List<String>> skillsByDepartment = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::getDepartment,
        Collectors.flatMapping(
            e -> e.getSkills().stream(),
            Collectors.toSet()
        )
    ));
```

### 評価ポイント
- Stream APIの適切な使用
- 可読性の高いコード
- 効率的なデータ処理
- 適切なCollectorsの選択

## 課題2：商品売上分析

### 目的
- 複雑な集計処理
- グループ化とパーティショニング
- カスタムCollectorの作成

### 要求仕様

1. **データモデル**
   ```java
   public class Product {
       private String id;
       private String name;
       private String category;
       private double price;
   }
   
   public class Sale {
       private String id;
       private Product product;
       private int quantity;
       private LocalDateTime saleTime;
       private String customerId;
   }
   ```

2. **分析機能**
   ```java
   public class SalesAnalyzer {
       private List<Sale> sales;
       
       // カテゴリ別の売上金額
       public Map<String, Double> getTotalSalesByCategory() {
           // 実装
       }
       
       // 月別の売上推移
       public Map<YearMonth, Double> getMonthlySales() {
           // 実装
       }
       
       // 売れ筋商品トップ10（金額ベース）
       public List<Product> getTopSellingProducts(int limit) {
           // 実装
       }
       
       // 時間帯別の売上分析（0-6, 6-12, 12-18, 18-24）
       public Map<String, Long> getSalesByTimeSlot() {
           // 実装
       }
   }
   ```

3. **カスタム集計**
   ```java
   // 統計情報を含む結果
   public class SalesStatistics {
       private double total;
       private double average;
       private double min;
       private double max;
       private long count;
   }
   
   public SalesStatistics calculateStatistics(List<Sale> sales) {
       return sales.stream()
           .map(s -> s.getProduct().getPrice() * s.getQuantity())
           .collect(Collector.of(
               SalesStatistics::new,
               (stats, amount) -> {
                   // 統計情報の更新
               },
               (stats1, stats2) -> {
                   // 統計情報の結合
               }
           ));
   }
   ```

### 評価ポイント
- 複雑な集計の実装
- パフォーマンスを考慮した処理
- カスタムCollectorの設計
- 日時処理の適切な実装

## 課題3：テキスト解析システム

### 目的
- 文字列処理とStream API
- flatMapの活用
- 並列ストリームの基本

### 要求仕様

1. **基本的な解析**
   ```java
   public class TextAnalyzer {
       // テキストファイルから単語を抽出
       public Stream<String> extractWords(Path filePath) {
           // ファイルを読み込み、単語に分割
       }
       
       // 単語の出現頻度
       public Map<String, Long> getWordFrequency(String text) {
           return Arrays.stream(text.toLowerCase().split("\\W+"))
               .filter(word -> !word.isEmpty())
               .collect(Collectors.groupingBy(
                   Function.identity(),
                   Collectors.counting()
               ));
       }
       
       // N-gram生成
       public List<String> generateNGrams(String text, int n) {
           // 実装
       }
   }
   ```

2. **高度な解析**
   ```java
   // 複数ファイルの統合解析
   public Map<String, Long> analyzeMultipleFiles(List<Path> files) {
       return files.parallelStream()
           .flatMap(this::extractWords)
           .collect(Collectors.toConcurrentMap(
               Function.identity(),
               word -> 1L,
               Long::sum
           ));
   }
   
   // センテンスの長さ分析
   public IntSummaryStatistics analyzeSentenceLengths(String text) {
       return Arrays.stream(text.split("[.!?]+"))
           .map(String::trim)
           .filter(s -> !s.isEmpty())
           .mapToInt(s -> s.split("\\s+").length)
           .summaryStatistics();
   }
   ```

3. **パフォーマンス比較**
   ```java
   public void comparePerformance(List<String> largeTextList) {
       // シーケンシャル処理
       long startSeq = System.currentTimeMillis();
       Map<String, Long> resultSeq = largeTextList.stream()
           .flatMap(text -> Arrays.stream(text.split("\\W+")))
           .collect(Collectors.groupingBy(
               Function.identity(),
               Collectors.counting()
           ));
       long timeSeq = System.currentTimeMillis() - startSeq;
       
       // 並列処理
       long startPar = System.currentTimeMillis();
       Map<String, Long> resultPar = largeTextList.parallelStream()
           .flatMap(text -> Arrays.stream(text.split("\\W+")))
           .collect(Collectors.toConcurrentMap(
               Function.identity(),
               word -> 1L,
               Long::sum
           ));
       long timePar = System.currentTimeMillis() - startPar;
       
       System.out.println("Sequential: " + timeSeq + "ms");
       System.out.println("Parallel: " + timePar + "ms");
   }
   ```

### 評価ポイント
- flatMapの適切な使用
- 文字列処理の効率性
- 並列処理の基本的な理解
- エラーハンドリング

## 課題4：データ変換パイプライン

### 目的
- 複雑なデータ変換
- 中間操作の連鎖
- Optional の活用

### 要求仕様

1. **CSVデータの処理**
   ```java
   public class DataPipeline {
       // CSVレコードのモデル
       public class RawRecord {
           private String[] fields;
       }
       
       // 変換後のモデル
       public class ProcessedData {
           private String id;
           private String name;
           private double value;
           private LocalDate date;
           private boolean isValid;
       }
       
       // 変換パイプライン
       public List<ProcessedData> processCSV(Path csvFile) {
           return Files.lines(csvFile)
               .skip(1) // ヘッダースキップ
               .map(line -> line.split(","))
               .filter(fields -> fields.length >= 4)
               .map(this::parseRecord)
               .filter(Optional::isPresent)
               .map(Optional::get)
               .filter(ProcessedData::isValid)
               .sorted(Comparator.comparing(ProcessedData::getDate))
               .collect(Collectors.toList());
       }
       
       private Optional<ProcessedData> parseRecord(String[] fields) {
           try {
               // フィールドの解析と検証
               return Optional.of(new ProcessedData(...));
           } catch (Exception e) {
               return Optional.empty();
           }
       }
   }
   ```

2. **データの集約と変換**
   ```java
   // 日別の集計
   public Map<LocalDate, DoubleSummaryStatistics> getDailyStatistics(
           List<ProcessedData> data) {
       return data.stream()
           .collect(Collectors.groupingBy(
               ProcessedData::getDate,
               Collectors.summarizingDouble(ProcessedData::getValue)
           ));
   }
   
   // カスタム集約
   public Map<String, List<ProcessedData>> groupByCategory(
           List<ProcessedData> data,
           Function<ProcessedData, String> categorizer) {
       return data.stream()
           .collect(Collectors.groupingBy(
               categorizer,
               LinkedHashMap::new,
               Collectors.toList()
           ));
   }
   ```

### 評価ポイント
- エラー処理とOptionalの活用
- 複雑な変換パイプライン
- 適切な中間操作の選択
- 可読性とパフォーマンスのバランス

## 提出方法

1. 各課題の実装とテストコード
2. パフォーマンス測定結果
3. Stream APIを使った利点の説明
4. 従来のループ処理との比較

## 発展学習の提案

- **カスタムSpliterator**：独自のストリームソースの作成
- **Collector の詳細**：カスタムコレクターの設計パターン
- **リアクティブストリーム**：Flow APIの基礎
- **並列処理の最適化**：ForkJoinPoolのカスタマイズ

## 参考リソース

- Modern Java in Action（Raoul-Gabriel Urma他）
- Java 8 in Action（Raoul-Gabriel Urma他）
- Oracle公式ドキュメント：Stream API
- Effective Java 第3版：項目45-48