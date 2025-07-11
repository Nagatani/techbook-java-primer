/**
 * リスト5-15
 * Birdクラス
 * 
 * 元ファイル: chapter05-inheritance-and-polymorphism.md (774行目)
 */

// 良い例：インターフェースで能力を分離
public interface Flyable {
    void fly();
}

public interface Swimmable {
    void swim();
}

public class Bird {
    public void eat() { /* ... */ }
    public void sleep() { /* ... */ }
}

public class Duck extends Bird implements Flyable, Swimmable {
    @Override
    public void fly() {
        System.out.println("鴨が飛んでいる");
    }
    
    @Override
    public void swim() {
        System.out.println("鴨が泳いでいる");
    }
}

public class Penguin extends Bird implements Swimmable {
    @Override
    public void swim() {
        System.out.println("ペンギンが高速で泳いでいる");
    }
}

public class Eagle extends Bird implements Flyable {
    @Override
    public void fly() {
        System.out.println("鷲が高く飛んでいる");
    }
}