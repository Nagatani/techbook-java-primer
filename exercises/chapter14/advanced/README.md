# 第14章 応用課題

## 🎯 学習目標
- 高度なデータ型設計
- カスタム型システムの実装
- 型安全性の強化
- メタデータ活用システム
- 実行時型情報の高度利用

## 📝 課題一覧

### 課題1: 型安全データベースORM
**ファイル名**: `TypeSafeORM.java`

完全に型安全なデータベースORMシステムを作成してください。

**要求仕様**:
- コンパイル時型チェック
- タイプセーフなクエリビルダー
- 自動スキーマ検証
- 型推論ベースマッピング

**ORM機能**:
- エンティティマッピング
- 関連データの遅延読み込み
- トランザクション管理
- キャッシュシステム

**実行例**:
```
=== 型安全データベースORM ===

🛡️ TypeSafeORM v3.0

=== 型システム構成 ===
🔒 コンパイル時型安全性:

サポート型:
- プリミティブ型: 8種類
- 参照型: 無制限
- ジェネリック型: 完全対応
- 関数型: Lambda式対応
- カスタム型: 動的生成

型推論エンジン:
- Hindley-Milner型推論
- 型変数統一
- 制約ベース型推論
- 部分型関係解析

型安全性レベル:
- コンパイル時: 100%保証
- 実行時: 動的検証
- SQL注入: 完全防止
- 型変換エラー: 0件

=== エンティティ定義 ===
🏗️ 型安全エンティティシステム:

```java
// 型安全エンティティ定義
@Entity
@Table("users")
public record User(
    @Id @GeneratedValue 
    TypedColumn<Long> id,
    
    @Column(name = "username", nullable = false, unique = true)
    TypedColumn<String> username,
    
    @Column(name = "email", nullable = false)
    TypedColumn<Email> email,
    
    @Column(name = "age", nullable = true)
    TypedColumn<Age> age,
    
    @Column(name = "created_at", nullable = false)
    TypedColumn<LocalDateTime> createdAt,
    
    @OneToMany(mappedBy = "userId", fetch = FetchType.LAZY)
    TypedCollection<Order> orders
) implements TypedEntity<Long> {
    
    // 型安全なビルダー
    public static TypedEntityBuilder<User> builder() {
        return new TypedEntityBuilder<>(User.class);
    }
    
    // 型安全な更新
    public User withUsername(String newUsername) {
        return new User(id, TypedColumn.of(newUsername), email, age, createdAt, orders);
    }
    
    // 型安全な検証
    public ValidationResult validate() {
        TypeValidator<User> validator = TypeValidator.forEntity(User.class);
        return validator.validate(this);
    }
}

// カスタム型定義
public final class Email {
    private final String value;
    
    private Email(String value) {
        this.value = Objects.requireNonNull(value);
    }
    
    public static Email of(String value) {
        if (!isValidEmail(value)) {
            throw new InvalidEmailException("無効なメールアドレス: " + value);
        }
        return new Email(value);
    }
    
    public String getValue() {
        return value;
    }
    
    private static boolean isValidEmail(String email) {
        return email != null && 
               email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}

public final class Age {
    private final int value;
    
    private Age(int value) {
        this.value = value;
    }
    
    public static Age of(int value) {
        if (value < 0 || value > 150) {
            throw new InvalidAgeException("無効な年齢: " + value);
        }
        return new Age(value);
    }
    
    public int getValue() {
        return value;
    }
}

// 型安全クエリビルダー
public class TypeSafeQueryBuilder<T extends TypedEntity<?>> {
    private final Class<T> entityClass;
    private final List<TypedPredicate> predicates = new ArrayList<>();
    private final List<TypedOrderBy> orderByList = new ArrayList<>();
    
