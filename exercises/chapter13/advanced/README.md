# 第13章 応用課題

## 🎯 学習目標
- パッケージ設計の高度な実践
- モジュールシステムの活用
- アーキテクチャレベルの依存管理
- 大規模システムの構造化
- レイヤードアーキテクチャ実装

## 📝 課題一覧

### 課題1: マルチモジュール企業システム
**ファイル名**: `MultiModuleEnterpriseSystem.java`

複数のモジュールから構成される企業向けシステムを作成してください。

**要求仕様**:
- JPMS (Java Platform Module System) 活用
- 依存関係の明確な分離
- API・実装の分離
- プラグイン可能アーキテクチャ

**モジュール構成**:
- Core API モジュール
- 認証・認可モジュール
- データアクセスモジュール
- ビジネスロジックモジュール
- Web API モジュール

**実行例**:
```
=== マルチモジュール企業システム ===

🏢 EnterpriseCore v4.0

=== モジュール構成 ===
📦 システムアーキテクチャ:

モジュール一覧:
1. com.enterprise.core.api (基盤API)
2. com.enterprise.auth (認証・認可)
3. com.enterprise.data (データアクセス)
4. com.enterprise.business (ビジネスロジック)
5. com.enterprise.web (Web API)
6. com.enterprise.integration (外部連携)
7. com.enterprise.monitoring (監視・メトリクス)
8. com.enterprise.security (セキュリティ)

依存関係:
```
web → business → data → core.api
auth → core.api
security → auth, core.api
monitoring → core.api
integration → business, auth
```

モジュールサイズ:
- core.api: 45 classes, 12 interfaces
- auth: 67 classes, 18 interfaces  
- data: 89 classes, 23 interfaces
- business: 234 classes, 67 interfaces
- web: 156 classes, 34 interfaces
- 総計: 591 classes, 154 interfaces

=== Core APIモジュール ===
🔧 基盤インターフェース定義:

```java
// module-info.java (Core API)
module com.enterprise.core.api {
    // 基盤APIの公開
    exports com.enterprise.core.api.common;
    exports com.enterprise.core.api.exception;
    exports com.enterprise.core.api.validation;
    exports com.enterprise.core.api.audit;
    exports com.enterprise.core.api.security;
    
    // SLF4J依存
    requires org.slf4j;
    requires java.validation;
    requires java.time;
}

// 共通インターフェース定義
package com.enterprise.core.api.common;

public interface Entity<ID> {
    ID getId();
    void setId(ID id);
    LocalDateTime getCreatedAt();
    LocalDateTime getUpdatedAt();
    String getCreatedBy();
    String getUpdatedBy();
}

public interface Repository<T extends Entity<ID>, ID> {
    Optional<T> findById(ID id);
    List<T> findAll();
    List<T> findAll(Pageable pageable);
    T save(T entity);
    void delete(T entity);
    void deleteById(ID id);
    boolean existsById(ID id);
    long count();
}

public interface Service<T extends Entity<ID>, ID> {
    T create(T entity) throws ValidationException;
    T update(T entity) throws ValidationException, EntityNotFoundException;
    Optional<T> findById(ID id);
    List<T> findAll(SearchCriteria criteria);
    void delete(ID id) throws EntityNotFoundException;
}

// 監査インターフェース
public interface AuditableOperation {
    @AuditLogged
    void execute() throws OperationException;
    
    String getOperationName();
    String getOperationDescription();
    Map<String, Object> getOperationParameters();
}

// セキュリティインターフェース
public interface SecuredResource {
    List<Permission> getRequiredPermissions();
    boolean isAccessibleBy(User user);
    void checkAccess(User user) throws AccessDeniedException;
}
```

=== 認証・認可モジュール ===
🔐 セキュリティシステム:

```java
// module-info.java (Auth)
module com.enterprise.auth {
    requires com.enterprise.core.api;
    requires java.security.jgss;
    requires java.security.sasl;
    requires spring.security.core;
    requires spring.security.web;
    
    exports com.enterprise.auth.api;
    exports com.enterprise.auth.service;
    
    // 内部実装は非公開
    // com.enterprise.auth.impl パッケージは export しない
}

// 認証サービス
package com.enterprise.auth.service;

@Service
public class AuthenticationService implements SecuredResource {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final AuditService auditService;
    
