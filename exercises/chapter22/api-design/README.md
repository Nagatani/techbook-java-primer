# 第22章: APIドキュメントとライブラリ設計

## 章の概要

この章では、保守性と拡張性に優れたライブラリの設計と、Javadocを使用した包括的なAPIドキュメンテーションの作成について学習します。実務レベルの開発で必要となる技術文書作成スキルを習得します。

## 学習目標

- Javadocによる包括的なAPIドキュメントが作成できる
- 再利用可能なライブラリを設計できる
- APIの使いやすさと拡張性を考慮した設計ができる
- 技術文書の作成と保守ができる
- オープンソースプロジェクトでの貢献ができる

## 課題構成

### basic/
APIドキュメンテーションの基礎スキルを習得するための課題です。

- **SimpleLibrary.java** - 基本的なJavadoc作成
- **UtilityClass.java** - ユーティリティクラスの文書化
- **ConfigurationAPI.java** - 設定APIの設計と文書化

### advanced/
小さなライブラリにJavadocを書き、APIの変更を利用者に伝える課題です。
このフォルダーのJavaファイルは解答例です。

- **課題1: 成績表ライブラリのAPIドキュメント**（`task1-scorebook/`） - 引数・戻り値・例外の約束をJavadocに書き、`javadoc -Xdoclint:all`で検査する
- **課題2: 非推奨にして新しいAPIへ移す**（`task2-deprecation/`） - `@since`・`@deprecated`・`@Deprecated`で、1.0の利用者を壊さずに1.1のAPIへ移す

### challenge/
ライブラリの公開APIを設計し、ドキュメントとともに仕上げる課題です。
このフォルダーのJavaファイルは解答例です。

- **課題1: ToDoリストのライブラリ** - 不変のレコード・変更できないリスト・`Optional`・例外を使い分けたAPIを設計し、`package-info.java`を含むJavadocを書き、別のプログラムからクラスパスで使う

### solutions/
各課題の完全な解答例と詳細な解説です。

- **APIDocumentation.java** - 包括的なJavadocドキュメンテーションの実装例
- **AdvancedAPIDocumentation.java** - 実務レベルの高度なAPI文書化例

## 課題の進め方

### 1. Javadocの基礎から開始
```bash
# 基本的なドキュメンテーション技法を理解
cd basic/
# SimpleLibrary.java から始めることを推奨
```

### 2. 実践的なライブラリ設計
```bash
# より複雑なAPIの設計と文書化
cd advanced/
# 実際のライブラリ開発フローを体験
```

### 3. ライブラリの設計
```bash
# 公開APIの設計とドキュメントの仕上げ
cd challenge/
# 自分で作ったライブラリを、別のプログラムからクラスパスで使う
```

## Javadocの基本構文

### クラスレベルドキュメント
```java
/**
 * ユーティリティクラスの説明
 * 
 * <p>このクラスは数学計算に関する便利なメソッドを提供します。
 * すべてのメソッドはスタティックで、スレッドセーフです。</p>
 * 
 * <h2>使用例</h2>
 * <pre>{@code
 * double result = MathUtils.calculateAverage(1.0, 2.0, 3.0);
 * IO.println("平均: " + result);
 * }</pre>
 * 
 * @author 開発者名
 * @version 1.2.0
 * @since 1.0
 * @see java.lang.Math
 */
public class MathUtils {
    // クラス実装
}
```

### メソッドレベルドキュメント
```java
/**
 * 配列の平均値を計算します。
 * 
 * <p>このメソッドは与えられた数値配列から算術平均を計算します。
 * 空の配列や null が渡された場合は例外が発生します。</p>
 * 
 * @param values 平均を計算する数値の配列（null不可、空不可）
 * @return 配列の算術平均値
 * @throws IllegalArgumentException 配列がnullまたは空の場合
 * @throws ArithmeticException 計算結果が無限大やNaNの場合
 * 
 * @since 1.0
 * @see #calculateMedian(double[]) 中央値計算メソッド
 * 
 * @example
 * <pre>{@code
 * double[] numbers = {1.0, 2.0, 3.0, 4.0, 5.0};
 * double avg = calculateAverage(numbers);
 * assert avg == 3.0;
 * }</pre>
 */
public static double calculateAverage(double[] values) {
    // メソッド実装
}
```

