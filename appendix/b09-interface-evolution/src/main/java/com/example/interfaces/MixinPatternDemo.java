package com.example.interfaces;

import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mixinパターンの実装例
 * 複数の機能を組み合わせて柔軟な設計を実現
 */
public class MixinPatternDemo {
    
    /**
     * タイムスタンプ機能のMixin
     */
    interface Timestamped {
        long getTimestamp();
        
        default boolean isExpired(long expirationTime) {
            return System.currentTimeMillis() - getTimestamp() > expirationTime;
        }
        
        default String getTimeString() {
            return "Created at: " + new java.util.Date(getTimestamp());
        }
    }
    
    /**
     * 識別可能オブジェクトのMixin
     */
    interface Identifiable {
        String getId();
        
        default String getFullIdentifier() {
            return getClass().getSimpleName() + "#" + getId();
        }
        
        default boolean isSameAs(Identifiable other) {
            return this.getId().equals(other.getId());
        }
    }
    
    /**
     * バージョン管理のMixin
     */
    interface Versioned {
        int getVersion();
        
        default boolean isNewerThan(Versioned other) {
            return this.getVersion() > other.getVersion();
        }
        
        default boolean isOlderThan(Versioned other) {
            return this.getVersion() < other.getVersion();
        }
        
        default String getVersionString() {
            return "v" + getVersion();
        }
    }
    
    /**
     * 印刷可能オブジェクトのMixin
     */
    interface Printable {
        // toString()は既存のメソッドを活用
        
        default void print() {
            IO.println(toString());
        }
        
        default void printToFile(String filename) {
            try (PrintWriter writer = new PrintWriter(filename)) {
                writer.println(toString());
                IO.println("Printed to file: " + filename);
            } catch (Exception e) {
                System.err.println("Failed to print to file: " + e.getMessage());
            }
        }
        
        default void printWithPrefix(String prefix) {
            IO.println(prefix + ": " + toString());
        }
    }
    
    /**
     * キャッシュ機能のMixin
     */
    interface Cacheable {
        // キャッシュストレージ（実装クラスがフィールドとして持ち、返す）
        // インターフェイスはフィールド（状態）を持てないので、抽象メソッドにする。
        // defaultメソッドで毎回新しいマップを返すと、入れた値がすぐに失われてしまう
        Map<String, Object> getCache();
        
        default void putCache(String key, Object value) {
            getCache().put(key, value);
        }
        
        @SuppressWarnings("unchecked")
        default <T> T getCache(String key, Class<T> type) {
            Object value = getCache().get(key);
            return type.isInstance(value) ? (T) value : null;
        }
        
        default void clearCache() {
            getCache().clear();
            IO.println("Cache cleared for " + getClass().getSimpleName());
        }
        
        default boolean hasCache(String key) {
            return getCache().containsKey(key);
        }
    }
    
    /**
     * 検証可能オブジェクトのMixin
     */
    interface Validatable {
        boolean validate();
        
        default boolean isValid() {
            return validate();
        }
        
        default void assertValid() {
            if (!validate()) {
                throw new IllegalStateException("Object is not valid: " + toString());
            }
        }
        
        default String getValidationStatus() {
            return isValid() ? "VALID" : "INVALID";
        }
    }
    
    /**
     * 複数のMixinを組み合わせた文書クラス
     */
    static class Document implements Timestamped, Identifiable, Versioned, Printable, Cacheable, Validatable {
        private final String id;
        private final long timestamp;
        private int version;
        private String content;
        private final Map<String, Object> cache = new ConcurrentHashMap<>();
        
        public Document(String id, String content) {
            this.id = id;
            this.content = content;
            this.timestamp = System.currentTimeMillis();
            this.version = 1;
        }
        
        // Timestamped implementation
        @Override
        public long getTimestamp() {
            return timestamp;
        }
        
        // Identifiable implementation
        @Override
        public String getId() {
            return id;
        }
        
        // Versioned implementation
        @Override
        public int getVersion() {
            return version;
        }
        
        // Cacheable implementation
        @Override
        public Map<String, Object> getCache() {
            return cache;
        }
        
