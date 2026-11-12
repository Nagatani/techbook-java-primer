/**
 * 本。貸出期間と延滞料金は親クラスの標準のまま使い、著者を表示に加える
 */
public class Book extends LibraryItem {
    private String author;

    public Book(String id, String title, String author) {
        super(id, title);
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public String getKind() {
        return "本";
    }

    // 親クラスの表示に、著者を付け足す
    @Override
    public String toString() {
        return super.toString() + " 著者: " + author;
    }
}
