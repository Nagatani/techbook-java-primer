/**
 * 金額（円）を表す値オブジェクト
 *
 * レコードなので、フィールドはfinalで、equals・hashCode・toStringのもとになる
 * アクセサーamount()は自動で作られる。
 */
public record Money(int amount) {
    public static final Money ZERO = new Money(0);

    // コンパクトコンストラクター: 代入の前に値を検証する
    public Money {
        if (amount < 0) {
            throw new IllegalArgumentException("金額は0以上にしてください: " + amount);
        }
    }

    // 静的ファクトリメソッド: Money.of(300) のように書ける
    public static Money of(int amount) {
        return new Money(amount);
    }

    // 計算の結果は新しいオブジェクトで返す（このオブジェクトは変わらない）
    public Money plus(Money other) {
        return new Money(amount + other.amount);
    }

    public Money times(int n) {
        return new Money(amount * n);
    }

    public boolean isGreaterThan(Money other) {
        return amount > other.amount;
    }

    // 自動で作られるtoString（Money[amount=300]）を、読みやすい形に変える
    @Override
    public String toString() {
        return "%,d円".formatted(amount);
    }
}
