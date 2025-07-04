# 第10章 Stream API

## 🎯総合演習プロジェクトへのステップ

本章で学ぶStream APIは、**総合演習プロジェクト「TODOリストアプリケーション」** のデータ処理を、より宣言的で洗練されたコードに進化させるための強力な武器となります。

- **タスクのフィルタリング**: `filter()`を使って、「完了済みのタスクのみ」「未完了のタスクのみ」を簡単に抽出できます。
  ```java
  // forループでの実装
  List<Task> completedTasks = new ArrayList<>();
  for (Task task : allTasks) {
      if (task.isDone()) {
          completedTasks.add(task);
      }
  }

  // Stream APIでの実装（簡潔！）
  List<Task> completedTasks = allTasks.stream()
                                      .filter(Task::isDone)
                                      .collect(Collectors.toList());
  ```
- **データの集計**: `count()`を使って、完了済みタスクの数を一行で計算できます。
- **データの変換**: `map()`を使って、`Task`オブジェクトのリストからタスク名のリストへと簡単に変換できます。

この章を学ぶことで、複雑なデ���タ操作を、まるで英文を読むかのように直感的に記述するスキルが身につきます。

## 📋 本章の学習目標

### 前提知識
- **第9章のラムダ式と関数型インターフェイスの習得**：ラムダ式による関数型プログラミングの理解
- **コレクションフレームワークの豊富な使用経験**：ArrayList、Map等の効果的な活用経験
- **データ処理を含む実用的なプログラム開発経験**：ループ処理とデータ変換の実装経験

### 到達目標

#### 知識理解目標
- Stream APIの設計思想と利点
- 中間操作と終端操作の概念と種類
- 遅延評価（lazy evaluation）のしくみ
- 並列ストリーム（parallel stream）の理解と注意点
- 関数型プログラミングの実践的活用

#### 技能習得目標
- Stream APIを使った効率的なデータ処理
- 複雑なデータ変換とフィルタリングの実装
- Collectorsを使った柔軟なデータ収集
- 並列処理による性能向上の実践
- 宣言的なデータ処理パイプラインの設計

#### 到達レベルの指標
- [ ] 複雑なデータ処理要件をStream APIで効率的に実装できる
- [ ] カスタムCollectorを使った専用のデータ収集処理が実装できる
- [ ] 並列ストリームを適切に活用した高性能データ処理ができる
- [ ] 従来のループ処理とStream処理を適切に使い分けできる

---

## 始めに：データ処理パラダイムの革命とStream APIの意義

前章でラムダ式と関数型プログラミングの基礎について学習しました。本章では、Java 8で同時に導入され、関数型プログラミングの威力を具体的に体験できる「Stream API」について詳細に学習します。

Stream APIは単なる新しいライブラリの追加ではありません。データ処理に対する従来のアプローチを根本的に変革し、より宣言的で理解しやすく、かつ高性能なプログラムの作成を可能にする、画期的な技術革新です。

### データ処理の歴史的変遷

コンピュータプログラミングの本質は「データの処理」です。入力されたデータを変換、集約、フィルタリングして、有用な情報を抽出することが、ほぼすべてのプログラムの基本的な役割です。この処理方法は、コンピュータサイエンスの発展とともに段階的に進化してきました。

**初期のデータ処理（1950年代〜1960年代）**：初期のプログラムでは、データ処理は主に配列とループを使用した命令的な手法で行われていました。プログラマは、「どのようにデータを処理するか」の詳細な手順をすべて記述する必要がありました。

```fortran
DO 10 I = 1, N
   IF (ARRAY(I) .GT. THRESHOLD) THEN
      RESULT(J) = ARRAY(I) * 2
      J = J + 1
   ENDIF
10 CONTINUE
```

**構造化プログラミング時代（1970年代〜1980年代）**：C言語などの構造化プログラミング言語により、関数を使用したデータ処理の抽象化が進みました。しかし、依然として命令的なアプローチが主流でした。

```c
for (int i = 0; i < size; i++) {
    if (data[i] > threshold) {
        result[count++] = data[i] * 2;
    }
}
```

**オブジェクト指向時代（1990年代〜2000年代）**：Javaのようなオブジェクト指向言語では、データ構造（コレクション）と処理ロジックが組み合わされましたが、データ変換の記述は依然として命令的でした。

```java
List<Integer> result = new ArrayList<>();
for (Integer item : data) {
    if (item > threshold) {
        result.add(item * 2);
    }
}
```

### 命令的データ処理の問題点

従来の命令的データ処理アプローチには、以下のような深刻な問題がありました：

**可読性の低下**：「何をしたいか」よりも「どのようにするか」に焦点が当たるため、プログラムの意図が不明確になりがちでした。特に複雑なデータ変換では、本来の目的がコードの詳細に埋もれてしまいました。

**再利用性の欠如**：似たような処理パターン（フィルタリング、マッピング、集約など）であっても、毎回ゼロから実装する必要があり、コードの重複が発生しやすい状況でした。

**エラーの多発**：ループのインデックス管理、一時変数の状態管理、nullチェックなど、処理の本質とは関係ない詳細でバグが発生しやすく、保守性が低下していました。

**並行処理の困難さ**：マルチコアプロセッサの普及により並行処理の重要性が高まりましたが、従来の命令的アプローチでは、安全で効率的な並行処理の実装が極めて困難でした。

**最適化の限界**：コンパイラや実行時システムによる自動最適化が困難で、特にループ処理の効率化には限界がありました。

### 関数型データ処理の思想

これらの問題を解決するため、関数型プログラミングの分野では、数学的な関数概念にもとづいた宣言的なデータ処理手法が研究されてきました。

**高階関数による抽象化**：map、filter、reduceなどの高階関数により、よく使用されるデータ処理パターンを抽象化し、再利用可能な形で提供することが可能になりました。

**遅延評価（Lazy Evaluation）**：必要になるまで計算を延期することで、無限のデータストリームや大量のデータを効率的に処理できるようになりました。

**関数の合成（Function Composition）**：単純な関数を組み合わせて複雑な処理を構築することで、モジュラーで理解しやすいプログラムが作成できるようになりました。

**不変性の活用**：元のデータを変更することなく新しいデータを生成することで、副作用を排除し、並行処理の安全性を確保できるようになりました。

### 他言語におけるStream処理の発展

Java 8のStream APIは、ほかのプログラミング言語における先進的な取り組みを参考にして設計されました：

**Lisp（1950年代〜）**：リスト処理の分野で、map、filter、reduceなどの概念が初めて導入されました。関数型データ処理の理論的基盤を提供しました。

**Haskell（1990年〜）**：純粋関数型言語として、遅延評価と無限リストの概念を確立しました。効率的で表現力豊かなデータ処理の手法を実現しました。

**Scala（2003年〜）**：JVM上で動作する関数型言語として、Java開発者にとって身近な環境で関数型データ処理の利点を示しました。

**C# LINQ（2007年〜）**：.NET Frameworkに統合された言語統合クエリとして、オブジェクト指向言語における関数型データ処理の成功例を提供しました。

**Python（list comprehensions）**：簡潔で読みやすい構文により、関数型データ処理の普及に貢献しました。

### Java 8 Stream APIの革新性

Java 8のStream APIは、これらの先進的な概念をJavaエコシステムに統合し、以下の革新的な特徴を実現しました：

**既存コレクションとの統合**：既存のList、Set、MapなどのコレクションからシームレスにStreamを生成でき、既存のコードベースへの影響を最小限に抑えながら新機能を導入できます。

**型安全性の保証**：Javaの強い型システムとの統合により、コンパイル時に多くのエラーを検出でき、実行時の安全性が向上しています。

**並行処理の自動化**：parallelStream()により、複雑な並行処理ロジックを記述することなく、マルチコア処理の恩恵を享受できます。

**遅延評価の実装**：中間操作（intermediate operations）は遅延評価され、終端操作（terminal operations）が呼ばれるまで実際の処理は実行されません。これにより、効率的なデータ処理が実現されています。

**専用プリミティブ型ストリーム**：IntStream、LongStream、DoubleStreamにより、ボクシング/アンボクシングのオーバーヘッドを回避し、高性能な数値処理が可能になっています。

### ビッグデータ時代におけるStream処理の重要性

現代のソフトウェア開発において、Stream処理の重要性はますます高まっています：

**ビッグデータ処理**：Apache Spark、Apache Flinkなどの分散処理フレームワークでは、Stream APIと類似した概念が中核的な役割を果たしています。Javaでの基礎学習が、これらの先進技術の理解につながります。

**リアルタイムデータ処理**：IoT、ソーシャルメディア、金融取引など、リアルタイムでのデータ処理が求められる分野では、Stream処理が標準的なアプローチとなっています。

**マイクロサービスアーキテクチャ**：サービス間でのデータ変換や集約において、Stream処理による宣言的なアプローチが可読性と保守性の向上に貢献しています。

**機械学習とデータサイエンス**：データの前処理、特徴抽出、結果の集約など、機械学習のパイプラインにおいてStream処理の概念が広く活用されています。

### 関数型思考への転換

Stream APIを効果的に活用するためには、従来の命令的思考から関数型思考への転換が重要です：

**What（何を）からHow（どのように）へ**：「ループをどう書くか」ではなく「データをどう変換したいか」に焦点を当てることで、より本質的な問題解決に集中できます。

**データフローの視覚化**：データが一連の変換を通じて流れていく様子を視覚的に理解することで、複雑な処理も直感的に把握できます。

**関数の組み合わせ**：単純な操作を組み合わせて複雑な処理を構築する思考パターンにより、モジュラーで拡張性の高いプログラムが作成できます。

### 本章で学習する内容の意義

本章では、これらの歴史的背景と技術的意義を踏まえて、Java Stream APIを体系的に学習していきます。単にAPIの使い方を覚えるのではなく、以下の点を重視して学習を進めます：

**宣言的プログラミングの習得**：「何をしたいか」を明確に表現するプログラミングスタイルを身につけ、コードの可読性と保守性を向上させます。

**効率的なデータ処理技術**：大量のデータを効率的に処理するための技術を習得し、現代的なアプリケーション開発の基礎を築きます。

**並行処理の理解**：Stream APIの並行処理機能を通じて、マルチコア時代のプログラミング技術を理解します。

**関数型設計パターン**：map-filter-reduceパターンなど、関数型プログラミングの基本的な設計パターンを習得します。

