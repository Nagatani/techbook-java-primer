package com.example.testing;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Testcontainersを模擬したデータベース統合テストのデモンストレーション
 * 実際のデータベースを使用した統合テストの重要性を学習
 */
public class TestContainersDemo {
    
    /**
     * データアクセス層のエンティティ
     */
    public static class User {
        private Long id;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private java.util.Date createdAt;
        private boolean active;
        
        public User() {}
        
        public User(String username, String email, String firstName, String lastName) {
            this.username = username;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.active = true;
            this.createdAt = new java.util.Date();
        }
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public java.util.Date getCreatedAt() { return createdAt; }
        public void setCreatedAt(java.util.Date createdAt) { this.createdAt = createdAt; }
        
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        
        @Override
        public String toString() {
            return String.format("User{id=%d, username='%s', email='%s', name='%s %s', active=%s}",
                    id, username, email, firstName, lastName, active);
        }
    }
    
    /**
     * データアクセス層のリポジトリ実装
     */
    public static class UserRepository {
        private final Connection connection;
        
        public UserRepository(Connection connection) {
            this.connection = connection;
            createTableIfNotExists();
        }
        
        private void createTableIfNotExists() {
            String sql = """
                CREATE TABLE IF NOT EXISTS users (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    email VARCHAR(100) UNIQUE NOT NULL,
                    first_name VARCHAR(50) NOT NULL,
                    last_name VARCHAR(50) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    active BOOLEAN DEFAULT TRUE
                )
                """;
            
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(sql);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to create users table", e);
            }
        }
        
        public User save(User user) {
            if (user.getId() == null) {
                return insert(user);
            } else {
                return update(user);
            }
        }
        
