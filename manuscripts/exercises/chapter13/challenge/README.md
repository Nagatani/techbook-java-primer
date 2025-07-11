# 第13章 チャレンジ課題：列挙型(Enums)

## 概要
Enumを使った高度な設計パターンとアーキテクチャに挑戦します。ビジネスルールエンジン、DSL（ドメイン特化言語）の実装、複雑な状態遷移システムなど、実践的なシステムをEnumを中心に構築します。

## 課題一覧

### 課題1: ビジネスルールエンジンの実装
`BusinessRuleEngine.java`を作成し、以下を実装してください：

税金計算と割引ルールを管理する柔軟なビジネスルールエンジンを構築します。

```java
// 税金計算ルール
public enum TaxRule {
    STANDARD(0.10) {
        @Override
        public boolean applies(Product product) {
            return !product.isFood() && !product.isBook();
        }
    },
    REDUCED(0.08) {
        @Override
        public boolean applies(Product product) {
            return product.isFood() || product.isNewspaper();
        }
    },
    EXEMPT(0.0) {
        @Override
        public boolean applies(Product product) {
            return product.isMedical() || product.isEducational();
        }
    };
    
    private final double rate;
    
    TaxRule(double rate) {
        this.rate = rate;
    }
    
    public abstract boolean applies(Product product);
    
    public BigDecimal calculateTax(BigDecimal price) {
        return price.multiply(BigDecimal.valueOf(rate));
    }
}

// 割引ルール
public enum DiscountRule {
    BULK_PURCHASE {
        @Override
        public Optional<Discount> calculate(Order order) {
            if (order.getTotalQuantity() >= 10) {
                return Optional.of(new Discount("Bulk Purchase", 0.15));
            }
            return Optional.empty();
        }
    },
    LOYALTY_MEMBER {
        @Override
        public Optional<Discount> calculate(Order order) {
            if (order.getCustomer().isLoyaltyMember()) {
                return Optional.of(new Discount("Loyalty Member", 0.05));
            }
            return Optional.empty();
        }
    },
    SEASONAL_PROMOTION {
        @Override
        public Optional<Discount> calculate(Order order) {
            LocalDate today = LocalDate.now();
            if (isWithinPromotionPeriod(today)) {
                return Optional.of(new Discount("Summer Sale", 0.20));
            }
            return Optional.empty();
        }
        
        private boolean isWithinPromotionPeriod(LocalDate date) {
            // 実装
        }
    };
    
    public abstract Optional<Discount> calculate(Order order);
    
    // 複数のルールを組み合わせる
    public static List<Discount> applyAll(Order order) {
        return Arrays.stream(values())
            .map(rule -> rule.calculate(order))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());
    }
}

// ルールエンジン
public class RuleEngine {
    public Invoice processOrder(Order order) {
        // 税金計算
        Map<Product, TaxRule> taxRules = order.getItems().stream()
            .collect(Collectors.toMap(
                OrderItem::getProduct,
                item -> findApplicableTaxRule(item.getProduct())
            ));
        
        // 割引適用
        List<Discount> discounts = DiscountRule.applyAll(order);
        
        // 最終的な請求書を生成
        return new Invoice(order, taxRules, discounts);
    }
}
```

### 課題2: DSL（ドメイン特化言語）の実装
`QueryDSL.java`を作成し、以下を実装してください：

Enumを使ってSQLライクなクエリを構築するDSLを実装します。

