/**
 * 第20章 課題2: モックオブジェクトを使用したテスト - 解答例
 * 
 * モッキングフレームワークを使用した高度なテスト技法の実装
 * 
 * 学習ポイント:
 * - モックオブジェクトの概念と使用法
 * - 依存関係の分離とテスト容易性
 * - スタブとモックの違い
 * - テストダブルの効果的な活用
 * - 外部依存をテストから切り離す技法
 */

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;

import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * モックテストのデモンストレーション用クラス群
 * 
 * このクラスは以下の概念を実装しています：
 * - サービス層とリポジトリ層の分離
 * - 依存性注入（Dependency Injection）
 * - 外部システムとの結合度を下げる設計
 */
public class MockExample {
    
    /**
     * ユーザーエンティティクラス
     */
    public static class User {
        private String id;
        private String name;
        private String email;
        private LocalDateTime createdAt;
        private boolean active;
        
        public User(String id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.createdAt = LocalDateTime.now();
            this.active = true;
        }
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof User)) return false;
            User user = (User) o;
            return Objects.equals(id, user.id);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
        
        @Override
        public String toString() {
            return String.format("User{id='%s', name='%s', email='%s', active=%s}",
                    id, name, email, active);
        }
    }
    
    /**
     * ユーザーリポジトリインターフェイス
     * 
     * データアクセス層の抽象化により、テスト時にモックオブジェクトを注入可能
     */
    public interface UserRepository {
        User findById(String id);
        List<User> findByName(String name);
        List<User> findAll();
        void save(User user);
        void delete(String id);
        boolean existsById(String id);
        long count();
    }
    
    /**
     * メール送信サービスインターフェイス
     * 
     * 外部システム（メールサーバー）への依存を抽象化
     */
    public interface EmailService {
        void sendEmail(String to, String subject, String body);
        void sendWelcomeEmail(User user);
        boolean isEmailServiceAvailable();
    }
    
    /**
     * 監査ログサービスインターフェイス
     * 
     * ログ出力システムへの依存を抽象化
     */
    public interface AuditService {
        void logUserAction(String userId, String action, String details);
        void logSystemEvent(String event, String details);
    }
    
    /**
     * ユーザーサービスクラス
     * 
     * ビジネスロジックを実装し、依存関係を注入で受け取る
     */
    public static class UserService {
        private final UserRepository userRepository;
        private final EmailService emailService;
        private final AuditService auditService;
        
        public UserService(UserRepository userRepository, 
                          EmailService emailService, 
                          AuditService auditService) {
            this.userRepository = userRepository;
            this.emailService = emailService;
            this.auditService = auditService;
        }
        
        /**
         * 新規ユーザーを登録する
         * 
         * @param name ユーザー名
         * @param email メールアドレス
         * @return 作成されたユーザー
         * @throws IllegalArgumentException 無効な入力の場合
         * @throws RuntimeException メール送信に失敗した場合
         */
        public User registerUser(String name, String email) {
            // 入力検証
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("ユーザー名は必須です");
            }
            if (email == null || !isValidEmail(email)) {
                throw new IllegalArgumentException("有効なメールアドレスを入力してください");
            }
            
            // 重複チェック
            List<User> existingUsers = userRepository.findByName(name);
            if (!existingUsers.isEmpty()) {
                throw new IllegalArgumentException("同名のユーザーが既に存在します");
            }
            
            // ユーザー作成
            String userId = generateUserId();
            User user = new User(userId, name, email);
            
            // データベースに保存
            userRepository.save(user);
            
            // ウェルカムメール送信
            try {
                if (emailService.isEmailServiceAvailable()) {
                    emailService.sendWelcomeEmail(user);
                }
            } catch (Exception e) {
                // メール送信失敗時はログに記録するが、ユーザー登録は継続
                auditService.logSystemEvent("EMAIL_FAILED", 
                    "ウェルカムメール送信失敗: " + user.getId());
            }
            
            // 監査ログ
            auditService.logUserAction(user.getId(), "USER_REGISTERED", 
                "新規ユーザー登録: " + user.getName());
            
            return user;
        }
        
        /**
         * ユーザーを無効化する
         * 
         * @param userId ユーザーID
         * @throws IllegalArgumentException ユーザーが存在しない場合
         */
        public void deactivateUser(String userId) {
            User user = userRepository.findById(userId);
            if (user == null) {
                throw new IllegalArgumentException("ユーザーが見つかりません: " + userId);
            }
            
            if (!user.isActive()) {
                throw new IllegalStateException("ユーザーは既に無効化されています: " + userId);
            }
            
            user.setActive(false);
            userRepository.save(user);
            
            // 無効化通知メール
            try {
                if (emailService.isEmailServiceAvailable()) {
                    emailService.sendEmail(user.getEmail(), 
                        "アカウント無効化のお知らせ", 
                        "あなたのアカウントが無効化されました。");
                }
            } catch (Exception e) {
                auditService.logSystemEvent("EMAIL_FAILED", 
                    "無効化通知メール送信失敗: " + userId);
            }
            
            auditService.logUserAction(userId, "USER_DEACTIVATED", 
                "ユーザー無効化: " + user.getName());
        }
        
        /**
         * アクティブなユーザー数を取得する
         * 
         * @return アクティブなユーザー数
         */
        public long getActiveUserCount() {
            List<User> allUsers = userRepository.findAll();
            return allUsers.stream().filter(User::isActive).count();
        }
        
        /**
         * ユーザー情報を更新する
         * 
         * @param userId ユーザーID
         * @param newName 新しい名前
         * @param newEmail 新しいメールアドレス
         * @return 更新されたユーザー
         */
        public User updateUser(String userId, String newName, String newEmail) {
            User user = userRepository.findById(userId);
            if (user == null) {
                throw new IllegalArgumentException("ユーザーが見つかりません: " + userId);
            }
            
            if (!user.isActive()) {
                throw new IllegalStateException("無効化されたユーザーは更新できません: " + userId);
            }
            
            // 変更内容の記録
            String changes = "";
            if (newName != null && !newName.equals(user.getName())) {
                user.setName(newName);
                changes += "名前: " + newName + " ";
            }
            if (newEmail != null && !newEmail.equals(user.getEmail())) {
                if (!isValidEmail(newEmail)) {
                    throw new IllegalArgumentException("無効なメールアドレス: " + newEmail);
                }
                user.setEmail(newEmail);
                changes += "メール: " + newEmail + " ";
            }
            
            if (!changes.isEmpty()) {
                userRepository.save(user);
                auditService.logUserAction(userId, "USER_UPDATED", "更新内容: " + changes);
            }
            
            return user;
        }
        
        // プライベートメソッド
        private String generateUserId() {
            return "USR" + System.currentTimeMillis();
        }
        
        private boolean isValidEmail(String email) {
            return email.contains("@") && email.contains(".") && email.length() > 5;
        }
    }
}

