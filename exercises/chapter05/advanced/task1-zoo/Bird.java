/**
 * 鳥類。分類名と平熱の範囲を決め、飛べるかどうかを持つ
 */
public class Bird extends Animal {
    private boolean canFly;

    public Bird(String name, int age, double weightKg, boolean canFly) {
        super(name, age, weightKg);
        this.canFly = canFly;
    }

    public boolean canFly() {
        return canFly;
    }

    @Override
    public String getGroup() {
        return "鳥類";
    }

    @Override
    public double getNormalTemperatureMin() {
        return 38.0;
    }

    @Override
    public double getNormalTemperatureMax() {
        return 42.0;
    }

    @Override
    public String toString() {
        return super.toString() + (canFly ? " 飛べる" : " 飛べない");
    }
}
