package com.example.theory.patterns;

/**
 * Singletonパターンの理論的分析
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
            IO.println("DatabaseConnection initialized");
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
            IO.println("Executing query: " + sql);
        }
    }
    
    /**
     * Enum による実装（推奨）
     */
    public enum DatabaseConnectionEnum {
        INSTANCE;
        
        DatabaseConnectionEnum() {
            // 初期化処理
            IO.println("DatabaseConnectionEnum initialized");
        }
        
        public void executeQuery(String sql) {
            // クエリ実行
            IO.println("Executing query via enum: " + sql);
        }
    }
    
    /**
     * Bill Pugh Singleton（staticな入れ子クラスを使用。staticな入れ子クラスは内部クラスではない）
     */
    public static class BillPughSingleton {
        private BillPughSingleton() {
            IO.println("BillPughSingleton initialized");
        }
        
        private static class SingletonHelper {
            private static final BillPughSingleton INSTANCE = new BillPughSingleton();
        }
        
        public static BillPughSingleton getInstance() {
            return SingletonHelper.INSTANCE;
        }
        
        public void doSomething() {
            IO.println("Doing something in BillPughSingleton");
        }
    }
    
    /**
     * スレッドセーフでない実装（問題のある例）
     */
    public static class UnsafeSingleton {
        private static UnsafeSingleton instance;
        
        private UnsafeSingleton() {
            IO.println("UnsafeSingleton initialized");
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