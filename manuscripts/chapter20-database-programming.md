# <b>20章</b> <span>データベースプログラミング</span> <small>JDBCで実現するデータ永続化</small>

## 本章の学習目標

### 前提知識

**必須前提**：
- 第14章の例外処理（SQLException等のデータベース例外処理）
- 第15章のファイルI/O（リソース管理、try-with-resources）
- 第10章のコレクションフレームワーク（ResultSetのデータ格納）
- 第4章のクラスとインスタンス（エンティティクラスの設計）

**望ましい前提**：
- SQLの基本知識（SELECT、INSERT、UPDATE、DELETE）
- リレーショナルデータベースの基本概念
- 第17章のネットワークプログラミング（接続の概念）
- デザインパターンの基礎（DAOパターン）

### 学習目標

#### 知識理解目標
- JDBCの役割とアーキテクチャの理解
- データベース接続の仕組みとコネクションプール
- PreparedStatementとSQL injectionのリスク
- トランザクション管理とACID特性

#### 技能習得目標
- JDBCを使用したデータベース接続の実装
- CRUD操作の実装（Create、Read、Update、Delete）
- PreparedStatementを使用した安全なSQL実行
- トランザクション制御の実装

#### 実践的な活用目標
- DAOパターンを使用したデータアクセス層の設計
- コネクションプールを使用した効率的な接続管理
- バッチ処理による大量データの効率的な処理
- 実践的なWebアプリケーションのデータ層実装

#### 到達レベルの指標
- データベースを使用したアプリケーションが設計・実装できる
- SQL injectionなどのセキュリティリスクを理解し対策できる
- トランザクションを適切に管理できる
- パフォーマンスを考慮したデータアクセス処理が実装できる

## 20.1 JDBCの基礎

### JDBCとは

JDBC（Java Database Connectivity）は、Javaプログラムからリレーショナルデータベースにアクセスするための標準APIです。JDBCを使用することで、データベースの種類に依存しない統一的な方法でデータベース操作を行うことができます。

### JDBCのアーキテクチャ

JDBCは以下の主要コンポーネントで構成されています：

1. **JDBC API**: Javaアプリケーションが使用するインターフェイス群
2. **JDBCドライバー**: 各データベース固有の実装
3. **DriverManager**: ドライバーの管理と接続の確立
4. **Connection**: データベースとの接続を表現
5. **Statement/PreparedStatement**: SQL文の実行
6. **ResultSet**: クエリ結果の保持

### 基本的な接続の流れ

<span class="listing-number">**サンプルコード20-1**</span>

```java
import java.sql.*;

public class BasicJDBCExample {
    public static void main(String[] args) {
        // データベース接続情報
        String url = "jdbc:mysql://localhost:3306/testdb";
        String username = "root";
        String password = "password";
        
        // try-with-resourcesで自動的にリソースをクローズ
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            System.out.println("データベースに接続しました");
            
            // データベースのメタデータを取得
            DatabaseMetaData metaData = conn.getMetaData();
            System.out.println("データベース製品: " + metaData.getDatabaseProductName());
            System.out.println("バージョン: " + metaData.getDatabaseProductVersion());
            
        } catch (SQLException e) {
            System.err.println("データベース接続エラー: " + e.getMessage());
        }
    }
}
```

## 20.2 基本的なCRUD操作

### テーブルの作成

まず、サンプルで使用するテーブルを作成します：

<span class="listing-number">**サンプルコード20-2**</span>

```java
public class CreateTableExample {
    private static final String CREATE_TABLE_SQL = """
        CREATE TABLE IF NOT EXISTS users (
            id INT PRIMARY KEY AUTO_INCREMENT,
            username VARCHAR(50) NOT NULL UNIQUE,
            email VARCHAR(100) NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        )
        """;
    
    public static void createTable(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(CREATE_TABLE_SQL);
            System.out.println("テーブルを作成しました");
        }
    }
}
```

### データの挿入（CREATE）

<span class="listing-number">**サンプルコード20-3**</span>

```java
public class InsertExample {
    private static final String INSERT_SQL = 
        "INSERT INTO users (username, email) VALUES (?, ?)";
    
    public static void insertUser(Connection conn, String username, String email) 
            throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL)) {
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            
            int rowsAffected = pstmt.executeUpdate();
            System.out.println(rowsAffected + "行を挿入しました");
        }
    }
}
```

### データの検索（READ）

