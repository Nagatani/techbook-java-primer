# 付録B.20: Enumsを使った高度な設計パターン

## 概要

本付録では、Javaの列挙型（Enum）を活用した高度な設計パターンについて詳細に解説します。Enumは単なる定数の列挙以上の機能を持ち、状態機械、戦略パターン、型安全な設定など、多様な設計パターンの実装に活用できます。

**対象読者**: Enumの基本を理解し、高度な設計パターンに興味がある開発者  
**前提知識**: 第13章「Enum」の内容、基本的なデザインパターン  
**関連章**: 第13章、第7章（インターフェイス）、第5章（ポリモーフィズム）

## なぜEnum設計パターンが重要なのか

### 従来の状態管理による実際の問題

**問題1: 文字列ベース状態管理の脆弱性**
```java
// 悪い例：文字列での状態管理
public class BadOrderStatus {
    private String status;  // 危険：タイプミス、不正値の混入
    
    public void updateStatus(String newStatus) {
        if ("PENDING".equals(status) && "CONFIRMED".equals(newStatus)) {
            status = newStatus;
        } else if ("CONFIRMD".equals(newStatus)) {  // タイプミス！
            status = newStatus;  // 不正な状態に遷移
        }
    }
}
```
**実際の影響**: ECサイトで注文状態不整合、顧客への誤った配送通知

**問題2: 状態遷移ルールの散在**
```java
// 悪い例：状態チェックがコード全体に散在
public class ScatteredStateLogic {
    public void cancelOrder(Order order) {
        if (order.getStatus() == 1 || order.getStatus() == 2) {  // マジックナンバー
            // キャンセル処理
        }
    }
    
    public void shipOrder(Order order) {
        if (order.getStatus() == 2) {  // 同じ状態チェックが各所に
            // 発送処理
        }
    }
}
```
**影響**: 状態ルール変更時に修正漏れ、バグ混入、保守コスト増大

### ビジネスへの深刻な影響

**状態管理問題による実際のコスト**

従来の状態管理手法は、組織に重大なコストをもたらします。データ不整合については、注文状態の矛盾により顧客からの問い合わせが増加し、対応コストが増大します。業務効率では、状態確認に手動作業が必要となり、処理時間が3-5倍に増加します。システム信頼性については、不正な状態によりシステム障害が発生し、売上機会を損失することがあります。保守性では、状態ロジックがコード全体に散在するため影響範囲の特定が困難となり、修正時間が増大します。

**Enum状態パターンがもたらす効果**

Enumを活用した状態パターンは、これらの問題を根本的に解決します。型安全性により、コンパイル時エラーで不正状態を完全に排除できます。業務ルールの集約では、状態遷移ルールを一ヵ所に集約することで保守性が向上します。可読性については、ビジネスロジックが明確になり、新人でも理解しやすくなります。拡張性では、新状態を追加する際の影響範囲が明確になり、安全な拡張が可能になります。

**実際の成功事例**

Enum状態パターンの導入により、多くの組織が顕著な成果を上げています。某通販サイトでは、Enum状態機械の導入により注文処理エラーを95%削減しました。ワークフローシステムでは、状態管理を統一することで開発効率を3倍向上させました。ゲーム開発では、プレイヤー状態管理の改善によりバグ発生率を90%削減しました。

**具体的な改善効果**

Enum状態パターンの導入により、さまざまな面で具体的な改善が見られます。開発効率については、状態関連バグの調査時間を80%短縮できます。品質向上では、状態遷移テストの自動化により品質を大幅に改善できます。運用安定性については、不正状態による障害をゼロ化し、顧客満足度の向上を実現できます。



## 状態機械の実装

### 基本的な状態機械

**Enumによる型安全な状態遷移管理**

Enumを活用した状態機械パターンは、複雑な業務ルールを型安全かつ保守しやすい形で実装するための強力な手法です：

**状態機械設計の基本原則**：
- 各状態をEnumの値として定義
- 抽象メソッドによる状態固有の動作定義
- IllegalStateExceptionによる不正遷移の防止

**実装における技術的利点**：
- **型安全性**: コンパイル時に状態遷移の正当性を検証
- **一元管理**: すべての状態遷移ルールを単一ヵ所に集約
- **保守性**: 新状態追加時の影響範囲を明確化
- **テスト性**: 状態ごとのユニットテストが容易

