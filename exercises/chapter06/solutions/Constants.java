import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collections;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 定数クラスの実装例
 * 
 * 【学習ポイント】
 * 1. public static final での定数定義
 * 2. 定数の命名規則（UPPER_SNAKE_CASE）
 * 3. プリミティブ型とオブジェクト型の定数
 * 4. 不変コレクションの作成
 * 5. 定数クラスの設計パターン
 * 
 * 【よくある間違い】
 * - static final を忘れる
 * - ミュータブルなオブジェクトをそのまま定数にする
 * - 定数クラスでコンストラクタを提供してしまう
 */
public final class Constants {
    
    // プライベートコンストラクタでインスタンス化を防ぐ
    private Constants() {
        throw new AssertionError("Constants class should not be instantiated");
    }
    
    // === 基本的な定数 ===
    
    // 数値定数
    public static final int MAX_RETRY_COUNT = 3;
    public static final double PI_APPROXIMATION = 3.14159;
    public static final long TIMEOUT_MILLISECONDS = 5000L;
    
    // 文字列定数
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final String APPLICATION_NAME = "Java Primer Exercise";
    public static final String VERSION = "1.0.0";
    
    // 真偽値定数
    public static final boolean DEBUG_MODE = false;
    public static final boolean ENABLE_LOGGING = true;
    
    // === オブジェクト型の定数 ===
    
    // BigDecimal定数（金額計算など）
    public static final BigDecimal TAX_RATE = new BigDecimal("0.10");
    public static final BigDecimal DISCOUNT_RATE = new BigDecimal("0.05");
    
    // Duration定数（時間間隔）
    public static final Duration SESSION_TIMEOUT = Duration.ofMinutes(30);
    public static final Duration CACHE_EXPIRY = Duration.ofHours(1);
    
    // === 不変コレクション定数 ===
    
    // 不変List
    public static final List<String> SUPPORTED_LANGUAGES = 
        Collections.unmodifiableList(Arrays.asList("Java", "Python", "JavaScript", "C++"));
    
    // 不変Set
    public static final Set<String> VALID_FILE_EXTENSIONS = 
        Collections.unmodifiableSet(new HashSet<>(Arrays.asList(".java", ".class", ".jar")));
    
    // 不変Map
    public static final Map<String, Integer> HTTP_STATUS_CODES;
    static {
        Map<String, Integer> map = new HashMap<>();
        map.put("OK", 200);
        map.put("NOT_FOUND", 404);
        map.put("INTERNAL_SERVER_ERROR", 500);
        HTTP_STATUS_CODES = Collections.unmodifiableMap(map);
    }
    
    // === 列挙型的な定数グループ ===
    
    // データベース関連定数
    public static final class Database {
        private Database() {}
        
        public static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
        public static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/";
        public static final int DEFAULT_PORT = 3306;
        public static final int MAX_CONNECTIONS = 20;
        public static final Duration CONNECTION_TIMEOUT = Duration.ofSeconds(30);
    }
    
    // ファイル関連定数
    public static final class Files {
        private Files() {}
        
        public static final String TEMP_DIR = System.getProperty("java.io.tmpdir");
        public static final String LINE_SEPARATOR = System.lineSeparator();
        public static final String DEFAULT_CHARSET = "UTF-8";
        public static final int BUFFER_SIZE = 8192;
        
        // ファイルサイズ定数
        public static final long KB = 1024L;
        public static final long MB = KB * 1024L;
        public static final long GB = MB * 1024L;
    }
    
    // UI関連定数
    public static final class UI {
        private UI() {}
        
        public static final int DEFAULT_WINDOW_WIDTH = 800;
        public static final int DEFAULT_WINDOW_HEIGHT = 600;
        public static final String DEFAULT_FONT_NAME = "MS Gothic";
        public static final int DEFAULT_FONT_SIZE = 12;
        
        // カラー定数（RGB値）
        public static final int COLOR_PRIMARY = 0x2196F3;
        public static final int COLOR_SECONDARY = 0x FFC107;
        public static final int COLOR_ERROR = 0xF44336;
        public static final int COLOR_SUCCESS = 0x4CAF50;
    }
    
    // === 計算関連定数 ===
    
    // 数学定数
    public static final class Math {
        private Math() {}
        
