/**
 * 第9章 発展課題1: 履修登録と時間割（解答例）
 *
 * 学習ポイント:
 * - キーで値を引くMap、重複を許さないSetの使い分け
 * - LinkedHashMap・LinkedHashSetで登録した順を保つ
 * - containsKey・getで「すでにあるか」を確かめてから登録する
 */
public class TimetableApp {
    public static void main(String[] args) {
        Timetable timetable = new Timetable(8);
        timetable.addToCatalog(new Course("C101", "プログラミング", "月1", 2));
        timetable.addToCatalog(new Course("C102", "線形代数", "月1", 2));
        timetable.addToCatalog(new Course("C103", "英語", "火2", 1));
        timetable.addToCatalog(new Course("C104", "統計学", "水1", 2));
        timetable.addToCatalog(new Course("C105", "情報倫理", "木3", 2));
        timetable.addToCatalog(new Course("C106", "データベース", "金2", 2));
        timetable.addToCatalog(new Course("C107", "体育", "金4", 1));

        IO.println("=== 開講科目 ===");
        for (Course course : timetable.getCatalog().values()) {
            IO.println("  " + course);
        }

        IO.println();
        IO.println("=== 履修登録 ===");
        String[] requests = {"C101", "C103", "C102", "C101", "C104", "C999", "C105", "C106", "C107"};
        for (String code : requests) {
            IO.println(timetable.register(code));
        }

        IO.println();
        IO.println("=== 登録の取り消し ===");
        IO.println(timetable.drop("C105"));
        IO.println(timetable.drop("C105"));
        IO.println(timetable.register("C106"));

        IO.println();
        IO.println("=== 時間割 ===");
        IO.print(timetable.toTable());
        IO.println("登録した科目（登録順）: " + timetable.getRegistered());
        IO.println("合計: " + timetable.getTotalCredits() + "単位");
    }
}