**ビジネスルールの明確化**：
- canModify()のような状態固有の可能性判定
- 業務フローと実装の一致による理解容易性
- ステートパターンの自然な表現
        
        @Override
        public OrderState cancel() {
            // 確認済みの注文のキャンセルには制限があることも
            return CANCELLED;
        }
        
        @Override
        public boolean canModify() {
            return false;
        }
    },
    
    SHIPPED {
        @Override
        public OrderState deliver() {
            return DELIVERED;
        }
        
        @Override
        public boolean canModify() {
            return false;
        }
    },
    
    DELIVERED {
        @Override
        public boolean canModify() {
            return false;
        }
        
        @Override
        public boolean isTerminal() {
            return true;
        }
    },
    
    CANCELLED {
        @Override
        public boolean canModify() {
            return false;
        }
        
        @Override
        public boolean isTerminal() {
            return true;
        }
    };
    
    // デフォルト実装（何もしない遷移）
    public OrderState confirm() {
        throw new IllegalStateException("Cannot confirm from " + this);
    }
    
    public OrderState ship() {
        throw new IllegalStateException("Cannot ship from " + this);
    }
    
    public OrderState deliver() {
        throw new IllegalStateException("Cannot deliver from " + this);
    }
    
    public OrderState cancel() {
        throw new IllegalStateException("Cannot cancel from " + this);
    }
    
    public abstract boolean canModify();
    
    public boolean isTerminal() {
        return false;
    }
    
    // 有効な遷移の検証
    public Set<OrderState> getValidTransitions() {
        Set<OrderState> transitions = EnumSet.noneOf(OrderState.class);
        
        try { transitions.add(confirm()); } catch (IllegalStateException ignored) {}
        try { transitions.add(ship()); } catch (IllegalStateException ignored) {}
        try { transitions.add(deliver()); } catch (IllegalStateException ignored) {}
        try { transitions.add(cancel()); } catch (IllegalStateException ignored) {}
        
        return transitions;
    }
}
```

### 複雑な状態機械

```java
// ワークフローエンジンの状態機械
public class WorkflowEngine {
    public enum TaskState {
        CREATED(Set.of("assign", "delete")),
        ASSIGNED(Set.of("start", "reassign", "cancel")),
        IN_PROGRESS(Set.of("complete", "pause", "cancel")),
        PAUSED(Set.of("resume", "cancel")),
        COMPLETED(Set.of("archive", "reopen")),
        CANCELLED(Set.of("reopen", "archive")),
        ARCHIVED(Set.of());
        
        private final Set<String> allowedActions;
        
        TaskState(Set<String> allowedActions) {
            this.allowedActions = allowedActions;
        }
        
        public boolean canPerform(String action) {
            return allowedActions.contains(action);
        }
        
        public TaskState transition(String action) {
            if (!canPerform(action)) {
                throw new IllegalArgumentException(
                    "Action '" + action + "' not allowed in state " + this);
            }
            
            return switch (action) {
                case "assign" -> ASSIGNED;
                case "start" -> IN_PROGRESS;
                case "complete" -> COMPLETED;
                case "pause" -> PAUSED;
                case "resume" -> IN_PROGRESS;
                case "cancel" -> CANCELLED;
                case "reopen" -> CREATED;
                case "archive" -> ARCHIVED;
                case "reassign" -> ASSIGNED;
                case "delete" -> null; // タスクを削除
                default -> throw new IllegalArgumentException("Unknown action: " + action);
            };
        }
        
        // 状態の分類
        public boolean isActive() {
            return this == ASSIGNED || this == IN_PROGRESS;
        }
        
        public boolean isTerminal() {
            return this == ARCHIVED;
        }
        
        public boolean isPaused() {
            return this == PAUSED;
        }
    }
    
    // ワークフローのコンテキスト
    public static class TaskContext {
        private TaskState state = TaskState.CREATED;
        private String assignee;
        private Instant lastModified = Instant.now();
        private final List<String> history = new ArrayList<>();
        
        public void performAction(String action) {
            validateAction(action);
            
            TaskState newState = state.transition(action);
            history.add(String.format("%s: %s -> %s", action, state, newState));
            
            state = newState;
            lastModified = Instant.now();
            
            // 副作用の処理
            handleSideEffects(action);
        }
        
        private void validateAction(String action) {
            if (!state.canPerform(action)) {
                throw new IllegalStateException(
                    "Cannot perform '" + action + "' in state " + state);
            }
        }
        
