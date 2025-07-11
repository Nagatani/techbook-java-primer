/**
 * リスト9-9
 * ProductServiceクラス
 * 
 * 元ファイル: chapter09-records.md (372行目)
 */

public class ProductService {
    // 純粋関数による処理
    public static boolean isAvailable(Product product) {
        return product.inventory().quantity() > 0;
    }
    
    public static Money calculateDiscountPrice(Product product, DiscountRate rate) {
        BigDecimal discountAmount = product.price().amount().multiply(rate.value());
        BigDecimal newAmount = product.price().amount().subtract(discountAmount);
        return new Money(newAmount, product.price().currency());
    }
    
    public static Product updateInventory(Product product, int soldQuantity) {
        Inventory newInventory = product.inventory().subtract(soldQuantity);
        return new Product(
            product.id(),
            product.name(),
            product.category(),
            product.price(),
            newInventory
        );
    }
}