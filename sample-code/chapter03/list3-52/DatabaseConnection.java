/**
 * リスト3-52
 * DatabaseConnectionクラス
 * 
 * 元ファイル: chapter03-oop-basics.md (2305行目)
 */

public class DatabaseConnection {
    private static DatabaseConnection instance;

    private DatabaseConnection() {
        // プライベートコンストラクタ
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
}