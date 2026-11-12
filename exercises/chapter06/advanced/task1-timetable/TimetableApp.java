public class TimetableApp {
    public static void main(String[] args) {
        Lesson math = new Lesson("数学", "201");
        Lesson english = new Lesson("英語", "305");
        Lesson java = new Lesson("Java", "演習室");
        Lesson pe = new Lesson("体育", "体育館");

        Lesson[][] source = new Lesson[Timetable.DAYS][Timetable.PERIODS];
        source[Timetable.MON][0] = math;
        source[Timetable.MON][1] = english;
        source[Timetable.TUE][2] = java;
        source[Timetable.TUE][3] = java;
        source[Timetable.THU][0] = pe;

        Timetable timetable = new Timetable(source);
        ShallowTimetable shallow = new ShallowTimetable(source);

        IO.println("=== 前期の時間割 ===");
        timetable.print();

        IO.println();
        IO.println("=== 渡した配列を書き換える ===");
        source[Timetable.MON][0] = pe;
        IO.println("月曜1限 Timetable: " + timetable.getLesson(Timetable.MON, 1));
        IO.println("月曜1限 ShallowTimetable: " + shallow.getLesson(Timetable.MON, 1));

        IO.println();
        IO.println("=== 受け取った配列を書き換える ===");
        Lesson[][] copy = timetable.toArray();
        copy[Timetable.TUE][2] = null;
        IO.println("火曜3限 受け取った配列: " + copy[Timetable.TUE][2]);
        IO.println("火曜3限 Timetable: " + timetable.getLesson(Timetable.TUE, 3));

        IO.println();
        IO.println("=== 後期の時間割（前期をもとに作る） ===");
        Timetable second = timetable
                .withoutLesson(Timetable.THU, 1)
                .withLesson(Timetable.WED, 1, new Lesson("統計学", "202"))
                .withLesson(Timetable.FRI, 4, english);
        second.print();
        IO.println("前期の時間割は変わらない:");
        timetable.print();

        IO.println();
        IO.println(Timetable.dayName(Timetable.WED) + "曜1限（後期）: " + second.getLesson(Timetable.WED, 1).getSubject()
                + "、教室 " + second.getLesson(Timetable.WED, 1).getRoom());
    }
}
