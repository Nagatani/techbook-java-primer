/**
 * 図書館の業務で起こる例外の親クラス（検査例外）
 * 呼び出し側に「起こりうる失敗」として処理を求めるため、Exceptionを継承する
 */
public abstract class LibraryException extends Exception {
    private final String bookId;

    protected LibraryException(String message, String bookId) {
        super(message);
        this.bookId = bookId;
    }

    public String getBookId() {
        return bookId;
    }
}
