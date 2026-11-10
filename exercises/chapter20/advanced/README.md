# 第20章 応用課題

## 学習目標
- 高度なMVCアーキテクチャパターン
- エンタープライズMVP/MVVM実装
- リアクティブプログラミング統合
- モダンアーキテクチャパターン応用
- 大規模アプリケーション設計

## 課題一覧

### 課題1: エンタープライズMVVM統合プラットフォーム
**ファイル名**: `EnterpriseMVVMIntegratedPlatform.java`

大規模エンタープライズ向けのMVVMアーキテクチャを中心とした統合プラットフォームを作成してください。

**要求仕様**:
- MVVM + Reactive Streams
- 双方向データバインディング
- 依存性注入フレームワーク
- 非同期処理統合

**アーキテクチャ構成**:
- Model: データ層 + ビジネスロジック
- ViewModel: プレゼンテーション層
- View: UI層 (JavaFX/Swing統合)
- Service: 外部システム連携

**実行例**:
```
=== エンタープライズMVVM統合プラットフォーム ===

EnterpriseMVVM Platform v4.0

=== アーキテクチャ概要 ===
統合アプリケーション構成:

アプリケーション階層:
Presentation Layer:
- JavaFX Views: 45個
- Swing Legacy Views: 23個
- Web Views (WebEngine): 12個

ViewModel Layer:
- ViewModels: 68個
- Commands: 247個
- ObservableProperties: 1,847個
- Validators: 156個

Model Layer:
- Domain Models: 89個
- Data Models: 134個
- Repository Interfaces: 45個
- Business Services: 67個

Infrastructure Layer:
- Database Repositories: 45個
- Web API Clients: 23個
- Message Queues: 8個
- File Systems: 12個

=== MVVM実装 ===
双方向データバインディング:

```java
@Component
public class EnterpriseMVVMPlatform {
    private final ViewModelFactory viewModelFactory;
    private final ModelFactory modelFactory;
    private final ServiceRegistry serviceRegistry;
    private final EventBus eventBus;
    private final DataBindingEngine bindingEngine;
    
    public EnterpriseMVVMPlatform() {
        initializePlatform();
        setupDependencyInjection();
        configureDataBinding();
        startReactiveStreams();
    }
    
    private void initializePlatform() {
        // ViewModelファクトリー
        this.viewModelFactory = new ViewModelFactory();
        
        // Modelファクトリー
        this.modelFactory = new ModelFactory();
        
        // サービスレジストリ
        this.serviceRegistry = new ServiceRegistry();
        
        // イベントバス
        this.eventBus = new AsyncEventBus();
        
        // データバインディングエンジン
        this.bindingEngine = new ReactiveDataBindingEngine();
        
        setupArchitecture();
    }
    
    private void setupArchitecture() {
        // Model層セットアップ
        setupModelLayer();
        
        // ViewModel層セットアップ
        setupViewModelLayer();
        
        // View層セットアップ
        setupViewLayer();
        
        // Service層セットアップ
        setupServiceLayer();
    }
    
    private void setupModelLayer() {
        // ドメインモデル
        modelFactory.registerModel(UserModel.class);
        modelFactory.registerModel(OrderModel.class);
        modelFactory.registerModel(ProductModel.class);
        modelFactory.registerModel(CustomerModel.class);
        
        // データアクセス層
        modelFactory.registerRepository(UserRepository.class);
        modelFactory.registerRepository(OrderRepository.class);
        modelFactory.registerRepository(ProductRepository.class);
        
        // ビジネスサービス
        modelFactory.registerService(UserService.class);
        modelFactory.registerService(OrderService.class);
        modelFactory.registerService(PaymentService.class);
    }
    
    private void setupViewModelLayer() {
        // ViewModel登録
        viewModelFactory.registerViewModel("UserManagement", 
            UserManagementViewModel.class);
        viewModelFactory.registerViewModel("OrderProcessing",
            OrderProcessingViewModel.class);
        viewModelFactory.registerViewModel("ProductCatalog",
            ProductCatalogViewModel.class);
        viewModelFactory.registerViewModel("Dashboard",
            DashboardViewModel.class);
        
        // コマンドセットアップ
        setupCommands();
        
        // バリデーション設定
        setupValidation();
    }
    
    private void setupViewLayer() {
        // JavaFX Views
        ViewRegistry.registerView("UserManagementView", 
            UserManagementView.class);
        ViewRegistry.registerView("OrderProcessingView",
            OrderProcessingView.class);
        
        // Swing Legacy Views
        ViewRegistry.registerLegacyView("LegacyReportView",
            LegacyReportView.class);
        
        // Web Views
        ViewRegistry.registerWebView("WebDashboard",
            "dashboard.html");
    }
}

