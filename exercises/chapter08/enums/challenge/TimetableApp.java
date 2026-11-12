import java.util.EnumSet;

/**
 * 第8章 チャレンジ課題: 大学の時間割（解答例）
 *
 * 曜日・時限・科目の区分を列挙型で表し、1週間の時間割を組みます。
 *
 * 学習ポイント:
 * - フィールドとメソッドを持つ列挙型と、ordinal()・values()の使い方
 * - 列挙型をキーにしたEnumMapと、列挙子の集合を表すEnumSet
 * - 列挙型を使ったswitch
 * - 時間割と一緒に使う科目を、静的内部クラスTimetable.Courseにする
 */
public class TimetableApp {
    public static void main(String[] args) {
        Timetable table = new Timetable();
        Timetable.Course programming = new Timetable.Course("プログラミング演習", Category.REQUIRED, 2, "演習室A");
        Timetable.Course math = new Timetable.Course("線形代数", Category.REQUIRED, 2, "201教室");
        Timetable.Course english = new Timetable.Course("英語コミュニケーション", Category.ELECTIVE_REQUIRED, 1, "305教室");
        Timetable.Course database = new Timetable.Course("データベース", Category.ELECTIVE_REQUIRED, 2, "202教室");
        Timetable.Course lab = new Timetable.Course("情報工学実験", Category.REQUIRED, 2, "実験室");
        Timetable.Course history = new Timetable.Course("科学技術史", Category.ELECTIVE, 2, "大講義室");
        Timetable.Course statistics = new Timetable.Course("統計学", Category.ELECTIVE_REQUIRED, 2, "201教室");

        IO.println("=== 科目の登録 ===");
        IO.println(table.add(Weekday.MONDAY, Period.FIRST, 1, programming));
        IO.println(table.add(Weekday.MONDAY, Period.THIRD, 1, math));
        IO.println(table.add(Weekday.TUESDAY, Period.SECOND, 1, english));
        IO.println(table.add(Weekday.WEDNESDAY, Period.FOURTH, 2, lab));    // 4限と5限の2コマ続き
        IO.println(table.add(Weekday.THURSDAY, Period.FIRST, 1, database));
        IO.println(table.add(Weekday.WEDNESDAY, Period.FIFTH, 1, history)); // 実験と重なる
        IO.println(table.add(Weekday.THURSDAY, Period.FIFTH, 2, history));  // 6限はない
        IO.println(table.add(Weekday.THURSDAY, Period.THIRD, 1, history));
        IO.println(table.add(Weekday.MONDAY, Period.FIRST, 1, statistics)); // プログラミング演習と重なる

        IO.println();
        IO.println("=== 時間割 ===");
        table.print();

        IO.println();
        IO.println("=== 曜日ごとの様子 ===");
        for (Weekday day : Weekday.values()) {
            IO.println("  " + day.getLabel() + ": " + describe(table, day));
        }
        EnumSet<Weekday> free = table.freeDays();
        IO.println("  授業のない曜日: " + free);
        IO.println("  授業のある曜日: " + EnumSet.complementOf(free));
        IO.println("  1限がある曜日: " + table.daysWithFirstPeriod());

        IO.println();
        IO.println("=== 単位の集計 ===");
        table.printCredits();
    }

    // 曜日ごとの一言。列挙型はswitchのcaseに列挙子の名前だけを書ける
    private static String describe(Timetable table, Weekday day) {
        int count = table.countClasses(day, false);
        String note = switch (day) {
            case MONDAY -> "週の始まり";
            case FRIDAY -> "週の終わり";
            default -> "週の半ば";
        };
        if (count == 0) {
            return note + "、全休";
        }
        return note + "、" + count + "コマ（午前" + table.countClasses(day, true) + "コマ）";
    }
}
