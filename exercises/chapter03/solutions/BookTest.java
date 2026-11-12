/**
 * Bookを試すプログラム（課題2の解答例）
 */
public class BookTest {
    public static void main(String[] args) {
        IO.println("=== 図書管理システム ===");

        Book book1 = new Book();
        book1.title = "Java入門";
        book1.author = "山田花子";
        book1.price = 2500;

        Book book2 = new Book();
        book2.title = "Python基礎";
        book2.author = "佐藤次郎";
        book2.price = 3000;

        book1.displayInfo();
        IO.println();
        book2.displayInfo();

        IO.println();
        IO.println("=== 価格比較 ===");
        int diff = book1.comparePriceWith(book2);
        if (diff < 0) {
            IO.println(book1.title + "の方が" + (-diff) + "円安いです");
        } else if (diff > 0) {
            IO.println(book2.title + "の方が" + diff + "円安いです");
        } else {
            IO.println("2冊は同じ価格です");
        }
    }
}
