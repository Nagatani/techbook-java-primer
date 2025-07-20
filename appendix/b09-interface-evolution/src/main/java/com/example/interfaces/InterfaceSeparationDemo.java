package com.example.interfaces;

import java.time.Duration;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

/**
 * インターフェイス分離原則（ISP）の実践例
 * 責務ごとに分離されたインターフェイスとdefaultメソッドの活用
 */
public class InterfaceSeparationDemo {
    
    /**
     * ユーザー情報を表現するエンティティ
     */
    static class User {
        private final String id;
        private String name;
        private String email;
        private boolean active;
        
        public User(String id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.active = true;
        }
        
        // Getters and setters
        public String getId() { return id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        
        @Override
        public String toString() {
            return "User{id='" + id + "', name='" + name + "', email='" + email + "', active=" + active + '}';
        }
    }
    
    /**
     * ユーザーデータの永続化機能
     */
    interface UserRepository {
        void save(User user);
        void delete(String id);
        User findById(String id);
        List<User> findAll();
        
        // 便利なdefaultメソッド
        default boolean exists(String id) {
            return findById(id) != null;
        }
        
        default List<User> findActiveUsers() {
            return findAll().stream()
                .filter(User::isActive)
                .collect(java.util.stream.Collectors.toList());
        }
        
        default long count() {
            return findAll().size();
        }
    }
    
    /**
     * ユーザー通知機能
     */
    interface UserNotificationService {
        void sendEmail(String userId, String subject, String message);
        
        // 一般的な通知のdefaultメソッド
        default void sendWelcomeEmail(String userId) {
            sendEmail(userId, "Welcome!", "Welcome to our service!");
        }
        
        default void sendPasswordResetEmail(String userId, String resetToken) {
            sendEmail(userId, "Password Reset", 
                     "Click here to reset your password: /reset?token=" + resetToken);
        }
        
        default void sendAccountDeactivationEmail(String userId) {
            sendEmail(userId, "Account Deactivated", 
                     "Your account has been deactivated. Contact support if this was in error.");
        }
    }
    
    /**
     * ユーザーレポート生成機能
     */
    interface UserReportService {
        void generateReport(String userId);
        
        // レポート生成のdefaultメソッド
        default void generateMonthlyReport(String userId) {
            System.out.println("Generating monthly report for user: " + userId);
            generateReport(userId);
        }
        
        default void generateAnnualReport(String userId) {
            System.out.println("Generating annual report for user: " + userId);
            generateReport(userId);
        }
        
        default CompletableFuture<Void> generateReportAsync(String userId) {
            return CompletableFuture.runAsync(() -> generateReport(userId));
        }
    }
    
    /**
     * ユーザーデータバックアップ機能
     */
    interface UserBackupService {
        void backupUserData(String userId);
        
        // バックアップ関連のdefaultメソッド
        default void scheduleBackup(String userId, Duration interval) {
            System.out.println("Scheduling backup for user " + userId + " every " + interval);
            // 実装はスケジューラーに依存
        }
        
        default void backupAllActiveUsers() {
            System.out.println("Starting backup for all active users");
            // UserRepositoryとの協調が必要（実装クラスで対応）
        }
        
        default boolean verifyBackup(String userId) {
            System.out.println("Verifying backup for user: " + userId);
            // バックアップの整合性チェック
            return true; // 簡略化
        }
    }
    
    /**
     * ユーザー認証機能
     */
    interface UserAuthenticationService {
        boolean authenticate(String userId, String password);
        void invalidateSession(String userId);
        
        // 認証関連のdefaultメソッド
        default boolean isValidSession(String userId, String sessionToken) {
            // セッション検証の基本実装
            return sessionToken != null && sessionToken.length() > 10;
        }
        
        default void lockAccount(String userId) {
            System.out.println("Locking account for user: " + userId);
            // アカウントロック処理
        }
        
        default void unlockAccount(String userId) {
            System.out.println("Unlocking account for user: " + userId);
            // アカウントロック解除処理
        }
    }
    
    /**
     * ユーザー統計機能
     */
    interface UserAnalyticsService {
        void trackUserActivity(String userId, String activity);
        
