# 第14章 チャレンジ課題：例外処理

## 概要
例外処理を使った高度なエラーハンドリングシステムの構築に挑戦します。分散システムでの例外処理、非同期処理での例外管理、エラーリカバリーシステムなど、実践的なシステムを実装します。

## 課題一覧

### 課題1: 分散トランザクション管理システム
`DistributedTransaction.java`を作成し、以下を実装してください：

複数のサービスにまたがるトランザクションを管理するシステムを構築します。

```java
// トランザクション参加者インターフェース
public interface TransactionParticipant {
    void prepare() throws TransactionException;
    void commit() throws TransactionException;
    void rollback() throws TransactionException;
    String getParticipantId();
}

// トランザクション例外階層
public class TransactionException extends Exception {
    public TransactionException(String message) {
        super(message);
    }
    
    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}

public class PrepareFailedException extends TransactionException {
    private final String participantId;
    
    public PrepareFailedException(String participantId, String message, Throwable cause) {
        super(message, cause);
        this.participantId = participantId;
    }
}

// 2フェーズコミットコーディネーター
public class TransactionCoordinator {
    private final List<TransactionParticipant> participants;
    private final Map<String, TransactionState> participantStates;
    
    public enum TransactionState {
        INITIAL, PREPARING, PREPARED, COMMITTING, COMMITTED, ABORTING, ABORTED
    }
    
    public void executeTransaction(Runnable businessLogic) throws TransactionException {
        String transactionId = generateTransactionId();
        List<String> preparedParticipants = new ArrayList<>();
        
        try {
            // Phase 1: Prepare
            for (TransactionParticipant participant : participants) {
                try {
                    participant.prepare();
                    preparedParticipants.add(participant.getParticipantId());
                    participantStates.put(participant.getParticipantId(), 
                                        TransactionState.PREPARED);
                } catch (Exception e) {
                    throw new PrepareFailedException(
                        participant.getParticipantId(),
                        "Prepare failed for participant: " + participant.getParticipantId(),
                        e
                    );
                }
            }
            
            // ビジネスロジック実行
            businessLogic.run();
            
            // Phase 2: Commit
            commitAll(preparedParticipants);
            
        } catch (Exception e) {
            // ロールバック処理
            rollbackAll(preparedParticipants);
            throw new TransactionException("Transaction failed: " + transactionId, e);
        }
    }
    
    private void commitAll(List<String> participantIds) throws TransactionException {
        List<Exception> commitFailures = new ArrayList<>();
        
        for (String participantId : participantIds) {
            try {
                TransactionParticipant participant = findParticipant(participantId);
                participant.commit();
                participantStates.put(participantId, TransactionState.COMMITTED);
            } catch (Exception e) {
                commitFailures.add(new TransactionException(
                    "Commit failed for " + participantId, e));
            }
        }
        
        if (!commitFailures.isEmpty()) {
            throw new CompositeTransactionException(
                "Some commits failed", commitFailures);
        }
    }
}
```

### 課題2: 非同期処理での例外管理
`AsyncExceptionHandling.java`を作成し、以下を実装してください：

```java
// 非同期タスクの結果を表す型
public sealed interface AsyncResult<T> {
    record Success<T>(T value) implements AsyncResult<T> {}
    record Failure<T>(Exception error) implements AsyncResult<T> {}
}

// 非同期例外ハンドラー
public class AsyncExceptionHandler {
    private final ExecutorService executor;
    private final List<Consumer<Exception>> globalHandlers;
    
    public <T> CompletableFuture<T> executeAsync(
            Supplier<T> task,
            Function<Exception, T> fallback) {
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                return task.get();
            } catch (Exception e) {
                notifyGlobalHandlers(e);
                if (fallback != null) {
                    return fallback.apply(e);
                }
                throw new CompletionException(e);
            }
        }, executor);
    }
    
    // タイムアウト付き実行
    public <T> CompletableFuture<T> executeWithTimeout(
            Supplier<T> task,
            long timeout,
            TimeUnit unit) {
        
        CompletableFuture<T> future = executeAsync(task, null);
        
        return future.applyToEither(
            timeoutAfter(timeout, unit),
            Function.identity()
        ).exceptionally(throwable -> {
            if (throwable instanceof TimeoutException) {
                throw new CompletionException(
                    new TaskTimeoutException("Task timed out after " + 
                                           timeout + " " + unit));
            }
            throw new CompletionException(throwable);
        });
    }
    
    // バルクヘッド（隔離）パターン
    public class BulkheadExecutor {
        private final Semaphore semaphore;
        private final int maxConcurrency;
        
        public <T> CompletableFuture<T> execute(Supplier<T> task) {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    if (!semaphore.tryAcquire(1, TimeUnit.SECONDS)) {
                        throw new BulkheadRejectedException(
                            "Bulkhead full, rejecting task");
                    }
                    
                    try {
                        return task.get();
                    } finally {
                        semaphore.release();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new CompletionException(e);
                }
            }, executor);
        }
    }
}
```

