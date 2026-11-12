public class Train extends PublicTransport {
    private int walkToStationMinutes;

    public Train(int walkToStationMinutes) {
        super("電車", 40, 5);
        this.walkToStationMinutes = walkToStationMinutes;
    }

    // 初乗り150円、1kmごとに20円
    @Override
    public int fare(double distanceKm) {
        return 150 + 20 * (int) Math.ceil(distanceKm);
    }

    // 駅まで歩く時間を、親クラスで計算した時間に足す
    @Override
    public int minutes(double distanceKm) {
        return super.minutes(distanceKm) + walkToStationMinutes;
    }

    // 2km未満なら電車は使わない
    @Override
    public boolean canUse(double distanceKm) {
        return super.canUse(distanceKm) && distanceKm >= 2;
    }
}