## ライブラリ設計の原則

### 1. SOLID原則の適用
```java
// S: Single Responsibility Principle
public class EmailValidator {
    public boolean isValid(String email) { /* 実装 */ }
}

// O: Open/Closed Principle
public abstract class MessageProcessor {
    public abstract void process(Message message);
}

// L: Liskov Substitution Principle
public interface PaymentProcessor {
    PaymentResult process(Payment payment);
}

// I: Interface Segregation Principle
public interface Readable {
    String read();
}
public interface Writable {
    void write(String data);
}

// D: Dependency Inversion Principle
public class OrderService {
    private final PaymentProcessor processor;
    public OrderService(PaymentProcessor processor) {
        this.processor = processor;
    }
}
```

### 2. APIの使いやすさ
```java
// 良い例: 流暢なインターフェイス
QueryBuilder query = new QueryBuilder()
    .select("name", "email")
    .from("users")
    .where("age").greaterThan(18)
    .orderBy("name")
    .limit(10);

// 良い例: ビルダーパターン
User user = User.builder()
    .name("山田太郎")
    .email("yamada@example.com")
    .age(30)
    .build();
```

### 3. エラーハンドリング
```java
/**
 * カスタム例外クラス
 * 
 * <p>このライブラリ固有のエラー状況を表現するために使用されます。</p>
 */
public class LibraryException extends Exception {
    
    /**
     * エラーコード
     */
    private final ErrorCode errorCode;
    
    /**
     * 例外を構築します。
     * 
     * @param errorCode エラーの種類を示すコード
     * @param message 詳細なエラーメッセージ
     * @param cause 原因となった例外（任意）
     */
    public LibraryException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
```

## 高度なJavadoc技法

### HTMLタグの活用
```java
/**
 * データベース接続プールの管理クラス
 * 
 * <h2>機能概要</h2>
 * <ul>
 * <li>接続プールの作成と管理</li>
 * <li>接続の自動リサイクル</li>
 * <li>パフォーマンス監視</li>
 * </ul>
 * 
 * <h2>設定パラメータ</h2>
 * <table border="1">
 * <caption>接続プール設定</caption>
 * <tr><th>パラメータ</th><th>デフォルト値</th><th>説明</th></tr>
 * <tr><td>minPoolSize</td><td>5</td><td>最小接続数</td></tr>
 * <tr><td>maxPoolSize</td><td>20</td><td>最大接続数</td></tr>
 * <tr><td>timeout</td><td>30秒</td><td>接続タイムアウト</td></tr>
 * </table>
 */
public class ConnectionPool {
    // 実装
}
```

### カスタムタグの使用
```java
/**
 * ユーザー認証サービス
 * 
 * @apiNote このAPIは認証情報を扱うため、セキュリティ要件に注意してください
 * @implNote 内部的にはBCryptを使用してパスワードをハッシュ化します
 * @performance 1秒間に約1000回の認証リクエストを処理可能
 * @threadsafety このクラスはスレッドセーフです
 */
public class AuthenticationService {
    // 実装
}
```

### {@code} と {@literal} の使い分け
```java
/**
 * JSONパーサーのユーティリティ
 * 
 * <p>JSON文字列を解析して {@code Map<String, Object>} として返します。</p>
 * 
 * <p>サポートされる形式の例:</p>
 * <pre>{@code
 * {
 * "name": "田中太郎",
 * "age": 30,
 * "active": true
 * }
 * }</pre>
 * 
 * <p>特殊文字の扱い: {@literal <tag>} のような文字列も正しく処理されます。</p>
 */
public class JSONParser {
    // 実装
}
```

## ドキュメント生成ツール

### Maven での Javadoc 生成
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-javadoc-plugin</artifactId>
    <version>3.4.1</version>
    <configuration>
        <source>11</source>
        <target>11</target>
        <encoding>UTF-8</encoding>
        <docencoding>UTF-8</docencoding>
        <charset>UTF-8</charset>
        <locale>ja_JP</locale>
        <windowTitle>My Library API</windowTitle>
        <doctitle>My Library API Documentation</doctitle>
    </configuration>
