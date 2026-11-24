public class Book {
    private String title;
    private String author;
    private int pages;
    private double price;
    
    // コンストラクター1：すべてのフィールドを初期化
    public Book(String title, String author, int pages, double price) {
        this.title = title;
        this.author = author;
        this.pages = pages;
        this.price = price;
    }
    
    // コンストラクター2：必須フィールドのみ
    public Book(String title, String author) {
        this(title, author, 0, 0.0);  // 他のコンストラクターを呼び出す
    }
    
    // コンストラクター3：タイトルのみ
    public Book(String title) {
        this(title, "Unknown", 0, 0.0);
    }
}
