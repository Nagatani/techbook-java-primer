import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.Map;

/**
 * Constantsクラスのテストクラス
 * 
 * 【テストの観点】
 * 1. 定数の値が正しく設定されているか
 * 2. 不変性が保持されているか
 * 3. インスタンス化が防がれているか
 * 4. 不変コレクションが正しく動作するか
 * 5. ユーティリティメソッドが正しく動作するか
 */
public class ConstantsTest {
    
    @Test
    @DisplayName("基本定数の値テスト")
    void testBasicConstants() {
        // 数値定数
        assertEquals(3, Constants.MAX_RETRY_COUNT);
        assertEquals(3.14159, Constants.PI_APPROXIMATION, 0.00001);
        assertEquals(5000L, Constants.TIMEOUT_MILLISECONDS);
        
        // 文字列定数
        assertEquals("UTF-8", Constants.DEFAULT_ENCODING);
        assertEquals("Java Primer Exercise", Constants.APPLICATION_NAME);
        assertEquals("1.0.0", Constants.VERSION);
        
        // 真偽値定数
        assertFalse(Constants.DEBUG_MODE);
        assertTrue(Constants.ENABLE_LOGGING);
    }
    
    @Test
    @DisplayName("オブジェクト型定数のテスト")
    void testObjectConstants() {
        // BigDecimal定数
        assertEquals(new BigDecimal("0.10"), Constants.TAX_RATE);
        assertEquals(new BigDecimal("0.05"), Constants.DISCOUNT_RATE);
        
        // Duration定数
        assertEquals(Duration.ofMinutes(30), Constants.SESSION_TIMEOUT);
        assertEquals(Duration.ofHours(1), Constants.CACHE_EXPIRY);
    }
    
    @Test
    @DisplayName("不変コレクション定数のテスト")
    void testImmutableCollections() {
        // List定数
        List<String> languages = Constants.SUPPORTED_LANGUAGES;
        assertEquals(4, languages.size());
        assertTrue(languages.contains("Java"));
        assertTrue(languages.contains("Python"));
        
        // Set定数
        Set<String> extensions = Constants.VALID_FILE_EXTENSIONS;
        assertEquals(3, extensions.size());
        assertTrue(extensions.contains(".java"));
        assertTrue(extensions.contains(".class"));
        assertTrue(extensions.contains(".jar"));
        
        // Map定数
        Map<String, Integer> statusCodes = Constants.HTTP_STATUS_CODES;
        assertEquals(3, statusCodes.size());
        assertEquals(Integer.valueOf(200), statusCodes.get("OK"));
        assertEquals(Integer.valueOf(404), statusCodes.get("NOT_FOUND"));
        assertEquals(Integer.valueOf(500), statusCodes.get("INTERNAL_SERVER_ERROR"));
    }
    
    @Test
    @DisplayName("不変コレクションの変更不可テスト")
    void testImmutableCollectionsCannotBeModified() {
        // List の変更不可テスト
        List<String> languages = Constants.SUPPORTED_LANGUAGES;
        assertThrows(UnsupportedOperationException.class, () -> {
            languages.add("C#");
        });
        assertThrows(UnsupportedOperationException.class, () -> {
            languages.remove("Java");
        });
        
        // Set の変更不可テスト
        Set<String> extensions = Constants.VALID_FILE_EXTENSIONS;
        assertThrows(UnsupportedOperationException.class, () -> {
            extensions.add(".txt");
        });
        assertThrows(UnsupportedOperationException.class, () -> {
            extensions.remove(".java");
        });
        
        // Map の変更不可テスト
        Map<String, Integer> statusCodes = Constants.HTTP_STATUS_CODES;
        assertThrows(UnsupportedOperationException.class, () -> {
            statusCodes.put("BAD_REQUEST", 400);
        });
        assertThrows(UnsupportedOperationException.class, () -> {
            statusCodes.remove("OK");
        });
    }
    
    @Test
    @DisplayName("Constantsクラスのインスタンス化防止テスト")
    void testConstantsCannotBeInstantiated() {
        // リフレクションを使用してプライベートコンストラクタを取得
        assertThrows(Exception.class, () -> {
            var constructor = Constants.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });
    }
    
