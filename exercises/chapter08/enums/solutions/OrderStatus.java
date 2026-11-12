import java.util.EnumSet;

/**
 * 注文の状態（課題2の解答例）
 *
 * どの状態からどの状態へ進めるかを、列挙型のメソッドにまとめる。
 */
public enum OrderStatus {
    PENDING("注文受付中"),
    CONFIRMED("注文確認済み"),
    PREPARING("準備中"),
    SHIPPED("発送済み"),
    DELIVERED("配達完了"),
    CANCELLED("キャンセル済み");

    // キャンセルできる状態。EnumSetは、列挙子の集まりを表す（第8章「EnumSetとEnumMap」）
    private static final EnumSet<OrderStatus> CANCELLABLE = EnumSet.of(PENDING, CONFIRMED, PREPARING);

    private final String label;

    OrderStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public boolean isCancellable() {
        return CANCELLABLE.contains(this);
    }

    // 次に進める状態。最後の状態（配達完了・キャンセル済み）にはない
    public OrderStatus nextStatus() {
        return switch (this) {
            case PENDING -> CONFIRMED;
            case CONFIRMED -> PREPARING;
            case PREPARING -> SHIPPED;
            case SHIPPED -> DELIVERED;
            case DELIVERED, CANCELLED -> null;
        };
    }

    // 移れるのは、次の状態か、キャンセルできる状態からのキャンセルだけ
    public boolean canTransitionTo(OrderStatus target) {
        if (target == CANCELLED) {
            return isCancellable();
        }
        return target == nextStatus();
    }
}
