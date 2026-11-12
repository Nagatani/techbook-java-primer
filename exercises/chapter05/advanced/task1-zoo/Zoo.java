/**
 * 動物をまとめて管理する動物園
 *
 * 給餌の予定と健康チェックでは動物の種類を調べない（instanceofを使わない）。
 * 集計で鳥だけが持つ「飛べるかどうか」を使うところだけ、instanceofで型を調べる。
 */
public class Zoo {
    private String name;
    private Animal[] animals;
    private int count;

    public Zoo(String name, int capacity) {
        this.name = name;
        this.animals = new Animal[capacity];
    }

    public boolean add(Animal animal) {
        if (count >= animals.length) {
            return false;
        }
        animals[count] = animal;
        count++;
        IO.println("登録: " + animal + " / " + animal.getGroup() + "・" + animal.getArea());
        return true;
    }

    public void showFeedingPlan() {
        int total = 0;
        for (int i = 0; i < count; i++) {
            Animal a = animals[i];
            IO.println("  %s（%s）: %s %.1fkg %d円".formatted(
                    a.getName(), a.getKind(), a.getFood(), a.dailyFoodKg(), a.dailyFoodCost()));
            total += a.dailyFoodCost();
        }
        IO.println("  1日のえさ代の合計: " + total + "円");
    }

    // temperatures[i]は、i番目に登録した動物の体温
    public void checkHealth(double[] temperatures) {
        for (int i = 0; i < count && i < temperatures.length; i++) {
            Animal a = animals[i];
            String result = a.isHealthy(temperatures[i])
                    ? "健康"
                    : "要注意（平熱 " + a.getNormalTemperatureMin() + "〜" + a.getNormalTemperatureMax() + "℃）";
            IO.println("  " + a.getName() + ": " + temperatures[i] + "℃ " + result);
        }
    }

    public void showStatistics() {
        int mammals = 0;
        int birds = 0;
        int flightless = 0;
        int ageTotal = 0;
        for (int i = 0; i < count; i++) {
            Animal a = animals[i];
            ageTotal += a.getAge();
            if (a instanceof Mammal) {
                mammals++;
            } else if (a instanceof Bird bird) {
                birds++;
                if (!bird.canFly()) {
                    flightless++;
                }
            }
        }
        IO.println("  " + name + "の飼育数: " + count + "（哺乳類 " + mammals + "、鳥類 " + birds
                + "、うち飛べない鳥 " + flightless + "）");
        IO.println("  平均年齢: %.1f歳".formatted((double) ageTotal / count));
    }
}