</plugin>
```

### Gradle での設定
```gradle
javadoc {
    options.encoding = 'UTF-8'
    options.charSet = 'UTF-8'
    options.locale = 'ja_JP'
    options.windowTitle = 'My Library API'
    options.docTitle = 'My Library API Documentation'
    
    // Java 11+ での警告抑制
    if(JavaVersion.current().isJava11Compatible()) {
        options.addBooleanOption('html5', true)
    }
}
```

## API設計のベストプラクティス

### 1. 一貫性のある命名
```java
// ○ 一貫した命名規則
public interface UserRepository {
    void createUser(User user);      // create + 名詞
    User findUserById(Long id);      // find + 名詞 + By + 条件
    void updateUser(User user);      // update + 名詞
    void deleteUser(Long id);        // delete + 名詞
}
```

### 2. 適切な抽象化レベル
```java
// ○ 適切な抽象化
public interface EmailService {
    void sendEmail(String to, String subject, String body);
    void sendEmailWithAttachment(String to, String subject, String body, Attachment attachment);
}

// × 低レベルすぎる抽象化
public interface EmailService {
    void openConnection();
    void authenticate(String username, String password);
    void setRecipient(String to);
    void setSubject(String subject);
    void setBody(String body);
    void send();
    void closeConnection();
}
```

### 3. 型安全性の確保
```java
// ○ 型安全なAPI設計
public class Configuration<T> {
    private final Class<T> type;
    private final T defaultValue;
    
    public Configuration(Class<T> type, T defaultValue) {
        this.type = type;
        this.defaultValue = defaultValue;
    }
    
    public T getValue(String key) {
        // 型安全な値の取得
        return type.cast(getValueInternal(key));
    }
    
    private Object getValueInternal(String key) {
        // 内部実装
        return defaultValue;
    }
}
```

## バージョニングとメンテナンス

### セマンティックバージョニング
```java
/**
 * ライブラリのバージョン情報
 * 
 * <p>このライブラリはセマンティックバージョニング（SemVer）に従います:</p>
 * <ul>
 * <li><strong>MAJOR</strong>: 互換性のない変更</li>
 * <li><strong>MINOR</strong>: 後方互換性のある機能追加</li>
 * <li><strong>PATCH</strong>: 後方互換性のあるバグ修正</li>
 * </ul>
 * 
 * @since 1.0.0
 */
public final class Version {
    public static final String CURRENT = "2.1.0";
    public static final int MAJOR = 2;
    public static final int MINOR = 1;
    public static final int PATCH = 0;
}
```

### 非推奨APIの管理
```java
/**
 * 旧バージョンとの互換性のためのメソッド
 * 
 * @param input 入力文字列
 * @return 処理結果
 * @deprecated バージョン 2.0.0 以降は {@link #processString(String, Options)} を使用してください。
 * このメソッドは 3.0.0 で削除予定です。
 */
@Deprecated(since = "2.0.0", forRemoval = true)
public String processString(String input) {
    return processString(input, Options.getDefault());
}
```

## ○ 評価基準

### 基本レベル (60-70点)
- [ ] 基本的なJavadocコメントが書ける
- [ ] クラスとメソッドの目的が明確に説明されている
- [ ] パラメータと戻り値が適切に文書化されている

### 応用レベル (70-85点)
- [ ] HTMLタグを使用した構造化された文書が作成できる
- [ ] 使用例とサンプルコードが含まれている
- [ ] 例外処理が適切に文書化されている
- [ ] APIの一貫性と使いやすさが考慮されている

### 発展レベル (85-100点)
- [ ] 包括的で実務レベルのAPIドキュメントが作成できる
- [ ] ライブラリ設計の原則が適用されている
- [ ] バージョニング戦略が適切に実装されている
- [ ] 拡張性と保守性が考慮された設計になっている

## 実務での応用

### オープンソースプロジェクト
- **GitHub Pages** での API ドキュメント公開
- **コントリビューション** ガイドラインの作成
- **Issue テンプレート** と **PR テンプレート** の整備

### 企業でのライブラリ開発
- **社内標準** に準拠したAPIガイドライン
- **レビュープロセス** でのドキュメント品質確認
- **自動生成** されるドキュメントのCI/CD統合



**ヒント**: 良いAPIドキュメントは、使用者が迷わずに目的を達成できることを目指します。サンプルコードは実際に動作するものを提供し、エラーケースも含めて説明することが重要です。