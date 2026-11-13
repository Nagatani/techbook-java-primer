import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// 個別のアノテーション使用例
@ToString
class Product {
    @Getter @Setter
    private String name;
    
    @Getter
    private final double price;  // finalフィールドはsetterなし
    
    @ToString.Include(name = "id")  // toStringでの名前を指定
    private long productId;
    
    public Product(double price) {
        this.price = price;
    }
}
