public class Book {
    private String title;
    private String author;
    private int pages;
    
    // コンストラクター（クラス名と同じ名前）
    public Book(String title, String author, int pages) {
        this.title = title;
        this.author = author;
        this.pages = pages;
    }
    
    // 引数のないコンストラクター
    public Book() {
        this.title = "未設定";
        this.author = "不明";
        this.pages = 0;
    }
    
    public void displayInfo() {
        IO.println("タイトル: " + title);
        IO.println("著者: " + author);
        IO.println("ページ数: " + pages);
    }
}