        // 分析関連のdefaultメソッド
        default void trackLogin(String userId) {
            trackUserActivity(userId, "LOGIN");
        }
        
        default void trackLogout(String userId) {
            trackUserActivity(userId, "LOGOUT");
        }
        
        default void trackFeatureUsage(String userId, String feature) {
            trackUserActivity(userId, "FEATURE_" + feature);
        }
    }
    
    /**
     * 基本的なユーザーサービス実装
     * 必要な機能のみを実装
     */
    static class BasicUserService implements UserRepository, UserNotificationService {
        private final List<User> users = new ArrayList<>();
        
        // UserRepository implementation
        @Override
        public void save(User user) {
            User existing = findById(user.getId());
            if (existing != null) {
                users.remove(existing);
            }
            users.add(user);
            System.out.println("Saved user: " + user);
        }
        
        @Override
        public void delete(String id) {
            users.removeIf(user -> user.getId().equals(id));
            System.out.println("Deleted user: " + id);
        }
        
        @Override
        public User findById(String id) {
            return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
        }
        
        @Override
        public List<User> findAll() {
            return new ArrayList<>(users);
        }
        
        // UserNotificationService implementation
        @Override
        public void sendEmail(String userId, String subject, String message) {
            User user = findById(userId);
            if (user != null) {
                System.out.println("Email sent to " + user.getEmail() + 
                                 " - Subject: " + subject + ", Message: " + message);
            } else {
                System.out.println("User not found: " + userId);
            }
        }
    }
    
    /**
     * フル機能のユーザーサービス実装
     * すべての機能を実装
     */
    static class FullUserService implements UserRepository, UserNotificationService, 
                                           UserReportService, UserBackupService,
                                           UserAuthenticationService, UserAnalyticsService {
        private final List<User> users = new ArrayList<>();
        
        // UserRepository implementation
        @Override
        public void save(User user) {
            User existing = findById(user.getId());
            if (existing != null) {
                users.remove(existing);
            }
            users.add(user);
            trackUserActivity(user.getId(), "USER_SAVED");
        }
        
        @Override
        public void delete(String id) {
            users.removeIf(user -> user.getId().equals(id));
            trackUserActivity(id, "USER_DELETED");
        }
        
        @Override
        public User findById(String id) {
            return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
        }
        
        @Override
        public List<User> findAll() {
            return new ArrayList<>(users);
        }
        
        // UserNotificationService implementation
        @Override
        public void sendEmail(String userId, String subject, String message) {
            User user = findById(userId);
            if (user != null) {
                System.out.println("Email sent to " + user.getEmail() + 
                                 " - Subject: " + subject);
                trackUserActivity(userId, "EMAIL_SENT");
            }
        }
        
        // UserReportService implementation
        @Override
        public void generateReport(String userId) {
            User user = findById(userId);
            if (user != null) {
                System.out.println("Generating report for: " + user.getName());
                trackUserActivity(userId, "REPORT_GENERATED");
            }
        }
        
        // UserBackupService implementation
        @Override
        public void backupUserData(String userId) {
            User user = findById(userId);
            if (user != null) {
                System.out.println("Backing up data for: " + user.getName());
                trackUserActivity(userId, "BACKUP_CREATED");
            }
        }
        
        @Override
        public void backupAllActiveUsers() {
            List<User> activeUsers = findActiveUsers();
            System.out.println("Backing up " + activeUsers.size() + " active users");
            activeUsers.forEach(user -> backupUserData(user.getId()));
        }
        
        // UserAuthenticationService implementation
        @Override
        public boolean authenticate(String userId, String password) {
            User user = findById(userId);
            boolean success = user != null && user.isActive();
            if (success) {
                trackLogin(userId);
            }
            return success;
        }
        
        @Override
        public void invalidateSession(String userId) {
            System.out.println("Invalidating session for: " + userId);
            trackLogout(userId);
        }
        
        // UserAnalyticsService implementation
        @Override
        public void trackUserActivity(String userId, String activity) {
            System.out.println("Analytics: User " + userId + " performed " + activity);
        }
    }
    
