/**
 * 不変の金額（課題4の解答例）
 *
 * 金額を円の整数（long）で持つ。doubleで持つと、0.1のような値を正確に表せず、計算に誤差が出る。
 * 円未満の金額を正確に扱いたいときは、標準ライブラリのjava.math.BigDecimalを使う。
 */
public final class Money {
    private final long yen;

    public Money(long yen) {
        if (yen < 0) {
            // 例外を投げて、作るのをやめる（例外は第13章で学ぶ）
            throw new IllegalArgumentException("金額は0円以上にします: " + yen);
        }
        this.yen = yen;
    }

    public long getYen() {
        return yen;
    }

    // どの計算も、自分を変えずに新しいMoneyを返す
    public Money plus(Money other) {
        return new Money(yen + other.yen);
    }

    // 結果が負になるときは、コンストラクターが例外を投げる
    public Money minus(Money other) {
        return new Money(yen - other.yen);
    }

    // percent%増しの金額。1円未満は切り捨てる（整数どうしの割り算）
    public Money increaseByPercent(int percent) {
        return new Money(yen * (100 + percent) / 100);
    }

    public boolean isGreaterThan(Money other) {
        return yen > other.yen;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return yen == ((Money) obj).yen;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(yen);
    }

    @Override
    public String toString() {
        return "%,d円".formatted(yen);
    }
}
