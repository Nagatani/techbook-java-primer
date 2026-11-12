import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

/**
 * 成績一覧。並べ方（Comparator）を受け取って、並べ替えた写しを返す
 */
public class Ranking {
    private final List<Student> students = new ArrayList<>();

    public void add(Student student) {
        students.add(student);
    }

    // 元のリストは変えずに、並べ替えた写しを返す
    public List<Student> sortedBy(Comparator<Student> order) {
        List<Student> copy = new ArrayList<>(students);
        copy.sort(order);
        return copy;
    }

    // 並べ方で最初に来る学生（いちばん「小さい」学生）を返す
    public Student first(Comparator<Student> order) {
        Student best = students.get(0);
        for (Student s : students) {
            if (order.compare(s, best) < 0) {
                best = s;
            }
        }
        return best;
    }

    // 並べ替えて、順位を付けて1人ずつ渡す
    public void printRanked(Comparator<Student> order, Consumer<String> out) {
        List<Student> sorted = sortedBy(order);
        for (int i = 0; i < sorted.size(); i++) {
            out.accept("  " + (i + 1) + ". " + sorted.get(i));
        }
    }
}
