// BookClassTest.java
public class BookClassTest {
    public static void main(String[] args) {
        Book book = new Book("Java入門", "田中太郎", 300, 2800.0);
        
        book.displayInfo();
        IO.println("---");
        
        // 価格変更
        book.setPrice(2500.0);
        IO.println("価格変更後:");
        book.displayInfo();
    }
}