        public static final double E = 2.71828182845904523536;
        public static final double GOLDEN_RATIO = 1.6180339887498948482;
        public static final double SQRT_2 = 1.41421356237309504880;
        
        // 単位変換定数
        public static final double DEGREES_TO_RADIANS = java.lang.Math.PI / 180.0;
        public static final double RADIANS_TO_DEGREES = 180.0 / java.lang.Math.PI;
    }
    
    // === 正規表現定数 ===
    
    public static final class Regex {
        private Regex() {}
        
        public static final String EMAIL_PATTERN = 
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        public static final String PHONE_NUMBER_PATTERN = 
            "^\\d{2,4}-\\d{2,4}-\\d{4}$";
        public static final String POSTAL_CODE_PATTERN = 
            "^\\d{3}-\\d{4}$";
        public static final String PASSWORD_PATTERN = 
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{8,}$";
    }
    
    // === 設定関連定数 ===
    
    public static final class Config {
        private Config() {}
        
        // 設定ファイル名
        public static final String APP_CONFIG_FILE = "application.properties";
        public static final String LOG_CONFIG_FILE = "logback.xml";
        
        // 設定キー名
        public static final String KEY_SERVER_PORT = "server.port";
        public static final String KEY_DB_URL = "database.url";
        public static final String KEY_LOG_LEVEL = "logging.level";
        
        // デフォルト値
        public static final String DEFAULT_SERVER_PORT = "8080";
        public static final String DEFAULT_LOG_LEVEL = "INFO";
    }
    
    // === 列挙型の代替（定数での実装） ===
    
    /**
     * 優先度レベル定数
     * 列挙型の代わりに定数で実装する場合の例
     */
    public static final class Priority {
        private Priority() {}
        
        public static final int LOW = 1;
        public static final int NORMAL = 2;
        public static final int HIGH = 3;
        public static final int CRITICAL = 4;
        
        // 優先度の文字列表現
        public static final Map<Integer, String> PRIORITY_NAMES;
        static {
            Map<Integer, String> map = new HashMap<>();
            map.put(LOW, "Low");
            map.put(NORMAL, "Normal");
            map.put(HIGH, "High");
            map.put(CRITICAL, "Critical");
            PRIORITY_NAMES = Collections.unmodifiableMap(map);
        }
        
        /**
         * 有効な優先度かチェック
         */
        public static boolean isValid(int priority) {
            return priority >= LOW && priority <= CRITICAL;
        }
        
        /**
         * 優先度の文字列表現を取得
         */
        public static String getName(int priority) {
            return PRIORITY_NAMES.getOrDefault(priority, "Unknown");
        }
    }
    
    // === 内部定数クラス（より複雑な定数グループ） ===
    
    /**
     * APIエンドポイント定数
     */
    public static final class ApiEndpoints {
        private ApiEndpoints() {}
        
        private static final String BASE_URL = "https://api.example.com";
        private static final String VERSION = "/v1";
        
        public static final String USERS = BASE_URL + VERSION + "/users";
        public static final String PRODUCTS = BASE_URL + VERSION + "/products";
        public static final String ORDERS = BASE_URL + VERSION + "/orders";
        
        // 動的エンドポイント生成メソッド
        public static String getUserById(long id) {
            return USERS + "/" + id;
        }
        
        public static String getProductById(long id) {
            return PRODUCTS + "/" + id;
        }
    }
    
    /**
     * 使用例を示すメソッド
     */
    public static void demonstrateUsage() {
        // 基本定数の使用
        System.out.println("アプリケーション名: " + APPLICATION_NAME);
        System.out.println("最大リトライ回数: " + MAX_RETRY_COUNT);
        
        // コレクション定数の使用
        System.out.println("サポート言語: " + SUPPORTED_LANGUAGES);
        System.out.println("有効なファイル拡張子: " + VALID_FILE_EXTENSIONS);
        
        // 入れ子定数の使用
        System.out.println("データベースポート: " + Database.DEFAULT_PORT);
        System.out.println("バッファサイズ: " + Files.BUFFER_SIZE);
        
        // 優先度定数の使用
        int priority = Priority.HIGH;
        System.out.println("優先度: " + Priority.getName(priority));
        System.out.println("有効な優先度: " + Priority.isValid(priority));
    }
}