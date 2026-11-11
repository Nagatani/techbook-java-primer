# 第21章 基本課題

## 学習目標
- JUnit5を使った単体テストの作成
- テスト駆動開発（TDD）の実践
- モックオブジェクトとスタブの活用
- テストカバレッジの理解と向上
- 継続的インテグレーション（CI）の基本
- 境界値分析とエラー推測による効果的なテストケース設計

## 課題一覧

### 課題1: 基本的な単体テスト作成
**ファイル名**: `Calculator.java`, `CalculatorTest.java`, `StringUtils.java`, `StringUtilsTest.java`

基本的なクラスに対する包括的な単体テストを作成してください。

**要求仕様**:
- JUnit5のアノテーション活用（@Test、@BeforeEach等）
- アサーションメソッドの適切な使用
- 境界値テストと例外テスト
- パラメータ化テスト
- テストデータの管理

**実行例**:
```
=== 基本的な単体テスト作成 ===

Calculator クラステスト実行:
○ testAdd_正の数の加算
○ testAdd_負の数の加算  
○ testAdd_ゼロの加算
○ testSubtract_正の数の減算
○ testSubtract_負の数の減算
○ testMultiply_正の数の乗算
○ testMultiply_ゼロの乗算
○ testDivide_正常な除算
× testDivide_ゼロ除算例外
○ testSqrt_正の数の平方根
× testSqrt_負の数例外

テスト結果: 9/11 成功 (81.8%)

StringUtils クラステスト実行:
○ testIsEmpty_空文字列
○ testIsEmpty_null文字列
○ testIsEmpty_空白文字列
○ testReverse_通常文字列
○ testReverse_空文字列
○ testReverse_一文字
○ testCapitalize_通常文字列
○ testCapitalize_空文字列
○ testCapitalize_null文字列

テスト結果: 9/9 成功 (100%)

詳細テストケース:

Calculator.add() テスト:
@Test
void testAdd_正の数() {
    Calculator calc = new Calculator();
    assertEquals(5, calc.add(2, 3));
}

@Test  
void testAdd_負の数() {
    Calculator calc = new Calculator();
    assertEquals(-1, calc.add(-3, 2));
}

@ParameterizedTest
@CsvSource({
    "1, 1, 2",
    "2, 3, 5", 
    "-1, 1, 0",
    "0, 0, 0"
})
void testAdd_パラメータ化(int a, int b, int expected) {
    Calculator calc = new Calculator();
    assertEquals(expected, calc.add(a, b));
}

例外テスト:
@Test
void testDivide_ゼロ除算() {
    Calculator calc = new Calculator();
    assertThrows(ArithmeticException.class, 
        () -> calc.divide(10, 0));
}

@Test
void testSqrt_負の数() {
    Calculator calc = new Calculator();
    IllegalArgumentException exception = 
        assertThrows(IllegalArgumentException.class,
            () -> calc.sqrt(-4));
    assertEquals("負の数の平方根は計算できません", 
        exception.getMessage());
}

境界値テスト:
@Test
void testAdd_最大値境界() {
    Calculator calc = new Calculator();
    // Integer.MAX_VALUE の境界テスト
    assertThrows(ArithmeticException.class,
        () -> calc.add(Integer.MAX_VALUE, 1));
}

@Test
void testDivide_小数点精度() {
    Calculator calc = new Calculator();
    assertEquals(0.333333, calc.divide(1, 3), 0.000001);
}

テストライフサイクル:
@BeforeEach
void setUp() {
    calculator = new Calculator();
    IO.println("テスト準備完了");
}

@AfterEach  
void tearDown() {
    calculator = null;
    IO.println("テスト後処理完了");
}

@BeforeAll
static void setUpClass() {
    IO.println("テストクラス初期化");
}

@AfterAll
static void tearDownClass() {
    IO.println("テストクラス終了処理");
}

テスト実行ログ:
[INFO] テストクラス初期化
[INFO] テスト準備完了
[INFO] testAdd_正の数の加算 - 成功
[INFO] テスト後処理完了
[INFO] テスト準備完了
[INFO] testAdd_負の数の加算 - 成功
[INFO] テスト後処理完了
...
[INFO] テストクラス終了処理

カバレッジレポート:
Calculator.java:
- 行カバレッジ: 85% (17/20行)
- 分岐カバレッジ: 75% (6/8分岐)
- 未テストコード: エラーハンドリング部分

StringUtils.java:
- 行カバレッジ: 100% (25/25行)
- 分岐カバレッジ: 100% (12/12分岐)
```

