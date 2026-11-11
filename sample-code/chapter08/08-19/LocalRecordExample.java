public class LocalRecordExample {
    public static void analyzeData(int[] numbers) {
        // ローカルRecord
        record Statistics(int count, double average, int max, int min) {}
        
        if (numbers.length == 0) {
            IO.println("データがありません");
            return;
        }
        
        int sum = 0;
        int max = numbers[0];
        int min = numbers[0];
        for (int n : numbers) {
            sum += n;
            if (n > max) {
                max = n;
            }
            if (n < min) {
                min = n;
            }
        }
        double average = (double) sum / numbers.length;
        
        Statistics stats = new Statistics(numbers.length, average, max, min);
        IO.println("統計情報: " + stats);
    }
}
