import java.time.LocalDateTime;

/**
 * 商品のエンティティ（課題3で使う。雛形と同じ）
 *
 * 作成日時・変更日時とsetterを持つ、変更できるクラス。ProductDTOと比べるために使う。
 */
public class ProductEntity {
    private Long id;
    private String name;
    private double price;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public ProductEntity(Long id, String name, double price, String category) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    // Setters
    public void setName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setPrice(double price) {
        this.price = price;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setCategory(String category) {
        this.category = category;
        this.updatedAt = LocalDateTime.now();
    }
}