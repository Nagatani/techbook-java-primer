# 第9章 チャレンジ課題：型安全なORMフレームワークの実装

## 課題概要

ジェネリクスの高度な機能をフル活用し、型安全なObject-Relational Mapping (ORM)フレームワークを実装します。実際のフレームワーク開発で必要となる複雑な型設計とメタプログラミング技術を習得します。

## チャレンジ課題：MiniORM Framework

### 目的
- エンタープライズレベルの型設計
- リフレクションとジェネリクスの統合
- DSL（Domain Specific Language）の実装
- 型安全なクエリビルダー

### 要求仕様

1. **エンティティ定義**
   ```java
   @Entity
   @Table(name = "users")
   public class User {
       @Id
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Long id;
       
       @Column(name = "username", unique = true, nullable = false)
       private String username;
       
       @Column(name = "email")
       private String email;
       
       @ManyToOne
       @JoinColumn(name = "department_id")
       private Department department;
       
       @OneToMany(mappedBy = "user")
       private List<Order> orders;
   }
   ```

2. **型安全なリポジトリ**
   ```java
   public interface Repository<T, ID> {
       T findById(ID id);
       List<T> findAll();
       T save(T entity);
       void delete(T entity);
       boolean existsById(ID id);
       
       // 型安全なクエリメソッド
       QueryBuilder<T> query();
       CriteriaBuilder<T> criteria();
   }
   
   public abstract class AbstractRepository<T, ID> implements Repository<T, ID> {
       protected final Class<T> entityClass;
       protected final EntityManager entityManager;
       
       @SuppressWarnings("unchecked")
       protected AbstractRepository(EntityManager entityManager) {
           this.entityManager = entityManager;
           // リフレクションで実際の型パラメータを取得
           ParameterizedType genericSuperclass = 
               (ParameterizedType) getClass().getGenericSuperclass();
           this.entityClass = 
               (Class<T>) genericSuperclass.getActualTypeArguments()[0];
       }
   }
   ```

3. **型安全なクエリビルダー**
   ```java
   public class QueryBuilder<T> {
       private final Class<T> entityClass;
       private final List<Predicate> predicates = new ArrayList<>();
       private final List<Order> orders = new ArrayList<>();
       private Integer limit;
       private Integer offset;
       
       // メソッドチェーン可能な流暢なAPI
       public QueryBuilder<T> where(String field, Object value) {
           return where(field, Operator.EQUALS, value);
       }
       
       public QueryBuilder<T> where(String field, Operator op, Object value) {
           predicates.add(new Predicate(field, op, value));
           return this;
       }
       
       public <R> QueryBuilder<T> where(
           Function<T, R> getter, Operator op, R value) {
           // メソッド参照から フィールド名を推定
           String fieldName = extractFieldName(getter);
           return where(fieldName, op, value);
       }
       
       public QueryBuilder<T> orderBy(String field, Direction direction) {
           orders.add(new Order(field, direction));
           return this;
       }
       
       public QueryBuilder<T> limit(int limit) {
           this.limit = limit;
           return this;
       }
       
       public List<T> list() {
           // SQLを生成して実行
       }
       
       public Optional<T> first() {
           return limit(1).list().stream().findFirst();
       }
   }
   ```

4. **型安全なCriteria API**
   ```java
   public class CriteriaBuilder<T> {
       private Root<T> root;
       
       public interface Root<T> {
           <R> Path<R> get(String attributeName);
           <R> Path<R> get(SingularAttribute<T, R> attribute);
       }
       
       public interface Path<T> {
           Predicate eq(T value);
           Predicate ne(T value);
           Predicate isNull();
           Predicate isNotNull();
       }
       
       public interface Predicate {
           Predicate and(Predicate other);
           Predicate or(Predicate other);
           Predicate not();
       }
       
       // 型安全な属性定義
       public static class SingularAttribute<T, R> {
           private final String name;
           private final Class<R> type;
           
           public SingularAttribute(String name, Class<R> type) {
               this.name = name;
               this.type = type;
           }
       }
   }
   
   // メタモデル生成
   @StaticMetamodel(User.class)
   public class User_ {
       public static volatile SingularAttribute<User, Long> id;
       public static volatile SingularAttribute<User, String> username;
       public static volatile SingularAttribute<User, String> email;
       public static volatile SingularAttribute<User, Department> department;
   }
   ```

