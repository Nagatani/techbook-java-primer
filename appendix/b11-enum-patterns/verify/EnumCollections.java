import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.EnumSet;

/**
 * EnumSetとEnumMapの中身を観察する。
 * 非公開のフィールドを読むので、java.utilを開いて実行する。
 *   javac EnumCollections.java
 *   java --add-opens java.base/java.util=ALL-UNNAMED EnumCollections
 */
public class EnumCollections {
    enum Day { MON, TUE, WED, THU, FRI, SAT, SUN }

    /** 定数が64個のenum */
    enum Small64 {
        C00, C01, C02, C03, C04, C05, C06, C07, C08, C09, C10, C11, C12, C13,
            C14, C15, C16, C17, C18, C19, C20, C21, C22, C23, C24, C25, C26, C27,
            C28, C29, C30, C31, C32, C33, C34, C35, C36, C37, C38, C39, C40, C41,
            C42, C43, C44, C45, C46, C47, C48, C49, C50, C51, C52, C53, C54, C55,
            C56, C57, C58, C59, C60, C61, C62, C63
    }

    /** 定数が65個のenum */
    enum Large65 {
        D00, D01, D02, D03, D04, D05, D06, D07, D08, D09, D10, D11, D12, D13,
            D14, D15, D16, D17, D18, D19, D20, D21, D22, D23, D24, D25, D26, D27,
            D28, D29, D30, D31, D32, D33, D34, D35, D36, D37, D38, D39, D40, D41,
            D42, D43, D44, D45, D46, D47, D48, D49, D50, D51, D52, D53, D54, D55,
            D56, D57, D58, D59, D60, D61, D62, D63, D64
    }

    public static void main(String[] args) throws Exception {
        // 1. 実装のクラスは定数の数で決まる
        IO.println("Day（%d個）: %s".formatted(Day.values().length,
            EnumSet.noneOf(Day.class).getClass().getName()));
        IO.println("Small64（%d個）: %s".formatted(Small64.values().length,
            EnumSet.noneOf(Small64.class).getClass().getName()));
        IO.println("Large65（%d個）: %s".formatted(Large65.values().length,
            EnumSet.noneOf(Large65.class).getClass().getName()));

        // 2. RegularEnumSetは1つのlongのビットで要素を表す（ビットの位置がordinal）
        EnumSet<Day> weekend = EnumSet.of(Day.SUN, Day.SAT);
        long bits = (long) field(weekend, "elements");
        IO.println("EnumSet.of(SUN, SAT) = %s, elements = %s（2進数）".formatted(
            weekend, Long.toBinaryString(bits)));
        EnumSet<Day> weekdays = EnumSet.complementOf(weekend);
        IO.println("complementOf = %s, elements = %s（2進数）".formatted(
            weekdays, Long.toBinaryString((long) field(weekdays, "elements"))));

        // JumboEnumSetはlongの配列を使う
        long[] words = (long[]) field(EnumSet.of(Large65.D00, Large65.D64), "elements");
        IO.println("Large65: elementsはlong[%d]、[0]=%s、[1]=%s".formatted(
            words.length, Long.toBinaryString(words[0]), Long.toBinaryString(words[1])));

        // 3. EnumMapは、ordinalを添字にした配列に値を入れる
        EnumMap<Day, String> plan = new EnumMap<>(Day.class);
        plan.put(Day.FRI, "飲み会");
        plan.put(Day.MON, "会議");
        Object[] vals = (Object[]) field(plan, "vals");
        IO.println("EnumMap = %s".formatted(plan));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < vals.length; i++) {
            sb.append(i == 0 ? "" : ", ").append(vals[i]);
        }
        IO.println("vals（長さ%d）= [%s]".formatted(vals.length, sb));
    }

    static Object field(Object target, String name) throws ReflectiveOperationException {
        for (Class<?> c = target.getClass(); c != null; c = c.getSuperclass()) {
            try {
                Field f = c.getDeclaredField(name);
                f.setAccessible(true);
                return f.get(target);
            } catch (NoSuchFieldException e) {
                // スーパークラスを探す
            }
        }
        throw new NoSuchFieldException(name);
    }
}
