/**
 * 通学の手段（親クラス）
 *
 * 所要時間と1日の費用の計算はこのクラスに1回だけ書く。
 * 子クラスは、待ち時間・片道の運賃・使える距離などをオーバーライドする。
 */
public class Transport {
    private String name;
    private double speedKmPerHour;

    public Transport(String name, double speedKmPerHour) {
        if (name == null || name.isBlank() || speedKmPerHour <= 0) {
            throw new IllegalArgumentException("名前と速さが正しくありません");
        }
        this.name = name;
        this.speedKmPerHour = speedKmPerHour;
    }

    public String getName() {
        return name;
    }

    public double getSpeedKmPerHour() {
        return speedKmPerHour;
    }

    // 乗るまでの待ち時間（分）。標準は0分
    public int getWaitMinutes() {
        return 0;
    }

    // 片道の運賃（円）。標準は0円
    public int fare(double distanceKm) {
        return 0;
    }

    // この距離で使えるか。標準はどの距離でも使える
    public boolean canUse(double distanceKm) {
        return distanceKm > 0;
    }

    // 片道の所要時間（分）= 待ち時間 + 移動の時間
    public int minutes(double distanceKm) {
        return getWaitMinutes() + (int) Math.ceil(distanceKm / speedKmPerHour * 60);
    }

    // 1日（往復）の費用
    public int dailyCost(double distanceKm) {
        return fare(distanceKm) * 2;
    }

    @Override
    public String toString() {
        return name + "（時速" + (int) speedKmPerHour + "km）";
    }
}
