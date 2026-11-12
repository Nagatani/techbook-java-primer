/**
 * 車（課題4の解答例）
 */
public class Car extends Vehicle {
    private int capacity;

    public Car(String brand, int capacity) {
        super(brand);
        this.capacity = capacity;
    }

    @Override
    public String getType() {
        return "車";
    }

    @Override
    public void showDetails() {
        IO.println("乗車人数: " + capacity + "人");
    }
}