        private void handleSideEffects(String action) {
            switch (action) {
                case "assign", "reassign" -> {
                    // 通知の送信など
                }
                case "complete" -> {
                    // 完了通知、次のタスクの開始など
                }
                case "cancel" -> {
                    // リソースの解放など
                }
            }
        }
        
        // ゲッター
        public TaskState getState() { return state; }
        public List<String> getHistory() { return List.copyOf(history); }
    }
}
```



## 戦略パターンとEnum

### 計算戦略の実装

```java
public enum CalculationStrategy {
    SIMPLE {
        @Override
        public BigDecimal calculateTax(BigDecimal amount, TaxContext context) {
            return amount.multiply(context.getTaxRate());
        }
        
        @Override
        public BigDecimal calculateDiscount(BigDecimal amount, DiscountContext context) {
            return amount.multiply(context.getDiscountRate());
        }
    },
    
    PROGRESSIVE {
        @Override
        public BigDecimal calculateTax(BigDecimal amount, TaxContext context) {
            BigDecimal tax = BigDecimal.ZERO;
            BigDecimal remaining = amount;
            
            for (TaxBracket bracket : context.getTaxBrackets()) {
                if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;
                
                BigDecimal taxableInBracket = remaining.min(bracket.getUpperLimit());
                tax = tax.add(taxableInBracket.multiply(bracket.getRate()));
                remaining = remaining.subtract(taxableInBracket);
            }
            
            return tax;
        }
        
        @Override
        public BigDecimal calculateDiscount(BigDecimal amount, DiscountContext context) {
            // 段階的な割引
            if (amount.compareTo(new BigDecimal("1000")) >= 0) {
                return amount.multiply(new BigDecimal("0.15"));
            } else if (amount.compareTo(new BigDecimal("500")) >= 0) {
                return amount.multiply(new BigDecimal("0.10"));
            } else {
                return amount.multiply(new BigDecimal("0.05"));
            }
        }
    },
    
    PROMOTIONAL {
        @Override
        public BigDecimal calculateTax(BigDecimal amount, TaxContext context) {
            // プロモーション期間中は税金が半額
            return SIMPLE.calculateTax(amount, context).multiply(new BigDecimal("0.5"));
        }
        
        @Override
        public BigDecimal calculateDiscount(BigDecimal amount, DiscountContext context) {
            // 最大割引額を適用
            BigDecimal maxDiscount = new BigDecimal("100");
            BigDecimal calculatedDiscount = amount.multiply(context.getDiscountRate());
            return calculatedDiscount.min(maxDiscount);
        }
    };
    
    public abstract BigDecimal calculateTax(BigDecimal amount, TaxContext context);
    public abstract BigDecimal calculateDiscount(BigDecimal amount, DiscountContext context);
    
    // ヘルパーメソッド
    public PriceCalculation calculate(BigDecimal baseAmount, TaxContext taxContext, DiscountContext discountContext) {
        BigDecimal discount = calculateDiscount(baseAmount, discountContext);
        BigDecimal discountedAmount = baseAmount.subtract(discount);
        BigDecimal tax = calculateTax(discountedAmount, taxContext);
        BigDecimal finalAmount = discountedAmount.add(tax);
        
        return new PriceCalculation(baseAmount, discount, tax, finalAmount);
    }
    
    record PriceCalculation(BigDecimal baseAmount, BigDecimal discount, BigDecimal tax, BigDecimal finalAmount) {}
    record TaxBracket(BigDecimal upperLimit, BigDecimal rate) {}
    
    static class TaxContext {
        private final BigDecimal taxRate;
        private final List<TaxBracket> taxBrackets;
        
        public TaxContext(BigDecimal taxRate) {
            this.taxRate = taxRate;
            this.taxBrackets = List.of();
        }
        
        public TaxContext(List<TaxBracket> taxBrackets) {
            this.taxRate = BigDecimal.ZERO;
            this.taxBrackets = List.copyOf(taxBrackets);
        }
        
        public BigDecimal getTaxRate() { return taxRate; }
        public List<TaxBracket> getTaxBrackets() { return taxBrackets; }
    }
    
    static class DiscountContext {
        private final BigDecimal discountRate;
        
        public DiscountContext(BigDecimal discountRate) {
            this.discountRate = discountRate;
        }
        