**現代技術への橋渡し**：ビッグデータ処理、リアクティブプログラミング、クラウドネイティブ開発など、現代的な技術領域への基礎知識を身につけます。

Stream APIを深く理解することは、Javaプログラマとしての表現力を大幅に向上させ、データドリブンな現代のソフトウェア開発において不可欠な技術を身につけることにつながります。本章を通じて、単なる「新しいライブラリの使い方」を超えて、「データ処理に対する新しい思考方法」を習得していきましょう。

本章では、Java 8で導入されたStream APIについて学習します。コレクションの処理を関数型プログラミングスタイルで行う強力な機能を習得しましょう。

## 10.1 Stream APIとは

### 従来の方法 vs Stream API：企業売上データ分析システム

以下の包括的な例では、企業の売上データ分析システムを通じて、従来の命令的アプローチとStream APIによる宣言的アプローチの違いと、Stream APIの強力な表現力を学習します：

```java
import java.util.*;
import java.util.stream.*;
import java.util.function.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;

/**
 * 企業売上データ分析システム
 * 従来の命令的プログラミングとStream APIの宣言的プログラミングの比較
 */

// 売上レコードクラス
class SalesRecord {
    private String productId;
    private String productName;
    private String category;
    private String region;
    private BigDecimal amount;
    private int quantity;
    private LocalDate saleDate;
    private String salesPerson;
    
    public SalesRecord(String productId, String productName, String category, 
                      String region, BigDecimal amount, int quantity, 
                      LocalDate saleDate, String salesPerson) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.region = region;
        this.amount = amount;
        this.quantity = quantity;
        this.saleDate = saleDate;
        this.salesPerson = salesPerson;
    }
    
    // ゲッターメソッド
    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getCategory() { return category; }
    public String getRegion() { return region; }
    public BigDecimal getAmount() { return amount; }
    public int getQuantity() { return quantity; }
    public LocalDate getSaleDate() { return saleDate; }
    public String getSalesPerson() { return salesPerson; }
    
    @Override
    public String toString() {
        return String.format("%s: %s (¥%s) - %s [%s]", 
            productId, productName, amount, region, saleDate);
    }
}

public class SalesDataAnalysisSystem {
    private List<SalesRecord> salesData;
    
    public SalesDataAnalysisSystem() {
        initializeSalesData();
    }
    
    private void initializeSalesData() {
        salesData = Arrays.asList(
            new SalesRecord("P001", "ノートパソコン", "電子機器", "東京", new BigDecimal("89800"), 2, LocalDate.of(2024, 1, 15), "田中"),
            new SalesRecord("P002", "マウス", "電子機器", "大阪", new BigDecimal("2800"), 5, LocalDate.of(2024, 1, 16), "佐藤"),
            new SalesRecord("P003", "キーボード", "電子機器", "名古屋", new BigDecimal("8500"), 3, LocalDate.of(2024, 1, 17), "鈴木"),
            new SalesRecord("P004", "モニター", "電子機器", "東京", new BigDecimal("35200"), 1, LocalDate.of(2024, 1, 18), "田中"),
            new SalesRecord("P005", "Java入門書", "書籍", "大阪", new BigDecimal("3200"), 10, LocalDate.of(2024, 1, 19), "高橋"),
            new SalesRecord("P006", "データ構造本", "書籍", "東京", new BigDecimal("4800"), 7, LocalDate.of(2024, 1, 20), "田中"),
            new SalesRecord("P001", "ノートパソコン", "電子機器", "福岡", new BigDecimal("89800"), 1, LocalDate.of(2024, 2, 5), "山田"),
            new SalesRecord("P007", "プリンター", "電子機器", "大阪", new BigDecimal("25600"), 2, LocalDate.of(2024, 2, 6), "佐藤"),
            new SalesRecord("P008", "スマートフォン", "電子機器", "名古屋", new BigDecimal("78900"), 4, LocalDate.of(2024, 2, 7), "鈴木"),
            new SalesRecord("P005", "Java入門書", "書籍", "東京", new BigDecimal("3200"), 15, LocalDate.of(2024, 2, 8), "田中"),
            new SalesRecord("P009", "タブレット", "電子機器", "福岡", new BigDecimal("45600"), 3, LocalDate.of(2024, 2, 9), "山田"),
            new SalesRecord("P010", "ヘッドフォン", "電子機器", "大阪", new BigDecimal("12800"), 6, LocalDate.of(2024, 2, 10), "佐藤")
        );
    }
    
    // 従来の命令的アプローチ：高額商品（50,000円以上）の地域別売上集計
    public void traditionalApproach() {
        System.out.println("=== 従来の命令的アプローチ ===");
        
        long startTime = System.nanoTime();
        
        // ステップ1: 高額商品をフィルタリング
        List<SalesRecord> expensiveProducts = new ArrayList<>();
        for (SalesRecord record : salesData) {
            if (record.getAmount().compareTo(new BigDecimal("50000")) >= 0) {
                expensiveProducts.add(record);
            }
        }
        
        // ステップ2: 地域別にグループ化
        Map<String, List<SalesRecord>> recordsByRegion = new HashMap<>();
        for (SalesRecord record : expensiveProducts) {
            String region = record.getRegion();
            if (!recordsByRegion.containsKey(region)) {
                recordsByRegion.put(region, new ArrayList<>());
            }
            recordsByRegion.get(region).add(record);
        }
        
        // ステップ3: 地域別売上合計を計算
        Map<String, BigDecimal> salesByRegion = new HashMap<>();
        for (Map.Entry<String, List<SalesRecord>> entry : recordsByRegion.entrySet()) {
            String region = entry.getKey();
            BigDecimal totalSales = BigDecimal.ZERO;
            for (SalesRecord record : entry.getValue()) {
                totalSales = totalSales.add(record.getAmount().multiply(BigDecimal.valueOf(record.getQuantity())));
            }
            salesByRegion.put(region, totalSales);
        }
        
        // ステップ4: 結果の表示（売上順にソート）
        List<Map.Entry<String, BigDecimal>> sortedSales = new ArrayList<>(salesByRegion.entrySet());
        sortedSales.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        long endTime = System.nanoTime();
        
        System.out.println("高額商品の地域別売上（命令的アプローチ）:");
        for (Map.Entry<String, BigDecimal> entry : sortedSales) {
            System.out.printf("  %s: ¥%s%n", entry.getKey(), entry.getValue());
        }
        System.out.printf("処理時間: %d ナノ秒%n", endTime - startTime);
        System.out.printf("コード行数: 約30行%n");
    }
    
    // Stream APIによる宣言的アプローチ：同じ処理をより簡潔に
    public void streamApproach() {
        System.out.println("\n=== Stream APIによる宣言的アプローチ ===");
        
        long startTime = System.nanoTime();
        
        Map<String, BigDecimal> salesByRegion = salesData.stream()
            .filter(record -> record.getAmount().compareTo(new BigDecimal("50000")) >= 0)  // 高額商品フィルタ
            .collect(Collectors.groupingBy(                                                // 地域別グループ化
                SalesRecord::getRegion,
                Collectors.reducing(                                                       // 売上合計計算
                    BigDecimal.ZERO,
                    record -> record.getAmount().multiply(BigDecimal.valueOf(record.getQuantity())),
                    BigDecimal::add
                )
            ));
        
        long endTime = System.nanoTime();
        
        System.out.println("高額商品の地域別売上（Stream API）:");
        salesByRegion.entrySet().stream()
            .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())          // 売上順ソート
            .forEach(entry -> System.out.printf("  %s: ¥%s%n", entry.getKey(), entry.getValue()));
        
        System.out.printf("処理時間: %d ナノ秒%n", endTime - startTime);
        System.out.printf("コード行数: 約10行%n");
    }
    
    // 複雑な分析処理：営業担当者別の月次パフォーマンス分析
    public void complexAnalysisTraditional() {
        System.out.println("\n=== 複雑分析（従来手法）: 営業担当者別月次パフォーマンス ===");
        
        // 営業担当者別、月別の売上集計
        Map<String, Map<Month, BigDecimal>> salesByPersonAndMonth = new HashMap<>();
        
        for (SalesRecord record : salesData) {
            String person = record.getSalesPerson();
            Month month = record.getSaleDate().getMonth();
            BigDecimal recordTotal = record.getAmount().multiply(BigDecimal.valueOf(record.getQuantity()));
            
            if (!salesByPersonAndMonth.containsKey(person)) {
                salesByPersonAndMonth.put(person, new HashMap<>());
            }
            
            Map<Month, BigDecimal> monthlyData = salesByPersonAndMonth.get(person);
            monthlyData.put(month, monthlyData.getOrDefault(month, BigDecimal.ZERO).add(recordTotal));
        }
        
        // トップパフォーマー（総売上が最も高い担当者）を特定
        String topPerformer = "";
        BigDecimal maxTotalSales = BigDecimal.ZERO;
        
        for (Map.Entry<String, Map<Month, BigDecimal>> personEntry : salesByPersonAndMonth.entrySet()) {
            BigDecimal totalSales = BigDecimal.ZERO;
            for (BigDecimal monthlySales : personEntry.getValue().values()) {
                totalSales = totalSales.add(monthlySales);
            }
            if (totalSales.compareTo(maxTotalSales) > 0) {
                maxTotalSales = totalSales;
                topPerformer = personEntry.getKey();
            }
        }
        
        System.out.println("営業担当者別月次実績（従来手法）:");
        for (Map.Entry<String, Map<Month, BigDecimal>> entry : salesByPersonAndMonth.entrySet()) {
            System.out.printf("  %s:%n", entry.getKey());
            for (Map.Entry<Month, BigDecimal> monthData : entry.getValue().entrySet()) {
                System.out.printf("    %s: ¥%s%n", monthData.getKey(), monthData.getValue());
            }
        }
        System.out.printf("トップパフォーマー: %s (¥%s)%n", topPerformer, maxTotalSales);
    }
    
    // 同じ分析をStream APIで実装
    public void complexAnalysisStream() {
        System.out.println("\n=== 複雑分析（Stream API）: 営業担当者別月次パフォーマンス ===");
        
        // 営業担当者別、月別の売上集計
        Map<String, Map<Month, BigDecimal>> salesByPersonAndMonth = salesData.stream()
            .collect(Collectors.groupingBy(
                SalesRecord::getSalesPerson,
                Collectors.groupingBy(
                    record -> record.getSaleDate().getMonth(),
                    Collectors.reducing(
                        BigDecimal.ZERO,
                        record -> record.getAmount().multiply(BigDecimal.valueOf(record.getQuantity())),
                        BigDecimal::add
                    )
                )
            ));
        
        // トップパフォーマーを特定
        Optional<Map.Entry<String, BigDecimal>> topPerformer = salesData.stream()
            .collect(Collectors.groupingBy(
                SalesRecord::getSalesPerson,
                Collectors.reducing(
                    BigDecimal.ZERO,
                    record -> record.getAmount().multiply(BigDecimal.valueOf(record.getQuantity())),
                    BigDecimal::add
                )
            ))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue());
        
        System.out.println("営業担当者別月次実績（Stream API）:");
        salesByPersonAndMonth.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> {
                System.out.printf("  %s:%n", entry.getKey());
                entry.getValue().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(monthData -> 
                        System.out.printf("    %s: ¥%s%n", monthData.getKey(), monthData.getValue()));
            });
        
        topPerformer.ifPresent(performer -> 
            System.out.printf("トップパフォーマー: %s (¥%s)%n", performer.getKey(), performer.getValue()));
    }
    
    // Stream APIの様々な操作パターンのデモンストレーション
    public void demonstrateStreamPatterns() {
        System.out.println("\n=== Stream API操作パターンのデモンストレーション ===");
        
        // パターン1: フィルタ → マップ → 収集
        List<String> productNames = salesData.stream()
            .filter(record -> record.getCategory().equals("電子機器"))
            .map(SalesRecord::getProductName)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        System.out.println("電子機器製品名: " + productNames);
        
        // パターン2: グループ化 → 集約
        Map<String, Double> avgSalesByCategory = salesData.stream()
            .collect(Collectors.groupingBy(
                SalesRecord::getCategory,
                Collectors.averagingDouble(record -> 
                    record.getAmount().multiply(BigDecimal.valueOf(record.getQuantity())).doubleValue())
            ));
        System.out.println("カテゴリ別平均売上: " + avgSalesByCategory);
        
        // パターン3: 複数条件での複雑なフィルタリング
        List<SalesRecord> complexFilter = salesData.stream()
            .filter(record -> record.getRegion().equals("東京"))
            .filter(record -> record.getSaleDate().getMonth() == Month.JANUARY)
            .filter(record -> record.getAmount().compareTo(new BigDecimal("5000")) > 0)
            .sorted(Comparator.comparing(SalesRecord::getAmount).reversed())
            .collect(Collectors.toList());
        System.out.println("東京・1月・5000円超の売上件数: " + complexFilter.size());
        
        // パターン4: 統計データの計算
        IntSummaryStatistics quantityStats = salesData.stream()
            .mapToInt(SalesRecord::getQuantity)
            .summaryStatistics();
        System.out.printf("数量統計 - 合計:%d, 平均:%.2f, 最大:%d, 最小:%d%n",
            quantityStats.getSum(), quantityStats.getAverage(), 
            quantityStats.getMax(), quantityStats.getMin());
        
        // パターン5: 並行処理（パフォーマンス向上）
        long parallelStartTime = System.nanoTime();
        BigDecimal parallelTotalSales = salesData.parallelStream()
            .map(record -> record.getAmount().multiply(BigDecimal.valueOf(record.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        long parallelEndTime = System.nanoTime();
        
        long sequentialStartTime = System.nanoTime();
        BigDecimal sequentialTotalSales = salesData.stream()
            .map(record -> record.getAmount().multiply(BigDecimal.valueOf(record.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        long sequentialEndTime = System.nanoTime();
        
        System.out.printf("総売上: ¥%s%n", parallelTotalSales);
        System.out.printf("並行処理時間: %d ns, 順次処理時間: %d ns%n", 
            parallelEndTime - parallelStartTime, sequentialEndTime - sequentialStartTime);
        
        // パターン6: Optional活用による安全なデータ取得
        Optional<SalesRecord> maxSaleRecord = salesData.stream()
            .max(Comparator.comparing(record -> record.getAmount().multiply(BigDecimal.valueOf(record.getQuantity()))));
        maxSaleRecord.ifPresent(record -> 
            System.out.println("最高売上記録: " + record));
        
        // パターン7: 条件付き集計（partitioningBy）
        Map<Boolean, List<SalesRecord>> partitionedSales = salesData.stream()
            .collect(Collectors.partitioningBy(record -> 
                record.getAmount().compareTo(new BigDecimal("10000")) >= 0));
        System.out.println("高額売上（1万円以上）: " + partitionedSales.get(true).size() + "件");
        System.out.println("一般売上（1万円未満）: " + partitionedSales.get(false).size() + "件");
        
        // パターン8: 営業担当者別の最高売上抽出
        Map<String, Optional<SalesRecord>> topSalesByPerson = salesData.stream()
            .collect(Collectors.groupingBy(
                SalesRecord::getSalesPerson,
                Collectors.maxBy(Comparator.comparing(record -> 
                    record.getAmount().multiply(BigDecimal.valueOf(record.getQuantity()))))
            ));
        
        System.out.println("営業担当者別最高売上:");
        topSalesByPerson.forEach((person, recordOpt) -> {
            recordOpt.ifPresent(record -> {
                BigDecimal saleAmount = record.getAmount().multiply(BigDecimal.valueOf(record.getQuantity()));
                System.out.printf("  %s: %s (¥%s)%n", person, record.getProductName(), saleAmount);
            });
        });
    }
    
    // Stream APIの高度な操作例：時系列データ分析
    public void demonstrateTimeSeriesAnalysis() {
        System.out.println("\n=== 時系列データ分析の高度なStream操作 ===");
        
        // 月別売上推移の分析
        Map<Month, BigDecimal> monthlySales = salesData.stream()
            .collect(Collectors.groupingBy(
                record -> record.getSaleDate().getMonth(),
                Collectors.reducing(
                    BigDecimal.ZERO,
                    record -> record.getAmount().multiply(BigDecimal.valueOf(record.getQuantity())),
                    BigDecimal::add
                )
            ));
        
        System.out.println("月別売上推移:");
        monthlySales.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> System.out.printf("  %s: ¥%s%n", entry.getKey(), entry.getValue()));
        
        // 地域別成長率計算（1月vs2月）
        Map<String, Map<Month, BigDecimal>> regionMonthSales = salesData.stream()
            .collect(Collectors.groupingBy(
                SalesRecord::getRegion,
                Collectors.groupingBy(
                    record -> record.getSaleDate().getMonth(),
                    Collectors.reducing(
                        BigDecimal.ZERO,
                        record -> record.getAmount().multiply(BigDecimal.valueOf(record.getQuantity())),
                        BigDecimal::add
                    )
                )
            ));
        
        System.out.println("\n地域別成長率（1月→2月）:");
        regionMonthSales.forEach((region, monthSales) -> {
            BigDecimal jan = monthSales.getOrDefault(Month.JANUARY, BigDecimal.ZERO);
            BigDecimal feb = monthSales.getOrDefault(Month.FEBRUARY, BigDecimal.ZERO);
            
            if (jan.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal growthRate = feb.subtract(jan)
                    .divide(jan, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
                System.out.printf("  %s: %.2f%%%n", region, growthRate);
            }
        });
        
        // 商品カテゴリ別のパフォーマンス指標
        Map<String, Map<String, Object>> categoryMetrics = salesData.stream()
            .collect(Collectors.groupingBy(
                SalesRecord::getCategory,
                Collectors.teeing(
                    Collectors.summingDouble(record -> 
                        record.getAmount().multiply(BigDecimal.valueOf(record.getQuantity())).doubleValue()),
                    Collectors.counting(),
                    (totalSales, count) -> Map.of(
                        "totalSales", BigDecimal.valueOf(totalSales),
                        "transactionCount", count,
                        "averagePerTransaction", BigDecimal.valueOf(totalSales / count)
                    )
                )
            ));
        
        System.out.println("\nカテゴリ別パフォーマンス指標:");
        categoryMetrics.forEach((category, metrics) -> {
            System.out.printf("  %s:%n", category);
            System.out.printf("    総売上: ¥%s%n", metrics.get("totalSales"));
            System.out.printf("    取引件数: %s件%n", metrics.get("transactionCount"));
            System.out.printf("    平均取引額: ¥%s%n", metrics.get("averagePerTransaction"));
        });
    }
    
    public static void main(String[] args) {
        SalesDataAnalysisSystem system = new SalesDataAnalysisSystem();
        
        // 従来手法とStream APIの比較
        system.traditionalApproach();
        system.streamApproach();
        
        // 複雑な分析の比較
        system.complexAnalysisTraditional();
        system.complexAnalysisStream();
        
        // Stream APIの様々なパターン
        system.demonstrateStreamPatterns();
        
        // 時系列データ分析
        system.demonstrateTimeSeriesAnalysis();
    }
}
```

