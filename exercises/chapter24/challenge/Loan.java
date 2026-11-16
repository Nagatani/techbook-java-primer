import java.time.LocalDate;

/**
 * 貸し出している1冊。loansテーブルにbooksとmembersを結合した結果から作る
 */
public record Loan(int id, String bookId, String title, String memberName, LocalDate loanedOn, LocalDate dueOn) {
}
