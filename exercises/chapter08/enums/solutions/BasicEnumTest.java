import java.util.Arrays;

/**
 * 曜日と優先度の列挙型を試すプログラム（課題1の解答例）
 */
public class BasicEnumTest {
    public static void main(String[] args) {
        IO.println("=== 基本的なEnum定義と活用 ===");

        DayOfWeek today = DayOfWeek.MONDAY;
        IO.println("今日: " + today + "（" + today.getJapaneseName() + "）");
        IO.println("明日: " + today.next());
        IO.println("日曜日の次: " + DayOfWeek.SUNDAY.next());
        IO.println("今日は週末: " + today.isWeekend());
        IO.println();

        IO.println("全曜日:");
        for (DayOfWeek day : DayOfWeek.values()) {
            String kind = day.isWeekend() ? "週末" : "平日";
            IO.println(day.ordinal() + ": " + day + " " + day.getJapaneseName() + "（" + kind + "）");
        }
        IO.println();

        // valueOfは、列挙子の名前の文字列から列挙子を取り出す
        DayOfWeek friday = DayOfWeek.valueOf("FRIDAY");
        IO.println("valueOf(\"FRIDAY\"): " + friday.getJapaneseName());
        IO.println();

        IO.println("優先度ごとの対応:");
        for (Priority priority : Priority.values()) {
            IO.println(priority + " → " + priority.getInstruction());
        }
        IO.println();

        Priority[] tasks = {Priority.LOW, Priority.URGENT, Priority.MEDIUM, Priority.HIGH, Priority.URGENT};
        // 列挙型はComparableなので、配列をそのまま並べ替えられる（書いた順になる）
        Arrays.sort(tasks);
        IO.println("並べ替えた優先度: " + Arrays.toString(tasks));
        IO.println("URGENT.compareTo(LOW): " + Priority.URGENT.compareTo(Priority.LOW));
        IO.println();

        IO.println("switchでの分岐:");
        for (Priority priority : Priority.values()) {
            // 列挙型のswitchでは、caseに列挙子の名前だけを書く
            String color = switch (priority) {
                case URGENT -> "赤";
                case HIGH -> "橙";
                case MEDIUM -> "黄";
                case LOW -> "緑";
            };
            IO.println(priority.name() + ": " + color);
        }
    }
}
