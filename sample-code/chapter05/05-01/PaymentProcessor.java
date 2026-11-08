import java.math.BigDecimal;

// 決済処理の基底クラス：テンプレートメソッドパターンの実装
public class PaymentProcessor {
    protected String transactionId;
    protected BigDecimal amount;
    protected String currency;
    protected String errorMessage; // エラーメッセージを保持するフィールド
    
    public PaymentProcessor(String transactionId, BigDecimal amount, String currency) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.currency = currency;
        this.errorMessage = null;
    }
    
    // テンプレートメソッド：決済処理の共通フロー
    public final PaymentResult processPayment() {
        // 1. 事前検証（共通処理）
        if (!validateCommonParameters()) {
            logError();
            return new PaymentResult(false, null, errorMessage);
        }
        
        // 2. 決済手段固有の検証（子クラスでオーバーライド可能）
        if (!validatePaymentSpecific()) {
            logError();
            return new PaymentResult(false, null, errorMessage);
        }
        
        // 3. 外部API呼び出し（子クラスでオーバーライド可能）
        String authorizationCode = callExternalAPI();
        if (authorizationCode == null) {
            logError();
            return new PaymentResult(false, null, errorMessage);
        }
        
        // 4. 処理結果の記録（共通処理）
        logTransaction(authorizationCode);
        
        return new PaymentResult(true, authorizationCode, "決済完了");
    }
    
    // 共通の検証ロジック（boolean返却、エラーメッセージはフィールドに設定）
    private boolean validateCommonParameters() {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            errorMessage = "金額は正の値である必要があります";
            return false;
        }
        if (amount.compareTo(new BigDecimal("1000000")) > 0) {
            errorMessage = "決済上限額を超えています";
            return false;
        }
        return true;
    }
    
    // 子クラスでオーバーライドされることを想定したメソッド（デフォルト実装を提供）
    protected boolean validatePaymentSpecific() {
        // デフォルトでは追加の検証なし
        // 子クラスでオーバーライド可能
        return true;
    }
    
    protected String callExternalAPI() {
        // デフォルトでは基本的な決済処理を実行
        // 実際の実装では外部決済サービスのAPIを呼び出す
        // エラーの場合はnullを返し、errorMessageを設定
        return "DEFAULT-" + System.currentTimeMillis();
    }
    
    // フックメソッド：子クラスでオーバーライド可能
    protected void logTransaction(String authCode) {
        System.out.println("取引ID: " + transactionId + " 承認コード: " + authCode);
    }
    
    protected void logError() {
        System.err.println("決済エラー[" + transactionId + "]: " + errorMessage);
    }
    
    // エラーメッセージを設定するヘルパーメソッド（子クラスから利用可能）
    protected void setErrorMessage(String message) {
        this.errorMessage = message;
    }
}

// 注意：このエラー処理の設計は単一スレッド環境を想定しています。
// 並行処理環境では、errorMessageフィールドの共有により問題が生じる可能性があります。
