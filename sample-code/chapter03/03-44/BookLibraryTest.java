// BookLibraryTest.java
public class BookLibraryTest {
    public static void main(String[] args) {
        Book book = new Book("Java入門", "田中太郎", 300, 2800.0);
        
        IO.println(book.getInfo());
        
        // 本を借りる
        if (book.borrow()) {
            IO.println("貸出処理が完了しました");
        }
        IO.println(book.getInfo());
        
        // もう一度借りようとする
        if (!book.borrow()) {
            IO.println("この本は貸出中です");
        }
        
        // 本を返却
        book.returnBook();
        IO.println("返却処理が完了しました");
        IO.println(book.getInfo());
    }
}