    @AuditLogged
    public AuthenticationResult authenticate(AuthenticationRequest request) 
            throws AuthenticationException {
        
        try {
            // ユーザー検索
            Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
            if (userOpt.isEmpty()) {
                auditService.logFailedAuthentication(request.getUsername(), 
                    "User not found");
                throw new UserNotFoundException("ユーザーが見つかりません");
            }
            
            User user = userOpt.get();
            
            // アカウント状態確認
            validateUserAccount(user);
            
            // パスワード検証
            if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                auditService.logFailedAuthentication(request.getUsername(), 
                    "Invalid password");
                handleFailedAuthentication(user);
                throw new InvalidCredentialsException("認証情報が正しくありません");
            }
            
            // 多要素認証（必要な場合）
            if (user.isMfaEnabled()) {
                validateMfaToken(user, request.getMfaToken());
            }
            
            // JWTトークン生成
            String accessToken = jwtTokenService.generateAccessToken(user);
            String refreshToken = jwtTokenService.generateRefreshToken(user);
            
            // セッション作成
            UserSession session = createUserSession(user, accessToken);
            
            // 成功監査ログ
            auditService.logSuccessfulAuthentication(user);
            
            return AuthenticationResult.success(user, accessToken, refreshToken, session);
            
        } catch (AuthenticationException e) {
            // 失敗カウンター更新
            incrementFailedAttempts(request.getUsername());
            throw e;
            
        } catch (Exception e) {
            logger.error("認証処理エラー", e);
            throw new AuthenticationException("認証処理中にエラーが発生しました", e);
        }
    }
    
    @AuditLogged
    public void authorize(User user, String resource, String action) 
            throws AuthorizationException {
        
        try {
            // ユーザーロール取得
            List<Role> userRoles = user.getRoles();
            
            // 必要権限取得
            List<Permission> requiredPermissions = 
                permissionService.getRequiredPermissions(resource, action);
            
            // 権限チェック
            boolean hasAccess = permissionService.hasPermissions(userRoles, requiredPermissions);
            
            if (!hasAccess) {
                auditService.logUnauthorizedAccess(user, resource, action);
                throw new InsufficientPermissionException(
                    String.format("リソース '%s' に対する '%s' 権限がありません", resource, action));
            }
            
            // 成功監査ログ
            auditService.logAuthorizedAccess(user, resource, action);
            
        } catch (AuthorizationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("認可処理エラー", e);
            throw new AuthorizationException("認可処理中にエラーが発生しました", e);
        }
    }
    
    private void validateUserAccount(User user) throws AuthenticationException {
        if (!user.isActive()) {
            throw new AccountDisabledException("アカウントが無効です");
        }
        
        if (user.isLocked()) {
            throw new AccountLockedException("アカウントがロックされています");
        }
        
        if (user.isPasswordExpired()) {
            throw new PasswordExpiredException("パスワードの有効期限が切れています");
        }
    }
}

// 権限管理サービス
@Service
public class PermissionService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PermissionCache permissionCache;
    
    public boolean hasPermissions(List<Role> userRoles, List<Permission> requiredPermissions) {
        
        // キャッシュ確認
        String cacheKey = generateCacheKey(userRoles, requiredPermissions);
        Boolean cachedResult = permissionCache.get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }
        
        // ユーザーの全権限取得
        Set<Permission> userPermissions = userRoles.stream()
            .flatMap(role -> role.getPermissions().stream())
            .collect(Collectors.toSet());
        
        // 必要権限がすべて含まれているかチェック
        boolean hasAllPermissions = userPermissions.containsAll(requiredPermissions);
        
        // 結果をキャッシュ
        permissionCache.put(cacheKey, hasAllPermissions, Duration.ofMinutes(5));
        
        return hasAllPermissions;
    }
}
```

認証実行例:
```
=== 認証処理ログ ===

認証要求:
ユーザー名: john.doe@enterprise.com
IPアドレス: 192.168.1.100
ユーザーエージェント: Mozilla/5.0...
リクエスト時刻: 2024-07-05 14:30:15

認証プロセス:
14:30:15.123 - ユーザー検索開始
14:30:15.145 - ユーザー発見: John Doe
14:30:15.147 - アカウント状態確認: アクティブ
14:30:15.148 - パスワード検証開始
14:30:15.167 - パスワード検証成功
14:30:15.168 - MFA確認: 有効
14:30:15.169 - MFAトークン検証開始
14:30:15.185 - MFAトークン検証成功
14:30:15.186 - JWTトークン生成開始
14:30:15.203 - JWTトークン生成完了
14:30:15.204 - ユーザーセッション作成
14:30:15.220 - 認証成功

認証結果:
認証状態: 成功
処理時間: 97ms
発行トークン: ey...
セッションID: sess_abc123...
有効期限: 2024-07-05 22:30:15

ユーザー情報:
ユーザーID: USR-001
氏名: John Doe
部署: IT部門
役職: システム管理者
権限: ADMIN, USER_MANAGER, SYSTEM_CONFIG
```

=== 権限チェック実行例 ===
```
認可要求:
ユーザー: john.doe@enterprise.com
リソース: /api/users
アクション: DELETE
リクエスト時刻: 2024-07-05 14:35:22

権限チェック:
14:35:22.001 - ユーザーロール取得
14:35:22.003 - ロール: [ADMIN, USER_MANAGER]  
14:35:22.004 - 必要権限計算
14:35:22.006 - 必要権限: [USER_DELETE, ADMIN_ACCESS]
14:35:22.007 - キャッシュ確認: ヒット
14:35:22.008 - 権限マッチング: 成功
14:35:22.009 - 認可成功

