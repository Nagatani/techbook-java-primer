import java.time.DayOfWeek;

/**
 * enumのswitchがどうコンパイルされるかを観察する。
 *   javac EnumSwitch.java && javap -c -p EnumSwitch
 *   javap -c -p 'EnumSwitch$1'   （別のクラスファイルのenumに使う表）
 *   java EnumSwitch
 */
public class EnumSwitch {
    enum Color { RED, GREEN, BLUE }

    /** 定数をすべて書いたswitch式（defaultなし） */
    static String japanese(Color c) {
        return switch (c) {
            case RED -> "赤";
            case GREEN -> "緑";
            case BLUE -> "青";
        };
    }

    /** 従来のswitch文（網羅していなくてもよい） */
    static int warmth(Color c) {
        int w = 0;
        switch (c) {
            case RED:
                w = 2;
                break;
            case BLUE:
                w = -1;
                break;
        }
        return w;
    }

    /** 別にコンパイルされたenum（JDKのDayOfWeek）のswitch式 */
    static boolean weekend(DayOfWeek d) {
        return switch (d) {
            case SATURDAY, SUNDAY -> true;
            case MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY -> false;
        };
    }

    public static void main(String[] args) {
        for (Color c : Color.values()) {
            IO.println(c + ": " + japanese(c) + " warmth=" + warmth(c));
        }
        IO.println("SATURDAY weekend=" + weekend(DayOfWeek.SATURDAY)
            + ", MONDAY weekend=" + weekend(DayOfWeek.MONDAY));
        try {
            japanese(null);
        } catch (NullPointerException e) {
            IO.println("japanese(null): NullPointerException");
        }
    }
}
