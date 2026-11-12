/**
 * 口座の取引。入金・出金・振込の3種類だけに限るため、sealedにしてpermitsで列挙する
 */
public sealed interface Transaction permits Deposit, Withdrawal, Transfer {
    // 取引をした日（1日、2日…の日の番号）
    int day();

    // 取引の金額
    int amount();
}
