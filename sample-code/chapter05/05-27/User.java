// 親クラスに共通インターフェイスを定義
public class User {
    protected String userId;
    protected String email;
    
    public User(String userId, String email) {
        this.userId = userId;
        this.email = email;
    }
    
    public void processRequest(String action) {
        IO.println("ユーザー " + userId + " が " + action + " をリクエストしました");
    }
    
    public boolean hasPermission(String resource) {
        // デフォルトでは読み取り権限のみ
        return resource.equals("READ");
    }
    
    public void notify(String message) {
        // 通知を送信
        IO.println(userId + " への通知: " + message);
    }
    
    public boolean isActive() {
        return true; // デフォルトではアクティブ
    }
    
    public String getUserId() { return userId; }
    public String getEmail() { return email; }
}
