// 型ごとに別々の処理を書く必要がある
public class UserSystemBefore {
    public static void main(String[] args) {
        RegularUser regular =
                new RegularUser("U001", "user1@example.com");
        AdminUser admin = new AdminUser("A001", "admin@example.com",
                "SuperAdmin");
        GuestUser guest = new GuestUser("session123");
        
        // それぞれの型に応じたリクエスト処理
        IO.println("=== 全ユーザーのリクエスト処理（ポリモーフィズムなし）===");
        regular.processRequest("READ");
        admin.processRequest("READ");
        guest.processRequest("READ");
        // 新しいユーザータイプを追加するたびに、ここにコードを追加する
        // PremiumUser premium =
        //         new PremiumUser("P001", "premium@example.com");
        // premium.processRequest("READ");
        
        IO.println();
        IO.println("=== DELETE権限のチェック ===");
        checkAccessPermission(regular, "DELETE");
        checkAccessPermission(admin, "DELETE");
        checkAccessPermission(guest, "DELETE");
    }
    
    // アクセス権限の確認も、型ごとに別のメソッドを用意する
    public static void checkAccessPermission(RegularUser user,
            String resource) {
        if (!(resource.equals("READ") || resource.equals("WRITE"))) {
            IO.println(user.getUserId() + " には " + resource
                    + " 権限がありません");
        }
    }
    
    public static void checkAccessPermission(AdminUser user,
            String resource) {
        // 管理者はすべてのリソースにアクセス可能
    }
    
    public static void checkAccessPermission(GuestUser user,
            String resource) {
        if (!resource.equals("READ")) {
            IO.println(user.getUserId() + " には " + resource
                    + " 権限がありません");
        }
    }
    // 新しい型が増えるたびに、このメソッドも1つ増える
}
