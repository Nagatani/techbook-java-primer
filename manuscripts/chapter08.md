# 第8章 ジェネリクス

## 始めに：型安全性の進化とジェネリックプログラミングの革新

前章まで、オブジェクト指向プログラミングの基本概念と、Javaのコレクションフレームワークについて学習してきました。本章では、現代のJavaプログラミングにおいて必須の技術である「ジェネリクス（Generics）」について詳細に学習します。

ジェネリクスは、単なる構文的な便利機能ではありません。これは、プログラミング言語における「型システム」の進化の成果であり、ソフトウェアの信頼性、保守性、再利用性を根本的に向上させる革新的なしくみです。

### プログラミング言語における型システムの重要性

コンピュータプログラムの本質は「データの変換」です。入力されたデータを処理し、期待される形式で出力することが、すべてのプログラムの基本的な動作です。この過程で最も重要な要素の1つが「型（Type）」の概念です。

型とは、データがどのような性質を持ち、どのような操作が可能かを定義するものです。整数型には加算や減算が可能ですが、文字列の長さを計算することはできません。文字列型には長さの取得や部分文字列の抽出が可能ですが、数値としての演算はできません。このように、型はデータの意味と操作の制約を明確に定義します。

### 静的型付き言語の利点と課題

Javaは「静的型付き言語」として設計されています。これは、変数や関数の引数、戻り値の型がコンパイル時に決定され、チェックされることを意味します。この特性により、以下の重要な利点が得られます：

**実行前のエラー検出**：型に関するエラーの多くは、プログラムが実行される前のコンパイル段階で発見されます。これにより、本番環境でのクラッシュやデータ破損のリスクが大幅に減少します。

**開発効率の向上**：IDEは型情報を活用して、コード補完、リファクタリング支援、エラーハイライトなどの強力な機能を提供できます。開発者は、より正確で効率的なコーディングが可能になります。

**実行性能の最適化**：コンパイラは型情報を元に、より効率的な機械語コードを生成できます。動的型付き言語と比較して、実行時のオーバーヘッドを削減できます。

しかし、従来の静的型付けシステムには重要な制約もありました。それは「1つの型に1つの実装」という原則です。たとえば、文字列を格納するリストと整数を格納するリストは、格納するデータの型が異なるだけで、本質的な動作（要素の追加、削除、検索など）は同じです。しかし、従来のシステムでは、これらを別々のクラスとして実装する必要がありました。

### コード重複問題と車輪の再発明

Java 5（2004年）以前の時代、開発者たちは深刻な「コード重複問題」に直面していました。データ構造やアルゴリズムの実装において、扱うデータの型が異なるだけで、ほぼ同一のコードを何度も書く必要があったのです。

たとえば、スタック（LIFO: Last In, First Out）データ構造を考えてみましょう。文字列用のスタック、整数用のスタック、日付用のスタックなど、型ごとに別々のクラスを実装する必要がありました。これらのクラスの内部実装は、型の宣言部分を除けばほぼ同一ですが、コードの共有はできませんでした。

この問題は、以下のような深刻な課題を引き起こしていました：

**保守コストの増大**：アルゴリズムの改善やバグ修正が必要になった場合、すべての型用のクラスを個別に修正する必要があり、修正漏れや新たなバグの原因となっていました。

**テストコストの増大**：本質的に同じロジックであっても、型ごとに個別のテストが必要で、テスト工数が型の数に比例して増加していました。

**品質の不一致**：同じアルゴリズムでも、実装者や実装時期により、性能や信頼性に差が生まれることがありました。

**学習コストの増大**：開発者は、本質的に同じ概念であっても、型ごとに異なるAPIを覚える必要がありました。

### Object型による汎用化の試みとその限界

この問題を解決する初期の試みとして、Java言語では「Object型」を使用した汎用化が行われました。Javaにおいて、すべてのクラスは暗黙的にObjectクラスを継承するため、Object型の変数にはあらゆるオブジェクトを格納できます。

この特性を活用して、「Object型を要素とするコレクション」を作成することで、1つの実装で任意の型のオブジェクトを扱えるコレクションが実現されました。Java 5以前のArrayListやHashMapは、まさにこの方式で実装されていました。

しかし、Object型による汎用化には重大な欠陥がありました：

**型安全性の喪失**：Object型のコレクションには、意図しない型のオブジェクトが混入する可能性があり、実行時に予期しないClassCastExceptionが発生するリスクがありました。

**明示的な型キャストの必要性**：Object型から具体的な型への変換には、明示的なキャストが必要で、コードが冗長になり、可読性が低下していました。

**性能上のオーバーヘッド**：基本データ型（int、double等）は、Object型で扱うためにラッパクラス（Integer、Double等）でのボクシングが必要で、メモリ使用量と実行時間のオーバーヘッドが発生していました。

**コンパイル時の型チェック不能**：型に関するエラーは、コンパイル時ではなく実行時に初めて発見され、デバッグが困難で、本番環境での障害リスクが高まっていました。

### ジェネリックプログラミングの概念

これらの課題を根本的に解決するため、計算機科学の分野では「ジェネリックプログラミング（Generic Programming）」という概念が研究されてきました。ジェネリックプログラミングは、「型に依存しない汎用的なアルゴリズムやデータ構造を定義する」プログラミングパラダイムです。

この概念は、1970年代のAda言語における「generic unit」に始まり、1980年代のC++の「template」、1990年代のメーリングリスト言語の「parametric polymorphism」など、さまざまな言語で実装されてきました。これらの研究成果を踏まえ、Java言語にも2004年にジェネリクスが導入されました。

ジェネリックプログラミングの核心的な思想は、「アルゴリズムの本質と型の詳細を分離する」ことです。リストの要素を追加するアルゴリズムは、要素がStringであってもIntegerであっても本質的に同じです。ジェネリクスは、この本質的な同一性を言語レベルで表現し、型安全性を保ちながら高度なコードの再利用を実現します。

### Javaジェネリクスの革新的特徴

Java 5で導入されたジェネリクスは、ほか言語の実装を参考にしながらも、Java独自の優れた特徴を持っています：

**型消去（Type Erasure）による後方互換性**：ジェネリクスの型情報は、コンパイル時のチェック後に「消去」され、実行時には従来のObject型ベースのコードとして動作します。これにより、既存のライブラリやフレームワークとの完全な互換性が保たれました。

**境界型パラメータ（Bounded Type Parameters）**：型パラメータに制約を設けることで、特定のインターフェイスを実装した型や、特定のクラスを継承した型のみを受け入れるジェネリクスを定義できます。これにより、型安全性を保ちながら、より具体的な操作が可能になります。

**ワイルドカード型**：「? extends Type」や「? super Type」といったワイルドカード記法により、柔軟な型関係を表現できます。これは、共変性と反変性という型理論の高度な概念をプログラマにとって理解しやすい形で実現しています。

**型推論の自動化**：Java 7のダイヤモンド演算子、Java 8以降のラムダ式での型推論など、ジェネリクスの記述を簡潔にする機能が継続的に強化されています。

### 現代ソフトウェア開発におけるジェネリクスの重要性

現代のソフトウェア開発において、ジェネリクスは単なる言語機能を超えて、設計思想の基盤となっています。その重要性は以下の点に現れています：

