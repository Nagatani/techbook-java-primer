/**
 * 時計の針の角度と、円周上の点を計算するクラス（画面に依存しないロジック）
 *
 * 角度は12時の方向を0度とし、時計回りに増やします。
 */
public class ClockMath {

    private ClockMath() {
    }

    /** 時針の角度。1時間で30度、さらに1分で0.5度進む */
    public static double hourAngle(int hour, int minute) {
        return (hour % 12) * 30.0 + minute * 0.5;
    }

    /** 分針の角度。1分で6度、さらに1秒で0.1度進む */
    public static double minuteAngle(int minute, int second) {
        return minute * 6.0 + second * 0.1;
    }

    /** 秒針の角度。1秒で6度進む */
    public static double secondAngle(int second) {
        return second * 6.0;
    }

    /**
     * 中心(cx, cy)から角度degrees、長さlengthの位置にある点のx座標
     * 12時の方向が0度なので、xにはsin、yにはcosを使う
     */
    public static int pointX(int cx, double degrees, double length) {
        return cx + (int) Math.round(length * Math.sin(Math.toRadians(degrees)));
    }

    /**
     * 点のy座標。画面のy座標は下へ行くほど大きいので、cosを引く
     */
    public static int pointY(int cy, double degrees, double length) {
        return cy - (int) Math.round(length * Math.cos(Math.toRadians(degrees)));
    }
}
