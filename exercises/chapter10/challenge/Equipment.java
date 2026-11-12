/**
 * サークルの備品。番号は101のような整数で、100番台がカメラ、200番台が三脚、300番台がプロジェクター
 */
public class Equipment implements Identifiable<Integer> {
    private final int id;
    private final String name;
    private String borrowerId;  // 借りている会員の番号。貸していなければnull
    private int dueDay;

    public Equipment(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isOnLoan() {
        return borrowerId != null;
    }

    public String getBorrowerId() {
        return borrowerId;
    }

    public int getDueDay() {
        return dueDay;
    }

    public void lendTo(String memberId, int dueDay) {
        this.borrowerId = memberId;
        this.dueDay = dueDay;
    }

    public void giveBack() {
        this.borrowerId = null;
    }

    @Override
    public String toString() {
        String state = isOnLoan() ? "貸出中（" + borrowerId + "、" + dueDay + "日まで）" : "貸出可";
        return id + " " + name + " " + state;
    }
}
