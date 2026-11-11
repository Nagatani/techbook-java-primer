public class CreditCardPayment implements PaymentMethod {  // ②
    private String cardNumber;
    
    public void processPayment(double amount) {  // ③
        IO.println("クレジットカードで" + amount + "円を決済しました");
    }
}
