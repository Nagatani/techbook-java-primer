// 一つのクラスに責任を詰め込みすぎた例
public class UserManager {
    private String username;
    private String password;
    private String email;
    
    // データベース操作
    public void saveToDatabase() { /* ... */ }
    public void deleteFromDatabase() { /* ... */ }
    
    // メール送信
    public void sendWelcomeEmail() { /* ... */ }
    public void sendPasswordResetEmail() { /* ... */ }
    
    // ログイン処理
    public boolean authenticate(String password) { /* ... */ return false; }
    public void generateSession() { /* ... */ }
    
    // レポート生成
    public void generateUserReport() { /* ... */ }
    public void exportToCSV() { /* ... */ }
}
