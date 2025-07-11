# 第13章 発展課題：列挙型(Enums)

## 概要
基礎課題を完了した方向けの発展的な課題です。抽象メソッドを持つEnum、ステートマシンの実装、EnumSetとEnumMapの活用など、より高度なEnumパターンを学びます。

## 課題一覧

### 課題1: 抽象メソッドとポリモーフィズム
`PolymorphicEnum.java`を作成し、以下を実装してください：

1. **演算子のEnum**
   ```java
   public enum Operation {
       PLUS("+") {
           @Override
           public double apply(double x, double y) {
               return x + y;
           }
       },
       MINUS("-") {
           @Override
           public double apply(double x, double y) {
               return x - y;
           }
       },
       MULTIPLY("*") {
           @Override
           public double apply(double x, double y) {
               return x * y;
           }
       },
       DIVIDE("/") {
           @Override
           public double apply(double x, double y) {
               if (y == 0) throw new ArithmeticException("Division by zero");
               return x / y;
           }
       };
       
       private final String symbol;
       
       Operation(String symbol) {
           this.symbol = symbol;
       }
       
       public abstract double apply(double x, double y);
       
       // シンボルから演算子を取得するメソッドも実装
   }
   ```

2. **支払い方法のEnum**
   ```java
   public enum PaymentMethod {
       CREDIT_CARD {
           @Override
           public PaymentResult process(double amount) {
               // クレジットカード処理のロジック
           }
           
           @Override
           public double calculateFee(double amount) {
               return amount * 0.03; // 3%の手数料
           }
       },
       BANK_TRANSFER {
           // 銀行振込の実装
       },
       CASH {
           // 現金支払いの実装
       };
       
       public abstract PaymentResult process(double amount);
       public abstract double calculateFee(double amount);
   }
   ```

### 課題2: ステートマシンの実装
`StateMachine.java`を作成し、以下を実装してください：

1. **注文状態の管理**
   ```java
   public enum OrderState {
       PENDING {
           @Override
           public OrderState nextState(OrderEvent event) {
               return switch (event) {
                   case PAYMENT_RECEIVED -> PROCESSING;
                   case CANCEL -> CANCELLED;
                   default -> throw new IllegalStateException(
                       "Invalid event " + event + " for state " + this);
               };
           }
       },
       PROCESSING {
           @Override
           public OrderState nextState(OrderEvent event) {
               // 実装
           }
       },
       SHIPPED {
           // 実装
       },
       DELIVERED {
           // 実装
       },
       CANCELLED {
           @Override
           public OrderState nextState(OrderEvent event) {
               throw new IllegalStateException("Cannot transition from CANCELLED state");
           }
       };
       
       public abstract OrderState nextState(OrderEvent event);
       
       public boolean canTransitionTo(OrderState target) {
           // 遷移可能かをチェック
       }
   }
   
   public enum OrderEvent {
       PAYMENT_RECEIVED, PROCESS_COMPLETE, SHIP, DELIVER, CANCEL, RETURN
   }
   ```

2. **ワークフロー実行エンジン**
   - 現在の状態を保持
   - イベントを受け取って状態遷移
   - 遷移履歴の記録
   - 不正な遷移の検出

### 課題3: EnumSetとEnumMapの活用
`EnumCollections.java`を作成し、以下を実装してください：

1. **権限管理システム**
   ```java
   public enum Permission {
       READ, WRITE, DELETE, EXECUTE, ADMIN
   }
   
   public class User {
       private final String name;
       private final EnumSet<Permission> permissions;
       
       // EnumSetを使った権限管理
       public boolean hasPermission(Permission permission) {
           return permissions.contains(permission);
       }
       
       public void grantPermissions(Permission... perms) {
           permissions.addAll(Arrays.asList(perms));
       }
       
       public void revokePermission(Permission permission) {
           permissions.remove(permission);
       }
       
       // 複数の権限をチェック
       public boolean hasAllPermissions(Permission... required) {
           return permissions.containsAll(Arrays.asList(required));
       }
   }
   ```

2. **設定管理システム**
   ```java
   public enum ConfigKey {
       DATABASE_URL, API_KEY, MAX_CONNECTIONS, TIMEOUT, LOG_LEVEL
   }
   
   public class Configuration {
       private final EnumMap<ConfigKey, String> settings;
       
       // EnumMapを使った効率的な設定管理
       public String get(ConfigKey key) {
           return settings.get(key);
       }
       
       public void set(ConfigKey key, String value) {
           settings.put(key, value);
       }
       
       // 型安全な設定取得
       public int getInt(ConfigKey key) {
           String value = settings.get(key);
           return value != null ? Integer.parseInt(value) : 0;
       }
   }
   ```

## 実装のヒント

### 抽象メソッドの定義
```java
public enum Shape {
    CIRCLE {
        @Override
        public double area(double... dimensions) {
            double radius = dimensions[0];
            return Math.PI * radius * radius;
        }
    },
    RECTANGLE {
        @Override
        public double area(double... dimensions) {
            double width = dimensions[0];
            double height = dimensions[1];
            return width * height;
        }
    };
    
    public abstract double area(double... dimensions);
}
```

### EnumSetの効率的な使用
```java
// 複数の要素を含むEnumSetの作成
EnumSet<DayOfWeek> weekdays = EnumSet.range(
    DayOfWeek.MONDAY, DayOfWeek.FRIDAY
);

// 補集合の作成
EnumSet<DayOfWeek> weekend = EnumSet.complementOf(weekdays);

// 条件に基づくフィルタリング
EnumSet<Permission> readOnlyPerms = EnumSet.of(Permission.READ);
EnumSet<Permission> adminPerms = EnumSet.allOf(Permission.class);
```

## 提出前チェックリスト
- [ ] 抽象メソッドが適切に実装されている
- [ ] ステートマシンが正しく動作する
- [ ] EnumSetとEnumMapを効果的に使用している
- [ ] エラーハンドリングが適切に実装されている

## 評価基準
- Enumのポリモーフィズムを理解し活用できているか
- ステートマシンパターンを正しく実装できているか
- EnumSetとEnumMapの特性を理解し適切に使用できているか
- 設計の拡張性と保守性が高いか