package bank;

/**
 * 口座を開き、口座のあいだの振込を行うクラス
 */
public class Bank {
    public static final int FEE = 200;
    private static final int MAX_ACCOUNTS = 20;

    private final String name;
    private Account[] accounts = new Account[MAX_ACCOUNTS];
    private int accountCount;
    private int successCount;
    private int failureCount;
    private int feeTotal;

    public Bank(String name) {
        this.name = name;
    }

    // 口座を開く。同じ番号の口座があるか、いっぱいのときはnull
    public Account open(String number, String owner, int openingBalance) {
        if (accountCount >= accounts.length || find(number) != null) {
            IO.println("口座を開けません: " + number);
            return null;
        }
        Account account = new Account(number, owner, openingBalance);
        accounts[accountCount] = account;
        accountCount++;
        IO.println("口座を開きました: " + number + "（" + owner + "） " + openingBalance + "円");
        return account;
    }

    // 最初の入金を省いたときは0円
    public Account open(String number, String owner) {
        return open(number, owner, 0);
    }

    // 口座番号で振り込む。口座を探してから、口座で振り込むメソッドに任せる
    public boolean transfer(String fromNumber, String toNumber, int amount) {
        return transfer(find(fromNumber), find(toNumber), amount);
    }

    // 条件をすべて確かめてから、2つの口座の残高をまとめて変える
    public boolean transfer(Account from, Account to, int amount) {
        String reason = check(from, to, amount);
        IO.println("振込 " + label(from) + " → " + label(to) + " " + amount + "円");
        if (reason != null) {
            failureCount++;
            IO.println("  できません: " + reason);
            return false;
        }
        from.withdraw(amount + FEE);
        to.deposit(amount);
        feeTotal += FEE;
        successCount++;
        Transaction t = new Transaction("T%03d".formatted(successCount), from, to, amount, FEE);
        from.addHistory(t);
        to.addHistory(t);
        IO.println("  " + t.getId() + " 完了（手数料" + FEE + "円） 残高 "
                + from.getOwner() + "様 " + from.getBalance() + "円 / "
                + to.getOwner() + "様 " + to.getBalance() + "円");
        return true;
    }

    // 振り込めない理由を返す。振り込めるときはnull
    private String check(Account from, Account to, int amount) {
        if (from == null || to == null) {
            return "口座が見つかりません";
        }
        if (find(from.getNumber()) != from || find(to.getNumber()) != to) {
            return "この銀行の口座ではありません";
        }
        if (from == to) {
            return "同じ口座には振り込めません";
        }
        if (amount <= 0) {
            return "金額は1円以上にしてください";
        }
        if (from.getBalance() < amount + FEE) {
            return "残高が足りません（必要 " + (amount + FEE) + "円、残高 " + from.getBalance() + "円）";
        }
        return null;
    }

    // お金が増えたり消えたりしていないかを確かめる
    public void audit() {
        int opening = 0;
        int current = 0;
        for (int i = 0; i < accountCount; i++) {
            opening += accounts[i].getOpeningBalance();
            current += accounts[i].getBalance();
        }
        IO.println(name + "の監査:");
        IO.println("  振込 成功 " + successCount + "件、失敗 " + failureCount + "件");
        IO.println("  開設時の残高の合計 " + opening + "円 = いまの残高の合計 " + current
                + "円 + 手数料 " + feeTotal + "円: " + (opening == current + feeTotal ? "一致" : "不一致"));
    }

    private Account find(String number) {
        for (int i = 0; i < accountCount; i++) {
            if (accounts[i].getNumber().equals(number)) {
                return accounts[i];
            }
        }
        return null;
    }

    private static String label(Account account) {
        return account == null ? "（不明）" : account.getNumber();
    }
}
