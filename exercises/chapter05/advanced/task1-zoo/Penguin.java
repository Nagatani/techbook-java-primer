public class Penguin extends Bird {
    public Penguin(String name, int age, double weightKg) {
        super(name, age, weightKg, false);  // ペンギンは飛べない
    }

    @Override
    public String getKind() {
        return "ペンギン";
    }

    @Override
    public String getArea() {
        return "極地の海";
    }

    @Override
    public String getFood() {
        return "魚";
    }

    @Override
    public int getFoodPricePerKg() {
        return 800;
    }

    @Override
    public double getFoodRatio() {
        return 0.1;
    }
}
