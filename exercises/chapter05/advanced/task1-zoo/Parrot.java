public class Parrot extends Bird {
    public Parrot(String name, int age, double weightKg) {
        super(name, age, weightKg, true);
    }

    @Override
    public String getKind() {
        return "オウム";
    }

    @Override
    public String getFood() {
        return "種子";
    }

    @Override
    public int getFoodPricePerKg() {
        return 1500;
    }

    @Override
    public double getFoodRatio() {
        return 0.1;
    }
    // 展示エリアは標準の「ふれあい広場」のまま
}
