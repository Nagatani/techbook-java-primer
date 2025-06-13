# 第4章 継承とポリモーフィズム

この章では、オブジェクト指向プログラミングの重要な概念である継承とポリモーフィズムについて学習します。

## 4.1 継承の基礎

### 基本的な継承

```java
// 基底クラス（スーパークラス）
public class Animal {
    protected String name;
    protected int age;
    
    public Animal(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    public void eat() {
        System.out.println(name + "が食事をしています");
    }
    
    public void sleep() {
        System.out.println(name + "が眠っています");
    }
    
    public void makeSound() {
        System.out.println(name + "が音を出しています");
    }
}

// 派生クラス（サブクラス）
public class Dog extends Animal {
    private String breed;
    
    public Dog(String name, int age, String breed) {
        super(name, age);  // 親クラスのコンストラクタ呼び出し
        this.breed = breed;
    }
    
    @Override
    public void makeSound() {
        System.out.println(name + "が「ワンワン」と鳴いています");
    }
    
    public void wagTail() {
        System.out.println(name + "が尻尾を振っています");
    }
}

public class Cat extends Animal {
    public Cat(String name, int age) {
        super(name, age);
    }
    
    @Override
    public void makeSound() {
        System.out.println(name + "が「ニャーニャー」と鳴いています");
    }
    
    public void purr() {
        System.out.println(name + "がゴロゴロ言っています");
    }
}
```

## 4.2 ポリモーフィズム

### 基本的なポリモーフィズム

```java
public class PolymorphismExample {
    public static void main(String[] args) {
        // ポリモーフィズム：親クラスの参照で子クラスのオブジェクトを参照
        Animal[] animals = {
            new Dog("ポチ", 3, "柴犬"),
            new Cat("ミケ", 2),
            new Dog("タロウ", 5, "ゴールデンレトリバー")
        };
        
        // 同じメソッド呼び出しでも、実際のオブジェクトに応じて異なる動作
        for (Animal animal : animals) {
            animal.makeSound();  // 動的メソッドディスパッチ
        }
        
        // instanceof 演算子による型チェック
        for (Animal animal : animals) {
            if (animal instanceof Dog) {
                Dog dog = (Dog) animal;
                dog.wagTail();
            } else if (animal instanceof Cat) {
                Cat cat = (Cat) animal;
                cat.purr();
            }
        }
    }
}
```

## 4.3 抽象クラス

```java
// 抽象クラス
public abstract class Shape {
    protected String color;
    
    public Shape(String color) {
        this.color = color;
    }
    
    // 抽象メソッド（サブクラスで必ず実装）
    public abstract double getArea();
    public abstract double getPerimeter();
    
    // 具象メソッド（サブクラスで共通の処理）
    public void displayInfo() {
        System.out.println("色: " + color);
        System.out.println("面積: " + getArea());
        System.out.println("周囲長: " + getPerimeter());
    }
}

public class Rectangle extends Shape {
    private double width;
    private double height;
    
    public Rectangle(String color, double width, double height) {
        super(color);
        this.width = width;
        this.height = height;
    }
    
    @Override
    public double getArea() {
        return width * height;
    }
    
    @Override
    public double getPerimeter() {
        return 2 * (width + height);
    }
}

public class Circle extends Shape {
    private double radius;
    
    public Circle(String color, double radius) {
        super(color);
        this.radius = radius;
    }
    
    @Override
    public double getArea() {
        return Math.PI * radius * radius;
    }
    
    @Override
    public double getPerimeter() {
        return 2 * Math.PI * radius;
    }
}
```

## 4.4 インターフェイス

```java
// インターフェイス
public interface Drawable {
    void draw();
    
    // Java 8以降：デフォルトメソッド
    default void highlight() {
        System.out.println("図形をハイライトしています");
    }
    
    // Java 8以降：静的メソッド
    static void printDrawingInfo() {
        System.out.println("図形描画システム v1.0");
    }
}

public interface Movable {
    void move(int x, int y);
    
    default void moveBy(int dx, int dy) {
        System.out.println("相対移動: (" + dx + ", " + dy + ")");
    }
}

// 複数インターフェイスの実装
public class DrawableRectangle extends Rectangle implements Drawable, Movable {
    private int x, y;
    
    public DrawableRectangle(String color, double width, double height, int x, int y) {
        super(color, width, height);
        this.x = x;
        this.y = y;
    }
    
    @Override
    public void draw() {
        System.out.println("長方形を(" + x + ", " + y + ")に描画します");
    }
    
    @Override
    public void move(int newX, int newY) {
        this.x = newX;
        this.y = newY;
        System.out.println("新しい位置: (" + x + ", " + y + ")");
    }
}
```

## 4.5 練習問題

1. 図形の階層構造を拡張し、三角形クラスを追加してください。

2. 楽器の抽象クラスを作成し、ピアノとギターのクラスを実装してください。

3. 複数のインターフェイスを組み合わせた実用的なクラス設計を行ってください。

## まとめ

この章では、継承とポリモーフィズムについて学習しました。これらの概念により、コードの再利用性と拡張性が大幅に向上します。