認可結果:
認可状態: 許可
処理時間: 8ms
チェック済み権限: 2個
キャッシュ効率: 100%
```
```

=== データアクセスモジュール ===
💾 高性能データ層:

```java
// module-info.java (Data)
module com.enterprise.data {
    requires com.enterprise.core.api;
    requires java.sql;
    requires java.persistence;
    requires spring.data.jpa;
    requires spring.tx;
    requires hikaricp;
    
    exports com.enterprise.data.repository;
    exports com.enterprise.data.entity;
    exports com.enterprise.data.specification;
    
    // JPA実装の詳細は非公開
}

// エンティティ基底クラス
package com.enterprise.data.entity;

@MappedSuperclass
public abstract class BaseEntity implements Entity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    
    @Column(name = "created_at", nullable = false)
    protected LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    protected LocalDateTime updatedAt;
    
    @Column(name = "created_by", nullable = false)
    protected String createdBy;
    
    @Column(name = "updated_by", nullable = false)
    protected String updatedBy;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        createdBy = getCurrentUser();
        updatedBy = getCurrentUser();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        updatedBy = getCurrentUser();
    }
    
    private String getCurrentUser() {
        // Spring Securityからの取得
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "system";
    }
}

// ユーザーエンティティ
@Entity
@Table(name = "users")
public class UserEntity extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String passwordHash;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(nullable = false)
    private boolean active = true;
    
    @Column(nullable = false)
    private boolean locked = false;
    
    @Column
    private LocalDateTime passwordExpiryDate;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles = new HashSet<>();
    
    // getters, setters...
}

// リポジトリ実装
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long>, 
                                       JpaSpecificationExecutor<UserEntity> {
    
    Optional<UserEntity> findByUsername(String username);
    
    Optional<UserEntity> findByEmail(String email);
    
    List<UserEntity> findByActiveTrue();
    
    @Query("SELECT u FROM UserEntity u WHERE u.passwordExpiryDate < :date")
    List<UserEntity> findUsersWithExpiredPasswords(@Param("date") LocalDateTime date);
    
    @Modifying
    @Query("UPDATE UserEntity u SET u.locked = true WHERE u.id = :userId")
    void lockUser(@Param("userId") Long userId);
    
    @Query(value = "SELECT COUNT(*) FROM users WHERE created_at >= :since", 
           nativeQuery = true)
    long countNewUsersSince(@Param("since") LocalDateTime since);
}

// カスタムリポジトリ実装
@Repository
public class CustomUserRepositoryImpl implements CustomUserRepository {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public List<UserEntity> findUsersWithComplexCriteria(UserSearchCriteria criteria) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserEntity> query = cb.createQuery(UserEntity.class);
        Root<UserEntity> root = query.from(UserEntity.class);
        
        List<Predicate> predicates = new ArrayList<>();
        
        // 動的条件構築
        if (criteria.getUsername() != null) {
            predicates.add(cb.like(cb.lower(root.get("username")), 
                "%" + criteria.getUsername().toLowerCase() + "%"));
        }
        
        if (criteria.getEmail() != null) {
            predicates.add(cb.like(cb.lower(root.get("email")), 
                "%" + criteria.getEmail().toLowerCase() + "%"));
        }
        
        if (criteria.getDepartment() != null) {
            Join<UserEntity, RoleEntity> roleJoin = root.join("roles");
            predicates.add(cb.equal(roleJoin.get("department"), criteria.getDepartment()));
        }
        
        if (criteria.getActiveOnly() != null && criteria.getActiveOnly()) {
            predicates.add(cb.isTrue(root.get("active")));
        }
        
        if (criteria.getCreatedAfter() != null) {
            predicates.add(cb.greaterThan(root.get("createdAt"), criteria.getCreatedAfter()));
        }
        
        // 条件を AND で結合
        query.where(cb.and(predicates.toArray(new Predicate[0])));
        
        // ソート条件
        if (criteria.getSortBy() != null) {
            if (criteria.getSortDirection() == SortDirection.DESC) {
                query.orderBy(cb.desc(root.get(criteria.getSortBy())));
            } else {
                query.orderBy(cb.asc(root.get(criteria.getSortBy())));
            }
        }
        
        TypedQuery<UserEntity> typedQuery = entityManager.createQuery(query);
        
        // ページング
        if (criteria.getPage() != null && criteria.getSize() != null) {
            typedQuery.setFirstResult(criteria.getPage() * criteria.getSize());
            typedQuery.setMaxResults(criteria.getSize());
        }
        
        return typedQuery.getResultList();
    }
}
```