**フレームワーク設計の基盤**：SpringフレームワークやJakarta EEなど、多くのJavaフレームワークは、ジェネリクスを活用して型安全で柔軟なAPIを提供しています。依存性注入やO/Rマッパマッピングなど、複雑な機能も、ジェネリクスにより簡潔で理解しやすいインターフェイスで提供されています。

**関数型プログラミングとの統合**：Java 8で導入されたStream APIやOptionalクラスは、ジェネリクスと関数型プログラミングの概念を融合させ、従来のJavaでは困難だった宣言的なプログラミングスタイルを実現しています。

**マイクロサービスアーキテクチャでの活用**：異なるサービス間でのデータ交換において、ジェネリクスを活用したDTOやAPIクライアントにより、型安全な通信が実現されています。

**ビッグデータ処理での活用**：Apache SparkやHadoopなどのビッグデータ処理フレームワークでは、ジェネリクスを活用して大量のデータを型安全かつ効率的に処理するAPIが提供されています。

### 本章で学習する内容の意義

本章では、これらの歴史的背景と技術的意義を踏まえて、Javaのジェネリクスを体系的に学習していきます。単にジェネリクスの記法を覚えるのではなく、以下の点を重視して学習を進めます：

**型安全性の理解**：なぜ型安全性が重要なのか、ジェネリクスがどのようにしてこれを実現するのかを理解し、より信頼性の高いプログラムを作成する能力を身につけます。

**適切な抽象化の技術**：具体的な型に依存しない汎用的なコンポーネントを設計する技術を習得し、再利用性の高いソフトウェアを作成する能力を養います。

**パフォーマンスの考慮**：型消去のしくみを理解し、ジェネリクスが実行時性能に与える影響を考慮したプログラムを作成する能力を身につけます。

**実践的な設計パターン**：境界型パラメータやワイルドカード型など、高度なジェネリクスの機能を活用した実践的な設計パターンを習得します。

**現代的なJava機能との統合**：Stream API、Optional、関数型インターフェイスなど、現代的なJava機能とジェネリクスの組み合わせ方を学習し、より表現力豊かなプログラムを作成する能力を身につけます。

ジェネリクスを深く理解することは、Javaプログラマとしての技術レベルを大幅に向上させ、保守性が高く、拡張性に優れた高品質なソフトウェアの開発能力を身につけることにつながります。また、ほかのプログラミング言語の型システムを理解するもと伴り、プログラマとしての視野を大きく広げることができるでしょう。

本章では、Javaのジェネリクス（総称型、Generics）について詳しく学習します。ジェネリクスは、Javaプログラミング言語における強力な機能の1つで、クラスやメソッドがさまざまなデータ型を扱えるようにするしくみです。これにより、作成するクラスの再利用性が向上し、型安全性が保証されます。

## 8.1 ジェネリクスとは

### ジェネリクスの概念

ジェネリクスは、クラスやメソッドが利用するデータ型を、それらを使用する側で指定できるようにするしくみです。これにより、特定のデータ型に依存しない、汎用的なコンポーネントを定義できます。

ジェネリクスクラスやジェネリックメソッドでは、具体的な型を指定する代わりに型パラメータ（type parameter）を使用します。この型パラメータがあるおかげで、異なるデータ型に対して同じロジックを適用でき、コードの重複を減らすことができます。

### 基本構文

ジェネリクスを使用するクラスやインターフェイス、メソッドは、型パラメータを山括弧（`<>`）で囲んで宣言します。

```java
// ジェネリクスクラスの基本的な構文
public class Box<T> { // T は型パラメータ（慣習的に大文字1文字が使われる）
    private T content; // T型のフィールド

    // T型の引数を取るメソッド
    public void setContent(T content) {
        this.content = content;
    }

    // T型の値を返すメソッド
    public T getContent() {
        return content;
    }
}
```

ここで、`T` は型パラメータであり、このクラスのインスタンスが生成される際に具体的な型（例： `String`, `Integer`など）に置き換えられます。

### ジェネリクスの利点

ジェネリクスを導入することには、以下のような重要な利点があります：

1. **型安全性の向上**: コンパイラがコンパイル時に型チェックを行い、実行時エラーを防ぐ
2. **再利用性の向上**: 一度定義すれば、さまざまな型に対して再利用できる
3. **コードの簡潔性**: 型キャストが不要になり、コードがより読みやすくなる

### ジェネリクス使用前と使用後の比較

```java
import java.util.*;

public class GenericsComparison {
    public static void main(String[] args) {
        // ======= ジェネリクス使用前（Java 5以前）の問題点 =======
        List oldList = new ArrayList();  // 型パラメータなし
        oldList.add("Hello");
        oldList.add(123);        // 異なる型も追加可能（問題の源）
        oldList.add("World");
        
        // 要素を取り出す際は型キャストが必要
        String str1 = (String) oldList.get(0);  // "Hello" - OK
        // String str2 = (String) oldList.get(1);  // ClassCastException！実行時エラー
        
        System.out.println("Old style: " + str1);
        
        // ======= ジェネリクス使用後（Java 5以降）の改善点 =======
        List<String> stringList = new ArrayList<>();  // String型専用
        stringList.add("Hello");
        stringList.add("World");
        // stringList.add(123);  // コンパイルエラー！型安全性が保証される
        
        // 型キャスト不要、型安全性が保証される
        String str2 = stringList.get(0);  // "Hello"
        String str3 = stringList.get(1);  // "World"
        
        System.out.println("New style: " + str2 + " " + str3);
        
        // 拡張for文でも型安全性が保証される
        for (String s : stringList) {
            System.out.println(s.toUpperCase());  // String型なのでtoUpperCase()が使える
        }
    }
}
```

### 型安全性の実際の例

```java
public class TypeSafetyExample {
    public static void main(String[] args) {
        // Box<String> はString型のオブジェクト専用のBox
        Box<String> stringBox = new Box<>();
        stringBox.setContent("こんにちは、ジェネリクス");
        String message = stringBox.getContent(); // キャスト不要
        System.out.println(message);
        
        // stringBox.setContent(123); // コンパイルエラー！String型ではないため代入できない
        
        Box<Integer> integerBox = new Box<>();
        integerBox.setContent(100);
        int number = integerBox.getContent(); // オートアンボクシング＋キャスト不要
        System.out.println(number);
        
        // 異なる型のBox間での代入もコンパイルエラーとなり、安全性が保たれる
        // Box<Integer> anotherIntegerBox = stringBox; // コンパイルエラー！
    }
}
```

## 8.2 ジェネリッククラスの作成

ジェネリクスの基本的なメリットを理解したところで、実際にジェネリッククラスを作成する方法を詳しく見ていきましょう。

### ジェネリッククラスの定義

ジェネリッククラスを定義するには、クラス名の後に山括弧（`<>`）を用いて1つ以上の型パラメータを指定します。型パラメータには任意の名前を付けることができますが、慣習として`T`（Type）、`E`（Element）、`K`（Key）、`V`（Value）など、大文字の一文字がよく使われます。

### 実践的なジェネリッククラスの例：データ分析システムの統計計算

以下の包括的な例では、データ分析システムにおける統計計算クラスを通じて、ジェネリクスの実用的な活用方法と型安全性の重要性を学習します：

