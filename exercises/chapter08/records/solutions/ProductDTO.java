/**
 * 画面や通信に渡すための商品データ（課題3の解答例）
 *
 * ProductEntityは作成日時や変更用のsetterを持つが、受け渡しに必要なのは4つの値だけ。
 * 必要な値だけを持つ不変のレコードにして渡す（DTO: Data Transfer Object）。
 */
public record ProductDTO(Long id, String name, double price, String category) {

    public ProductDTO {
        if (price < 0) {
            throw new IllegalArgumentException("価格は0以上にします: " + price);
        }
        if (name == null || name.isBlank() || category == null || category.isBlank()) {
            throw new IllegalArgumentException("商品名とカテゴリは必須です");
        }
    }

    public double getPriceWithTax() {
        return price * 1.1;
    }

    public String getFormattedPrice() {
        return "¥%,.0f".formatted(price);
    }

    public String toJson() {
        return "{\"id\":%d,\"name\":\"%s\",\"price\":%.1f,\"category\":\"%s\"}"
                .formatted(id, name, price, category);
    }

    public static ProductDTO fromEntity(ProductEntity entity) {
        return new ProductDTO(entity.getId(), entity.getName(), entity.getPrice(), entity.getCategory());
    }
}
