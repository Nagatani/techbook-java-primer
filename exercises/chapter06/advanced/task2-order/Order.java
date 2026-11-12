import java.util.ArrayList;
import java.util.List;

/**
 * 1件の注文。注文番号・お客さんの名前・注文の行のリストを持つ不変クラス
 *
 * リストは受け取るときも渡すときもコピーする。
 * 中身のOrderLineも不変なので、リストをコピーするだけで外から変えられなくなる。
 */
public final class Order {
    private final String number;
    private final String customer;
    private final List<OrderLine> lines;

    public Order(String number, String customer, List<OrderLine> lines) {
        if (number == null || number.isBlank() || customer == null || customer.isBlank()) {
            throw new IllegalArgumentException("注文番号と名前を指定してください");
        }
        this.number = number;
        this.customer = customer;
        this.lines = new ArrayList<>(lines);  // 防御的コピー
    }

    // 行のない注文
    public Order(String number, String customer) {
        this(number, customer, new ArrayList<>());
    }

    public String getNumber() {
        return number;
    }

    public String getCustomer() {
        return customer;
    }

    public List<OrderLine> getLines() {
        return new ArrayList<>(lines);  // 内部のリストそのものは渡さない
    }

    public int total() {
        int sum = 0;
        for (OrderLine line : lines) {
            sum += line.subtotal();
        }
        return sum;
    }

    // 行を足した新しい注文を返す。同じ商品があれば個数を足す
    public Order withLine(OrderLine line) {
        List<OrderLine> newLines = new ArrayList<>(lines);
        int index = indexOf(line.getProduct());
        if (index >= 0) {
            OrderLine old = newLines.get(index);
            newLines.set(index, old.withQuantity(old.getQuantity() + line.getQuantity()));
        } else {
            newLines.add(line);
        }
        return new Order(number, customer, newLines);
    }

    // 商品の個数を変えた新しい注文を返す。0なら行を消す。商品がなければ自分を返す
    public Order withQuantity(String product, int quantity) {
        int index = indexOf(product);
        if (index < 0) {
            return this;
        }
        List<OrderLine> newLines = new ArrayList<>(lines);
        if (quantity == 0) {
            newLines.remove(index);
        } else {
            newLines.set(index, newLines.get(index).withQuantity(quantity));
        }
        return new Order(number, customer, newLines);
    }

    private int indexOf(String product) {
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).getProduct().equals(product)) {
                return i;
            }
        }
        return -1;
    }

    public void print() {
        IO.println("  注文 " + number + "（" + customer + "様）");
        for (OrderLine line : lines) {
            IO.println("    " + line + " = " + line.subtotal() + "円");
        }
        IO.println("    合計 " + total() + "円");
    }
}
