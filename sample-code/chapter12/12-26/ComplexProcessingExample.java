import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ComplexProcessingExample {
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
            new Student("Alice", 85, 165),
            new Student("Bob", 92, 170),
            new Student("Charlie", 78, 180),
            new Student("David", 92, 175),
            new Student("Eve", 88, 160),
            new Student("Frank", 82, 178),
            new Student("Grace", 95, 168)
        );
        
        // 複雑な処理：高得点者（85点以上）を身長でグループ化し、
        // 各グループで最高得点者の名前を取得
        Map<String, String> heightGroupTopScorers = students.stream()
            .filter(s -> s.getScore() >= 85)                    // 高得点者のみ
            .collect(Collectors.groupingBy(                     // 身長でグループ化
                s -> s.getHeight() >= 170 ? "高身長" : "標準身長",
                Collectors.collectingAndThen(                   // グループ内での最高得点者を取得
                    Collectors.maxBy((s1, s2) -> Integer.compare(s1.getScore(), s2.getScore())),
                    opt -> opt.map(Student::getName).orElse("該当者なし")
                )
            ));
        
        System.out.println("身長グループ別最高得点者:");
        heightGroupTopScorers.forEach((group, name) -> 
            System.out.println(group + ": " + name));
        
        // さらに複雑な例：統計情報の収集
        Map<String, Double> heightGroupAverages = students.stream()
            .collect(Collectors.groupingBy(
                s -> s.getHeight() >= 170 ? "高身長" : "標準身長",
                Collectors.averagingInt(Student::getScore)
            ));
        
        System.out.println("\n身長グループ別平均点:");
        heightGroupAverages.forEach((group, avg) -> 
            System.out.printf("%s: %.1f点%n", group, avg));
    }
}