        // Validatable implementation
        @Override
        public boolean validate() {
            return content != null && !content.trim().isEmpty() && id != null;
        }
        
        // Document specific methods
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
            this.version++;
            clearCache(); // 内容変更時にキャッシュをクリア
        }
        
        public void update() {
            version++;
            
            // Mixinの機能を活用
            if (isExpired(3600000)) { // 1時間
                IO.println("Document " + getFullIdentifier() + " has expired");
            }
            
            // キャッシュに統計情報を保存
            putCache("lastUpdate", System.currentTimeMillis());
            putCache("updateCount", getCache("updateCount", Integer.class) != null ? 
                    getCache("updateCount", Integer.class) + 1 : 1);
        }
        
        @Override
        public String toString() {
            return String.format("Document{id='%s', version=%d, content='%s', valid=%s}", 
                    id, version, content, getValidationStatus());
        }
    }
    
    /**
     * 複数のMixinを組み合わせたユーザークラス
     */
    static class User implements Identifiable, Timestamped, Validatable, Printable {
        private final String id;
        private final long timestamp;
        private String name;
        private String email;
        
        public User(String id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.timestamp = System.currentTimeMillis();
        }
        
        @Override
        public String getId() {
            return id;
        }
        
        @Override
        public long getTimestamp() {
            return timestamp;
        }
        
        @Override
        public boolean validate() {
            return name != null && !name.trim().isEmpty() && 
                   email != null && email.contains("@");
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        @Override
        public String toString() {
            return String.format("User{id='%s', name='%s', email='%s', valid=%s}", 
                    id, name, email, getValidationStatus());
        }
    }
    
    /**
     * Mixinパターンの活用例
     */
    public static void demonstrateMixinPattern() {
        IO.println("=== Mixin Pattern Demonstration ===");
        
        // 文書オブジェクトの作成と操作
        Document doc1 = new Document("doc001", "Important document content");
        Document doc2 = new Document("doc002", "Another document");
        
        IO.println("\n--- Document Operations ---");
        
        // 各Mixinの機能を使用
        IO.println("Document 1: " + doc1.getFullIdentifier());
        IO.println("Time: " + doc1.getTimeString());
        IO.println("Version: " + doc1.getVersionString());
        IO.println("Validation: " + doc1.getValidationStatus());
        
        // 印刷機能
        doc1.printWithPrefix("DOCUMENT");
        
        // 文書の更新
        doc1.update();
        IO.println("After update - Version: " + doc1.getVersionString());
        
        // バージョン比較
        doc2.update();
        doc2.update();
        IO.println("Doc2 is newer than Doc1: " + doc2.isNewerThan(doc1));
        
        // キャッシュ機能
        IO.println("\n--- Cache Operations ---");
        IO.println("Update count: " + doc1.getCache("updateCount", Integer.class));
        IO.println("Last update: " + doc1.getCache("lastUpdate", Long.class));
        
        // ユーザーオブジェクトの操作
        IO.println("\n--- User Operations ---");
        User user1 = new User("user001", "Alice", "alice@example.com");
        User user2 = new User("user002", "Bob", "invalid-email");
        
        user1.print();
        user2.print();
        
        // 検証
        IO.println("User1 validation: " + user1.getValidationStatus());
        IO.println("User2 validation: " + user2.getValidationStatus());
        
        try {
            user1.assertValid();
            IO.println("User1 is valid");
        } catch (IllegalStateException e) {
            IO.println("User1 validation failed: " + e.getMessage());
        }
        
        try {
            user2.assertValid();
            IO.println("User2 is valid");
        } catch (IllegalStateException e) {
            IO.println("User2 validation failed: " + e.getMessage());
        }
        
        // 識別機能
        IO.println("\n--- Identity Operations ---");
        IO.println("User1 full ID: " + user1.getFullIdentifier());
        IO.println("User2 full ID: " + user2.getFullIdentifier());
        IO.println("Are they the same? " + user1.isSameAs(user2));
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        demonstrateMixinPattern();
    }
}