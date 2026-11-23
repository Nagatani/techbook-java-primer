public class Book {
    private String title;
    private String author;
    private int pages;
    private double price;
    private boolean isAvailable = true;  // 追加：貸出できるかどうか
    
    public Book(String title, String author, int pages, double price) {
        this.title = title;
        this.author = author;
        this.pages = pages;
        this.price = price;
    }
    
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
    
    public String getTitle() {
        return title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        if (price >= 0) {
            this.price = price;
        }
    }
    
    // 追加：本を借りる（貸出中なら借りられない）
    public boolean borrow() {
        if (isAvailable) {
            isAvailable = false;
            return true;
        }
        return false;
    }
    
    // 追加：本を返す
    public void returnBook() {
        isAvailable = true;
    }
    
    // 追加：貸出状況を含めた本の情報
    public String getInfo() {
        String status = isAvailable ? "貸出可能" : "貸出中";
        return "%s (%s) [%s]".formatted(title, author, status);
    }
}