```java
// クエリ演算子
public enum QueryOperator {
    EQUALS("=") {
        @Override
        public boolean evaluate(Object left, Object right) {
            return Objects.equals(left, right);
        }
    },
    NOT_EQUALS("!=") {
        @Override
        public boolean evaluate(Object left, Object right) {
            return !Objects.equals(left, right);
        }
    },
    GREATER_THAN(">") {
        @Override
        @SuppressWarnings("unchecked")
        public boolean evaluate(Object left, Object right) {
            if (left instanceof Comparable && right instanceof Comparable) {
                return ((Comparable) left).compareTo(right) > 0;
            }
            throw new IllegalArgumentException("Values must be comparable");
        }
    },
    LESS_THAN("<") {
        // 実装
    },
    IN("IN") {
        @Override
        public boolean evaluate(Object left, Object right) {
            if (right instanceof Collection) {
                return ((Collection<?>) right).contains(left);
            }
            throw new IllegalArgumentException("Right operand must be a collection");
        }
    },
    LIKE("LIKE") {
        @Override
        public boolean evaluate(Object left, Object right) {
            if (left instanceof String && right instanceof String) {
                String pattern = ((String) right).replace("%", ".*");
                return ((String) left).matches(pattern);
            }
            return false;
        }
    };
    
    private final String symbol;
    
    QueryOperator(String symbol) {
        this.symbol = symbol;
    }
    
    public abstract boolean evaluate(Object left, Object right);
}

// クエリビルダー
public class Query {
    private final List<Condition> conditions = new ArrayList<>();
    
    public Query where(String field, QueryOperator op, Object value) {
        conditions.add(new Condition(field, op, value));
        return this;
    }
    
    public Query and(String field, QueryOperator op, Object value) {
        conditions.add(new Condition(field, op, value, LogicalOperator.AND));
        return this;
    }
    
    public Query or(String field, QueryOperator op, Object value) {
        conditions.add(new Condition(field, op, value, LogicalOperator.OR));
        return this;
    }
    
    public <T> List<T> execute(List<T> data) {
        return data.stream()
            .filter(this::matches)
            .collect(Collectors.toList());
    }
    
    private boolean matches(Object item) {
        // リフレクションを使ってフィールド値を取得し、条件を評価
    }
}

// 使用例
List<Person> filtered = new Query()
    .where("age", QueryOperator.GREATER_THAN, 20)
    .and("name", QueryOperator.LIKE, "John%")
    .or("city", QueryOperator.IN, List.of("Tokyo", "Osaka"))
    .execute(people);
```

## 実装のヒント

### 複雑な状態遷移の管理
```java
public enum ComplexState {
    INITIAL {
        @Override
        public Set<ComplexState> getValidTransitions() {
            return EnumSet.of(PENDING_APPROVAL, DRAFT);
        }
    },
    // 他の状態...
    
    public abstract Set<ComplexState> getValidTransitions();
    
    public boolean canTransitionTo(ComplexState target) {
        return getValidTransitions().contains(target);
    }
    
    // 遷移時のアクション
    public void onEnter(Context context) {
        // デフォルト実装
    }
    
    public void onExit(Context context) {
        // デフォルト実装
    }
}
```

### パフォーマンスの最適化
```java
// Enumの事前計算とキャッシュ
public enum CachedEnum {
    VALUE1, VALUE2, VALUE3;
    
    private static final Map<String, CachedEnum> NAME_MAP;
    private static final EnumSet<CachedEnum> ACTIVE_VALUES;
    
    static {
        // 起動時に一度だけ計算
        NAME_MAP = Arrays.stream(values())
            .collect(Collectors.toMap(
                e -> e.name().toLowerCase(),
                Function.identity()
            ));
        
        ACTIVE_VALUES = EnumSet.of(VALUE1, VALUE2);
    }
    
    public static Optional<CachedEnum> fromString(String name) {
        return Optional.ofNullable(NAME_MAP.get(name.toLowerCase()));
    }
}
```

## 提出前チェックリスト
- [ ] ビジネスルールが正しく動作する
- [ ] DSLが直感的で使いやすい
- [ ] パフォーマンスが考慮されている
- [ ] 包括的なテストケースが作成されている

## 評価基準
- 複雑なビジネスロジックをEnumで適切に表現できているか
- DSLの設計が洗練されているか
- 拡張性と保守性を考慮した設計になっているか
- エッジケースが適切に処理されているか

## ボーナス課題
時間に余裕がある場合は、以下の追加実装に挑戦してください：
- ルールエンジン：ルールの優先順位と競合解決メカニズム
- DSL：JOINやGROUP BYに相当する機能の実装
- 状態遷移：並行状態や階層状態のサポート