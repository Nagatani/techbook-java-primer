/**
 * リストAC-19
 * DatabaseConnectionクラス
 * 
 * 元ファイル: appendix-c-theoretical-foundations.md (481行目)
 */

// スレッドセーフなSingleton実装
public class DatabaseConnection {
    private static volatile DatabaseConnection instance;
    private static final Object lock = new Object();
    
    private DatabaseConnection() {
        // 初期化処理
    }
    
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }
}

// Enum による実装（推奨）
public enum DatabaseConnection {
    INSTANCE;
    
    public void executeQuery(String sql) {
        // クエリ実行
    }
}