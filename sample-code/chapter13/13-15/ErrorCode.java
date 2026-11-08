public enum ErrorCode {
    // アカウント関連エラー
    ACC_001("ACC-001", "残高が不足しています"),
    ACC_002("ACC-002", "アカウントが凍結されています"),
    ACC_003("ACC-003", "取引限度額を超えています"),
    
    // 認証関連エラー
    AUTH_001("AUTH-001", "認証に失敗しました"),
    AUTH_002("AUTH-002", "セッションの有効期限が切れました"),
    AUTH_003("AUTH-003", "アクセス権限がありません"),
    
    // システムエラー
    SYS_001("SYS-001", "システムエラーが発生しました"),
    SYS_002("SYS-002", "サービスが一時的に利用できません"),
    SYS_003("SYS-003", "データベース接続エラー");
    
    private final String code;
    private final String defaultMessage;
    
    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getMessage() {
        return defaultMessage;
    }
    
    // メッセージテンプレートを使用したエラーメッセージ生成
    public String formatMessage(Object... args) {
        return String.format(defaultMessage, args);
    }
}
