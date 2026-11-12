/**
 * 通帳。取引を順に記帳し、残高を計算する
 */
public class Passbook {

    /**
     * 通帳の1行。通帳と一緒にしか使わないので、入れ子のレコードにする
     * （入れ子のレコードは暗黙にstaticになる）
     */
    public record Entry(Transaction transaction, int fee, int balance, boolean accepted) {
    }

    private final String owner;
    private final Entry[] entries;
    private int count;
    private int balance;

    public Passbook(String owner, int capacity) {
        this.owner = owner;
        this.entries = new Entry[capacity];
    }

    // 取引を記帳する。手数料を含めて残高が足りなければ、取引を断って記録だけ残す
    public Entry record(Transaction t) {
        int fee = Fees.fee(t);
        int after = balance + Fees.change(t) - fee;
        boolean accepted = after >= 0;
        if (accepted) {
            balance = after;
        }
        Entry entry = new Entry(t, fee, balance, accepted);
        if (count < entries.length) {
            entries[count] = entry;
            count++;
        }
        return entry;
    }

    public int getBalance() {
        return balance;
    }

    public void print() {
        IO.println(owner + " 様");
        for (int i = 0; i < count; i++) {
            Entry e = entries[i];
            Transaction t = e.transaction();
            String line = "  %d日 %s %,d円".formatted(t.day(), Fees.describe(t), t.amount());
            if (!e.accepted()) {
                line += " → 残高不足のため取り消し";
            } else {
                if (e.fee() > 0) {
                    line += " 手数料 " + e.fee() + "円";
                }
                line += " → 残高 %,d円".formatted(e.balance());
            }
            IO.println(line);
        }
    }

    // 種類ごとの件数と手数料の合計。sealedなので、ここもdefaultなしで書ける
    public void printSummary() {
        int deposits = 0;
        int withdrawals = 0;
        int transfers = 0;
        int totalFee = 0;
        int rejected = 0;
        for (int i = 0; i < count; i++) {
            Entry e = entries[i];
            if (!e.accepted()) {
                rejected++;
                continue;
            }
            totalFee += e.fee();
            switch (e.transaction()) {
                case Deposit d -> deposits++;
                case Withdrawal w -> withdrawals++;
                case Transfer t -> transfers++;
            }
        }
        IO.println("  入金 " + deposits + "件、出金 " + withdrawals + "件、振込 " + transfers + "件、取り消し " + rejected + "件");
        IO.println("  支払った手数料 " + totalFee + "円");
    }
}
