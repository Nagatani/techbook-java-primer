/**
 * リスト5-9
 * Eagleクラス
 * 
 * 元ファイル: chapter05-inheritance-and-polymorphism.md (507行目)
 */

// 良い例：能力をインターフェースで表現
public abstract class Bird {
    protected String name;
    
    public Bird(String name) {
        this.name = name;
    }
    
    public abstract void move();
}

// 飛行能力を表すインターフェース
interface Flyable {
    void fly();
}

// 泳ぐ能力を表すインターフェース
interface Swimmable {
    void swim();
}

public class Eagle extends Bird implements Flyable {
    public Eagle(String name) {
        super(name);
    }
    
    @Override
    public void move() {
        System.out.println(name + " が歩いている");
    }
    
    @Override
    public void fly() {
        System.out.println(name + " が大空を飛んでいる");
    }
}

public class Penguin extends Bird implements Swimmable {
    public Penguin(String name) {
        super(name);
    }
    
    @Override
    public void move() {
        System.out.println(name + " がよちよち歩いている");
    }
    
    @Override
    public void swim() {
        System.out.println(name + " が泳いでいる");
    }
}

// 安全な使用例
public class BirdPark {
    public static void makeFlyableBirdsFly(List<Flyable> flyables) {
        for (Flyable bird : flyables) {
            bird.fly(); // 飛べる鳥だけが対象
        }
    }
    
    public static void makeSwimmableBirdsSwim(List<Swimmable> swimmers) {
        for (Swimmable bird : swimmers) {
            bird.swim(); // 泳げる鳥だけが対象
        }
    }
}