**このプログラムから学ぶ重要なStream APIの概念：**

1. **宣言的プログラミング**：「何をしたいか」に集中でき、「どのようにするか」の詳細は言語に任せられます。

2. **関数の組み合わせ**：filter、map、collectなどの操作を組み合わせて複雑な処理パイプラインを構築できます。

3. **可読性の向上**：処理の流れが直線的で理解しやすく、コードの意図が明確になります。

4. **並行処理の簡素化**：parallelStream()により、複雑な並行処理ロジックなしにマルチコア処理を活用できます。

5. **エラーの削減**：ループや一時変数の管理が不要になり、インデックス外参照などのバグを避けられます。

6. **遅延評価**：中間操作は終端操作が呼ばれるまで実行されず、効率的なデータ処理が実現されます。

7. **複雑な集約処理**：`groupingBy`、`partitioningBy`、`teeing`などにより、従来では複雑だった分析処理を簡潔に記述できます。

## 10.2 Streamの作成

### 基本的なStream生成方法：データソース別活用例

Stream APIを効果的に活用するためには、さまざまなデータソースからStreamを生成する方法を理解することが重要です。以下で実用的な例を通じて学習します：

```java
import java.util.*;
import java.util.stream.*;
import java.nio.file.*;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Stream生成の包括的デモンストレーション
 * 様々なデータソースからのStream作成方法を実証
 */
public class StreamCreationDemo {
    
    // 1. コレクションからのStream生成
    public static void demonstrateCollectionStreams() {
        System.out.println("=== コレクションからのStream生成 ===");
        
        // List からの Stream
        List<String> fruits = Arrays.asList("apple", "banana", "cherry", "date", "elderberry");
        fruits.stream()
              .filter(fruit -> fruit.length() > 5)
              .map(String::toUpperCase)
              .forEach(System.out::println);
        
        // Set からの Stream
        Set<Integer> numbers = Set.of(1, 2, 3, 4, 5, 2, 3); // 重複は除去される
        numbers.stream()
               .filter(n -> n % 2 == 0)
               .map(n -> n * n)
               .forEach(System.out::println);
        
        // Map からの Stream (エントリーストリーム)
        Map<String, Integer> scores = Map.of("Alice", 85, "Bob", 92, "Charlie", 78);
        scores.entrySet().stream()
              .filter(entry -> entry.getValue() > 80)
              .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
    }
    
    // 2. 配列からのStream生成
    public static void demonstrateArrayStreams() {
        System.out.println("\n=== 配列からのStream生成 ===");
        
        // Object配列からのStream
        String[] languages = {"Java", "Python", "JavaScript", "C++", "Go"};
        Arrays.stream(languages)
              .filter(lang -> lang.contains("Java"))
              .forEach(System.out::println);
        
        // プリミティブ配列からの特殊Stream
        int[] ages = {25, 30, 35, 40, 45, 50};
        IntStream ageStream = Arrays.stream(ages);
        System.out.println("平均年齢: " + ageStream.average().orElse(0.0));
        
        double[] prices = {100.5, 200.0, 150.75, 300.25};
        DoubleStream priceStream = Arrays.stream(prices);
        System.out.println("総価格: " + priceStream.sum());
    }
    
    // 3. Stream.of()による直接生成
    public static void demonstrateDirectStreams() {
        System.out.println("\n=== Stream.of()による直接生成 ===");
        
        // 直接値からのStream
        Stream.of("red", "green", "blue", "yellow")
              .map(color -> "Color: " + color)
              .forEach(System.out::println);
        
        // 混合型のObject Stream
        Stream.of(1, "hello", 3.14, true)
              .map(Object::toString)
              .map(String::toUpperCase)
              .forEach(System.out::println);
    }
    
    // 4. 範囲指定による数値Stream
    public static void demonstrateRangeStreams() {
        System.out.println("\n=== 範囲指定による数値Stream ===");
        
        // range: 終端値を含まない
        System.out.print("range(1, 6): ");
        IntStream.range(1, 6)
                 .forEach(n -> System.out.print(n + " "));
        System.out.println();
        
        // rangeClosed: 終端値を含む
        System.out.print("rangeClosed(1, 5): ");
        IntStream.rangeClosed(1, 5)
                 .forEach(n -> System.out.print(n + " "));
        System.out.println();
        
        // 数学的計算例：1から100までの合計
        int sum = IntStream.rangeClosed(1, 100).sum();
        System.out.println("1から100までの合計: " + sum);
        
        // 階乗計算例
        int factorial = IntStream.rangeClosed(1, 5)
                                .reduce(1, (a, b) -> a * b);
        System.out.println("5の階乗: " + factorial);
    }
    
    // 5. 無限Streamの生成と制御
    public static void demonstrateInfiniteStreams() {
        System.out.println("\n=== 無限Streamの生成と制御 ===");
        
        // Stream.iterate()による無限Stream
        System.out.print("偶数の最初の10個: ");
        Stream.iterate(0, n -> n + 2)
              .limit(10)
              .forEach(n -> System.out.print(n + " "));
        System.out.println();
        
        // フィボナッチ数列の生成
        System.out.print("フィボナッチ数列の最初の10個: ");
        Stream.iterate(new int[]{0, 1}, fib -> new int[]{fib[1], fib[0] + fib[1]})
              .limit(10)
              .mapToInt(fib -> fib[0])
              .forEach(n -> System.out.print(n + " "));
        System.out.println();
        
        // Stream.generate()による無限Stream
        System.out.print("ランダムな5つの数値: ");
        Stream.generate(Math::random)
              .limit(5)
              .forEach(n -> System.out.printf("%.2f ", n));
        System.out.println();
        
        // カスタム生成器
        System.out.print("カウントダウン: ");
        Stream.iterate(10, n -> n > 0, n -> n - 1)  // Java 9以降の条件付きiterate
              .forEach(n -> System.out.print(n + " "));
        System.out.println();
    }
    
    // 6. ファイルからのStream生成
    public static void demonstrateFileStreams() {
        System.out.println("\n=== ファイルからのStream生成 ===");
        
        try {
            // 一時ファイルの作成
            Path tempFile = Files.createTempFile("sample", ".txt");
            Files.write(tempFile, Arrays.asList(
                "apple,100,fruit",
                "banana,80,fruit", 
                "carrot,60,vegetable",
                "date,120,fruit",
                "eggplant,90,vegetable"
            ));
            
            // ファイルの各行をStreamとして処理
            System.out.println("ファイル内容:");
            Files.lines(tempFile)
                 .forEach(System.out::println);
            
            // CSVデータの解析
            System.out.println("\n果物のみ抽出:");
            Files.lines(tempFile)
                 .map(line -> line.split(","))
                 .filter(parts -> parts.length == 3 && "fruit".equals(parts[2]))
                 .forEach(parts -> System.out.println(parts[0] + ": " + parts[1] + "円"));
            
            // ファイルの削除
            Files.delete(tempFile);
            
        } catch (IOException e) {
            System.err.println("ファイル処理エラー: " + e.getMessage());
        }
    }
    
    // 7. 正規表現によるStream生成
    public static void demonstratePatternStreams() {
        System.out.println("\n=== 正規表現によるStream生成 ===");
        
        String text = "apple,banana;cherry:date|elderberry";
        
        // 区切り文字で分割
        Pattern.compile("[,;:|]")
               .splitAsStream(text)
               .filter(word -> word.length() > 4)
               .map(String::toUpperCase)
               .forEach(System.out::println);
        
        // HTML文字列から要素抽出
        String html = "<div>Hello</div><span>World</span><p>Java</p>";
        Pattern.compile("<(\\w+)>([^<]+)</\\1>")
               .matcher(html)
               .results()
               .map(result -> result.group(1) + ": " + result.group(2))
               .forEach(System.out::println);
    }
    
    // 8. 空Streamと条件付きStream
    public static void demonstrateEmptyAndConditionalStreams() {
        System.out.println("\n=== 空Streamと条件付きStream ===");
        
        // 空Stream
        Stream<String> emptyStream = Stream.empty();
        System.out.println("空Streamの要素数: " + emptyStream.count());
        
        // 条件によるStream生成
        List<String> data = Arrays.asList("a", "b", "c");
        boolean processData = true;
        
        (processData ? data.stream() : Stream.<String>empty())
            .map(String::toUpperCase)
            .forEach(System.out::println);
        
        // Optional からのStream (Java 9以降)
        Optional<String> optionalValue = Optional.of("Hello Stream");
        optionalValue.stream()
                    .map(String::toUpperCase)
                    .forEach(System.out::println);
    }
    
    public static void main(String[] args) {
        demonstrateCollectionStreams();
        demonstrateArrayStreams();
        demonstrateDirectStreams();
        demonstrateRangeStreams();
        demonstrateInfiniteStreams();
        demonstrateFileStreams();
        demonstratePatternStreams();
        demonstrateEmptyAndConditionalStreams();
    }
}
```

