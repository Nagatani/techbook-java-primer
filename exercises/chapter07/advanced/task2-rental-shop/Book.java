/**
 * 販売専用の本
 */
public class Book implements Sellable {
    private final String title;
    private final int price;

    public Book(String title, int price) {
        this.title = title;
        this.price = price;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public int price() {
        return price;
    }
}
