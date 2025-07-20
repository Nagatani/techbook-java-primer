package com.example.theory.patterns;

/**
 * Singletonパターンの理論的分析
 * リストAC-19
 */
public class SingletonPattern {
    
    /**
     * スレッドセーフなSingleton実装（Double-checked locking）
     */
    public static class DatabaseConnection {
        private static volatile DatabaseConnection instance;
        private static final Object lock = new Object();
        
        private DatabaseConnection() {
            // 初期化処理
            System.out.println("DatabaseConnection initialized");
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
        
        public void executeQuery(String sql) {
            System.out.println("Executing query: " + sql);
        }
    }
    
    /**
     * Enum による実装（推奨）
     */
    public enum DatabaseConnectionEnum {
        INSTANCE;
        
        DatabaseConnectionEnum() {
            // 初期化処理
            System.out.println("DatabaseConnectionEnum initialized");
        }
        
        public void executeQuery(String sql) {
            // クエリ実行
            System.out.println("Executing query via enum: " + sql);
        }
    }
    
    /**
     * Bill Pugh Singleton（静的内部クラスを使用）
     */
    public static class BillPughSingleton {
        private BillPughSingleton() {
            System.out.println("BillPughSingleton initialized");
        }
        
        private static class SingletonHelper {
            private static final BillPughSingleton INSTANCE = new BillPughSingleton();
        }
        
        public static BillPughSingleton getInstance() {
            return SingletonHelper.INSTANCE;
        }
        
        public void doSomething() {
            System.out.println("Doing something in BillPughSingleton");
        }
    }
    
    /**
     * スレッドセーフでない実装（問題のある例）
     */
    public static class UnsafeSingleton {
        private static UnsafeSingleton instance;
        
        private UnsafeSingleton() {
            System.out.println("UnsafeSingleton initialized");
        }
        
        // 複数のスレッドが同時にアクセスした場合、複数のインスタンスが作成される可能性
        public static UnsafeSingleton getInstance() {
            if (instance == null) {
                instance = new UnsafeSingleton();
            }
            return instance;
        }
    }
}