    @Test
    @DisplayName("Database定数のテスト")
    void testDatabaseConstants() {
        assertEquals("com.mysql.cj.jdbc.Driver", Constants.Database.DRIVER_CLASS);
        assertEquals("jdbc:mysql://localhost:3306/", Constants.Database.DEFAULT_URL);
        assertEquals(3306, Constants.Database.DEFAULT_PORT);
        assertEquals(20, Constants.Database.MAX_CONNECTIONS);
        assertEquals(Duration.ofSeconds(30), Constants.Database.CONNECTION_TIMEOUT);
    }
    
    @Test
    @DisplayName("Files定数のテスト")
    void testFilesConstants() {
        assertNotNull(Constants.Files.TEMP_DIR);
        assertNotNull(Constants.Files.LINE_SEPARATOR);
        assertEquals("UTF-8", Constants.Files.DEFAULT_CHARSET);
        assertEquals(8192, Constants.Files.BUFFER_SIZE);
        
        // ファイルサイズ定数
        assertEquals(1024L, Constants.Files.KB);
        assertEquals(1024L * 1024L, Constants.Files.MB);
        assertEquals(1024L * 1024L * 1024L, Constants.Files.GB);
    }
    
    @Test
    @DisplayName("UI定数のテスト")
    void testUIConstants() {
        assertEquals(800, Constants.UI.DEFAULT_WINDOW_WIDTH);
        assertEquals(600, Constants.UI.DEFAULT_WINDOW_HEIGHT);
        assertEquals("MS Gothic", Constants.UI.DEFAULT_FONT_NAME);
        assertEquals(12, Constants.UI.DEFAULT_FONT_SIZE);
        
        // カラー定数
        assertEquals(0x2196F3, Constants.UI.COLOR_PRIMARY);
        assertEquals(0xFFC107, Constants.UI.COLOR_SECONDARY);
        assertEquals(0xF44336, Constants.UI.COLOR_ERROR);
        assertEquals(0x4CAF50, Constants.UI.COLOR_SUCCESS);
    }
    
    @Test
    @DisplayName("Math定数のテスト")
    void testMathConstants() {
        assertEquals(2.71828182845904523536, Constants.Math.E, 0.00000000000001);
        assertEquals(1.6180339887498948482, Constants.Math.GOLDEN_RATIO, 0.00000000000001);
        assertEquals(1.41421356237309504880, Constants.Math.SQRT_2, 0.00000000000001);
        
        // 単位変換定数
        assertEquals(java.lang.Math.PI / 180.0, Constants.Math.DEGREES_TO_RADIANS, 0.00000001);
        assertEquals(180.0 / java.lang.Math.PI, Constants.Math.RADIANS_TO_DEGREES, 0.00000001);
    }
    
    @Test
    @DisplayName("Regex定数のテスト")
    void testRegexConstants() {
        assertNotNull(Constants.Regex.EMAIL_PATTERN);
        assertNotNull(Constants.Regex.PHONE_NUMBER_PATTERN);
        assertNotNull(Constants.Regex.POSTAL_CODE_PATTERN);
        assertNotNull(Constants.Regex.PASSWORD_PATTERN);
        
        // 実際の正規表現パターンのテスト
        assertTrue("test@example.com".matches(Constants.Regex.EMAIL_PATTERN));
        assertTrue("03-1234-5678".matches(Constants.Regex.PHONE_NUMBER_PATTERN));
        assertTrue("123-4567".matches(Constants.Regex.POSTAL_CODE_PATTERN));
        assertTrue("Password123".matches(Constants.Regex.PASSWORD_PATTERN));
        
        // 不正なパターンのテスト
        assertFalse("invalid-email".matches(Constants.Regex.EMAIL_PATTERN));
        assertFalse("123-45-6789".matches(Constants.Regex.PHONE_NUMBER_PATTERN));
        assertFalse("1234567".matches(Constants.Regex.POSTAL_CODE_PATTERN));
        assertFalse("weak".matches(Constants.Regex.PASSWORD_PATTERN));
    }
    
    @Test
    @DisplayName("Config定数のテスト")
    void testConfigConstants() {
        assertEquals("application.properties", Constants.Config.APP_CONFIG_FILE);
        assertEquals("logback.xml", Constants.Config.LOG_CONFIG_FILE);
        
        assertEquals("server.port", Constants.Config.KEY_SERVER_PORT);
        assertEquals("database.url", Constants.Config.KEY_DB_URL);
        assertEquals("logging.level", Constants.Config.KEY_LOG_LEVEL);
        
        assertEquals("8080", Constants.Config.DEFAULT_SERVER_PORT);
        assertEquals("INFO", Constants.Config.DEFAULT_LOG_LEVEL);
    }
    
