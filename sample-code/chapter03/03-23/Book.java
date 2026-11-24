public class Book {
    private String title;
    private String author;
    private int pages;
    private double price;  // 追加：価格
    
    // 価格も受け取るようにしたコンストラクター
    public Book(String title, String author, int pages, double price) {
        this.title = title;
        this.author = author;
        this.pages = pages;
        this.price = price;
    }
    
    // 引数のないコンストラクター
    public Book() {
        this.title = "未設定";
        this.author = "不明";
        this.pages = 0;
        this.price = 0.0;
    }
    
    public void displayInfo() {
        IO.println("タイトル: " + title);
        IO.println("著者: " + author);
        IO.println("ページ数: " + pages);
        IO.println("価格: " + price + "円");
    }
    
    // getter（読み取り用）
    public String getTitle() {
        return title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public double getPrice() {
        return price;
    }
    
    // setter（価格だけは変更できる）
    public void setPrice(double price) {
        if (price >= 0) {
            this.price = price;
        }
    }
}