```java
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * データ分析システムにおけるジェネリクス活用例
 * 型安全な統計計算と再利用可能なコンポーネントの実践的デモンストレーション
 */

// 基本的なジェネリック結果クラス
class AnalysisResult<T> {
    private T value;
    private String description;
    private boolean isValid;
    private String errorMessage;
    
    public AnalysisResult(T value, String description) {
        this.value = value;
        this.description = description;
        this.isValid = true;
    }
    
    public AnalysisResult(String errorMessage) {
        this.errorMessage = errorMessage;
        this.isValid = false;
    }
    
    public T getValue() { return value; }
    public String getDescription() { return description; }
    public boolean isValid() { return isValid; }
    public String getErrorMessage() { return errorMessage; }
    
    @Override
    public String toString() {
        if (isValid) {
            return String.format("%s: %s", description, value);
        } else {
            return String.format("Error: %s", errorMessage);
        }
    }
}

// 統計データクラス（複数の型パラメータを使用）
class StatisticalData<T extends Number, U> {
    private List<T> numericData;
    private U metadata;
    private String dataSetName;
    
    public StatisticalData(String dataSetName, U metadata) {
        this.dataSetName = dataSetName;
        this.metadata = metadata;
        this.numericData = new ArrayList<>();
    }
    
    public void addData(T data) {
        numericData.add(data);
    }
    
    public void addAll(Collection<? extends T> data) {
        numericData.addAll(data);
    }
    
    public List<T> getData() { return new ArrayList<>(numericData); }
    public U getMetadata() { return metadata; }
    public String getDataSetName() { return dataSetName; }
    public int getSize() { return numericData.size(); }
    
    public AnalysisResult<Double> calculateMean() {
        if (numericData.isEmpty()) {
            return new AnalysisResult<>("データセットが空です");
        }
        
        double sum = numericData.stream()
                                .mapToDouble(Number::doubleValue)
                                .sum();
        double mean = sum / numericData.size();
        
        return new AnalysisResult<>(mean, "平均値");
    }
    
    public AnalysisResult<T> findMinimum() {
        if (numericData.isEmpty()) {
            return new AnalysisResult<>("データセットが空です");
        }
        
        T min = numericData.stream()
                          .min((a, b) -> Double.compare(a.doubleValue(), b.doubleValue()))
                          .orElseThrow();
        
        return new AnalysisResult<>(min, "最小値");
    }
    
    public AnalysisResult<T> findMaximum() {
        if (numericData.isEmpty()) {
            return new AnalysisResult<>("データセットが空です");
        }
        
        T max = numericData.stream()
                          .max((a, b) -> Double.compare(a.doubleValue(), b.doubleValue()))
                          .orElseThrow();
        
        return new AnalysisResult<>(max, "最大値");
    }
    
    public AnalysisResult<Double> calculateStandardDeviation() {
        AnalysisResult<Double> meanResult = calculateMean();
        if (!meanResult.isValid()) {
            return meanResult;
        }
        
        double mean = meanResult.getValue();
        double variance = numericData.stream()
                                    .mapToDouble(Number::doubleValue)
                                    .map(x -> Math.pow(x - mean, 2))
                                    .sum() / numericData.size();
        
        return new AnalysisResult<>(Math.sqrt(variance), "標準偏差");
    }
    
    @Override
    public String toString() {
        return String.format("StatisticalData{name='%s', size=%d, metadata=%s}", 
            dataSetName, numericData.size(), metadata);
    }
}

// メタデータ用のクラス
class DataMetadata {
    private String source;
    private String unit;
    private Date collectionDate;
    
    public DataMetadata(String source, String unit) {
        this.source = source;
        this.unit = unit;
        this.collectionDate = new Date();
    }
    
    public String getSource() { return source; }
    public String getUnit() { return unit; }
    public Date getCollectionDate() { return collectionDate; }
    
    @Override
    public String toString() {
        return String.format("{source='%s', unit='%s'}", source, unit);
    }
}

// ジェネリック分析エンジン
class AnalysisEngine<T extends Number> {
    private String engineName;
    
    public AnalysisEngine(String engineName) {
        this.engineName = engineName;
    }
    
    // ジェネリックメソッド：複数データセットの相関分析
    public <U, V> AnalysisResult<Double> calculateCorrelation(
            StatisticalData<T, U> data1, 
            StatisticalData<T, V> data2) {
        
        List<T> dataset1 = data1.getData();
        List<T> dataset2 = data2.getData();
        
        if (dataset1.size() != dataset2.size()) {
            return new AnalysisResult<>("データセットのサイズが一致しません");
        }
        
        if (dataset1.isEmpty()) {
            return new AnalysisResult<>("データセットが空です");
        }
        
        // ピアソンの相関係数を計算
        double mean1 = dataset1.stream().mapToDouble(Number::doubleValue).average().orElse(0.0);
        double mean2 = dataset2.stream().mapToDouble(Number::doubleValue).average().orElse(0.0);
        
        double numerator = 0.0;
        double sumSq1 = 0.0;
        double sumSq2 = 0.0;
        
        for (int i = 0; i < dataset1.size(); i++) {
            double x = dataset1.get(i).doubleValue() - mean1;
            double y = dataset2.get(i).doubleValue() - mean2;
            
            numerator += x * y;
            sumSq1 += x * x;
            sumSq2 += y * y;
        }
        
        double denominator = Math.sqrt(sumSq1 * sumSq2);
        if (denominator == 0) {
            return new AnalysisResult<>("相関係数を計算できません（分散が0）");
        }
        
        double correlation = numerator / denominator;
        return new AnalysisResult<>(correlation, 
            String.format("相関係数（%s vs %s）", data1.getDataSetName(), data2.getDataSetName()));
    }
    
    // ジェネリック変換メソッド
    public <R extends Number> StatisticalData<R, String> transformData(
            StatisticalData<T, ?> originalData,
            Function<T, R> transformer,
            String transformationDescription) {
        
        StatisticalData<R, String> transformedData = 
            new StatisticalData<>(originalData.getDataSetName() + "_transformed", transformationDescription);
        
        originalData.getData().stream()
                   .map(transformer)
                   .forEach(transformedData::addData);
        
        return transformedData;
    }
    
    public void generateReport(List<? extends StatisticalData<T, ?>> dataSets) {
        System.out.println("\n=== " + engineName + " 分析レポート ===");
        
        for (StatisticalData<T, ?> dataSet : dataSets) {
            System.out.println("\n【" + dataSet.getDataSetName() + "】");
            System.out.println("  " + dataSet.calculateMean());
            System.out.println("  " + dataSet.findMinimum());
            System.out.println("  " + dataSet.findMaximum());
            System.out.println("  " + dataSet.calculateStandardDeviation());
            System.out.println("  サンプル数: " + dataSet.getSize());
            System.out.println("  メタデータ: " + dataSet.getMetadata());
        }
    }
}

public class DataAnalysisSystem {
    public static void main(String[] args) {
        System.out.println("=== データ分析システム - ジェネリクス活用例 ===");
        
        // 売上データの分析（Integer型）
        StatisticalData<Integer, DataMetadata> salesData = 
            new StatisticalData<>("月次売上", new DataMetadata("販売システム", "万円"));
        salesData.addAll(Arrays.asList(850, 920, 780, 1050, 980, 1120, 890, 960, 1080, 750, 920, 1200));
        
        // 気温データの分析（Double型）
        StatisticalData<Double, DataMetadata> temperatureData = 
            new StatisticalData<>("日平均気温", new DataMetadata("気象庁", "℃"));
        temperatureData.addAll(Arrays.asList(5.2, 8.1, 12.5, 18.3, 22.7, 26.8, 29.1, 28.5, 24.2, 17.6, 11.3, 6.9));
        
        // 株価データの分析（Double型）
        StatisticalData<Double, String> stockPriceData = 
            new StatisticalData<>("日経平均", "Tokyo Stock Exchange");
        stockPriceData.addAll(Arrays.asList(28500.0, 29200.0, 28800.0, 30100.0, 29700.0, 
                                          31200.0, 30800.0, 29900.0, 31500.0, 30300.0));
        
        // 分析エンジンの作成と分析実行
        AnalysisEngine<Integer> integerEngine = new AnalysisEngine<>("整数データ分析エンジン");
        AnalysisEngine<Double> doubleEngine = new AnalysisEngine<>("実数データ分析エンジン");
        
        // 各データセットの個別分析
        integerEngine.generateReport(Arrays.asList(salesData));
        doubleEngine.generateReport(Arrays.asList(temperatureData, stockPriceData));
        
        // 相関分析（型安全な異なるメタデータ型同士の比較）
        System.out.println("\n=== 相関分析 ===");
        AnalysisResult<Double> correlation = doubleEngine.calculateCorrelation(temperatureData, stockPriceData);
        System.out.println(correlation);
        
        // データ変換の例（型安全な変換）
        System.out.println("\n=== データ変換例 ===");
        
        // 売上データを百万円単位に変換
        Function<Integer, Double> toMillionYen = value -> value / 100.0;
        
        // Note: transformDataは型の制約により直接使用できないので、別の方法で変換をデモ
        StatisticalData<Double, String> salesInMillion = 
            new StatisticalData<>("月次売上_百万円単位", "百万円単位変換");
        salesData.getData().stream()
                 .map(toMillionYen)
                 .forEach(salesInMillion::addData);
        
        System.out.println("変換後の売上データ:");
        System.out.println("  " + salesInMillion.calculateMean());
        System.out.println("  " + salesInMillion.findMaximum());
        
        // 型安全性のデモンストレーション
        demonstrateTypeSafety();
    }
    
    private static void demonstrateTypeSafety() {
        System.out.println("\n=== 型安全性のデモンストレーション ===");
        
        // 異なる型のStatisticalDataは互換性がない
        StatisticalData<Integer, String> intData = new StatisticalData<>("整数データ", "テスト");
        StatisticalData<Double, String> doubleData = new StatisticalData<>("実数データ", "テスト");
        
        intData.addData(100);
        doubleData.addData(100.5);
        
        // 以下はコンパイルエラーとなり、型安全性が保証される
        // intData.addData(100.5);  // Double を Integer のリストに追加しようとするとエラー
        // doubleData.addData(100); // Integer を Double のリストに追加は自動変換で可能
        
        System.out.println("整数データ: " + intData.calculateMean());
        System.out.println("実数データ: " + doubleData.calculateMean());
        
        // ジェネリクスなしの場合の問題点をコメントで示す
        /*
        // ジェネリクスなしの場合（Java 5以前）
        List rawList = new ArrayList();
        rawList.add(100);
        rawList.add("文字列"); // 異なる型が混在！実行時エラーの原因
        
        Integer number = (Integer) rawList.get(0); // キャストが必要
        Integer error = (Integer) rawList.get(1);  // ClassCastException！
        */
    }
}
```

