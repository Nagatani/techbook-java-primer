/**
 * 車両の基底クラス（課題4の解答例）
 */
public class Vehicle {
    protected String brand;
    protected int speed;

    public Vehicle(String brand) {
        this.brand = brand;
        this.speed = 0;
    }

    // 車両の種類の名前。サブクラスでオーバーライドする
    public String getType() {
        return "車両";
    }

    public String getName() {
        return brand + "の" + getType();
    }

    public void accelerate(int amount) {
        IO.println("加速中...");
        speed += amount;
        IO.println("現在の速度: " + speed + "km/h");
    }

    // 車両の種類ごとの情報（乗車人数・排気量など）を表示する
    public void showDetails() {
    }

    public void showInfo() {
        IO.println(getName());
        showDetails();
        IO.println("現在の速度: " + speed + "km/h");
    }

    public String getSummary() {
        return getName() + " - 速度: " + speed + "km/h";
    }
}
