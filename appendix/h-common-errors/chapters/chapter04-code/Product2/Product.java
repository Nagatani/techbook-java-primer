public class Product {
    private String name;
    
    public Product(String name) {
        // コンストラクターでの検証
        if (name == null) {
            throw new IllegalArgumentException("商品名はnullにできません");
        }
        this.name = name;
    }
    
    public int getNameLength() {
        // コンストラクターでnullを弾いているので、nameはnullにならない
        return name.length();
    }
}
