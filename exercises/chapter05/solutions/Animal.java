/**
 * 第5章 演習問題1: Animalクラスの解答例（基底クラス）
 * 
 * 【学習ポイント】
 * - 継承の基礎
 * - 抽象メソッドの定義
 * - 共通機能の実装
 */
public abstract class Animal {
    protected String name;
    protected int age;
    
    public Animal(String name, int age) {
        this.name = name;
        this.age = Math.max(0, age);
    }
    
    // 抽象メソッド - 子クラスで必ず実装
    public abstract void makeSound();
    
    // 共通メソッド
    public void displayInfo() {
        System.out.println("動物の名前: " + name);
        System.out.println("動物の年齢: " + age + "歳");
    }
    
    public void sleep() {
        System.out.println(name + "は眠っています。");
    }
    
    public void eat() {
        System.out.println(name + "は食べています。");
    }
    
    // getter/setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = Math.max(0, age); }
}