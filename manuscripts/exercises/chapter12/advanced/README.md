# 第12章 発展課題：レコード(Records)

## 概要
基礎課題を完了した方向けの発展的な課題です。Recordとパターンマッチング、ジェネリクス、シリアライゼーションとの組み合わせなど、より高度な活用方法を学びます。

## 課題一覧

### 課題1: Recordとパターンマッチング
`RecordPatternMatching.java`を作成し、以下を実装してください：

1. **図形の階層構造**
   ```java
   public sealed interface Shape 
       permits Circle, Rectangle, Triangle {}
   
   public record Circle(double radius) implements Shape {}
   public record Rectangle(double width, double height) implements Shape {}
   public record Triangle(double base, double height) implements Shape {}
   ```

2. **パターンマッチングを使った処理**
   - 面積を計算するメソッド
   - 周囲の長さを計算するメソッド
   - Record パターンを使った分解

   ```java
   public static double calculateArea(Shape shape) {
       return switch (shape) {
           case Circle(var radius) -> Math.PI * radius * radius;
           case Rectangle(var w, var h) -> w * h;
           case Triangle(var b, var h) -> 0.5 * b * h;
       };
   }
   ```

### 課題2: Recordとジェネリクス
`GenericRecords.java`を作成し、以下を実装してください：

1. **ジェネリックなResultタイプ**
   ```java
   public sealed interface Result<T, E> 
       permits Result.Success, Result.Failure {
       
       record Success<T, E>(T value) implements Result<T, E> {}
       record Failure<T, E>(E error) implements Result<T, E> {}
   }
   ```

2. **Pairレコード**
   ```java
   public record Pair<T, U>(T first, U second) {
       // 値を入れ替えたPairを返すメソッド
       public Pair<U, T> swap() {
           return new Pair<>(second, first);
       }
   }
   ```

3. **使用例の実装**
   - ファイル読み込み処理でResultを使用
   - データベース操作でPairを使用

### 課題3: RecordとJSON/シリアライゼーション
`RecordSerialization.java`を作成し、以下を実装してください：

1. **JSON変換可能なRecord**
   ```java
   public record Product(
       String id,
       String name,
       BigDecimal price,
       List<String> categories,
       Map<String, String> attributes
   ) {
       // JSON文字列からProductを生成するファクトリメソッド
       public static Product fromJson(String json) {
           // 実装してください
       }
       
       // ProductをJSON文字列に変換
       public String toJson() {
           // 実装してください
       }
   }
   ```

2. **設定管理用のRecord**
   ```java
   public record AppConfig(
       String appName,
       int port,
       DatabaseConfig database,
       List<String> allowedOrigins
   ) {}
   
   public record DatabaseConfig(
       String url,
       String username,
       int maxConnections
   ) {}
   ```
   - プロパティファイルからの読み込み
   - 環境変数からの読み込み
   - デフォルト値の設定

## 実装のヒント

### パターンマッチングとRecord
```java
// Java 19以降のRecord パターン
if (shape instanceof Rectangle(var width, var height)) {
    System.out.println("Rectangle: " + width + " x " + height);
}

// switch式でのRecord パターン
String description = switch (shape) {
    case Circle(var r) when r > 10 -> "Large circle";
    case Circle(var r) -> "Small circle";
    case Rectangle(var w, var h) -> "Rectangle " + w + "x" + h;
    default -> "Unknown shape";
};
```

### Builderパターンの代替
```java
public record PersonBuilder(
    String name,
    Integer age,
    String email
) {
    public PersonBuilder withName(String name) {
        return new PersonBuilder(name, this.age, this.email);
    }
    
    public PersonBuilder withAge(int age) {
        return new PersonBuilder(this.name, age, this.email);
    }
    
    public PersonBuilder withEmail(String email) {
        return new PersonBuilder(this.name, this.age, email);
    }
    
    public Person build() {
        Objects.requireNonNull(name, "Name is required");
        Objects.requireNonNull(age, "Age is required");
        return new Person(name, age, email);
    }
}
```

## 提出前チェックリスト
- [ ] パターンマッチングが正しく実装されている
- [ ] ジェネリクスを適切に使用している
- [ ] シリアライゼーション/デシリアライゼーションが動作する
- [ ] エラーハンドリングが適切に実装されている

## 評価基準
- Recordとパターンマッチングを組み合わせて使えているか
- ジェネリックRecordを適切に設計できているか
- 実践的なユースケースでRecordを活用できているか
- コードの可読性と保守性が高いか