<span class="listing-number">**サンプルコード20-4**</span>

```java
public class SelectExample {
    private static final String SELECT_ALL_SQL = "SELECT * FROM users";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM users WHERE id = ?";
    
    public static void selectAllUsers(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String email = rs.getString("email");
                Timestamp createdAt = rs.getTimestamp("created_at");
                
                System.out.printf("ID: %d, Username: %s, Email: %s, Created: %s%n",
                    id, username, email, createdAt);
            }
        }
    }
    
    public static User selectUserById(Connection conn, int userId) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getTimestamp("created_at")
                    );
                }
                return null;
            }
        }
    }
}
```

### データの更新（UPDATE）

<span class="listing-number">**サンプルコード20-5**</span>

```java
public class UpdateExample {
    private static final String UPDATE_SQL = 
        "UPDATE users SET email = ? WHERE username = ?";
    
    public static void updateUserEmail(Connection conn, String username, String newEmail) 
            throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL)) {
            pstmt.setString(1, newEmail);
            pstmt.setString(2, username);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("ユーザー情報を更新しました");
            } else {
                System.out.println("該当するユーザーが見つかりませんでした");
            }
        }
    }
}
```

### データの削除（DELETE）

<span class="listing-number">**サンプルコード20-6**</span>

```java
public class DeleteExample {
    private static final String DELETE_SQL = "DELETE FROM users WHERE id = ?";
    
    public static void deleteUser(Connection conn, int userId) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(DELETE_SQL)) {
            pstmt.setInt(1, userId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("ユーザーを削除しました");
            } else {
                System.out.println("該当するユーザーが見つかりませんでした");
            }
        }
    }
}
```

## 20.3 PreparedStatementとSQL Injection対策

### SQL Injectionの危険性

SQL Injectionは、悪意のあるSQL文をアプリケーションに実行させる攻撃手法です。以下は脆弱な例です：

<span class="listing-number">**サンプルコード20-7**</span>

```java
// 危険な例 - 絶対に使用しないでください！
public class VulnerableExample {
    public static void unsafeLogin(Connection conn, String username, String password) 
            throws SQLException {
        // ユーザー入力を直接SQL文に結合している（危険！）
        String sql = "SELECT * FROM users WHERE username = '" + username + 
                    "' AND password = '" + password + "'";
        
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                System.out.println("ログイン成功");
            } else {
                System.out.println("ログイン失敗");
            }
        }
    }
}
```

攻撃者が `username` に `admin' OR '1'='1' --` を入力すると、すべてのユーザーでログインできてしまいます。

### PreparedStatementによる安全な実装

<span class="listing-number">**サンプルコード20-8**</span>

```java
public class SecureExample {
    private static final String LOGIN_SQL = 
        "SELECT * FROM users WHERE username = ? AND password_hash = ?";
    
    public static boolean secureLogin(Connection conn, String username, String passwordHash) 
            throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(LOGIN_SQL)) {
            // パラメータバインディングにより、SQL Injectionを防ぐ
            pstmt.setString(1, username);
            pstmt.setString(2, passwordHash);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
```

## 20.4 トランザクション管理

### トランザクションの基本

トランザクションは、複数のデータベース操作を1つの作業単位として扱う仕組みです。ACID特性（Atomicity、Consistency、Isolation、Durability）を保証します。

<span class="listing-number">**サンプルコード20-9**</span>

```java
public class TransactionExample {
    public static void transferMoney(Connection conn, int fromAccountId, 
                                   int toAccountId, double amount) throws SQLException {
        // 自動コミットを無効化
        conn.setAutoCommit(false);
        
        try {
            // 送金元から減額
            deductBalance(conn, fromAccountId, amount);
            
            // 送金先に加算
            addBalance(conn, toAccountId, amount);
            
            // すべて成功したらコミット
            conn.commit();
            System.out.println("送金が完了しました");
            
        } catch (SQLException e) {
            // エラーが発生したらロールバック
            conn.rollback();
            System.err.println("送金に失敗しました: " + e.getMessage());
            throw e;
        } finally {
            // 自動コミットを元に戻す
            conn.setAutoCommit(true);
        }
    }
    
    private static void deductBalance(Connection conn, int accountId, double amount) 
            throws SQLException {
        String sql = "UPDATE accounts SET balance = balance - ? WHERE id = ? AND balance >= ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, accountId);
            pstmt.setDouble(3, amount);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("残高不足または口座が存在しません");
            }
        }
    }
    
    private static void addBalance(Connection conn, int accountId, double amount) 
            throws SQLException {
        String sql = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setInt(2, accountId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("送金先口座が存在しません");
            }
        }
    }
}
```

