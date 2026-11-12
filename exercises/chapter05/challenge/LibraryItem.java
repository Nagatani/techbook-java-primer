import java.util.Objects;

/**
 * 図書館の資料を表す親クラス
 *
 * 貸出期間・1日あたりの延滞料金・貸し出せるかどうかは、資料の種類ごとに違う。
 * このクラスでは標準の値（14日、10円、貸出中でなければ貸し出せる）を返し、
 * 子クラスが必要に応じてオーバーライドする。
 * 返却日の計算や延滞料金の計算は、このクラスに1回だけ書く。
 */
public class LibraryItem {
    private String id;
    private String title;
    private boolean onLoan;
    private int dueDay;  // 返却期限（貸出中のときだけ意味を持つ）

    public LibraryItem(String id, String title) {
        this.id = id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isOnLoan() {
        return onLoan;
    }

    public int getDueDay() {
        return dueDay;
    }

    // 貸出期間（日数）。子クラスでオーバーライドする
    public int getLoanDays() {
        return 14;
    }

    // 1日あたりの延滞料金。子クラスでオーバーライドする
    public int getDailyLateFee() {
        return 10;
    }

    // 貸し出せるかどうか。子クラスで条件を足すときは、super.canBorrow()も確かめる
    public boolean canBorrow() {
        return !onLoan;
    }

    // 種類の名前（表示用）。子クラスでオーバーライドする
    public String getKind() {
        return "資料";
    }

    // day日に貸し出す。返却期限はgetLoanDays()で決まるので、どの子クラスでも同じ処理で済む
    public boolean borrow(int day) {
        if (!canBorrow()) {
            return false;
        }
        onLoan = true;
        dueDay = day + getLoanDays();
        return true;
    }

    // day日に返却し、延滞料金を返す。貸出中でなければ-1を返す
    public int giveBack(int day) {
        if (!onLoan) {
            return -1;
        }
        onLoan = false;
        return calculateLateFee(day);
    }

    // 延滞料金。期限を過ぎた日数 × 1日あたりの料金
    public int calculateLateFee(int returnDay) {
        int lateDays = returnDay - dueDay;
        if (lateDays <= 0) {
            return 0;
        }
        return lateDays * getDailyLateFee();
    }

    @Override
    public String toString() {
        String status = onLoan ? "貸出中（" + dueDay + "日まで）" : "在庫あり";
        return "[" + getKind() + "] " + id + " 「" + title + "」 " + status;
    }

    // 資料番号が同じなら同じ資料とみなす（第5章のStudentと同じ書き方）
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        LibraryItem other = (LibraryItem) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
