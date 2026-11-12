/**
 * バイク（課題4の解答例）
 */
public class Motorcycle extends Vehicle {
    private int displacement;

    public Motorcycle(String brand, int displacement) {
        super(brand);
        this.displacement = displacement;
    }

    @Override
    public String getType() {
        return "バイク";
    }

    @Override
    public void showDetails() {
        IO.println("排気量: " + displacement + "cc");
    }
}