    public TypeSafeQueryBuilder(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
    
    // 型安全なWHERE条件
    public <V> TypeSafeQueryBuilder<T> where(TypedColumn<V> column, 
                                           TypedOperator<V> operator, 
                                           V value) {
        predicates.add(new TypedPredicate(column, operator, value));
        return this;
    }
    
    // 型安全なORDER BY
    public <V extends Comparable<V>> TypeSafeQueryBuilder<T> orderBy(
            TypedColumn<V> column, SortDirection direction) {
        orderByList.add(new TypedOrderBy(column, direction));
        return this;
    }
    
    // 型安全なJOIN
    public <R extends TypedEntity<?>> TypeSafeJoinBuilder<T, R> join(
            Class<R> joinEntityClass, 
            TypedJoinCondition<T, R> condition) {
        return new TypeSafeJoinBuilder<>(this, joinEntityClass, condition);
    }
    
    // 実行
    public TypedQuery<T> build() {
        TypedQueryCompiler compiler = new TypedQueryCompiler();
        return compiler.compile(entityClass, predicates, orderByList);
    }
}

// ORM実装
@Repository
public class TypeSafeRepository<T extends TypedEntity<ID>, ID> {
    private final EntityManager entityManager;
    private final TypedEntityMetadata<T> metadata;
    private final TypeSafeQueryCompiler queryCompiler;
    
    public TypeSafeRepository(Class<T> entityClass, EntityManager entityManager) {
        this.entityManager = entityManager;
        this.metadata = TypedEntityMetadata.of(entityClass);
        this.queryCompiler = new TypedQueryCompiler();
    }
    
    public Optional<T> findById(ID id) {
        try {
            // 型安全なプライマリキー検索
            TypedQuery<T> query = queryCompiler.createFindByIdQuery(metadata, id);
            
            // SQL生成・実行
            String sql = query.toSQL();
            Query nativeQuery = entityManager.createNativeQuery(sql, metadata.getEntityClass());
            nativeQuery.setParameter("id", id);
            
            @SuppressWarnings("unchecked")
            T result = (T) nativeQuery.getSingleResult();
            
            return Optional.ofNullable(result);
            
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new TypeSafeORMException("findById実行エラー", e);
        }
    }
    
    public List<T> findAll() {
        TypedQuery<T> query = queryCompiler.createFindAllQuery(metadata);
        return executeQuery(query);
    }
    
    public List<T> findWhere(TypedPredicate... predicates) {
        TypedQuery<T> query = queryCompiler.createSelectQuery(metadata, 
            Arrays.asList(predicates));
        return executeQuery(query);
    }
    
    @Transactional
    public T save(T entity) {
        try {
            // 型安全な保存前検証
            ValidationResult validation = entity.validate();
            if (!validation.isValid()) {
                throw new ValidationException("エンティティ検証失敗: " + 
                    validation.getErrors());
            }
            
            if (entity.getId() == null) {
                // INSERT
                TypedQuery<T> insertQuery = queryCompiler.createInsertQuery(metadata, entity);
                executeUpdate(insertQuery);
                
                // 生成されたIDを取得
                ID generatedId = getGeneratedId(entity);
                return entity.withId(generatedId);
            } else {
                // UPDATE
                TypedQuery<T> updateQuery = queryCompiler.createUpdateQuery(metadata, entity);
                executeUpdate(updateQuery);
                return entity;
            }
            
        } catch (Exception e) {
            throw new TypeSafeORMException("save実行エラー", e);
        }
    }
    
