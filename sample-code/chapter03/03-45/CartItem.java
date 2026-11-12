public class CartItem {
    private String name;
    private double price;
    private int quantity;
    
    public CartItem(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
    
    public double getSubtotal() {
        return price * quantity;
    }
    
    public String getDescription() {
        return "%s - %.2f円 × %d = %.2f円".formatted(name, price, quantity, getSubtotal());
    }
}
