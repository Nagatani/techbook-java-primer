import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * 貸出と返却、貸出状況の検索
 */
public class LoanService {
    public static final int LOAN_DAYS = 14;
    public static final int MAX_LOANS = 3;
    public static final int FEE_PER_DAY = 10;

    /** 返却の結果。overdueDaysは期限を過ぎた日数（過ぎていなければ0） */
    public record ReturnResult(String title, String memberName, int overdueDays, int fee) {
    }

    /** 利用者ごとの貸出回数と、払った延滞料金の合計 */
    public record MemberStat(String name, int loans, int fees) {
    }

    private static final String LOAN_SELECT = """
        SELECT l.id, l.book_id, b.title, m.name, l.loaned_on, l.due_on
        FROM loans l
        INNER JOIN books b ON l.book_id = b.id
        INNER JOIN members m ON l.member_id = m.id
        """;

    private final Connection connection;

    public LoanService(Connection connection) {
        this.connection = connection;
    }

    /**
     * 本を貸し出す。返却期限は貸した日の14日後。
     * 決まりに合わなければLoanExceptionを投げ、データベースは何も変えない。
     */
    public Loan lend(int memberId, String bookId, LocalDate today) throws SQLException, LoanException {
        boolean originalAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            if (!exists("SELECT COUNT(*) FROM members WHERE id = ?", memberId)) {
                throw new LoanException("利用者" + memberId + "は登録されていません");
            }
            if (activeLoanCount(memberId) >= MAX_LOANS) {
                throw new LoanException("利用者" + memberId + "はすでに" + MAX_LOANS + "冊借りています");
            }
            markOnLoan(bookId);
            int loanId = insertLoan(bookId, memberId, today, today.plusDays(LOAN_DAYS));
            connection.commit();
            return findLoan(loanId);
        } catch (SQLException | LoanException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    /** 本を返却する。期限を過ぎていれば、1日10円の延滞料金を記録する */
    public ReturnResult giveBack(String bookId, LocalDate today) throws SQLException, LoanException {
        boolean originalAutoCommit = connection.getAutoCommit();
        connection.setAutoCommit(false);
        try {
            Loan loan = findActiveLoanByBook(bookId);
            if (loan == null) {
                throw new LoanException(bookId + "は貸し出していません");
            }
            int overdueDays = (int) Math.max(0, ChronoUnit.DAYS.between(loan.dueOn(), today));
            int fee = overdueDays * FEE_PER_DAY;
            try (PreparedStatement pstmt = connection.prepareStatement(
                    "UPDATE loans SET returned_on = ?, fee = ? WHERE id = ?")) {
                pstmt.setObject(1, today);
                pstmt.setInt(2, fee);
                pstmt.setInt(3, loan.id());
                pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = connection.prepareStatement(
                    "UPDATE books SET on_loan = FALSE WHERE id = ?")) {
                pstmt.setString(1, bookId);
                pstmt.executeUpdate();
            }
            connection.commit();
            return new ReturnResult(loan.title(), loan.memberName(), overdueDays, fee);
        } catch (SQLException | LoanException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(originalAutoCommit);
        }
    }

    /** 貸出中の本を、返却期限の順に返す */
    public List<Loan> currentLoans() throws SQLException {
        String sql = LOAN_SELECT + "WHERE l.returned_on IS NULL ORDER BY l.due_on, l.id";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            return queryLoans(pstmt);
        }
    }

    /** todayの時点で返却期限を過ぎている本を、返却期限の順に返す */
    public List<Loan> overdueLoans(LocalDate today) throws SQLException {
        String sql = LOAN_SELECT + "WHERE l.returned_on IS NULL AND l.due_on < ? ORDER BY l.due_on, l.id";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setObject(1, today);
            return queryLoans(pstmt);
        }
    }

    /** 題名または著者にキーワードを含む本を「[貸出中] B001 題名（著者）」の形で返す */
    public List<String> searchBooks(String keyword) throws SQLException {
        String sql = "SELECT id, title, author, on_loan FROM books WHERE title LIKE ? OR author LIKE ? ORDER BY id";
        List<String> lines = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, "%" + keyword + "%");
            pstmt.setString(2, "%" + keyword + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getBoolean("on_loan") ? "[貸出中]" : "[在庫あり]";
                    lines.add(status + " " + rs.getString("id") + " " + rs.getString("title")
                            + "（" + rs.getString("author") + "）");
                }
            }
        }
        return lines;
    }

    /** 利用者ごとの貸出回数（返却済みを含む）と延滞料金の合計を、回数の多い順に返す */
    public List<MemberStat> memberStats() throws SQLException {
        String sql = """
            SELECT m.id, m.name, COUNT(*) AS loans, SUM(l.fee) AS fees
            FROM loans l
            INNER JOIN members m ON l.member_id = m.id
            GROUP BY m.id, m.name
            ORDER BY loans DESC, m.id
            """;
        List<MemberStat> list = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                // 返却がまだなら、SUM(l.fee)はNULLになる。getIntはNULLを0として返す
                list.add(new MemberStat(rs.getString("name"), rs.getInt("loans"), rs.getInt("fees")));
            }
        }
        return list;
    }

    private boolean exists(String countSql, int id) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(countSql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    private int activeLoanCount(int memberId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM loans WHERE member_id = ? AND returned_on IS NULL";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    /** 在庫のある本だけを貸出中にする。書き換えた行が0なら、理由を調べて例外にする */
    private void markOnLoan(String bookId) throws SQLException, LoanException {
        try (PreparedStatement pstmt = connection.prepareStatement(
                "UPDATE books SET on_loan = TRUE WHERE id = ? AND on_loan = FALSE")) {
            pstmt.setString(1, bookId);
            if (pstmt.executeUpdate() > 0) {
                return;
            }
        }
        try (PreparedStatement pstmt = connection.prepareStatement("SELECT title FROM books WHERE id = ?")) {
            pstmt.setString(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    throw new LoanException("「" + rs.getString("title") + "」は貸出中です");
                }
                throw new LoanException(bookId + "という本はありません");
            }
        }
    }

    private int insertLoan(String bookId, int memberId, LocalDate loanedOn, LocalDate dueOn)
            throws SQLException {
        String sql = "INSERT INTO loans (book_id, member_id, loaned_on, due_on) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, bookId);
            pstmt.setInt(2, memberId);
            pstmt.setObject(3, loanedOn);
            pstmt.setObject(4, dueOn);
            pstmt.executeUpdate();
            try (ResultSet keys = pstmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
                throw new SQLException("貸出のIDを取得できませんでした");
            }
        }
    }

    private Loan findLoan(int loanId) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(LOAN_SELECT + "WHERE l.id = ?")) {
            pstmt.setInt(1, loanId);
            return queryLoans(pstmt).get(0);
        }
    }

    private Loan findActiveLoanByBook(String bookId) throws SQLException {
        String sql = LOAN_SELECT + "WHERE l.book_id = ? AND l.returned_on IS NULL";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, bookId);
            List<Loan> loans = queryLoans(pstmt);
            return loans.isEmpty() ? null : loans.get(0);
        }
    }

    private List<Loan> queryLoans(PreparedStatement pstmt) throws SQLException {
        List<Loan> loans = new ArrayList<>();
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                loans.add(new Loan(
                        rs.getInt("id"),
                        rs.getString("book_id"),
                        rs.getString("title"),
                        rs.getString("name"),
                        rs.getObject("loaned_on", LocalDate.class),
                        rs.getObject("due_on", LocalDate.class)));
            }
        }
        return loans;
    }
}
