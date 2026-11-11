import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class OptionalPracticalExample {
    // データベースの代わりとなるメモリ内ストレージ
    static class UserRepository {
        private Map<Long, User> users = new HashMap<>();
        
        public UserRepository() {
            users.put(1L, new User(1L, "Alice", "alice@example.com"));
            users.put(2L, new User(2L, "Bob", null));  // メールアドレスなし
        }
        
        // Optionalを返すことで、ユーザーが見つからない可能性を明示
        public Optional<User> findById(Long id) {
            return Optional.ofNullable(users.get(id));
        }
    }
    
    static class User {
        private Long id;
        private String name;
        private String email;
        
        public User(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
        
        public Long getId() { return id; }
        public String getName() { return name; }
        public Optional<String> getEmail() { 
            return Optional.ofNullable(email); 
        }
    }
    
    static class ConfigService {
        private Map<String, String> config = new HashMap<>();
        
        public ConfigService() {
            config.put("app.name", "MyApp");
            config.put("app.version", "1.0.0");
            // app.timeoutは設定されていない
        }
        
        // 設定値の取得（存在しない可能性を考慮）
        public Optional<String> getConfig(String key) {
            return Optional.ofNullable(config.get(key));
        }
        
        // タイムアウト値の取得（デフォルト値付き）
        public int getTimeout() {
            return getConfig("app.timeout")
                .map(Integer::parseInt)
                .orElse(30);  // デフォルト30秒
        }
    }
    
    public static void main(String[] args) {
        UserRepository repo = new UserRepository();
        ConfigService config = new ConfigService();
        
        // データベース検索の例
        Long userId = 1L;
        String emailDomain = repo.findById(userId)
            .flatMap(User::getEmail)
            .map(email -> email.substring(email.indexOf("@") + 1))
            .orElse("ドメインなし");
        
        IO.println("ユーザー " + userId + " のメールドメイン: " + emailDomain);
        
        // 存在しないユーザーの処理
        Long nonExistentId = 999L;
        repo.findById(nonExistentId)
            .ifPresentOrElse(
                user -> IO.println("ユーザー名: " + user.getName()),
                () -> IO.println("ユーザーID " + nonExistentId + " は存在しません")
            );
        
        // 設定値の取得
        String appName = config.getConfig("app.name")
            .orElse("Unknown App");
        IO.println("アプリケーション名: " + appName);
        
        int timeout = config.getTimeout();
        IO.println("タイムアウト値: " + timeout + "秒");
    }
}
