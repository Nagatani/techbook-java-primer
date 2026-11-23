public class StringCalculator {
    public int add(String numbers) {
        if (numbers.isEmpty()) {
            return 0;
        }
        String[] parts = numbers.split(",");
        return Integer.parseInt(parts[0]) + Integer.parseInt(parts[1]);
    }
}