**このプログラムから学ぶ重要なジェネリクスの概念：**

1. **型安全性の保証**：ジェネリクスにより、コンパイル時に型の整合性がチェックされ、実行時の`ClassCastException`を防げます。

2. **コードの再利用性**：同じアルゴリズム（統計計算）を、異なる数値型（Integer、Double）で再利用できます。

3. **複数型パラメータの活用**：`StatisticalData<T, U>`のように、データ型とメタデータ型を独立して指定できます。

4. **境界付き型パラメータ**：`T extends Number`により、数値型のみを受け入れる制約を設けることができます。

5. **型推論の活用**：ダイヤモンド演算子（`<>`）により、冗長な型宣言を省略できます。

### 複数の型パラメータを持つクラス

クラスは複数の型パラメータを持つことができます。カンマで区切って指定します。

```java
// 2つの型パラメータ K (Key) と V (Value) を持つ OrderedPair クラス
public class OrderedPair<K, V> {
    private K key;
    private V value;
    
    public OrderedPair(K key, V value) {
        this.key = key;
        this.value = value;
    }
    
    public K getKey() { return key; }
    public V getValue() { return value; }
    
    public void setKey(K key) { this.key = key; }
    public void setValue(V value) { this.value = value; }
    
    @Override
    public String toString() {
        return "(" + key + ", " + value + ")";
    }
    
    // ペアの等価性をチェックするメソッド
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        OrderedPair<?, ?> that = (OrderedPair<?, ?>) obj;
        return key.equals(that.key) && value.equals(that.value);
    }
}

class MultipleTypeParametersExample {
    public static void main(String[] args) {
        // Stringをキー、Integerを値とするペア
        OrderedPair<String, Integer> pair1 = new OrderedPair<>("Score", 100);
        System.out.println("Key: " + pair1.getKey() + ", Value: " + pair1.getValue());
        
        // Integerをキー、Stringを値とするペア
        OrderedPair<Integer, String> pair2 = new OrderedPair<>(1, "One");
        System.out.println("Key: " + pair2.getKey() + ", Value: " + pair2.getValue());
        
        // Double型の座標ペア
        OrderedPair<Double, Double> coordinates = new OrderedPair<>(35.6762, 139.6503);
        System.out.println("座標: " + coordinates);
        
        // ペアの等価性チェック
        OrderedPair<String, Integer> anotherPair = new OrderedPair<>("Score", 100);
        System.out.println("pair1とanotherPairは等しいか: " + pair1.equals(anotherPair));
    }
}
```

## 8.3 ジェネリックメソッドの定義：柔軟で再利用可能な汎用処理

### ジェネリックメソッドの基本概念

クラス全体をジェネリックにするのではなく、特定のメソッドだけをジェネリックにすることも可能です。これをジェネリックメソッドと呼びます。ジェネリックメソッドは、非ジェネリックなクラス内でも定義でき、メソッドレベルでの型安全性と再利用性を提供します。

### 実践的なジェネリックメソッドの例：データ処理ユーティリティシステム

以下の包括的な例では、さまざまなデータ処理において活用されるジェネリックメソッドの実用的な使用方法を学習します：

