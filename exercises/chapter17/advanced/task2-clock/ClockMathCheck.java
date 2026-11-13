/**
 * ClockMathの動作確認
 *
 * 画面を開かずに、針の角度と円周上の点の計算だけを確かめます。
 */
public class ClockMathCheck {
    private static int total = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        IO.println("=== ClockMathの動作確認 ===");
        check("3:00の時針は90度", 90.0, ClockMath.hourAngle(3, 0));
        check("15:00の時針も90度", 90.0, ClockMath.hourAngle(15, 0));
        check("10:10の時針は305度", 305.0, ClockMath.hourAngle(10, 10));
        check("10分30秒の分針は63度", 63.0, ClockMath.minuteAngle(10, 30));
        check("30秒の秒針は180度", 180.0, ClockMath.secondAngle(30));
        check("0度の点は真上（x）", 100.0, ClockMath.pointX(100, 0, 50));
        check("0度の点は真上（y）", 50.0, ClockMath.pointY(100, 0, 50));
        check("90度の点は右（x）", 150.0, ClockMath.pointX(100, 90, 50));
        check("90度の点は右（y）", 100.0, ClockMath.pointY(100, 90, 50));
        check("180度の点は真下（y）", 150.0, ClockMath.pointY(100, 180, 50));
        check("270度の点は左（x）", 50.0, ClockMath.pointX(100, 270, 50));
        IO.println();
        IO.println(total + "件中" + (total - failed) + "件OK");
    }

    private static void check(String label, double expected, double actual) {
        total++;
        if (Math.abs(expected - actual) < 1e-9) {
            IO.println("OK  " + label);
        } else {
            failed++;
            IO.println("NG  " + label + "（期待: " + expected + "、実際: " + actual + "）");
        }
    }
}
