# 第12章 チャレンジ課題：レコード(Records)

## 概要
Recordを使った高度なデータモデリングとアーキテクチャ設計に挑戦します。イベントソーシング、CQRSパターン、代数的データ型など、実践的なパターンでRecordを活用します。

## 課題一覧

### 課題1: イベントソーシングシステムの実装
`EventSourcing.java`を作成し、以下を実装してください：

イベントソーシングパターンを使った銀行口座システムを構築します。

```java
// イベントの定義
public sealed interface AccountEvent {
    record AccountCreated(
        String accountId,
        String ownerName,
        LocalDateTime timestamp
    ) implements AccountEvent {}
    
    record MoneyDeposited(
        String accountId,
        BigDecimal amount,
        LocalDateTime timestamp
    ) implements AccountEvent {}
    
    record MoneyWithdrawn(
        String accountId,
        BigDecimal amount,
        LocalDateTime timestamp
    ) implements AccountEvent {}
    
    record AccountClosed(
        String accountId,
        String reason,
        LocalDateTime timestamp
    ) implements AccountEvent {}
}

// アカウントの状態
public record AccountState(
    String accountId,
    String ownerName,
    BigDecimal balance,
    boolean isActive,
    LocalDateTime lastModified
) {
    // イベントを適用して新しい状態を生成
    public AccountState apply(AccountEvent event) {
        return switch (event) {
            case AccountEvent.AccountCreated e -> 
                new AccountState(e.accountId(), e.ownerName(), 
                    BigDecimal.ZERO, true, e.timestamp());
            
            case AccountEvent.MoneyDeposited e -> 
                new AccountState(accountId, ownerName, 
                    balance.add(e.amount()), isActive, e.timestamp());
            
            case AccountEvent.MoneyWithdrawn e -> 
                new AccountState(accountId, ownerName, 
                    balance.subtract(e.amount()), isActive, e.timestamp());
            
            case AccountEvent.AccountClosed e -> 
                new AccountState(accountId, ownerName, 
                    balance, false, e.timestamp());
        };
    }
}

// イベントストア
public class EventStore {
    private final Map<String, List<AccountEvent>> events = new HashMap<>();
    
    public void append(String aggregateId, AccountEvent event) {
        events.computeIfAbsent(aggregateId, k -> new ArrayList<>()).add(event);
    }
    
    public AccountState replay(String accountId) {
        // すべてのイベントを再生して現在の状態を構築
    }
}
```

実装要件：
1. コマンドハンドラーの実装（CreateAccount、Deposit、Withdraw、CloseAccount）
2. イベントの永続化と再生
3. スナップショット機能
4. 監査ログの生成

### 課題2: 代数的データ型による式の評価器
`ExpressionEvaluator.java`を作成し、以下を実装してください：

```java
// 式の代数的データ型
public sealed interface Expr {
    record Const(double value) implements Expr {}
    record Var(String name) implements Expr {}
    record Add(Expr left, Expr right) implements Expr {}
    record Mul(Expr left, Expr right) implements Expr {}
    record Pow(Expr base, Expr exponent) implements Expr {}
    record Function(String name, List<Expr> args) implements Expr {}
}

// 評価コンテキスト
public record EvalContext(
    Map<String, Double> variables,
    Map<String, java.util.function.Function<List<Double>, Double>> functions
) {
    public static EvalContext standard() {
        return new EvalContext(
            Map.of(),
            Map.of(
                "sin", args -> Math.sin(args.get(0)),
                "cos", args -> Math.cos(args.get(0)),
                "max", args -> args.stream().max(Double::compare).orElse(0.0)
            )
        );
    }
}
```

実装すべき機能：
1. **式の評価器**
   ```java
   public static double eval(Expr expr, EvalContext context) {
       return switch (expr) {
           case Expr.Const(var value) -> value;
           // 他のケースを実装
       };
   }
   ```

2. **式の簡約化**
   ```java
   public static Expr simplify(Expr expr) {
       // x + 0 → x
       // x * 1 → x
       // x * 0 → 0
       // など
   }
   ```

3. **微分の実装**
   ```java
   public static Expr differentiate(Expr expr, String variable) {
       // 自動微分の実装
   }
   ```

4. **式のパーサー**
   ```java
   public static Expr parse(String expression) {
       // "2 * x + sin(y)" のような文字列をパース
   }
   ```

## 実装のヒント

### イベントソーシングのベストプラクティス
```java
// イベントのバージョニング
public record EventEnvelope<T extends AccountEvent>(
    T event,
    long version,
    Map<String, String> metadata
) {}

// イベントのプロジェクション
public interface Projection<T> {
    void handle(AccountEvent event);
    T getView();
}
```

### パターンマッチングの活用
```java
// ガード条件を使った評価
public static Expr optimize(Expr expr) {
    return switch (expr) {
        case Add(Const(0), var right) -> optimize(right);
        case Add(var left, Const(0)) -> optimize(left);
        case Mul(Const(0), var any) -> new Const(0);
        case Mul(var any, Const(0)) -> new Const(0);
        case Mul(Const(1), var right) -> optimize(right);
        case Mul(var left, Const(1)) -> optimize(left);
        default -> expr;
    };
}
```

## 提出前チェックリスト
- [ ] すべての必須機能が実装されている
- [ ] パターンマッチングを効果的に使用している
- [ ] 不変性が保たれている
- [ ] 包括的なテストケースが作成されている

## 評価基準
- 複雑なドメインモデルをRecordで適切に表現できているか
- イベントソーシングやCQRSなどのパターンを理解しているか
- 代数的データ型の概念を理解し活用できているか
- コードの拡張性と保守性が高いか

## ボーナス課題
時間に余裕がある場合は、以下の追加実装に挑戦してください：
- イベントソーシング：分散トランザクションのサポート
- 式評価器：最適化コンパイラの実装（式をバイトコードに変換）
- CQRSパターンの完全な実装（コマンドとクエリの分離）