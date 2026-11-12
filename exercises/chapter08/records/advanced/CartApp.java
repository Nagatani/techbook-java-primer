/**
 * 第8章 発展課題: ショッピングカート（解答例）
 *
 * 金額・商品・カートの1行をレコードで表し、カートの合計を求めます。
 *
 * 学習ポイント:
 * - レコードの宣言と、自動で作られるアクセサー・equals・toString
 * - コンパクトコンストラクターによる検証と値の整形
 * - 静的ファクトリメソッドとwithメソッド
 * - メソッドの中だけで使うローカルレコード
 */
public class CartApp {
    public static void main(String[] args) {
        Product notebook = new Product("B01", "ノート", Money.of(150));
        Product pen = new Product("B02", "ボールペン", Money.of(120));
        Product bag = new Product("C10", "トートバッグ", Money.of(1800));

        IO.println("=== 商品 ===");
        IO.println(notebook);
        IO.println("名前: " + notebook.name() + "、価格: " + notebook.price());

        // コンパクトコンストラクターで整えるので、空白や小文字が混じっても同じ商品になる
        Product sameNotebook = new Product(" b01 ", "ノート ", Money.of(150));
        IO.println(sameNotebook);
        IO.println("同じ商品か: " + notebook.equals(sameNotebook));
        IO.println("== で比べると: " + (notebook == sameNotebook));

        IO.println();
        IO.println("=== カートに入れる ===");
        Cart cart = new Cart(10);
        IO.println(cart.add(notebook, 3));
        IO.println(cart.add(pen, 2));
        IO.println(cart.add(bag, 1));
        IO.println(cart.add(sameNotebook, 2));  // 同じ商品なので個数が増える
        cart.print();

        IO.println();
        IO.println("=== withメソッド ===");
        CartItem original = new CartItem(pen, 2);
        CartItem changed = original.withQuantity(5);
        IO.println("元のオブジェクト: " + original);
        IO.println("新しいオブジェクト: " + changed);

        IO.println();
        IO.println("=== 取り除く ===");
        IO.println(cart.remove(pen));
        IO.println(cart.remove(pen));
        cart.print();

        IO.println();
        IO.println("=== まとめ ===");
        cart.printSummary();
    }
}
