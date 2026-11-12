/**
 * 動物（いちばん上の親クラス）
 *
 * えさの量と代金の計算、健康チェックの手順はこのクラスに1回だけ書く。
 * 子クラスは、種類名・展示エリア・えさの種類・体重に対するえさの割合などをオーバーライドする。
 */
public class Animal {
    private String name;
    private int age;
    private double weightKg;

    public Animal(String name, int age, double weightKg) {
        if (name == null || name.isBlank() || age < 0 || weightKg <= 0) {
            throw new IllegalArgumentException("名前・年齢・体重が正しくありません");
        }
        this.name = name;
        this.age = age;
        this.weightKg = weightKg;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public double getWeightKg() {
        return weightKg;
    }

    // 以下は子クラスでオーバーライドする標準の値
    public String getKind() {
        return "動物";
    }

    public String getGroup() {
        return "その他";
    }

    public String getArea() {
        return "ふれあい広場";
    }

    public String getFood() {
        return "配合飼料";
    }

    public int getFoodPricePerKg() {
        return 300;
    }

    // 1日に食べるえさは体重の何割か（標準は3%）
    public double getFoodRatio() {
        return 0.03;
    }

    public double getNormalTemperatureMin() {
        return 35.0;
    }

    public double getNormalTemperatureMax() {
        return 40.0;
    }

    // 1日のえさの量と代金。オーバーライドされた割合や単価が使われる
    public double dailyFoodKg() {
        return weightKg * getFoodRatio();
    }

    public int dailyFoodCost() {
        return (int) Math.round(dailyFoodKg() * getFoodPricePerKg());
    }

    public boolean isHealthy(double temperature) {
        return temperature >= getNormalTemperatureMin() && temperature <= getNormalTemperatureMax();
    }

    @Override
    public String toString() {
        return "[" + getKind() + "] " + name + "（" + age + "歳、" + weightKg + "kg）";
    }
}
