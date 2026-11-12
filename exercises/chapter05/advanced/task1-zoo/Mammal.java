/**
 * 哺乳類。分類名と平熱の範囲を決める
 */
public class Mammal extends Animal {
    public Mammal(String name, int age, double weightKg) {
        super(name, age, weightKg);
    }

    @Override
    public String getGroup() {
        return "哺乳類";
    }

    @Override
    public double getNormalTemperatureMin() {
        return 35.5;
    }

    @Override
    public double getNormalTemperatureMax() {
        return 39.0;
    }
}
