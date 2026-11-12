/**
 * 本を表すクラス（課題2の解答例）
 */
public class Book {
    String title;
    String author;
    int price;

    // タイトル・著者・価格と、税込価格を表示する
    void displayInfo() {
        IO.println("書籍情報:");
        IO.println("タイトル: " + title);
        IO.println("著者: " + author);
        IO.println("価格: " + "%,d".formatted(price) + "円");
        IO.println("税込価格: " + "%,d".formatted(calculateTaxIncludedPrice()) + "円");
    }

    // 税込価格（10%）。(int)で小数点以下を切り捨てる
    int calculateTaxIncludedPrice() {
        return (int) (price * 1.1);
    }

    // この本の価格から相手の本の価格を引いた差。負なら、この本のほうが安い
    int comparePriceWith(Book other) {
        return price - other.price;
    }
}
