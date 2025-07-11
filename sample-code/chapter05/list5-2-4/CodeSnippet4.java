/**
 * リスト5-4
 * Carクラス
 * 
 * 元ファイル: chapter05-inheritance-and-polymorphism.md (250行目)
 */

// リファクタリング後：重複が除去された
public class Car extends Vehicle {
    public Car(String model, String color) {
        super(model, color);
    }
    
    // Car固有の機能があれば追加
    public void openTrunk() {
        System.out.println(model + " のトランクを開く");
    }
}

public class Truck extends Vehicle {
    private int loadCapacity;
    
    public Truck(String model, String color, int loadCapacity) {
        super(model, color);
        this.loadCapacity = loadCapacity;
    }
    
    // accelerateメソッドをオーバーライド（重い車両は加速が遅い）
    @Override
    public void accelerate() {
        speed += 5; // トラックは加速が遅い
        System.out.println(model + " がゆっくり加速: " + speed + "km/h");
    }
    
    public void loadCargo(int weight) {
        if (weight <= loadCapacity) {
            System.out.println(weight + "kg の荷物を積載");
        } else {
            System.out.println("積載量オーバー！");
        }
    }
}

public class Motorcycle extends Vehicle {
    public Motorcycle(String model, String color) {
        super(model, color);  // ①
    }
    
    @Override  // ②
    public void accelerate() {
        speed += 20;  // ③
        System.out.println(model + " が素早く加速: " + speed + "km/h");
    }
    
    public void wheelie() {  // ④
        System.out.println(model + " がウィリー！");
    }
}