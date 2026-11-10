// 本文では省略している子クラス
class RegularUser extends User {
    RegularUser(String userId, String email) {
        super(userId, email);
    }

    @Override
    public boolean hasPermission(String resource) {
        return resource.equals("READ") || resource.equals("WRITE");
    }
}

class AdminUser extends User {
    private final String role;

    AdminUser(String userId, String email, String role) {
        super(userId, email);
        this.role = role;
    }

    @Override
    public void processRequest(String action) {
        IO.println("管理者権限での処理: " + action);
        IO.println("セキュリティチェックを実行中...");
    }

    @Override
    public boolean hasPermission(String resource) {
        return true; // 管理者はすべてのリソースにアクセス可能
    }
}

class GuestUser extends User {
    GuestUser(String sessionId) {
        super("guest_" + sessionId, null);
    }

    @Override
    public void processRequest(String action) {
        super.processRequest(action);
        IO.println("ゲストユーザーのため、一部機能が制限されています");
    }
}