// 高度なViewModel実装
public class UserManagementViewModel extends BaseViewModel {
    // Observable Properties
    private final ObservableList<User> users;
    private final ObservableProperty<User> selectedUser;
    private final ObservableProperty<String> searchText;
    private final ObservableProperty<Boolean> isLoading;
    private final ObservableProperty<String> statusMessage;
    
    // Commands
    private final Command loadUsersCommand;
    private final Command createUserCommand;
    private final Command updateUserCommand;
    private final Command deleteUserCommand;
    private final Command searchUsersCommand;
    
    // Services
    private final UserService userService;
    private final ValidationService validationService;
    
    public UserManagementViewModel(UserService userService,
                                  ValidationService validationService) {
        this.userService = userService;
        this.validationService = validationService;
        
        initializeProperties();
        initializeCommands();
        setupValidation();
        setupReactiveStreams();
    }
    
    private void initializeProperties() {
        // Observable Properties初期化
        this.users = FXCollections.observableArrayList();
        this.selectedUser = new ObservableProperty<>();
        this.searchText = new ObservableProperty<>("");
        this.isLoading = new ObservableProperty<>(false);
        this.statusMessage = new ObservableProperty<>("");
        
        // プロパティ変更監視
        selectedUser.addListener(this::onSelectedUserChanged);
        searchText.addListener(this::onSearchTextChanged);
    }
    
    private void initializeCommands() {
        // ユーザー読み込みコマンド
        this.loadUsersCommand = new AsyncCommand(
            this::canLoadUsers,
            this::executeLoadUsers
        );
        
        // ユーザー作成コマンド
        this.createUserCommand = new AsyncCommand(
            this::canCreateUser,
            this::executeCreateUser
        );
        
        // ユーザー更新コマンド
        this.updateUserCommand = new AsyncCommand(
            this::canUpdateUser,
            this::executeUpdateUser
        );
        
        // ユーザー削除コマンド
        this.deleteUserCommand = new AsyncCommand(
            this::canDeleteUser,
            this::executeDeleteUser
        );
        
        // 検索コマンド
        this.searchUsersCommand = new AsyncCommand(
            this::canSearchUsers,
            this::executeSearchUsers
        );
    }
    
    private void setupReactiveStreams() {
        // リアクティブな検索
        searchText.asObservable()
            .debounce(300, TimeUnit.MILLISECONDS) // 300ms デバウンス
            .distinctUntilChanged()
            .filter(text -> text.length() >= 2)
            .switchMap(text -> userService.searchUsers(text))
            .subscribeOn(Schedulers.io())
            .observeOn(JavaFxScheduler.platform())
            .subscribe(
                this::updateUsersList,
                this::handleSearchError
            );
        
        // 選択ユーザー変更の連鎖反応
        selectedUser.asObservable()
            .map(user -> user != null)
            .subscribe(hasSelection -> {
                updateUserCommand.setCanExecute(hasSelection);
                deleteUserCommand.setCanExecute(hasSelection);
            });
    }
    
    private CompletableFuture<Void> executeLoadUsers() {
        isLoading.set(true);
        statusMessage.set("ユーザー一覧を読み込み中...");
        
        return userService.getAllUsersAsync()
            .thenAccept(userList -> {
                Platform.runLater(() -> {
                    users.setAll(userList);
                    statusMessage.set(String.format("%d件のユーザーを読み込みました", 
                        userList.size()));
                });
            })
            .exceptionally(throwable -> {
                Platform.runLater(() -> {
                    statusMessage.set("エラー: " + throwable.getMessage());
                });
                return null;
            })
            .whenComplete((result, throwable) -> {
                Platform.runLater(() -> isLoading.set(false));
            });
    }
    
    private CompletableFuture<Void> executeCreateUser() {
        User newUser = createNewUserFromForm();
        
        // バリデーション
        ValidationResult validation = validationService.validate(newUser);
        if (!validation.isValid()) {
            showValidationErrors(validation);
            return CompletableFuture.completedFuture(null);
        }
        
        isLoading.set(true);
        statusMessage.set("ユーザーを作成中...");
        
        return userService.createUserAsync(newUser)
            .thenAccept(createdUser -> {
                Platform.runLater(() -> {
                    users.add(createdUser);
                    selectedUser.set(createdUser);
                    statusMessage.set("ユーザーを作成しました");
                    clearForm();
                });
            })
            .exceptionally(throwable -> {
                Platform.runLater(() -> {
                    statusMessage.set("作成エラー: " + throwable.getMessage());
                });
                return null;
            })
            .whenComplete((result, throwable) -> {
                Platform.runLater(() -> isLoading.set(false));
            });
    }
}

