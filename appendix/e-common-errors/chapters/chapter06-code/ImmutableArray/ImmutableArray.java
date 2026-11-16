import java.util.Arrays;

public final class ImmutableArray {
    private final int[] numbers;
    
    public ImmutableArray(int[] numbers) {
        // 防御的コピー
        this.numbers = Arrays.copyOf(numbers, numbers.length);
    }
    
    public int get(int index) {
        return numbers[index];
    }
    
    public int size() {
        return numbers.length;
    }
    
    public int[] toArray() {
        // 防御的コピーを返却
        return Arrays.copyOf(numbers, numbers.length);
    }
}
