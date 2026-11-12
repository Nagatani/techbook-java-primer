/**
 * 取引の種類ごとの処理を、レコードパターンを使ったswitchでまとめたクラス
 *
 * Transactionはsealedなので、3種類のcaseを書けばdefaultは要らない。
 * 種類を足したときは、caseの足りないswitchがコンパイルエラーになって知らせてくれる。
 */
public class Fees {
    // この金額以上の振込は、手数料が220円高くなる
    public static final int LARGE_TRANSFER = 30000;

    private Fees() {
    }

    // 手数料
    public static int fee(Transaction t) {
        return switch (t) {
            case Deposit d -> 0;
            case Withdrawal(int day, int amount, Channel channel) -> channel.getWithdrawalFee();
            case Transfer(int day, int amount, String to, Channel channel) ->
                    amount >= LARGE_TRANSFER ? channel.getTransferFee() + 220 : channel.getTransferFee();
        };
    }

    // 残高の増減（手数料を除く）
    public static int change(Transaction t) {
        return switch (t) {
            case Deposit d -> d.amount();
            case Withdrawal w -> -w.amount();
            case Transfer tr -> -tr.amount();
        };
    }

    // 通帳に印字する摘要
    public static String describe(Transaction t) {
        return switch (t) {
            case Deposit(int day, int amount, Channel channel) -> "入金（" + channel.getLabel() + "）";
            case Withdrawal(int day, int amount, Channel channel) -> "出金（" + channel.getLabel() + "）";
            case Transfer(int day, int amount, String to, Channel channel) -> "振込 " + to + "（" + channel.getLabel() + "）";
        };
    }
}
