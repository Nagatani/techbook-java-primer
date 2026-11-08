// BookLibraryTest.java
public class BookLibraryTest {
    public static void main(String[] args) {
        Book book = new Book("978-4-12345-678-9", "Java入門", "田中太郎");
        
        System.out.println(book.getInfo());
        
        // 本を借りる
        if (book.borrow()) {
            System.out.println("貸出処理が完了しました");
        }
        System.out.println(book.getInfo());
        
        // もう一度借りようとする
        if (!book.borrow()) {
            System.out.println("この本は貸出中です");
        }
        
        // 本を返却
        book.returnBook();
        System.out.println("返却処理が完了しました");
        System.out.println(book.getInfo());
    }
}
