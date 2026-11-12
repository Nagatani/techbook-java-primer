/**
 * ショッピングカート。CartItemの配列で中身を持つ
 *
 * CartItemは不変なので、個数を変えるときは新しいCartItemに置き換える。
 */
public class Cart {
    private final CartItem[] items;
    private int count;

    public Cart(int capacity) {
        this.items = new CartItem[capacity];
    }

    // 同じ商品（equalsで等しい商品）がすでにあれば、個数を足したCartItemに置き換える
    public String add(Product product, int quantity) {
        int index = indexOf(product);
        if (index >= 0) {
            int newQuantity = items[index].quantity() + quantity;
            if (newQuantity > CartItem.MAX_QUANTITY) {
                return product.name() + "はこれ以上入れられません";
            }
            items[index] = items[index].withQuantity(newQuantity);
            return product.name() + "を" + newQuantity + "個にしました";
        }
        if (count >= items.length) {
            return "カートがいっぱいです";
        }
        items[count] = new CartItem(product, quantity);
        count++;
        return product.name() + "を" + quantity + "個入れました";
    }

    public String remove(Product product) {
        int index = indexOf(product);
        if (index < 0) {
            return product.name() + "はカートにありません";
        }
        for (int i = index; i < count - 1; i++) {
            items[i] = items[i + 1];
        }
        count--;
        items[count] = null;
        return product.name() + "を取り除きました";
    }

    public Money total() {
        Money sum = Money.ZERO;
        for (int i = 0; i < count; i++) {
            sum = sum.plus(items[i].subtotal());
        }
        return sum;
    }

    public void print() {
        for (int i = 0; i < count; i++) {
            CartItem item = items[i];
            IO.println("  " + item.product().code() + " " + item.product().name() + " "
                    + item.product().price() + " × " + item.quantity() + " = " + item.subtotal());
        }
        IO.println("  合計 " + total());
    }

    // いちばん小計の大きい行と、品数・個数をまとめて表示する
    public void printSummary() {
        // このメソッドの中だけで使う集計結果を、ローカルレコードで表す
        record Summary(int kinds, int pieces, CartItem largest) {
        }

        int pieces = 0;
        CartItem largest = null;
        for (int i = 0; i < count; i++) {
            pieces += items[i].quantity();
            if (largest == null || items[i].subtotal().isGreaterThan(largest.subtotal())) {
                largest = items[i];
            }
        }
        Summary summary = new Summary(count, pieces, largest);
        IO.println("  " + summary.kinds() + "品目 " + summary.pieces() + "個");
        if (summary.largest() != null) {
            IO.println("  いちばん高い行: " + summary.largest().product().name() + " " + summary.largest().subtotal());
        }
    }

    private int indexOf(Product product) {
        for (int i = 0; i < count; i++) {
            if (items[i].product().equals(product)) {
                return i;
            }
        }
        return -1;
    }
}