**評価ポイント**:
- JUnit5の基本機能の理解
- 包括的なテストケースの作成
- 適切なアサーションの使用



### 課題2: テスト駆動開発（TDD）実践
**ファイル名**: `BankAccount.java`, `BankAccountTest.java`, `PasswordValidator.java`, `PasswordValidatorTest.java`

TDDのサイクル（Red-Green-Refactor）を実践し、テストファーストでクラスを作成してください。

**要求仕様**:
- 失敗するテストの作成（Red）
- 最小限の実装で成功させる（Green）
- リファクタリングによる改善（Refactor）
- 段階的な機能追加
- テストケースの充実

**実行例**:
```
=== テスト駆動開発（TDD）実践 ===

TDDサイクル1: BankAccount基本機能

Red フェーズ:
× testCreateAccount_初期残高ゼロ
× testDeposit_正常入金
× testWithdraw_正常出金
× testWithdraw_残高不足例外

実装前テスト実行:
コンパイルエラー: BankAccountクラスが存在しません

Green フェーズ:
最小限実装:
public class BankAccount {
    private double balance = 0;
    
    public double getBalance() { return balance; }
    public void deposit(double amount) { balance += amount; }
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount > balance) throw new InsufficientFundsException();
        balance -= amount;
    }
}

テスト実行結果:
○ testCreateAccount_初期残高ゼロ
○ testDeposit_正常入金
○ testWithdraw_正常出金
○ testWithdraw_残高不足例外

Refactor フェーズ:
リファクタリング:
- 入力値検証の追加
- メソッドの分割
- 定数の抽出

TDDサイクル2: 入金制限機能追加

Red フェーズ:
× testDeposit_負の金額例外
× testDeposit_ゼロ金額例外
× testDeposit_上限額チェック

実装前テスト実行:
3件のテストが失敗

Green フェーズ:
機能追加:
public void deposit(double amount) throws InvalidAmountException {
    if (amount <= 0) throw new InvalidAmountException("入金額は正の数である必要があります");
    if (balance + amount > MAX_BALANCE) throw new InvalidAmountException("残高上限を超えます");
    balance += amount;
}

テスト実行結果:
○ testDeposit_負の金額例外
○ testDeposit_ゼロ金額例外
○ testDeposit_上限額チェック

Refactor フェーズ:
- バリデーション処理の共通化
- エラーメッセージの定数化

TDDサイクル3: パスワード検証機能

Red フェーズ:
× testValidatePassword_8文字以上
× testValidatePassword_大文字含む
× testValidatePassword_小文字含む
× testValidatePassword_数字含む
× testValidatePassword_特殊文字含む

テスト駆動でのPasswordValidator実装:

@Test
void testValidatePassword_8文字以上() {
    assertFalse(PasswordValidator.isValid("1234567")); // 7文字
    assertTrue(PasswordValidator.isValid("12345678"));  // 8文字
}

@Test
void testValidatePassword_大文字含む() {
    assertFalse(PasswordValidator.isValid("password123"));
    assertTrue(PasswordValidator.isValid("Password123"));
}

Green フェーズ段階実装:
public class PasswordValidator {
    public static boolean isValid(String password) {
        return password.length() >= 8 &&
               hasUpperCase(password) &&
               hasLowerCase(password) &&
               hasDigit(password) &&
               hasSpecialChar(password);
    }
    
    private static boolean hasUpperCase(String password) {
        return password.chars().anyMatch(Character::isUpperCase);
    }
    
    // 他のヘルパーメソッド...
}

全テスト実行結果:
○ testValidatePassword_8文字以上
○ testValidatePassword_大文字含む
○ testValidatePassword_小文字含む
○ testValidatePassword_数字含む
○ testValidatePassword_特殊文字含む

TDD統計:
総サイクル数: 5
実装時間: 45分
テスト作成時間: 30分
リファクタリング時間: 15分

コードメトリクス:
生産性: 15行/分
テストコード比率: 65%
リファクタリング回数: 8回

TDDの利点確認:
○ 要件の明確化
○ デバッグ時間の短縮
○ リグレッション防止
○ 設計の改善
○ 仕様の文書化

ベストプラクティス:
1. 一度に一つの機能をテスト
2. 最小限の実装で成功させる
3. テストコードもリファクタリング対象
4. 意味のあるテスト名を付ける
5. AAA（Arrange-Act-Assert）パターン使用

失敗パターンの学習:
× 複雑すぎるテスト → 分割する
× 実装に依存したテスト → 振る舞いをテスト
× テストの重複 → DRY原則適用
× あいまいなアサーション → 具体的な期待値
```