データアクセス実行例:
```
=== データアクセス実行ログ ===

クエリ要求:
検索条件: 部署=IT, アクティブ=true
ソート: 作成日降順
ページング: 20件/ページ, 1ページ目

クエリ実行:
14:30:25.001 - 動的クエリ構築開始
14:30:25.003 - 条件追加: department = 'IT'
14:30:25.004 - 条件追加: active = true
14:30:25.005 - ソート条件: created_at DESC
14:30:25.006 - ページング設定: LIMIT 20 OFFSET 0
14:30:25.007 - SQL生成完了
14:30:25.008 - データベース実行開始
14:30:25.023 - クエリ実行完了

実行結果:
取得件数: 18件
実行時間: 22ms
生成SQL: SELECT u.* FROM users u INNER JOIN...
インデックス使用: users_department_idx, users_active_idx
キャッシュ状況: 2次キャッシュヒット率 87%

パフォーマンス:
データベース接続: HikariCP Pool (5ms取得)
クエリ最適化: コストベースオプティマイザ使用
メモリ使用量: 2.4MB
ネットワーク転送: 45KB
```
```

### 課題2: プラグイン対応設計フレームワーク
**ファイル名**: `PluginCapableDesignFramework.java`

プラグインによる機能拡張が可能な設計フレームワークを作成してください。

**要求仕様**:
- SPI (Service Provider Interface) 活用
- 動的プラグイン発見・ロード
- プラグイン間の依存関係管理
- 安全なプラグイン実行環境

**プラグイン種別**:
- データ処理プラグイン
- 認証プラグイン
- 通知プラグイン
- レポートプラグイン

**実行例**:
```
=== プラグイン対応設計フレームワーク ===

🔌 ExtensibleFramework v3.0

=== プラグインシステム ===
⚙️ 動的拡張アーキテクチャ:

フレームワーク構成:
コアフレームワーク: com.framework.core
プラグインAPI: com.framework.plugin.api
プラグインローダー: com.framework.plugin.loader
セキュリティマネージャー: com.framework.security

プラグインディレクトリ:
- /plugins/official/ (公式プラグイン)
- /plugins/community/ (コミュニティ)
- /plugins/enterprise/ (エンタープライズ)
- /plugins/development/ (開発用)

発見済みプラグイン:
データ処理: 12個
認証: 8個
通知: 15個
レポート: 6個
カスタム: 23個

=== プラグインAPI設計 ===
🛠️ 拡張ポイント定義:

```java
// module-info.java (Plugin API)
module com.framework.plugin.api {
    exports com.framework.plugin.api;
    exports com.framework.plugin.api.data;
    exports com.framework.plugin.api.auth;
    exports com.framework.plugin.api.notification;
    exports com.framework.plugin.api.report;
    
    uses com.framework.plugin.api.DataProcessor;
    uses com.framework.plugin.api.AuthenticationProvider;
    uses com.framework.plugin.api.NotificationService;
    uses com.framework.plugin.api.ReportGenerator;
}

// プラグイン基底インターフェース
public interface Plugin {
    PluginInfo getInfo();
    void initialize(PluginContext context) throws PluginException;
    void start() throws PluginException;
    void stop() throws PluginException;
    void destroy() throws PluginException;
    PluginState getState();
}

// データ処理プラグインSPI
public interface DataProcessor extends Plugin {
    boolean canProcess(DataFormat format);
    ProcessingResult process(DataInput input, ProcessingOptions options) 
        throws ProcessingException;
    List<DataFormat> getSupportedFormats();
    ProcessingCapabilities getCapabilities();
}

// 認証プラグインSPI
public interface AuthenticationProvider extends Plugin {
    boolean supports(AuthenticationType type);
    AuthenticationResult authenticate(AuthenticationRequest request) 
        throws AuthenticationException;
    List<AuthenticationType> getSupportedTypes();
    AuthenticationConfiguration getConfiguration();
}

// プラグイン情報
public record PluginInfo(
    String id,
    String name,
    String version,
    String description,
    String author,
    List<String> dependencies,
    Map<String, String> metadata
) {}

// プラグインローダー
@Service
public class PluginLoader {
    private final Map<String, LoadedPlugin> loadedPlugins = new ConcurrentHashMap<>();
    private final DependencyResolver dependencyResolver = new DependencyResolver();
    private final SecurityValidator securityValidator = new SecurityValidator();
    
    public void discoverAndLoadPlugins() {
        try {
            // プラグインディレクトリスキャン
            List<Path> pluginPaths = discoverPluginJars();
            
            // プラグイン情報解析
            List<PluginDescriptor> descriptors = new ArrayList<>();
            for (Path path : pluginPaths) {
                try {
                    PluginDescriptor descriptor = parsePluginDescriptor(path);
                    descriptors.add(descriptor);
                } catch (Exception e) {
                    logger.warn("プラグイン解析失敗: {}", path, e);
                }
            }
            
            // 依存関係ソート
            List<PluginDescriptor> sortedDescriptors = 
                dependencyResolver.sortByDependencies(descriptors);
            
            // プラグインロード
            for (PluginDescriptor descriptor : sortedDescriptors) {
                try {
                    loadPlugin(descriptor);
                } catch (Exception e) {
                    logger.error("プラグインロード失敗: {}", descriptor.getId(), e);
                }
            }
            
            logger.info("プラグインロード完了: {}個", loadedPlugins.size());
            
        } catch (Exception e) {
            logger.error("プラグイン発見・ロードエラー", e);
        }
    }
    
    private void loadPlugin(PluginDescriptor descriptor) throws PluginLoadException {
        try {
            // セキュリティ検証
            securityValidator.validatePlugin(descriptor);
            
            // 依存関係確認
            dependencyResolver.validateDependencies(descriptor, loadedPlugins.keySet());
            
            // 分離されたクラスローダー作成
            PluginClassLoader classLoader = createPluginClassLoader(descriptor);
            
            // プラグインクラスロード
            Class<?> pluginClass = classLoader.loadClass(descriptor.getMainClass());
            
            // インスタンス作成
            Plugin plugin = (Plugin) pluginClass.getDeclaredConstructor().newInstance();
            
            // プラグインコンテキスト作成
            PluginContext context = createPluginContext(descriptor);
            
            // プラグイン初期化
            plugin.initialize(context);
            
            // プラグイン登録
            LoadedPlugin loadedPlugin = new LoadedPlugin(plugin, descriptor, 
                classLoader, context);
            loadedPlugins.put(descriptor.getId(), loadedPlugin);
            
            // SPIプロバイダー登録
            registerServiceProviders(plugin);
            
            logger.info("プラグインロード完了: {} v{}", 
                descriptor.getName(), descriptor.getVersion());
            
        } catch (Exception e) {
            throw new PluginLoadException("プラグインロード失敗: " + descriptor.getId(), e);
        }
    }
    
    private void registerServiceProviders(Plugin plugin) {
        // DataProcessor SPI登録
        if (plugin instanceof DataProcessor dataProcessor) {
            ServiceLoader.load(DataProcessor.class)
                .reload(); // 再ロードでSPIプロバイダー更新
            
            dataProcessorRegistry.register(dataProcessor);
        }
        
        // AuthenticationProvider SPI登録
        if (plugin instanceof AuthenticationProvider authProvider) {
            ServiceLoader.load(AuthenticationProvider.class)
                .reload();
            
            authProviderRegistry.register(authProvider);
        }
        
        // 他のSPIも同様に登録...
    }
}
```

