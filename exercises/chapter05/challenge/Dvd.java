/**
 * DVD。貸出期間は7日、延滞料金は1日100円。再生時間を持つ
 */
public class Dvd extends LibraryItem {
    private int minutes;  // 再生時間（分）

    public Dvd(String id, String title, int minutes) {
        super(id, title);
        this.minutes = minutes;
    }

    // DVDだけが持つメソッド。LibraryItem型の変数からは呼べない
    public int getMinutes() {
        return minutes;
    }

    @Override
    public String getKind() {
        return "DVD";
    }

    @Override
    public int getLoanDays() {
        return 7;
    }

    @Override
    public int getDailyLateFee() {
        return 100;
    }

    @Override
    public String toString() {
        return super.toString() + " " + minutes + "分";
    }
}
