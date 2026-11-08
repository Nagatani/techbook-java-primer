public class SecurityManager {
    // テンプレートメソッドパターンでfinalを使用
    public final boolean authenticate(String username, String password) {
        // 認証の基本フローは変更させない
        if (!validateInput(username, password)) {
            return false;
        }
        
        // 具体的な認証方法はサブクラスで実装
        return doAuthenticate(username, password);
    }
    
    // サブクラスでオーバーライド可能
    protected boolean doAuthenticate(String username, String password) {
        // デフォルト実装
        return false;
    }
    
    private boolean validateInput(String username, String password) {
        return username != null && !username.isEmpty() 
            && password != null && !password.isEmpty();
    }
}
