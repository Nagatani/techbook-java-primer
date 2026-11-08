public class Product {
    private String name;
    
    public Product(String name) {
        // コンストラクタでの検証
        if (name == null) {
            throw new IllegalArgumentException("商品名はnullにできません");
        }
        this.name = name;
    }
    
    public int getNameLength() {
        // 防御的プログラミング
        return (name != null) ? name.length() : 0;
    }
}