/**
 * MockExampleのテストクラス
 * 
 * Mockitoフレームワークを使用した包括的なテストスイート
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ユーザーサービスのモックテスト")
class MockExampleTest {
    
    @Mock
    private MockExample.UserRepository userRepository;
    
    @Mock
    private MockExample.EmailService emailService;
    
    @Mock
    private MockExample.AuditService auditService;
    
    @InjectMocks
    private MockExample.UserService userService;
    
    private MockExample.User sampleUser;
    
    @BeforeEach
    void setUp() {
        sampleUser = new MockExample.User("USR123", "田中太郎", "tanaka@example.com");
    }
    
    @Test
    @DisplayName("正常なユーザー登録")
    void testRegisterUser_Success() {
        // Arrange
        String name = "新規ユーザー";
        String email = "newuser@example.com";
        
        when(userRepository.findByName(name)).thenReturn(new ArrayList<>());
        when(emailService.isEmailServiceAvailable()).thenReturn(true);
        
        // Act
        MockExample.User result = userService.registerUser(name, email);
        
        // Assert
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(email, result.getEmail());
        assertTrue(result.isActive());
        
        // モックの呼び出し検証
        verify(userRepository).findByName(name);
        verify(userRepository).save(any(MockExample.User.class));
        verify(emailService).isEmailServiceAvailable();
        verify(emailService).sendWelcomeEmail(any(MockExample.User.class));
        verify(auditService).logUserAction(anyString(), eq("USER_REGISTERED"), anyString());
    }
    
    @Test
    @DisplayName("重複ユーザー名での登録失敗")
    void testRegisterUser_DuplicateName() {
        // Arrange
        String name = "既存ユーザー";
        String email = "test@example.com";
        
        List<MockExample.User> existingUsers = Arrays.asList(sampleUser);
        when(userRepository.findByName(name)).thenReturn(existingUsers);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> userService.registerUser(name, email)
        );
        
        assertEquals("同名のユーザーが既に存在します", exception.getMessage());
        
        // 保存やメール送信が呼ばれていないことを確認
        verify(userRepository, never()).save(any());
        verify(emailService, never()).sendWelcomeEmail(any());
    }
    
    @Test
    @DisplayName("無効なメールアドレスでの登録失敗")
    void testRegisterUser_InvalidEmail() {
        // Arrange
        String name = "テストユーザー";
        String invalidEmail = "invalid-email";
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.registerUser(name, invalidEmail)
        );
        
        assertEquals("有効なメールアドレスを入力してください", exception.getMessage());
        
        // リポジトリが呼ばれていないことを確認
        verify(userRepository, never()).findByName(anyString());
        verify(userRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("メール送信失敗時の処理")
    void testRegisterUser_EmailFailure() {
        // Arrange
        String name = "テストユーザー";
        String email = "test@example.com";
        
        when(userRepository.findByName(name)).thenReturn(new ArrayList<>());
        when(emailService.isEmailServiceAvailable()).thenReturn(true);
        doThrow(new RuntimeException("メールサーバーエラー"))
            .when(emailService).sendWelcomeEmail(any());
        
        // Act - 例外が発生しないことを確認
        assertDoesNotThrow(() -> {
            MockExample.User result = userService.registerUser(name, email);
            assertNotNull(result);
        });
        
        // Assert
        verify(userRepository).save(any());
        verify(auditService).logSystemEvent(eq("EMAIL_FAILED"), anyString());
        verify(auditService).logUserAction(anyString(), eq("USER_REGISTERED"), anyString());
    }
    
    @Test
    @DisplayName("ユーザー無効化の正常処理")
    void testDeactivateUser_Success() {
        // Arrange
        String userId = "USR123";
        sampleUser.setActive(true);
        
        when(userRepository.findById(userId)).thenReturn(sampleUser);
        when(emailService.isEmailServiceAvailable()).thenReturn(true);
        
        // Act
        userService.deactivateUser(userId);
        
        // Assert
        assertFalse(sampleUser.isActive());
        verify(userRepository).findById(userId);
        verify(userRepository).save(sampleUser);
        verify(emailService).sendEmail(eq(sampleUser.getEmail()), anyString(), anyString());
        verify(auditService).logUserAction(eq(userId), eq("USER_DEACTIVATED"), anyString());
    }
    
    @Test
    @DisplayName("存在しないユーザーの無効化")
    void testDeactivateUser_UserNotFound() {
        // Arrange
        String userId = "NONEXISTENT";
        when(userRepository.findById(userId)).thenReturn(null);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.deactivateUser(userId)
        );
        
        assertEquals("ユーザーが見つかりません: " + userId, exception.getMessage());
        verify(userRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("既に無効化されたユーザーの無効化")
    void testDeactivateUser_AlreadyInactive() {
        // Arrange
        String userId = "USR123";
        sampleUser.setActive(false);
        
        when(userRepository.findById(userId)).thenReturn(sampleUser);
        
        // Act & Assert
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> userService.deactivateUser(userId)
        );
        
        assertEquals("ユーザーは既に無効化されています: " + userId, exception.getMessage());
        verify(userRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("アクティブユーザー数の取得")
    void testGetActiveUserCount() {
        // Arrange
        MockExample.User user1 = new MockExample.User("USR001", "ユーザー1", "user1@example.com");
        MockExample.User user2 = new MockExample.User("USR002", "ユーザー2", "user2@example.com");
        MockExample.User user3 = new MockExample.User("USR003", "ユーザー3", "user3@example.com");
        
        user1.setActive(true);
        user2.setActive(false);
        user3.setActive(true);
        
        List<MockExample.User> allUsers = Arrays.asList(user1, user2, user3);
        when(userRepository.findAll()).thenReturn(allUsers);
        
        // Act
        long activeCount = userService.getActiveUserCount();
        
        // Assert
        assertEquals(2, activeCount);
        verify(userRepository).findAll();
    }
    
    @Test
    @DisplayName("ユーザー情報更新の正常処理")
    void testUpdateUser_Success() {
        // Arrange
        String userId = "USR123";
        String newName = "更新後の名前";
        String newEmail = "updated@example.com";
        
        sampleUser.setActive(true);
        when(userRepository.findById(userId)).thenReturn(sampleUser);
        
        // Act
        MockExample.User result = userService.updateUser(userId, newName, newEmail);
        
        // Assert
        assertEquals(newName, result.getName());
        assertEquals(newEmail, result.getEmail());
        verify(userRepository).save(sampleUser);
        verify(auditService).logUserAction(eq(userId), eq("USER_UPDATED"), anyString());
    }
    
    @Test
    @DisplayName("無効化されたユーザーの更新失敗")
    void testUpdateUser_InactiveUser() {
        // Arrange
        String userId = "USR123";
        sampleUser.setActive(false);
        
        when(userRepository.findById(userId)).thenReturn(sampleUser);
        
        // Act & Assert
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> userService.updateUser(userId, "新しい名前", "new@example.com")
        );
        
        assertEquals("無効化されたユーザーは更新できません: " + userId, exception.getMessage());
        verify(userRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("ArgumentCaptorを使用した引数の詳細検証")
    void testRegisterUser_ArgumentCaptor() {
        // Arrange
        String name = "キャプチャーテスト";
        String email = "capture@example.com";
        
        when(userRepository.findByName(name)).thenReturn(new ArrayList<>());
        when(emailService.isEmailServiceAvailable()).thenReturn(true);
        
        ArgumentCaptor<MockExample.User> userCaptor = ArgumentCaptor.forClass(MockExample.User.class);
        ArgumentCaptor<String> actionCaptor = ArgumentCaptor.forClass(String.class);
        
        // Act
        userService.registerUser(name, email);
        
        // Assert - 保存されたユーザーオブジェクトの詳細検証
        verify(userRepository).save(userCaptor.capture());
        MockExample.User savedUser = userCaptor.getValue();
        
        assertEquals(name, savedUser.getName());
        assertEquals(email, savedUser.getEmail());
        assertTrue(savedUser.isActive());
        assertNotNull(savedUser.getId());
        assertNotNull(savedUser.getCreatedAt());
        
        // 監査ログの引数検証
        verify(auditService).logUserAction(anyString(), actionCaptor.capture(), anyString());
        assertEquals("USER_REGISTERED", actionCaptor.getValue());
    }
    
    @Test
    @DisplayName("複数回呼び出しの動作検証")
    void testMultipleMethodCalls() {
        // Arrange
        when(userRepository.findById("USR123")).thenReturn(sampleUser);
        when(emailService.isEmailServiceAvailable()).thenReturn(true);
        
        // Act - 同じメソッドを複数回呼び出し
        userService.updateUser("USR123", "名前1", null);
        userService.updateUser("USR123", "名前2", null);
        
        // Assert - 呼び出し回数の検証
        verify(userRepository, times(2)).findById("USR123");
        verify(userRepository, times(2)).save(sampleUser);
        verify(auditService, times(2)).logUserAction(anyString(), eq("USER_UPDATED"), anyString());
    }
    
    @Test
    @DisplayName("メソッド呼び出し順序の検証")
    void testMethodCallOrder() {
        // Arrange
        String name = "順序テスト";
        String email = "order@example.com";
        
        when(userRepository.findByName(name)).thenReturn(new ArrayList<>());
        when(emailService.isEmailServiceAvailable()).thenReturn(true);
        
        // Act
        userService.registerUser(name, email);
        
        // Assert - 呼び出し順序の検証
        var inOrder = inOrder(userRepository, emailService, auditService);
        inOrder.verify(userRepository).findByName(name);
        inOrder.verify(userRepository).save(any());
        inOrder.verify(emailService).isEmailServiceAvailable();
        inOrder.verify(emailService).sendWelcomeEmail(any());
        inOrder.verify(auditService).logUserAction(anyString(), eq("USER_REGISTERED"), anyString());
    }
}

/*
 * モックテストの実装ポイント:
 * 
 * 1. モックオブジェクトの活用
 *    - @Mock アノテーションによる依存関係のモック化
 *    - @InjectMocks による依存性注入の自動化
 *    - when().thenReturn() による戻り値の設定
 *    - doThrow() による例外発生のシミュレーション
 * 
 * 2. 検証パターン
 *    - verify() によるメソッド呼び出しの検証
 *    - times(), never() による呼び出し回数の確認
 *    - ArgumentCaptor による引数の詳細検証
 *    - inOrder() による呼び出し順序の検証
 * 
 * 3. テスト設計の原則
 *    - 依存関係の分離によるテスト容易性向上
 *    - 外部システムへの依存を排除
 *    - ビジネスロジックのみに焦点を当てたテスト
 *    - 境界値とエラーケースの網羅的なテスト
 * 
 * 4. 実践的なテクニック
 *    - スタブとモックの使い分け
 *    - 部分モック（Spy）の活用
 *    - モック設定の再利用
 *    - テストデータの効率的な準備
 * 
 * 5. モックテストの利点
 *    - 高速なテスト実行
 *    - 外部依存の排除
 *    - 障害の局所化
 *    - テストの信頼性向上
 * 
 * 注意点:
 * - 過度なモック化は避ける
 * - 実際の統合テストも併用する
 * - モックの設定ミスに注意
 * - テストが実装に依存しすぎないよう配慮
 */