### 課題3: エラーリカバリーシステム
`ErrorRecoverySystem.java`を作成し、以下を実装してください：

```java
// リカバリー戦略
public interface RecoveryStrategy<T> {
    T recover(Exception error, RecoveryContext context) throws Exception;
}

// リカバリーコンテキスト
public class RecoveryContext {
    private final int attemptNumber;
    private final List<Exception> previousErrors;
    private final Map<String, Object> metadata;
    
    // getter/setter
}

// 高度なリカバリーマネージャー
public class RecoveryManager {
    private final Map<Class<? extends Exception>, List<RecoveryStrategy<?>>> strategies;
    
    public <T> T executeWithRecovery(
            CheckedSupplier<T> operation,
            Class<T> resultType) throws UnrecoverableException {
        
        RecoveryContext context = new RecoveryContext();
        Exception lastError = null;
        
        // 初回実行
        try {
            return operation.get();
        } catch (Exception e) {
            lastError = e;
            context.addError(e);
        }
        
        // リカバリー試行
        List<RecoveryStrategy<?>> applicableStrategies = 
            findApplicableStrategies(lastError);
        
        for (RecoveryStrategy<?> strategy : applicableStrategies) {
            try {
                @SuppressWarnings("unchecked")
                RecoveryStrategy<T> typedStrategy = (RecoveryStrategy<T>) strategy;
                T result = typedStrategy.recover(lastError, context);
                
                // リカバリー成功をログ
                logRecoverySuccess(lastError, strategy, context);
                return result;
                
            } catch (Exception recoveryError) {
                context.addError(recoveryError);
                lastError = recoveryError;
            }
        }
        
        // すべてのリカバリー戦略が失敗
        throw new UnrecoverableException(
            "All recovery strategies failed", 
            context.getAllErrors()
        );
    }
    
    // 実装すべきリカバリー戦略
    public static class RetryWithBackoffStrategy<T> implements RecoveryStrategy<T> {
        // 指数バックオフでリトライ
    }
    
    public static class FallbackValueStrategy<T> implements RecoveryStrategy<T> {
        // デフォルト値を返す
    }
    
    public static class CacheStrategy<T> implements RecoveryStrategy<T> {
        // キャッシュから値を取得
    }
    
    public static class CircuitBreakerStrategy<T> implements RecoveryStrategy<T> {
        // サーキットブレーカーパターン
    }
}
```

## 実装のヒント

### 複合例外の処理
```java
public class CompositeException extends Exception {
    private final List<Exception> exceptions;
    
    public CompositeException(String message, List<Exception> exceptions) {
        super(message);
        this.exceptions = new ArrayList<>(exceptions);
        
        // すべての例外をsuppressedとして追加
        exceptions.forEach(this::addSuppressed);
    }
    
    public void printStackTrace(PrintWriter pw) {
        super.printStackTrace(pw);
        pw.println("Composite exception contains " + exceptions.size() + " exceptions:");
        for (int i = 0; i < exceptions.size(); i++) {
            pw.println("Exception " + (i + 1) + ":");
            exceptions.get(i).printStackTrace(pw);
        }
    }
}
```

### 構造化された例外ログ
```java
public class StructuredExceptionLogger {
    public void logException(Exception e, Map<String, Object> context) {
        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put("timestamp", Instant.now());
        logEntry.put("exception_class", e.getClass().getName());
        logEntry.put("message", e.getMessage());
        logEntry.put("stack_trace", getStackTraceAsString(e));
        logEntry.put("context", context);
        
        if (e.getCause() != null) {
            logEntry.put("cause", buildCauseChain(e.getCause()));
        }
        
        // JSON形式でログ出力
        logger.error(toJson(logEntry));
    }
}
```

## 提出前チェックリスト
- [ ] 分散トランザクションが正しく動作する
- [ ] 非同期例外が適切に処理される
- [ ] リカバリー戦略が効果的に機能する
- [ ] 包括的なテストケースが作成されている

## 評価基準
- 複雑な例外処理シナリオを適切に設計できているか
- 分散システムの課題を理解し対処できているか
- 非同期処理での例外を適切に管理できているか
- リカバリーメカニズムが実用的で拡張可能か

## ボーナス課題
時間に余裕がある場合は、以下の追加実装に挑戦してください：
- 分散トレーシング：例外の伝播を複数サービス間で追跡
- 例外メトリクス：例外の発生頻度や種類を監視・分析
- 自己修復システム：特定の例外パターンを検出して自動的に修復