プラグインロード実行例:
```
=== プラグインロードログ ===

プラグイン発見:
スキャンディレクトリ: /plugins/
発見ファイル数: 47個
有効プラグイン: 41個
無効プラグイン: 6個 (署名検証失敗等)

依存関係解決:
依存関係グラフ構築: 完了
循環依存検査: 異常なし
ロード順序決定: 41ステップ

プラグインロードプロセス:
14:30:30.001 - CoreUtilities v1.2.0 ロード開始
14:30:30.145 - CoreUtilities v1.2.0 ロード完了 (144ms)
14:30:30.146 - DataValidation v2.1.5 ロード開始
14:30:30.289 - DataValidation v2.1.5 ロード完了 (143ms)
...
14:30:32.567 - AdvancedReports v3.0.2 ロード完了 (89ms)

ロード結果:
成功: 41個
失敗: 0個
総ロード時間: 2.566秒
平均ロード時間: 62ms/プラグイン

SPIプロバイダー登録:
DataProcessor: 12プロバイダー
AuthenticationProvider: 8プロバイダー  
NotificationService: 15プロバイダー
ReportGenerator: 6プロバイダー

メモリ使用量:
プラグイン総メモリ: 234MB
分離クラスローダー: 41個
ロードクラス数: 3,847個
```

=== プラグイン実行例 ===
```java
// データ処理プラグインの使用
@Service
public class DataProcessingService {
    
    public ProcessingResult processData(DataInput input) {
        // 適切なプラグイン選択
        Optional<DataProcessor> processor = findProcessorForFormat(input.getFormat());
        
        if (processor.isEmpty()) {
            throw new UnsupportedFormatException("対応プロセッサーが見つかりません: " + 
                input.getFormat());
        }
        
        DataProcessor dataProcessor = processor.get();
        
        // 処理オプション設定
        ProcessingOptions options = ProcessingOptions.builder()
            .parallelProcessing(true)
            .maxMemoryUsage(1024 * 1024 * 1024) // 1GB
            .timeout(Duration.ofMinutes(10))
            .build();
        
        try {
            // プラグインによる処理実行
            ProcessingResult result = dataProcessor.process(input, options);
            
            logger.info("データ処理完了: プロセッサー={}, 処理時間={}ms", 
                dataProcessor.getInfo().name(), result.getProcessingTime());
            
            return result;
            
        } catch (ProcessingException e) {
            logger.error("データ処理エラー", e);
            throw new DataProcessingException("データ処理失敗", e);
        }
    }
    
    private Optional<DataProcessor> findProcessorForFormat(DataFormat format) {
        return ServiceLoader.load(DataProcessor.class)
            .stream()
            .map(ServiceLoader.Provider::get)
            .filter(processor -> processor.canProcess(format))
            .max(Comparator.comparing(this::calculateProcessorScore));
    }
    
    private int calculateProcessorScore(DataProcessor processor) {
        // プロセッサーの優先度計算
        ProcessingCapabilities caps = processor.getCapabilities();
        int score = 0;
        
        score += caps.supportsParallelProcessing() ? 100 : 0;
        score += caps.getMaxThroughput() / 1000; // MB/s to score
        score += caps.supportsStreaming() ? 50 : 0;
        
        return score;
    }
}
```