**評価ポイント**:
- TDDサイクルの正しい実践
- 段階的な機能開発
- 効果的なリファクタリング



### 課題3: モックとスタブを使ったテスト
**ファイル名**: `UserService.java`, `UserServiceTest.java`, `EmailService.java`, `EmailServiceTest.java`

モックフレームワーク（Mockito）を使用し、依存関係を持つクラスのテストを作成してください。

**要求仕様**:
- Mockitoによるモックオブジェクト作成
- スタブによる戻り値の制御
- メソッド呼び出しの検証
- 例外のモック
- スパイオブジェクトの活用

**実行例**:
```
=== モックとスタブを使ったテスト ===

UserService テストセットアップ:
依存関係:
UserService → UserRepository (データアクセス)
UserService → EmailService (メール送信)
UserService → AuditLogger (監査ログ)

モック作成:
@Mock UserRepository mockRepository;
@Mock EmailService mockEmailService;
@Mock AuditLogger mockAuditLogger;

@InjectMocks UserService userService;

スタブテスト例:

@Test
void testFindUser_存在するユーザー() {
    // Arrange (準備)
    User expectedUser = new User(1L, "田中太郎", "tanaka@example.com");
    when(mockRepository.findById(1L)).thenReturn(expectedUser);
    
    // Act (実行)
    User actualUser = userService.findUser(1L);
    
    // Assert (検証)
    assertEquals(expectedUser, actualUser);
    verify(mockRepository).findById(1L);
}

@Test
void testFindUser_存在しないユーザー() {
    // Arrange
    when(mockRepository.findById(999L)).thenReturn(null);
    
    // Act & Assert
    assertThrows(UserNotFoundException.class, 
        () -> userService.findUser(999L));
    
    verify(mockRepository).findById(999L);
}

複雑なスタブパターン:

@Test
void testCreateUser_メール送信成功() {
    // Arrange
    User newUser = new User("佐藤花子", "sato@example.com");
    User savedUser = new User(2L, "佐藤花子", "sato@example.com");
    
    when(mockRepository.save(any(User.class))).thenReturn(savedUser);
    when(mockEmailService.sendWelcomeEmail(any(String.class)))
        .thenReturn(true);
    
    // Act
    User result = userService.createUser(newUser);
    
    // Assert
    assertEquals(savedUser, result);
    
    // メソッド呼び出し検証
    verify(mockRepository).save(newUser);
    verify(mockEmailService).sendWelcomeEmail("sato@example.com");
    verify(mockAuditLogger).logUserCreation(savedUser);
}

@Test
void testCreateUser_メール送信失敗() {
    // Arrange
    User newUser = new User("鈴木一郎", "suzuki@example.com");
    
    when(mockRepository.save(any(User.class)))
        .thenReturn(new User(3L, "鈴木一郎", "suzuki@example.com"));
    when(mockEmailService.sendWelcomeEmail("suzuki@example.com"))
        .thenThrow(new EmailSendException("SMTP server unavailable"));
    
    // Act & Assert
    assertThrows(UserCreationException.class, 
        () -> userService.createUser(newUser));
    
    // ロールバック確認
    verify(mockRepository).delete(any(User.class));
    verify(mockAuditLogger).logError(any(String.class));
}

引数マッチャー使用例:

@Test
void testUpdateUser_部分更新() {
    // Arrange
    Long userId = 1L;
    User existingUser = new User(1L, "田中太郎", "tanaka@example.com");
    User updateData = new User(null, "田中二郎", null);
    
    when(mockRepository.findById(userId)).thenReturn(existingUser);
    when(mockRepository.save(argThat(user -> 
        user.getName().equals("田中二郎") && 
        user.getEmail().equals("tanaka@example.com")
    ))).thenReturn(existingUser);
    
    // Act
    userService.updateUser(userId, updateData);
    
    // Assert
    verify(mockRepository).save(argThat(user -> 
        user.getName().equals("田中二郎")));
}

モック呼び出し回数検証:

@Test
void testBulkUserCreation() {
    // Arrange
    List<User> users = Arrays.asList(
        new User("ユーザー1", "user1@example.com"),
        new User("ユーザー2", "user2@example.com"),
        new User("ユーザー3", "user3@example.com")
    );
    
    when(mockRepository.save(any(User.class)))
        .thenReturn(new User(1L, "dummy", "dummy"));
    when(mockEmailService.sendWelcomeEmail(any(String.class)))
        .thenReturn(true);
    
    // Act
    userService.createUsers(users);
    
    // Assert
    verify(mockRepository, times(3)).save(any(User.class));
    verify(mockEmailService, times(3)).sendWelcomeEmail(any(String.class));
    verify(mockAuditLogger, atLeast(1)).logBulkOperation(any(String.class));
}

スパイオブジェクト使用例:

@Test
void testEmailService_実際のオブジェクトの一部をスパイ() {
    // Arrange
    EmailService realEmailService = new EmailService();
    EmailService spyEmailService = spy(realEmailService);
    
    // 特定のメソッドのみスタブ
    doReturn(true).when(spyEmailService).isServerAvailable();
    
    // Act
    boolean result = spyEmailService.sendEmail("test@example.com", "件名", "本文");
    
    // Assert
    assertTrue(result);
    
    // 実際のメソッドが呼ばれたか確認
    verify(spyEmailService).validateEmailAddress("test@example.com");
    verify(spyEmailService).formatMessage("件名", "本文");
}

カスタムアンサー:

@Test
void testAsyncOperation_カスタム動作() {
    // Arrange
    when(mockEmailService.sendEmailAsync(any(String.class), any(String.class)))
        .thenAnswer(invocation -> {
            String email = invocation.getArgument(0);
            String message = invocation.getArgument(1);
            
            // カスタムロジック
            if (email.contains("@invalid.com")) {
                throw new IllegalArgumentException("Invalid domain");
            }
            
            return CompletableFuture.completedFuture("送信完了: " + email);
        });
    
    // Act & Assert
    CompletableFuture<String> result = userService.sendNotificationAsync(
        "user@example.com", "通知メッセージ");
    
    assertEquals("送信完了: user@example.com", result.get());
}

モック検証統計:
実行テスト数: 15
モック作成数: 45
スタブ設定数: 38
検証実行数: 52
例外モック数: 8

テスト実行時間:
スタブ使用: 0.05秒/テスト
実際の依存関係: 2.3秒/テスト
効果: 46倍高速化

カバレッジ向上:
モック使用前: 45%
モック使用後: 92%
テスト困難部分をモック化により検証
```