**Stream生成の重要なポイント：**

1. **データソースの多様性**：コレクション、配列、ファイル、生成器などさまざまなソースからStreamを作成できます。

2. **専用プリミティブStream**：`IntStream`、`LongStream`、`DoubleStream`により、ボクシング/アンボクシングのオーバーヘッドを回避できます。

3. **無限Streamの制御**：`limit()`や条件付き`iterate()`により、無限シーケンスを安全に扱えます。

4. **遅延評価の活用**：Streamは終端操作が呼ばれるまで実際の処理を開始しないため、効率的です。

5. **リソース管理**：ファイルStreamなどは適切にクローズされるよう注意が必要です。

## 10.3 中間操作（Intermediate Operations）

中間操作は、Streamパイプラインの中核を成す操作で、Streamを別のStreamに変換します。重要な特徴は**遅延評価**されることで、終端操作が呼ばれるまで実際の処理は実行されません。

### 主要な中間操作の実践的活用

```java
import java.util.*;
import java.util.stream.*;
import java.util.function.*;

/**
 * Stream中間操作の包括的デモンストレーション
 * filter, map, flatMap, distinct, sorted, limit, skip, peek等の実用例
 */
public class IntermediateOperationsDemo {
    
    // サンプルデータクラス
    static class Employee {
        private String name;
        private String department;
        private int age;
        private double salary;
        private List<String> skills;
        
        public Employee(String name, String department, int age, double salary, List<String> skills) {
            this.name = name;
            this.department = department;
            this.age = age;
            this.salary = salary;
            this.skills = skills;
        }
        
        // ゲッターメソッド
        public String getName() { return name; }
        public String getDepartment() { return department; }
        public int getAge() { return age; }
        public double getSalary() { return salary; }
        public List<String> getSkills() { return skills; }
        
        @Override
        public String toString() {
            return String.format("%s(%s, %d歳, ¥%.0f)", name, department, age, salary);
        }
    }
    
    // 1. filter - 条件によるフィルタリング
    public static void demonstrateFilter() {
        System.out.println("=== filter() - フィルタリング操作 ===");
        
        List<Employee> employees = createEmployees();
        
        // 基本的なフィルタリング
        System.out.println("30歳以上の従業員:");
        employees.stream()
                 .filter(emp -> emp.getAge() >= 30)
                 .forEach(System.out::println);
        
        // 複数条件によるフィルタリング
        System.out.println("\nIT部門で年収500万以上:");
        employees.stream()
                 .filter(emp -> "IT".equals(emp.getDepartment()))
                 .filter(emp -> emp.getSalary() >= 5000000)
                 .forEach(System.out::println);
        
        // 複雑な条件（スキルベース）
        System.out.println("\nJavaスキルを持つ従業員:");
        employees.stream()
                 .filter(emp -> emp.getSkills().contains("Java"))
                 .forEach(System.out::println);
    }
    
    // 2. map - 要素の変換
    public static void demonstrateMap() {
        System.out.println("\n=== map() - 要素変換操作 ===");
        
        List<Employee> employees = createEmployees();
        
        // 基本的な変換
        System.out.println("従業員名一覧:");
        employees.stream()
                 .map(Employee::getName)
                 .forEach(System.out::println);
        
        // 計算を含む変換
        System.out.println("\n月給一覧:");
        employees.stream()
                 .map(emp -> emp.getName() + ": ¥" + String.format("%.0f", emp.getSalary() / 12))
                 .forEach(System.out::println);
        
        // 複雑なオブジェクト変換
        System.out.println("\n従業員サマリー:");
        employees.stream()
                 .map(emp -> String.format("%s (%s部門) - スキル数: %d", 
                             emp.getName(), emp.getDepartment(), emp.getSkills().size()))
                 .forEach(System.out::println);
    }
    
    // 3. flatMap - ネストしたコレクションの平坦化
    public static void demonstrateFlatMap() {
        System.out.println("\n=== flatMap() - 平坦化操作 ===");
        
        List<Employee> employees = createEmployees();
        
        // 全従業員のスキルを平坦化
        System.out.println("全スキル一覧:");
        employees.stream()
                 .flatMap(emp -> emp.getSkills().stream())
                 .distinct()
                 .sorted()
                 .forEach(System.out::println);
        
        // 部門別スキル統計
        System.out.println("\n部門別ユニークスキル数:");
        employees.stream()
                 .collect(Collectors.groupingBy(Employee::getDepartment))
                 .forEach((dept, empList) -> {
                     long uniqueSkills = empList.stream()
                                               .flatMap(emp -> emp.getSkills().stream())
                                               .distinct()
                                               .count();
                     System.out.println(dept + ": " + uniqueSkills + "種類");
                 });
        
        // 文字列分割の例
        List<String> sentences = Arrays.asList("Hello World", "Java Stream API", "Functional Programming");
        System.out.println("\n全単語一覧:");
        sentences.stream()
                 .flatMap(sentence -> Arrays.stream(sentence.split(" ")))
                 .map(String::toLowerCase)
                 .distinct()
                 .sorted()
                 .forEach(System.out::println);
    }
    
    // 4. distinct - 重複除去
    public static void demonstrateDistinct() {
        System.out.println("\n=== distinct() - 重複除去操作 ===");
        
        List<Employee> employees = createEmployees();
        
        // 部門の重複除去
        System.out.println("存在する部門:");
        employees.stream()
                 .map(Employee::getDepartment)
                 .distinct()
                 .forEach(System.out::println);
        
        // 年齢層の重複除去
        System.out.println("\n年齢層（10歳刻み）:");
        employees.stream()
                 .mapToInt(emp -> (emp.getAge() / 10) * 10)
                 .distinct()
                 .sorted()
                 .forEach(age -> System.out.println(age + "代"));
    }
    
    // 5. sorted - ソート操作
    public static void demonstrateSorted() {
        System.out.println("\n=== sorted() - ソート操作 ===");
        
        List<Employee> employees = createEmployees();
        
        // 基本的なソート（年齢順）
        System.out.println("年齢順:");
        employees.stream()
                 .sorted(Comparator.comparingInt(Employee::getAge))
                 .forEach(System.out::println);
        
        // 複合ソート（部門別→年収順）
        System.out.println("\n部門別年収順:");
        employees.stream()
                 .sorted(Comparator.comparing(Employee::getDepartment)
                                  .thenComparing(Employee::getSalary, Comparator.reverseOrder()))
                 .forEach(System.out::println);
        
        // カスタムソート（スキル数順）
        System.out.println("\nスキル数順（降順）:");
        employees.stream()
                 .sorted((e1, e2) -> Integer.compare(e2.getSkills().size(), e1.getSkills().size()))
                 .forEach(emp -> System.out.println(emp.getName() + ": " + emp.getSkills().size() + "スキル"));
    }
    
    // 6. limit と skip - 要素数制御
    public static void demonstrateLimitAndSkip() {
        System.out.println("\n=== limit() & skip() - 要素数制御 ===");
        
        List<Employee> employees = createEmployees();
        
        // 上位3名の取得
        System.out.println("年収上位3名:");
        employees.stream()
                 .sorted(Comparator.comparingDouble(Employee::getSalary).reversed())
                 .limit(3)
                 .forEach(System.out::println);
        
        // ページネーション（2番目から3件）
        System.out.println("\n年収ランキング2-4位:");
        employees.stream()
                 .sorted(Comparator.comparingDouble(Employee::getSalary).reversed())
                 .skip(1)
                 .limit(3)
                 .forEach(System.out::println);
        
        // 無限Streamでの活用
        System.out.println("\n偶数の5つ目から10個:");
        Stream.iterate(0, n -> n + 2)
              .skip(4)
              .limit(10)
              .forEach(n -> System.out.print(n + " "));
        System.out.println();
    }
    
    // 7. peek - デバッグとモニタリング
    public static void demonstratePeek() {
        System.out.println("\n=== peek() - デバッグ・モニタリング ===");
        
        List<String> words = Arrays.asList("apple", "banana", "cherry", "date");
        
        // 処理パイプラインのデバッグ
        System.out.println("処理パイプラインの監視:");
        long count = words.stream()
                         .peek(word -> System.out.println("入力: " + word))
                         .filter(word -> word.length() > 4)
                         .peek(word -> System.out.println("フィルタ通過: " + word))
                         .map(String::toUpperCase)
                         .peek(word -> System.out.println("大文字変換: " + word))
                         .count();
        
        System.out.println("最終結果件数: " + count);
    }
    
    // 8. 中間操作のチェイニング例
    public static void demonstrateChaining() {
        System.out.println("\n=== 中間操作のチェイニング ===");
        
        List<Employee> employees = createEmployees();
        
        // 複雑な処理パイプライン
        System.out.println("IT部門の高スキル者（3スキル以上）の年収昇順:");
        employees.stream()
                 .filter(emp -> "IT".equals(emp.getDepartment()))          // IT部門のみ
                 .filter(emp -> emp.getSkills().size() >= 3)               // 3スキル以上
                 .sorted(Comparator.comparingDouble(Employee::getSalary))   // 年収昇順
                 .map(emp -> String.format("%s: ¥%.0f (%dスキル)", 
                                         emp.getName(), emp.getSalary(), emp.getSkills().size()))
                 .forEach(System.out::println);
    }
    
    // サンプルデータ作成
    private static List<Employee> createEmployees() {
        return Arrays.asList(
            new Employee("田中", "IT", 28, 4500000, Arrays.asList("Java", "Python", "SQL")),
            new Employee("佐藤", "営業", 35, 5200000, Arrays.asList("営業", "プレゼン")),
            new Employee("鈴木", "IT", 42, 7800000, Arrays.asList("Java", "AWS", "Docker", "Kubernetes")),
            new Employee("高橋", "HR", 29, 4200000, Arrays.asList("採用", "研修")),
            new Employee("山田", "IT", 31, 5800000, Arrays.asList("Python", "機械学習", "データ分析")),
            new Employee("中村", "営業", 38, 6100000, Arrays.asList("営業", "マーケティング", "企画")),
            new Employee("小林", "IT", 26, 4000000, Arrays.asList("JavaScript", "React", "Node.js"))
        );
    }
    
    public static void main(String[] args) {
        demonstrateFilter();
        demonstrateMap();
        demonstrateFlatMap();
        demonstrateDistinct();
        demonstrateSorted();
        demonstrateLimitAndSkip();
        demonstratePeek();
        demonstrateChaining();
    }
}
```

