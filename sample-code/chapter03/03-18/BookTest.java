// BookTest.java
public class BookTest {
    public static void main(String[] args) {
        // 引数ありコンストラクターで作成
        Book book1 = new Book("Java入門", "田中太郎", 300);
        book1.displayInfo();
        
        IO.println("---");
        
        // 引数のないコンストラクターで作成
        Book book2 = new Book();
        book2.displayInfo();
    }
}
