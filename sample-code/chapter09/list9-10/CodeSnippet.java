/**
 * リスト9-10
 * コードスニペット
 * 
 * 元ファイル: chapter09-records.md (401行目)
 */

// 状態を型で表現
public sealed interface OrderStatus permits Pending, Confirmed, Shipped, Delivered {}
public record Pending() implements OrderStatus {}
public record Confirmed(LocalDateTime confirmedAt) implements OrderStatus {}
public record Shipped(String trackingNumber, LocalDateTime shippedAt) implements OrderStatus {}
public record Delivered(LocalDateTime deliveredAt) implements OrderStatus {}

// パターンマッチングによる状態処理
public static String getStatusMessage(OrderStatus status) {
    return switch (status) {
        case Pending() -> "注文を受け付けました";
        case Confirmed(var confirmedAt) -> "注文が確定しました: " + confirmedAt;
        case Shipped(var trackingNumber, var shippedAt) -> 
            "発送しました（追跡番号: " + trackingNumber + "）";
        case Delivered(var deliveredAt) -> "配達完了: " + deliveredAt;
    };
}