// データバインディングエンジン
public class ReactiveDataBindingEngine {
    private final Map<String, Binding> bindings;
    private final ObservablePropertyFactory propertyFactory;
    
    public ReactiveDataBindingEngine() {
        this.bindings = new ConcurrentHashMap<>();
        this.propertyFactory = new ObservablePropertyFactory();
    }
    
    public <T> void bind(ObservableProperty<T> viewModelProperty,
                        Property<T> viewProperty) {
        // 双方向データバインディング
        Binding<T> binding = new TwoWayBinding<>(viewModelProperty, viewProperty);
        
        // ViewModelからViewへ
        viewModelProperty.addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                if (!Objects.equals(viewProperty.getValue(), newValue)) {
                    viewProperty.setValue(newValue);
                }
            });
        });
        
        // ViewからViewModelへ
        viewProperty.addListener((observable, oldValue, newValue) -> {
            if (!Objects.equals(viewModelProperty.get(), newValue)) {
                viewModelProperty.set(newValue);
            }
        });
        
        String bindingId = UUID.randomUUID().toString();
        bindings.put(bindingId, binding);
    }
    
    public <T, R> void bindConverted(ObservableProperty<T> viewModelProperty,
                                   Property<R> viewProperty,
                                   Function<T, R> viewModelToView,
                                   Function<R, T> viewToViewModel) {
        // 変換付きバインディング
        viewModelProperty.addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                R convertedValue = viewModelToView.apply(newValue);
                if (!Objects.equals(viewProperty.getValue(), convertedValue)) {
                    viewProperty.setValue(convertedValue);
                }
            });
        });
        
        viewProperty.addListener((observable, oldValue, newValue) -> {
            T convertedValue = viewToViewModel.apply(newValue);
            if (!Objects.equals(viewModelProperty.get(), convertedValue)) {
                viewModelProperty.set(convertedValue);
            }
        });
    }
}

// 非同期コマンド実装
public class AsyncCommand implements Command {
    private final Supplier<Boolean> canExecutePredicate;
    private final Supplier<CompletableFuture<Void>> executeFunction;
    private final ObservableProperty<Boolean> canExecute;
    private final ObservableProperty<Boolean> isExecuting;
    
    public AsyncCommand(Supplier<Boolean> canExecutePredicate,
                       Supplier<CompletableFuture<Void>> executeFunction) {
        this.canExecutePredicate = canExecutePredicate;
        this.executeFunction = executeFunction;
        this.canExecute = new ObservableProperty<>(true);
        this.isExecuting = new ObservableProperty<>(false);
        
        updateCanExecute();
    }
    
    @Override
    public CompletableFuture<Void> executeAsync() {
        if (!canExecute.get()) {
            return CompletableFuture.completedFuture(null);
        }
        
        isExecuting.set(true);
        updateCanExecute();
        
        return executeFunction.get()
            .whenComplete((result, throwable) -> {
                Platform.runLater(() -> {
                    isExecuting.set(false);
                    updateCanExecute();
                });
            });
    }
    
    private void updateCanExecute() {
        boolean newCanExecute = !isExecuting.get() && 
                               canExecutePredicate.get();
        canExecute.set(newCanExecute);
    }
}
```

実行ログ:
```
=== MVVM プラットフォーム実行ログ ===

プラットフォーム初期化:
14:30:00.001 - 依存性注入コンテナ起動
14:30:00.015 - Model層初期化完了 (89個のモデル)
14:30:00.028 - ViewModel層初期化完了 (68個のViewModel)
14:30:00.041 - View層初期化完了 (80個のView)
14:30:00.055 - Service層初期化完了 (67個のService)
14:30:00.068 - データバインディング設定完了
14:30:00.082 - リアクティブストリーム開始
14:30:00.095 - プラットフォーム起動完了

ユーザー管理機能実行:
14:30:15.123 - UserManagementView表示要求
14:30:15.125 - UserManagementViewModel作成
14:30:15.128 - データバインディング確立
14:30:15.131 - loadUsersCommand実行開始
14:30:15.134 - UserService.getAllUsersAsync()呼び出し
14:30:15.245 - 1,247件のユーザーデータ取得完了
14:30:15.248 - ObservableList更新
14:30:15.251 - View自動更新 (データバインディング)
14:30:15.254 - ステータス更新: "1,247件のユーザーを読み込みました"