    @SuppressWarnings("unchecked")
    private List<T> executeQuery(TypedQuery<T> query) {
        String sql = query.toSQL();
        Query nativeQuery = entityManager.createNativeQuery(sql, metadata.getEntityClass());
        
        // パラメータ設定
        for (TypedParameter param : query.getParameters()) {
            nativeQuery.setParameter(param.getName(), param.getValue());
        }
        
        return nativeQuery.getResultList();
    }
}
```

型安全クエリ実行例:
```
=== 型安全クエリ実行 ===

クエリ構築:
TypeSafeQueryBuilder<User> builder = TypeSafeQueryBuilder.forEntity(User.class);

TypedQuery<User> query = builder
    .where(User::username, TypedOperator.LIKE, "john%")
    .where(User::age, TypedOperator.GREATER_THAN, Age.of(25))
    .orderBy(User::createdAt, SortDirection.DESC)
    .build();

コンパイル時型チェック:
✅ カラム型: username (String) ← "john%" (String) 
✅ 比較演算: age (Age) ← Age.of(25) (Age)
✅ ソート型: createdAt (LocalDateTime) implements Comparable
✅ 戻り値型: List<User>

生成SQL:
SELECT u.id, u.username, u.email, u.age, u.created_at
FROM users u
WHERE u.username LIKE ?
  AND u.age > ?
ORDER BY u.created_at DESC

実行結果:
取得件数: 23件
実行時間: 45ms
型安全性: 100%維持
SQLインジェクション: 0件 (防止完了)

型検証結果:
コンパイル時エラー: 0件
実行時型エラー: 0件
制約違反: 0件
パフォーマンス: 従来比105% (型安全オーバーヘッド5%)
```

=== 関連データ処理 ===
```java
// 型安全な関連データ取得
TypedQuery<User> usersWithOrders = TypeSafeQueryBuilder
    .forEntity(User.class)
    .join(Order.class, (user, order) -> user.id().equals(order.userId()))
    .where(Order::status, TypedOperator.EQUALS, OrderStatus.COMPLETED)
    .select(User.class)
    .distinct()
    .build();

実行例:
関連テーブル: users ⟷ orders
結合条件: users.id = orders.user_id (型安全)
フィルタ条件: orders.status = 'COMPLETED' (enum型安全)
重複排除: DISTINCT users
遅延読み込み: orders プロパティは必要時取得

結果:
ユーザー数: 156人
平均注文数: 3.2件/人
読み込み戦略: N+1問題回避済み
```
```

### 課題2: 動的型システム構築フレームワーク
**ファイル名**: `DynamicTypeSystemFramework.java`

実行時に型を動的生成・管理するフレームワークを作成してください。

**要求仕様**:
- 実行時型生成
- 型継承関係の動的変更
- カスタム型演算子定義
- 型推論エンジン実装

**型システム機能**:
- 構造的部分型
- 交差型・合併型
- 高階型
- 依存型

**実行例**:
```
=== 動的型システム構築フレームワーク ===

🔄 DynamicTypeSystem v2.0

=== 動的型生成 ===
⚡ 実行時型構築:

型生成エンジン:
- バイトコード生成: ASM 9.0
- 型推論: Hindley-Milner拡張
- 制約解決: Unification Algorithm
- 型チェック: 構造的型付け

サポート型種別:
- 基本型: プリミティブ + 参照型
- 複合型: Record、Union、Intersection
- 関数型: 高階関数対応
- 依存型: 値依存型付け
- 量化型: ∀型、∃型

型推論精度: 99.7%
型生成速度: 10万型/秒
メモリ効率: 従来比150%

=== 動的型定義 ===
🛠️ 実行時型システム:

```java
public class DynamicTypeBuilder {
    private final TypeRegistry typeRegistry;
    private final ConstraintSolver constraintSolver;
    private final TypeInferenceEngine inferenceEngine;
    
    // 構造的型定義
    public DynamicType createStructuralType(String name, 
                                          Map<String, TypeDescriptor> fields) {
        try {
            // 型制約収集
            Set<TypeConstraint> constraints = collectConstraints(fields);
            
            // 制約解決
            TypeSolution solution = constraintSolver.solve(constraints);
            
            if (!solution.hasUniqueSolution()) {
                throw new AmbiguousTypeException("型定義が曖昧です: " + name);
            }
            
            // バイトコード生成
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            
            // クラス定義
            classWriter.visit(
                Opcodes.V17,
                Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL,
                name.replace('.', '/'),
                null,
                "java/lang/Object",
                null
            );
            
            // フィールド生成
            for (Map.Entry<String, TypeDescriptor> field : fields.entrySet()) {
                generateField(classWriter, field.getKey(), field.getValue());
            }
            
            // コンストラクタ生成
            generateConstructor(classWriter, fields);
            
            // アクセサメソッド生成
            generateAccessors(classWriter, fields);
            
            // 型演算メソッド生成
            generateTypeOperations(classWriter, solution);
            
            classWriter.visitEnd();
            
            // クラスロード
            byte[] bytecode = classWriter.toByteArray();
            DynamicClassLoader loader = new DynamicClassLoader();
            Class<?> generatedClass = loader.defineClass(name, bytecode);
            
            // 型登録
            DynamicType dynamicType = new DynamicType(generatedClass, solution);
            typeRegistry.register(name, dynamicType);
            
            logger.info("動的型生成完了: {} (フィールド数: {})", name, fields.size());
            
            return dynamicType;
            
        } catch (Exception e) {
            throw new TypeGenerationException("動的型生成失敗: " + name, e);
        }
    }
    
