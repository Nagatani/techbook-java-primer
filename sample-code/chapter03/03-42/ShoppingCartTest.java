public class ShoppingCartTest {
    public static void main(String[] args) {
        ShoppingCart cart = new ShoppingCart(0.10); // 消費税10%
        
        cart.addItem("ノートPC", 98000, 1);
        cart.addItem("マウス", 3500, 2);
        cart.addItem("キーボード", 8500, 1);
        
        cart.displayCart();
    }
}
