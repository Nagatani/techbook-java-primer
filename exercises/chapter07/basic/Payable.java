package chapter07.basic;

/**
 * 支払い可能なオブジェクトを表すインターフェイス
 * 
 * このインターフェイスを実装するクラスは、支払い金額を計算する機能を提供する必要があります。
 * 例：従業員、請求書、契約書など
 */
public interface Payable {
    /**
     * 支払い金額を計算する
     * TODO: 実装クラスでこのメソッドを実装してください
     * @return 支払い金額
     */
    double calculatePayment();
    
    /**
     * 支払い情報を表示する
     * デフォルト実装を提供していますが、必要に応じてオーバーライドできます
     */
    default void displayPaymentInfo() {
        System.out.println("支払い金額: " + calculatePayment() + "円");
    }
}