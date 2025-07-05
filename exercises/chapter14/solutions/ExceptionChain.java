import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * 例外チェーンによる根本原因追跡クラス
 * 複雑な例外の連鎖を管理し、根本原因を追跡する
 */
public class ExceptionChain {
    
    private static final Logger logger = Logger.getLogger(ExceptionChain.class.getName());
    
    /**
     * ビジネスロジック例外
     */
    public static class BusinessLogicException extends Exception {
        private final String businessOperation;
        private final String errorCode;
        
        public BusinessLogicException(String businessOperation, String errorCode, String message) {
            super(message);
            this.businessOperation = businessOperation;
            this.errorCode = errorCode;
        }
        
        public BusinessLogicException(String businessOperation, String errorCode, String message, Throwable cause) {
            super(message, cause);
            this.businessOperation = businessOperation;
            this.errorCode = errorCode;
        }
        
        public String getBusinessOperation() {
            return businessOperation;
        }
        
        public String getErrorCode() {
            return errorCode;
        }
    }
    
    /**
     * データアクセス例外
     */
    public static class DataAccessException extends Exception {
        private final String dataSource;
        private final String operation;
        
        public DataAccessException(String dataSource, String operation, String message) {
            super(message);
            this.dataSource = dataSource;
            this.operation = operation;
        }
        
        public DataAccessException(String dataSource, String operation, String message, Throwable cause) {
            super(message, cause);
            this.dataSource = dataSource;
            this.operation = operation;
        }
        
        public String getDataSource() {
            return dataSource;
        }
        
        public String getOperation() {
            return operation;
        }
    }
    
    /**
     * 設定例外
     */
    public static class ConfigurationException extends Exception {
        private final String configKey;
        private final String configValue;
        
        public ConfigurationException(String configKey, String configValue, String message) {
            super(message);
            this.configKey = configKey;
            this.configValue = configValue;
        }
        
        public ConfigurationException(String configKey, String configValue, String message, Throwable cause) {
            super(message, cause);
            this.configKey = configKey;
            this.configValue = configValue;
        }
        
        public String getConfigKey() {
            return configKey;
        }
        
        public String getConfigValue() {
            return configValue;
        }
    }
    
    /**
     * 低レベルなデータベース操作（例外を発生させる）
     */
    private static void performDatabaseOperation() throws SQLException {
        throw new SQLException("データベース接続に失敗しました", "08001");
    }
    
    /**
     * 設定ファイルの読み込み（例外を発生させる）
     */
    private static void loadConfiguration() throws IOException {
        throw new IOException("設定ファイルが見つかりません: config.properties");
    }
    
    /**
     * ネットワーク通信（例外を発生させる）
     */
    private static void performNetworkCall() throws IOException {
        throw new IOException("ネットワーク接続がタイムアウトしました", 
            new java.net.ConnectException("Connection refused"));
    }
    
