public class ShoppingCart {
    private static final int MAX_ITEMS = 10;  // カートに入れられる商品の種類の上限
    private CartItem[] items;
    private int itemCount;  // カートに入っている商品の種類の数
    private double taxRate;
    
    public ShoppingCart(double taxRate) {
        this.items = new CartItem[MAX_ITEMS];
        this.itemCount = 0;
        this.taxRate = taxRate;
    }
    
    public boolean addItem(String name, double price, int quantity) {
        if (itemCount >= items.length) {
            return false;  // カートがいっぱい
        }
        items[itemCount] = new CartItem(name, price, quantity);
        itemCount++;
        return true;
    }
    
    public double calculateTotal() {
        double subtotal = 0;
        for (int i = 0; i < itemCount; i++) {
            subtotal += items[i].getSubtotal();
        }
        return subtotal * (1 + taxRate);
    }
    
    public void displayCart() {
        IO.println("=== ショッピングカート ===");
        for (int i = 0; i < itemCount; i++) {
            IO.println(items[i].getDescription());
        }
        IO.println("合計（税込）: %.2f円".formatted(calculateTotal()));
    }
}
