# 付録B.10 レコードとデータ指向プログラミング

Java 16で正式導入されたRecords（Java 14と15ではプレビュー機能）と、それを活用したデータ指向プログラミング（Data-Oriented Programming, DOP）について学習できるプロジェクトです。

## 概要

RecordsとData-Oriented Programmingは、Javaプログラミングに新しいパラダイムをもたらします。従来のオブジェクト指向プログラミングで必要だった大量のボイラープレートコードを削減し、データとロジックを明確に分離することで、より保守性の高いコードを実現できます。

## なぜRecordsとDOPが重要なのか

### ボイラープレートコードの問題

**従来のアプローチ:**
```java
public class TraditionalUser {
    private final String id;
    private final String name;
    private final String email;
    private final LocalDateTime createdAt;
    
    public TraditionalUser(String id, String name, String email, LocalDateTime createdAt) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.email = Objects.requireNonNull(email);
        this.createdAt = Objects.requireNonNull(createdAt);
    }
    
    // getters, equals, hashCode, toString...
    // これらをすべて手で書く必要がある
}
```

**Recordsを使用（コンストラクター・アクセサー・equals・hashCode・toStringが自動で用意される）:**
```java
public record User(String id, String name, String email, LocalDateTime createdAt) {}
```

### 開発への影響

- equalsとhashCodeを手で書くときの実装ミス（片方だけ直し忘れるなど）がなくなる
- コンポーネントを追加・変更すると、equals・hashCode・toStringも自動で追従する

## サンプルコード構成

### 1. RecordsとDOPの基本
- `RecordsAndDOPDemo.java`: Records基本機能とDOPパターンの実装
  - シンプルなRecordの定義と使用
  - データ指向プログラミング（図形処理の例）
  - ビジネスドメインモデル（ECサイトの例）
  - 高度なパターンマッチング
  - 代数的データ型の実装
  - カスタム検証付きレコード

### 2. データパイプラインと変換
- `DataPipelineDemo.java`: イベントソーシングとストリーム処理
  - イベントソーシングパターンの実装
  - 売上データの変換と集計パイプライン
  - リアルタイムメトリクス処理
  - APIレスポンスの変換
  - ページングとエラーハンドリング

### 3. 最適化とパフォーマンス
- `RecordOptimizationDemo.java`: Records最適化テクニック
  - メモリレイアウトの最適化
  - オブジェクトプーリング
  - 高速シリアライゼーション
  - リフレクション最適化（MethodHandleのキャッシュ）
  - 並行処理での活用

### 4. パターンマッチングを使わない版
- `RecordsAndDOPSimple.java`: `RecordsAndDOPDemo`と同じ内容を、sealedとswitchのレコードパターンを使わず、instanceofとキャストで書いた版
- `DataPipelineSimple.java`: `DataPipelineDemo`の同じ版
- パターンマッチングを使う版と読み比べると、レコードパターンで分岐と分解が簡潔になることがわかる

## 実行方法

### コンパイルと実行
```bash
javac -d . src/main/java/com/example/records/*.java

# RecordsとDOPの基本デモ
java com.example.records.RecordsAndDOPDemo

# データパイプラインデモ
java com.example.records.DataPipelineDemo  

# 最適化とパフォーマンスデモ
java com.example.records.RecordOptimizationDemo

# パターンマッチングを使わない版
java com.example.records.RecordsAndDOPSimple
java com.example.records.DataPipelineSimple
```

### JVMフラグを使用した詳細分析
```bash
# JITコンパイル情報を表示
java -XX:+PrintCompilation com.example.records.RecordOptimizationDemo

# メモリ使用量の詳細
java -Xlog:gc* -Xms256m -Xmx512m com.example.records.RecordsAndDOPDemo
```

## 学習ポイント

### 1. Recordsの基本機能

#### シンプルな定義
```java
public record User(String id, String name, String email, LocalDateTime createdAt) {}
```

#### 自動生成される機能
- **コンストラクタ**: 全フィールドを引数に取る標準コンストラクタ
- **アクセサメソッド**: フィールド名と同じメソッド（getterではない）
- **equals/hashCode**: 全コンポーネントに基づく実装
- **toString**: 読みやすい文字列表現

#### コンパクトコンストラクタ
```java
public record ValidatedEmail(String value) {
    public ValidatedEmail {  // コンパクトコンストラクタ
        if (!value.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email: " + value);
        }
        value = value.toLowerCase();  // 正規化
    }
}
```

### 2. データ指向プログラミング（DOP）

#### 原則
1. **データとロジックの分離**: データは純粋なデータ構造、ロジックは独立した関数
2. **不変性**: データは不変、新しい状態は新しいインスタンスとして作成
3. **パターンマッチング**: データの形状に基づく処理の分岐

#### 実装例
```java
// データ定義（sealed階層）
sealed interface Shape permits Circle, Rectangle, Triangle {}
record Circle(double radius) implements Shape {}
record Rectangle(double width, double height) implements Shape {}
record Triangle(double a, double b, double c) implements Shape {}

// ロジック（独立した関数）
static double area(Shape shape) {
    return switch (shape) {
        case Circle(var r) -> Math.PI * r * r;
        case Rectangle(var w, var h) -> w * h;
        case Triangle(var a, var b, var c) -> {
            double s = (a + b + c) / 2;
            yield Math.sqrt(s * (s - a) * (s - b) * (s - c));
        }
    };
}
```