**評価ポイント**:
- Mockitoの効果的な活用
- 依存関係の適切な分離
- 複雑なシナリオのテスト実装



### 課題4: テストスイートと継続的インテグレーション
**ファイル名**: `TestSuite.java`, `IntegrationTest.java`, `PerformanceTest.java`, `CITest.java`

包括的なテストスイートを作成し、継続的インテグレーションの基盤を構築してください。

**要求仕様**:
- JUnit5のテストスイート機能
- 統合テストとパフォーマンステスト
- テストカテゴリの分類
- Maven/Gradleとの連携
- CI/CDパイプラインの基本設計

**実行例**:
```
=== テストスイートと継続的インテグレーション ===

テストスイート構成:
@Suite
@SelectClasses({
    CalculatorTest.class,
    StringUtilsTest.class,
    BankAccountTest.class,
    UserServiceTest.class
})
class AllUnitTests { }

@Suite  
@SelectPackages("com.example.integration")
class IntegrationTestSuite { }

@Suite
@SelectClasspathResource("performance-tests")
class PerformanceTestSuite { }

テストカテゴリ分類:

単体テスト (Unit Tests):
@Tag("unit")
class CalculatorTest {
    @Test
    @Tag("fast")
    void testBasicOperations() { ... }
    
    @Test  
    @Tag("edge-case")
    void testBoundaryValues() { ... }
}

統合テスト (Integration Tests):
@Tag("integration")
class DatabaseIntegrationTest {
    @Test
    @Tag("database")
    void testUserRepository() { ... }
    
    @Test
    @Tag("api")
    void testRestApiEndpoint() { ... }
}

パフォーマンステスト:
@Tag("performance")
class PerformanceTest {
    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void testLargeDataProcessing() {
        // 大量データ処理のテスト
        List<Integer> data = generateLargeDataSet(100000);
        long startTime = System.currentTimeMillis();
        
        List<Integer> result = dataProcessor.process(data);
        
        long endTime = System.currentTimeMillis();
        assertTrue((endTime - startTime) < 2000, 
            "処理時間が2秒を超えました");
        assertEquals(100000, result.size());
    }
    
    @RepeatedTest(10)
    @Tag("stress")
    void testConcurrentAccess() {
        // 並行アクセステスト
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<String>> futures = new ArrayList<>();
        
        for (int i = 0; i < 100; i++) {
            futures.add(executor.submit(() -> 
                userService.processRequest("concurrent-test")));
        }
        
        // 全ての処理が完了することを確認
        futures.forEach(future -> {
            assertDoesNotThrow(() -> future.get(5, TimeUnit.SECONDS));
        });
    }
}

Maven pom.xml設定:
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.6.1</version>
            <configuration>
                <groups>unit,integration</groups>
                <excludedGroups>performance</excludedGroups>
                <parallel>methods</parallel>
                <threadCount>4</threadCount>
            </configuration>
        </plugin>
        
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.16</version>
            <executions>
                <execution>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>

CI/CDパイプライン (.github/workflows/ci.yml):
name: CI Pipeline

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
    - uses: actions/checkout@v7
    
    - name: Set up JDK 25
      uses: actions/setup-java@v6
      with:
        java-version: '25'
        distribution: 'temurin'
    
    - name: Cache Maven dependencies
      uses: actions/cache@v3
      with:
        path: ~/.m2
        key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
    
    - name: Run unit tests
      run: mvn test -Dgroups="unit"
    
    - name: Run integration tests  
      run: mvn test -Dgroups="integration"
    
    - name: Generate test report
      run: mvn jacoco:report
    
    - name: Upload coverage to Codecov
      uses: codecov/codecov-action@v3

テスト実行結果:

Phase 1: Unit Tests
===================
[INFO] Running com.example.CalculatorTest
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.example.StringUtilsTest  
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.example.BankAccountTest
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0

Unit Tests Summary:
総テスト数: 47
成功: 47
失敗: 0
スキップ: 0
実行時間: 1.23秒

Phase 2: Integration Tests  
=========================
[INFO] Running com.example.DatabaseIntegrationTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.example.ApiIntegrationTest
[INFO] Tests run: 8, Failures: 0, Errors: 0, Skipped: 0

Integration Tests Summary:
総テスト数: 13
成功: 13  
失敗: 0
スキップ: 0
実行時間: 8.45秒

Phase 3: Performance Tests
=========================
[INFO] Running com.example.PerformanceTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0

Performance Metrics:
大量データ処理: 1.45秒 (閾値: 2秒) ○
並行アクセス: 100リクエスト/3.2秒 ○
メモリ使用量: 245MB (上限: 512MB) ○

テストカバレッジレポート:
======================
全体カバレッジ: 89.2%

パッケージ別詳細:
com.example.model: 95.5% (21/22 lines)
com.example.service: 87.3% (127/145 lines)  
com.example.repository: 92.1% (58/63 lines)
com.example.util: 100% (34/34 lines)

未カバー行:
- ErrorHandler.java:45 (例外処理)
- DataValidator.java:78-82 (エッジケース)

品質ゲート:
○ 単体テスト成功率: 100% (100%以上必要)
○ 統合テスト成功率: 100% (100%以上必要)  
○ コードカバレッジ: 89.2% (80%以上必要)
○ パフォーマンス: 全て基準内
× 技術的負債: 2件 (0件必要)

CI/CD統計:
ビルド時間: 4分23秒
テスト実行時間: 2分15秒
デプロイ時間: 1分30秒
総実行時間: 8分8秒

改善提案:
1. 並列テスト実行でテスト時間短縮
2. テストデータのキャッシュ活用
3. 軽量なDockerイメージ使用
4. 差分テスト実行の導入

自動化レポート生成:
- HTML形式レポート: target/site/jacoco/index.html
- XML形式レポート: target/site/jacoco/jacoco.xml
- トレンド分析: 過去30日のカバレッジ推移
- 品質メトリクス: 循環的複雑度、重複コード率
```

