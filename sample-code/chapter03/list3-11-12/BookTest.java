/**
 * リスト3-12
 * BookTestクラス
 * 
 * 元ファイル: chapter03-oop-basics.md (881行目)
 */

public class BookTest {
    public static void main(String[] args) {
        // オブジェクトの作成（インスタンス化）
        Book book1 = new Book("Java入門", "田中太郎", 300, 2800.0);
        Book book2 = new Book("データ構造", "佐藤花子", 250, 3200.0);
        
        // メソッドの呼び出し
        book1.displayInfo();
        System.out.println("---");
        book2.displayInfo();
        
        // 価格の変更
        book1.setPrice(2500.0);
        System.out.println("変更後の価格: " + book1.getTitle() + " - " + book1.getPrice() + "円");
    }
}