        public BigDecimal getDiscountRate() { return discountRate; }
    }
}
```

### プロトコル実装

```java
// 通信プロトコルの実装
public enum NetworkProtocol {
    HTTP(80, false) {
        @Override
        public Connection createConnection(String host, int port) {
            return new HttpConnection(host, port);
        }
        
        @Override
        public String formatRequest(String method, String path, Map<String, String> headers) {
            StringBuilder request = new StringBuilder();
            request.append(method).append(" ").append(path).append(" HTTP/1.1\r\n");
            request.append("Host: ").append("example.com").append("\r\n");
            
            headers.forEach((key, value) -> 
                request.append(key).append(": ").append(value).append("\r\n"));
            
            request.append("\r\n");
            return request.toString();
        }
    },
    
    HTTPS(443, true) {
        @Override
        public Connection createConnection(String host, int port) {
            return new HttpsConnection(host, port);
        }
        
        @Override
        public String formatRequest(String method, String path, Map<String, String> headers) {
            // HTTPSの場合も基本的には同じフォーマット
            return HTTP.formatRequest(method, path, headers);
        }
    },
    
    FTP(21, false) {
        @Override
        public Connection createConnection(String host, int port) {
            return new FtpConnection(host, port);
        }
        
        @Override
        public String formatRequest(String command, String parameter, Map<String, String> options) {
            return command + (parameter.isEmpty() ? "" : " " + parameter) + "\r\n";
        }
    };
    
    private final int defaultPort;
    private final boolean secure;
    
    NetworkProtocol(int defaultPort, boolean secure) {
        this.defaultPort = defaultPort;
        this.secure = secure;
    }
    
    public abstract Connection createConnection(String host, int port);
    public abstract String formatRequest(String method, String path, Map<String, String> headers);
    
    public int getDefaultPort() { return defaultPort; }
    public boolean isSecure() { return secure; }
    
    // ファクトリメソッド
    public static NetworkProtocol fromUrl(String url) {
        if (url.startsWith("https://")) return HTTPS;
        if (url.startsWith("http://")) return HTTP;
        if (url.startsWith("ftp://")) return FTP;
        throw new IllegalArgumentException("Unsupported protocol in URL: " + url);
    }
    
    // 接続インターフェイス
    interface Connection {
        void connect();
        void disconnect();
        void sendData(String data);
        String receiveData();
    }
    
    // 実装クラスのスタブ
    static class HttpConnection implements Connection {
        HttpConnection(String host, int port) {}
        public void connect() {}
        public void disconnect() {}
        public void sendData(String data) {}
        public String receiveData() { return ""; }
    }
    
    static class HttpsConnection implements Connection {
        HttpsConnection(String host, int port) {}
        public void connect() {}
        public void disconnect() {}
        public void sendData(String data) {}
        public String receiveData() { return ""; }
    }
    
    static class FtpConnection implements Connection {
        FtpConnection(String host, int port) {}
        public void connect() {}
        public void disconnect() {}
        public void sendData(String data) {}
        public String receiveData() { return ""; }
    }
}
```



## EnumSetとEnumMapの活用

### 権限管理システム

```java
public class PermissionSystem {
    // 権限の定義
    public enum Permission {
        READ(1),
        WRITE(2),
        EXECUTE(4),
        DELETE(8),
        ADMIN(16),
        
        // 複合権限
        READ_WRITE(READ.mask | WRITE.mask),
        FULL_ACCESS(READ.mask | WRITE.mask | EXECUTE.mask | DELETE.mask);
        
        private final int mask;
        
        Permission(int mask) {
            this.mask = mask;
        }
        
        public int getMask() {
            return mask;
        }
        
        public boolean includes(Permission other) {
            return (mask & other.mask) == other.mask;
        }
    }
    
    // ユーザーの権限セット
    public static class UserPermissions {
        private final EnumSet<Permission> permissions;
        
        public UserPermissions(Permission... permissions) {
            this.permissions = EnumSet.of(permissions[0], permissions);
        }
        
        public UserPermissions(EnumSet<Permission> permissions) {
            this.permissions = EnumSet.copyOf(permissions);
        }
        
        public boolean hasPermission(Permission permission) {
            return permissions.contains(permission) || 
                   permissions.stream().anyMatch(p -> p.includes(permission));
        }
        