```java
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

/**
 * データ処理ユーティリティシステム
 * ジェネリックメソッドの柔軟性と再利用性の実践的デモンストレーション
 */

// 処理結果を包むクラス
class ProcessingResult<T> {
    private T result;
    private boolean success;
    private String message;
    private long processingTimeMs;
    
    public ProcessingResult(T result, long processingTimeMs) {
        this.result = result;
        this.success = true;
        this.processingTimeMs = processingTimeMs;
        this.message = "処理が正常に完了しました";
    }
    
    public ProcessingResult(String errorMessage) {
        this.success = false;
        this.message = errorMessage;
        this.processingTimeMs = 0;
    }
    
    public T getResult() { return result; }
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public long getProcessingTimeMs() { return processingTimeMs; }
    
    @Override
    public String toString() {
        if (success) {
            return String.format("Success: %s (%dms)", result, processingTimeMs);
        } else {
            return String.format("Error: %s", message);
        }
    }
}

public class DataProcessingUtilities {
    
    // 基本的なジェネリックメソッド：配列の安全な要素交換
    public static <T> void safeSwap(T[] array, int i, int j) {
        if (array == null || i < 0 || j < 0 || i >= array.length || j >= array.length) {
            throw new IllegalArgumentException("無効なインデックスまたは配列です");
        }
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
    
    // 境界付き型パラメータ：比較可能なオブジェクトの最大値/最小値
    public static <T extends Comparable<T>> T findMax(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException("コレクションが空または無効です");
        }
        return collection.stream().max(T::compareTo).orElseThrow();
    }
    
    public static <T extends Comparable<T>> T findMin(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException("コレクションが空または無効です");
        }
        return collection.stream().min(T::compareTo).orElseThrow();
    }
    
    // 複数の型パラメータ：キー・値ペアのマッピング
    public static <K, V, R> List<R> mapPairs(Map<K, V> map, BiFunction<K, V, R> mapper) {
        return map.entrySet().stream()
                  .map(entry -> mapper.apply(entry.getKey(), entry.getValue()))
                  .collect(Collectors.toList());
    }
    
    // 高階関数を使用したジェネリック処理：フィルタリングと変換
    public static <T, R> ProcessingResult<List<R>> processWithFilter(
            Collection<T> input, 
            Predicate<T> filter, 
            Function<T, R> transformer) {
        
        long startTime = System.currentTimeMillis();
        
        try {
            if (input == null) {
                return new ProcessingResult<>("入力コレクションがnullです");
            }
            
            List<R> result = input.stream()
                                  .filter(filter)
                                  .map(transformer)
                                  .collect(Collectors.toList());
            
            long endTime = System.currentTimeMillis();
            return new ProcessingResult<>(result, endTime - startTime);
            
        } catch (Exception e) {
            return new ProcessingResult<>("処理中にエラーが発生しました: " + e.getMessage());
        }
    }
    
    // ジェネリック集約処理：任意の型の集約
    public static <T, R> ProcessingResult<R> aggregate(
            Collection<T> input,
            R identity,
            BinaryOperator<R> accumulator,
            Function<T, R> mapper) {
        
        long startTime = System.currentTimeMillis();
        
        try {
            if (input == null) {
                return new ProcessingResult<>("入力コレクションがnullです");
            }
            
            R result = input.stream()
                           .map(mapper)
                           .reduce(identity, accumulator);
            
            long endTime = System.currentTimeMillis();
            return new ProcessingResult<>(result, endTime - startTime);
            
        } catch (Exception e) {
            return new ProcessingResult<>("集約処理中にエラーが発生しました: " + e.getMessage());
        }
    }
    
    // ジェネリック並列処理：複数の同種データの並列変換
    public static <T, R> ProcessingResult<List<R>> parallelTransform(
            List<T> input,
            Function<T, R> transformer) {
        
        long startTime = System.currentTimeMillis();
        
        try {
            if (input == null) {
                return new ProcessingResult<>("入力リストがnullです");
            }
            
            List<R> result = input.parallelStream()
                                  .map(transformer)
                                  .collect(Collectors.toList());
            
            long endTime = System.currentTimeMillis();
            return new ProcessingResult<>(result, endTime - startTime);
            
        } catch (Exception e) {
            return new ProcessingResult<>("並列変換中にエラーが発生しました: " + e.getMessage());
        }
    }
    
    // 型安全なキャッシュシステム
    private static Map<String, Object> cache = new ConcurrentHashMap<>();
    
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> getFromCache(String key, Class<T> type) {
        Object value = cache.get(key);
        if (value != null && type.isInstance(value)) {
            return Optional.of((T) value);
        }
        return Optional.empty();
    }
    
    public static <T> void putToCache(String key, T value) {
        cache.put(key, value);
    }
    
    // ジェネリック検証システム
    public static <T> boolean validate(T object, Predicate<T> validator, String validationName) {
        try {
            boolean isValid = validator.test(object);
            System.out.println(validationName + ": " + (isValid ? "✓ 有効" : "✗ 無効"));
            return isValid;
        } catch (Exception e) {
            System.out.println(validationName + ": ✗ エラー - " + e.getMessage());
            return false;
        }
    }
    
    // ジェネリックメソッドのデモンストレーション
    public static void main(String[] args) {
        System.out.println("=== データ処理ユーティリティシステム - ジェネリックメソッド活用例 ===");
        
        // 基本的な配列操作
        demonstrateBasicOperations();
        
        // 数値データの処理
        demonstrateNumericProcessing();
        
        // 文字列データの処理
        demonstrateStringProcessing();
        
        // 複合データの処理
        demonstrateComplexDataProcessing();
        
        // キャッシュシステムのテスト
        demonstrateCacheSystem();
        
        // 検証システムのテスト
        demonstrateValidationSystem();
    }
    
    private static void demonstrateBasicOperations() {
        System.out.println("\n=== 基本的な配列操作 ===");
        
        String[] fruits = {"apple", "banana", "cherry", "date"};
        System.out.println("交換前: " + Arrays.toString(fruits));
        safeSwap(fruits, 0, 2);
        System.out.println("交換後: " + Arrays.toString(fruits));
        
        Integer[] numbers = {5, 2, 8, 1, 9};
        System.out.println("数値配列の最大値: " + findMax(Arrays.asList(numbers)));
        System.out.println("数値配列の最小値: " + findMin(Arrays.asList(numbers)));
    }
    
    private static void demonstrateNumericProcessing() {
        System.out.println("\n=== 数値データの処理 ===");
        
        List<Integer> scores = Arrays.asList(85, 92, 78, 96, 87, 89, 94, 82);
        
        // フィルタリングと変換：合格点以上の点数を文字列に変換
        ProcessingResult<List<String>> passedScores = processWithFilter(
            scores,
            score -> score >= 85,
            score -> score + "点(合格)"
        );
        System.out.println("合格スコア: " + passedScores.getResult());
        System.out.println("処理時間: " + passedScores.getProcessingTimeMs() + "ms");
        
        // 集約処理：平均点の計算
        ProcessingResult<Double> averageResult = aggregate(
            scores,
            0.0,
            Double::sum,
            Integer::doubleValue
        );
        if (averageResult.isSuccess()) {
            double average = averageResult.getResult() / scores.size();
            System.out.println("平均点: " + String.format("%.2f", average));
        }
    }
    
    private static void demonstrateStringProcessing() {
        System.out.println("\n=== 文字列データの処理 ===");
        
        List<String> words = Arrays.asList("Java", "Python", "JavaScript", "Go", "Rust", "Kotlin");
        
        // 並列変換：文字列の長さ計算
        ProcessingResult<List<Integer>> lengthResult = parallelTransform(
            words,
            String::length
        );
        System.out.println("文字列長: " + lengthResult.getResult());
        System.out.println("並列処理時間: " + lengthResult.getProcessingTimeMs() + "ms");
        
        // フィルタリング：5文字以上の言語名
        ProcessingResult<List<String>> longNames = processWithFilter(
            words,
            word -> word.length() >= 5,
            String::toUpperCase
        );
        System.out.println("5文字以上の言語名(大文字): " + longNames.getResult());
    }
    
    private static void demonstrateComplexDataProcessing() {
        System.out.println("\n=== 複合データの処理 ===");
        
        Map<String, Integer> productPrices = Map.of(
            "ノートパソコン", 89800,
            "マウス", 2800,
            "キーボード", 8500,
            "モニター", 35200
        );
        
        // キー・値ペアのマッピング：商品情報の文字列化
        List<String> productInfo = mapPairs(
            productPrices,
            (product, price) -> String.format("%s: %,d円", product, price)
        );
        
        System.out.println("商品情報:");
        productInfo.forEach(info -> System.out.println("  " + info));
    }
    
    private static void demonstrateCacheSystem() {
        System.out.println("\n=== キャッシュシステムのテスト ===");
        
        // 異なる型のデータをキャッシュ
        putToCache("user_count", 1500);
        putToCache("app_version", "2.1.0");
        putToCache("last_update", LocalDateTime.now());
        
        // 型安全な取得
        Optional<Integer> userCount = getFromCache("user_count", Integer.class);
        Optional<String> version = getFromCache("app_version", String.class);
        Optional<LocalDateTime> lastUpdate = getFromCache("last_update", LocalDateTime.class);
        
        System.out.println("ユーザー数: " + userCount.orElse(0));
        System.out.println("アプリバージョン: " + version.orElse("不明"));
        System.out.println("最終更新: " + lastUpdate.orElse(LocalDateTime.MIN));
        
        // 型不一致の安全な処理
        Optional<String> wrongType = getFromCache("user_count", String.class);
        System.out.println("型不一致アクセス: " + wrongType.orElse("取得失敗"));
    }
    
    private static void demonstrateValidationSystem() {
        System.out.println("\n=== 検証システムのテスト ===");
        
        String email = "user@example.com";
        Integer age = 25;
        List<String> tags = Arrays.asList("java", "spring", "database");
        
        // 各種検証の実行
        validate(email, e -> e.contains("@"), "メール形式チェック");
        validate(age, a -> a >= 18, "年齢チェック");
        validate(tags, t -> !t.isEmpty(), "タグ存在チェック");
        validate(tags, t -> t.size() <= 5, "タグ数制限チェック");
    }
}
```

