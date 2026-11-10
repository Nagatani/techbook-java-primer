# 第7章 抽象クラスとインターフェイス：よくあるエラーと対処法

書籍『Java入門』第7章「抽象クラスとインターフェイス」で扱う内容に関連して、よく遭遇するエラーとその対処法をまとめています。

抽象クラスとインターフェイスの学習過程で遭遇する典型的なエラーと、その対処法を解説します。

## 1. 抽象クラスのインスタンス化エラー

抽象クラスは直接インスタンス化できません。

エラー例：

```java
abstract class DataProcessor {
    public abstract void processData();
}

// コンパイルエラーが発生
DataProcessor processor = new DataProcessor();  // 抽象クラスはnewできない
```

エラーメッセージ：
```
error: DataProcessor is abstract; cannot be instantiated
```

対処法：

```java
// 抽象クラスを継承した具象クラスを作成
class CsvDataProcessor extends DataProcessor {
    @Override
    public void processData() {
        IO.println("CSVデータを処理中");
    }
}

// 具象クラスをインスタンス化
DataProcessor processor = new CsvDataProcessor();  // OK
```

## 2. 抽象メソッドの実装忘れ

抽象メソッドを実装しないと、継承先のクラスも抽象クラスになってしまいます。

エラー例：

```java
abstract class Shape {
    public abstract double getArea();
    public abstract void draw();
}

// コンパイルエラー：抽象メソッドが未実装
class Rectangle extends Shape {
    private double width, height;
    
    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    @Override
    public double getArea() {
        return width * height;
    }
    
    // draw()メソッドの実装が不足
}
```

エラーメッセージ：
```
error: Rectangle is not abstract and does not override abstract method draw() in Shape
```

対処法：

```java
class Rectangle extends Shape {
    private double width, height;
    
    public Rectangle(double width, double height) {
        this.width = width;
        this.height = height;
    }
    
    @Override
    public double getArea() {
        return width * height;
    }
    
    @Override
    public void draw() {
        IO.println("長方形を描画");
    }
}
```

## 3. インターフェイスの実装不備

インターフェイスのすべての抽象メソッドを実装してください。

エラー例：

```java
interface Drawable {
    void draw();
    void setColor(String color);
}

// コンパイルエラー：すべてのメソッドが実装されていない
class Circle implements Drawable {
    private double radius;
    
    public Circle(double radius) {
        this.radius = radius;
    }
    
    @Override
    public void draw() {
        IO.println("円を描画");
    }
    
    // setColor()メソッドの実装が不足
}
```

エラーメッセージ：
```
error: Circle is not abstract and does not override abstract method setColor(String) in Drawable
```

対処法：

```java
class Circle implements Drawable {
    private double radius;
    private String color = "black";
    
    public Circle(double radius) {
        this.radius = radius;
    }
    
    @Override
    public void draw() {
        IO.println(color + "の円を描画");
    }
    
    @Override
    public void setColor(String color) {
        this.color = color;
    }
}
```

## 4. 多重継承とダイヤモンド問題

Javaでは複数の抽象クラスを継承できませんが、インターフェイスの場合は実装時に名前衝突が発生する可能性があります。

エラー例：

```java
interface Exportable {
    default void export() {
        IO.println("データをエクスポート");
    }
}

interface Printable {
    default void export() {
        IO.println("印刷用にエクスポート");
    }
}

// コンパイルエラー：同名のdefaultメソッドが衝突
class ReportDocument implements Exportable, Printable {
    // どちらのexport()メソッドを使用するか不明
}
```

エラーメッセージ：
```
error: types Exportable and Printable are incompatible;
  class ReportDocument inherits unrelated defaults for export() from types Exportable and Printable
```

対処法：

```java
class ReportDocument implements Exportable, Printable {
    @Override
    public void export() {
        // 明示的にどちらのメソッドを使用するか指定
        IO.println("レポートのエクスポート:");
        Exportable.super.export();  // データエクスポートを選択
        IO.println("および");
        Printable.super.export();  // 印刷エクスポートを選択
    }
    
    // または独自の実装を提供
    public void exportToPdf() {
        IO.println("PDFファイルにエクスポート");
    }
}
```

## 5. defaultメソッドとstaticメソッドの混乱

インターフェイスのstaticメソッドは実装クラスに継承されないため、実装クラスからはインターフェイス名を付けて呼び出す必要があります。
インターフェイス自身のメソッド（defaultメソッドなど）からは、インターフェイス名を省略して呼び出せます。

エラー例：