    /**
     * 読み取り専用のユーザーサービス
     * 参照機能のみを実装
     */
    static class ReadOnlyUserService implements UserRepository, UserAnalyticsService {
        private final List<User> users;
        
        public ReadOnlyUserService(List<User> users) {
            this.users = new ArrayList<>(users);
        }
        
        // UserRepository implementation (read-only)
        @Override
        public void save(User user) {
            throw new UnsupportedOperationException("Read-only service");
        }
        
        @Override
        public void delete(String id) {
            throw new UnsupportedOperationException("Read-only service");
        }
        
        @Override
        public User findById(String id) {
            return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
        }
        
        @Override
        public List<User> findAll() {
            return new ArrayList<>(users);
        }
        
        // UserAnalyticsService implementation
        @Override
        public void trackUserActivity(String userId, String activity) {
            System.out.println("Read-only Analytics: " + userId + " -> " + activity);
        }
    }
    
    /**
     * デモンストレーション
     */
    public static void demonstrateInterfaceSeparation() {
        System.out.println("=== Interface Separation Principle Demo ===");
        
        // 基本サービスの使用
        System.out.println("\n--- Basic User Service ---");
        BasicUserService basicService = new BasicUserService();
        
        User user1 = new User("user001", "Alice", "alice@example.com");
        User user2 = new User("user002", "Bob", "bob@example.com");
        
        basicService.save(user1);
        basicService.save(user2);
        
        // UserRepositoryの機能
        System.out.println("Total users: " + basicService.count());
        System.out.println("User exists: " + basicService.exists("user001"));
        
        // UserNotificationServiceの機能
        basicService.sendWelcomeEmail("user001");
        basicService.sendPasswordResetEmail("user002", "reset123");
        
        // フルサービスの使用
        System.out.println("\n--- Full User Service ---");
        FullUserService fullService = new FullUserService();
        
        fullService.save(user1);
        fullService.save(user2);
        
        // 認証機能
        boolean authenticated = fullService.authenticate("user001", "password");
        System.out.println("Authentication result: " + authenticated);
        
        // レポート機能
        fullService.generateMonthlyReport("user001");
        
        // バックアップ機能
        fullService.backupUserData("user002");
        fullService.backupAllActiveUsers();
        
        // 分析機能
        fullService.trackFeatureUsage("user001", "SEARCH");
        
        // 読み取り専用サービス
        System.out.println("\n--- Read-Only Service ---");
        List<User> existingUsers = fullService.findAll();
        ReadOnlyUserService readOnlyService = new ReadOnlyUserService(existingUsers);
        
        System.out.println("Read-only user count: " + readOnlyService.count());
        readOnlyService.trackUserActivity("user001", "READ_OPERATION");
        
        try {
            readOnlyService.save(new User("user003", "Charlie", "charlie@example.com"));
        } catch (UnsupportedOperationException e) {
            System.out.println("Expected error: " + e.getMessage());
        }
        
        // 機能の組み合わせ
        System.out.println("\n--- Service Combination ---");
        demonstrateServiceCombination(fullService);
    }
    
    /**
     * サービス機能の組み合わせデモ
     */
    private static void demonstrateServiceCombination(FullUserService service) {
        String userId = "user001";
        
        // ユーザー登録からバックアップまでの一連の流れ
        User newUser = new User("user003", "Charlie", "charlie@example.com");
        
        // 1. ユーザー保存
        service.save(newUser);
        
        // 2. ウェルカムメール送信
        service.sendWelcomeEmail(newUser.getId());
        
        // 3. 認証テスト
        boolean auth = service.authenticate(newUser.getId(), "password");
        System.out.println("New user authentication: " + auth);
        
        // 4. 活動追跡
        service.trackFeatureUsage(newUser.getId(), "PROFILE_UPDATE");
        
        // 5. レポート生成（非同期）
        service.generateReportAsync(newUser.getId())
            .thenRun(() -> System.out.println("Async report generation completed"));
        
        // 6. バックアップ検証
        boolean backupValid = service.verifyBackup(newUser.getId());
        System.out.println("Backup verification: " + backupValid);
        
        // 7. セッション管理
        service.invalidateSession(newUser.getId());
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        demonstrateInterfaceSeparation();
    }
}