**このプログラムから学ぶ重要なジェネリックメソッドの概念：**

1. **メソッドレベルの型パラメータ**：`<T>`をメソッド宣言に追加することで、そのメソッドでのみ有効な型パラメータを定義できます。

2. **境界付き型パラメータ**：`<T extends Comparable<T>>`により、特定のインターフェイスを実装した型のみを受け入れる制約を設けられます。

3. **複数型パラメータの活用**：`<K, V, R>`のように複数の型を独立して処理できる柔軟なメソッドを作成できます。

4. **高階関数との組み合わせ**：Function、Predicate、BinaryOperatorなどと組み合わせることで、非常に柔軟な処理システムを構築できます。

5. **型安全なキャスト**：ジェネリクスにより、実行時の型チェックとキャストを安全に行えます。

**実用的な応用場面：**

- **データ処理フレームワーク**: さまざまな型のデータを統一的に処理するパイプライン
- **ユーティリティライブラリ**: 型に依存しない汎用的な処理機能
- **API設計**: 型安全で柔軟なインターフェイスの提供
- **関数型プログラミング**: Stream APIや関数型インターフェイスとの統合
```

### ジェネリックメソッドの型推論

ジェネリックメソッドを呼び出す際、多くの場合、コンパイラが引数の型から型パラメータを自動的に推論してくれます。これを**型推論**と呼びます。

```java
public class TypeInferenceExample {
    // 型推論の例
    public static void main(String[] args) {
        // 明示的な型指定
        String[] stringArray = {"A", "B", "C"};
        GenericMethodDemo.<String>printArrayContent(stringArray);
        
        // 型推論を使用（推奨される方法）
        GenericMethodDemo.printArrayContent(stringArray); // Stringと推論される
        
        // 複雑な型推論の例
        List<String> names = Arrays.asList("Alice", "Bob");
        String firstName = GenericMethodDemo.getFirstElement(names); // TはStringと推論
        
        List<Integer> numbers = Arrays.asList(1, 2, 3);
        Integer firstNumber = GenericMethodDemo.getFirstElement(numbers); // TはIntegerと推論
    }
}
```

## 8.4 型制約（境界のある型パラメータ）

型パラメータに制約を設けることで、ジェネリクスで使用できる型を特定のクラスのサブクラスや、特定のインターフェイスを実装したクラスに限定できます。これを**境界のある型パラメータ**（bounded type parameters）と呼びます。キーワード`extends`を使用します。

### 基本的な境界指定

`<T extends UpperBoundType>`のように記述します。これは「Tは`UpperBoundType`またはそのサブタイプでなければならない」という意味です。`UpperBoundType`はクラスでもインターフェイスでもかまいません。

複数の境界を指定することも可能です（例： `<T extends Number & Runnable>`）。この場合、`&`で区切り、クラス指定がある場合は最初に記述します。

```java
// Numberクラスまたはそのサブクラスのみを受け入れるジェネリクスクラス
public class NumericBox<T extends Number> {
    private T number;
    
    public void setNumber(T number) {
        this.number = number;
    }
    
    public T getNumber() {
        return number;
    }
    
    // TはNumberのサブクラスなので、Numberクラスのメソッド (例: doubleValue) を呼び出せる
    public double getDoubleValue() {
        return number.doubleValue();
    }
    
    public boolean isPositive() {
        return number.doubleValue() > 0;
    }
    
    public boolean isZero() {
        return number.doubleValue() == 0.0;
    }
}

// 複数の境界を持つ例
interface SerializableComparable<T> extends java.io.Serializable, Comparable<T> {}

class DataItem<T extends Number & Comparable<T>> { // TはNumberのサブクラスかつComparableを実装
    private T data;
    public DataItem(T data) { this.data = data; }
    public T getData() { return data; }
    public boolean isGreaterThan(DataItem<T> other) {
        return this.data.compareTo(other.getData()) > 0;
    }
    public double getAsDouble() {
        return data.doubleValue(); // Numberのメソッドを使用可能
    }
}

class BoundedTypeParameterExample {
    public static void main(String[] args) {
        NumericBox<Integer> intBox = new NumericBox<>();
        intBox.setNumber(123); // IntegerはNumberのサブクラスなのでOK
        System.out.println("Integer値: " + intBox.getNumber() + ", Double値: " + intBox.getDoubleValue());
        System.out.println("正の値か: " + intBox.isPositive());
        
        NumericBox<Double> doubleBox = new NumericBox<>();
        doubleBox.setNumber(3.14159); // DoubleはNumberのサブクラスなのでOK
        System.out.println("Double値: " + doubleBox.getNumber() + ", Double値: " + doubleBox.getDoubleValue());
        
        // NumericBox<String> stringBox = new NumericBox<>(); // コンパイルエラー! StringはNumberのサブクラスではない
        
        DataItem<Integer> di1 = new DataItem<>(10);
        DataItem<Integer> di2 = new DataItem<>(20);
        System.out.println("di2はdi1より大きいか: " + di2.isGreaterThan(di1)); // true
        System.out.println("di1のDouble値: " + di1.getAsDouble());
        
        // DataItem<Float> df = new DataItem<>(3.0f); // FloatはNumberのサブクラスでComparableを実装しているのでOK
    }
}
```

### 境界指定の利点

境界のある型パラメータを使用することで、以下の利点が得られます：

1. **メソッドの利用可能性**: 境界として指定したクラスやインターフェイスのメソッドを型パラメータの型に対して呼び出せる
2. **型安全性の保証**: 指定した境界内の型のみを受け入れることで、実行時エラーを防ぐ
3. **コードの再利用性**: 特定の特性を持つ型のグループに対して汎用的なロジックを適用できる

## 8.5 ワイルドカード（`?`）

ジェネリクスでは、型パラメータが未知であるか、またはある範囲内の任意の型であることを表現するために**ワイルドカード**（`?`）を使用します。

ワイルドカードは主にメソッドの引数、フィールド、ローカル変数の型として使用され、ジェネリック型のインスタンス化には使用できません。

ワイルドカードには3つの形式があります：

### 1. 非境界ワイルドカード（`<?>`）

`List<?>`のように使用し、「未知の型（any type）」を表します。このリストにはあらゆる型の要素が含まれている可能性があります。`List<?>`から要素を取得する場合、その型は`Object`として扱われます。`null`以外の要素を`List<?>`に追加することはできません（型安全性を壊す可能性があるため）。

```java
import java.util.Arrays;
import java.util.List;

