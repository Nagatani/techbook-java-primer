/**
 * 公共交通（バス・電車）。待ち時間を持つ
 */
public class PublicTransport extends Transport {
    private int waitMinutes;

    public PublicTransport(String name, double speedKmPerHour, int waitMinutes) {
        super(name, speedKmPerHour);
        this.waitMinutes = waitMinutes;
    }

    @Override
    public int getWaitMinutes() {
        return waitMinutes;
    }

    @Override
    public String toString() {
        return super.toString() + " 待ち" + waitMinutes + "分";
    }
}
