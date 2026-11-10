// 本文では省略している補助クラス（共通の親クラスを持たない3種類の利用者）
class RegularUser {
    private final String userId;
    private final String email;

    RegularUser(String userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    void processRequest(String action) {
        IO.println("ユーザー " + userId + " が " + action + " をリクエストしました");
    }
}

class AdminUser {
    private final String userId;
    private final String email;
    private final String role;

    AdminUser(String userId, String email, String role) {
        this.userId = userId;
        this.email = email;
        this.role = role;
    }

    void processRequest(String action) {
        IO.println("管理者権限での処理: " + action);
        IO.println("セキュリティチェックを実行中...");
    }
}

class GuestUser {
    private final String sessionId;

    GuestUser(String sessionId) {
        this.sessionId = sessionId;
    }

    void processRequest(String action) {
        IO.println("ユーザー guest_" + sessionId + " が " + action + " をリクエストしました");
        IO.println("ゲストユーザーのため、一部機能が制限されています");
    }
}
