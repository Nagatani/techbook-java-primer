public class Product {
    private String name;
    private double price;
    
    // getter：値を取得
    public String getName() {
        return name;
    }
    
    public double getPrice() {
        return price;
    }
    
    // setter：データ検証付きで値を設定
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("商品名は必須です");
        }
        this.name = name;
    }
    
    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("価格は負の値にできません");
        }
        this.price = price;
    }
}

class ProductTest {
    public static void main(String[] args) {
        Product product = new Product();
        
        try {
            // 正常なデータ設定
            product.setName("ノートPC");
            product.setPrice(98000);
            System.out.println("商品名: " + product.getName());
            System.out.println("価格: " + product.getPrice() + "円");
            
            // 不正なデータの検証
            product.setName("");  // 例外が発生
        } catch (IllegalArgumentException e) {
            System.out.println("エラー: " + e.getMessage());
        }
        
        try {
            product.setPrice(-1000);  // 例外が発生
        } catch (IllegalArgumentException e) {
            System.out.println("エラー: " + e.getMessage());
        }
    }
}
