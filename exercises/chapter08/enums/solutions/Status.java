/**
 * 基本的なEnum使用例
 * システムの状態を表現するEnum
 */
public enum Status {
    /** 待機中 */
    PENDING,
    /** 実行中 */
    RUNNING,
    /** 完了 */
    COMPLETED,
    /** 失敗 */
    FAILED,
    /** キャンセル */
    CANCELLED;
    
    /**
     * 次の状態に遷移可能かチェック
     * @param next 次の状態
     * @return 遷移可能な場合true
     */
    public boolean canTransitionTo(Status next) {
        return switch (this) {
            case PENDING -> next == RUNNING || next == CANCELLED;
            case RUNNING -> next == COMPLETED || next == FAILED || next == CANCELLED;
            case COMPLETED, FAILED, CANCELLED -> false;
        };
    }
    
    /**
     * 終了状態かどうかを判定
     * @return 終了状態の場合true
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }
    
    /**
     * 状態の日本語表示名を取得
     * @return 日本語表示名
     */
    public String getDisplayName() {
        return switch (this) {
            case PENDING -> "待機中";
            case RUNNING -> "実行中";
            case COMPLETED -> "完了";
            case FAILED -> "失敗";
            case CANCELLED -> "キャンセル";
        };
    }
}