**中間操作の重要な特徴：**

1. **遅延評価**：終端操作が呼ばれるまで実際の処理は実行されません。
2. **チェイン可能**：複数の中間操作を連続して適用できます。
3. **不変性**：元のStreamを変更せず、新しいStreamを返します。
4. **最適化**：JVMがパイプライン全体を最適化できます。

## 10.4 終端操作（Terminal Operations）

終端操作は、Streamパイプラインを完了し、結果を生成する操作です。終端操作が呼ばれた時点で、遅延評価されていた中間操作が実際に実行されます。

### 主要な終端操作の実践的活用

```java
import java.util.*;
import java.util.stream.*;
import static java.util.stream.Collectors.*;

/**
 * Stream終端操作の包括的デモンストレーション
 * collect, forEach, reduce, find, match, count等の実用例
 */
public class TerminalOperationsDemo {
    
    // サンプルデータクラス（再利用）
    static class Product {
        private String name;
        private String category;
        private double price;
        private int stock;
        private double rating;
        
        public Product(String name, String category, double price, int stock, double rating) {
            this.name = name;
            this.category = category;
            this.price = price;
            this.stock = stock;
            this.rating = rating;
        }
        
        // ゲッターメソッド
        public String getName() { return name; }
        public String getCategory() { return category; }
        public double getPrice() { return price; }
        public int getStock() { return stock; }
        public double getRating() { return rating; }
        
        @Override
        public String toString() {
            return String.format("%s(¥%.0f, 在庫:%d, 評価:%.1f)", name, price, stock, rating);
        }
    }
    
    // 1. forEach と forEachOrdered
    public static void demonstrateForEach() {
        System.out.println("=== forEach() & forEachOrdered() ===");
        
        List<Product> products = createProducts();
        
        // 基本的なforEach
        System.out.println("全商品一覧:");
        products.stream()
               .forEach(System.out::println);
        
        // 並列処理での順序保証
        System.out.println("\n並列処理での順序保証:");
        products.parallelStream()
               .forEachOrdered(product -> 
                   System.out.println("処理中: " + product.getName()));
        
        // 条件付きforEach
        System.out.println("\n高評価商品（4.0以上）:");
        products.stream()
               .filter(p -> p.getRating() >= 4.0)
               .forEach(product -> 
                   System.out.println("おすすめ: " + product.getName()));
    }
    
    // 2. collect - 最も強力な終端操作
    public static void demonstrateCollect() {
        System.out.println("\n=== collect() - 収集操作 ===");
        
        List<Product> products = createProducts();
        
        // 基本的なコレクション収集
        List<String> productNames = products.stream()
                                          .map(Product::getName)
                                          .collect(toList());
        System.out.println("商品名リスト: " + productNames);
        
        Set<String> categories = products.stream()
                                       .map(Product::getCategory)
                                       .collect(toSet());
        System.out.println("カテゴリ一覧: " + categories);
        
        // 文字列結合
        String allNames = products.stream()
                                .map(Product::getName)
                                .collect(joining(", "));
        System.out.println("全商品名: " + allNames);
        
        // グループ化
        Map<String, List<Product>> productsByCategory = products.stream()
                                                              .collect(groupingBy(Product::getCategory));
        System.out.println("\nカテゴリ別商品:");
        productsByCategory.forEach((category, productList) -> {
            System.out.println(category + ": " + productList.size() + "商品");
        });
        
        // 複雑な集約
        Map<String, Double> avgPriceByCategory = products.stream()
                                                       .collect(groupingBy(
                                                           Product::getCategory,
                                                           averagingDouble(Product::getPrice)
                                                       ));
        System.out.println("\nカテゴリ別平均価格:");
        avgPriceByCategory.forEach((category, avgPrice) -> 
            System.out.printf("%s: ¥%.0f%n", category, avgPrice));
        
        // 分割
        Map<Boolean, List<Product>> expensivePartition = products.stream()
                                                               .collect(partitioningBy(p -> p.getPrice() > 50000));
        System.out.println("\n高額商品数: " + expensivePartition.get(true).size());
        System.out.println("一般商品数: " + expensivePartition.get(false).size());
        
        // 統計情報の収集
        DoubleSummaryStatistics priceStats = products.stream()
                                                    .collect(summarizingDouble(Product::getPrice));
        System.out.printf("\n価格統計 - 平均:¥%.0f, 最高:¥%.0f, 最低:¥%.0f%n",
                         priceStats.getAverage(), priceStats.getMax(), priceStats.getMin());
    }
    
    // 3. reduce - 削減操作
    public static void demonstrateReduce() {
        System.out.println("\n=== reduce() - 削減操作 ===");
        
        List<Product> products = createProducts();
        
        // 総在庫数の計算
        int totalStock = products.stream()
                               .mapToInt(Product::getStock)
                               .reduce(0, Integer::sum);
        System.out.println("総在庫数: " + totalStock);
        
        // 最高価格商品の検索
        Optional<Product> mostExpensive = products.stream()
                                                .reduce((p1, p2) -> p1.getPrice() > p2.getPrice() ? p1 : p2);
        mostExpensive.ifPresent(product -> 
            System.out.println("最高価格商品: " + product.getName() + " (¥" + product.getPrice() + ")"));
        
        // 商品名の連結（カスタムreduce）
        Optional<String> combinedNames = products.stream()
                                               .map(Product::getName)
                                               .reduce((name1, name2) -> name1 + " & " + name2);
        combinedNames.ifPresent(names -> System.out.println("結合商品名: " + names));
        
        // 複雑なreduce：加重平均評価の計算
        double weightedRating = products.stream()
                                      .mapToDouble(p -> p.getRating() * p.getStock())
                                      .sum() / 
                                products.stream()
                                      .mapToInt(Product::getStock)
                                      .sum();
        System.out.printf("在庫加重平均評価: %.2f%n", weightedRating);
    }
    
    // 4. find操作
    public static void demonstrateFind() {
        System.out.println("\n=== find操作 ===");
        
        List<Product> products = createProducts();
        
        // 条件に合う最初の要素
        Optional<Product> firstElectronics = products.stream()
                                                   .filter(p -> "電子機器".equals(p.getCategory()))
                                                   .findFirst();
        firstElectronics.ifPresent(product -> 
            System.out.println("最初の電子機器: " + product.getName()));
        
        // 任意の要素（並列処理で有効）
        Optional<Product> anyHighRated = products.parallelStream()
                                                .filter(p -> p.getRating() >= 4.5)
                                                .findAny();
        anyHighRated.ifPresent(product -> 
            System.out.println("高評価商品の一つ: " + product.getName()));
        
        // 見つからない場合の処理
        Optional<Product> veryExpensive = products.stream()
                                                .filter(p -> p.getPrice() > 1000000)
                                                .findFirst();
        System.out.println("100万円超商品: " + 
                          veryExpensive.map(Product::getName).orElse("該当なし"));
    }
    
    // 5. match操作
    public static void demonstrateMatch() {
        System.out.println("\n=== match操作 ===");
        
        List<Product> products = createProducts();
        
        // いずれかが条件を満たすか
        boolean hasExpensive = products.stream()
                                     .anyMatch(p -> p.getPrice() > 100000);
        System.out.println("10万円超の商品あり: " + hasExpensive);
        
        // すべてが条件を満たすか
        boolean allInStock = products.stream()
                                   .allMatch(p -> p.getStock() > 0);
        System.out.println("全商品在庫あり: " + allInStock);
        
        // いずれも条件を満たさないか
        boolean noLowRated = products.stream()
                                   .noneMatch(p -> p.getRating() < 2.0);
        System.out.println("低評価商品なし（評価2.0未満）: " + noLowRated);
        
        // 複雑な条件でのマッチ
        boolean hasPopularAffordable = products.stream()
                                             .anyMatch(p -> p.getRating() >= 4.0 && p.getPrice() <= 30000);
        System.out.println("人気かつ手頃な商品あり（評価4.0以上、3万円以下）: " + hasPopularAffordable);
    }
    
    // 6. count操作
    public static void demonstrateCount() {
        System.out.println("\n=== count操作 ===");
        
        List<Product> products = createProducts();
        
        // 基本的なカウント
        long totalProducts = products.stream().count();
        System.out.println("総商品数: " + totalProducts);
        
        // 条件付きカウント
        long electronicsCount = products.stream()
                                      .filter(p -> "電子機器".equals(p.getCategory()))
                                      .count();
        System.out.println("電子機器商品数: " + electronicsCount);
        
        long highValueCount = products.stream()
                                    .filter(p -> p.getPrice() > 50000)
                                    .count();
        System.out.println("高額商品数（5万円超）: " + highValueCount);
        
        // カテゴリ別カウント
        System.out.println("\nカテゴリ別商品数:");
        products.stream()
               .collect(groupingBy(Product::getCategory, counting()))
               .forEach((category, count) -> 
                   System.out.println(category + ": " + count + "商品"));
    }
    
    // 7. 統計操作（数値Stream専用）
    public static void demonstrateStatistics() {
        System.out.println("\n=== 統計操作 ===");
        
        List<Product> products = createProducts();
        
        // 価格の統計
        IntSummaryStatistics priceStats = products.stream()
                                                .mapToInt(p -> (int)p.getPrice())
                                                .summaryStatistics();
        
        System.out.println("価格統計:");
        System.out.println("  件数: " + priceStats.getCount());
        System.out.println("  合計: ¥" + priceStats.getSum());
        System.out.println("  平均: ¥" + String.format("%.0f", priceStats.getAverage()));
        System.out.println("  最大: ¥" + priceStats.getMax());
        System.out.println("  最小: ¥" + priceStats.getMin());
        
        // 在庫の統計
        OptionalDouble avgStock = products.stream()
                                        .mapToInt(Product::getStock)
                                        .average();
        avgStock.ifPresent(avg -> System.out.printf("平均在庫数: %.1f個%n", avg));
        
        // 評価の最大値
        OptionalDouble maxRating = products.stream()
                                         .mapToDouble(Product::getRating)
                                         .max();
        maxRating.ifPresent(max -> System.out.printf("最高評価: %.1f%n", max));
    }
    
    // サンプルデータ作成
    private static List<Product> createProducts() {
        return Arrays.asList(
            new Product("ノートパソコン", "電子機器", 89800, 15, 4.2),
            new Product("マウス", "電子機器", 2800, 50, 4.0),
            new Product("キーボード", "電子機器", 8500, 30, 4.1),
            new Product("デスク", "家具", 25000, 8, 3.8),
            new Product("チェア", "家具", 45000, 12, 4.5),
            new Product("Java入門書", "書籍", 3200, 25, 4.3),
            new Product("プログラミング本", "書籍", 4800, 20, 4.0),
            new Product("モニター", "電子機器", 35200, 10, 4.4),
            new Product("スピーカー", "電子機器", 12800, 18, 3.9),
            new Product("本棚", "家具", 18000, 6, 3.7)
        );
    }
    
    public static void main(String[] args) {
        demonstrateForEach();
        demonstrateCollect();
        demonstrateReduce();
        demonstrateFind();
        demonstrateMatch();
        demonstrateCount();
        demonstrateStatistics();
    }
}
```

