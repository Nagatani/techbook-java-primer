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
            System.out.println(toString());
        }
        
        default void printToFile(String filename) {
            try (PrintWriter writer = new PrintWriter(filename)) {
                writer.println(toString());
                System.out.println("Printed to file: " + filename);
            } catch (Exception e) {
                System.err.println("Failed to print to file: " + e.getMessage());
            }
        }
        
        default void printWithPrefix(String prefix) {
            System.out.println(prefix + ": " + toString());
        }
    }
    
    /**
     * キャッシュ機能のMixin
     */
    interface Cacheable {
        // キャッシュストレージ（実装クラスで初期化）
        default Map<String, Object> getCache() {
            // 各実装クラスでオーバーライドすることを想定
            return new ConcurrentHashMap<>();
        }
        
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
            System.out.println("Cache cleared for " + getClass().getSimpleName());
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
                System.out.println("Document " + getFullIdentifier() + " has expired");
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
        System.out.println("=== Mixin Pattern Demonstration ===");
        
        // 文書オブジェクトの作成と操作
        Document doc1 = new Document("doc001", "Important document content");
        Document doc2 = new Document("doc002", "Another document");
        
        System.out.println("\n--- Document Operations ---");
        
        // 各Mixinの機能を使用
        System.out.println("Document 1: " + doc1.getFullIdentifier());
        System.out.println("Time: " + doc1.getTimeString());
        System.out.println("Version: " + doc1.getVersionString());
        System.out.println("Validation: " + doc1.getValidationStatus());
        
        // 印刷機能
        doc1.printWithPrefix("DOCUMENT");
        
        // 文書の更新
        doc1.update();
        System.out.println("After update - Version: " + doc1.getVersionString());
        
        // バージョン比較
        doc2.update();
        doc2.update();
        System.out.println("Doc2 is newer than Doc1: " + doc2.isNewerThan(doc1));
        
        // キャッシュ機能
        System.out.println("\n--- Cache Operations ---");
        System.out.println("Update count: " + doc1.getCache("updateCount", Integer.class));
        System.out.println("Last update: " + doc1.getCache("lastUpdate", Long.class));
        
        // ユーザーオブジェクトの操作
        System.out.println("\n--- User Operations ---");
        User user1 = new User("user001", "Alice", "alice@example.com");
        User user2 = new User("user002", "Bob", "invalid-email");
        
        user1.print();
        user2.print();
        
        // 検証
        System.out.println("User1 validation: " + user1.getValidationStatus());
        System.out.println("User2 validation: " + user2.getValidationStatus());
        
        try {
            user1.assertValid();
            System.out.println("User1 is valid");
        } catch (IllegalStateException e) {
            System.out.println("User1 validation failed: " + e.getMessage());
        }
        
        try {
            user2.assertValid();
            System.out.println("User2 is valid");
        } catch (IllegalStateException e) {
            System.out.println("User2 validation failed: " + e.getMessage());
        }
        
        // 識別機能
        System.out.println("\n--- Identity Operations ---");
        System.out.println("User1 full ID: " + user1.getFullIdentifier());
        System.out.println("User2 full ID: " + user2.getFullIdentifier());
        System.out.println("Are they the same? " + user1.isSameAs(user2));
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        demonstrateMixinPattern();
    }
}