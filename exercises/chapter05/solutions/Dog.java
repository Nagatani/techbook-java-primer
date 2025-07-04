/**
 * 第5章 演習問題1: Dogクラスの解答例（派生クラス）
 */
public class Dog extends Animal {
    private String breed;
    
    public Dog(String name, int age) {
        super(name, age);
        this.breed = "雑種";
    }
    
    public Dog(String name, int age, String breed) {
        super(name, age);
        this.breed = breed;
    }
    
    @Override
    public void makeSound() {
        System.out.println(name + "は「ワンワン」と鳴きます。");
    }
    
    // Dog固有のメソッド
    public void wagTail() {
        System.out.println(name + "は尻尾を振っています。");
    }
    
    public void fetch() {
        System.out.println(name + "はボールを取ってきました。");
    }
    
    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("犬種: " + breed);
    }
    
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
}