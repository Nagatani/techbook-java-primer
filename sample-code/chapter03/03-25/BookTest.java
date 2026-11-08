// BookTest.java
public class BookTest {
    public static void main(String[] args) {
        // 引数ありコンストラクタで作成
        Book book1 = new Book("Java入門", "田中太郎", 300);
        book1.displayInfo();
        
        System.out.println("---");
        
        // デフォルトコンストラクタで作成
        Book book2 = new Book();
        book2.displayInfo();
    }
}