5. **関連エンティティの遅延読み込み**
   ```java
   public class LazyLoader<T> {
       private final Supplier<T> loader;
       private volatile T value;
       private volatile boolean loaded = false;
       
       public T get() {
           if (!loaded) {
               synchronized (this) {
                   if (!loaded) {
                       value = loader.get();
                       loaded = true;
                   }
               }
           }
           return value;
       }
   }
   
   public interface LazyCollection<T> extends Collection<T> {
       // 遅延読み込みを実装したコレクション
       boolean isLoaded();
       void load();
   }
   ```

6. **トランザクション管理**
   ```java
   @FunctionalInterface
   public interface TransactionCallback<T> {
       T doInTransaction(EntityManager em) throws Exception;
   }
   
   public class TransactionTemplate {
       public <T> T execute(TransactionCallback<T> action) {
           EntityTransaction tx = entityManager.getTransaction();
           try {
               tx.begin();
               T result = action.doInTransaction(entityManager);
               tx.commit();
               return result;
           } catch (Exception e) {
               tx.rollback();
               throw new TransactionException(e);
           }
       }
       
       // リトライ機能付き
       public <T> T executeWithRetry(
           TransactionCallback<T> action, 
           int maxRetries,
           Predicate<Exception> retryOn) {
           // 実装
       }
   }
   ```

### 使用例
```java
public class UserService {
    private final Repository<User, Long> userRepository;
    private final TransactionTemplate transactionTemplate;
    
    public List<User> findActiveUsersInDepartment(String departmentName) {
        return userRepository.query()
            .where("active", true)
            .where("department.name", departmentName)
            .orderBy("username", Direction.ASC)
            .list();
    }
    
    public Optional<User> findByEmailTypeSafe(String email) {
        CriteriaBuilder<User> cb = userRepository.criteria();
        return cb.query()
            .where(cb.root.get(User_.email).eq(email))
            .first();
    }
    
    public User createUserInTransaction(String username, String email) {
        return transactionTemplate.execute(em -> {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            return userRepository.save(user);
        });
    }
}
```

### パフォーマンス要件
- エンティティのメタデータはキャッシュ
- プリペアドステートメントの使用
- N+1問題の回避（フェッチ戦略）
- コネクションプーリング対応

### 評価ポイント
- 型安全性の完全な保証
- 使いやすく直感的なAPI
- パフォーマンスの最適化
- エラーハンドリングの適切さ
- テスタビリティ
- ドキュメントの充実度

### ボーナス機能
1. **マイグレーション機能**：スキーマの自動生成と更新
2. **キャッシング**：二次キャッシュの実装
3. **監査機能**：作成日時、更新日時の自動設定
4. **イベントリスナー**：エンティティのライフサイクルイベント
5. **バリデーション統合**：Bean Validation風の検証

## 提出要件

1. **完全な実装**
   - ORMフレームワークのコア機能
   - 包括的なテストスイート
   - サンプルアプリケーション

2. **設計ドキュメント**
   - アーキテクチャ設計書
   - API仕様書
   - パフォーマンス測定結果

3. **比較分析**
   - Hibernate/JPAとの機能比較
   - 長所と短所の分析
   - 改善提案

## 発展的な考察

- **アノテーションプロセッサ**：コンパイル時のメタモデル生成
- **バイトコード生成**：動的プロキシとCGLIB
- **リアクティブ対応**：非同期・ノンブロッキングAPI
- **NoSQL対応**：MongoDBなどへの拡張

## 参考リソース

- Java Persistence with Hibernate（Christian Bauer、Gavin King）
- Pro JPA 2（Mike Keith、Merrick Schincariol）
- MyBatisソースコード
- QueryDSLソースコード