/** ほかの人が借りている本を借りようとしたときの例外 */
public class BookUnavailableException extends LibraryException {
    private final int dueDay;

    public BookUnavailableException(String bookId, String title, int dueDay) {
        super("「" + title + "」は貸出中です（" + dueDay + "日に返却予定）", bookId);
        this.dueDay = dueDay;
    }

    public int getDueDay() {
        return dueDay;
    }
}
