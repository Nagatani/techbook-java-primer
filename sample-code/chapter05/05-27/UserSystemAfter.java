// 統一的な処理で全ての型を扱える
public class UserSystemAfter {
    public static void main(String[] args) {
        // ポリモーフィズム：親クラスの型で管理
        User[] users = {
            new RegularUser("U001", "user1@example.com"),
            new AdminUser("A001", "admin@example.com", "SuperAdmin"),
            new GuestUser("session123")
            // 新しいユーザータイプを追加しても、以下のコードは変更不要
            // new PremiumUser("P001", "premium@example.com")
        };
        
        IO.println("=== 全ユーザーのリクエスト処理（ポリモーフィズムあり）===");
        
        // 統一的な処理で全員を扱える
        for (User user : users) {
            user.processRequest("READ"); // 実際の型に応じた処理が自動的に呼ばれる
        }
        
        IO.println();
        IO.println("=== DELETE権限のチェック ===");
        checkAccessPermission(users, "DELETE");
        
        IO.println();
        IO.println("=== 通知の送信 ===");
        sendNotification(users, "システムメンテナンスのお知らせ");
    }
    
    // アクセス権限チェックも拡張性が高い
    public static boolean checkAccessPermission(User[] users, String resource) {
        boolean hasPermission = true;
        
        // 型を意識せずに処理できる
        for (User user : users) {
            if (!user.hasPermission(resource)) {
                hasPermission = false;
                IO.println(user.getUserId() + " には " + resource + " 権限がありません");
            }
        }
        
        return hasPermission;
    }
    
    // 特定の条件での処理も簡潔に書ける
    public static void sendNotification(User[] users, String message) {
        for (User user : users) {
            if (user.isActive()) {
                user.notify(message);
                IO.println(user.getUserId() + " に通知を送信しました");
            }
        }
    }
}
