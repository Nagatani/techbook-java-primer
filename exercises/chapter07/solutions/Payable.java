package chapter07.solutions;

/**
 * 支払い可能なオブジェクトを表すインターフェース
 * 
 * このインターフェースは、給与計算システムで使用される
 * 支払い対象を統一的に扱うためのものです。
 */
public interface Payable {
    
    /**
     * 支払い金額を計算する
     * 
     * @return 支払い金額
     */
    double getPaymentAmount();
    
    /**
     * 支払い対象の名前を取得する
     * 
     * @return 支払い対象の名前
     */
    String getPaymentName();
    
    /**
     * 支払い対象の説明を取得する
     * 
     * @return 支払い対象の説明
     */
    default String getPaymentDescription() {
        return "支払い対象: " + getPaymentName() + " (金額: " + getPaymentAmount() + "円)";
    }
}