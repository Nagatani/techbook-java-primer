import java.time.LocalDate;

/** 貸出の記録（どの本を、だれが、いつまで借りているか） */
public record Loan(Book book, String memberId, LocalDate dueDate) {
}
