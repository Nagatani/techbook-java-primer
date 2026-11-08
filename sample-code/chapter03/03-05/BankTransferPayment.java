public class BankTransferPayment implements PaymentMethod {  // ②
    private String accountNumber;
    
    public void processPayment(double amount) {  // ④
        System.out.println("銀行振込で" + amount + "円を送金しました");
    }
}
