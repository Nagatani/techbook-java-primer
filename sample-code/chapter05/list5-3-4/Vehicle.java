/**
 * リスト5-3
 * コードスニペット
 * 
 * 元ファイル: chapter05-inheritance-and-polymorphism.md (213行目)
 */

// 共通部分を親クラスとして抽出
public abstract class Vehicle {
    protected String model;
    protected String color;
    protected int speed;
    
    public Vehicle(String model, String color) {
        this.model = model;
        this.color = color;
        this.speed = 0;
    }
    
    // 共通メソッドを親クラスに移動
    public void start() {
        System.out.println(model + " のエンジンを始動");
    }
    
    public void accelerate() {
        speed += 10;
        System.out.println(model + " が加速: " + speed + "km/h");
    }
    
    public void brake() {
        speed = Math.max(0, speed - 10);
        System.out.println(model + " が減速: " + speed + "km/h");
    }
    
    // ゲッターとセッター
    public String getModel() { return model; }
    public int getSpeed() { return speed; }
}