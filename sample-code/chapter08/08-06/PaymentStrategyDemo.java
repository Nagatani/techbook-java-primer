public class PaymentStrategyDemo {
    public static void main(String[] args) {
        IO.println("支払い方法のテスト:");
        for (PaymentStrategy strategy : PaymentStrategy.values()) {
            strategy.pay(1500);
        }
    }
}
