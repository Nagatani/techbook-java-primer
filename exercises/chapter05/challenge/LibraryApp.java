/**
 * 第5章 チャレンジ課題: 図書館の資料管理システム（解答例）
 *
 * 本・雑誌・DVDを、親クラスLibraryItemの型でまとめて管理します。
 * 貸出期間・延滞料金・貸し出せるかどうかは、子クラスのオーバーライドで変わります。
 *
 * 学習ポイント:
 * - extendsによる継承と、super(...)による親クラスのコンストラクターの呼び出し
 * - メソッドのオーバーライドと、super.メソッド名()による親クラスの処理の再利用
 * - 親クラスの型の配列で、種類の違うオブジェクトを同じ処理で扱う（ポリモーフィズム）
 * - instanceofによる型の判定（子クラスだけが持つメソッドを使うとき）
 * - toString・equals・hashCodeのオーバーライド
 */
public class LibraryApp {
    public static void main(String[] args) {
        Library library = new Library(10);

        IO.println("=== 資料の登録 ===");
        IO.println(library.add(new Book("B001", "Java入門", "山田一郎")));
        IO.println(library.add(new Book("B002", "アルゴリズム図鑑", "佐藤次郎")));
        Magazine magazine = new Magazine("M001", "月刊プログラミング", 12, true);
        IO.println(library.add(magazine));
        IO.println(library.add(new Dvd("D001", "宇宙の旅", 120)));
        IO.println(library.add(new Dvd("D002", "深海の世界", 95)));
        IO.println(library.add(new Book("B001", "Java入門（別の本）", "鈴木三郎")));  // 資料番号が同じ

        IO.println();
        IO.println("=== 貸出 ===");
        IO.println(library.borrow("B001", 1));
        IO.println(library.borrow("D001", 1));
        IO.println(library.borrow("M001", 1));  // 最新号は貸し出せない
        IO.println(library.borrow("B001", 2));  // 貸出中
        magazine.markAsBackIssue();             // 次の号が届いた
        IO.println(library.borrow("M001", 3));
        IO.println(library.borrow("D002", 5));

        IO.println();
        IO.println("=== 12日の時点で延滞している資料 ===");  // D002は12日が期限なので、まだ延滞ではない
        library.showOverdue(12);

        IO.println();
        IO.println("=== 返却 ===");
        IO.println(library.giveBack("B001", 12));  // 期限内（15日まで）
        IO.println(library.giveBack("D001", 12));  // 期限（8日）を4日過ぎた
        IO.println(library.giveBack("M001", 12));  // 期限（10日）を2日過ぎた
        IO.println(library.giveBack("B002", 12));  // 貸し出していない

        IO.println();
        IO.println("=== 資料の一覧 ===");
        library.showItems();

        IO.println();
        IO.println("=== 集計 ===");
        library.showReport();
    }
}