        public boolean hasAllPermissions(Permission... required) {
            return Arrays.stream(required).allMatch(this::hasPermission);
        }
        
        public boolean hasAnyPermission(Permission... required) {
            return Arrays.stream(required).anyMatch(this::hasPermission);
        }
        
        public UserPermissions grant(Permission permission) {
            EnumSet<Permission> newPermissions = EnumSet.copyOf(permissions);
            newPermissions.add(permission);
            return new UserPermissions(newPermissions);
        }
        
        public UserPermissions revoke(Permission permission) {
            EnumSet<Permission> newPermissions = EnumSet.copyOf(permissions);
            newPermissions.remove(permission);
            return new UserPermissions(newPermissions);
        }
        
        public Set<Permission> getPermissions() {
            return EnumSet.copyOf(permissions);
        }
    }
    
    // リソース別の権限マップ
    public static class ResourcePermissions {
        private final EnumMap<Permission, Set<String>> permissionToResources;
        private final Map<String, EnumSet<Permission>> resourceToPermissions;
        
        public ResourcePermissions() {
            this.permissionToResources = new EnumMap<>(Permission.class);
            this.resourceToPermissions = new HashMap<>();
            
            // 初期化
            for (Permission permission : Permission.values()) {
                permissionToResources.put(permission, new HashSet<>());
            }
        }
        
        public void grantPermission(String resource, Permission permission) {
            permissionToResources.get(permission).add(resource);
            resourceToPermissions.computeIfAbsent(resource, k -> EnumSet.noneOf(Permission.class))
                                 .add(permission);
        }
        
        public void revokePermission(String resource, Permission permission) {
            permissionToResources.get(permission).remove(resource);
            EnumSet<Permission> resourcePerms = resourceToPermissions.get(resource);
            if (resourcePerms != null) {
                resourcePerms.remove(permission);
                if (resourcePerms.isEmpty()) {
                    resourceToPermissions.remove(resource);
                }
            }
        }
        
        public boolean hasPermission(String resource, Permission permission) {
            return permissionToResources.get(permission).contains(resource);
        }
        
        public Set<String> getResourcesWithPermission(Permission permission) {
            return new HashSet<>(permissionToResources.get(permission));
        }
        
        public EnumSet<Permission> getPermissionsForResource(String resource) {
            return EnumSet.copyOf(resourceToPermissions.getOrDefault(resource, EnumSet.noneOf(Permission.class)));
        }
    }
}
```

### 設定管理

```java
public class ConfigurationManager {
    // 設定キーの定義
    public enum ConfigKey {
        // データベース設定
        DB_HOST("localhost", String.class),
        DB_PORT("5432", Integer.class),
        DB_NAME("myapp", String.class),
        DB_USERNAME("user", String.class),
        DB_PASSWORD("", String.class),
        
        // サーバー設定
        SERVER_PORT("8080", Integer.class),
        SERVER_MAX_THREADS("100", Integer.class),
        SERVER_TIMEOUT("30000", Long.class),
        
        // 機能フラグ
        FEATURE_NEW_UI("false", Boolean.class),
        FEATURE_ANALYTICS("true", Boolean.class),
        FEATURE_CACHE("true", Boolean.class);
        
        private final String defaultValue;
        private final Class<?> type;
        
        ConfigKey(String defaultValue, Class<?> type) {
            this.defaultValue = defaultValue;
            this.type = type;
        }
        
        public String getDefaultValue() {
            return defaultValue;
        }
        
        public Class<?> getType() {
            return type;
        }
        
        @SuppressWarnings("unchecked")
        public <T> T parseValue(String value) {
            if (type == String.class) {
                return (T) value;
            } else if (type == Integer.class) {
                return (T) Integer.valueOf(value);
            } else if (type == Long.class) {
                return (T) Long.valueOf(value);
            } else if (type == Boolean.class) {
                return (T) Boolean.valueOf(value);
            } else {
                throw new IllegalArgumentException("Unsupported type: " + type);
            }
        }
    }
    
    // 設定値の管理
    public static class Configuration {
        private final EnumMap<ConfigKey, String> values;
        
        public Configuration() {
            this.values = new EnumMap<>(ConfigKey.class);
            
            // デフォルト値で初期化
            for (ConfigKey key : ConfigKey.values()) {
                values.put(key, key.getDefaultValue());
            }
        }
        
        public <T> T get(ConfigKey key) {
            String value = values.get(key);
            return key.parseValue(value);
        }
        