### 3. 高度なパターンマッチング

#### ネストしたパターン
```java
// record Person(String name, int age, Address address, List<String> hobbies)
// record Address(String street, String city, String country, String postalCode)
// レコードパターンの中に定数（"Japan"など）は書けないので、変数で受けてwhenで比べる
case Person(var name, var age, Address(var street, var city, var country, _), _)
        when country.equals("Japan") ->
    name + " lives in " + city + ", Japan";
```

#### ガード条件
```java
case Person(var name, var age, _, _) when age >= 18 -> 
    name + " is an adult";
```

#### 代数的データ型
```java
sealed interface Expr permits Const, Var, Add, Mul {}

static double eval(Expr expr, Map<String, Double> env) {
    return switch (expr) {
        case Const(var value) -> value;
        case Var(var name) -> env.getOrDefault(name, 0.0);
        case Add(var left, var right) -> eval(left, env) + eval(right, env);
        case Mul(var left, var right) -> eval(left, env) * eval(right, env);
    };
}
```

### 4. イベントソーシング

#### イベント定義
```java
sealed interface UserEvent permits UserCreated, UserUpdated, UserDeleted {
    String userId();
    Instant timestamp();
}
```

#### イベント処理
```java
static UserSnapshot processEvent(UserSnapshot snapshot, UserEvent event) {
    return switch (event) {
        case UserCreated(var id, var name, var email, _, var time) ->
            new UserSnapshot(id, name, email, true, time, 1);
        case UserUpdated(var id, var changes, _, var time) ->
            updateSnapshot(snapshot, changes, time);
        case UserDeleted(var id, _, _, var time) ->
            markAsDeleted(snapshot, time);
    };
}
```

### 5. パフォーマンス最適化

#### メモリレイアウト
HotSpot JVMはフィールドを自動で並べ替えて配置するので、レコードのコンポーネントを宣言する順序はオブジェクトの大きさに影響しません。既定で有効なcompressed oopsでは、参照（Stringなど）は4バイトで表されます。

#### オブジェクトプーリング
```java
public static CacheKey getCacheKey(String namespace, String key, long version) {
    String poolKey = namespace + ":" + key + ":" + version;
    return KEY_POOL.computeIfAbsent(poolKey, k -> 
        new CacheKey(namespace.intern(), key.intern(), version)
    );
}
```

## パフォーマンス特性

### 実行結果について

`RecordsAndDOPDemo`と`RecordOptimizationDemo`は、処理時間を次のような形式で表示します（数値の部分は環境で変わります）。

```
=== Record vs Traditional Class Performance ===
Creation - Traditional: (時間) ms, Record: (時間) ms
Equals - Traditional: (時間) ms, Record: (時間) ms
ToString - Traditional: (時間) ms, Record: (時間) ms

=== Serialization Performance ===
Standard serialization (round trip): (時間) ms
Fast serialization (round trip): (時間) ms
Speedup: (倍率)x
```

時間は数回の空回し（ウォームアップ）のあとに`System.nanoTime()`で測っていますが、結果はJVM・CPU・負荷などの環境で変わり、どちらが速いかが入れ替わることもあります。正確に比べるにはJMH（Java Microbenchmark Harness）で測ります。

## 実践的な使用ガイドライン

### 1. いつRecordsを使うべきか

#### 適している場合
○ **データ転送オブジェクト（DTO）**
```java
public record UserDTO(String id, String name, String email) {}
```

○ **イミュータブルな値オブジェクト**
```java
public record Money(BigDecimal amount, Currency currency) {}
```

○ **設定値やパラメータ群**
```java
public record DatabaseConfig(String url, String username, int poolSize) {}
```

#### 適さない場合
× **可変状態を持つエンティティ**
```java
// Recordsは不変なので、頻繁に状態が変わるものには不適切
public class MutableShoppingCart { // Recordではなくクラスを使用
    private final List<Item> items = new ArrayList<>();
    public void addItem(Item item) { items.add(item); }
}
```

× **継承階層を持つクラス**
```java
// Recordsは他のクラスを継承できない
public abstract class Animal { } // Recordは使えない
```

### 2. パターンマッチングのベストプラクティス

#### 網羅的なパターン
```java
// sealedを使って網羅性を保証
sealed interface Result<T> permits Success, Failure {} // permitsにはクラス名だけを書く（型引数は付けない）
record Success<T>(T value) implements Result<T> {}
record Failure<T>(String error) implements Result<T> {}

// switchで全ケースを処理
String message = switch (result) {
    case Success(var value) -> "Success: " + value;
    case Failure(var error) -> "Error: " + error;
    // defaultは不要（sealed階層で網羅的）
};
```

