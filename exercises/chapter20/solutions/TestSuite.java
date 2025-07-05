/**
 * 第20章 課題4: テストスイート - 解答例
 * 
 * JUnit 5を使用したテストスイートの構築と実行制御
 * 
 * 学習ポイント:
 * - @Suite アノテーションによるテストスイート構築
 * - テストクラスの組織化と分類
 * - 条件付きテスト実行
 * - テストライフサイクルの管理
 * - CI/CD環境での効率的なテスト実行
 */

import org.junit.platform.suite.api.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import java.time.Duration;
import java.util.logging.Logger;

/**
 * メインテストスイート
 * 
 * 全てのテストクラスを統合したマスタースイート
 */
@Suite
@SuiteDisplayName("Java学習プロジェクト - 統合テストスイート")
@SelectClasses({
    TestSuite.FastTestSuite.class,
    TestSuite.SlowTestSuite.class,
    TestSuite.IntegrationTestSuite.class
})
@IncludeClassNamePatterns(".*Test")
public class TestSuite {
    
    private static final Logger logger = Logger.getLogger(TestSuite.class.getName());
    
    @BeforeAll
    static void setUpSuite() {
        logger.info("=== 統合テストスイート開始 ===");
        System.setProperty("test.environment", "junit");
    }
    
    @AfterAll
    static void tearDownSuite() {
        logger.info("=== 統合テストスイート完了 ===");
        System.clearProperty("test.environment");
    }
    
    /**
     * 高速テストスイート
     * 
     * 単体テストと基本的な機能テストを含む
     */
    @Suite
    @SuiteDisplayName("高速テストスイート (< 1秒)")
    @SelectClasses({
        UnitTests.class,
        BasicFunctionTests.class
    })
    @IncludeTags("fast")
    public static class FastTestSuite {
        
        @BeforeAll
        static void setUp() {
            logger.info("高速テスト開始");
        }
        
        @AfterAll
        static void tearDown() {
            logger.info("高速テスト完了");
        }
    }
    
    /**
     * 低速テストスイート
     * 
     * パフォーマンステストや複雑な計算テストを含む
     */
    @Suite
    @SuiteDisplayName("低速テストスイート (> 1秒)")
    @SelectClasses({
        PerformanceTests.class,
        ComplexCalculationTests.class
    })
    @IncludeTags("slow")
    public static class SlowTestSuite {
        
        @BeforeAll
        static void setUp() {
            logger.info("低速テスト開始");
        }
        
        @AfterAll
        static void tearDown() {
            logger.info("低速テスト完了");
        }
    }
    
    /**
     * 統合テストスイート
     * 
     * システム間連携や外部依存を含むテスト
     */
    @Suite
    @SuiteDisplayName("統合テストスイート")
    @SelectClasses({
        DatabaseIntegrationTests.class,
        ExternalAPITests.class
    })
    @IncludeTags("integration")
    public static class IntegrationTestSuite {
        
        @BeforeAll
        static void setUp() {
            logger.info("統合テスト開始");
            // テスト用データベースの準備など
        }
        
        @AfterAll
        static void tearDown() {
            logger.info("統合テスト完了");
            // リソースクリーンアップ
        }
    }
    
    /**
     * 単体テストクラス
     */
    @DisplayName("単体テスト")
    @Tag("fast")
    @Tag("unit")
    static class UnitTests {
        
        @Test
        @DisplayName("文字列操作テスト")
        @Tag("string")
        void testStringOperations() {
            String input = "Hello World";
            String result = input.toLowerCase().replace(" ", "-");
            assertEquals("hello-world", result);
        }
        
        @Test
        @DisplayName("数値計算テスト")
        @Tag("math")
        void testMathOperations() {
            assertEquals(4, 2 + 2);
            assertEquals(6, 2 * 3);
            assertEquals(2.5, 5.0 / 2.0);
        }
        
        @Test
        @DisplayName("配列操作テスト")
        @Tag("array")
        void testArrayOperations() {
            int[] numbers = {1, 2, 3, 4, 5};
            assertEquals(5, numbers.length);
            assertEquals(1, numbers[0]);
            assertEquals(5, numbers[numbers.length - 1]);
        }
        
        @Test
        @DisplayName("例外処理テスト")
        @Tag("exception")
        void testExceptionHandling() {
            assertThrows(ArithmeticException.class, () -> {
                @SuppressWarnings("unused")
                int result = 10 / 0;
            });
        }
    }
    
    /**
     * 基本機能テストクラス
     */
    @DisplayName("基本機能テスト")
    @Tag("fast")
    @Tag("functional")
    static class BasicFunctionTests {
        
