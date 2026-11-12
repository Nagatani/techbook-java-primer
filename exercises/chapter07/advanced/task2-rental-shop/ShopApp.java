/**
 * 第7章 発展課題2: レンタルと販売の店（解答例）
 *
 * 継承関係のないDVD・本・ゲームソフトに、
 * 「レンタルできる」「販売できる」という能力をインターフェイスで持たせます。
 */
public class ShopApp {
    public static void main(String[] args) {
        Dvd newDvd = new Dvd("宇宙の旅2", true);
        Dvd oldDvd = new Dvd("深海の世界", false);
        Book book = new Book("Java入門", 2800);
        GameSoft game = new GameSoft("パズル王国", 5980);

        IO.println("=== 商品の説明 ===");
        IO.println(newDvd.getTitle() + ": " + newDvd.describe());
        IO.println(oldDvd.getTitle() + ": " + oldDvd.describe());
        IO.println(book.getTitle() + ": " + book.describe());
        IO.println(game.getTitle() + ": " + game.describe());

        // 同じゲームソフトのオブジェクトが、両方の配列に入る
        Rentable[] rentals = {newDvd, oldDvd, game};
        Sellable[] goods = {book, game};

        IO.println();
        IO.println("=== 3日間借りたときの料金 ===");
        printRentalFees(rentals, 3);

        IO.println();
        IO.println("=== 2日間借りたときの料金 ===");
        printRentalFees(rentals, 2);

        IO.println();
        IO.println("=== 販売価格の一覧 ===");
        int total = 0;
        for (Sellable item : goods) {
            IO.println("  " + item.getTitle() + " " + item.price() + "円 → 税込 " + item.priceWithTax() + "円");
            total += item.price();
        }
        // staticメソッドは「インターフェイス名.メソッド名」で呼ぶ
        IO.println("  まとめて買うと 税込 " + Sellable.withTax(total) + "円");

        IO.println();
        IO.println("一度に借りられるのは最長" + Rentable.MAX_DAYS + "日です");
    }

    // Rentable型で受け取るので、DVDかゲームソフトかを調べずに料金を求められる
    private static void printRentalFees(Rentable[] items, int days) {
        for (Rentable item : items) {
            int fee = item.rentalFee(days);
            if (fee < 0) {
                IO.println("  " + item.getTitle() + " " + days + "日間は借りられません");
            } else {
                IO.println("  " + item.getTitle() + " " + fee + "円");
            }
        }
    }
}
