/** 1人が借りられる冊数を超えたときの例外 */
public class LoanLimitException extends LibraryException {
    private final String member;
    private final int limit;

    public LoanLimitException(String member, String bookId, int limit) {
        super(member + "さんはすでに" + limit + "冊借りています", bookId);
        this.member = member;
        this.limit = limit;
    }

    public String getMember() {
        return member;
    }

    public int getLimit() {
        return limit;
    }
}