public class UnboundedWildcardExample {
    // List<?> は、任意の型の要素を持つListを受け入れる
    public static void printListElements(List<?> list) {
        // list.add("new element"); // コンパイルエラー！? の型が不明なため、安全に追加できない (nullは可)
        for (Object element : list) { // 要素はObject型として取得できる
            System.out.print(element + " ");
        }
        System.out.println();
    }
    
    public static int getListSize(List<?> list) {
        return list.size(); // サイズの取得は安全
    }
    
    public static void main(String[] args) {
        List<String> names = Arrays.asList("Alice", "Bob", "Carol");
        List<Integer> numbers = Arrays.asList(1, 2, 3);
        List<Object> mixed = Arrays.asList("Text", 4, true);
        
        System.out.print("Names: ");
        printListElements(names);
        System.out.println("Size: " + getListSize(names));
        
        System.out.print("Numbers: ");
        printListElements(numbers);
        System.out.println("Size: " + getListSize(numbers));
        
        System.out.print("Mixed: ");
        printListElements(mixed);
        System.out.println("Size: " + getListSize(mixed));
    }
}
```

### 2. 上限境界ワイルドカード（`<? extends Type>`）

`List<? extends Number>`のように使用し、「`Number`または`Number`の任意のサブクラスの型」を表します。このリストから要素を取得する場合、その型は`Number`として扱われます。このリストに`null`以外の要素を追加することはできません（具体的なサブタイプが不明なため、型安全性を壊す可能性がある）。これは主に読み取り操作に使用されます（Producer Extends）。

```java
import java.util.Arrays;
import java.util.List;

public class UpperBoundedWildcardExample {
    // Numberまたはそのサブクラスのリストを受け取り、合計を計算
    public static double sumOfList(List<? extends Number> list) {
        double sum = 0.0;
        for (Number n : list) {
            sum += n.doubleValue(); // Numberのメソッドを安全に呼び出せる
        }
        return sum;
    }
    
    public static void printNumbers(List<? extends Number> numbers) {
        // numbers.add(10); // コンパイルエラー！<? extends Number> には追加できない (nullは可)
        for (Number n : numbers) { // Numberとして安全に取得できる
            System.out.print(n + " ");
        }
        System.out.println();
    }
    
    public static void main(String[] args) {
        List<Integer> integers = Arrays.asList(1, 2, 3, 4, 5);
        List<Double> doubles = Arrays.asList(1.1, 2.2, 3.3);
        List<Float> floats = Arrays.asList(1.0f, 2.0f, 3.0f);
        
        System.out.println("Sum of integers: " + sumOfList(integers));
        System.out.println("Sum of doubles: " + sumOfList(doubles));
        System.out.println("Sum of floats: " + sumOfList(floats));
        
        System.out.print("Integers: ");
        printNumbers(integers);
        System.out.print("Doubles: ");
        printNumbers(doubles);
        
        // List<String> strings = Arrays.asList("a", "b");
        // sumOfList(strings); // コンパイルエラー！StringはNumberのサブクラスではない
    }
}
```

### 3. 下限境界ワイルドカード（`<? super Type>`）

`List<? super Integer>`のように使用し、「`Integer`または`Integer`の任意のスーパータイプ（親クラスや実装インターフェイス）」を表します。このリストには`Integer`型またはそのサブクラスのインスタンスを追加できます。リストから要素を取得する場合、その型は`Object`として扱われます（具体的なスーパータイプが不明なため）。これは主に書き込み操作に使用されます（Consumer Super）。

```java
import java.util.ArrayList;
import java.util.List;

public class LowerBoundedWildcardExample {
    // IntegerまたはそのスーパータイプのリストにInteger型の要素を追加する
    // このリストは、Integer型の値を受け入れることができる (Consumer)
    public static void addIntegersToList(List<? super Integer> list, int count) {
        for (int i = 1; i <= count; i++) {
            list.add(i); // Integer型 (またはそのサブクラス) の値を追加できる
        }
        // Object obj = list.get(0); // 取得はObject型としてのみ安全
    }
    
    public static void printObjects(List<? super Integer> list) {
        for (Object obj : list) {
            System.out.print(obj + " ");
        }
        System.out.println();
    }
    
    public static void main(String[] args) {
        List<Integer> integerList = new ArrayList<>();
        List<Number> numberList = new ArrayList<>();
        List<Object> objectList = new ArrayList<>();
        
        addIntegersToList(integerList, 3);
        System.out.print("Integer List: ");
        printObjects(integerList); // 1 2 3
        
        addIntegersToList(numberList, 4);
        System.out.print("Number List: ");
        printObjects(numberList); // 1 2 3 4
        
        addIntegersToList(objectList, 5);
        System.out.print("Object List: ");
        printObjects(objectList); // 1 2 3 4 5
        
        // List<Double> doubleList = new ArrayList<>();
        // addIntegersToList(doubleList, 3); // コンパイルエラー！DoubleはIntegerのスーパータイプではない
    }
}
```

### PECS原則

**Producer Extends, Consumer Super (PECS)** という原則があります：

- `<? extends T>`: ジェネリックな構造から`T`型のデータを取りだす（Produceする）が、追加はしない場合に使用
- `<? super T>`: ジェネリックな構造に`T`型のデータを追加（Consumeする）するが、取りだすことは少ない（取り出してもObject型）場合に使用
- 構造がProducerとConsumerの両方である場合は、ワイルドカードを使用せず、正確な型パラメータ（例： `<T>`）を使用
- どちらでもない場合は`<?>`を使用

## 8.6 ジェネリックインターフェイス

クラスと同様に、インターフェイスもジェネリックにできます。ジェネリックインターフェイスを実装するクラスは、その型パラメータを指定するか、自身もジェネリッククラスとして型パラメータを渡す必要があります。

```java
// ジェネリックインターフェイス
interface Processor<T> {
    T process(T input); // T型の値を処理し、T型の値を返す
    boolean canProcess(Object obj); // 型チェックなど
}

// String型に特化してジェネリックインターフェイスを実装するクラス
class StringProcessor implements Processor<String> {
    @Override
    public String process(String input) {
        return input.toUpperCase(); // 文字列を大文字に変換
    }
    @Override
    public boolean canProcess(Object obj){
        return obj instanceof String;
    }
}

