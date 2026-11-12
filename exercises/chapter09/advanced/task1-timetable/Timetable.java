import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 1人の学生の時間割（履修登録）
 *
 * - 開講科目: 科目コードから科目を引くのでMap。一覧を登録した順に出すためLinkedHashMap
 * - 登録した科目コード: 重複を許さないのでSet。登録順に表示するためLinkedHashSet
 * - 曜日時限ごとの科目: 「月1」から科目を引くのでMap。順序は時間割表の表示で決めるのでHashMap
 */
public class Timetable {
    private static final String[] DAYS = {"月", "火", "水", "木", "金"};
    private static final int PERIODS = 4;

    private final Map<String, Course> catalog = new LinkedHashMap<>();
    private final Set<String> registered = new LinkedHashSet<>();
    private final Map<String, Course> bySlot = new HashMap<>();
    private final int creditLimit;

    public Timetable(int creditLimit) {
        this.creditLimit = creditLimit;
    }

    // 開講科目を加える
    public void addToCatalog(Course course) {
        catalog.put(course.getCode(), course);
    }

    public Map<String, Course> getCatalog() {
        return catalog;
    }

    // 履修登録する。登録できないときは理由を返す
    public String register(String code) {
        Course course = catalog.get(code);
        if (course == null) {
            return code + "という科目はありません";
        }
        if (registered.contains(code)) {
            return course.getName() + "はすでに登録しています";
        }
        Course other = bySlot.get(course.getSlot());
        if (other != null) {
            return course.getName() + "は" + course.getSlot() + "の" + other.getName() + "と重なります";
        }
        if (getTotalCredits() + course.getCredits() > creditLimit) {
            return course.getName() + "を登録すると上限の" + creditLimit + "単位を超えます";
        }
        registered.add(code);
        bySlot.put(course.getSlot(), course);
        return course.getName() + "を登録しました（合計 " + getTotalCredits() + "単位）";
    }

    // 登録を取り消す
    public String drop(String code) {
        if (!registered.remove(code)) {
            return code + "は登録していません";
        }
        Course course = catalog.get(code);
        bySlot.remove(course.getSlot());
        return course.getName() + "の登録を取り消しました（合計 " + getTotalCredits() + "単位）";
    }

    public int getTotalCredits() {
        int total = 0;
        for (String code : registered) {
            total += catalog.get(code).getCredits();
        }
        return total;
    }

    public Set<String> getRegistered() {
        return registered;
    }

    // 時間割を曜日ごとの行にする。科目のない曜日は「なし」
    public String toTable() {
        StringBuilder sb = new StringBuilder();
        for (String day : DAYS) {
            sb.append(day).append(": ");
            int count = 0;
            for (int period = 1; period <= PERIODS; period++) {
                Course course = bySlot.get(day + period);
                if (course != null) {
                    if (count > 0) {
                        sb.append("、");
                    }
                    sb.append(period).append("限 ").append(course.getName());
                    count++;
                }
            }
            if (count == 0) {
                sb.append("なし");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
