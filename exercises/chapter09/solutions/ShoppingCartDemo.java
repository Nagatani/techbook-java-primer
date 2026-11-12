package chapter09.solutions;

/**
 * ショッピングカートのデモ（課題2の解答例）
 */
public class ShoppingCartDemo {
    public static void main(String[] args) {
        IO.println("=== ショッピングカートシステムデモ ===\n");

        IO.println("--- 商品在庫 ---");
        Product laptop = new Product("P001", "ノートパソコン", 80000, 5);
        Product mouse = new Product("P002", "マウス", 2000, 10);
        Product keyboard = new Product("P003", "キーボード", 5000, 8);
        IO.println(laptop);
        IO.println(mouse);
        IO.println(keyboard);

        IO.println("\n--- カート作成 ---");
        ShoppingCart cart = new ShoppingCart("山田太郎");
        IO.println("カートは空？: " + cart.isEmpty());

        IO.println("\n--- 商品追加 ---");
        IO.println("ノートパソコン×1: " + cart.addItem(laptop, 1));
        IO.println("マウス×2: " + cart.addItem(mouse, 2));

        IO.println("\n--- カート内容 ---");
        cart.displayCart();

        IO.println("\n--- 同じ商品を追加 ---");
        cart.addItem(mouse, 3);
        cart.displayCart();

        IO.println("\n--- 数量更新 ---");
        cart.updateQuantity("P002", 1);
        cart.displayCart();

        IO.println("\n--- 在庫チェック ---");
        IO.println("ノートパソコン×10: " + cart.addItem(laptop, 10));

        IO.println("\n--- 商品削除 ---");
        cart.removeItem("P001");
        cart.addItem(keyboard, 1);
        cart.displayCart();

        IO.println("\n--- カート統計 ---");
        IO.println("総アイテム数: " + cart.getTotalItemCount());
        IO.println("合計金額: " + (int) cart.getTotalAmount() + "円");

        IO.println("\n--- カートクリア ---");
        cart.clearCart();
        IO.println("カートは空？: " + cart.isEmpty());
    }
}