## 20.5 DAOパターンの実装

### DAOパターンとは

DAO（Data Access Object）パターンは、データアクセスロジックをビジネスロジックから分離するデザインパターンです。

### エンティティクラス

<span class="listing-number">**サンプルコード20-10**</span>

```java
import java.sql.Timestamp;

public class User {
    private int id;
    private String username;
    private String email;
    private Timestamp createdAt;
    
    // コンストラクタ
    public User(int id, String username, String email, Timestamp createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.createdAt = createdAt;
    }
    
    // IDなしのコンストラクタ（新規作成用）
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
    
    // getterとsetter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    @Override
    public String toString() {
        return String.format("User[id=%d, username=%s, email=%s, createdAt=%s]",
            id, username, email, createdAt);
    }
}
```

### DAOインターフェイス

<span class="listing-number">**サンプルコード20-11**</span>

```java
import java.util.List;
import java.util.Optional;

public interface UserDao {
    // ユーザーを作成
    User create(User user) throws SQLException;
    
    // IDでユーザーを検索
    Optional<User> findById(int id) throws SQLException;
    
    // ユーザー名でユーザーを検索
    Optional<User> findByUsername(String username) throws SQLException;
    
    // すべてのユーザーを取得
    List<User> findAll() throws SQLException;
    
    // ユーザー情報を更新
    boolean update(User user) throws SQLException;
    
    // ユーザーを削除
    boolean delete(int id) throws SQLException;
}
```

### DAO実装クラス

<span class="listing-number">**サンプルコード20-12**</span>

```java
import java.sql.*;
import java.util.*;

public class UserDaoImpl implements UserDao {
    private final Connection connection;
    
    public UserDaoImpl(Connection connection) {
        this.connection = connection;
    }
    
    @Override
    public User create(User user) throws SQLException {
        String sql = "INSERT INTO users (username, email) VALUES (?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("ユーザーの作成に失敗しました");
            }
            
            // 自動生成されたIDを取得
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                    user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                    return user;
                } else {
                    throw new SQLException("IDの取得に失敗しました");
                }
            }
        }
    }
    
    @Override
    public Optional<User> findById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
                return Optional.empty();
            }
        }
    }
    
    @Override
    public Optional<User> findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
                return Optional.empty();
            }
        }
    }
    
    @Override
    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        List<User> users = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        
        return users;
    }
    
    @Override
    public boolean update(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, email = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setInt(3, user.getId());
            
            return pstmt.executeUpdate() > 0;
        }
    }
    
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }
    
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("id"),
            rs.getString("username"),
            rs.getString("email"),
            rs.getTimestamp("created_at")
        );
    }
}
```

### DAOの使用例

<span class="listing-number">**サンプルコード20-13**</span>

```java
public class DaoExample {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/testdb";
        String username = "root";
        String password = "password";
        
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            UserDao userDao = new UserDaoImpl(conn);
            
            // ユーザーを作成
            User newUser = new User("tanaka", "tanaka@example.com");
            User createdUser = userDao.create(newUser);
            System.out.println("作成されたユーザー: " + createdUser);
            
            // ユーザーを検索
            Optional<User> foundUser = userDao.findById(createdUser.getId());
            foundUser.ifPresent(user -> 
                System.out.println("検索結果: " + user));
            
            // ユーザー情報を更新
            createdUser.setEmail("tanaka.new@example.com");
            if (userDao.update(createdUser)) {
                System.out.println("ユーザー情報を更新しました");
            }
            
            // すべてのユーザーを表示
            System.out.println("\nすべてのユーザー:");
            List<User> allUsers = userDao.findAll();
            allUsers.forEach(System.out::println);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

## 20.6 コネクションプール

### コネクションプールの必要性

データベース接続の確立は時間のかかる処理です。アプリケーションの性能を向上させるため、接続を再利用するコネクションプールを使用します。

### HikariCPの使用例

HikariCPは高性能なコネクションプールライブラリです：

<span class="listing-number">**サンプルコード20-14**</span>

```java
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

public class ConnectionPoolExample {
    private static HikariDataSource dataSource;
    
    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/testdb");
        config.setUsername("root");
        config.setPassword("password");
        
