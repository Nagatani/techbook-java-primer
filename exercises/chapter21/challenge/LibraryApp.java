import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * LoanServiceを本物の部品で組み立てて動かすデモ
 */
public class LibraryApp {
    /** デモ用の暦。日付を進められる */
    static class DemoCalendar implements TodayProvider {
        private LocalDate date;

        DemoCalendar(LocalDate start) {
            this.date = start;
        }

        void advance(int days) {
            date = date.plusDays(days);
            IO.println();
            IO.println("--- " + date + " ---");
        }

        @Override
        public LocalDate today() {
            return date;
        }
    }

    public static void main(String[] args) {
        Map<String, Book> books = new HashMap<>();
        for (Book book : new Book[] {
                new Book("978-4-0001", "Java入門"),
                new Book("978-4-0002", "アルゴリズム図鑑"),
                new Book("978-4-0003", "データベースの基礎"),
                new Book("978-4-0004", "ネットワーク概論")}) {
            books.put(book.isbn(), book);
        }
        BookCatalog catalog = isbn -> Optional.ofNullable(books.get(isbn));

        DemoCalendar calendar = new DemoCalendar(LocalDate.of(2026, 4, 1));
        // 実際のアプリでは new LoanService(catalog, repository, notifier, LocalDate::now)
        LoanService service = new LoanService(
            catalog, new InMemoryLoanRepository(), new ConsoleNotifier(), calendar);

        IO.println("--- " + calendar.today() + " ---");
        borrow(service, "M001", "978-4-0001");
        borrow(service, "M001", "978-4-0002");
        borrow(service, "M002", "978-4-0001");
        borrow(service, "M002", "978-4-9999");

        calendar.advance(10);
        borrow(service, "M002", "978-4-0003");

        calendar.advance(7);
        IO.println("延滞のお知らせ: " + service.sendOverdueNotices() + "件");
        borrow(service, "M001", "978-4-0004");
        giveBack(service, "978-4-0001");
        giveBack(service, "978-4-0003");
    }

    private static void borrow(LoanService service, String memberId, String isbn) {
        try {
            Loan loan = service.borrow(memberId, isbn);
            IO.println(memberId + "さんに「" + loan.book().title()
                + "」を貸し出しました（返却期限 " + loan.dueDate() + "）");
        } catch (LoanException e) {
            IO.println("貸出できません: " + e.getMessage());
        }
    }

    private static void giveBack(LoanService service, String isbn) {
        try {
            int fee = service.returnBook(isbn);
            IO.println(isbn + " が返却されました（延滞料金 " + fee + "円）");
        } catch (LoanException e) {
            IO.println("返却できません: " + e.getMessage());
        }
    }
}
