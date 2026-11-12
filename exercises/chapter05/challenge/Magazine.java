/**
 * 雑誌。貸出期間は7日。最新号は館内で読むだけで、貸し出さない
 */
public class Magazine extends LibraryItem {
    private int issue;      // 号数
    private boolean latest; // 最新号かどうか

    public Magazine(String id, String title, int issue, boolean latest) {
        super(id, title);
        this.issue = issue;
        this.latest = latest;
    }

    public boolean isLatest() {
        return latest;
    }

    // 次の号が届いたら、最新号ではなくなる
    public void markAsBackIssue() {
        this.latest = false;
    }

    @Override
    public String getKind() {
        return "雑誌";
    }

    @Override
    public int getLoanDays() {
        return 7;
    }

    // 親クラスの条件（貸出中でない）に、「最新号でない」を加える
    @Override
    public boolean canBorrow() {
        return super.canBorrow() && !latest;
    }

    @Override
    public String toString() {
        String mark = latest ? " 最新号（貸出不可）" : "";
        return super.toString() + " 第" + issue + "号" + mark;
    }
}