#### 部分的な分解
```java
// 必要なフィールドのみ抽出
case Person(var name, _, Address(_, var city, _, _), _) -> // 4つのコンポーネントのうち必要なものだけ
    name + " from " + city;
```

### 3. データパイプラインの設計

#### ストリーム処理との統合
```java
var processedData = rawData.stream()
    .map(Pipeline::normalize)      // Records変換
    .filter(Pipeline::validate)    // パターンマッチング
    .collect(Collectors.groupingBy(
        record -> record.category(),  // Recordのメソッド
        Collectors.summarizingDouble(record -> record.amount())
    ));
```

#### イベント駆動の処理
```java
// sealed interface OrderEvent permits OrderPlaced, PaymentReceived, OrderCancelled
List<Result> results = events.stream()
    .map(event -> switch (event) {
        case OrderPlaced(var order) -> processOrder(order);
        case PaymentReceived(var payment) -> processPayment(payment);
        case OrderCancelled(var cancellation) -> processCancellation(cancellation);
    })
    .toList();
```

## 高度なトピック

### 1. カスタムシリアライゼーション

```java
// 高速なバイナリシリアライゼーション
// （writeValueは値の型ごとに書き込む補助メソッド。型の情報も書いておかないと元の型に戻せない）
public static byte[] serialize(Record record) {
    var components = record.getClass().getRecordComponents();
    try (var baos = new ByteArrayOutputStream();
         var dos = new DataOutputStream(baos)) {
        
        for (var component : components) {
            var value = component.getAccessor().invoke(record);
            writeValue(dos, value);
        }
        return baos.toByteArray();
    } catch (Exception e) {
        throw new RuntimeException("Serialization failed", e);
    }
}
```

### 2. リフレクション最適化

```java
// MethodHandleはクラスごとに1回だけ作ってキャッシュする
// （呼び出すたびにunreflectすると、通常のリフレクションより遅くなる）
private static final Map<Class<?>, List<Map.Entry<String, MethodHandle>>> HANDLE_CACHE =
    new ConcurrentHashMap<>();

public static Map<String, Object> recordToMap(Record record) {
    var handles = HANDLE_CACHE.computeIfAbsent(record.getClass(), clazz -> {
        var lookup = MethodHandles.lookup();
        var list = new ArrayList<Map.Entry<String, MethodHandle>>();
        for (var component : clazz.getRecordComponents()) {
            try {
                list.add(Map.entry(component.getName(), lookup.unreflect(component.getAccessor())));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return list;
    });
    var result = new HashMap<String, Object>();
    for (var entry : handles) {
        try {
            result.put(entry.getKey(), entry.getValue().invoke(record));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
    return result;
}
```

`RecordOptimizationDemo`の`recordToMapFast`は、この考え方で書いています。

### 3. 将来の発展（Project Valhalla）

Project ValhallaのValue Objects（JEP 401）は、Java 25には含まれていません（プレビュー機能でもなく、試験用のビルドでだけ試せます）。次のコードはJava 25ではコンパイルできない、考え方を示す例です。

```java
// 将来のValue Records（概念的）
public value record ComplexNumber(double real, double imaginary) {
    // スタック割り当て可能
    // 配列がフラットになる
    // == での値比較が可能
}
```

## トラブルシューティング

### よくある問題と対策

#### 問題1: NullPointerException
```java
// 問題: nullを含むコレクション
public record Data(List<String> items) {}

// ○ 解決: コンパクトコンストラクタで防御的コピー
public record Data(List<String> items) {
    public Data {
        items = items == null ? List.of() : List.copyOf(items);
    }
}
```

#### 問題2: 循環参照
```java
// 問題: 可変のListをコンポーネントに持つと、自分自身を要素に入れて循環を作れる
public record Node(String value, List<Node> children) {}

var children = new ArrayList<Node>();
var node = new Node("root", children);
children.add(node);   // nodeの子にnode自身が入る
node.toString();      // toString（hashCodeも）が自分を何度もたどり、StackOverflowErrorになる

// ○ 解決: コンパクトコンストラクターで不変のコピーにする（作ったあとに循環を作れなくなる）
public record Node(String value, List<Node> children) {
    public Node {
        children = List.copyOf(children);
    }
}
```

`Node(String value, Node parent)`のように、自分と同じレコードを直接持つだけなら循環は作れません。レコードは不変なので、親は子より先に作られていなければならないからです。

#### 問題3: シリアライゼーション互換性
```java
// レコードは、コンポーネントの名前と値で直列化される
public record VersionedData(String id, int version) 
    implements Serializable {
}
```

通常のクラスでは`serialVersionUID`を宣言して互換性を管理しますが、レコードでは`serialVersionUID`の一致は検査されません（宣言しなければ0Lになります）。互換性は、コンポーネントの名前と型で決まります。コンポーネントを追加しても、古いデータに無いものは既定値（nullや0）で正規コンストラクターに渡されます。

このプロジェクトを通じて、Recordsとデータ指向プログラミングの理論と実践を深く理解し、モダンなJavaアプリケーション開発のスキルを身につけることができます。Recordsは単なる構文糖ではなく、Javaプログラミングの新しいパラダイムを可能にする重要な機能です。