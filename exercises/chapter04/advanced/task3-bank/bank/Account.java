package bank;

/**
 * 銀行口座。残高を変えるメソッドはパッケージプライベートにし、Bankを通してしか変えられないようにする
 */
public class Account {
    private static final int MAX_HISTORY = 50;

    private final String number;
    private final String owner;
    private final int openingBalance;
    private int balance;
    private Transaction[] history = new Transaction[MAX_HISTORY];
    private int historyCount;

    // コンストラクターもパッケージプライベート。口座はBank.openで開く
    Account(String number, String owner, int openingBalance) {
        if (number == null || number.isBlank() || owner == null || owner.isBlank()) {
            throw new IllegalArgumentException("口座番号と名義を指定してください");
        }
        if (openingBalance < 0) {
            throw new IllegalArgumentException("最初の入金額は0円以上にしてください");
        }
        this.number = number;
        this.owner = owner;
        this.openingBalance = openingBalance;
        this.balance = openingBalance;
    }

    public String getNumber() {
        return number;
    }

    public String getOwner() {
        return owner;
    }

    public int getBalance() {
        return balance;
    }

    int getOpeningBalance() {
        return openingBalance;
    }

    // 以下の3つはBankだけが呼ぶ
    void withdraw(int amount) {
        balance -= amount;
    }

    void deposit(int amount) {
        balance += amount;
    }

    // 履歴は追加するだけで、消したり書き換えたりするメソッドは作らない
    void addHistory(Transaction transaction) {
        if (historyCount < history.length) {
            history[historyCount] = transaction;
            historyCount++;
        }
    }

    public void showHistory() {
        IO.println(number + "（" + owner + "）の取引:");
        IO.println("  開設          +" + openingBalance + "円");
        for (int i = 0; i < historyCount; i++) {
            Transaction t = history[i];
            if (t.getFrom() == this) {
                IO.println("  " + t.getId() + " 振込送金 -" + (t.getAmount() + t.getFee())
                        + "円（" + t.getTo().getOwner() + "様へ、手数料" + t.getFee() + "円を含む）");
            } else {
                IO.println("  " + t.getId() + " 振込受取 +" + t.getAmount()
                        + "円（" + t.getFrom().getOwner() + "様から）");
            }
        }
        IO.println("  残高 " + balance + "円");
    }
}
