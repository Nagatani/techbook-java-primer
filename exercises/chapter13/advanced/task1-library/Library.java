import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/** 本の貸出と返却を管理する図書館 */
public class Library {
    public static final int LOAN_DAYS = 14;
    public static final int LIMIT = 2;

    /** 本の情報（借りている人がいなければborrowerはnull） */
    private static class Book {
        final String title;
        String borrower;
        int dueDay;

        Book(String title) {
            this.title = title;
        }
    }

    private final Map<String, Book> books = new LinkedHashMap<>();
    private final Map<String, Integer> loanCounts = new HashMap<>();

    public void addBook(String bookId, String title) {
        books.put(bookId, new Book(title));
    }

    /**
     * 本を貸し出し、返却期限の日を返す
     * 引数の誤り（プログラムの誤り）は非検査例外、業務上の失敗は検査例外で知らせる
     */
    public int lend(String member, String bookId, int today) throws LibraryException {
        if (member == null || member.isBlank()) {
            throw new IllegalArgumentException("利用者の名前が空です");
        }
        Book book = findBook(bookId);
        if (book.borrower != null) {
            throw new BookUnavailableException(bookId, book.title, book.dueDay);
        }
        int count = loanCounts.getOrDefault(member, 0);
        if (count >= LIMIT) {
            throw new LoanLimitException(member, bookId, LIMIT);
        }
        book.borrower = member;
        book.dueDay = today + LOAN_DAYS;
        loanCounts.put(member, count + 1);
        return book.dueDay;
    }

    /** 本を返却し、借りていた人の名前を返す */
    public String giveBack(String bookId) throws BookNotFoundException {
        Book book = findBook(bookId);
        if (book.borrower == null) {
            // 貸していない本の返却は、呼び出し側の誤りとして扱う
            throw new IllegalStateException("「" + book.title + "」は貸し出していません");
        }
        String member = book.borrower;
        book.borrower = null;
        loanCounts.merge(member, -1, Integer::sum);
        return member;
    }

    public String getTitle(String bookId) throws BookNotFoundException {
        return findBook(bookId).title;
    }

    private Book findBook(String bookId) throws BookNotFoundException {
        Book book = books.get(bookId);
        if (book == null) {
            throw new BookNotFoundException(bookId);
        }
        return book;
    }
}
