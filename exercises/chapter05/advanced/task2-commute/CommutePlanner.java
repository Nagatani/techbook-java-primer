/**
 * 通学の手段を比べるクラス
 *
 * 比べる処理では手段の種類を調べず、Transport型の変数でメソッドを呼ぶ。
 */
public class CommutePlanner {
    private Transport[] transports;

    public CommutePlanner(Transport[] transports) {
        this.transports = transports;
    }

    public void compare(double distanceKm, int days) {
        IO.println("距離 " + distanceKm + "km（" + days + "日通う場合）");
        Transport fastest = null;
        Transport cheapest = null;
        for (Transport t : transports) {
            if (!t.canUse(distanceKm)) {
                IO.println("  " + t.getName() + ": この距離では使わない");
                continue;
            }
            int cost = t.dailyCost(distanceKm) * days;
            IO.println("  %s: 片道%d分、合計%d円".formatted(t.getName(), t.minutes(distanceKm), cost));
            if (fastest == null || t.minutes(distanceKm) < fastest.minutes(distanceKm)) {
                fastest = t;
            }
            if (cheapest == null || cost < cheapest.dailyCost(distanceKm) * days) {
                cheapest = t;
            }
        }
        if (fastest != null) {
            IO.println("  いちばん速い: " + fastest.getName() + "、いちばん安い: " + cheapest.getName());
        }
    }

    // 公共交通だけが持つ待ち時間を表示する（ここだけinstanceofで型を調べる）
    public void showWaitTimes() {
        for (Transport t : transports) {
            if (t instanceof PublicTransport pt) {
                IO.println("  " + pt.getName() + ": 待ち" + pt.getWaitMinutes() + "分");
            }
        }
    }
}
