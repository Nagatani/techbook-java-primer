public class Elephant extends Mammal {
    public Elephant(String name, int age, double weightKg) {
        super(name, age, weightKg);
    }

    @Override
    public String getKind() {
        return "ゾウ";
    }

    @Override
    public String getArea() {
        return "アフリカの森";
    }

    @Override
    public String getFood() {
        return "干し草";
    }

    @Override
    public int getFoodPricePerKg() {
        return 60;
    }

    @Override
    public double getFoodRatio() {
        return 0.025;
    }
}
