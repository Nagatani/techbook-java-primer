package bank;

/**
 * 1件の振込。作ったあとは値を変えられない（フィールドはすべてfinal、setterなし）
 */
public class Transaction {
    private final String id;
    private final Account from;
    private final Account to;
    private final int amount;
    private final int fee;

    // パッケージプライベート: Bankだけが取引を作れる
    Transaction(String id, Account from, Account to, int amount, int fee) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.fee = fee;
    }

    public String getId() {
        return id;
    }

    public Account getFrom() {
        return from;
    }

    public Account getTo() {
        return to;
    }

    public int getAmount() {
        return amount;
    }

    public int getFee() {
        return fee;
    }
}
