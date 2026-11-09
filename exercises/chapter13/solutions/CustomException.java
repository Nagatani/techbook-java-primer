/**
 * カスタム例外クラス
 * アプリケーション固有の例外処理を実装
 */
public class CustomException extends Exception {
    
    /**
     * エラーコード
     */
    public enum ErrorCode {
        /** 無効な入力 */
        INVALID_INPUT("E001", "無効な入力です"),
        /** データが見つからない */
        DATA_NOT_FOUND("E002", "データが見つかりません"),
        /** 権限不足 */
        PERMISSION_DENIED("E003", "権限が不足しています"),
        /** システムエラー */
        SYSTEM_ERROR("E004", "システムエラーが発生しました"),
        /** リソース不足 */
        RESOURCE_EXHAUSTED("E005", "リソースが不足しています");
        
        private final String code;
        private final String message;
        
        ErrorCode(String code, String message) {
            this.code = code;
            this.message = message;
        }
        
        public String getCode() { return code; }
        public String getMessage() { return message; }
    }
    
    private final ErrorCode errorCode;
    private final String details;
    private final long timestamp;
    
    /**
     * エラーコードのみを指定するコンストラクタ
     * @param errorCode エラーコード
     */
    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.details = null;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * エラーコードと詳細メッセージを指定するコンストラクタ
     * @param errorCode エラーコード
     * @param details 詳細メッセージ
     */
    public CustomException(ErrorCode errorCode, String details) {
        super(errorCode.getMessage() + (details != null ? ": " + details : ""));
        this.errorCode = errorCode;
        this.details = details;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * エラーコードと原因例外を指定するコンストラクタ
     * @param errorCode エラーコード
     * @param cause 原因例外
     */
    public CustomException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.details = null;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * エラーコード、詳細メッセージ、原因例外を指定するコンストラクタ
     * @param errorCode エラーコード
     * @param details 詳細メッセージ
     * @param cause 原因例外
     */
    public CustomException(ErrorCode errorCode, String details, Throwable cause) {
        super(errorCode.getMessage() + (details != null ? ": " + details : ""), cause);
        this.errorCode = errorCode;
        this.details = details;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * エラーコードを取得
     * @return エラーコード
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    /**
     * 詳細メッセージを取得
     * @return 詳細メッセージ
     */
    public String getDetails() {
        return details;
    }
    
    /**
     * 例外発生時刻を取得
     * @return タイムスタンプ
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * 詳細な例外情報を取得
     * @return 詳細情報
     */
    public String getDetailedMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(errorCode.getCode()).append("] ");
        sb.append(getMessage());
        sb.append(" (発生時刻: ").append(new java.util.Date(timestamp)).append(")");
        
        if (getCause() != null) {
            sb.append("\n原因: ").append(getCause().getClass().getSimpleName());
            sb.append(": ").append(getCause().getMessage());
        }
        
        return sb.toString();
    }
    
    /**
     * 特定の種類の例外かどうかを判定
     * @param targetErrorCode 対象のエラーコード
     * @return 該当する場合true
     */
    public boolean isErrorType(ErrorCode targetErrorCode) {
        return this.errorCode == targetErrorCode;
    }
    
    /**
     * システムエラーかどうかを判定
     * @return システムエラーの場合true
     */
    public boolean isSystemError() {
        return errorCode == ErrorCode.SYSTEM_ERROR || 
               errorCode == ErrorCode.RESOURCE_EXHAUSTED;
    }
    
    /**
     * ユーザーエラーかどうかを判定
     * @return ユーザーエラーの場合true
     */
    public boolean isUserError() {
        return errorCode == ErrorCode.INVALID_INPUT || 
               errorCode == ErrorCode.DATA_NOT_FOUND || 
               errorCode == ErrorCode.PERMISSION_DENIED;
    }
    
    /**
     * 例外チェーンを辿って特定の例外を検索
     * @param exceptionClass 検索する例外クラス
     * @return 見つかった例外、見つからない場合null
     */
    @SuppressWarnings("unchecked")
    public <T extends Throwable> T findCauseByType(Class<T> exceptionClass) {
        Throwable current = this;
        while (current != null) {
            if (exceptionClass.isInstance(current)) {
                return (T) current;
            }
            current = current.getCause();
        }
        return null;
    }
    
    /**
     * 例外の重要度を取得
     * @return 重要度（1:低, 2:中, 3:高）
     */
    public int getSeverity() {
        return switch (errorCode) {
            case INVALID_INPUT, DATA_NOT_FOUND -> 1;
            case PERMISSION_DENIED -> 2;
            case SYSTEM_ERROR, RESOURCE_EXHAUSTED -> 3;
        };
    }
}