    // 合併型 (Union Type) 作成
    public DynamicType createUnionType(String name, List<DynamicType> types) {
        // Union<A, B> = A | B
        TypeDescriptor unionDescriptor = TypeDescriptor.union(
            types.stream()
                .map(DynamicType::getDescriptor)
                .collect(Collectors.toList())
        );
        
        // 型安全な値クラス生成
        return generateUnionValueClass(name, types, unionDescriptor);
    }
    
    // 交差型 (Intersection Type) 作成
    public DynamicType createIntersectionType(String name, List<DynamicType> types) {
        // Intersection<A, B> = A & B
        TypeDescriptor intersectionDescriptor = TypeDescriptor.intersection(
            types.stream()
                .map(DynamicType::getDescriptor)
                .collect(Collectors.toList())
        );
        
        // 全ての型の制約を満たすクラス生成
        return generateIntersectionClass(name, types, intersectionDescriptor);
    }
    
    // 依存型作成
    public DynamicType createDependentType(String name, 
                                         DynamicType baseType,
                                         Predicate<Object> constraint) {
        // 値に依存する型: {x: T | P(x)}
        DependentTypeDescriptor descriptor = new DependentTypeDescriptor(
            baseType.getDescriptor(), constraint);
        
        return generateDependentTypeClass(name, baseType, descriptor);
    }
}

// 型推論エンジン
public class TypeInferenceEngine {
    private final UnificationAlgorithm unification;
    private final ConstraintGenerator constraintGenerator;
    
    public InferenceResult inferType(Expression expression, TypeEnvironment env) {
        try {
            // 制約生成
            ConstraintSet constraints = constraintGenerator.generate(expression, env);
            
            // 制約解決
            Substitution substitution = unification.unify(constraints);
            
            // 型推論
            Type inferredType = substitution.apply(expression.getType());
            
            // 一般化
            TypeScheme scheme = generalize(env, inferredType);
            
            return InferenceResult.success(scheme, substitution);
            
        } catch (UnificationException e) {
            return InferenceResult.failure("型推論失敗: " + e.getMessage());
        }
    }
    
    private TypeScheme generalize(TypeEnvironment env, Type type) {
        // 自由型変数を量化
        Set<TypeVariable> freeVars = type.getFreeVariables();
        freeVars.removeAll(env.getFreeVariables());
        
        return new TypeScheme(new ArrayList<>(freeVars), type);
    }
}

// 動的型演算子
public class DynamicTypeOperators {
    
    // 型レベル加算
    public static DynamicType add(DynamicType a, DynamicType b) {
        if (isNumericType(a) && isNumericType(b)) {
            return promoteNumericType(a, b);
        } else if (isStringType(a) || isStringType(b)) {
            return DynamicType.STRING;
        } else {
            throw new TypeOperationException("加算不可能な型: " + a + " + " + b);
        }
    }
    
    // 型レベル合成
    public static DynamicType compose(DynamicType f, DynamicType g) {
        if (!isFunctionType(f) || !isFunctionType(g)) {
            throw new TypeOperationException("関数型以外は合成できません");
        }
        
        FunctionType funcF = (FunctionType) f;
        FunctionType funcG = (FunctionType) g;
        
        // g: A -> B, f: B -> C なら f ∘ g: A -> C
        if (!funcF.getInputType().equals(funcG.getOutputType())) {
            throw new TypeOperationException("関数合成の型が一致しません");
        }
        
        return new FunctionType(funcG.getInputType(), funcF.getOutputType());
    }
    
