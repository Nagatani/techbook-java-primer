// 型ごとに別々の処理を書く必要がある
public class UserSystemBefore {
    public static void main(String[] args) {
        RegularUser regular = new RegularUser("U001", "user1@example.com");
        AdminUser admin = new AdminUser("A001", "admin@example.com", "SuperAdmin");
        GuestUser guest = new GuestUser("session123");
        
        // それぞれの型に応じたリクエスト処理
        IO.println("=== 全ユーザーのリクエスト処理（ポリモーフィズムなし）===");
        
        // RegularUserの処理
        regular.processRequest("READ");
        
        // AdminUserの処理
        admin.processRequest("DELETE");
        
        // GuestUserの処理
        guest.processRequest("WRITE");
        
        IO.println();
        IO.println("=== アクセス権限チェック ===");
        IO.println("Regular user READ権限: " + checkAccessPermission(regular, null, null, "READ"));
        IO.println("Admin user DELETE権限: " + checkAccessPermission(null, admin, null, "DELETE"));
        IO.println("Guest user WRITE権限: " + checkAccessPermission(null, null, guest, "WRITE"));
        
        // 新しいユーザータイプを追加するたびに、ここにコードを追加する必要がある
        // PremiumUser premium = new PremiumUser("P001", "premium@example.com");
        // premium.processRequest("CREATE");
    }
    
    // アクセス権限を確認する場合も型別処理が必要
    public static boolean checkAccessPermission(RegularUser regular, AdminUser admin, GuestUser guest, String resource) {
        // 各型ごとに個別に処理
        if (regular != null) {
            return resource.equals("READ") || resource.equals("WRITE");
        }
        if (admin != null) {
            return true; // 管理者はすべてのリソースにアクセス可能
        }
        if (guest != null) {
            return resource.equals("READ"); // ゲストは読み取りのみ
        }
        
        // 新しい型が増えるたびに、このメソッドも修正が必要
        return false;
    }
}