// ジェネリックインターフェイスを実装するジェネリッククラス
class GenericDataProcessor<T> implements Processor<T> {
    @Override
    public T process(T input) {
        System.out.println("Processing item of type: " + input.getClass().getName());
        return input; // 何もせずにそのまま返す例
    }
    @Override
    public boolean canProcess(Object obj){
        return true; // 簡単な例
    }
}

public class GenericInterfaceExample {
    public static void main(String[] args) {
        Processor<String> sp = new StringProcessor();
        System.out.println("StringProcessor: " + sp.process("hello")); // HELLO
        
        Processor<Integer> gip = new GenericDataProcessor<>(); // 型推論
        System.out.println("GenericDataProcessor (Integer): " + gip.process(123));
        
        Processor<Double> gdp = new GenericDataProcessor<Double>();
        System.out.println("GenericDataProcessor (Double): " + gdp.process(3.14));
    }
}
```

## 8.7 型消去（Type Erasure）

Javaのジェネリクスは、**型消去**というしくみによって実装されています。これは、コンパイル時に型パラメータの情報がチェックされた後、バイトコードレベルでは型パラメータがその境界（bounded type）または`Object`に置き換えられることを意味します。

- `<T>` は `Object` に置き換えられます
- `<T extends SomeClass>` は `SomeClass` に置き換えられます
- `<T extends SomeInterface>` は `SomeInterface` に（実際にはObjectに、そしてキャストが挿入される）置き換えられます

このため、実行時にはジェネリック型に関する情報はほとんど残りません。

### 型消去の影響

1. **ジェネリック型のインスタンス化**: `new T()`, `new T[]` のようなコードは直接書けません
2. **`instanceof` 演算子**: `obj instanceof T` のようなチェックは直接できません（`obj instanceof List<?>` は可能）
3. **キャスト**: コンパイラは必要に応じてキャストを挿入しますが、プログラマが意識することは少ないです
4. **オーバーロード**: 型消去後にシグネチャが同じになるメソッドはオーバーロードできません

```java
import java.util.ArrayList;
import java.util.List;

public class TypeErasureExample {
    
    // ジェネリクスを使うことで、1つのメソッドで対応可能
    public <E> void printGenericList(List<E> list) {
        for (E element : list) {
            System.out.println(element);
        }
    }
    
    public static void main(String[] args) {
        List<String> names = new ArrayList<>();
        names.add("Alice");
        // names.add(123); // コンパイル時エラー
        
        // コンパイル後、List<String> は List に、List<Integer> も List になる (型消去)。
        // ただし、要素の追加や取得時にはコンパイラが型チェックやキャストを適切に行う。
        
        // 非具象化可能型 (Non-Reifiable Type)
        // System.out.println(names instanceof List<String>); // コンパイルエラー！
        // 実行時には List<String> の String の情報は消えているため、このチェックはできない。
        System.out.println(names instanceof List); // これはOK (List<?> と同じ意味合い)
        
        TypeErasureExample ex = new TypeErasureExample();
        List<Integer> numbers = List.of(1,2,3);
        ex.printGenericList(names);
        ex.printGenericList(numbers);
    }
}
```

## 8.8 ジェネリクスのベストプラクティス

### 1. 適切な型パラメータ名の使用

型パラメータには慣習的な名前を使用し、意味を明確にしましょう：

```java
// 良い例：意味のある型パラメータ名
public class Cache<K, V> {  // K=Key, V=Value
    private Map<K, V> cache = new HashMap<>();
    
    public void put(K key, V value) {
        cache.put(key, value);
    }
    
    public V get(K key) {
        return cache.get(key);
    }
    
    public boolean containsKey(K key) {
        return cache.containsKey(key);
    }
    
    public int size() {
        return cache.size();
    }
}

// 良い例：コレクション要素の型
public class Stack<E> {  // E=Element
    private List<E> elements = new ArrayList<>();
    
    public void push(E element) {
        elements.add(element);
    }
    
    public E pop() {
        if (elements.isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        return elements.remove(elements.size() - 1);
    }
    
    public E peek() {
        if (elements.isEmpty()) {
            throw new IllegalStateException("Stack is empty");
        }
        return elements.get(elements.size() - 1);
    }
    
    public boolean isEmpty() {
        return elements.isEmpty();
    }
}
```

### 2. ワイルドカードの適切な使用

PECS原則に従ってワイルドカードを使い分けましょう：

```java
import java.util.*;

public class WildcardBestPractices {
    // Producer: データを取り出すのみ → extends
    public static void printNumbers(List<? extends Number> numbers) {
        for (Number n : numbers) {
            System.out.println(n.doubleValue());
        }
    }
    
    // Consumer: データを追加するのみ → super
    public static void addNumbers(List<? super Integer> numbers) {
        for (int i = 1; i <= 5; i++) {
            numbers.add(i);
        }
    }
    
    // 両方を行う場合は具体的な型を使用
    public static <T extends Number> List<T> processNumbers(List<T> input) {
        List<T> result = new ArrayList<>();
        for (T number : input) {
            // 何らかの処理
            result.add(number);
        }
        return result;
    }
    
    public static void main(String[] args) {
        List<Integer> integers = Arrays.asList(1, 2, 3);
        List<Double> doubles = Arrays.asList(1.1, 2.2, 3.3);
        
        // Producer例
        printNumbers(integers);
        printNumbers(doubles);
        
        // Consumer例
        List<Number> numbers = new ArrayList<>();
        addNumbers(numbers);
        System.out.println("追加された数値: " + numbers);
    }
}
```

### 3. 境界のある型パラメータの活用

型パラメータに制約を設けることで、より安全で表現力のあるAPIを作成できます：

```java
// 数値計算ライブラリの例
public class MathUtils {
    // Comparableを実装した型のみ受け入れる
    public static <T extends Comparable<T>> T max(T a, T b) {
        return a.compareTo(b) > 0 ? a : b;
    }
    
    // Numberのサブクラスのみ受け入れる
    public static <T extends Number> double average(List<T> numbers) {
        if (numbers.isEmpty()) {
            throw new IllegalArgumentException("リストが空です");
        }
        
        double sum = 0.0;
        for (T number : numbers) {
            sum += number.doubleValue();
        }
        return sum / numbers.size();
    }
}
```

## まとめ

本章では、Javaのジェネリクスについて詳しく学習しました。ジェネリクスは現代のJavaプログラミングにおいて欠かせない重要な機能です。

### 重要なポイント

1. **型安全性の向上**: コンパイル時に型チェックを行い、実行時エラーを防ぐ
2. **コードの再利用性**: 1つのクラスやメソッドで複数の型に対応
3. **可読性の向上**: コードの意図が明確になり、自己文書化を促進
4. **保守性の改善**: 型キャストが不要になり、コードが簡潔になる

### 学習した機能

- **ジェネリッククラス**: 型パラメータを持つクラスの定義と使用
- **ジェネリックメソッド**: 特定のメソッドのみをジェネリック化
- **境界のある型パラメータ**: 型に制約を設けることでより安全なAPI設計
- **ワイルドカード**: 柔軟な型指定と、PECS原則による適切な使い分け
- **型消去**: ジェネリクスの実装メカニズムとその影響

### 実践での活用

ジェネリクスを効果的に活用することで、型安全でメンテナンスしやすいコードを作成できます。特に、コレクションフレームワークやライブラリ設計において、ジェネリクスの理解は必須です。

次の章では、ジェネリクスと密接に関連するコレクションフレームワークについてさらに詳しく学習し、実践的なプログラミング技術を身につけていきましょう。