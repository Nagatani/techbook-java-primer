import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 図書の貸出管理（チャレンジ課題の解答例）
 */
public class LibraryApp {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("M/d");

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:mem:library", "sa", "")) {
            LibraryDatabase.createTables(conn);

            IO.println("=== 本と利用者の登録 ===");
            int books = LibraryDatabase.registerBooks(conn, List.of(
                    new Book("B001", "Java入門", "山田一郎"),
                    new Book("B002", "データベース入門", "山田一郎"),
                    new Book("B003", "アルゴリズム図鑑", "鈴木次郎"),
                    new Book("B004", "はじめての統計学", "佐藤花子"),
                    new Book("B005", "英語で読むコンピューターの歴史", "高橋誠"),
                    new Book("B006", "レポートの書き方", "田中恵")));
            int members = LibraryDatabase.registerMembers(conn, List.of(
                    new Member(1, "青木"), new Member(2, "井上"), new Member(3, "上田")));
            IO.println("本" + books + "冊、利用者" + members + "人を登録しました");

            LoanService service = new LoanService(conn);
            IO.println();
            IO.println("=== 貸出 ===");
            lend(service, 1, "B001", LocalDate.of(2026, 11, 2));
            lend(service, 1, "B003", LocalDate.of(2026, 11, 2));
            lend(service, 2, "B002", LocalDate.of(2026, 11, 5));
            lend(service, 2, "B001", LocalDate.of(2026, 11, 5));
            lend(service, 1, "B004", LocalDate.of(2026, 11, 9));
            lend(service, 1, "B006", LocalDate.of(2026, 11, 9));
            lend(service, 3, "B009", LocalDate.of(2026, 11, 9));
            lend(service, 4, "B005", LocalDate.of(2026, 11, 9));

            IO.println();
            IO.println("=== 「入門」で検索 ===");
            service.searchBooks("入門").forEach(IO::println);

            IO.println();
            IO.println("=== 貸出中の本（返却期限の順） ===");
            printLoans(service.currentLoans());

            LocalDate check = LocalDate.of(2026, 11, 18);
            IO.println();
            IO.println("=== " + check.format(DATE) + "の時点で期限を過ぎている本 ===");
            printLoans(service.overdueLoans(check));

            IO.println();
            IO.println("=== 返却 ===");
            giveBack(service, "B003", LocalDate.of(2026, 11, 16));
            giveBack(service, "B001", LocalDate.of(2026, 11, 20));
            giveBack(service, "B002", LocalDate.of(2026, 11, 26));
            giveBack(service, "B005", LocalDate.of(2026, 11, 26));
            lend(service, 3, "B001", LocalDate.of(2026, 11, 26));

            IO.println();
            IO.println("=== 貸出中の本（返却期限の順） ===");
            printLoans(service.currentLoans());

            IO.println();
            IO.println("=== 利用者ごとの貸出回数 ===");
            for (LoanService.MemberStat s : service.memberStats()) {
                IO.println("  " + s.name() + ": " + s.loans() + "回、延滞料金 " + s.fees() + "円");
            }
        } catch (SQLException e) {
            System.err.println("データベースのエラー: " + e.getMessage());
        }
    }

    private static void lend(LoanService service, int memberId, String bookId, LocalDate today)
            throws SQLException {
        try {
            Loan loan = service.lend(memberId, bookId, today);
            IO.println(today.format(DATE) + " " + loan.memberName() + "さんに「" + loan.title()
                    + "」を貸し出しました（返却期限 " + loan.dueOn().format(DATE) + "）");
        } catch (LoanException e) {
            IO.println(today.format(DATE) + " 貸し出せません: " + e.getMessage());
        }
    }

    private static void giveBack(LoanService service, String bookId, LocalDate today) throws SQLException {
        try {
            LoanService.ReturnResult r = service.giveBack(bookId, today);
            String fee = r.fee() == 0 ? "" : "（" + r.overdueDays() + "日延滞、" + r.fee() + "円）";
            IO.println(today.format(DATE) + " " + r.memberName() + "さんが「" + r.title() + "」を返却しました" + fee);
        } catch (LoanException e) {
            IO.println(today.format(DATE) + " 返却できません: " + e.getMessage());
        }
    }

    private static void printLoans(List<Loan> loans) {
        if (loans.isEmpty()) {
            IO.println("  ありません");
        }
        for (Loan l : loans) {
            IO.println("  %s 「%s」 %sさん 貸出 %s 期限 %s".formatted(
                    l.bookId(), l.title(), l.memberName(), l.loanedOn().format(DATE), l.dueOn().format(DATE)));
        }
    }
}
