/**
 * 商品。商品コード・名前・価格が同じなら同じ商品とみなす（equalsは自動で作られる）
 */
public record Product(String code, String name, Money price) {
    // コンパクトコンストラクター: 検証に加えて、値を整えてからフィールドに入れる
    public Product {
        if (code == null || code.isBlank() || name == null || name.isBlank() || price == null) {
            throw new IllegalArgumentException("商品コード・名前・価格は必須です");
        }
        code = code.strip().toUpperCase();  // " b01 " → "B01"
        name = name.strip();
    }
}
