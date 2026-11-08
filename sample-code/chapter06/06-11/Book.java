import java.time.LocalDate;

// ビルダーパターンを使った不変オブジェクトの構築
public final class Book {
    private final String isbn;
    private final String title;
    private final String author;
    private final String publisher;
    private final int publicationYear;
    private final int pages;
    private final String genre;
    
    // プライベートコンストラクタ
    private Book(Builder builder) {
        this.isbn = builder.isbn;
        this.title = builder.title;
        this.author = builder.author;
        this.publisher = builder.publisher;
        this.publicationYear = builder.publicationYear;
        this.pages = builder.pages;
        this.genre = builder.genre;
    }
    
    // ビルダークラス
    public static class Builder {
        // 必須パラメータ
        private final String isbn;
        private final String title;
        
        // オプションパラメータ（デフォルト値を設定）
        private String author = "Unknown";
        private String publisher = "";
        private int publicationYear = 0;
        private int pages = 0;
        private String genre = "General";
        
        // 必須パラメータのコンストラクタ
        public Builder(String isbn, String title) {
            if (isbn == null || isbn.trim().isEmpty()) {
                throw new IllegalArgumentException("ISBNは必須です");
            }
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("タイトルは必須です");
            }
            this.isbn = isbn;
            this.title = title;
        }
        
        // オプションパラメータのセッター（メソッドチェーン）
        public Builder author(String author) {
            this.author = author;
            return this;
        }
        
        public Builder publisher(String publisher) {
            this.publisher = publisher;
            return this;
        }
        
        public Builder publicationYear(int year) {
            if (year < 0 || year > LocalDate.now().getYear()) {
                throw new IllegalArgumentException("無効な出版年です");
            }
            this.publicationYear = year;
            return this;
        }
        
        public Builder pages(int pages) {
            if (pages < 0) {
                throw new IllegalArgumentException("ページ数は正の値である必要があります");
            }
            this.pages = pages;
            return this;
        }
        
        public Builder genre(String genre) {
            this.genre = genre;
            return this;
        }
        
        // Bookオブジェクトの構築
        public Book build() {
            return new Book(this);
        }
    }
    // getterメソッド（省略）
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    // ... 他のgetterメソッド
}