        // コネクションプールの設定
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        
        dataSource = new HikariDataSource(config);
    }
    
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    public static void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
```

### コネクションプールを使用したDAO

<span class="listing-number">**サンプルコード20-15**</span>

```java
public class PooledDaoExample {
    public static void main(String[] args) {
        try {
            // コネクションプールから接続を取得
            try (Connection conn = ConnectionPoolExample.getConnection()) {
                UserDao userDao = new UserDaoImpl(conn);
                
                // 複数のユーザーを作成
                for (int i = 1; i <= 100; i++) {
                    User user = new User("user" + i, "user" + i + "@example.com");
                    userDao.create(user);
                }
                
                System.out.println("100人のユーザーを作成しました");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // アプリケーション終了時にプールを閉じる
            ConnectionPoolExample.close();
        }
    }
}
```

## 20.7 バッチ処理

### 大量データの効率的な処理

大量のデータを処理する場合、バッチ処理を使用すると性能が大幅に向上します：

<span class="listing-number">**サンプルコード20-16**</span>

```java
public class BatchProcessingExample {
    public static void batchInsertUsers(Connection conn, List<User> users) 
            throws SQLException {
        String sql = "INSERT INTO users (username, email) VALUES (?, ?)";
        
        // 自動コミットを無効化
        conn.setAutoCommit(false);
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (User user : users) {
                pstmt.setString(1, user.getUsername());
                pstmt.setString(2, user.getEmail());
                pstmt.addBatch();
                
                // 1000件ごとに実行
                if (users.indexOf(user) % 1000 == 0) {
                    pstmt.executeBatch();
                }
            }
            
            // 残りのバッチを実行
            pstmt.executeBatch();
            conn.commit();
            
            System.out.println(users.size() + "件のデータを挿入しました");
            
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }
}
```

## より深い理解のために

本章で学んだデータベースプログラミングをさらに深く理解したい方は、GitHubリポジトリの付録資料を参照してください：

付録リソース: `https://github.com/Nagatani/techbook-java-primer/tree/main/appendix/b20-database-advanced/`

この付録では以下の高度なトピックを扱います：

- ストアドプロシージャとファンクション: データベース側での処理の実装
- インデックスとクエリ最適化: パフォーマンスチューニング
- NoSQLデータベース: MongoDB、Redisとの連携
- ORMフレームワーク: Hibernate、MyBatisの基礎
- データベースマイグレーション: Flywayを使用したスキーマ管理

これらの技術は、エンタープライズアプリケーション開発において重要な役割を果たします。

## まとめ

本章では、JavaのJDBCを使用したデータベースプログラミングの基礎を学習しました：

- JDBC: Javaからデータベースにアクセスするための標準API
- CRUD操作: Create、Read、Update、Deleteの基本操作
- PreparedStatement: SQL Injectionを防ぐ安全なSQL実行
- トランザクション: 複数の操作を1つの作業単位として扱う
- DAOパターン: データアクセスロジックの分離と再利用
- コネクションプール: 接続の再利用による性能向上
- バッチ処理: 大量データの効率的な処理

これらの技術を組み合わせることで、実践的なデータベースアプリケーションを開発できます。

## 章末演習

本章で学んだデータベースプログラミングを実践的な課題で確認しましょう。

### 演習課題へのアクセス

本書の演習課題は、以下のGitHubリポジトリで提供されています：

リポジトリ: `https://github.com/Nagatani/techbook-java-primer/tree/main/exercises`

### 第20章の課題構成

```
exercises/chapter20/
├── basic/              # 基礎課題（必須）
│   ├── README.md       # 詳細な課題説明
│   └── DatabaseBasics.java
├── advanced/           # 発展課題（推奨）
├── challenge/          # チャレンジ課題（任意）
└── solutions/          # 解答例（実装後に参照）
```

### 学習の目標

本章の演習を通じて以下のスキルを習得します：
- JDBCを使用した基本的なデータベース操作
- DAOパターンの実装と活用
- トランザクション管理の実践

### 課題の概要

1. **基礎課題**: 書籍管理システムのデータアクセス層実装
2. **発展課題**: 在庫管理システムのトランザクション処理
3. **チャレンジ課題**: マルチテナントアプリケーションのデータ分離

詳細な課題内容と実装のヒントは、GitHubリポジトリの各課題フォルダ内のREADME.mdを参照してください。

次のステップ: 基礎課題が完了したら、第21章「高度なGUIコンポーネント」に進みましょう。