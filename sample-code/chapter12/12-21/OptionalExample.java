import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class OptionalExample {
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
            new Student("Alice", 85, 165),
            new Student("Bob", 92, 170),
            new Student("Charlie", 78, 180)
        );
        
        // Optionalの基本使用：最初の高得点者を安全に取得
        // findFirst()はOptional<Student>を返すため、null安全
        Optional<Student> firstHighScorer = students.stream()
            .filter(s -> s.getScore() >= 90)
            .findFirst();
        
        // 値が存在する場合のみ処理を実行
        firstHighScorer.ifPresent(student -> 
            System.out.println("最初の高得点者: " + student.getName()));
        
        // デフォルト値を使用した安全な処理
        String topScorerName = firstHighScorer
            .map(Student::getName)
            .orElse("該当者なし");
        System.out.println("トップスコアラー: " + topScorerName);
    }
}
