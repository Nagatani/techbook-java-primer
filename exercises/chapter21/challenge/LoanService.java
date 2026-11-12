import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

/**
 * 図書館の貸出・返却・延滞のお知らせを扱うサービス
 * 依存するものはすべてコンストラクターで受け取る（コンストラクターインジェクション）
 */
public class LoanService {
    /** 貸出期間（日） */
    public static final int LOAN_DAYS = 14;
    /** 1人が同時に借りられる冊数 */
    public static final int MAX_LOANS = 3;
    /** 1日あたりの延滞料金（円） */
    public static final int FEE_PER_DAY = 10;

    private final BookCatalog catalog;
    private final LoanRepository repository;
    private final Notifier notifier;
    private final TodayProvider todayProvider;

    public LoanService(BookCatalog catalog, LoanRepository repository,
                       Notifier notifier, TodayProvider todayProvider) {
        this.catalog = catalog;
        this.repository = repository;
        this.notifier = notifier;
        this.todayProvider = todayProvider;
    }

    /** 本を貸し出す。返却期限は今日から14日後 */
    public Loan borrow(String memberId, String isbn) throws LoanException {
        Optional<Book> book = catalog.findByIsbn(isbn);
        if (book.isEmpty()) {
            throw new LoanException("ISBN " + isbn + " の本は蔵書にありません");
        }
        if (repository.findByIsbn(isbn).isPresent()) {
            throw new LoanException("「" + book.get().title() + "」は貸出中です");
        }
        List<Loan> current = repository.findByMember(memberId);
        if (current.size() >= MAX_LOANS) {
            throw new LoanException(memberId + "さんはすでに" + MAX_LOANS + "冊借りています");
        }
        LocalDate today = todayProvider.today();
        for (Loan loan : current) {
            if (loan.dueDate().isBefore(today)) {
                throw new LoanException(memberId + "さんには返却期限を過ぎた本があります");
            }
        }
        Loan loan = new Loan(book.get(), memberId, today.plusDays(LOAN_DAYS));
        repository.save(loan);
        return loan;
    }

    /** 本を返却し、延滞料金（円）を返す。延滞していればお知らせを送る */
    public int returnBook(String isbn) throws LoanException {
        Optional<Loan> found = repository.findByIsbn(isbn);
        if (found.isEmpty()) {
            throw new LoanException("ISBN " + isbn + " の本は貸し出していません");
        }
        Loan loan = found.get();
        repository.delete(isbn);
        int fee = lateDays(loan) * FEE_PER_DAY;
        if (fee > 0) {
            notifier.send(loan.memberId(),
                "「" + loan.book().title() + "」の延滞料金は" + fee + "円です");
        }
        return fee;
    }

    /** 返却期限を過ぎた貸出のそれぞれに、お知らせを送る。送った件数を返す */
    public int sendOverdueNotices() {
        int count = 0;
        for (Loan loan : repository.findAll()) {
            int days = lateDays(loan);
            if (days > 0) {
                notifier.send(loan.memberId(),
                    "「" + loan.book().title() + "」の返却期限（" + loan.dueDate()
                        + "）を" + days + "日過ぎています");
                count++;
            }
        }
        return count;
    }

    /** 返却期限を過ぎた日数（過ぎていなければ0） */
    private int lateDays(Loan loan) {
        long days = ChronoUnit.DAYS.between(loan.dueDate(), todayProvider.today());
        return (int) Math.max(0, days);
    }
}
