import java.util.EnumMap;
import java.util.Map;

/**
 * ordinal()を保存に使うと、定数の並べ替えで意味が変わることを観察する。
 * 並べ替えの前と後のenumを、SizeV1とSizeV2の2つで表している。
 *   javac OrdinalPitfall.java && java OrdinalPitfall
 */
public class OrdinalPitfall {
    /** 最初の版 */
    enum SizeV1 { SMALL, MEDIUM, LARGE }

    /** 先頭にEXTRA_SMALLを足した版 */
    enum SizeV2 { EXTRA_SMALL, SMALL, MEDIUM, LARGE }

    /** 明示的なフィールドに番号を持たせた版 */
    enum Size {
        EXTRA_SMALL(5), SMALL(10), MEDIUM(20), LARGE(30);

        private final int code;

        Size(int code) {
            this.code = code;
        }

        int code() { return code; }

        static Size fromCode(int code) {
            for (Size s : values()) {
                if (s.code == code) {
                    return s;
                }
            }
            throw new IllegalArgumentException("不明なコード: " + code);
        }
    }

    public static void main(String[] args) {
        // 古い版で、ordinalと名前を保存したとする
        SizeV1 saved = SizeV1.MEDIUM;
        int storedOrdinal = saved.ordinal();
        String storedName = saved.name();
        IO.println("保存した値: %s（ordinal=%d）".formatted(saved, storedOrdinal));

        // 新しい版で読み戻す
        IO.println("ordinalで読み戻す: " + SizeV2.values()[storedOrdinal]);
        IO.println("名前で読み戻す: " + SizeV2.valueOf(storedName));

        // 明示的なフィールドなら、定数を足しても並べ替えても変わらない
        IO.println("コードで読み戻す: " + Size.fromCode(Size.MEDIUM.code()));

        // 定数ごとの値はEnumMapで持つ（ordinalを添字にした配列を自分で作らない）
        Map<SizeV2, Integer> price = new EnumMap<>(SizeV2.class);
        price.put(SizeV2.SMALL, 300);
        price.put(SizeV2.LARGE, 500);
        IO.println("EnumMap: " + price);
    }
}
