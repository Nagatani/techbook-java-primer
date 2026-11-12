import java.util.EnumMap;
import java.util.EnumSet;

/**
 * 1週間の時間割
 *
 * 曜日ごとに、時限の数だけの配列を持つ（配列の添字はPeriodのordinal()）。
 * 曜日をキーにした対応表はEnumMap、曜日の集合はEnumSetで表す。
 */
public class Timetable {

    /**
     * 科目。時間割と一緒に使うので、静的内部クラスにする（Timetable.Courseと書く）
     */
    public static class Course {
        private final String name;
        private final Category category;
        private final int credits;
        private final String room;

        public Course(String name, Category category, int credits, String room) {
            this.name = name;
            this.category = category;
            this.credits = credits;
            this.room = room;
        }

        public String getName() {
            return name;
        }

        public Category getCategory() {
            return category;
        }

        public int getCredits() {
            return credits;
        }

        @Override
        public String toString() {
            return name + "（" + category.getLabel() + " " + credits + "単位 " + room + "）";
        }
    }

    private final EnumMap<Weekday, Course[]> slots = new EnumMap<>(Weekday.class);
    private final EnumMap<Category, Integer> credits = new EnumMap<>(Category.class);

    public Timetable() {
        for (Weekday day : Weekday.values()) {
            slots.put(day, new Course[Period.values().length]);
        }
        for (Category category : Category.values()) {
            credits.put(category, 0);
        }
    }

    // startからlength個の時限に科目を入れる。重なりや範囲外があれば何も入れない
    public String add(Weekday day, Period start, int length, Course course) {
        Course[] daySlots = slots.get(day);
        for (int i = 0; i < length; i++) {
            Period p = start.after(i);
            if (p == null) {
                return course.getName() + ": " + day.getLabel() + "曜" + start.getLabel() + "から" + length + "コマは入りません";
            }
            if (daySlots[p.ordinal()] != null) {
                return course.getName() + ": " + day.getLabel() + "曜" + p.getLabel() + "は「"
                        + daySlots[p.ordinal()].getName() + "」と重なります";
            }
        }
        for (int i = 0; i < length; i++) {
            daySlots[start.after(i).ordinal()] = course;
        }
        // 2コマ続きの科目でも、単位は1回だけ数える
        Category category = course.getCategory();
        credits.put(category, credits.get(category) + course.getCredits());
        String range = (length == 1) ? start.getLabel() : start.getLabel() + "〜" + start.after(length - 1).getLabel();
        return day.getLabel() + "曜" + range + "に「" + course.getName() + "」を登録しました";
    }

    public void print() {
        for (Weekday day : Weekday.values()) {
            IO.println(day.getLabel() + "曜日");
            if (countClasses(day, false) == 0) {
                IO.println("  （授業なし）");
            }
            Course[] daySlots = slots.get(day);
            for (Period p : Period.values()) {
                Course c = daySlots[p.ordinal()];
                if (c != null) {
                    IO.println("  " + p.getLabel() + " " + p.getTimeRange() + " " + c);
                }
            }
        }
    }

    // 授業が1つもない曜日
    public EnumSet<Weekday> freeDays() {
        EnumSet<Weekday> result = EnumSet.noneOf(Weekday.class);
        for (Weekday day : Weekday.values()) {
            if (countClasses(day, false) == 0) {
                result.add(day);
            }
        }
        return result;
    }

    // 1限に授業がある曜日
    public EnumSet<Weekday> daysWithFirstPeriod() {
        EnumSet<Weekday> result = EnumSet.noneOf(Weekday.class);
        for (Weekday day : Weekday.values()) {
            if (slots.get(day)[Period.FIRST.ordinal()] != null) {
                result.add(day);
            }
        }
        return result;
    }

    // その曜日のコマ数。morningOnlyがtrueなら午前だけ数える
    public int countClasses(Weekday day, boolean morningOnly) {
        int count = 0;
        for (Period p : Period.values()) {
            if (slots.get(day)[p.ordinal()] != null && (!morningOnly || p.isMorning())) {
                count++;
            }
        }
        return count;
    }

    public void printCredits() {
        int total = 0;
        for (Category category : Category.values()) {
            int got = credits.get(category);
            int target = category.getTargetCredits();
            String status = (got >= target) ? "達成" : "あと" + (target - got) + "単位";
            IO.println("  " + category.getLabel() + ": " + got + "単位 / 目標" + target + "単位（" + status + "）");
            total += got;
        }
        IO.println("  合計: " + total + "単位");
    }
}
