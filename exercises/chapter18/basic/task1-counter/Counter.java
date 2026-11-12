/**
 * 0から上限までの値を数えるカウンター（画面に依存しないロジック）
 */
public class Counter {
    private final int max;
    private int value;

    public Counter(int max) {
        this.max = max;
    }

    public void increment() {
        if (canIncrement()) {
            value++;
        }
    }

    public void decrement() {
        if (canDecrement()) {
            value--;
        }
    }

    public void reset() {
        value = 0;
    }

    public boolean canIncrement() {
        return value < max;
    }

    public boolean canDecrement() {
        return value > 0;
    }

    public int getValue() {
        return value;
    }

    public int getMax() {
        return max;
    }
}
