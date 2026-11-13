public record BankAccount(String accountNumber, double balance) {
    // コンパクトコンストラクター
    public BankAccount {
        // 暗黙的にフィールドへの代入が行われる前に実行される
        if (accountNumber == null || accountNumber.isBlank()) {
            throw new IllegalArgumentException("口座番号は必須です");
        }
        if (balance < 0) {
            throw new IllegalArgumentException("残高は0以上でなければなりません");
        }
        // フィールドへの代入は自動的に行われる
    }
}
