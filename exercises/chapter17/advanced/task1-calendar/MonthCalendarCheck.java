/**
 * MonthCalendarの動作確認
 *
 * 画面を開かずに、マス目の計算だけを確かめます。
 */
public class MonthCalendarCheck {
    private static int total = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        IO.println("=== MonthCalendarの動作確認 ===");
        MonthCalendar november = new MonthCalendar(2026, 11);
        check("2026年11月1日は日曜（0列目）", 0, november.getFirstColumn());
        check("2026年11月は30日まで", 30, november.getDaysInMonth());
        check("最初のマスは1日", 1, november.getCells()[0]);
        check("30日は29番目のマス", 30, november.getCells()[29]);
        check("31番目のマスは空", 0, november.getCells()[30]);

        MonthCalendar february = new MonthCalendar(2028, 2);
        check("2028年2月1日は火曜（2列目）", 2, february.getFirstColumn());
        check("2028年はうるう年で2月は29日まで", 29, february.getDaysInMonth());
        check("1日より前のマスは空", 0, february.getCells()[1]);
        check("1日は2番目のマス", 1, february.getCells()[2]);

        MonthCalendar may = new MonthCalendar(2027, 5);
        check("2027年5月1日は土曜（6列目）", 6, may.getFirstColumn());
        check("31日は6行目まで使う（36番目のマス）", 31, may.getCells()[36]);

        IO.println();
        IO.println(total + "件中" + (total - failed) + "件OK");
    }

    private static void check(String label, int expected, int actual) {
        total++;
        if (expected == actual) {
            IO.println("OK  " + label);
        } else {
            failed++;
            IO.println("NG  " + label + "（期待: " + expected + "、実際: " + actual + "）");
        }
    }
}
