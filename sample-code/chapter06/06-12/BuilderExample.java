public class BuilderExample {
    public static void main(String[] args) {
        // ビルダーパターンによる不変オブジェクトの構築
        Book book1 = new Book.Builder("978-4-123456-78-9", "Javaプログラミング入門")
            .author("山田太郎")
            .publisher("みなと出版")
            .publicationYear(2024)
            .pages(350)
            .genre("プログラミング")
            .build();
        
        // 必須パラメーターのみで構築
        Book book2 = new Book.Builder("978-4-987654-32-1", "データベース設計")
            .build();
        
        // 一部のオプションパラメーターを設定
        Book book3 = new Book.Builder("978-4-111111-11-1", "アルゴリズム入門")
            .author("鈴木花子")
            .pages(280)
            .build();
        
        // メソッドチェーンによる可読性の高いコード
        Book textbook = new Book.Builder("978-4-222222-22-2", "オブジェクト指向設計")
            .author("佐藤一郎")
            .publisher("さくら書房")
            .publicationYear(2023)
            .pages(450)
            .genre("ソフトウェア工学")
            .build();

        IO.println("Book 1: " + book1.getTitle() + " by " + book1.getAuthor());
        IO.println("Book 2: " + book2.getTitle() + " by " + book2.getAuthor());
        IO.println("Book 3: " + book3.getTitle() + " by " + book3.getAuthor());
        IO.println("Textbook: " + textbook.getTitle() + " by " + textbook.getAuthor());
    }
}
