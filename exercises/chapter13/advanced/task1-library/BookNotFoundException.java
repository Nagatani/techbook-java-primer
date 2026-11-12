/** 登録されていない資料番号を指定したときの例外 */
public class BookNotFoundException extends LibraryException {
    public BookNotFoundException(String bookId) {
        super("資料番号 " + bookId + " の本は登録されていません", bookId);
    }
}