        @Test
        @DisplayName("リスト操作テスト")
        @Tag("collection")
        void testListOperations() {
            java.util.List<String> list = new java.util.ArrayList<>();
            list.add("apple");
            list.add("banana");
            list.add("cherry");
            
            assertEquals(3, list.size());
            assertTrue(list.contains("banana"));
            assertEquals("apple", list.get(0));
        }
        
        @Test
        @DisplayName("マップ操作テスト")
        @Tag("collection")
        void testMapOperations() {
            java.util.Map<String, Integer> map = new java.util.HashMap<>();
            map.put("one", 1);
            map.put("two", 2);
            map.put("three", 3);
            
            assertEquals(3, map.size());
            assertEquals(Integer.valueOf(2), map.get("two"));
            assertTrue(map.containsKey("one"));
        }
        
        @Test
        @DisplayName("ストリーム操作テスト")
        @Tag("stream")
        void testStreamOperations() {
            java.util.List<Integer> numbers = java.util.Arrays.asList(1, 2, 3, 4, 5);
            
            long evenCount = numbers.stream()
                    .filter(n -> n % 2 == 0)
                    .count();
            
            assertEquals(2, evenCount);
        }
    }
    
    /**
     * パフォーマンステストクラス
     */
    @DisplayName("パフォーマンステスト")
    @Tag("slow")
    @Tag("performance")
    static class PerformanceTests {
        
        @Test
        @DisplayName("大量データ処理テスト")
        @Timeout(value = 5, unit = java.util.concurrent.TimeUnit.SECONDS)
        void testLargeDataProcessing() {
            // 大量のデータを処理
            java.util.List<Integer> largeList = new java.util.ArrayList<>();
            for (int i = 0; i < 1_000_000; i++) {
                largeList.add(i);
            }
            
            long sum = largeList.stream()
                    .mapToLong(Integer::longValue)
                    .sum();
            
            assertEquals(499999500000L, sum);
        }
        
        @Test
        @DisplayName("ソートアルゴリズムテスト")
        @Timeout(value = 3, unit = java.util.concurrent.TimeUnit.SECONDS)
        void testSortingPerformance() {
            java.util.List<Integer> numbers = new java.util.ArrayList<>();
            java.util.Random random = new java.util.Random(42); // 固定シード
            
            // 10万個のランダムな数値を生成
            for (int i = 0; i < 100_000; i++) {
                numbers.add(random.nextInt(1000));
            }
            
            // ソート実行
            numbers.sort(Integer::compareTo);
            
            // ソート結果の検証
            for (int i = 1; i < numbers.size(); i++) {
                assertTrue(numbers.get(i - 1) <= numbers.get(i),
                        "ソート結果が正しくありません");
            }
        }
        
        @Test
        @DisplayName("メモリ使用量テスト")
        void testMemoryUsage() {
            Runtime runtime = Runtime.getRuntime();
            long initialMemory = runtime.totalMemory() - runtime.freeMemory();
            
            // メモリを消費する処理
            java.util.List<String> stringList = new java.util.ArrayList<>();
            for (int i = 0; i < 10_000; i++) {
                stringList.add("String number " + i);
            }
            
            long finalMemory = runtime.totalMemory() - runtime.freeMemory();
            long memoryUsed = finalMemory - initialMemory;
            
            assertTrue(memoryUsed > 0, "メモリが使用されているはずです");
            assertTrue(memoryUsed < 50_000_000, "メモリ使用量が多すぎます"); // 50MB未満
        }
    }
    
    /**
     * 複雑な計算テストクラス
     */
    @DisplayName("複雑な計算テスト")
    @Tag("slow")
    @Tag("computation")
    static class ComplexCalculationTests {
        
        @Test
        @DisplayName("円周率計算テスト")
        void testPiCalculation() {
            double pi = calculatePiUsingLeibniz(1_000_000);
            assertEquals(Math.PI, pi, 0.01, "円周率の計算精度が不足しています");
        }
        
        @Test
        @DisplayName("フィボナッチ数列テスト")
        void testFibonacciSequence() {
            assertEquals(832040, fibonacciIterative(30));
            assertEquals(1346269, fibonacciIterative(31));
        }
        
        @Test
        @DisplayName("素因数分解テスト")
        void testPrimeFactorization() {
            java.util.List<Integer> factors = primeFactorize(60);
            java.util.List<Integer> expected = java.util.Arrays.asList(2, 2, 3, 5);
            assertEquals(expected, factors);
        }
        
        // ヘルパーメソッド
        private double calculatePiUsingLeibniz(int iterations) {
            double pi = 0.0;
            for (int i = 0; i < iterations; i++) {
                pi += Math.pow(-1, i) / (2 * i + 1);
            }
            return pi * 4;
        }
        
        private long fibonacciIterative(int n) {
            if (n <= 1) return n;
            long a = 0, b = 1;
            for (int i = 2; i <= n; i++) {
                long temp = a + b;
                a = b;
                b = temp;
            }
            return b;
        }
        
