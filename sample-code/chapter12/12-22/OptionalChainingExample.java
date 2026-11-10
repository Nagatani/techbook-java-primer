import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class OptionalChainingExample {
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
            new Student("Alice", 85, 165),
            new Student("Bob", 92, 170),
            new Student("Charlie", 78, 180)
        );
        
        // Optionalの連鎖：複数の変換を安全に実行
        // 最高得点者の名前を大文字で取得（該当者がいない場合は"N/A"）
        String result = students.stream()
            .max((s1, s2) -> Integer.compare(s1.getScore(), s2.getScore()))
            .map(Student::getName)          // 学生が見つかれば名前を取得
            .map(String::toUpperCase)       // 名前を大文字に変換
            .orElse("N/A");                 // 見つからない場合のデフォルト値
        
        IO.println("最高得点者（大文字）: " + result);
    }
}