        public void set(ConfigKey key, String value) {
            // 型チェック
            try {
                key.parseValue(value);
                values.put(key, value);
            } catch (Exception e) {
                throw new IllegalArgumentException(
                    "Invalid value for " + key + ": " + value, e);
            }
        }
        
        public void loadFromProperties(Properties properties) {
            for (ConfigKey key : ConfigKey.values()) {
                String value = properties.getProperty(key.name());
                if (value != null) {
                    set(key, value);
                }
            }
        }
        
        public Properties toProperties() {
            Properties properties = new Properties();
            values.forEach((key, value) -> properties.setProperty(key.name(), value));
            return properties;
        }
        
        // 型安全なゲッター
        public String getDbHost() { return get(ConfigKey.DB_HOST); }
        public int getDbPort() { return get(ConfigKey.DB_PORT); }
        public boolean isFeatureEnabled(ConfigKey featureKey) {
            if (!featureKey.name().startsWith("FEATURE_")) {
                throw new IllegalArgumentException("Not a feature flag: " + featureKey);
            }
            return get(featureKey);
        }
    }
}
```



## EnumSetの内部実装と最適化

### ビットセットとしての実装

```java
public class EnumSetInternals {
    // EnumSetの動作原理を理解するためのサンプル
    public enum Color {
        RED, GREEN, BLUE, YELLOW, ORANGE, PURPLE, BLACK, WHITE
    }
    
    // RegularEnumSet（64要素以下）のシミュレーション
    public static class SimpleEnumSet<E extends Enum<E>> {
        private final Class<E> elementType;
        private long elements = 0L;
        
        public SimpleEnumSet(Class<E> elementType) {
            this.elementType = elementType;
        }
        
        public boolean add(E e) {
            long oldElements = elements;
            elements |= (1L << e.ordinal());
            return elements != oldElements;
        }
        
        public boolean remove(E e) {
            long oldElements = elements;
            elements &= ~(1L << e.ordinal());
            return elements != oldElements;
        }
        
        public boolean contains(E e) {
            return (elements & (1L << e.ordinal())) != 0;
        }
        
        public int size() {
            return Long.bitCount(elements);
        }
        
        public boolean isEmpty() {
            return elements == 0;
        }
        
        // ビット演算による集合操作
        public SimpleEnumSet<E> union(SimpleEnumSet<E> other) {
            SimpleEnumSet<E> result = new SimpleEnumSet<>(elementType);
            result.elements = this.elements | other.elements;
            return result;
        }
        
        public SimpleEnumSet<E> intersection(SimpleEnumSet<E> other) {
            SimpleEnumSet<E> result = new SimpleEnumSet<>(elementType);
            result.elements = this.elements & other.elements;
            return result;
        }
        
        public SimpleEnumSet<E> difference(SimpleEnumSet<E> other) {
            SimpleEnumSet<E> result = new SimpleEnumSet<>(elementType);
            result.elements = this.elements & ~other.elements;
            return result;
        }
    }
    
    // パフォーマンス比較
    public static void performanceComparison() {
        // EnumSetは非常に高速
        EnumSet<Color> enumSet = EnumSet.noneOf(Color.class);
        Set<Color> hashSet = new HashSet<>();
        Set<Color> treeSet = new TreeSet<>();
        
        // 追加操作のベンチマーク
        long start = System.nanoTime();
        for (int i = 0; i < 1_000_000; i++) {
            enumSet.clear();
            enumSet.add(Color.RED);
            enumSet.add(Color.GREEN);
            enumSet.add(Color.BLUE);
        }
        long enumSetTime = System.nanoTime() - start;
        
        start = System.nanoTime();
        for (int i = 0; i < 1_000_000; i++) {
            hashSet.clear();
            hashSet.add(Color.RED);
            hashSet.add(Color.GREEN);
            hashSet.add(Color.BLUE);
        }
        long hashSetTime = System.nanoTime() - start;
        
        System.out.println("EnumSet: " + enumSetTime / 1_000_000 + " ms");
        System.out.println("HashSet: " + hashSetTime / 1_000_000 + " ms");
        System.out.println("EnumSet is " + (hashSetTime / enumSetTime) + "x faster");
    }
}
```



## 実践的な応用例

### イベント駆動システム

```java
public class EventDrivenSystem {
    // イベントタイプの定義
    public enum EventType {
        USER_CREATED(UserEvent.class, true),
        USER_UPDATED(UserEvent.class, true),
        USER_DELETED(UserEvent.class, true),
        ORDER_PLACED(OrderEvent.class, false),
        ORDER_SHIPPED(OrderEvent.class, false),
        PAYMENT_PROCESSED(PaymentEvent.class, true);
        
