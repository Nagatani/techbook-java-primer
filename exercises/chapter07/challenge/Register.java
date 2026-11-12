/**
 * レジ。読み取った商品をItem型の配列で持ち、税率ごとに合計してレシートを出す
 *
 * 税率や値引きの違いは各商品のメソッドが受け持つので、
 * このクラスは商品の種類を調べずに合計できる。
 */
public class Register {
    private final Item[] items;
    private int count;

    public Register(int capacity) {
        this.items = new Item[capacity];
    }

    public boolean scan(Item item) {
        if (item == null || count >= items.length) {
            return false;
        }
        items[count] = item;
        count++;
        return true;
    }

    // 税率ごとの税抜き合計
    public int subtotal(int taxRate) {
        int sum = 0;
        for (int i = 0; i < count; i++) {
            if (items[i].taxRate() == taxRate) {
                sum += items[i].salePrice();
            }
        }
        return sum;
    }

    // 税額は税率ごとの合計にかける（1円未満は切り捨て）
    public int tax(int taxRate) {
        return subtotal(taxRate) * taxRate / 100;
    }

    public int total() {
        return subtotal(8) + tax(8) + subtotal(10) + tax(10);
    }

    // 値引きした金額の合計。値引きは一部の商品の能力なので、ここだけinstanceofで調べる
    public int totalDiscount() {
        int sum = 0;
        for (int i = 0; i < count; i++) {
            if (items[i] instanceof Discountable d && d.isDiscounted()) {
                sum += items[i].getPrice() - items[i].salePrice();
            }
        }
        return sum;
    }

    public void printReceipt(int cash) {
        IO.println("---------- レシート ----------");
        for (int i = 0; i < count; i++) {
            IO.println(items[i].receiptLine());
        }
        IO.println("------------------------------");
        IO.println("  8%対象 " + subtotal(8) + "円（消費税 " + tax(8) + "円）");
        IO.println(" 10%対象 " + subtotal(10) + "円（消費税 " + tax(10) + "円）");
        IO.println("  合計   " + total() + "円");
        if (totalDiscount() > 0) {
            IO.println("  （値引き " + totalDiscount() + "円）");
        }
        IO.println("  お預かり " + cash + "円");
        IO.println("  お釣り   " + (cash - total()) + "円");
        IO.println("  ※は軽減税率（8%）の対象です");
    }
}