    // 型レベル適用
    public static DynamicType apply(DynamicType function, DynamicType argument) {
        if (!isFunctionType(function)) {
            throw new TypeOperationException("関数型ではありません: " + function);
        }
        
        FunctionType funcType = (FunctionType) function;
        
        if (!isSubtypeOf(argument, funcType.getInputType())) {
            throw new TypeOperationException("引数型が一致しません: 期待=" + 
                funcType.getInputType() + ", 実際=" + argument);
        }
        
        return funcType.getOutputType();
    }
}
```

動的型実行例:
```
=== 動的型生成実行ログ ===

型定義要求:
型名: Person
フィールド:
- name: String
- age: int  
- email: Optional<String>
- address: Address

型生成プロセス:
14:30:00.001 - 制約収集開始
14:30:00.003 - 制約解決実行
14:30:00.005 - バイトコード生成開始
14:30:00.015 - フィールドアクセサ生成
14:30:00.020 - 型演算メソッド生成
14:30:00.025 - クラスロード完了
14:30:00.027 - 型登録完了

生成結果:
クラス名: com.dynamic.Person
バイトコードサイズ: 2,847 bytes
メソッド数: 12個
型安全性: 構造的型付けで保証
実行時検証: 有効

型推論テスト:
式: person.age + 5
推論結果: int
推論時間: 0.03ms
制約数: 7個
統一成功: ✅

合併型テスト:
型定義: StringOrNumber = String | Number
値テスト:
- "hello" → ✅ String部分型
- 42 → ✅ Number部分型  
- true → ❌ 型エラー

交差型テスト:
型定義: ReadableWritable = Readable & Writable
実装確認:
- read()メソッド: ✅ 実装済み
- write()メソッド: ✅ 実装済み
- 型適合性: ✅ 両インターフェイス満足
```

=== 高階型実装例 ===
```java
// 高階型定義
DynamicType listType = typeBuilder.createHigherKindedType("List", 1);
DynamicType mapType = typeBuilder.createHigherKindedType("Map", 2);

// 型適用
DynamicType stringList = typeBuilder.applyTypeArgs(listType, 
    Arrays.asList(DynamicType.STRING));
DynamicType stringIntMap = typeBuilder.applyTypeArgs(mapType, 
    Arrays.asList(DynamicType.STRING, DynamicType.INTEGER));

実行結果:
高階型: List<*> (Kind: * -> *)
適用後: List<String> (Kind: *)
検証: ✅ 型適用成功

高階型: Map<*, *> (Kind: * -> * -> *)  
適用後: Map<String, Integer> (Kind: *)
検証: ✅ 型適用成功
```
```

### 課題3: メタデータ駆動開発プラットフォーム
**ファイル名**: `MetadataDrivenPlatform.java`

メタデータから自動的にシステムを生成するプラットフォームを作成してください。

**要求仕様**:
- スキーマ駆動コード生成
- アノテーション処理による自動化
- リフレクション最適化
- 実行時メタデータ更新

**プラットフォーム機能**:
- API自動生成
- データバリデーション
- シリアライゼーション
- ドキュメント生成

**実行例**:
```
=== メタデータ駆動開発プラットフォーム ===

🏗️ MetadataDriven Platform v4.0

=== メタデータスキーマ ===
📋 自動生成システム:

メタデータ種別:
- エンティティスキーマ: JSON Schema
- API仕様: OpenAPI 3.0  
- バリデーション: JSON Schema + カスタム
- ドキュメント: Markdown + テンプレート

生成可能コンポーネント:
- REST API: Spring Boot
- データモデル: JPA Entity
- バリデーター: Bean Validation
- テストコード: JUnit 5
- ドキュメント: Asciidoc

コード生成速度: 50,000行/秒
生成品質: 手動実装比98%
メンテナンス性: 手動実装比150%

=== スキーマ駆動生成 ===
🔧 メタデータからコード生成:

```java
// メタデータ定義
@Schema(
    name = "User",
    description = "ユーザーエンティティ",
    version = "1.0.0"
)
@Entity
@Table(name = "users")
public interface UserSchema {
    
    @Property(
        name = "id",
        type = PropertyType.IDENTIFIER,
        required = true,
        generated = true
    )
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long getId();
    
    @Property(
        name = "username",
        type = PropertyType.STRING,
        required = true,
        unique = true,
        minLength = 3,
        maxLength = 50,
        pattern = "^[a-zA-Z0-9_]+$"
    )
    @Column(nullable = false, unique = true)
    String getUsername();
    
    @Property(
        name = "email",
        type = PropertyType.EMAIL,
        required = true,
        format = "email"
    )
    @Column(nullable = false)
    String getEmail();
    
    @Property(
        name = "profile",
        type = PropertyType.OBJECT,
        reference = "UserProfile",
        cascade = CascadeType.ALL
    )
    @OneToOne(cascade = CascadeType.ALL)
    UserProfile getProfile();
}

// コード生成エンジン
@Service
public class MetadataCodeGenerator {
    private final TemplateEngine templateEngine;
    private final SchemaAnalyzer schemaAnalyzer;
    private final CodeOptimizer codeOptimizer;
    