実行例:
```
=== データ処理実行 ===

処理要求:
データ形式: JSON
ファイルサイズ: 125MB
処理種別: 変換・集計

プラグイン選択:
候補プロセッサー: 3個
- JsonProcessor v2.1.0 (スコア: 245)
- FastJsonProcessor v1.8.2 (スコア: 320) ← 選択
- SimpleJsonProcessor v1.0.1 (スコア: 120)

処理実行:
14:35:15.001 - FastJsonProcessor初期化
14:35:15.023 - 並列処理開始 (8スレッド)
14:35:15.567 - 進捗: 25% (31.25MB処理済み)
14:35:16.234 - 進捗: 50% (62.5MB処理済み)
14:35:16.891 - 進捗: 75% (93.75MB処理済み)
14:35:17.445 - 処理完了 (125MB)

処理結果:
処理時間: 2.444秒
スループット: 51.2MB/秒
メモリ使用量: 89MB (ピーク)
CPU使用率: 76% (平均)
出力レコード数: 1,247,392件
```
```

### 課題3: レイヤードアーキテクチャ実装システム
**ファイル名**: `LayeredArchitectureSystem.java`

厳密なレイヤー分離を持つアーキテクチャシステムを作成してください。

**要求仕様**:
- 4層アーキテクチャの実装
- 層間の単方向依存
- 横断的関心事の適切な分離
- アーキテクチャ制約の自動検証

**レイヤー構成**:
- プレゼンテーション層
- アプリケーション層  
- ドメイン層
- インフラストラクチャ層

**実行例**:
```
=== レイヤードアーキテクチャ実装システム ===

🏗️ ArchitectureGuard v2.0

=== アーキテクチャ構成 ===
📐 4層アーキテクチャ:

レイヤー依存関係:
```
Presentation → Application → Domain
                ↓
           Infrastructure
```

パッケージ構成:
- com.system.presentation (UI層)
- com.system.application (アプリケーション層)
- com.system.domain (ドメイン層)
- com.system.infrastructure (インフラ層)

依存ルール:
✅ Presentation → Application (OK)
✅ Application → Domain (OK)  
✅ Application → Infrastructure (OK)
❌ Domain → Infrastructure (NG)
❌ Domain → Application (NG)
❌ Infrastructure → Domain (NG)

アーキテクチャ制約:
自動検証: 有効
違反検知: コンパイル時
違反レポート: 自動生成
修正提案: AI支援

=== アーキテクチャ実装 ===
🔧 厳密なレイヤー分離:

```java
// ドメイン層 - ビジネスロジック
package com.system.domain.order;

// 純粋なビジネスロジック（インフラ依存なし）
public class Order {
    private OrderId id;
    private CustomerId customerId;
    private List<OrderItem> items;
    private OrderStatus status;
    private Money totalAmount;
    
    // ドメインサービスとして分離
    public void applyDiscount(DiscountPolicy policy) {
        Money discount = policy.calculateDiscount(this);
        this.totalAmount = this.totalAmount.subtract(discount);
        
        // ドメインイベント発行
        DomainEventPublisher.publish(new DiscountAppliedEvent(id, discount));
    }
    
    // ビジネスルール
    public void addItem(Product product, Quantity quantity) {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalOrderOperationException("確定済み注文は変更できません");
        }
        
        if (quantity.isZeroOrNegative()) {
            throw new InvalidQuantityException("数量は正の値である必要があります");
        }
        
        OrderItem item = new OrderItem(product, quantity);
        items.add(item);
        recalculateTotal();
        
        DomainEventPublisher.publish(new ItemAddedEvent(id, item));
    }
    
    public void confirm(PaymentMethod paymentMethod) {
        if (items.isEmpty()) {
            throw new EmptyOrderException("空の注文は確定できません");
        }
        
        if (totalAmount.isZeroOrNegative()) {
            throw new InvalidOrderAmountException("注文金額が無効です");
        }
        
        this.status = OrderStatus.CONFIRMED;
        
        DomainEventPublisher.publish(new OrderConfirmedEvent(id, paymentMethod));
    }
}

// ドメインサービス
@DomainService
public class DiscountPolicyService {
    
    public Money calculateBestDiscount(Order order, List<DiscountPolicy> policies) {
        return policies.stream()
            .map(policy -> policy.calculateDiscount(order))
            .max(Money::compareTo)
            .orElse(Money.ZERO);
    }
    
    public boolean isEligibleForVipDiscount(Customer customer) {
        return customer.getTotalPurchaseAmount().isGreaterThan(Money.of(1_000_000)) &&
               customer.getMembershipYears() >= 3;
    }
}

// リポジトリインターフェース（ドメイン層で定義）
public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(OrderId id);
    List<Order> findByCustomerId(CustomerId customerId);
    List<Order> findByStatus(OrderStatus status);
}
```