        private java.util.List<Integer> primeFactorize(int n) {
            java.util.List<Integer> factors = new java.util.ArrayList<>();
            for (int i = 2; i * i <= n; i++) {
                while (n % i == 0) {
                    factors.add(i);
                    n /= i;
                }
            }
            if (n > 1) factors.add(n);
            return factors;
        }
    }
    
    /**
     * データベース統合テストクラス
     */
    @DisplayName("データベース統合テスト")
    @Tag("integration")
    @Tag("database")
    @EnabledIfSystemProperty(named = "test.database.enabled", matches = "true")
    static class DatabaseIntegrationTests {
        
        @BeforeAll
        static void setUpDatabase() {
            // テスト用データベースの初期化
            logger.info("テスト用データベース初期化");
        }
        
        @AfterAll
        static void tearDownDatabase() {
            // データベースクリーンアップ
            logger.info("データベースクリーンアップ");
        }
        
        @Test
        @DisplayName("データベース接続テスト")
        void testDatabaseConnection() {
            assumeTrue(isDatabaseAvailable(), "データベースが利用できません");
            
            // データベース接続のシミュレーション
            boolean connected = simulateDatabaseConnection();
            assertTrue(connected, "データベース接続に失敗しました");
        }
        
        @Test
        @DisplayName("CRUD操作テスト")
        void testCRUDOperations() {
            assumeTrue(isDatabaseAvailable(), "データベースが利用できません");
            
            // Create
            String userId = createUser("testuser", "test@example.com");
            assertNotNull(userId);
            
            // Read
            String[] userInfo = readUser(userId);
            assertEquals("testuser", userInfo[0]);
            assertEquals("test@example.com", userInfo[1]);
            
            // Update
            boolean updated = updateUser(userId, "newname", "new@example.com");
            assertTrue(updated);
            
            // Delete
            boolean deleted = deleteUser(userId);
            assertTrue(deleted);
        }
        
        @Test
        @DisplayName("トランザクションテスト")
        void testTransaction() {
            assumeTrue(isDatabaseAvailable(), "データベースが利用できません");
            
            // トランザクション処理のシミュレーション
            boolean transactionSuccessful = executeTransaction();
            assertTrue(transactionSuccessful, "トランザクションが失敗しました");
        }
        
        // データベース操作のシミュレーション
        private boolean isDatabaseAvailable() {
            return "true".equals(System.getProperty("test.database.enabled"));
        }
        
        private boolean simulateDatabaseConnection() {
            // 実際の実装では本物のデータベース接続を行う
            return true;
        }
        
        private String createUser(String name, String email) {
            return "user_" + System.currentTimeMillis();
        }
        
        private String[] readUser(String userId) {
            return new String[]{"testuser", "test@example.com"};
        }
        
        private boolean updateUser(String userId, String name, String email) {
            return true;
        }
        
        private boolean deleteUser(String userId) {
            return true;
        }
        
        private boolean executeTransaction() {
            return true;
        }
    }
    
    /**
     * 外部API統合テストクラス
     */
    @DisplayName("外部API統合テスト")
    @Tag("integration")
    @Tag("api")
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    @EnabledOnOs(OS.LINUX) // CI環境でのみ実行
    static class ExternalAPITests {
        
        @Test
        @DisplayName("REST APIテスト")
        @EnabledIfSystemProperty(named = "api.test.enabled", matches = "true")
        void testRESTAPI() {
            assumeTrue(isNetworkAvailable(), "ネットワークが利用できません");
            
            // REST API呼び出しのシミュレーション
            String response = callRESTAPI("https://api.example.com/users");
            assertNotNull(response);
            assertTrue(response.contains("users"), "期待されるレスポンスが返されませんでした");
        }
        
        @Test
        @DisplayName("API認証テスト")
        void testAPIAuthentication() {
            assumeTrue(isNetworkAvailable(), "ネットワークが利用できません");
            
            // API認証のシミュレーション
            String token = authenticateAPI("username", "password");
            assertNotNull(token);
            assertTrue(token.startsWith("token_"), "無効な認証トークンです");
        }
        
        @Test
        @DisplayName("APIタイムアウトテスト")
        @Timeout(value = 10, unit = java.util.concurrent.TimeUnit.SECONDS)
        void testAPITimeout() {
            assumeTrue(isNetworkAvailable(), "ネットワークが利用できません");
            
            assertTimeoutPreemptively(Duration.ofSeconds(5), () -> {
                // タイムアウトが発生する可能性のある処理
                Thread.sleep(1000); // 1秒待機
                return callRESTAPI("https://api.example.com/slow-endpoint");
            });
        }
        