    public GenerationResult generateFromSchema(SchemaDefinition schema) {
        try {
            // スキーマ解析
            SchemaAnalysis analysis = schemaAnalyzer.analyze(schema);
            
            // 生成計画作成
            GenerationPlan plan = createGenerationPlan(analysis);
            
            // 並行コード生成
            List<CompletableFuture<GeneratedComponent>> futures = 
                plan.getComponents().stream()
                    .map(component -> CompletableFuture.supplyAsync(() -> 
                        generateComponent(component, analysis)))
                    .collect(Collectors.toList());
            
            // 全コンポーネント生成待機
            List<GeneratedComponent> components = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
            
            // コード最適化
            OptimizedCode optimizedCode = codeOptimizer.optimize(components);
            
            // ファイル出力
            List<GeneratedFile> files = writeGeneratedFiles(optimizedCode);
            
            return GenerationResult.success(files, analysis.getMetrics());
            
        } catch (Exception e) {
            return GenerationResult.failure("コード生成失敗", e);
        }
    }
    
    private GeneratedComponent generateComponent(ComponentType type, 
                                               SchemaAnalysis analysis) {
        switch (type) {
            case ENTITY:
                return generateEntity(analysis);
            case REPOSITORY:
                return generateRepository(analysis);
            case SERVICE:
                return generateService(analysis);
            case CONTROLLER:
                return generateController(analysis);
            case DTO:
                return generateDTO(analysis);
            case VALIDATOR:
                return generateValidator(analysis);
            case TEST:
                return generateTest(analysis);
            default:
                throw new UnsupportedComponentException("未対応コンポーネント: " + type);
        }
    }
    
    private GeneratedComponent generateEntity(SchemaAnalysis analysis) {
        EntityTemplate template = templateEngine.loadTemplate("entity.ftl");
        
        Map<String, Object> context = new HashMap<>();
        context.put("schema", analysis.getSchema());
        context.put("properties", analysis.getProperties());
        context.put("relationships", analysis.getRelationships());
        context.put("constraints", analysis.getConstraints());
        context.put("indexes", analysis.getIndexes());
        
        String code = template.process(context);
        
        return new GeneratedComponent(ComponentType.ENTITY, 
            analysis.getSchema().getName() + ".java", code);
    }
    
    private GeneratedComponent generateRepository(SchemaAnalysis analysis) {
        RepositoryTemplate template = templateEngine.loadTemplate("repository.ftl");
        
        // カスタムクエリメソッド生成
        List<QueryMethod> queryMethods = generateQueryMethods(analysis);
        
        Map<String, Object> context = new HashMap<>();
        context.put("entityName", analysis.getSchema().getName());
        context.put("idType", analysis.getIdProperty().getType());
        context.put("queryMethods", queryMethods);
        context.put("customQueries", analysis.getCustomQueries());
        
        String code = template.process(context);
        
        return new GeneratedComponent(ComponentType.REPOSITORY,
            analysis.getSchema().getName() + "Repository.java", code);
    }
    
    private GeneratedComponent generateController(SchemaAnalysis analysis) {
        ControllerTemplate template = templateEngine.loadTemplate("controller.ftl");
        
        // REST API仕様から自動生成
        List<ApiEndpoint> endpoints = generateApiEndpoints(analysis);
        
        Map<String, Object> context = new HashMap<>();
        context.put("entityName", analysis.getSchema().getName());
        context.put("endpoints", endpoints);
        context.put("validationRules", analysis.getValidationRules());
        context.put("securityRules", analysis.getSecurityRules());
        
        String code = template.process(context);
        
        return new GeneratedComponent(ComponentType.CONTROLLER,
            analysis.getSchema().getName() + "Controller.java", code);
    }
}

// 実行時メタデータ更新
@Component
public class RuntimeMetadataManager {
    private final MetadataRegistry metadataRegistry;
    private final HotReloadManager hotReloadManager;
    
