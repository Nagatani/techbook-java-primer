/**
 * 第1章 発展課題2: レシートを表示する - 解答例
 */
public class ShoppingReceipt {
    public static void main(String[] args) {
        String item = IO.readln("商品名を入力: ");
        int price = Integer.parseInt(IO.readln("値段を入力: "));
        int quantity = Integer.parseInt(IO.readln("個数を入力: "));
        int subtotal = price * quantity;

        IO.println();
        IO.println("===== レシート =====");
        IO.println("商品: %s".formatted(item));
        IO.println("単価: %5d円".formatted(price));
        IO.println("個数: %5d個".formatted(quantity));
        IO.println("小計: %5d円".formatted(subtotal));
        IO.println("====================");
    }
}