        // API操作のシミュレーション
        private boolean isNetworkAvailable() {
            try {
                java.net.InetAddress.getByName("www.google.com").isReachable(3000);
                return true;
            } catch (java.io.IOException e) {
                return false;
            }
        }
        
        private String callRESTAPI(String url) {
            // 実際の実装では HTTP クライアントを使用
            return "{\"users\": []}";
        }
        
        private String authenticateAPI(String username, String password) {
            return "token_" + System.currentTimeMillis();
        }
    }
    
    /**
     * 環境別テストスイート
     */
    @Suite
    @SuiteDisplayName("開発環境テストスイート")
    @SelectClasses({UnitTests.class, BasicFunctionTests.class})
    @EnabledIfSystemProperty(named = "test.env", matches = "development")
    public static class DevelopmentTestSuite {
        // 開発環境専用のテスト設定
    }
    
    @Suite
    @SuiteDisplayName("本番環境テストスイート")
    @SelectClasses({UnitTests.class, PerformanceTests.class})
    @EnabledIfSystemProperty(named = "test.env", matches = "production")
    public static class ProductionTestSuite {
        // 本番環境専用のテスト設定
    }
    
    /**
     * 条件付きテストの例
     */
    @DisplayName("条件付きテスト")
    static class ConditionalTests {
        
        @Test
        @EnabledOnOs(OS.WINDOWS)
        @DisplayName("Windows専用テスト")
        void testOnWindows() {
            assertTrue(System.getProperty("os.name").toLowerCase().contains("windows"));
        }
        
        @Test
        @EnabledOnJre(JRE.JAVA_11)
        @DisplayName("Java 11専用テスト")
        void testOnJava11() {
            assertTrue(System.getProperty("java.version").startsWith("11"));
        }
        
        @Test
        @EnabledIfEnvironmentVariable(named = "USER", matches = "developer")
        @DisplayName("開発者専用テスト")
        void testForDeveloper() {
            assertEquals("developer", System.getenv("USER"));
        }
        
        @Test
        @DisabledOnOs(OS.MAC)
        @DisplayName("Mac以外で実行するテスト")
        void testNotOnMac() {
            assertFalse(System.getProperty("os.name").toLowerCase().contains("mac"));
        }
        
        @Test
        @DisabledIf("isSlowTestDisabled")
        @DisplayName("条件付きで無効化されるテスト")
        void testConditionallyDisabled() {
            // このテストは isSlowTestDisabled() が true を返す場合のみ実行される
            assertTrue(true);
        }
        
        static boolean isSlowTestDisabled() {
            return "true".equals(System.getProperty("skip.slow.tests"));
        }
    }
}

/*
 * テストスイートの実装ポイント:
 * 
 * 1. スイート構成の設計
 *    - @Suite による階層的なテスト構成
 *    - @SelectClasses による明示的なクラス選択
 *    - @IncludeTags/@ExcludeTags によるタグベースフィルタリング
 *    - @IncludeClassNamePatterns による名前ベース選択
 * 
 * 2. テストの分類と組織化
 *    - 実行速度による分類（fast/slow）
 *    - テストタイプによる分類（unit/integration）
 *    - 機能別の分類（string/math/collection）
 *    - 環境別の分類（development/production）
 * 
 * 3. 条件付きテスト実行
 *    - @EnabledOnOs/@DisabledOnOs（OS条件）
 *    - @EnabledOnJre/@DisabledOnJre（Java バージョン条件）
 *    - @EnabledIfSystemProperty（システムプロパティ条件）
 *    - @EnabledIfEnvironmentVariable（環境変数条件）
 *    - @EnabledIf/@DisabledIf（カスタム条件）
 * 
 * 4. ライフサイクル管理
 *    - @BeforeAll/@AfterAll によるスイートレベルの初期化
 *    - リソースの適切な管理
 *    - テスト環境のセットアップとクリーンアップ
 * 
 * 5. CI/CD での活用
 *    - 環境別テスト実行
 *    - パフォーマンステストの分離
 *    - 統合テストの条件付き実行
 *    - タイムアウト設定による実行時間制御
 * 
 * 実践的な活用方法:
 * - mvn test -Dtest.env=development（開発環境テスト）
 * - mvn test -Dskip.slow.tests=true（高速テストのみ）
 * - mvn test -Dtest.database.enabled=true（DB統合テスト）
 * - gradle test --tests "*FastTestSuite"（Gradle での実行）
 * 
 * 利点:
 * - テストの体系的な管理
 * - 実行時間の最適化
 * - 環境に応じた柔軟なテスト実行
 * - CI/CD パイプラインでの効率的な活用
 * 
 * 注意点:
 * - テストの依存関係を避ける
 * - 適切なタグ付けとネーミング
 * - 実行環境の前提条件を明確にする
 * - パフォーマンスとカバレッジのバランス
 */