**評価ポイント**:
- 包括的なテストスイートの構築
- CI/CDパイプラインの理解
- テスト自動化の実装

## ヒント

### 課題1のヒント
- @DisplayName でテスト名を日本語化
- @ParameterizedTest で複数パターンを効率的にテスト
- assertAll() で複数のアサーションをまとめて実行

### 課題2のヒント
- 小さなテストから始めて段階的に機能を追加
- リファクタリング後は全テストを実行して回帰を防ぐ
- テストコード自体も可読性を重視

### 課題3のヒント
- @Mock でモック作成、@InjectMocks で依存注入
- verify() でメソッド呼び出しを検証
- ArgumentCaptor で引数の内容を詳細に検証

### 課題4のヒント
- @Tag でテストを分類し、maven-surefire-plugin で実行制御
- TestContainers で統合テスト用のインフラ環境
- JaCoCo でコードカバレッジ測定

## 単体テストのポイント

1. **テストピラミッド**: 単体テスト > 統合テスト > E2Eテスト
2. **FIRST原則**: Fast, Independent, Repeatable, Self-validating, Timely
3. **AAAパターン**: Arrange(準備) - Act(実行) - Assert(検証)
4. **境界値テスト**: 正常値、境界値、異常値の網羅
5. **モック活用**: 依存関係の分離とテスト高速化
6. **継続的改善**: カバレッジとメトリクスによる品質向上

## ○ 完了チェックリスト

- [ ] 課題1: JUnit5を使った基本的な単体テストが作成できている
- [ ] 課題2: TDDサイクルを実践してクラスを開発できている
- [ ] 課題3: モックを使って依存関係のあるクラスをテストできている
- [ ] 課題4: テストスイートとCI/CDパイプラインが構築できている
- [ ] テストカバレッジを理解して品質向上に活用できている
- [ ] 効果的なテスト戦略を立てることができている

**次のステップ**: 基本課題が完了したら、advanced/の発展課題でより高度なテスト技法に挑戦しましょう！