    @EventListener
    public void handleSchemaUpdate(SchemaUpdateEvent event) {
        try {
            SchemaDefinition oldSchema = metadataRegistry.getSchema(event.getSchemaName());
            SchemaDefinition newSchema = event.getNewSchema();
            
            // 変更分析
            SchemaComparison comparison = compareSchemas(oldSchema, newSchema);
            
            if (comparison.hasBreakingChanges()) {
                logger.warn("破壊的変更検出: {}", comparison.getBreakingChanges());
                
                // 段階的移行計画
                MigrationPlan plan = createMigrationPlan(comparison);
                executeMigration(plan);
            } else {
                // 非破壊的変更 - ホットリロード
                hotReloadManager.reloadSchema(newSchema);
            }
            
            // メタデータ更新
            metadataRegistry.updateSchema(event.getSchemaName(), newSchema);
            
        } catch (Exception e) {
            logger.error("スキーマ更新エラー", e);
        }
    }
    
    public void enableHotReload(String schemaName) {
        SchemaWatcher watcher = new SchemaWatcher(schemaName);
        watcher.onSchemaChange(this::handleSchemaUpdate);
        watcher.start();
    }
}
```

実行例:
```
=== メタデータ駆動開発実行ログ ===

スキーマ読み込み:
ファイル: user-schema.json
エンティティ: User
プロパティ数: 15個
関連数: 3個
制約数: 8個

コード生成プロセス:
14:30:00.001 - スキーマ解析開始
14:30:00.015 - 生成計画作成完了
14:30:00.016 - 並行コード生成開始

生成コンポーネント:
UserEntity.java (完了: 156ms)
UserRepository.java (完了: 89ms)
UserService.java (完了: 134ms)
UserController.java (完了: 198ms)
UserDTO.java (完了: 67ms)
UserValidator.java (完了: 101ms)
UserTest.java (完了: 245ms)

最適化:
デッドコード除去: 12%削減
パフォーマンス最適化: 23%向上
メモリ使用量: 15%削減

出力結果:
生成ファイル数: 7個
総行数: 3,247行
生成時間: 245ms
品質スコア: 9.2/10

実行時更新:
スキーマファイル変更検知: user-schema.json
変更内容: email フィールドに maxLength 制約追加
変更種別: 非破壊的変更
ホットリロード: 実行中...
更新完了: 89ms

更新結果:
影響コンポーネント: UserValidator, UserController
再生成: 不要（バリデーション追加のみ）
ダウンタイム: 0秒
エラー: 0件
```

=== API自動生成例 ===
```yaml
# スキーマ定義 (user-api.yaml)
openapi: 3.0.0
info:
  title: User API
  version: 1.0.0

paths:
  /users:
    get:
      summary: ユーザー一覧取得
      parameters:
        - name: page
          in: query
          schema:
            type: integer
            minimum: 0
            default: 0
      responses:
        '200':
          description: 成功
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/User'
    post:
      summary: ユーザー作成
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateUserRequest'

自動生成結果:
@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    
    @GetMapping
    public ResponseEntity<List<UserDTO>> getUsers(
            @RequestParam(defaultValue = "0") @Min(0) Integer page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userService.findAll(pageable);
        List<UserDTO> userDTOs = users.getContent().stream()
            .map(userMapper::toDTO)
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(userDTOs);
    }
    
    @PostMapping
    public ResponseEntity<UserDTO> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        
        User user = userMapper.toEntity(request);
        User savedUser = userService.save(user);
        UserDTO userDTO = userMapper.toDTO(savedUser);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    }
}

生成統計:
エンドポイント数: 12個
バリデーション: 自動適用
ドキュメント: Swagger UI対応
テストケース: 36個自動生成
```
```

## 🎯 習得すべき技術要素

### 型システム理論
- 型推論アルゴリズム
- 構造的型付け
- 部分型関係
- 高階型システム

### メタプログラミング
- リフレクション API
- アノテーション処理
- バイトコード操作
- 動的プロキシ

### コード生成技術
- テンプレートエンジン
- AST操作
- スキーマ駆動開発
- モデル駆動アーキテクチャ

## 📚 参考リソース

- Types and Programming Languages (Pierce)
- Advanced Topics in Types and Programming Languages (Pierce)
- Domain-Specific Languages (Fowler)
- Generative Programming (Czarnecki & Eisenecker)

## ⚠️ 重要な注意事項

動的型システムと メタデータ駆動開発は強力ですが、複雑性も増加します。パフォーマンスと保守性のバランスを十分に考慮してください。