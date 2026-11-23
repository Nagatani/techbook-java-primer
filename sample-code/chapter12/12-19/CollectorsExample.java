import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CollectorsExample {
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
            new Student("Alice", 85, 165),
            new Student("Bob", 92, 170),
            new Student("Charlie", 78, 180),
            new Student("David", 92, 175),
            new Student("Eve", 88, 160)
        );
        
        // 基本的なcollect操作：リストへの収集
        // 高得点（85点以上）の学生の名前をリストで取得
        List<String> highScorers = students.stream()
            .filter(s -> s.getScore() >= 85)
            .map(Student::getName)
            .collect(Collectors.toList());
        IO.println("高得点者: " + highScorers);
        
        // グループ化の例：点数でグループ分け
        // 同じ点数の学生をグループ化する実用的な例
        Map<Integer, List<Student>> scoreGroups = students.stream()
            .collect(Collectors.groupingBy(Student::getScore));
        IO.println("点数別グループ: " + scoreGroups);
        
        // より複雑な変換：条件による分類
        // 80点以上かどうかで学生を分類
        Map<Boolean, List<Student>> passFail = students.stream()
            .collect(Collectors.partitioningBy(s -> s.getScore() >= 80));
        IO.println("合格者: " + passFail.get(true).size() + "人");
        IO.println("不合格者: " + passFail.get(false).size() + "人");
    }
}