=== アプリケーション層 ===
```java
// アプリケーションサービス
package com.system.application.order;

@ApplicationService
@Transactional
public class OrderApplicationService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final InventoryService inventoryService;
    private final PaymentService paymentService;
    private final NotificationService notificationService;
    
    // ユースケース実装
    public OrderResult createOrder(CreateOrderCommand command) {
        try {
            // 入力検証
            validateCreateOrderCommand(command);
            
            // 顧客確認
            Customer customer = customerRepository.findById(command.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(command.getCustomerId()));
            
            // 注文作成
            Order order = new Order(OrderId.generate(), command.getCustomerId());
            
            // 商品追加
            for (CreateOrderCommand.Item itemCmd : command.getItems()) {
                Product product = productRepository.findById(itemCmd.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(itemCmd.getProductId()));
                
                // 在庫確認（インフラサービス呼び出し）
                if (!inventoryService.checkAvailability(itemCmd.getProductId(), 
                                                       itemCmd.getQuantity())) {
                    throw new InsufficientInventoryException(itemCmd.getProductId());
                }
                
                order.addItem(product, itemCmd.getQuantity());
            }
            
            // 割引適用
            List<DiscountPolicy> applicablePolicies = 
                findApplicableDiscountPolicies(customer, order);
            
            for (DiscountPolicy policy : applicablePolicies) {
                order.applyDiscount(policy);
            }
            
            // 注文保存
            Order savedOrder = orderRepository.save(order);
            
            // 非同期通知（インフラサービス）
            notificationService.sendOrderCreatedNotification(customer, savedOrder);
            
            return OrderResult.success(savedOrder);
            
        } catch (DomainException e) {
            logger.warn("注文作成失敗（ドメインエラー）: {}", e.getMessage());
            return OrderResult.failure(e.getMessage());
            
        } catch (Exception e) {
            logger.error("注文作成失敗（システムエラー）", e);
            throw new OrderCreationException("注文作成中にシステムエラーが発生しました", e);
        }
    }
    
    public PaymentResult processPayment(ProcessPaymentCommand command) {
        try {
            // 注文取得
            Order order = orderRepository.findById(command.getOrderId())
                .orElseThrow(() -> new OrderNotFoundException(command.getOrderId()));
            
            // 支払い処理（インフラサービス）
            PaymentRequest paymentRequest = new PaymentRequest(
                order.getTotalAmount(),
                command.getPaymentMethod(),
                command.getCardDetails()
            );
            
            PaymentResponse paymentResponse = paymentService.processPayment(paymentRequest);
            
            if (paymentResponse.isSuccessful()) {
                // 注文確定
                order.confirm(command.getPaymentMethod());
                orderRepository.save(order);
                
                // 在庫引当（インフラサービス）
                for (OrderItem item : order.getItems()) {
                    inventoryService.reserveInventory(item.getProduct().getId(), 
                                                     item.getQuantity());
                }
                
                return PaymentResult.success(paymentResponse.getTransactionId());
            } else {
                return PaymentResult.failure(paymentResponse.getErrorMessage());
            }
            
        } catch (Exception e) {
            logger.error("支払い処理エラー", e);
            throw new PaymentProcessingException("支払い処理中にエラーが発生しました", e);
        }
    }
}

// アプリケーションイベントハンドラー
@EventHandler
public class OrderEventHandler {
    
    @Async
    @EventListener
    public void handleOrderConfirmed(OrderConfirmedEvent event) {
        // 在庫更新
        inventoryService.updateInventory(event.getOrderId());
        
        // 配送手配
        shippingService.arrangeShipping(event.getOrderId());
        
        // 顧客通知
        notificationService.sendOrderConfirmationEmail(event.getCustomerId());
    }
    
    @EventListener
    public void handlePaymentFailed(PaymentFailedEvent event) {
        // 注文キャンセル
        orderApplicationService.cancelOrder(event.getOrderId());
        
        // 在庫解放
        inventoryService.releaseReservedInventory(event.getOrderId());
    }
}
```