        private User insert(User user) {
            String sql = """
                INSERT INTO users (username, email, first_name, last_name, created_at, active)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
            
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getEmail());
                stmt.setString(3, user.getFirstName());
                stmt.setString(4, user.getLastName());
                stmt.setTimestamp(5, new Timestamp(user.getCreatedAt().getTime()));
                stmt.setBoolean(6, user.isActive());
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Creating user failed, no rows affected.");
                }
                
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getLong(1));
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
                
                return user;
            } catch (SQLException e) {
                throw new RuntimeException("Failed to insert user", e);
            }
        }
        
        private User update(User user) {
            String sql = """
                UPDATE users 
                SET username = ?, email = ?, first_name = ?, last_name = ?, active = ?
                WHERE id = ?
                """;
            
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getEmail());
                stmt.setString(3, user.getFirstName());
                stmt.setString(4, user.getLastName());
                stmt.setBoolean(5, user.isActive());
                stmt.setLong(6, user.getId());
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new RuntimeException("User not found with id: " + user.getId());
                }
                
                return user;
            } catch (SQLException e) {
                throw new RuntimeException("Failed to update user", e);
            }
        }
        
        public Optional<User> findById(Long id) {
            String sql = "SELECT * FROM users WHERE id = ?";
            
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setLong(1, id);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(mapResultSetToUser(rs));
                    }
                }
                
                return Optional.empty();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to find user by id", e);
            }
        }
        
        public Optional<User> findByUsername(String username) {
            String sql = "SELECT * FROM users WHERE username = ?";
            
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, username);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(mapResultSetToUser(rs));
                    }
                }
                
                return Optional.empty();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to find user by username", e);
            }
        }
        
        public List<User> findByEmailDomain(String domain) {
            String sql = "SELECT * FROM users WHERE email LIKE ? ORDER BY created_at DESC";
            
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, "%@" + domain);
                
                List<User> users = new ArrayList<>();
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        users.add(mapResultSetToUser(rs));
                    }
                }
                
                return users;
            } catch (SQLException e) {
                throw new RuntimeException("Failed to find users by email domain", e);
            }
        }
        
        public List<User> findActiveUsers() {
            String sql = "SELECT * FROM users WHERE active = TRUE ORDER BY created_at DESC";
            
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                List<User> users = new ArrayList<>();
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
                
                return users;
            } catch (SQLException e) {
                throw new RuntimeException("Failed to find active users", e);
            }
        }
        
        public boolean deleteById(Long id) {
            String sql = "DELETE FROM users WHERE id = ?";
            
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setLong(1, id);
                
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            } catch (SQLException e) {
                throw new RuntimeException("Failed to delete user", e);
            }
        }
        
        public void deactivateUser(Long id) {
            String sql = "UPDATE users SET active = FALSE WHERE id = ?";
            
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setLong(1, id);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new RuntimeException("User not found with id: " + id);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to deactivate user", e);
            }
        }
        
        public long countActiveUsers() {
            String sql = "SELECT COUNT(*) FROM users WHERE active = TRUE";
            
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                if (rs.next()) {
                    return rs.getLong(1);
                }
                
                return 0;
            } catch (SQLException e) {
                throw new RuntimeException("Failed to count active users", e);
            }
        }
        
        private User mapResultSetToUser(ResultSet rs) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setEmail(rs.getString("email"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setCreatedAt(rs.getTimestamp("created_at"));
            user.setActive(rs.getBoolean("active"));
            return user;
        }
    }
    
    /**
     * インメモリデータベース（H2）を使った統合テストの実装
     */
    public static class DatabaseIntegrationTest {
        private Connection connection;
        private UserRepository userRepository;
        
        public void setUp() throws SQLException {
            // インメモリH2データベースの作成
            connection = DriverManager.getConnection(
                "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
                "sa",
                ""
            );
            
            userRepository = new UserRepository(connection);
            System.out.println("✓ Test database initialized");
        }
        
        public void tearDown() throws SQLException {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            System.out.println("✓ Test database cleaned up");
        }
        
        /**
         * 基本的なCRUD操作のテスト
         */
        public void testBasicCrudOperations() {
            System.out.println("\n=== Basic CRUD Operations Test ===");
            
            // Create
            User user = new User("johndoe", "john@example.com", "John", "Doe");
            User savedUser = userRepository.save(user);
            
            assertNotNull(savedUser.getId(), "User ID should be generated");
            assertEquals("johndoe", savedUser.getUsername(), "Username should match");
            System.out.println("✓ User created: " + savedUser);
            
            // Read
            Optional<User> foundUser = userRepository.findById(savedUser.getId());
            assertTrue(foundUser.isPresent(), "User should be found by ID");
            assertEquals("john@example.com", foundUser.get().getEmail(), "Email should match");
            System.out.println("✓ User found by ID: " + foundUser.get());
            
            // Update
            foundUser.get().setEmail("john.doe@example.com");
            User updatedUser = userRepository.save(foundUser.get());
            assertEquals("john.doe@example.com", updatedUser.getEmail(), "Email should be updated");
            System.out.println("✓ User updated: " + updatedUser);
            
            // Delete
            boolean deleted = userRepository.deleteById(savedUser.getId());
            assertTrue(deleted, "User should be deleted");
            
            Optional<User> deletedUser = userRepository.findById(savedUser.getId());
            assertFalse(deletedUser.isPresent(), "User should not be found after deletion");
            System.out.println("✓ User deleted successfully");
        }
        
        /**
         * 一意制約のテスト
         */
        public void testUniqueConstraints() {
            System.out.println("\n=== Unique Constraints Test ===");
            
            // 最初のユーザーを作成
            User user1 = new User("uniqueuser", "unique@example.com", "Unique", "User");
            userRepository.save(user1);
            System.out.println("✓ First user created: " + user1.getUsername());
            
            // 同じユーザー名で作成を試行
            try {
                User user2 = new User("uniqueuser", "different@example.com", "Different", "User");
                userRepository.save(user2);
                fail("Should have thrown exception for duplicate username");
            } catch (RuntimeException e) {
                System.out.println("✓ Duplicate username rejected: " + e.getMessage());
            }
            
            // 同じメールアドレスで作成を試行
            try {
                User user3 = new User("differentuser", "unique@example.com", "Different", "User");
                userRepository.save(user3);
                fail("Should have thrown exception for duplicate email");
            } catch (RuntimeException e) {
                System.out.println("✓ Duplicate email rejected: " + e.getMessage());
            }
        }
        
        /**
         * 複雑なクエリのテスト
         */
        public void testComplexQueries() {
            System.out.println("\n=== Complex Queries Test ===");
            
            // テストデータの作成
            List<User> testUsers = Arrays.asList(
                new User("alice", "alice@company.com", "Alice", "Smith"),
                new User("bob", "bob@company.com", "Bob", "Jones"),
                new User("charlie", "charlie@external.org", "Charlie", "Brown"),
                new User("diana", "diana@company.com", "Diana", "Wilson")
            );
            
            for (User user : testUsers) {
                userRepository.save(user);
            }
            System.out.println("✓ Test data created: " + testUsers.size() + " users");
            
            // ドメイン別検索のテスト
            List<User> companyUsers = userRepository.findByEmailDomain("company.com");
            assertEquals(3, companyUsers.size(), "Should find 3 company.com users");
            System.out.println("✓ Found " + companyUsers.size() + " users from company.com domain");
            
            List<User> externalUsers = userRepository.findByEmailDomain("external.org");
            assertEquals(1, externalUsers.size(), "Should find 1 external.org user");
            System.out.println("✓ Found " + externalUsers.size() + " users from external.org domain");
            
            // アクティブユーザーのテスト
            long activeCount = userRepository.countActiveUsers();
            assertEquals(4, activeCount, "Should have 4 active users");
            System.out.println("✓ Active users count: " + activeCount);
            
            // ユーザーの非アクティブ化
            User alice = userRepository.findByUsername("alice").orElseThrow();
            userRepository.deactivateUser(alice.getId());
            
            long activeCountAfterDeactivation = userRepository.countActiveUsers();
            assertEquals(3, activeCountAfterDeactivation, "Should have 3 active users after deactivation");
            System.out.println("✓ Active users after deactivation: " + activeCountAfterDeactivation);
            
            List<User> activeUsers = userRepository.findActiveUsers();
            assertEquals(3, activeUsers.size(), "Should find 3 active users");
            assertFalse(activeUsers.stream().anyMatch(u -> "alice".equals(u.getUsername())),
                       "Alice should not be in active users list");
            System.out.println("✓ Active users query excludes deactivated user");
        }
        
        /**
         * トランザクションのテスト
         */
        public void testTransactionBehavior() {
            System.out.println("\n=== Transaction Behavior Test ===");
            
            try {
                connection.setAutoCommit(false);
                
                // トランザクション内でユーザーを作成
                User user1 = new User("tx_user1", "tx1@example.com", "TX", "User1");
                User user2 = new User("tx_user2", "tx2@example.com", "TX", "User2");
                
                userRepository.save(user1);
                userRepository.save(user2);
                
                // コミット前の確認（まだ見えるはず - 同一トランザクション内）
                long countBeforeCommit = userRepository.countActiveUsers();
                System.out.println("✓ Users in transaction before commit: " + countBeforeCommit);
                
                // コミット
                connection.commit();
                System.out.println("✓ Transaction committed");
                
                // コミット後の確認
                long countAfterCommit = userRepository.countActiveUsers();
                System.out.println("✓ Users after commit: " + countAfterCommit);
                
                // ロールバックのテスト
                User user3 = new User("tx_user3", "tx3@example.com", "TX", "User3");
                userRepository.save(user3);
                
                long countBeforeRollback = userRepository.countActiveUsers();
                System.out.println("✓ Users before rollback: " + countBeforeRollback);
                
                // ロールバック
                connection.rollback();
                System.out.println("✓ Transaction rolled back");
                
                long countAfterRollback = userRepository.countActiveUsers();
                assertEquals(countAfterCommit, countAfterRollback, 
                           "Count should be same as before the rolled back transaction");
                System.out.println("✓ Users after rollback: " + countAfterRollback);
                
                // user3は存在しないはず
                Optional<User> rolledBackUser = userRepository.findByUsername("tx_user3");
                assertFalse(rolledBackUser.isPresent(), "Rolled back user should not exist");
                System.out.println("✓ Rolled back user does not exist");
                
            } catch (SQLException e) {
                throw new RuntimeException("Transaction test failed", e);
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    // Ignore
                }
            }
        }
        
        /**
         * パフォーマンステスト
         */
        public void testPerformance() {
            System.out.println("\n=== Performance Test ===");
            
            int userCount = 1000;
            List<User> users = new ArrayList<>();
            
            // 大量ユーザーデータの生成
            for (int i = 0; i < userCount; i++) {
                users.add(new User(
                    "user" + i,
                    "user" + i + "@performance.test",
                    "User",
                    "Number" + i
                ));
            }
            
            // 一括挿入のパフォーマンステスト
            long startTime = System.currentTimeMillis();
            
            try {
                connection.setAutoCommit(false);
                for (User user : users) {
                    userRepository.save(user);
                }
                connection.commit();
            } catch (SQLException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    // Ignore
                }
                throw new RuntimeException("Bulk insert failed", e);
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e) {
                    // Ignore
                }
            }
            
            long insertTime = System.currentTimeMillis() - startTime;
            System.out.println("✓ Inserted " + userCount + " users in " + insertTime + "ms");
            
            // 検索パフォーマンステスト
            startTime = System.currentTimeMillis();
            
            for (int i = 0; i < 100; i++) {
                int randomIndex = ThreadLocalRandom.current().nextInt(userCount);
                userRepository.findByUsername("user" + randomIndex);
            }
            
            long searchTime = System.currentTimeMillis() - startTime;
            System.out.println("✓ Performed 100 searches in " + searchTime + "ms");
            
            // カウントクエリのパフォーマンス
            startTime = System.currentTimeMillis();
            long totalCount = userRepository.countActiveUsers();
            long countTime = System.currentTimeMillis() - startTime;
            
            System.out.println("✓ Counted " + totalCount + " users in " + countTime + "ms");
        }
    }
    
    /**
     * 簡易アサーションメソッド群
     */
    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
    
    private static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message);
        }
    }
    
    private static void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + " - Expected: " + expected + ", Actual: " + actual);
        }
    }
    
    private static void assertNotNull(Object object, String message) {
        if (object == null) {
            throw new AssertionError(message);
        }
    }
    
    private static void fail(String message) {
        throw new AssertionError(message);
    }
    
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        System.out.println("Testcontainers Integration Testing Demonstration");
        System.out.println("================================================");
        
        DatabaseIntegrationTest test = new DatabaseIntegrationTest();
        
        try {
            test.setUp();
            
            test.testBasicCrudOperations();
            test.testUniqueConstraints();
            test.testComplexQueries();
            test.testTransactionBehavior();
            test.testPerformance();
            
            System.out.println("\n" + "=".repeat(50));
            System.out.println("🎉 All integration tests passed!");
            
            System.out.println("\n📊 Testcontainers Benefits:");
            System.out.println("✓ Real database behavior - no mocking limitations");
            System.out.println("✓ Isolated test environment - no shared state issues");
            System.out.println("✓ Multiple database engines - test with production DB");
            System.out.println("✓ Automatic cleanup - containers removed after tests");
            System.out.println("✓ Version consistency - same DB version as production");
            
            System.out.println("\n🔧 Testcontainers in Practice:");
            System.out.println("- Use for repository layer testing");
            System.out.println("- Test complex SQL queries and transactions");
            System.out.println("- Validate database migrations");
            System.out.println("- Integration testing with message queues");
            System.out.println("- End-to-end testing with external services");
            
        } catch (Exception e) {
            System.err.println("❌ Integration test failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                test.tearDown();
            } catch (SQLException e) {
                System.err.println("Warning: Failed to cleanup test resources: " + e.getMessage());
            }
        }
    }
}