/**
 * 第8章 チャレンジ課題: 銀行口座の通帳（解答例）
 *
 * 入金・出金・振込をsealedインターフェイスとレコードで表し、
 * レコードパターンを使ったswitchで手数料と残高を計算します。
 *
 * 学習ポイント:
 * - sealedインターフェイスと、それを実装するレコード
 * - レコードパターンを使ったswitch（defaultなしで網羅できる）
 * - 列挙型（Channel）とレコードの組み合わせ
 * - 入れ子のレコード（Passbook.Entry）
 */
public class BankApp {
    public static void main(String[] args) {
        Transaction[] transactions = {
            new Deposit(1, 200000, Channel.COUNTER),
            new Withdrawal(3, 20000, Channel.ATM),
            new Transfer(5, 65000, "家賃 大家 一郎", Channel.ONLINE),
            new Transfer(10, 8000, "サークル会費", Channel.ATM),
            new Withdrawal(12, 30000, Channel.COUNTER),
            new Transfer(20, 50000, "授業料", Channel.COUNTER),
            new Withdrawal(25, 30000, Channel.ATM),
            new Deposit(25, 80000, Channel.ONLINE),
            new Withdrawal(28, 10000, Channel.ATM)
        };

        IO.println("=== 取引のレコード ===");
        IO.println(transactions[0]);
        IO.println(transactions[2]);
        IO.println("同じ内容の取引か: " + transactions[1].equals(new Withdrawal(3, 20000, Channel.ATM)));

        IO.println();
        IO.println("=== 手数料の確認 ===");
        IO.println("ATMの振込 8,000円: " + Fees.fee(transactions[3]) + "円");
        IO.println("ネットの振込 65,000円: " + Fees.fee(transactions[2]) + "円（%,d円以上）".formatted(Fees.LARGE_TRANSFER));

        Passbook passbook = new Passbook("山田 花子", 20);
        for (Transaction t : transactions) {
            passbook.record(t);
        }

        IO.println();
        IO.println("=== 通帳 ===");
        passbook.print();

        IO.println();
        IO.println("=== 集計 ===");
        passbook.printSummary();
        IO.println("  最終残高 %,d円".formatted(passbook.getBalance()));
    }
}
