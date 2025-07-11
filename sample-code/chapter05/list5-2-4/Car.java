/**
 * リスト5-2
 * Carクラス
 * 
 * 元ファイル: chapter05-inheritance-and-polymorphism.md (127行目)
 */

public class Car {
    private String model;     // ①
    private String color;     // ①
    private int speed;        // ①
    
    public void start() {     // ②
        System.out.println(model + " のエンジンを始動");
    }
    
    public void accelerate() {  // ③
        speed += 10;
        System.out.println(model + " が加速: " + speed + "km/h");
    }
    
    public void brake() {       // ④
        speed = Math.max(0, speed - 10);
        System.out.println(model + " が減速: " + speed + "km/h");
    }
}

public class Truck {
    private String model;       // ①と重複
    private String color;       // ①と重複
    private int speed;          // ①と重複
    private int loadCapacity;   // ⑤
    
    public void start() {       // ②と完全重複
        System.out.println(model + " のエンジンを始動");
    }
    
    public void accelerate() {  // ③と完全重複
        speed += 10;
        System.out.println(model + " が加速: " + speed + "km/h");
    }
    
    public void brake() {       // ④と完全重複
        speed = Math.max(0, speed - 10);
        System.out.println(model + " が減速: " + speed + "km/h");
    }
    
    public void loadCargo(int weight) {  // ⑥
        System.out.println(weight + "kg の荷物を積載");
    }
}