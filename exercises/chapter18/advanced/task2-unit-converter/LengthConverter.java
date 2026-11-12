import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 長さの換算（GUIに依存しない）。
 */
public class LengthConverter {

    /** 入力文字列を0以上の数に変換する。変換できなければ例外を投げる */
    public static double parse(String text) {
        String trimmed = text.strip();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("値を入力してください");
        }
        double value;
        try {
            value = Double.parseDouble(trimmed);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("「" + trimmed + "」は数値ではありません");
        }
        if (value < 0 || Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("0以上の数を入力してください");
        }
        return value;
    }

    /** いったんメートルに直してから、目的の単位に直す */
    public static double convert(double value, LengthUnit from, LengthUnit to) {
        return value * from.getMeters() / to.getMeters();
    }

    /** 小数点以下digits桁に丸め、末尾の0を取って文字列にする */
    public static String format(double value, int digits) {
        return BigDecimal.valueOf(value)
                .setScale(digits, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }
}
