package chapter07.solutions;

/**
 * 支払い金額を計算できるもの（課題2の解答例）
 *
 * 従業員と請求書は継承の関係にないが、どちらも「支払い金額を計算できる」という共通の約束を持つ。
 */
public interface Payable {
    double calculatePayment();

    // デフォルトメソッド。実装するクラスは、書き直さなくてもこのメソッドを使える
    default void displayPaymentInfo() {
        IO.println("支払い金額: " + "%,.0f".formatted(calculatePayment()) + "円");
    }
}