=== インフラストラクチャ層 ===
```java
// リポジトリ実装
package com.system.infrastructure.persistence;

@Repository
public class JpaOrderRepository implements OrderRepository {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public Order save(Order order) {
        OrderJpaEntity entity = OrderMapper.toEntity(order);
        entity = entityManager.merge(entity);
        return OrderMapper.toDomain(entity);
    }
    
    @Override
    public Optional<Order> findById(OrderId id) {
        OrderJpaEntity entity = entityManager.find(OrderJpaEntity.class, id.getValue());
        return Optional.ofNullable(entity).map(OrderMapper::toDomain);
    }
    
    @Override
    @Query("SELECT o FROM OrderJpaEntity o WHERE o.customerId = :customerId")
    public List<Order> findByCustomerId(CustomerId customerId) {
        TypedQuery<OrderJpaEntity> query = entityManager.createQuery(
            "SELECT o FROM OrderJpaEntity o WHERE o.customerId = :customerId", 
            OrderJpaEntity.class);
        query.setParameter("customerId", customerId.getValue());
        
        return query.getResultList().stream()
            .map(OrderMapper::toDomain)
            .collect(Collectors.toList());
    }
}

// 外部サービス連携
@Service
public class ExternalPaymentService implements PaymentService {
    
    private final PaymentGatewayClient paymentClient;
    private final PaymentConfigurationProperties config;
    
    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        try {
            // 外部決済API呼び出し
            PaymentGatewayRequest gatewayRequest = new PaymentGatewayRequest(
                request.getAmount().getValue(),
                request.getAmount().getCurrency().getCurrencyCode(),
                request.getCardDetails().getNumber(),
                request.getCardDetails().getExpiryMonth(),
                request.getCardDetails().getExpiryYear(),
                request.getCardDetails().getCvv()
            );
            
            PaymentGatewayResponse gatewayResponse = 
                paymentClient.processPayment(gatewayRequest);
            
            if (gatewayResponse.isSuccess()) {
                return PaymentResponse.success(
                    gatewayResponse.getTransactionId(),
                    gatewayResponse.getAmount()
                );
            } else {
                return PaymentResponse.failure(
                    gatewayResponse.getErrorCode(),
                    gatewayResponse.getErrorMessage()
                );
            }
            
        } catch (PaymentGatewayException e) {
            logger.error("決済ゲートウェイエラー", e);
            return PaymentResponse.failure("GATEWAY_ERROR", 
                "決済ゲートウェイとの通信に失敗しました");
        }
    }
}
```

実行例:
```
=== レイヤードアーキテクチャ実行ログ ===

注文作成フロー:
14:40:00.001 - [Presentation] REST API リクエスト受信
14:40:00.003 - [Application] CreateOrderCommand検証開始
14:40:00.005 - [Application] 顧客情報取得開始
14:40:00.012 - [Infrastructure] データベースクエリ実行
14:40:00.018 - [Application] 顧客確認完了
14:40:00.019 - [Domain] Order エンティティ生成
14:40:00.020 - [Application] 商品情報取得開始
14:40:00.025 - [Infrastructure] 商品データベースアクセス
14:40:00.031 - [Application] 在庫確認開始
14:40:00.034 - [Infrastructure] 在庫システムAPI呼び出し
14:40:00.067 - [Application] 在庫確認完了
14:40:00.068 - [Domain] OrderItem追加処理
14:40:00.069 - [Domain] 合計金額再計算
14:40:00.070 - [Domain] ドメインイベント発行
14:40:00.071 - [Application] 割引ポリシー適用
14:40:00.073 - [Domain] 割引計算実行
14:40:00.074 - [Application] 注文保存開始
14:40:00.076 - [Infrastructure] データベース保存実行
14:40:00.089 - [Application] 注文作成完了

アーキテクチャ制約チェック:
✅ レイヤー依存関係: 違反なし
✅ 循環依存: 検出なし
✅ ドメイン純粋性: 維持
✅ インフラ分離: 適切

処理パフォーマンス:
総処理時間: 88ms
- Presentation: 2ms
- Application: 71ms  
- Domain: 9ms
- Infrastructure: 6ms

レイヤー別メトリクス:
呼び出し回数: P:1, A:15, D:8, I:12
平均レスポンス: P:2ms, A:4.7ms, D:1.1ms, I:0.5ms
エラー率: P:0%, A:0%, D:0%, I:0%
```
```

## 🎯 習得すべき技術要素

### モジュールシステム
- JPMS (Java Platform Module System)
- モジュール宣言 (module-info.java)
- 依存関係管理
- カプセル化の強化

### アーキテクチャパターン
- レイヤードアーキテクチャ
- ヘキサゴナルアーキテクチャ
- 依存性逆転の原則
- 関心の分離

### プラグインアーキテクチャ
- SPI (Service Provider Interface)
- 動的クラスローディング
- セキュリティマネージャー
- プラグインライフサイクル

## 📚 参考リソース

- Java Platform Module System (JPMS) Specification
- Clean Architecture (Robert C. Martin)
- Building Modular Cloud Apps with OSGi (Siebenrock)
- Enterprise Integration Patterns (Hohpe & Woolf)

## ⚠️ 重要な注意事項

モジュールシステムとアーキテクチャ設計では、将来の拡張性と保守性を十分に考慮してください。早期の最適化よりも、明確な構造と責任分離を優先することが重要です。