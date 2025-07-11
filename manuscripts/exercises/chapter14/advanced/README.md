# 第14章 発展課題：例外処理

## 概要
基礎課題を完了した方向けの発展的な課題です。try-with-resources、マルチキャッチ、例外翻訳パターン、リトライメカニズムなど、より高度な例外処理技術を学びます。

## 課題一覧

### 課題1: try-with-resourcesの活用
`ResourceManagement.java`を作成し、以下を実装してください：

1. **複数リソースの管理**
   ```java
   public static void copyFile(String source, String destination) 
           throws IOException {
       try (BufferedReader reader = new BufferedReader(new FileReader(source));
            BufferedWriter writer = new BufferedWriter(new FileWriter(destination))) {
           String line;
           while ((line = reader.readLine()) != null) {
               writer.write(line);
               writer.newLine();
           }
       }
   }
   ```

2. **カスタムリソースクラス**
   ```java
   public class DatabaseConnection implements AutoCloseable {
       private final String connectionString;
       private boolean isOpen = false;
       
       public DatabaseConnection(String connectionString) {
           this.connectionString = connectionString;
           connect();
       }
       
       private void connect() {
           // 接続処理のシミュレーション
           System.out.println("Database connected: " + connectionString);
           isOpen = true;
       }
       
       public void executeQuery(String query) {
           if (!isOpen) {
               throw new IllegalStateException("Connection is closed");
           }
           System.out.println("Executing: " + query);
       }
       
       @Override
       public void close() throws Exception {
           if (isOpen) {
               System.out.println("Database connection closed");
               isOpen = false;
           }
       }
   }
   ```

3. **複雑なリソース管理**
   ```java
   public static void processTransaction() {
       try (DatabaseConnection db = new DatabaseConnection("jdbc:example");
            FileWriter log = new FileWriter("transaction.log");
            BufferedWriter logWriter = new BufferedWriter(log)) {
           
           db.executeQuery("BEGIN TRANSACTION");
           logWriter.write("Transaction started\n");
           
           // トランザクション処理
           
           db.executeQuery("COMMIT");
           logWriter.write("Transaction committed\n");
       } catch (Exception e) {
           // エラー処理とロールバック
       }
   }
   ```

### 課題2: 例外翻訳パターン
`ExceptionTranslation.java`を作成し、以下を実装してください：

1. **レイヤー別例外階層**
   ```java
   // データアクセス層の例外
   public class DataAccessException extends Exception {
       public DataAccessException(String message) {
           super(message);
       }
       
       public DataAccessException(String message, Throwable cause) {
           super(message, cause);
       }
   }
   
   // ビジネスロジック層の例外
   public class BusinessException extends Exception {
       public BusinessException(String message) {
           super(message);
       }
       
       public BusinessException(String message, Throwable cause) {
           super(message, cause);
       }
   }
   
   // プレゼンテーション層の例外
   public class PresentationException extends Exception {
       // 実装
   }
   ```

2. **例外翻訳の実装**
   ```java
   public class UserService {
       private final UserRepository repository;
       
       public User findUser(String id) throws BusinessException {
           try {
               return repository.findById(id);
           } catch (SQLException e) {
               // SQLExceptionをBusinessExceptionに翻訳
               throw new BusinessException(
                   "ユーザー情報の取得に失敗しました: " + id, e);
           } catch (DataAccessException e) {
               // より具体的な例外に翻訳
               throw new BusinessException(
                   "データベースアクセスエラー", e);
           }
       }
   }
   ```

### 課題3: リトライメカニズムの実装
`RetryMechanism.java`を作成し、以下を実装してください：

1. **シンプルなリトライ**
   ```java
   public class RetryUtil {
       public static <T> T retry(Supplier<T> operation, int maxAttempts) 
               throws Exception {
           Exception lastException = null;
           
           for (int attempt = 1; attempt <= maxAttempts; attempt++) {
               try {
                   return operation.get();
               } catch (Exception e) {
                   lastException = e;
                   if (attempt < maxAttempts) {
                       System.out.println("Attempt " + attempt + " failed. Retrying...");
                       Thread.sleep(1000 * attempt); // 指数バックオフ
                   }
               }
           }
           
           throw new Exception("Operation failed after " + maxAttempts + " attempts", 
                             lastException);
       }
   }
   ```

2. **高度なリトライポリシー**
   ```java
   public class RetryPolicy {
       private final int maxAttempts;
       private final long initialDelay;
       private final double backoffMultiplier;
       private final Set<Class<? extends Exception>> retryableExceptions;
       
       // Builder パターンで構築
       
       public <T> T execute(CheckedSupplier<T> operation) throws Exception {
           // 実装：
           // - 指定された例外のみリトライ
           // - 指数バックオフ
           // - 最大リトライ回数
       }
   }
   
   @FunctionalInterface
   public interface CheckedSupplier<T> {
       T get() throws Exception;
   }
   ```

## 実装のヒント

### try-with-resourcesの入れ子
```java
try (InputStream is = new FileInputStream("input.txt")) {
    try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
         BufferedReader reader = new BufferedReader(isr)) {
        // 処理
    }
}
```

### 例外の集約
```java
public class ValidationException extends Exception {
    private final List<String> errors;
    
    public ValidationException(List<String> errors) {
        super("Validation failed: " + String.join(", ", errors));
        this.errors = new ArrayList<>(errors);
    }
    
    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
```

### サーキットブレーカーパターン
```java
public class CircuitBreaker {
    private enum State { CLOSED, OPEN, HALF_OPEN }
    private State state = State.CLOSED;
    private int failureCount = 0;
    private final int threshold = 5;
    private long lastFailureTime;
    
    public <T> T execute(Supplier<T> operation) throws Exception {
        if (state == State.OPEN && !shouldAttemptReset()) {
            throw new Exception("Circuit breaker is OPEN");
        }
        
        try {
            T result = operation.get();
            onSuccess();
            return result;
        } catch (Exception e) {
            onFailure();
            throw e;
        }
    }
}
```

## 提出前チェックリスト
- [ ] try-with-resourcesが適切に使用されている
- [ ] 例外翻訳が適切に実装されている
- [ ] リトライメカニズムが正しく動作する
- [ ] リソースリークが発生しないことを確認

## 評価基準
- try-with-resourcesを効果的に使用できているか
- 例外翻訳パターンを理解し実装できているか
- リトライメカニズムが実用的か
- エラー処理の設計が適切か