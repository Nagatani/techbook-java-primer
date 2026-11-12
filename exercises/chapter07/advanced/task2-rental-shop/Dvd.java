/**
 * レンタル専用のDVD
 */
public class Dvd implements Rentable {
    private final String title;
    private final boolean newRelease;  // 新作かどうか

    public Dvd(String title, boolean newRelease) {
        this.title = title;
        this.newRelease = newRelease;
    }

    @Override
    public String getTitle() {
        return title;
    }

    // 新作は1日300円、旧作は1日100円
    @Override
    public int dailyFee() {
        return newRelease ? 300 : 100;
    }

    // 新作は2日までしか借りられない。インターフェイスの条件に足す
    @Override
    public boolean canRent(int days) {
        return Rentable.super.canRent(days) && (!newRelease || days <= 2);
    }
}
