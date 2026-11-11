import java.util.Collections;
import java.util.List;

public class LocalRecordExample {
    public static void analyzeData(List<Integer> numbers) {
        // ローカルRecord
        record Statistics(int count, double average, int max, int min) {}
        
        if (numbers.isEmpty()) {
            IO.println("データがありません");
            return;
        }
        
        int count = numbers.size();
        double average = numbers.stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0);
        int max = Collections.max(numbers);
        int min = Collections.min(numbers);
        
        Statistics stats = new Statistics(count, average, max, min);
        IO.println("統計情報: " + stats);
    }
}