    @Test
    @DisplayName("Priority定数のテスト")
    void testPriorityConstants() {
        assertEquals(1, Constants.Priority.LOW);
        assertEquals(2, Constants.Priority.NORMAL);
        assertEquals(3, Constants.Priority.HIGH);
        assertEquals(4, Constants.Priority.CRITICAL);
        
        // 優先度名の取得テスト
        assertEquals("Low", Constants.Priority.getName(Constants.Priority.LOW));
        assertEquals("Normal", Constants.Priority.getName(Constants.Priority.NORMAL));
        assertEquals("High", Constants.Priority.getName(Constants.Priority.HIGH));
        assertEquals("Critical", Constants.Priority.getName(Constants.Priority.CRITICAL));
        assertEquals("Unknown", Constants.Priority.getName(999));
        
        // 有効性チェックのテスト
        assertTrue(Constants.Priority.isValid(Constants.Priority.LOW));
        assertTrue(Constants.Priority.isValid(Constants.Priority.NORMAL));
        assertTrue(Constants.Priority.isValid(Constants.Priority.HIGH));
        assertTrue(Constants.Priority.isValid(Constants.Priority.CRITICAL));
        assertFalse(Constants.Priority.isValid(0));
        assertFalse(Constants.Priority.isValid(5));
    }
    
    @Test
    @DisplayName("ApiEndpoints定数のテスト")
    void testApiEndpointsConstants() {
        assertEquals("https://api.example.com/v1/users", Constants.ApiEndpoints.USERS);
        assertEquals("https://api.example.com/v1/products", Constants.ApiEndpoints.PRODUCTS);
        assertEquals("https://api.example.com/v1/orders", Constants.ApiEndpoints.ORDERS);
        
        // 動的エンドポイント生成のテスト
        assertEquals("https://api.example.com/v1/users/123", 
                    Constants.ApiEndpoints.getUserById(123));
        assertEquals("https://api.example.com/v1/products/456", 
                    Constants.ApiEndpoints.getProductById(456));
    }
    
    @Test
    @DisplayName("内部定数クラスのインスタンス化防止テスト")
    void testNestedConstantClassesCannotBeInstantiated() {
        // 各内部定数クラスがインスタンス化できないことを確認
        assertThrows(Exception.class, () -> {
            var constructor = Constants.Database.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });
        
        assertThrows(Exception.class, () -> {
            var constructor = Constants.Files.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });
        
        assertThrows(Exception.class, () -> {
            var constructor = Constants.UI.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });
    }
    
    @Test
    @DisplayName("定数の一意性テスト")
    void testConstantUniqueness() {
        // 同じ定数が複数回参照されても同じインスタンスであることを確認
        assertSame(Constants.SUPPORTED_LANGUAGES, Constants.SUPPORTED_LANGUAGES);
        assertSame(Constants.VALID_FILE_EXTENSIONS, Constants.VALID_FILE_EXTENSIONS);
        assertSame(Constants.HTTP_STATUS_CODES, Constants.HTTP_STATUS_CODES);
        
        // BigDecimal定数の一意性
        assertSame(Constants.TAX_RATE, Constants.TAX_RATE);
        assertSame(Constants.DISCOUNT_RATE, Constants.DISCOUNT_RATE);
    }
    
    @Test
    @DisplayName("定数を使用した計算のテスト")
    void testCalculationsWithConstants() {
        // 税率を使用した計算
        BigDecimal price = new BigDecimal("1000");
        BigDecimal tax = price.multiply(Constants.TAX_RATE);
        assertEquals(new BigDecimal("100.00"), tax);
        
        // 割引率を使用した計算
        BigDecimal discount = price.multiply(Constants.DISCOUNT_RATE);
        assertEquals(new BigDecimal("50.00"), discount);
        
        // ファイルサイズ計算
        long sizeInBytes = 5 * Constants.Files.MB;
        assertEquals(5 * 1024 * 1024, sizeInBytes);
    }
    
    @Test
    @DisplayName("demonstrateUsageメソッドのテスト")
    void testDemonstrateUsage() {
        // 例外が発生しないことを確認
        assertDoesNotThrow(() -> Constants.demonstrateUsage());
    }
}