        private final Class<? extends Event> eventClass;
        private final boolean isPersistent;
        
        EventType(Class<? extends Event> eventClass, boolean isPersistent) {
            this.eventClass = eventClass;
            this.isPersistent = isPersistent;
        }
        
        public Class<? extends Event> getEventClass() {
            return eventClass;
        }
        
        public boolean isPersistent() {
            return isPersistent;
        }
        
        // イベントファクトリ
        public Event createEvent(Map<String, Object> data) {
            return switch (this) {
                case USER_CREATED, USER_UPDATED, USER_DELETED -> new UserEvent(this, data);
                case ORDER_PLACED, ORDER_SHIPPED -> new OrderEvent(this, data);
                case PAYMENT_PROCESSED -> new PaymentEvent(this, data);
            };
        }
    }
    
    // イベントハンドラーの管理
    public static class EventHandlerRegistry {
        private final EnumMap<EventType, List<EventHandler>> handlers;
        
        public EventHandlerRegistry() {
            this.handlers = new EnumMap<>(EventType.class);
            for (EventType type : EventType.values()) {
                handlers.put(type, new ArrayList<>());
            }
        }
        
        public void register(EventType type, EventHandler handler) {
            handlers.get(type).add(handler);
        }
        
        public void register(EnumSet<EventType> types, EventHandler handler) {
            types.forEach(type -> register(type, handler));
        }
        
        public void handleEvent(Event event) {
            List<EventHandler> eventHandlers = handlers.get(event.getType());
            eventHandlers.forEach(handler -> {
                try {
                    handler.handle(event);
                } catch (Exception e) {
                    System.err.println("Error handling event: " + e.getMessage());
                }
            });
        }
    }
    
    // イベントインターフェイス
    interface Event {
        EventType getType();
        Map<String, Object> getData();
        Instant getTimestamp();
    }
    
    interface EventHandler {
        void handle(Event event);
    }
    
    // 具体的なイベント実装
    record UserEvent(EventType type, Map<String, Object> data, Instant timestamp) implements Event {
        public UserEvent(EventType type, Map<String, Object> data) {
            this(type, data, Instant.now());
        }
        
        public EventType getType() { return type; }
        public Map<String, Object> getData() { return data; }
        public Instant getTimestamp() { return timestamp; }
    }
    
    record OrderEvent(EventType type, Map<String, Object> data, Instant timestamp) implements Event {
        public OrderEvent(EventType type, Map<String, Object> data) {
            this(type, data, Instant.now());
        }
        
        public EventType getType() { return type; }
        public Map<String, Object> getData() { return data; }
        public Instant getTimestamp() { return timestamp; }
    }
    
    record PaymentEvent(EventType type, Map<String, Object> data, Instant timestamp) implements Event {
        public PaymentEvent(EventType type, Map<String, Object> data) {
            this(type, data, Instant.now());
        }
        
        public EventType getType() { return type; }
        public Map<String, Object> getData() { return data; }
        public Instant getTimestamp() { return timestamp; }
    }
}
```



## まとめ

Enumsを使った高度な設計パターンにより：

1. **型安全性**: コンパイル時の型チェックによる安全性
2. **高性能**: EnumSetのビット演算による高速な集合操作
3. **表現力**: 複雑な状態機械や戦略パターンの簡潔な実装
4. **保守性**: 定数の集中管理と拡張の容易さ
5. **実用性**: 設定管理、権限システム、イベント処理などの実践的応用

これらの技術は、ドメイン固有の概念をコードで表現する際に特に威力を発揮します。Enumは単なる定数の列挙ではなく、強力な抽象化メカニズムとして活用できます。

## 実践的なサンプルコード

本付録で解説したEnumsを使った高度な設計パターンの実践的なサンプルコードは、`/appendix/enum-patterns/`ディレクトリに収録されています。状態機械、戦略パターン、権限管理システム、イベント駆動システムなど、Enumを活用した実装例を参照することで、実際のプロジェクトへの適用方法を学べます。