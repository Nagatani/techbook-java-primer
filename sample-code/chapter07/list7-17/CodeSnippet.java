/**
 * リスト7-17
 * コードスニペット
 * 
 * 元ファイル: chapter07-abstract-classes-and-interfaces.md (653行目)
 */

// 悪い例：肥大化したインターフェイス
interface BadUserService {
    void createUser(User user);
    void deleteUser(String id);
    void updateUser(User user);
    User findUser(String id);
    List<User> findAllUsers();
    void sendEmail(String userId, String message);
    void generateReport(String userId);
    void backupUserData(String userId);
}

// 良い例：責務ごとに分離されたインターフェイス
interface UserRepository {
    void save(User user);
    void delete(String id);
    User findById(String id);
    List<User> findAll();
}

interface UserNotificationService {
    void sendEmail(String userId, String message);
    
    default void sendWelcomeEmail(String userId) {
        sendEmail(userId, "ようこそ！サービスへの登録ありがとうございます。");
    }
    
    default void sendPasswordResetEmail(String userId, String resetLink) {
        sendEmail(userId, "パスワードリセット: " + resetLink);
    }
}

interface UserReportService {
    void generateReport(String userId);
    
    default void generateMonthlyReport(String userId) {
        System.out.println("月次レポートを生成中: " + userId);
        generateReport(userId);
    }
    
    default void generateYearlyReport(String userId) {
        System.out.println("年次レポートを生成中: " + userId);
        generateReport(userId);
    }
}

// 必要な機能だけを実装
class BasicUserService implements UserRepository, UserNotificationService {
    private Map<String, User> users = new HashMap<>();
    
    @Override
    public void save(User user) {
        users.put(user.getId(), user);
        sendWelcomeEmail(user.getId());  // defaultメソッドを活用
    }
    
    @Override
    public void delete(String id) {
        users.remove(id);
    }
    
    @Override
    public User findById(String id) {
        return users.get(id);
    }
    
    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
    
    @Override
    public void sendEmail(String userId, String message) {
        System.out.println("メール送信 to " + userId + ": " + message);
    }
}