**終端操作の重要な特徴：**

1. **即座実行**：終端操作が呼ばれた時点で、すべての遅延評価されていた操作が実行されます。
2. **Stream消費**：終端操作後、そのStreamは使用不可になります。
3. **結果生成**：具体的な値やコレクションを生成します。
4. **パフォーマンス最適化**：JVMがパイプライン全体を最適化して実行します。

## 10.5 並列ストリーム（Parallel Streams）

並列ストリームは、マルチコアプロセッサの能力を活用して、データ処理を並列化する強力な機能です。適切に使用することで、大量のデータ処理において劇的な性能向上を実現できます。

### 並列ストリームの基本と注意点

```java
import java.util.*;
import java.util.stream.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * 並列ストリームの包括的デモンストレーション
 * 性能比較、適切な使用場面、注意点の実証
 */
public class ParallelStreamDemo {
    
    // 1. 基本的な並列処理の比較
    public static void demonstrateBasicParallel() {
        System.out.println("=== 基本的な並列処理の比較 ===");
        
        // 大量データの生成
        List<Integer> largeDataset = IntStream.rangeClosed(1, 10_000_000)
                                            .boxed()
                                            .collect(Collectors.toList());
        
        // CPU集約的な処理関数
        Function<Integer, Double> heavyComputation = n -> {
            // 重い計算のシミュレーション
            double result = 0;
            for (int i = 0; i < 100; i++) {
                result += Math.sqrt(n * i);
            }
            return result;
        };
        
        // シーケンシャル処理
        long sequentialStart = System.currentTimeMillis();
        double sequentialSum = largeDataset.stream()
                                         .mapToDouble(heavyComputation::apply)
                                         .sum();
        long sequentialTime = System.currentTimeMillis() - sequentialStart;
        
        // 並列処理
        long parallelStart = System.currentTimeMillis();
        double parallelSum = largeDataset.parallelStream()
                                       .mapToDouble(heavyComputation::apply)
                                       .sum();
        long parallelTime = System.currentTimeMillis() - parallelStart;
        
        System.out.printf("シーケンシャル: %.2f (時間: %dms)%n", sequentialSum, sequentialTime);
        System.out.printf("並列処理: %.2f (時間: %dms)%n", parallelSum, parallelTime);
        System.out.printf("性能向上: %.2f倍%n", (double)sequentialTime / parallelTime);
        System.out.printf("使用可能プロセッサ数: %d%n", Runtime.getRuntime().availableProcessors());
    }
    
    // 2. 並列処理が効果的な場面
    public static void demonstrateEffectiveParallel() {
        System.out.println("\n=== 並列処理が効果的な場面 ===");
        
        // 大量データのフィルタリングと変換
        List<String> words = generateLargeWordList();
        
        long start = System.currentTimeMillis();
        List<String> sequentialResult = words.stream()
                                           .filter(word -> word.length() > 5)
                                           .map(String::toUpperCase)
                                           .filter(word -> word.contains("A"))
                                           .collect(Collectors.toList());
        long sequentialTime = System.currentTimeMillis() - start;
        
        start = System.currentTimeMillis();
        List<String> parallelResult = words.parallelStream()
                                         .filter(word -> word.length() > 5)
                                         .map(String::toUpperCase)
                                         .filter(word -> word.contains("A"))
                                         .collect(Collectors.toList());
        long parallelTime = System.currentTimeMillis() - start;
        
        System.out.printf("大量データ処理 - シーケンシャル: %dms, 並列: %dms%n", 
                         sequentialTime, parallelTime);
        System.out.printf("結果一致: %s%n", sequentialResult.equals(parallelResult));
        System.out.printf("性能向上: %.2f倍%n", (double)sequentialTime / parallelTime);
    }
    
    // 3. 並列処理で注意が必要な場面
    public static void demonstrateParallelPitfalls() {
        System.out.println("\n=== 並列処理の注意点 ===");
        
        // 副作用のある操作（危険な例）
        List<Integer> numbers = IntStream.rangeClosed(1, 1000).boxed().collect(Collectors.toList());
        List<Integer> dangerousResult = new ArrayList<>();
        
        // 【危険】：ArrayListは並列環境でスレッドセーフではない
        System.out.println("危険な並列処理の例（結果が不定）:");
        numbers.parallelStream()
               .filter(n -> n % 2 == 0)
               .forEach(dangerousResult::add);  // スレッドセーフでない操作
        
        System.out.println("危険な方法での結果数: " + dangerousResult.size() + " (正解: 500)");
        
        // 【安全】：Collectorsを使用
        List<Integer> safeResult = numbers.parallelStream()
                                        .filter(n -> n % 2 == 0)
                                        .collect(Collectors.toList());
        System.out.println("安全な方法での結果数: " + safeResult.size());
        
        // 【安全】：同期化されたコレクション
        List<Integer> synchronizedResult = Collections.synchronizedList(new ArrayList<>());
        numbers.parallelStream()
               .filter(n -> n % 2 == 0)
               .forEach(synchronizedResult::add);
        System.out.println("同期化リストでの結果数: " + synchronizedResult.size());
        
        // AtomicIntegerを使った並列カウンタ
        AtomicInteger atomicCounter = new AtomicInteger(0);
        numbers.parallelStream()
               .filter(n -> n % 2 == 0)
               .forEach(n -> atomicCounter.incrementAndGet());
        System.out.println("AtomicIntegerでのカウント: " + atomicCounter.get());
    }
    
    // 4. 並列処理の性能特性
    public static void demonstrateParallelPerformance() {
        System.out.println("\n=== 並列処理の性能特性 ===");
        
        // データサイズによる性能差
        int[] dataSizes = {1000, 10000, 100000, 1000000};
        
        for (int size : dataSizes) {
            List<Integer> data = IntStream.rangeClosed(1, size).boxed().collect(Collectors.toList());
            
            // 軽い処理（並列化のオーバーヘッドが目立つ場合）
            long sequentialTime = measureTime(() -> 
                data.stream().mapToInt(n -> n * 2).sum());
            
            long parallelTime = measureTime(() -> 
                data.parallelStream().mapToInt(n -> n * 2).sum());
            
            System.out.printf("データ数 %,7d: シーケンシャル %3dms, 並列 %3dms (%.2f倍)%n",
                             size, sequentialTime, parallelTime, 
                             (double)sequentialTime / parallelTime);
        }
        
        // 重い処理での比較
        System.out.println("\n重い処理での比較:");
        List<Integer> data = IntStream.rangeClosed(1, 100000).boxed().collect(Collectors.toList());
        
        Function<Integer, Double> heavyProcess = n -> {
            double result = 0;
            for (int i = 0; i < 1000; i++) {
                result += Math.sqrt(n * i);
            }
            return result;
        };
        
        long heavySequential = measureTime(() -> 
            data.stream().mapToDouble(heavyProcess::apply).sum());
        
        long heavyParallel = measureTime(() -> 
            data.parallelStream().mapToDouble(heavyProcess::apply).sum());
        
        System.out.printf("重い処理: シーケンシャル %dms, 並列 %dms (%.2f倍)%n",
                         heavySequential, heavyParallel, 
                         (double)heavySequential / heavyParallel);
    }
    
    // 5. カスタム並列処理の設定
    public static void demonstrateCustomParallel() {
        System.out.println("\n=== カスタム並列処理設定 ===");
        
        // デフォルトのForkJoinPoolサイズ
        System.out.println("デフォルト並列度: " + ForkJoinPool.commonPool().getParallelism());
        
        // カスタムForkJoinPoolでの実行
        int customParallelism = 2;
        ForkJoinPool customThreadPool = new ForkJoinPool(customParallelism);
        
        List<Integer> data = IntStream.rangeClosed(1, 1000).boxed().collect(Collectors.toList());
        
        try {
            List<Integer> result = customThreadPool.submit(() ->
                data.parallelStream()
                    .filter(n -> n % 2 == 0)
                    .map(n -> n * n)
                    .collect(Collectors.toList())
            ).get();
            
            System.out.println("カスタムプール（並列度" + customParallelism + "）での処理完了");
            System.out.println("処理結果数: " + result.size());
            
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            customThreadPool.shutdown();
        }
        
        // システムプロパティでの設定例
        System.out.println("\nシステムプロパティ設定例:");
        System.out.println("-Djava.util.concurrent.ForkJoinPool.common.parallelism=4");
    }
    
    // 6. 並列処理での順序制御
    public static void demonstrateParallelOrdering() {
        System.out.println("\n=== 並列処理での順序制御 ===");
        
        List<Integer> numbers = IntStream.rangeClosed(1, 20).boxed().collect(Collectors.toList());
        
        // 順序が保証されない並列forEach
        System.out.println("parallelStream().forEach (順序不定):");
        numbers.parallelStream()
               .map(n -> n * 2)
               .forEach(n -> System.out.print(n + " "));
        System.out.println();
        
        // 順序が保証されるforEachOrdered
        System.out.println("parallelStream().forEachOrdered (順序保証):");
        numbers.parallelStream()
               .map(n -> n * 2)
               .forEachOrdered(n -> System.out.print(n + " "));
        System.out.println();
        
        // collect操作では順序が保たれる
        List<Integer> collectedResult = numbers.parallelStream()
                                             .map(n -> n * 2)
                                             .collect(Collectors.toList());
        System.out.println("collect結果: " + collectedResult);
    }
    
    // 7. 並列ストリーム使用のガイドライン
    public static void demonstrateUsageGuidelines() {
        System.out.println("\n=== 並列ストリーム使用ガイドライン ===");
        
        System.out.println("並列処理が効果的な場面:");
        System.out.println("1. 大量データ（数万〜数百万件以上）の処理");
        System.out.println("2. CPU集約的な処理（計算、変換等）");
        System.out.println("3. 副作用のない純粋な関数型操作");
        System.out.println("4. データの独立性が保たれた処理");
        
        System.out.println("\n並列処理を避けるべき場面:");
        System.out.println("1. 小さなデータセット（数千件以下）");
        System.out.println("2. I/O待機の多い処理");
        System.out.println("3. 共有状態を変更する処理");
        System.out.println("4. 順序に依存する処理");
        System.out.println("5. デバッグが困難な場面");
        
        // 実際の判断基準例
        List<String> smallDataset = Arrays.asList("a", "b", "c", "d", "e");
        List<String> largeDataset = generateLargeWordList();
        
        // 小さなデータでは並列化しない方が良い例
        long smallSequential = measureTime(() -> 
            smallDataset.stream().map(String::toUpperCase).collect(Collectors.toList()));
        long smallParallel = measureTime(() -> 
            smallDataset.parallelStream().map(String::toUpperCase).collect(Collectors.toList()));
        
        System.out.printf("\n小データ（%d件）: シーケンシャル %dms, 並列 %dms%n",
                         smallDataset.size(), smallSequential, smallParallel);
        
        // 大きなデータでは並列化が効果的な例
        long largeSequential = measureTime(() -> 
            largeDataset.stream().filter(s -> s.length() > 5).collect(Collectors.toList()));
        long largeParallel = measureTime(() -> 
            largeDataset.parallelStream().filter(s -> s.length() > 5).collect(Collectors.toList()));
        
        System.out.printf("大データ（%,d件）: シーケンシャル %dms, 並列 %dms%n",
                         largeDataset.size(), largeSequential, largeParallel);
    }
    
    // ユーティリティメソッド
    private static long measureTime(Runnable task) {
        long start = System.currentTimeMillis();
        task.run();
        return System.currentTimeMillis() - start;
    }
    
    private static List<String> generateLargeWordList() {
        Random random = new Random(42); // 再現可能な結果のため
        return IntStream.range(0, 1_000_000)
                       .mapToObj(i -> generateRandomWord(random, 3 + random.nextInt(15)))
                       .collect(Collectors.toList());
    }
    
    private static String generateRandomWord(Random random, int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append((char)('a' + random.nextInt(26)));
        }
        return sb.toString();
    }
    
    public static void main(String[] args) {
        demonstrateBasicParallel();
        demonstrateEffectiveParallel();
        demonstrateParallelPitfalls();
        demonstrateParallelPerformance();
        demonstrateCustomParallel();
        demonstrateParallelOrdering();
        demonstrateUsageGuidelines();
    }
}
```