リアクティブ検索実行:
14:30:45.567 - 検索テキスト入力: "ta"
14:30:45.570 - デバウンス待機中...
14:30:45.890 - 検索テキスト確定: "tanaka"
14:30:45.892 - UserService.searchUsers("tanaka")実行
14:30:45.956 - 23件の検索結果取得
14:30:45.958 - ユーザーリスト更新
14:30:45.961 - View自動更新

データバインディング統計:
確立済みバインディング: 1,847個
双方向バインディング: 1,234個
変換バインディング: 456個
コマンドバインディング: 157個
バインディング同期エラー: 0件
```
```

### 課題2: リアクティブ統合アーキテクチャ
**ファイル名**: `ReactiveIntegratedArchitecture.java`

Reactive Streams、RxJava、Project Reactorを統合した高度なリアクティブアーキテクチャを作成してください。

**要求仕様**:
- マルチリアクティブライブラリ統合
- 背圧制御
- エラーハンドリング戦略
- パフォーマンス最適化

**実行例**:
```
=== リアクティブ統合アーキテクチャ ===

ReactiveArch v3.0

=== リアクティブストリーム構成 ===
非同期データフロー:

ライブラリ統合:
- RxJava 3.x: UI層リアクティブ処理
- Project Reactor: Web層・データ層
- Akka Streams: 大容量データ処理
- Reactive Streams: 標準化インターフェイス

ストリーム統計:
アクティブストリーム: 247個
処理済みイベント: 12,847,562件
バックプレッシャー発生: 23回
エラー処理: 156件
平均スループット: 50,000 events/sec

=== データフロー例 ===
```
ユーザーイベント → debounce(300ms) → distinctUntilChanged() 
     ↓
バリデーション → filter(valid) → map(transform)
     ↓  
ビジネスロジック → flatMap(async) → retry(3)
     ↓
データベース → observeOn(io) → subscribe(update)
     ↓
UI更新 → observeOn(fx) → bindTo(properties)
```

背圧制御:
DROP: 25,234回 (過負荷時の最新データ保持)
BUFFER: 12,891回 (一時バッファリング)
LATEST: 8,456回 (最新値のみ保持)
ERROR: 0回 (エラー発生なし)
```

### 課題3: マイクロフロントエンドアーキテクチャ
**ファイル名**: `MicroFrontendArchitecture.java`

Javaベースのマイクロフロントエンドアーキテクチャを実装してください。

**要求仕様**:
- コンポーネント分離
- 独立デプロイメント
- イベント間通信
- 統合シェル

**実行例**:
```
=== マイクロフロントエンドアーキテクチャ ===

MicroFrontend v2.0

=== マイクロフロントエンド構成 ===
分離されたコンポーネント:

登録済みマイクロフロントエンド:
1. user-management-mfe (Port: 8081)
   - 技術: JavaFX + Spring Boot
   - 機能: ユーザー管理
   - 状態: アクティブ

2. order-processing-mfe (Port: 8082)
   - 技術: Swing + Reactive Streams
   - 機能: 注文処理
   - 状態: アクティブ

3. analytics-dashboard-mfe (Port: 8083)
   - 技術: WebView + Chart.js
   - 機能: 分析ダッシュボード
   - 状態: アクティブ

統合シェル:
- ナビゲーション統合
- 認証状態共有
- テーマ統一
- イベント配信

イベント間通信:
送信イベント: 12,847件
受信イベント: 11,923件
ルーティングエラー: 0件
通信遅延: 平均12ms
```

## 習得すべき技術要素

### アーキテクチャパターン
- MVVM (Model-View-ViewModel)
- MVP (Model-View-Presenter)
- 依存性注入パターン
- ファクトリーパターン

### リアクティブプログラミング
- Reactive Streams仕様
- RxJava Observables
- Project Reactor Flux/Mono
- バックプレッシャー制御

### データバインディング
- 双方向データバインディング
- 変換バインディング
- コマンドバインディング
- 検証バインディング

## 参考リソース

- Reactive Programming with RxJava
- Hands-On Reactive Programming with Reactor
- JavaFX Enterprise Application Development
- Modern Java EE Design Patterns

## 重要な注意事項

大規模なMVVMアーキテクチャでは、適切な関心の分離と依存性管理が重要です。パフォーマンスとメモリ使用量に注意して実装してください。