    /**
     * データアクセス層での例外処理
     */
    public static void dataAccessLayer() throws DataAccessException {
        try {
            performDatabaseOperation();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "データベース操作でエラーが発生", e);
            throw new DataAccessException("primary_database", "SELECT", 
                "ユーザーデータの取得に失敗しました", e);
        }
    }
    
    /**
     * 設定管理層での例外処理
     */
    public static void configurationLayer() throws ConfigurationException {
        try {
            loadConfiguration();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "設定ファイル読み込みでエラーが発生", e);
            throw new ConfigurationException("database.url", null, 
                "データベース設定の読み込みに失敗しました", e);
        }
    }
    
    /**
     * ビジネスロジック層での例外処理
     */
    public static void businessLogicLayer() throws BusinessLogicException {
        try {
            dataAccessLayer();
        } catch (DataAccessException e) {
            logger.log(Level.SEVERE, "データアクセス層でエラーが発生", e);
            throw new BusinessLogicException("user_registration", "BL001", 
                "ユーザー登録処理に失敗しました", e);
        }
    }
    
    /**
     * 複雑な例外チェーンを生成
     */
    public static void complexExceptionChain() throws BusinessLogicException {
        try {
            // 設定エラーから始まる
            configurationLayer();
        } catch (ConfigurationException configEx) {
            try {
                // データアクセスエラーも発生
                dataAccessLayer();
            } catch (DataAccessException dataEx) {
                // 両方の例外を原因として持つビジネス例外
                BusinessLogicException businessEx = new BusinessLogicException(
                    "system_initialization", "BL002", 
                    "システム初期化に失敗しました", dataEx);
                businessEx.addSuppressed(configEx);
                throw businessEx;
            }
        }
    }
    
    /**
     * ネットワーク関連の例外チェーン
     */
    public static void networkExceptionChain() throws BusinessLogicException {
        try {
            performNetworkCall();
        } catch (IOException e) {
            DataAccessException dataEx = new DataAccessException("external_api", "GET", 
                "外部APIへのアクセスに失敗しました", e);
            throw new BusinessLogicException("external_data_sync", "BL003", 
                "外部データ同期に失敗しました", dataEx);
        }
    }
    
    /**
     * 例外チェーンの詳細情報を取得
     */
    public static String getExceptionChainDetails(Throwable exception) {
        StringBuilder details = new StringBuilder();
        
        details.append("=== 例外チェーン詳細 ===\n");
        
        Throwable current = exception;
        int level = 0;
        
        while (current != null) {
            String indent = "  ".repeat(level);
            details.append(indent).append("レベル ").append(level).append(": ")
                   .append(current.getClass().getSimpleName()).append("\n");
            details.append(indent).append("メッセージ: ").append(current.getMessage()).append("\n");
            
            // カスタム例外の追加情報
            if (current instanceof BusinessLogicException) {
                BusinessLogicException blEx = (BusinessLogicException) current;
                details.append(indent).append("ビジネス操作: ").append(blEx.getBusinessOperation()).append("\n");
                details.append(indent).append("エラーコード: ").append(blEx.getErrorCode()).append("\n");
            } else if (current instanceof DataAccessException) {
                DataAccessException daEx = (DataAccessException) current;
                details.append(indent).append("データソース: ").append(daEx.getDataSource()).append("\n");
                details.append(indent).append("操作: ").append(daEx.getOperation()).append("\n");
            } else if (current instanceof ConfigurationException) {
                ConfigurationException cfEx = (ConfigurationException) current;
                details.append(indent).append("設定キー: ").append(cfEx.getConfigKey()).append("\n");
                details.append(indent).append("設定値: ").append(cfEx.getConfigValue()).append("\n");
            }
            
            current = current.getCause();
            level++;
        }
        
        // 抑制された例外も表示
        Throwable[] suppressed = exception.getSuppressed();
        if (suppressed.length > 0) {
            details.append("\n=== 抑制された例外 ===\n");
            for (int i = 0; i < suppressed.length; i++) {
                details.append("抑制された例外 ").append(i + 1).append(": ")
                       .append(suppressed[i].getClass().getSimpleName()).append("\n");
                details.append("メッセージ: ").append(suppressed[i].getMessage()).append("\n");
            }
        }
        
        return details.toString();
    }
    
    /**
     * 特定の例外タイプを検索
     */
    public static <T extends Throwable> T findExceptionByType(Throwable exception, Class<T> exceptionType) {
        Throwable current = exception;
        
        while (current != null) {
            if (exceptionType.isInstance(current)) {
                return exceptionType.cast(current);
            }
            current = current.getCause();
        }
        
        return null;
    }
    
    /**
     * 根本原因を取得
     */
    public static Throwable getRootCause(Throwable exception) {
        Throwable current = exception;
        
        while (current.getCause() != null) {
            current = current.getCause();
        }
        
        return current;
    }
    
    /**
     * 例外チェーンの深さを取得
     */
    public static int getExceptionChainDepth(Throwable exception) {
        int depth = 0;
        Throwable current = exception;
        
        while (current != null) {
            depth++;
            current = current.getCause();
        }
        
        return depth;
    }
    
    /**
     * 特定のエラーコードを持つ例外を検索
     */
    public static BusinessLogicException findBusinessExceptionByCode(Throwable exception, String errorCode) {
        Throwable current = exception;
        
        while (current != null) {
            if (current instanceof BusinessLogicException) {
                BusinessLogicException blEx = (BusinessLogicException) current;
                if (errorCode.equals(blEx.getErrorCode())) {
                    return blEx;
                }
            }
            current = current.getCause();
        }
        
        return null;
    }
    
    /**
     * 例外の重要度を判定
     */
    public static int getExceptionSeverity(Throwable exception) {
        // 根本原因を基準に重要度を判定
        Throwable rootCause = getRootCause(exception);
        
        if (rootCause instanceof SQLException) {
            return 3; // 高
        } else if (rootCause instanceof IOException) {
            return 2; // 中
        } else if (rootCause instanceof IllegalArgumentException) {
            return 1; // 低
        } else {
            return 2; // デフォルト：中
        }
    }
    
    /**
     * 再試行可能な例外かどうかを判定
     */
    public static boolean isRetryableException(Throwable exception) {
        Throwable rootCause = getRootCause(exception);
        
        // ネットワーク関連の例外は再試行可能
        if (rootCause instanceof java.net.ConnectException ||
            rootCause instanceof java.net.SocketTimeoutException) {
            return true;
        }
        
        // データベース接続の例外は再試行可能
        if (rootCause instanceof SQLException) {
            SQLException sqlEx = (SQLException) rootCause;
            return "08001".equals(sqlEx.getSQLState()) || // 接続エラー
                   "08003".equals(sqlEx.getSQLState());   // 接続が存在しない
        }
        
        return false;
    }
}