```java
interface MathUtils {
    static double PI = 3.14159;
    
    static double calculateCircleArea(double radius) {
        return PI * radius * radius;
    }
    
    default void printInfo() {
        // インターフェイスの中からは、インターフェイス名を省略して呼び出せる
        IO.println("面積: " + calculateCircleArea(5.0));
    }
}

class Calculator implements MathUtils {
    public void doCalculation() {
        // staticメソッドは実装クラスに継承されない
        double area = calculateCircleArea(3.0);  // コンパイルエラー
    }
}
```

エラーメッセージ：
```
error: cannot find symbol
  symbol:   method calculateCircleArea(double)
  location: class Calculator
```

対処法：

コンパイルできる全文：[chapter07-code/MathUtils/](chapter07-code/MathUtils/)

```java
interface MathUtils {
    static double PI = 3.14159;
    
    static double calculateCircleArea(double radius) {
        return PI * radius * radius;
    }
    
    default void printInfo() {
        // インターフェイス名を明示すると、staticメソッドであることがわかりやすい
        IO.println("面積: " + MathUtils.calculateCircleArea(5.0));
    }
}

class Calculator implements MathUtils {
    public void doCalculation() {
        // インターフェイス名を明示して呼び出し
        double area = MathUtils.calculateCircleArea(3.0);
    }
}
```

## 6. 抽象クラスvsインターフェイスの使い分け間違い

どちらを使用するべきか迷うケースがあります。

間違った選択例：

コンパイルできる全文：[chapter07-code/Service/](chapter07-code/Service/)

```java
// 状態を持つのに無理にインターフェイスを使用
interface Service {
    // インターフェイスでは実装できない
    // private String serviceId;  // コンパイルエラー
    
    void initialize();
    void shutdown();
    
    default String getServiceId() {
        // 状態を持てないため、この実装は不完全
        return "unknown";
    }
}
```

正しい選択：

コンパイルできる全文：[chapter07-code/BaseService/](chapter07-code/BaseService/)

```java
// 共通の状態と実装を持つ場合は抽象クラス
abstract class BaseService {
    protected String serviceId;
    protected boolean isActive;
    protected LocalDateTime startTime;
    
    public BaseService(String serviceId) {
        this.serviceId = serviceId;
        this.isActive = false;
    }
    // ...
    public abstract void initialize();
    public abstract void shutdown();
    
    public final boolean isActive() {
        return isActive;
    }
    
    // ...
}

// 行動の契約のみを定義する場合はインターフェイス
interface Monitorable {
    void checkHealth();
    Map<String, Object> getMetrics();
    
    default void scheduleHealthCheck() {
        IO.println("ヘルスチェックをスケジュール");
    }
}

class EmailService extends BaseService implements Monitorable {
    private int sentCount = 0;
    // ...
    @Override
    public void initialize() {
        isActive = true;
        startTime = LocalDateTime.now();
        IO.println(serviceId + " メールサービスを起動");
    }
    // ...
    @Override
    public void checkHealth() {
        IO.println("メールサービスのヘルスチェック実行");
    }
    // ...
}
```

## 7. オーバーライドアノテーションの省略

`@Override`アノテーションを省略すると、メソッドの実装ミスに気づきにくくなります。

問題のあるコード：

```java
abstract class ReportGenerator {
    public abstract void generateReport();
}

class PdfReportGenerator extends ReportGenerator {
    // @Overrideアノテーションがない
    public void generatereport() {  // メソッド名のタイプミス
        IO.println("PDFレポートを生成中");
    }
}
```

この場合、`generatereport()`は新しいメソッドとして認識され、`generateReport()`の実装が不足しているエラーが発生します。

正しい実装：

```java
class PdfReportGenerator extends ReportGenerator {
    @Override
    public void generateReport() {  // 正しいメソッド名
        IO.println("PDFレポートを生成中");
    }
}
```

## トラブルシューティングのベストプラクティス

1. IDEの活用
    + 現代のIDEはコンパイルエラーを即座に表示し、修正提案を提供する
2. @Overrideアノテーション
    + 必ず付与して、実装ミスを防ぎましょう
3. 段階的な実装
    + 複雑な継承階層は一度に実装せず、段階的に構築しましょう
4. テストの活用
    + 抽象クラスやインターフェイスの実装は、単体テストで動作確認しましょう
5. コードレビュー: 設計の妥当性を同僚と確認しましょう

これらの知識を身につけることで、抽象クラスとインターフェイスを効果的に活用した、保守性の高いJavaプログラムを作成できるようになります。
