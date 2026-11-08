import java.util.Properties;

// データベース接続インターフェイス
interface DatabaseConnection {
    void connect();
    void executeQuery(String sql);
    void close();
}

// 具体的な実装
class MySQLConnection implements DatabaseConnection {
    private String connectionString;
    
    public MySQLConnection(String host, int port, String database) {
        this.connectionString = String.format("mysql://%s:%d/%s", host, port, database);
    }
    
    @Override
    public void connect() {
        System.out.println("MySQLに接続: " + connectionString);
    }
    
    @Override
    public void executeQuery(String sql) {
        System.out.println("MySQLでクエリ実行: " + sql);
    }
    
    @Override
    public void close() {
        System.out.println("MySQL接続を閉じる");
    }
}

class PostgreSQLConnection implements DatabaseConnection {
    private String connectionString;
    
    public PostgreSQLConnection(String host, int port, String database) {
        this.connectionString = String.format("postgresql://%s:%d/%s", host, port, database);
    }
    
    @Override
    public void connect() {
        System.out.println("PostgreSQLに接続: " + connectionString);
    }
    
    @Override
    public void executeQuery(String sql) {
        System.out.println("PostgreSQLでクエリ実行: " + sql);
    }
    
    @Override
    public void close() {
        System.out.println("PostgreSQL接続を閉じる");
    }
}

// データベース接続ファクトリー
public class DatabaseConnectionFactory {
    public static DatabaseConnection createConnection(String type, String host, int port, String database) {
        switch (type.toUpperCase()) {
            case "MYSQL":
                return new MySQLConnection(host, port, database);
            case "POSTGRESQL":
                return new PostgreSQLConnection(host, port, database);
            default:
                throw new IllegalArgumentException("サポートされていないデータベース: " + type);
        }
    }
    
    // 設定ファイルから接続を作成
    public static DatabaseConnection createConnectionFromConfig(Properties config) {
        String type = config.getProperty("db.type");
        String host = config.getProperty("db.host", "localhost");
        int port = Integer.parseInt(config.getProperty("db.port", "3306"));
        String database = config.getProperty("db.database");
        
        return createConnection(type, host, port, database);
    }
}