**並列ストリームの重要な考慮事項：**

1. **適切なデータサイズ**：小さなデータセットでは並列化のオーバーヘッドが処理時間を上回る場合があります。
2. **CPU集約的処理**：I/O待機が多い処理では並列化の効果は限定的です。
3. **副作用の回避**：並列処理では副作用のある操作は避けるべきです。
4. **スレッドセーフティ**：共有リソースへのアクセスは適切に同期化する必要があります。

## 10.6 専用プリミティブストリーム

Javaでは、ボクシング/アンボクシングのオーバーヘッドを避けるため、プリミティブ型専用のストリームが提供されています。

### IntStream、LongStream、DoubleStreamの活用

```java
import java.util.*;
import java.util.stream.*;
import java.util.function.*;

/**
 * プリミティブストリームの包括的デモンストレーション
 * 性能比較、専用メソッド、実用的な活用例
 */
public class PrimitiveStreamDemo {
    
    // 1. 基本的なプリミティブストリーム操作
    public static void demonstrateBasicPrimitiveStreams() {
        System.out.println("=== 基本的なプリミティブストリーム ===");
        
        // IntStream の基本操作
        System.out.println("IntStream の基本操作:");
        IntStream.rangeClosed(1, 10)
                 .filter(n -> n % 2 == 0)
                 .map(n -> n * n)
                 .forEach(n -> System.out.print(n + " "));
        System.out.println();
        
        // LongStream の大きな数値処理
        System.out.println("LongStream での大きな数値:");
        long factorial = LongStream.rangeClosed(1, 20)
                                  .reduce(1, (a, b) -> a * b);
        System.out.println("20! = " + factorial);
        
        // DoubleStream の数学的計算
        System.out.println("DoubleStream での数学計算:");
        double average = DoubleStream.of(1.1, 2.2, 3.3, 4.4, 5.5)
                                   .average()
                                   .orElse(0.0);
        System.out.printf("平均値: %.2f%n", average);
        
        // 統計情報の取得
        IntSummaryStatistics stats = IntStream.rangeClosed(1, 100)
                                            .summaryStatistics();
        System.out.println("1-100の統計:");
        System.out.printf("  合計: %d, 平均: %.2f, 最大: %d, 最小: %d%n",
                         stats.getSum(), stats.getAverage(), 
                         stats.getMax(), stats.getMin());
    }
    
    // 2. 生成メソッドの活用
    public static void demonstrateGenerationMethods() {
        System.out.println("\n=== ストリーム生成メソッド ===");
        
        // range と rangeClosed
        System.out.println("range(1, 5): ");
        IntStream.range(1, 5).forEach(n -> System.out.print(n + " "));
        System.out.println();
        
        System.out.println("rangeClosed(1, 5): ");
        IntStream.rangeClosed(1, 5).forEach(n -> System.out.print(n + " "));
        System.out.println();
        
        // iterate - 無限ストリーム
        System.out.println("フィボナッチ数列（最初の10項）:");
        IntStream.iterate(1, n -> n <= 100, n -> n + 1)
                 .filter(PrimitiveStreamDemo::isPrime)
                 .limit(10)
                 .forEach(n -> System.out.print(n + " "));
        System.out.println();
        
        // generate - ランダム値生成
        System.out.println("ランダムな整数（5個）:");
        IntStream.generate(() -> (int)(Math.random() * 100))
                 .limit(5)
                 .forEach(n -> System.out.print(n + " "));
        System.out.println();
        
        // Random クラスとの連携
        System.out.println("正規分布に従う乱数（5個）:");
        new Random().doubles(5, 0.0, 1.0)
                   .forEach(d -> System.out.printf("%.3f ", d));
        System.out.println();
    }
    
    // 3. 性能比較：プリミティブ vs オブジェクト
    public static void demonstratePerformanceComparison() {
        System.out.println("\n=== 性能比較：プリミティブストリーム vs オブジェクトストリーム ===");
        
        int size = 10_000_000;
        
        // オブジェクトストリーム（Integer）
        long objectStart = System.currentTimeMillis();
        long objectSum = IntStream.rangeClosed(1, size)
                                 .boxed()  // Integer型に変換
                                 .mapToInt(Integer::intValue)
                                 .asLongStream()
                                 .sum();
        long objectTime = System.currentTimeMillis() - objectStart;
        
        // プリミティブストリーム（int）
        long primitiveStart = System.currentTimeMillis();
        long primitiveSum = IntStream.rangeClosed(1, size)
                                   .asLongStream()
                                   .sum();
        long primitiveTime = System.currentTimeMillis() - primitiveStart;
        
        System.out.printf("データ数: %,d%n", size);
        System.out.printf("オブジェクトストリーム: 合計=%d, 時間=%dms%n", objectSum, objectTime);
        System.out.printf("プリミティブストリーム: 合計=%d, 時間=%dms%n", primitiveSum, primitiveTime);
        System.out.printf("性能向上: %.2f倍%n", (double)objectTime / primitiveTime);
        
        // メモリ使用量の違い
        System.out.println("\nメモリ効率の違い:");
        System.out.println("int[1000000] ≒ 4MB (プリミティブ配列)");
        System.out.println("Integer[1000000] ≒ 16MB以上 (オブジェクト配列 + オーバーヘッド)");
    }
    
    // 4. 実用的な数学計算例
    public static void demonstrateMathematicalOperations() {
        System.out.println("\n=== 実用的な数学計算例 ===");
        
        // 素数の生成
        System.out.println("100以下の素数:");
        IntStream.rangeClosed(2, 100)
                 .filter(PrimitiveStreamDemo::isPrime)
                 .forEach(n -> System.out.print(n + " "));
        System.out.println();
        
        // 平方根の近似計算（ニュートン法）
        System.out.println("\n平方根の近似計算（ニュートン法）:");
        double target = 2.0;
        double sqrt2 = DoubleStream.iterate(1.0, x -> (x + target / x) / 2)
                                  .limit(10)
                                  .reduce((first, second) -> second)
                                  .orElse(0);
        System.out.printf("√2 ≈ %.10f (実際: %.10f)%n", sqrt2, Math.sqrt(2));
        
        // 幾何級数の和
        System.out.println("\n幾何級数の和 (1/2 + 1/4 + 1/8 + ...):");
        double geometricSum = IntStream.rangeClosed(1, 20)
                                     .mapToDouble(n -> 1.0 / Math.pow(2, n))
                                     .sum();
        System.out.printf("部分和: %.10f (理論値: 1.0)%n", geometricSum);
        
        // 円周率の近似（モンテカルロ法）
        System.out.println("\n円周率の近似（モンテカルロ法）:");
        Random random = new Random(42);
        long samples = 1_000_000;
        long insideCircle = random.doubles(samples)
                                 .mapToLong(x -> random.doubles()
                                                      .limit(1)
                                                      .mapToLong(y -> (x*x + y*y <= 1.0) ? 1 : 0)
                                                      .sum())
                                 .sum();
        double piApprox = 4.0 * insideCircle / samples;
        System.out.printf("π ≈ %.6f (実際: %.6f, サンプル数: %,d)%n", 
                         piApprox, Math.PI, samples);
    }
    
    // 5. ビジネスロジックでの活用例
    public static void demonstrateBusinessApplications() {
        System.out.println("\n=== ビジネスロジックでの活用例 ===");
        
        // 売上データの分析
        int[] dailySales = {12000, 15000, 18000, 14000, 16000, 22000, 19000,
                           13000, 17000, 20000, 15000, 18000, 25000, 21000};
        
        IntSummaryStatistics salesStats = Arrays.stream(dailySales)
                                                .summaryStatistics();
        
        System.out.println("売上分析結果:");
        System.out.printf("  総売上: ¥%,d%n", salesStats.getSum());
        System.out.printf("  平均日売上: ¥%,.0f%n", salesStats.getAverage());
        System.out.printf("  最高日売上: ¥%,d%n", salesStats.getMax());
        System.out.printf("  最低日売上: ¥%,d%n", salesStats.getMin());
        
        // 目標達成日数
        int target = 17000;
        long achievedDays = Arrays.stream(dailySales)
                                 .filter(sales -> sales >= target)
                                 .count();
        System.out.printf("  目標達成日数: %d日 (目標: ¥%,d以上)%n", achievedDays, target);
        
        // 成長率の計算
        System.out.println("\n前日比成長率:");
        IntStream.range(1, dailySales.length)
                 .mapToDouble(i -> ((double)(dailySales[i] - dailySales[i-1]) / dailySales[i-1]) * 100)
                 .forEach(rate -> System.out.printf("%.1f%% ", rate));
        System.out.println();
        
        // 移動平均の計算（3日間）
        System.out.println("\n3日移動平均:");
        IntStream.rangeClosed(2, dailySales.length - 1)
                 .mapToDouble(i -> (dailySales[i-2] + dailySales[i-1] + dailySales[i]) / 3.0)
                 .forEach(avg -> System.out.printf("¥%,.0f ", avg));
        System.out.println();
    }
    
    // 6. ストリーム間の変換
    public static void demonstrateStreamConversions() {
        System.out.println("\n=== ストリーム間の変換 ===");
        
        // IntStream → DoubleStream
        System.out.println("IntStream → DoubleStream (平方根):");
        IntStream.rangeClosed(1, 5)
                 .asDoubleStream()
                 .map(Math::sqrt)
                 .forEach(d -> System.out.printf("%.2f ", d));
        System.out.println();
        
        // DoubleStream → IntStream (切り捨て)
        System.out.println("DoubleStream → IntStream (切り捨て):");
        DoubleStream.of(1.1, 2.9, 3.7, 4.2)
                   .mapToInt(d -> (int)d)
                   .forEach(i -> System.out.print(i + " "));
        System.out.println();
        
        // プリミティブ → オブジェクト
        System.out.println("プリミティブ → オブジェクトストリーム:");
        List<String> numbers = IntStream.rangeClosed(1, 5)
                                       .boxed()
                                       .map(n -> "Number: " + n)
                                       .collect(Collectors.toList());
        System.out.println(numbers);
        
        // オブジェクト → プリミティブ
        String[] numberStrings = {"10", "20", "30", "40", "50"};
        int sum = Arrays.stream(numberStrings)
                       .mapToInt(Integer::parseInt)
                       .sum();
        System.out.println("文字列数値の合計: " + sum);
    }
    
    // 7. 特殊な集計操作
    public static void demonstrateSpecialAggregations() {
        System.out.println("\n=== 特殊な集計操作 ===");
