/**
 * 第5章 演習問題1: Catクラスの解答例（派生クラス）
 */
public class Cat extends Animal {
    private String color;
    
    public Cat(String name, int age) {
        super(name, age);
        this.color = "グレー";
    }
    
    public Cat(String name, int age, String color) {
        super(name, age);
        this.color = color;
    }
    
    @Override
    public void makeSound() {
        System.out.println(name + "は「ニャーニャー」と鳴きます。");
    }
    
    // Cat固有のメソッド
    public void groom() {
        System.out.println(name + "は毛づくろいをしています。");
    }
    
    public void purr() {
        System.out.println(name + "はゴロゴロとのどを鳴らしています。");
    }
    
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("毛色: " + color);
    }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}