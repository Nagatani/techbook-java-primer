// 責任を分離したクラス設計
public class User {
    private String username;
    private String password;
    private String email;
    
    // コンストラクター、getter、setter
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
    
    // 基本的なユーザー操作のみ
    public boolean validatePassword(String password) {
        return this.password.equals(password);
    }
}
