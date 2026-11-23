public class StringCalculator {
    public int add(String numbers) {
        if (numbers.isEmpty()) {
            return 0;
        }
        int sum = 0;
        for (String part : numbers.split(",")) {
            sum += Integer.parseInt(part);
        }
        return sum;
    }
}
