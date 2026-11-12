/**
 * 貸出記録（会員名・書名・ジャンル・貸し出した月）
 */
public record Loan(String member, String title, String genre, int month) {
}
