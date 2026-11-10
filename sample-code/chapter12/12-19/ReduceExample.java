import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ReduceExample {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        // 基本的なreduce操作：数値の合計を計算
        // reduce(初期値, 二項演算子)の形式
        int sum = numbers.stream()
            .reduce(0, (a, b) -> a + b);  // 初期値0から開始し、各要素を加算
        IO.println("合計: " + sum); // 出力: 合計: 15
        
        // より複雑な例：最大値を求める（初期値なし）
        // この場合、空のストリームの可能性があるためOptionalが返される
        Optional<Integer> max = numbers.stream()
            .reduce((a, b) -> a > b ? a : b);
        max.ifPresent(value -> IO.println("最大値: " + value)); // 出力: 最大値: 5
        
        // 文字列の結合例
        List<String> words = Arrays.asList("Java", "is", "awesome");
        String sentence = words.stream()
            .reduce("", (result, word) -> result.isEmpty() ? word : result + " " + word);
        IO.println(sentence); // 出力: Java is awesome
        
        // 複雑な集約：学生の点数から統計情報を計算
        List<Student> students = Arrays.asList(
            new Student("Alice", 85, 165),
            new Student("Bob", 92, 170),
            new Student("Charlie", 78, 180)
        );
        
        // 全学生の平均点を計算（reduceを使った方法）
        double average = students.stream()
            .mapToInt(Student::getScore)
            .reduce(0, Integer::sum) / (double) students.size();
        IO.println("平均点: " + average); // 出力: 平均点: 85.0
    }
}
