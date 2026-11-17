public class UserManagementSystem {
    public static void main(String[] args) {
        // 親クラスの配列に、さまざまな子クラスのインスタンスを格納できる
        User[] users = new User[3];
        users[0] = new RegularUser("U001", "user1@example.com");
        users[1] = new AdminUser("A001", "admin@example.com",
                "SuperAdmin");
        users[2] = new GuestUser("session123");

        // 全ユーザーのリクエストを処理
        for (User user : users) {
            // user変数の型はUserだが、実行時には
            // 実際のインスタンスのprocessRequest()が呼び出される
            user.processRequest("READ"); 
        }
    }
}
