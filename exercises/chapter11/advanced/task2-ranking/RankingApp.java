import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 第11章 発展課題2: 成績一覧の並べ替え（解答例）
 *
 * Comparator.comparing・comparingInt・thenComparing・reversedで並べ方を組み立て、
 * 名前を付けてMapにまとめておき、同じ一覧をいろいろな順に並べます。
 */
public class RankingApp {
    public static void main(String[] args) {
        Ranking ranking = new Ranking();
        ranking.add(new Student("S003", "佐藤", 2, 78));
        ranking.add(new Student("S001", "鈴木", 1, 92));
        ranking.add(new Student("S005", "高橋", 2, 92));
        ranking.add(new Student("S002", "田中", 1, 65));
        ranking.add(new Student("S006", "伊藤", 3, 78));
        ranking.add(new Student("S004", "渡辺", 3, 55));

        // 並べ方に名前を付けて、登録した順に保持する
        Map<String, Comparator<Student>> orders = new LinkedHashMap<>();
        orders.put("学籍番号順", Comparator.comparing(Student::id));
        orders.put("点数の高い順（同点は学籍番号順）",
                Comparator.comparingInt(Student::score).reversed()
                        .thenComparing(Student::id));
        orders.put("学年順、学年の中は点数の高い順",
                Comparator.comparingInt(Student::grade)
                        .thenComparing(Comparator.comparingInt(Student::score).reversed()));

        for (Map.Entry<String, Comparator<Student>> entry : orders.entrySet()) {
            IO.println("=== " + entry.getKey() + " ===");
            ranking.printRanked(entry.getValue(), IO::println);
            IO.println();
        }

        IO.println("=== reversed()の位置による違い ===");
        // 全体に reversed() を付けると、2つ目の条件（学籍番号）も逆順になる
        Comparator<Student> wrong = Comparator.comparingInt(Student::score)
                .thenComparing(Student::id)
                .reversed();
        IO.println("全体を逆にした場合:");
        ranking.printRanked(wrong, IO::println);
        IO.println();

        IO.println("=== 最高点と最低点 ===");
        Comparator<Student> byScoreDesc = orders.get("点数の高い順（同点は学籍番号順）");
        IO.println("  最高点: " + ranking.first(byScoreDesc));
        IO.println("  最低点: " + ranking.first(byScoreDesc.reversed()));
        IO.println();

        IO.println("=== 再試験の対象（60点未満） ===");
        // sortedByは写しを返すので、removeIfで消しても元の一覧は変わらない
        List<Student> retest = ranking.sortedBy(Comparator.comparing(Student::id));
        retest.removeIf(s -> s.score() >= 60);
        retest.forEach(s -> IO.println("  " + s));
    }
}
