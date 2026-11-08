import java.math.BigDecimal;

// PayPal決済の実装
public class PayPalPayment extends PaymentProcessor {
    private String email;
    private String password;
    
    public PayPalPayment(String transactionId, BigDecimal amount, 
                        String currency, String email, String password) {
        super(transactionId, amount, currency);
        this.email = email;
        this.password = password;
    }
    
    @Override
    protected boolean validatePaymentSpecific() {
        // PayPal固有の検証
        if (email == null || !email.contains("@")) {
            setErrorMessage("無効なメールアドレスです");
            return false;
        }
        if (password == null || password.length() < 8) {
            setErrorMessage("パスワードが短すぎます");
            return false;
        }
        return true;
    }
    
    @Override
    protected String callExternalAPI() {
        // PayPal API呼び出しのシミュレーション
        System.out.println("PayPal APIを呼び出し中...");
        
        // 実際の実装では外部APIを呼び出す
        // エラーが発生した場合はnullを返す
        if (email.equals("error@example.com")) {  // テスト用のエラー条件
            setErrorMessage("PayPalアカウントが一時的に利用できません");
            return null;
        }
        
        return "PP-" + System.currentTimeMillis();
    }
    
    @Override
    protected void logTransaction(String authCode) {
        // PayPalでは詳細なログを記録
        super.logTransaction(authCode);
        System.out.println("PayPalアカウント: " + email);
    }
}
