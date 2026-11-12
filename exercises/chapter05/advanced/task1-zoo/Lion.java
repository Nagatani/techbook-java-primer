public class Lion extends Mammal {
    public Lion(String name, int age, double weightKg) {
        super(name, age, weightKg);
    }

    @Override
    public String getKind() {
        return "ライオン";
    }

    @Override
    public String getArea() {
        return "サバンナ";
    }

    @Override
    public String getFood() {
        return "牛肉";
    }

    @Override
    public int getFoodPricePerKg() {
        return 1200;
    }
    // えさの割合は標準の3%のまま
}
