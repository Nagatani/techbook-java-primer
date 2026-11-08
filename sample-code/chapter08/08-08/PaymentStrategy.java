public enum PaymentStrategy {
    CREDIT_CARD {
        @Override
        public void pay(double amount) {
            System.out.println("クレジットカードで" + amount + "円を支払いました");
        }
    },
    BANK_TRANSFER {
        @Override
        public void pay(double amount) {
            System.out.println("銀行振込で" + amount + "円を支払いました");
        }
    },
    CASH {
        @Override
        public void pay(double amount) {
            System.out.println("現金で" + amount + "円を支払いました");
        }
    };
    
    public abstract void pay(double amount);
}
