/**
 * 商品を表す抽象クラス
 *
 * 商品コード・名前・税抜きの価格はすべての商品に共通なので、このクラスに持つ。
 * 税率と種類の名前は商品によって違うので、抽象メソッドにする。
 */
public abstract class Item {
    private final String code;
    private final String name;
    private final int price;  // 税抜きの定価

    protected Item(String code, String name, int price) {
        this.code = code;
        this.name = name;
        this.price = price;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    // 消費税率（%）。子クラスで必ず実装する
    public abstract int taxRate();

    // 種類の名前。子クラスで必ず実装する
    public abstract String category();

    // 実際に売る税抜きの価格。値引きのある商品は子クラスでオーバーライドする
    public int salePrice() {
        return price;
    }

    // 軽減税率（8%）の対象かどうか
    public boolean isReducedRate() {
        return taxRate() == 8;
    }

    // レシートの1行。軽減税率の商品には「※」を付ける
    public String receiptLine() {
        String mark = isReducedRate() ? "※" : "  ";
        return mark + name + "  " + salePrice() + "円";
    }
}
